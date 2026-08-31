package com.toutiaoyan.compose.tailwind

import androidx.compose.ui.unit.dp
import com.toutiaoyan.compose.tailwind.core.TwContainer
import com.toutiaoyan.compose.tailwind.core.twContainerQuery
import kotlin.test.Test
import kotlin.test.assertEquals

class TwContainerQueryTest {
    @Test
    fun narrowerThanSmallestProvided_resolvesToBase() {
        val value =
            twContainerQuery(
                containerWidth = 200.dp,
                default = "base",
                sm = "sm",
                md = "md",
            )
        assertEquals("base", value)
    }

    @Test
    fun betweenSmAndMd_resolvesToSm() {
        val value =
            twContainerQuery(
                containerWidth = 400.dp,
                default = "base",
                sm = "sm",
                md = "md",
            )
        assertEquals("sm", value)
    }

    @Test
    fun pastEveryProvidedSize_resolvesToLargest() {
        val value =
            twContainerQuery(
                containerWidth = 900.dp,
                default = "base",
                sm = "sm",
                md = "md",
                lg = "lg",
            )
        assertEquals("lg", value)
    }

    @Test
    fun skipsUnprovidedSteps_andPicksLargestMatch() {
        // 500dp is past @md (448) but below @lg (512); lg omitted → md wins
        val value =
            twContainerQuery(
                containerWidth = 500.dp,
                default = "base",
                xs = "xs",
                md = "md",
                xl = "xl",
            )
        assertEquals("md", value)
    }

    @Test
    fun containerScale_matchesTailwindRemDefaults() {
        assertEquals(256.dp, TwContainer.xs3)
        assertEquals(384.dp, TwContainer.sm)
        assertEquals(448.dp, TwContainer.md)
        assertEquals(1280.dp, TwContainer.xl7)
    }
}
