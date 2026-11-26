package com.toutiaoyan.compose.ui.navigation

import androidx.navigation3.runtime.deeplink.DeepLinkMatcher
import androidx.navigation3.runtime.deeplink.DeepLinkRequest
import androidx.navigation3.runtime.deeplink.DeepLinkUri
import androidx.navigation3.runtime.deeplink.UriDeepLinkMatcher
import kotlinx.serialization.InternalSerializationApi
import kotlinx.serialization.KSerializer
import kotlinx.serialization.serializer

/**
 * Native custom-scheme deep links and [UriDeepLinkMatcher] registry.
 *
 * Browser SPA (no `/app` prefix) is [WebLinks]. Outbound paths use [RouteKey.path].
 *
 * Catalog has no production domain, so there is no Universal Link / App Link host —
 * only `shadcncompose://app/…`. Keep [SCHEME] / [NATIVE_HOST] in sync with
 * `shadcn/androidApp/build.gradle.kts`, `shadcn/androidApp/src/main/AndroidManifest.xml`,
 * `app/iosApp/iosApp/Info.plist`, and `app/desktopApp/build.gradle.kts`.
 */
object DeepLinks {
    const val SCHEME: String = "shadcncompose"
    const val NATIVE_HOST: String = "app"
    const val SCHEME_AND_HOST: String = "$SCHEME://$NATIVE_HOST"

    @OptIn(InternalSerializationApi::class)
    private val matchers: List<DeepLinkMatcher<RouteKey>> by lazy {
        buildList {
            RouteKey.deepLinkSamples.forEach { route ->
                add(
                    uriMatcher(route::class.serializer(), route.pathPattern),
                )
            }
        }
    }

    /**
     * Parse a URI; `null` when nothing matches.
     *
     * Supports `shadcncompose://app` and `shadcncompose://app/…`.
     */
    fun resolve(uri: String): RouteKey? {
        val request = DeepLinkRequest(uri.trim())
        return matchers.firstNotNullOfOrNull { it.match(request) }?.key
    }

    /** Outbound custom-scheme URI, e.g. `shadcncompose://app/create`. */
    fun nativeUri(route: RouteKey): String = "$SCHEME_AND_HOST${nativePath(route)}"

    /**
     * Docs-site: inbound links replace the whole stack (same as sidebar / browser).
     */
    fun open(
        router: AppRouter,
        route: RouteKey,
    ) {
        router.replaceAll(route)
    }

    private fun nativePath(route: RouteKey): String = route.path.trimEnd('/')

    private fun uriMatcher(
        serializer: KSerializer<out RouteKey>,
        pathPattern: String,
    ): UriDeepLinkMatcher<RouteKey> {
        @Suppress("UNCHECKED_CAST")
        val keySerializer = serializer as KSerializer<RouteKey>
        return UriDeepLinkMatcher(DeepLinkUri("$SCHEME_AND_HOST$pathPattern"), keySerializer)
    }
}
