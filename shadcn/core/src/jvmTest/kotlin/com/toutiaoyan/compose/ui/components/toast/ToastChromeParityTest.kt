@file:OptIn(androidx.compose.foundation.style.ExperimentalFoundationStyleApi::class)

package com.toutiaoyan.compose.ui.components.toast

import androidx.compose.animation.EnterTransition
import androidx.compose.animation.ExitTransition
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.style.Style
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.toPixelMap
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.semantics.SemanticsActions
import androidx.compose.ui.test.*
import androidx.compose.ui.text.TextLayoutResult
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.toutiaoyan.compose.ui.ScreenshotTest
import com.toutiaoyan.compose.ui.theme.currentTheme
import com.toutiaoyan.compose.ui.tokens.StylePreset
import org.junit.runner.RunWith
import org.junit.runners.Parameterized
import kotlin.test.*

@RunWith(Parameterized::class)
class ToastChromeParityTest(private val dark: Boolean) : ScreenshotTest() {
    companion object {
        @JvmStatic
        @Parameterized.Parameters(name = "dark={0}")
        fun modes() = listOf(arrayOf(false), arrayOf(true))
    }

    @Test fun cardUsesPopoverFillAndSixteenPixelRoundCorners() {
        var fill = Color.Unspecified
        setThemedContent(darkTheme = dark, stylePreset = StylePreset.Nova) {
            fill = currentTheme.colors.popover
            Box(Modifier.width(384.dp).background(Color.Magenta).testTag("canvas")) {
                Toast(
                    ToastMessage(0, "Title", "Details", durationMillis = 0),
                    {},
                    Style,
                    EnterTransition.None,
                    ExitTransition.None,
                    closeButton = false,
                )
            }
        }
        val p = composeRule.onNodeWithTag("canvas").captureToImage().toPixelMap()
        assertEquals(fill, p[p.width / 2, 2])
        assertTrue(p[4, 4].green < .2f, "At 4,4 the 16dp corner must expose its magenta backing")
    }

    @Test fun defaultHasNoGlyphAndStatusAddsExactlySixteenPlusTwelve() {
        setThemedContent(darkTheme = dark, stylePreset = StylePreset.Nova) {
            Column(Modifier.width(384.dp)) {
                Toast(
                    ToastMessage(0, "Default title", durationMillis = 0),
                    {},
                    Style,
                    EnterTransition.None,
                    ExitTransition.None,
                    closeButton = false,
                )
                Toast(
                    ToastMessage(1, "Success title", variant = ToastVariant.Success, durationMillis = 0),
                    {},
                    Style,
                    EnterTransition.None,
                    ExitTransition.None,
                    closeButton = false,
                )
            }
        }
        val plain = composeRule.onNodeWithText("Default title").fetchSemanticsNode().boundsInRoot
        val status = composeRule.onNodeWithText("Success title").fetchSemanticsNode().boundsInRoot
        assertEquals(28f, status.left - plain.left, .1f)
        val result = mutableListOf<TextLayoutResult>()
        composeRule.onNodeWithText(
            "Default title",
        ).performSemanticsAction(SemanticsActions.GetTextLayoutResult) { it(result) }
        assertEquals(14f, result.single().layoutInput.style.fontSize.value)
        assertEquals(20f, result.single().layoutInput.style.lineHeight.value)
        assertEquals(FontWeight.Medium, result.single().layoutInput.style.fontWeight)
    }

    @Test fun errorUsesTheDestructiveGlyphInsteadOfAColoredDot() {
        var destructive = Color.Unspecified
        setThemedContent(darkTheme = dark, stylePreset = StylePreset.Nova) {
            destructive = currentTheme.colors.destructive
            Box(Modifier.width(384.dp).testTag("canvas")) {
                Toast(
                    ToastMessage(
                        0,
                        "Error title",
                        "Details",
                        variant =
                            ToastVariant.Error,
                        durationMillis =
                        0,
                    ),
                    {},
                    Style,
                    EnterTransition.None,
                    ExitTransition.None,
                    closeButton = false,
                )
            }
        }
        val p = composeRule.onNodeWithTag("canvas").captureToImage().toPixelMap()
        var found = false
        for (y in 25 until minOf(51, p.height)) for (x in 16 until 34) {
            val color = p[x, y]
            if (kotlin.math.abs(color.red - destructive.red) < .01f &&
                kotlin.math.abs(color.green - destructive.green) < .01f &&
                kotlin.math.abs(color.blue - destructive.blue) < .01f
            ) {
                found = true
            }
        }
        assertTrue(found, "The outlined error glyph must use destructive foreground")
    }
}
