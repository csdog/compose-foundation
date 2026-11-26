package com.toutiaoyan.compose.ui.components.attachment

import androidx.compose.foundation.ScrollState
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.requiredWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.rememberScrollState
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.getValue
import androidx.compose.runtime.key
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.input.key.Key
import androidx.compose.ui.platform.LocalLayoutDirection
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.semantics.SemanticsActions
import androidx.compose.ui.test.*
import androidx.compose.ui.unit.LayoutDirection
import androidx.compose.ui.unit.dp
import com.toutiaoyan.compose.ui.ScreenshotTest
import com.toutiaoyan.compose.ui.components.text.Text
import com.toutiaoyan.compose.ui.tokens.StylePreset
import kotlin.test.Test
import kotlin.test.assertEquals

class AttachmentGroupSnapTest : ScreenshotTest() {
    @Test fun groupOnlyAddsAKeyboardStopWhileContentOverflows() {
        var overflowing by mutableStateOf(false)
        setThemedContent(stylePreset = StylePreset.Nova) {
            AttachmentGroup(Modifier.size(240.dp, 48.dp).testTag("group")) {
                Box(Modifier.size(160.dp, 40.dp))
                if (overflowing) Box(Modifier.size(160.dp, 40.dp))
            }
        }

        val group = composeRule.onNodeWithTag("group")
        group.assert(SemanticsMatcher.keyNotDefined(SemanticsActions.RequestFocus))
        composeRule.runOnIdle { overflowing = true }
        group.requestFocus().assertIsFocused()
        composeRule.runOnIdle { overflowing = false }
        group.assert(SemanticsMatcher.keyNotDefined(SemanticsActions.RequestFocus))
    }

    private fun drag(
        logicalDistance: Float,
        direction: LayoutDirection,
    ) {
        val sign = if (direction == LayoutDirection.Ltr) -1f else 1f
        composeRule.onNodeWithTag("group").performTouchInput {
            down(center)
            moveBy(Offset(20f * sign, 0f))
            moveBy(Offset(logicalDistance * sign, 0f))
            advanceEventTime(300)
            up()
        }
        composeRule.waitForIdle()
    }

    @Test fun variableWidthCardsSnapWithLogicalPaddingInBothDirections() {
        var direction by mutableStateOf(LayoutDirection.Ltr)
        setThemedContent(stylePreset = StylePreset.Nova) {
            CompositionLocalProvider(LocalLayoutDirection provides direction) {
                key(direction) {
                    AttachmentGroup(Modifier.size(240.dp, 48.dp).testTag("group")) {
                        Attachment(Modifier.size(160.dp, 40.dp).testTag("first")) { }
                        Attachment(Modifier.size(210.dp, 40.dp).testTag("second")) { }
                        Attachment(Modifier.size(180.dp, 40.dp).testTag("last")) { }
                    }
                }
            }
        }
        for (dir in listOf(LayoutDirection.Ltr, LayoutDirection.Rtl)) {
            composeRule.runOnIdle { direction = dir }
            drag(110f, dir)
            val group = composeRule.onNodeWithTag("group").getUnclippedBoundsInRoot()
            val second = composeRule.onNodeWithTag("second").getUnclippedBoundsInRoot()
            val gap = if (dir == LayoutDirection.Ltr) second.left - group.left else group.right - second.right
            assertEquals(4f, gap.value, 1f, "The next card snaps 4dp from the logical start")
            drag(160f, dir)
            val last = composeRule.onNodeWithTag("last").getUnclippedBoundsInRoot()
            val end = if (dir == LayoutDirection.Ltr) group.right - last.right else last.left - group.left
            assertEquals(0f, end.value, 1f, "The last card can reach the container end")
        }
    }

    @Test fun oversizedCardCanSettleInsideItsOwnSnapArea() {
        setThemedContent(stylePreset = StylePreset.Nova) {
            AttachmentGroup(Modifier.size(240.dp, 48.dp).testTag("group")) {
                Attachment(Modifier.requiredWidth(500.dp).height(40.dp).testTag("wide")) { }
                Attachment(Modifier.size(160.dp, 40.dp)) { }
            }
        }
        composeRule.onNodeWithTag("group").performTouchInput {
            down(center)
            moveBy(Offset(-20f, 0f))
            moveBy(Offset(-110f, 0f))
        }
        composeRule.waitForIdle()
        val held = composeRule.onNodeWithTag("wide").getUnclippedBoundsInRoot().left.value
        kotlin.test.assertTrue(held < -50f, "The gesture must move inside the oversized card")
        composeRule.onNodeWithTag("group").performTouchInput {
            advanceEventTime(300)
            up()
        }
        composeRule.waitForIdle()
        val released = composeRule.onNodeWithTag("wide").getUnclippedBoundsInRoot().left.value
        assertEquals(held, released, 1f, "Mandatory snapping must not pull an oversized card back to its start")
    }

