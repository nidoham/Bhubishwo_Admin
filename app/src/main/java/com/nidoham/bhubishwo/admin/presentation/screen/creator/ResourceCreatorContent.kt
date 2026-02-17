package com.nidoham.bhubishwo.admin.presentation.screen.creator

import android.net.Uri
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.animation.*
import androidx.compose.animation.core.*
import androidx.compose.foundation.*
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Image
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.scale
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.layout.Layout
import androidx.compose.ui.layout.Placeable
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardCapitalization
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import coil.compose.AsyncImage
import coil.request.ImageRequest
import com.nidoham.bhubishwo.admin.presentation.viewmodel.ResourceEvent
import com.nidoham.bhubishwo.admin.presentation.viewmodel.ResourceViewModel
import com.nidoham.bhubishwo.admin.ui.theme.*

// ═══════════════════════════════════════════════════════════════════════
// DATA CLASSES
// ═══════════════════════════════════════════════════════════════════════

@Stable
data class ResourceFormState(
    val title: String = "",
    val selectedImageUri: Uri? = null,
    val selectedTypes: Set<ResourceType> = emptySet(),
    val isLoading: Boolean = false,
    val isImageLoading: Boolean = false,
    val errorMessage: String? = null
) {
    val tags: Set<String> get() = selectedTypes.map { it.id }.toSet()
}

sealed class ResourceType(
    val id: String,
    val displayName: String,
    val emoji: String,
    val selectedGradientDark: List<Color>,
    val selectedGradientLight: List<Color>,
    val badgeColor: Color,
    val glowColor: Color
) {
    data object Flag : ResourceType(
        id = "flag",
        displayName = "Flag",
        emoji = "🚩",
        selectedGradientDark = listOf(Color(0xFF6A1B9A), Color(0xFF4A148C)),
        selectedGradientLight = listOf(Color(0xFFCE93D8), Color(0xFFAB47BC)),
        badgeColor = Color(0xFF7B1FA2),
        glowColor = Color(0xFF9C27B0)
    )
    data object Tourist : ResourceType(
        id = "tourist",
        displayName = "Tourist",
        emoji = "📸",
        selectedGradientDark = listOf(Color(0xFF00695C), Color(0xFF004D40)),
        selectedGradientLight = listOf(Color(0xFF4DB6AC), Color(0xFF26A69A)),
        badgeColor = Color(0xFF00796B),
        glowColor = Color(0xFF009688)
    )

    companion object {
        val all: List<ResourceType> = listOf(Flag, Tourist)
    }
}

sealed class ResourceFormEvent {
    data class TitleChanged(val title: String) : ResourceFormEvent()
    data class ImageSelected(val uri: Uri?) : ResourceFormEvent()
    data class ImageLoading(val loading: Boolean) : ResourceFormEvent()
    data class TypeToggled(val type: ResourceType) : ResourceFormEvent()
    data object UploadClicked : ResourceFormEvent()
    data object ErrorDismissed : ResourceFormEvent()
}

// ═══════════════════════════════════════════════════════════════════════
// MAIN SCREEN
// ═══════════════════════════════════════════════════════════════════════

@Composable
fun ResourceCreatorScreen(
    viewModel: ResourceViewModel = hiltViewModel(),
    onUploadSuccess: (resourceId: String) -> Unit = {},
    onError: (message: String) -> Unit = {}
) {
    val state by viewModel.state.collectAsState()

    LaunchedEffect(Unit) {
        viewModel.events.collect { event ->
            when (event) {
                is ResourceEvent.UploadSuccess -> onUploadSuccess(event.resourceId)
                is ResourceEvent.ShowError -> onError(event.message)
                is ResourceEvent.NavigateToDetail -> { /* Optional nav logic */ }
            }
        }
    }

    ResourceCreatorContent(
        state = state,
        onEvent = viewModel::onEvent
    )
}

