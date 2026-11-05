package com.toutiaoyan.compose.ui.catalog

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.width
import androidx.compose.ui.Modifier
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
                        toggleDoc.examples.single { it.title == title }.preview()
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
        assertTrue(toggleDoc.examples.drop(6).all { it.title.startsWith("Kotlin extension: ") })
        assertEquals(null, toggleDoc.examplesHeading)
        val markdown = componentPageMarkdown(toggleDoc)
        val order = listOf(
            "## Outline",
            "## With Text",
            "## Size",
            "## Disabled",
            "## RTL",
            "## API Reference",
        ).map { markdown.indexOf(it) }
        assertTrue(order.all { it >= 0 }, "missing a section heading: $order")
        assertEquals(order.sorted(), order)
        assertContains(markdown, "Use `variant = ToggleVariant.Outline` for an outline style.")
        assertContains(markdown, "Use the `size` prop to change the size of the toggle.")
        assertFalse(markdown.contains("## Examples"))
    }

    @Test fun demoIsTheOfficialBookmarkToggle() {
        show("Default")
        val toggle = rule.onNodeWithContentDescription("Toggle bookmark")
        toggle.assertIsDisplayed().assertIsOff()
        toggle.performClick()
        rule.onNodeWithContentDescription("Toggle bookmark").assertIsOn()
    }

    @Test fun outlineAndTextToggleStillPress() {
        show("Outline")
        rule.onNodeWithText("Italic").performClick()
        rule.onNodeWithText("Italic").assertIsOn()
    }

    @Test fun disabledToggleDoesNotChangeState() {
        show("Disabled")
        val disabled = rule.onAllNodes(hasClickAction() and isNotEnabled()).onFirst()
        disabled.assertExists()
    }

    @Test fun rtlPreviewSwitchesBetweenTranslations() {
        show("RTL")
        for ((label, toggleLabel) in listOf(
            "Arabic (العربية)" to "إشارة مرجعية",
            "English" to "Bookmark",
            "Hebrew (עברית)" to "סימנייה",
        )) {
            rule.onNodeWithContentDescription("Preview language").performClick()
            rule.onAllNodesWithText(label).onLast().performClick()
            rule.onNodeWithText(toggleLabel).assertIsDisplayed()
        }
    }
}
