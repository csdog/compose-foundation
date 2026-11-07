package com.toutiaoyan.compose.ui.catalog

import androidx.compose.foundation.layout.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.platform.LocalWindowInfo
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.platform.WindowInfo
import androidx.compose.ui.unit.IntSize
import androidx.compose.ui.Modifier
import androidx.compose.ui.semantics.SemanticsActions
import androidx.compose.ui.text.TextLayoutResult
import androidx.compose.ui.test.*
import androidx.compose.ui.test.junit4.v2.createComposeRule
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.toutiaoyan.compose.ui.catalog.docs.PreviewCodeSection
import com.toutiaoyan.compose.ui.catalog.docs.cardDoc
import com.toutiaoyan.compose.ui.catalog.docs.componentPageMarkdown
import com.toutiaoyan.compose.ui.components.overlay.OverlayHost
import com.toutiaoyan.compose.ui.components.label.Label
import com.toutiaoyan.compose.ui.theme.Theme
import com.toutiaoyan.compose.ui.tokens.StylePreset
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import org.junit.runners.Parameterized
import kotlin.test.*

@RunWith(Parameterized::class)
class CardExamplesTest(private val width: Int, private val dark: Boolean) {
    @get:Rule val rule = createComposeRule()
    private var index by mutableIntStateOf(0)

    private fun content() {
        rule.setContent {
            val original = LocalWindowInfo.current
            val window =
                remember(original) {
                    object : WindowInfo by original {
                        override val containerSize = IntSize(width, 1000)
                    }
                }
            CompositionLocalProvider(LocalCatalogViewportWidth provides width.dp, LocalWindowInfo provides window) {
                Theme(
                    preset = StylePreset.Nova,
                    isDark = dark,
                    minTouchTarget = null,
                    fontFamily = catalogFontFamily(),
                ) {
                    OverlayHost {
                        Box(
                            Modifier.requiredSize((if (width < 640) 325 else 638).dp, 600.dp),
                            contentAlignment = Alignment.Center,
                        ) {
                            Box(
                                Modifier.fillMaxWidth().padding(if (index == 5) 0.dp else 40.dp),
                                contentAlignment = Alignment.Center,
                            ) {
                                key(index) { cardDoc.examples[index].preview() }
                            }
                        }
                    }
                }
            }
        }
    }

    private fun show(value: Int) {
        rule.runOnIdle { index = value }
    }

