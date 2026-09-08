@file:OptIn(ExperimentalFoundationStyleApi::class)

package com.toutiaoyan.compose.ui.components.drawer

import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.style.ExperimentalFoundationStyleApi
import androidx.compose.foundation.style.Style
import androidx.compose.foundation.style.contentPadding
import androidx.compose.runtime.Composable
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
fun DrawerSwipeDirection.rememberPanelStyle(): Style =
    rememberTheme(this) {
        Style {
            shape(drawerPanelShape(this@rememberPanelStyle, radius.xl))
            background(colors.popover)
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
            width(100.dp)
            height(8.dp)
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
