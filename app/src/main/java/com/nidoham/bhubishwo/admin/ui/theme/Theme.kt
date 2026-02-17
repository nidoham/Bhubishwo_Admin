package com.nidoham.bhubishwo.admin.ui.theme

import android.app.Activity
import android.os.Build
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Typography
import androidx.compose.material3.ColorScheme
import androidx.compose.material3.Shapes
import androidx.compose.material3.dynamicDarkColorScheme
import androidx.compose.material3.dynamicLightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.Immutable
import androidx.compose.runtime.ReadOnlyComposable
import androidx.compose.runtime.compositionLocalOf
import androidx.compose.runtime.staticCompositionLocalOf
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalView
import androidx.compose.ui.unit.dp
import androidx.core.view.WindowCompat

// ╔══════════════════════════════════════════════════════════╗
// ║         PREMIUM iOS-GLASS ADMIN THEME SYSTEM            ║
// ╚══════════════════════════════════════════════════════════╝

// ══════════════════════════════════════════
// GLASS THEME CONFIGURATION
// ══════════════════════════════════════════

@Immutable
data class GlassThemeConfig(
    val blurRadius       : Float   = 24f,
    val noiseIntensity   : Float   = 0.010f,
    val saturationBoost  : Float   = 1.25f,
    val brightnessBoost  : Float   = 1.10f,
    val useVibrancy      : Boolean = true,
    val backdropAlpha    : Float   = 0.75f,
    val elevationOverlay : Boolean = true,
    val borderGlow       : Boolean = true      // electric blue borders
)

@Immutable
data class DisplayConfig(
    val profile             : DisplayProfile = DisplayProfile.SuperRetina,
    val applyGammaCorrection: Boolean        = true,
    val boostBrightness     : Boolean        = true,
    val contrastEnhancement : Float          = 1.15f,
    val enableHDR           : Boolean        = false
)

// ══════════════════════════════════════════
// COMPOSITION LOCALS
// ══════════════════════════════════════════

val LocalGlassConfig    = staticCompositionLocalOf { GlassThemeConfig() }
val LocalDisplayConfig  = staticCompositionLocalOf { DisplayConfig()    }
val LocalIsGlassSurface = compositionLocalOf      { false              }

// ══════════════════════════════════════════
// SHAPES — iOS Squircle (Continuous Corner)
// Approximated with generous corner radii
// ══════════════════════════════════════════

val AdminShapes = Shapes(
    extraSmall = RoundedCornerShape(8.dp),    // pills, tags, chips
    small      = RoundedCornerShape(12.dp),   // buttons, text fields
    medium     = RoundedCornerShape(16.dp),   // cards, tiles
    large      = RoundedCornerShape(22.dp),   // bottom sheets, dialogs
    extraLarge = RoundedCornerShape(32.dp),   // full modal cards
)

// ══════════════════════════════════════════
// ADMIN THEME OBJECT
// ══════════════════════════════════════════

object AdminTheme {
    val colorScheme: ColorScheme
        @Composable @ReadOnlyComposable get() = MaterialTheme.colorScheme

    val typography: Typography
        @Composable @ReadOnlyComposable get() = MaterialTheme.typography

    val glassConfig: GlassThemeConfig
        @Composable @ReadOnlyComposable get() = LocalGlassConfig.current

    val displayConfig: DisplayConfig
        @Composable @ReadOnlyComposable get() = LocalDisplayConfig.current

    val shapes: Shapes
        @Composable @ReadOnlyComposable get() = MaterialTheme.shapes
}

// ══════════════════════════════════════════
// GLASS SURFACE WRAPPER
// ══════════════════════════════════════════

@Composable
fun GlassSurface(
    config  : GlassThemeConfig = GlassThemeConfig(),
    content : @Composable () -> Unit
) {
    CompositionLocalProvider(
        LocalGlassConfig    provides config,
        LocalIsGlassSurface provides true
    ) { content() }
}

// ══════════════════════════════════════════
// MAIN THEME COMPOSABLE
// ══════════════════════════════════════════

