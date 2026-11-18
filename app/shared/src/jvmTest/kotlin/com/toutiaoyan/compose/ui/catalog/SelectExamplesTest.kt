package com.toutiaoyan.compose.ui.catalog

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.width
import androidx.compose.ui.Modifier
import androidx.compose.ui.semantics.SemanticsProperties
import androidx.compose.ui.input.key.Key
import androidx.compose.ui.test.*
import androidx.compose.ui.test.junit4.v2.createComposeRule
import androidx.compose.ui.unit.dp
import com.toutiaoyan.compose.ui.catalog.docs.componentPageMarkdown
import com.toutiaoyan.compose.ui.catalog.docs.selectDoc
import com.toutiaoyan.compose.ui.components.overlay.OverlayHost
import com.toutiaoyan.compose.ui.theme.Theme
import com.toutiaoyan.compose.ui.tokens.StylePreset
import org.junit.Rule
import kotlin.test.*

class SelectExamplesTest {
    @get:Rule val rule = createComposeRule()

    private fun show(title: String) {
        rule.setContent {
            Theme(preset = StylePreset.Nova, minTouchTarget = null) {
                OverlayHost {
                    Box(Modifier.width(560.dp).height(600.dp)) {
                        selectDoc.examples.single { it.title == title }.preview()
                    }
                }
            }
        }
    }

    @Test fun officialRecipesKeepOfficialOrderAndKotlinExtensionsComeAfter() {
        assertEquals(
            listOf(
                "Default",
                "Align Item With Trigger",
                "Groups",
                "Scrollable",
                "Disabled",
                "Invalid",
                "RTL",
            ),
            selectDoc.examples.take(7).map { it.title },
        )
        assertTrue(
            selectDoc.examples.drop(7).all { it.title.startsWith("Kotlin extension: ") },
        )
        assertEquals(
            "Displays a list of options for the user to pick from—triggered by a button.",
            selectDoc.description,
        )
        assertEquals(null, selectDoc.examplesHeading)
        val markdown = componentPageMarkdown(selectDoc)
        val order =
            listOf(
                "## Align Item With Trigger",
                "## Groups",
                "## Scrollable",
                "## Disabled",
                "## Invalid",
                "## RTL",
            ).map { markdown.indexOf(it) }
        assertTrue(order.all { it >= 0 }, "missing a recipe heading: $order")
        assertEquals(order.sorted(), order)
        assertContains(markdown, "alignItemWithTrigger")
        assertContains(markdown, "Use the following composition to build a `Select`:")
        assertFalse(markdown.contains("## Examples"))
    }

    @Test fun demoOpensAndSelectsAFruit() {
        show("Default")
        rule.onNodeWithText("Select a fruit").performClick()
        rule.onNodeWithText("Apple").assertIsDisplayed().performClick()
        rule.onNodeWithText("Apple").assertIsDisplayed()
    }

    @Test fun groupsSeparateFruitsAndVegetables() {
        show("Groups")
        rule.onNodeWithText("Select a fruit").performClick()
        rule.onNodeWithText("Fruits").assertIsDisplayed()
        rule.onNodeWithText("Vegetables").assertIsDisplayed()
        rule.onNodeWithText("Carrot").performClick()
        rule.onNodeWithText("Carrot").assertIsDisplayed()
    }

    @Test fun scrollableListOpensWithLaterGroups() {
        show("Scrollable")
        rule.onNodeWithText("Select a timezone").performClick()
        rule.onNodeWithText("North America").assertIsDisplayed()
        rule.onAllNodesWithText("South America").onFirst().performScrollTo()
        rule.onNodeWithText("Chile Standard Time").assertExists()
    }

    @Test fun disabledSelectCannotOpen() {
        show("Disabled")
        rule.onNodeWithText("Select a fruit").assertIsNotEnabled()
        rule.onNodeWithText("Select a fruit").performClick()
        rule.onNodeWithText("Apple").assertDoesNotExist()
    }

    @Test fun invalidFieldShowsDestructiveState() {
        show("Invalid")
        rule.onNodeWithText("Fruit").assertIsDisplayed()
        rule.onNodeWithText("Select a fruit").assertIsDisplayed()
    }

    @Test fun rtlPreviewSwitchesBetweenTranslations() {
        show("RTL")
        for ((label, placeholder) in listOf(
            "Arabic (العربية)" to "اختر فاكهة",
            "English" to "Select a fruit",
            "Hebrew (עברית)" to "בחר פרי",
        )) {
            rule.onNodeWithContentDescription("Preview language").performClick()
            rule.onAllNodesWithText(label).onLast().performClick()
            rule.onNodeWithText(placeholder).assertIsDisplayed()
        }
    }

    @Test fun invalidRecipeIncludesSourceErrorAndAllThreeOptions() {
        show("Invalid")
        rule.onNodeWithText("Please select a fruit.").assertIsDisplayed()
        rule.onNodeWithText("Select a fruit").performClick()
        rule.onNodeWithText("Blueberry").assertIsDisplayed().performClick()
        rule.onNodeWithText("Blueberry").assertIsDisplayed()
    }

