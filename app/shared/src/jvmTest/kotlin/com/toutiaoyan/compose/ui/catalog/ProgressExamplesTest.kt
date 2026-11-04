package com.toutiaoyan.compose.ui.catalog

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.toAwtImage
import androidx.compose.ui.input.key.Key
import androidx.compose.ui.platform.*
import androidx.compose.ui.semantics.SemanticsProperties
import androidx.compose.ui.semantics.SemanticsActions
import androidx.compose.ui.test.*
import androidx.compose.ui.test.junit4.v2.createComposeRule
import androidx.compose.ui.unit.*
import com.toutiaoyan.compose.ui.catalog.docs.progressDoc
import com.toutiaoyan.compose.ui.theme.Theme
import com.toutiaoyan.compose.ui.theme.currentTheme
import com.toutiaoyan.compose.ui.tokens.StylePreset
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import org.junit.runners.Parameterized
import java.io.File
import javax.imageio.ImageIO
import kotlin.test.*

@RunWith(Parameterized::class)
class ProgressExamplesTest(private val viewport: Int, private val dark: Boolean) {
    @get:Rule val rule = createComposeRule()

    companion object {
        @JvmStatic
        @Parameterized.Parameters(name = "viewport={0},dark={1}")
        fun modes() =
            listOf(
                arrayOf<Any>(1440, false),
                arrayOf<Any>(1440, true),
                arrayOf<Any>(390, false),
                arrayOf<Any>(390, true),
            )
    }

    private fun advance(ms: Long = 400) {
        rule.mainClock.advanceTimeBy(ms)
        rule.waitForIdle()
    }

    private fun show(
        title: String,
        settle: Boolean = true,
    ) {
        rule.mainClock.autoAdvance = false
        rule.setContent {
            val original = LocalWindowInfo.current
            val density = LocalDensity.current
            val window =
                remember(original, density) {
                    object : WindowInfo by original {
                        override val containerSize =
                            with(density) { IntSize(viewport.dp.roundToPx(), 800.dp.roundToPx()) }
                    }
                }
            CompositionLocalProvider(LocalWindowInfo provides window) {
                Theme(preset = StylePreset.Nova, isDark = dark, minTouchTarget = null) {
                    Box(
                        Modifier.width((if (viewport >= 640) 640 else viewport).dp).height(360.dp)
                            .background(currentTheme.colors.background).padding(24.dp),
                        contentAlignment =
                            Alignment.Center,
                    ) {
                        progressDoc.examples.single { it.title == title }.preview()
                    }
                }
            }
        }
        if (settle) {
            advance()
        } else {
            rule.mainClock.advanceTimeByFrame()
            rule.waitForIdle()
        }
    }

    private fun fraction() =
        rule.onNodeWithTag("progress-recipe").fetchSemanticsNode()
            .config[SemanticsProperties.ProgressBarRangeInfo].current

    private fun capture(
        name: String,
        tag: String = "progress-recipe",
    ) {
        val file = File("build/reports/progress-recipes/$name-$viewport-${if (dark) "dark" else "light"}.png")
        file.parentFile.mkdirs()
        ImageIO.write(rule.onNodeWithTag(tag).captureToImage().toAwtImage(), "png", file)
    }

    @Test fun defaultStartsAtThirteenAndUpdatesToSixtySixAfterFiveHundredMilliseconds() {
        show("Default", settle = false)
        assertEquals(0.13f, fraction(), 0.001f)
        val expectedWidth = ((if (viewport >= 640) 640 else viewport) - 48) * 0.6f
        assertEquals(expectedWidth, rule.onNodeWithTag("progress-recipe").getUnclippedBoundsInRoot().width.value, 0.6f)
        rule.onNodeWithTag("progress-recipe").assertHeightIsEqualTo(4.dp)
        capture("default13")
        advance(450)
        assertEquals(0.13f, fraction(), 0.001f)
        advance(250)
        assertEquals(0.66f, fraction(), 0.001f)
        capture("default66")
    }

    @Test fun labelHasTheSourceLayoutAndCopyCodeIsStandalone() {
        assertEquals(listOf("Default", "Label", "Controlled", "RTL"), progressDoc.examples.take(4).map { it.title })
        progressDoc.examples.take(4).forEach {
            assertFalse(it.code.contains("RtlPreview"))
            assertFalse(it.code.contains("progressDoc"))
        }
        show("Label")
        rule.onNodeWithText("Upload progress", useUnmergedTree = true).assertIsDisplayed()
        rule.onNodeWithText("56%", useUnmergedTree = true).assertIsDisplayed()
        rule.onNodeWithTag("progress-recipe").assertHeightIsEqualTo(36.dp)
        val expectedWidth = minOf(384, (if (viewport >= 640) 640 else viewport) - 48)
        rule.onNodeWithTag("progress-recipe").assertWidthIsEqualTo(expectedWidth.dp)
        capture("label")
    }

    @Test fun sliderKeysAndRealPointerClicksChangeTheProgress() {
        show("Controlled")
        assertEquals(0.5f, fraction(), 0.001f)
        val thumb =
            rule.onNode(
                SemanticsMatcher.keyIsDefined(SemanticsActions.SetProgress) and hasRequestFocusAction(),
                useUnmergedTree = true,
            )
        thumb.requestFocus()
        thumb.performKeyInput { pressKey(Key.DirectionRight) }
        advance()
        assertEquals(0.51f, fraction(), 0.001f)
        thumb.performKeyInput { pressKey(Key.MoveEnd) }
        advance()
        assertEquals(1f, fraction(), 0.001f)
        thumb.performKeyInput { pressKey(Key.MoveHome) }
        advance()
        assertEquals(0f, fraction(), 0.001f)
        rule.onNodeWithTag("progress-slider").performMouseInput { click(Offset(width * 0.25f, height / 2f)) }
        advance()
        assertTrue(fraction() in 0.2f..0.3f)
        val sliderValue = thumb.fetchSemanticsNode().config[SemanticsProperties.ProgressBarRangeInfo].current
        assertEquals(sliderValue / 100f, fraction(), 0.001f)
        capture("controlled-pointer", "progress-controlled")
    }

    @Test fun rtlUsesArabicDigitsAndChangesLabelAndDirectionWithLanguage() {
        show("RTL")
        rule.onNodeWithText("تقدم الرفع", useUnmergedTree = true).assertIsDisplayed()
        rule.onNodeWithText("٥٦%", useUnmergedTree = true).assertIsDisplayed()
        capture("rtl-arabic")
        rule.onNodeWithText("Arabic (العربية)").performClick()
        advance()
        rule.onNodeWithText("English").performClick()
        advance()
        rule.onNodeWithText("Upload progress", useUnmergedTree = true).assertIsDisplayed()
        rule.onNodeWithText("56%", useUnmergedTree = true).assertIsDisplayed()
        capture("rtl-english")
        rule.onNodeWithText("English").performClick()
        advance()
        rule.onNodeWithText("Hebrew (עברית)").performClick()
        advance()
        rule.onNodeWithText("התקדמות העלאה", useUnmergedTree = true).assertIsDisplayed()
        rule.onNodeWithText("56%", useUnmergedTree = true).assertIsDisplayed()
        capture("rtl-hebrew")
    }
}
