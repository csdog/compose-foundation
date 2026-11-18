package com.toutiaoyan.compose.ui.catalog

import com.toutiaoyan.compose.ui.catalog.docs.ComponentGuideBlock
import com.toutiaoyan.compose.ui.catalog.docs.componentPageMarkdown
import com.toutiaoyan.compose.ui.catalog.docs.shimmerDoc
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertTrue

class ShimmerDocumentationTest {
    @Test
    fun copyPageKeepsFallbackAndReducedMotionBetweenDisablingAndRtl() {
        val markdown = componentPageMarkdown(shimmerDoc)
        val headings = Regex("^## (.+)$", RegexOption.MULTILINE).findAll(markdown).map { it.groupValues[1] }.toList()
        val disabling = headings.indexOf("Disabling the Shimmer")
        assertEquals(
            listOf("Disabling the Shimmer", "Fallback", "Reduced Motion", "RTL"),
            headings.subList(disabling, disabling + 4),
        )
        assertEquals(1, headings.count { it == "Fallback" })
        assertEquals(1, headings.count { it == "Reduced Motion" })
        assertTrue(markdown.contains("On Web, shimmer observes `prefers-reduced-motion`"))
    }

    @Test
    fun utilityReferenceCoversAllSourceClassesAndMarksUnsupportedSpreadUnits() {
        val table =
            shimmerDoc.beforeExamples.flatMap { it.blocks }.filterIsInstance<ComponentGuideBlock.Table>().single()
        assertEquals(listOf("Class", "CSS behavior", "Kotlin equivalent"), table.headers)
        assertEquals(
            listOf(
                "shimmer", "shimmer-once", "shimmer-reverse", "shimmer-none",
                "shimmer-color-<color>", "shimmer-color-[<value>]", "shimmer-color-<color>/<pct>",
                "shimmer-duration-<number>", "shimmer-spread-<number>", "shimmer-spread-[<value>]",
                "shimmer-angle-<number>",
            ),
            table.rows.map { it.first().trim('`') },
        )
        assertTrue(table.rows.all { it.size == 3 })
        val markdown = componentPageMarkdown(shimmerDoc)
        assertTrue(markdown.contains("font-relative lengths and percentage spreads are not supported"))
        assertTrue(markdown.contains("The source previews measure `calc(3ch + 40px)` using their actual font."))
        val spread = markdown.indexOf("## Spread")
        val arbitraryLength = markdown.indexOf("For a one-off length")
        assertTrue(arbitraryLength > spread && arbitraryLength < markdown.indexOf("## Angle"))
        assertTrue(markdown.contains("shimmer-spread-[5rem]"))
        assertTrue(markdown.contains("rememberShimmerStyle(spread = 80.dp)"))
    }
}
