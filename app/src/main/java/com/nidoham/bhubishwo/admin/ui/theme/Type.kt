package com.nidoham.bhubishwo.admin.ui.theme

import androidx.compose.material3.Typography
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.LineHeightStyle
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.sp

// ╔══════════════════════════════════════════════════════════╗
// ║      iOS SF PRO-INSPIRED TYPOGRAPHY SYSTEM              ║
// ║      Optimised for Deep Glass + Vibrant Surfaces        ║
// ╚══════════════════════════════════════════════════════════╝

private val GlassLineHeightStyle = LineHeightStyle(
    alignment = LineHeightStyle.Alignment.Center,
    trim      = LineHeightStyle.Trim.None
)

// ══════════════════════════════════════════
// DISPLAY — Hero & Large Title
// SF Display: Bold/Heavy, tight tracking
// ══════════════════════════════════════════

val DisplayLarge = TextStyle(
    fontFamily      = FontFamily.Default,
    fontWeight      = FontWeight.Black,         // Heavy — iOS Large Title hero
    fontSize        = 57.sp,
    lineHeight      = 64.sp,
    letterSpacing   = (-0.30).sp,              // Very tight — premium feel
    lineHeightStyle = GlassLineHeightStyle
)

val DisplayMedium = TextStyle(
    fontFamily      = FontFamily.Default,
    fontWeight      = FontWeight.ExtraBold,
    fontSize        = 45.sp,
    lineHeight      = 52.sp,
    letterSpacing   = (-0.25).sp,
    lineHeightStyle = GlassLineHeightStyle
)

val DisplaySmall = TextStyle(
    fontFamily      = FontFamily.Default,
    fontWeight      = FontWeight.Bold,
    fontSize        = 36.sp,
    lineHeight      = 44.sp,
    letterSpacing   = (-0.18).sp,
    lineHeightStyle = GlassLineHeightStyle
)

// ══════════════════════════════════════════
// HEADLINE — Section Titles
// SF Display Semibold
// ══════════════════════════════════════════

val HeadlineLarge = TextStyle(
    fontFamily      = FontFamily.Default,
    fontWeight      = FontWeight.Bold,
    fontSize        = 32.sp,
    lineHeight      = 40.sp,
    letterSpacing   = (-0.12).sp,
    lineHeightStyle = GlassLineHeightStyle
)

val HeadlineMedium = TextStyle(
    fontFamily      = FontFamily.Default,
    fontWeight      = FontWeight.SemiBold,
    fontSize        = 28.sp,
    lineHeight      = 36.sp,
    letterSpacing   = (-0.08).sp,
    lineHeightStyle = GlassLineHeightStyle
)

val HeadlineSmall = TextStyle(
    fontFamily      = FontFamily.Default,
    fontWeight      = FontWeight.SemiBold,
    fontSize        = 24.sp,
    lineHeight      = 32.sp,
    letterSpacing   = (-0.04).sp,
    lineHeightStyle = GlassLineHeightStyle
)

// ══════════════════════════════════════════
// TITLE — Card / List Headers
// SF Text Semibold / Medium
// ══════════════════════════════════════════

val TitleLarge = TextStyle(
    fontFamily      = FontFamily.Default,
    fontWeight      = FontWeight.SemiBold,
    fontSize        = 22.sp,
    lineHeight      = 28.sp,
    letterSpacing   = (-0.02).sp,
    lineHeightStyle = GlassLineHeightStyle
)

val TitleMedium = TextStyle(
    fontFamily      = FontFamily.Default,
    fontWeight      = FontWeight.SemiBold,
    fontSize        = 16.sp,
    lineHeight      = 24.sp,
    letterSpacing   = (0.01).sp,
    lineHeightStyle = GlassLineHeightStyle
)

val TitleSmall = TextStyle(
    fontFamily      = FontFamily.Default,
    fontWeight      = FontWeight.Medium,
    fontSize        = 14.sp,
    lineHeight      = 20.sp,
    letterSpacing   = (0.02).sp,
    lineHeightStyle = GlassLineHeightStyle
)

// ══════════════════════════════════════════
// BODY — Primary Content
// SF Text Regular — optimised for glass readability
// ══════════════════════════════════════════

