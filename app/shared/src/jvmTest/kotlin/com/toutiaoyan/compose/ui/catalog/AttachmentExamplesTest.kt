package com.toutiaoyan.compose.ui.catalog

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.background
import androidx.compose.foundation.Image
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.ColorFilter
import androidx.compose.ui.graphics.compositeOver
import androidx.compose.ui.graphics.toPixelMap
import androidx.compose.ui.input.key.Key
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.platform.LocalUriHandler
import androidx.compose.ui.platform.UriHandler
import androidx.compose.ui.semantics.SemanticsActions
import androidx.compose.ui.test.*
import androidx.compose.ui.test.junit4.v2.createComposeRule
import androidx.compose.ui.text.TextLayoutResult
import androidx.compose.ui.unit.dp
import com.toutiaoyan.compose.ui.catalog.docs.attachmentDoc
import com.toutiaoyan.compose.ui.catalog.docs.PreviewCodeSection
import com.toutiaoyan.compose.ui.components.overlay.OverlayHost
import com.toutiaoyan.compose.ui.theme.Theme
import com.toutiaoyan.compose.ui.theme.currentTheme
import com.toutiaoyan.compose.ui.tokens.StylePreset
import org.junit.Rule
import kotlin.math.abs
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertTrue
import javax.imageio.ImageIO
import java.io.File
import androidx.compose.ui.text.font.FontWeight
import com.composables.icons.lucide.Lucide
import com.composables.icons.lucide.FileSearch

class AttachmentExamplesTest {
    @Test
    fun triggerRecipeContainsOneAlignedVectorWithoutLeakedGeneratorCode() {
        val code = attachmentDoc.examples.single { it.title == "Trigger" }.code
        val first = code.lineSequence().first { it.isNotBlank() }
        assertEquals(first.trimStart(), first)
        val vector = code.lineSequence().single { "val previewIcon = remember" in it }
        assertEquals(4, vector.takeWhile { it == ' ' }.length)
        assertEquals(1, Regex(Regex.escape("ImageVector.Builder")).findAll(code).count())
        assertTrue("fileSearchPaths.joinToString" !in code)
        assertTrue("attachmentPreviewIconCode.prependIndent" !in code)
    }

    @get:Rule val rule = createComposeRule()

    @Test fun triggerGlyphMatchesTheCurrentNativeSvgInBothThemes() {
        var dark by mutableStateOf(false)
        rule.setContent {
            Theme(preset = StylePreset.Nova, fontFamily = catalogFontFamily(), isDark = dark, minTouchTarget = null) {
                OverlayHost {
                    Column(Modifier.width(384.dp)) {
                        attachmentDoc.examples.single { it.title == "Trigger" }.preview()
                        Box(Modifier.background(currentTheme.colors.muted).testTag("old glyph")) {
                            Image(
                                Lucide.FileSearch,
                                null,
                                Modifier.size(16.dp),
                                colorFilter = ColorFilter.tint(currentTheme.colors.foreground),
                            )
                        }
                    }
                }
            }
        }
        for (night in listOf(false, true)) {
            rule.runOnIdle { dark = night }
            val card = rule.onNodeWithContentDescription("Preview research-summary.pdf").captureToImage().toPixelMap()
            val theme = if (night) "dark" else "light"
            val ref = ImageIO.read(File("src/jvmTest/resources/attachment/file-search-source-$theme-16.png"))

            fun error(
                pixels: androidx.compose.ui.graphics.PixelMap,
                inset: Int,
            ): Float {
                var error = 0f
                for (y in 0 until 16) for (x in 0 until 16) {
                    val expected = Color(ref.getRGB(x, y))
                    val actual = pixels[inset + x, inset + y]
                    error += (
                        abs(expected.red - actual.red) +
                            abs(expected.green - actual.green) +
                            abs(expected.blue - actual.blue)
                    ) / 3f
                }
                return error / 256f
            }
            // Clickable semantics captures the382x56 content box, excluding the
            // outer1dp border: padding8 +12px centring gives a20px inset.
            assertEquals(382, card.width)
            assertEquals(56, card.height)
            val currentError = error(card, 20)
            val oldError = error(rule.onNodeWithTag("old glyph").captureToImage().toPixelMap(), 0)
            assertTrue(
                currentError < .06f,
                "Current outline must match native SVG within edge AA tolerance ($theme): $currentError",
            )
            assertTrue(oldError >= .06f, "The same criterion must reject the installed old outline ($theme): $oldError")
        }
    }

