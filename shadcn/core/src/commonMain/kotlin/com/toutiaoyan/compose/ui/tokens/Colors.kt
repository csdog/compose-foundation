package com.toutiaoyan.compose.ui.tokens

import androidx.compose.runtime.Immutable
import androidx.compose.ui.graphics.Color
import com.toutiaoyan.compose.tailwind.core.TwColors

/**
 * Theme color tokens aligned with shadcn/ui CSS variables
 * ([theming docs](https://ui.shadcn.com/docs/theming)), expressed as Kotlin camelCase
 * (`primary-foreground` → [primaryForeground]).
 *
 * Light/dark mode lives on [com.toutiaoyan.compose.ui.theme.ThemeData.isDark], not here.
 */
@Immutable
data class Colors(
    val background: Color,
    val foreground: Color,
    val card: Color,
    val cardForeground: Color,
    val popover: Color,
    val popoverForeground: Color,
    val primary: Color,
    val primaryForeground: Color,
    val secondary: Color,
    val secondaryForeground: Color,
    val muted: Color,
    val mutedForeground: Color,
    val accent: Color,
    val accentForeground: Color,
    val destructive: Color,
    val destructiveForeground: Color,
    val border: Color,
    val input: Color,
    val ring: Color,
    val chart1: Color,
    val chart2: Color,
    val chart3: Color,
    val chart4: Color,
    val chart5: Color,
    val sidebar: Color,
    val sidebarForeground: Color,
    val sidebarPrimary: Color,
    val sidebarPrimaryForeground: Color,
    val sidebarAccent: Color,
    val sidebarAccentForeground: Color,
    val sidebarBorder: Color,
    val sidebarRing: Color,
)

val LightColors =
    Colors(
        background = TwColors.white,
        foreground = TwColors.zinc950,
        card = TwColors.white,
        cardForeground = TwColors.zinc950,
        popover = TwColors.white,
        popoverForeground = TwColors.zinc950,
        // primary / secondaryForeground match real shadcn oklch(0.205 0 0) (neutral900)
        // -- see docs/shadcn-parity.md.
        primary = TwColors.neutral900,
        primaryForeground = TwColors.zinc50,
        secondary = TwColors.zinc100,
        secondaryForeground = TwColors.neutral900,
        muted = TwColors.zinc100,
        mutedForeground = TwColors.zinc500,
        accent = TwColors.zinc100,
        accentForeground = TwColors.neutral900,
        // Verified against oklch(0.577 0.245 27.325) -- see docs/shadcn-parity.md.
        destructive = TwColors.red600,
        // Real button/badge .tsx hardcode text-white for destructive rather than a themed
        // foreground token.
        destructiveForeground = TwColors.white,
        border = TwColors.zinc200,
        input = TwColors.zinc200,
        // shadcn ring is mid-gray (oklch ≈ zinc400), not primary.
        ring = TwColors.zinc400,
        // Approximate official neutral chart-* oklch triples with named TwColors.
        chart1 = TwColors.orange600,
        chart2 = TwColors.teal600,
        chart3 = TwColors.blue900,
        chart4 = TwColors.yellow400,
        chart5 = TwColors.amber500,
        sidebar = TwColors.zinc50,
        sidebarForeground = TwColors.zinc950,
        sidebarPrimary = TwColors.neutral900,
        sidebarPrimaryForeground = TwColors.zinc50,
        sidebarAccent = TwColors.zinc100,
        sidebarAccentForeground = TwColors.neutral900,
        sidebarBorder = TwColors.zinc200,
        sidebarRing = TwColors.zinc400,
    )

val DarkColors =
    Colors(
        background = TwColors.zinc950,
        foreground = TwColors.zinc50,
        card = TwColors.zinc900,
        cardForeground = TwColors.zinc50,
        popover = TwColors.zinc900,
        popoverForeground = TwColors.zinc50,
        // Dark primary is light gray oklch(0.922 0 0), not near-white -- see docs.
        primary = TwColors.neutral200,
        primaryForeground = TwColors.neutral900,
        secondary = TwColors.zinc800,
        secondaryForeground = TwColors.zinc50,
        muted = TwColors.zinc800,
        mutedForeground = TwColors.zinc400,
        accent = TwColors.zinc800,
        accentForeground = TwColors.zinc50,
        // Dark destructive is brighter coral than light (oklch(0.704 0.191 22.216)).
        destructive = TwColors.red400,
        destructiveForeground = TwColors.white,
        border = TwColors.zinc800,
        input = TwColors.zinc800,
        ring = TwColors.zinc500,
        chart1 = TwColors.violet600,
        chart2 = TwColors.teal500,
        chart3 = TwColors.amber500,
        chart4 = TwColors.fuchsia600,
        chart5 = TwColors.rose500,
        sidebar = TwColors.zinc900,
        sidebarForeground = TwColors.zinc50,
        sidebarPrimary = TwColors.neutral200,
        sidebarPrimaryForeground = TwColors.neutral900,
        sidebarAccent = TwColors.zinc800,
        sidebarAccentForeground = TwColors.zinc50,
        sidebarBorder = TwColors.zinc800,
        sidebarRing = TwColors.zinc500,
    )
