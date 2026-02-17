package com.nidoham.bhubishwo.admin.presentation.viewmodel

import android.content.Context
import android.net.Uri
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.nidoham.bhubishwo.admin.data.repository.ResourceRepository
import com.nidoham.bhubishwo.admin.domain.media.Resource
import com.nidoham.bhubishwo.admin.imgbb.ImgbbRepository
import com.nidoham.bhubishwo.admin.presentation.screen.creator.ResourceFormEvent
import com.nidoham.bhubishwo.admin.presentation.screen.creator.ResourceFormState
import com.nidoham.bhubishwo.admin.presentation.screen.creator.ResourceType
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

@HiltViewModel
class ResourceViewModel @Inject constructor(
    private val repository: ResourceRepository,
    private val imgbbRepository: ImgbbRepository, // ✅ Injected instead of static usage
    @ApplicationContext private val context: Context
) : ViewModel() {

    // ════════════════════════════════════════════════════════
    // UI STATE
    // ════════════════════════════════════════════════════════

    private val _state = MutableStateFlow(ResourceFormState())
    val state: StateFlow<ResourceFormState> = _state.asStateFlow()

    // ════════════════════════════════════════════════════════
    // ONE-SHOT EVENTS
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
                        isImageLoading = false, // Stop loading spinner whether success or cancel
                        errorMessage = null
                    )
                }
            }

            is ResourceFormEvent.ImageLoading -> {
                _state.update { it.copy(isImageLoading = event.loading) }
            }

            is ResourceFormEvent.TypeToggled -> {
                _state.update { current ->
                    val newTypes = if (event.type in current.selectedTypes) {
                        current.selectedTypes - event.type
                    } else {
                        current.selectedTypes + event.type
                    }
                    current.copy(selectedTypes = newTypes)
                }
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
            var tempFile: File? = null

            try {
                // Step 1: Convert Uri to specific Temp File in IO context
                val uri = checkNotNull(current.selectedImageUri)
                tempFile = withContext(Dispatchers.IO) { uriToTempFile(uri) }

                if (tempFile == null || !tempFile.exists()) {
                    throw Exception("Could not process image file")
                }

                // Step 2: Upload to ImgBB
                val uploadResult = withContext(Dispatchers.IO) {
                    imgbbRepository.upload(
                        file = tempFile,
                        customName = current.title.trim()
                    )
                }

                if (!uploadResult.success || uploadResult.url == null) {
                    throw Exception(uploadResult.error ?: "Image upload failed")
                }

                // Step 3: Save metadata to Firestore
                val resource = Resource(
                    id = UUID.randomUUID().toString(),
                    title = current.title.trim(),
                    url = uploadResult.url, // The public URL from ImgBB
                    tags = current.tags
                )

                val dbResult = repository.push(resource)

                if (dbResult.isSuccess) {
                    _state.update { ResourceFormState() } // Reset form
                    _events.emit(ResourceEvent.UploadSuccess(resource.id))
                } else {
                    throw dbResult.exceptionOrNull() ?: Exception("Database save failed")
                }

            } catch (e: Exception) {
                _state.update { it.copy(isLoading = false, errorMessage = e.message) }
                _events.emit(ResourceEvent.ShowError(e.message ?: "Unknown error"))
            } finally {
                // Cleanup temp file
                withContext(Dispatchers.IO) {
                    try { tempFile?.delete() } catch (_: Exception) { }
                }
            }
        }
    }

    // ════════════════════════════════════════════════════════
    // UTILS
    // ════════════════════════════════════════════════════════

    private fun uriToTempFile(uri: Uri): File? {
        return try {
            val contentResolver = context.contentResolver
            val inputStream = contentResolver.openInputStream(uri) ?: return null

            // Try to guess extension or default to jpg
            val type = contentResolver.getType(uri)
            val ext = when {
                type?.contains("png") == true -> ".png"
                type?.contains("webp") == true -> ".webp"
                else -> ".jpg"
            }

            val file = File.createTempFile("upload_", ext, context.cacheDir)
            file.outputStream().use { output ->
                inputStream.copyTo(output)
            }
            inputStream.close()
            file
        } catch (e: Exception) {
            e.printStackTrace()
            null
        }
    }

    private fun validate(state: ResourceFormState): String? = when {
        state.title.isBlank() -> "Title cannot be empty"
        state.selectedImageUri == null -> "Please select an image"
        state.selectedTypes.isEmpty() -> "Select at least one resource type"
        else -> null
    }
}

sealed class ResourceEvent {
    data class UploadSuccess(val resourceId: String) : ResourceEvent()
    data class ShowError(val message: String) : ResourceEvent()
    data class NavigateToDetail(val resourceId: String) : ResourceEvent()
}