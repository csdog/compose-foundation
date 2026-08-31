package com.toutiaoyan.compose.ui.navigation

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.width
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.navigation3.runtime.NavBackStack
import androidx.navigation3.runtime.entryProvider
import androidx.navigation3.ui.NavDisplay
import com.toutiaoyan.compose.ui.catalog.CatalogSidebar
import com.toutiaoyan.compose.ui.catalog.CatalogTopBar
import com.toutiaoyan.compose.ui.catalog.ComponentDetailScreen
import com.toutiaoyan.compose.ui.catalog.CreatePage
import com.toutiaoyan.compose.ui.theme.Theme
import com.toutiaoyan.compose.ui.theme.currentTheme
import com.toutiaoyan.compose.ui.tokens.Accent
import com.toutiaoyan.compose.ui.tokens.BaseColor
import com.toutiaoyan.compose.ui.tokens.Radius
import com.toutiaoyan.compose.ui.tokens.StylePreset

/**
 * Below this width, there isn't enough room for a persistent 240dp sidebar plus a
 * usable content pane -- single-word labels (chip/button text) start wrapping
 * letter-by-letter instead of the layout adapting. Matches a typical phone-portrait
 * width plus a little headroom.
 */
private val COMPACT_BREAKPOINT = 720.dp

// Matches App.kt's own outer Theme's baseRadius -- the nested content-pane
// Theme below needs the same value so the content pane's shapes stay visually
// consistent with the chrome around it.
private val CATALOG_BASE_RADIUS = Radius(4.dp)

/**
 * Sidebar + content-pane shell -- only the content pane swaps via [NavDisplay]. Matches
 * the shadcn/ui docs-site layout rather than a phone-app push/pop list. Above
 * [COMPACT_BREAKPOINT] the sidebar stays persistently on screen; below it, the
 * sidebar becomes a dismissible drawer opened from a menu button in the top bar,
 * so narrow/phone-width screens get a full-width content pane instead of a
 * squeezed-down sidebar-plus-content split.
 *
 * [backStack] / [appRouter] are owned by [com.toutiaoyan.compose.ui.App] so web can
 * bind browser history (see angryecho-kmp `onNavHostReady`).
 *
 * Two theming axes, two different scopes -- see [CatalogContentTheme], nested around
 * everything below the top bar:
 * - [isDarkMode]/[onToggleDarkMode] apply everywhere, top bar included -- both this
 *   nested theme and the *outer* Theme in
 *   [com.toutiaoyan.compose.ui.App] read the same shared `isDarkMode` value,
 *   since dark/light is a real accessibility preference, not a preview-only setting.
 * - [stylePreset]/[baseColor]/[accent] apply to the sidebar and content pane only
 *   (and Create page pickers) -- the outer theme wrapping the top bar uses fixed
 *   constants instead, so the top bar keeps a stable brand identity.
 */
