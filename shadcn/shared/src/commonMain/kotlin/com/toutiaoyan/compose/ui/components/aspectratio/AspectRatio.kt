@file:OptIn(ExperimentalFoundationStyleApi::class)

package com.toutiaoyan.compose.ui.components.aspectratio
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.style.ExperimentalFoundationStyleApi
import androidx.compose.foundation.style.MutableStyleState
import androidx.compose.foundation.style.Style
import androidx.compose.foundation.style.styleable
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier

/**
 * Constrains its content to a fixed width/height ratio. Matches real shadcn/ui's
 * current Base wrapper, which uses CSS aspect-ratio without imposing a surface.
 * Caller modifiers and Style establish the available size before the ratio is
 * measured. Clipping, images, and accessibility descriptions belong to content.
 * Like the web wrapper, [ratio] is required and must be finite and greater than zero.
 *
 * Usage:
 * ```
 * AspectRatio(ratio = 16f / 9f) {
 *     Image(painter, contentDescription = null, modifier = Modifier.fillMaxSize())
 * }
 * ```
 */
@Composable
fun AspectRatio(
    ratio: Float,
    modifier: Modifier = Modifier,
    style: Style = Style,
    content: @Composable () -> Unit,
) {
    validateAspectRatio(ratio)
    val styleState = remember { MutableStyleState(MutableInteractionSource()) }
    Box(modifier = modifier.styleable(styleState, style).aspectRatio(ratio)) {
        content()
    }
}

internal fun validateAspectRatio(ratio: Float): Float {
    require(ratio.isFinite() && ratio > 0f) {
        "ratio must be finite and greater than 0"
    }
    return ratio
}
