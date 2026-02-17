package com.nidoham.bhubishwo.admin.presentation.screen.creator

import android.net.Uri
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.animateContentSize
import androidx.compose.animation.core.EaseOutBack
import androidx.compose.animation.core.EaseOutCubic
import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.animateDpAsState
import androidx.compose.animation.core.animateFloatAsState
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
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
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
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.scale
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
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

// ═══════════════════════════════════════════════════════════════════════
// DATA CLASSES & SEALED CLASSES
// ═══════════════════════════════════════════════════════════════════════

data class ResourceFormState(
    val title: String = "",
    val selectedImageUri: Uri? = null,
    val selectedTypes: Set<ResourceType> = emptySet(),
    val customTags: List<String> = emptyList(),        // ← free-form extra tags
    val currentTagInput: String = "",                   // ← text field for tag entry
    val isLoading: Boolean = false,
    val isImageLoading: Boolean = false,
    val errorMessage: String? = null
) {
    /** All tags combined: type IDs + custom free-form tags */
    val allTags: Set<String>
        get() = selectedTypes.map { it.id }.toSet() + customTags.toSet()
}

sealed class ResourceType(
    val id: String,
    val displayName: String,
    val emoji: String,
    // Each type has its own unique selected gradient
    val selectedGradientDark: List<Color>,
    val selectedGradientLight: List<Color>,
    val badgeColor: Color,           // pill badge behind emoji
    val glowColor: Color             // shadow / border glow when selected
) {
    data object Country : ResourceType(
        id                   = "country",
        displayName          = "Country",
        emoji                = "🌍",
        selectedGradientDark = listOf(Color(0xFF1565C0), Color(0xFF0D47A1)),
        selectedGradientLight= listOf(Color(0xFF42A5F5), Color(0xFF1E88E5)),
        badgeColor           = Color(0xFF1976D2),
        glowColor            = Color(0xFF2196F3)
    )
    data object Flag : ResourceType(
        id                   = "flag",
        displayName          = "Flag",
        emoji                = "🚩",
        selectedGradientDark = listOf(Color(0xFF6A1B9A), Color(0xFF4A148C)),
        selectedGradientLight= listOf(Color(0xFFCE93D8), Color(0xFFAB47BC)),
        badgeColor           = Color(0xFF7B1FA2),
        glowColor            = Color(0xFF9C27B0)
    )
    data object Tourist : ResourceType(
        id                   = "tourist",
        displayName          = "Tourist",
        emoji                = "📸",
        selectedGradientDark = listOf(Color(0xFF00695C), Color(0xFF004D40)),
        selectedGradientLight= listOf(Color(0xFF4DB6AC), Color(0xFF26A69A)),
        badgeColor           = Color(0xFF00796B),
        glowColor            = Color(0xFF009688)
    )
    data object Landmark : ResourceType(
        id                   = "landmark",
        displayName          = "Landmark",
        emoji                = "🏛️",
        selectedGradientDark = listOf(Color(0xFFB71C1C), Color(0xFF7F0000)),
        selectedGradientLight= listOf(Color(0xFFEF9A9A), Color(0xFFE57373)),
        badgeColor           = Color(0xFFC62828),
        glowColor            = Color(0xFFF44336)
    )
    data object Culture : ResourceType(
        id                   = "culture",
        displayName          = "Culture",
        emoji                = "🎭",
        selectedGradientDark = listOf(Color(0xFFE65100), Color(0xFFBF360C)),
        selectedGradientLight= listOf(Color(0xFFFFCC80), Color(0xFFFFA726)),
        badgeColor           = Color(0xFFF57C00),
        glowColor            = Color(0xFFFF9800)
    )
    data object Nature : ResourceType(
        id                   = "nature",
        displayName          = "Nature",
        emoji                = "🌿",
        selectedGradientDark = listOf(Color(0xFF2E7D32), Color(0xFF1B5E20)),
        selectedGradientLight= listOf(Color(0xFFA5D6A7), Color(0xFF66BB6A)),
        badgeColor           = Color(0xFF388E3C),
        glowColor            = Color(0xFF4CAF50)
    )
    data object Food : ResourceType(
        id                   = "food",
        displayName          = "Food",
        emoji                = "🍜",
        selectedGradientDark = listOf(Color(0xFF4E342E), Color(0xFF3E2723)),
        selectedGradientLight= listOf(Color(0xFFBCAAA4), Color(0xFF8D6E63)),
        badgeColor           = Color(0xFF5D4037),
        glowColor            = Color(0xFF795548)
    )
    data object Festival : ResourceType(
        id                   = "festival",
        displayName          = "Festival",
        emoji                = "🎉",
        selectedGradientDark = listOf(Color(0xFFC62828), Color(0xFF880E4F)),
        selectedGradientLight= listOf(Color(0xFFF48FB1), Color(0xFFEC407A)),
        badgeColor           = Color(0xFFAD1457),
        glowColor            = Color(0xFFE91E63)
    )

    companion object {
        val all: List<ResourceType> = listOf(
            Country, Flag, Tourist, Landmark, Culture, Nature, Food, Festival
        )
    }
}

