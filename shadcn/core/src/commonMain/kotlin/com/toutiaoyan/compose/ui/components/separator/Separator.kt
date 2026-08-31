@file:OptIn(ExperimentalFoundationStyleApi::class)

package com.toutiaoyan.compose.ui.components.separator
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.style.ExperimentalFoundationStyleApi
import androidx.compose.foundation.style.MutableStyleState
import androidx.compose.foundation.style.Style
import androidx.compose.foundation.style.styleable
import androidx.compose.foundation.style.then
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.semantics.semantics

/**
 * A thin dividing line. Matches real shadcn/ui's `separator.tsx`: `h-px w-full` when
 * horizontal, `h-full w-px` when vertical, painted with the `border` token.
 *
 * Size / color live in Style — override with [style].
 *
 * Usage:
 * ```
 * Separator()
 * Separator(orientation = SeparatorOrientation.Vertical)
 * Separator(decorative = false)
 * ```
 *
 * [decorative] matches Radix `decorative` (default `true`): the line is visual-only.
 * Pass `false` to expose a "Separator" accessibility description.
 */
@Composable
fun Separator(
    modifier: Modifier = Modifier,
    orientation: SeparatorOrientation = SeparatorOrientation.Horizontal,
    decorative: Boolean = true,
    style: Style = Style,
) {
    val styleState = remember { MutableStyleState(MutableInteractionSource()) }
    Box(
        modifier =
            modifier
                .then(
                    if (decorative) {
                        Modifier
                    } else {
                        Modifier.semantics { contentDescription = "Separator" }
                    },
                )
                .styleable(
                    styleState,
                    orientation.rememberStyle() then style,
                ),
    )
}
