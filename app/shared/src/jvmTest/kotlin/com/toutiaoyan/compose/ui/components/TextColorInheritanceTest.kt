package com.toutiaoyan.compose.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.padding
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.toPixelMap
import androidx.compose.ui.test.captureToImage
import androidx.compose.ui.test.junit4.v2.createComposeRule
import androidx.compose.ui.test.onNodeWithTag
import androidx.compose.ui.unit.dp
import com.toutiaoyan.compose.ui.theme.Theme
import com.toutiaoyan.compose.ui.theme.currentTheme
import com.toutiaoyan.compose.ui.tokens.BaseColor
import org.junit.Rule
import kotlin.math.sqrt
import kotlin.test.Test
import kotlin.test.assertTrue

class TextColorInheritanceTest {
    @get:Rule
    val composeRule = createComposeRule()

    @Test
    fun taggedTexts_paintExpectedColors() {
        val colors = BaseColor.Neutral.light
        composeRule.setContent {
            Theme(baseColor = BaseColor.Neutral, isDark = false) {
                Box(
                    Modifier
                        .background(currentTheme.colors.background)
                        .padding(16.dp),
                ) {
                    TextColorInheritanceContent()
                }
            }
        }
        composeRule.waitForIdle()

        val background = colors.background
        assertTaggedColor(TextColorTags.TextStyle, expected = TextStyleColor, rival = OwnStyleColor, background)
        assertTaggedColor(TextColorTags.OwnStyle, expected = OwnStyleColor, rival = AncestorStyleColor, background)
        assertTaggedColor(
            TextColorTags.Ancestor,
            expected = AncestorStyleColor,
            rival = LocalContentColorValue,
            background
        )
        assertTaggedColor(
            TextColorTags.AncestorBeatsLocal,
            expected = AncestorStyleColor,
            rival = LocalContentColorValue,
            background,
        )
        assertTaggedColor(
            TextColorTags.LocalContent,
            expected = LocalContentColorValue,
            rival = colors.foreground,
            background,
        )
        assertTaggedColor(
            TextColorTags.Fallback,
            expected = colors.foreground,
            rival = LocalContentColorValue,
            background
        )
        assertTaggedColor(
            TextColorTags.CardBody,
            expected = colors.cardForeground,
            rival = colors.mutedForeground,
            background,
        )
        assertTaggedColor(
            TextColorTags.CardMuted,
            expected = colors.mutedForeground,
            rival = colors.cardForeground,
            background,
        )
        assertTaggedColor(
            TextColorTags.CardTextStyle,
            expected = TextStyleColor,
            rival = colors.cardForeground,
            background
        )
        assertTaggedColor(
            TextColorTags.CardOwnStyle,
            expected = OwnStyleColor,
            rival = colors.cardForeground,
            background
        )
        assertTaggedColor(
            TextColorTags.ButtonBody,
            expected = colors.primaryForeground,
            rival = TextStyleColor,
            colors.primary,
        )
        assertTaggedColor(
            TextColorTags.ButtonTextStyle,
            expected = TextStyleColor,
            rival = colors.primaryForeground,
            colors.primary,
        )

        assertTaggedColor(
            TextColorTags.SelectionTextStyle,
            expected = TextStyleColor,
            rival = OwnStyleColor,
            background,
        )
        assertTaggedColor(
            TextColorTags.SelectionOwnStyle,
            expected = colors.foreground,
            rival = OwnStyleColor,
            background,
        )
        assertTaggedColor(
            TextColorTags.SelectionAncestor,
            expected = colors.foreground,
            rival = AncestorStyleColor,
            background,
        )
        assertTaggedColor(
            TextColorTags.SelectionLocalBeatsAncestor,
            expected = LocalContentColorValue,
            rival = AncestorStyleColor,
            background,
        )
        assertTaggedColor(
            TextColorTags.SelectionLocal,
            expected = LocalContentColorValue,
            rival = colors.foreground,
            background,
        )
        assertTaggedColor(
            TextColorTags.SelectionCardBody,
            expected = colors.cardForeground,
            rival = OwnStyleColor,
            background,
        )
        assertTaggedColor(
            TextColorTags.SelectionCardMuted,
            expected = colors.mutedForeground,
            rival = colors.cardForeground,
            background,
        )
        assertTaggedColor(
            TextColorTags.SelectionCardOwnStyle,
            expected = colors.cardForeground,
            rival = OwnStyleColor,
            background,
        )
    }

    private fun assertTaggedColor(
        tag: String,
        expected: Color,
        rival: Color,
        background: Color,
    ) {
        val map =
            composeRule
                .onNodeWithTag(tag, useUnmergedTree = true)
                .captureToImage()
                .toPixelMap()
        var nearExpected = 0
        var nearRival = 0
        for (x in 0 until map.width) {
            for (y in 0 until map.height) {
                val pixel = map[x, y]
                if (pixel.alpha < 0.85f) continue
                if (colorDistance(pixel, background) < 0.2f) continue
                if (colorDistance(pixel, expected) < 0.25f) nearExpected++
                if (colorDistance(pixel, rival) < 0.25f) nearRival++
            }
        }
        assertTrue(
            nearExpected > 0,
            "$tag: no glyph pixels near expected $expected (${map.width}x${map.height})",
        )
        assertTrue(
            nearExpected > nearRival,
            "$tag: more pixels near rival $rival ($nearRival) than expected $expected ($nearExpected)",
        )
    }
}

private fun colorDistance(
    a: Color,
    b: Color,
): Float {
    val dr = a.red - b.red
    val dg = a.green - b.green
    val db = a.blue - b.blue
    return sqrt(dr * dr + dg * dg + db * db)
}
