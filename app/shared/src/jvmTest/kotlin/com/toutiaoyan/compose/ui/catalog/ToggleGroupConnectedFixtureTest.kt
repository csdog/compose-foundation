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
    ) {
        val axis = if (vertical) "vertical" else "horizontal"
        val direction = if (rtl) "rtl" else "ltr"
        val config =
            ToggleGroupConnectedFixtureConfig.fromPath("/__verification/toggle-group-connected/$axis-$direction")
        assertEquals(ToggleGroupConnectedFixtureConfig(vertical, rtl), config)
        rule.setContent { ToggleGroupConnectedFixture(checkNotNull(config), isDark = dark) }
        val a = rule.onNodeWithText("A")
        val c = rule.onNodeWithText("C")
        val aBounds = a.fetchSemanticsNode().boundsInRoot
        val bBounds = rule.onNodeWithText("B").fetchSemanticsNode().boundsInRoot
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
        rule.onNodeWithText("B").assertIsNotEnabled().assertIsOff()
        rule.onNodeWithText("Before").requestFocus().performKeyInput { pressKey(Key.Tab) }
        a.assertIsFocused().assertIsOff().performKeyInput {
            pressKey(
                when {
                    vertical -> Key.DirectionDown
                    rtl -> Key.DirectionLeft
                    else -> Key.DirectionRight
                },
            )
        }
        c.assertIsFocused().performKeyInput { pressKey(Key.Spacebar) }
        c.assertIsOn().performKeyInput { pressKey(Key.Tab) }
        rule.onNodeWithText("After").assertIsFocused().performKeyInput {
            keyDown(Key.ShiftLeft)
            pressKey(Key.Tab)
            keyUp(Key.ShiftLeft)
        }
        c.assertIsFocused().assertIsOn()
        rule.onNodeWithText("B").assertIsNotEnabled().assertIsOff()
    }

    @Test fun horizontalLtrLight() = verify(false, false, false)

    @Test fun horizontalRtlLight() = verify(false, true, false)

    @Test fun verticalLtrLight() = verify(true, false, false)

    @Test fun verticalRtlLight() = verify(true, true, false)

    @Test fun horizontalLtrDark() = verify(false, false, true)

    @Test fun horizontalRtlDark() = verify(false, true, true)

    @Test fun verticalLtrDark() = verify(true, false, true)

    @Test fun verticalRtlDark() = verify(true, true, true)

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
