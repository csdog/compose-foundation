package com.toutiaoyan.compose.ui.tokens

import androidx.compose.ui.graphics.Color
import com.toutiaoyan.compose.tailwind.core.Oklch
import com.toutiaoyan.compose.tailwind.core.toColor
import kotlin.test.Test
import kotlin.test.assertEquals

/** Independent, frozen upstream CSS fixture; does not derive expectations from BaseColor. */
class BaseColorSourceTest {
    @Test fun allLightRolesMatchOfficialRegistry() = check("light")

    @Test fun allDarkRolesMatchOfficialRegistry() = check("dark")

    private fun check(mode: String) {
        val fixture =
            checkNotNull(
                javaClass.getResourceAsStream("/themes/shadcn-base-colors.tsv"),
            ).bufferedReader().use {
                it.readLines()
            }
        var checked = 0
        for (line in fixture.drop(2)) {
            val (base, sheet, role, css) = line.split('\t')
            if (sheet != mode) continue
            val palette = BaseColor.entries.single { it.name.equals(base, ignoreCase = true) }
            val actual = roles(if (sheet == "dark") palette.dark else palette.light).getValue(role)
            assertEquals(cssColor(css), actual, "$base/$sheet/$role must use $css")
            checked++
        }
        assertEquals(217, checked, "All 31 upstream roles across seven base colors must be checked")
    }

    private fun cssColor(css: String): Color {
        val match = Regex("""oklch\(([\d.]+) ([\d.]+) ([\d.]+)(?: / ([\d.]+)%)?\)""").matchEntire(css) ?: error(css)
        val (l, c, h, percentage) = match.destructured
        return Oklch(
            l.toFloat(),
            c.toFloat(),
            h.toFloat(),
        ).toColor().copy(alpha = if (percentage.isEmpty()) 1f else percentage.toFloat() / 100f)
    }

    private fun roles(colors: Colors) =
        mapOf(
            "accent" to colors.accent,
            "accent-foreground" to colors.accentForeground,
            "background" to colors.background,
            "border" to colors.border,
            "card" to colors.card,
            "card-foreground" to colors.cardForeground,
            "chart-1" to colors.chart1,
            "chart-2" to colors.chart2,
            "chart-3" to colors.chart3,
            "chart-4" to colors.chart4,
            "chart-5" to colors.chart5,
            "destructive" to colors.destructive,
            "foreground" to colors.foreground,
            "input" to colors.input,
            "muted" to colors.muted,
            "muted-foreground" to colors.mutedForeground,
            "popover" to colors.popover,
            "popover-foreground" to colors.popoverForeground,
            "primary" to colors.primary,
            "primary-foreground" to colors.primaryForeground,
            "ring" to colors.ring,
            "secondary" to colors.secondary,
            "secondary-foreground" to colors.secondaryForeground,
            "sidebar" to colors.sidebar,
            "sidebar-accent" to colors.sidebarAccent,
            "sidebar-accent-foreground" to colors.sidebarAccentForeground,
            "sidebar-border" to colors.sidebarBorder,
            "sidebar-foreground" to colors.sidebarForeground,
            "sidebar-primary" to colors.sidebarPrimary,
            "sidebar-primary-foreground" to colors.sidebarPrimaryForeground,
            "sidebar-ring" to colors.sidebarRing,
        )
}
