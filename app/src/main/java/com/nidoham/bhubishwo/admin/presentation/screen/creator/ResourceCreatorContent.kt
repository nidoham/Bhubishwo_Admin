package com.nidoham.bhubishwo.admin.presentation.screen.creator

import android.net.Uri
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.EaseOutBack
import androidx.compose.animation.core.spring
import androidx.compose.animation.core.tween
import androidx.compose.animation.expandHorizontally
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.scaleIn
import androidx.compose.animation.scaleOut
import androidx.compose.animation.shrinkHorizontally
import androidx.compose.animation.togetherWith
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Image
import androidx.compose.material.icons.rounded.CheckCircle
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Divider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.Stable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.scale
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardCapitalization
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.compose.animation.core.animateFloatAsState
import coil.compose.AsyncImage
import coil.request.ImageRequest
import com.nidoham.bhubishwo.admin.presentation.viewmodel.ResourceEvent
import com.nidoham.bhubishwo.admin.presentation.viewmodel.ResourceViewModel
import com.nidoham.bhubishwo.admin.ui.theme.Accents
import com.nidoham.bhubishwo.admin.ui.theme.GlassDark
import com.nidoham.bhubishwo.admin.ui.theme.GlassLight
import com.nidoham.bhubishwo.admin.ui.theme.GlassTypography
import com.nidoham.bhubishwo.admin.ui.theme.glassBorderColor

// ═══════════════════════════════════════════════════════════════════════
// DATA CLASSES & SEALED CLASSES
// ═══════════════════════════════════════════════════════════════════════

/**
 * @Stable tells Compose that this data class correctly implements equals(),
 * so Compose will recompose whenever state actually changes.
 * Without this, unstable fields like Uri? and Set<ResourceType> cause
 * Compose to skip recomposition, making the entire UI appear frozen —
 * inputs don't respond, images don't appear, selections don't register.
 */
@Stable
data class ResourceFormState(
    val title: String = "",
    val selectedImageUri: Uri? = null,
    val selectedTypes: Set<ResourceType> = emptySet(),
    val isLoading: Boolean = false,
    val isImageLoading: Boolean = false,
    val errorMessage: String? = null
) {
    val tags: Set<String>
        get() = selectedTypes.map { it.id }.toSet()
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
        id                    = "flag",
        displayName           = "Flag",
        emoji                 = "🚩",
        selectedGradientDark  = listOf(Color(0xFF6A1B9A), Color(0xFF4A148C)),
        selectedGradientLight = listOf(Color(0xFFCE93D8), Color(0xFFAB47BC)),
        badgeColor            = Color(0xFF7B1FA2),
        glowColor             = Color(0xFF9C27B0)
    )
    data object Tourist : ResourceType(
        id                    = "tourist",
        displayName           = "Tourist",
        emoji                 = "📸",
        selectedGradientDark  = listOf(Color(0xFF00695C), Color(0xFF004D40)),
        selectedGradientLight = listOf(Color(0xFF4DB6AC), Color(0xFF26A69A)),
        badgeColor            = Color(0xFF00796B),
        glowColor             = Color(0xFF009688)
    )

    companion object {
        val all: List<ResourceType> = listOf(Flag, Tourist)
    }
}

sealed class ResourceFormEvent {
    data class TitleChanged(val title: String)    : ResourceFormEvent()
    data class ImageSelected(val uri: Uri?)       : ResourceFormEvent()
    data class ImageLoading(val loading: Boolean) : ResourceFormEvent()
    data class TypeToggled(val type: ResourceType): ResourceFormEvent()
    data object UploadClicked                     : ResourceFormEvent()
    data object ErrorDismissed                    : ResourceFormEvent()
}

// ═══════════════════════════════════════════════════════════════════════
// ROOT SCREEN
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
                is ResourceEvent.UploadSuccess    -> onUploadSuccess(event.resourceId)
                is ResourceEvent.ShowError        -> onError(event.message)
                is ResourceEvent.NavigateToDetail -> { /* handle navigation if needed */ }
            }
        }
    }

    ResourceCreatorContent(
        state   = state,
        // FIX: Explicit lambda wrapper instead of viewModel::onEvent.
        // A method reference is not a stable lambda in Compose — it gets a
        // new instance each recomposition, which can break child composables
        // that capture it via remember {}.
        onEvent = { viewModel.onEvent(it) }
    )
}

