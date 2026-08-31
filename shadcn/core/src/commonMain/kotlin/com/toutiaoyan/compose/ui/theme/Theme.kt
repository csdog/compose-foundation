package com.toutiaoyan.compose.ui.theme

import androidx.compose.foundation.LocalIndication
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.Immutable
import androidx.compose.runtime.ProvidableCompositionLocal
import androidx.compose.runtime.remember
import androidx.compose.runtime.staticCompositionLocalOf
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import com.composables.interactioncapabilities.currentInteractionCapabilities
import com.toutiaoyan.compose.tailwind.core.DefaultUnitSpacing
import com.toutiaoyan.compose.tailwind.core.LocalTwRadius
import com.toutiaoyan.compose.tailwind.core.LocalTwSpacing
import com.toutiaoyan.compose.tailwind.core.LocalUnitSpacing
import com.toutiaoyan.compose.tailwind.core.TwSpacingScale
import com.toutiaoyan.compose.ui.modifiers.DefaultRippleIndication
import com.toutiaoyan.compose.ui.tokens.Accent
import com.toutiaoyan.compose.ui.tokens.Animations
import com.toutiaoyan.compose.ui.tokens.BaseColor
import com.toutiaoyan.compose.ui.tokens.Colors
import com.toutiaoyan.compose.ui.tokens.IconStyles
import com.toutiaoyan.compose.ui.tokens.Radius
import com.toutiaoyan.compose.ui.tokens.Ring
import com.toutiaoyan.compose.ui.tokens.StylePreset
import com.toutiaoyan.compose.ui.tokens.Typography

/**
 * Global design-system state. Corner radius / named spacing / rem step live in
 * [LocalTwRadius] / [LocalTwSpacing] / [LocalUnitSpacing] (and the matching
 * `twRadius` / `twSpacing` / `unitSpacing` readers), not here.
 */
@Immutable
data class ThemeData(
    val colors: Colors,
    val typography: Typography,
    val ring: Ring,
    val animations: Animations,
    val icons: IconStyles,
    val baseRadius: Radius,
    val activePreset: StylePreset,
    val activeBaseColor: BaseColor,
    val activeAccent: Accent,
    val fontFamily: FontFamily = FontFamily.Default,
    /** Whether this theme is in dark mode (independent of the [colors] sheet). */
    val isDark: Boolean = false,
    /**
     * Minimum interactive hit size for controls (Material / RikkaUi 48dp).
     * `null` disables theme-level min touch targets (dense desktop / web layouts).
     */
    val minTouchTarget: Dp? = 48.dp,
) {
    val isLight: Boolean get() = !isDark
}

/**
 * Composition-local access point for [ThemeData].
 */
object Theme {
    val current: ThemeData
        @Composable get() = LocalTheme.current

    val isDark: Boolean
        @Composable get() = LocalTheme.current.isDark

    val colors: Colors
        @Composable get() = LocalTheme.current.colors

    val fontFamily: FontFamily
        @Composable get() = LocalTheme.current.fontFamily

    internal val LocalTheme: ProvidableCompositionLocal<ThemeData> =
        staticCompositionLocalOf {
            val defaultPreset = StylePreset.Vega
            val defaultBase = BaseColor.Neutral
            val defaultAccent = Accent.Base
            val defaultBaseRadius = Radius(10.dp)

            ThemeData(
                colors = defaultBase.light,
                typography = defaultPreset.typography,
                ring = defaultPreset.ring,
                animations = defaultPreset.animations,
                icons = defaultPreset.icons,
                baseRadius = defaultBaseRadius,
                activePreset = defaultPreset,
                activeBaseColor = defaultBase,
                activeAccent = defaultAccent,
                fontFamily = FontFamily.Default,
                isDark = false,
                minTouchTarget = 48.dp,
            )
        }
}

@Composable
fun Theme(
    preset: StylePreset = StylePreset.Vega,
    baseColor: BaseColor = BaseColor.Neutral,
    accent: Accent = Accent.Base,
    isDark: Boolean = isSystemInDarkTheme(),
    baseRadius: Radius = Radius(10.dp),
    // Defaults to the selected preset's own ring (verified per-style in real
    // shadcn-ui/ui CSS -- see StylePreset.kt's doc comment), but stays
    // independently overridable -- same pattern as `baseRadius`.
    ring: Ring = preset.ring,
    /**
     * `--spacing` step for `n.spacing.dp` / `n.spacing.sp` and [TwSpacingScale]
     * ([LocalUnitSpacing]). Default `4` (= `0.25rem` at `1rem = 16px`).
     *
     * Does **not** rescale font sizes (`TwTextStyle` / `TwFontSize` / [Typography]) —
     * those stay on Tailwind's independent `--text-*` ladder. Absolute StylePreset
     * spacing ladders are also left alone; Vega/Sera/… defaults re-derive via
     * [TwSpacingScale.fromUnit] when [StylePreset.spacingDeriveFromUnit] is true.
     */
    unitSpacing: Float = DefaultUnitSpacing,
    fontFamily: FontFamily = FontFamily.Default,
    /**
     * Minimum hit area for interactive controls.
     * Omit (default) to derive from device capabilities: touch → `48.dp`, pointer → `null`.
     * Pass an explicit [Dp] or `null` to override.
     */
    minTouchTarget: Dp? = MinTouchTargetUnspecified,
    content: @Composable () -> Unit,
) {
    val resolvedMinTouch =
        if (minTouchTarget == MinTouchTargetUnspecified) {
            if (currentInteractionCapabilities().hasPointer) null else 48.dp
        } else {
            minTouchTarget
        }

    val configuredThemeData =
        remember(
            preset,
            baseColor,
            accent,
            isDark,
            baseRadius,
            ring,
            fontFamily,
            resolvedMinTouch,
        ) {
            val rawBaseColors = if (isDark) baseColor.dark else baseColor.light
            val finalizedColors = accent.applyTo(base = rawBaseColors, dark = isDark)

            ThemeData(
                colors = finalizedColors,
                typography = preset.typography,
                ring = ring,
                animations = preset.animations,
                icons = preset.icons,
                baseRadius = baseRadius,
                activePreset = preset,
                activeBaseColor = baseColor,
                activeAccent = accent,
                fontFamily = fontFamily,
                isDark = isDark,
                minTouchTarget = resolvedMinTouch,
            )
        }

    // Vega/Nova/Sera re-derive from baseRadius; personality presets keep explicit ladders.
    val resolvedRadius =
        remember(preset, baseRadius) {
            preset.resolveShapes(baseRadius.value)
        }

    val resolvedSpacing =
        remember(preset, unitSpacing) {
            preset.resolveSpacing(unitSpacing)
        }

    CompositionLocalProvider(
        Theme.LocalTheme provides configuredThemeData,
        LocalTwRadius provides resolvedRadius,
        LocalTwSpacing provides resolvedSpacing,
        LocalUnitSpacing provides unitSpacing,
        LocalIndication provides DefaultRippleIndication,
        content = content,
    )
}

/** Sentinel so [Theme] can tell "caller omitted minTouchTarget" from "caller passed null". */
private val MinTouchTargetUnspecified: Dp = (-1).dp

val currentTheme: ThemeData
    @Composable get() = Theme.current
