package com.toutiaoyan.compose.ui.catalog

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.width
import androidx.compose.ui.Modifier
import androidx.compose.ui.input.key.Key
import androidx.compose.ui.test.*
import androidx.compose.ui.test.junit4.v2.createComposeRule
import androidx.compose.ui.unit.dp
import com.toutiaoyan.compose.ui.catalog.docs.avatarDoc
import com.toutiaoyan.compose.ui.components.overlay.OverlayHost
import com.toutiaoyan.compose.ui.theme.Theme
import com.toutiaoyan.compose.ui.tokens.StylePreset
import org.junit.Rule
import kotlin.test.*

class AvatarExamplesTest {
    @get:Rule val rule = createComposeRule()

    @Test fun defaultUsesRealImagesAndCount() {
        rule.setContent {
            Theme(preset = StylePreset.Nova, minTouchTarget = null) {
                Box(Modifier.width(260.dp)) { avatarDoc.examples.first().preview() }
            }
        }
        rule.onAllNodesWithContentDescription("@shadcn").assertCountEquals(2)
        rule.onNodeWithContentDescription("@maxleiter").assertIsDisplayed()
        rule.onAllNodesWithContentDescription("@evilrabbit").assertCountEquals(2)
        rule.onNodeWithText("+3").assertIsDisplayed()
        rule.onNodeWithText("CN").assertDoesNotExist()
    }

    @Test fun dropdownOpensSelectsAndDismissesWithEscape() {
        rule.setContent {
            Theme(preset = StylePreset.Nova, minTouchTarget = null) {
                OverlayHost { avatarDoc.examples.single { it.title == "Dropdown" }.preview() }
            }
        }
        rule.onNodeWithContentDescription("Open user menu").performClick()
        rule.onNodeWithText("Profile").assertIsDisplayed()
        rule.onNodeWithText("Billing").assertIsDisplayed()
        rule.onNodeWithText("Settings").assertIsDisplayed()
        rule.onNodeWithText("Log out").performClick()
        rule.onNodeWithText("Profile").assertDoesNotExist()
        rule.onNodeWithContentDescription("Open user menu").performClick()
        rule.onNodeWithText("Profile").assertIsFocused()
        rule.onNodeWithText("Profile").performKeyInput { pressKey(Key.DirectionDown) }
        rule.onNodeWithText("Billing").assertIsFocused()
        rule.onNodeWithText("Billing").performKeyInput { pressKey(Key.Escape) }
        rule.onNodeWithText("Profile").assertDoesNotExist()
    }

    @Test fun languageSelectorMirrorsAvatarsAndTranslatesCount() {
        rule.setContent {
            Theme(preset = StylePreset.Nova, minTouchTarget = null) {
                OverlayHost {
                    Box(
                        Modifier.width(340.dp),
                    ) { avatarDoc.examples.single { it.title == "RTL" }.preview() }
                }
            }
        }

        fun firstAvatarLeft() =
            rule.onAllNodesWithContentDescription(
                "@shadcn",
            ).fetchSemanticsNodes().first().boundsInRoot.left
        rule.onNodeWithText("+٣").assertIsDisplayed()
        val arabicLeft = firstAvatarLeft()
        rule.onNodeWithContentDescription("Preview language").performClick()
        rule.onNodeWithText("English").performClick()
        rule.onNodeWithText("+3").assertIsDisplayed()
        rule.onNodeWithText("+٣").assertDoesNotExist()
        assertTrue(firstAvatarLeft() < arabicLeft)
        rule.onNodeWithContentDescription("Preview language").performClick()
        rule.onNodeWithText("Hebrew (עברית)").performClick()
        rule.onNodeWithText("+3").assertIsDisplayed()
        assertEquals(arabicLeft, firstAvatarLeft(), .1f)
    }

    @Test fun docsIncludeEveryOfficialRecipeAndSixApiParts() {
        assertEquals(
            listOf(
                "Default",
                "Basic",
                "Badge",
                "Badge with Icon",
                "Avatar Group",
                "Avatar Group Count",
                "Avatar Group with Icon",
                "Sizes",
                "Dropdown",
                "RTL",
                "Fallback",
            ),
            avatarDoc.examples.map {
                it.title
            },
        )
        assertEquals(6, avatarDoc.apiSections.size)
        assertFalse(avatarDoc.examples.any { "model = null" in it.code })
    }
}
