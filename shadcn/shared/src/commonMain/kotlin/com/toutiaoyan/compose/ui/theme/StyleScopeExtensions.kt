package com.toutiaoyan.compose.ui.theme

import androidx.compose.foundation.style.ExperimentalFoundationStyleApi
import androidx.compose.foundation.style.StyleScope
import com.toutiaoyan.compose.ui.tokens.Colors
import com.toutiaoyan.compose.ui.tokens.Typography

// The only correct way to read a CompositionLocal inside a Style block: Styles run
// outside Composition, so `Theme.LocalTheme.current` would capture a stale
// value at definition time. StyleScope extends CompositionLocalAccessorScope, whose
// `currentValue` / [twRadius] / [spacing] extensions read at consume time instead.
//
// Prefer [colors]/[typography]/[spacing]/[radius]/[twRadius] shortcuts inside Style { }.
// Do not read the @Composable [currentTheme] getter from Style blocks.
//
// Spacing / radius StyleScope accessors live in tailwind
// ([com.toutiaoyan.compose.tailwind.core.StyleScope.spacing],
// [com.toutiaoyan.compose.tailwind.core.StyleScope.radius]).
// Inside [com.toutiaoyan.compose.ui.utils.rememberTheme], `colors` / `spacing` /
// `radius` usually resolve via the outer RememberThemeScope receiver (no import).

@OptIn(ExperimentalFoundationStyleApi::class)
val StyleScope.theme: ThemeData
    get() = Theme.LocalTheme.currentValue

@OptIn(ExperimentalFoundationStyleApi::class)
val StyleScope.colors: Colors
    get() = Theme.LocalTheme.currentValue.colors

@OptIn(ExperimentalFoundationStyleApi::class)
val StyleScope.typography: Typography
    get() = Theme.LocalTheme.currentValue.typography
