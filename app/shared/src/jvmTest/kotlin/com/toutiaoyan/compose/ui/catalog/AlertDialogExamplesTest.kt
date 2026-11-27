package com.toutiaoyan.compose.ui.catalog

import androidx.compose.runtime.*
import androidx.compose.ui.test.*
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.compositeOver
import androidx.compose.ui.graphics.toPixelMap
import com.toutiaoyan.compose.ui.theme.currentTheme
import kotlin.math.abs
import androidx.compose.ui.semantics.SemanticsActions
import androidx.compose.ui.text.TextLayoutResult
import androidx.compose.ui.test.junit4.v2.createComposeRule
import com.toutiaoyan.compose.ui.catalog.docs.alertDialogDoc
import com.toutiaoyan.compose.ui.catalog.docs.componentPageMarkdown
import com.toutiaoyan.compose.ui.theme.Theme
import com.toutiaoyan.compose.ui.tokens.StylePreset
import org.junit.Rule
import kotlin.test.*

class AlertDialogExamplesTest {
    @get:Rule val rule = createComposeRule()

    @Test fun realExamplesOpenAndBothActionPathsClose() {
        var current by mutableStateOf("Default")
        rule.setContent {
            Theme(preset = StylePreset.Nova, minTouchTarget = null) {
                key(current) { alertDialogDoc.examples.single { it.title == current }.RenderPreview() }
            }
        }

        data class Example(
            val name: String,
            val trigger: String,
            val title: String,
            val cancel: String,
            val action: String,
        )
        for (example in listOf(
            Example("Default", "Show Dialog", "Are you absolutely sure?", "Cancel", "Continue"),
            Example("Basic", "Show Dialog", "Are you absolutely sure?", "Cancel", "Continue"),
            Example("Small", "Show Dialog", "Allow accessory to connect?", "Don't allow", "Allow"),
            Example("Media", "Share Project", "Share this project?", "Cancel", "Share"),
            Example("Small with Media", "Show Dialog", "Allow accessory to connect?", "Don't allow", "Allow"),
            Example("Destructive", "Delete Chat", "Delete chat?", "Cancel", "Delete"),
        )) {
            rule.runOnIdle { current = example.name }
            for (button in listOf(example.cancel, example.action)) {
                rule.onNodeWithText(example.trigger).performClick()
                rule.onNodeWithText(example.title).assertIsDisplayed()
                rule.onNodeWithText(button).performClick()
                rule.waitForIdle()
                rule.onNodeWithText(example.title).assertDoesNotExist()
            }
        }
    }

    @Test fun rtlPreviewChangesLanguageAndBothDialogSizesUseTranslation() {
        rule.setContent {
            Theme(preset = StylePreset.Nova, minTouchTarget = null) {
                alertDialogDoc.examples.single { it.title == "RTL" }.RenderPreview()
            }
        }

        data class Translation(
            val label: String,
            val trigger: String,
            val title: String,
            val cancel: String,
            val smallTrigger: String,
            val smallTitle: String,
            val deny: String,
        )
        var previous = "Arabic (العربية)"
        for (translation in listOf(
            Translation(
                "Arabic (العربية)",
                "إظهار الحوار",
                "هل أنت متأكد تمامًا؟",
                "إلغاء",
                "إظهار الحوار (صغير)",
                "السماح للملحق بالاتصال؟",
                "عدم السماح",
            ),
            Translation(
                "English",
                "Show Dialog",
                "Are you absolutely sure?",
                "Cancel",
                "Show Dialog (sm)",
                "Allow accessory to connect?",
                "Don't allow",
            ),
            Translation(
                "Hebrew (עברית)",
                "הצג דיאלוג",
                "האם אתה בטוח לחלוטין?",
                "ביטול",
                "הצג דיאלוג (קטן)",
                "לאפשר להתקן להתחבר?",
                "אל תאפשר",
            ),
        )) {
            if (translation.label != previous) {
                rule.onNodeWithText(previous).performClick()
                rule.onNodeWithText(translation.label).performClick()
                previous = translation.label
            }
            for ((trigger, title, cancel) in listOf(
                Triple(translation.trigger, translation.title, translation.cancel),
                Triple(translation.smallTrigger, translation.smallTitle, translation.deny),
            )) {
                rule.onNodeWithText(trigger).performClick()
                rule.onNodeWithText(title).assertIsDisplayed()
                rule.onNodeWithText(cancel).performClick()
                rule.waitForIdle()
                rule.onNodeWithText(title).assertDoesNotExist()
            }
        }
    }

