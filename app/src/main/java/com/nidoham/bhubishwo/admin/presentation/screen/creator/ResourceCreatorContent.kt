package com.nidoham.bhubishwo.admin.presentation.screen.creator

import android.net.Uri
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Image
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.layout.Layout
import androidx.compose.ui.layout.Placeable
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardCapitalization
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.AsyncImage
import com.nidoham.bhubishwo.admin.ui.theme.Accents
import com.nidoham.bhubishwo.admin.ui.theme.GlassDark
import com.nidoham.bhubishwo.admin.ui.theme.GlassLight
import com.nidoham.bhubishwo.admin.ui.theme.GlassTypography
import com.nidoham.bhubishwo.admin.ui.theme.glassBorderColor

// ═══════════════════════════════════════════════════════════
// GLASS MORPHISM RESOURCE CREATOR
// FilterChip multi-select • ChipFlowLayout wrapping
// ═══════════════════════════════════════════════════════════

data class ResourceFormState(
    val title: String = "",
    val selectedImageUri: Uri? = null,
    val selectedTypes: Set<ResourceType> = emptySet(),
    val isLoading: Boolean = false,
    val isImageLoading: Boolean = false,
    val errorMessage: String? = null
)

sealed class ResourceType(
    val id: String,
    val displayName: String,
    val icon: String,
    val color: Color
) {
    data object Country : ResourceType(
        id          = "country",
        displayName = "Country",
        icon        = "🌍",
        color       = Accents.Blue
    )
    data object Flag : ResourceType(
        id          = "flag",
        displayName = "Flag",
        icon        = "🚩",
        color       = Accents.Indigo
    )
    data object Tourist : ResourceType(
        id          = "tourist",
        displayName = "Tourist",
        icon        = "📸",
        color       = Accents.Cyan
    )
    data object Landmark : ResourceType(
        id          = "landmark",
        displayName = "Landmark",
        icon        = "🏛️",
        color       = Accents.Purple
    )
    data object Culture : ResourceType(
        id          = "culture",
        displayName = "Culture",
        icon        = "🎭",
        color       = Accents.Teal
    )

    companion object {
        val all: List<ResourceType> = listOf(Country, Flag, Tourist, Landmark, Culture)
    }
}

sealed class ResourceFormEvent {
    data class TitleChanged(val title: String)        : ResourceFormEvent()
    data class ImageSelected(val uri: Uri?)           : ResourceFormEvent()
    data class ImageLoading(val loading: Boolean)     : ResourceFormEvent()
    data class TypeToggled(val type: ResourceType)    : ResourceFormEvent()
    data object UploadClicked                         : ResourceFormEvent()
    data object ErrorDismissed                        : ResourceFormEvent()
}

// ═══════════════════════════════════════════════════════════
// ROOT CONTENT
// ═══════════════════════════════════════════════════════════

