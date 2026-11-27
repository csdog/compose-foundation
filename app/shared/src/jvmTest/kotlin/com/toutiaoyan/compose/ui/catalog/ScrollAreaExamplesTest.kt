@file:OptIn(androidx.compose.foundation.style.ExperimentalFoundationStyleApi::class)

package com.toutiaoyan.compose.ui.catalog

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.toAwtImage
import androidx.compose.ui.input.key.Key
import androidx.compose.ui.platform.*
import androidx.compose.ui.semantics.SemanticsProperties
import androidx.compose.ui.test.*
import androidx.compose.ui.test.junit4.v2.createComposeRule
import androidx.compose.ui.unit.*
import com.toutiaoyan.compose.ui.catalog.docs.scrollAreaDoc
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
class ScrollAreaExamplesTest(private val viewport: Int, private val dark: Boolean) {
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
                        Modifier.width((if (viewport >= 640) 640 else viewport).dp).height(640.dp)
                            .background(currentTheme.colors.background).padding(24.dp),
                        contentAlignment =
                            Alignment.Center,
                    ) {
                        scrollAreaDoc.examples.single { it.title == title }.RenderPreview()
                    }
                }
            }
        }
        advance()
    }

    private fun node(tag: String) = rule.onNodeWithTag("scroll-area-$tag", useUnmergedTree = true)

    private fun capture(name: String) {
        val file = File("build/reports/scroll-area-recipes/$name-$viewport-${if (dark) "dark" else "light"}.png")
        file.parentFile.mkdirs()
        ImageIO.write(node("example").captureToImage().toAwtImage(), "png", file)
    }

    private fun scrollValue() =
        node("viewport").fetchSemanticsNode()
            .config[SemanticsProperties.VerticalScrollAxisRange].value()

    @Test fun pageKeepsTheThreeOfficialRecipesAndLabelsExtensions() {
        assertEquals(
            listOf("Default", "Horizontal", "RTL"),
            scrollAreaDoc.examples.take(3).map { it.title },
        )
        assertTrue(scrollAreaDoc.examples.drop(3).all { it.title.startsWith("Kotlin extension: ") })
        assertEquals(null, scrollAreaDoc.examplesHeading)
        assertEquals("ScrollArea\n└── ScrollBar", scrollAreaDoc.compositionCode)
        assertEquals(
            "Augments native scroll functionality for custom, cross-browser styling.",
            scrollAreaDoc.description,
        )
    }

    @Test fun defaultHasSourceFrameFiftyTagsAndKeyboardReachesLastTag() {
        show("Default")
        node("example").assertWidthIsEqualTo(192.dp).assertHeightIsEqualTo(288.dp)
        rule.onNodeWithText("Tags").assertIsDisplayed()
        rule.onNodeWithText("v1.2.0-beta.50").assertIsDisplayed()
        rule.onNodeWithText("v1.2.0-beta.1").assertIsNotDisplayed()
        capture("default-initial")
        node("viewport").requestFocus().performKeyInput { pressKey(Key.MoveEnd) }
        advance()
        rule.onNodeWithText("v1.2.0-beta.1").assertIsDisplayed()
        assertTrue(scrollValue() > 1000)
        capture("default-end")
    }

    @Test fun horizontalUsesOriginalArtworkDimensionsAndScrollsToThirdArtist() {
        show("Horizontal")
        rule.onNodeWithContentDescription("Photo by Ornella Binni").assertWidthIsEqualTo(300.dp)
            .assertHeightIsEqualTo(400.dp).assertIsDisplayed()
        node("horizontal-track").assertHeightIsEqualTo(10.dp)
        capture("horizontal-initial")
        node("viewport").requestFocus().performKeyInput { pressKey(Key.MoveEnd) }
        advance()
        rule.onNodeWithText("Photo by Vladimir Malyavko").assertIsDisplayed()
        rule.onNodeWithContentDescription("Photo by Ornella Binni").assertIsNotDisplayed()
        capture("horizontal-end")
    }

    @Test fun rtlStartsArabicMovesScrollbarAndRetainsOffsetAcrossLanguages() {
        show("RTL")
        rule.onNodeWithText("العلامات").assertIsDisplayed()
        val area = node("example").getUnclippedBoundsInRoot()
        val arabicTrack = node("vertical-track").getUnclippedBoundsInRoot()
        assertEquals(area.left.value + 1, arabicTrack.left.value, 0.5f)
        capture("rtl-arabic")
        node("viewport").requestFocus().performKeyInput { pressKey(Key.DirectionDown) }
        advance()
        val position = scrollValue()
        assertTrue(position > 0)
        rule.onNodeWithText("Arabic (العربية)").performClick()
        advance()
        rule.onNodeWithText("English").performClick()
        advance()
        assertEquals(position, scrollValue())
        val englishTrack = node("vertical-track").getUnclippedBoundsInRoot()
        assertEquals(area.right.value - 1, englishTrack.right.value, 0.5f)
        capture("rtl-english")
        rule.onNodeWithText("English").performClick()
        advance()
        rule.onNodeWithText("Hebrew (עברית)").performClick()
        advance()
        assertEquals(position, scrollValue())
        capture("rtl-hebrew")
    }
}
