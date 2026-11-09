@file:OptIn(androidx.compose.ui.ExperimentalComposeUiApi::class)

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
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.compositeOver
import androidx.compose.ui.graphics.toArgb
import androidx.compose.ui.graphics.toPixelMap
import com.toutiaoyan.compose.tailwind.core.TwColors
import com.toutiaoyan.compose.ui.theme.currentTheme
import androidx.compose.ui.platform.ClipEntry
import androidx.compose.ui.platform.Clipboard
import androidx.compose.ui.platform.LocalClipboard
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.test.*
import androidx.compose.ui.test.junit4.v2.createComposeRule
import androidx.compose.ui.unit.dp
import androidx.compose.ui.text.TextLayoutResult
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.semantics.SemanticsActions
import com.toutiaoyan.compose.ui.catalog.docs.ComponentGuideBlock
import com.toutiaoyan.compose.ui.catalog.docs.inputGroupDoc
import com.toutiaoyan.compose.ui.components.inputgroup.InputGroup
import com.toutiaoyan.compose.ui.components.inputgroup.InputGroupInput
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
class InputGroupExamplesTest(private val width: Int, private val dark: Boolean) {
    @get:Rule val rule = createComposeRule()

    @Test fun officialPageOrderAndCopyMatchCurrentOfficialPage() {
        assertEquals(
            listOf(
                "Default",
                "inline-start",
                "inline-end",
                "block-start",
                "block-end",
                "Icon",
                "Text",
                "Button",
                "Kbd",
                "Dropdown",
                "Spinner",
                "Textarea",
                "Custom Input",
                "RTL",
            ),
            inputGroupDoc.examples.map { it.title },
        )
        assertEquals("Add addons, buttons, and helper content to inputs.", inputGroupDoc.description)
        assertEquals(null, inputGroupDoc.examplesHeading)
        assertEquals(listOf("Align"), inputGroupDoc.beforeExamples.map { it.title })
        val align = inputGroupDoc.beforeExamples.single().blocks
        assertTrue(assertIs<ComponentGuideBlock.Paragraph>(align.first()).markdown.contains("`align`"))
        assertTrue(
            assertIs<ComponentGuideBlock.Paragraph>(align.last()).markdown.contains("after `InputGroupInput`"),
        )
        assertEquals(
            "Use `align=\"inline-start\"` to position the addon at the start of the input. This is the default.",
            inputGroupDoc.examples.single { it.title == "inline-start" }.descriptionMarkdown,
        )
        assertEquals(
            "Use `align=\"inline-end\"` to position the addon at the end of the input.",
            inputGroupDoc.examples.single { it.title == "inline-end" }.descriptionMarkdown,
        )
        assertEquals(
            "Use `align=\"block-start\"` to position the addon above the input.",
            inputGroupDoc.examples.single { it.title == "block-start" }.descriptionMarkdown,
        )
        assertEquals(
            "Use `align=\"block-end\"` to position the addon below the input.",
            inputGroupDoc.examples.single { it.title == "block-end" }.descriptionMarkdown,
        )
        assertEquals(
            listOf(
                "InputGroup",
                "InputGroupAddon",
                "InputGroupButton",
                "InputGroupInput",
                "InputGroupTextarea",
                "InputGroupText",
            ),
            inputGroupDoc.apiSections.map { it.title },
        )
        assertEquals(
            listOf("inline-start", "inline-end", "block-start", "block-end"),
            inputGroupDoc.examples.filter { it.headingLevel == 3 }.map { it.title },
        )
    }

    @Test fun copyButtonWritesTheOfficialUrlToClipboard() {
        var copied: String? = null
        val clipboard =
            object : Clipboard {
                override suspend fun getClipEntry(): ClipEntry? = null

                override suspend fun setClipEntry(clipEntry: ClipEntry?) {
                    copied =
                        (clipEntry?.nativeClipEntry as? java.awt.datatransfer.Transferable)
                            ?.getTransferData(java.awt.datatransfer.DataFlavor.stringFlavor) as? String
                }
            }
        rule.setContent {
            CompositionLocalProvider(LocalClipboard provides clipboard) {
                Theme(preset = StylePreset.Nova, isDark = dark, minTouchTarget = null) {
                    inputGroupDoc.examples.single { it.title == "Button" }.preview()
                }
            }
        }
        rule.onNodeWithContentDescription("Copy").performClick()
        rule.waitForIdle()
        assertEquals("https://x.com/shadcn", copied)
    }

    @Test fun secureUrlFieldStaysBeforeItsTrailingFavoriteButton() {
        rule.setContent {
            Theme(preset = StylePreset.Nova, isDark = dark, minTouchTarget = null) {
                Box(Modifier.requiredSize((if (width < 768) 245 else 384).dp, 300.dp)) {
                    inputGroupDoc.examples.single { it.title == "Button" }.preview()
                }
            }
        }
        val fields = rule.onAllNodes(hasSetTextAction()).fetchSemanticsNodes()
        assertEquals(2, fields.size)
        val secureField = fields[0].boundsInRoot
        val prefix = rule.onNodeWithText("https://").fetchSemanticsNode().boundsInRoot
        assertTrue(secureField.left >= prefix.right, "URL text must follow the prefix")
        val favorite = rule.onNodeWithContentDescription("Favorite").fetchSemanticsNode().boundsInRoot
        assertTrue(secureField.right <= favorite.left, "Editable URL must leave room for the trailing button")
    }

