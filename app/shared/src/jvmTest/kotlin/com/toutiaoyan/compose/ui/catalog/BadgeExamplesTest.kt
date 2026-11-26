package com.toutiaoyan.compose.ui.catalog

import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.requiredSize
import androidx.compose.runtime.*
import androidx.compose.ui.graphics.toPixelMap
import com.toutiaoyan.compose.tailwind.core.TwColors
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.test.*
import androidx.compose.ui.test.junit4.v2.createComposeRule
import androidx.compose.ui.unit.dp
import com.toutiaoyan.compose.ui.catalog.docs.badgeDoc
import com.toutiaoyan.compose.ui.theme.Theme
import com.toutiaoyan.compose.ui.tokens.StylePreset
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import org.junit.runners.Parameterized
import kotlin.test.*

@RunWith(Parameterized::class)
class BadgeExamplesTest(private val width: Int, private val dark: Boolean) {
    @get:Rule val rule = createComposeRule()

    @Test fun officialRecipesAndLogicalIconsRemainVisibleAtBothWidths() {
        var index by mutableIntStateOf(0)
        rule.setContent {
            CompositionLocalProvider(LocalCatalogViewportWidth provides width.dp) {
                Theme(
                    preset = StylePreset.Nova,
                    isDark = dark,
                    minTouchTarget = null,
                    fontFamily = catalogFontFamily(),
                ) {
                    Box(
                        Modifier.requiredSize((if (width < 768) width - 65 else 638).dp, 400.dp)
                            .padding(if (index == 6) 0.dp else 40.dp).testTag("preview"),
                        contentAlignment = Alignment.Center,
                    ) {
                        key(index) { badgeDoc.examples[index].preview() }
                    }
                }
            }
        }
        assertEquals(
            listOf(
                "Default",
                "Variants",
                "With Icon",
                "With Spinner",
                "Link",
                "Custom Colors",
                "RTL",
            ),
            badgeDoc.examples.map {
                it.title
            },
        )
        assertEquals(
            "Use `href` to render a link as a badge. `onClick` can override navigation, " +
                "as this catalog does for its section anchor.",
            badgeDoc.examples.single { it.title == "Link" }.descriptionMarkdown,
        )
        assertTrue(badgeDoc.usageImportsCode!!.contains("badge.BadgeVariant"))
        rule.onNodeWithText("Badge").assertExists()
        rule.runOnIdle { index = 1 }
        rule.onNodeWithText("Ghost").assertExists()
        if (width == 390) rule.onNodeWithTag("badge-recipe-1").assertWidthIsEqualTo(245.dp)
        rule.runOnIdle { index = 2 }
        rule.onNodeWithText("Verified").assertExists()
        val left = rule.onNodeWithTag("badge-leading-icon").getUnclippedBoundsInRoot()
        val label = rule.onNodeWithTag("badge-verified-label").getUnclippedBoundsInRoot()
        assertEquals(4f, (label.left - left.right).value, .2f)
        rule.onNodeWithTag("badge-leading-icon").assertWidthIsEqualTo(12.dp).assertHeightIsEqualTo(12.dp)
        rule.runOnIdle { index = 3 }
        rule.onNodeWithText("Deleting").assertExists()
        val deleting = rule.onNodeWithText("Deleting").getUnclippedBoundsInRoot()
        val generating = rule.onNodeWithText("Generating").getUnclippedBoundsInRoot()
        assertEquals(deleting.top.value, generating.top.value, .1f, "Both spinner badges fit on one line")
        rule.onNodeWithTag("badge-generating-spinner").assertWidthIsEqualTo(12.dp).assertHeightIsEqualTo(12.dp)
        rule.runOnIdle { index = 5 }
        rule.onNodeWithText("Purple").assertExists()
        val pixels = rule.onNodeWithTag("badge-color-0").captureToImage().toPixelMap()
        val actual = pixels[pixels.width / 2, 2]
        val expected = if (dark) TwColors.blue950 else TwColors.blue50
        assertEquals(expected.red, actual.red, .015f)
        assertEquals(expected.green, actual.green, .015f)
        assertEquals(expected.blue, actual.blue, .015f)
        rule.runOnIdle { index = 6 }
        rule.onNodeWithText("متحقق").assertExists()
        val rtlIcon = rule.onNodeWithTag("badge-leading-icon").getUnclippedBoundsInRoot()
        val rtlLabel = rule.onNodeWithTag("badge-verified-label").getUnclippedBoundsInRoot()
        assertEquals(4f, (rtlIcon.left - rtlLabel.right).value, .2f)
        assertTrue((rtlLabel.bottom - rtlLabel.top).value <= 16.1f, "Arabic uses the same 16dp label line box")
        rule.onNodeWithContentDescription("Preview language").performClick()
        rule.onNodeWithText("English").performClick()
        rule.onNodeWithText("Verified").assertExists()
        val englishIcon = rule.onNodeWithTag("badge-leading-icon").getUnclippedBoundsInRoot()
        val englishLabel = rule.onNodeWithTag("badge-verified-label").getUnclippedBoundsInRoot()
        assertTrue(englishIcon.right <= englishLabel.left, "English restores LTR order")
        rule.onNodeWithContentDescription("Preview language").performClick()
        rule.onNodeWithText("Hebrew (עברית)").performClick()
        rule.onNodeWithText("מאומת").assertExists()
        val hebrewLabel = rule.onNodeWithTag("badge-verified-label").getUnclippedBoundsInRoot()
        assertTrue((hebrewLabel.bottom - hebrewLabel.top).value <= 16.1f, "Hebrew uses the same 16dp label line box")
    }

    @Test fun actualLinkRecipeNavigatesToItsDocumentAnchor() {
        var selectedFragment: String? = null
        val host =
            object : DocNavigation {
                override val location = DocLocation("badge", "")

                override fun navigate(fragment: String) {
                    selectedFragment = fragment
                }
            }
        rule.setContent {
            CompositionLocalProvider(LocalDocNavigation provides host) {
                Theme(preset = StylePreset.Nova, isDark = dark) { badgeDoc.examples[4].preview() }
            }
        }
        rule.onNodeWithTag("badge-link").performClick()
        assertEquals("link", selectedFragment)
    }

    @Test fun linkRecipeUsesTheLocalHeadingWithoutBrowserNavigation() {
        val selectedHeadings = mutableListOf<String>()
        val headings =
            mapOf(
                "examples-default" to DocHeadingLink("default") { selectedHeadings += "default" },
                "examples-link" to DocHeadingLink("link") { selectedHeadings += "link" },
            )
        rule.setContent {
            CompositionLocalProvider(LocalDocHeadingLinks provides headings) {
                Theme(preset = StylePreset.Nova, isDark = dark) { badgeDoc.examples[4].preview() }
            }
        }
        rule.onNodeWithTag("badge-link").performClick()
        assertEquals(listOf("link"), selectedHeadings)
    }

    companion object {
        @JvmStatic
        @Parameterized.Parameters(name = "width={0}, dark={1}")
        fun cases(): List<Array<Any>> = listOf(390, 1440).flatMap { w -> listOf(false, true).map { arrayOf(w, it) } }
    }
}
