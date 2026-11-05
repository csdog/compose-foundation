package com.toutiaoyan.compose.ui.catalog

import androidx.compose.foundation.layout.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.test.*
import androidx.compose.ui.test.junit4.v2.createComposeRule
import androidx.compose.ui.unit.dp
import com.toutiaoyan.compose.ui.catalog.docs.hoverCardDoc
import com.toutiaoyan.compose.ui.components.overlay.OverlayHost
import com.toutiaoyan.compose.ui.theme.Theme
import com.toutiaoyan.compose.ui.tokens.StylePreset
import org.junit.Rule
import kotlin.test.*

class HoverCardExamplesTest {
    @get:Rule val rule = createComposeRule()

    private fun show(title: String) {
        rule.setContent {
            Theme(preset = StylePreset.Nova, minTouchTarget = null) {
                OverlayHost {
                    Box(Modifier.width(800.dp).height(650.dp), contentAlignment = Alignment.Center) {
                        hoverCardDoc.examples.single { it.title == title }.preview()
                    }
                }
            }
        }
    }

    private fun hover(
        text: String,
        expected: String,
    ) {
        rule.onNodeWithText(text).performMouseInput {
            enter(center, delayMillis = 0)
            moveTo(center, delayMillis = 0)
        }
        rule.waitUntil(3000) { rule.onAllNodesWithText(expected).fetchSemanticsNodes().isNotEmpty() }
        rule.onNodeWithText(expected).assertIsDisplayed()
    }

    @Test fun officialRecipesAndApiSectionsAreComplete() {
        assertEquals(listOf("Default", "Basic", "Sides", "RTL"), hoverCardDoc.examples.take(4).map { it.title })
        assertEquals(
            listOf(
                "HoverCard",
                "HoverCardTrigger",
                "HoverCardContent",
            ),
            hoverCardDoc.apiSections.map { it.title },
        )
        assertFalse(hoverCardDoc.examples.first().code.contains("Avatar("))
        assertFalse(hoverCardDoc.examples.single { it.title == "RTL" }.code.contains("RtlPreview("))
        assertEquals(
            listOf("Composition", "Trigger Delays", "Positioning"),
            hoverCardDoc.beforeExamples.map { it.title },
        )
        assertEquals(
            listOf("Default", "Basic", "Sides", "RTL"),
            hoverCardDoc.examples.take(4).map { it.title },
        )
        assertTrue(
            hoverCardDoc.examples.drop(4).all { it.title.startsWith("Kotlin extension: ") },
            "Kotlin-only recipes must be titled `Kotlin extension: ...`",
        )
    }

    @Test fun defaultShowsTheActualNextjsPreview() {
        show("Default")
        hover("Hover Here", "@nextjs")
        rule.onNodeWithText("The React Framework – created and maintained by @vercel.").assertIsDisplayed()
        rule.onNodeWithText("Joined December 2021").assertIsDisplayed()
    }

    @Test fun fourPhysicalSideExamplesUseTheirOwnCopy() {
        show("Sides")
        for (side in listOf("Left", "Top", "Bottom", "Right")) {
            hover(side, "This hover card appears on the " + side.lowercase() + " side of the trigger.")
            rule.onNodeWithText(side).performMouseInput { exit(delayMillis = 0) }
            rule.mainClock.advanceTimeBy(400)
            rule.waitForIdle()
        }
    }

    @Test fun rtlExamplesTranslateTheProductAndOfferBothLogicalSides() {
        show("RTL")
        rule.onNodeWithText("بداية السطر").assertIsDisplayed()
        rule.onNodeWithText("نهاية السطر").assertIsDisplayed()
        hover("بداية السطر", "سماعات لاسلكية")
        rule.onNodeWithText("٩٩.٩٩ $").assertIsDisplayed()
        rule.onNodeWithText("بداية السطر").performMouseInput { exit(delayMillis = 0) }
        rule.mainClock.advanceTimeBy(400)
        rule.onNodeWithContentDescription("Preview language").performClick()
        rule.onNodeWithText("English").performClick()
        rule.onNodeWithText("Inline Start").assertIsDisplayed()
        hover("Inline End", "Wireless Headphones")
        rule.onNodeWithText("$99.99").assertIsDisplayed()
        rule.onNodeWithText("Inline End").performMouseInput { exit(delayMillis = 0) }
        rule.mainClock.advanceTimeBy(400)
        rule.onNodeWithContentDescription("Preview language").performClick()
        rule.onNodeWithText("Hebrew (עברית)").performClick()
        rule.onNodeWithText("תחילת השורה").assertIsDisplayed()
        hover("סוף השורה", "אוזניות אלחוטיות")
        rule.onNodeWithText("99.99 $").assertIsDisplayed()
    }

    @Test fun rtlCopyUsesTheOfficialWording() {
        val rtl = hoverCardDoc.examples.single { it.title == "RTL" }
        assertTrue(rtl.descriptionMarkdown!!.contains("shadcn/ui"))
    }

    @Test fun controlledExamplePublishesItsOpenState() {
        show("Kotlin extension: Controlled")
        rule.onNodeWithText("Closed").assertIsDisplayed()
        hover("Controlled preview", "The parent owns the open state.")
        rule.onNodeWithText("Open").assertIsDisplayed()
    }
}
