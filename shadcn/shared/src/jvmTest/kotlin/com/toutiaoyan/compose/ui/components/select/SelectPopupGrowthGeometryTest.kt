package com.toutiaoyan.compose.ui.components.select

import kotlin.test.Test
import kotlin.test.assertEquals

class SelectPopupGrowthGeometryTest {
    @Test fun growthRoundingRespectsTheFractionalHeightLimit() {
        val geometry =
            SelectPopupGeometry(
                top = 40f,
                height = 100f,
                initialScroll = 0f,
                itemAligned = true,
                growthAnchor = SelectPopupAnchor.Bottom,
            )
        val result =
            checkNotNull(
                selectPopupGrowth(
                    geometry,
                    windowHeight = 200f,
                    scroll = 10f,
                    maxScroll = 20f,
                    maxHeight = 103f,
                    scale = 2f,
                ),
            )
        assertEquals(103f, result.geometry.height)
        assertEquals(37f, result.geometry.top)
        assertEquals(7f, result.scroll)
    }
}
