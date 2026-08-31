package com.toutiaoyan.compose.ui.tokens

import androidx.compose.animation.core.TweenSpec
import androidx.compose.runtime.Immutable
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.TextUnit
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.isSpecified
import com.toutiaoyan.compose.tailwind.core.TwRadiusScale
import com.toutiaoyan.compose.tailwind.core.TwSpacingScale

/**
 * shadcn/ui's real "Style" picker (ui.shadcn.com/create) -- Vega, Nova, Maia, Lyra,
 * Mira, Luma, Sera, Rhea -- ships as ~500KB of per-component CSS overrides per style
 * (individually restyling every component's Tailwind classes), which isn't something
 * we can or should replicate 1:1 in a token-based system. This models the *intent*
 * behind each style's real description as our existing shape/spacing/typography
 * tokens instead: a deliberate approximation, not per-component CSS parity.
 *
 * [ring] is a genuine exception to that "approximation, not parity" rule: every
 * style's real `.cn-button` (and every other focusable component) CSS was checked
 * directly against `shadcn-ui/ui`'s `apps/v4/registry/styles/style-<name>.css` files
 * (2026-07-09) -- ring width and opacity really do vary per style in the real
 * registry, not just an invented distinction:
 * `focus-visible:ring-<N> focus-visible:ring-ring/<opacity>`, `ring-offset-*` absent
 * everywhere (offset stays 0 for all 8). A prior version of this file removed `ring`
 * entirely after wrongly assuming it didn't vary by style -- that assumption was never
 * checked against the real per-style CSS files, only against a retired, unrelated
 * two-style axis (`shadcn init`'s old `Default`/`New York` split). Re-added with the
 * real values below once actually verified.
 */
