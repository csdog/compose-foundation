package com.toutiaoyan.compose.ui.catalog

import androidx.compose.foundation.ScrollState
import androidx.compose.foundation.rememberScrollState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.staticCompositionLocalOf
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.platform.LocalWindowInfo
import androidx.compose.ui.unit.Dp

/** The Web host mirrors a document scroll state into the browser's native scrollbar. */
interface DocumentScrollHost {
    /** The caller owns cancellation; null means a route with independent inner scrollers. */
    suspend fun bind(
        state: ScrollState?,
        pixelsPerCssPixel: Float,
    )
}

val LocalDocumentScrollHost = staticCompositionLocalOf<DocumentScrollHost?> { null }

/** CSS media queries use the full viewport width, including a classic scrollbar. */
val LocalCatalogViewportWidth = staticCompositionLocalOf<Dp?> { null }

internal val LocalGuideScrollState = staticCompositionLocalOf<ScrollState?> { null }

@Composable
internal fun rememberGuideScrollState(): ScrollState = LocalGuideScrollState.current ?: rememberScrollState()

@Composable
internal fun catalogViewportWidth(): Dp =
    LocalCatalogViewportWidth.current
        ?: with(LocalDensity.current) { LocalWindowInfo.current.containerSize.width.toDp() }
