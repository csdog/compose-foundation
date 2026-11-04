@file:OptIn(androidx.compose.foundation.style.ExperimentalFoundationStyleApi::class)

package com.toutiaoyan.compose.ui.components.kbd

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.style.Style
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.*
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.semantics.SemanticsActions
import androidx.compose.ui.test.*
import androidx.compose.ui.text.TextLayoutResult
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.toutiaoyan.compose.ui.ScreenshotTest
import com.toutiaoyan.compose.ui.components.button.*
import com.toutiaoyan.compose.ui.components.icon.Icon
import com.toutiaoyan.compose.ui.components.text.Text
import com.toutiaoyan.compose.ui.components.tooltip.*
import com.toutiaoyan.compose.ui.theme.Theme
import com.toutiaoyan.compose.ui.theme.currentTheme
import com.toutiaoyan.compose.ui.tokens.StylePreset
import kotlin.test.*

class KbdParityTest : ScreenshotTest() {
    private fun layout(text: String): TextLayoutResult {
        val results = mutableListOf<TextLayoutResult>()
        composeRule.onNodeWithText(
            text,
            useUnmergedTree =
            true,
        ).performSemanticsAction(SemanticsActions.GetTextLayoutResult) { it(results) }
        return results.single()
    }

    @Test fun novaUsesTwentyPixelCapsFourPixelGapAndTwelvePixelText() {
        setThemedContent(stylePreset = StylePreset.Nova) {
            KbdGroup {
                Kbd("K", Modifier.testTag("first"))
                Kbd("B", Modifier.testTag("second"))
                Kbd("Ctrl", Modifier.testTag("long"))
            }
        }
        val first =
            composeRule.onNodeWithTag(
                "first",
            ).assertWidthIsEqualTo(20.dp).assertHeightIsEqualTo(20.dp).fetchSemanticsNode().boundsInRoot
        val second = composeRule.onNodeWithTag("second").fetchSemanticsNode().boundsInRoot
        assertEquals(4f, second.left - first.right, 0.1f)
        assertEquals(12f, layout("K").layoutInput.style.fontSize.value)
        assertEquals(16f, layout("K").layoutInput.style.lineHeight.value)
        assertEquals(FontWeight.Medium, layout("K").layoutInput.style.fontWeight)
        val label = composeRule.onNodeWithText("K").fetchSemanticsNode().boundsInRoot
        assertEquals(first.center.x, label.center.x, 0.5f)
        val long = composeRule.onNodeWithTag("long").fetchSemanticsNode().boundsInRoot
        val longLabel = composeRule.onNodeWithText("Ctrl").fetchSemanticsNode().boundsInRoot
        assertEquals(4f, longLabel.left - long.left, 0.1f)
        assertEquals(4f, long.right - longLabel.right, 0.1f)
    }

    @Test fun groupTextIsMonospaceButIndividualKeysUseTheThemeSansFamily() {
        setThemedContent(stylePreset = StylePreset.Nova) {
            KbdGroup {
                Kbd("Ctrl")
                Text { "+" }
                Kbd("B")
            }
        }
        assertEquals(FontFamily.Monospace, layout("+").layoutInput.style.fontFamily)
        assertEquals(FontFamily.Default, layout("Ctrl").layoutInput.style.fontFamily)
    }

    @Test fun contentSlotProvidesTwelvePixelIconsAndFourPixelInnerSpacing() {
        val vector = ImageVector.Builder("modifier", 24.dp, 24.dp, 24f, 24f).build()
        setThemedContent(stylePreset = StylePreset.Nova) {
            Kbd(modifier = Modifier.testTag("key"), content = {
                Icon(vector, contentDescription = "first")
                Icon(vector, contentDescription = "second")
            })
        }
        val first =
            composeRule.onNodeWithContentDescription(
                "first",
            ).assertWidthIsEqualTo(12.dp).assertHeightIsEqualTo(12.dp).fetchSemanticsNode().boundsInRoot
        val second = composeRule.onNodeWithContentDescription("second").fetchSemanticsNode().boundsInRoot
        assertEquals(4f, second.left - first.right, 0.1f)
        composeRule.onNodeWithTag("key").assertWidthIsEqualTo(36.dp).assertHeightIsEqualTo(20.dp)
    }

