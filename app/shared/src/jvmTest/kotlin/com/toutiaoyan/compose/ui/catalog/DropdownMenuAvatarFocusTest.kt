package com.toutiaoyan.compose.ui.catalog

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.toPixelMap
import androidx.compose.ui.input.InputMode
import androidx.compose.ui.input.InputModeManager
import androidx.compose.ui.input.key.Key
import androidx.compose.ui.platform.LocalInputModeManager
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.semantics.SemanticsProperties
import androidx.compose.ui.test.*
import androidx.compose.ui.test.junit4.v2.createComposeRule
import androidx.compose.ui.unit.dp
import com.toutiaoyan.compose.ui.catalog.docs.dropdownMenuDoc
import com.toutiaoyan.compose.ui.components.button.Button
import com.toutiaoyan.compose.ui.components.overlay.OverlayHost
import com.toutiaoyan.compose.ui.theme.Theme
import com.toutiaoyan.compose.ui.tokens.StylePreset
import org.junit.Rule
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertTrue

class DropdownMenuAvatarFocusTest {
    @get:Rule val rule = createComposeRule()
    private lateinit var inputMode: InputModeManager

    private fun render() {
        rule.setContent {
            inputMode = LocalInputModeManager.current
            Theme(preset = StylePreset.Nova, minTouchTarget = null) {
                OverlayHost {
                    Column(
                        Modifier.width(240.dp).height(240.dp).testTag("surface")
                            .background(Color.White).padding(24.dp),
                        verticalArrangement = Arrangement.spacedBy(24.dp),
                    ) {
                        Button("Before", onClick = {})
                        dropdownMenuDoc.examples.single { it.title == "Avatar" }.preview()
                        Button("After", onClick = {})
                    }
                }
            }
        }
    }

    private fun trigger() = rule.onNodeWithContentDescription("Open account menu")

    @Test fun avatarHasOneNamedUnmergedClickOwner() {
        render()
        val avatarActions = hasClickAction() and !hasContentDescription("Before") and !hasContentDescription("After")
        rule.onAllNodes(avatarActions, useUnmergedTree = true).assertCountEquals(1)
        rule.onNode(avatarActions, useUnmergedTree = true).assert(
            hasContentDescription("Open account menu") and
                SemanticsMatcher.expectValue(SemanticsProperties.Role, Role.Button),
        ).assertWidthIsEqualTo(32.dp).assertHeightIsEqualTo(32.dp)
    }

    @Test fun realTabVisitsAvatarOnceAndPaintsACircularKeyboardRing() {
        render()
        rule.onNodeWithText("Before").requestFocus()
        rule.runOnIdle { inputMode.requestInputMode(InputMode.Touch) }
        rule.onNodeWithText("Before").performKeyInput { pressKey(Key.Tab) }
        trigger().assertIsFocused()
        assertEquals(InputMode.Keyboard, inputMode.inputMode)
        val bounds = trigger().fetchSemanticsNode().boundsInRoot
        val surface = rule.onNodeWithTag("surface")
        val origin = surface.fetchSemanticsNode().boundsInRoot.topLeft
        val pixels = surface.captureToImage().toPixelMap()
        val outside = with(rule.density) { 2.dp.roundToPx() }
        val x = (bounds.left - origin.x).toInt() - outside
        val y = (bounds.center.y - origin.y).toInt()
        val cornerY = (bounds.top - origin.y).toInt() - outside
        val ring = pixels[x, y]
        assertTrue(ring != Color.White, "Real Tab must paint the ring outside the avatar: $ring")
        assertEquals(Color.White, pixels[x, cornerY], "Circular ring must not fill the square corner")
        rule.runOnIdle { inputMode.requestInputMode(InputMode.Touch) }
        trigger().assertIsFocused()
        assertEquals(Color.White, surface.captureToImage().toPixelMap()[x, y])
        println("Avatar ring pixel=($x,$y) keyboard=$ring touch=white; circular corner=white; bounds=$bounds")
        trigger().performKeyInput { pressKey(Key.Tab) }
        rule.onNodeWithText("After").assertIsFocused().performKeyInput {
            keyDown(Key.ShiftLeft)
            pressKey(Key.Tab)
            keyUp(Key.ShiftLeft)
        }
        trigger().assertIsFocused().performKeyInput {
            keyDown(Key.ShiftLeft)
            pressKey(Key.Tab)
            keyUp(Key.ShiftLeft)
        }
        rule.onNodeWithText("Before").assertIsFocused()
    }

    @Test fun avatarOpeningKeysFocusRowsAndEscapeRestoresTheSingleOwner() {
        render()
        rule.onNodeWithText("Before").requestFocus().performKeyInput { pressKey(Key.Tab) }
        for (key in listOf(Key.Enter, Key.Spacebar, Key.DirectionDown, Key.DirectionUp)) {
            trigger().assertIsFocused().performKeyInput { pressKey(key) }
            val row = rule.onNodeWithText(if (key == Key.DirectionUp) "Sign Out" else "Account")
            row.assertIsFocused().performKeyInput { pressKey(Key.Escape) }
            rule.onNodeWithText("Account").assertDoesNotExist()
            trigger().assertIsFocused()
        }
    }

    @Test fun pointerAndEveryAccountActionStillOpenAndDismiss() {
        render()
        for (label in listOf("Account", "Billing", "Notifications", "Sign Out")) {
            trigger().performClick()
            rule.onNodeWithText("Account").assertIsNotFocused()
            rule.onNodeWithText(label).assertIsDisplayed().performClick()
            rule.onNodeWithText("Account").assertDoesNotExist()
            trigger().assertIsFocused()
        }
    }
}
