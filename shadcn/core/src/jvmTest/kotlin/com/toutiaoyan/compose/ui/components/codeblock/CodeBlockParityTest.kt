@file:OptIn(androidx.compose.ui.ExperimentalComposeUiApi::class)

package com.toutiaoyan.compose.ui.components.codeblock

import androidx.compose.foundation.layout.*
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.ui.platform.Clipboard
import androidx.compose.ui.platform.ClipEntry
import androidx.compose.ui.platform.LocalClipboard
import androidx.compose.ui.platform.LocalLayoutDirection
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.test.*
import androidx.compose.ui.unit.LayoutDirection
import androidx.compose.ui.unit.dp
import androidx.compose.ui.input.key.Key
import com.toutiaoyan.compose.ui.ScreenshotTest
import com.toutiaoyan.compose.ui.components.text.Text
import com.toutiaoyan.compose.ui.theme.Theme
import com.toutiaoyan.compose.ui.tokens.StylePreset
import kotlin.test.*

class CodeBlockParityTest : ScreenshotTest() {
    @Composable private fun sample() {
        Column(Modifier.width(448.dp), verticalArrangement = Arrangement.spacedBy(12.dp)) {
            CodeBlock {
                CodeBlockHeader {
                    CodeBlockTitle("terminal")
                    CodeBlockActions { CodeBlockCopyButton("npm install @base-ui/react") }
                }
                CodeBlockContent("npm install @base-ui/react\n\nnpm run dev", lineNumbers = true)
                CodeBlockFooter { Text { "npm" } }
            }
            CodeBlock {
                CodeBlockContent(lineNumbers = true) {
                    CodeBlockLine("val count = 1", variant = CodeBlockLineVariant.Removed, lineNumber = 1)
                    CodeBlockLine("val count = 2", variant = CodeBlockLineVariant.Added, lineNumber = 2)
                    CodeBlockLine("println(count)", variant = CodeBlockLineVariant.Highlighted, lineNumber = 3)
                }
            }
            CodeBlock(variant = CodeBlockVariant.Destructive, size = CodeBlockSize.Sm) {
                CodeBlockContent("Error: Unable to resolve dependency")
            }
        }
    }

    @Test fun lightMatrix() = snapshot("code_block_parity", stylePreset = StylePreset.Nova) { sample() }

    @Test fun darkMatrix() =
        snapshot(
            "code_block_parity",
            darkTheme = true,
            stylePreset = StylePreset.Nova,
        ) { sample() }

    @Test fun contentFocusRing() =
        snapshotFocused("code_block_focus", "code", stylePreset = StylePreset.Nova) {
            Box(Modifier.width(320.dp)) { CodeBlock { CodeBlockContent("npm install", Modifier.testTag("code")) } }
        }

    @Test fun contentFocusRingDark() =
        snapshotFocused("code_block_focus", "code", darkTheme = true, stylePreset = StylePreset.Nova) {
            Box(Modifier.width(320.dp)) { CodeBlock { CodeBlockContent("npm install", Modifier.testTag("code")) } }
        }

    @Test fun numberingStartsAtRequestedLineAndPreservesBlankLines() {
        composeRule.setContent {
            Theme(preset = StylePreset.Nova) {
                CodeBlock { CodeBlockContent("first\n\nlast\n", lineNumbers = true, startLine = 10) }
            }
        }
        listOf("10", "11", "12", "first", "last").forEach { composeRule.onNodeWithText(it).assertExists() }
        composeRule.onNodeWithText("13").assertDoesNotExist()
        val first = composeRule.onNodeWithText("first").fetchSemanticsNode().boundsInRoot
        val last = composeRule.onNodeWithText("last").fetchSemanticsNode().boundsInRoot
        assertTrue(last.top - first.top >= 44f)
    }

    @Test fun wrappedCodeStaysWithinNarrowViewport() {
        composeRule.setContent {
            Theme(preset = StylePreset.Nova) {
                CodeBlock(Modifier.width(180.dp).testTag("block")) {
                    CodeBlockContent(
                        "a long line of code that must wrap within this narrow block",
                        wrap = true,
                        modifier = Modifier.testTag("code"),
                    )
                }
            }
        }
        val block = composeRule.onNodeWithTag("block").fetchSemanticsNode().boundsInRoot
        val code = composeRule.onNodeWithTag("code").fetchSemanticsNode().boundsInRoot
        assertTrue(code.width <= block.width)
        assertTrue(code.height > 23f)
    }