    @Test fun triggerTitleKeepsTheSourceMediumWeightAndGlyphAdvance() {
        rule.setContent {
            Theme(preset = StylePreset.Nova, fontFamily = catalogFontFamily(), minTouchTarget = null) {
                OverlayHost {
                    Box(
                        Modifier.width(384.dp),
                    ) { attachmentDoc.examples.single { it.title == "Trigger" }.preview() }
                }
            }
        }
        val results = mutableListOf<TextLayoutResult>()
        rule.onNodeWithText(
            "research-summary.pdf",
        ).performSemanticsAction(SemanticsActions.GetTextLayoutResult) { it(results) }
        val result = results.single()
        assertEquals(FontWeight.Medium, result.layoutInput.style.fontWeight)
        assertEquals(14f, result.layoutInput.style.fontSize.value, .01f)
        // Actual website Canvas shaping, including kerning. The unshaped hmtx
        // sum is152.04px and is not a valid expected rendered text width.
        assertEquals(148.00816f, result.multiParagraph.getLineRight(0), .1f)
    }

    @Test fun defaultLoadsThreeRealPhotosAndKeepsSquareMediaAcrossWidthsAndThemes() {
        var width by mutableStateOf(638.dp)
        var dark by mutableStateOf(false)
        rule.setContent {
            Theme(preset = StylePreset.Nova, fontFamily = catalogFontFamily(), isDark = dark, minTouchTarget = null) {
                Box(Modifier.width(width)) { attachmentDoc.examples.first().preview() }
            }
        }
        for ((w, night) in listOf(638.dp to false, 340.dp to false, 340.dp to true, 638.dp to true)) {
            rule.runOnIdle {
                width = w
                dark = night
            }
            val photo = rule.onNodeWithContentDescription("Workspace")
            photo.assertIsDisplayed()
            rule.onNodeWithContentDescription("Desk").assertExists()
            rule.onNodeWithContentDescription("Office").assertExists()
            val bounds = photo.getUnclippedBoundsInRoot()
            assertEquals(102.dp, (bounds.right - bounds.left))
            assertEquals(102.dp, (bounds.bottom - bounds.top))
            val image = photo.captureToImage().toPixelMap()
            val colors =
                (8 until image.height - 8 step 8).flatMap { y ->
                    (8 until image.width - 8 step 8).map { x -> image[x, y] }
                }.toSet()
            assertTrue(colors.size > 40, "Workspace must paint a photographic image rather than a flat placeholder")
            rule.onNodeWithText("Uploading · 64%").assertExists()
            rule.onNodeWithContentDescription("Cancel upload").assertHasClickAction()
        }
    }

    @Test fun statesRenderAllFiveAndErrorDescriptionInheritsItsColor() {
        var dark by mutableStateOf(false)
        var width by mutableStateOf(340.dp)
        var error = Color.Unspecified
        var card = Color.Unspecified
        rule.setContent {
            Theme(preset = StylePreset.Nova, fontFamily = catalogFontFamily(), isDark = dark, minTouchTarget = null) {
                error = currentTheme.colors.destructive
                card = currentTheme.colors.card
                Box(Modifier.width(width)) { attachmentDoc.examples.single { it.title == "States" }.preview() }
            }
        }
        for ((w, night) in listOf(340.dp to false, 638.dp to false, 638.dp to true, 340.dp to true)) {
            rule.runOnIdle {
                width = w
                dark = night
            }
            for (file in listOf(
                "selected-file.pdf",
                "design-system.zip",
                "market-research.pdf",
                "financial-model.xlsx",
                "uploaded-report.pdf",
            )) {
                rule.onNodeWithText(file).assertExists()
            }
            rule.onNodeWithContentDescription("Retry upload").assertIsDisplayed().assertHasClickAction()
            val image = rule.onNodeWithText("Upload failed. Try again.").captureToImage().toPixelMap()
            val expected = error.copy(alpha = .8f).compositeOver(card)
            assertTrue(
                (0 until image.height).any { y ->
                    (0 until image.width).any { x ->
                        val c = image[x, y]
                        abs(c.red - expected.red) < .04f &&
                            abs(c.green - expected.green) < .04f &&
                            abs(c.blue - expected.blue) < .04f
                    }
                },
                "The error row must inherit destructive text without an explicit isError on the description",
            )
        }
    }

