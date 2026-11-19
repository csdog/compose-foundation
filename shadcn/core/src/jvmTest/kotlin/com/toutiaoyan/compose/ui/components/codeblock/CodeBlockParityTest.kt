@file:OptIn(androidx.compose.ui.ExperimentalComposeUiApi::class)

package com.toutiaoyan.compose.ui.components.codeblock

import androidx.compose.foundation.layout.*
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.ui.platform.Clipboard
import androidx.compose.ui.platform.ClipEntry
import androidx.compose.ui.platform.LocalClipboard
import androidx.compose.ui.platform.LocalLayoutDirection
import androidx.compose.runtime.getValue
import androidx.compose.runtime.setValue
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.compositeOver
import com.toutiaoyan.compose.ui.theme.currentTheme
import androidx.compose.ui.graphics.toPixelMap
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.semantics.SemanticsProperties
import androidx.compose.ui.test.*
import androidx.compose.ui.unit.LayoutDirection
import androidx.compose.ui.unit.dp
import androidx.compose.ui.input.key.Key
import androidx.compose.ui.input.key.KeyEventType
import androidx.compose.ui.input.key.key
import androidx.compose.ui.input.key.onKeyEvent
import androidx.compose.ui.input.key.type
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

    @Test fun numberGutterGrowsAcrossDigitBoundaryAndShrinksWithReplacement() {
        var start by androidx.compose.runtime.mutableStateOf(999)
        var output by androidx.compose.runtime.mutableStateOf("first\nlast")
        composeRule.setContent {
            Theme(preset = StylePreset.Nova) {
                CodeBlock(Modifier.width(320.dp)) {
                    CodeBlockContent(output, lineNumbers = true, startLine = start)
                }
            }
        }

        fun codeLeft() = composeRule.onNodeWithText("first").fetchSemanticsNode().boundsInRoot.left

        fun assertSingleRow(label: String) {
            val number = composeRule.onNodeWithText(label).fetchSemanticsNode().boundsInRoot
            val first = composeRule.onNodeWithText("first").fetchSemanticsNode().boundsInRoot
            assertTrue(number.height <= first.height + 1f, "Number $label must remain on one line")
        }
        assertSingleRow("1000")
        val wide = codeLeft()
        assertEquals(wide, composeRule.onNodeWithText("last").fetchSemanticsNode().boundsInRoot.left)
        composeRule.runOnIdle { start = 1000000 }
        assertSingleRow("1000001")
        val wider = codeLeft()
        assertTrue(wider > wide)
        composeRule.runOnIdle {
            start = 1
            output = "first"
        }
        assertSingleRow("1")
        assertTrue(codeLeft() < wide, "The gutter must shrink when output is replaced")
    }

    @Test fun richLinesShareTheWidestNumberGutter() {
        composeRule.setContent {
            Theme(preset = StylePreset.Nova) {
                CodeBlock(Modifier.width(320.dp)) {
                    CodeBlockContent(lineNumbers = true, wrap = true) {
                        CodeBlockLine("first", lineNumber = 1)
                        CodeBlockLine("last", lineNumber = 1000000)
                    }
                }
            }
        }
        val first = composeRule.onNodeWithText("first").fetchSemanticsNode().boundsInRoot
        val last = composeRule.onNodeWithText("last").fetchSemanticsNode().boundsInRoot
        val number = composeRule.onNodeWithText("1000000").fetchSemanticsNode().boundsInRoot
        assertEquals(first.left, last.left)
        assertTrue(number.height <= first.height + 1f)
        assertTrue(number.right < last.left)
    }

    @Test fun rtlCodeKeepsTheSamePhysicalHorizontalFadeAsLtr() {
        composeRule.setContent {
            Theme(preset = StylePreset.Nova) {
                Column {
                    listOf(LayoutDirection.Ltr, LayoutDirection.Rtl).forEach { direction ->
                        CompositionLocalProvider(LocalLayoutDirection provides direction) {
                            CodeBlock(Modifier.width(240.dp)) {
                                CodeBlockContent(modifier = Modifier.testTag(direction.name)) {
                                    CodeBlockLine("long output ".repeat(30), variant = CodeBlockLineVariant.Highlighted)
                                }
                            }
                        }
                    }
                }
            }
        }
        val ltr = composeRule.onNodeWithTag("Ltr").captureToImage().toPixelMap()
        val rtl = composeRule.onNodeWithTag("Rtl").captureToImage().toPixelMap()
        // Sample the line background away from glyphs: only the overflow mask can differ.
        listOf(2, ltr.width - 3).forEach { x ->
            assertEquals(ltr[x, ltr.height - 2], rtl[x, rtl.height - 2], "Physical code fade at x=$x")
        }
    }

    @Test fun headerActionsUseOfficialNegativeMarginsInBothDirections() {
        composeRule.setContent {
            Theme(preset = StylePreset.Nova, minTouchTarget = null) {
                Column {
                    listOf(LayoutDirection.Ltr, LayoutDirection.Rtl).forEach { direction ->
                        CompositionLocalProvider(LocalLayoutDirection provides direction) {
                            CodeBlock(Modifier.width(320.dp)) {
                                CodeBlockHeader(Modifier.testTag("header-$direction")) {
                                    CodeBlockTitle("terminal")
                                    CodeBlockActions(Modifier.testTag("actions-$direction")) {
                                        CodeBlockCopyButton("output")
                                    }
                                }
                                CodeBlockContent("output")
                            }
                        }
                    }
                }
            }
        }
        listOf(LayoutDirection.Ltr, LayoutDirection.Rtl).forEach { direction ->
            val header = composeRule.onNodeWithTag("header-$direction").fetchSemanticsNode().boundsInRoot
            val actions = composeRule.onNodeWithTag("actions-$direction").fetchSemanticsNode().boundsInRoot
            assertEquals(20f, header.height, .5f, "-my-1 keeps the 24dp action from raising the 20dp title row")
            assertEquals(header.center.y, actions.center.y, .5f)
            if (direction == LayoutDirection.Ltr) {
                assertEquals(header.right + 6f, actions.right, .5f)
            } else {
                assertEquals(header.left - 6f, actions.left, .5f)
            }
        }
    }

    @Test fun removedLineUsesOneTenPercentTintLayer() {
        var expected = Color.Unspecified
        composeRule.setContent {
            Theme(preset = StylePreset.Nova, minTouchTarget = null) {
                val theme = currentTheme
                expected = theme.colors.destructive.copy(alpha = .1f).compositeOver(theme.colors.background)
                CodeBlock(Modifier.width(240.dp)) {
                    CodeBlockContent {
                        CodeBlockLine(
                            "removed",
                            variant = CodeBlockLineVariant.Removed,
                            modifier = Modifier.testTag("removed"),
                        )
                    }
                }
            }
        }
        val pixels = composeRule.onNodeWithTag("removed").captureToImage().toPixelMap()
        val actual = pixels[pixels.width / 2, pixels.height - 2]
        assertEquals(expected.red, actual.red, .005f)
        assertEquals(expected.green, actual.green, .005f)
        assertEquals(expected.blue, actual.blue, .005f)
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

    @Test
    fun eachSuccessfulCopyRenewsTheConfirmation() {
        var writes = 0
        val clipboard =
            object : Clipboard {
                override suspend fun getClipEntry(): ClipEntry? = null

                override suspend fun setClipEntry(clipEntry: ClipEntry?) {
                    writes++
                }
            }
        composeRule.mainClock.autoAdvance = false
        composeRule.setContent {
            Theme(preset = StylePreset.Nova) {
                CompositionLocalProvider(LocalClipboard provides clipboard) { CodeBlockCopyButton("code") }
            }
        }
        composeRule.onNodeWithContentDescription("Copy").performClick()
        composeRule.mainClock.advanceTimeBy(1000)
        composeRule.onNodeWithContentDescription("Copied").performClick()
        composeRule.mainClock.advanceTimeBy(1200)
        assertEquals(2, writes)
        composeRule.onNodeWithContentDescription("Copied").assertExists()
        composeRule.mainClock.advanceTimeBy(1000)
        composeRule.onNodeWithContentDescription("Copy").assertExists()
    }

    @Test
    fun pageKeysScrollByTheMeasuredViewport() {
        setThemedContent(stylePreset = StylePreset.Nova) {
            CodeBlock(Modifier.width(260.dp)) {
                CodeBlockContent(
                    (1..30).joinToString("\n") { "Line $it" },
                    modifier = Modifier.testTag("paginated-code"),
                    wrap = true,
                    maxHeight = 80.dp,
                )
            }
        }
        val viewport = composeRule.onNodeWithTag("paginated-code")
        val range =
            viewport.fetchSemanticsNode().config[SemanticsProperties.VerticalScrollAxisRange]
        viewport.assertHeightIsEqualTo(80.dp).requestFocus().performKeyInput { pressKey(Key.PageDown) }
        composeRule.waitForIdle()
        assertEquals(80f, range.value(), 0.5f)
        viewport.performKeyInput { pressKey(Key.PageUp) }
        composeRule.waitForIdle()
        assertEquals(0f, range.value(), 0.5f)
    }

    @Test
    fun unscrollableCodeLetsTheParentHandleVerticalArrows() {
        val parentKeys = mutableListOf<Key>()
        setThemedContent(stylePreset = StylePreset.Nova) {
            Column(
                Modifier.onKeyEvent { event ->
                    if (event.type == KeyEventType.KeyDown) parentKeys += event.key
                    false
                },
            ) {
                CodeBlock {
                    CodeBlockContent(
                        "Only one line",
                        Modifier.testTag("uncapped-code"),
                        wrap = true,
                    )
                }
                CodeBlock {
                    CodeBlockContent(
                        "Only one line",
                        Modifier.testTag("clipped-code"),
                        wrap = true,
                        maxHeight = 12.dp,
                        scrollable = false,
                    )
                }
            }
        }
        for (tag in listOf("uncapped-code", "clipped-code")) {
            composeRule.onNodeWithTag(tag).requestFocus().performKeyInput { pressKey(Key.DirectionDown) }
        }
        composeRule.runOnIdle { assertEquals(listOf(Key.DirectionDown, Key.DirectionDown), parentKeys) }
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