    @Test fun arrowKeysSnapWithoutActivatingIndependentActions() {
        var direction by mutableStateOf(LayoutDirection.Ltr)
        var clicks = 0
        setThemedContent(stylePreset = StylePreset.Nova) {
            CompositionLocalProvider(LocalLayoutDirection provides direction) {
                key(direction) {
                    AttachmentGroup(Modifier.size(240.dp, 48.dp).testTag("group")) {
                        Attachment(Modifier.size(160.dp, 40.dp)) {
                            AttachmentAction({ clicks++ }, label = "Remove first file") { Text { "×" } }
                        }
                        Attachment(Modifier.size(210.dp, 40.dp).testTag("second")) { }
                        Attachment(Modifier.size(180.dp, 40.dp)) { }
                    }
                }
            }
        }
        for (dir in listOf(LayoutDirection.Ltr, LayoutDirection.Rtl)) {
            composeRule.runOnIdle { direction = dir }
            val action = composeRule.onNodeWithContentDescription("Remove first file")
            action.performSemanticsAction(SemanticsActions.RequestFocus) { it() }
            action.performKeyInput {
                pressKey(
                    if (dir == LayoutDirection.Ltr) Key.DirectionRight else Key.DirectionLeft,
                )
            }
            composeRule.waitForIdle()
            val group = composeRule.onNodeWithTag("group").getUnclippedBoundsInRoot()
            val second = composeRule.onNodeWithTag("second").getUnclippedBoundsInRoot()
            val gap = if (dir == LayoutDirection.Ltr) second.left - group.left else group.right - second.right
            assertEquals(4f, gap.value, 1f)
            assertEquals(0, clicks, "Arrow navigation must not activate remove")
        }
    }

    @Test fun horizontalOverflowDoesNotChainToParentAtTheStartingEdge() {
        lateinit var parent: ScrollState
        setThemedContent(stylePreset = StylePreset.Nova) {
            parent = rememberScrollState(50)
            Row(Modifier.size(300.dp, 48.dp).horizontalScroll(parent)) {
                AttachmentGroup(Modifier.size(240.dp, 48.dp).testTag("group")) {
                    repeat(3) { Attachment(Modifier.size(160.dp, 40.dp)) { } }
                }
                Box(Modifier.size(500.dp, 40.dp))
            }
        }
        assertEquals(50, parent.value)
        drag(-80f, LayoutDirection.Ltr)
        assertEquals(50, parent.value, "Unconsumed horizontal motion must stay inside the attachment scroller")
    }

    @Test fun keyboardCanScrollInsideAnOversizedCard() {
        setThemedContent(stylePreset = StylePreset.Nova) {
            AttachmentGroup(Modifier.size(240.dp, 48.dp).testTag("group")) {
                Attachment(Modifier.requiredWidth(500.dp).height(40.dp).testTag("wide")) { }
                Attachment(Modifier.size(160.dp, 40.dp)) { }
            }
        }
        val group = composeRule.onNodeWithTag("group")
        group.performSemanticsAction(SemanticsActions.RequestFocus) { it() }
        val origin = group.getUnclippedBoundsInRoot().left.value
        for ((key, offset) in listOf(
            Key.DirectionRight to -40f,
            Key.DirectionRight to -80f,
            Key.DirectionLeft to -40f,
        )) {
            val before = composeRule.onNodeWithTag("wide").getUnclippedBoundsInRoot().left.value
            group.performKeyInput { pressKey(key) }
            composeRule.waitForIdle()
            assertEquals(
                offset,
                composeRule.onNodeWithTag("wide").getUnclippedBoundsInRoot().left.value - origin,
                1f,
                "Key $key from $before; target $offset relative to group",
            )
            group.assertIsFocused()
        }
    }

    @Test fun nonAttachmentContentDoesNotCreateSnapStops() {
        setThemedContent(stylePreset = StylePreset.Nova) {
            AttachmentGroup(Modifier.size(240.dp, 48.dp).testTag("group")) {
                Attachment(Modifier.size(160.dp, 40.dp).testTag("first")) { }
                Box(Modifier.size(80.dp, 40.dp))
                Attachment(Modifier.size(180.dp, 40.dp).testTag("second")) { }
                Attachment(Modifier.size(160.dp, 40.dp)) { }
            }
        }
        val origin = composeRule.onNodeWithTag("group").getUnclippedBoundsInRoot().left.value
        drag(110f, LayoutDirection.Ltr)
        assertEquals(0f, composeRule.onNodeWithTag("first").getUnclippedBoundsInRoot().left.value - origin, 1f)
        drag(200f, LayoutDirection.Ltr)
        assertEquals(4f, composeRule.onNodeWithTag("second").getUnclippedBoundsInRoot().left.value - origin, 1f)
    }

