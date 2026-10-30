package com.toutiaoyan.compose.ui.components.drawer

import androidx.compose.ui.unit.Density
import androidx.compose.ui.unit.dp
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFalse
import kotlin.test.assertTrue

class DrawerTest {
    @Test
    fun a_small_drag_below_the_threshold_springs_back_open() {
        // 400px tall drawer, dragged only 50px (12.5%) toward closing -- below the 30% default.
        val dismiss = shouldDismissDrawer(dragOffsetPx = 50f, contentExtentPx = 400f, thresholdFraction = 0.3f)
        assertFalse(dismiss)
    }

    @Test
    fun a_drag_past_the_threshold_dismisses() {
        // 130px of 400px is 32.5%, past the 30% default threshold.
        val dismiss = shouldDismissDrawer(dragOffsetPx = 130f, contentExtentPx = 400f, thresholdFraction = 0.3f)
        assertTrue(dismiss)
    }

    @Test
    fun a_drag_exactly_at_the_threshold_dismisses() {
        val dismiss = shouldDismissDrawer(dragOffsetPx = 120f, contentExtentPx = 400f, thresholdFraction = 0.3f)
        assertTrue(dismiss)
    }

    @Test
    fun zero_drag_never_dismisses() {
        val dismiss = shouldDismissDrawer(dragOffsetPx = 0f, contentExtentPx = 400f, thresholdFraction = 0.3f)
        assertFalse(dismiss)
    }

    @Test
    fun an_unmeasured_drawer_never_dismisses_rather_than_dividing_by_zero() {
        val dismiss = shouldDismissDrawer(dragOffsetPx = 50f, contentExtentPx = 0f, thresholdFraction = 0.3f)
        assertFalse(dismiss)
    }

    @Test
    fun a_lower_threshold_dismisses_with_a_shorter_drag() {
        // Same 50px drag that didn't clear a 30% threshold does clear a 10% one.
        val dismiss = shouldDismissDrawer(dragOffsetPx = 50f, contentExtentPx = 400f, thresholdFraction = 0.1f)
        assertTrue(dismiss)
    }

    @Test
    fun fromNumber_maps_fraction_and_absolute_like_base_ui() {
        assertEquals(DrawerSnapPoint.Fraction(0.5f), DrawerSnapPoint.fromNumber(0.5f))
        assertEquals(DrawerSnapPoint.Fraction(1f), DrawerSnapPoint.fromNumber(1f))
        assertEquals(DrawerSnapPoint.Absolute(148.dp), DrawerSnapPoint.fromNumber(148f))
    }

    @Test
    fun resolvePx_uses_viewport_fraction_or_density() {
        val density = Density(density = 2f)
        assertEquals(400f, DrawerSnapPoint.Fraction(0.5f).resolvePx(800f, density))
        assertEquals(296f, DrawerSnapPoint.Absolute(148.dp).resolvePx(800f, density))
    }

    @Test
    fun snap_closing_offsets_put_taller_snaps_nearer_zero() {
        val offsets = drawerSnapClosingOffsets(snapHeightsPx = listOf(200f, 800f), maxHeightPx = 800f)
        assertEquals(listOf(600f, 0f, 800f), offsets)
    }

    @Test
    fun nearest_snap_without_sequential_picks_closest_offset() {
        val offsets = listOf(600f, 0f, 800f)
        assertEquals(
            1,
            nearestDrawerSnapIndex(50f, offsets, snapToSequentialPoints = false, fromIndex = 0, velocityClosing = 0f),
        )
        assertEquals(
            2,
            nearestDrawerSnapIndex(750f, offsets, snapToSequentialPoints = false, fromIndex = 1, velocityClosing = 0f),
        )
    }

    @Test fun sequential_snap_uses_physical_offsets_instead_of_list_order() {
        val offsets = listOf(600f, 0f, 800f)
        assertEquals(0, nearestDrawerSnapIndex(20f, offsets, true, 1, 1000f))
        assertEquals(2, nearestDrawerSnapIndex(650f, offsets, true, 0, 1000f))
        assertEquals(1, nearestDrawerSnapIndex(580f, offsets, true, 0, -1000f))
        assertEquals(0, nearestDrawerSnapIndex(700f, offsets, true, 2, -1000f))
    }

    @Test fun sequential_zero_velocity_keeps_a_short_cancelled_drag_at_its_snap() {
        assertEquals(1, nearestDrawerSnapIndex(20f, listOf(600f, 0f, 800f), true, 1, 0f))
    }

    @Test fun deeperDrawersContributeToEveryAncestorStack() {
        val root = DrawerNestHost()
        val child = DrawerNestHost(root)
        root.onChildOpenChange(true)
        child.onChildOpenChange(true)
        assertEquals(2, root.openChildCount)
        assertEquals(1, child.openChildCount)
        child.onChildOpenChange(false)
        assertEquals(1, root.openChildCount)
        root.onChildOpenChange(false)
        assertEquals(0, root.openChildCount)
    }
}
