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
import androidx.compose.ui.test.*
import androidx.compose.ui.test.junit4.v2.createComposeRule
import androidx.compose.ui.unit.*
import com.toutiaoyan.compose.ui.catalog.docs.resizableDoc
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
class ResizableExamplesTest(private val viewport: Int, private val dark: Boolean) {
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

    private fun advance() {
        rule.mainClock.advanceTimeBy(64)
        rule.waitForIdle()
    }

    private fun show(title: String) {
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
                        Modifier.width((if (viewport >= 640) 640 else viewport).dp).height(480.dp)
                            .background(currentTheme.colors.background).padding(24.dp),
                        contentAlignment =
                            Alignment.Center,
                    ) {
                        resizableDoc.examples.single { it.title == title }.preview()
                    }
                }
            }
        }
        advance()
    }

    private fun fraction(tag: String = "resizable-outer-handle") =
        rule.onNodeWithTag(tag).fetchSemanticsNode()
            .config[SemanticsProperties.ProgressBarRangeInfo].current

    private fun capture(name: String) {
        val file = File("build/reports/resizable-recipes/$name-$viewport-${if (dark) "dark" else "light"}.png")
        file.parentFile.mkdirs()
        ImageIO.write(rule.onNodeWithTag("resizable-example").captureToImage().toAwtImage(), "png", file)
    }

    private fun drag(delta: Offset) {
        rule.onNodeWithTag("resizable-outer-handle").performMouseInput {
            moveTo(center)
            press()
            moveBy(delta, delayMillis = 16)
            release()
        }
        advance()
    }

    @Test fun nestedSourceDefaultKeepsIndependentRatiosAndSupportsActualDragging() {
        assertEquals(listOf("Default", "Vertical", "Handle", "RTL"), resizableDoc.examples.take(4).map { it.title })
        resizableDoc.examples.take(4).forEach {
            assertFalse(it.code.contains("RtlPreview"))
            assertFalse(it.code.contains("resizableDoc"))
        }
        show("Default")
        listOf("One", "Two", "Three").forEach { rule.onNodeWithText(it).assertIsDisplayed() }
        assertEquals(0.5f, fraction())
        assertEquals(0.25f, fraction("resizable-inner-handle"))
        rule.onNodeWithTag("resizable-example").assertHeightIsEqualTo(202.dp)
        capture("default-initial")
        rule.onNodeWithTag("resizable-outer-handle").requestFocus().performKeyInput { pressKey(Key.DirectionRight) }
        advance()
        assertEquals(0.55f, fraction(), 0.001f)
        assertEquals(0.25f, fraction("resizable-inner-handle"))
        rule.onNodeWithTag("resizable-inner-handle").requestFocus().performKeyInput { pressKey(Key.DirectionDown) }
        advance()
        assertEquals(0.3f, fraction("resizable-inner-handle"), 0.001f)
        val width = rule.onNodeWithTag("resizable-example").getUnclippedBoundsInRoot().width.value
        drag(Offset(90f, 0f))
        assertEquals(0.55f + 90f / (width - 3), fraction(), 0.001f)
        assertEquals(0.3f, fraction("resizable-inner-handle"), 0.001f)
        capture("default-resized")
    }

    @Test fun verticalSourceUsesQuarterHeightAndAHairlineWithoutAGrip() {
        show("Vertical")
        rule.onNodeWithText("Header").assertIsDisplayed()
        rule.onNodeWithText("Content").assertIsDisplayed()
        assertEquals(0.25f, fraction())
        rule.onNodeWithTag("resizable-outer-handle").assertHeightIsEqualTo(1.dp)
        capture("vertical-initial")
        rule.onNodeWithTag("resizable-outer-handle").requestFocus().performKeyInput { pressKey(Key.DirectionDown) }
        advance()
        rule.onNodeWithTag("resizable-outer-handle").performTouchInput {
            down(center + Offset(0f, -9f))
            moveBy(Offset(0f, 20f))
            up()
        }
        advance()
        assertEquals(0.3f + 20f / 197f, fraction(), 0.001f)
        capture("vertical-resized")
    }

    @Test fun handleSourceStartsAtTwentyFivePercentAndKeepsBothPanesVisible() {
        show("Handle")
        rule.onNodeWithText("Sidebar").assertIsDisplayed()
        rule.onNodeWithText("Content").assertIsDisplayed()
        assertEquals(0.25f, fraction())
        capture("handle-initial")
        val width = rule.onNodeWithTag("resizable-example").getUnclippedBoundsInRoot().width.value
        drag(Offset(60f, 0f))
        assertEquals(0.25f + 60f / (width - 3), fraction(), 0.001f)
        capture("handle-resized")
    }

    @Test fun rtlMirrorsPhysicalDraggingAndKeepsTheSplitWhenChangingLanguages() {
        show("RTL")
        listOf("واحد", "اثنان", "ثلاثة").forEach { rule.onNodeWithText(it).assertIsDisplayed() }
        val group = rule.onNodeWithTag("resizable-example").getUnclippedBoundsInRoot()
        val first = rule.onNodeWithTag("resizable-one").getUnclippedBoundsInRoot()
        assertEquals(group.right.value - 1, first.right.value, 0.5f)
        capture("rtl-arabic")
        drag(Offset(24f, 0f))
        val resized = fraction()
        assertEquals(0.5f - 24f / (group.width.value - 3), resized, 0.001f)
        rule.onNodeWithText("Arabic (العربية)").performClick()
        advance()
        rule.onNodeWithText("English").performClick()
        advance()
        listOf("One", "Two", "Three").forEach { rule.onNodeWithText(it).assertIsDisplayed() }
        assertEquals(resized, fraction(), 0.001f)
        val englishFirst = rule.onNodeWithTag("resizable-one").getUnclippedBoundsInRoot()
        assertEquals(group.left.value + 1, englishFirst.left.value, 0.5f)
        capture("rtl-english")
        rule.onNodeWithText("English").performClick()
        advance()
        rule.onNodeWithText("Hebrew (עברית)").performClick()
        advance()
        listOf("אחד", "שניים", "שלושה").forEach { rule.onNodeWithText(it).assertIsDisplayed() }
        assertEquals(resized, fraction(), 0.001f)
        capture("rtl-hebrew")
    }
}
