package com.toutiaoyan.compose.ui.tokens

import androidx.compose.runtime.Immutable
import androidx.compose.ui.graphics.Color
import com.toutiaoyan.compose.tailwind.core.TwColors

/**
 * Official shadcn/ui base palettes from apps/v4/public/r/colors/<base>.json.
 * Named Tailwind shades preserve the source OKLCH values. Dark border/input use
 * translucent white roles; surface, ring, chart and sidebar roles are explicit,
 * rather than approximated by RGB blends or borrowed from other base families.
 * Accent overrides are applied separately by Theme.
 */
@Immutable
enum class BaseColor(
    val light: Colors,
    val dark: Colors,
) {
    Neutral(
        light =
            baseLight(
                background = TwColors.white,
                foreground = TwColors.neutral950,
                primary = TwColors.neutral900,
                primaryForeground = TwColors.neutral50,
                secondary = TwColors.neutral100,
                mutedForeground = TwColors.neutral500,
                border = TwColors.neutral200,
                ring = TwColors.neutral400,
                charts =
                    listOf(
                        TwColors.neutral300,
                        TwColors.neutral500,
                        TwColors.neutral600,
                        TwColors.neutral700,
                        TwColors.neutral800,
                    ),
            ),
        dark =
            baseDark(
                background = TwColors.neutral950,
                foreground = TwColors.neutral50,
                primary = TwColors.neutral200,
                primaryForeground = TwColors.neutral900,
                secondary = TwColors.neutral800,
                mutedForeground = TwColors.neutral400,
                ring = TwColors.neutral500,
                charts =
                    listOf(
                        TwColors.neutral300,
                        TwColors.neutral500,
                        TwColors.neutral600,
                        TwColors.neutral700,
                        TwColors.neutral800,
                    ),
            ),
    ),

    Stone(
        light =
            baseLight(
                background = TwColors.white,
                foreground = TwColors.stone950,
                primary = TwColors.stone900,
                primaryForeground = TwColors.stone50,
                secondary = TwColors.stone100,
                mutedForeground = TwColors.stone500,
                border = TwColors.stone200,
                ring = TwColors.stone400,
                charts =
                    listOf(
                        TwColors.stone300,
                        TwColors.stone500,
                        TwColors.stone600,
                        TwColors.stone700,
                        TwColors.stone800,
                    ),
            ),
        dark =
            baseDark(
                background = TwColors.stone950,
                foreground = TwColors.stone50,
                primary = TwColors.stone200,
                primaryForeground = TwColors.stone900,
                secondary = TwColors.stone800,
                mutedForeground = TwColors.stone400,
                ring = TwColors.stone500,
                charts =
                    listOf(
                        TwColors.stone300,
                        TwColors.stone500,
                        TwColors.stone600,
                        TwColors.stone700,
                        TwColors.stone800,
                    ),
            ),
    ),

    Zinc(
        light =
            baseLight(
                background = TwColors.white,
                foreground = TwColors.zinc950,
                primary = TwColors.zinc900,
                primaryForeground = TwColors.zinc50,
                secondary = TwColors.zinc100,
                mutedForeground = TwColors.zinc500,
                border = TwColors.zinc200,
                ring = TwColors.zinc400,
                charts =
                    listOf(
                        TwColors.zinc300,
                        TwColors.zinc500,
                        TwColors.zinc600,
                        TwColors.zinc700,
                        TwColors.zinc800,
                    ),
            ),
        dark =
            baseDark(
                background = TwColors.zinc950,
                foreground = TwColors.zinc50,
                primary = TwColors.zinc200,
                primaryForeground = TwColors.zinc900,
                secondary = TwColors.zinc800,
                mutedForeground = TwColors.zinc400,
                ring = TwColors.zinc500,
                charts =
                    listOf(
                        TwColors.zinc300,
                        TwColors.zinc500,
                        TwColors.zinc600,
                        TwColors.zinc700,
                        TwColors.zinc800,
                    ),
            ),
    ),

    Mauve(
        light =
            baseLight(
                background = TwColors.white,
                foreground = TwColors.mauve950,
                primary = TwColors.mauve900,
                primaryForeground = TwColors.mauve50,
                secondary = TwColors.mauve100,
                mutedForeground = TwColors.mauve500,
                border = TwColors.mauve200,
                ring = TwColors.mauve400,
                charts =
                    listOf(
                        TwColors.mauve300,
                        TwColors.mauve500,
                        TwColors.mauve600,
                        TwColors.mauve700,
                        TwColors.mauve800,
                    ),
            ),
        dark =
            baseDark(
                background = TwColors.mauve950,
                foreground = TwColors.mauve50,
                primary = TwColors.mauve200,
                primaryForeground = TwColors.mauve900,
                secondary = TwColors.mauve800,
                mutedForeground = TwColors.mauve400,
                ring = TwColors.mauve500,
                charts =
                    listOf(
                        TwColors.mauve300,
                        TwColors.mauve500,
                        TwColors.mauve600,
                        TwColors.mauve700,
                        TwColors.mauve800,
                    ),
            ),
    ),

    Olive(
        light =
            baseLight(
                background = TwColors.white,
                foreground = TwColors.olive950,
                primary = TwColors.olive900,
                primaryForeground = TwColors.olive50,
                secondary = TwColors.olive100,
                mutedForeground = TwColors.olive500,
                border = TwColors.olive200,
                ring = TwColors.olive400,
                charts =
                    listOf(
                        TwColors.olive300,
                        TwColors.olive500,
                        TwColors.olive600,
                        TwColors.olive700,
                        TwColors.olive800,
                    ),
            ),
        dark =
            baseDark(
                background = TwColors.olive950,
                foreground = TwColors.olive50,
                primary = TwColors.olive200,
                primaryForeground = TwColors.olive900,
                secondary = TwColors.olive800,
                mutedForeground = TwColors.olive400,
                ring = TwColors.olive500,
                charts =
                    listOf(
                        TwColors.olive300,
                        TwColors.olive500,
                        TwColors.olive600,
                        TwColors.olive700,
                        TwColors.olive800,
                    ),
            ),
    ),

    Mist(
        light =
            baseLight(
                background = TwColors.white,
                foreground = TwColors.mist950,
                primary = TwColors.mist900,
                primaryForeground = TwColors.mist50,
                secondary = TwColors.mist100,
                mutedForeground = TwColors.mist500,
                border = TwColors.mist200,
                ring = TwColors.mist400,
                charts =
                    listOf(
                        TwColors.mist300,
                        TwColors.mist500,
                        TwColors.mist600,
                        TwColors.mist700,
                        TwColors.mist800,
                    ),
            ),
        dark =
            baseDark(
                background = TwColors.mist950,
                foreground = TwColors.mist50,
                primary = TwColors.mist200,
                primaryForeground = TwColors.mist900,
                secondary = TwColors.mist800,
                mutedForeground = TwColors.mist400,
                ring = TwColors.mist500,
                charts =
                    listOf(
                        TwColors.mist300,
                        TwColors.mist500,
                        TwColors.mist600,
                        TwColors.mist700,
                        TwColors.mist800,
                    ),
            ),
    ),

    Taupe(
        light =
            baseLight(
                background = TwColors.white,
                foreground = TwColors.taupe950,
                primary = TwColors.taupe900,
                primaryForeground = TwColors.taupe50,
                secondary = TwColors.taupe100,
                mutedForeground = TwColors.taupe500,
                border = TwColors.taupe200,
                ring = TwColors.taupe400,
                charts =
                    listOf(
                        TwColors.taupe300,
                        TwColors.taupe500,
                        TwColors.taupe600,
                        TwColors.taupe700,
                        TwColors.taupe800,
                    ),
            ),
        dark =
            baseDark(
                background = TwColors.taupe950,
                foreground = TwColors.taupe50,
                primary = TwColors.taupe200,
                primaryForeground = TwColors.taupe900,
                secondary = TwColors.taupe800,
                mutedForeground = TwColors.taupe400,
                ring = TwColors.taupe500,
                charts =
                    listOf(
                        TwColors.taupe300,
                        TwColors.taupe500,
                        TwColors.taupe600,
                        TwColors.taupe700,
                        TwColors.taupe800,
                    ),
            ),
    ),

    ;

    val label: String get() = name
}

