package com.toutiaoyan.compose.ui.catalog

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.pager.PagerState
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.semantics.SemanticsActions
import androidx.compose.ui.test.*
import androidx.compose.ui.test.junit4.v2.createComposeRule
import androidx.compose.ui.text.TextLayoutResult
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.toutiaoyan.compose.ui.catalog.docs.CarouselAutoplay
import com.toutiaoyan.compose.ui.catalog.docs.carouselAutoplayEnabledWhileIdle
import com.toutiaoyan.compose.ui.catalog.docs.carouselDoc
import com.toutiaoyan.compose.ui.catalog.docs.componentPageMarkdown
import com.toutiaoyan.compose.ui.components.carousel.Carousel
import com.toutiaoyan.compose.ui.components.overlay.OverlayHost
import com.toutiaoyan.compose.ui.components.text.Text
import com.toutiaoyan.compose.ui.theme.Theme
import com.toutiaoyan.compose.ui.tokens.StylePreset
import org.junit.Rule
import kotlin.test.*

class CarouselExamplesTest {
    @get:Rule val rule = createComposeRule()

    private fun show(title: String) {
        rule.setContent {
            Theme(preset = StylePreset.Nova, minTouchTarget = null) {
                OverlayHost {
                    Box(
                        Modifier.width(560.dp).height(600.dp),
                        contentAlignment = Alignment.Center,
                    ) { carouselDoc.examples.single { it.title == title }.preview() }
                }
            }
        }
    }

    @Test fun defaultHasOfficialLargeSemiboldNumberAndCircularControls() {
        show("Default")
        val layouts = mutableListOf<TextLayoutResult>()
        rule.onNodeWithText("1").performSemanticsAction(SemanticsActions.GetTextLayoutResult) { it(layouts) }
        assertEquals(36f, layouts.single().layoutInput.style.fontSize.value)
        assertEquals(FontWeight.SemiBold, layouts.single().layoutInput.style.fontWeight)
        val slide = rule.onNodeWithContentDescription("Slide 1 of 5").fetchSemanticsNode().boundsInRoot
        val number = rule.onNodeWithText("1").fetchSemanticsNode().boundsInRoot
        assertEquals(slide.center.y, number.center.y, .5f)
        rule.onNodeWithContentDescription("Previous slide").assertWidthIsEqualTo(28.dp).assertIsNotEnabled()
        rule.onNodeWithContentDescription("Next slide").assertWidthIsEqualTo(28.dp).assertIsEnabled()
        assertNavigationInsideCarousel()
    }

    @Test fun verticalControlsStayInsideTheCarouselBounds() {
        show("Orientation")
        assertNavigationInsideCarousel()
    }

    private fun assertNavigationInsideCarousel() {
        val carousel =
            rule.onNodeWithContentDescription(
                "Slides",
                useUnmergedTree = true,
            ).fetchSemanticsNode().boundsInRoot
        for (label in listOf("Previous slide", "Next slide")) {
            val button = rule.onNodeWithContentDescription(label).assertIsDisplayed().fetchSemanticsNode().boundsInRoot
            assertTrue(
                button.left >= carousel.left && button.top >= carousel.top &&
                    button.right <= carousel.right && button.bottom <= carousel.bottom,
                "$label bounds $button must be inside carousel bounds $carousel",
            )
        }
    }

    @Test fun apiCounterFollowsSelectionAndReturnsToTheFirstSlide() {
        show("API")
        rule.onNodeWithText("Slide 1 of 5").assertIsDisplayed()
        rule.onNodeWithContentDescription("Next slide").performClick()
        rule.onNodeWithText("Slide 2 of 5").assertIsDisplayed()
        rule.onNodeWithContentDescription("Previous slide").performClick()
        rule.onNodeWithText("Slide 1 of 5").assertIsDisplayed()
    }

    @Test fun sizesShowsMultipleDistinctCards() {
        show("Sizes")
        rule.onNodeWithText("1").assertIsDisplayed()
        rule.onNodeWithText("2").assertIsDisplayed()
    }

    @Test fun rtlStartsWithArabicIndicNumbers() {
        show("RTL")
        rule.onNodeWithText("١").assertIsDisplayed()
        rule.onNodeWithContentDescription("Previous slide").assertIsNotEnabled()
        rule.onNodeWithContentDescription("Next slide").performClick()
        rule.onNodeWithText("٢").assertIsDisplayed()
    }

