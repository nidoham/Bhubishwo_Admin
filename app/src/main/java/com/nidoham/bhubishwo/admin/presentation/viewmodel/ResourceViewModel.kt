package com.nidoham.bhubishwo.admin.presentation.viewmodel

import android.content.Context
import android.net.Uri
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.nidoham.bhubishwo.admin.data.repository.ResourceRepository
import com.nidoham.bhubishwo.admin.domain.media.Resource
import com.nidoham.bhubishwo.admin.imgbb.ImgbbStorage
import com.nidoham.bhubishwo.admin.presentation.screen.creator.ResourceFormEvent
import com.nidoham.bhubishwo.admin.presentation.screen.creator.ResourceFormState
import dagger.hilt.android.lifecycle.HiltViewModel
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.io.File
import java.util.UUID
import javax.inject.Inject

/**
 * ViewModel for resource creation with proper state management and one-shot events.
 *
 * FIX 1: Removed @Inject CoroutineDispatcher — no Hilt binding existed; use Dispatchers.IO directly.
 * FIX 2: Removed SavedStateHandle for ResourceFormState — it's not Parcelable (contains Uri, Set of
 *         sealed objects). State survives config changes naturally via MutableStateFlow in ViewModel.
 * FIX 3: Inject ApplicationContext to implement URI → File correctly instead of always returning null.
 */
