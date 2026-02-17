package com.nidoham.bhubishwo.admin.ui.theme

import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.material3.ColorScheme
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.compositeOver
import kotlin.math.pow

// ╔══════════════════════════════════════════════════════════╗
// ║     PREMIUM iOS-STYLE GLASS MORPHISM COLOR SYSTEM       ║
// ║     Rich · Vibrant · Deep · Modern Material 3           ║
// ╚══════════════════════════════════════════════════════════╝

object GlassAlpha {
    const val UltraThin  = 0.07f
    const val Thin       = 0.15f
    const val Regular    = 0.26f
    const val Thick      = 0.44f
    const val UltraThick = 0.66f
    const val Solid      = 1.00f
}

// ══════════════════════════════════════════
// CORE PALETTE — iOS System Blue
// ══════════════════════════════════════════

val PrimaryLight          = Color(0xFF007AFF)
val PrimaryDark           = Color(0xFF4DA6FF)   // brighter, more visible on dark bg
val PrimaryContainerLight = Color(0xFFCCE5FF)
val PrimaryContainerDark  = Color(0xFF00337A)

val SecondaryLight          = Color(0xFF5856D6)
val SecondaryDark           = Color(0xFF857DFF)   // brighter indigo
val SecondaryContainerLight = Color(0xFFE8E7FF)
val SecondaryContainerDark  = Color(0xFF221F70)

val TertiaryLight          = Color(0xFF32ADE6)
val TertiaryDark           = Color(0xFF6DD5FA)    // bright cyan
val TertiaryContainerLight = Color(0xFFD0F0FF)
val TertiaryContainerDark  = Color(0xFF004466)

// ══════════════════════════════════════════
// ▌DARK GLASS  ▌
// Blue-black that's VISIBLE — not pitch dark.
// Like a phone screen at ~30% brightness
// showing a deep midnight-blue glass panel.
// ══════════════════════════════════════════

object GlassDark {

    // ── Backgrounds ──────────────────────────
    val Background         = Color(0xFF0F1B30)   // deep navy-blue — visible blue-black
    val BackgroundElevated = Color(0xFF132035)
    val BackgroundAlt      = Color(0xFF0C1828)   // deepest (behind sheets)

    // ── Frosted Glass Sheets ─────────────────
    val GlassUltraThin  = Color(0xFF1E3A6E).copy(alpha = 0.32f)
    val GlassThin       = Color(0xFF1E3A6E).copy(alpha = 0.48f)
    val GlassRegular    = Color(0xFF1E3A6E).copy(alpha = 0.62f)
    val GlassThick      = Color(0xFF204080).copy(alpha = 0.74f)
    val GlassUltraThick = Color(0xFF2547A0).copy(alpha = 0.86f)

    // ── Card / Surface Layers ─────────────────
    val Surface        = Color(0xFF162540)
    val SurfaceVariant = Color(0xFF1C2E4E)
    val SurfaceTint    = PrimaryDark.copy(alpha = 0.14f)

    // ── Elevation Steps ──────────────────────
    val Elevation0 = Color(0xFF162540)
    val Elevation1 = Color(0xFF1A2D4D).copy(alpha = 0.78f)
    val Elevation2 = Color(0xFF1E355A).copy(alpha = 0.84f)
    val Elevation3 = Color(0xFF233E68).copy(alpha = 0.90f)
    val Elevation4 = Color(0xFF284778).copy(alpha = 0.96f)

    // ── Backdrop ─────────────────────────────
    val BackdropBase     = Color(0xFF0A1422).copy(alpha = 0.80f)
    val BackdropElevated = Color(0xFF0F1B30).copy(alpha = 0.88f)
    val BackdropModal    = Color(0xFF07101E).copy(alpha = 0.92f)

    // ── Vibrancy / Glow ──────────────────────
    val VibrancyPrimary   = PrimaryDark.copy(alpha = 0.38f)
    val VibrancySecondary = SecondaryDark.copy(alpha = 0.24f)
    val VibrancyCyan      = TertiaryDark.copy(alpha = 0.20f)
    val VibrancyWhite     = Color(0xFFFFFFFF).copy(alpha = 0.10f)

    // ── Borders ──────────────────────────────
    val GlassBorder       = Color(0xFF4DA6FF).copy(alpha = 0.32f)
    val GlassBorderSubtle = Color(0xFF2547A0).copy(alpha = 0.42f)
}

// ══════════════════════════════════════════
// ▌LIGHT GLASS▌
// Clean white with a clear blue personality
// ══════════════════════════════════════════

