package com.toutiaoyan.compose.ui.components.sidebar

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.size
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.toPixelMap
import androidx.compose.ui.input.key.Key
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.semantics.SemanticsActions
import androidx.compose.ui.test.assertIsFocused
import androidx.compose.ui.test.captureToImage
import androidx.compose.ui.test.onNodeWithTag
import androidx.compose.ui.test.performClick
import androidx.compose.ui.test.performKeyInput
import androidx.compose.ui.test.performMouseInput
import androidx.compose.ui.test.performSemanticsAction
import androidx.compose.ui.test.pressKey
import androidx.compose.ui.test.withKeyDown
import androidx.compose.ui.unit.dp
import com.toutiaoyan.compose.ui.ScreenshotTest
import com.toutiaoyan.compose.ui.components.button.Button
import com.toutiaoyan.compose.ui.tokens.StylePreset
import org.junit.Test
import kotlin.test.assertEquals
import kotlin.test.assertFalse
import kotlin.test.assertTrue

class SidebarMenuActionVisibilityTest : ScreenshotTest() {
    @Test fun desktopRevealRetainsFocusabilityAndMobilePaintsAtRest() {
        var mobile by mutableStateOf(false)
        var expanded by mutableStateOf(false)
        var clicks = 0
        setThemedContent(stylePreset = StylePreset.Nova) {
            SidebarProvider(mobile = mobile, modifier = Modifier.size(320.dp, 220.dp)) {
                SidebarInset {
                    Button("Outside", modifier = Modifier.testTag("outside"))
                    SidebarMenu {
                        SidebarMenuItem {
                            SidebarMenuButton("Project", onClick = {}, modifier = Modifier.testTag("project"))
                            SidebarMenuAction(
                                onClick = { clicks++ },
                                modifier = Modifier.testTag("action"),
                                showOnHover = true,
                                expanded = expanded,
                            ) {
                                Box(Modifier.size(8.dp).background(Color.Red))
                            }
                        }
                    }
                }
            }
        }

        val action = composeRule.onNodeWithTag("action")

        fun actionPainted(): Boolean {
            val pixels = action.captureToImage().toPixelMap()
            return (0 until pixels.height).any { y -> (0 until pixels.width).any { x -> pixels[x, y] == Color.Red } }
        }

        assertFalse(actionPainted())
        action.performClick()
        assertEquals(1, clicks)

        val outside = composeRule.onNodeWithTag("outside")
        outside.performMouseInput { moveTo(center) }
        val project = composeRule.onNodeWithTag("project")
        project.performSemanticsAction(SemanticsActions.RequestFocus) { it() }
        project.assertIsFocused()
        assertTrue(actionPainted())
        project.performKeyInput { pressKey(Key.Tab) }
        action.assertIsFocused()

        action.performKeyInput { withKeyDown(Key.ShiftLeft) { pressKey(Key.Tab) } }
        project.assertIsFocused().performKeyInput { withKeyDown(Key.ShiftLeft) { pressKey(Key.Tab) } }
        outside.assertIsFocused()
        assertFalse(actionPainted())

        project.performMouseInput { moveTo(center) }
        outside.assertIsFocused()
        assertTrue(actionPainted())
        outside.performMouseInput { moveTo(center) }
        assertFalse(actionPainted())
        composeRule.runOnIdle { expanded = true }
        assertTrue(actionPainted())

        composeRule.runOnIdle {
            expanded = false
            mobile = true
        }
        assertTrue(actionPainted())
    }
}
