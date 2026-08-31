package com.toutiaoyan.compose.ui.navigation

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.compose.runtime.snapshots.Snapshot
import androidx.navigation3.runtime.NavBackStack

/**
 * App navigation commands over a Navigation 3 [NavBackStack].
 *
 * [com.toutiaoyan.compose.ui.App] attaches the stack; web binds browser history via
 * [current] / [replaceAll]. Same role as angryecho-kmp [AppRouter] (no Koin here).
 */
interface AppRouter {
    /**
     * Top-of-stack route. Reads subscribe to Compose snapshots (including [attach]
     * and stack mutations) so [androidx.compose.runtime.snapshotFlow] updates.
     */
    val current: RouteKey

    fun push(route: RouteKey)

    fun back(): Boolean

    /** Replace the top entry; no-op when [route] is already current. */
    fun replace(route: RouteKey)

    /** Replace the entire back stack with [routes] (empty → [default]). */
    fun replaceAll(vararg routes: RouteKey): AppRouter
}

class AppRouterImpl(
    val default: RouteKey = RouteKey.Introduction,
) : AppRouter {
    private var backStack by mutableStateOf<NavBackStack<RouteKey>?>(null)

    override val current: RouteKey
        get() = backStack?.lastOrNull() ?: default

    override fun push(route: RouteKey) {
        if (current == route) return
        backStack?.add(route)
    }

    override fun back(): Boolean {
        val stack = backStack ?: return false
        if (stack.size <= 1) return false
        stack.removeLast()
        return true
    }

    override fun replace(route: RouteKey) {
        if (current == route) return
        val stack = backStack ?: return
        Snapshot.withMutableSnapshot {
            if (stack.isNotEmpty()) stack.removeLast()
            stack.add(route)
        }
    }

    override fun replaceAll(vararg routes: RouteKey): AppRouter {
        val stack = backStack ?: return this
        val next = if (routes.isEmpty()) listOf(default) else routes.asList()
        Snapshot.withMutableSnapshot {
            stack.clear()
            stack.addAll(next)
        }
        return this
    }

    fun attach(stack: NavBackStack<RouteKey>) {
        backStack = stack
    }

    fun detach(stack: NavBackStack<RouteKey>) {
        if (backStack !== stack) return
        backStack = null
    }
}
