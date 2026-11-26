package com.toutiaoyan.compose.ui.components.drawer

import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberUpdatedState
import androidx.compose.runtime.staticCompositionLocalOf
import com.toutiaoyan.compose.ui.components.overlay.LocalOverlayExitOutsideDismiss
import com.toutiaoyan.compose.ui.components.overlay.TeleportPrimitive

private val LocalDrawerOutsideDismiss = staticCompositionLocalOf<(() -> Unit)?> { null }

/**
 * Full-window portal ([TeleportPrimitive]) — web `DrawerPortal`.
 * Prefer [DrawerContent] unless you need to customize Overlay / [DrawerContentImpl] separately.
 */
@Composable
fun DrawerPortal(
    onPointerDownOutside: (() -> Boolean)? = null,
    onInteractOutside: (() -> Boolean)? = null,
    content: @Composable () -> Unit,
) {
    val drawer = LocalDrawerState.current
    val parentDismiss = LocalDrawerOutsideDismiss.current
    val latestParentDismiss = rememberUpdatedState(parentDismiss)
    val latestDrawer = rememberUpdatedState(drawer)
    val latestPointerOutside = rememberUpdatedState(onPointerDownOutside)
    val latestInteractOutside = rememberUpdatedState(onInteractOutside)
    val dismissNearestOpen: () -> Unit =
        remember {
            {
                val current = latestDrawer.value
                if (!current.open) {
                    latestParentDismiss.value?.invoke()
                } else if (current.dismissOnClickOutside &&
                    latestPointerOutside.value?.invoke() != true &&
                    latestInteractOutside.value?.invoke() != true
                ) {
                    current.onOpenChange(false)
                }
            }
        }
    CompositionLocalProvider(
        LocalDrawerOutsideDismiss provides dismissNearestOpen,
        LocalOverlayExitOutsideDismiss provides parentDismiss,
    ) {
        TeleportPrimitive(
            visible = drawer.open,
            onDismissRequest = { drawer.onOpenChange(false) },
            dismissOnClickOutside = drawer.dismissOnClickOutside,
            onPointerDownOutside = onPointerDownOutside,
            onInteractOutside = onInteractOutside,
        ) {
            CompositionLocalProvider(LocalDrawerTeleportScope provides this) {
                content()
            }
        }
    }
}
