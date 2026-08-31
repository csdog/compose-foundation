@file:OptIn(androidx.compose.foundation.style.ExperimentalFoundationStyleApi::class)

package com.toutiaoyan.compose.ui.components.inputotp

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.padding
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.test.hasAnyAncestor
import androidx.compose.ui.test.hasRequestFocusAction
import androidx.compose.ui.test.hasTestTag
import androidx.compose.ui.test.requestFocus
import androidx.compose.ui.unit.dp
import com.toutiaoyan.compose.ui.ScreenshotTest
import com.toutiaoyan.compose.ui.theme.Theme
import com.toutiaoyan.compose.ui.theme.currentTheme
import com.toutiaoyan.compose.ui.tokens.StylePreset
import kotlin.test.Test
import com.toutiaoyan.compose.ui.components.field.FieldDescription
import com.toutiaoyan.compose.ui.components.field.FieldLabel
import com.toutiaoyan.compose.tailwind.core.twSpacing

class InputOTPScreenshotTest : ScreenshotTest() {
    private fun states(darkTheme: Boolean) {
        snapshot("input_otp_states", darkTheme = darkTheme) {
            InputOTP(value = "12", onValueChange = {}, length = 6)
        }
    }

    private fun verifyEmail(darkTheme: Boolean) {
        snapshot("input_otp_verify_email", darkTheme = darkTheme) {
            Column(verticalArrangement = Arrangement.spacedBy(twSpacing.sm)) {
                FieldLabel("One-time password")
                InputOTP(value = "12", onValueChange = {}, length = 6)
                FieldDescription("Please enter the one-time password sent to your phone.")
            }
        }
    }

    @Test
    fun states_light() = states(darkTheme = false)

    @Test
    fun states_dark() = states(darkTheme = true)

    @Test
    fun verify_email_light() = verifyEmail(darkTheme = false)

    @Test
    fun verify_email_dark() = verifyEmail(darkTheme = true)

    @Test
    fun focused_light() {
        snapshotFocused("input_otp_focused", focusTag = "otp", darkTheme = false) {
            InputOTP(value = "12", onValueChange = {}, length = 6, modifier = Modifier.testTag("otp"))
        }
    }

    @Test
    fun focused_dark() {
        snapshotFocused("input_otp_focused", focusTag = "otp", darkTheme = true) {
            InputOTP(value = "12", onValueChange = {}, length = 6, modifier = Modifier.testTag("otp"))
        }
    }

    /**
     * [com.toutiaoyan.compose.ui.tokens.Ring.enabled] = false must NOT
     * silence the active slot's ring here -- a PIN/OTP entry is a text-entry component
     * (see `OtpSlot`'s doc comment), so it stays exempt from the toggle just like
     * `Input` and `InputGroup`.
     */
    @Test
    fun focused_with_ring_disabled_light() {
        val focusTag = "otp-ring-disabled"
        composeRule.setContent {
            Theme(ring = StylePreset.Vega.ring.copy(enabled = false)) {
                Box(modifier = Modifier.background(currentTheme.colors.background).padding(24.dp)) {
                    InputOTP(value = "12", onValueChange = {}, length = 6, modifier = Modifier.testTag(focusTag))
                }
            }
        }
        val focusable = hasRequestFocusAction() and (hasTestTag(focusTag) or hasAnyAncestor(hasTestTag(focusTag)))
        composeRule.onNode(focusable, useUnmergedTree = true).requestFocus()
        composeRule.waitForIdle()
        captureNamed("input_otp_focused_with_ring_disabled", darkTheme = false)
    }
}
