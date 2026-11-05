package com.toutiaoyan.compose.ui.catalog

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.width
import androidx.compose.ui.Modifier
import androidx.compose.ui.test.*
import androidx.compose.ui.test.junit4.v2.createComposeRule
import androidx.compose.ui.unit.dp
import com.toutiaoyan.compose.ui.catalog.docs.traceDoc
import com.toutiaoyan.compose.ui.theme.Theme
import com.toutiaoyan.compose.ui.tokens.StylePreset
import org.junit.Rule
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertTrue

class TraceExamplesTest {
    @get:Rule val rule = createComposeRule()

    private fun show(title: String) {
        rule.setContent {
            Theme(preset = StylePreset.Nova, minTouchTarget = null) {
                Box(Modifier.width(480.dp)) {
                    traceDoc.examples.single { it.title == title }.preview()
                }
            }
        }
    }

    @Test fun officialTraceExamplesKeepSourceOrderAndReplaceTheExpandedAlias() {
        assertEquals(
            listOf("Default", "Status", "Description", "Collapsible", "Open State", "Group"),
            traceDoc.examples.map { it.title },
        )
        assertTrue(traceDoc.examples.none { it.title == "Expanded" })
    }

    @Test fun descriptionKeepsCountsAndSearchResultsVisible() {
        show("Description")
        rule.onNodeWithText("+48 −12").assertIsDisplayed()
        rule.onNodeWithText("14s").assertIsDisplayed()
        rule.onNodeWithText("8 results").assertIsDisplayed()
    }

    @Test fun collapsibleRendersTheTerminalOutputOnlyAfterExpansion() {
        show("Collapsible")
        rule.onNodeWithText("v24.18.0").assertDoesNotExist()
        rule.onNodeWithText("Ran node --version").performClick()
        rule.onNodeWithText("v24.18.0", substring = true).assertIsDisplayed()
    }

    @Test fun openStateCombinesDefaultOpenControlledOpenAndAnExternalTrigger() {
        show("Open State")
        rule.onNodeWithText("v24.18.0", substring = true).assertIsDisplayed()
        rule.onNodeWithText("10.33.4", substring = true).assertDoesNotExist()
        rule.onNodeWithText("Show the pnpm output").performClick()
        rule.onNodeWithText("10.33.4", substring = true).assertIsDisplayed()
        rule.onNodeWithText("Hide the pnpm output").performClick()
        rule.onNodeWithText("10.33.4", substring = true).assertDoesNotExist()
    }

    @Test fun groupShowsThreeIndependentTraceLines() {
        show("Group")
        listOf(
            "Searched for minimumReleaseAge",
            "Read pnpm-workspace.yaml",
            "Ran pnpm install",
        ).forEach { rule.onNodeWithText(it).assertIsDisplayed() }
    }
}
