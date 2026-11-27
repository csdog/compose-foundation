package com.toutiaoyan.compose.ui.catalog

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.width
import androidx.compose.ui.Modifier
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.semantics.SemanticsProperties
import androidx.compose.ui.test.*
import androidx.compose.ui.test.junit4.v2.createComposeRule
import androidx.compose.ui.unit.dp
import com.toutiaoyan.compose.ui.catalog.docs.checkboxDoc
import com.toutiaoyan.compose.ui.theme.Theme
import com.toutiaoyan.compose.ui.tokens.StylePreset
import org.junit.Rule
import org.junit.Test
import kotlin.test.assertEquals

class CheckboxExamplesTest {
    @get:Rule val rule = createComposeRule()
    private val controls = SemanticsMatcher.expectValue(SemanticsProperties.Role, Role.Checkbox)

    private fun show(name: String) {
        rule.setContent {
            Theme(preset = StylePreset.Nova, minTouchTarget = null, fontFamily = catalogFontFamily()) {
                Box(Modifier.width(480.dp)) { checkboxDoc.examples.single { it.title == name }.RenderPreview() }
            }
        }
    }

    @Test fun officialRecipesReplaceMisleadingDefaultStateDuplicates() {
        assertEquals(
            listOf(
                "Default",
                "Invalid State",
                "Basic",
                "Description",
                "Disabled",
                "Group",
                "Table",
                "RTL",
            ),
            checkboxDoc.examples.take(8).map {
                it.title
            },
        )
    }

    @Test fun defaultIncludesFourIndependentControlsAndDisabledNotifications() {
        show("Default")
        rule.onAllNodes(controls).assertCountEquals(4)
        rule.onAllNodes(controls)[2].assertIsNotEnabled()
        rule.onAllNodes(controls)[1].assertIsOn()
    }

    @Test fun tableSelectsAllAndIndividualRowsWithoutIndeterminateHeader() {
        show("Table")
        rule.onAllNodes(controls).assertCountEquals(5)
        rule.onAllNodes(controls)[0].assertIsOff()
        rule.onAllNodes(controls)[1].assertIsOn()
        rule.onAllNodes(controls)[0].performClick()
        (0..4).forEach { rule.onAllNodes(controls)[it].assertIsOn() }
        rule.onAllNodes(controls)[2].performClick()
        rule.onAllNodes(controls)[0].assertIsOff()
        rule.onAllNodes(controls)[2].assertIsOff()
        rule.onAllNodes(controls)[1].assertIsOn()
    }
}