object GlassLight {

    val Background         = Color(0xFFF0F5FF)   // white with clear blue tint
    val BackgroundGrouped  = Color(0xFFFFFFFF)
    val BackgroundElevated = Color(0xFFFFFFFF)

    val GlassUltraThin  = Color(0xFFFFFFFF).copy(alpha = 0.55f)
    val GlassThin       = Color(0xFFEBF2FF).copy(alpha = 0.70f)
    val GlassRegular    = Color(0xFFE0EEFF).copy(alpha = 0.78f)
    val GlassThick      = Color(0xFFD0E4FF).copy(alpha = 0.86f)
    val GlassUltraThick = Color(0xFFC4DCFF).copy(alpha = 0.93f)

    val Surface        = Color(0xFFFFFFFF)
    val SurfaceVariant = Color(0xFFEBF2FF)        // very light blue card
    val SurfaceTint    = PrimaryLight.copy(alpha = 0.06f)

    val Elevation0 = Color(0xFFFFFFFF)
    val Elevation1 = Color(0xFFF5F9FF).copy(alpha = 0.90f)
    val Elevation2 = Color(0xFFEEF5FF).copy(alpha = 0.94f)
    val Elevation3 = Color(0xFFE6F0FF).copy(alpha = 0.97f)

    val BackdropBase     = Color(0xFFEBF2FF).copy(alpha = 0.76f)
    val BackdropElevated = Color(0xFFD0E4FF).copy(alpha = 0.84f)

    val GlassBorder       = Color(0xFF007AFF).copy(alpha = 0.24f)
    val GlassBorderSubtle = Color(0xFF007AFF).copy(alpha = 0.12f)
}

// ══════════════════════════════════════════
// VIBRANT ACCENT PALETTE
// ══════════════════════════════════════════

object Accents {
    val Blue         = Color(0xFF007AFF)
    val BlueDark     = Color(0xFF4DA6FF)
    val Indigo       = Color(0xFF5856D6)
    val IndigoDark   = Color(0xFF857DFF)
    val Purple       = Color(0xFFAF52DE)
    val PurpleDark   = Color(0xFFCF80FF)
    val Teal         = Color(0xFF5AC8FA)
    val TealDark     = Color(0xFF6DD5FA)
    val Cyan         = Color(0xFF32ADE6)
    val CyanDark     = Color(0xFF6DD5FA)
    val Mint         = Color(0xFF00C7BE)
    val MintDark     = Color(0xFF63E6E2)

    val ElectricBlue  = Color(0xFF2979FF)
    val DeepBlue      = Color(0xFF1565C0)
    val NeonBlue      = Color(0xFF00B0FF)
    val GlowBlue      = Color(0xFF82CFFF)

    // Gradient pairs
    val GradientBlueStart   = Color(0xFF007AFF)
    val GradientBlueEnd     = Color(0xFF5856D6)
    val GradientCyanStart   = Color(0xFF32ADE6)
    val GradientCyanEnd     = Color(0xFF007AFF)
    val GradientIndigoStart = Color(0xFF5856D6)
    val GradientIndigoEnd   = Color(0xFFAF52DE)
}

// ══════════════════════════════════════════
// SEMANTIC COLORS
// ══════════════════════════════════════════

val ErrorLight   = Color(0xFFFF3B30)
val ErrorDark    = Color(0xFFFF6B6B)
val SuccessLight = Color(0xFF34C759)
val SuccessDark  = Color(0xFF4CD96A)
val WarningLight = Color(0xFFFF9500)
val WarningDark  = Color(0xFFFFB340)
val InfoLight    = Color(0xFF007AFF)
val InfoDark     = Color(0xFF4DA6FF)

// ══════════════════════════════════════════
// DARK COLOR SCHEME
// ══════════════════════════════════════════