    @Test fun imageActionsDoNotActivateTheImageLink() {
        val opened = mutableListOf<String>()
        val handler =
            object : UriHandler {
                override fun openUri(uri: String) {
                    opened += uri
                }
            }
        rule.setContent {
            Theme(preset = StylePreset.Nova, fontFamily = catalogFontFamily(), minTouchTarget = null) {
                CompositionLocalProvider(LocalUriHandler provides handler) {
                    Box(Modifier.width(638.dp)) { attachmentDoc.examples.single { it.title == "Image" }.preview() }
                }
            }
        }
        rule.onNodeWithContentDescription("Remove workspace.png").performTouchInput { click(center) }
        assertTrue(opened.isEmpty())
        rule.onNodeWithContentDescription("Open workspace.png").performTouchInput { click(center) }
        assertEquals(
            listOf("https://images.unsplash.com/photo-1497366754035-f200968a6e72?w=900&auto=format&fit=crop&q=80"),
            opened,
        )
    }

    @Test fun triggerOpensDialogByKeyboardWhileActionsStayIndependent() {
        rule.setContent {
            Theme(preset = StylePreset.Nova, fontFamily = catalogFontFamily(), minTouchTarget = null) {
                OverlayHost {
                    Box(
                        Modifier.width(638.dp),
                    ) { attachmentDoc.examples.single { it.title == "Trigger" }.preview() }
                }
            }
        }
        val dialogText = (
            "The attachment trigger fills the card and opens the dialog, " +
                "while the actions stay independently clickable above it."
        )
        rule.onNodeWithContentDescription("Copy link").performTouchInput { click(center) }
        rule.onNodeWithContentDescription("Remove research-summary.pdf").performTouchInput { click(center) }
        rule.onNodeWithText(dialogText).assertDoesNotExist()
        val trigger = rule.onNodeWithContentDescription("Preview research-summary.pdf")
        trigger.requestFocus()
        trigger.performKeyInput { pressKey(Key.Enter) }
        rule.onNodeWithText(dialogText).assertIsDisplayed()
        rule.onAllNodes(isRoot()).onLast().performKeyInput { pressKey(Key.Escape) }
        rule.onNodeWithText(dialogText).assertDoesNotExist()
        trigger.assertIsFocused()
    }

    @Test fun fullPreviewKeepsOuterPaddingAndUpdatesItsSurfaceAcrossThemes() {
        var dark by mutableStateOf(false)
        var background = Color.Unspecified
        val example = attachmentDoc.examples.first()
        rule.setContent {
            Theme(preset = StylePreset.Nova, fontFamily = catalogFontFamily(), isDark = dark, minTouchTarget = null) {
                background = currentTheme.colors.background
                Box(Modifier.width(640.dp)) {
                    PreviewCodeSection(
                        example.code,
                        modifier = Modifier.testTag("preview"),
                        minPreviewHeight = example.minPreviewHeight,
                        previewPadding = example.previewPadding,
                        previewBackground = example.previewBackground,
                        preview = example.preview,
                    )
                }
            }
        }
        for (night in listOf(false, true, false)) {
            rule.runOnIdle { dark = night }
            val image = rule.onNodeWithTag("preview").captureToImage().toPixelMap()
            assertEquals(if (night) background else Color(0xFFF8F8F8), image[20, 20])
            val photo = rule.onNodeWithContentDescription("Workspace").getUnclippedBoundsInRoot()
            assertEquals(102.dp, photo.top)
            assertEquals(137.dp, photo.left)
        }
    }

    @Test fun sizeRecipeInherits14Then12SpLabels() {
        rule.setContent {
            Theme(preset = StylePreset.Nova, fontFamily = catalogFontFamily(), minTouchTarget = null) {
                Box(Modifier.width(340.dp)) { attachmentDoc.examples.single { it.title == "Sizes" }.preview() }
            }
        }
        for ((title, expected) in listOf(
            "Default attachment" to 14f,
            "Small attachment" to 12f,
            "Extra small attachment" to 12f,
        )) {
            val result = mutableListOf<TextLayoutResult>()
            rule.onNodeWithText(title).performSemanticsAction(SemanticsActions.GetTextLayoutResult) { it(result) }
            assertEquals(expected, result.single().layoutInput.style.fontSize.value, .1f)
            assertEquals(1, result.single().lineCount)
        }
    }
}