@Composable
fun AdminTheme(
    darkTheme    : Boolean          = isSystemInDarkTheme(),
    dynamicColor : Boolean          = false,
    glassConfig  : GlassThemeConfig = GlassThemeConfig(),
    displayConfig: DisplayConfig    = DisplayConfig(),
    content      : @Composable () -> Unit
) {
    val context = LocalContext.current
    val view    = LocalView.current

    val colorScheme: ColorScheme = when {
        dynamicColor && Build.VERSION.SDK_INT >= Build.VERSION_CODES.S ->
            if (darkTheme) dynamicDarkColorScheme(context)
            else           dynamicLightColorScheme(context)
        darkTheme -> DarkColorScheme
        else      -> LightColorScheme
    }

    // Full edge-to-edge — system bars blend into glass bg
    if (!view.isInEditMode) {
        (context as? Activity)?.window?.let { win ->
            WindowCompat.setDecorFitsSystemWindows(win, false)
            WindowCompat.getInsetsController(win, view).apply {
                isAppearanceLightStatusBars     = !darkTheme
                isAppearanceLightNavigationBars = !darkTheme
            }
        }
    }

    CompositionLocalProvider(
        LocalGlassConfig   provides glassConfig,
        LocalDisplayConfig provides displayConfig
    ) {
        MaterialTheme(
            colorScheme = colorScheme,
            typography  = Typography,
            shapes      = AdminShapes,
            content     = content
        )
    }
}

// ══════════════════════════════════════════
// GLASS VARIANT THEMES
// Each tuned for a specific UI layer
// ══════════════════════════════════════════

/**
 * Navigation / Tab Bar — ultra-crisp, thin blur
 * Like iPhone's bottom bar frosted glass
 */
@Composable
fun NavigationGlassTheme(
    darkTheme: Boolean = isSystemInDarkTheme(),
    content  : @Composable () -> Unit
) = AdminTheme(
    darkTheme   = darkTheme,
    glassConfig = GlassThemeConfig(
        blurRadius      = 32f,
        noiseIntensity  = 0.006f,
        saturationBoost = 1.18f,
        brightnessBoost = 1.10f,
        backdropAlpha   = 0.82f,
        borderGlow      = false
    ),
    content = content
)

/**
 * Bottom Sheet / Action Sheet — deep glass layer
 * Like iOS Share Sheet
 */
@Composable
fun SheetGlassTheme(
    darkTheme: Boolean = isSystemInDarkTheme(),
    content  : @Composable () -> Unit
) = AdminTheme(
    darkTheme   = darkTheme,
    glassConfig = GlassThemeConfig(
        blurRadius      = 48f,
        noiseIntensity  = 0.012f,
        saturationBoost = 1.30f,
        brightnessBoost = 1.12f,
        backdropAlpha   = 0.70f
    ),
    content = content
)

/**
 * Popover / Context Menu — compact precise glass
 * Like iOS long-press context menu
 */
@Composable
fun PopoverGlassTheme(
    darkTheme: Boolean = isSystemInDarkTheme(),
    content  : @Composable () -> Unit
) = AdminTheme(
    darkTheme   = darkTheme,
    glassConfig = GlassThemeConfig(
        blurRadius      = 26f,
        noiseIntensity  = 0.008f,
        saturationBoost = 1.20f,
        brightnessBoost = 1.12f,
        backdropAlpha   = 0.78f,
        borderGlow      = true
    ),
    content = content
)

/**
 * Sidebar / Drawer — wide frosted panel
 * Like iPadOS sidebar
 */
@Composable
fun SidebarGlassTheme(
    darkTheme: Boolean = isSystemInDarkTheme(),
    content  : @Composable () -> Unit
) = AdminTheme(
    darkTheme   = darkTheme,
    glassConfig = GlassThemeConfig(
        blurRadius      = 36f,
        noiseIntensity  = 0.010f,
        saturationBoost = 1.22f,
        brightnessBoost = 1.08f,
        backdropAlpha   = 0.74f
    ),
    content = content
)

/**
 * HUD / Notification Banner — maximum blur vibrancy
 * Like iOS Live Activity / Dynamic Island
 */
@Composable
fun HUDGlassTheme(
    darkTheme: Boolean = isSystemInDarkTheme(),
    content  : @Composable () -> Unit
) = AdminTheme(
    darkTheme   = darkTheme,
    glassConfig = GlassThemeConfig(
        blurRadius      = 56f,
        noiseIntensity  = 0.018f,
        saturationBoost = 1.45f,
        brightnessBoost = 1.22f,
        backdropAlpha   = 0.58f,
        borderGlow      = true
    ),
    content = content
)

/**
 * Widget / Home Screen Tile — balanced depth
 * Like iOS 17/18 interactive widgets
 */