@Composable
fun ResourceCreatorContent(
    modifier: Modifier = Modifier,
    state: ResourceFormState,
    onEvent: (ResourceFormEvent) -> Unit
) {
    val isDark = isSystemInDarkTheme()
    val focusManager = LocalFocusManager.current
    val scrollState = rememberScrollState()

    val onPickerResult = remember(onEvent) {
        { uri: Uri? ->
            if (uri != null) {
                onEvent(ResourceFormEvent.ImageLoading(true))
            }
            onEvent(ResourceFormEvent.ImageSelected(uri))
        }
    }

    val imagePicker = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.GetContent(),
        onResult = onPickerResult
    )

    val onPickImage = remember(imagePicker) {
        { imagePicker.launch("image/*") }
    }

    val glassBg = if (isDark)
        Brush.verticalGradient(listOf(GlassDark.Background, GlassDark.BackgroundElevated))
    else
        Brush.verticalGradient(listOf(GlassLight.Background, GlassLight.BackgroundGrouped))

    Column(
        modifier = modifier
            .fillMaxSize()
            .background(glassBg)
            .verticalScroll(scrollState)
            .padding(20.dp),
        verticalArrangement = Arrangement.spacedBy(24.dp)
    ) {
        // ── Image Picker ──
        GlassImagePicker(
            uri = state.selectedImageUri,
            isLoading = state.isImageLoading,
            onClick = onPickImage,
            onRemove = { onEvent(ResourceFormEvent.ImageSelected(null)) },
            onRepick = onPickImage,
            enabled = !state.isLoading
        )

        // ── Title Input ──
        GlassOutlinedTextField(
            value = state.title,
            onValueChange = { onEvent(ResourceFormEvent.TitleChanged(it)) },
            label = "Resource Title",
            placeholder = "e.g. Mount Everest Base Camp",
            enabled = !state.isLoading,
            keyboardOptions = KeyboardOptions(
                capitalization = KeyboardCapitalization.Words,
                imeAction = ImeAction.Done
            ),
            keyboardActions = KeyboardActions(onDone = { focusManager.clearFocus() }),
            modifier = Modifier.fillMaxWidth()
        )

        // ── Types ──
        ResourceTypeSection(
            selectedTypes = state.selectedTypes,
            enabled = !state.isLoading,
            onToggle = { onEvent(ResourceFormEvent.TypeToggled(it)) }
        )

        // ── Tags Preview ──
        AnimatedVisibility(visible = state.tags.isNotEmpty()) {
            TagsPreview(tags = state.tags)
        }

        // ── Error Banner ──
        AnimatedVisibility(
            visible = state.errorMessage != null,
            enter = fadeIn() + scaleIn(),
            exit = fadeOut() + scaleOut()
        ) {
            GlassErrorSurface(
                message = state.errorMessage ?: "",
                onDismiss = { onEvent(ResourceFormEvent.ErrorDismissed) }
            )
        }

        Spacer(Modifier.height(8.dp))

        // ── Submit Button ──
        GlassUploadButton(
            isLoading = state.isLoading,
            enabled = !state.isLoading && state.title.isNotBlank() &&
                    state.selectedImageUri != null && state.selectedTypes.isNotEmpty(),
            tagCount = state.tags.size,
            onClick = { onEvent(ResourceFormEvent.UploadClicked) }
        )

        Spacer(Modifier.height(16.dp))
    }
}

// ═══════════════════════════════════════════════════════════════════════
// HELPER COMPOSABLES
// ═══════════════════════════════════════════════════════════════════════

@Composable
private fun ResourceTypeSection(
    selectedTypes: Set<ResourceType>,
    enabled: Boolean,
    onToggle: (ResourceType) -> Unit
) {
    val isDark = isSystemInDarkTheme()
    Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column {
                Text(
                    text = "Resource Type",
                    style = GlassTypography.VibrancyPrimary.copy(fontWeight = FontWeight.SemiBold),
                    color = MaterialTheme.colorScheme.onSurface
                )
                Text(
                    text = "Select all that apply",
                    style = GlassTypography.VibrancySecondary.copy(fontSize = 12.sp),
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
        }

        ChipFlowLayout {
            ResourceType.all.forEach { type ->
                ResourceTypeChip(
                    type = type,
                    isSelected = type in selectedTypes,
                    enabled = enabled,
                    isDark = isDark,
                    onClick = { onToggle(type) }
                )
            }
        }
    }
}

