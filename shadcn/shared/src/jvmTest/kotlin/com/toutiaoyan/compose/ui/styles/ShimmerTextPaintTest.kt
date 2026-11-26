@file:OptIn(androidx.compose.foundation.style.ExperimentalFoundationStyleApi::class)

package com.toutiaoyan.compose.ui.styles

import androidx.compose.foundation.text.selection.SelectionContainer
import androidx.compose.ui.graphics.toAwtImage
import java.io.File
import javax.imageio.ImageIO
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.style.Style
import androidx.compose.foundation.style.styleable
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.toPixelMap
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.test.*
import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.text.TextStyle
import com.toutiaoyan.compose.ui.ScreenshotTest
import com.toutiaoyan.compose.ui.components.text.Text
import com.toutiaoyan.compose.ui.components.text.TextColors
import kotlin.test.*

class ShimmerTextPaintTest : ScreenshotTest() {
    private fun pixels() = composeRule.onNodeWithTag("text", useUnmergedTree = true).captureToImage().toPixelMap()

    private fun bytes(): List<Color> {
        val image = pixels()
        return (0 until image.height).flatMap { y -> (0 until image.width).map { x -> image[x, y] } }
    }

    private val blueStyle = Style { contentBrush(Brush.linearGradient(listOf(Color.Blue, Color.Blue))) }

    private fun capture(name: String) {
        val file = File("build/reports/shimmer-r1/$name.png")
        file.parentFile.mkdirs()
        ImageIO.write(
            composeRule.onNodeWithTag("text", useUnmergedTree = true).captureToImage().toAwtImage(),
            "png",
            file,
        )
    }

    private fun styleMotion(dark: Boolean) {
        composeRule.mainClock.autoAdvance = false
        setThemedContent(darkTheme = dark) {
            Text(Modifier.testTag("text"), textStyle = TextColors.Muted, style = rememberShimmerStyle()) {
                "Generating response…"
            }
        }
        composeRule.mainClock.advanceTimeBy(64)
        val start = bytes()
        capture("style-start-${if (dark) "dark" else "light"}")
        composeRule.mainClock.advanceTimeBy(960)
        val middle = bytes()
        capture("style-middle-${if (dark) "dark" else "light"}")
        assertTrue(start.zip(middle).count { it.first != it.second } > 20, "Style shimmer must repaint actual glyphs")
    }

    @Test fun styleShimmerActuallyChangesGlyphPixels() = styleMotion(false)

    @Test fun darkStyleShimmerActuallyChangesGlyphPixels() = styleMotion(true)

    @Test fun contentBrushOverridesSpecifiedPlainTextColor() {
        setThemedContent {
            Text(Modifier.testTag("text"), textStyle = TextStyle(color = Color.Red), style = blueStyle) { "MMMM" }
        }
        assertTrue(bytes().any { it.blue > .9f && it.red < .1f }, "Specified text color must not hide Style brush")
    }

    @Test fun contentBrushAppliesToAnnotatedText() {
        setThemedContent {
            Text(Modifier.testTag("text"), textStyle = TextStyle(color = Color.Red), style = blueStyle) {
                AnnotatedString("MMMM")
            }
        }
        assertTrue(bytes().any { it.blue > .9f && it.red < .1f }, "Annotated text must use Style brush")
    }

    @Test fun ancestorBrushIsInheritedWithoutTintingWhitespace() {
        setThemedContent {
            Box(Modifier.styleable(style = blueStyle)) {
                Text(Modifier.testTag("text")) { "M   M" }
            }
        }
        val image = pixels()
        assertTrue(bytes().any { it.blue > .9f && it.red < .1f })
        assertEquals(Color.White, image[image.width / 2, image.height / 2])
    }

    @Test fun translucentBrushDoesNotMultiplyOriginalTextAlphaTwice() {
        val halfBlue = Color.Blue.copy(alpha = .5f)
        setThemedContent {
            Text(
                Modifier.testTag("text"),
                textStyle = TextStyle(color = Color.Red.copy(alpha = .5f)),
                style = Style { contentBrush(Brush.linearGradient(listOf(halfBlue, halfBlue))) },
            ) { "MMMM" }
        }
        val darkest = bytes().minBy { it.red }
        assertEquals(.5f, darkest.red, .02f)
        assertEquals(.5f, darkest.green, .02f)
        assertEquals(1f, darkest.blue)
    }

