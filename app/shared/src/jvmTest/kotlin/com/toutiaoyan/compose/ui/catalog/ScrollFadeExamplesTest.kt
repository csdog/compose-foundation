@file:OptIn(androidx.compose.foundation.style.ExperimentalFoundationStyleApi::class)

package com.toutiaoyan.compose.ui.catalog

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.toAwtImage
import androidx.compose.ui.platform.*
import androidx.compose.ui.semantics.SemanticsActions
import androidx.compose.ui.semantics.SemanticsProperties
import androidx.compose.ui.semantics.getOrNull
import androidx.compose.ui.test.*
import androidx.compose.ui.test.junit4.v2.createComposeRule
import androidx.compose.ui.unit.*
import com.toutiaoyan.compose.ui.catalog.docs.scrollFadeDoc
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
class ScrollFadeExamplesTest(private val viewport: Int, private val dark: Boolean) {
    @get:Rule val rule = createComposeRule()

    companion object {
        @JvmStatic
        @Parameterized.Parameters(name = "viewport={0},dark={1}")
        fun modes() =
            listOf(
                arrayOf<Any>(
                    1440,
                    false,
                ),
                arrayOf<Any>(
                    1440,
                    true,
                ),
                arrayOf<Any>(
                    390,
                    false,
                ),
                arrayOf<Any>(
                    390,
                    true,
                ),
            )
    }

