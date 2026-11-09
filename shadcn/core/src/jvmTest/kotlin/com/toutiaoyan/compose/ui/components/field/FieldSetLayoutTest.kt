package com.toutiaoyan.compose.ui.components.field

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.test.*
import androidx.compose.ui.test.junit4.v2.createComposeRule
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.LayoutDirection
import androidx.compose.ui.platform.LocalLayoutDirection
import androidx.compose.runtime.CompositionLocalProvider
import com.toutiaoyan.compose.ui.theme.Theme
import com.toutiaoyan.compose.ui.tokens.StylePreset
import org.junit.Rule
import kotlin.test.*

class FieldSetLayoutTest {
    @get:Rule val rule = createComposeRule()

    @Test fun weightValidationMatchesColumnScope() {
        val failures = mutableListOf<Float>()
        rule.setContent {
            Theme(preset = StylePreset.Nova) {
                FieldSet {
                    listOf(0f, -1f, Float.NaN, Float.NEGATIVE_INFINITY).forEach { value ->
                        try {
                            Modifier.weight(value)
                        } catch (_: IllegalArgumentException) {
                            failures += value
                        }
                    }
                }
            }
        }
        rule.runOnIdle { assertEquals(4, failures.size) }
    }

    @Test fun weightedChildrenFillRemainingSpaceAfterAllGapsIncludingInfiniteWeight() {
        rule.setContent {
            Theme(preset = StylePreset.Nova) {
                FieldSet(Modifier.height(100.dp)) {
                    Box(Modifier.width(10.dp).height(20.dp).testTag("fixed"))
                    Box(Modifier.width(10.dp).weight(Float.POSITIVE_INFINITY).testTag("weighted"))
                }
            }
        }
        rule.onNodeWithTag("weighted").assertHeightIsEqualTo(64.dp)
        assertEquals(36f, bounds("weighted").top - bounds("fixed").top)
    }

    @Test fun nonFillingWeightDoesNotReserveUnusedSpace() {
        rule.setContent {
            Theme(preset = StylePreset.Nova) {
                FieldSet(Modifier.heightIn(max = 100.dp).testTag("set")) {
                    Box(Modifier.width(10.dp).weight(1f, fill = false).height(10.dp).testTag("small"))
                    Box(Modifier.width(10.dp).height(20.dp).testTag("last"))
                }
            }
        }
        rule.onNodeWithTag("set").assertHeightIsEqualTo(46.dp)
        assertEquals(16f, bounds("last").top - bounds("small").bottom)
    }

    @Test fun weightedChildUsesMinimumHeightInsideAnUnboundedScrollContainer() {
        rule.setContent {
            Theme(preset = StylePreset.Nova) {
                Column(Modifier.height(200.dp).verticalScroll(rememberScrollState())) {
                    FieldSet(Modifier.heightIn(min = 100.dp)) {
                        Box(Modifier.height(20.dp))
                        Box(Modifier.weight(1f).testTag("weighted"))
                    }
                }
            }
        }
        rule.onNodeWithTag("weighted").assertHeightIsEqualTo(64.dp)
    }

    @Test fun alignmentLinesAndRtlAlignmentMatchColumn() {
        rule.setContent {
            Theme(preset = StylePreset.Nova) {
                CompositionLocalProvider(LocalLayoutDirection provides LayoutDirection.Rtl) {
                    Column {
                        FieldSet(Modifier.width(100.dp).testTag("set")) {
                            Box(Modifier.size(20.dp).alignBy { 5 }.testTag("a"))
                            Box(Modifier.size(30.dp).alignBy { 15 }.testTag("b"))
                            Box(Modifier.size(10.dp).align(androidx.compose.ui.Alignment.End).testTag("end"))
                        }
                        Column(Modifier.width(100.dp).testTag("column")) {
                            Box(Modifier.size(20.dp).alignBy { 5 }.testTag("ref-a"))
                            Box(Modifier.size(30.dp).alignBy { 15 }.testTag("ref-b"))
                            Box(Modifier.size(10.dp).align(androidx.compose.ui.Alignment.End).testTag("ref-end"))
                        }
                    }
                }
            }
        }
        listOf("a", "b", "end").forEach { tag ->
            assertEquals(bounds("ref-$tag").left - bounds("column").left, bounds(tag).left - bounds("set").left)
        }
    }

    private fun bounds(tag: String) = rule.onNodeWithTag(tag).fetchSemanticsNode().boundsInRoot
}