@Composable
fun ResourceCreatorContent(
    modifier: Modifier = Modifier,
    state: ResourceFormState = ResourceFormState(),
    onEvent: (ResourceFormEvent) -> Unit = {}
) {
    val isDark       = isSystemInDarkTheme()
    val focusManager = LocalFocusManager.current
    val scrollState  = rememberScrollState()

    // ── Image picker ────────────────────────────────────────
    val imagePicker = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.GetContent()
    ) { uri: Uri? ->
        // Only emit ImageLoading when we have an actual URI; skip for null (user cancelled)
        if (uri != null) onEvent(ResourceFormEvent.ImageLoading(true))
        onEvent(ResourceFormEvent.ImageSelected(uri))
        if (uri != null) onEvent(ResourceFormEvent.ImageLoading(false))
    }

    // ── Background gradient ──────────────────────────────────
    val glassBg = if (isDark) {
        Brush.verticalGradient(listOf(GlassDark.Background, GlassDark.BackgroundElevated))
    } else {
        Brush.verticalGradient(listOf(GlassLight.Background, GlassLight.BackgroundGrouped))
    }

    Column(
        modifier = modifier
            .fillMaxSize()
            .background(glassBg)
            .verticalScroll(scrollState)
            .padding(horizontal = 20.dp, vertical = 16.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(24.dp)
    ) {

        // ── Image picker ─────────────────────────────────────
        GlassImagePicker(
            uri       = state.selectedImageUri,
            isLoading = state.isImageLoading,
            onClick   = { imagePicker.launch("image/*") },
            onRemove  = { onEvent(ResourceFormEvent.ImageSelected(null)) },
            enabled   = !state.isLoading
        )

        // ── Title field ──────────────────────────────────────
        GlassOutlinedTextField(
            value         = state.title,
            onValueChange = { onEvent(ResourceFormEvent.TitleChanged(it)) },
            label         = "Title",
            enabled       = !state.isLoading,
            keyboardOptions = KeyboardOptions(
                capitalization = KeyboardCapitalization.Words,
                imeAction      = ImeAction.Done
            ),
            keyboardActions = KeyboardActions(
                onDone = { focusManager.clearFocus() }
            ),
            modifier = Modifier.fillMaxWidth()
        )

        // ═══════════════════════════════════════════════════
        // RESOURCE TYPE — FilterChip multi-select (ChipFlowLayout)
        // ═══════════════════════════════════════════════════
        Column(
            modifier            = Modifier.fillMaxWidth(),
            verticalArrangement = Arrangement.spacedBy(10.dp)
        ) {
            // Header + count badge
            Row(
                modifier              = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment     = Alignment.CenterVertically
            ) {
                Text(
                    text  = "Resource Types",
                    style = GlassTypography.VibrancyPrimary,
                    color = MaterialTheme.colorScheme.onSurface
                )

                AnimatedVisibility(
                    visible = state.selectedTypes.isNotEmpty(),
                    enter   = fadeIn(),
                    exit    = fadeOut()
                ) {
                    Surface(
                        shape = RoundedCornerShape(12.dp),
                        color = MaterialTheme.colorScheme.primaryContainer
                    ) {
                        Text(
                            text     = "${state.selectedTypes.size} selected",
                            modifier = Modifier.padding(horizontal = 12.dp, vertical = 4.dp),
                            style    = MaterialTheme.typography.labelMedium,
                            color    = MaterialTheme.colorScheme.onPrimaryContainer
                        )
                    }
                }
            }

            // ── ChipFlowLayout: version-safe wrapping row of chips ──
            ChipFlowLayout(
                horizontalGap = 8.dp,
                verticalGap   = 8.dp,
                modifier      = Modifier.fillMaxWidth()
            ) {
                ResourceType.all.forEach { type ->
                    GlassTypeChip(
                        type       = type,
                        isSelected = type in state.selectedTypes,
                        enabled    = !state.isLoading,
                        onClick    = { onEvent(ResourceFormEvent.TypeToggled(type)) }
                    )
                }
            }
        }

        // ── Error banner ─────────────────────────────────────
        AnimatedVisibility(
            visible = state.errorMessage != null,
            enter   = fadeIn(),
            exit    = fadeOut()
        ) {
            GlassErrorSurface(
                message   = state.errorMessage ?: "",
                onDismiss = { onEvent(ResourceFormEvent.ErrorDismissed) }
            )
        }

        Spacer(modifier = Modifier.weight(1f))

        // ── Upload button ─────────────────────────────────────
        GlassUploadButton(
            isLoading = state.isLoading,
            enabled   = !state.isLoading &&
                    state.title.isNotBlank() &&
                    state.selectedImageUri != null &&
                    state.selectedTypes.isNotEmpty(),
            onClick   = { onEvent(ResourceFormEvent.UploadClicked) }
        )
    }
}

// ═══════════════════════════════════════════════════════════
// GLASS TYPE CHIP — fully custom so colors are guaranteed
// Selected  → solid accent background + white text + ✓ icon
// Unselected → glass background + subtle border + muted text
// ═══════════════════════════════════════════════════════════

