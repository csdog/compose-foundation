package com.toutiaoyan.compose.ui.tokens

import androidx.compose.ui.graphics.Color
import com.toutiaoyan.compose.tailwind.core.Oklch
import com.toutiaoyan.compose.tailwind.core.toColor
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertSame

/** Frozen upstream overrides, independent of the implementation's named shades. */
class AccentSourceTest {
    @Test fun lightOverridesAndUnmentionedRolesMatchOfficialSource() = check(false)

    @Test fun darkOverridesAndUnmentionedRolesMatchOfficialSource() = check(true)

    @Test fun baseAccentPreservesPaletteIdentity() {
        for (base in BaseColor.entries) {
            assertSame(base.light, Accent.Base.applyTo(base.light, false))
            assertSame(base.dark, Accent.Base.applyTo(base.dark, true))
        }
    }

    private fun check(dark: Boolean) {
        val mode = if (dark) "dark" else "light"
        val rows =
            checkNotNull(javaClass.getResourceAsStream("/themes/shadcn-accent-overrides.tsv"))
                .bufferedReader().use { it.readLines().drop(2) }.map { it.split('\t') }
        assertEquals(375, rows.size)
        for (accent in Accent.entries.filter { it != Accent.Base }) {
            val overrides =
                rows.filter { it[0].equals(accent.name, ignoreCase = true) && it[1] == mode }
                    .associate { it[2] to cssColor(it[3]) }
            assertEquals(if (accent == Accent.Rose && dark) 12 else 11, overrides.size, accent.name)
            for (base in BaseColor.entries) {
                val original = if (dark) base.dark else base.light
                val expected = roles(original) + overrides
                val actual = roles(accent.applyTo(original, dark))
                assertEquals(expected, actual, "${accent.name}/${base.name}/$mode must only apply official overrides")
                assertEquals(original.destructiveForeground, accent.applyTo(original, dark).destructiveForeground)
            }
        }
    }

    private fun cssColor(css: String): Color {
        val match = Regex("""oklch\(([\d.]+) ([\d.]+) ([\d.]+)\)""").matchEntire(css) ?: error(css)
        val (l, c, h) = match.destructured
        return Oklch(l.toFloat(), c.toFloat(), h.toFloat()).toColor()
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
