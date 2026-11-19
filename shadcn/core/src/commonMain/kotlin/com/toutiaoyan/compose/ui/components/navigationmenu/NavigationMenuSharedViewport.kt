@file:OptIn(androidx.compose.foundation.style.ExperimentalFoundationStyleApi::class)

package com.toutiaoyan.compose.ui.components.navigationmenu

import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.AnimatedVisibilityScope
import androidx.compose.animation.EnterExitState
import androidx.compose.animation.EnterTransition
import androidx.compose.animation.ExitTransition
import androidx.compose.animation.SizeTransform
import androidx.compose.animation.core.CubicBezierEasing
import androidx.compose.animation.core.MutableTransitionState
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.animateIntOffset
import androidx.compose.animation.core.tween
import androidx.compose.animation.core.updateTransition
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.slideInHorizontally
import androidx.compose.animation.slideOutHorizontally
import androidx.compose.animation.togetherWith
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.style.MutableStyleState
import androidx.compose.foundation.style.styleable
import androidx.compose.runtime.Composable
import androidx.compose.runtime.SideEffect
import androidx.compose.runtime.State
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateMapOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberUpdatedState
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.graphics.TransformOrigin
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.platform.LocalLayoutDirection
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.IntOffset
import androidx.compose.ui.unit.IntRect
import androidx.compose.ui.unit.IntSize
import androidx.compose.ui.unit.LayoutDirection
import androidx.compose.ui.util.lerp
import androidx.compose.ui.window.Popup
import androidx.compose.ui.window.PopupPositionProvider
import androidx.compose.ui.window.PopupProperties
import com.toutiaoyan.compose.ui.components.overlay.PopupAlign
import com.toutiaoyan.compose.ui.components.overlay.PopupPlacement
import com.toutiaoyan.compose.ui.components.overlay.popupGraphicsLayer
import com.toutiaoyan.compose.ui.components.overlay.PopupPositionProvider as AnchoredPositionProvider

internal class NavigationMenuViewportState(
    val align: PopupAlign,
    private val orientation: NavigationMenuOrientation,
    private val direction: LayoutDirection,
) {
    val anchors = mutableStateMapOf<String, IntRect>()
    val panels = mutableStateMapOf<String, NavigationMenuPanel>()
    val rootFocus = linkedMapOf<String, FocusRequester>()
    val disabledFocus = mutableSetOf<String>()

    fun adjacentFocus(
        from: String,
        step: Int,
    ): FocusRequester? {
        val eligible =
            rootFocus.keys.filter { it !in disabledFocus }.sortedBy { item ->
                val anchor = anchors[item] ?: return@sortedBy 0
                if (orientation == NavigationMenuOrientation.Vertical) {
                    anchor.top
                } else if (direction == LayoutDirection.Rtl) {
                    -anchor.right
                } else {
                    anchor.left
                }
            }
        val index = eligible.indexOf(from)
        return if (index < 0) null else eligible.getOrNull(index + step)?.let(rootFocus::get)
    }
}

internal class NavigationMenuPanel(
    val side: PopupPlacement,
    val sideOffset: Dp,
    val alignOffset: Dp,
    val dismissOnClickOutside: Boolean,
    val focusable: () -> Boolean,
    val keyboardSession: () -> Boolean,
    val content: @Composable (active: Boolean) -> Unit,
)

internal const val NavigationMenuViewportTag = "navigation-menu-viewport"
private const val ViewportDuration = 350
private const val ViewportExitDuration = 150
private val ViewportEasing = CubicBezierEasing(0.22f, 1f, 0.36f, 1f)

