package com.toutiaoyan.compose.ui.catalog

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.width
import androidx.compose.ui.Modifier
import androidx.compose.ui.input.key.Key
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.semantics.SemanticsProperties
import androidx.compose.ui.test.*
import androidx.compose.ui.test.junit4.v2.createComposeRule
import androidx.compose.ui.unit.dp
import com.toutiaoyan.compose.ui.catalog.docs.selectDoc
import com.toutiaoyan.compose.ui.components.overlay.OverlayHost
import com.toutiaoyan.compose.ui.theme.Theme
import com.toutiaoyan.compose.ui.tokens.StylePreset
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import org.junit.runners.Parameterized

@RunWith(Parameterized::class)
class SelectExamplesTestExtensions(private val title: String) {
    @get:Rule val rule = createComposeRule()

    @Test fun extensionPreviewOpensAndClosesOrKeepsItsDisabledContract() {
        rule.setContent {
            Theme(preset = StylePreset.Nova, minTouchTarget = null) {
                OverlayHost {
                    Box(Modifier.width(560.dp).height(600.dp)) {
                        selectDoc.examples.single { it.title == title }.RenderPreview()
                    }
                }
            }
        }
        if (title == "Kotlin extension: Default open") {
            rule.onNodeWithText("Show default-open select").performClick()
        }
        val trigger =
            rule.onNode(SemanticsMatcher.expectValue(SemanticsProperties.Role, Role.DropdownList))
        trigger.assertIsDisplayed()
        if (trigger.fetchSemanticsNode().config.contains(SemanticsProperties.Disabled)) {
            trigger.assertIsNotEnabled().performClick()
            trigger.assert(SemanticsMatcher.expectValue(SemanticsProperties.StateDescription, "Collapsed"))
        } else {
            if (title != "Kotlin extension: Default open") trigger.performClick()
            trigger.assert(SemanticsMatcher.expectValue(SemanticsProperties.StateDescription, "Expanded"))
            rule.onAllNodes(isFocused()).onLast().performKeyInput { pressKey(Key.Escape) }
            trigger.assert(SemanticsMatcher.expectValue(SemanticsProperties.StateDescription, "Collapsed"))
        }
    }

    companion object {
        @JvmStatic
        @Parameterized.Parameters(name = "{0}")
        fun cases() = selectDoc.examples.drop(7).map { arrayOf(it.title) }
    }
}
