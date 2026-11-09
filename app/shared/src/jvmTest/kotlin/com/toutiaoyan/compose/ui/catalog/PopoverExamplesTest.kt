package com.toutiaoyan.compose.ui.catalog

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.toAwtImage
import androidx.compose.ui.input.key.Key
import androidx.compose.ui.platform.*
import androidx.compose.ui.test.*
import androidx.compose.ui.test.junit4.v2.createComposeRule
import androidx.compose.ui.unit.*
import com.toutiaoyan.compose.ui.catalog.docs.popoverDoc
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
class PopoverExamplesTest(private val viewport: Int, private val dark: Boolean) {
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
                        Modifier.width((if (viewport >= 640) 640 else viewport).dp).height(480.dp)
                            .background(currentTheme.colors.background).padding(24.dp),
                        contentAlignment = Alignment.Center,
                    ) {
                        popoverDoc.examples.single { it.title == title }.preview()
                    }
                }
            }
        }
        advance()
    }

    private fun open(label: String) {
        rule.onNodeWithText(label).performMouseInput { moveTo(center) }
        rule.onNodeWithText(label).performClick()
        advance()
    }

    private fun dismiss() {
        rule.onNodeWithTag("popover-panel").performKeyInput { pressKey(Key.Escape) }
        advance()
        rule.onNodeWithTag("popover-panel").assertDoesNotExist()
    }

    private fun capture(name: String) {
        val file = File("build/reports/popover-recipes/$name-$viewport-${if (dark) "dark" else "light"}.png")
        file.parentFile.mkdirs()
        ImageIO.write(rule.onNodeWithTag("popover-panel").captureToImage().toAwtImage(), "png", file)
    }

    @Test fun officialRecipesComeFirstAndExportStandaloneCode() {
        assertEquals("Use the following composition to build a `Popover`:", popoverDoc.beforeCompositionMarkdown)
        val examples = popoverDoc.examples.take(5)
        assertEquals(listOf("Default", "Basic", "Align", "With Form", "RTL"), examples.map { it.title })
        assertEquals(
            "A simple popover with a header, title, and description.",
            popoverDoc.examples.single { it.title == "Basic" }.descriptionMarkdown,
        )
        assertTrue(
            popoverDoc.examples.single { it.title == "Align" }.descriptionMarkdown.orEmpty()
                .contains("`align` prop"),
        )
        assertEquals(
            "A popover with form fields inside.",
            popoverDoc.examples.single { it.title == "With Form" }.descriptionMarkdown,
        )
        assertTrue(
            popoverDoc.examples.single { it.title == "RTL" }.descriptionMarkdown.orEmpty()
                .startsWith("To enable RTL support"),
        )
        assertTrue(popoverDoc.examples.drop(5).all { it.title.startsWith("Kotlin extension: ") })
        examples.forEach {
            assertContains(it.code, "import com.toutiaoyan.compose.ui.components.popover.*")
            assertFalse(it.code.contains("RtlPreview"))
            assertFalse(it.code.contains("popoverDoc"))
        }
    }

    @Test fun defaultHasFourEditableGridInputsAndRestartsValuesAfterClose() {
        show("Default")
        open("Open popover")
        rule.onNodeWithTag("popover-panel").assertWidthIsEqualTo(320.dp).assertHeightIsEqualTo(230.dp)
        listOf("Width" to "100%", "Max. width" to "300px", "Height" to "25px", "Max. height" to "none")
            .forEach { (label, value) ->
                rule.onNodeWithContentDescription(label).assertTextEquals(value)
                rule.onNodeWithTag("popover-row-" + label).assertHeightIsEqualTo(32.dp)
            }
        rule.onNodeWithText("Max. width").performClick()
        rule.onNodeWithContentDescription("Max. width").assertIsFocused().performTextReplacement("480px")
        advance()
        rule.onNodeWithContentDescription("Max. width").assertTextEquals("480px")
        capture("default-edited")
        dismiss()
        open("Open popover")
        rule.onNodeWithContentDescription("Max. width").assertTextEquals("300px")
    }

    @Test fun basicHasOnlyTheSourceHeaderAndStartAlignment() {
        show("Basic")
        open("Open Popover")
        rule.onNodeWithTag("popover-panel").assertWidthIsEqualTo(288.dp).assertHeightIsEqualTo(62.dp)
        rule.onNodeWithText("Dimensions").assertIsDisplayed()
        rule.onNodeWithText("Set the dimensions for the layer.").assertIsDisplayed()
        rule.onNode(hasSetTextAction()).assertDoesNotExist()
        capture("basic")
        dismiss()
    }

    @Test fun alignUsesSmallTriggersAndThreeIndependent160PixelPanels() {
        show("Align")
        listOf("Start", "Center", "End").forEach { label ->
            rule.onNodeWithText(label).assertHeightIsEqualTo(28.dp)
            open(label)
            rule.onNodeWithTag("popover-panel").assertWidthIsEqualTo(160.dp)
            rule.onNodeWithText("Aligned to " + label.lowercase()).assertIsDisplayed()
            capture("align-" + label.lowercase())
            dismiss()
        }
    }

    @Test fun formHasTwoHorizontalFieldsWithClickableLabelsAndEditableValues() {
        show("With Form")
        open("Open Popover")
        rule.onNodeWithTag("popover-panel").assertWidthIsEqualTo(256.dp).assertHeightIsEqualTo(152.dp)
        rule.onNodeWithText("Width").performClick()
        rule.onNodeWithContentDescription("Width").assertIsFocused().performTextReplacement("80%")
        rule.onNodeWithText("Height").performClick()
        rule.onNodeWithContentDescription("Height").assertIsFocused().performTextReplacement("48px")
        advance()
        rule.onNodeWithContentDescription("Width").assertTextEquals("80%")
        rule.onNodeWithContentDescription("Height").assertTextEquals("48px")
        val label = rule.onNodeWithText("Height").getUnclippedBoundsInRoot()
        val input = rule.onNodeWithContentDescription("Height").getUnclippedBoundsInRoot()
        assertEquals((label.top.value + label.bottom.value) / 2, (input.top.value + input.bottom.value) / 2, 0.5f)
        assertTrue(label.right <= input.left)
        capture("form-edited")
        dismiss()
    }

    @Test fun rtlChangesAllSixTriggersAndPopupTextAcrossThreeLanguages() {
        show("RTL")
        val arabic = listOf("يسار", "أعلى", "أسفل", "يمين", "بداية السطر", "نهاية السطر")
        arabic.forEach { label ->
            open(label)
            rule.onNodeWithText("الأبعاد").assertIsDisplayed()
            rule.onNodeWithText("تعيين الأبعاد للطبقة.").assertIsDisplayed()
            if (label == arabic.last()) capture("rtl-arabic")
            dismiss()
        }
        rule.onNodeWithText("Arabic (العربية)").performClick()
        advance()
        rule.onNodeWithText("English").performClick()
        advance()
        listOf("Left", "Top", "Bottom", "Right", "Inline Start", "Inline End").forEach {
            rule.onNodeWithText(it).assertIsDisplayed()
        }
        open("Inline Start")
        rule.onNodeWithText("Dimensions").assertIsDisplayed()
        capture("rtl-english")
        dismiss()
        rule.onNodeWithText("English").performClick()
        advance()
        rule.onNodeWithText("Hebrew (עברית)").performClick()
        advance()
        listOf("שמאל", "למעלה", "למטה", "ימין", "תחילת השורה", "סוף השורה").forEach {
            rule.onNodeWithText(it).assertIsDisplayed()
        }
        open("סוף השורה")
        rule.onNodeWithText("מימדים").assertIsDisplayed()
        rule.onNodeWithText("הגדר את המימדים לשכבה.").assertIsDisplayed()
        capture("rtl-hebrew")
        dismiss()
    }
}
