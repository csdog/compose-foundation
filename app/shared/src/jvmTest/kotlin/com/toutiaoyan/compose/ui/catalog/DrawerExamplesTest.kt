package com.toutiaoyan.compose.ui.catalog

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.width
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.Modifier
import androidx.compose.ui.input.key.Key
import androidx.compose.ui.test.*
import androidx.compose.ui.test.junit4.v2.createComposeRule
import androidx.compose.ui.unit.dp
import com.toutiaoyan.compose.ui.catalog.docs.componentPageMarkdown
import com.toutiaoyan.compose.ui.catalog.docs.drawerDoc
import com.toutiaoyan.compose.ui.components.overlay.OverlayHost
import com.toutiaoyan.compose.ui.theme.Theme
import com.toutiaoyan.compose.ui.tokens.StylePreset
import org.junit.Rule
import kotlin.test.*

class DrawerExamplesTest {
    @get:Rule val rule = createComposeRule()

    private fun show(
        title: String,
        viewportWidth: androidx.compose.ui.unit.Dp? = null,
    ) {
        rule.setContent {
            val width = viewportWidth
            CompositionLocalProvider(
                LocalCatalogViewportWidth provides width,
            ) {
                Theme(preset = StylePreset.Nova, minTouchTarget = null) {
                    OverlayHost {
                        Box(Modifier.width(560.dp).height(600.dp)) {
                            drawerDoc.examples.single { it.title == title }.preview()
                        }
                    }
                }
            }
        }
    }

    @Test fun officialRecipesKeepOfficialOrderAndKotlinExtensionsComeAfter() {
        val titles = drawerDoc.examples.map { it.title }
        assertEquals(
            listOf(
                "Delivery time",
                "Position",
                "Swipe Handle",
                "Nested",
                "Non Modal",
                "Snap Points",
                "Responsive",
            ),
            titles.take(7),
        )
        assertTrue(titles.drop(7).all { it.startsWith("Kotlin extension: ") })
        assertTrue(titles.drop(7).isNotEmpty())
    }

    @Test fun demoOpensFromTheTriggerAndCancelCloses() {
        show("Delivery time")
        rule.onNodeWithText("Open Drawer").performClick()
        rule.onNodeWithText("Pick a delivery time").assertIsDisplayed()
        rule.onNodeWithText("Confirm Delivery Time").assertIsDisplayed()
        rule.onNodeWithText("Cancel").performClick()
        rule.waitForIdle()
        rule.onNodeWithText("Pick a delivery time").assertDoesNotExist()
    }

    @Test fun confirmClosesTheDrawerAndShowsTheToast() {
        show("Delivery time")
        rule.onNodeWithText("Open Drawer").performClick()
        rule.onNodeWithText("Confirm Delivery Time").performClick()
        rule.waitForIdle()
        rule.onNodeWithText("Pick a delivery time").assertDoesNotExist()
        rule.onNodeWithText("Delivery time confirmed").assertIsDisplayed()
        rule.onNodeWithText("Standard delivery").assertIsDisplayed()
    }

    @Test fun positionRecipeShowsTheOfficialMoveGoalDrawer() {
        show("Position")
        rule.onNodeWithText("Open Left Drawer").performClick()
        rule.onNodeWithText("Move Goal").assertIsDisplayed()
        rule.onNodeWithText("Set your daily activity goal.").assertIsDisplayed()
        rule.onNodeWithText("Close").performClick()
        rule.waitForIdle()
        rule.onNodeWithText("Move Goal").assertDoesNotExist()
    }

    @Test fun swipeHandleRecipeMatchesTheOfficialHeader() {
        show("Swipe Handle")
        rule.onNodeWithText("Open Drawer").performClick()
        rule.onNodeWithText("Drawer").assertIsDisplayed()
        rule.onNodeWithText("Drawer with a swipe handle.").assertIsDisplayed()
        rule.onNodeWithText("Close").performClick()
        rule.waitForIdle()
        rule.onNodeWithText("Drawer with a swipe handle.").assertDoesNotExist()
    }

    @Test fun nestedDrawersStackFourLevels() {
        show("Nested")
        rule.onNodeWithText("Open Drawer").performClick()
        rule.onNodeWithText("Open another drawer from the same direction.").assertIsDisplayed()
        for ((trigger, title) in listOf(
            "Open Nested Drawer" to "Nested Drawer",
            "Open Third Drawer" to "Third Drawer",
            "Open Fourth Drawer" to "Fourth Drawer",
        )) {
            rule.onNodeWithText(trigger).performClick()
            rule.onNodeWithText(title).assertIsDisplayed()
        }
        for (title in listOf("Fourth Drawer", "Third Drawer", "Nested Drawer", "Drawer")) {
            rule.onAllNodesWithText("Close").onLast().performClick()
            rule.waitUntil(3_000) {
                rule.onAllNodesWithText(title).fetchSemanticsNodes().isEmpty()
            }
        }
    }

