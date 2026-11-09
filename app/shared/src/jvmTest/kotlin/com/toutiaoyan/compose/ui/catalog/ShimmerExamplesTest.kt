package com.toutiaoyan.compose.ui.catalog

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.toAwtImage
import androidx.compose.ui.graphics.toPixelMap
import androidx.compose.ui.platform.*
import androidx.compose.ui.semantics.SemanticsProperties
import androidx.compose.ui.test.*
import androidx.compose.ui.test.junit4.v2.createComposeRule
import androidx.compose.ui.unit.*
import com.toutiaoyan.compose.ui.catalog.docs.shimmerDoc
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
class ShimmerExamplesTest(private val viewport: Int, private val dark: Boolean) {
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

    private fun advance(time: Long = 512) {
        rule.mainClock.advanceTimeBy(time)
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
                        Modifier.width(minOf(viewport, 640).dp).height(320.dp)
                            .background(currentTheme.colors.background).padding(24.dp),
                        contentAlignment = Alignment.Center,
                    ) { shimmerDoc.examples.single { it.title == title }.preview() }
                }
            }
        }
        advance(64)
    }

    private fun node(tag: String) = rule.onNodeWithTag(tag, useUnmergedTree = true)

    private fun pixels(node: SemanticsNodeInteraction): List<Color> {
        val image = node.captureToImage().toPixelMap()
        return (0 until image.height).flatMap { y -> (0 until image.width).map { x -> image[x, y] } }
    }

    private fun capture(name: String) {
        val file = File("build/reports/shimmer-recipes/$name-$viewport-${if (dark) "dark" else "light"}.png")
        file.parentFile.mkdirs()
        ImageIO.write(node("shimmer-example").captureToImage().toAwtImage(), "png", file)
    }

    private fun comparisonLayout() {
        val left = node("shimmer-left").getUnclippedBoundsInRoot()
        val right = node("shimmer-right").getUnclippedBoundsInRoot()
        if (viewport >= 640) {
            assertEquals(left.top, right.top)
            assertEquals(24.dp, right.left - left.right)
            node("shimmer-example").assertWidthIsEqualTo(512.dp)
        } else {
            assertEquals(left.left, right.left)
            assertEquals(24.dp, right.top - left.bottom)
            node("shimmer-example").assertWidthIsEqualTo(342.dp)
        }
    }

    @Test fun defaultTextUsesFullTwentyPixelLineAndActuallyAnimates() {
        show("Default")
        node("shimmer-text").assertHeightIsEqualTo(20.dp).assertTextEquals("Generating response…")
        val before = pixels(node("shimmer-text"))
        advance(960)
        assertNotEquals(before, pixels(node("shimmer-text")))
        capture("default")
    }

    @Test fun markerLabelsEachAnimateAndExposeStatusWithoutAnnouncingSpinner() {
        show("With Marker")
        rule.onAllNodes(SemanticsMatcher.keyIsDefined(SemanticsProperties.LiveRegion)).assertCountEquals(2)
        rule.onNodeWithContentDescription("Loading").assertDoesNotExist()
        val thinking = rule.onNodeWithText("Thinking...")
        val reading = rule.onNodeWithText("Reading 4 files")
        val first = pixels(thinking)
        val second = pixels(reading)
        advance(960)
        assertNotEquals(first, pixels(thinking))
        assertNotEquals(second, pixels(reading))
        capture("marker")
    }

    @Test fun explicitColorExamplesBothAnimateWithEightPixelGap() {
        show("Color")
        val first = pixels(node("shimmer-blue"))
        val second = pixels(node("shimmer-custom"))
        val top = node("shimmer-blue").getUnclippedBoundsInRoot()
        val bottom = node("shimmer-custom").getUnclippedBoundsInRoot()
        assertEquals(8.dp, bottom.top - top.bottom)
        advance(960)
        assertNotEquals(first, pixels(node("shimmer-blue")))
        assertNotEquals(second, pixels(node("shimmer-custom")))
        assertNotEquals(pixels(node("shimmer-blue")), pixels(node("shimmer-custom")))
        capture("color")
    }

    @Test fun durationExamplesRespondAndHaveDifferentSweepPhases() {
        show("Duration")
        comparisonLayout()
        rule.onNodeWithText("shimmer-duration-1000").assertIsDisplayed()
        advance()
        assertNotEquals(pixels(node("shimmer-default")), pixels(node("shimmer-fast")))
        capture("duration")
    }

    @Test fun spreadExamplesHaveSourceLabelsAndDistinctBands() {
        show("Spread")
        comparisonLayout()
        rule.onNodeWithText("shimmer-spread-4").assertIsDisplayed()
        rule.onNodeWithText("shimmer-spread-24").assertIsDisplayed()
        advance(960)
        assertNotEquals(pixels(node("shimmer-narrow")), pixels(node("shimmer-wide")))
        capture("spread")
    }

    @Test fun angleExamplesHaveSourceLabelsAndDistinctTilt() {
        show("Angle")
        comparisonLayout()
        rule.onNodeWithText("shimmer-angle-45").assertIsDisplayed()
        advance(960)
        assertNotEquals(pixels(node("shimmer-default")), pixels(node("shimmer-angle")))
        capture("angle")
    }

    @Test fun onceStopsAndReplayRestartsVisibleAnimation() {
        show("Play Once")
        val initial = pixels(node("shimmer-once"))
        advance()
        val middle = pixels(node("shimmer-once"))
        assertNotEquals(initial, middle)
        capture("once")
        advance(1200)
        val completed = pixels(node("shimmer-once"))
        advance()
        assertEquals(completed, pixels(node("shimmer-once")))
        rule.onNodeWithText("Replay").performClick()
        advance(64)
        advance()
        assertNotEquals(completed, pixels(node("shimmer-once")))
        capture("replayed")
    }

    @Test fun responsiveNoneIsStaticAtDesktopAndAnimatedBelowMd() {
        show("Disabling the Shimmer")
        rule.onNodeWithText("shimmer md:shimmer-none").assertIsDisplayed()
        val start = pixels(node("shimmer-responsive"))
        advance(960)
        val middle = pixels(node("shimmer-responsive"))
        if (viewport >= 768) assertEquals(start, middle) else assertNotEquals(start, middle)
        capture("none")
    }

    @Test fun rtlShowsBothReadingDirectionsAndArabicCopy() {
        show("RTL")
        comparisonLayout()
        node("shimmer-ltr").assertTextEquals("Generating response…")
        node("shimmer-rtl").assertTextEquals("جارٍ إنشاء الرد…")
        rule.onNodeWithText("dir=\"ltr\"").assertIsDisplayed()
        rule.onNodeWithText("dir=\"rtl\"").assertIsDisplayed()
        val before = pixels(node("shimmer-rtl"))
        advance(960)
        assertNotEquals(before, pixels(node("shimmer-rtl")))
        capture("rtl")
    }

    @Test fun allTenCopiedRecipesComeFromCompiledSourceAndUseCssStylePaint() {
        assertEquals(
            listOf(
                "Default", "With Marker", "Color", "Duration", "Spread", "Angle", "Reverse",
                "Play Once", "Disabling the Shimmer", "RTL",
            ),
            shimmerDoc.examples.take(10).map { it.title },
        )
        shimmerDoc.examples.take(10).forEach { example ->
            assertTrue(example.code.contains("import androidx.compose.runtime.*"))
            assertTrue(example.code.contains("private fun ShimmerText"))
            assertTrue(example.code.contains("rememberShimmerStyle"))
        }
        assertTrue(shimmerDoc.examples.single { it.title == "Play Once" }.code.contains("key(replay)"))
        assertTrue(shimmerDoc.examples.single { it.title == "Disabling the Shimmer" }.code.contains("768.dp"))
        assertTrue(shimmerDoc.examples.single { it.title == "Reverse" }.code.contains("reverse = true"))
        assertTrue(
            shimmerDoc.examples.drop(10).all { it.title.startsWith("Kotlin extension: ") },
            "Kotlin-only recipes must be titled `Kotlin extension: ...`",
        )
        assertEquals(null, shimmerDoc.examplesHeading)
        assertEquals(
            listOf("Kotlin extension: CSS class mapping"),
            shimmerDoc.beforeExamples.map { it.title },
        )
        assertTrue(shimmerDoc.afterExamples.isEmpty())
        assertEquals(
            listOf("Fallback", "Reduced Motion"),
            shimmerDoc.beforeExampleSections.getValue("RTL").map { it.title },
        )
        assertTrue(
            shimmerDoc.examples.single { it.title == "Spread" }.descriptionMarkdown!!.contains("calc(3ch + 40px)"),
        )
        assertTrue(
            shimmerDoc.examples.single { it.title == "With Marker" }.descriptionMarkdown!!.contains("Marker"),
        )
        assertTrue(
            shimmerDoc.examples.single { it.title == "RTL" }.descriptionMarkdown!!.contains("reading direction"),
        )
    }

    @Test fun reverseSweepsOppositeToTheDefaultAtTheSameFrame() {
        show("Reverse")
        comparisonLayout()
        rule.onNodeWithText("shimmer").assertIsDisplayed()
        rule.onNodeWithText("shimmer-reverse").assertIsDisplayed()
        advance(960)
        assertNotEquals(pixels(node("shimmer-forward")), pixels(node("shimmer-reverse")))
        capture("reverse")
    }
}
