package com.toutiaoyan.compose.ui.tokens

import androidx.compose.ui.graphics.Color
import kotlin.math.pow
import kotlin.test.Test
import kotlin.test.assertTrue

/**
 * Every component Style pairs a background token with a content-color token (e.g.
 * `background(colors.primary)` + `contentColor(colors.primaryForeground)`). A bug that makes
 * one of those pairs resolve to (near-)identical colors in dark mode -- exactly the
 * class of bug chased down manually earlier in this project -- doesn't show up as a
 * crash or a layout diff; the text just silently stops being legible. These tests
 * catch that class of regression at the token level, for every base/accent/light/dark
 * combination, without needing to render anything.
 *
 * [MIN_CONTRAST] is deliberately below the WCAG AA text minimum (4.5) or even the
 * large-text/UI-component minimum (3.0): real shadcn/ui's own dark-mode destructive
 * button (bright coral background + white text) sits at ~2.9, and that's a source
 * value we're matching intentionally, not a bug. 2.5 is chosen to still catch a
 * pair collapsing toward "indistinguishable" (ratio near 1.0) while not flagging
 * upstream shadcn's existing design choices as failures.
 */
private const val MIN_CONTRAST = 2.5

private fun relativeLuminance(color: Color): Double {
    fun channel(value: Float): Double {
        val c = value.toDouble()
        return if (c <= 0.03928) c / 12.92 else ((c + 0.055) / 1.055).pow(2.4)
    }
    return 0.2126 * channel(color.red) + 0.7152 * channel(color.green) + 0.0722 * channel(color.blue)
}

/** WCAG 2.1 contrast ratio: (L1 + 0.05) / (L2 + 0.05), L1 the lighter of the two. */
private fun contrastRatio(
    a: Color,
    b: Color,
): Double {
    val la = relativeLuminance(a)
    val lb = relativeLuminance(b)
    val lighter = maxOf(la, lb)
    val darker = minOf(la, lb)
    return (lighter + 0.05) / (darker + 0.05)
}

private fun assertContrast(
    background: Color,
    content: Color,
    label: String,
    min: Double = MIN_CONTRAST,
) {
    val ratio = contrastRatio(background, content)
    assertTrue(
        ratio >= min,
        "$label: contrast ratio $ratio is below the $min minimum (background=$background, content=$content)",
    )
}

/**
 * Every background/content token pair a component's `Style` (Button, Badge, Chip, ...) actually
 * pairs up, plus the "surface-on-surface" pairs used by non-text controls (Switch's thumb on its
 * checked track, Slider's thumb on its range) where a `background`-colored shape must stay visible
 * against a `primary`-colored fill instead of a text color against it.
 *
 * Deliberately NOT asserted here: `background` vs `border`/`muted`. Switch's *unchecked* track and
 * Slider's empty track are intentionally low-contrast subtle surfaces (matching real shadcn/ui's
 * own switch.tsx/slider.tsx) -- asserting contrast there would fail on a real, intentional design
 * choice rather than catch a regression.
 */
private fun assertPalette(
    colors: Colors,
    label: String,
) {
    assertContrast(colors.primary, colors.primaryForeground, "$label primary/primaryForeground")
    assertContrast(colors.secondary, colors.secondaryForeground, "$label secondary/secondaryForeground")
    assertContrast(colors.destructive, colors.destructiveForeground, "$label destructive/destructiveForeground")
    assertContrast(colors.background, colors.foreground, "$label background/foreground")
    assertContrast(colors.muted, colors.mutedForeground, "$label muted/mutedForeground")
    assertContrast(colors.background, colors.mutedForeground, "$label background/mutedForeground (muted text)")
    assertContrast(colors.accent, colors.accentForeground, "$label accent/accentForeground")
    assertContrast(colors.primary, colors.background, "$label primary/background (Switch checked thumb, Slider thumb)")
    assertContrast(colors.card, colors.cardForeground, "$label card/cardForeground")
    assertContrast(colors.popover, colors.popoverForeground, "$label popover/popoverForeground")
    assertContrast(colors.sidebar, colors.sidebarForeground, "$label sidebar/sidebarForeground")
    assertContrast(colors.sidebarPrimary, colors.sidebarPrimaryForeground, "$label sidebarPrimary/sidebarPrimaryForeground")
    assertContrast(colors.sidebarAccent, colors.sidebarAccentForeground, "$label sidebarAccent/sidebarAccentForeground")
}

class ColorContrastTest {
    @Test
    fun `default light and dark palettes have sufficient contrast`() {
        assertPalette(LightColors, "LightColors")
        assertPalette(DarkColors, "DarkColors")
    }

    @Test
    fun `every BaseColor light and dark palette has sufficient contrast`() {
        for (base in BaseColor.entries) {
            assertPalette(base.light, "BaseColor.${base.name}.light")
            assertPalette(base.dark, "BaseColor.${base.name}.dark")
        }
    }

    @Test
    fun `every Accent overrides primary with sufficient contrast on every base in both light and dark`() {
        for (accent in Accent.entries) {
            for (base in BaseColor.entries) {
                val light = accent.applyTo(base.light, dark = false)
                val dark = accent.applyTo(base.dark, dark = true)
                assertContrast(
                    light.primary,
                    light.primaryForeground,
                    "Accent.${accent.name} on ${base.name}.light primary/primaryForeground",
                )
                assertContrast(
                    dark.primary,
                    dark.primaryForeground,
                    "Accent.${accent.name} on ${base.name}.dark primary/primaryForeground",
                )
            }
        }
    }
}