    @Test fun autoplayAdvancesAndStoppingCancelsFutureTicks() {
        lateinit var state: PagerState
        var playing by mutableStateOf(true)
        rule.mainClock.autoAdvance = false
        rule.setContent {
            val pager = rememberPagerState { 3 }
            SideEffect { state = pager }
            Theme(preset = StylePreset.Nova) {
                Carousel(pager, Modifier.width(200.dp).height(100.dp)) { Text { it.toString() } }
                CarouselAutoplay(pager, playing)
            }
        }
        rule.mainClock.advanceTimeBy(2900)
        rule.runOnIdle {
            assertEquals(1, state.settledPage)
            playing = false
        }
        rule.mainClock.advanceTimeBy(9000)
        rule.runOnIdle { assertEquals(1, state.settledPage) }
    }

    @Test fun autoplayResumesOnlyAfterHoverAndDragBothEnd() {
        assertTrue(carouselAutoplayEnabledWhileIdle(hovered = false, dragged = false))
        assertFalse(carouselAutoplayEnabledWhileIdle(hovered = true, dragged = false))
        assertFalse(carouselAutoplayEnabledWhileIdle(hovered = false, dragged = true))
        assertFalse(carouselAutoplayEnabledWhileIdle(hovered = true, dragged = true))
        assertTrue(carouselAutoplayEnabledWhileIdle(hovered = false, dragged = false))
    }

    @Test fun examplesAndExportedRecipesCoverTheOfficialPatterns() {
        assertEquals(8, carouselDoc.examples.size)
        assertEquals(
            "Kotlin extension: With dot indicators",
            carouselDoc.examples.last().title,
        )
        assertTrue(carouselDoc.examples.none { it.code.contains("TextTypography") })

        fun code(title: String) = carouselDoc.examples.single { it.title == title }.code
        assertTrue(code("Sizes").contains("PageSize.Fixed"))
        assertTrue(code("API").contains("state.settledPage"))
        assertTrue(code("Plugins").contains("delay(2000)"))
        assertTrue(code("Plugins").contains("Modifier.hoverable(hover)"))
        assertTrue(code("Plugins").contains("playing = !hovered && !dragged"))
        assertTrue(code("Default").contains("CarouselItem(Modifier.fillMaxSize())"))
        assertTrue(code("RTL").contains("LocalLayoutDirection provides"))
        assertTrue(code("RTL").contains("٠١٢٣٤٥٦٧٨٩"))
    }

    @Test fun pageKeepsOfficialInterleavedSectionOrder() {
        val titles = docSections(carouselDoc).map { it.title }
        val officialOrder =
            listOf(
                "About",
                "Installation",
                "Usage",
                "Composition",
                "Sizes",
                "Spacing",
                "Orientation",
                "Options",
                "API",
                "Events",
                "Plugins",
                "RTL",
                "API Reference",
            ).map { titles.indexOf(it) }
        assertTrue(officialOrder.all { it >= 0 }, "missing section: $officialOrder")
        assertEquals(officialOrder.sorted(), officialOrder)
        assertTrue(titles.indexOf("Options") < titles.indexOf("API"))
        assertTrue(titles.indexOf("Events") < titles.indexOf("Plugins"))

        val markdown = componentPageMarkdown(carouselDoc)
        val markdownOrder =
            listOf(
                "## About",
                "## Installation",
                "## Composition",
                "## Sizes",
                "## Spacing",
                "## Orientation",
                "## Options",
                "## API",
                "## Events",
                "## Plugins",
                "## RTL",
                "## API Reference",
            ).map { markdown.indexOf(it) }
        assertTrue(markdownOrder.all { it >= 0 }, "missing markdown section: $markdownOrder")
        assertEquals(markdownOrder.sorted(), markdownOrder)
        assertContains(markdown, "[Embla Carousel](https://www.embla-carousel.com/)")
        assertContains(markdown, "https://www.embla-carousel.com/api/options/")
        assertContains(markdown, "https://www.embla-carousel.com/api/events/")
        assertContains(markdown, "You can use the `plugins` prop to add plugins to the carousel.")
    }
}
