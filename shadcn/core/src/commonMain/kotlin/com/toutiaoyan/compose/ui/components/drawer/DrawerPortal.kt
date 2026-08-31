package com.toutiaoyan.compose.ui.components.drawer

import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import com.toutiaoyan.compose.ui.components.overlay.TeleportPrimitive

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
