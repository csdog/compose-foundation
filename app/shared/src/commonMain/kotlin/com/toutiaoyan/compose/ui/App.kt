package com.toutiaoyan.compose.ui

import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.navigation3.runtime.NavBackStack
import androidx.navigation3.runtime.rememberNavBackStack
import com.toutiaoyan.compose.ui.navigation.AppNavSavedStateConfiguration
import com.toutiaoyan.compose.ui.navigation.AppRouter
import com.toutiaoyan.compose.ui.navigation.AppRouterImpl
import com.toutiaoyan.compose.ui.navigation.CatalogNavDisplay
import com.toutiaoyan.compose.ui.navigation.DeepLinks
import com.toutiaoyan.compose.ui.navigation.RouteKey
import com.toutiaoyan.compose.ui.theme.Theme
import com.toutiaoyan.compose.ui.tokens.Accent
import com.toutiaoyan.compose.ui.tokens.BaseColor
import com.toutiaoyan.compose.ui.tokens.Radius
import com.toutiaoyan.compose.ui.tokens.StylePreset
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.emptyFlow

// Two axes, two different scopes:
// - isDark (light/light-with-manual-override) applies everywhere -- top bar, sidebar,
//   and content pane all share the same isDarkMode below, since dark/light is a real
//   accessibility preference, not a "preview" setting.
// - Style/Base Color/Accent apply to the detail content pane ONLY. This outer
//   Theme deliberately uses fixed constants (not the live stylePreset/baseColor/
//   accent state), so the top bar keeps a stable brand identity regardless of what a
//   reader is previewing; CatalogNavHost nests a second Theme (same isDarkMode,
//   but the live picker values) around the sidebar + content pane.
@Composable
@Preview
fun App(
    inboundDeepLinks: Flow<String> = emptyFlow(),
    /**
     * Web: called once the Navigation 3 back stack is attached, for browser path sync
     * (see angryecho-kmp `onNavHostReady`).
     */
    onNavHostReady: suspend (AppRouter) -> Unit = {},
) {
    @Suppress("UNCHECKED_CAST")
    val backStack =
        rememberNavBackStack(AppNavSavedStateConfiguration, RouteKey.Introduction)
                as NavBackStack<RouteKey>
    val appRouter = remember { AppRouterImpl(RouteKey.Introduction) }

    DisposableEffect(backStack) {
        appRouter.attach(backStack)
        onDispose { appRouter.detach(backStack) }
    }

    LaunchedEffect(inboundDeepLinks) {
        inboundDeepLinks.collect { uri ->
            DeepLinks.resolve(uri)?.let { DeepLinks.open(appRouter, it) }
        }
    }

    LaunchedEffect(backStack) {
        onNavHostReady(appRouter)
    }

    var darkModeOverride by remember { mutableStateOf<Boolean?>(null) }
    var stylePreset by remember { mutableStateOf(StylePreset.Vega) }
    var baseColor by remember { mutableStateOf(BaseColor.Zinc) }
    var accent by remember { mutableStateOf(Accent.Base) }
    val isDarkMode = darkModeOverride ?: isSystemInDarkTheme()

    Theme(
        // Fixed chrome identity -- not stylePreset/baseColor/accent, see doc comment above.
        preset = StylePreset.Vega,
        baseColor = BaseColor.Zinc,
        accent = Accent.Base,
        baseRadius = Radius(4.dp),
        isDark = isDarkMode,
    ) {
        CatalogNavDisplay(
            backStack = backStack,
            appRouter = appRouter,
            stylePreset = stylePreset,
            onStylePresetChange = { stylePreset = it },
            baseColor = baseColor,
            onBaseColorChange = { baseColor = it },
            accent = accent,
            onAccentChange = { accent = it },
            isDarkMode = isDarkMode,
            onToggleDarkMode = { darkModeOverride = it },
        )
    }
}