sealed class ResourceFormEvent {
    data class TitleChanged(val title: String)          : ResourceFormEvent()
    data class ImageSelected(val uri: Uri?)             : ResourceFormEvent()
    data class ImageLoading(val loading: Boolean)       : ResourceFormEvent()
    data class TypeToggled(val type: ResourceType)      : ResourceFormEvent()
    data class TagInputChanged(val value: String)       : ResourceFormEvent()
    data class CustomTagAdded(val tag: String)          : ResourceFormEvent()
    data class CustomTagRemoved(val tag: String)        : ResourceFormEvent()
    data object UploadClicked                           : ResourceFormEvent()
    data object ErrorDismissed                          : ResourceFormEvent()
}

// ═══════════════════════════════════════════════════════════════════════
// ROOT SCREEN — local state holder
// ═══════════════════════════════════════════════════════════════════════

@Composable
fun ResourceCreatorScreen() {
    var state by remember { mutableStateOf(ResourceFormState()) }

    ResourceCreatorContent(
        state   = state,
        onEvent = { event ->
            state = when (event) {
                is ResourceFormEvent.TitleChanged    -> state.copy(title = event.title)
                is ResourceFormEvent.ImageSelected   -> state.copy(selectedImageUri = event.uri, isImageLoading = false)
                is ResourceFormEvent.ImageLoading    -> state.copy(isImageLoading = event.loading)
                is ResourceFormEvent.TypeToggled     -> {
                    val updated = if (event.type in state.selectedTypes)
                        state.selectedTypes - event.type
                    else
                        state.selectedTypes + event.type
                    state.copy(selectedTypes = updated)
                }
                is ResourceFormEvent.TagInputChanged -> state.copy(currentTagInput = event.value)
                is ResourceFormEvent.CustomTagAdded  -> {
                    val tag = event.tag.trim().lowercase()
                    if (tag.isNotBlank() && tag !in state.customTags)
                        state.copy(customTags = state.customTags + tag, currentTagInput = "")
                    else
                        state.copy(currentTagInput = "")
                }
                is ResourceFormEvent.CustomTagRemoved -> {
                    state.copy(customTags = state.customTags - event.tag)
                }
                is ResourceFormEvent.UploadClicked   -> state.copy(isLoading = true)
                is ResourceFormEvent.ErrorDismissed  -> state.copy(errorMessage = null)
            }
        }
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

        // ── Header ────────────────────────────────────────────
        ScreenHeader(isDark = isDark)

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

        // ── Resource Types (multi-select chips) ───────────────
        ResourceTypeSection(
            selectedTypes = state.selectedTypes,
            enabled       = !state.isLoading,
            onToggle      = { onEvent(ResourceFormEvent.TypeToggled(it)) }
        )

        // ── Custom Tags ───────────────────────────────────────
        CustomTagsSection(
            customTags      = state.customTags,
            tagInput        = state.currentTagInput,
            enabled         = !state.isLoading,
            onInputChange   = { onEvent(ResourceFormEvent.TagInputChanged(it)) },
            onAddTag        = { onEvent(ResourceFormEvent.CustomTagAdded(it)) },
            onRemoveTag     = { onEvent(ResourceFormEvent.CustomTagRemoved(it)) },
            focusManager    = focusManager
        )

        // ── Combined tag preview ──────────────────────────────
        AnimatedVisibility(visible = state.allTags.isNotEmpty()) {
            AllTagsPreview(tags = state.allTags)
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
                    state.allTags.isNotEmpty(),
            tagCount  = state.allTags.size,
            onClick   = { onEvent(ResourceFormEvent.UploadClicked) }
        )

        Spacer(Modifier.height(16.dp))
    }
}