    @Test fun existingStringLambdaKeepsReturningItsUpdatedLabel() {
        var text by mutableStateOf("Ctrl")
        setThemedContent(stylePreset = StylePreset.Nova) { Kbd { text } }
        composeRule.onNodeWithText("Ctrl").assertIsDisplayed()
        composeRule.runOnIdle { text = "Alt" }
        composeRule.onNodeWithText("Alt").assertIsDisplayed()
        composeRule.onNodeWithText("Ctrl").assertDoesNotExist()
    }

    @Test fun keyInsideAButtonDoesNotConsumeTheButtonsPointerAction() {
        var clicks = 0
        setThemedContent(stylePreset = StylePreset.Nova) {
            Button(onClick = { clicks++ }) { Kbd("S") }
        }
        composeRule.onNodeWithText("S", useUnmergedTree = true)
            .assert(SemanticsMatcher.keyNotDefined(SemanticsActions.OnClick))
            .performTouchInput { click(center) }
        assertEquals(1, clicks)
    }

    @Test fun callerStyleOverridesTheKeycapShellAndRenderedForeground() {
        setThemedContent(stylePreset = StylePreset.Nova) {
            Kbd(
                "K",
                Modifier.testTag("key"),
                style =
                    Style {
                        background(Color.Green)
                        contentColor(Color.Red)
                        height(24.dp)
                        minWidth(28.dp)
                    },
            )
        }
        val image =
            composeRule.onNodeWithTag(
                "key",
            ).assertWidthIsEqualTo(28.dp).assertHeightIsEqualTo(24.dp).captureToImage().toPixelMap()
        assertEquals(Color.Green, image[image.width / 2, 1])
        assertTrue(
            (0 until image.height).any { y ->
                (0 until image.width).any { x ->
                    image[
                        x,
                        y,
                    ].red -
                        image[
                            x,
                            y,
                        ].green > 0.4f
                }
            },
        )
    }

    @Test fun actualTooltipScopeSwitchesItsKeycapColorsLiveWithTheTheme() {
        var dark by mutableStateOf(false)
        var foreground = Color.Unspecified
        var background = Color.Unspecified
        composeRule.setContent {
            Theme(preset = StylePreset.Nova, isDark = dark) {
                val colors = currentTheme.colors
                foreground = colors.background
                background = colors.background.copy(alpha = if (dark) 0.1f else 0.2f).compositeOver(colors.foreground)
                Tooltip(open = true) {
                    TooltipTrigger("Trigger")
                    TooltipContent(showArrow = false) { Kbd("K", Modifier.testTag("key")) }
                }
            }
        }

        fun check() {
            val image = composeRule.onNodeWithTag("key").captureToImage().toPixelMap()
            val actual = image[image.width / 2, 1]
            assertEquals(background.red, actual.red, 0.012f)
            assertEquals(background.green, actual.green, 0.012f)
            assertEquals(background.blue, actual.blue, 0.012f)
            assertEquals(foreground, layout("K").layoutInput.style.color)
        }
        check()
        composeRule.runOnIdle { dark = true }
        check()
    }

    private fun nova(dark: Boolean) {
        snapshot("kbd_nova", darkTheme = dark, stylePreset = StylePreset.Nova) {
            Column(
                horizontalAlignment = androidx.compose.ui.Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.spacedBy(16.dp),
            ) {
                KbdGroup {
                    Kbd("⌘")
                    Kbd("⇧")
                    Kbd("⌥")
                    Kbd("⌃")
                }
                KbdGroup {
                    Kbd("Ctrl")
                    Text { "+" }
                    Kbd("B")
                }
            }
        }
    }

    private fun tooltip(dark: Boolean) {
        snapshot("kbd_tooltip_nova", darkTheme = dark, stylePreset = StylePreset.Nova) {
            Tooltip(open = true) {
                TooltipTrigger("Trigger")
                TooltipContent(showArrow = false) {
                    Row(
                        horizontalArrangement =
                            Arrangement.spacedBy(6.dp),
                        verticalAlignment =
                            androidx.compose.ui.Alignment.CenterVertically,
                    ) {
                        Text(
                            textStyle =
                                androidx.compose.ui.text.TextStyle(
                                    color =
                                        currentTheme.colors.background,
                                ),
                        ) { "Save Changes" }
                        Kbd("S")
                    }
                }
            }
        }
    }

    @Test fun nova_light() = nova(false)

    @Test fun nova_dark() = nova(true)

    @Test fun tooltip_light() = tooltip(false)

    @Test fun tooltip_dark() = tooltip(true)
}
