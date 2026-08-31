package com.toutiaoyan.compose.ui.tokens

import androidx.compose.runtime.Immutable
import androidx.compose.ui.graphics.Color
import com.toutiaoyan.compose.tailwind.core.Oklch
import com.toutiaoyan.compose.tailwind.core.TwColors
import com.toutiaoyan.compose.tailwind.core.toColor


/**
 * shadcn/ui's real "base color" picker (ui.shadcn.com/create): a named neutral gray
 * family that defines the *entire* non-accent palette -- background, foreground,
 * secondary, muted, border, and ring. This is a different axis from [Accent],
 * which only overrides primary/primaryForeground on top of whichever base is selected.
 *
 * Every field below is a named `TwColors` shade (tailwind-compose's real, oklch-sourced
 * Tailwind v4 palette -- verified byte-for-byte identical to this file's previous hex
 * literals via a scratch test comparing every field against the full 26-hue/11-shade
 * table before migrating, see PR history). `background` is plain white (real shadcn's
 * `oklch(1 0 0)`, not a tinted shade). Each family follows the same real-shadcn shade
 * mapping: background=white/{f}950=foreground/{f}900=primary/{f}50=onPrimary/
 * {f}100=secondary/{f}500=onSurfaceVariant/{f}200=border/{f}400=ring, with dark mode
 * inverting light's primary/onPrimary pair and raising secondary/border to {f}800.
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
            ),
        dark =
            baseDark(
                background = TwColors.neutral950,
                foreground = TwColors.neutral50,
                primary = TwColors.neutral200,
                primaryForeground = TwColors.neutral900,
                secondary = TwColors.neutral800,
                mutedForeground = TwColors.neutral400,
                border = TwColors.neutral800,
                ring = TwColors.neutral400,
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
            ),
        dark =
            baseDark(
                background = TwColors.stone950,
                foreground = TwColors.stone50,
                primary = TwColors.stone200,
                primaryForeground = TwColors.stone900,
                secondary = TwColors.stone800,
                mutedForeground = TwColors.stone400,
                border = TwColors.stone800,
                ring = TwColors.stone400,
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
            ),
        // zinc.json dark sidebar-primary is a hardcoded blue, not aliased to primary
        // like other base families / light mode.
        dark =
            baseDark(
                background = TwColors.zinc950,
                foreground = TwColors.zinc50,
                primary = TwColors.zinc200,
                primaryForeground = TwColors.zinc900,
                secondary = TwColors.zinc800,
                mutedForeground = TwColors.zinc400,
                border = TwColors.zinc800,
                ring = TwColors.zinc400,
            ).copy(
                sidebarPrimary = Oklch(0.488f, 0.243f, 264.376f).toColor(),
                sidebarPrimaryForeground = TwColors.zinc50,
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
            ),
        dark =
            baseDark(
                background = TwColors.mauve950,
                foreground = TwColors.mauve50,
                primary = TwColors.mauve200,
                primaryForeground = TwColors.mauve900,
                secondary = TwColors.mauve800,
                mutedForeground = TwColors.mauve400,
                border = TwColors.mauve800,
                ring = TwColors.mauve400,
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
            ),
        dark =
            baseDark(
                background = TwColors.olive950,
                foreground = TwColors.olive50,
                primary = TwColors.olive200,
                primaryForeground = TwColors.olive900,
                secondary = TwColors.olive800,
                mutedForeground = TwColors.olive400,
                border = TwColors.olive800,
                ring = TwColors.olive400,
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
            ),
        dark =
            baseDark(
                background = TwColors.mist950,
                foreground = TwColors.mist50,
                primary = TwColors.mist200,
                primaryForeground = TwColors.mist900,
                secondary = TwColors.mist800,
                mutedForeground = TwColors.mist400,
                border = TwColors.mist800,
                ring = TwColors.mist400,
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
            ),
        dark =
            baseDark(
                background = TwColors.taupe950,
                foreground = TwColors.taupe50,
                primary = TwColors.taupe200,
                primaryForeground = TwColors.taupe900,
                secondary = TwColors.taupe800,
                mutedForeground = TwColors.taupe400,
                border = TwColors.taupe800,
                ring = TwColors.taupe400,
            ),
    ),

    ;

    val label: String get() = name
}

// Shared semantic colors that stay constant across every base -- matches real
// shadcn, which never re-derives destructive/success/warning from the base color.

private val DESTRUCTIVE_LIGHT = TwColors.red600
private val DESTRUCTIVE_DARK = TwColors.red400

/**
 * Midpoint RGB blend of two opaque colors -- used to derive dark-mode card/popover/
 * sidebar tones as a "raised panel" between `background` and `secondary`. Verified
 * against real shadcn's zinc.json: blending Zinc's dark `background` (#09090B) and
 * `secondary` (#27272A) this way reproduces its real oklch-derived `card`/`popover`/
 * `sidebar` value (#18181B) to within one rounding unit -- close enough that deriving
 * the other six base-color families' card/popover/sidebar the same way (rather than
 * hand-verifying oklch values across 7 families x 2 modes) is a reasonable trade.
 */
private fun blend(
    a: Color,
    b: Color,
): Color {
    val ar = (a.red * 255f).toInt()
    val ag = (a.green * 255f).toInt()
    val ab = (a.blue * 255f).toInt()
    val br = (b.red * 255f).toInt()
    val bg = (b.green * 255f).toInt()
    val bb = (b.blue * 255f).toInt()
    val r = (ar + br + 1) / 2
    val g = (ag + bg + 1) / 2
    val bl = (ab + bb + 1) / 2
    return Color(0xFF000000.toInt() or (r shl 16) or (g shl 8) or bl)
}

private fun baseLight(
    background: Color,
    foreground: Color,
    primary: Color,
    primaryForeground: Color,
    secondary: Color,
    mutedForeground: Color,
    border: Color,
    ring: Color,
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
    chart1 = TwColors.orange600,
    chart2 = TwColors.teal600,
    chart3 = TwColors.blue900,
    chart4 = TwColors.yellow400,
    chart5 = TwColors.amber500,
    sidebar = background,
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
    border: Color,
    ring: Color,
) = Colors(
    background = background,
    foreground = foreground,
    card = blend(background, secondary),
    cardForeground = foreground,
    popover = blend(background, secondary),
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
    border = border,
    input = border,
    ring = ring,
    chart1 = TwColors.violet600,
    chart2 = TwColors.teal500,
    chart3 = TwColors.amber500,
    chart4 = TwColors.fuchsia600,
    chart5 = TwColors.rose500,
    sidebar = blend(background, secondary),
    sidebarForeground = foreground,
    sidebarPrimary = primary,
    sidebarPrimaryForeground = primaryForeground,
    sidebarAccent = secondary,
    sidebarAccentForeground = foreground,
    sidebarBorder = border,
    sidebarRing = ring,
)
