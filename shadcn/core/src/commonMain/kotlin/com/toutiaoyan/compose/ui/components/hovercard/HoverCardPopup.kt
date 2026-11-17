package com.toutiaoyan.compose.ui.components.hovercard

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.EnterExitState
import androidx.compose.animation.EnterTransition
import androidx.compose.animation.ExitTransition
import androidx.compose.animation.core.MutableTransitionState
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.foundation.layout.Box
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.unit.*
import androidx.compose.ui.window.Popup
import androidx.compose.ui.window.PopupProperties
import androidx.compose.ui.window.PopupPositionProvider
import com.toutiaoyan.compose.ui.theme.currentTheme
import com.toutiaoyan.compose.ui.tokens.StylePreset
import com.toutiaoyan.compose.ui.components.overlay.PopupAlign
import com.toutiaoyan.compose.ui.components.overlay.PopupPlacement
import com.toutiaoyan.compose.ui.components.overlay.PopupPositionResult
import com.toutiaoyan.compose.ui.components.overlay.PopupTransitions
import com.toutiaoyan.compose.ui.components.overlay.PopupPositionProvider as DefaultPopupPositionProvider

// Identity markers let explicit custom transitions, including None, retain priority.
internal val HoverCardDefaultEnter = fadeIn(tween(0))
internal val HoverCardDefaultExit = fadeOut(tween(0))

/** Coordinates are consumed only by the popup measure policy, without recomposing either sibling. */
internal class HoverCardAnchor {
    var root by mutableStateOf<IntRect?>(null)
    var trigger by mutableStateOf<IntRect?>(null)
}

internal class HoverCardPositionProvider(
    private val anchor: HoverCardAnchor,
    private val delegate: PopupPositionProvider,
    private val onPositioned: (PopupPositionResult) -> Unit = {},
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
        return if (delegate is HoverCardCollisionPositionProvider) {
            val result = delegate.calculatePositionResult(actual, windowSize, layoutDirection, popupContentSize)
            onPositioned(result)
            result.offset
        } else {
            delegate.calculatePosition(actual, windowSize, layoutDirection, popupContentSize)
        }
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
    val defaultEnter = enter === HoverCardDefaultEnter
    val defaultExit = exit === HoverCardDefaultExit
    val animatePosition = nova && (defaultEnter || defaultExit)
    val geometry = remember { mutableStateOf<PopupPositionResult?>(null) }
    val collisionPadding = with(LocalDensity.current) { 5.dp.roundToPx() }
    val position =
        remember(anchor, placement, offsetPx, align, alignOffsetPx, nova, collisionPadding) {
            val delegate =
                if (nova) {
                    HoverCardCollisionPositionProvider(placement, offsetPx, align, alignOffsetPx, collisionPadding)
                } else {
                    DefaultPopupPositionProvider(placement, offsetPx, align, alignOffsetPx)
                }
            HoverCardPositionProvider(anchor, delegate) { geometry.value = it }
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
        AnimatedVisibility(
            visibleState = visible,
            enter =
                when {
                    !defaultEnter -> enter
                    nova -> EnterTransition.None
                    else -> PopupTransitions.Enter
                },
            exit =
                when {
                    !defaultExit -> exit
                    nova -> ExitTransition.None
                    else -> PopupTransitions.Exit
                },
        ) {
            if (animatePosition) {
                val visibility = transition
                val progress =
                    visibility.animateFloat(
                        transitionSpec = {
                            tween(if (targetState == EnterExitState.Visible || defaultExit) 100 else 0)
                        },
                        label = "Hover Card progress",
                    ) { state ->
                        when (state) {
                            EnterExitState.PreEnter -> if (defaultEnter) 0f else 1f
                            EnterExitState.Visible -> 1f
                            EnterExitState.PostExit -> if (defaultExit) 0f else 1f
                        }
                    }
                val slide =
                    if (defaultEnter) {
                        visibility.animateFloat(
                            transitionSpec = { tween(100) },
                            label = "Hover Card enter slide",
                        ) { state -> if (state == EnterExitState.PreEnter) 1f else 0f }
                    } else {
                        null
                    }
                val slidePx = with(LocalDensity.current) { 8.dp.toPx() }
                Box(
                    Modifier.graphicsLayer {
                        val positioned = geometry.value
                        val fraction = progress.value
                        alpha = if (defaultEnter && positioned == null) 0f else fraction
                        scaleX = 0.95f + 0.05f * fraction
                        scaleY = scaleX
                        if (positioned != null) transformOrigin = positioned.transformOrigin
                        val amount = (slide?.value ?: 0f) * slidePx
                        translationX =
                            when (positioned?.physicalSide) {
                                PopupPlacement.Start -> amount
                                PopupPlacement.End -> -amount
                                else -> 0f
                            }
                        translationY =
                            when (positioned?.physicalSide) {
                                PopupPlacement.Top -> amount
                                PopupPlacement.Bottom -> -amount
                                else -> 0f
                            }
                    },
                ) { content() }
            } else {
                content()
            }
        }
    }
}