// ═══════════════════════════════════════════════════════════════════════
// ROOT CONTENT
// ═══════════════════════════════════════════════════════════════════════

@Composable
fun ResourceCreatorContent(
    modifier: Modifier = Modifier,
    state: ResourceFormState = ResourceFormState(),
    onEvent: (ResourceFormEvent) -> Unit = {}
) {
    val isDark       = isSystemInDarkTheme()
    val focusManager = LocalFocusManager.current
    val scrollState  = rememberScrollState()

    val imagePicker = rememberLauncherForActivityResult(
        ActivityResultContracts.GetContent()
    ) { uri: Uri? ->
        if (uri != null) onEvent(ResourceFormEvent.ImageLoading(true))
        onEvent(ResourceFormEvent.ImageSelected(uri))
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
            .padding(horizontal = 20.dp, vertical = 20.dp),
        verticalArrangement = Arrangement.spacedBy(24.dp)
    ) {
        // ── Image picker ──────────────────────────────────────
        GlassImagePicker(
            uri       = state.selectedImageUri,
            isLoading = state.isImageLoading,
            onClick   = { imagePicker.launch("image/*") },
            onRemove  = { onEvent(ResourceFormEvent.ImageSelected(null)) },
            enabled   = !state.isLoading
        )

        // ── Title field ───────────────────────────────────────
        GlassOutlinedTextField(
            value         = state.title,
            onValueChange = { onEvent(ResourceFormEvent.TitleChanged(it)) },
            label         = "Resource Title",
            placeholder   = "e.g. Eiffel Tower, Tokyo Street Food...",
            enabled       = !state.isLoading,
            keyboardOptions = KeyboardOptions(
                capitalization = KeyboardCapitalization.Words,
                imeAction      = ImeAction.Next
            ),
            keyboardActions = KeyboardActions(onNext = { focusManager.clearFocus() }),
            modifier      = Modifier.fillMaxWidth()
        )

        // ── Resource Types ────────────────────────────────────
        ResourceTypeSection(
            selectedTypes = state.selectedTypes,
            enabled       = !state.isLoading,
            onToggle      = { onEvent(ResourceFormEvent.TypeToggled(it)) }
        )

        // ── Tag preview ───────────────────────────────────────
        AnimatedVisibility(visible = state.tags.isNotEmpty()) {
            TagsPreview(tags = state.tags)
        }

        // ── Error banner ──────────────────────────────────────
        AnimatedVisibility(
            visible = state.errorMessage != null,
            enter   = fadeIn() + scaleIn(initialScale = 0.95f),
            exit    = fadeOut() + scaleOut(targetScale = 0.95f)
        ) {
            GlassErrorSurface(
                message   = state.errorMessage ?: "",
                onDismiss = { onEvent(ResourceFormEvent.ErrorDismissed) }
            )
        }

        Spacer(Modifier.height(8.dp))

        // ── Upload button ─────────────────────────────────────
        GlassUploadButton(
            isLoading = state.isLoading,
            enabled   = !state.isLoading &&
                    state.title.isNotBlank() &&
                    state.selectedImageUri != null &&
                    state.tags.isNotEmpty(),
            tagCount  = state.tags.size,
            onClick   = { onEvent(ResourceFormEvent.UploadClicked) }
        )

        Spacer(Modifier.height(16.dp))
    }
}

