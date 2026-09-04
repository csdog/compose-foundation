@file:OptIn(ExperimentalFoundationStyleApi::class)

package com.toutiaoyan.compose.ui.components.empty

import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.style.ExperimentalFoundationStyleApi
import androidx.compose.foundation.style.Style
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import com.toutiaoyan.compose.tailwind.core.twRadius
import com.toutiaoyan.compose.ui.styles.dashedBorder
import com.toutiaoyan.compose.ui.utils.rememberTheme

/**
 * base-nova Empty (`style-nova.css` `.cn-empty`): `gap-4 rounded-xl border-dashed p-6`.
 */
object EmptyDefaults {
    /** Official root `gap-4`. */
    val Spacing = 16.dp

    /** Official `p-6`. Root Style already uses this when [Empty.padding] is null. */
    val Padding = 24.dp

    /** Docs / responsive `md:p-12` (not in nova root CSS; opt-in). */
    val MdPadding = 48.dp

    /** Official root `rounded-xl`. Theme-aware. */
    val Radius: Dp
        @Composable get() = twRadius.xl
}

object EmptyMediaDefaults {
    /** Official icon variant `size-8` (nova). */
    val IconSize = 32.dp

    /** Alias of [IconSize]. */
    val OfficialIconSize = IconSize

    /** Official EmptyMedia `variant="default"` (unstyled). */
    val Variant = EmptyMediaVariant.Default
}

object EmptyContentDefaults {
    /** Official EmptyContent `gap-2.5` (nova, 10.dp). */
    val Spacing = 10.dp
}

/**
 * Matches base-nova Empty (`gap-4 rounded-xl p-6`). Dashed border is applied via
 * [emptyDashedBorder] on [Empty] default variant (Compose Style has no dash stroke).
 */
@Composable
fun rememberEmptyStyle(): Style =
    rememberTheme {
        Style {
            width(1f)
            // Official `p-6` = 24.dp
            contentPadding(spacing.xl2)
            shape(RoundedCornerShape(radius.xl))
        }
    }

/** Docs Outline: solid `border` 1dp (overrides root dashed). */
@Composable
fun rememberEmptyOutlineStyle(): Style =
    rememberTheme {
        Style {
            borderWidth(1.dp)
            borderColor(colors.border)
        }
    }

/** Overlay root corners when callers pass [Empty.radius]. */
@Composable
fun rememberEmptyRadiusStyle(radius: Dp): Style =
    rememberTheme(radius) {
        Style { shape(RoundedCornerShape(radius)) }
    }

/** Overlay root inset when callers pass official `md:p-12` (or any [Empty.padding]). */
@Composable
fun rememberEmptyPaddingStyle(padding: Dp): Style =
    rememberTheme(padding) {
        Style { contentPadding(padding) }
    }

/** Matches nova icon media: `rounded-lg` `bg-muted` `size-8`. */
@Composable
fun rememberEmptyMediaStyle(size: Dp = EmptyMediaDefaults.IconSize): Style =
    rememberTheme(size) {
        Style {
            width(size)
            height(size)
            shape(RoundedCornerShape(radius.lg))
            background(colors.muted)
        }
    }

/**
 * CSS `border-dashed` for Empty root. Prefer [dashedBorder]; kept as a thin alias.
 */
fun Modifier.emptyDashedBorder(
    color: Color,
    corner: Dp,
    width: Dp = 1.dp,
    dash: Dp = 4.dp,
    gap: Dp = 4.dp,
): Modifier = dashedBorder(color = color, corner = corner, width = width, dash = dash, gap = gap)