    @Test fun styleAnimationRepaintsWithoutRecomposingCaller() {
        composeRule.mainClock.autoAdvance = false
        var compositions = 0
        setThemedContent {
            val shimmer = rememberShimmerStyle()
            SideEffect { compositions++ }
            Text(Modifier.testTag("text"), textStyle = TextColors.Muted, style = shimmer) { "Generating response…" }
        }
        composeRule.mainClock.advanceTimeBy(64)
        val start = bytes()
        var baseline = 0
        composeRule.runOnIdle { baseline = compositions }
        composeRule.mainClock.advanceTimeBy(960)
        assertTrue(start != bytes())
        composeRule.runOnIdle { assertEquals(baseline, compositions, "Style frames must stay out of composition") }
    }

    @Test fun modifierAnimationRepaintsWithoutRecomposingCaller() {
        composeRule.mainClock.autoAdvance = false
        var compositions = 0
        setThemedContent {
            val modifier = Modifier.shimmer()
            SideEffect { compositions++ }
            Text(modifier.testTag("text"), textStyle = TextColors.Muted) { "Generating response…" }
        }
        composeRule.mainClock.advanceTimeBy(64)
        val start = bytes()
        var baseline = 0
        composeRule.runOnIdle { baseline = compositions }
        composeRule.mainClock.advanceTimeBy(960)
        assertTrue(start != bytes())
        composeRule.runOnIdle {
            assertEquals(
                baseline,
                compositions,
                "Modifier frames must stay out of caller composition",
            )
        }
    }

    @Test fun explicitTextColorStillOverridesOwnStyleSolidColor() {
        setThemedContent {
            Text(
                Modifier.testTag("text"),
                textStyle = TextStyle(color = Color.Green),
                style = Style { contentColor(Color.Red) },
            ) { "MMMM" }
        }
        assertTrue(bytes().any { it.green > .9f && it.red < .1f && it.blue < .1f })
    }

    @Test fun brushRemovalRestoresSpecifiedSolidColorAndDisabledAnimationStops() {
        composeRule.mainClock.autoAdvance = false
        var enabled by mutableStateOf(true)
        setThemedContent {
            Text(
                Modifier.testTag("text"),
                textStyle = TextStyle(color = Color.Red),
                style = rememberShimmerStyle(enabled = enabled, baseColor = Color.Red, color = Color.Blue),
            ) { "Generating response…" }
        }
        composeRule.mainClock.advanceTimeBy(1024)
        assertTrue(bytes().any { it.blue > .5f && it.red < .5f })
        composeRule.runOnIdle { enabled = false }
        composeRule.mainClock.advanceTimeBy(32)
        val disabled = bytes()
        assertTrue(disabled.any { it.red > .9f && it.green < .1f && it.blue < .1f })
        composeRule.mainClock.advanceTimeBy(2048)
        assertEquals(disabled, bytes())
        capture("disabled-restored-red")
    }

    @Test fun onceStyleChangesPixelsThenKeepsItsFinalFrame() {
        composeRule.mainClock.autoAdvance = false
        setThemedContent {
            Text(
                Modifier.testTag("text"),
                textStyle = TextColors.Muted,
                style = rememberShimmerStyle(once = true, durationMillis = 1100),
            ) { "Generating response…" }
        }
        composeRule.mainClock.advanceTimeBy(64)
        val initial = bytes()
        composeRule.mainClock.advanceTimeBy(512)
        assertTrue(initial != bytes())
        composeRule.mainClock.advanceTimeBy(1200)
        val end = bytes()
        composeRule.mainClock.advanceTimeBy(1500)
        assertEquals(end, bytes())
        capture("once-settled")
    }

    @Test fun selectionContainerStillPaintsStyleBrush() {
        setThemedContent {
            SelectionContainer {
                Text(Modifier.testTag("text"), textStyle = TextStyle(color = Color.Red), style = blueStyle) { "MMMM" }
            }
        }
        assertTrue(bytes().any { it.blue > .9f && it.red < .1f })
    }
}
