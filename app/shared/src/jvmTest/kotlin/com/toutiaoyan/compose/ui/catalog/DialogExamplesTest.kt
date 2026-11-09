package com.toutiaoyan.compose.ui.catalog

import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.compose.ui.platform.LocalWindowInfo
import androidx.compose.ui.platform.WindowInfo
import androidx.compose.ui.unit.IntSize
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.width
import androidx.compose.ui.Modifier
import androidx.compose.ui.input.key.Key
import androidx.compose.ui.test.*
import androidx.compose.ui.test.junit4.v2.createComposeRule
import androidx.compose.ui.unit.dp
import com.toutiaoyan.compose.ui.catalog.docs.componentPageMarkdown
import com.toutiaoyan.compose.ui.catalog.docs.dialogDoc
import com.toutiaoyan.compose.ui.components.overlay.OverlayHost
import com.toutiaoyan.compose.ui.theme.Theme
import com.toutiaoyan.compose.ui.tokens.StylePreset
import org.junit.Rule
import kotlin.test.*

class DialogExamplesTest {
    @get:Rule val rule = createComposeRule()

    private fun show(title: String) {
        rule.setContent {
            Theme(preset = StylePreset.Nova, minTouchTarget = null) {
                OverlayHost {
                    Box(Modifier.width(560.dp).height(600.dp)) {
                        dialogDoc.examples.single { it.title == title }.preview()
                    }
                }
            }
        }
    }

    @Test fun officialRecipesKeepOfficialOrderAndKotlinExtensionsComeAfter() {
        val titles = dialogDoc.examples.map { it.title }
        assertEquals(
            listOf(
                "Edit profile",
                "Custom Close Button",
                "No Close Button",
                "Sticky Footer",
                "Scrollable Content",
                "RTL",
            ),
            titles.take(6),
        )
        assertEquals(
            listOf(
                "Kotlin extension: Custom close icon",
                "Kotlin extension: Default close button",
                "Kotlin extension: Size",
                "Kotlin extension: Default size",
                "Kotlin extension: Non-modal",
                "Kotlin extension: Modal",
                "Kotlin extension: Default modal",
                "Kotlin extension: Non-dismissible",
                "Kotlin extension: Default dismiss",
                "Kotlin extension: Disabled close",
                "Kotlin extension: Default disabled",
                "Kotlin extension: Prevent pointer dismiss",
                "Kotlin extension: Prevent interact outside",
                "Kotlin extension: Accessible close label",
                "Kotlin extension: Content close label",
                "Kotlin extension: Default close label",
                "Kotlin extension: Uncontrolled",
                "Kotlin extension: Default open",
                "Kotlin extension: Title and description",
                "Kotlin extension: Footer",
                "Kotlin extension: Footer close button",
                "Kotlin extension: Header",
                "Kotlin extension: Trigger",
                "Kotlin extension: Content",
                "Kotlin extension: Title",
                "Kotlin extension: Close",
                "Kotlin extension: Description",
                "Kotlin extension: Overlay",
                "Kotlin extension: Portal",
            ),
            titles.drop(6),
        )
        assertTrue(titles.drop(6).all { it.startsWith("Kotlin extension: ") })
    }

    @Test fun demoOpensAndCancelCloses() {
        show("Edit profile")
        rule.onNodeWithText("Open Dialog").performClick()
        rule.onNodeWithText("Edit profile").assertIsDisplayed()
        rule.onNodeWithText("Pedro Duarte").assertIsDisplayed()
        rule.onNodeWithText("@peduarte").assertIsDisplayed()
        rule.onNodeWithText("Cancel").performClick()
        rule.waitForIdle()
        rule.onNodeWithText("Edit profile").assertDoesNotExist()
    }

    @Test fun customCloseButtonShowsTheOfficialShareLinkDialog() {
        show("Custom Close Button")
        rule.onNodeWithText("Share").performClick()
        rule.onNodeWithText("Share link").assertIsDisplayed()
        rule.onNodeWithText("Anyone who has this link will be able to view this.").assertIsDisplayed()
        rule.onNodeWithText("https://ui.shadcn.com/docs/installation").assertIsDisplayed()
        val header = rule.onNodeWithText("Share link").fetchSemanticsNode().boundsInRoot
        val close = rule.onNodeWithText("Close").fetchSemanticsNode().boundsInRoot
        assertEquals(header.left, close.left, .5f)
        rule.onNodeWithText("Close").performClick()
        rule.waitForIdle()
        rule.onNodeWithText("Share link").assertDoesNotExist()
    }

    @Test fun noCloseButtonOmitsTheCornerCloseControl() {
        show("No Close Button")
        rule.onNodeWithText("No Close Button").performClick()
        rule.onNodeWithText("This dialog doesn't have a close button in the top-right corner.")
            .assertIsDisplayed()
        // Trigger + heading only: the corner close control is not composed.
        rule.onAllNodesWithText("No Close Button").assertCountEquals(2)
        rule.onAllNodesWithContentDescription("Close").assertCountEquals(0)
    }

    @Test fun stickyFooterKeepsActionsVisibleWhileTheBodyScrolls() {
        show("Sticky Footer")
        rule.onNodeWithText("Sticky Footer").performClick()
        rule.onNodeWithText(
            "This dialog has a sticky footer that stays visible while the content scrolls.",
        ).assertIsDisplayed()
        rule.onNodeWithText("Close").assertIsDisplayed().performClick()
        rule.waitForIdle()
        rule.onNodeWithText("Sticky Footer").assertIsDisplayed()
    }

