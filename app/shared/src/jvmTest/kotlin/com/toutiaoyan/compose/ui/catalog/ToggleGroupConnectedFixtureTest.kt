package com.toutiaoyan.compose.ui.catalog

import androidx.compose.ui.unit.dp
import androidx.compose.ui.input.key.Key
import androidx.compose.ui.test.*
import androidx.compose.ui.test.junit4.v2.createComposeRule
import com.toutiaoyan.compose.ui.verification.ToggleGroupConnectedFixture
import com.toutiaoyan.compose.ui.verification.ToggleGroupConnectedFixtureConfig
import org.junit.Rule
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertNull
import kotlin.test.assertTrue

class ToggleGroupConnectedFixtureTest {
    @get:Rule val rule = createComposeRule()

    private fun verify(
        vertical: Boolean,
        rtl: Boolean,
        dark: Boolean,
        disableMiddle: Boolean = true,
    ) {
        val axis = if (vertical) "vertical" else "horizontal"
        val direction = if (rtl) "rtl" else "ltr"
        val suffix = if (disableMiddle) "" else "/all-enabled"
        val config =
            ToggleGroupConnectedFixtureConfig.fromPath("/__verification/toggle-group-connected/$axis-$direction$suffix")
        assertEquals(ToggleGroupConnectedFixtureConfig(vertical, rtl, disableMiddle), config)
        rule.setContent { ToggleGroupConnectedFixture(checkNotNull(config), isDark = dark) }
        val a = rule.onNodeWithText("A")
        val b = rule.onNodeWithText("B")
        val c = rule.onNodeWithText("C")
        val aBounds = a.fetchSemanticsNode().boundsInRoot
        val bBounds = b.fetchSemanticsNode().boundsInRoot
        val cBounds = c.fetchSemanticsNode().boundsInRoot
        // The pinned wrapper removes leading border paint without overlapping item bounds.
        val itemSize = with(rule.density) { 32.dp.toPx() }
        for (bounds in listOf(aBounds, bBounds, cBounds)) {
            assertEquals(itemSize, bounds.width)
            assertEquals(itemSize, bounds.height)
        }
        val groupBounds = rule.onNodeWithTag("connected-outline").fetchSemanticsNode().boundsInRoot
        if (vertical) {
            assertEquals(aBounds.bottom, bBounds.top)
            assertEquals(bBounds.bottom, cBounds.top)
            assertEquals(itemSize * 3, groupBounds.height)
        } else if (rtl) {
            assertEquals(aBounds.left, bBounds.right)
            assertEquals(bBounds.left, cBounds.right)
            assertEquals(itemSize * 3, groupBounds.width)
        } else {
            assertEquals(aBounds.right, bBounds.left)
            assertEquals(bBounds.right, cBounds.left)
            assertEquals(itemSize * 3, groupBounds.width)
        }
        assertTrue(aBounds.width > 0 && aBounds.height > 0)
        if (disableMiddle) b.assertIsNotEnabled() else b.assertIsEnabled()
        b.assertIsOff()
        rule.onNodeWithText("Before").requestFocus().performKeyInput { pressKey(Key.Enter) }
        rule.onNodeWithText("Before").assertIsOn().performKeyInput { pressKey(Key.Tab) }
        a.assertIsFocused().assertIsOff().performKeyInput {
            pressKey(
                when {
                    vertical -> Key.DirectionDown
                    rtl -> Key.DirectionLeft
                    else -> Key.DirectionRight
                },
            )
        }
        val target = if (disableMiddle) c else b
        target.assertIsFocused().performKeyInput { pressKey(Key.Spacebar) }
        target.assertIsOn()
        if (disableMiddle) b.assertIsOff()
        rule.onNodeWithText(
            if (disableMiddle) "B selected: false. B activations: 0." else "B selected: true. B activations: 1.",
        ).assertExists()
        target.performKeyInput { pressKey(Key.Enter) }
        target.assertIsOff()
        b.assertIsOff()
        rule.onNodeWithText(
            if (disableMiddle) "B selected: false. B activations: 0." else "B selected: false. B activations: 2.",
        ).assertExists()
        target.performKeyInput { pressKey(Key.Tab) }
        rule.onNodeWithText("After").assertIsFocused().performKeyInput { pressKey(Key.Spacebar) }
        rule.onNodeWithText("After").assertIsOn()
        rule.onNodeWithText("After").assertIsFocused().performKeyInput {
            keyDown(Key.ShiftLeft)
            pressKey(Key.Tab)
            keyUp(Key.ShiftLeft)
        }
        target.assertIsFocused().assertIsOff()
    }

    @Test fun horizontalLtrLight() = verify(false, false, false)

    @Test fun horizontalRtlLight() = verify(false, true, false)

    @Test fun verticalLtrLight() = verify(true, false, false)

    @Test fun verticalRtlLight() = verify(true, true, false)

    @Test fun horizontalLtrDark() = verify(false, false, true)

    @Test fun horizontalRtlDark() = verify(false, true, true)

    @Test fun verticalLtrDark() = verify(true, false, true)

    @Test fun verticalRtlDark() = verify(true, true, true)

    @Test fun allEnabledHorizontalLtr() = verify(false, false, false, disableMiddle = false)

    @Test fun allEnabledHorizontalRtl() = verify(false, true, false, disableMiddle = false)

    @Test fun allEnabledVerticalLtr() = verify(true, false, false, disableMiddle = false)

    @Test fun allEnabledVerticalRtl() = verify(true, true, false, disableMiddle = false)

    @Test fun verificationRoutesDoNotCaptureCatalogOrUnknownPaths() {
        listOf(
            "/toggle-group",
            "/",
            "/__verification/toggle-group-connected",
            "/__verification/toggle-group-connected/nope",
        )
            .forEach { assertNull(ToggleGroupConnectedFixtureConfig.fromPath(it)) }
    }
}
