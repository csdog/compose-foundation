package com.toutiaoyan.compose.ui.components.text

import androidx.compose.foundation.layout.Column
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.test.onNodeWithText
import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.text.TextStyle
import com.toutiaoyan.compose.ui.ScreenshotTest
import kotlin.test.Test

class TextBrushTest : ScreenshotTest() {
    @Test fun anExplicitTextStyleBrushPaintsBothOverloads() {
        val style = TextStyle(brush = SolidColor(Color.Magenta))
        setThemedContent {
            Column {
                Text(textStyle = style) { "Plain brush" }
                Text(textStyle = style) { AnnotatedString("Annotated brush") }
            }
        }
        composeRule.onNodeWithText("Plain brush").assertPaintedTextColor(Color.Magenta)
        composeRule.onNodeWithText("Annotated brush").assertPaintedTextColor(Color.Magenta)
    }
}
