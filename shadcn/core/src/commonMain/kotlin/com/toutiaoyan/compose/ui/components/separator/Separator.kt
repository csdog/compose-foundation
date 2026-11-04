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
import androidx.compose.ui.semantics.stateDescription

/**
 * A thin dividing line. Matches real shadcn/ui's `separator.tsx`: `h-px w-full` when
 * horizontal, `w-px self-stretch` when vertical, painted with the `border` token.
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
 * Base UI separators are semantic by default. [decorative] is a Compose/Radix extension:
 * pass true to omit the default accessibility description. Native semantics exposes
 * a description and orientation; Compose does not yet provide an ARIA separator role.
 * Vertical separators require a bounded parent height; use IntrinsicSize.Min on an auto-height Row.
 */
@Composable
fun Separator(
    modifier: Modifier = Modifier,
    orientation: SeparatorOrientation = SeparatorOrientation.Horizontal,
    decorative: Boolean = false,
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
                        Modifier.semantics {
                            contentDescription = "Separator"
                            stateDescription =
                                if (orientation == SeparatorOrientation.Horizontal) {
                                    "Horizontal"
                                } else {
                                    "Vertical"
                                }
                        }
                    },
                )
                .styleable(
                    styleState,
                    orientation.rememberStyle() then style,
                ),
    )
}
