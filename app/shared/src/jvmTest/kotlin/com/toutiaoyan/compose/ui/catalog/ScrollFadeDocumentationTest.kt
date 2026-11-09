package com.toutiaoyan.compose.ui.catalog

import com.toutiaoyan.compose.ui.catalog.docs.ComponentGuideBlock
import com.toutiaoyan.compose.ui.catalog.docs.componentPageMarkdown
import com.toutiaoyan.compose.ui.catalog.docs.scrollFadeDoc
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFalse
import kotlin.test.assertTrue

class ScrollFadeDocumentationTest {
    @Test
    fun everyCopiedRecipeKeepsUnboundedNoOverflowContainersNonScrollable() {
        scrollFadeDoc.examples.take(7).forEach { example ->
            assertTrue(
                example.code.contains(
                    "val scroller = if (height == null) Modifier else Modifier.verticalScroll(state)",
                ),
                "${example.title}: copied helper must support the unconstrained no-overflow recipe",
            )
            assertTrue(example.code.contains("viewport.then(scroller).focusable().padding(6.dp)"), example.title)
            assertFalse(example.code.contains("viewport.verticalScroll(state)"), example.title)
        }
        val overflow = scrollFadeDoc.examples.single { it.title == "No Overflow, No Fade" }.code
        assertTrue(overflow.contains("height = null"))
        assertTrue(overflow.contains("private fun ScrollFadeFrame("))
        assertTrue(overflow.contains("import androidx.compose.foundation.verticalScroll"))
    }

    @Test
    fun copyPageIncludesAllUtilityClassesAndFallbackBeforeRtl() {
        val reference =
            scrollFadeDoc.beforeExamples.flatMap { it.blocks }
                .filterIsInstance<ComponentGuideBlock.Table>().single()
        assertEquals(14, reference.rows.size)
        assertEquals(3, reference.headers.size)
        val classes = reference.rows.map { it.first().removeSurrounding("`") }
        assertEquals(
            listOf(
                "scroll-fade", "scroll-fade-y", "scroll-fade-x", "scroll-fade-t", "scroll-fade-b",
                "scroll-fade-l", "scroll-fade-r", "scroll-fade-s", "scroll-fade-e", "scroll-fade-<number>",
                "scroll-fade-[<value>]", "scroll-fade-{t,b,s,e}-<number>",
                "scroll-fade-{t,b,s,e}-[<value>]", "scroll-fade-none",
            ),
            classes,
        )
        val page = componentPageMarkdown(scrollFadeDoc)
        classes.forEach { assertTrue(page.contains("`$it`"), it) }
        assertTrue(page.indexOf("## Fallback") > page.indexOf("## Disabling the Fade"))
        assertTrue(page.indexOf("## Fallback") < page.indexOf("## RTL"))
        assertTrue(page.contains("Browsers without support use a static fade"))
        assertTrue(page.contains("Compose drives the scroll-aware mask from the `ScrollState`"))
    }
}
