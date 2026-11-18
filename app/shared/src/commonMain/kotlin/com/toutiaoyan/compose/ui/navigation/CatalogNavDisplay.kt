@file:OptIn(androidx.compose.foundation.style.ExperimentalFoundationStyleApi::class)

package com.toutiaoyan.compose.ui.navigation

import androidx.compose.foundation.background
import androidx.compose.foundation.ScrollState
import androidx.compose.foundation.focusable
import androidx.compose.foundation.style.Style
import androidx.compose.foundation.style.contentPadding
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.platform.LocalDensity
import com.toutiaoyan.compose.ui.catalog.LocalDocumentScrollHost
import com.toutiaoyan.compose.ui.catalog.LocalDocNavigation
import com.toutiaoyan.compose.ui.catalog.LocalCatalogViewportWidth
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.withFrameNanos
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.focus.focusRequester
import androidx.compose.ui.input.key.Key
import androidx.compose.ui.input.key.KeyEventType
import androidx.compose.ui.input.key.isCtrlPressed
import androidx.compose.ui.input.key.isMetaPressed
import androidx.compose.ui.input.key.key
import androidx.compose.ui.input.key.type
import androidx.compose.ui.input.key.onPreviewKeyEvent
import com.toutiaoyan.compose.ui.catalog.CatalogSearchDialog
import com.toutiaoyan.compose.ui.components.sheet.Sheet
import com.toutiaoyan.compose.ui.components.sheet.SheetSide
import com.toutiaoyan.compose.ui.components.sheet.SheetContent
import kotlinx.coroutines.launch
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.emptyFlow
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.widthIn
import androidx.compose.ui.Alignment
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
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
 * Below this width, there isn't enough room for a persistent 288dp sidebar plus a
 * usable content pane -- single-word labels (chip/button text) start wrapping
 * letter-by-letter instead of the layout adapting. Matches a typical phone-portrait
 * width plus a little headroom.
 */
private val COMPACT_BREAKPOINT = 1024.dp

