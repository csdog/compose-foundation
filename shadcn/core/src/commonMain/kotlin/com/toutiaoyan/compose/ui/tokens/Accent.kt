package com.toutiaoyan.compose.ui.tokens

import androidx.compose.runtime.Immutable
import androidx.compose.ui.graphics.Color
import com.toutiaoyan.compose.tailwind.core.TwColors

/**
 * Official accent overrides from apps/v4/registry/themes.ts, applied over BaseColor.
 * Primary, secondary, charts and sidebar primary are explicit upstream roles.
 * Unmentioned roles, including the focus ring, retain the base palette's values.
 * Rose additionally overrides the dark sidebar surface. Base applies no overrides.
 */
@Immutable
enum class Accent(
    private val lightPrimary: Color? = null,
    private val darkPrimary: Color? = null,
    private val onPrimary: Color? = null,
    private val charts: List<Color> = emptyList(),
    private val lightSidebarForeground: Color? = onPrimary,
    private val darkSidebarForeground: Color? = lightSidebarForeground,
) {
    Base(),

    Amber(
        TwColors.amber700,
        TwColors.amber800,
        TwColors.amber50,
        listOf(TwColors.amber300, TwColors.amber500, TwColors.amber600, TwColors.amber700, TwColors.amber800),
        darkSidebarForeground = TwColors.amber950,
    ),
    Blue(
        TwColors.blue700,
        TwColors.blue800,
        TwColors.blue50,
        listOf(TwColors.blue300, TwColors.blue500, TwColors.blue600, TwColors.blue700, TwColors.blue800),
    ),
    Cyan(
        TwColors.cyan700,
        TwColors.cyan800,
        TwColors.cyan50,
        listOf(TwColors.cyan300, TwColors.cyan500, TwColors.cyan600, TwColors.cyan700, TwColors.cyan800),
        darkSidebarForeground = TwColors.cyan950,
    ),
    Emerald(
        TwColors.emerald700,
        TwColors.emerald800,
        TwColors.emerald50,
        listOf(TwColors.emerald300, TwColors.emerald500, TwColors.emerald600, TwColors.emerald700, TwColors.emerald800),
        darkSidebarForeground = TwColors.emerald950,
    ),
    Fuchsia(
        TwColors.fuchsia700,
        TwColors.fuchsia800,
        TwColors.fuchsia50,
        listOf(TwColors.fuchsia300, TwColors.fuchsia500, TwColors.fuchsia600, TwColors.fuchsia700, TwColors.fuchsia800),
    ),
    Green(
        TwColors.green700,
        TwColors.green800,
        TwColors.green50,
        listOf(TwColors.green300, TwColors.green500, TwColors.green600, TwColors.green700, TwColors.green800),
    ),
    Indigo(
        TwColors.indigo700,
        TwColors.indigo800,
        TwColors.indigo50,
        listOf(TwColors.indigo300, TwColors.indigo500, TwColors.indigo600, TwColors.indigo700, TwColors.indigo800),
    ),
    Lime(
        TwColors.lime400,
        TwColors.lime500,
        TwColors.lime900,
        listOf(TwColors.lime300, TwColors.lime500, TwColors.lime600, TwColors.lime700, TwColors.lime800),
        lightSidebarForeground = TwColors.lime50,
        darkSidebarForeground = TwColors.lime950,
    ),
    Orange(
        TwColors.orange700,
        TwColors.orange800,
        TwColors.orange50,
        listOf(TwColors.orange300, TwColors.orange500, TwColors.orange600, TwColors.orange700, TwColors.orange800),
    ),
    Pink(
        TwColors.pink700,
        TwColors.pink800,
        TwColors.pink50,
        listOf(TwColors.pink300, TwColors.pink500, TwColors.pink600, TwColors.pink700, TwColors.pink800),
    ),
    Purple(
        TwColors.purple700,
        TwColors.purple800,
        TwColors.purple50,
        listOf(TwColors.purple300, TwColors.purple500, TwColors.purple600, TwColors.purple700, TwColors.purple800),
    ),
    Red(
        TwColors.red700,
        TwColors.red800,
        TwColors.red50,
        listOf(TwColors.red300, TwColors.red500, TwColors.red600, TwColors.red700, TwColors.red800),
    ),
    Rose(
        TwColors.rose700,
        TwColors.rose800,
        TwColors.rose50,
        listOf(TwColors.rose300, TwColors.rose500, TwColors.rose600, TwColors.rose700, TwColors.rose800),
    ),
    Sky(
        TwColors.sky700,
        TwColors.sky800,
        TwColors.sky50,
        listOf(TwColors.sky300, TwColors.sky500, TwColors.sky600, TwColors.sky700, TwColors.sky800),
        darkSidebarForeground = TwColors.sky950,
    ),
    Teal(
        TwColors.teal700,
        TwColors.teal800,
        TwColors.teal50,
        listOf(TwColors.teal300, TwColors.teal500, TwColors.teal600, TwColors.teal700, TwColors.teal800),
        darkSidebarForeground = TwColors.teal950,
    ),
    Violet(
        TwColors.violet700,
        TwColors.violet800,
        TwColors.violet50,
        listOf(TwColors.violet300, TwColors.violet500, TwColors.violet600, TwColors.violet700, TwColors.violet800),
    ),
    Yellow(
        TwColors.yellow400,
        TwColors.yellow500,
        TwColors.yellow900,
        listOf(TwColors.yellow300, TwColors.yellow500, TwColors.yellow600, TwColors.yellow700, TwColors.yellow800),
        lightSidebarForeground = TwColors.yellow50,
    ),
    ;

    val label: String get() = name

    /** Applies only the accent roles declared by the official theme registry. */
    fun applyTo(
        base: Colors,
        dark: Boolean,
    ): Colors {
        val primary = (if (dark) darkPrimary else lightPrimary) ?: return base
        return base.copy(
            primary = primary,
            primaryForeground = checkNotNull(onPrimary),
            secondary = if (dark) TwColors.zinc800 else TwColors.zinc100,
            secondaryForeground = if (dark) TwColors.zinc50 else TwColors.zinc900,
            chart1 = charts[0],
            chart2 = charts[1],
            chart3 = charts[2],
            chart4 = charts[3],
            chart5 = charts[4],
            sidebar = if (dark && this == Rose) TwColors.zinc900 else base.sidebar,
            sidebarPrimary = if (dark) charts[1] else charts[2],
            sidebarPrimaryForeground = checkNotNull(if (dark) darkSidebarForeground else lightSidebarForeground),
        )
    }
}
