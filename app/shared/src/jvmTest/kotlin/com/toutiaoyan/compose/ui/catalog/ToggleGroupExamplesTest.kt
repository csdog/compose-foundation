package com.toutiaoyan.compose.ui.catalog

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.width
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.test.*
import androidx.compose.ui.test.junit4.v2.createComposeRule
import androidx.compose.ui.unit.dp
import com.toutiaoyan.compose.ui.catalog.docs.toggleGroupDoc
import com.toutiaoyan.compose.ui.components.overlay.OverlayHost
import com.toutiaoyan.compose.ui.theme.Theme
import com.toutiaoyan.compose.ui.tokens.StylePreset
import org.junit.Rule
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertTrue

class ToggleGroupExamplesTest {
    @get:Rule val rule = createComposeRule()

    private fun show(title: String) {
        rule.setContent {
            Theme(preset = StylePreset.Nova, minTouchTarget = null) {
                OverlayHost {
                    Box(Modifier.width(720.dp).height(720.dp), contentAlignment = Alignment.TopCenter) {
                        toggleGroupDoc.examples.single { it.title == title }.preview()
                    }
                }
            }
        }
    }

    @Test fun officialRecipesAndKotlinExtensionsAppearInOrder() {
        assertEquals(
            listOf(
                "Default", "Outline", "Size", "Spacing", "Vertical", "Disabled", "Custom", "RTL",
                "Kotlin extension: Single select", "Kotlin extension: Connected",
            ),
            toggleGroupDoc.examples.map { it.title },
        )
        assertTrue(
            toggleGroupDoc.examples.single { it.title == "Size" }
                .descriptionMarkdown.orEmpty()
                .contains("`size` prop"),
        )
        assertTrue(
            toggleGroupDoc.examples.single { it.title == "Spacing" }
                .descriptionMarkdown.orEmpty()
                .contains("spacing"),
        )
        assertEquals(
            "A custom toggle group example.",
            toggleGroupDoc.examples.single { it.title == "Custom" }.descriptionMarkdown,
        )
        assertTrue(
            toggleGroupDoc.examples.single { it.title == "RTL" }.descriptionMarkdown.orEmpty()
                .startsWith("To enable RTL support"),
        )
        assertTrue(toggleGroupDoc.compositionCode.orEmpty().contains("ToggleGroupItem"))
    }

    @Test fun outlineRecipeKeepsTheLiteralAllSelection() {
        show("Outline")
        rule.onNodeWithText("All").assertIsOn()
        rule.onNodeWithText("Missed").assertIsOff()
        rule.onNodeWithText("Missed").performClick()
        rule.onNodeWithText("Missed").assertIsOn()
        rule.onNodeWithText("All").assertIsOn()
    }

    @Test fun singleSelectRecipeDeselectsTheActiveItem() {
        show("Kotlin extension: Single select")
        rule.onNodeWithText("I").assertIsOn()
        rule.onNodeWithText("I").performClick()
        rule.onNodeWithText("I").assertIsOff()
        rule.onNodeWithText("B").performClick()
        rule.onNodeWithText("B").assertIsOn()
        rule.onNodeWithText("I").assertIsOff()
    }

    @Test fun verticalRecipeStacksItsItems() {
        show("Vertical")
        val bold = rule.onNodeWithContentDescription("Toggle bold").fetchSemanticsNode().boundsInRoot
        val italic = rule.onNodeWithContentDescription("Toggle italic").fetchSemanticsNode().boundsInRoot
        assertTrue(italic.top > bold.bottom, "vertical group should stack below")
    }

    @Test fun disabledRecipeDoesNotToggle() {
        show("Disabled")
        rule.onNodeWithContentDescription("Toggle bold").assertIsOn()
        rule.onNodeWithContentDescription("Toggle bold").performClick()
        rule.onNodeWithContentDescription("Toggle bold").assertIsOn()
    }
}
