package com.toutiaoyan.compose.ui.navigation

import androidx.navigation3.runtime.NavKey
import kotlinx.serialization.Serializable
import kotlinx.serialization.Transient

/**
 * App destinations (Navigation 3 [NavKey]).
 *
 * Web SPA paths (see [WebLinks]):
 * - `/` → [Introduction]
 * - `/{componentId}` → [ComponentDetail]
 * - `/create` → [Create]
 *
 * Native custom scheme (see [DeepLinks]): `shadcncompose://app/…` with the same paths
 * after `/app`.
 */
@Serializable
sealed class RouteKey(
    @Transient val pathPattern: String = "",
) : NavKey {
    /** Outbound browser path; default equals [pathPattern]. */
    open val path: String get() = pathPattern

    /** Getting-started landing page (catalog entry id `introduction`). */
    @Serializable
    data object Introduction : RouteKey("") {
        override val path: String get() = "/"
    }

    /** Component / docs detail page keyed by catalog entry id. */
    @Serializable
    data class ComponentDetail(
        val componentId: String,
    ) : RouteKey("/{componentId}") {
        override val path: String get() = "/$componentId"
    }

    /** Theme configurator — see [com.toutiaoyan.compose.ui.catalog.CreatePage]. */
    @Serializable
    data object Create : RouteKey("/create")

    companion object {
        /**
         * Deep-link registration samples: parameterless singletons; parameterized with a
         * dummy instance (pattern / serializer only). [Introduction] / [Create] must stay
         * before [ComponentDetail] so `/` and `/create` are not swallowed by `/{componentId}`.
         */
        val deepLinkSamples: List<RouteKey> by lazy {
            listOf(Introduction, Create, ComponentDetail(""))
        }

        /** Map a catalog entry id (sidebar / docs) to its [RouteKey]. */
        fun forComponentId(componentId: String): RouteKey =
            if (componentId == "introduction") Introduction else ComponentDetail(componentId)
    }
}

/** Sidebar highlight id for the current route, if any. */
val RouteKey.selectedComponentId: String?
    get() =
        when (this) {
            RouteKey.Introduction -> "introduction"
            is RouteKey.ComponentDetail -> componentId
            RouteKey.Create -> null
        }
