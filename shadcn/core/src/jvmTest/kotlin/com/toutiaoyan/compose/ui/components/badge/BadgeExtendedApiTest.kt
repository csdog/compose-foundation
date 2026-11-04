package com.toutiaoyan.compose.ui.components.badge

import androidx.compose.foundation.background
import androidx.compose.ui.graphics.toPixelMap
import androidx.compose.ui.graphics.compositeOver
import androidx.compose.ui.input.InputMode
import androidx.compose.ui.input.InputModeManager
import androidx.compose.ui.platform.LocalInputModeManager
import com.toutiaoyan.compose.ui.tokens.BaseColor
import kotlin.math.abs
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.padding
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalLayoutDirection
import androidx.compose.ui.platform.LocalUriHandler
import androidx.compose.ui.platform.UriHandler
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.semantics.getOrNull
import androidx.compose.ui.semantics.SemanticsProperties
import androidx.compose.ui.test.*
import androidx.compose.ui.test.junit4.v2.createComposeRule
import androidx.compose.ui.unit.LayoutDirection
import androidx.compose.ui.unit.dp
import com.toutiaoyan.compose.ui.components.icon.Icon
import com.toutiaoyan.compose.ui.components.text.Text
import com.toutiaoyan.compose.ui.icons.Check
import com.toutiaoyan.compose.ui.theme.Theme
import com.toutiaoyan.compose.ui.tokens.StylePreset
import org.junit.Rule
import org.junit.Test
import kotlin.test.*

class BadgeExtendedApiTest {
    @get:Rule val rule = createComposeRule()

    @Test fun iconInsetsUseLogicalEdgesAndRetainTheTwentyDpHeight() {
        var placement by mutableStateOf<BadgeIconPlacement?>(null)
        var direction by mutableStateOf(LayoutDirection.Ltr)
        rule.setContent {
            CompositionLocalProvider(LocalLayoutDirection provides direction) {
                Theme(preset = StylePreset.Nova, minTouchTarget = null) {
                    Box(Modifier.padding(24.dp)) {
                        Badge(Modifier.testTag("badge"), iconPlacement = placement) {
                            if (placement == BadgeIconPlacement.Start || placement == BadgeIconPlacement.Both) {
                                Icon(Check, modifier = Modifier.testTag("start"))
                            }
                            Text(Modifier.testTag("label")) { "Label" }
                            if (placement == BadgeIconPlacement.End || placement == BadgeIconPlacement.Both) {
                                Icon(Check, modifier = Modifier.testTag("end"))
                            }
                        }
                    }
                }
            }
        }
        for (dir in listOf(LayoutDirection.Ltr, LayoutDirection.Rtl)) {
            for (edge in listOf(null, BadgeIconPlacement.Start, BadgeIconPlacement.End, BadgeIconPlacement.Both)) {
                rule.runOnIdle {
                    direction = dir
                    placement = edge
                }
                rule.onNodeWithTag("badge").assertHeightIsEqualTo(20.dp)
                val badge = rule.onNodeWithTag("badge").getUnclippedBoundsInRoot()
                val first =
                    rule.onNodeWithTag(
                        if (edge == BadgeIconPlacement.Start || edge == BadgeIconPlacement.Both) "start" else "label",
                    ).getUnclippedBoundsInRoot()
                val last =
                    rule.onNodeWithTag(
                        if (edge == BadgeIconPlacement.End || edge == BadgeIconPlacement.Both) "end" else "label",
                    ).getUnclippedBoundsInRoot()
                val expectedStart = if (edge == BadgeIconPlacement.Start || edge == BadgeIconPlacement.Both) 7f else 9f
                val expectedEnd = if (edge == BadgeIconPlacement.End || edge == BadgeIconPlacement.Both) 7f else 9f
                val startGap = if (dir == LayoutDirection.Ltr) first.left - badge.left else badge.right - first.right
                val endGap = if (dir == LayoutDirection.Ltr) badge.right - last.right else last.left - badge.left
                assertEquals(expectedStart, startGap.value, .1f, "$dir/$edge start padding includes border")
                assertEquals(expectedEnd, endGap.value, .1f, "$dir/$edge end padding includes border")
            }
        }
    }