@Composable
fun CatalogNavDisplay(
    backStack: NavBackStack<RouteKey>,
    appRouter: AppRouter,
    stylePreset: StylePreset,
    onStylePresetChange: (StylePreset) -> Unit,
    baseColor: BaseColor,
    onBaseColorChange: (BaseColor) -> Unit,
    accent: Accent,
    onAccentChange: (Accent) -> Unit,
    isDarkMode: Boolean,
    onToggleDarkMode: (Boolean) -> Unit,
) {
    val current = appRouter.current
    val isOnCreateRoute = current is RouteKey.Create

    var drawerOpen by remember { mutableStateOf(false) }
    // Hoisted here, not a local `remember` inside CatalogSidebar, since this composable
    // instantiates two separate CatalogSidebar instances below (persistent desktop
    // sidebar + mobile drawer overlay) that need to share one query rather than each
    // starting fresh -- a resize crossing COMPACT_BREAKPOINT would otherwise silently
    // drop an in-progress search.
    var searchQuery by remember { mutableStateOf("") }
    var createGetCodeRequest by remember { mutableIntStateOf(0) }

    fun navigateTo(route: RouteKey) {
        // Docs-site replace: always land on a single content route (clears Create).
        appRouter.replaceAll(route)
        drawerOpen = false
        searchQuery = ""
    }

    fun navigateToCreate() {
        appRouter.push(RouteKey.Create)
    }

    BoxWithConstraints(modifier = Modifier.fillMaxSize()) {
        val isCompact = maxWidth < COMPACT_BREAKPOINT

        Column(modifier = Modifier.fillMaxSize()) {
            CatalogTopBar(
                isDarkMode = isDarkMode,
                onToggleDarkMode = onToggleDarkMode,
                isOnCreateRoute = isOnCreateRoute,
                onNavigateHome = { appRouter.replaceAll(RouteKey.Introduction) },
                onNavigateToComponents = {
                    navigateTo(current)
                },
                onNavigateToCreate = ::navigateToCreate,
                onGetCode = { createGetCodeRequest++ },
            )

            // Nested Theme, scoped to everything below the top bar -- isDarkMode
            // (the manual toggle) applies to the sidebar and content pane, but not the
            // top bar above (which stays on the outer, system-only theme in App.kt).
            // Same preset/baseColor/accent/baseRadius as the outer theme so shapes and
            // color axes stay consistent; only isDark differs.
            CatalogContentTheme(stylePreset, baseColor, accent, isDarkMode) {
                Row(modifier = Modifier.weight(1f)) {
                    if (!isCompact && !isOnCreateRoute) {
                        CatalogSidebar(
                            currentRoute = current,
                            onEntryClick = ::navigateTo,
                            query = searchQuery,
                            onQueryChange = { searchQuery = it },
                        )
                        Box(
                            modifier =
                                Modifier
                                    .width(1.dp)
                                    .fillMaxHeight()
                                    .background(currentTheme.colors.border),
                        )
                    }
                    Box(
                        modifier =
                            Modifier
                                .weight(1f)
                                .background(currentTheme.colors.background),
                    ) {
                        NavDisplay(
                            backStack = backStack,
                            modifier = Modifier.fillMaxSize(),
                            onBack = { appRouter.back() },
                            entryProvider =
                                entryProvider {
                                    entry<RouteKey.Introduction> {
                                        ComponentDetailScreen(componentId = "introduction")
                                    }
                                    entry<RouteKey.ComponentDetail> { route ->
                                        ComponentDetailScreen(componentId = route.componentId)
                                    }
                                    entry<RouteKey.Create> {
                                        CreatePage(
                                            stylePreset = stylePreset,
                                            onStylePresetChange = onStylePresetChange,
                                            baseColor = baseColor,
                                            onBaseColorChange = onBaseColorChange,
                                            accent = accent,
                                            onAccentChange = onAccentChange,
                                            getCodeRequest = createGetCodeRequest,
                                        )
                                    }
                                },
                        )
                    }
                }
            }
        }

        if (isCompact && drawerOpen && !isOnCreateRoute) {
            Box(
                modifier =
                    Modifier
                        .fillMaxSize()
                        .background(Color.Black.copy(alpha = 0.4f))
                        .clickable {
                            drawerOpen = false
                            searchQuery = ""
                        },
            )
            CatalogContentTheme(stylePreset, baseColor, accent, isDarkMode) {
                CatalogSidebar(
                    currentRoute = current,
                    onEntryClick = ::navigateTo,
                    query = searchQuery,
                    onQueryChange = { searchQuery = it },
                    modifier = Modifier.fillMaxHeight(),
                )
            }
        }
    }
}

/**
 * The dark-mode-toggle-aware theme scope shared by the sidebar (both the persistent
 * desktop instance and the mobile drawer instance) and the content pane -- factored out
 * so both [CatalogNavDisplay] call sites stay in sync rather than risking the two nested
 * `Theme` calls drifting out of sync with each other over time.
 */
@Composable
private fun CatalogContentTheme(
    stylePreset: StylePreset,
    baseColor: BaseColor,
    accent: Accent,
    isDarkMode: Boolean,
    content: @Composable () -> Unit,
) {
    Theme(
        preset = stylePreset,
        baseColor = baseColor,
        accent = accent,
        baseRadius = CATALOG_BASE_RADIUS,
        isDark = isDarkMode,
        content = content,
    )
}