val BodyLarge = TextStyle(
    fontFamily      = FontFamily.Default,
    fontWeight      = FontWeight.Normal,
    fontSize        = 17.sp,               // Apple default body
    lineHeight      = 26.sp,               // Extra line height for glass
    letterSpacing   = (-0.01).sp,
    lineHeightStyle = GlassLineHeightStyle
)

val BodyMedium = TextStyle(
    fontFamily      = FontFamily.Default,
    fontWeight      = FontWeight.Normal,
    fontSize        = 15.sp,
    lineHeight      = 22.sp,
    letterSpacing   = 0.sp,
    lineHeightStyle = GlassLineHeightStyle
)

val BodySmall = TextStyle(
    fontFamily      = FontFamily.Default,
    fontWeight      = FontWeight.Normal,
    fontSize        = 13.sp,
    lineHeight      = 18.sp,
    letterSpacing   = (0.01).sp,
    lineHeightStyle = GlassLineHeightStyle
)

// ══════════════════════════════════════════
// LABEL — Captions, Badges, UI Elements
// SF Text Medium — tight but legible
// ══════════════════════════════════════════

val LabelLarge = TextStyle(
    fontFamily      = FontFamily.Default,
    fontWeight      = FontWeight.Medium,
    fontSize        = 14.sp,
    lineHeight      = 20.sp,
    letterSpacing   = (0.03).sp,
    lineHeightStyle = GlassLineHeightStyle
)

val LabelMedium = TextStyle(
    fontFamily      = FontFamily.Default,
    fontWeight      = FontWeight.Medium,
    fontSize        = 12.sp,
    lineHeight      = 16.sp,
    letterSpacing   = (0.05).sp,
    lineHeightStyle = GlassLineHeightStyle
)

val LabelSmall = TextStyle(
    fontFamily      = FontFamily.Default,
    fontWeight      = FontWeight.SemiBold,  // Heavier for tiny glass labels
    fontSize        = 11.sp,
    lineHeight      = 14.sp,
    letterSpacing   = (0.07).sp,
    lineHeightStyle = GlassLineHeightStyle
)

// ══════════════════════════════════════════
// GLASS-SPECIFIC TYPOGRAPHY
// For translucent/frosted backgrounds
// ══════════════════════════════════════════

object GlassTypography {

    /** Tab bar / navigation labels — thin glass */
    val OnGlassThin = TextStyle(
        fontFamily      = FontFamily.Default,
        fontWeight      = FontWeight.Medium,
        fontSize        = 15.sp,
        lineHeight      = 20.sp,
        letterSpacing   = (0.01).sp,
        lineHeightStyle = GlassLineHeightStyle
    )

    /** Card / list item primary text */
    val OnGlassRegular = TextStyle(
        fontFamily      = FontFamily.Default,
        fontWeight      = FontWeight.Normal,
        fontSize        = 16.sp,
        lineHeight      = 24.sp,
        letterSpacing   = 0.sp,
        lineHeightStyle = GlassLineHeightStyle
    )

    /** Modal sheet / action sheet — thick glass */
    val OnGlassThick = TextStyle(
        fontFamily      = FontFamily.Default,
        fontWeight      = FontWeight.Normal,
        fontSize        = 17.sp,
        lineHeight      = 26.sp,
        letterSpacing   = (-0.01).sp,
        lineHeightStyle = GlassLineHeightStyle
    )

    /** iOS vibrancy primary — bold, high contrast */
    val VibrancyPrimary = TextStyle(
        fontFamily      = FontFamily.Default,
        fontWeight      = FontWeight.SemiBold,
        fontSize        = 16.sp,
        lineHeight      = 22.sp,
        letterSpacing   = (0.01).sp,
        lineHeightStyle = GlassLineHeightStyle
    )

    /** iOS vibrancy secondary — normal, softer */
    val VibrancySecondary = TextStyle(
        fontFamily      = FontFamily.Default,
        fontWeight      = FontWeight.Normal,
        fontSize        = 14.sp,
        lineHeight      = 20.sp,
        letterSpacing   = (0.02).sp,
        lineHeightStyle = GlassLineHeightStyle
    )

    /** Placeholder / tertiary hint text on glass */
    val VibrancyTertiary = TextStyle(
        fontFamily      = FontFamily.Default,
        fontWeight      = FontWeight.Light,
        fontSize        = 13.sp,
        lineHeight      = 18.sp,
        letterSpacing   = (0.03).sp,
        lineHeightStyle = GlassLineHeightStyle
    )

