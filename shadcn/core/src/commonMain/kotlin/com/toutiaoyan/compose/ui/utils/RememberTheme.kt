package com.toutiaoyan.compose.ui.utils

import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.TextUnit
import com.toutiaoyan.compose.tailwind.core.DefaultUnitSpacing
import com.toutiaoyan.compose.tailwind.core.SpacingUnit
import com.toutiaoyan.compose.tailwind.core.TwRadiusScale
import com.toutiaoyan.compose.tailwind.core.TwSpacingScale
import com.toutiaoyan.compose.tailwind.core.toDp
import com.toutiaoyan.compose.tailwind.core.toSp
import com.toutiaoyan.compose.tailwind.core.twRadius
import com.toutiaoyan.compose.tailwind.core.twSpacing
import com.toutiaoyan.compose.tailwind.core.unitSpacing
import com.toutiaoyan.compose.ui.theme.Theme
import com.toutiaoyan.compose.ui.theme.ThemeData
import com.toutiaoyan.compose.ui.tokens.Animations
import com.toutiaoyan.compose.ui.tokens.Colors
import com.toutiaoyan.compose.ui.tokens.IconStyles
import com.toutiaoyan.compose.ui.tokens.Radius
import com.toutiaoyan.compose.ui.tokens.Ring
import com.toutiaoyan.compose.ui.tokens.Typography

/**
 * Receiver for [rememberTheme] factories. Exposes [ThemeData] fields plus the active
 * [radius] / [spacing] / [unitSpacing] CompositionLocals so nested `Style { }` blocks can
 * write `colors` / `radius` / `spacing` / `n.spacing.dp` without importing StyleScope
 * extensions (outer-receiver resolution).
 */
class RememberThemeScope(
    val theme: ThemeData,
    val radius: TwRadiusScale,
    val spacing: TwSpacingScale,
    val unitSpacing: Float = DefaultUnitSpacing,
) {
    val colors: Colors get() = theme.colors
    val typography: Typography get() = theme.typography
    val ring: Ring get() = theme.ring
    val animations: Animations get() = theme.animations
    val icons: IconStyles get() = theme.icons
    val baseRadius: Radius get() = theme.baseRadius
    val isDark: Boolean get() = theme.isDark
    val isLight: Boolean get() = theme.isLight

    /** Resolve [SpacingUnit] with this scope's [unitSpacing] (for `Style { 1.5.spacing.dp }`). */
    val SpacingUnit.dp: Dp get() = toDp(unitSpacing)

    val SpacingUnit.sp: TextUnit get() = toSp(unitSpacing)
}

/**
 * Remembers a value derived from [Theme.LocalTheme], keying on [theme], the active
 * [twRadius] / [twSpacing] / [unitSpacing], and optional [keys].
 */
@Composable
fun <T> rememberTheme(
    vararg keys: Any?,
    factory: RememberThemeScope.() -> T,
): T {
    val theme = Theme.LocalTheme.current
    val radius = twRadius
    val spacing = twSpacing
    val unit = unitSpacing
    val scope = RememberThemeScope(theme, radius, spacing, unit)
    return remember(theme, radius, spacing, unit, *keys) { scope.factory() }
}
