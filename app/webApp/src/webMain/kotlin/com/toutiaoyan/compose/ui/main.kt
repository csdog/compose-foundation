@file:OptIn(kotlin.js.ExperimentalWasmJsInterop::class)

package com.toutiaoyan.compose.ui

import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.runtime.snapshotFlow
import androidx.compose.ui.ExperimentalComposeUiApi
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.ComposeViewport
import com.toutiaoyan.compose.ui.catalog.DocLocation
import com.toutiaoyan.compose.ui.catalog.DocNavigation
import com.toutiaoyan.compose.ui.catalog.LocalCatalogViewportWidth
import com.toutiaoyan.compose.ui.catalog.LocalDocExternalLink
import com.toutiaoyan.compose.ui.catalog.LocalDocNavigation
import com.toutiaoyan.compose.ui.catalog.LocalDocumentScrollHost
import com.toutiaoyan.compose.ui.catalog.documentTitle
import com.toutiaoyan.compose.ui.navigation.AppRouter
import com.toutiaoyan.compose.ui.navigation.RouteKey
import com.toutiaoyan.compose.ui.navigation.WebLinks
import com.toutiaoyan.compose.ui.verification.ToggleGroupConnectedFixture
import com.toutiaoyan.compose.ui.verification.ToggleGroupConnectedFixtureConfig
import kotlinx.browser.document
import kotlinx.browser.window
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.callbackFlow
import org.w3c.dom.HTMLElement
import org.w3c.dom.events.Event
import org.w3c.dom.events.KeyboardEvent

@OptIn(ExperimentalComposeUiApi::class)
fun main() {
    val initialDarkModeOverride = readThemePreference()
    applyBrowserColorScheme(initialDarkModeOverride)
    val connectedFixture = ToggleGroupConnectedFixtureConfig.fromPath(window.location.pathname)
    ComposeViewport("compose-root") {
        if (connectedFixture != null) {
            ToggleGroupConnectedFixture(
                config = connectedFixture,
                isDark = initialDarkModeOverride ?: window.matchMedia("(prefers-color-scheme: dark)").matches,
            )
            return@ComposeViewport
        }
        var viewportWidth by remember { mutableStateOf(window.innerWidth.dp) }
        DisposableEffect(Unit) {
            val listener: (Event) -> Unit = { viewportWidth = window.innerWidth.dp }
            window.addEventListener("resize", listener)
            // Compose dispatches wheel input to its own scrollers (including popup content).
            // Do not let an unconsumed wheel also scroll the browser document underneath it.
            val container = checkNotNull(document.getElementById("compose-root"))
            val wheelListener: (Event) -> Unit = { it.preventDefault() }
            container.addEventListener("wheel", wheelListener)
            onDispose {
                window.removeEventListener("resize", listener)
                container.removeEventListener("wheel", wheelListener)
            }
        }
        val documentScrollHost = remember { BrowserDocumentScrollHost() }
        val searchRequests = remember { browserSearchRequests() }
        val docNavigation = remember { BrowserDocNavigation() }
        CompositionLocalProvider(
            LocalDocNavigation provides docNavigation,
            LocalDocExternalLink provides { href -> window.open(href, "_blank", "noopener,noreferrer") },
            LocalCatalogViewportWidth provides viewportWidth,
            LocalDocumentScrollHost provides documentScrollHost,
        ) {
            App(
                searchRequests = searchRequests,
                initialDarkModeOverride = initialDarkModeOverride,
                onDarkModeOverrideChange = ::writeThemePreference,
                onNavHostReady = { appRouter ->
                    navigateFromBrowser(appRouter)
                    bindAppRouterToBrowser(appRouter, docNavigation)
                },
            )
        }
    }
}

private suspend fun bindAppRouterToBrowser(
    appRouter: AppRouter,
    docs: BrowserDocNavigation,
) {
    coroutineScope {
        var committedPath = window.location.pathname
        val popStateListener: (Event) -> Unit = {
            // Router observers can already have reacted; compare committed browser paths.
            val restoreScroll = window.location.pathname != committedPath
            committedPath = window.location.pathname
            navigateFromBrowser(appRouter)
            docs.sync(restoreScroll = restoreScroll, force = restoreScroll)
        }
        window.addEventListener("popstate", popStateListener)
        val hashListener: (Event) -> Unit = { docs.sync() }
        window.addEventListener("hashchange", hashListener)
        try {
            snapshotFlow { appRouter.current }.collect { route ->
                document.title = route.documentTitle
                val path = route.path
                if (window.location.pathname != path) {
                    window.history.pushState(null, "", path)
                    committedPath = path
                }
                docs.sync()
            }
        } finally {
            window.removeEventListener("popstate", popStateListener)
            window.removeEventListener("hashchange", hashListener)
        }
    }
}

private fun browserDocLocation(): DocLocation =
    DocLocation(
        componentId = (WebLinks.resolve(window.location.href) as? RouteKey.ComponentDetail)?.componentId,
        fragment = window.location.hash.removePrefix("#"),
    )

private class BrowserDocNavigation : DocNavigation {
    override var location by mutableStateOf(browserDocLocation())
        private set

    fun sync(
        animate: Boolean = false,
        force: Boolean = false,
        restoreScroll: Boolean = false,
    ) {
        val next = browserDocLocation()
        if (force || next.componentId != location.componentId || next.fragment != location.fragment) {
            location = next.copy(revision = location.revision + 1, animate = animate, restoreScroll = restoreScroll)
        }
    }

    override fun navigate(fragment: String) {
        val hash = "#$fragment"
        val currentHash = window.location.hash
        val atTarget =
            currentHash == hash ||
                (fragment.isEmpty() && currentHash.isEmpty() && window.location.href.endsWith('#'))
        if (!atTarget) {
            window.history.pushState(null, "", window.location.pathname + window.location.search + hash)
        }
        // A second click must scroll again even if the URL already has this hash.
        sync(animate = true, force = true)
    }
}

private fun navigateFromBrowser(appRouter: AppRouter) {
    WebLinks.resolve(window.location.href)?.let {
        if (appRouter.current != it) {
            // Docs-site: URL is the source of truth — replace the whole stack.
            appRouter.replaceAll(it)
        }
    }
}

/** Capture the browser shortcut before it reaches either the address bar or Compose's focused node. */
private fun browserSearchRequests(): Flow<Unit> =
    callbackFlow {
        val listener: (Event) -> Unit = { raw ->
            val event = raw as KeyboardEvent
            if ((event.ctrlKey || event.metaKey) && event.key.lowercase() == "k") {
                event.preventDefault()
                event.stopImmediatePropagation()
                trySend(Unit)
            }
        }
        window.addEventListener("keydown", listener, true)
        awaitClose { window.removeEventListener("keydown", listener, true) }
    }

private const val ThemePreferenceKey = "compose-foundation.theme"

private fun readThemePreference(): Boolean? =
    runCatching {
        when (window.localStorage.getItem(ThemePreferenceKey)) {
            "dark" -> true
            "light" -> false
            else -> null
        }
    }.getOrNull()

private fun writeThemePreference(isDark: Boolean) {
    applyBrowserColorScheme(isDark)
    // Private browsing policies can deny storage; the current in-memory choice still works.
    runCatching { window.localStorage.setItem(ThemePreferenceKey, if (isDark) "dark" else "light") }
}

private fun applyBrowserColorScheme(isDark: Boolean?) {
    (document.documentElement as? HTMLElement)?.style?.setProperty(
        "color-scheme",
        when (isDark) {
            true -> "dark"
            false -> "light"
            null -> "light dark"
        },
    )
}
