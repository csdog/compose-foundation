package com.toutiaoyan.compose.ui.components.emojitext

import androidx.compose.foundation.layout.Column
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.semantics.SemanticsActions
import androidx.compose.ui.test.onNodeWithTag
import androidx.compose.ui.test.performSemanticsAction
import androidx.compose.ui.text.TextLayoutResult
import com.toutiaoyan.compose.ui.ScreenshotTest
import com.toutiaoyan.compose.ui.theme.LocalContentColor
import kotlin.test.Test
import kotlin.test.assertEquals

class EmojiTextTest : ScreenshotTest() {
    @Test fun unsupportedEmojiSequencesStayPlain() {
        val text = "👍🏽👨‍👨‍👧‍👦❤︎ ❤️"
        setThemedContent { EmojiText(text, Modifier.testTag("emoji")) }
        val result = layout("emoji")
        assertEquals(text, result.layoutInput.text.text)
        assertEquals(1, result.placeholderRects.size)
    }

    @Test fun curatedEmojiKeepsTheSameAmbientTextColorAsPlainText() {
        setThemedContent {
            CompositionLocalProvider(LocalContentColor provides Color.Red) {
                Column {
                    EmojiText("Plain", Modifier.testTag("plain"))
                    EmojiText("Emoji 👍", Modifier.testTag("emoji"))
                }
            }
        }
        assertEquals(Color.Red, layout("plain").layoutInput.style.color)
        assertEquals(Color.Red, layout("emoji").layoutInput.style.color)
    }

    private fun layout(tag: String): TextLayoutResult {
        val layouts = mutableListOf<TextLayoutResult>()
        composeRule.onNodeWithTag(tag).performSemanticsAction(SemanticsActions.GetTextLayoutResult) { it(layouts) }
        return layouts.single()
    }
}