val DarkColorScheme: ColorScheme = darkColorScheme(
    primary             = Color(0xFF4DA6FF),      // bright, visible blue
    onPrimary           = Color(0xFF001A40),
    primaryContainer    = Color(0xFF003D99),
    onPrimaryContainer  = Color(0xFFB8D8FF),

    secondary             = Color(0xFF857DFF),    // bright indigo
    onSecondary           = Color(0xFF100E40),
    secondaryContainer    = Color(0xFF221F70),
    onSecondaryContainer  = Color(0xFFD0CBFF),

    tertiary             = Color(0xFF6DD5FA),      // bright cyan
    onTertiary           = Color(0xFF002233),
    tertiaryContainer    = Color(0xFF004466),
    onTertiaryContainer  = Color(0xFFB8EEFF),

    error             = Color(0xFFFF6B6B),
    onError           = Color(0xFF1A0000),
    errorContainer    = Color(0xFF4A0000),
    onErrorContainer  = Color(0xFFFFB8B8),

    // ★ Deep navy — visible blue-black
    background   = GlassDark.Background,          // #0F1B30
    onBackground = Color(0xFFDDEEFF),

    surface             = GlassDark.Surface,      // #162540
    onSurface           = Color(0xFFE0EEFF),
    surfaceVariant      = GlassDark.SurfaceVariant,
    onSurfaceVariant    = Color(0xFF9DC4FF),
    surfaceTint         = GlassDark.SurfaceTint,

    inverseSurface    = Color(0xFFEBF2FF),
    inverseOnSurface  = Color(0xFF0F1B30),
    inversePrimary    = PrimaryLight,

    outline        = Color(0xFF2D5A99).copy(alpha = 0.70f),
    outlineVariant = Color(0xFF1A2E50),

    scrim = Color(0xFF040C1A).copy(alpha = 0.70f),

    surfaceBright           = GlassDark.Elevation4,
    surfaceDim              = GlassDark.BackgroundAlt,
    surfaceContainer        = GlassDark.Elevation1,
    surfaceContainerHigh    = GlassDark.Elevation2,
    surfaceContainerHighest = GlassDark.Elevation3,
    surfaceContainerLow     = GlassDark.Surface,
    surfaceContainerLowest  = Color(0xFF0A1220),
)

// ══════════════════════════════════════════
// LIGHT COLOR SCHEME
// ══════════════════════════════════════════

val LightColorScheme: ColorScheme = lightColorScheme(
    primary             = Color(0xFF007AFF),
    onPrimary           = Color(0xFFFFFFFF),
    primaryContainer    = Color(0xFFCCE5FF),
    onPrimaryContainer  = Color(0xFF00306B),

    secondary             = Color(0xFF5856D6),
    onSecondary           = Color(0xFFFFFFFF),
    secondaryContainer    = Color(0xFFE8E7FF),
    onSecondaryContainer  = Color(0xFF1B1885),

    tertiary             = Color(0xFF32ADE6),
    onTertiary           = Color(0xFFFFFFFF),
    tertiaryContainer    = Color(0xFFD0F0FF),
    onTertiaryContainer  = Color(0xFF00406A),

    error             = Color(0xFFFF3B30),
    onError           = Color(0xFFFFFFFF),
    errorContainer    = Color(0xFFFFDAD6),
    onErrorContainer  = Color(0xFF4A0000),

    background   = GlassLight.Background,         // #F0F5FF
    onBackground = Color(0xFF0A1628),

    surface             = GlassLight.Surface,     // #FFFFFF
    onSurface           = Color(0xFF0A1628),
    surfaceVariant      = GlassLight.SurfaceVariant, // #EBF2FF
    onSurfaceVariant    = Color(0xFF1A3D7A),
    surfaceTint         = GlassLight.SurfaceTint,

    inverseSurface    = Color(0xFF162540),
    inverseOnSurface  = Color(0xFFE0EEFF),
    inversePrimary    = PrimaryDark,

    outline        = Color(0xFF007AFF).copy(alpha = 0.38f),
    outlineVariant = Color(0xFFBDD8FF),

    scrim = Color(0xFF0A1628).copy(alpha = 0.38f),

    surfaceBright           = Color(0xFFFFFFFF),
    surfaceDim              = Color(0xFFEBF2FF),
    surfaceContainer        = GlassLight.Elevation1,
    surfaceContainerHigh    = GlassLight.Elevation2,
    surfaceContainerHighest = GlassLight.Elevation3,
    surfaceContainerLow     = GlassLight.Surface,
    surfaceContainerLowest  = Color(0xFFFFFFFF),
)

// ══════════════════════════════════════════
// GLASS CONFIG
// ══════════════════════════════════════════

data class GlassConfig(
    val blurRadius      : Float = 24f,
    val noiseIntensity  : Float = 0.010f,
    val saturationBoost : Float = 1.25f,
    val brightnessBoost : Float = 1.10f
)