@HiltViewModel
class ResourceViewModel @Inject constructor(
    private val repository: ResourceRepository,
    @ApplicationContext private val context: Context
) : ViewModel() {

    // ════════════════════════════════════════════════════════
    // UI STATE
    // ════════════════════════════════════════════════════════

    private val _state = MutableStateFlow(ResourceFormState())
    val state: StateFlow<ResourceFormState> = _state.asStateFlow()

    // ════════════════════════════════════════════════════════
    // ONE-SHOT EVENTS — consumed only by CreatorActivity,
    // NOT also collected inside ResourceCreatorScreen (was a race condition).
    // ════════════════════════════════════════════════════════

    private val _events = MutableSharedFlow<ResourceEvent>(extraBufferCapacity = 1)
    val events: SharedFlow<ResourceEvent> = _events.asSharedFlow()

    // ════════════════════════════════════════════════════════
    // EVENT HANDLER
    // ════════════════════════════════════════════════════════

    fun onEvent(event: ResourceFormEvent) {
        when (event) {
            is ResourceFormEvent.TitleChanged -> {
                _state.update { it.copy(title = event.title, errorMessage = null) }
            }

            is ResourceFormEvent.ImageSelected -> {
                _state.update {
                    it.copy(
                        selectedImageUri = event.uri,
                        isImageLoading = false,
                        errorMessage = null
                    )
                }
            }

            is ResourceFormEvent.ImageLoading -> {
                _state.update { it.copy(isImageLoading = event.loading) }
            }

            is ResourceFormEvent.TypeToggled -> {
                _state.update { current ->
                    val updated = if (event.type in current.selectedTypes)
                        current.selectedTypes - event.type
                    else
                        current.selectedTypes + event.type
                    current.copy(selectedTypes = updated)
                }
            }

            is ResourceFormEvent.TagInputChanged -> {
                _state.update { it.copy(currentTagInput = event.value) }
            }

            is ResourceFormEvent.CustomTagAdded -> {
                val tag = event.tag.trim().lowercase()
                _state.update { current ->
                    if (tag.isNotBlank() && tag !in current.customTags)
                        current.copy(customTags = current.customTags + tag, currentTagInput = "")
                    else
                        current.copy(currentTagInput = "")
                }
            }

            is ResourceFormEvent.CustomTagRemoved -> {
                _state.update { it.copy(customTags = it.customTags - event.tag) }
            }

            is ResourceFormEvent.UploadClicked -> uploadResource()

            is ResourceFormEvent.ErrorDismissed -> {
                _state.update { it.copy(errorMessage = null) }
            }
        }
    }

    // ════════════════════════════════════════════════════════
    // UPLOAD PIPELINE
    // ════════════════════════════════════════════════════════

    private fun uploadResource() {
        val current = _state.value

        val validationError = validate(current)
        if (validationError != null) {
            _state.update { it.copy(errorMessage = validationError) }
            return
        }

        _state.update { it.copy(isLoading = true, errorMessage = null) }

        viewModelScope.launch {
            try {
                // Step 1 — URI → temp File using ContentResolver (FIX 3: was always returning null)
                val uri = checkNotNull(current.selectedImageUri)
                val imageFile = withContext(Dispatchers.IO) {
                    uriToTempFile(uri)
                }

                if (imageFile == null) {
                    _state.update {
                        it.copy(isLoading = false, errorMessage = "Failed to read image from storage")
                    }
                    _events.tryEmit(ResourceEvent.ShowError("Failed to read image file"))
                    return@launch
                }

                // Step 2 — Upload to ImgBB
                val imgbbResult = withContext(Dispatchers.IO) {
                    ImgbbStorage.upload(file = imageFile, name = current.title.trim())
                }

                imageFile.delete()

                if (!imgbbResult.success || imgbbResult.url.isNullOrBlank()) {
                    _state.update {
                        it.copy(
                            isLoading = false,
                            errorMessage = imgbbResult.errorMessage ?: "Upload failed"
                        )
                    }
                    _events.tryEmit(
                        ResourceEvent.ShowError(imgbbResult.errorMessage ?: "Image upload failed")
                    )
                    return@launch
                }

                // Step 3 — Persist to Firestore
                val resource = Resource(
                    id = UUID.randomUUID().toString(),
                    title = current.title.trim(),
                    url = imgbbResult.url,
                    tags = current.allTags
                )

                val firestoreResult = repository.push(resource)

                if (firestoreResult.isSuccess) {
                    _state.update { ResourceFormState() } // Reset form on success
                    _events.tryEmit(ResourceEvent.UploadSuccess(resource.id))
                } else {
                    val error = firestoreResult.exceptionOrNull()?.message ?: "Save failed"
                    _state.update { it.copy(isLoading = false, errorMessage = error) }
                    _events.tryEmit(ResourceEvent.ShowError(error))
                }

            } catch (e: Exception) {
                val msg = "Unexpected error: ${e.localizedMessage}"
                _state.update { it.copy(isLoading = false, errorMessage = msg) }
                _events.tryEmit(ResourceEvent.ShowError(msg))
            }
        }
    }

    // ════════════════════════════════════════════════════════
    // URI → FILE  (FIX 3: real implementation using ContentResolver)
    // ════════════════════════════════════════════════════════

    private fun uriToTempFile(uri: Uri): File? {
        return try {
            val inputStream = context.contentResolver.openInputStream(uri) ?: return null
            val ext = context.contentResolver.getType(uri)
                ?.substringAfterLast('/')
                ?.let { ".$it" }
                ?: ".jpg"
            val tempFile = File.createTempFile("upload_", ext, context.cacheDir)
            tempFile.outputStream().use { out -> inputStream.copyTo(out) }
            inputStream.close()
            tempFile
        } catch (e: Exception) {
            null
        }
    }

    // ════════════════════════════════════════════════════════
    // VALIDATION
    // ════════════════════════════════════════════════════════

    private fun validate(state: ResourceFormState): String? = when {
        state.title.isBlank() -> "Title cannot be empty"
        state.selectedImageUri == null -> "Please select an image"
        state.allTags.isEmpty() -> "Select at least one type or add a custom tag"
        else -> null
    }
}

// ════════════════════════════════════════════════════════
// SEALED CLASS — One-shot UI events
// ════════════════════════════════════════════════════════

sealed class ResourceEvent {
    data class UploadSuccess(val resourceId: String) : ResourceEvent()
    data class ShowError(val message: String) : ResourceEvent()
    data class NavigateToDetail(val resourceId: String) : ResourceEvent()
}