package com.toutiaoyan.compose.ui.navigation

import androidx.navigation3.runtime.deeplink.DeepLinkMatcher
import androidx.navigation3.runtime.deeplink.DeepLinkRequest
import androidx.navigation3.runtime.deeplink.DeepLinkUri
import androidx.navigation3.runtime.deeplink.UriDeepLinkMatcher
import com.toutiaoyan.compose.ui.appConfig
import kotlinx.serialization.InternalSerializationApi
import kotlinx.serialization.KSerializer
import kotlinx.serialization.serializer

/**
 * Browser SPA link resolver — pathname at site root.
 *
 * Any `http(s)` host (incl. localhost) and relative paths are [canonicalize]d onto
 * [HTTPS_BASE] before matching. Outbound paths use [RouteKey.path].
 *
 * Same role as angryecho-kmp [WebLinks]. Custom scheme falls through to [DeepLinks].
 */
object WebLinks {
    /** Matching authority only; [canonicalize] rewrites every browser origin onto this. */
    val HTTPS_BASE: String = appConfig.BACKEND_BASE_URL.trimEnd('/')

    @OptIn(InternalSerializationApi::class)
    private val matchers: List<DeepLinkMatcher<RouteKey>> by lazy {
        buildList {
            RouteKey.deepLinkSamples.forEach { route ->
                addAll(uriMatchers(route::class.serializer(), route.pathPattern))
            }
        }
    }

    /** Resolve a browser / relative URI to an [RouteKey], or `null` if unmatched. */
    fun resolve(uri: String): RouteKey? {
        val canonical = canonicalize(uri.trim())
        val parsed = DeepLinkUri(canonical)
        val segments = parsed.getPathSegments().filter { it.isNotEmpty() }
        if (segments.isEmpty()) {
            return RouteKey.Introduction
        }
        val request = DeepLinkRequest(canonical)
        return matchers.firstNotNullOfOrNull { it.match(request) }?.key
            ?: DeepLinks.resolve(canonical)
    }

    /**
     * Normalize browser / relative URIs to an absolute URI under [HTTPS_BASE] so
     * [UriDeepLinkMatcher] can match on authority.
     */
    fun canonicalize(uri: String): String {
        if (uri.isEmpty()) return HTTPS_BASE

        val parsed = DeepLinkUri(uri)
        val scheme = parsed.getScheme()?.lowercase()
        if (scheme != null && scheme != "http" && scheme != "https") {
            return uri
        }

        val suffix =
            buildString {
                parsed.getQuery()?.let {
                    append('?')
                    append(it)
                }
                parsed.getFragment()?.let {
                    append('#')
                    append(it)
                }
            }

        if (scheme == null) {
            val pathPart = uri.substringBefore('?').substringBefore('#')
            val segments =
                pathPart
                    .trim('/')
                    .split('/')
                    .filter { it.isNotEmpty() }
            val clientPath = if (segments.isEmpty()) "" else "/" + segments.joinToString("/")
            return "$HTTPS_BASE$clientPath$suffix"
        }

        val segments = parsed.getPathSegments().filter { it.isNotEmpty() }
        val clientPath = if (segments.isEmpty()) "" else "/" + segments.joinToString("/")
        return "$HTTPS_BASE$clientPath$suffix"
    }

    private fun uriMatchers(
        serializer: KSerializer<out RouteKey>,
        pathPattern: String,
    ): List<UriDeepLinkMatcher<RouteKey>> {
        @Suppress("UNCHECKED_CAST")
        val keySerializer = serializer as KSerializer<RouteKey>
        return listOf(
            UriDeepLinkMatcher(DeepLinkUri("$HTTPS_BASE$pathPattern"), keySerializer),
        )
    }
}
