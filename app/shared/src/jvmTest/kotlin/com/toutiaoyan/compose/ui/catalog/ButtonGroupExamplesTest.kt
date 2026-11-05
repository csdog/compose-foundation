package com.toutiaoyan.compose.ui.catalog

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.requiredSize
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.test.*
import androidx.compose.ui.test.junit4.v2.createComposeRule
import androidx.compose.ui.unit.dp
import com.toutiaoyan.compose.ui.catalog.docs.buttonGroupDoc
import com.toutiaoyan.compose.ui.components.overlay.OverlayHost
import com.toutiaoyan.compose.ui.theme.Theme
import com.toutiaoyan.compose.ui.tokens.StylePreset
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import org.junit.runners.Parameterized
import kotlin.test.assertEquals
import kotlin.test.assertTrue

@RunWith(Parameterized::class)
class ButtonGroupExamplesTest(
    private val width: Int,
    private val dark: Boolean,
) {
    @get:Rule val rule = createComposeRule()

    @Test fun currentOfficialRecipesRenderAtBothWidths() {
        val indexState = mutableIntStateOf(0)
        var index by indexState
        setContent(indexState)
        assertEquals(
            listOf(
                "Default",
                "Orientation",
                "Size",
                "Nested",
                "Separator",
                "Split",
                "Input",
                "Input Group",
                "Dropdown Menu",
                "Select",
                "Popover",
                "RTL",
            ),
            buttonGroupDoc.examples.map { it.title },
        )
        for (i in buttonGroupDoc.examples.indices) {
            rule.runOnIdle { index = i }
            rule.waitForIdle()
        }
    }

    @Test fun sizeRecipeUsesSmDefaultAndLgButtonHeights() {
        val indexState = mutableIntStateOf(2)
        setContent(indexState)
        rule.onNodeWithText("Small").assertHeightIsEqualTo(28.dp)
        rule.onNodeWithText("Default").assertHeightIsEqualTo(32.dp)
        rule.onNodeWithText("Large").assertHeightIsEqualTo(36.dp)
    }

    @Test fun dropdownAndPopoverRecipesOpenTheirRealContent() {
        val indexState = mutableIntStateOf(8)
        var index by indexState
        setContent(indexState)
        rule.onNodeWithContentDescription("Open options").performClick()
        rule.onNodeWithText("Option 1").assertIsDisplayed()
        rule.onNodeWithText("Option 2").performClick()
        rule.runOnIdle { index = 10 }
        rule.onNodeWithText("Copilot").performClick()
        rule.onNodeWithText("Start a new task with Copilot").assertIsDisplayed()
        rule.onNodeWithText("Describe your task...").assertIsDisplayed()
    }

    @Test fun rtlLanguageSelectorKeepsButtonGroupsLogical() {
        val indexState = mutableIntStateOf(11)
        setContent(indexState)
        rule.onNodeWithText("تقرير").assertIsDisplayed()
        rule.onNodeWithContentDescription("Preview language").performClick()
        rule.onNodeWithText("English").performClick()
        rule.onNodeWithText("Report").assertIsDisplayed()
        assertTrue(buttonGroupDoc.examples.last().code.contains("LocalLayoutDirection"))
    }

    private fun setContent(index: State<Int>) {
        rule.setContent {
            Theme(
                preset = StylePreset.Nova,
                isDark = dark,
                minTouchTarget = null,
                fontFamily = catalogFontFamily(),
            ) {
                OverlayHost {
                    Box(
                        Modifier.requiredSize((if (width < 768) 325 else 638).dp, 600.dp),
                    ) {
                        key(index.value) { buttonGroupDoc.examples[index.value].preview() }
                    }
                }
            }
        }
    }

    companion object {
        @JvmStatic
        @Parameterized.Parameters(name = "width={0}, dark={1}")
        fun cases(): List<Array<Any>> =
            listOf(390, 1440).flatMap { width ->
                listOf(false, true).map { dark -> arrayOf<Any>(width, dark) }
            }
    }
}
