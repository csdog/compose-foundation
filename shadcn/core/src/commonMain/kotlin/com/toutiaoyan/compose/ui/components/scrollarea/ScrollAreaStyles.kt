@file:OptIn(ExperimentalFoundationStyleApi::class)

package com.toutiaoyan.compose.ui.components.scrollarea

import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.style.ExperimentalFoundationStyleApi
import androidx.compose.foundation.style.Style
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.platform.LocalLayoutDirection
import androidx.compose.ui.unit.LayoutDirection
import com.toutiaoyan.compose.ui.styles.focusRingShadow
import com.toutiaoyan.compose.ui.utils.rememberTheme
import kotlinx.coroutines.delay

/**
 * Scrollbar visibility policy, matching Radix `ScrollArea` `type` (+ embedded
 * `scrollHideDelay` on the variants that use a hide timer).
 *
 * - [Auto] — visible while content overflows
 * - [Always] — always visible (full-length thumb when content does not overflow)
 * - [Scroll] — visible while scrolling (and for [Scroll.hideDelayMs] after)
 * - [Hover] — visible while scrolling **or** hovering the scroll area (and for
 *   [Hover.hideDelayMs] after)
 */
sealed interface ScrollType {
    data object Auto : ScrollType

    data object Always : ScrollType

    data class Scroll(
        val hideDelayMs: Int = DEFAULT_HIDE_DELAY_MS,
    ) : ScrollType

    data class Hover(
        val hideDelayMs: Int = DEFAULT_HIDE_DELAY_MS,
    ) : ScrollType

    companion object {
        /** Radix `scrollHideDelay` default. */
        const val DEFAULT_HIDE_DELAY_MS = 600
    }
}

/** Applies Radix `scrollHideDelay` onto [Scroll]/[Hover]; other types ignore it. */
internal fun ScrollType.withHideDelay(scrollHideDelay: Int?): ScrollType {
    if (scrollHideDelay == null) return this
    return when (this) {
        is ScrollType.Scroll -> copy(hideDelayMs = scrollHideDelay)
        is ScrollType.Hover -> copy(hideDelayMs = scrollHideDelay)
        else -> this
    }
}

/**
 * Reading direction override for [ScrollArea] / [LazyScrollArea], matching Radix `dir`.
 * Pass `null` (default) to inherit [LocalLayoutDirection].
 */
sealed interface ScrollDir {
    data object Ltr : ScrollDir

    data object Rtl : ScrollDir
}

internal fun ScrollDir.toLayoutDirection(): LayoutDirection =
    when (this) {
        ScrollDir.Ltr -> LayoutDirection.Ltr
        ScrollDir.Rtl -> LayoutDirection.Rtl
    }

/**
 * Provides [dir] as [LocalLayoutDirection] when set; otherwise leaves the ambient
 * direction unchanged (Radix "omit → inherit" behavior).
 */
@Composable
internal fun ProvideScrollDir(
    dir: ScrollDir?,
    content: @Composable () -> Unit,
) {
    if (dir == null) {
        content()
    } else {
        CompositionLocalProvider(
            LocalLayoutDirection provides dir.toLayoutDirection(),
            content = content,
        )
    }
}

/** Whether a thumb should be composed at all for [type] given overflow state. */
internal fun scrollbarMayShow(
    type: ScrollType,
    overflowing: Boolean,
): Boolean =
    when (type) {
        ScrollType.Always -> true
        else -> overflowing
    }

/**
 * Resolves whether the scrollbar is currently shown for [Scroll]/[ScrollType.Hover]
 * (with hide delay) or the static [ScrollType.Auto]/[ScrollType.Always] policies.
 */
@Composable
internal fun rememberScrollbarVisible(
    type: ScrollType,
    overflowing: Boolean,
    scrolling: Boolean,
    hovering: Boolean,
): Boolean {
    when (type) {
        ScrollType.Auto -> return overflowing
        ScrollType.Always -> return true
        is ScrollType.Scroll,
        is ScrollType.Hover,
        -> {
            val hideDelayMs =
                when (type) {
                    is ScrollType.Scroll -> type.hideDelayMs
                    is ScrollType.Hover -> type.hideDelayMs
                    else -> ScrollType.DEFAULT_HIDE_DELAY_MS
                }
            val active =
                when (type) {
                    is ScrollType.Scroll -> scrolling
                    is ScrollType.Hover -> scrolling || hovering
                    else -> false
                }
            var visible by remember(type) { mutableStateOf(false) }
            LaunchedEffect(active, overflowing, hideDelayMs) {
                if (!overflowing) {
                    visible = false
                    return@LaunchedEffect
                }
                if (active) {
                    visible = true
                } else {
                    delay(hideDelayMs.toLong())
                    visible = false
                }
            }
            return visible && overflowing
        }
    }
}

@Composable
fun rememberScrollThumbStyle(): Style =
    rememberTheme {
        Style {
            shape(RoundedCornerShape(radius.full))
            background(colors.border)
        }
    }

/** Viewport focus ring matching shadcn `focus-visible:ring-[3px] focus-visible:ring-ring/50`. */
@Composable
fun rememberScrollAreaViewportStyle(focused: Boolean): Style =
    rememberTheme(focused) {
        Style {
            if (focused) dropShadow(focusRingShadow())
        }
    }
