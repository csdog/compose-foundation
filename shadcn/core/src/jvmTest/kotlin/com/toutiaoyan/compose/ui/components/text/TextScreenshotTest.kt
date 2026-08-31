@file:OptIn(androidx.compose.foundation.style.ExperimentalFoundationStyleApi::class)

package com.toutiaoyan.compose.ui.components.text

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.style.MutableStyleState
import androidx.compose.foundation.style.Style
import androidx.compose.foundation.style.styleable
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.unit.dp
import com.toutiaoyan.compose.ui.ScreenshotTest
import com.toutiaoyan.compose.ui.components.button.Button
import com.toutiaoyan.compose.ui.components.card.Card
import com.toutiaoyan.compose.ui.theme.LocalContentColor
import kotlin.test.Test

/** Distinct colors so the painted winner is obvious in the golden. */
private val TextStyleColor = Color(0xFFE11D48) // rose
private val OwnStyleColor = Color(0xFF0891B2) // cyan
private val AncestorStyleColor = Color(0xFFD97706) // amber
private val LocalContentColorValue = Color(0xFF2563EB) // blue

class TextScreenshotTest : ScreenshotTest() {
    private fun allStyles(darkTheme: Boolean) {
        snapshot("text_styles", darkTheme = darkTheme) {
            Column(verticalArrangement = Arrangement.spacedBy(4.dp)) {
                TextVariant.entries.forEach { typography ->
                    Text(variant = typography) { typography.name }
                }
                Text(textStyle = TextColors.Muted) { "Muted body text" }
            }
        }
    }

    /**
     * Color precedence, highest first:
     * 1. [textStyle] color
     * 2. this Text's `style { contentColor }`
     * 3. ancestor Style `contentColor` (Card / Button / [WithAncestorContentColor])
     * 4. [LocalContentColor]
     * 5. `theme.colors.foreground`
     */
    private fun colorInheritance(darkTheme: Boolean) {
        snapshot("text_color_inheritance", darkTheme = darkTheme) {
            Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                WithAncestorContentColor(AncestorStyleColor) {
                    Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                        Text(
                            textStyle = TextStyle(color = TextStyleColor),
                            style = Style { contentColor(OwnStyleColor) },
                        ) { "1 textStyle beats own style" }
                        Text(style = Style { contentColor(OwnStyleColor) }) {
                            "2 own style beats ancestor"
                        }
                        Text { "3 ancestor Style contentColor" }
                        CompositionLocalProvider(LocalContentColor provides LocalContentColorValue) {
                            Text { "3 ancestor Style beats LocalContentColor" }
                        }
                    }
                }
                CompositionLocalProvider(LocalContentColor provides LocalContentColorValue) {
                    Text { "4 LocalContentColor (no ancestor Style)" }
                }
                Text { "5 fallback = theme.foreground" }
                Card {
                    Text { "Card body = ancestor Style (cardForeground)" }
                    Text(textStyle = TextColors.Muted) { "Muted textStyle beats Card" }
                    Text(textStyle = TextStyle(color = TextStyleColor)) {
                        "textStyle beats Card"
                    }
                    Text(style = Style { contentColor(OwnStyleColor) }) {
                        "own style beats Card"
                    }
                }
                Button {
                    Text { "Button = ancestor Style (primaryForeground)" }
                }
                Button {
                    Text(textStyle = TextStyle(color = TextStyleColor)) {
                        "textStyle beats Button"
                    }
                }
            }
        }
    }

    @Test
    fun styles_light() = allStyles(darkTheme = false)

    @Test
    fun styles_dark() = allStyles(darkTheme = true)

    @Test
    fun colorInheritance_light() = colorInheritance(darkTheme = false)

    @Test
    fun colorInheritance_dark() = colorInheritance(darkTheme = true)
}

@Composable
private fun WithAncestorContentColor(
    color: Color,
    content: @Composable () -> Unit,
) {
    val styleState = remember { MutableStyleState(interactionSource = null) }
    Box(Modifier.styleable(styleState, Style { contentColor(color) })) {
        content()
    }
}
