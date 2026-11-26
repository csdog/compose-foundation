package com.toutiaoyan.compose.ui.components.navigationmenu

import androidx.compose.foundation.layout.Column
import androidx.compose.runtime.key
import androidx.compose.runtime.mutableStateOf
import androidx.compose.ui.Modifier
import androidx.compose.ui.input.key.Key
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.test.assertIsFocused
import androidx.compose.ui.test.onNodeWithTag
import androidx.compose.ui.test.onNodeWithText
import androidx.compose.ui.test.performKeyInput
import androidx.compose.ui.test.pressKey
import androidx.compose.ui.test.requestFocus
import androidx.compose.ui.test.withKeyDown
import com.toutiaoyan.compose.ui.ScreenshotTest
import com.toutiaoyan.compose.ui.tokens.StylePreset
import kotlin.test.Test

class NavigationMenuPanelFocusTest : ScreenshotTest() {
    @Test
    fun reorderedPanelLinksUseTheVisibleTabBoundaries() {
        val order = mutableStateOf(listOf("One", "Two"))
        setThemedContent(stylePreset = StylePreset.Nova) {
            NavigationMenu {
                NavigationMenuList {
                    NavigationMenuItem("first") {
                        NavigationMenuTrigger("First", Modifier.testTag("first"))
                        NavigationMenuContent {
                            Column {
                                order.value.forEach { label ->
                                    key(label) { NavigationMenuLink(label, {}, Modifier.testTag(label)) }
                                }
                            }
                        }
                    }
                    NavigationMenuItem("second") { NavigationMenuLink("Second", {}) }
                }
            }
        }
        composeRule.onNodeWithTag("first").requestFocus().performKeyInput { pressKey(Key.DirectionDown) }
        val one = composeRule.onNodeWithTag("One").assertIsFocused()
        composeRule.runOnIdle { order.value = order.value.reversed() }
        one.performKeyInput { withKeyDown(Key.ShiftLeft) { pressKey(Key.Tab) } }
        composeRule.onNodeWithTag("Two").assertIsFocused()
        composeRule.onNodeWithTag("Two").performKeyInput { pressKey(Key.Tab) }
        one.assertIsFocused().performKeyInput { pressKey(Key.Tab) }
        composeRule.onNodeWithText("Second").assertIsFocused()
    }
}
