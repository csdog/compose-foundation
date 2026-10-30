@file:OptIn(kotlin.js.ExperimentalWasmJsInterop::class)

package com.toutiaoyan.compose.ui

import androidx.compose.runtime.snapshotFlow
import androidx.compose.runtime.remember
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.getValue
import androidx.compose.runtime.setValue
import androidx.compose.runtime.mutableStateOf
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.callbackFlow
import org.w3c.dom.events.Event
import org.w3c.dom.events.KeyboardEvent
import androidx.compose.ui.ExperimentalComposeUiApi
import androidx.compose.ui.window.ComposeViewport
import com.toutiaoyan.compose.ui.navigation.AppRouter
import com.toutiaoyan.compose.ui.navigation.WebLinks
import com.toutiaoyan.compose.ui.navigation.RouteKey
import com.toutiaoyan.compose.ui.catalog.DocLocation
import com.toutiaoyan.compose.ui.catalog.DocNavigation
import com.toutiaoyan.compose.ui.catalog.LocalDocNavigation
import kotlinx.browser.window
import kotlinx.coroutines.coroutineScope

@OptIn(ExperimentalComposeUiApi::class)
fun main() {
    ComposeViewport {
        val searchRequests = remember { browserSearchRequests() }
        val docNavigation = remember { BrowserDocNavigation() }
        CompositionLocalProvider(LocalDocNavigation provides docNavigation) {
            App(
                searchRequests = searchRequests,
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
        window.onpopstate = {
            navigateFromBrowser(appRouter)
            docs.sync()
        }
        val hashListener: (Event) -> Unit = { docs.sync() }
        window.addEventListener("hashchange", hashListener)
        try {
            snapshotFlow { appRouter.current }.collect { route ->
                val path = route.path
                if (window.location.pathname != path) {
                    window.history.pushState(null, "", path)
                }
                docs.sync()
            }
        } finally {
            window.onpopstate = null
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
    ) {
        val next = browserDocLocation()
        if (force || next.componentId != location.componentId || next.fragment != location.fragment) {
            location = next.copy(revision = location.revision + 1, animate = animate)
        }
    }

    override fun navigate(fragment: String) {
        val hash = "#$fragment"
        if (window.location.hash != hash) {
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