@Composable
private fun GlassTypeChip(
    type: ResourceType,
    isSelected: Boolean,
    enabled: Boolean,
    onClick: () -> Unit
) {
    val isDark = isSystemInDarkTheme()

    val bgColor = when {
        !enabled && isSelected -> type.color.copy(alpha = 0.30f)
        !enabled               -> if (isDark) GlassDark.SurfaceVariant.copy(alpha = 0.25f)
        else        GlassLight.SurfaceVariant.copy(alpha = 0.25f)
        isSelected             -> type.color                          // full, opaque accent
        isDark                 -> GlassDark.GlassRegular.copy(alpha = 0.40f)
        else                   -> GlassLight.GlassRegular.copy(alpha = 0.50f)
    }

    val contentColor = when {
        !enabled   -> MaterialTheme.colorScheme.onSurface.copy(alpha = 0.35f)
        isSelected -> Color.White
        isDark     -> MaterialTheme.colorScheme.onSurface.copy(alpha = 0.72f)
        else       -> MaterialTheme.colorScheme.onSurfaceVariant
    }

    val border = if (!isSelected) BorderStroke(
        width = 1.dp,
        color = if (isDark) GlassDark.GlassBorderSubtle.copy(alpha = 0.50f)
        else        GlassLight.GlassBorderSubtle.copy(alpha = 0.40f)
    ) else null

    Surface(
        shape           = RoundedCornerShape(20.dp),
        color           = bgColor,
        border          = border,
        shadowElevation = if (isSelected) 2.dp else 0.dp,
        modifier        = Modifier.clickable(
            enabled             = enabled,
            interactionSource   = remember { MutableInteractionSource() },
            indication          = null,
            onClick             = onClick
        )
    ) {
        Row(
            modifier = Modifier.padding(
                start  = 12.dp,
                end    = 14.dp,
                top    = 8.dp,
                bottom = 8.dp
            ),
            horizontalArrangement = Arrangement.spacedBy(6.dp),
            verticalAlignment     = Alignment.CenterVertically
        ) {
            // ✓ checkmark when selected, emoji icon when not
            AnimatedVisibility(visible = isSelected) {
                Icon(
                    imageVector        = Icons.Default.Check,
                    contentDescription = null,
                    modifier           = Modifier.size(15.dp),
                    tint               = Color.White
                )
            }

            Text(
                text  = "${type.icon}  ${type.displayName}",
                style = GlassTypography.VibrancyPrimary.copy(fontSize = 14.sp),
                color = contentColor
            )
        }
    }
}

// ═══════════════════════════════════════════════════════════
// CHIP FLOW LAYOUT — version-safe wrapping layout
// Replaces FlowRow to avoid API-version mismatches
// ═══════════════════════════════════════════════════════════

@Composable
private fun ChipFlowLayout(
    modifier: Modifier = Modifier,
    horizontalGap: androidx.compose.ui.unit.Dp = 8.dp,
    verticalGap: androidx.compose.ui.unit.Dp = 8.dp,
    content: @Composable () -> Unit
) {
    Layout(
        content  = content,
        modifier = modifier
    ) { measurables, constraints ->
        val hGap = horizontalGap.roundToPx()
        val vGap = verticalGap.roundToPx()

        val placeables: List<Placeable> = measurables.map { it.measure(constraints.copy(minWidth = 0)) }

        // Build rows
        val rows = mutableListOf<List<Placeable>>()
        var currentRow = mutableListOf<Placeable>()
        var currentRowWidth = 0

        for (placeable in placeables) {
            val needed = if (currentRow.isEmpty()) placeable.width
            else currentRowWidth + hGap + placeable.width
            if (currentRow.isNotEmpty() && needed > constraints.maxWidth) {
                rows += currentRow
                currentRow = mutableListOf(placeable)
                currentRowWidth = placeable.width
            } else {
                currentRow += placeable
                currentRowWidth = needed
            }
        }
        if (currentRow.isNotEmpty()) rows += currentRow

        val totalHeight = rows.sumOf { row -> row.maxOf { it.height } } +
                (rows.size - 1).coerceAtLeast(0) * vGap

        layout(
            width  = constraints.maxWidth,
            height = totalHeight.coerceIn(constraints.minHeight, constraints.maxHeight)
        ) {
            var y = 0
            rows.forEach { row ->
                val rowHeight = row.maxOf { it.height }
                var x = 0
                row.forEach { placeable ->
                    placeable.placeRelative(x, y + (rowHeight - placeable.height) / 2)
                    x += placeable.width + hGap
                }
                y += rowHeight + vGap
            }
        }
    }
}

