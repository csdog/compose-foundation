package com.toutiaoyan.compose.ui.components.avatar

import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.remember
import androidx.compose.runtime.staticCompositionLocalOf
import coil3.ImageLoader
import coil3.PlatformContext
import coil3.compose.LocalPlatformContext
import coil3.network.ktor3.KtorNetworkFetcherFactory
import coil3.svg.SvgDecoder

/**
 * Optional shared [ImageLoader] for [AvatarImage].
 *
 * Prefer providing one near your app root via [ProvideAvatarImageLoader] so every avatar
 * reuses the same loader (disk/memory cache + SVG + Ktor network).
 */
val LocalAvatarImageLoader = staticCompositionLocalOf<ImageLoader?> { null }

/**
 * Builds an [ImageLoader] with multiplatform HTTP ([KtorNetworkFetcherFactory]) and SVG
 * ([SvgDecoder]) support. Apps may also pass this to Coil's `setSingletonImageLoaderFactory`.
 */
fun createAvatarImageLoader(context: PlatformContext): ImageLoader =
    ImageLoader
        .Builder(context)
        .components {
            add(KtorNetworkFetcherFactory())
            add(SvgDecoder.Factory())
        }.build()

/** Remembered [createAvatarImageLoader], or the value from [LocalAvatarImageLoader] if set. */
@Composable
fun rememberAvatarImageLoader(): ImageLoader {
    val local = LocalAvatarImageLoader.current
    if (local != null) return local
    val context = LocalPlatformContext.current
    return remember(context) { createAvatarImageLoader(context) }
}

/** Provides a process-shared [ImageLoader] to [AvatarImage] descendants. */
@Composable
fun ProvideAvatarImageLoader(content: @Composable () -> Unit) {
    val context = LocalPlatformContext.current
    val loader = remember(context) { createAvatarImageLoader(context) }
    CompositionLocalProvider(LocalAvatarImageLoader provides loader, content = content)
}