    @Test fun destructiveSettingsLinkNavigatesCurrentPageWithoutConfirmingDeletion() {
        val navigation =
            object : DocNavigation {
                override var location by mutableStateOf(DocLocation("alert-dialog", "destructive"))

                override fun navigate(fragment: String) {
                    location = location.copy(fragment = fragment, revision = location.revision + 1)
                }
            }
        rule.setContent {
            CompositionLocalProvider(LocalDocNavigation provides navigation) {
                Theme(preset = StylePreset.Nova, minTouchTarget = null) {
                    alertDialogDoc.examples.single { it.title == "Destructive" }.RenderPreview()
                }
            }
        }
        rule.onNodeWithText("Delete Chat").performClick()
        val text = (
            "This will permanently delete this chat conversation. View " +
                "Settings delete any memories saved during this chat."
        )
        val node = rule.onNodeWithText(text)
        val layouts = mutableListOf<TextLayoutResult>()
        node.performSemanticsAction(SemanticsActions.GetTextLayoutResult) { it(layouts) }
        val bounds = layouts.single().getBoundingBox(text.indexOf("Settings") + 2)
        node.performTouchInput { click(bounds.center) }
        rule.waitForIdle()
        assertEquals("", navigation.location.fragment)
        assertEquals(1, navigation.location.revision)
        rule.onNodeWithText("Delete chat?").assertIsDisplayed()
        rule.onNodeWithText("Cancel").performClick()
    }

    @Test fun destructiveMediaBackgroundChangesWithLiveTheme() {
        var dark by mutableStateOf(false)
        var expected = Color.Unspecified
        rule.setContent {
            Theme(preset = StylePreset.Nova, isDark = dark, minTouchTarget = null) {
                val colors = currentTheme.colors
                expected = colors.destructive.copy(alpha = if (dark) .2f else .1f).compositeOver(colors.popover)
                alertDialogDoc.examples.single { it.title == "Destructive" }.RenderPreview()
            }
        }
        rule.onNodeWithText("Delete Chat").performClick()

        fun check() {
            val title = rule.onNodeWithText("Delete chat?").fetchSemanticsNode().boundsInRoot
            val image = rule.onAllNodes(isRoot()).onLast().captureToImage().toPixelMap()
            val pixel = image[(title.center.x - 14).toInt(), (title.top - 48).toInt()]
            assertTrue(
                abs(pixel.red - expected.red) < .035f &&
                    abs(pixel.green - expected.green) < .035f &&
                    abs(pixel.blue - expected.blue) < .035f,
                "Media background must repaint on theme changes: expected $expected, actual $pixel",
            )
        }
        check()
        rule.runOnIdle { dark = true }
        check()
        rule.runOnIdle { dark = false }
        check()
    }

    @Test fun exportContainsRealExamplesCompositionAndKotlinApi() {
        assertEquals(
            listOf(
                "Installation",
                "Usage",
                "Composition",
                "Basic",
                "Small",
                "Media",
                "Small with Media",
                "Destructive",
                "RTL",
                "API Reference",
                "AlertDialog",
                "AlertDialogContent",
                "Header and Media",
                "Footer and Actions",
            ),
            docSections(alertDialogDoc).map { it.title },
        )
        assertEquals(
            listOf(
                "Default",
                "Basic",
                "Small",
                "Media",
                "Small with Media",
                "Destructive",
                "RTL",
            ),
            alertDialogDoc.examples.map {
                it.title
            },
        )
        assertEquals(
            mapOf(
                "Basic" to
                    "A basic alert dialog with a title, description, and cancel and continue buttons.",
                "Small with Media" to
                    (
                        "Use `size = DialogSize.Sm` to make the alert dialog smaller and " +
                            "`AlertDialogMedia` to add a media element such as an icon or image."
                    ),
                "Destructive" to
                    (
                        "Use `AlertDialogAction` to add a destructive action button to the " +
                            "alert dialog. The Settings link is a current-page placeholder, " +
                            "as in the upstream example."
                    ),
                "RTL" to
                    (
                        "To enable RTL support in shadcn/ui, see the " +
                            "[RTL configuration guide](https://ui.shadcn.com/docs/rtl)."
                    ),
            ),
            alertDialogDoc.examples
                .filter { it.title in setOf("Basic", "Small with Media", "Destructive", "RTL") }
                .associate { it.title to it.descriptionMarkdown },
        )
        val text = componentPageMarkdown(alertDialogDoc)
        assertContains(text, "AlertDialogMedia (optional)")
        assertContains(text, "DialogSize.Sm")
        assertContains(text, "CircleFadingPlus")
        assertContains(text, "Trash2")
        assertContains(text, "### AlertDialogContent")
        assertContains(text, "LinkAnnotation.Url(\"#\"")
        assertFalse(text.contains("EmojiText"))
    }
}