    @Test fun codeTextareaPreservesOfficialMinimumHeight() {
        rule.setContent {
            Theme(preset = StylePreset.Nova, isDark = dark, minTouchTarget = null) {
                inputGroupDoc.examples.single { it.title == "Textarea" }.preview()
            }
        }
        // The editable semantics excludes the control's 8.dp top and bottom content padding.
        rule.onNode(hasSetTextAction()).assertHeightIsAtLeast(184.dp)
    }

    @Test fun favoriteSelectionUsesTheOfficialBlue() {
        rule.setContent {
            Theme(preset = StylePreset.Nova, isDark = dark, minTouchTarget = null) {
                inputGroupDoc.examples.single { it.title == "Button" }.preview()
            }
        }
        val favorite = rule.onNodeWithContentDescription("Favorite")
        favorite.performClick()
        val pixels = favorite.captureToImage().toPixelMap()
        var bluePixels = 0
        for (y in 0 until pixels.height) {
            for (x in 0 until pixels.width) {
                if (pixels[x, y].toArgb() == TwColors.blue600.toArgb()) bluePixels++
            }
        }
        assertTrue(bluePixels > 0, "Selected favorite must visibly use blue-600")
    }

    @Test fun codeEditorHasHeaderAndFooterDividers() {
        var border = Color.Unspecified
        rule.setContent {
            Theme(preset = StylePreset.Nova, isDark = dark, minTouchTarget = null) {
                val colors = currentTheme.colors
                val background = if (dark) colors.input.copy(alpha = colors.input.alpha * .3f) else Color.Transparent
                border = colors.border.compositeOver(background)
                inputGroupDoc.examples.single { it.title == "Textarea" }.preview()
            }
        }
        val header = rule.onNodeWithTag("input-group-code-header").captureToImage().toPixelMap()
        val footer = rule.onNodeWithTag("input-group-code-footer").captureToImage().toPixelMap()

        fun assertBorder(actual: Color) {
            // Raster compositing may round the translucent dark border by one 8-bit unit.
            assertEquals(border.red, actual.red, .005f)
            assertEquals(border.green, actual.green, .005f)
            assertEquals(border.blue, actual.blue, .005f)
            assertEquals(border.alpha, actual.alpha, .005f)
        }
        assertBorder(header[header.width / 2, header.height - 1])
        assertBorder(footer[footer.width / 2, 0])
    }

    @Test fun infoButtonUsesSecondaryFillAndCircularCorners() {
        var secondary = Color.Unspecified
        rule.setContent {
            Theme(preset = StylePreset.Nova, isDark = dark, minTouchTarget = null) {
                secondary = currentTheme.colors.secondary
                inputGroupDoc.examples.single { it.title == "Button" }.preview()
            }
        }
        val pixels = rule.onNodeWithContentDescription("Info").captureToImage().toPixelMap()
        val fill = pixels[2, pixels.height / 2]
        assertEquals(secondary.red, fill.red, .005f)
        assertEquals(secondary.green, fill.green, .005f)
        assertEquals(secondary.blue, fill.blue, .005f)
        assertEquals(secondary.alpha, fill.alpha, .005f)
        assertTrue(pixels[3, 3] != fill, "Circle corners must remain outside the secondary button fill")
    }

    @Test fun inlineButtonsUseTheSourceNegativeOuterMarginEquivalent() {
        rule.setContent {
            Theme(preset = StylePreset.Nova, isDark = dark, minTouchTarget = null) {
                Box(Modifier.requiredSize((if (width < 768) 245 else 384).dp, 300.dp).testTag("recipe")) {
                    inputGroupDoc.examples.single { it.title == "Button" }.preview()
                }
            }
        }
        val root = rule.onNodeWithTag("recipe").fetchSemanticsNode().boundsInRoot
        val copy = rule.onNodeWithContentDescription("Copy").fetchSemanticsNode().boundsInRoot
        // CSS border1 + addon padding8 - negative margin4.8 =4.2 from the outer edge.
        assertEquals(4.2f, root.right - copy.right, .5f)
    }

    @Test fun kbdUsesTheSourceNegativeOuterMarginEquivalent() {
        rule.setContent {
            Theme(preset = StylePreset.Nova, isDark = dark, minTouchTarget = null) {
                Box(Modifier.requiredSize((if (width < 768) 245 else 384).dp, 100.dp).testTag("recipe")) {
                    inputGroupDoc.examples.single { it.title == "Kbd" }.preview()
                }
            }
        }
        val root = rule.onNodeWithTag("recipe").fetchSemanticsNode().boundsInRoot
        val kbd = rule.onNodeWithText("⌘K", useUnmergedTree = true).fetchSemanticsNode().boundsInRoot
        // Text excludes Kbd's4dp end padding: border1 + addon8 - margin2.4 + key4.
        assertEquals(10.6f, root.right - kbd.right, .5f)
    }