    @Test fun hrefUsesTheUriHandlerAndAnExplicitCallbackOverridesIt() {
        val opened = mutableListOf<String>()
        var overrideNavigation by mutableStateOf(false)
        var clicks = 0
        val uri =
            object : UriHandler {
                override fun openUri(uri: String) {
                    opened += uri
                }
            }
        rule.setContent {
            CompositionLocalProvider(LocalUriHandler provides uri) {
                Theme(preset = StylePreset.Nova) {
                    Badge(
                        "Open",
                        href = "https://example.com/docs",
                        onClick = if (overrideNavigation) ({ clicks++ }) else null,
                    )
                }
            }
        }
        rule.onNodeWithText("Open").performClick()
        assertEquals(listOf("https://example.com/docs"), opened)
        rule.runOnIdle { overrideNavigation = true }
        rule.onNodeWithText("Open").performClick()
        assertEquals(1, clicks)
        assertEquals(1, opened.size)
        assertNull(
            rule.onNodeWithText("Open").fetchSemanticsNode().config.getOrNull(SemanticsProperties.Role),
            "A href must not be announced as a button",
        )
    }

    @Test fun invalidBorderUsesTheCurrentDestructiveColorInBothThemes() {
        lateinit var inputMode: InputModeManager
        var dark by mutableStateOf(false)
        rule.setContent {
            Theme(preset = StylePreset.Nova, isDark = dark) {
                val palette = if (dark) BaseColor.Neutral.dark else BaseColor.Neutral.light
                inputMode = LocalInputModeManager.current
                Box(Modifier.testTag("surface").background(palette.background).padding(12.dp)) {
                    Badge("Invalid", Modifier.testTag("invalid"), onClick = {}, invalid = true)
                }
            }
        }
        for (value in listOf(false, true)) {
            rule.runOnIdle { dark = value }
            val pixels = rule.onNodeWithTag("invalid").captureToImage().toPixelMap()
            val actual = pixels[pixels.width / 2, 0]
            val expected = (if (value) BaseColor.Neutral.dark else BaseColor.Neutral.light).destructive
            assertTrue(
                abs(expected.red - actual.red) < .03f &&
                    abs(expected.green - actual.green) < .03f &&
                    abs(expected.blue - actual.blue) < .03f,
                "Invalid border must use the active destructive token: expected=$expected actual=$actual",
            )
            rule.runOnIdle { inputMode.requestInputMode(InputMode.Keyboard) }
            rule.onNodeWithTag("invalid").requestFocus().assertIsFocused()
            val surface = rule.onNodeWithTag("surface").getUnclippedBoundsInRoot()
            val bounds = rule.onNodeWithTag("invalid").getUnclippedBoundsInRoot()
            val ring = rule.onNodeWithTag("surface").captureToImage().toPixelMap()
            val ringPixel =
                ring[
                    ((bounds.left + bounds.right) / 2 - surface.left).value.toInt(),
                    (bounds.top - surface.top).value.toInt() - 2,
                ]
            val palette = if (value) BaseColor.Neutral.dark else BaseColor.Neutral.light
            val expectedRing = expected.copy(alpha = if (value) .4f else .2f).compositeOver(palette.background)
            assertEquals(expectedRing.red, ringPixel.red, .03f)
            assertEquals(expectedRing.green, ringPixel.green, .03f)
            assertEquals(expectedRing.blue, ringPixel.blue, .03f)
        }
    }

    @Test fun invalidPublishesAnErrorAndCanBeCleared() {
        var invalid by mutableStateOf(true)
        rule.setContent {
            Theme(preset = StylePreset.Nova) { Badge("Invalid", invalid = invalid) }
        }
        rule.onNodeWithText("Invalid").assert(SemanticsMatcher.expectValue(SemanticsProperties.Error, "Invalid badge"))
        rule.runOnIdle { invalid = false }
        rule.onNodeWithText("Invalid").assert(SemanticsMatcher.keyNotDefined(SemanticsProperties.Error))
    }
}