@Immutable
enum class StylePreset(
    val radius: TwRadiusScale,
    val spacing: TwSpacingScale,
    val typography: Typography,
    val ring: Ring,
    val animations: Animations,
    val icons: IconStyles,
) {
    /** Vega: "Clean, neutral, and familiar." Classic 16px icon boxes,
     *  balanced standard animations. `baseRadius = 10.dp` matches real shadcn's
     *  `--radius: 0.625rem` (10px): `shapes.md` (= 8dp) is `rounded-md`,
     *  `shapes.lg` (= 10dp) is `rounded-lg`. */
    Vega(
        radius = TwRadiusScale.fromBaseRadius(baseRadius = 10.dp),
        spacing = TwSpacingScale.Default,
        typography = Typography(),
        ring = Ring(width = 3.dp, opacity = 0.5f),
        animations =
            Animations(
                defaultTransition = TweenSpec(durationMillis = 200),
                visibilityTransition = TweenSpec(durationMillis = 150),
            ),
        icons = IconStyles(standardSize = 16.dp, smallSize = 14.dp, strokeWidth = 2f),
    ),

    /** Nova: "Reduced padding and margins." Snappy animation frames,
     *  ultra-tight spacing layouts, and compact 14px icon bounds. */
    Nova(
        radius = TwRadiusScale.fromBaseRadius(baseRadius = 10.dp),
        spacing =
            TwSpacingScale.named(
                xs2 = 1.dp,
                xs = 3.dp,
                sm = 6.dp,
                md = 9.dp,
                lg = 12.dp,
                xl = 15.dp,
                xl2 = 18.dp,
                xl3 = 24.dp,
            ),
        typography = Typography(),
        ring = Ring(width = 3.dp, opacity = 0.5f),
        animations =
            Animations(
                defaultTransition = TweenSpec(durationMillis = 100),
                visibilityTransition = TweenSpec(durationMillis = 75),
            ),
        icons = IconStyles(standardSize = 14.dp, smallSize = 12.dp, strokeWidth = 1.5f),
    ),

    /** Maia: "Rounded, with generous spacing." Fluid, bouncy transitions and
     *  larger icon touch targets. */
    Maia(
        radius =
            TwRadiusScale.of(
                xs = 4.dp,
                sm = 8.dp,
                md = 12.dp,
                lg = 16.dp,
                xl = 20.dp,
                xl2 = 28.dp,
            ),
        spacing =
            TwSpacingScale.named(
                xs2 = 3.dp,
                xs = 6.dp,
                sm = 10.dp,
                md = 16.dp,
                lg = 21.dp,
                xl = 26.dp,
                xl2 = 32.dp,
                xl3 = 42.dp,
            ),
        typography = Typography(),
        ring = Ring(width = 3.dp, opacity = 0.5f),
        animations =
            Animations(
                defaultTransition = TweenSpec(durationMillis = 300),
                visibilityTransition = TweenSpec(durationMillis = 200),
            ),
        icons = IconStyles(standardSize = 18.dp, smallSize = 16.dp, strokeWidth = 2f),
    ),

    /** Lyra: "Boxy and sharp. For mono fonts." High-speed technical transitions,
     *  and thin-stroke icons for a blueprint aesthetic. */
    Lyra(
        radius =
            TwRadiusScale.of(
                xs = 0.dp,
                sm = 0.dp,
                md = 0.dp,
                lg = 0.dp,
                xl = 0.dp,
                xl2 = 0.dp,
            ),
        spacing = TwSpacingScale.Default,
        typography = Typography().withFontFamily(FontFamily.Monospace),
        ring = Ring(width = 1.dp, opacity = 0.5f),
        animations =
            Animations(
                defaultTransition = TweenSpec(durationMillis = 150),
                visibilityTransition = TweenSpec(durationMillis = 100),
            ),
        icons = IconStyles(standardSize = 16.dp, smallSize = 14.dp, strokeWidth = 1f),
    ),

    /** Mira: "Made for compact interfaces." Tightest animation timings and
     *  minimal icon footprints. */
    Mira(
        radius =
            TwRadiusScale.of(
                xs = 1.dp,
                sm = 2.dp,
                md = 4.dp,
                lg = 6.dp,
                xl = 8.dp,
                xl2 = 10.dp,
            ),
        spacing =
            TwSpacingScale.named(
                xs2 = 1.dp,
                xs = 2.dp,
                sm = 5.dp,
                md = 8.dp,
                lg = 10.dp,
                xl = 13.dp,
                xl2 = 16.dp,
                xl3 = 21.dp,
            ),
        typography = Typography().scaled(0.9f),
        ring = Ring(width = 2.dp, opacity = 0.3f),
        animations =
            Animations(
                defaultTransition = TweenSpec(durationMillis = 120),
                visibilityTransition = TweenSpec(durationMillis = 80),
            ),
        icons = IconStyles(standardSize = 14.dp, smallSize = 12.dp, strokeWidth = 1.5f),
    ),

    /** Luma: "Fluid, luminous, and soft." Slow, elegant fade transitions
     *  and standard icon weights. */
    Luma(
        radius =
            TwRadiusScale.of(
                xs = 6.dp,
                sm = 10.dp,
                md = 14.dp,
                lg = 18.dp,
                xl = 22.dp,
                xl2 = 26.dp,
            ),
        spacing = TwSpacingScale.Default,
        typography = Typography(),
        ring = Ring(width = 3.dp, opacity = 0.3f),
        animations =
            Animations(
                defaultTransition = TweenSpec(durationMillis = 400),
                visibilityTransition = TweenSpec(durationMillis = 250),
            ),
        icons = IconStyles(standardSize = 16.dp, smallSize = 14.dp, strokeWidth = 1.5f),
    ),

    /** Sera: "Editorial and typographic." Standard reliable transitions
     *  with icon weights that balance the larger type. */
    Sera(
        radius = TwRadiusScale.fromBaseRadius(baseRadius = 10.dp),
        spacing = TwSpacingScale.Default,
        typography = Typography().scaled(1.15f),
        ring = Ring(width = 2.dp, opacity = 0.3f),
        animations =
            Animations(
                defaultTransition = TweenSpec(durationMillis = 200),
                visibilityTransition = TweenSpec(durationMillis = 150),
            ),
        icons = IconStyles(standardSize = 18.dp, smallSize = 16.dp, strokeWidth = 2f),
    ),

    /** Rhea: "Like Luma but compact." Fluid Luma transitions but Nova-level
     *  compact spacing and icons. */
    Rhea(
        radius =
            TwRadiusScale.of(
                xs = 6.dp,
                sm = 10.dp,
                md = 14.dp,
                lg = 18.dp,
                xl = 22.dp,
                xl2 = 26.dp,
            ),
        spacing =
            TwSpacingScale.named(
                xs2 = 1.dp,
                xs = 2.dp,
                sm = 5.dp,
                md = 8.dp,
                lg = 10.dp,
                xl = 13.dp,
                xl2 = 16.dp,
                xl3 = 21.dp,
            ),
        typography = Typography(),
        ring = Ring(width = 3.dp, opacity = 0.3f),
        animations =
            Animations(
                defaultTransition = TweenSpec(durationMillis = 350),
                visibilityTransition = TweenSpec(durationMillis = 200),
            ),
        icons = IconStyles(standardSize = 14.dp, smallSize = 12.dp, strokeWidth = 1.5f),
    ),
    ;

    val label: String get() = name

    /**
     * Whether this style's corner scale is a pure multiple of Theme's `baseRadius`
     * (real shadcn `--radius`), vs an independently tuned personality that ignores it.
     */
    val shapesDeriveFromBaseRadius: Boolean
        get() =
            when (this) {
                Vega, Nova, Sera -> true
                Maia, Lyra, Mira, Luma, Rhea -> false
            }

    /** Resolved shapes for a Theme: derive from [baseRadius] when applicable. */
    fun resolveShapes(baseRadius: Dp): TwRadiusScale =
        if (shapesDeriveFromBaseRadius) TwRadiusScale.fromBaseRadius(baseRadius) else radius

    /**
     * Whether this style's spacing ladder is the default Tailwind-aligned scale
     * (scales with Theme `unitSpacing`) vs an independently tuned personality.
     */
    val spacingDeriveFromUnit: Boolean
        get() =
            when (this) {
                Vega, Lyra, Luma, Sera -> true
                Nova, Maia, Mira, Rhea -> false
            }

    /** Resolved spacing for a Theme: derive from [unitSpacing] when applicable. */
    fun resolveSpacing(unitSpacing: Float): TwSpacingScale =
        if (spacingDeriveFromUnit) TwSpacingScale.fromUnit(unitSpacing) else spacing
}