    @Test fun sixOfficialRecipesHaveTheirContentAndSectionStructure() {
        content()
        assertEquals(
            listOf("Default", "Size", "Spacing", "Edge to Edge", "Image", "RTL"),
            cardDoc.examples.map { it.title },
        )
        assertFalse(docSections(cardDoc).any { it.title == "Edge to Edge" })
        assertFalse(componentPageMarkdown(cardDoc).contains("## Edge to Edge"))
        assertFalse(componentPageMarkdown(cardDoc).contains("Kotlin extension: Edge to Edge"))
        assertContains(
            componentPageMarkdown(cardDoc),
            "remove the section gap on `CardContent` with the matching negative bottom offset.",
        )
        assertContains(
            componentPageMarkdown(cardDoc),
            "Places content in the top-right of the header (logical end in RTL), such as a button or badge.",
        )
        val apiThenChangelog =
            listOf("## API Reference", "## Changelog", "### Spacing Variable").map {
                componentPageMarkdown(cardDoc).indexOf(it)
            }
        assertTrue(apiThenChangelog.all { it >= 0 })
        assertEquals(apiThenChangelog.sorted(), apiThenChangelog)
        val sectionTitles = docSections(cardDoc).map { it.title }
        assertTrue(sectionTitles.indexOf("API Reference") < sectionTitles.indexOf("Changelog"))
        assertTrue(sectionTitles.indexOf("Changelog") < sectionTitles.indexOf("Spacing Variable"))
        assertTrue(sectionTitles.none { it.startsWith("Update ") })
        assertContains(componentPageMarkdown(cardDoc), "1. **Update the Card root spacing classes.**")
        assertContains(componentPageMarkdown(cardDoc), "2. **Update CardHeader spacing classes.**")
        assertContains(
            componentPageMarkdown(cardDoc),
            "3. **Update CardContent and CardFooter spacing classes.**",
        )
        assertContains(componentPageMarkdown(cardDoc), "### CardImage (Kotlin extension)")
        assertContains(componentPageMarkdown(cardDoc), "Kotlin-only image region")
        rule.onNodeWithText("Login to your account").assertIsDisplayed()
        rule.onNodeWithText("m@example.com").performTextInput("alice@example.com")
        rule.onNodeWithContentDescription("Password", useUnmergedTree = true).performTextInput("secret")
        rule.onNodeWithText("Forgot your password?").assertIsDisplayed()
        if (width == 390) {
            val password = rule.onNodeWithText("Password", useUnmergedTree = true).getUnclippedBoundsInRoot()
            val forgot =
                rule.onNodeWithText("Forgot your password?", useUnmergedTree = true).getUnclippedBoundsInRoot()
            assertEquals(2f, (forgot.left - password.right).value, 1f)
        }
        show(1)
        rule.onNodeWithText("Scheduled reports").assertIsDisplayed()
        val title = mutableListOf<TextLayoutResult>()
        rule.onNodeWithText(
            "Scheduled reports",
        ).performSemanticsAction(SemanticsActions.GetTextLayoutResult) { it(title) }
        assertEquals(14.sp, title.single().layoutInput.style.fontSize)
        rule.onNodeWithText("See what's new").assertIsDisplayed()
        show(3)
        rule.onNodeWithText("Terms of Service").assertIsDisplayed()
        rule.onNodeWithText("By continuing, you agree", substring = true).performScrollTo().assertIsDisplayed()
        rule.onNodeWithText("Accept").assertIsDisplayed()
        show(4)
        rule.onNodeWithText("Featured").assertIsDisplayed()
        val image = rule.onNodeWithContentDescription("Event cover").getUnclippedBoundsInRoot()
        val imageWidth = if (width < 640) 245.dp else 384.dp
        assertEquals(imageWidth, image.right - image.left)
        assertEquals((image.right - image.left).value / (image.bottom - image.top).value, 16f / 9f, .02f)
        rule.onNodeWithText("View Event").assertIsDisplayed()
    }

    @Test fun spacingChangesInsetAndKeepsTheFormAndNonEmptySelection() {
        index = 2
        content()
        rule.onNodeWithText("m@example.com").performTextInput("alice@example.com")
        val initialCard = rule.onNodeWithTag("login-card").fetchSemanticsNode().boundsInRoot
        val controls =
            listOf(16, 20, 24, 32).map {
                rule.onNodeWithTag("card-spacing-$it").fetchSemanticsNode().boundsInRoot
            }
        assertEquals(initialCard.left, controls.first().left)
        controls.zipWithNext().forEach { (first, next) -> assertEquals(8f, next.left - first.right) }
        for (spacing in listOf(32, 20, 24, 16)) {
            rule.onNodeWithText("${spacing}px").performClick().assertIsOn()
            rule.onNodeWithText("alice@example.com").assertIsDisplayed()
            val card = rule.onNodeWithTag("login-card").fetchSemanticsNode().boundsInRoot
            // Screen bounds match the actual clipped/placed region of Style-backed nodes.
            val field = rule.onNodeWithTag("login-fields-body").fetchSemanticsNode().boundsInRoot
            assertEquals(spacing.toFloat(), field.left - card.left)
            assertEquals(spacing.toFloat(), card.right - field.right)
            rule.onNodeWithText("${spacing}px").performClick().assertIsOn()
        }
    }

