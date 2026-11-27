@file:OptIn(androidx.compose.foundation.style.ExperimentalFoundationStyleApi::class)

package com.toutiaoyan.compose.ui.catalog

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.toAwtImage
import androidx.compose.ui.platform.*
import androidx.compose.ui.test.*
import androidx.compose.ui.test.junit4.v2.createComposeRule
import androidx.compose.ui.unit.*
import com.toutiaoyan.compose.ui.catalog.docs.separatorDoc
import com.toutiaoyan.compose.ui.theme.Theme
import com.toutiaoyan.compose.ui.theme.currentTheme
import com.toutiaoyan.compose.ui.tokens.StylePreset
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import org.junit.runners.Parameterized
import java.io.File
import javax.imageio.ImageIO
import kotlin.test.*

@RunWith(Parameterized::class)
class SeparatorExamplesTest(private val viewport: Int, private val dark: Boolean) {
    @get:Rule val rule = createComposeRule()

    companion object {
        @JvmStatic
        @Parameterized.Parameters(name = "viewport={0},dark={1}")
        fun modes() =
            listOf(
                arrayOf<Any>(1440, false),
                arrayOf<Any>(1440, true),
                arrayOf<Any>(390, false),
                arrayOf<Any>(390, true),
            )
    }

    private fun advance() {
        rule.mainClock.advanceTimeBy(64)
        rule.waitForIdle()
    }

    private fun show(title: String) {
        rule.mainClock.autoAdvance = false
        rule.setContent {
            val original = LocalWindowInfo.current
            val density = LocalDensity.current
            val window =
                remember(original, density) {
                    object : WindowInfo by original {
                        override val containerSize =
                            with(density) { IntSize(viewport.dp.roundToPx(), 800.dp.roundToPx()) }
                    }
                }
            CompositionLocalProvider(LocalWindowInfo provides window) {
                Theme(preset = StylePreset.Nova, isDark = dark, minTouchTarget = null) {
                    Box(
                        Modifier.width((if (viewport >= 640) 640 else viewport).dp).height(640.dp)
                            .background(currentTheme.colors.background).padding(24.dp),
                        contentAlignment =
                            Alignment.Center,
                    ) {
                        separatorDoc.examples.single { it.title == title }.RenderPreview()
                    }
                }
            }
        }
        advance()
    }

    private fun node(tag: String) = rule.onNodeWithTag(tag, useUnmergedTree = true)

    private fun capture(name: String) {
        val file = File("build/reports/separator-recipes/$name-$viewport-${if (dark)"dark" else "light"}.png")
        file.parentFile.mkdirs()
        ImageIO.write(node("separator-example").captureToImage().toAwtImage(), "png", file)
    }

    @Test fun defaultHasExactSourceTextAndOnePixelDivider() {
        show("Default")
        rule.onNodeWithText("shadcn/ui").assertIsDisplayed()
        rule.onNodeWithText("The Foundation for your Design System").assertIsDisplayed()
        rule.onNodeWithText(
            (
                "A set of beautifully designed components that you can " +
                    "customize, extend, and build on."
            ),
        ).assertIsDisplayed()
        node("separator-line").assertHeightIsEqualTo(1.dp)
        node("separator-example").assertWidthIsEqualTo((if (viewport >= 640)384 else 342).dp)
        capture("default")
    }

    @Test fun verticalUsesTwentyPixelRowAndOnePixelLines() {
        show("Vertical")
        listOf("Blog", "Docs", "Source").forEach { rule.onNodeWithText(it).assertIsDisplayed() }
        node("separator-first").assertWidthIsEqualTo(1.dp).assertHeightIsEqualTo(20.dp)
        node("separator-second").assertWidthIsEqualTo(1.dp).assertHeightIsEqualTo(20.dp)
        capture("vertical")
    }

    @Test fun menuStretchesDividersToContentAndRespondsAtMd() {
        show("Menu")
        listOf(
            "Settings",
            "Manage preferences",
            "Account",
            "Profile & security",
        ).forEach { rule.onNodeWithText(it).assertIsDisplayed() }
        node("separator-first").assertHeightIsEqualTo(40.dp).assertWidthIsEqualTo(1.dp)
        if (viewport >= 768) {
            rule.onNodeWithText("Help").assertIsDisplayed()
            rule.onNodeWithText("Support & docs").assertIsDisplayed()
            node("separator-second").assertHeightIsEqualTo(40.dp)
        } else {
            rule.onNodeWithText("Help").assertDoesNotExist()
            node("separator-second").assertDoesNotExist()
        }
        capture("menu")
    }

    @Test fun listHasThreeDefinitionPairsTwoDividersAndEightPixelGaps() {
        show("List")
        (1..3).forEach {
            rule.onNodeWithText("Item $it").assertIsDisplayed()
            rule.onNodeWithText("Value $it").assertIsDisplayed()
        }
        node("separator-list-1").assertHeightIsEqualTo(1.dp)
        node("separator-list-2").assertHeightIsEqualTo(1.dp)
        node("separator-example").assertHeightIsEqualTo(94.dp)
        capture("list")
    }

    @Test fun rtlUsesAllThreeExactTranslationsAndMirrorsTextAlignment() {
        show("RTL")
        rule.onNodeWithText("الأساس لنظام التصميم الخاص بك").assertIsDisplayed()
        capture("rtl-arabic")
        rule.onNodeWithText("Arabic (العربية)").performClick()
        advance()
        rule.onNodeWithText("English").performClick()
        advance()
        rule.onNodeWithText("The Foundation for your Design System").assertIsDisplayed()
        capture("rtl-english")
        rule.onNodeWithText("English").performClick()
        advance()
        rule.onNodeWithText("Hebrew (עברית)").performClick()
        advance()
        rule.onNodeWithText("הבסיס למערכת העיצוב שלך").assertIsDisplayed()
        capture("rtl-hebrew")
    }
}
