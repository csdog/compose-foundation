package com.toutiaoyan.compose.ui.catalog

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.requiredSize
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.getValue
import androidx.compose.runtime.key
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.test.*
import androidx.compose.ui.test.junit4.v2.createComposeRule
import androidx.compose.ui.unit.dp
import com.toutiaoyan.compose.ui.catalog.docs.ComponentGuideBlock
import com.toutiaoyan.compose.ui.catalog.docs.contextMenuDoc
import com.toutiaoyan.compose.ui.theme.Theme
import com.toutiaoyan.compose.ui.tokens.StylePreset
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import org.junit.runners.Parameterized
import kotlin.test.assertEquals
import kotlin.test.assertIs
import kotlin.test.assertTrue

@RunWith(Parameterized::class)
class ContextMenuExamplesTest(private val width: Int, private val dark: Boolean) {
    @get:Rule val rule = createComposeRule()

    @Test fun officialPageOrderAndCopyMatchCurrentOfficialPage() {
        assertEquals(
            listOf(
                "Default",
                "Basic",
                "Submenu",
                "Shortcuts",
                "Groups",
                "Icons",
                "Checkboxes",
                "Radio",
                "Destructive",
                "Sides",
                "RTL",
            ),
            contextMenuDoc.examples.map { it.title },
        )
        assertEquals("Displays a menu of actions triggered by a right click.", contextMenuDoc.description)
        assertEquals(null, contextMenuDoc.examplesHeading)
        val composition = contextMenuDoc.compositionCode!!
        for (part in listOf("ContextMenuTrigger", "ContextMenuGroup", "ContextMenuCheckboxItem", "ContextMenuSubContent")) {
            assertTrue(composition.contains(part), "composition is missing $part")
        }
        assertEquals(
            "A simple context menu with a few actions.",
            contextMenuDoc.examples.single { it.title == "Basic" }.descriptionMarkdown,
        )
        assertEquals(
            "Use `ContextMenuSub` to nest secondary actions.",
            contextMenuDoc.examples.single { it.title == "Submenu" }.descriptionMarkdown,
        )
        assertEquals(
            "Add `ContextMenuShortcut` to show keyboard hints.",
            contextMenuDoc.examples.single { it.title == "Shortcuts" }.descriptionMarkdown,
        )
        assertEquals(
            "Combine icons with labels for quick scanning.",
            contextMenuDoc.examples.single { it.title == "Icons" }.descriptionMarkdown,
        )
        assertEquals(
            "Use `ContextMenuCheckboxItem` for toggles.",
            contextMenuDoc.examples.single { it.title == "Checkboxes" }.descriptionMarkdown,
        )
        assertEquals(
            "Use `ContextMenuRadioItem` for exclusive choices.",
            contextMenuDoc.examples.single { it.title == "Radio" }.descriptionMarkdown,
        )
        assertEquals(
            "Control submenu placement with side and align props.",
            contextMenuDoc.examples.single { it.title == "Sides" }.descriptionMarkdown,
        )
        val api = assertIs<ComponentGuideBlock.Paragraph>(
            contextMenuDoc.afterExamples.single { it.title == "API Reference" }.blocks.single(),
        )
        assertTrue(api.markdown.contains("https://base-ui.com/react/components/context-menu#api-reference"))
        assertTrue(contextMenuDoc.usageImportsCode!!.contains("contextmenu.ContextMenuTrigger"))
        assertTrue(contextMenuDoc.examples.single { it.title == "Sides" }.code.contains("ContextMenuSide.Top"))
    }

    @Test fun everyOfficialPreviewRendersAtBothWidths() {
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
                        Modifier.requiredSize((if (width < 768) width - 65 else 638).dp, 360.dp)
                            .padding(40.dp)
                            .testTag("preview"),
                        contentAlignment = Alignment.Center,
                    ) {
                        key(index) { contextMenuDoc.examples[index].preview() }
                    }
                }
            }
        }
        for (i in contextMenuDoc.examples.indices) {
            rule.runOnIdle { index = i }
            rule.waitForIdle()
        }
        assertEquals(11, contextMenuDoc.examples.size)
    }

    @Test fun dashedTriggerAndSidesRenderFourTargets() {
        val sides = contextMenuDoc.examples.single { it.title == "Sides" }
        rule.setContent {
            Theme(preset = StylePreset.Nova, isDark = dark, minTouchTarget = null, fontFamily = catalogFontFamily()) {
                sides.preview()
            }
        }
        rule.onNodeWithText("Right click (top)").assertExists()
        rule.onNodeWithText("Right click (right)").assertExists()
        rule.onNodeWithText("Right click (bottom)").assertExists()
        rule.onNodeWithText("Right click (left)").assertExists()
    }

    @Test fun rtlPreviewSwitchesBetweenArabicEnglishAndHebrew() {
        val rtl = contextMenuDoc.examples.single { it.title == "RTL" }
        rule.setContent {
            Theme(preset = StylePreset.Nova, isDark = dark, minTouchTarget = null, fontFamily = catalogFontFamily()) {
                rtl.preview()
            }
        }
        rule.onNodeWithText("انقر بزر الماوس الأيمن هنا").assertExists()
        rule.onNodeWithContentDescription("Preview language").performClick()
        rule.onNodeWithText("English").performClick()
        rule.onNodeWithText("Right click here").assertExists()
        rule.onNodeWithContentDescription("Preview language").performClick()
        rule.onNodeWithText("Hebrew (עברית)").performClick()
        rule.onNodeWithText("לחץ לחיצה ימנית כאן").assertExists()
    }

    companion object {
        @JvmStatic
        @Parameterized.Parameters(name = "width={0}, dark={1}")
        fun cases(): List<Array<Any>> = listOf(390, 1440).flatMap { w -> listOf(false, true).map { arrayOf(w, it) } }
    }
}
