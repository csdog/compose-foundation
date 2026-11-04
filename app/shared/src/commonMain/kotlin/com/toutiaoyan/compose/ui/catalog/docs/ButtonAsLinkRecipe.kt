@file:OptIn(androidx.compose.foundation.style.ExperimentalFoundationStyleApi::class)

package com.toutiaoyan.compose.ui.catalog.docs

import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.hoverable
import androidx.compose.foundation.interaction.FocusInteraction
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.style.rememberUpdatedStyleState
import androidx.compose.foundation.text.selection.DisableSelection
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.focus.focusRequester
import androidx.compose.ui.focus.onFocusChanged
import androidx.compose.ui.input.InputMode
import androidx.compose.ui.input.key.onPreviewKeyEvent
import androidx.compose.ui.input.pointer.PointerIcon
import androidx.compose.ui.input.pointer.pointerHoverIcon
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.platform.LocalInputModeManager
import androidx.compose.ui.platform.LocalUriHandler
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.*
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.LineHeightStyle
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.unit.sp
import com.toutiaoyan.compose.ui.catalog.LocalDocNavigation
import com.toutiaoyan.compose.ui.components.button.*
import com.toutiaoyan.compose.ui.components.text.Text
import com.toutiaoyan.compose.ui.theme.currentTheme

@Composable
internal fun ButtonAsLinkRecipe(onOpen: (() -> Unit)? = null) {
    val navigation = LocalDocNavigation.current
    val uri = LocalUriHandler.current
    ButtonTextLink("Login", "#", onOpen = onOpen ?: { navigation?.navigate("") ?: uri.openUri("#") })
}

/** The link span owns keyboard/accessibility behavior; padding adds only pointer hit testing. */
@Composable
private fun ButtonTextLink(
    text: String,
    href: String,
    onOpen: (() -> Unit)? = null,
) {
    val theme = currentTheme
    val uri = LocalUriHandler.current
    val open by rememberUpdatedState(onOpen ?: { uri.openUri(href) })
    val interactions = remember { MutableInteractionSource() }
    val focusInteraction = remember { FocusInteraction.Focus() }
    val focus = remember { FocusRequester() }
    val mode = LocalInputModeManager.current
    val styleState = rememberUpdatedStyleState(interactions)
    Box(
        Modifier.testTag("button-as-link")
            .focusRequester(focus)
            .onFocusChanged {
                interactions.tryEmit(if (it.hasFocus) focusInteraction else FocusInteraction.Unfocus(focusInteraction))
            }
            .onPreviewKeyEvent {
                mode.requestInputMode(InputMode.Keyboard)
                false
            }
            .hoverable(interactions)
            .pointerHoverIcon(PointerIcon.Hand)
            .pointerInput(Unit) {
                detectTapGestures {
                    mode.requestInputMode(InputMode.Touch)
                    focus.requestFocus()
                    open()
                }
            }
            .buttonStyle(variant = ButtonVariant.Secondary, size = ButtonSize.Sm, styleState = styleState),
        contentAlignment = Alignment.Center,
    ) {
        DisableSelection {
            Text(
                maxLines = 1,
                textStyle =
                    TextStyle(
                        fontFamily = theme.fontFamily,
                        fontSize = 12.8.sp,
                        lineHeight = 20.sp,
                        lineHeightStyle =
                            LineHeightStyle(
                                LineHeightStyle.Alignment.Center,
                                LineHeightStyle.Trim.None,
                                LineHeightStyle.Mode.Tight,
                            ),
                        fontWeight = FontWeight.Medium,
                        color = theme.colors.secondaryForeground,
                    ),
            ) {
                buildAnnotatedString {
                    withLink(
                        LinkAnnotation.Url(
                            href,
                            styles =
                                TextLinkStyles(
                                    style =
                                        SpanStyle(
                                            color = theme.colors.secondaryForeground,
                                            textDecoration = TextDecoration.None,
                                        ),
                                ),
                            linkInteractionListener = { open() },
                        ),
                    ) { append(text) }
                }
            }
        }
    }
}
