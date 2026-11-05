package com.toutiaoyan.compose.ui.catalog

import androidx.compose.foundation.layout.*
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.ui.Modifier
import androidx.compose.ui.semantics.SemanticsActions
import androidx.compose.ui.test.*
import androidx.compose.ui.test.junit4.v2.createComposeRule
import androidx.compose.ui.text.TextLayoutResult
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.unit.dp
import com.toutiaoyan.compose.ui.catalog.docs.separatorDoc
import com.toutiaoyan.compose.ui.components.overlay.OverlayHost
import com.toutiaoyan.compose.ui.theme.Theme
import com.toutiaoyan.compose.ui.tokens.StylePreset
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import org.junit.runners.Parameterized
import kotlin.test.*

@RunWith(Parameterized::class)
class SeparatorPageTest(private val width: Int, private val dark: Boolean) {
    @get:Rule val rule = createComposeRule()

    @Test fun pageContainsTheFiveOfficialRecipesWithoutLegacyDuplicates() {
        assertEquals(listOf("Default", "Vertical", "Menu", "List", "RTL"), separatorDoc.examples.map { it.title })
        for (example in separatorDoc.examples) {
            assertFalse(example.code.contains("import com.toutiaoyan.compose.ui.catalog."))
            assertFalse(example.code.contains("testTag("))
            assertTrue(example.code.trimEnd().endsWith("Recipe()"))
        }
    }

    @Test fun rtlBodyAndBundledFontsKeepTwentyPixelSubtitleLines() {
        var fonts = emptyList<FontFamily>()
        rule.setContent {
            fonts = listOf(catalogFontFamily(), catalogArabicFontFamily(), catalogHebrewFontFamily())
            Theme(preset = StylePreset.Nova, isDark = dark, minTouchTarget = null, fontFamily = fonts[0]) {
                OverlayHost {
                    Box(Modifier.width((if (width < 640) 325 else 638).dp)) {
                        separatorDoc.examples.single { it.title == "RTL" }.preview()
                    }
                }
            }
        }
        rule.onNodeWithTag("separator-rtl-body").assertHeightIsEqualTo(288.dp)
        for ((index, title) in listOf(
            1 to "الأساس لنظام التصميم الخاص بك",
            0 to "The Foundation for your Design System",
            2 to "הבסיס למערכת העיצוב שלך",
        )) {
            if (index != 1) {
                rule.onNodeWithContentDescription("Preview language").performClick()
                rule.onNodeWithText(if (index == 0) "English" else "Hebrew (עברית)").performClick()
            }
            val layouts = mutableListOf<TextLayoutResult>()
            rule.onNodeWithText(title).performSemanticsAction(SemanticsActions.GetTextLayoutResult) { it(layouts) }
            val layout = layouts.single()
            assertEquals(fonts[index], layout.layoutInput.style.fontFamily)
            assertEquals(layout.lineCount * 20, layout.size.height)
        }
    }

    @Test fun rtlSectionCarriesTheCurrentGuideCopy() {
        val rtl = separatorDoc.examples.single { it.title == "RTL" }.descriptionMarkdown.orEmpty()
        assertContains(rtl, "To enable RTL support in shadcn/ui")
        assertContains(rtl, "https://ui.shadcn.com/docs/rtl")
    }

    companion object {
        @JvmStatic
        @Parameterized.Parameters(name = "{0}-{1}")
        fun parameters() =
            listOf(
                arrayOf<Any>(1440, false),
                arrayOf<Any>(1440, true),
                arrayOf<Any>(390, false),
                arrayOf<Any>(390, true),
            )
    }
}

@RunWith(Parameterized::class)
class SeparatorBreakpointTest(private val viewport: Int) {
    @get:Rule val rule = createComposeRule()

    @Test fun menuUsesTheCssViewportIncludingScrollbarWidth() {
        rule.setContent {
            CompositionLocalProvider(LocalCatalogViewportWidth provides viewport.dp) {
                Theme(preset = StylePreset.Nova, minTouchTarget = null, fontFamily = catalogFontFamily()) {
                    Box(Modifier.width(500.dp)) { separatorDoc.examples.single { it.title == "Menu" }.preview() }
                }
            }
        }
        if (viewport >= 768) {
            rule.onNodeWithText("Help").assertIsDisplayed()
        } else {
            rule.onNodeWithText("Help").assertDoesNotExist()
        }
    }

    companion object {
        @JvmStatic
        @Parameterized.Parameters(name = "{0}")
        fun parameters() = listOf(arrayOf<Any>(767), arrayOf<Any>(768))
    }
}