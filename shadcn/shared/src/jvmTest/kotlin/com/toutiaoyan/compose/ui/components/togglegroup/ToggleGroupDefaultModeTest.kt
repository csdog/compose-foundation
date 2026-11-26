package com.toutiaoyan.compose.ui.components.togglegroup

import androidx.compose.runtime.Composable
import androidx.compose.ui.test.*
import androidx.compose.ui.test.junit4.v2.createComposeRule
import com.toutiaoyan.compose.ui.theme.Theme
import org.junit.Rule
import kotlin.test.Test

class ToggleGroupDefaultModeTest {
    @get:Rule val rule = createComposeRule()

    private fun selection(overload: Int) {
        rule.setContent {
            Theme {
                val items: @Composable ToggleGroupScope.() -> Unit = {
                    ToggleGroupItem("a", "Alpha")
                    ToggleGroupItem("b", "Beta")
                }
                when (overload) {
                    0 -> ToggleGroup(content = items)
                    1 -> ToggleGroup(loopFocus = false, content = items)
                    2 -> ToggleGroup(onValueChangeRequest = { _, _ -> }, content = items)
                    else -> ToggleGroup(type = ToggleGroupType.Multiple, content = items)
                }
            }
        }
        val alpha = rule.onNodeWithText("Alpha")
        val beta = rule.onNodeWithText("Beta")
        alpha.performClick().assertIsOn()
        beta.performClick().assertIsOn()
        if (overload == 3) alpha.assertIsOn() else alpha.assertIsOff()
        beta.performClick().assertIsOff()
        if (overload == 3) alpha.assertIsOn() else alpha.assertIsOff()
    }

    @Test fun originalSetRootDefaultsSingle() = selection(0)

    @Test fun loopFocusSetRootDefaultsSingle() = selection(1)

    @Test fun cancellableSetRootDefaultsSingle() = selection(2)

    @Test fun explicitMultipleRetainsMembership() = selection(3)
}