    @Test fun changingDirectionUpdatesExistingGroupSnapGeometry() {
        var direction by mutableStateOf(LayoutDirection.Ltr)
        setThemedContent(stylePreset = StylePreset.Nova) {
            CompositionLocalProvider(LocalLayoutDirection provides direction) {
                AttachmentGroup(Modifier.size(240.dp, 48.dp).testTag("group")) {
                    Attachment(Modifier.size(160.dp, 40.dp)) { }
                    Attachment(Modifier.size(210.dp, 40.dp).testTag("second")) { }
                    Attachment(Modifier.size(180.dp, 40.dp).testTag("last")) { }
                }
            }
        }
        drag(110f, LayoutDirection.Ltr)
        composeRule.runOnIdle { direction = LayoutDirection.Rtl }
        composeRule.waitForIdle()
        val group = composeRule.onNodeWithTag("group").getUnclippedBoundsInRoot()
        val second = composeRule.onNodeWithTag("second").getUnclippedBoundsInRoot()
        assertEquals(4f, (group.right - second.right).value, 1f)
        drag(160f, LayoutDirection.Rtl)
        val last = composeRule.onNodeWithTag("last").getUnclippedBoundsInRoot()
        assertEquals(0f, (last.left - group.left).value, 1f)
    }

    @Test fun tabReachesOffscreenActionsAndEnterActivatesOnlyTheFocusedOne() {
        var selected = 0
        setThemedContent(stylePreset = StylePreset.Nova) {
            AttachmentGroup(Modifier.size(240.dp, 58.dp).testTag("group")) {
                for (i in 1..3) Attachment(Modifier.size(160.dp, 50.dp), actions = {
                    AttachmentActions {
                        AttachmentAction({ selected = i }, label = "Remove file $i") { Text { "×" } }
                    }
                }) { AttachmentContent { AttachmentTitle("file-$i.pdf") } }
            }
        }
        val first = composeRule.onNodeWithContentDescription("Remove file 1")
        val second = composeRule.onNodeWithContentDescription("Remove file 2")

        fun assertFullyVisible(action: SemanticsNodeInteraction) {
            val viewport = composeRule.onNodeWithTag("group").getUnclippedBoundsInRoot()
            val bounds = action.getUnclippedBoundsInRoot()
            kotlin.test.assertTrue(
                bounds.left >= viewport.left && bounds.right <= viewport.right,
                "The focused action must fit inside the viewport: $bounds in $viewport",
            )
        }
        first.performSemanticsAction(SemanticsActions.RequestFocus) { it() }
        first.performKeyInput { pressKey(Key.Tab) }
        composeRule.waitForIdle()
        second.assertIsFocused().assertIsDisplayed()
        assertFullyVisible(second)
        second.performKeyInput { pressKey(Key.Enter) }
        assertEquals(2, selected)
        second.performKeyInput {
            keyDown(Key.ShiftLeft)
            pressKey(Key.Tab)
            keyUp(Key.ShiftLeft)
        }
        composeRule.waitForIdle()
        first.assertIsFocused().assertIsDisplayed()
        assertFullyVisible(first)
        assertEquals(2, selected)
    }

    @Test fun wheelSettlesWithoutAddingTouchFlingInertia() {
        setThemedContent(stylePreset = StylePreset.Nova) {
            AttachmentGroup(Modifier.size(240.dp, 48.dp).testTag("group")) {
                for (i in 1..6) Attachment(Modifier.size(160.dp, 40.dp).testTag("card$i")) { }
            }
        }
        val group = composeRule.onNodeWithTag("group")
        val viewport = group.getUnclippedBoundsInRoot()
        // Foundation 1.12 desktop wheel units differ by OS: at 240px wide,
        // 26 units become 260px (Mac), 312px (Windows), ~403px (Linux).
        // All three are in the third card's snap basin, not at the last card.
        group.performMouseInput {
            moveTo(center)
            repeat(4) {
                scroll(6.5f, ScrollWheel.Horizontal)
                advanceEventTime(2)
            }
        }
        var stableSince = 0L
        composeRule.waitUntil(timeoutMillis = 5000) {
            val offset = (composeRule.onNodeWithTag("card3").getUnclippedBoundsInRoot().left - viewport.left).value
            val now = System.nanoTime()
            if (kotlin.math.abs(offset - 4f) > 1f) {
                stableSince = 0L
            } else if (stableSince == 0L) {
                stableSince = now
            }
            stableSince != 0L && now - stableSince >= 250_000_000L
        }
        val third = composeRule.onNodeWithTag("card3").getUnclippedBoundsInRoot()
        assertEquals(
            4f,
            (third.left - viewport.left).value,
            1f,
            "A wheel sequence must settle at340dp without another touch-fling decay",
        )
    }
}
