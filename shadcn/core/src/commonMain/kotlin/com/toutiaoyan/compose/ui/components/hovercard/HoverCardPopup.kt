package com.toutiaoyan.compose.ui.components.hovercard

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.EnterTransition
import androidx.compose.animation.ExitTransition
import androidx.compose.animation.core.MutableTransitionState
import androidx.compose.runtime.*
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.unit.*
import androidx.compose.ui.window.Popup
import androidx.compose.ui.window.PopupProperties
import androidx.compose.ui.window.PopupPositionProvider
import com.toutiaoyan.compose.ui.theme.currentTheme
import com.toutiaoyan.compose.ui.tokens.StylePreset
import com.toutiaoyan.compose.ui.components.overlay.PopupAlign
import com.toutiaoyan.compose.ui.components.overlay.PopupPlacement
import com.toutiaoyan.compose.ui.components.overlay.PopupPositionProvider as DefaultPopupPositionProvider

/** Coordinates are consumed only by the popup measure policy, without recomposing either sibling. */
internal class HoverCardAnchor {
    var root by mutableStateOf<IntRect?>(null)
    var trigger by mutableStateOf<IntRect?>(null)
}

internal class HoverCardPositionProvider(
    private val anchor: HoverCardAnchor,
    private val delegate: PopupPositionProvider,
) : PopupPositionProvider {
    override fun calculatePosition(
        anchorBounds: IntRect,
        windowSize: IntSize,
        layoutDirection: LayoutDirection,
        popupContentSize: IntSize,
    ): IntOffset {
        val root = anchor.root
        val trigger = anchor.trigger
        // The platform may normalize window insets before calling the position provider.
        // Preserve that normalization, replacing just the root-relative trigger geometry.
        val actual =
            if (root != null && trigger != null) {
                IntRect(anchorBounds.topLeft + (trigger.topLeft - root.topLeft), trigger.size)
            } else {
                anchorBounds
            }
        return delegate.calculatePosition(actual, windowSize, layoutDirection, popupContentSize)
    }
}

@Composable
internal fun HoverCardPopup(
    open: Boolean,
    anchor: HoverCardAnchor,
    onDismissRequest: () -> Unit,
    placement: PopupPlacement,
    offset: Dp,
    align: PopupAlign,
    alignOffset: Dp,
    dismissOnClickOutside: Boolean,
    enter: EnterTransition,
    exit: ExitTransition,
    content: @Composable () -> Unit,
) {
    val visible = remember { MutableTransitionState(false) }
    visible.targetState = open
    if (!visible.currentState && !visible.targetState) return
    val offsetPx = with(LocalDensity.current) { offset.roundToPx() }
    val alignOffsetPx = with(LocalDensity.current) { alignOffset.roundToPx() }
    val nova = currentTheme.activePreset == StylePreset.Nova
    val collisionPadding = with(LocalDensity.current) { 5.dp.roundToPx() }
    val position =
        remember(anchor, placement, offsetPx, align, alignOffsetPx, nova, collisionPadding) {
            val delegate =
                if (nova) {
                    HoverCardCollisionPositionProvider(placement, offsetPx, align, alignOffsetPx, collisionPadding)
                } else {
                    DefaultPopupPositionProvider(placement, offsetPx, align, alignOffsetPx)
                }
            HoverCardPositionProvider(anchor, delegate)
        }
    Popup(
        popupPositionProvider = position,
        onDismissRequest = onDismissRequest,
        properties =
            PopupProperties(
                focusable = false,
                dismissOnBackPress = false,
                dismissOnClickOutside = dismissOnClickOutside && visible.targetState,
            ),
    ) {
        AnimatedVisibility(visibleState = visible, enter = enter, exit = exit) { content() }
    }
}
