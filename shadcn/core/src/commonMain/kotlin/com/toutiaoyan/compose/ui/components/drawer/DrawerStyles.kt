@file:OptIn(ExperimentalFoundationStyleApi::class)

package com.toutiaoyan.compose.ui.components.drawer

import com.toutiaoyan.compose.ui.tokens.StylePreset
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.style.ExperimentalFoundationStyleApi
import androidx.compose.foundation.style.Style
import androidx.compose.foundation.style.contentPadding
import androidx.compose.runtime.Composable
import androidx.compose.ui.platform.LocalLayoutDirection
import androidx.compose.ui.unit.LayoutDirection
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.LinearGradientShader
import androidx.compose.ui.graphics.Shader
import androidx.compose.ui.graphics.ShaderBrush
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import com.toutiaoyan.compose.ui.utils.rememberTheme

/**
 * Base UI / shadcn base-nova `swipeDirection`: which way the user swipes to dismiss
 * (and therefore which edge the panel sits on).
 *
 * - [Down] → bottom sheet (default)
 * - [Up] → top
 * - [Left] → left edge
 * - [Right] → right edge
 */
enum class DrawerSwipeDirection { Up, Down, Left, Right }

/** Panel chrome for [DrawerContent] — `bg-popover` + `rounded-t/b/l/r-xl`. */
@Composable
fun DrawerSwipeDirection.rememberPanelStyle(): Style {
    val layoutDirection = LocalLayoutDirection.current
    val density = LocalDensity.current
    return rememberTheme(this, layoutDirection, density) {
        Style {
            shape(
                drawerPhysicalPanelShape(this@rememberPanelStyle, radius.xl, layoutDirection),
            )
            background(colors.popover)
            if (theme.activePreset == StylePreset.Nova) {
                borderWidth(1.dp)
                borderBrush(
                    DrawerEdgeBorderBrush(this@rememberPanelStyle, colors.border, with(density) { radius.xl.toPx() }),
                )
            }
        }
    }
}

/** [DrawerHeader] — official `p-4 pb-0`. Content itself stays unpadded (edge-to-edge). */
@Composable
fun rememberDrawerHeaderStyle(): Style =
    rememberTheme {
        Style {
            contentPadding(spacing.scale4)
            contentPaddingBottom(0.dp)
        }
    }

/** [DrawerFooter] — official `p-4 pt-0`. */
@Composable
fun rememberDrawerFooterStyle(): Style =
    rememberTheme {
        Style {
            contentPadding(spacing.scale4)
            contentPaddingTop(0.dp)
        }
    }

/** Drag-handle pill for [DrawerSwipeHandle] (Base `after:h-1 after:w-24` on y-axis). */
@Composable
fun rememberDrawerSwipeHandleStyle(): Style =
    rememberTheme {
        Style {
            width(if (theme.activePreset == StylePreset.Nova) 96.dp else 100.dp)
            height(if (theme.activePreset == StylePreset.Nova) 4.dp else 8.dp)
            shape(RoundedCornerShape(percent = 50))
            background(colors.muted)
        }
    }

/** Rounds only the edge facing the viewport interior. */
fun drawerPanelShape(
    swipeDirection: DrawerSwipeDirection,
    radius: Dp,
): RoundedCornerShape =
    when (swipeDirection) {
        DrawerSwipeDirection.Down ->
            RoundedCornerShape(topStart = radius, topEnd = radius)
        DrawerSwipeDirection.Up ->
            RoundedCornerShape(bottomStart = radius, bottomEnd = radius)
        DrawerSwipeDirection.Left ->
            RoundedCornerShape(topEnd = radius, bottomEnd = radius)
        DrawerSwipeDirection.Right ->
            RoundedCornerShape(topStart = radius, bottomStart = radius)
    }

/** Keep the public logical-corner helper compatible while painting physical swipe edges. */
internal fun drawerPhysicalPanelShape(
    direction: DrawerSwipeDirection,
    radius: Dp,
    layoutDirection: LayoutDirection,
): RoundedCornerShape =
    drawerPanelShape(
        if (layoutDirection == LayoutDirection.Rtl) {
            when (direction) {
                DrawerSwipeDirection.Left -> DrawerSwipeDirection.Right
                DrawerSwipeDirection.Right -> DrawerSwipeDirection.Left
                else -> direction
            }
        } else {
            direction
        },
        radius,
    )

/** A directional border through Style's brush slot, so caller border overrides still cascade normally. */
private class DrawerEdgeBorderBrush(
    private val direction: DrawerSwipeDirection,
    private val color: Color,
    private val radiusPx: Float,
) : ShaderBrush() {
    override fun createShader(size: Size): Shader {
        val vertical = direction.isVertical()
        val extent = if (vertical) size.height else size.width
        val band = (radiusPx / extent.coerceAtLeast(1f)).coerceIn(0f, 1f)
        val start =
            when (direction) {
                DrawerSwipeDirection.Down -> Offset.Zero
                DrawerSwipeDirection.Up -> Offset(0f, size.height)
                DrawerSwipeDirection.Left -> Offset(size.width, 0f)
                DrawerSwipeDirection.Right -> Offset.Zero
            }
        val end =
            when (direction) {
                DrawerSwipeDirection.Down -> Offset(0f, size.height)
                DrawerSwipeDirection.Up -> Offset.Zero
                DrawerSwipeDirection.Left -> Offset.Zero
                DrawerSwipeDirection.Right -> Offset(size.width, 0f)
            }
        return LinearGradientShader(
            from = start,
            to = end,
            colors = listOf(color, color, Color.Transparent, Color.Transparent),
            colorStops = listOf(0f, band, band, 1f),
        )
    }
}