// Shared semantic colors that stay constant across every base -- matches real
// shadcn, which never re-derives destructive/success/warning from the base color.

private val DESTRUCTIVE_LIGHT = TwColors.red600
private val DESTRUCTIVE_DARK = TwColors.red400

private fun baseLight(
    background: Color,
    foreground: Color,
    primary: Color,
    primaryForeground: Color,
    secondary: Color,
    mutedForeground: Color,
    border: Color,
    ring: Color,
    charts: List<Color>,
) = Colors(
    background = background,
    foreground = foreground,
    card = background,
    cardForeground = foreground,
    popover = background,
    popoverForeground = foreground,
    primary = primary,
    primaryForeground = primaryForeground,
    secondary = secondary,
    secondaryForeground = primary,
    muted = secondary,
    mutedForeground = mutedForeground,
    accent = secondary,
    accentForeground = primary,
    destructive = DESTRUCTIVE_LIGHT,
    destructiveForeground = TwColors.white,
    border = border,
    input = border,
    ring = ring,
    chart1 = charts[0],
    chart2 = charts[1],
    chart3 = charts[2],
    chart4 = charts[3],
    chart5 = charts[4],
    sidebar = primaryForeground,
    sidebarForeground = foreground,
    sidebarPrimary = primary,
    sidebarPrimaryForeground = primaryForeground,
    sidebarAccent = secondary,
    sidebarAccentForeground = primary,
    sidebarBorder = border,
    sidebarRing = ring,
)

private fun baseDark(
    background: Color,
    foreground: Color,
    primary: Color,
    primaryForeground: Color,
    secondary: Color,
    mutedForeground: Color,
    ring: Color,
    charts: List<Color>,
) = Colors(
    background = background,
    foreground = foreground,
    card = primaryForeground,
    cardForeground = foreground,
    popover = primaryForeground,
    popoverForeground = foreground,
    primary = primary,
    primaryForeground = primaryForeground,
    secondary = secondary,
    secondaryForeground = foreground,
    muted = secondary,
    mutedForeground = mutedForeground,
    accent = secondary,
    accentForeground = foreground,
    destructive = DESTRUCTIVE_DARK,
    destructiveForeground = TwColors.white,
    border = Color.White.copy(alpha = 0.1f),
    input = Color.White.copy(alpha = 0.15f),
    ring = ring,
    chart1 = charts[0],
    chart2 = charts[1],
    chart3 = charts[2],
    chart4 = charts[3],
    chart5 = charts[4],
    sidebar = primaryForeground,
    sidebarForeground = foreground,
    sidebarPrimary = TwColors.blue700,
    sidebarPrimaryForeground = foreground,
    sidebarAccent = secondary,
    sidebarAccentForeground = foreground,
    sidebarBorder = Color.White.copy(alpha = 0.1f),
    sidebarRing = ring,
)