@Composable
internal fun NavigationMenuSharedViewport(
    state: NavigationMenuViewportState,
    value: String?,
    onDismiss: () -> Unit,
) {
    val open = value != null && state.panels[value] != null
    var lastValue by remember { mutableStateOf<String?>(null) }
    SideEffect {
        if (open) lastValue = value
    }
    val visible = remember { MutableTransitionState(false) }
    visible.targetState = open
    val shown = if (open) value else lastValue
    val panel = state.panels[shown] ?: return
    val selected = checkNotNull(shown)
    if (!visible.targetState && !visible.currentState && visible.isIdle) return

    val rtl = LocalLayoutDirection.current == LayoutDirection.Rtl
    val transition = updateTransition(selected, label = "Navigation menu position")
    val start by transition.animateIntOffset(
        transitionSpec = { tween(ViewportDuration, easing = ViewportEasing) },
        label = "Navigation menu anchor start",
    ) { state.anchors[it]?.topLeft ?: IntOffset.Zero }
    val end by transition.animateIntOffset(
        transitionSpec = { tween(ViewportDuration, easing = ViewportEasing) },
        label = "Navigation menu anchor end",
    ) { state.anchors[it]?.bottomRight ?: IntOffset.Zero }

    val side =
        if (rtl) {
            when (panel.side) {
                PopupPlacement.Start -> PopupPlacement.End
                PopupPlacement.End -> PopupPlacement.Start
                else -> panel.side
            }
        } else {
            panel.side
        }
    val mirrorAlignment = rtl && (side == PopupPlacement.Top || side == PopupPlacement.Bottom)
    val align =
        if (mirrorAlignment) {
            when (state.align) {
                PopupAlign.Start -> PopupAlign.End
                PopupAlign.End -> PopupAlign.Start
                PopupAlign.Center -> PopupAlign.Center
            }
        } else {
            state.align
        }
    val offset = with(LocalDensity.current) { panel.sideOffset.roundToPx() }
    val alignment = with(LocalDensity.current) { panel.alignOffset.roundToPx() } * if (mirrorAlignment) -1 else 1
    val animatedAnchor = rememberUpdatedState(IntRect(start.x, start.y, end.x, end.y))
    val origin = remember(rtl) { mutableStateOf(TransformOrigin(if (rtl) 1f else 0f, 0f)) }
    val provider =
        remember(side, offset, align, alignment, origin) {
            val delegate = AnchoredPositionProvider(side, offset, align, alignment)
            object : PopupPositionProvider {
                override fun calculatePosition(
                    anchorBounds: IntRect,
                    windowSize: IntSize,
                    layoutDirection: LayoutDirection,
                    popupContentSize: IntSize,
                ): IntOffset {
                    val trigger = animatedAnchor.value
                    val position =
                        delegate.calculatePositionResult(
                            if (trigger.width > 0) trigger else anchorBounds,
                            windowSize,
                            layoutDirection,
                            popupContentSize,
                        )
                    origin.value = position.transformOrigin
                    return position.offset
                }
            }
        }

    if (open && !panel.keyboardSession()) NavigationMenuWindowEscape(onDismiss)

    Popup(
        popupPositionProvider = provider,
        onDismissRequest = { if (open) onDismiss() },
        properties =
            PopupProperties(
                focusable = open && panel.focusable(),
                dismissOnBackPress = open,
                dismissOnClickOutside = open && panel.dismissOnClickOutside,
                usePlatformDefaultWidth = false,
            ),
    ) {
        val chrome = remember { MutableStyleState(MutableInteractionSource()) }
        AnimatedVisibility(
            visibleState = visible,
            enter = EnterTransition.None,
            exit = ExitTransition.None,
        ) {
            val motion = viewportMotion(origin).testTag(NavigationMenuViewportTag)
            Box(motion.styleable(chrome, rememberNavMenuViewportStyle())) {
                AnimatedContent(
                    targetState = selected,
                    contentAlignment = Alignment.TopStart,
                    label = "Navigation menu viewport",
                    transitionSpec = {
                        val from = state.anchors[initialState]?.center?.x ?: 0
                        val to = state.anchors[targetState]?.center?.x ?: 0
                        val direction = (to - from).compareTo(0)
                        val fade = tween<Float>(ViewportDuration, easing = ViewportEasing)
                        val slide = tween<IntOffset>(ViewportDuration, easing = ViewportEasing)
                        val enter = slideInHorizontally(slide) { direction * it / 2 } + fadeIn(fade)
                        val exit = slideOutHorizontally(slide) { -direction * it / 2 } + fadeOut(fade)
                        (enter togetherWith exit).using(
                            SizeTransform { _, _ -> tween(ViewportDuration, easing = ViewportEasing) },
                        )
                    },
                ) { item ->
                    state.panels[item]?.content?.invoke(open && item == value)
                }
            }
        }
    }
}

private const val VIEWPORT_COLLAPSED_SCALE = 0.9f

@Composable
private fun AnimatedVisibilityScope.viewportMotion(origin: State<TransformOrigin>): Modifier {
    val visibility =
        transition.animateFloat(
            transitionSpec = {
                if (targetState == EnterExitState.Visible) {
                    tween(ViewportDuration, easing = ViewportEasing)
                } else {
                    tween(ViewportExitDuration)
                }
            },
            label = "Navigation menu opacity and scale",
        ) { state -> if (state == EnterExitState.Visible) 1f else 0f }

    return Modifier.popupGraphicsLayer {
        val scale = lerp(VIEWPORT_COLLAPSED_SCALE, 1f, visibility.value)
        alpha = visibility.value
        scaleX = scale
        scaleY = scale
        transformOrigin = origin.value
    }
}