    @Test fun scrollableContentKeepsTheHeaderOutsideTheScrollContainer() {
        show("Scrollable Content")
        rule.onNodeWithText("Scrollable Content").performClick()
        rule.onNodeWithText("This is a dialog with scrollable content.").assertIsDisplayed()
        // The body is the only scroll container; the header and title stay outside it.
        rule.onAllNodes(hasScrollAction()).assertCountEquals(1)
        rule.onAllNodesWithText("Lorem ipsum dolor sit amet", substring = true).assertCountEquals(10)
    }

    @Test fun scrollableRecipeUsesHalfTheViewportAndKeepsTheCompleteParagraphs() {
        var viewport by mutableStateOf(IntSize(560, 600))
        val window =
            object : WindowInfo {
                override val isWindowFocused = true
                override val containerSize get() = viewport
            }
        rule.setContent {
            Theme(preset = StylePreset.Nova, minTouchTarget = null) {
                CompositionLocalProvider(LocalWindowInfo provides window) {
                    OverlayHost { dialogDoc.examples.single { it.title == "Scrollable Content" }.preview() }
                }
            }
        }
        rule.onNodeWithText("Scrollable Content").performClick()

        fun scrollHeight() = rule.onNode(hasScrollAction()).fetchSemanticsNode().boundsInRoot.height
        assertEquals(300f, scrollHeight(), .5f)
        rule.onAllNodesWithText("sunt in culpa qui officia deserunt mollit anim id est laborum.", substring = true)
            .assertCountEquals(10)
        rule.runOnIdle { viewport = IntSize(560, 480) }
        assertEquals(240f, scrollHeight(), .5f)
    }

    @Test fun footerExtensionCloseActionDismisses() {
        show("Kotlin extension: Footer")
        rule.onNodeWithText("Open dialog").performClick()
        rule.onNodeWithText("Notice").assertIsDisplayed()
        rule.onNodeWithText("Close").performClick()
        rule.waitForIdle()
        rule.onNodeWithText("Notice").assertDoesNotExist()
    }

    @Test fun disabledCloseControlDoesNotDismissTheDialog() {
        show("Kotlin extension: Disabled close")
        rule.onNodeWithText("Open dialog").performClick()
        rule.onNodeWithText("Notice").assertIsDisplayed()
        rule.onNodeWithContentDescription("Close").performClick()
        rule.waitForIdle()
        rule.onNodeWithText("Notice").assertIsDisplayed()
    }

    @Test fun escapeClosesAndRestoresFocusToTheTrigger() {
        show("Edit profile")
        rule.onNodeWithText("Open Dialog").performClick()
        rule.onNodeWithText("Edit profile").assertIsDisplayed()
        rule.onAllNodes(isRoot()).onLast().performKeyInput { pressKey(Key.Escape) }
        rule.waitForIdle()
        rule.onNodeWithText("Edit profile").assertDoesNotExist()
        rule.onNodeWithText("Open Dialog").assertIsFocused()
    }

    @Test fun rtlPreviewSwitchesBetweenTheThreeTranslations() {
        show("RTL")
        for ((label, trigger, title) in listOf(
            Triple("Arabic (العربية)", "فتح الحوار", "تعديل الملف الشخصي"),
            Triple("English", "Open Dialog", "Edit profile"),
            Triple("Hebrew (עברית)", "פתח דיאלוג", "ערוך פרופיל"),
        )) {
            rule.onNodeWithContentDescription("Preview language").performClick()
            rule.onAllNodesWithText(label).onLast().performClick()
            rule.onNodeWithText(trigger).performClick()
            rule.onNodeWithText(title).assertIsDisplayed()
            rule.onAllNodes(isRoot()).onLast().performKeyInput { pressKey(Key.Escape) }
            rule.waitUntil(3_000) {
                rule.onAllNodesWithText(title).fetchSemanticsNodes().isEmpty()
            }
        }
    }

    @Test fun rtlProfileFieldsAcceptEditsInEveryPreviewLanguage() {
        show("RTL")
        for ((label, trigger) in listOf(
            "Arabic (العربية)" to "فتح الحوار",
            "English" to "Open Dialog",
            "Hebrew (עברית)" to "פתח דיאלוג",
        )) {
            rule.onNodeWithContentDescription("Preview language").performClick()
            rule.onAllNodesWithText(label).onLast().performClick()
            rule.onNodeWithText(trigger).performClick()
            rule.onNodeWithText("Pedro Duarte").performTextReplacement("Updated name")
            rule.onNodeWithText("@peduarte").performTextReplacement("@updated")
            rule.onNodeWithText("Updated name").assertIsDisplayed()
            rule.onNodeWithText("@updated").assertIsDisplayed()
            rule.onAllNodes(isRoot()).onLast().performKeyInput { pressKey(Key.Escape) }
            rule.waitUntil(3_000) {
                rule.onAllNodesWithText("Updated name").fetchSemanticsNodes().isEmpty()
            }
        }
    }

    @Test fun exportKeepsOfficialCopyCompositionAndKotlinApi() {
        val markdown = componentPageMarkdown(dialogDoc)
        assertContains(markdown, "DialogContent")
        assertFalse(markdown.contains("## Examples"))
        val sections =
            listOf(
                "## Custom Close Button",
                "## No Close Button",
                "## Sticky Footer",
                "## Scrollable Content",
                "## RTL",
                "## API Reference",
            ).map { markdown.indexOf(it) }
        assertTrue(sections.all { it >= 0 }, "missing a recipe heading: $sections")
        assertEquals(sections.sorted(), sections)
        assertContains(markdown, "To enable RTL support in shadcn/ui")
        assertContains(markdown, "Kotlin extension: Size")
        assertContains(markdown, "DialogTrigger")
        assertFalse(markdown.contains("EmojiText"))
    }
}
