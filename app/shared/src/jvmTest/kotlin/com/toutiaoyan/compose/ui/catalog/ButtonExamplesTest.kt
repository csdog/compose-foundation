package com.toutiaoyan.compose.ui.catalog

import androidx.compose.foundation.layout.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.test.*
import androidx.compose.ui.test.junit4.v2.createComposeRule
import androidx.compose.ui.unit.dp
import com.toutiaoyan.compose.ui.catalog.docs.buttonDoc
import com.toutiaoyan.compose.ui.theme.Theme
import com.toutiaoyan.compose.ui.tokens.StylePreset
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import org.junit.runners.Parameterized
import kotlin.test.*

@RunWith(Parameterized::class)
class ButtonExamplesTest(private val width: Int, private val dark: Boolean) {
    @get:Rule val rule = createComposeRule()

    @Test fun recipesShowOfficialLabelsSizesAndLoadingState() {
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
                        Modifier.requiredSize(
                            (if (width < 768) width - 65 else 638).dp,
                            400.dp,
                        ).padding(if (index == 13) 0.dp else 40.dp),
                        contentAlignment = Alignment.Center,
                    ) {
                        key(index) { buttonDoc.examples[index].preview() }
                    }
                }
            }
        }
        assertEquals(
            listOf(
                "Default",
                "Size",
                "Default",
                "Outline",
                "Secondary",
                "Ghost",
                "Destructive",
                "Link",
                "Icon",
                "With Icon",
                "Rounded",
                "Spinner",
                "Button Group",
                "RTL",
            ),
            buttonDoc.examples.map {
                it.title
            },
        )
        rule.onNodeWithText("Button").assertIsDisplayed()
        rule.runOnIdle { index = 1 }
        val small = rule.onNodeWithText("Extra Small").getUnclippedBoundsInRoot()
        val large = rule.onNodeWithText("Large").getUnclippedBoundsInRoot()
        if (width < 640) assertTrue(large.top > small.bottom) else assertEquals(small.top.value, large.top.value, .1f)
        for (i in 2..10) {
            rule.runOnIdle { index = i }
            when (i) {
                8 -> rule.onNodeWithContentDescription("Upload").assertWidthIsEqualTo(32.dp)
                9 -> rule.onNodeWithText("New Branch").assertIsDisplayed()
                10 -> rule.onNodeWithText("Get Started").assertIsDisplayed()
            }
        }
        rule.runOnIdle { index = 11 }
        rule.onNodeWithText("Generating").assertIsNotEnabled()
        rule.onNodeWithText("Downloading").assertIsNotEnabled()
        rule.onNodeWithTag("button-generating-spinner", useUnmergedTree = true).assertWidthIsEqualTo(16.dp)
        val generating = rule.onNodeWithText("Generating").getUnclippedBoundsInRoot()
        val downloading = rule.onNodeWithText("Downloading").getUnclippedBoundsInRoot()
        assertEquals(generating.top.value, downloading.top.value, .1f, "Loading buttons remain on one row")
    }

    @Test fun rtlLanguageSwitchesRestoreDirectionAndLoadingButtonsRemainDisabled() {
        rule.setContent {
            CompositionLocalProvider(LocalCatalogViewportWidth provides width.dp) {
                Theme(
                    preset = StylePreset.Nova,
                    isDark = dark,
                    minTouchTarget = null,
                    fontFamily = catalogFontFamily(),
                ) {
                    Box(
                        Modifier.requiredSize((if (width < 768) width - 65 else 638).dp, 400.dp),
                    ) { buttonDoc.examples.last().preview() }
                }
            }
        }
        rule.onNodeWithText("زر").assertExists()
        rule.onNodeWithText("جاري التحميل").assertIsNotEnabled()
        rule.onNodeWithText("زر").assertHeightIsEqualTo(32.dp)
        rule.onNodeWithText("زر", useUnmergedTree = true).assertHeightIsEqualTo(20.dp)
        if (width < 640) {
            rule.onNodeWithTag("button-recipe-14").assertWidthIsEqualTo(245.dp)
            val first = rule.onNodeWithText("زر").getUnclippedBoundsInRoot()
            val last = rule.onNodeWithText("جاري التحميل").getUnclippedBoundsInRoot()
            assertEquals(first.right.value, last.right.value, .1f, "RTL wrapped rows align at logical start")
        } else {
            val first = rule.onNodeWithText("زر").getUnclippedBoundsInRoot()
            val last = rule.onNodeWithText("جاري التحميل").getUnclippedBoundsInRoot()
            assertEquals(first.top.value, last.top.value, .1f, "Desktop RTL keeps all five buttons on one row")
        }
        rule.onNodeWithContentDescription("Preview language").performClick()
        rule.onNodeWithText("English").performClick()
        rule.onNodeWithText("Button").assertExists()
        rule.onNodeWithText("Loading").assertIsNotEnabled()
        rule.onNodeWithContentDescription("Preview language").performClick()
        rule.onNodeWithText("Hebrew (עברית)").performClick()
        rule.onNodeWithText("כפתור").assertExists()
        rule.onNodeWithText("טוען").assertIsNotEnabled()
    }

    companion object {
        @JvmStatic
        @Parameterized.Parameters(name = "width={0}, dark={1}")
        fun cases(): List<Array<Any>> = listOf(390, 1440).flatMap { w -> listOf(false, true).map { arrayOf(w, it) } }
    }
}
