@file:OptIn(ExperimentalFoundationStyleApi::class)

package com.toutiaoyan.compose.ui.components.inputgroup

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.style.ExperimentalFoundationStyleApi
import androidx.compose.foundation.style.Style
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.unit.dp
import com.toutiaoyan.compose.ui.ScreenshotTest
import com.toutiaoyan.compose.ui.components.button.Button
import com.toutiaoyan.compose.ui.components.button.ButtonSize
import com.toutiaoyan.compose.ui.components.button.ButtonVariant
import com.toutiaoyan.compose.ui.components.card.Card
import com.toutiaoyan.compose.ui.components.card.CardAction
import com.toutiaoyan.compose.ui.components.card.CardDescription
import com.toutiaoyan.compose.ui.components.card.CardHeader
import com.toutiaoyan.compose.ui.components.card.CardTitle
import com.toutiaoyan.compose.ui.components.input.Input
import com.toutiaoyan.compose.ui.components.message.Message
import com.toutiaoyan.compose.ui.components.message.MessageAvatar
import com.toutiaoyan.compose.ui.components.text.Text
import com.toutiaoyan.compose.ui.theme.currentTheme
import com.toutiaoyan.compose.ui.tokens.StylePreset
import kotlin.test.Test
import com.toutiaoyan.compose.tailwind.core.twSpacing

class InputGroupScreenshotTest : ScreenshotTest() {
    @Test
    fun novaInlineLayoutLight() = novaInlineLayout(darkTheme = false)

    @Test
    fun novaInlineLayoutDark() = novaInlineLayout(darkTheme = true)