    @Test fun alignmentRecipeIncludesSourceDescriptionAndClearOption() {
        show("Align Item With Trigger")
        rule.onNodeWithText("Toggle to align the item with the trigger.").assertIsDisplayed()
        rule.onNodeWithText("Banana").performClick()
        rule.onNodeWithText("Select a fruit").performClick()
        rule.onNodeWithText("Select a fruit").assertIsDisplayed()
    }

    @Test fun rtlRecipeContainsBothSourceGroupsAndAllOptionsInEachLanguage() {
        show("RTL")
        for ((language, placeholder, vegetable, lastFruit, lastVegetable) in listOf(
            listOf("Arabic (العربية)", "اختر فاكهة", "الخضروات", "أناناس", "سبانخ"),
            listOf("English", "Select a fruit", "Vegetables", "Pineapple", "Spinach"),
            listOf("Hebrew (עברית)", "בחר פרי", "ירקות", "אננס", "תרד"),
        )) {
            rule.onNodeWithContentDescription("Preview language").performClick()
            rule.onAllNodesWithText(language).onLast().performClick()
            rule.onNodeWithText(placeholder).performClick()
            rule.onNodeWithText(lastFruit).assertExists()
            rule.onNodeWithText(vegetable).assertExists()
            rule.onNodeWithText(lastVegetable).performScrollTo().performClick()
            rule.onNodeWithText(lastVegetable).assertIsDisplayed()
        }
    }

    @Test fun alignItemFieldStretchesTriggerToSourceGroupWidth() {
        show("Align Item With Trigger")
        rule.onNodeWithText("Banana").assertWidthIsEqualTo(320.dp)
        rule.onNodeWithText("Align Item").performClick()
        rule.onNodeWithContentDescription("Align Item").assertIsOff()
    }

    @Test fun invalidFieldHasSourceWidthNameAndClearOption() {
        show("Invalid")
        rule.onNodeWithContentDescription("Fruit").assertWidthIsEqualTo(192.dp).performClick()
        rule.onNodeWithText("Apple").performClick()
        rule.onNodeWithContentDescription("Fruit").performClick()
        rule.onAllNodesWithText("Apple").onLast().assertIsFocused().performKeyInput {
            pressKey(Key.MoveHome)
            pressKey(Key.Enter)
        }
        rule.onNodeWithContentDescription("Fruit").assert(hasText("Select a fruit"))
    }

    @Test fun copyCodeAndApiSectionsFollowTheCompiledPageOrder() {
        val markdown = componentPageMarkdown(selectDoc)
        val rtl = markdown.indexOf("## RTL")
        val api = markdown.indexOf("## API Reference")
        val extension = markdown.indexOf("## Kotlin extension: Uncontrolled")
        val kotlinApi = markdown.indexOf("## Kotlin extension: API")
        assertTrue(rtl < api && api < extension && extension < kotlinApi)
        assertEquals(41, selectDoc.examples.size)
        selectDoc.examples.forEach { example ->
            assertContains(example.code, "@Composable", message = example.title)
            assertContains(example.code, "import androidx.compose.runtime.*", message = example.title)
            assertFalse(example.code.contains("catalogArabicFontFamily"), example.title)
            assertFalse(example.code.contains("catalogHebrewFontFamily"), example.title)
            assertFalse(example.code.contains("controller.setOpen(false)"), example.title)
        }
        assertContains(selectDoc.usageImportsCode.orEmpty(), "import androidx.compose.runtime.*")
        assertContains(selectDoc.usageCode, "@Composable")
        assertContains(
            selectDoc.examples.single { it.title == "Kotlin extension: Scroll buttons" }.code,
            "Option ${'$'}it",
        )
    }

    @Test fun defaultOpenExampleMountsOnlyWhenLaunchedAndOpensImmediately() {
        show("Kotlin extension: Default open")
        rule.onNodeWithText("Light").assertDoesNotExist()
        rule.onNodeWithText("Show default-open select").performClick()
        rule.onNodeWithText("Light").assertIsDisplayed().performClick()
        rule.onNodeWithText("Light").assertIsDisplayed()
    }

    private fun assertScrollControls(
        title: String,
        initial: Int,
        up: Boolean = false,
    ) {
        show("Kotlin extension: $title")
        rule.onNodeWithText("Option $initial").performClick()
        val controls = SemanticsMatcher.keyIsDefined(SemanticsProperties.HideFromAccessibility)
        rule.onAllNodes(controls).assertCountEquals(if (initial == 0) 1 else 2)
        val observed = if (initial == 0) 3 else 19
        val before = rule.onNodeWithText("Option $observed").fetchSemanticsNode().boundsInWindow.top
        val control = if (up) rule.onAllNodes(controls).onFirst() else rule.onAllNodes(controls).onLast()
        control.performTouchInput { click() }
        val after = rule.onNodeWithText("Option $observed").fetchSemanticsNode().boundsInWindow.top
        assertTrue(if (up) after > before else after < before, "the scroll control must move the overflowing list")
        rule.onNodeWithText("Option 39").performScrollTo().assertIsDisplayed()
    }

    @Test fun scrollButtonsExampleActuallyOverflowsAndScrolls() = assertScrollControls("Scroll buttons", 20)

    @Test fun scrollUpExampleStartsWithAnUpControl() = assertScrollControls("Scroll up button", 20, up = true)

    @Test fun scrollDownExampleStartsWithADownControl() = assertScrollControls("Scroll down button", 0)
}
