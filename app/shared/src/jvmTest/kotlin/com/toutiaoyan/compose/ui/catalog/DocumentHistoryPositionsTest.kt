package com.toutiaoyan.compose.ui.catalog

import org.junit.Test
import kotlin.test.assertEquals
import kotlin.test.assertNull

class DocumentHistoryPositionsTest {
    private fun DocumentHistoryPositions.fragmentPosition(id: String): Double? =
        restoreForTraversal(id, hasFragment = true, crossRoute = false, savedPosition = position(id))

    @Test fun explicitAnchorPushThenRepeatedTraversalKeepsIndependentSamples() {
        val positions = DocumentHistoryPositions()
        positions.record("first-entry", 5976.0) // Anchor 5806, then independent +170 input.
        positions.depart("first-entry", sameRouteTraversal = false)
        positions.record("second-entry", 6554.0) // Anchor 6434, then independent +120 input.
        assertNull(positions.fragmentPosition("first-entry"), "Explicit push departure must re-seek")
        assertEquals(5976.0, positions.position("first-entry"), "Reload y is not traversal intent")
        repeat(2) {
            positions.depart("second-entry", sameRouteTraversal = true)
            if (it == 0) {
                assertNull(positions.fragmentPosition("first-entry"))
            } else {
                assertEquals(5806.0, positions.fragmentPosition("first-entry"))
            }
            positions.record("first-entry", 5806.0)
            positions.depart("first-entry", sameRouteTraversal = true)
            assertEquals(6554.0, positions.fragmentPosition("second-entry"))
            assertEquals(5806.0, positions.fragmentPosition("first-entry"))
        }
        positions.record("second-entry", 6600.0)
        positions.depart("second-entry", sameRouteTraversal = false)
        assertNull(positions.fragmentPosition("second-entry"), "A later explicit push resets eligibility")
        assertEquals(6600.0, positions.position("second-entry"))
    }

    @Test fun crossRouteAndUnownedDeparturesDoNotCreateFragmentTraversalIntent() {
        val positions = DocumentHistoryPositions()
        positions.record("no-hash", 170.0)
        positions.depart("no-hash", sameRouteTraversal = false)
        assertEquals(170.0, positions.position("no-hash"), "Existing no-hash restore sample is retained")
        positions.record("cross-route", 6554.0)
        positions.depart("cross-route", sameRouteTraversal = false)
        assertNull(positions.fragmentPosition("cross-route"))
        assertEquals(6554.0, positions.position("cross-route"), "Route-cache policy remains external")
        assertEquals(170.0, positions.restoreForTraversal("no-hash", false, false, 170.0))
        assertEquals(190.0, positions.restoreForTraversal("new-owner", false, false, 190.0), "Persisted fallback")
        positions.depart("cross-route", sameRouteTraversal = true)
        assertNull(positions.restoreForTraversal("cross-route", true, true, 6554.0), "Route cache wins")
        positions.depart(null, sameRouteTraversal = true)
        assertNull(positions.restoreForTraversal(null, true, false, 6554.0))
        assertNull(positions.fragmentPosition("foreign-entry"))
    }
}
