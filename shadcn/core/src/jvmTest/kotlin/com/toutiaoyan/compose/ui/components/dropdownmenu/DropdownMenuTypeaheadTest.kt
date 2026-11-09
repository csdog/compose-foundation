package com.toutiaoyan.compose.ui.components.dropdownmenu

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.size
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.input.key.Key
import androidx.compose.ui.test.*
import androidx.compose.ui.test.junit4.v2.createComposeRule
import androidx.compose.ui.unit.dp
import com.toutiaoyan.compose.ui.components.overlay.OverlayHost
import com.toutiaoyan.compose.ui.theme.Theme
import com.toutiaoyan.compose.ui.tokens.StylePreset
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import org.junit.runners.Parameterized

@RunWith(Parameterized::class)
class DropdownMenuTypeaheadTest(private val kind: String) {
    @get:Rule val rule = createComposeRule()

    @Test fun explicitTextValueControlsKeyboardSearch() {
        rule.setContent {
            Theme(preset = StylePreset.Nova, minTouchTarget = null) {
                OverlayHost {
                    Box(Modifier.size(560.dp, 400.dp), contentAlignment = Alignment.Center) {
                        DropdownMenu {
                            DropdownMenuTrigger("Open")
                            DropdownMenuContent {
                                DropdownMenuItem("First", {})
                                when (kind) {
                                    "action" -> DropdownMenuItem("Visible label", {}, textValue = "Zebra")
                                    "checkbox" ->
                                        DropdownMenuCheckboxItem(
                                            "Visible label",
                                            checked = false,
                                            textValue = "Zebra",
                                            onCheckedChange = {},
                                        )
                                    "radio" ->
                                        DropdownMenuRadioGroup("", {}) {
                                            DropdownMenuRadioItem("target", "Visible label", textValue = "Zebra")
                                        }
                                    "submenu" ->
                                        DropdownMenuSub {
                                            DropdownMenuSubTrigger("Visible label", textValue = "Zebra")
                                            DropdownMenuSubContent { DropdownMenuItem("Child", {}) }
                                        }
                                }
                            }
                        }
                    }
                }
            }
        }
        rule.onNodeWithText("Open").requestFocus().performKeyInput { pressKey(Key.DirectionDown) }
        rule.onNodeWithText("First").assertIsFocused().performKeyInput { pressKey(Key.Z) }
        rule.onNodeWithText("Visible label").assertIsFocused()
    }

    companion object {
        @JvmStatic
        @Parameterized.Parameters(name = "{0}")
        fun cases() = listOf("action", "checkbox", "radio", "submenu")
    }
}