private fun Typography.withFontFamily(family: FontFamily): Typography =
    Typography(
        displayLarge = displayLarge.copy(fontFamily = family),
        displayMedium = displayMedium.copy(fontFamily = family),
        titleLarge = titleLarge.copy(fontFamily = family),
        titleMedium = titleMedium.copy(fontFamily = family),
        titleSmall = titleSmall.copy(fontFamily = family),
        bodyLarge = bodyLarge.copy(fontFamily = family),
        bodyMedium = bodyMedium.copy(fontFamily = family),
        bodySmall = bodySmall.copy(fontFamily = family),
        labelLarge = labelLarge.copy(fontFamily = family),
        labelSmall = labelSmall.copy(fontFamily = family),
    )

private fun Typography.scaled(factor: Float): Typography =
    Typography(
        displayLarge = displayLarge.scaled(factor),
        displayMedium = displayMedium.scaled(factor),
        titleLarge = titleLarge.scaled(factor),
        titleMedium = titleMedium.scaled(factor),
        titleSmall = titleSmall.scaled(factor),
        bodyLarge = bodyLarge.scaled(factor),
        bodyMedium = bodyMedium.scaled(factor),
        bodySmall = bodySmall.scaled(factor),
        labelLarge = labelLarge.scaled(factor),
        labelSmall = labelSmall.scaled(factor),
    )

private fun TextStyle.scaled(factor: Float): TextStyle =
    copy(
        fontSize = fontSize.scaled(factor),
        lineHeight = lineHeight.scaled(factor),
    )

private fun TextUnit.scaled(factor: Float): TextUnit = if (isSpecified) this * factor else this