    @Test fun rtlSwitchesAllThreeTranslationsAndKeepsInputValues() {
        index = 5
        content()
        rule.onNodeWithText("تسجيل الدخول إلى حسابك").assertIsDisplayed()
        listOf(
            "تسجيل الدخول إلى حسابك" to 22,
            "أدخل بريدك الإلكتروني أدناه لتسجيل الدخول إلى حسابك" to 20,
            "البريد الإلكتروني" to 14,
            "نسيت كلمة المرور؟" to 20,
        ).forEach { (label, lineHeight) ->
            val layouts = mutableListOf<TextLayoutResult>()
            rule.onNodeWithText(label).performSemanticsAction(SemanticsActions.GetTextLayoutResult) { it(layouts) }
            if (lineHeight == 14) {
                // CSS line box, distinct from Skia's padded internal paragraph bounds.
                assertEquals(
                    14f * layouts.single().lineCount,
                    rule.onNodeWithTag("login-email-label").fetchSemanticsNode().boundsInRoot.height,
                    label,
                )
                assertEquals(14.sp, layouts.single().layoutInput.style.fontSize)
            } else {
                assertEquals(lineHeight * layouts.single().lineCount, layouts.single().size.height, label)
            }
        }
        val google = "تسجيل الدخول باستخدام Google"
        val googleLayout = mutableListOf<TextLayoutResult>()
        rule.onNodeWithText(google).performSemanticsAction(SemanticsActions.GetTextLayoutResult) { it(googleLayout) }
        assertEquals(google.length, googleLayout.single().getLineEnd(0, visibleEnd = true))
        rule.onNodeWithText("m@example.com").performTextInput("alice@example.com")
        rule.onNodeWithContentDescription("Preview language").performClick()
        rule.onNodeWithText("Hebrew (עברית)").performClick()
        rule.onNodeWithText("התחבר לחשבון שלך").assertIsDisplayed()
        rule.onNodeWithText("alice@example.com").assertIsDisplayed()
        rule.onNodeWithContentDescription("Preview language").performClick()
        rule.onNodeWithText("English").performClick()
        rule.onNodeWithText("Login to your account").assertIsDisplayed()
        rule.onNodeWithText("alice@example.com").assertIsDisplayed()
    }

    @Test fun rtlPreviewKeepsToolbarInsideItsFullHeight() {
        val example = cardDoc.examples.last()
        assertEquals(544.dp, example.previewHeight)
        rule.setContent {
            CompositionLocalProvider(LocalCatalogViewportWidth provides width.dp) {
                Theme(
                    preset = StylePreset.Nova,
                    isDark = dark,
                    minTouchTarget = null,
                    fontFamily = catalogFontFamily(),
                ) {
                    OverlayHost {
                        PreviewCodeSection(
                            example.code,
                            modifier = Modifier.width(if (width < 640) 327.dp else 640.dp).testTag("preview"),
                            previewHeight = example.previewHeight,
                            previewPadding = 0.dp,
                            preview = example.preview,
                        )
                    }
                }
            }
        }
        val frame = rule.onNodeWithTag("preview").fetchSemanticsNode().boundsInRoot
        val toolbar = rule.onNodeWithTag("rtl-preview-toolbar").fetchSemanticsNode().boundsInRoot
        assertTrue(toolbar.top >= frame.top)
        rule.onNodeWithTag("rtl-preview-toolbar").assertHeightIsEqualTo(63.dp)
        rule.onNodeWithContentDescription("Preview language").assertIsDisplayed()
    }

    @Test fun arabicLabelsKeepCssLineBoxesWhenTheyWrap() {
        rule.setContent {
            Theme(
                preset = StylePreset.Nova,
                isDark = dark,
                minTouchTarget = null,
                fontFamily = catalogArabicFontFamily(),
            ) {
                Label("البريد الإلكتروني\nالبريد الإلكتروني", Modifier.testTag("multiline-label"))
            }
        }
        rule.onNodeWithTag("multiline-label").assertHeightIsEqualTo(28.dp)
    }

    companion object {
        @JvmStatic
        @Parameterized.Parameters(name = "width={0} dark={1}")
        fun parameters() =
            listOf(
                arrayOf<Any>(390, false),
                arrayOf<Any>(390, true),
                arrayOf<Any>(1440, false),
                arrayOf<Any>(1440, true),
            )
    }
}
