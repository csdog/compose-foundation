@file:OptIn(androidx.compose.foundation.style.ExperimentalFoundationStyleApi::class)

package com.toutiaoyan.compose.ui.components.attachment

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.style.Style
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.platform.LocalLayoutDirection
import androidx.compose.ui.test.*
import androidx.compose.ui.unit.LayoutDirection
import androidx.compose.ui.unit.dp
import com.toutiaoyan.compose.ui.ScreenshotTest
import com.toutiaoyan.compose.ui.tokens.StylePreset
import kotlin.test.Test
import kotlin.test.assertEquals

class AttachmentGroupConstraintsTest : ScreenshotTest() {
    private fun width(tag: String): Float {
        val bounds = composeRule.onNodeWithTag(tag).getUnclippedBoundsInRoot()
        return (bounds.right - bounds.left).value
    }

    @Test fun preferredAndStyleWidthsFollowLiveViewportInBothDirections() {
        var viewport by mutableStateOf(245.dp)
        var direction by mutableStateOf(LayoutDirection.Ltr)
        setThemedContent(stylePreset = StylePreset.Nova) {
            CompositionLocalProvider(LocalLayoutDirection provides direction) {
                AttachmentGroup(Modifier.width(viewport)) {
                    Attachment(Modifier.width(256.dp).testTag("preferred")) { AttachmentContent("file.pdf") }
                    Attachment(
                        Modifier.testTag("styled"),
                        style = Style { width(256.dp) },
                    ) { AttachmentContent("file.pdf") }
                }
            }
        }
        for (dir in listOf(
            LayoutDirection.Ltr,
            LayoutDirection.Rtl,
        )) for (size in listOf(245.dp, 300.dp, 200.dp, 245.dp)) {
            composeRule.runOnIdle {
                viewport = size
                direction = dir
            }
            composeRule.waitForIdle()
            assertEquals(minOf(size.value, 256f), width("preferred"), 1f)
            assertEquals(minOf(size.value, 256f), width("styled"), 1f)
        }
    }

    @Test fun explicitRequiredWidthRemainsScrollable() {
        setThemedContent(stylePreset = StylePreset.Nova) {
            AttachmentGroup(Modifier.width(245.dp).testTag("group")) {
                Attachment(Modifier.requiredWidth(500.dp).height(40.dp).testTag("wide")) { }
            }
        }
        assertEquals(500f, width("wide"), 1f)
        composeRule.onNodeWithTag(
            "group",
        ).performSemanticsAction(androidx.compose.ui.semantics.SemanticsActions.ScrollBy) {
            it(10000f, 0f)
        }
        composeRule.waitForIdle()
        val group = composeRule.onNodeWithTag("group").getUnclippedBoundsInRoot()
        val card = composeRule.onNodeWithTag("wide").getUnclippedBoundsInRoot()
        assertEquals(group.right.value, card.right.value, 1f)
    }

    @Test fun horizontalMinimumAndVerticalCapUseDifferentRules() {
        setThemedContent(stylePreset = StylePreset.Nova) {
            Column {
                AttachmentGroup(Modifier.width(100.dp)) {
                    Attachment(Modifier.testTag("horizontal")) { }
                }
                AttachmentGroup(Modifier.width(100.dp)) {
                    Attachment(
                        Modifier.width(120.dp).testTag("vertical"),
                        orientation = AttachmentOrientation.Vertical,
                    ) { }
                }
            }
        }
        assertEquals(160f, width("horizontal"), 1f)
        assertEquals(100f, width("vertical"), 1f)
    }

    @Test fun groupLimitsStayIndependentAndNestedGroupsUseNearestViewport() {
        setThemedContent(stylePreset = StylePreset.Nova) {
            Column {
                AttachmentGroup(Modifier.width(245.dp)) {
                    Attachment(Modifier.width(256.dp).testTag("first")) { }
                }
                AttachmentGroup(Modifier.width(300.dp)) {
                    Attachment(Modifier.width(256.dp).testTag("second")) { }
                }
                AttachmentGroup(Modifier.width(320.dp)) {
                    Attachment(Modifier.width(300.dp).testTag("outer")) {
                        AttachmentGroup(Modifier.width(200.dp)) {
                            Attachment(Modifier.width(256.dp).testTag("inner")) { }
                        }
                    }
                }
            }
        }
        assertEquals(245f, width("first"), 1f)
        assertEquals(256f, width("second"), 1f)
        assertEquals(300f, width("outer"), 1f)
        assertEquals(200f, width("inner"), 1f)
    }
}
