package com.toutiaoyan.compose.ui.components.togglegroup

import androidx.compose.runtime.SideEffect
import androidx.compose.ui.test.*
import androidx.compose.ui.test.junit4.v2.createComposeRule
import com.toutiaoyan.compose.ui.theme.Theme
import org.junit.Rule
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertSame

class ToggleGroupCancellationTest {
    @get:Rule val rule = createComposeRule()

    private fun cancellation(
        itemVeto: Boolean,
        groupVeto: Boolean,
        controlled: Boolean = false,
    ) {
        val events = mutableListOf<String>()
        var itemDetails: ToggleGroupChangeDetails? = null
        rule.setContent {
            Theme {
                ToggleGroup(
                    value = if (controlled) emptySet() else null,
                    onValueChange = { events.add("accepted") },
                    onValueChangeRequest = { next, details ->
                        assertSame(itemDetails, details)
                        assertEquals(setOf("a"), next)
                        events.add("group")
                        if (groupVeto) details.cancel()
                    },
                ) {
                    val state = toggleGroupState()
                    SideEffect { if (state.isPressed("a")) events.add("committed") }
                    ToggleGroupItem(
                        "a",
                        "Alpha",
                        onPressedChange = { next, details ->
                            assertEquals(true, next)
                            itemDetails = details
                            events.add("item")
                            if (itemVeto) details.cancel()
                        },
                    )
                }
            }
        }
        rule.onNodeWithText("Alpha").performClick()
        rule.waitForIdle()
        val expected =
            when {
                itemVeto -> listOf("item")
                groupVeto -> listOf("item", "group")
                controlled -> listOf("item", "group", "accepted")
                else -> listOf("item", "group", "accepted", "committed")
            }
        assertEquals(expected, events)
        if (itemVeto || groupVeto || controlled) {
            rule.onNodeWithText("Alpha").assertIsOff()
        } else {
            rule.onNodeWithText("Alpha").assertIsOn()
        }
    }

    @Test
    fun itemVetoStopsGroup() = cancellation(itemVeto = true, groupVeto = false)

    @Test
    fun groupVetoStopsUncontrolledCommit() = cancellation(itemVeto = false, groupVeto = true)

    @Test
    fun itemThenGroupThenCommit() = cancellation(itemVeto = false, groupVeto = false)

    @Test
    fun controlledCallerCanReject() = cancellation(itemVeto = false, groupVeto = false, controlled = true)
}
