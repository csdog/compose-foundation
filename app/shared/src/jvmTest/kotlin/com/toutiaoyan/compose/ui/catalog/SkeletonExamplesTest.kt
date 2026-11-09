@file:OptIn(androidx.compose.foundation.style.ExperimentalFoundationStyleApi::class)

package com.toutiaoyan.compose.ui.catalog

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.toAwtImage
import androidx.compose.ui.platform.*
import androidx.compose.ui.test.*
import androidx.compose.ui.test.junit4.v2.createComposeRule
import androidx.compose.ui.unit.*
import com.toutiaoyan.compose.ui.catalog.docs.skeletonDoc
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
class SkeletonExamplesTest(private val viewport: Int, private val dark: Boolean) {
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
        rule.mainClock.advanceTimeBy(192)
        rule.waitForIdle()
    }

    private fun show(title: String) {
        rule.mainClock.autoAdvance = false
        rule.setContent {
            val original = LocalWindowInfo.current
            val window =
                remember(original) {
                    object : WindowInfo by original {
                        override val containerSize = IntSize(viewport, 800)
                    }
                }
            CompositionLocalProvider(LocalWindowInfo provides window) {
                Theme(preset = StylePreset.Nova, isDark = dark, minTouchTarget = null) {
                    Box(
                        Modifier.width(minOf(viewport, 640).dp).height(480.dp)
                            .background(currentTheme.colors.background)
                            .padding(if (title == "RTL") 0.dp else 24.dp),
                        contentAlignment = Alignment.Center,
                    ) {
                        skeletonDoc.examples.single { it.title == title }.preview()
                    }
                }
            }
        }
        rule.mainClock.advanceTimeByFrame()
        rule.waitForIdle()
    }

    private fun node(tag: String) = rule.onNodeWithTag(tag, useUnmergedTree = true)

    private fun bounds(tag: String) = node(tag).fetchSemanticsNode().boundsInRoot

    private fun capture(name: String) {
        val file = File("build/reports/skeleton-recipes/$name-$viewport-${if (dark) "dark" else "light"}.png")
        file.parentFile.mkdirs()
        ImageIO.write(node("skeleton-example").captureToImage().toAwtImage(), "png", file)
    }

    private fun profile(
        avatar: Int,
        first: Int,
        second: Int,
        rtl: Boolean = false,
    ) {
        node("skeleton-avatar").assertWidthIsEqualTo(avatar.dp).assertHeightIsEqualTo(avatar.dp)
        node("skeleton-line-0").assertWidthIsEqualTo(first.dp).assertHeightIsEqualTo(16.dp)
        node("skeleton-line-1").assertWidthIsEqualTo(second.dp).assertHeightIsEqualTo(16.dp)
        assertEquals(bounds("skeleton-line-0").bottom + 8, bounds("skeleton-line-1").top, .1f)
        if (rtl) {
            assertEquals(bounds("skeleton-avatar").left - 16, bounds("skeleton-line-0").right, .1f)
            assertEquals(bounds("skeleton-line-0").right, bounds("skeleton-line-1").right, .1f)
        } else {
            assertEquals(bounds("skeleton-avatar").right + 16, bounds("skeleton-line-0").left, .1f)
        }
    }

    @Test fun defaultMatchesAvatarAndTwoLines() {
        assertEquals(
            listOf(
                "Default",
                "Avatar",
                "Card",
                "Text",
                "Form",
                "Table",
                "RTL",
            ),
            skeletonDoc.examples.take(7).map {
                it.title
            },
        )
        assertEquals(null, skeletonDoc.examplesHeading)
        assertTrue(skeletonDoc.examples.single { it.title == "RTL" }.descriptionMarkdown!!.contains("shadcn/ui"))
        assertTrue(skeletonDoc.examples.drop(7).all { it.title.startsWith("Kotlin extension: ") })
        show("Default")
        profile(48, 250, 200)
        node("skeleton-example").assertWidthIsEqualTo(314.dp).assertHeightIsEqualTo(48.dp)
        capture("default")
    }

    @Test fun avatarHasCircleAndCompactLines() {
        show("Avatar")
        profile(40, 150, 100)
        node("skeleton-example").assertWidthIsEqualTo(206.dp).assertHeightIsEqualTo(40.dp)
        capture("avatar")
    }

    @Test fun cardUsesSourceFractionsAndVideoAspectRatio() {
        show("Card")
        node("skeleton-example").assertWidthIsEqualTo(320.dp)
        node("skeleton-line-0").assertWidthIsEqualTo(192.dp)
        node("skeleton-line-1").assertWidthIsEqualTo(144.dp)
        node("skeleton-image").assertWidthIsEqualTo(288.dp).assertHeightIsEqualTo(162.dp)
        assertEquals(bounds("skeleton-line-0").bottom + 4, bounds("skeleton-line-1").top, .1f)
        capture("card")
    }

    @Test fun textUsesThreeRowsAndFractionalLastLine() {
        show("Text")
        node("skeleton-example").assertWidthIsEqualTo(320.dp).assertHeightIsEqualTo(64.dp)
        node("skeleton-line-2").assertWidthIsEqualTo(240.dp).assertHeightIsEqualTo(16.dp)
        capture("text")
    }

    @Test fun formUsesTwoGroupsAndButtonWithExactGaps() {
        show("Form")
        node("skeleton-example").assertWidthIsEqualTo(320.dp).assertHeightIsEqualTo(208.dp)
        node("skeleton-label-0").assertWidthIsEqualTo(80.dp)
        node("skeleton-label-1").assertWidthIsEqualTo(96.dp)
        node("skeleton-input-0").assertWidthIsEqualTo(320.dp).assertHeightIsEqualTo(32.dp)
        node("skeleton-button").assertWidthIsEqualTo(96.dp).assertHeightIsEqualTo(32.dp)
        assertEquals(bounds("skeleton-label-0").bottom + 12, bounds("skeleton-input-0").top, .1f)
        assertEquals(bounds("skeleton-input-0").bottom + 28, bounds("skeleton-label-1").top, .1f)
        assertEquals(bounds("skeleton-input-1").bottom + 28, bounds("skeleton-button").top, .1f)
        capture("form")
    }

    @Test fun tableHasFiveRowsAndResponsiveFirstColumn() {
        show("Table")
        val width = if (viewport == 390) 342 else 384
        node("skeleton-example").assertWidthIsEqualTo(width.dp).assertHeightIsEqualTo(112.dp)
        repeat(5) { row ->
            node("skeleton-cell-$row-0").assertWidthIsEqualTo((width - 208).dp).assertHeightIsEqualTo(16.dp)
            node("skeleton-cell-$row-1").assertWidthIsEqualTo(96.dp)
            node("skeleton-cell-$row-2").assertWidthIsEqualTo(80.dp)
        }
        capture("table")
    }

    @Test fun projectShimmerKeepsThreeRowsAndMatchesItsCopiedCode() {
        show("Kotlin extension: Shimmer")
        node("skeleton-example").assertWidthIsEqualTo((if (viewport == 390) 342 else 592).dp)
            .assertHeightIsEqualTo(64.dp)
        repeat(3) { node("skeleton-line-$it").assertHeightIsEqualTo(16.dp) }
        capture("shimmer")
    }

    @Test fun rtlArabicDefaultMirrorsProfile() {
        show("RTL")
        profile(48, 250, 200, rtl = true)
        capture("rtl-arabic")
    }

    private fun language(label: String) {
        rule.onNodeWithContentDescription("Preview language").performClick()
        advance()
        rule.onNodeWithText(label).performClick()
        advance()
    }

    @Test fun rtlEnglishSelectorRestoresLtrProfile() {
        show("RTL")
        language("English")
        profile(48, 250, 200)
        capture("rtl-english")
    }

    @Test fun rtlHebrewSelectorKeepsRtlProfile() {
        show("RTL")
        language("Hebrew (עברית)")
        profile(48, 250, 200, rtl = true)
        capture("rtl-hebrew")
    }
}
