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
import com.toutiaoyan.compose.ui.catalog.docs.paginationDoc
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
class PaginationExamplesTest(private val viewport: Int, private val dark: Boolean) {
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
        rule.mainClock.advanceTimeBy(400)
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
                        Modifier.testTag("pagination-capture").width(
                            (
                                if (viewport >= 640) {
                                    640
                                } else {
                                    viewport
                                }
                            ).dp,
                        ).height(500.dp)
                            .background(currentTheme.colors.background).padding(24.dp),
                        contentAlignment =
                            Alignment.TopCenter,
                    ) {
                        paginationDoc.examples.single { it.title == title }.preview()
                    }
                }
            }
        }
        advance()
    }

    private fun capture(name: String) {
        val out = File("build/reports/pagination-recipes/$name-$viewport-${if (dark) "dark" else "light"}.png")
        out.parentFile.mkdirs()
        ImageIO.write(rule.onNodeWithTag("pagination-capture").captureToImage().toAwtImage(), "png", out)
    }

    @Test fun officialRecipesKeepTheirOfficialCopyAndKotlinExtensionsFollow() {
        assertEquals(
            listOf("Default", "Simple", "Icons Only", "RTL"),
            paginationDoc.examples.take(4).map { it.title },
        )
        assertEquals(
            "A simple pagination with only page numbers.",
            paginationDoc.examples.single { it.title == "Simple" }.descriptionMarkdown,
        )
        assertTrue(
            paginationDoc.examples.single { it.title == "Icons Only" }
                .descriptionMarkdown.orEmpty()
                .startsWith("Use just the previous and next buttons"),
        )
        assertTrue(
            paginationDoc.examples.single { it.title == "RTL" }.descriptionMarkdown.orEmpty()
                .startsWith("To enable RTL support"),
        )
        assertTrue(paginationDoc.examples.drop(4).all { it.title.startsWith("Kotlin extension: ") })
        assertTrue(paginationDoc.examples.any { it.title == "Kotlin extension: Controlled" })
        assertFalse(paginationDoc.examples.any { it.title.contains("(Kotlin)") && it.title.startsWith("Kotlin extension") })
    }

    @Test fun defaultIsTheStaticSourceCompositionRatherThanAnInventedControlledPager() {
        show("Default")
        (1..3).forEach { rule.onNodeWithText(it.toString()).assertIsDisplayed() }
        rule.onNodeWithText("2").assertIsSelected()
        rule.onNodeWithText("10").assertDoesNotExist()
        rule.onNodeWithContentDescription("Go to previous page").assertIsEnabled()
        rule.onNodeWithContentDescription("Go to next page").assertIsEnabled()
        if (viewport >= 640) {
            rule.onNodeWithText("Previous").assertIsDisplayed()
        } else {
            rule.onNodeWithText("Previous").assertDoesNotExist()
        }
        rule.onNodeWithText("3").performClick()
        advance()
        rule.onNodeWithText("2").assertIsSelected()
        rule.onNodeWithText("3").assertIsNotSelected()
        capture("default")
    }

    @Test fun simpleHasFivePagesAndKeepsTheLiteralSecondPageActive() {
        show("Simple")
        (1..5).forEach { rule.onNodeWithText(it.toString()).assertIsDisplayed() }
        rule.onNodeWithContentDescription("Go to previous page").assertDoesNotExist()
        rule.onNodeWithText("5").performClick()
        advance()
        rule.onNodeWithText("2").assertIsSelected()
        capture("simple")
    }

    @Test fun iconsOnlyIncludesTheRealRowsSelectorWith25DefaultAnd80Width() {
        show("Icons Only")
        rule.onNodeWithTag("pagination-row-count").assertWidthIsEqualTo(80.dp)
        rule.onNodeWithText("25").assertIsDisplayed()
        rule.onNodeWithContentDescription("Go to previous page").assertIsEnabled()
        val content = rule.onNodeWithTag("pagination-icons-recipe").getUnclippedBoundsInRoot()
        val frame = rule.onNodeWithTag("pagination-capture").getUnclippedBoundsInRoot()
        assertTrue(content.right <= frame.right && content.left >= frame.left)
        capture("icons-25")
        rule.onNodeWithText("Rows per page").performClick()
        advance()
        listOf("10", "50", "100").forEach { rule.onNodeWithText(it).assertIsDisplayed() }
        capture("icons-menu")
        rule.onNodeWithText("50").performClick()
        advance()
        rule.onNodeWithText("50").assertIsDisplayed()
        rule.onNodeWithText("100").assertDoesNotExist()
        capture("icons-50")
    }

    @Test fun rtlStartsWithArabicDigitsAndSwitchesLanguagesAndDirections() {
        show("RTL")
        rule.onNodeWithText("٢").assertIsSelected()
        val arabicOne = rule.onNodeWithText("١").getUnclippedBoundsInRoot()
        val arabicThree = rule.onNodeWithText("٣").getUnclippedBoundsInRoot()
        assertTrue(arabicOne.left > arabicThree.left)
        if (viewport >= 640) {
            rule.onNodeWithText("السابق").assertIsDisplayed()
        } else {
            rule.onNodeWithText("السابق").assertDoesNotExist()
        }
        rule.onNodeWithText("٣").performClick()
        advance()
        rule.onNodeWithText("٢").assertIsSelected()
        capture("rtl-arabic")
        rule.onNodeWithContentDescription("Preview language").performClick()
        advance()
        rule.onNodeWithText("English").performClick()
        advance()
        rule.onNodeWithText("2").assertIsSelected()
        val englishOne = rule.onNodeWithText("1").getUnclippedBoundsInRoot()
        val englishThree = rule.onNodeWithText("3").getUnclippedBoundsInRoot()
        assertTrue(englishOne.left < englishThree.left)
        rule.onNodeWithContentDescription("Preview language").performClick()
        advance()
        rule.onNodeWithText("Hebrew (עברית)").performClick()
        advance()
        rule.onNodeWithText("2").assertIsSelected()
        if (viewport >= 640) rule.onNodeWithText("הקודם").assertIsDisplayed()
        capture("rtl-hebrew")
    }
}