    @Test fun nonModalDrawerOpensAndCloses() {
        show("Non Modal")
        rule.onNodeWithText("Non Modal").performClick()
        rule.onNodeWithText("Non Modal Drawer").assertIsDisplayed()
        rule.onNodeWithText("Close").performClick()
        rule.waitForIdle()
        rule.onNodeWithText("Non Modal Drawer").assertDoesNotExist()
    }

    @Test fun snapPointsDrawerStartsAtTheCompactPeek() {
        show("Snap Points")
        rule.onNodeWithText("Open Snap Drawer").performClick()
        rule.onNodeWithText("Snap points").assertIsDisplayed()
        rule.onNodeWithText(
            "Drag the drawer to snap between a compact peek and a near full-height view.",
        ).assertIsDisplayed()
        rule.onNodeWithText("Close").performClick()
        rule.waitForIdle()
        rule.onNodeWithText("Snap points").assertDoesNotExist()
    }

    @Test fun responsiveRecipeUsesTheDialogOnDesktopAndTheDrawerOnMobile() {
        show("Responsive", viewportWidth = 1024.dp)
        rule.onNodeWithText("Edit Profile").performClick()
        rule.onNodeWithText("Edit profile").assertIsDisplayed()
        rule.onNodeWithText("shadcn@example.com").assertIsDisplayed()
        rule.onAllNodes(isRoot()).onLast().performKeyInput { pressKey(Key.Escape) }
        rule.waitForIdle()
        rule.onNodeWithText("Edit profile").assertDoesNotExist()

        show("Responsive", viewportWidth = 390.dp)
        rule.onNodeWithText("Edit Profile").performClick()
        rule.onNodeWithText("Edit profile").assertIsDisplayed()
        rule.onNodeWithText("@shadcn").assertIsDisplayed()
    }

    @Test fun undismissibleExtensionBlocksOutsidePressButKeepsCloseControl() {
        show("Kotlin extension: Undismissible")
        rule.onNodeWithText("Open drawer").performClick()
        rule.onAllNodes(isRoot()).onLast().performTouchInput { click(Offset(2f, 2f)) }
        rule.waitForIdle()
        rule.onNodeWithText("Notice").assertIsDisplayed()
        rule.onNodeWithContentDescription("Close").performClick()
        rule.waitForIdle()
        rule.onNodeWithText("Notice").assertDoesNotExist()
    }

    @Test fun footerExtensionCloseActionClosesTheDrawer() {
        show("Kotlin extension: Footer")
        rule.onNodeWithText("Open drawer").performClick()
        rule.onNodeWithText("Close").performClick()
        rule.waitForIdle()
        rule.onNodeWithText("Notice").assertDoesNotExist()
    }

    @Test fun exportIncludesMigrationCalloutAndHonestStylingTables() {
        val markdown = componentPageMarkdown(drawerDoc)
        val callout = markdown.indexOf("The drawer now follows [Base UI]")
        assertTrue(callout >= 0 && callout < markdown.indexOf("## Installation"))
        assertContains(markdown, "| Web variable | Default | Compose behavior |")
        assertContains(markdown, "| Web attribute | Values / when set | Compose mapping |")
        assertContains(markdown, "No public swipe-progress state.")
        assertContains(markdown, "snap points keep an opacity floor of 0.5")
        assertContains(markdown, "Without the host, the popup fallback has no backdrop capture")
        assertContains(markdown, "`DrawerScope.nestedOpenCount > 0`")
    }

    @Test fun exportKeepsOfficialSectionsInOrder() {
        val markdown = componentPageMarkdown(drawerDoc)
        assertFalse(markdown.contains("## Examples"))
        val order =
            listOf(
                "## Custom Sizes",
                "## Styling",
                "## Position",
                "## Swipe Handle",
                "## Nested",
                "## Non Modal",
                "## Snap Points",
                "## Responsive",
                "## Migrating from Vaul",
                "## API Reference",
            ).map { markdown.indexOf(it) }
        assertTrue(order.all { it >= 0 }, "missing a section heading: $order")
        assertEquals(order.sorted(), order)
        assertContains(markdown, "DrawerContent")
        assertContains(markdown, "## Swipe direction")
        assertContains(markdown, "DrawerSwipeDirection")
        assertFalse(markdown.contains("EmojiText"))
    }
}