// ═══════════════════════════════════════════════════════════════════════
// SCREEN HEADER
// ═══════════════════════════════════════════════════════════════════════

@Composable
private fun ScreenHeader(isDark: Boolean) {
    Column(verticalArrangement = Arrangement.spacedBy(4.dp)) {
        Text(
            text       = "Create Resource",
            style      = GlassTypography.VibrancyPrimary.copy(
                fontSize   = 26.sp,
                fontWeight = FontWeight.Bold
            ),
            color      = MaterialTheme.colorScheme.onSurface
        )
        Text(
            text  = "Upload & tag a new media resource",
            style = GlassTypography.VibrancySecondary.copy(fontSize = 14.sp),
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )
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

        // Header row
        Row(
            modifier              = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment     = Alignment.CenterVertically
        ) {
            Column {
                Text(
                    text  = "Resource Types",
                    style = GlassTypography.VibrancyPrimary.copy(fontWeight = FontWeight.SemiBold),
                    color = MaterialTheme.colorScheme.onSurface
                )
                Text(
                    text  = "Select all that apply",
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

        // ── Version-safe wrapping chip grid ──
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
// RESOURCE TYPE CHIP — each type has its OWN distinct selected style
// ═══════════════════════════════════════════════════════════════════════

@Composable
private fun ResourceTypeChip(
    type: ResourceType,
    isSelected: Boolean,
    enabled: Boolean,
    isDark: Boolean,
    onClick: () -> Unit
) {
    // Animate scale for a satisfying "pop" on select
    val scale by animateFloatAsState(
        targetValue = if (isSelected) 1.04f else 1f,
        animationSpec = spring(dampingRatio = 0.5f, stiffness = 400f),
        label = "chipScale"
    )

    // Animate shadow elevation
    val elevation by animateDpAsState(
        targetValue   = if (isSelected) 6.dp else 0.dp,
        animationSpec = tween(200, easing = FastOutSlowInEasing),
        label         = "chipElevation"
    )

    val selectedGradient = if (isDark) type.selectedGradientDark else type.selectedGradientLight

    // Unselected glass surface colors
    val unselectedBg = if (isDark)
        GlassDark.GlassRegular.copy(alpha = 0.40f)
    else
        GlassLight.GlassRegular.copy(alpha = 0.50f)

    val border = when {
        isSelected -> BorderStroke(1.5.dp, type.glowColor.copy(alpha = 0.70f))
        else       -> BorderStroke(
            1.dp,
            if (isDark) GlassDark.GlassBorderSubtle.copy(alpha = 0.45f)
            else        GlassLight.GlassBorderSubtle.copy(alpha = 0.35f)
        )
    }

    Box(
        modifier = Modifier
            .scale(scale)
            .clip(RoundedCornerShape(22.dp))
            .then(
                if (isSelected) Modifier.background(
                    Brush.linearGradient(selectedGradient)
                ) else Modifier.background(unselectedBg)
            )
            .then(
                Modifier.clickable(
                    enabled           = enabled,
                    interactionSource = remember { MutableInteractionSource() },
                    indication        = null,
                    onClick           = onClick
                )
            )
    ) {
        // Glow ring overlay on selected
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

        // Chip content
        Row(
            modifier              = Modifier.padding(start = 10.dp, end = 14.dp, top = 9.dp, bottom = 9.dp),
            horizontalArrangement = Arrangement.spacedBy(7.dp),
            verticalAlignment     = Alignment.CenterVertically
        ) {
            // Left: badge circle with emoji — color matches type
            Box(
                modifier        = Modifier
                    .size(26.dp)
                    .clip(CircleShape)
                    .background(
                        if (isSelected) Color.White.copy(alpha = 0.20f)
                        else            type.badgeColor.copy(alpha = if (isDark) 0.30f else 0.15f)
                    ),
                contentAlignment = Alignment.Center
            ) {
                AnimatedContent(
                    targetState   = isSelected,
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
                        Text(
                            text     = type.emoji,
                            fontSize = 13.sp
                        )
                    }
                }
            }

            // Label
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
// CUSTOM TAGS SECTION
// ═══════════════════════════════════════════════════════════════════════

@Composable
private fun CustomTagsSection(
    customTags: List<String>,
    tagInput: String,
    enabled: Boolean,
    onInputChange: (String) -> Unit,
    onAddTag: (String) -> Unit,
    onRemoveTag: (String) -> Unit,
    focusManager: androidx.compose.ui.focus.FocusManager
) {
    val isDark = isSystemInDarkTheme()

    Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
        Text(
            text  = "Custom Tags",
            style = GlassTypography.VibrancyPrimary.copy(fontWeight = FontWeight.SemiBold),
            color = MaterialTheme.colorScheme.onSurface
        )
        Text(
            text  = "Add extra searchable keywords",
            style = GlassTypography.VibrancySecondary.copy(fontSize = 12.sp),
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )

        // Input row
        Row(
            modifier          = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(8.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            OutlinedTextField(
                value         = tagInput,
                onValueChange = onInputChange,
                placeholder   = { Text("e.g. mountains, historic, hidden-gem", fontSize = 12.sp) },
                enabled       = enabled,
                singleLine    = true,
                modifier      = Modifier.weight(1f),
                shape         = RoundedCornerShape(12.dp),
                keyboardOptions = KeyboardOptions(imeAction = ImeAction.Done),
                keyboardActions = KeyboardActions(
                    onDone = {
                        if (tagInput.isNotBlank()) onAddTag(tagInput)
                        else focusManager.clearFocus()
                    }
                ),
                colors = OutlinedTextFieldDefaults.colors(
                    focusedContainerColor   = if (isDark) GlassDark.GlassThin     else GlassLight.GlassThin,
                    unfocusedContainerColor = if (isDark) GlassDark.GlassUltraThin else GlassLight.GlassUltraThin,
                    focusedBorderColor      = MaterialTheme.colorScheme.primary.copy(alpha = 0.8f),
                    unfocusedBorderColor    = glassBorderColor().copy(alpha = 0.5f)
                ),
                textStyle = GlassTypography.OnGlassRegular.copy(fontSize = 13.sp)
            )

            // Add button
            Surface(
                shape    = RoundedCornerShape(12.dp),
                color    = if (tagInput.isNotBlank() && enabled)
                    MaterialTheme.colorScheme.primary
                else
                    MaterialTheme.colorScheme.surfaceVariant,
                modifier = Modifier
                    .size(48.dp)
                    .clickable(
                        enabled = tagInput.isNotBlank() && enabled,
                        onClick = { onAddTag(tagInput) }
                    )
            ) {
                Box(contentAlignment = Alignment.Center) {
                    Icon(
                        imageVector        = Icons.Default.Add,
                        contentDescription = "Add tag",
                        tint               = if (tagInput.isNotBlank() && enabled)
                            MaterialTheme.colorScheme.onPrimary
                        else
                            MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.4f),
                        modifier           = Modifier.size(20.dp)
                    )
                }
            }
        }

        // Existing custom tags as removable chips
        AnimatedVisibility(visible = customTags.isNotEmpty()) {
            ChipFlowLayout(
                horizontalGap = 6.dp,
                verticalGap   = 6.dp,
                modifier      = Modifier.fillMaxWidth()
            ) {
                customTags.forEach { tag ->
                    CustomTagChip(
                        tag      = tag,
                        enabled  = enabled,
                        isDark   = isDark,
                        onRemove = { onRemoveTag(tag) }
                    )
                }
            }
        }
    }
}

@Composable
private fun CustomTagChip(
    tag: String,
    enabled: Boolean,
    isDark: Boolean,
    onRemove: () -> Unit
) {
    Surface(
        shape  = RoundedCornerShape(20.dp),
        color  = if (isDark)
            MaterialTheme.colorScheme.secondaryContainer.copy(alpha = 0.70f)
        else
            MaterialTheme.colorScheme.secondaryContainer,
        border = BorderStroke(1.dp, MaterialTheme.colorScheme.secondary.copy(alpha = 0.30f))
    ) {
        Row(
            modifier              = Modifier.padding(start = 10.dp, end = 6.dp, top = 6.dp, bottom = 6.dp),
            horizontalArrangement = Arrangement.spacedBy(4.dp),
            verticalAlignment     = Alignment.CenterVertically
        ) {
            Text(
                text  = "#$tag",
                style = MaterialTheme.typography.labelMedium,
                color = MaterialTheme.colorScheme.onSecondaryContainer
            )
            if (enabled) {
                Box(
                    modifier = Modifier
                        .size(18.dp)
                        .clip(CircleShape)
                        .background(MaterialTheme.colorScheme.onSecondaryContainer.copy(alpha = 0.12f))
                        .clickable(onClick = onRemove),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        imageVector        = Icons.Default.Close,
                        contentDescription = "Remove $tag",
                        modifier           = Modifier.size(10.dp),
                        tint               = MaterialTheme.colorScheme.onSecondaryContainer
                    )
                }
            }
        }
    }
}

// ═══════════════════════════════════════════════════════════════════════
// ALL TAGS PREVIEW — combined type + custom tags at a glance
// ═══════════════════════════════════════════════════════════════════════

@Composable
private fun AllTagsPreview(tags: Set<String>) {
    val isDark = isSystemInDarkTheme()

    Surface(
        shape  = RoundedCornerShape(16.dp),
        color  = if (isDark) GlassDark.SurfaceVariant.copy(alpha = 0.50f)
        else   GlassLight.SurfaceVariant.copy(alpha = 0.50f),
        border = BorderStroke(1.dp, glassBorderColor().copy(alpha = 0.35f))
    ) {
        Column(
            modifier = Modifier.padding(14.dp),
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
                    text  = "${tags.size} tags will be saved",
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
// CHIP FLOW LAYOUT — version-safe wrapping layout (no FlowRow API needed)
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
    val isDark = isSystemInDarkTheme()

    Box(
        modifier = Modifier
            .fillMaxWidth()
            .height(220.dp)
            .clip(RoundedCornerShape(20.dp))
            .background(if (isDark) GlassDark.SurfaceVariant else GlassLight.SurfaceVariant)
            .clickable(enabled = enabled && !isLoading, onClick = onClick),
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
                AsyncImage(
                    model              = uri,
                    contentDescription = "Selected image",
                    modifier           = Modifier.fillMaxSize(),
                    contentScale       = ContentScale.Crop
                )
                // Overlay gradient for legibility of remove button
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
            modifier              = Modifier.padding(16.dp).fillMaxWidth(),
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
    val isDark          = isSystemInDarkTheme()
    val useGradient     = enabled && isDark

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
                    Brush.horizontalGradient(listOf(Color(0xFF1565C0), Color(0xFF6A1B9A)))
                ) else Modifier
            ),
        contentAlignment = Alignment.Center
    ) {
        Button(
            onClick   = onClick,
            modifier  = Modifier.fillMaxSize(),
            enabled   = enabled,
            shape     = RoundedCornerShape(16.dp),
            colors    = ButtonDefaults.buttonColors(
                containerColor         = if (useGradient) Color.Transparent
                else MaterialTheme.colorScheme.primary,
                disabledContainerColor = if (isDark) GlassDark.SurfaceVariant
                else GlassLight.SurfaceVariant
            ),
            elevation = ButtonDefaults.buttonElevation(0.dp, 0.dp),
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
                    // Show tag count badge inline
                    if (enabled && tagCount > 0) {
                        Surface(
                            shape = CircleShape,
                            color = Color.White.copy(alpha = 0.22f)
                        ) {
                            Text(
                                text     = "$tagCount tags",
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