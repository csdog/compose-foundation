package com.toutiaoyan.compose.ui.catalog

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.width
import androidx.compose.ui.Modifier
import androidx.compose.ui.semantics.SemanticsProperties
import androidx.compose.ui.test.*
import androidx.compose.ui.test.junit4.v2.createComposeRule
import androidx.compose.ui.unit.dp
import com.toutiaoyan.compose.ui.catalog.docs.componentPageMarkdown
import com.toutiaoyan.compose.ui.catalog.docs.toggleDoc
import com.toutiaoyan.compose.ui.components.overlay.OverlayHost
import com.toutiaoyan.compose.ui.theme.Theme
import com.toutiaoyan.compose.ui.tokens.StylePreset
import org.junit.Rule
import kotlin.test.*

class ToggleExamplesTest {
    @get:Rule val rule = createComposeRule()

    private fun show(title: String) {
        rule.setContent {
            Theme(preset = StylePreset.Nova, minTouchTarget = null) {
                OverlayHost {
                    Box(Modifier.width(560.dp).height(400.dp)) {
                        toggleDoc.examples.single { it.title == title }.RenderPreview()
                    }
                }
            }
        }
    }

    @Test fun officialRecipesKeepOfficialOrderAndKotlinExtensionsComeAfter() {
        assertEquals(
            listOf("Default", "Outline", "With Text", "Size", "Disabled", "RTL"),
            toggleDoc.examples.take(6).map { it.title },
        )
        assertEquals(
            listOf(
                "Default variant",
                "Default size",
                "Default disabled",
                "Uncontrolled",
                "Default pressed",
                "Accessible label",
                "Default label",
                "Invalid",
            ).map { "Kotlin extension: $it" },
            toggleDoc.examples.drop(6).map { it.title },
        )
        assertEquals(null, toggleDoc.examplesHeading)
        val markdown = componentPageMarkdown(toggleDoc)
        val order =
            listOf(
                "## Installation",
                "## Usage",
                "## Outline",
                "## With Text",
                "## Size",
                "## Disabled",
                "## RTL",
                "## API Reference",
            ).map { markdown.indexOf(it) }
        assertTrue(order.all { it >= 0 }, "missing a section heading: $order")
        assertEquals(order.sorted(), order)
        assertEquals("import com.toutiaoyan.compose.ui.components.toggle.Toggle", toggleDoc.usageImportsCode)
        assertEquals("Toggle(\"Toggle\")", toggleDoc.usageCode)
        assertContains(markdown, "[Base UI Toggle](https://base-ui.com/react/components/toggle#api-reference)")
        assertEquals(1, markdown.lineSequence().count { it == "## API Reference" })
        assertContains(markdown, "Use `variant = ToggleVariant.Outline` for an outline style.")
        assertContains(markdown, "Use the `size` prop to change the size of the toggle.")
        assertFalse(markdown.contains("## Examples"))
        assertFalse(markdown.contains("## Default"))
    }

    @Test fun demoIsTheOfficialBookmarkToggle() {
        show("Default")
        val toggle = rule.onNodeWithContentDescription("Toggle bookmark")
        toggle.assertIsDisplayed().assertIsOff()
        toggle.performClick()
        rule.onNodeWithContentDescription("Toggle bookmark").assertIsOn()
    }

    @Test fun outlineTogglesItalicAndBoldIndependently() {
        show("Outline")
        val italic = rule.onNodeWithContentDescription("Toggle italic")
        val bold = rule.onNodeWithContentDescription("Toggle bold")
        italic.assertIsOff().performClick()
        italic.assertIsOn()
        bold.assertIsOff().performClick()
        bold.assertIsOn()
        italic.performClick()
        italic.assertIsOff()
        bold.assertIsOn()
    }

    @Test fun textRecipeTogglesItalic() {
        show("With Text")
        val italic = rule.onNodeWithContentDescription("Toggle italic")
        italic.assertIsOff().performClick()
        italic.assertIsOn()
        rule.onNodeWithText("Italic").assertIsDisplayed()
    }

    @Test fun sizeFixturesStartOffAndToggleIndependently() {
        show("Size")
        val small = rule.onNodeWithContentDescription("Toggle small")
        val medium = rule.onNodeWithContentDescription("Toggle default")
        val large = rule.onNodeWithContentDescription("Toggle large")
        small.assertIsOff()
        medium.assertIsOff().performClick()
        medium.assertIsOn()
        large.assertIsOff().performClick()
        large.assertIsOn()
        small.assertIsOff().performClick()
        small.assertIsOn()
    }

    @Test fun disabledVariantsAreBothOff() {
        show("Disabled")
        rule.onNodeWithContentDescription("Toggle disabled").assertIsNotEnabled().assertIsOff()
        rule.onNodeWithContentDescription("Toggle disabled outline").assertIsNotEnabled().assertIsOff()
    }

    @Test fun defaultDisabledExtensionTogglesWhileEnabled() {
        show("Kotlin extension: Default disabled")
        val toggle = rule.onNodeWithContentDescription("Bold")
        toggle.assertIsEnabled().assertIsOff().performClick()
        toggle.assertIsOn().performClick()
        toggle.assertIsOff()
    }

    @Test fun rtlPreviewKeepsBookmarkStateAcrossTranslations() {
        show("RTL")
        val bookmark = rule.onNodeWithContentDescription("Toggle bookmark")
        rule.onNodeWithText("إشارة مرجعية").assertIsDisplayed()
        bookmark.assertIsOff().performClick()
        for ((label, toggleLabel) in listOf(
            "English" to "Bookmark",
            "Hebrew (עברית)" to "סימנייה",
            "Arabic (العربية)" to "إشارة مرجعية",
        )) {
            rule.onNodeWithContentDescription("Preview language").performClick()
            rule.onAllNodesWithText(label).onLast().performClick()
            rule.onNodeWithText(toggleLabel).assertIsDisplayed()
            bookmark.assertIsOn()
        }
    }

    @Test fun invalidExtensionClearsItsErrorWhenConfirmed() {
        show("Kotlin extension: Invalid")
        val toggle = rule.onNodeWithContentDescription("Confirm selection")
        val invalid = SemanticsMatcher.expectValue(SemanticsProperties.Error, "Invalid selection")
        toggle.assertIsOff().assert(invalid).performClick()
        toggle.assertIsOn().assert(!invalid)
    }
}
