package com.toutiaoyan.compose.ui.components.scrollarea

import androidx.compose.ui.unit.LayoutDirection
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFalse
import kotlin.test.assertTrue

class ScrollTypeTest {
    @Test
    fun mayShow_auto_onlyWhenOverflowing() {
        assertTrue(scrollbarMayShow(ScrollType.Auto, overflowing = true))
        assertFalse(scrollbarMayShow(ScrollType.Auto, overflowing = false))
    }

    @Test
    fun mayShow_always_regardlessOfOverflow() {
        assertTrue(scrollbarMayShow(ScrollType.Always, overflowing = true))
        assertTrue(scrollbarMayShow(ScrollType.Always, overflowing = false))
    }

    @Test
    fun mayShow_scrollAndHover_requireOverflow() {
        assertTrue(scrollbarMayShow(ScrollType.Scroll(), overflowing = true))
        assertFalse(scrollbarMayShow(ScrollType.Scroll(), overflowing = false))
        assertTrue(scrollbarMayShow(ScrollType.Hover(hideDelayMs = 1000), overflowing = true))
        assertFalse(scrollbarMayShow(ScrollType.Hover(), overflowing = false))
    }

    @Test
    fun dir_mapsToLayoutDirection() {
        assertEquals(LayoutDirection.Ltr, ScrollDir.Ltr.toLayoutDirection())
        assertEquals(LayoutDirection.Rtl, ScrollDir.Rtl.toLayoutDirection())
    }
}