@Composable
private fun ResourceTypeChip(
    type: ResourceType,
    isSelected: Boolean,
    enabled: Boolean,
    isDark: Boolean,
    onClick: () -> Unit
) {
    val scale by animateFloatAsState(
        targetValue = if (isSelected) 1.05f else 1f,
        label = "scale"
    )
    val selectedGradient = if (isDark) type.selectedGradientDark else type.selectedGradientLight
    val unselectedBg = if (isDark) GlassDark.GlassRegular.copy(alpha = 0.4f) else GlassLight.GlassRegular.copy(alpha = 0.5f)

    Box(
        modifier = Modifier
            .scale(scale)
            .clip(RoundedCornerShape(22.dp))
            .background(if (isSelected) Brush.linearGradient(selectedGradient) else SolidColor(unselectedBg))
            .clickable(enabled = enabled, onClick = onClick)
    ) {
        Row(
            modifier = Modifier.padding(horizontal = 14.dp, vertical = 10.dp),
            horizontalArrangement = Arrangement.spacedBy(8.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            if (isSelected) {
                Icon(
                    imageVector = Icons.Default.Check,
                    contentDescription = null,
                    tint = Color.White,
                    modifier = Modifier.size(16.dp)
                )
            } else {
                Text(type.emoji)
            }
            Text(
                text = type.displayName,
                color = if (isSelected) Color.White else MaterialTheme.colorScheme.onSurface,
                style = MaterialTheme.typography.labelLarge
            )
        }
    }
}

@Composable
private fun GlassImagePicker(
    uri: Uri?,
    isLoading: Boolean,
    onClick: () -> Unit,
    onRemove: () -> Unit,
    onRepick: () -> Unit,
    enabled: Boolean
) {
    val isDark = isSystemInDarkTheme()
    val context = LocalContext.current

    Box(
        modifier = Modifier
            .fillMaxWidth()
            .height(220.dp)
            .clip(RoundedCornerShape(20.dp))
            .background(if (isDark) GlassDark.SurfaceVariant else GlassLight.SurfaceVariant)
            .clickable(enabled = enabled && uri == null, onClick = onClick),
        contentAlignment = Alignment.Center
    ) {
        if (uri != null) {
            AsyncImage(
                model = ImageRequest.Builder(context).data(uri).crossfade(true).build(),
                contentDescription = "Selected Image",
                contentScale = ContentScale.Crop,
                modifier = Modifier
                    .fillMaxSize()
                    .clickable(enabled = enabled, onClick = onRepick)
            )
            IconButton(
                onClick = onRemove,
                modifier = Modifier
                    .align(Alignment.TopEnd)
                    .padding(8.dp)
                    .background(Color.Black.copy(0.6f), CircleShape)
            ) {
                Icon(Icons.Default.Close, null, tint = Color.White)
            }
        } else if (isLoading) {
            CircularProgressIndicator()
        } else {
            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                Icon(Icons.Default.Image, null, modifier = Modifier.size(48.dp), tint = MaterialTheme.colorScheme.primary)
                Text("Tap to select image", style = MaterialTheme.typography.bodyMedium)
            }
        }
    }
}

@Composable
private fun GlassOutlinedTextField(
    value: String,
    onValueChange: (String) -> Unit,
    label: String,
    placeholder: String,
    enabled: Boolean,
    keyboardOptions: KeyboardOptions,
    keyboardActions: KeyboardActions,
    modifier: Modifier
) {
    OutlinedTextField(
        value = value,
        onValueChange = onValueChange,
        label = { Text(label) },
        placeholder = { Text(placeholder) },
        enabled = enabled,
        keyboardOptions = keyboardOptions,
        keyboardActions = keyboardActions,
        modifier = modifier,
        singleLine = true,
        colors = OutlinedTextFieldDefaults.colors(
            focusedBorderColor = MaterialTheme.colorScheme.primary,
            unfocusedBorderColor = MaterialTheme.colorScheme.outline
        )
    )
}

