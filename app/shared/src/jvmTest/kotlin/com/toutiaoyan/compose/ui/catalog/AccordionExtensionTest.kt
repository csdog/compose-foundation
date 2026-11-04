package com.toutiaoyan.compose.ui.catalog

import androidx.compose.foundation.layout.*
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.toAwtImage
import java.io.File
import javax.imageio.ImageIO
import kotlin.test.assertTrue
import androidx.compose.ui.input.InputMode
import androidx.compose.ui.input.InputModeManager
import androidx.compose.ui.input.key.Key
import androidx.compose.ui.platform.*
import androidx.compose.ui.test.*
import androidx.compose.ui.test.junit4.v2.createComposeRule
import androidx.compose.ui.unit.*
import com.toutiaoyan.compose.ui.catalog.docs.accordionDoc
import com.toutiaoyan.compose.ui.theme.Theme
import com.toutiaoyan.compose.ui.tokens.StylePreset
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import org.junit.runners.Parameterized

@RunWith(Parameterized::class)
class AccordionExtensionTest(private val viewport: Int, private val dark: Boolean, private val rtl: Boolean) {
    @get:Rule val rule = createComposeRule()
    private lateinit var input: InputModeManager

    companion object {
        @JvmStatic
        @Parameterized.Parameters(name = "viewport={0},dark={1},rtl={2}")
        fun modes() =
            listOf(
                1440,
                390,
            ).flatMap { width ->
                listOf(
                    false,
                    true,
                ).flatMap { dark ->
                    listOf(
                        false,
                        true,
                    ).map { rtl ->
                        arrayOf<Any>(
                            width,
                            dark,
                            rtl,
                        )
                    }
                }
            }
    }

    private fun show(title: String) {
        val example = accordionDoc.examples.single { it.title == "Kotlin extension: $title" }
        rule.setContent {
            input = LocalInputModeManager.current
            CompositionLocalProvider(
                LocalLayoutDirection provides
                    if (rtl) {
                        LayoutDirection.Rtl
                    } else {
                        LayoutDirection.Ltr
                    },
            ) {
                Theme(preset = StylePreset.Nova, isDark = dark, minTouchTarget = null) {
                    Box(
                        Modifier.width(
                            if (viewport == 390) {
                                260.dp
                            } else {
                                384.dp
                            },
                        ).testTag("extension-scene"),
                    ) { example.preview() }
                }
            }
        }
        rule.runOnIdle { input.requestInputMode(InputMode.Keyboard) }
    }

    @Test fun horizontalUsesTheLogicalArrowAxisAndEnterActivates() {
        show("Horizontal")
        val first = rule.onNodeWithText("Accessible?")
        val second = rule.onNodeWithText("Styled?")
        first.requestFocus()
        first.performKeyInput { pressKey(Key.DirectionDown) }
        first.assertIsFocused()
        first.performKeyInput { pressKey(if (rtl) Key.DirectionLeft else Key.DirectionRight) }
        second.assertIsFocused().performKeyInput { pressKey(Key.Enter) }
        rule.onNodeWithText("Yes. It comes with default styles.").assertIsDisplayed()
        rule.onNodeWithText("Yes. It adheres to WAI-ARIA.").assertDoesNotExist()
        val a = first.fetchSemanticsNode().boundsInRoot
        val b = second.fetchSemanticsNode().boundsInRoot
        assertTrue(b.top >= a.bottom, "Keyboard orientation preserves shadcn's vertically stacked default style")
        val file = File("build/reports/accordion-extension-r7", "horizontal-$viewport-$dark-$rtl.png")
        file.parentFile.mkdirs()
        ImageIO.write(rule.onNodeWithTag("extension-scene").captureToImage().toAwtImage(), "png", file)
    }

    @Test fun noLoopKeepsFocusAtBothEnds() {
        show("No loop")
        val first = rule.onNodeWithText("Accessible?")
        val second = rule.onNodeWithText("Styled?")
        first.requestFocus()
        first.performKeyInput { pressKey(Key.DirectionUp) }
        first.assertIsFocused()
        first.performKeyInput {
            pressKey(Key.MoveEnd)
            pressKey(Key.DirectionDown)
        }
        second.assertIsFocused()
    }

    @Test fun loopWrapsInBothDirections() {
        show("Loop")
        val first = rule.onNodeWithText("Accessible?")
        val second = rule.onNodeWithText("Styled?")
        first.requestFocus()
        first.performKeyInput { pressKey(Key.DirectionUp) }
        second.assertIsFocused().performKeyInput { pressKey(Key.DirectionDown) }
        first.assertIsFocused()
    }

    @Test fun notCollapsibleKeepsTheActiveAnswerButAllowsAnotherItem() {
        show("Not collapsible")
        rule.onNodeWithText("Is it accessible?").performClick()
        rule.waitForIdle()
        rule.onNodeWithText("Yes. It adheres to the WAI-ARIA design pattern.").assertIsDisplayed()
        rule.onNodeWithText("Is it styled?").performClick()
        rule.waitForIdle()
        rule.onNodeWithText("Yes. It comes with default styles.").assertIsDisplayed()
        rule.onNodeWithText("Yes. It adheres to the WAI-ARIA design pattern.").assertDoesNotExist()
    }
}