    @Test fun codeHeaderUsesMediumMonospaceTypography() {
        rule.setContent {
            Theme(preset = StylePreset.Nova, isDark = dark, minTouchTarget = null) {
                inputGroupDoc.examples.single { it.title == "Textarea" }.preview()
            }
        }
        val layouts = mutableListOf<TextLayoutResult>()
        rule.onNodeWithText("script.js", useUnmergedTree = true)
            .performSemanticsAction(SemanticsActions.GetTextLayoutResult) { it(layouts) }
        assertEquals(FontFamily.Monospace, layouts.single().layoutInput.style.fontFamily)
        assertEquals(FontWeight.Medium, layouts.single().layoutInput.style.fontWeight)
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
                        Modifier.requiredSize((if (width < 768) width - 65 else 638).dp, 420.dp)
                            .padding(40.dp)
                            .testTag("preview"),
                        contentAlignment = Alignment.Center,
                    ) {
                        key(index) { inputGroupDoc.examples[index].preview() }
                    }
                }
            }
        }
        for (i in inputGroupDoc.examples.indices) {
            rule.runOnIdle { index = i }
            rule.waitForIdle()
        }
        assertEquals(14, inputGroupDoc.examples.size)
    }

    @Test fun demoAddonContentAndBlockEndFooterRender() {
        rule.setContent {
            Theme(preset = StylePreset.Nova, isDark = dark, minTouchTarget = null, fontFamily = catalogFontFamily()) {
                inputGroupDoc.examples.single { it.title == "Default" }.preview()
            }
        }
        rule.onNodeWithText("Search...").assertExists()
        rule.onNodeWithText("12 results").assertExists()
        rule.onNodeWithContentDescription("Search").assertExists()
    }

    @Test fun blockEndTextareaShowsCountAndPostAction() {
        rule.setContent {
            Theme(preset = StylePreset.Nova, isDark = dark, minTouchTarget = null, fontFamily = catalogFontFamily()) {
                inputGroupDoc.examples.single { it.title == "block-end" }.preview()
            }
        }
        rule.onNodeWithText("USD").assertExists()
        rule.onNodeWithText("0/280").assertExists()
        rule.onNodeWithText("Post").assertExists()
        rule.onNodeWithText("Write a comment...").assertExists()
    }

    @Test fun spinnerRecipeExposesAllFourLoadingIndicators() {
        rule.setContent {
            Theme(preset = StylePreset.Nova, isDark = dark, minTouchTarget = null, fontFamily = catalogFontFamily()) {
                inputGroupDoc.examples.single { it.title == "Spinner" }.preview()
            }
        }
        rule.onAllNodesWithContentDescription("Loading").assertCountEquals(4)
    }

    @Test fun kbdRecipeRendersTheShortcut() {
        rule.setContent {
            Theme(preset = StylePreset.Nova, isDark = dark, minTouchTarget = null, fontFamily = catalogFontFamily()) {
                inputGroupDoc.examples.single { it.title == "Kbd" }.preview()
            }
        }
        rule.onNodeWithText("⌘K").assertExists()
    }

    @Test fun disabledAndInvalidGroupStatesKeepInputSemantics() {
        rule.setContent {
            Theme(preset = StylePreset.Nova, isDark = dark, minTouchTarget = null, fontFamily = catalogFontFamily()) {
                androidx.compose.foundation.layout.Column {
                    InputGroup(disabled = true) {
                        InputGroupInput(value = "20", onValueChange = {})
                    }
                    InputGroup(invalid = true) {
                        InputGroupInput(value = "20", onValueChange = {})
                    }
                }
            }
        }
        // A disabled group drops the editable text action; the invalid group stays editable.
        rule.onAllNodes(hasSetTextAction()).assertCountEquals(1)
        rule.onAllNodes(hasSetTextAction())[0].assertIsEnabled()
    }

    @Test fun rtlPreviewSwitchesLanguages() {
        rule.setContent {
            Theme(preset = StylePreset.Nova, isDark = dark, minTouchTarget = null, fontFamily = catalogFontFamily()) {
                inputGroupDoc.examples.single { it.title == "RTL" }.preview()
            }
        }
        rule.onNodeWithText("١٢ نتيجة").assertExists()
        rule.onNodeWithText("جاري الحفظ...").assertExists()
        rule.onNodeWithContentDescription("Preview language").performClick()
        rule.onNodeWithText("English").performClick()
        rule.onNodeWithText("12 results").assertExists()
        rule.onNodeWithText("Saving...").assertExists()
        rule.onNodeWithContentDescription("Preview language").performClick()
        rule.onNodeWithText("Hebrew (עברית)").performClick()
        rule.onNodeWithText("12 תוצאות").assertExists()
        rule.onNodeWithText("שומר...").assertExists()
    }

    companion object {
        @JvmStatic
        @Parameterized.Parameters(name = "width={0}, dark={1}")
        fun cases(): List<Array<Any>> = listOf(390, 1440).flatMap { w -> listOf(false, true).map { arrayOf(w, it) } }
    }
}