// Matches App.kt's own outer Theme's baseRadius -- the nested content-pane
// Theme below needs the same value so the content pane's shapes stay visually
// consistent with the chrome around it.
private val CATALOG_BASE_RADIUS = Radius(10.dp)

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
    searchRequests: Flow<Unit> = emptyFlow(),
) {
    val current = appRouter.current
    val isOnCreateRoute = current is RouteKey.Create

    val documentScrollStates = remember { mutableMapOf<String, ScrollState>() }
    val activeDocumentScroll =
        when (current) {
            is RouteKey.ComponentDetail -> documentScrollStates.getOrPut(current.componentId) { ScrollState(0) }
            RouteKey.Introduction -> documentScrollStates.getOrPut("introduction") { ScrollState(0) }
            RouteKey.Create -> null
        }
    val documentHost = LocalDocumentScrollHost.current
    val pixelsPerCssPixel = LocalDensity.current.density
    val documentLocation = LocalDocNavigation.current?.location
    val activeDocumentId =
        when (current) {
            is RouteKey.ComponentDetail -> current.componentId
            RouteKey.Introduction -> "introduction"
            RouteKey.Create -> null
        }
    LaunchedEffect(documentHost, activeDocumentScroll, pixelsPerCssPixel, documentLocation) {
        // The router can recompose before the browser commits its new history entry.
        if (documentLocation == null || documentLocation.componentId == activeDocumentId) {
            documentHost?.bind(activeDocumentScroll, pixelsPerCssPixel)
        }
    }

    var drawerOpen by remember { mutableStateOf(false) }
    var searchOpen by remember { mutableStateOf(false) }
    val shellFocus = remember { FocusRequester() }
    val searchFocus = remember { FocusRequester() }
    val scope = rememberCoroutineScope()
    LaunchedEffect(shellFocus) { shellFocus.requestFocus() }

    fun closeSearch() {
        searchOpen = false
        scope.launch {
            withFrameNanos { }
            searchFocus.requestFocus()
        }
    }

    LaunchedEffect(searchRequests) {
        searchRequests.collect { if (searchOpen) closeSearch() else searchOpen = true }
    }

    var createGetCodeRequest by remember { mutableIntStateOf(0) }

    fun navigateTo(route: RouteKey) {
        // Docs-site replace: always land on a single content route (clears Create).
        appRouter.replaceAll(route)
        drawerOpen = false
        searchOpen = false
    }

    fun navigateToCreate() {
        appRouter.push(RouteKey.Create)
    }

    BoxWithConstraints(
        modifier =
            Modifier.fillMaxSize().focusRequester(shellFocus)
                .onPreviewKeyEvent { event ->
                    if (event.type == KeyEventType.KeyDown &&
                        event.key == Key.K &&
                        (
                            event.isCtrlPressed ||
                                event.isMetaPressed
                        )
                    ) {
                        if (searchOpen) closeSearch() else searchOpen = true
                        true
                    } else {
                        false
                    }
                }.focusable(),
    ) {
        val isCompact = (LocalCatalogViewportWidth.current ?: maxWidth) < COMPACT_BREAKPOINT

        Column(modifier = Modifier.fillMaxSize()) {
            CatalogTopBar(
                isDarkMode = isDarkMode,
                onToggleDarkMode = onToggleDarkMode,
                isOnCreateRoute = isOnCreateRoute,
                onNavigateHome = { appRouter.replaceAll(RouteKey.Introduction) },
                onNavigateToComponents = {
                    navigateTo(RouteKey.ComponentDetail("button"))
                },
                onNavigateToCreate = ::navigateToCreate,
                onGetCode = { createGetCodeRequest++ },
                isCompact = isCompact,
                onOpenNavigation = { drawerOpen = true },
                onSearch = { searchOpen = true },
                searchButtonModifier = Modifier.focusRequester(searchFocus),
            )

            // Preview tokens apply below the top bar; light/dark and the bundled
            // documentation font are shared with the outer chrome theme.
            CatalogContentTheme(stylePreset, baseColor, accent, isDarkMode) {
                Box(modifier = Modifier.weight(1f).fillMaxSize(), contentAlignment = Alignment.TopCenter) {
                    Row(
                        modifier =
                            Modifier
                                .then(if (isOnCreateRoute) Modifier.widthIn(max = 1536.dp) else Modifier)
                                .fillMaxSize()
                                .padding(horizontal = if (isOnCreateRoute) 0.dp else 8.dp),
                    ) {
                        if (!isCompact && !isOnCreateRoute) {
                            CatalogSidebar(
                                currentRoute = current,
                                onEntryClick = ::navigateTo,
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
                                            ComponentDetailScreen(
                                                componentId = "introduction",
                                                scrollState =
                                                    documentScrollStates.getOrPut(
                                                        "introduction",
                                                    ) { ScrollState(0) },
                                            )
                                        }
                                        entry<RouteKey.ComponentDetail> { route ->
                                            ComponentDetailScreen(
                                                componentId = route.componentId,
                                                onNavigateComponent = { navigateTo(RouteKey.ComponentDetail(it)) },
                                                scrollState =
                                                    documentScrollStates.getOrPut(
                                                        route.componentId,
                                                    ) { ScrollState(0) },
                                            )
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
        }

        if (isCompact && !isOnCreateRoute) {
            CatalogContentTheme(stylePreset, baseColor, accent, isDarkMode) {
                Sheet(open = drawerOpen, onOpenChange = { drawerOpen = it }) {
                    SheetContent(
                        side = SheetSide.Start,
                        style = Style { contentPadding(0.dp) },
                        closeAriaLabel = "Close navigation",
                    ) {
                        CatalogSidebar(
                            currentRoute = current,
                            onEntryClick = ::navigateTo,
                            modifier = Modifier.fillMaxWidth(),
                        )
                    }
                }
            }
        }
        if (searchOpen) {
            CatalogSearchDialog(onDismiss = ::closeSearch, onNavigate = ::navigateTo)
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
        fontFamily = currentTheme.fontFamily,
        content = content,
    )
}