object GlassPresets {
    val MenuBar  = GlassConfig(blurRadius = 32f, noiseIntensity = 0.007f, saturationBoost = 1.15f, brightnessBoost = 1.08f)
    val Sheet    = GlassConfig(blurRadius = 48f, noiseIntensity = 0.010f, saturationBoost = 1.28f, brightnessBoost = 1.12f)
    val Popover  = GlassConfig(blurRadius = 26f, noiseIntensity = 0.009f, saturationBoost = 1.18f, brightnessBoost = 1.10f)
    val Sidebar  = GlassConfig(blurRadius = 36f, noiseIntensity = 0.010f, saturationBoost = 1.22f, brightnessBoost = 1.08f)
    val HUD      = GlassConfig(blurRadius = 56f, noiseIntensity = 0.020f, saturationBoost = 1.45f, brightnessBoost = 1.22f)
    val Card     = GlassConfig(blurRadius = 20f, noiseIntensity = 0.009f, saturationBoost = 1.12f, brightnessBoost = 1.06f)
    val Widget   = GlassConfig(blurRadius = 24f, noiseIntensity = 0.010f, saturationBoost = 1.20f, brightnessBoost = 1.10f)
    val Toolbar  = GlassConfig(blurRadius = 28f, noiseIntensity = 0.006f, saturationBoost = 1.10f, brightnessBoost = 1.06f)
}

// ══════════════════════════════════════════
// DISPLAY PROFILES
// ══════════════════════════════════════════

sealed class DisplayProfile(
    val name             : String,
    val gamma            : Float,
    val contrastRatio    : Float,
    val colorTemperature : Int,
    val supportsHDR      : Boolean,
    val maxBrightness    : Float
) {
    object StandardLCD   : DisplayProfile("Standard LCD",       2.2f, 1_000f,                  6500, false, 300f)
    object RetinaDisplay : DisplayProfile("Retina Display",     2.2f, 1_000f,                  6500, false, 600f)
    object ProDisplayXDR : DisplayProfile("Pro Display XDR",    2.2f, 1_000_000f,              6500, true,  1600f)
    object OLED          : DisplayProfile("OLED",               2.4f, Float.POSITIVE_INFINITY,  6500, true,  1000f)
    object LiquidRetina  : DisplayProfile("Liquid Retina XDR",  2.2f, 1_000_000f,              6500, true,  1200f)
    object AndroidAMOLED : DisplayProfile("Android AMOLED",     2.2f, Float.POSITIVE_INFINITY,  6500, true,  1400f)
    object SuperRetina   : DisplayProfile("Super Retina XDR",   2.2f, 2_000_000f,              6500, true,  1200f)
}

// ══════════════════════════════════════════
// EXTENSIONS
// ══════════════════════════════════════════

fun Color.applyDisplayProfile(profile: DisplayProfile): Color {
    val g = 1f / profile.gamma
    return Color(
        red   = red.pow(g).coerceIn(0f, 1f),
        green = green.pow(g).coerceIn(0f, 1f),
        blue  = blue.pow(g).coerceIn(0f, 1f),
        alpha = alpha
    )
}

fun Color.toGlass(alpha: Float): Color = copy(alpha = alpha.coerceIn(0f, 1f))

fun Color.withElevation(elevation: Float, isDark: Boolean): Color {
    val a = (elevation / 100f).coerceIn(0f, 0.30f)
    val tint = if (isDark) Color(0xFF4DA6FF) else Color(0xFFFFFFFF)
    return copy(alpha = 1f).compositeOver(tint.copy(alpha = a))
}

fun Color.blend(other: Color, fraction: Float): Color {
    val f = fraction.coerceIn(0f, 1f)
    return Color(
        red   = red   * (1f - f) + other.red   * f,
        green = green * (1f - f) + other.green * f,
        blue  = blue  * (1f - f) + other.blue  * f,
        alpha = alpha * (1f - f) + other.alpha * f
    )
}

fun Color.boosted(saturation: Float = 1.2f): Color {
    val avg = (red + green + blue) / 3f
    return Color(
        red   = (avg + (red   - avg) * saturation).coerceIn(0f, 1f),
        green = (avg + (green - avg) * saturation).coerceIn(0f, 1f),
        blue  = (avg + (blue  - avg) * saturation).coerceIn(0f, 1f),
        alpha = alpha
    )
}

// ══════════════════════════════════════════
// BACKWARD COMPAT
// ══════════════════════════════════════════

val Purple80     = PrimaryDark
val PurpleGrey80 = SecondaryDark
val Pink80       = TertiaryDark
val Purple40     = PrimaryLight
val PurpleGrey40 = SecondaryLight
val Pink40       = TertiaryLight

val TopBarColor = Color(0xFF4A148C)