// ═══════════════════════════════════════════════════════════════════════
// RESOURCE TYPE SECTION
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
            modifier              = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment     = Alignment.CenterVertically
        ) {
            Column {
                Text(
                    text  = "Resource Type",
                    style = GlassTypography.VibrancyPrimary.copy(fontWeight = FontWeight.SemiBold),
                    color = MaterialTheme.colorScheme.onSurface
                )
                Text(
                    text  = "Select one or both",
                    style = GlassTypography.VibrancySecondary.copy(fontSize = 12.sp),
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }

            AnimatedVisibility(
                visible = selectedTypes.isNotEmpty(),
                enter   = fadeIn() + expandHorizontally(),
                exit    = fadeOut() + shrinkHorizontally()
            ) {
                Surface(
                    shape = RoundedCornerShape(20.dp),
                    color = MaterialTheme.colorScheme.primaryContainer
                ) {
                    Text(
                        text     = "${selectedTypes.size} / ${ResourceType.all.size}",
                        modifier = Modifier.padding(horizontal = 10.dp, vertical = 4.dp),
                        style    = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.Bold),
                        color    = MaterialTheme.colorScheme.onPrimaryContainer
                    )
                }
            }
        }

        ChipFlowLayout(
            horizontalGap = 8.dp,
            verticalGap   = 8.dp,
            modifier      = Modifier.fillMaxWidth()
        ) {
            ResourceType.all.forEach { type ->
                ResourceTypeChip(
                    type       = type,
                    isSelected = type in selectedTypes,
                    enabled    = enabled,
                    isDark     = isDark,
                    onClick    = { onToggle(type) }
                )
            }
        }
    }
}

// ═══════════════════════════════════════════════════════════════════════
// RESOURCE TYPE CHIP
// ═══════════════════════════════════════════════════════════════════════

@Composable
private fun ResourceTypeChip(
    type: ResourceType,
    isSelected: Boolean,
    enabled: Boolean,
    isDark: Boolean,
    onClick: () -> Unit
) {
    val scale by animateFloatAsState(
        targetValue   = if (isSelected) 1.04f else 1f,
        animationSpec = spring(dampingRatio = 0.5f, stiffness = 400f),
        label         = "chipScale"
    )

    val selectedGradient = if (isDark) type.selectedGradientDark else type.selectedGradientLight

    val unselectedBg = if (isDark)
        GlassDark.GlassRegular.copy(alpha = 0.40f)
    else
        GlassLight.GlassRegular.copy(alpha = 0.50f)

    Box(
        modifier = Modifier
            .scale(scale)
            .clip(RoundedCornerShape(22.dp))
            .then(
                if (isSelected) Modifier.background(Brush.linearGradient(selectedGradient))
                else            Modifier.background(unselectedBg)
            )
            // FIX: Removed custom interactionSource + indication = null.
            // Using the default clickable provides correct ripple and ensures
            // touch events are properly registered and not silently dropped.
            .clickable(enabled = enabled, onClick = onClick)
    ) {
        if (isSelected) {
            Box(
                modifier = Modifier
                    .matchParentSize()
                    .background(
                        Brush.radialGradient(
                            listOf(type.glowColor.copy(alpha = 0.18f), Color.Transparent)
                        )
                    )
            )
        }

        Row(
            modifier              = Modifier.padding(start = 10.dp, end = 14.dp, top = 9.dp, bottom = 9.dp),
            horizontalArrangement = Arrangement.spacedBy(7.dp),
            verticalAlignment     = Alignment.CenterVertically
        ) {
            Box(
                modifier = Modifier
                    .size(26.dp)
                    .clip(CircleShape)
                    .background(
                        if (isSelected) Color.White.copy(alpha = 0.20f)
                        else            type.badgeColor.copy(alpha = if (isDark) 0.30f else 0.15f)
                    ),
                contentAlignment = Alignment.Center
            ) {
                AnimatedContent(
                    targetState  = isSelected,
                    transitionSpec = {
                        (scaleIn(tween(180, easing = EaseOutBack)) + fadeIn()) togetherWith
                                (scaleOut(tween(120)) + fadeOut())
                    },
                    label = "emojiCheck"
                ) { selected ->
                    if (selected) {
                        Icon(
                            imageVector        = Icons.Default.Check,
                            contentDescription = "Selected",
                            modifier           = Modifier.size(14.dp),
                            tint               = Color.White
                        )
                    } else {
                        Text(text = type.emoji, fontSize = 13.sp)
                    }
                }
            }

            Text(
                text  = type.displayName,
                style = GlassTypography.VibrancyPrimary.copy(
                    fontSize   = 13.sp,
                    fontWeight = if (isSelected) FontWeight.SemiBold else FontWeight.Normal
                ),
                color = when {
                    !enabled   -> MaterialTheme.colorScheme.onSurface.copy(alpha = 0.35f)
                    isSelected -> Color.White
                    isDark     -> MaterialTheme.colorScheme.onSurface.copy(alpha = 0.80f)
                    else       -> MaterialTheme.colorScheme.onSurfaceVariant
                }
            )
        }
    }
}

