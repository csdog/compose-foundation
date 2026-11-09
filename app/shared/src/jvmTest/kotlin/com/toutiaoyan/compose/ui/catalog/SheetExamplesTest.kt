package com.toutiaoyan.compose.ui.catalog

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.toAwtImage
import androidx.compose.ui.platform.*
import androidx.compose.ui.test.*
import androidx.compose.ui.test.junit4.v2.createComposeRule
import androidx.compose.ui.unit.*
import com.toutiaoyan.compose.ui.catalog.docs.componentPageMarkdown
import com.toutiaoyan.compose.ui.catalog.docs.sheetDoc
import com.toutiaoyan.compose.ui.catalog.docs.SheetRtlRecipe
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
class SheetExamplesTest(private val viewport: Int, private val dark: Boolean) {
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
        rule.mainClock.advanceTimeBy(320)
        rule.waitForIdle()
    }

    private fun show(
        title: String,
        language: Int? = null,
    ) {
        rule.mainClock.autoAdvance = false
        rule.setContent {
            val original = LocalWindowInfo.current
            val density = LocalDensity.current
            val window =
                remember(original, density) {
                    object : WindowInfo by original {
                        override val containerSize =
                            with(density) { IntSize(viewport.dp.roundToPx(), 768.dp.roundToPx()) }
                    }
                }
            CompositionLocalProvider(LocalWindowInfo provides window) {
                Theme(preset = StylePreset.Nova, isDark = dark, minTouchTarget = null) {
                    Box(
                        Modifier.width(minOf(viewport, 640).dp).height(480.dp)
                            .background(currentTheme.colors.background).padding(24.dp),
                        contentAlignment = Alignment.Center,
                    ) {
                        if (language != null) {
                            SheetRtlRecipe(language)
                        } else {
                            sheetDoc.examples.single { it.title == title }.preview()
                        }
                    }
                }
            }
        }
        advance()
    }

    private fun panel() = rule.onNodeWithTag("sheet-panel", useUnmergedTree = true)

    private fun capture(name: String) {
        val file = File("build/reports/sheet-recipes/$name-$viewport-${if (dark) "dark" else "light"}.png")
        file.parentFile.mkdirs()
        ImageIO.write(panel().captureToImage().toAwtImage(), "png", file)
    }

    private fun click(text: String) {
        rule.onNodeWithText(text).performClick()
        advance()
    }

    @Test fun defaultHasEditableSourceFieldsLabelFocusAndExplicitClose() {
        show("Default")
        click("Open")
        panel().assertWidthIsEqualTo((if (viewport < 640) viewport * .75f else 384f).dp)
        rule.onNodeWithText("Edit profile").assertIsDisplayed()
        rule.onNodeWithText("Make changes to your profile here. Click save when you're done.").assertIsDisplayed()
        rule.onNodeWithTag("sheet-name").assertTextContains("Pedro Duarte").assertIsFocused()
        rule.onNodeWithTag("sheet-username").assertTextContains("@peduarte")
        capture("default")
        rule.onNodeWithTag("sheet-username").performTextReplacement("@ada")
        rule.onNode(hasClickAction() and hasAnyDescendant(hasText("Name"))).performClick()
        advance()
        rule.onNodeWithTag("sheet-name").assertIsFocused().performTextReplacement("Ada")
        click("Save changes")
        panel().assertExists()
        rule.onNodeWithTag("sheet-name").assertTextContains("Ada")
        click("Close")
        panel().assertDoesNotExist()
        click("Open")
        rule.onNodeWithTag("sheet-name").assertTextContains("Pedro Duarte")
    }

    @Test fun fourPhysicalSidesHaveScrollableLargeContentAndVerticalHalfViewportLimit() {
        show("Side")
        for (side in listOf("Top", "Right", "Bottom", "Left")) {
            click(side)
            val bounds = panel().getUnclippedBoundsInRoot()
            if (side == "Top" || side == "Bottom") {
                panel().assertHeightIsEqualTo(384.dp).assertWidthIsEqualTo(1024.dp)
                if (side == "Top") assertEquals(0.dp, bounds.top) else assertEquals(768.dp, bounds.bottom)
            } else {
                panel().assertHeightIsEqualTo(768.dp)
                if (side == "Right") assertEquals(1024.dp, bounds.right) else assertEquals(0.dp, bounds.left)
            }
            rule.onAllNodes(hasTestTag("sheet-paragraph-9"), useUnmergedTree = true).assertCountEquals(1)
            capture(side.lowercase())
            // performScrollTo loops until scrolling settles; this fixture advances its clock explicitly.
            rule.onNodeWithTag("sheet-scroll").performSemanticsAction(
                androidx.compose.ui.semantics.SemanticsActions.ScrollBy,
            ) { it(0f, 100000f) }
            rule.mainClock.advanceTimeBy(1000)
            rule.waitForIdle()
            rule.onNodeWithTag("sheet-paragraph-9").assertIsDisplayed()
            click("Save changes")
            panel().assertExists()
            click("Cancel")
            panel().assertDoesNotExist()
        }
    }

    @Test fun noCloseButtonUsesExactSourceCopyAndOutsideDismiss() {
        show("No Close Button")
        click("Open Sheet")
        rule.onNodeWithContentDescription("Close").assertDoesNotExist()
        rule.onNodeWithText("This sheet doesn't have a close button in the top-right corner. Click outside to close.")
            .assertIsDisplayed()
        capture("no-close")
        val roots = rule.onAllNodes(isRoot())
        roots[roots.fetchSemanticsNodes().lastIndex].performMouseInput { click(Offset(10f, 700f)) }
        advance()
        panel().assertDoesNotExist()
    }

    private fun rtl(
        language: Int,
        open: String,
        name: String,
        username: String,
        save: String,
        close: String,
    ) {
        show("RTL", language)
        click(open)
        val bounds = panel().getUnclippedBoundsInRoot()
        if (language == 0) assertEquals(1024.dp, bounds.right) else assertEquals(0.dp, bounds.left)
        rule.onNodeWithContentDescription(name).assertTextContains("Pedro Duarte").assertIsFocused()
        rule.onNodeWithContentDescription(username).assertTextContains("peduarte")
        capture("rtl-$language")
        rule.onNodeWithContentDescription(username).performTextReplacement("ada")
        click(save)
        panel().assertExists()
        click(close)
        panel().assertDoesNotExist()
    }

    @Test fun rtlEnglish() = rtl(0, "Open", "Name", "Username", "Save changes", "Close")

    @Test fun rtlArabic() = rtl(1, "فتح", "الاسم", "اسم المستخدم", "حفظ التغييرات", "إغلاق")

    @Test fun rtlHebrew() = rtl(2, "פתח", "שם", "שם משתמש", "שמור שינויים", "סגור")

    @Test fun copiedRecipesContainTheirHelpersAndCurrentSourceBehaviors() {
        assertEquals(listOf("Default", "Side", "No Close Button", "RTL"), sheetDoc.examples.take(4).map { it.title })
        for (title in listOf("Default", "Side", "No Close Button", "RTL")) {
            val code = sheetDoc.examples.single { it.title == title }.code
            assertTrue(code.contains("import androidx.compose.runtime.*"))
            assertTrue(code.contains("fun SheetProfileInput"))
            assertTrue(code.contains("SheetContent"))
        }
        assertTrue(sheetDoc.examples.single { it.title == "Side" }.code.contains("repeat(10)"))
        assertTrue(sheetDoc.examples.single { it.title == "Side" }.code.contains("viewportHeight / 2"))
        assertTrue(sheetDoc.examples.single { it.title == "RTL" }.code.contains("peduarte"))
    }

    @Test fun exportKeepsOfficialSectionsCompositionAndKotlinExtensions() {
        assertEquals(
            listOf("Default", "Side", "No Close Button", "RTL"),
            sheetDoc.examples.take(4).map { it.title },
        )
        assertTrue(sheetDoc.examples.drop(4).all { it.title.startsWith("Kotlin extension: ") })
        assertEquals(null, sheetDoc.examplesHeading)
        val markdown = componentPageMarkdown(sheetDoc)
        val order =
            listOf(
                "## Composition",
                "## Side",
                "## No Close Button",
                "## RTL",
                "## API Reference",
            ).map { markdown.indexOf(it) }
        assertTrue(order.all { it >= 0 }, "missing a section heading: $order")
        assertEquals(order.sorted(), order)
        assertContains(markdown, "Use the `side` prop on `SheetContent`")
        assertContains(markdown, "showCloseButton = false")
        assertContains(markdown, "Kotlin extension: Side")
        assertFalse(markdown.contains("## Examples"))
        assertContains(markdown, "Default variant unless overridden")
        assertContains(markdown, "`SheetClose` renders an icon button")
        assertContains(markdown, "Footer stacks the actions")
        assertContains(markdown, "trigger slot overload")
    }
}
