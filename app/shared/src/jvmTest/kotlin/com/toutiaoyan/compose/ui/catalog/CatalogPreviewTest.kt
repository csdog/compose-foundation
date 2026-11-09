package com.toutiaoyan.compose.ui.catalog

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.test.assert
import androidx.compose.ui.test.SemanticsMatcher
import androidx.compose.ui.test.hasSetTextAction
import androidx.compose.ui.semantics.SemanticsProperties
import androidx.compose.ui.test.isFocused
import androidx.compose.ui.test.assertCountEquals
import com.toutiaoyan.compose.ui.catalog.docs.inputDoc
import com.toutiaoyan.compose.ui.catalog.docs.textareaDoc
import androidx.compose.ui.test.junit4.v2.createComposeRule
import androidx.compose.ui.test.onNodeWithContentDescription
import androidx.compose.ui.test.onNodeWithText
import androidx.compose.ui.test.performClick
import com.toutiaoyan.compose.ui.catalog.docs.PreviewCodeSection
import com.toutiaoyan.compose.ui.components.button.Button
import com.toutiaoyan.compose.ui.theme.Theme
import org.junit.Rule
import kotlin.test.Test
import kotlin.test.assertTrue

class CatalogPreviewTest {
    @get:Rule val rule = createComposeRule()

    @Test
    fun expandingSourceKeepsLivePreviewState() {
        rule.setContent {
            Theme(minTouchTarget = null) {
                PreviewCodeSection(code = "Button(onClick = increment)") {
                    var count by remember { mutableIntStateOf(0) }
                    Button("Count $count", onClick = { count++ })
                }
            }
        }
        rule.onNodeWithText("Count 0").performClick()
        rule.onNodeWithText("View Code").performClick()
        rule.onNodeWithText("Count 1").assertExists()
        rule.onNodeWithText("Button(onClick = increment)").assertExists()
    }

    @Test
    fun compactHeaderHasWorkingNavigationEntry() {
        var opened = false
        rule.setContent {
            Theme(minTouchTarget = null) {
                CatalogTopBar(
                    isDarkMode = false,
                    onToggleDarkMode = {},
                    isCompact = true,
                    onOpenNavigation = { opened = true },
                )
            }
        }
        rule.onNodeWithContentDescription("Open navigation").performClick()
        assertTrue(opened)
    }

    @Test fun inputAutofocusRequiresAnExplicitTryClick() {
        rule.setContent {
            Theme(
                minTouchTarget = null,
            ) { inputDoc.examples.single { it.title == "Kotlin extension: Autofocus" }.preview() }
        }
        rule.onAllNodes(isFocused()).assertCountEquals(0)
        rule.onNodeWithText("Try autofocus").performClick()
        rule.onAllNodes(isFocused()).assertCountEquals(1)
    }

    @Test fun textareaAutofocusRequiresAnExplicitTryClick() {
        rule.setContent {
            Theme(
                minTouchTarget = null,
            ) { textareaDoc.examples.single { it.title == "Kotlin extension: Autofocus" }.preview() }
        }
        rule.onAllNodes(isFocused()).assertCountEquals(0)
        rule.onNodeWithText("Try autofocus").performClick()
        rule.onAllNodes(isFocused()).assertCountEquals(1)
    }

    @Test fun apiKeyPreviewUsesPasswordSemantics() {
        rule.setContent { Theme { inputDoc.examples.first().preview() } }
        rule.onNode(hasSetTextAction()).assert(SemanticsMatcher.keyIsDefined(SemanticsProperties.Password))
    }
}