// ═══════════════════════════════════════════════════════════
// IMAGE PICKER
// ═══════════════════════════════════════════════════════════

@Composable
private fun GlassImagePicker(
    uri: Uri?,
    isLoading: Boolean,
    onClick: () -> Unit,
    onRemove: () -> Unit,
    enabled: Boolean
) {
    val isDark = isSystemInDarkTheme()

    Box(
        modifier = Modifier
            .fillMaxWidth()
            .height(240.dp)
            .clip(RoundedCornerShape(20.dp))
            .background(if (isDark) GlassDark.SurfaceVariant else GlassLight.SurfaceVariant)
            .clickable(enabled = enabled && !isLoading) { onClick() },
        contentAlignment = Alignment.Center
    ) {
        // Subtle radial accent in dark mode
        if (isDark) {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .background(
                        Brush.radialGradient(
                            colors = listOf(
                                Accents.BlueDark.copy(alpha = 0.08f),
                                Color.Transparent
                            )
                        )
                    )
            )
        }

        when {
            isLoading -> {
                CircularProgressIndicator(
                    color    = MaterialTheme.colorScheme.primary,
                    modifier = Modifier.size(48.dp)
                )
            }

            uri != null -> {
                AsyncImage(
                    model            = uri,
                    contentDescription = "Selected image",
                    modifier         = Modifier.fillMaxSize(),
                    contentScale     = ContentScale.Crop
                )
                // Remove button
                IconButton(
                    onClick  = onRemove,
                    modifier = Modifier
                        .align(Alignment.TopEnd)
                        .padding(12.dp)
                        .size(36.dp)
                        .background(
                            color = if (isDark) GlassDark.GlassThick else GlassLight.GlassThick,
                            shape = RoundedCornerShape(50)
                        )
                ) {
                    Icon(
                        imageVector        = Icons.Default.Close,
                        contentDescription = "Remove image",
                        tint               = MaterialTheme.colorScheme.error,
                        modifier           = Modifier.size(20.dp)
                    )
                }
            }

            else -> {
                Column(
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.spacedBy(16.dp)
                ) {
                    Surface(
                        shape    = RoundedCornerShape(16.dp),
                        color    = if (isDark) GlassDark.GlassThin else GlassLight.GlassThin,
                        modifier = Modifier.size(72.dp)
                    ) {
                        Box(contentAlignment = Alignment.Center) {
                            Icon(
                                imageVector        = Icons.Default.Image,
                                contentDescription = null,
                                modifier           = Modifier.size(36.dp),
                                tint               = MaterialTheme.colorScheme.primary
                            )
                        }
                    }
                    Text(
                        text  = "Tap to select image",
                        style = GlassTypography.VibrancySecondary,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            }
        }
    }
}

// ═══════════════════════════════════════════════════════════
// OUTLINED TEXT FIELD
// ═══════════════════════════════════════════════════════════

@Composable
private fun GlassOutlinedTextField(
    value: String,
    onValueChange: (String) -> Unit,
    label: String,
    enabled: Boolean,
    keyboardOptions: KeyboardOptions = KeyboardOptions.Default,
    keyboardActions: KeyboardActions = KeyboardActions.Default,
    modifier: Modifier = Modifier
) {
    val isDark = isSystemInDarkTheme()

    OutlinedTextField(
        value           = value,
        onValueChange   = onValueChange,
        label           = { Text(text = label, style = GlassTypography.VibrancySecondary) },
        enabled         = enabled,
        keyboardOptions = keyboardOptions,
        keyboardActions = keyboardActions,
        modifier        = modifier,
        shape           = RoundedCornerShape(12.dp),
        colors          = OutlinedTextFieldDefaults.colors(
            focusedContainerColor   = if (isDark) GlassDark.GlassThin     else GlassLight.GlassThin,
            unfocusedContainerColor = if (isDark) GlassDark.GlassUltraThin else GlassLight.GlassUltraThin,
            focusedBorderColor      = MaterialTheme.colorScheme.primary.copy(alpha = 0.8f),
            unfocusedBorderColor    = glassBorderColor().copy(alpha = 0.5f),
            disabledBorderColor     = Color.Transparent
        ),
        textStyle  = GlassTypography.OnGlassRegular,
        singleLine = true
    )
}

// ═══════════════════════════════════════════════════════════
// ERROR SURFACE
// ═══════════════════════════════════════════════════════════

@Composable
private fun GlassErrorSurface(
    message: String,
    onDismiss: () -> Unit
) {
    Surface(
        modifier = Modifier.fillMaxWidth(),
        shape    = RoundedCornerShape(16.dp),
        color    = MaterialTheme.colorScheme.errorContainer.copy(alpha = 0.8f)
    ) {
        Row(
            modifier              = Modifier
                .padding(16.dp)
                .fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment     = Alignment.CenterVertically
        ) {
            Text(
                text  = message,
                color = MaterialTheme.colorScheme.onErrorContainer,
                style = MaterialTheme.typography.bodyMedium,
                modifier = Modifier.weight(1f)
            )
            TextButton(onClick = onDismiss) {
                Text("Dismiss")
            }
        }
    }
}

// ═══════════════════════════════════════════════════════════
// UPLOAD BUTTON  — fixed gradient layout
// ═══════════════════════════════════════════════════════════

@Composable
private fun GlassUploadButton(
    isLoading: Boolean,
    enabled: Boolean,
    onClick: () -> Unit
) {
    val isDark     = isSystemInDarkTheme()
    val useDarkGradient = isDark && enabled

    // Wrap in a Box so the gradient can be a proper background layer
    Box(
        modifier          = Modifier
            .fillMaxWidth()
            .height(56.dp)
            .clip(RoundedCornerShape(16.dp))
            // Dark + enabled → gradient background behind the button
            .then(
                if (useDarkGradient) Modifier.background(
                    Brush.horizontalGradient(listOf(Accents.BlueDark, Accents.IndigoDark))
                ) else Modifier
            ),
        contentAlignment  = Alignment.Center
    ) {
        Button(
            onClick   = onClick,
            modifier  = Modifier.fillMaxSize(),
            enabled   = enabled,
            shape     = RoundedCornerShape(16.dp),
            colors    = ButtonDefaults.buttonColors(
                // In dark+enabled: transparent so gradient shows through
                containerColor         = if (useDarkGradient) Color.Transparent
                else MaterialTheme.colorScheme.primary,
                disabledContainerColor = if (isDark) GlassDark.SurfaceVariant
                else GlassLight.SurfaceVariant
            ),
            elevation = ButtonDefaults.buttonElevation(
                defaultElevation = 0.dp,
                pressedElevation = 0.dp
            )
        ) {
            if (isLoading) {
                CircularProgressIndicator(
                    modifier    = Modifier.size(24.dp),
                    color       = MaterialTheme.colorScheme.onPrimary,
                    strokeWidth = 2.dp
                )
            } else {
                Text(
                    text  = "Upload Resource",
                    style = GlassTypography.VibrancyPrimary.copy(
                        fontSize   = 16.sp,
                        fontWeight = FontWeight.Bold
                    ),
                    color = if (enabled) MaterialTheme.colorScheme.onPrimary
                    else MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
        }
    }
}

// ═══════════════════════════════════════════════════════════
// SCREEN — local state holder
// ═══════════════════════════════════════════════════════════

@Composable
fun ResourceCreatorScreen() {
    var state by remember { mutableStateOf(ResourceFormState()) }

    ResourceCreatorContent(
        state   = state,
        onEvent = { event ->
            state = when (event) {
                is ResourceFormEvent.TitleChanged   -> state.copy(title = event.title)
                is ResourceFormEvent.ImageSelected  -> state.copy(selectedImageUri = event.uri, isImageLoading = false)
                is ResourceFormEvent.ImageLoading   -> state.copy(isImageLoading = event.loading)
                is ResourceFormEvent.TypeToggled    -> {
                    val updated = if (event.type in state.selectedTypes)
                        state.selectedTypes - event.type
                    else
                        state.selectedTypes + event.type
                    state.copy(selectedTypes = updated)
                }
                is ResourceFormEvent.UploadClicked  -> state.copy(isLoading = true)
                is ResourceFormEvent.ErrorDismissed -> state.copy(errorMessage = null)
            }
        }
    )
}