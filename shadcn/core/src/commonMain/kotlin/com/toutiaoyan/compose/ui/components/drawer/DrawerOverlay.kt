package com.toutiaoyan.compose.ui.components.drawer

import androidx.compose.ui.graphics.Color
import com.toutiaoyan.compose.ui.theme.currentTheme
import com.toutiaoyan.compose.ui.tokens.StylePreset
import androidx.compose.animation.EnterTransition
import androidx.compose.animation.ExitTransition
import androidx.compose.runtime.Composable
import com.toutiaoyan.compose.ui.components.overlay.Overlay
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
    modal.Overlay(
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