@Composable
private fun GlassUploadButton(
    isLoading: Boolean,
    enabled: Boolean,
    tagCount: Int,
    onClick: () -> Unit
) {
    Button(
        onClick = onClick,
        enabled = enabled,
        modifier = Modifier.fillMaxWidth().height(50.dp)
    ) {
        if (isLoading) {
            CircularProgressIndicator(modifier = Modifier.size(24.dp), color = Color.White)
        } else {
            Text("Upload Resource ($tagCount tags)")
        }
    }
}

@Composable
private fun TagsPreview(tags: Set<String>) {
    Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
        tags.forEach { tag ->
            Surface(
                color = MaterialTheme.colorScheme.secondaryContainer,
                shape = RoundedCornerShape(8.dp)
            ) {
                Text(
                    text = tag,
                    modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp),
                    style = MaterialTheme.typography.labelSmall
                )
            }
        }
    }
}

@Composable
private fun GlassErrorSurface(message: String, onDismiss: () -> Unit) {
    Surface(
        color = MaterialTheme.colorScheme.errorContainer,
        shape = RoundedCornerShape(8.dp),
        modifier = Modifier.fillMaxWidth()
    ) {
        Row(
            modifier = Modifier.padding(16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = message,
                color = MaterialTheme.colorScheme.onErrorContainer,
                modifier = Modifier.weight(1f)
            )
            TextButton(onClick = onDismiss) {
                Text("Dismiss", color = MaterialTheme.colorScheme.onErrorContainer)
            }
        }
    }
}

// ═══════════════════════════════════════════════════════════════════════
// FIXED: Custom Layout implementation (No Experimental FlowRow)
// ═══════════════════════════════════════════════════════════════════════
@Composable
private fun ChipFlowLayout(
    modifier: Modifier = Modifier,
    horizontalGap: Dp = 8.dp,
    verticalGap: Dp = 8.dp,
    content: @Composable () -> Unit
) {
    Layout(
        content = content,
        modifier = modifier.fillMaxWidth()
    ) { measurables, constraints ->
        val hGapPx = horizontalGap.roundToPx()
        val vGapPx = verticalGap.roundToPx()

        val rows = mutableListOf<List<Placeable>>()
        var currentRow = mutableListOf<Placeable>()
        var currentRowWidth = 0

        val placeables = measurables.map { it.measure(constraints.copy(minWidth = 0)) }

        for (placeable in placeables) {
            if (currentRow.isNotEmpty() && currentRowWidth + hGapPx + placeable.width > constraints.maxWidth) {
                rows.add(currentRow)
                currentRow = mutableListOf(placeable)
                currentRowWidth = placeable.width
            } else {
                if (currentRow.isNotEmpty()) currentRowWidth += hGapPx
                currentRow.add(placeable)
                currentRowWidth += placeable.width
            }
        }
        if (currentRow.isNotEmpty()) rows.add(currentRow)

        val totalHeight = rows.sumOf { row -> row.maxOfOrNull { it.height } ?: 0 } +
                ((rows.size - 1).coerceAtLeast(0) * vGapPx)

        layout(width = constraints.maxWidth, height = totalHeight) {
            var yOffset = 0
            for (row in rows) {
                var xOffset = 0
                val rowHeight = row.maxOfOrNull { it.height } ?: 0
                for (placeable in row) {
                    // Vertically center items in the row
                    val yPos = yOffset + (rowHeight - placeable.height) / 2
                    placeable.placeRelative(x = xOffset, y = yPos)
                    xOffset += placeable.width + hGapPx
                }
                yOffset += rowHeight + vGapPx
            }
        }
    }
}