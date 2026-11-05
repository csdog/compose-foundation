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
import com.toutiaoyan.compose.ui.catalog.docs.componentPageMarkdown
import com.toutiaoyan.compose.ui.catalog.docs.tableDoc
import com.toutiaoyan.compose.ui.catalog.docs.ApiReference
import com.toutiaoyan.compose.ui.catalog.docs.ApiProperty
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
class TableExamplesTest(private val viewport: Int, private val dark: Boolean) {
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
        rule.waitForIdle()
        rule.mainClock.advanceTimeBy(320)
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
                        Modifier.width(minOf(viewport, 640).dp).height(640.dp)
                            .background(currentTheme.colors.background)
                            .padding(if (title == "RTL") 0.dp else 24.dp),
                        contentAlignment = Alignment.Center,
                    ) {
                        if (title == "API") {
                            ApiReference(listOf(ApiProperty("onValueChange", "(String) -> Unit", "{}")))
                        } else {
                            tableDoc.examples.single { it.title == title }.preview()
                        }
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
        val file = File("build/reports/table-recipes/$name-$viewport-${if (dark) "dark" else "light"}.png")
        file.parentFile.mkdirs()
        ImageIO.write(node("table-example").captureToImage().toAwtImage(), "png", file)
    }

    @Test fun pageKeepsOfficialRecipeOrderCompositionAndKotlinExtensions() {
        assertEquals(
            listOf("Default", "Footer", "Actions", "Data Table", "RTL"),
            tableDoc.examples.take(5).map { it.title },
        )
        assertTrue(
            tableDoc.examples.drop(5).all { it.title.startsWith("Kotlin extension: ") },
        )
        assertEquals(null, tableDoc.examplesHeading)
        val markdown = componentPageMarkdown(tableDoc)
        val order = listOf(
            "## Composition",
            "## Footer",
            "## Actions",
            "## Data Table",
            "## RTL",
            "## API Reference",
        ).map { markdown.indexOf(it) }
        assertTrue(order.all { it >= 0 }, "missing a section heading: $order")
        assertEquals(order.sorted(), order)
        assertContains(markdown, "Use the `TableFooter` component to add a footer to the table.")
        assertContains(markdown, "A table showing actions for each row using a `DropdownMenu` component.")
        assertFalse(markdown.contains("## Examples"))
    }

    @Test fun defaultHasAllSevenInvoicesAndSourceFooter() {
        show("Default")
        repeat(7) { rule.onNodeWithText("INV00${it + 1}").assertIsDisplayed() }
        rule.onNodeWithText("$2,500.00").assertIsDisplayed()
        val captionText = rule.onNodeWithText("A list of your recent invoices.").fetchSemanticsNode().boundsInRoot
        assertEquals(bounds("table-total").bottom + 16f, captionText.top, .1f)
        capture("default")
    }

    @Test fun footerRecipeHasThreeInvoicesButKeepsSourceTotal() {
        show("Footer")
        rule.onNodeWithText("INV003").assertIsDisplayed()
        rule.onNodeWithText("INV004").assertDoesNotExist()
        rule.onNodeWithText("$2,500.00").assertIsDisplayed()
        capture("footer")
    }

    @Test fun actionsUseRealMenusAndDismissAfterSelection() {
        show("Actions")
        rule.onNodeWithText("Wireless Mouse").assertIsDisplayed()
        rule.onNodeWithText("Mechanical Keyboard").assertIsDisplayed()
        rule.onNodeWithText("USB-C Hub").assertIsDisplayed()
        node("table-menu-0").assertWidthIsEqualTo(32.dp).assertHeightIsEqualTo(32.dp).performClick()
        advance()
        rule.onNodeWithText("Edit").assertIsDisplayed()
        rule.onNodeWithText("Duplicate").assertIsDisplayed()
        rule.onNodeWithText("Delete").assertIsDisplayed().performClick()
        advance()
        rule.onNodeWithText("Delete").assertDoesNotExist()
        rule.onNodeWithText("Wireless Mouse").assertIsDisplayed()
        capture("actions")
    }

    private fun assertDirection(rtl: Boolean) {
        val invoice = rule.onNodeWithText("INV001").fetchSemanticsNode().positionInRoot.x
        val amount = rule.onNodeWithText("$250.00").fetchSemanticsNode().positionInRoot.x
        assertEquals(rtl, invoice > amount, "Invoice and Amount must mirror with the selected direction")
    }

    @Test fun rtlArabicUsesTranslatedRows() {
        show("RTL")
        rule.onNodeWithText("الفاتورة").assertExists()
        rule.onNodeWithText("المجموع").assertExists()
        rule.onNodeWithText("قائمة بفواتيرك الأخيرة.").assertIsDisplayed()
        assertDirection(rtl = true)
        capture("rtl-arabic")
    }

    private fun language(label: String) {
        rule.onNodeWithContentDescription("Preview language").performClick()
        advance()
        rule.onNodeWithText(label).performClick()
        advance()
    }

    @Test fun rtlEnglishSelectorChangesLabelsAndDirection() {
        show("RTL")
        language("English")
        rule.onNodeWithText("Invoice").assertIsDisplayed()
        rule.onNodeWithText("Total").assertIsDisplayed()
        assertDirection(rtl = false)
        capture("rtl-english")
    }

    @Test fun rtlHebrewSelectorChangesLabelsAndDirection() {
        show("RTL")
        language("Hebrew (עברית)")
        rule.onNodeWithText("חשבונית").assertExists()
        rule.onNodeWithText("רשימת החשבוניות האחרונות שלך.").assertIsDisplayed()
        assertDirection(rtl = true)
        capture("rtl-hebrew")
    }
}
