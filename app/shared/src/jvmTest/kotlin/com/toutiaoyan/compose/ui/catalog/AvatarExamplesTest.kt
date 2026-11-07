package com.toutiaoyan.compose.ui.catalog

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.width
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.toPixelMap
import androidx.compose.ui.input.key.Key
import androidx.compose.ui.test.*
import androidx.compose.ui.test.junit4.v2.createComposeRule
import androidx.compose.ui.unit.dp
import com.toutiaoyan.compose.ui.catalog.docs.avatarDoc
import com.toutiaoyan.compose.ui.components.overlay.OverlayHost
import com.toutiaoyan.compose.ui.theme.Theme
import com.toutiaoyan.compose.ui.tokens.StylePreset
import org.junit.Rule
import kotlin.math.abs
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
        // Base UI pointer opens leave every row unhighlighted; the first ArrowDown reaches row 1.
        rule.onNodeWithText("Profile").assertIsNotFocused()
        rule.onNodeWithText("Billing").assertIsNotFocused()
        rule.onNodeWithText("Profile").performKeyInput { pressKey(Key.DirectionUp) }
        rule.onNodeWithText("Log out").assertIsFocused()
        rule.onNodeWithText("Log out").performKeyInput { pressKey(Key.MoveHome) }
        rule.onNodeWithText("Profile").assertIsFocused()
        rule.onNodeWithText("Profile").performKeyInput { pressKey(Key.MoveEnd) }
        rule.onNodeWithText("Log out").assertIsFocused()
        rule.onNodeWithText("Log out").performKeyInput { pressKey(Key.DirectionDown) }
        rule.onNodeWithText("Profile").assertIsFocused()
        rule.onNodeWithText("Profile").performKeyInput { pressKey(Key.DirectionDown) }
        rule.onNodeWithText("Billing").assertIsFocused()
        rule.onNodeWithText("Billing").performKeyInput { pressKey(Key.Escape) }
        rule.onNodeWithText("Profile").assertDoesNotExist()
        rule.onNodeWithContentDescription("Open user menu").assertIsFocused()
        assertTrue(
            avatarDoc.examples.single { it.title == "Dropdown" }.code.contains("DropdownMenuGroup {"),
        )
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

    @Test fun docsKeepOfficialRecipesFirstAndLabelKotlinFallback() {
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
            ),
            avatarDoc.examples.take(10).map { it.title },
        )
        assertEquals("Kotlin extension: Fallback", avatarDoc.examples[10].title)
        assertNull(avatarDoc.examplesHeading)
        assertEquals(
            "Use the `size` prop to change the size of the avatar.",
            avatarDoc.examples.single { it.title == "Sizes" }.descriptionMarkdown,
        )
        assertEquals(
            "Use the `AvatarBadge` component to add a badge to the avatar. The badge is " +
                "positioned at the bottom right of the avatar. Use `Style` to customize its color.",
            avatarDoc.examples.single { it.title == "Badge" }.descriptionMarkdown,
        )
        assertEquals(
            "Use the `AvatarGroup` component to add a group of avatars.",
            avatarDoc.examples.single { it.title == "Avatar Group" }.descriptionMarkdown,
        )
        assertEquals(
            "Use `AvatarGroupCount` to add a count to the group.",
            avatarDoc.examples.single { it.title == "Avatar Group Count" }.descriptionMarkdown,
        )
        assertEquals(
            "You can use the `Avatar` component as a trigger for a dropdown menu.",
            avatarDoc.examples.single { it.title == "Dropdown" }.descriptionMarkdown,
        )
        assertEquals(
            "To enable RTL support in shadcn/ui, see the " +
                "[RTL configuration guide](https://ui.shadcn.com/docs/rtl).",
            avatarDoc.examples.single { it.title == "RTL" }.descriptionMarkdown,
        )
        assertEquals(6, avatarDoc.apiSections.size)
        assertFalse(avatarDoc.examples.any { "model = null" in it.code })
        val groupCountApi = avatarDoc.apiSections.single { it.title == "AvatarGroupCount" }
        assertEquals(
            "AvatarSize?",
            groupCountApi.properties.single { it.name == "size" }.type,
        )
        assertTrue(groupCountApi.descriptionMarkdown.contains("any sm member makes it sm"))
    }

    @Test fun badgeExampleKeepsThePhotoInColorLikeTheOfficialExample() {
        rule.setContent {
            Theme(preset = StylePreset.Nova, minTouchTarget = null) {
                Box(Modifier.width(120.dp)) {
                    avatarDoc.examples.single { it.title == "Badge" }.preview()
                }
            }
        }
        val pixels = rule.onNodeWithContentDescription("@shadcn").captureToImage().toPixelMap()
        var chromatic = 0
        for (y in 0 until pixels.height) {
            for (x in 0 until pixels.width) {
                val color = pixels[x, y]
                if (color.alpha > .9f &&
                    (
                        abs(color.red - color.green) > .05f ||
                            abs(color.green - color.blue) > .05f ||
                            abs(color.red - color.blue) > .05f
                    )
                ) {
                    chromatic++
                }
            }
        }
        assertTrue(
            chromatic > pixels.width * pixels.height / 10,
            "The official Badge example keeps the colored photo; chromatic=$chromatic",
        )
    }
}