// ═══════════════════════════════════════════════════════════════════════
// TAGS PREVIEW
// ═══════════════════════════════════════════════════════════════════════

@Composable
private fun TagsPreview(tags: Set<String>) {
    val isDark = isSystemInDarkTheme()

    Surface(
        shape  = RoundedCornerShape(16.dp),
        color  = if (isDark) GlassDark.SurfaceVariant.copy(alpha = 0.50f)
        else        GlassLight.SurfaceVariant.copy(alpha = 0.50f),
        border = BorderStroke(1.dp, glassBorderColor().copy(alpha = 0.35f))
    ) {
        Column(
            modifier            = Modifier.padding(14.dp),
            verticalArrangement = Arrangement.spacedBy(10.dp)
        ) {
            Row(
                horizontalArrangement = Arrangement.spacedBy(6.dp),
                verticalAlignment     = Alignment.CenterVertically
            ) {
                Icon(
                    imageVector        = Icons.Rounded.CheckCircle,
                    contentDescription = null,
                    modifier           = Modifier.size(15.dp),
                    tint               = MaterialTheme.colorScheme.primary
                )
                Text(
                    text  = "${tags.size} tag${if (tags.size > 1) "s" else ""} will be saved",
                    style = GlassTypography.VibrancySecondary.copy(
                        fontSize   = 12.sp,
                        fontWeight = FontWeight.Medium
                    ),
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }

            Divider(color = glassBorderColor().copy(alpha = 0.25f))

            ChipFlowLayout(
                horizontalGap = 5.dp,
                verticalGap   = 5.dp,
                modifier      = Modifier.fillMaxWidth()
            ) {
                tags.forEach { tag ->
                    Surface(
                        shape = RoundedCornerShape(6.dp),
                        color = MaterialTheme.colorScheme.primary.copy(alpha = 0.12f)
                    ) {
                        Text(
                            text     = tag,
                            modifier = Modifier.padding(horizontal = 8.dp, vertical = 3.dp),
                            style    = MaterialTheme.typography.labelSmall,
                            color    = MaterialTheme.colorScheme.primary
                        )
                    }
                }
            }
        }
    }
}

// ═══════════════════════════════════════════════════════════════════════
// CHIP FLOW LAYOUT
// ═══════════════════════════════════════════════════════════════════════

@Composable
private fun ChipFlowLayout(
    modifier: Modifier = Modifier,
    horizontalGap: androidx.compose.ui.unit.Dp = 8.dp,
    verticalGap: androidx.compose.ui.unit.Dp = 8.dp,
    content: @Composable () -> Unit
) {
    androidx.compose.ui.layout.Layout(
        content  = content,
        modifier = modifier
    ) { measurables, constraints ->
        val hGap = horizontalGap.roundToPx()
        val vGap = verticalGap.roundToPx()
        val placeables = measurables.map { it.measure(constraints.copy(minWidth = 0)) }

        val rows = mutableListOf<List<androidx.compose.ui.layout.Placeable>>()
        var currentRow = mutableListOf<androidx.compose.ui.layout.Placeable>()
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

// ═══════════════════════════════════════════════════════════════════════
// IMAGE PICKER
// ═══════════════════════════════════════════════════════════════════════

@Composable
private fun GlassImagePicker(
    uri: Uri?,
    isLoading: Boolean,
    onClick: () -> Unit,
    onRemove: () -> Unit,
    enabled: Boolean
) {
    val isDark  = isSystemInDarkTheme()
    // FIX: LocalContext needed to build a proper ImageRequest.
    // Without it, Coil cannot resolve content:// URIs returned by GetContent()
    // on many devices, so the image silently fails to load.
    val context = LocalContext.current

    Box(
        modifier = Modifier
            .fillMaxWidth()
            .height(220.dp)
            .clip(RoundedCornerShape(20.dp))
            .background(if (isDark) GlassDark.SurfaceVariant else GlassLight.SurfaceVariant)
            // FIX: Only allow clicking the empty state to open picker.
            // When an image is shown, the clickable area below handles re-picking.
            .clickable(enabled = enabled && !isLoading && uri == null, onClick = onClick),
        contentAlignment = Alignment.Center
    ) {
        if (isDark) {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .background(
                        Brush.radialGradient(
                            listOf(Accents.BlueDark.copy(alpha = 0.07f), Color.Transparent)
                        )
                    )
            )
        }

        when {
            isLoading -> CircularProgressIndicator(
                color    = MaterialTheme.colorScheme.primary,
                modifier = Modifier.size(44.dp)
            )

            uri != null -> {
                // FIX: Use ImageRequest.Builder with context for reliable
                // content:// URI resolution across all Android API levels.
                AsyncImage(
                    model = ImageRequest.Builder(context)
                        .data(uri)
                        .crossfade(true)
                        .build(),
                    contentDescription = "Selected image",
                    modifier           = Modifier.fillMaxSize(),
                    contentScale       = ContentScale.Crop
                )
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .background(
                            Brush.verticalGradient(
                                listOf(Color.Black.copy(alpha = 0.25f), Color.Transparent),
                                endY = 120f
                            )
                        )
                )
                IconButton(
                    onClick  = onRemove,
                    modifier = Modifier
                        .align(Alignment.TopEnd)
                        .padding(10.dp)
                        .size(34.dp)
                        .background(Color.Black.copy(alpha = 0.55f), CircleShape)
                ) {
                    Icon(
                        imageVector        = Icons.Default.Close,
                        contentDescription = "Remove image",
                        tint               = Color.White,
                        modifier           = Modifier.size(16.dp)
                    )
                }
            }

            else -> {
                Column(
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.spacedBy(14.dp)
                ) {
                    Surface(
                        shape    = RoundedCornerShape(18.dp),
                        color    = if (isDark) GlassDark.GlassThin else GlassLight.GlassThin,
                        modifier = Modifier.size(68.dp)
                    ) {
                        Box(contentAlignment = Alignment.Center) {
                            Icon(
                                imageVector        = Icons.Default.Image,
                                contentDescription = null,
                                modifier           = Modifier.size(32.dp),
                                tint               = MaterialTheme.colorScheme.primary
                            )
                        }
                    }
                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        Text(
                            text  = "Tap to select image",
                            style = GlassTypography.VibrancyPrimary.copy(fontWeight = FontWeight.Medium),
                            color = MaterialTheme.colorScheme.onSurface
                        )
                        Spacer(Modifier.height(2.dp))
                        Text(
                            text  = "JPG, PNG, WEBP — max 32 MB",
                            style = GlassTypography.VibrancySecondary.copy(fontSize = 12.sp),
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                }
            }
        }
    }
}

// ═══════════════════════════════════════════════════════════════════════
// OUTLINED TEXT FIELD
// ═══════════════════════════════════════════════════════════════════════

@Composable
private fun GlassOutlinedTextField(
    value: String,
    onValueChange: (String) -> Unit,
    label: String,
    placeholder: String = "",
    enabled: Boolean,
    keyboardOptions: KeyboardOptions = KeyboardOptions.Default,
    keyboardActions: KeyboardActions = KeyboardActions.Default,
    modifier: Modifier = Modifier
) {
    val isDark = isSystemInDarkTheme()

    OutlinedTextField(
        value           = value,
        onValueChange   = onValueChange,
        label           = { Text(label, style = GlassTypography.VibrancySecondary) },
        placeholder     = { Text(placeholder, style = GlassTypography.VibrancySecondary.copy(fontSize = 13.sp)) },
        enabled         = enabled,
        keyboardOptions = keyboardOptions,
        keyboardActions = keyboardActions,
        modifier        = modifier,
        shape           = RoundedCornerShape(12.dp),
        colors          = OutlinedTextFieldDefaults.colors(
            focusedContainerColor   = if (isDark) GlassDark.GlassThin      else GlassLight.GlassThin,
            unfocusedContainerColor = if (isDark) GlassDark.GlassUltraThin else GlassLight.GlassUltraThin,
            focusedBorderColor      = MaterialTheme.colorScheme.primary.copy(alpha = 0.8f),
            unfocusedBorderColor    = glassBorderColor().copy(alpha = 0.5f),
            disabledBorderColor     = Color.Transparent
        ),
        textStyle  = GlassTypography.OnGlassRegular,
        singleLine = true
    )
}

// ═══════════════════════════════════════════════════════════════════════
// ERROR SURFACE
// ═══════════════════════════════════════════════════════════════════════

@Composable
private fun GlassErrorSurface(message: String, onDismiss: () -> Unit) {
    Surface(
        modifier = Modifier.fillMaxWidth(),
        shape    = RoundedCornerShape(16.dp),
        color    = MaterialTheme.colorScheme.errorContainer.copy(alpha = 0.85f)
    ) {
        Row(
            modifier              = Modifier
                .padding(16.dp)
                .fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment     = Alignment.CenterVertically
        ) {
            Text(
                text     = message,
                color    = MaterialTheme.colorScheme.onErrorContainer,
                style    = MaterialTheme.typography.bodyMedium,
                modifier = Modifier.weight(1f)
            )
            TextButton(onClick = onDismiss) { Text("Dismiss") }
        }
    }
}

// ═══════════════════════════════════════════════════════════════════════
// UPLOAD BUTTON
// ═══════════════════════════════════════════════════════════════════════

@Composable
private fun GlassUploadButton(
    isLoading: Boolean,
    enabled: Boolean,
    tagCount: Int,
    onClick: () -> Unit
) {
    val isDark      = isSystemInDarkTheme()
    val useGradient = enabled && isDark

    val alpha by animateFloatAsState(
        targetValue   = if (enabled) 1f else 0.50f,
        animationSpec = tween(200),
        label         = "btnAlpha"
    )

    Box(
        modifier = Modifier
            .fillMaxWidth()
            .height(56.dp)
            .alpha(alpha)
            .clip(RoundedCornerShape(16.dp))
            .then(
                if (useGradient) Modifier.background(
                    Brush.horizontalGradient(listOf(Color(0xFF6A1B9A), Color(0xFF00695C)))
                ) else Modifier
            ),
        contentAlignment = Alignment.Center
    ) {
        Button(
            onClick        = onClick,
            modifier       = Modifier.fillMaxSize(),
            enabled        = enabled,
            shape          = RoundedCornerShape(16.dp),
            colors         = ButtonDefaults.buttonColors(
                containerColor         = if (useGradient) Color.Transparent
                else                     MaterialTheme.colorScheme.primary,
                disabledContainerColor = if (isDark) GlassDark.SurfaceVariant
                else                     GlassLight.SurfaceVariant
            ),
            elevation      = ButtonDefaults.buttonElevation(0.dp, 0.dp),
            contentPadding = PaddingValues(horizontal = 24.dp)
        ) {
            if (isLoading) {
                CircularProgressIndicator(
                    modifier    = Modifier.size(22.dp),
                    color       = Color.White,
                    strokeWidth = 2.dp
                )
            } else {
                Row(
                    horizontalArrangement = Arrangement.spacedBy(10.dp),
                    verticalAlignment     = Alignment.CenterVertically
                ) {
                    Text(
                        text  = "Upload Resource",
                        style = GlassTypography.VibrancyPrimary.copy(
                            fontSize   = 15.sp,
                            fontWeight = FontWeight.Bold
                        ),
                        color = if (enabled) Color.White
                        else   MaterialTheme.colorScheme.onSurfaceVariant
                    )
                    if (enabled && tagCount > 0) {
                        Surface(
                            shape = CircleShape,
                            color = Color.White.copy(alpha = 0.22f)
                        ) {
                            Text(
                                text     = "$tagCount tag${if (tagCount > 1) "s" else ""}",
                                modifier = Modifier.padding(horizontal = 8.dp, vertical = 2.dp),
                                style    = MaterialTheme.typography.labelSmall,
                                color    = Color.White
                            )
                        }
                    }
                }
            }
        }
    }
}