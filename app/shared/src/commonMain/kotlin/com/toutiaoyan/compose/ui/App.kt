package com.toutiaoyan.compose.ui

import com.toutiaoyan.compose.ui.components.drawer.DrawerIndentHost
import com.toutiaoyan.compose.ui.components.overlay.OverlayHost
import com.toutiaoyan.compose.ui.catalog.CatalogToastHost
import androidx.compose.foundation.background
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.navigation3.runtime.NavBackStack
import androidx.navigation3.runtime.rememberNavBackStack
import com.toutiaoyan.compose.ui.catalog.catalogFontFamily
import com.toutiaoyan.compose.ui.navigation.AppNavSavedStateConfiguration
import com.toutiaoyan.compose.ui.navigation.AppRouter
import com.toutiaoyan.compose.ui.navigation.AppRouterImpl
import com.toutiaoyan.compose.ui.navigation.CatalogNavDisplay
import com.toutiaoyan.compose.ui.navigation.DeepLinks
import com.toutiaoyan.compose.ui.navigation.RouteKey
import com.toutiaoyan.compose.ui.theme.Theme
import com.toutiaoyan.compose.ui.theme.currentTheme
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
// - Style/Base Color/Accent apply to the catalog preview scopes. This outer Theme
//   uses fixed constants so the top bar keeps a stable brand identity.
//   CatalogNavDisplay themes the sidebar and content pane. CatalogToastHost themes
//   the window-level Toaster only while the Toast detail page is active.
@Composable
@Preview
@Suppress("LongParameterList")
fun App(
    inboundDeepLinks: Flow<String> = emptyFlow(),
    searchRequests: Flow<Unit> = emptyFlow(),
    /** Optional startup preference; null follows the system until the first manual toggle. */
    initialDarkModeOverride: Boolean? = null,
    /** Called for explicit theme changes so the host can persist its preference. */
    onDarkModeOverrideChange: (Boolean) -> Unit = {},
    modifier: Modifier = Modifier,
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

    var darkModeOverride by remember { mutableStateOf(initialDarkModeOverride) }
    var stylePreset by remember { mutableStateOf(StylePreset.Nova) }
    var baseColor by remember { mutableStateOf(BaseColor.Neutral) }
    var accent by remember { mutableStateOf(Accent.Base) }
    val isDarkMode = darkModeOverride ?: isSystemInDarkTheme()

    Theme(
        // Fixed chrome identity -- not stylePreset/baseColor/accent, see doc comment above.
        preset = StylePreset.Vega,
        baseColor = BaseColor.Neutral,
        accent = Accent.Base,
        baseRadius = Radius(10.dp),
        isDark = isDarkMode,
        fontFamily = catalogFontFamily(),
    ) {
        OverlayHost(Modifier.background(currentTheme.colors.background).then(modifier)) {
            CatalogToastHost(
                toastPreset = stylePreset.takeIf { appRouter.current == RouteKey.ComponentDetail("toast") },
                toastBaseColor = baseColor,
                toastAccent = accent,
            ) {
                DrawerIndentHost {
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
                        onToggleDarkMode = {
                            darkModeOverride = it
                            onDarkModeOverrideChange(it)
                        },
                        searchRequests = searchRequests,
                    )
                }
            }
        }
    }
}