@Composable
fun WidgetGlassTheme(
    darkTheme: Boolean = isSystemInDarkTheme(),
    content  : @Composable () -> Unit
) = AdminTheme(
    darkTheme   = darkTheme,
    glassConfig = GlassThemeConfig(
        blurRadius      = 24f,
        noiseIntensity  = 0.010f,
        saturationBoost = 1.22f,
        brightnessBoost = 1.10f,
        backdropAlpha   = 0.66f
    ),
    content = content
)

/**
 * Card — subtle frosted card surface
 * Like Material 3 elevated card with glass
 */
@Composable
fun CardGlassTheme(
    darkTheme: Boolean = isSystemInDarkTheme(),
    content  : @Composable () -> Unit
) = AdminTheme(
    darkTheme   = darkTheme,
    glassConfig = GlassThemeConfig(
        blurRadius      = 20f,
        noiseIntensity  = 0.008f,
        saturationBoost = 1.15f,
        brightnessBoost = 1.06f,
        backdropAlpha   = 0.62f,
        borderGlow      = true
    ),
    content = content
)

// ══════════════════════════════════════════
// DISPLAY PROFILE THEMES
// ══════════════════════════════════════════

/** iPhone 15 Pro Max — Super Retina XDR profile */
@Composable
fun iPhoneProTheme(
    darkTheme: Boolean = isSystemInDarkTheme(),
    content  : @Composable () -> Unit
) = AdminTheme(
    darkTheme     = darkTheme,
    displayConfig = DisplayConfig(
        profile              = DisplayProfile.SuperRetina,
        applyGammaCorrection = true,
        boostBrightness      = true,
        contrastEnhancement  = 1.20f,
        enableHDR            = true
    ),
    glassConfig = GlassThemeConfig(
        blurRadius      = 28f,
        saturationBoost = 1.30f,
        borderGlow      = true
    ),
    content = content
)

/** Android AMOLED — deep blue-black, ultra-vivid */
@Composable
fun AndroidVibrantTheme(
    content: @Composable () -> Unit
) = AdminTheme(
    darkTheme     = true,
    displayConfig = DisplayConfig(
        profile             = DisplayProfile.AndroidAMOLED,
        applyGammaCorrection = true,
        boostBrightness      = true,
        contrastEnhancement  = 1.35f
    ),
    glassConfig = GlassThemeConfig(
        blurRadius      = 28f,
        saturationBoost = 1.38f,
        brightnessBoost = 1.18f,
        backdropAlpha   = 0.65f,
        borderGlow      = true
    ),
    content = content
)

// ══════════════════════════════════════════
// UTILITY HELPERS
// ══════════════════════════════════════════

val isGlassSurface: Boolean
    @Composable @ReadOnlyComposable get() = LocalIsGlassSurface.current

@Composable @ReadOnlyComposable
fun glassBackgroundColor(thickness: Float = GlassAlpha.Regular): Color =
    if (isSystemInDarkTheme()) GlassDark.GlassRegular.copy(alpha = thickness)
    else GlassLight.GlassRegular.copy(alpha = thickness)

@Composable @ReadOnlyComposable
fun glassSurfaceColor(elevation: Int = 0): Color =
    if (isSystemInDarkTheme()) when (elevation) {
        0    -> GlassDark.Elevation0
        1    -> GlassDark.Elevation1
        2    -> GlassDark.Elevation2
        3    -> GlassDark.Elevation3
        else -> GlassDark.Elevation4
    }
    else when (elevation) {
        0    -> GlassLight.Elevation0
        1    -> GlassLight.Elevation1
        2    -> GlassLight.Elevation2
        else -> GlassLight.Elevation3
    }

@Composable @ReadOnlyComposable
fun glassBorderColor(): Color =
    if (isSystemInDarkTheme()) GlassDark.GlassBorder
    else GlassLight.GlassBorder

/** Solid color for TopAppBar on dark — sits visibly above the #0F1B30 dark background */
@Composable @ReadOnlyComposable
fun glassTopBarColor(): Color =
    if (isSystemInDarkTheme()) Color(0xFF1A2D4A)
    else Color(0xFFFFFFFF)

/** Scrolled/elevated TopAppBar — slightly brighter */
@Composable @ReadOnlyComposable
fun glassTopBarScrolledColor(): Color =
    if (isSystemInDarkTheme()) Color(0xFF1F3456)
    else Color(0xFFEBF2FF)

/** Solid color for BottomNavigationBar */
@Composable @ReadOnlyComposable
fun glassNavBarColor(): Color =
    if (isSystemInDarkTheme()) Color(0xFF112240)
    else Color(0xFFF5F9FF)