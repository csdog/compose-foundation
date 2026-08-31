@file:OptIn(kotlin.js.ExperimentalWasmJsInterop::class)

package com.toutiaoyan.compose.ui

import androidx.compose.runtime.snapshotFlow
import androidx.compose.ui.ExperimentalComposeUiApi
import androidx.compose.ui.window.ComposeViewport
import com.toutiaoyan.compose.ui.navigation.AppRouter
import com.toutiaoyan.compose.ui.navigation.WebLinks
import kotlinx.browser.window
import kotlinx.coroutines.coroutineScope

@OptIn(ExperimentalComposeUiApi::class)
fun main() {
    ComposeViewport {
        App(
            onNavHostReady = { appRouter ->
                navigateFromBrowser(appRouter)
                bindAppRouterToBrowser(appRouter)
            },
        )
    }
}

private suspend fun bindAppRouterToBrowser(appRouter: AppRouter) {
    coroutineScope {
        window.onpopstate = { navigateFromBrowser(appRouter) }
        try {
            snapshotFlow { appRouter.current }.collect { route ->
                val path = route.path
                if (window.location.pathname != path) {
                    window.history.pushState(null, "", path)
                }
            }
        } finally {
            window.onpopstate = null
        }
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
