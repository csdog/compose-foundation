package com.toutiaoyan.compose.ui.components.contextmenu

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.size
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.input.key.Key
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.test.*
import androidx.compose.ui.test.junit4.v2.createComposeRule
import androidx.compose.ui.unit.dp
import com.toutiaoyan.compose.ui.components.overlay.OverlayHost
import com.toutiaoyan.compose.ui.theme.Theme
import com.toutiaoyan.compose.ui.tokens.StylePreset
import org.junit.Rule
import org.junit.Test

/** Base UI `cn-context-menu-content` is `min-w-36 p-1`; recipes override it with `w-48`/`w-44`. */
class ContextMenuPanelTest {
    @get:Rule val rule = createComposeRule()

    @Test fun openPanelUsesTheOfficialMinWidthAndOneDpInset() {
        rule.setContent {
            Theme(preset = StylePreset.Nova, minTouchTarget = null) {
                OverlayHost {
                    Box(Modifier.size(320.dp, 200.dp), contentAlignment = Alignment.Center) {
                        ContextMenu {
                            ContextMenuTrigger("Area")
                            ContextMenuContent {
                                ContextMenuItem("Back", onClick = {}, modifier = Modifier.testTag("cm-item"))
                            }
                        }
                    }
                }
            }
        }
        rule.onNodeWithText("Area").requestFocus().performKeyInput {
            keyDown(Key.ShiftLeft)
            pressKey(Key.F10)
            keyUp(Key.ShiftLeft)
        }
        rule.waitForIdle()
        // 144dp panel minus 4dp padding on each side.
        rule.onNodeWithTag("cm-item").assertWidthIsEqualTo(136.dp)
        rule.onNodeWithText("Back").assertExists()
    }

    @Test fun wideRecipePanelKeepsTheSameFourDpInset() {
        rule.setContent {
            Theme(preset = StylePreset.Nova, minTouchTarget = null) {
                OverlayHost {
                    Box(Modifier.size(320.dp, 200.dp), contentAlignment = Alignment.Center) {
                        ContextMenu {
                            ContextMenuTrigger("Area")
                            ContextMenuContent(panelWidth = 192.dp) {
                                ContextMenuItem("Back", onClick = {}, modifier = Modifier.testTag("cm-item"))
                            }
                        }
                    }
                }
            }
        }
        rule.onNodeWithText("Area").requestFocus().performKeyInput {
            keyDown(Key.ShiftLeft)
            pressKey(Key.F10)
            keyUp(Key.ShiftLeft)
        }
        rule.waitForIdle()
        rule.onNodeWithTag("cm-item").assertWidthIsEqualTo(184.dp)
    }
}
