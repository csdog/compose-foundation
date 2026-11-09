package com.toutiaoyan.compose.ui.components.drawer

import androidx.compose.ui.graphics.Color
import com.toutiaoyan.compose.ui.theme.currentTheme
import com.toutiaoyan.compose.ui.tokens.StylePreset
import androidx.compose.animation.EnterTransition
import androidx.compose.animation.ExitTransition
import androidx.compose.runtime.Composable
import com.toutiaoyan.compose.ui.components.overlay.OverlayWithOpacity
import com.toutiaoyan.compose.ui.components.overlay.OverlayTransitions

/** Scrim — web `DrawerOverlay`. Must be composed under [DrawerPortal]. */
@Composable
fun DrawerOverlay(
    enter: EnterTransition = OverlayTransitions.ScrimEnter,
    exit: ExitTransition = OverlayTransitions.ScrimExit,
) {
    val modal =
        LocalDrawerTeleportScope.current
            ?: error("DrawerOverlay must be used within DrawerPortal { }")
    val drawer = LocalDrawerState.current
    val nestHost = LocalDrawerNestHost.current
    modal.OverlayWithOpacity(
        opacity = { drawerOverlayOpacity(nestHost?.swipeProgress?.invoke() ?: 0f, drawer.hasSnapPoints) },
        enter = enter,
        exit = exit,
        color =
            if (currentTheme.activePreset == StylePreset.Nova) {
                Color.Black.copy(
                    alpha = 0.1f,
                )
            } else {
                currentTheme.colors.foreground.copy(alpha = 0.5f)
            },
    )
}

/** CSS max(min-opacity, 1 - swipe-progress), applied to backdrop and scrim together. */
internal fun drawerOverlayOpacity(
    swipeProgress: Float,
    hasSnapPoints: Boolean,
): Float = (1f - swipeProgress.coerceIn(0f, 1f)).coerceAtLeast(if (hasSnapPoints) 0.5f else 0f)

internal fun drawerSwipeProgress(
    offset: Float,
    extent: Float,
): Float = if (!offset.isFinite() || !extent.isFinite() || extent <= 0f) 0f else (offset / extent).coerceIn(0f, 1f)

/** Keep held swipes direct while the logical child count catches up during its exit. */
internal fun drawerStackDepth(
    animatedDepth: Float,
    nestedCount: Int,
    swipeProgress: Float,
): Float {
    val closingDelta = animatedDepth - nestedCount
    val restoredStep = if (closingDelta > 0f) closingDelta.coerceAtMost(1f) else 1f
    return (animatedDepth - restoredStep * swipeProgress.coerceIn(0f, 1f)).coerceAtLeast(0f)
}