    private fun advance() {
        rule.mainClock.advanceTimeBy(1000)
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
                        Modifier.width((if (viewport >= 640)640 else viewport).dp).height(720.dp)
                            .background(currentTheme.colors.background).padding(24.dp),
                        contentAlignment =
                            Alignment.Center,
                    ) {
                        scrollFadeDoc.examples.single { it.title == title }.RenderPreview()
                    }
                }
            }
        }
        advance()
    }

    private fun node(tag: String) = rule.onNodeWithTag("scroll-fade-$tag-viewport", useUnmergedTree = true)

    private fun value(
        tag: String,
        horizontal: Boolean = false,
    ) = node(tag).fetchSemanticsNode().config[
        if (horizontal) {
            SemanticsProperties.HorizontalScrollAxisRange
        } else {
            SemanticsProperties.VerticalScrollAxisRange
        },
    ].value()

    private fun maximum(
        tag: String,
        horizontal: Boolean = false,
    ) = node(tag).fetchSemanticsNode().config[
        if (horizontal) {
            SemanticsProperties.HorizontalScrollAxisRange
        } else {
            SemanticsProperties.VerticalScrollAxisRange
        },
    ].maxValue()

    private fun scroll(
        tag: String,
        delta: Float,
        horizontal: Boolean = false,
    ) {
        node(tag).performSemanticsAction(SemanticsActions.ScrollBy) {
            it(
                if (horizontal) {
                    delta
                } else {
                    0f
                },
                if (horizontal) {
                    0f
                } else {
                    delta
                },
            )
        }
        advance()
    }

    private fun capture(name: String) {
        val file = File("build/reports/scroll-fade-recipes/$name-$viewport-${if (dark)"dark" else "light"}.png")
        file.parentFile.mkdirs()
        ImageIO.write(rule.onNodeWithTag("scroll-fade-example").captureToImage().toAwtImage(), "png", file)
    }

    @Test fun defaultHasTwelveSourceTilesAndReachesBothEnds() {
        show("Default")
        rule.onNodeWithTag("scroll-fade-example").assertWidthIsEqualTo(320.dp).assertHeightIsEqualTo(290.dp)
        rule.onNodeWithText("Item 1").assertIsDisplayed()
        rule.onNodeWithText("Item 12").assertIsNotDisplayed()
        capture("default-start")
        scroll("default", 96f)
        assertEquals(96f, value("default"), .5f)
        capture("default-middle")
        scroll("default", 100000f)
        assertEquals(maximum("default"), value("default"))
        rule.onNodeWithText("Item 12").assertIsDisplayed()
        capture("default-end")
    }

    @Test fun noOverflowKeepsAllThreeTilesCrispWithoutScrollRange() {
        show("No Overflow, No Fade")
        rule.onNodeWithTag("scroll-fade-example").assertHeightIsEqualTo(146.dp)
        // The source container has no fixed height, so it must not be a scroll container
        // (a nested verticalScroll would get infinite constraints on the Web page).
        assertNull(
            node("overflow").fetchSemanticsNode().config.getOrNull(
                SemanticsProperties.VerticalScrollAxisRange,
            ),
        )
        (1..3).forEach { rule.onNodeWithText("Item $it").assertIsDisplayed() }
        capture("no-overflow")
    }

    @Test fun officialRecipeOrderAndKotlinExtensionAreExplicit() {
        assertEquals(
            listOf(
                "Default",
                "No Overflow, No Fade",
                "Horizontal Scrolling",
                "Edge Fades",
                "Fade Size",
                "Disabling the Fade",
                "RTL",
            ),
            scrollFadeDoc.examples.take(7).map { it.title },
        )
        assertEquals("Kotlin extension: Rows", scrollFadeDoc.examples[7].title)
        assertEquals(8, scrollFadeDoc.examples.size)
        assertTrue(scrollFadeDoc.examples.drop(7).all { it.title.startsWith("Kotlin extension: ") })
        assertTrue(scrollFadeDoc.isKotlinExtension)
        assertEquals(null, scrollFadeDoc.examplesHeading)
    }

    @Test fun horizontalIncludesAllTwelveTagsAndScrollsToSecurity() {
        show("Horizontal Scrolling")
        rule.onNodeWithText("Design").assertIsDisplayed()
        rule.onNodeWithText("Security").assertIsNotDisplayed()
        capture("horizontal-start")
        scroll("horizontal", 96f, true)
        assertTrue(value("horizontal", true) > 0)
        capture("horizontal-middle")
        scroll("horizontal", 100000f, true)
        rule.onNodeWithText("Security").assertIsDisplayed()
        assertEquals(maximum("horizontal", true), value("horizontal", true))
        capture("horizontal-end")
    }

    @Test fun edgeExamplesKeepTheirFourIndependentScrollStatesAndCaptions() {
        show("Edge Fades")
        listOf("t", "b", "s", "e").forEach { rule.onNodeWithText("scroll-fade-$it").assertIsDisplayed() }
        capture("edges-start")
        scroll("t", 96f)
        assertEquals(0f, value("b"))
        scroll("b", 96f)
        scroll("s", 96f, true)
        scroll("e", 96f, true)
        capture("edges-scrolled")
    }

    @Test fun explicitSizesPreserveSixteenAndNinetySixDepthExamples() {
        show("Fade Size")
        rule.onNodeWithText("scroll-fade-4").assertIsDisplayed()
        rule.onNodeWithText("scroll-fade-24").assertIsDisplayed()
        node("size-4").assertHeightIsEqualTo(192.dp)
        node("size-24").assertHeightIsEqualTo(192.dp)
        capture("sizes-start")
        scroll("size-4", 96f)
        scroll("size-24", 96f)
        capture("sizes-scrolled")
    }

    @Test fun noFadeStillScrollsAndKeepsTheSourcePairOfExamples() {
        show("Disabling the Fade")
        rule.onNodeWithText("scroll-fade").assertIsDisplayed()
        rule.onNodeWithText("scroll-fade scroll-fade-none").assertIsDisplayed()
        capture("none-start")
        scroll("on", 96f)
        scroll("off", 96f)
        assertEquals(value("on"), value("off"))
        capture("none-scrolled")
    }

    @Test fun rtlContainsExactTranslationsAndKeepsItsPositionAcrossLanguageChanges() {
        show("RTL")
        rule.onNodeWithText("تصميم").assertIsDisplayed()
        capture("rtl-arabic")
        scroll("rtl", 96f, true)
        val position = value("rtl", true)
        assertTrue(position > 0)
        rule.onNodeWithText("Arabic (العربية)").performClick()
        advance()
        rule.onNodeWithText("English").performClick()
        advance()
        assertEquals(position, value("rtl", true))
        capture("rtl-english")
        rule.onNodeWithText("English").performClick()
        advance()
        rule.onNodeWithText("Hebrew (עברית)").performClick()
        advance()
        assertEquals(position, value("rtl", true))
        capture("rtl-hebrew")
    }
}
