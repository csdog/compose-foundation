@file:OptIn(androidx.compose.foundation.style.ExperimentalFoundationStyleApi::class)

package com.toutiaoyan.compose.ui.catalog.docs

import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.slideInVertically
import androidx.compose.animation.slideOutVertically
import androidx.compose.foundation.clickable
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.hoverable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.interaction.collectIsHoveredAsState
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.style.Style
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.drawBehind
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.focus.focusRequester
import androidx.compose.ui.focus.onFocusChanged
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.input.InputMode
import androidx.compose.ui.input.key.onPreviewKeyEvent
import androidx.compose.ui.input.pointer.PointerIcon
import androidx.compose.ui.input.pointer.pointerHoverIcon
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.platform.LocalInputModeManager
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.platform.LocalUriHandler
import androidx.compose.ui.platform.LocalWindowInfo
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.text.*
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.unit.IntOffset
import androidx.compose.ui.unit.IntRect
import androidx.compose.ui.unit.IntSize
import androidx.compose.ui.unit.LayoutDirection
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Popup
import androidx.compose.ui.window.PopupPositionProvider
import androidx.compose.ui.window.PopupProperties
import com.composables.icons.lucide.*
import com.toutiaoyan.compose.ui.catalog.LocalDocNavigation
import com.toutiaoyan.compose.ui.components.icon.Icon
import com.toutiaoyan.compose.ui.components.marker.*
import com.toutiaoyan.compose.ui.components.text.Text
import com.toutiaoyan.compose.ui.components.toast.*
import com.toutiaoyan.compose.ui.theme.currentTheme

@Composable
internal fun MarkerActionsRecipe() {
    val navigation = LocalDocNavigation.current
    val uri = LocalUriHandler.current
    val toasts = rememberToastState()
    Column(
        Modifier.widthIn(max = 384.dp).fillMaxWidth().padding(vertical = 48.dp),
        verticalArrangement = Arrangement.spacedBy(32.dp),
    ) {
        MarkerLinkAction { navigation?.navigate("links-and-buttons") ?: uri.openUri("#links-and-buttons") }
        MarkerButtonAction { toasts.show("You clicked the revert button", durationMillis = 4000L) }
    }
    MarkerActionToaster(toasts)
}

@Composable
private fun MarkerActionToaster(state: ToastState) {
    if (state.toasts.isEmpty()) return
    val density = LocalDensity.current
    val viewportWidth = with(density) { LocalWindowInfo.current.containerSize.width.toDp() }
    val compact = viewportWidth > 0.dp && viewportWidth <= 600.dp
    val width = if (compact) (viewportWidth - 32.dp).coerceAtLeast(0.dp) else 356.dp
    val inset = with(density) { (if (compact) 16.dp else 24.dp).roundToPx() }
    val position =
        remember(inset) {
            object : PopupPositionProvider {
                override fun calculatePosition(
                    anchorBounds: IntRect,
                    windowSize: IntSize,
                    layoutDirection: LayoutDirection,
                    popupContentSize: IntSize,
                ) = IntOffset((windowSize.width - popupContentSize.width) / 2, inset)
            }
        }
    Popup(
        popupPositionProvider = position,
        properties = PopupProperties(focusable = false, dismissOnBackPress = false, dismissOnClickOutside = false),
    ) {
        Toaster(
            state = state,
            modifier = Modifier.width(width).testTag("marker-toast"),
            cardStyle =
                Style {
                    maxWidth(width)
                    minHeight(54.dp)
                    shape(RoundedCornerShape(10.dp))
                },
            position = ToastPosition.TopCenter,
            closeButton = false,
            enter = fadeIn() + slideInVertically(initialOffsetY = { -it / 2 }),
            exit = fadeOut() + slideOutVertically(targetOffsetY = { -it / 2 }),
        )
    }
}

@Composable
private fun Modifier.markerActionFocusOutline(offset: Int): Modifier {
    var hasFocus by remember { mutableStateOf(false) }
    val mode = LocalInputModeManager.current
    val ring = currentTheme.colors.ring.copy(alpha = 0.5f)
    val showOutline = currentTheme.ring.enabled && hasFocus && mode.inputMode == InputMode.Keyboard
    return onFocusChanged { hasFocus = it.hasFocus }
        .onPreviewKeyEvent {
            mode.requestInputMode(InputMode.Keyboard)
            false
        }.drawBehind {
            if (showOutline) {
                val stroke = 1.dp.toPx()
                val outset = offset.dp.toPx() + stroke / 2
                drawRect(
                    color = ring,
                    topLeft = Offset(-outset, -outset),
                    size = Size(size.width + 2 * outset, size.height + 2 * outset),
                    style = Stroke(stroke),
                )
            }
        }
}

@Composable
private fun MarkerLinkAction(onOpen: () -> Unit) {
    val open by rememberUpdatedState(onOpen)
    val interactions = remember { MutableInteractionSource() }
    val hovered by interactions.collectIsHoveredAsState()
    val colors = currentTheme.colors
    val color = if (hovered) colors.foreground else colors.mutedForeground
    val focus = remember { FocusRequester() }
    val mode = LocalInputModeManager.current
    Marker(
        Modifier.testTag("marker-link")
            .markerActionFocusOutline(offset = 1)
            .focusRequester(focus)
            .hoverable(interactions)
            .pointerHoverIcon(PointerIcon.Hand)
            .pointerInput(Unit) {
                detectTapGestures {
                    mode.requestInputMode(InputMode.Touch)
                    focus.requestFocus()
                    open()
                }
            },
    ) {
        MarkerIcon { Icon(Lucide.GitBranch, contentDescription = null, tint = color) }
        MarkerContent {
            Text(textStyle = TextStyle(color = color, textDecoration = TextDecoration.Underline)) {
                buildAnnotatedString {
                    withLink(
                        LinkAnnotation.Url(
                            "#links-and-buttons",
                            styles =
                                TextLinkStyles(
                                    style = SpanStyle(color = color, textDecoration = TextDecoration.Underline),
                                ),
                            linkInteractionListener = {
                                focus.requestFocus()
                                open()
                            },
                        ),
                    ) { append("View the pull request") }
                }
            }
        }
    }
}

@Composable
private fun MarkerButtonAction(onClick: () -> Unit) {
    val interactions = remember { MutableInteractionSource() }
    val hovered by interactions.collectIsHoveredAsState()
    val colors = currentTheme.colors
    val color by animateColorAsState(if (hovered) colors.foreground else colors.mutedForeground, tween(150))
    val focus = remember { FocusRequester() }
    Marker(
        Modifier.testTag("marker-button").markerActionFocusOutline(offset = 0)
            .focusRequester(focus)
            .clickable(
                interactionSource = interactions,
                indication = null,
                role = Role.Button,
                onClick = {
                    focus.requestFocus()
                    onClick()
                },
            ).pointerHoverIcon(PointerIcon.Hand),
    ) {
        MarkerIcon { Icon(Lucide.RotateCcw, contentDescription = null, tint = color) }
        MarkerContent { Text(textStyle = TextStyle(color = color)) { "Revert this change" } }
    }
}
