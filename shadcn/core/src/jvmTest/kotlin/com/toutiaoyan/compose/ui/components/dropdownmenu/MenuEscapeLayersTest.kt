package com.toutiaoyan.compose.ui.components.dropdownmenu

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.size
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.input.key.Key
import androidx.compose.ui.test.*
import androidx.compose.ui.test.junit4.v2.createComposeRule
import androidx.compose.ui.unit.dp
import com.toutiaoyan.compose.ui.components.contextmenu.*
import com.toutiaoyan.compose.ui.components.overlay.OverlayHost
import com.toutiaoyan.compose.ui.theme.Theme
import com.toutiaoyan.compose.ui.tokens.StylePreset
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import org.junit.runners.Parameterized

@RunWith(Parameterized::class)
class MenuEscapeLayersTest(private val kind: String) {
    @get:Rule val rule = createComposeRule()

    @Test fun escapeClosesOnlyTheDeepestOpenLayerAndRestoresEachTrigger() {
        rule.setContent {
            Theme(preset = StylePreset.Nova, minTouchTarget = null) {
                OverlayHost {
                    Box(Modifier.size(560.dp, 400.dp), contentAlignment = Alignment.Center) {
                        if (kind == "dropdown") {
                            DropdownMenu {
                                DropdownMenuTrigger("Open")
                                DropdownMenuContent { NestedMenu() }
                            }
                        } else {
                            ContextMenu(modal = kind == "context-modal") {
                                ContextMenuTrigger("Area")
                                ContextMenuContent { NestedMenu() }
                            }
                        }
                    }
                }
            }
        }
        if (kind == "dropdown") {
            rule.onNodeWithText("Open").requestFocus().performKeyInput {
                pressKey(Key.DirectionDown)
            }
        } else {
            rule.onNodeWithText("Area").requestFocus().performKeyInput {
                keyDown(Key.ShiftLeft)
                pressKey(Key.F10)
                keyUp(Key.ShiftLeft)
            }
        }
        // Open the layer stack through its keyboard path: this test owns Escape/focus
        // restoration, while the catalog suite independently covers pointer-open behavior.
        rule.onNodeWithText("Level one").assertIsFocused().performKeyInput {
            pressKey(Key.DirectionRight)
        }
        rule.onNodeWithText("Level two").assertIsFocused().performKeyInput {
            pressKey(Key.DirectionRight)
        }
        rule.onNodeWithText("Leaf").assertIsFocused().performKeyInput { pressKey(Key.Escape) }
        rule.onNodeWithText("Leaf").assertDoesNotExist()
        rule.onNodeWithText("Level two").assertIsFocused().performKeyInput { pressKey(Key.Escape) }
        rule.onNodeWithText("Level two").assertDoesNotExist()
        rule.onNodeWithText("Level one").assertIsFocused().performKeyInput { pressKey(Key.Escape) }
        rule.onNodeWithText("Level one").assertDoesNotExist()
        rule.onNodeWithText(if (kind == "dropdown") "Open" else "Area").assertIsFocused()
    }

    companion object {
        @JvmStatic
        @Parameterized.Parameters(name = "{0}")
        fun cases() = listOf("dropdown", "context-modal", "context-nonmodal")
    }
}

@Composable
private fun DropdownMenuScope.NestedMenu() {
    DropdownMenuSub {
        DropdownMenuSubTrigger("Level one")
        DropdownMenuSubContent {
            DropdownMenuSub {
                DropdownMenuSubTrigger("Level two")
                DropdownMenuSubContent { DropdownMenuItem("Leaf", {}) }
            }
        }
    }
}
