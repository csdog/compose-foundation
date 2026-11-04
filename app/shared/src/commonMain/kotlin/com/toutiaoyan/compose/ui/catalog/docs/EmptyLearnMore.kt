@file:OptIn(androidx.compose.foundation.style.ExperimentalFoundationStyleApi::class)

package com.toutiaoyan.compose.ui.catalog.docs

import androidx.compose.foundation.Image
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.hoverable
import androidx.compose.foundation.interaction.collectIsHoveredAsState
import androidx.compose.foundation.interaction.FocusInteraction
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.style.Style
import androidx.compose.foundation.style.contentPadding
import androidx.compose.foundation.style.rememberUpdatedStyleState
import androidx.compose.foundation.text.selection.DisableSelection
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.rotate
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.focus.focusRequester
import androidx.compose.ui.focus.onFocusChanged
import androidx.compose.ui.graphics.ColorFilter
import androidx.compose.ui.input.InputMode
import androidx.compose.ui.input.key.onPreviewKeyEvent
import androidx.compose.ui.input.pointer.PointerIcon
import androidx.compose.ui.input.pointer.pointerHoverIcon
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.platform.LocalInputModeManager
import androidx.compose.ui.platform.LocalUriHandler
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.semantics.*
import androidx.compose.ui.text.*
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.LineHeightStyle
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.composables.icons.lucide.ArrowUpRight
import com.composables.icons.lucide.Lucide
import com.toutiaoyan.compose.ui.catalog.LocalDocNavigation
import com.toutiaoyan.compose.ui.components.button.*
import com.toutiaoyan.compose.ui.components.text.Text
import com.toutiaoyan.compose.ui.theme.currentTheme

/** One actual link; the surrounding chrome extends its pointer target through padding and icon. */
@Composable
internal fun EmptyLearnMore(
    text: String,
    rtl: Boolean = false,
    inlineEnd: Boolean = false,
) {
    val theme = currentTheme
    val navigation = LocalDocNavigation.current
    val uri = LocalUriHandler.current
    val open by rememberUpdatedState<() -> Unit>({ navigation?.navigate("") ?: uri.openUri("#") })
    val interactions = remember { MutableInteractionSource() }
    val hovered by interactions.collectIsHoveredAsState()
    val focusInteraction = remember { FocusInteraction.Focus() }
    val focus = remember { FocusRequester() }
    val mode = LocalInputModeManager.current
    val styleState = rememberUpdatedStyleState(interactions)
    Row(
        Modifier.testTag("empty-learn-more")
            .semantics(mergeDescendants = true) {
                role = Role.Button
                onClick {
                    open()
                    true
                }
            }
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
            .buttonStyle(
                variant = ButtonVariant.Link,
                size = ButtonSize.Sm,
                styleState = styleState,
                style = if (inlineEnd) Style { contentPadding(PaddingValues(start = 10.dp, end = 6.dp)) } else Style,
            ),
        horizontalArrangement = Arrangement.spacedBy(4.dp),
        verticalAlignment = Alignment.CenterVertically,
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
                        color = theme.colors.mutedForeground,
                    ),
            ) {
                buildAnnotatedString {
                    withLink(
                        LinkAnnotation.Url(
                            "#",
                            TextLinkStyles(
                                style =
                                    SpanStyle(
                                        color = theme.colors.mutedForeground,
                                        textDecoration = if (hovered) TextDecoration.Underline else TextDecoration.None,
                                    ),
                            ),
                            linkInteractionListener = {
                                open()
                            },
                        ),
                    ) { append(text) }
                }
            }
        }
        Image(
            Lucide.ArrowUpRight,
            null,
            Modifier.size(14.dp).rotate(if (rtl) 270f else 0f),
            colorFilter = ColorFilter.tint(theme.colors.mutedForeground),
        )
    }
}
