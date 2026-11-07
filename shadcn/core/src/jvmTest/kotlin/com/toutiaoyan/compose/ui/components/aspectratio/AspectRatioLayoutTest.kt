@file:OptIn(androidx.compose.foundation.style.ExperimentalFoundationStyleApi::class)

package com.toutiaoyan.compose.ui.components.aspectratio

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.style.Style
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.test.*
import androidx.compose.ui.unit.dp
import com.toutiaoyan.compose.ui.ScreenshotTest
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFailsWith

class AspectRatioLayoutTest : ScreenshotTest() {
    @Test fun widthRatioAndChildTrackLiveConstraints() {
        var width by mutableStateOf(384.dp)
        var ratio by mutableStateOf(16f / 9f)
        setThemedContent {
            Box(Modifier.width(width)) {
                AspectRatio(ratio, Modifier.fillMaxWidth().testTag("ratio")) {
                    Box(Modifier.fillMaxSize().testTag("image"))
                }
            }
        }

        fun check(
            w: Float,
            h: Float,
        ) {
            composeRule.onNodeWithTag("ratio").assertWidthIsEqualTo(w.dp).assertHeightIsEqualTo(h.dp)
            composeRule.onNodeWithTag("image").assertWidthIsEqualTo(w.dp).assertHeightIsEqualTo(h.dp)
        }
        check(384f, 216f)
        composeRule.runOnIdle { width = 232.dp }
        check(232f, 131f)
        composeRule.runOnIdle { ratio = 1f }
        check(232f, 232f)
        composeRule.runOnIdle {
            width = 160.dp
            ratio = 9f / 16f
        }
        check(160f, 284f)
    }

    @Test fun styleWidthParticipatesBeforeRatioMeasurement() {
        setThemedContent {
            Box(Modifier.width(400.dp)) {
                AspectRatio(16f / 9f, Modifier.testTag("ratio"), style = Style { width(160.dp) }) {
                    Box(Modifier.fillMaxSize().testTag("image"))
                }
            }
        }
        composeRule.onNodeWithTag("ratio").assertWidthIsEqualTo(160.dp).assertHeightIsEqualTo(90.dp)
        composeRule.onNodeWithTag("image").assertWidthIsEqualTo(160.dp).assertHeightIsEqualTo(90.dp)
    }

    @Test fun invalidRatioFailsFast() {
        listOf(0f, -1f, Float.NaN, Float.POSITIVE_INFINITY).forEach { ratio ->
            val error =
                assertFailsWith<IllegalArgumentException> {
                    validateAspectRatio(ratio)
                }
            assertEquals("ratio must be finite and greater than 0", error.message)
        }
    }
}
