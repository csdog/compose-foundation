package com.toutiaoyan.compose.ui.components.dropdownmenu

import androidx.compose.foundation.interaction.HoverInteraction
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.runtime.mutableStateOf
import androidx.compose.ui.test.junit4.v2.createComposeRule
import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import org.junit.runners.Parameterized

@RunWith(Parameterized::class)
class DropdownMenuDisabledHoverTest(private val disabledFlag: Boolean) {
    @get:Rule val rule = createComposeRule()

    @Test fun disabledTriggerRejectsHoverAndClearsExistingHover() {
        val interactions = MutableInteractionSource()
        val enabled = mutableStateOf(disabledFlag)
        val disabled = mutableStateOf(false)
        var reportedHover = false
        if (disabledFlag) disabled.value = true
        rule.setContent {
            DropdownMenuSubTriggerHoverObserver(interactions, enabled.value && !disabled.value) {
                reportedHover = it
            }
        }
        rule.runOnIdle { check(interactions.tryEmit(HoverInteraction.Enter())) }
        rule.waitForIdle()
        rule.runOnIdle { assertFalse(reportedHover) }
        rule.runOnIdle {
            enabled.value = true
            disabled.value = false
        }
        rule.waitForIdle()
        rule.runOnIdle { assertTrue(reportedHover) }
        rule.runOnIdle {
            if (disabledFlag) disabled.value = true else enabled.value = false
        }
        rule.waitForIdle()
        rule.runOnIdle { assertFalse(reportedHover) }
    }

    companion object {
        @JvmStatic
        @Parameterized.Parameters(name = "disabled flag={0}")
        fun cases() = listOf(false, true)
    }
}