    private fun novaInlineLayout(darkTheme: Boolean) {
        snapshot("input_group_nova_inline_layout", darkTheme, stylePreset = StylePreset.Nova) {
            Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
                InputGroup(modifier = Modifier.width(260.dp)) {
                    InputGroupInput(value = "Search")
                    InputGroupAddon(InputGroupAddonAlign.InlineEnd) {
                        InputGroupButton("Go", onClick = {})
                    }
                }
                InputGroup(modifier = Modifier.width(260.dp)) {
                    InputGroupInput(value = "Tall inline addon")
                    InputGroupAddon(InputGroupAddonAlign.InlineEnd) {
                        InputGroupButton(onClick = {}, size = InputGroupButtonSize.IconSm) {
                            Text { "+" }
                        }
                    }
                }
            }
        }
    }

    /**
     * The trailing-addon row is a regression guard: `Input` hardcodes
     * `fillMaxWidth()`, which used to eat the whole group row and squeeze the trailing
     * addon into one-character-per-line vertical text before the group measured the
     * control slot against remaining width after inline addons.
     */
    private fun states(darkTheme: Boolean) {
        snapshot("input_group_states", darkTheme = darkTheme) {
            Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
                InputGroup(modifier = Modifier.width(260.dp)) {
                    InputGroupAddon(InputGroupAddonAlign.InlineStart) {
                        InputGroupText("$")
                    }
                    Input(value = "0.00", onValueChange = {})
                }
                InputGroup(modifier = Modifier.width(260.dp)) {
                    Input(value = "example", onValueChange = {})
                    InputGroupAddon(InputGroupAddonAlign.InlineEnd) {
                        InputGroupText(".com")
                    }
                }
            }
        }
    }

    @Test
    fun states_light() = states(darkTheme = false)

    @Test
    fun states_dark() = states(darkTheme = true)

    // Exact reproduction of MessageScrollerDoc.kt's InteractiveChatPanel: Card
    // 320dp x 420dp, header with title/description/reset button, footer with the real
    // InputGroup (Ghost leading "+" button, Default trailing send button),
    // unfocused (matching a static @Preview render, no cursor visible).
    private fun chatPanelLayout(darkTheme: Boolean) {
        snapshot("chat_panel_layout", darkTheme = darkTheme) {
            Card(modifier = Modifier.width(320.dp).height(420.dp)) {
                CardHeader {
                    Box(Modifier.fillMaxWidth()) {
                        Column(verticalArrangement = Arrangement.spacedBy(twSpacing.xs)) {
                            CardTitle { "New Chat" }
                            CardDescription { "How can I help you today?" }
                        }
                        CardAction(modifier = Modifier.align(Alignment.TopEnd)) {
                            Button(onClick = {}, variant = ButtonVariant.Outline, size = ButtonSize.Icon) {
                                Text { "↻" }
                            }
                        }
                    }
                }
                Message(avatar = { MessageAvatar { Text { "AI" } } }) {
                    Text { "Hi! Ask me anything about MessageScroller." }
                }
                InputGroup {
                    InputGroupAddon(InputGroupAddonAlign.InlineStart) {
                        InputGroupButton(onClick = {}, size = InputGroupButtonSize.IconSm) {
                            Text { "+" }
                        }
                    }
                    Input(
                        value = "this is single",
                        onValueChange = {},
                        placeholder = "Message MessageScroller…",
                        singleLine = false,
                    )
                    InputGroupAddon(InputGroupAddonAlign.InlineEnd) {
                        InputGroupButton(
                            onClick = {},
                            variant = ButtonVariant.Default,
                            size = InputGroupButtonSize.IconSm,
                        ) {
                            Text(textStyle = TextStyle(color = currentTheme.colors.primaryForeground)) { "➤" }
                        }
                    }
                }
            }
        }
    }

    @Test
    fun chat_panel_layout_light() = chatPanelLayout(darkTheme = false)

    @Test
    fun chat_panel_layout_dark() = chatPanelLayout(darkTheme = true)

    @androidx.compose.runtime.Composable
    private fun previewComposer(
        darkTheme: Boolean,
        value: String,
    ) {
        InputGroup(modifier = Modifier.width(250.dp).testTag("preview-composer")) {
            InputGroupAddon(InputGroupAddonAlign.InlineStart) {
                InputGroupButton(onClick = {}, size = InputGroupButtonSize.IconSm) {
                    Text { "+" }
                }
            }
            Input(
                modifier = Modifier.testTag("preview-field"),
                value = value,
                onValueChange = {},
                singleLine = false,
            )
            InputGroupAddon(InputGroupAddonAlign.InlineEnd) {
                InputGroupButton(
                    onClick = {},
                    variant = ButtonVariant.Default,
                    size = InputGroupButtonSize.IconSm,
                    style = Style { shape(CircleShape) },
                ) {
                    Text(textStyle = TextStyle(color = currentTheme.colors.primaryForeground)) { "➤" }
                }
            }
        }
    }

    private fun previewTwoLineFocused(darkTheme: Boolean) {
        snapshotFocused("preview_composer_two_line_focused", focusTag = "preview-field", darkTheme = darkTheme) {
            previewComposer(darkTheme, "this is two line of words")
        }
    }

    @Test
    fun preview_composer_two_line_focused_light() = previewTwoLineFocused(darkTheme = false)

    private fun previewSingleLineFocused(darkTheme: Boolean) {
        snapshotFocused("preview_composer_single_line_focused", focusTag = "preview-field", darkTheme = darkTheme) {
            previewComposer(darkTheme, "this is single line")
        }
    }

    @Test
    fun preview_composer_single_line_focused_light() = previewSingleLineFocused(darkTheme = false)

    /**
     * Regression guard: addon gap must hold when the field value fills its slot
     * (group-owned `spacing.xs` between addon padding and control).
     */
    private fun longValue(darkTheme: Boolean) {
        snapshot("input_group_long_value", darkTheme = darkTheme) {
            Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
                InputGroup(modifier = Modifier.width(260.dp)) {
                    InputGroupAddon(InputGroupAddonAlign.InlineStart) {
                        InputGroupText("$")
                    }
                    Input(value = "123456789012345678901234", onValueChange = {})
                }
                InputGroup(modifier = Modifier.width(260.dp)) {
                    Input(value = "areallylongsubdomainname", onValueChange = {})
                    InputGroupAddon(InputGroupAddonAlign.InlineEnd) {
                        InputGroupText(".com")
                    }
                }
            }
        }
    }

    @Test
    fun long_value_light() = longValue(darkTheme = false)

    @Test
    fun long_value_dark() = longValue(darkTheme = true)

    /**
     * Focusing the inner field must ring/highlight the *group container* (real shadcn's
     * `has-[:focus-visible]:ring-[3px]` on `InputGroup`), with no inner border or ring
     * on the field itself.
     */
    private fun focused(darkTheme: Boolean) {
        snapshotFocused("input_group_focused", focusTag = "focused-input", darkTheme = darkTheme) {
            InputGroup(modifier = Modifier.width(260.dp).testTag("ig")) {
                InputGroupAddon(InputGroupAddonAlign.InlineStart) {
                    InputGroupText("$")
                }
                Input(value = "0.00", onValueChange = {}, modifier = Modifier.testTag("focused-input"))
            }
        }
    }

    @Test
    fun focused_light() = focused(darkTheme = false)

    @Test
    fun focused_dark() = focused(darkTheme = true)

    /**
     * Chat-composer layout: [InputGroupAddonAlign.BlockEnd] puts a full-width toolbar
     * below a growing multi-line field.
     */
    private fun bottomBar(darkTheme: Boolean) {
        snapshot("input_group_bottom_bar", darkTheme = darkTheme) {
            InputGroup(modifier = Modifier.width(280.dp)) {
                Input(
                    value = "I'm building a chat for our app and the scroll behavior is driving me nuts.",
                    onValueChange = {},
                    singleLine = false,
                )
                InputGroupAddon(InputGroupAddonAlign.BlockEnd) {
                    InputGroupButton(
                        onClick = {},
                        size = InputGroupButtonSize.IconSm,
                        style = Style { shape(CircleShape) },
                    ) {
                        Text { "+" }
                    }
                    Spacer(Modifier.weight(1f))
                    InputGroupButton(
                        onClick = {},
                        variant = ButtonVariant.Default,
                        size = InputGroupButtonSize.IconSm,
                        style = Style { shape(CircleShape) },
                    ) {
                        Text(textStyle = TextStyle(color = currentTheme.colors.primaryForeground)) { "↑" }
                    }
                }
            }
        }
    }

    @Test
    fun bottom_bar_light() = bottomBar(darkTheme = false)

    @Test
    fun bottom_bar_dark() = bottomBar(darkTheme = true)
}