    @Test fun cappedOutputScrollsVerticallyWithKeyboard() {
        val code = (1..40).joinToString("\n") { "line $it" }
        composeRule.setContent {
            Theme(preset = StylePreset.Nova) {
                CodeBlock(Modifier.width(260.dp)) {
                    CodeBlockContent(
                        code = code,
                        lineNumbers = true,
                        maxHeight = 96.dp,
                        modifier = Modifier.testTag("capped-code"),
                    )
                }
            }
        }
        composeRule.onNodeWithTag("capped-code").assertHeightIsEqualTo(96.dp)
        composeRule.onNodeWithText("line 40").assertIsNotDisplayed()
        composeRule.onNodeWithTag("capped-code").requestFocus().performKeyInput { pressKey(Key.MoveEnd) }
        composeRule.onNodeWithText("line 40").assertIsDisplayed()
        composeRule.onNodeWithTag("capped-code").performKeyInput { pressKey(Key.MoveHome) }
        composeRule.onNodeWithText("line 1").assertIsDisplayed()
    }

    @Test fun nonScrollableCapClipsInsteadOfScrolling() {
        val code = (1..40).joinToString("\n") { "line $it" }
        composeRule.setContent {
            Theme(preset = StylePreset.Nova) {
                CodeBlock(Modifier.width(260.dp)) {
                    CodeBlockContent(
                        code = code,
                        lineNumbers = true,
                        maxHeight = 96.dp,
                        scrollable = false,
                        modifier = Modifier.testTag("clipped-code"),
                    )
                }
            }
        }
        composeRule.onNodeWithTag("clipped-code").assertHeightIsEqualTo(96.dp)
        composeRule.onNodeWithText("line 40").assertIsNotDisplayed()
        composeRule.onNodeWithTag("clipped-code").requestFocus().performKeyInput { pressKey(Key.MoveEnd) }
        // overflow-y-hidden: the content stays clipped, the last line never scrolls into view.
        composeRule.onNodeWithText("line 40").assertIsNotDisplayed()
        composeRule.onNodeWithText("line 1").assertIsDisplayed()
    }

    @Test fun rtlPageKeepsCodeContentLeftToRight() {
        composeRule.setContent {
            Theme(preset = StylePreset.Nova) {
                CompositionLocalProvider(LocalLayoutDirection provides LayoutDirection.Rtl) {
                    CodeBlock(Modifier.width(260.dp)) {
                        CodeBlockContent("const answer = 42", lineNumbers = true)
                    }
                }
            }
        }
        val number = composeRule.onNodeWithText("1").fetchSemanticsNode().boundsInRoot
        val code = composeRule.onNodeWithText("const answer = 42").fetchSemanticsNode().boundsInRoot
        assertTrue(number.left < code.left, "number ${number.left} should stay left of ${code.left}")
    }

    @Test fun copyWritesOriginalWhitespaceAndConfirmsForTwoSeconds() {
        var written: ClipEntry? = null
        val clipboard =
            object : Clipboard {
                override suspend fun getClipEntry() = written

                override suspend fun setClipEntry(clipEntry: ClipEntry?) {
                    written = clipEntry
                }
            }
        composeRule.mainClock.autoAdvance = false
        composeRule.setContent {
            Theme(preset = StylePreset.Nova) {
                CompositionLocalProvider(LocalClipboard provides clipboard) { CodeBlockCopyButton(" first\n\nlast\n") }
            }
        }
        composeRule.onNodeWithContentDescription("Copy").performClick()
        composeRule.mainClock.advanceTimeByFrame()
        composeRule.onNodeWithContentDescription("Copied").assertExists()
        assertEquals(
            " first\n\nlast\n",
            (written!!.nativeClipEntry as java.awt.datatransfer.Transferable).getTransferData(
                java.awt.datatransfer.DataFlavor.stringFlavor,
            ),
        )
        composeRule.mainClock.advanceTimeBy(2100)
        composeRule.onNodeWithContentDescription("Copy").assertExists()
    }

    @Test fun rejectedClipboardWriteDoesNotShowSuccess() {
        val clipboard =
            object : Clipboard {
                override suspend fun getClipEntry(): ClipEntry? = null

                override suspend fun setClipEntry(clipEntry: ClipEntry?) {
                    throw IllegalStateException("Permission denied")
                }
            }
        composeRule.setContent {
            Theme(preset = StylePreset.Nova) {
                CompositionLocalProvider(LocalClipboard provides clipboard) { CodeBlockCopyButton("code") }
            }
        }
        composeRule.onNodeWithContentDescription("Copy").performClick()
        composeRule.onNodeWithContentDescription("Copied").assertDoesNotExist()
    }
}
