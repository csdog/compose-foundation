package com.toutiaoyan.compose.ui.catalog

import androidx.compose.foundation.layout.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.toAwtImage
import androidx.compose.ui.platform.*
import androidx.compose.ui.test.*
import androidx.compose.ui.test.junit4.v2.createComposeRule
import androidx.compose.ui.unit.*
import com.toutiaoyan.compose.ui.catalog.docs.PreviewCodeSection
import com.toutiaoyan.compose.ui.catalog.docs.accordionDoc
import com.toutiaoyan.compose.ui.theme.Theme
import com.toutiaoyan.compose.ui.tokens.StylePreset
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import org.junit.runners.Parameterized
import java.io.File
import javax.imageio.ImageIO
import kotlin.test.*

@RunWith(Parameterized::class)
class AccordionRtlPreviewTest(private val viewport: Int, private val dark: Boolean) {
    @get:Rule val rule = createComposeRule()

    companion object {
        @JvmStatic
        @Parameterized.Parameters(name = "viewport={0},dark={1}")
        fun modes() = listOf(1440, 390).flatMap { width -> listOf(false, true).map { arrayOf<Any>(width, it) } }
    }

    @Test fun languageToolbarIsOutsideFixedPreviewAndSwitchesTheOpenAnswer() {
        val example = accordionDoc.examples.single { it.title == "RTL" }
        rule.setContent {
            val original = LocalWindowInfo.current
            val window =
                remember(original) {
                    object : WindowInfo by original {
                        override val containerSize = IntSize(viewport, 1000)
                    }
                }
            CompositionLocalProvider(LocalWindowInfo provides window) {
                Theme(preset = StylePreset.Nova, isDark = dark, minTouchTarget = null) {
                    PreviewCodeSection(
                        example.code,
                        modifier = Modifier.width(if (viewport == 390) 327.dp else 640.dp).testTag("frame"),
                        minPreviewHeight = example.minPreviewHeight,
                        previewPadding = example.previewPadding,
                        preview = example.preview,
                    )
                }
            }
        }
        val toolbar = rule.onNodeWithTag("rtl-preview-toolbar").fetchSemanticsNode().boundsInRoot
        val frame = rule.onNodeWithTag("frame").fetchSemanticsNode().boundsInRoot
        assertEquals(463f, frame.height, .5f)
        assertEquals(frame.top + 1, toolbar.top, .5f)
        assertEquals(63f, toolbar.height, .5f)
        val first = rule.onNodeWithText("كيف يمكنني إعادة تعيين كلمة المرور؟").fetchSemanticsNode().boundsInRoot
        assertEquals(if (viewport == 390) 327.dp else 640.dp, frame.width.dp)
        assertEquals(toolbar.bottom + 41, first.top, .5f)
        assertEquals(42f, first.height, .5f, "Official single-line trigger retains a20px line box")
        assertEquals(if (viewport == 390) 245f else 448f, first.width, .5f)
        rule.onNodeWithContentDescription("Preview language").assertHeightIsEqualTo(28.dp).assertWidthIsEqualTo(144.dp)

        fun capture(language: String) {
            val file =
                File("build/reports/accordion-rtl-r9", "$language-$viewport-${if (dark) "dark" else "light"}.png")
            file.parentFile.mkdirs()
            ImageIO.write(rule.onNodeWithTag("frame").captureToImage().toAwtImage(), "png", file)
        }
        rule.onNodeWithText("ما هي طرق الدفع التي تقبلونها؟").assertIsDisplayed()
        val third = rule.onNodeWithText("ما هي طرق الدفع التي تقبلونها؟").fetchSemanticsNode().boundsInRoot
        assertTrue(third.height >= 42f, "Fixed preview must retain the natural height of every trigger")
        capture("ar")
        rule.onNodeWithContentDescription("Preview language").performClick()
        rule.onNodeWithText("English").performClick()
        rule.waitForIdle()
        rule.onNodeWithText("How do I reset my password?").assertIsDisplayed()
        rule.onNodeWithText(
            "Click on 'Forgot Password' on the login page, enter your email address, " +
                "and we'll send you a link to reset your password.",
        ).assertIsDisplayed()
        capture("en")
        rule.onNodeWithContentDescription("Preview language").performClick()
        rule.onNodeWithText("Hebrew (עברית)").performClick()
        rule.waitForIdle()
        rule.onNodeWithText("איך אני מאפס את הסיסמה שלי?").assertIsDisplayed()
        rule.onNodeWithText(
            "לחץ על 'שכחתי סיסמה' בעמוד ההתחברות, הזן את כתובת האימייל שלך, " +
                "ונשלח לך קישור לאיפוס הסיסמה. הקישור יפוג תוך 24 שעות.",
        ).assertIsDisplayed()
        capture("he")
        assertEquals(frame.height, rule.onNodeWithTag("frame").fetchSemanticsNode().boundsInRoot.height, .5f)
        rule.onNodeWithContentDescription("Translation information").performClick()
        rule.onNodeWithText("I used AI to translate", substring = true).assertIsDisplayed()
        assertFalse(example.code.contains("SelectTrigger"), "Catalog controls must not be copied as component code")
    }
}