    /** Large stat/number on widget or HUD */
    val LargeNumeral = TextStyle(
        fontFamily      = FontFamily.Default,
        fontWeight      = FontWeight.Black,
        fontSize        = 52.sp,
        lineHeight      = 60.sp,
        letterSpacing   = (-0.35).sp,
        lineHeightStyle = GlassLineHeightStyle
    )

    /** Medium stat number (dashboard cards) */
    val MediumNumeral = TextStyle(
        fontFamily      = FontFamily.Default,
        fontWeight      = FontWeight.Bold,
        fontSize        = 34.sp,
        lineHeight      = 42.sp,
        letterSpacing   = (-0.20).sp,
        lineHeightStyle = GlassLineHeightStyle
    )

    /** Monospace — code, IDs, timestamps on glass */
    val Monospace = TextStyle(
        fontFamily      = FontFamily.Monospace,
        fontWeight      = FontWeight.Normal,
        fontSize        = 14.sp,
        lineHeight      = 20.sp,
        letterSpacing   = (0.04).sp,
        lineHeightStyle = GlassLineHeightStyle
    )

    /** Uppercase tracking — section headers, caps labels */
    val Caps = TextStyle(
        fontFamily      = FontFamily.Default,
        fontWeight      = FontWeight.SemiBold,
        fontSize        = 11.sp,
        lineHeight      = 16.sp,
        letterSpacing   = (1.20).sp,    // Wide uppercase tracking like iOS section headers
        lineHeightStyle = GlassLineHeightStyle
    )
}

// ══════════════════════════════════════════
// DYNAMIC TYPE ACCESSIBILITY SCALING
// ══════════════════════════════════════════

object DynamicType {
    const val XSmall        = 0.82f
    const val Small         = 0.88f
    const val Medium        = 0.94f
    const val Large         = 1.00f  // default
    const val XLarge        = 1.12f
    const val XXLarge       = 1.23f
    const val XXXLarge      = 1.35f
    const val Accessibility1 = 1.60f
    const val Accessibility2 = 1.90f
    const val Accessibility3 = 2.20f
    const val Accessibility4 = 2.60f
    const val Accessibility5 = 3.10f
}

fun TextStyle.scaled(scale: Float = DynamicType.Large): TextStyle = copy(
    fontSize   = (fontSize.value   * scale).sp,
    lineHeight = (lineHeight.value * scale).sp
)

// ══════════════════════════════════════════
// MATERIAL3 TYPOGRAPHY REGISTRATION
// ══════════════════════════════════════════

val Typography = Typography(
    displayLarge   = DisplayLarge,
    displayMedium  = DisplayMedium,
    displaySmall   = DisplaySmall,
    headlineLarge  = HeadlineLarge,
    headlineMedium = HeadlineMedium,
    headlineSmall  = HeadlineSmall,
    titleLarge     = TitleLarge,
    titleMedium    = TitleMedium,
    titleSmall     = TitleSmall,
    bodyLarge      = BodyLarge,
    bodyMedium     = BodyMedium,
    bodySmall      = BodySmall,
    labelLarge     = LabelLarge,
    labelMedium    = LabelMedium,
    labelSmall     = LabelSmall
)

// ══════════════════════════════════════════
// UTILITY EXTENSIONS
// ══════════════════════════════════════════

fun TextStyle.centered()   : TextStyle = copy(textAlign = TextAlign.Center)

fun TextStyle.emphasized() : TextStyle = copy(
    fontWeight    = FontWeight.SemiBold,
    letterSpacing = (letterSpacing.value - 0.01f).sp
)

fun TextStyle.prominent()  : TextStyle = copy(
    fontWeight    = FontWeight.Bold,
    letterSpacing = (letterSpacing.value - 0.02f).sp
)

fun TextStyle.secondary()  : TextStyle = copy(
    fontWeight    = FontWeight.Normal,
    letterSpacing = (letterSpacing.value + 0.01f).sp
)

fun TextStyle.glowCaps()   : TextStyle = copy(
    fontWeight    = FontWeight.SemiBold,
    letterSpacing = 1.0.sp,
    fontSize      = (fontSize.value * 0.78f).sp  // Slightly smaller for all-caps
)