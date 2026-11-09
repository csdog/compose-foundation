package com.toutiaoyan.compose.ui.catalog

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.mutableStateOf
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.toPixelMap
import com.toutiaoyan.compose.ui.catalog.docs.LocalTextDownloader
import com.toutiaoyan.compose.ui.theme.currentTheme
import androidx.compose.ui.Modifier
import androidx.compose.ui.input.key.Key
import androidx.compose.ui.test.*
import androidx.compose.ui.test.junit4.v2.createComposeRule
import androidx.compose.ui.unit.dp
import com.toutiaoyan.compose.ui.catalog.docs.ComponentGuideBlock
import com.toutiaoyan.compose.ui.catalog.docs.componentPageMarkdown
import com.toutiaoyan.compose.ui.catalog.docs.codeBlockDoc
import com.toutiaoyan.compose.ui.theme.Theme
import com.toutiaoyan.compose.ui.tokens.StylePreset
import org.junit.Rule
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFalse
import kotlin.test.assertNull
import kotlin.test.assertTrue

class CodeBlockExamplesTest {
    @get:Rule val rule = createComposeRule()

    private fun show(title: String) {
        rule.setContent {
            Theme(preset = StylePreset.Nova, minTouchTarget = null) {
                Box(Modifier.width(560.dp).verticalScroll(rememberScrollState())) {
                    codeBlockDoc.examples.single { it.title == title }.preview()
                }
            }
        }
    }

    @Test fun pageListsOfficialRecipesInOrder() {
        assertEquals(
            listOf(
                "Default",
                "Basic",
                "Header",
                "Actions",
                "Floating Actions",
                "Line Numbers",
                "Start Line",
                "Wrap",
                "Highlighted Lines",
                "Diff",
                "Height",
                "Show More",
                "Max Height",
                "Size",
                "Destructive",
                "Trace",
                "Tabs",
                "Message",
                "Typeset",
                "RTL",
                "Syntax Highlighting",
                "Languages",
                "Terminal Output",
            ),
            codeBlockDoc.examples.map { it.title },
        )
        assertEquals(
            "Displays code, terminal output and diffs with an optional header, line numbers and a copy button.",
            codeBlockDoc.description,
        )
        // Official recipes are each their own section; the demo above Installation has no heading.
        assertNull(codeBlockDoc.examplesHeading)
        assertFalse(codeBlockDoc.examples.first().showHeading)
        assertEquals(3, codeBlockDoc.examples.single { it.title == "Max Height" }.headingLevel)
        assertEquals(3, codeBlockDoc.examples.single { it.title == "Languages" }.headingLevel)
        assertEquals(3, codeBlockDoc.examples.single { it.title == "Terminal Output" }.headingLevel)
    }

    @Test fun pageKeepsOfficialUsageCompositionFeaturesAndAccessibilityCopy() {
        assertEquals(
            "import com.toutiaoyan.compose.ui.components.codeblock.*",
            codeBlockDoc.usageImportsCode,
        )
        listOf(
            "CodeBlock {",
            "CodeBlockHeader {",
            "CodeBlockTitle(\"Terminal\")",
            "CodeBlockActions { CodeBlockCopyButton(value = output) }",
            "CodeBlockContent(output)",
        ).forEach { assertTrue(codeBlockDoc.usageCode.contains(it), it) }
        val composition = codeBlockDoc.compositionCode!!
        listOf(
            "CodeBlock",
            "├── CodeBlockHeader",
            "│   ├── CodeBlockTitle",
            "│   └── CodeBlockActions",
            "├── CodeBlockActions (floating, when there is no header)",
            "├── CodeBlockContent",
            "│   └── CodeBlockLine",
            "└── CodeBlockFooter",
        ).forEach { assertTrue(composition.contains(it), it) }

        val features = codeBlockDoc.beforeExamples.single()
        assertEquals("Features", features.title)
        val featureItems = (features.blocks.single() as ComponentGuideBlock.Bullets).items
        assertEquals(6, featureItems.size)
        assertTrue(featureItems[0].contains("individual code lines"))
        assertFalse(featureItems.joinToString().contains("one text node"))
        assertTrue(featureItems.any { it.contains("Syntax highlighting is out of scope") })

        val accessibility = codeBlockDoc.afterExamples.single()
        assertEquals("Accessibility", accessibility.title)
        val accessibilityItems = (accessibility.blocks.single() as ComponentGuideBlock.Bullets).items
        assertEquals(8, accessibilityItems.size)
        assertTrue(accessibilityItems.any { it.contains("Line numbers are hidden from screen readers") })
        assertTrue(accessibilityItems.any { it.contains("focus") })
    }

    @Test fun restoredProseAppearsInCopyPageInOfficialOrder() {
        val intro = requireNotNull(codeBlockDoc.introMarkdown)
        assertTrue(intro.contains("logs and diffs"))
        assertTrue(intro.contains("syntax highlighting is out of scope"))
        assertTrue(codeBlockDoc.compositionMarkdown!!.contains("Use lines when you need line variants"))
        val page = componentPageMarkdown(codeBlockDoc)
        assertTrue(page.indexOf("logs and diffs") < page.indexOf("## Installation"))
        assertTrue(page.indexOf("## Composition") < page.indexOf("Use lines when you need line variants"))
        assertTrue(page.indexOf("Use lines when you need line variants") < page.indexOf("## Features"))
        val withoutProse = componentPageMarkdown(codeBlockDoc.copy(introMarkdown = null, compositionMarkdown = null))
        assertFalse(withoutProse.contains("logs and diffs"))
        assertFalse(withoutProse.contains("highlighted token rendering is caller-provided"))
        assertEquals(page.lines().filter { it.startsWith("#") }, withoutProse.lines().filter { it.startsWith("#") })
    }

    @Test fun compositionGuidanceRendersWithoutAddingATocEntry() {
        rule.setContent {
            Theme(preset = StylePreset.Nova, minTouchTarget = null) {
                ComponentDetailContent(codeBlockDoc.copy(examples = emptyList()))
            }
        }
        rule.onNodeWithText("Use lines when you need line variants", substring = true).assertExists()
        assertEquals(
            docSections(codeBlockDoc.copy(compositionMarkdown = null)),
            docSections(codeBlockDoc),
        )
    }

    private fun assertDownloaded(
        title: String,
        filename: String,
        expected: String,
    ) {
        var downloaded: Pair<String, String>? = null
        rule.setContent {
            CompositionLocalProvider(LocalTextDownloader provides { value, name -> downloaded = value to name }) {
                Theme(preset = StylePreset.Nova, minTouchTarget = null) {
                    codeBlockDoc.examples.single { it.title == title }.preview()
                }
            }
        }
        rule.onNodeWithContentDescription("Download").performClick()
        assertEquals(expected to filename, downloaded)
    }

    @Test fun demoDownloadsTheExactRouteSource() =
        assertDownloaded(
            "Default",
            "route.ts",
            """
            import { streamText } from "ai"

            export async function POST(request: Request) {
              const { messages } = await request.json()

              const result = streamText({
                model: "anthropic/claude-sonnet-4.5",
                system: "You are a helpful assistant. Answer in one short paragraph.",
                messages,
              })

              return result.toUIMessageStreamResponse()
            }
            """.trimIndent(),
        )

    @Test fun actionsDownloadTheExactLintOutput() =
        assertDownloaded(
            "Actions",
            "lint.log",
            """
            $ pnpm lint
            apps/web/components/message-list.tsx
              18:22  error  'messages' is possibly 'undefined'. Add a guard before calling map  @typescript-eslint/no-unsafe-call
              42:7   warning  React Hook React.useEffect has a missing dependency: 'scrollToBottom'  react-hooks/exhaustive-deps

            ✖ 2 problems (1 error, 1 warning)
            """.trimIndent(),
        )

    @Test fun unsupportedDownloadIsDisabledAndLabelled() {
        rule.setContent {
            CompositionLocalProvider(LocalTextDownloader provides null) {
                Theme(preset = StylePreset.Nova) { codeBlockDoc.examples.first().preview() }
            }
        }
        rule.onNodeWithContentDescription("Download unavailable on this platform").assertIsNotEnabled()
    }

    @Test fun floatingCopyUsesOpaqueSurfaceOnLiveThemeSwitch() {
        val dark = mutableStateOf(true)
        var expected = Color.Unspecified
        rule.setContent {
            Theme(preset = StylePreset.Nova, minTouchTarget = null, isDark = dark.value) {
                expected = currentTheme.colors.background
                codeBlockDoc.examples.single { it.title == "Floating Actions" }.preview()
            }
        }
        rule.onNodeWithContentDescription("Copy").requestFocus()

        fun assertSurface() {
            val pixels = rule.onNodeWithTag("floating-actions").captureToImage().toPixelMap()
            assertEquals(expected, pixels[pixels.width / 2, 3], "The outer ring must not tint the opaque surface")
        }
        assertSurface()
        rule.runOnIdle { dark.value = false }
        assertSurface()
    }

    @Test fun demoKeepsOfficialRouteCodeHeaderAndHighlightedLines() {
        show("Default")
        rule.onNodeWithText("app/api/chat/route.ts").assertIsDisplayed()
        rule.onNodeWithText("return result.toUIMessageStreamResponse()", substring = true).assertIsDisplayed()
        listOf("1", "13").forEach { rule.onNodeWithText(it).assertExists() }
        val demo = codeBlockDoc.examples.first()
        assertTrue(demo.code.contains("index in 5..9"))
        assertTrue(demo.code.contains("CodeBlockLineVariant.Highlighted"))
        assertTrue(demo.code.contains("Icon(Lucide.Download)"))
        assertTrue(demo.code.contains("onDownload: (String, String) -> Unit"))
        assertFalse(demo.code.contains("LocalTextDownloader"))
        assertTrue(
            codeBlockDoc.examples.single { it.title == "Actions" }.code.contains("onDownload(output, \"lint.log\")"),
        )
        assertTrue(demo.code.contains("label = \"Wrap lines\""))
    }

    @Test fun actionsWrapToggleChangesTheRenderedHeight() {
        show("Actions")
        // The official Actions recipe starts wrapped.
        val wrapped = rule.onNodeWithTag("actions-code").fetchSemanticsNode().boundsInRoot.height
        rule.onNodeWithContentDescription("Wrap lines").performClick()
        rule.waitForIdle()
        val unwrapped = rule.onNodeWithTag("actions-code").fetchSemanticsNode().boundsInRoot.height
        assertTrue(unwrapped < wrapped - 20f, "unwrapped height $unwrapped should be below $wrapped")
        rule.onNodeWithContentDescription("Wrap lines").performClick()
        rule.waitForIdle()
        val restored = rule.onNodeWithTag("actions-code").fetchSemanticsNode().boundsInRoot.height
        assertEquals(wrapped, restored)
    }

    @Test fun startLineMovesTheNumberGutterTo995() {
        show("Start Line")
        rule.onNodeWithText("app/dashboard/page.tsx").assertIsDisplayed()
        rule.onNodeWithText("995").assertIsDisplayed()
        rule.onNodeWithText("1005").assertIsDisplayed()
        assertTrue(codeBlockDoc.examples.single { it.title == "Diff" }.code.contains("forEachIndexed { index, line ->"))
    }

    @Test fun heightCapsTheOutputAndKeyboardEndRevealsTheLastLine() {
        show("Height")
        val block = rule.onNodeWithTag("code-height", useUnmergedTree = true)
        block.assertHeightIsEqualTo(192.dp)
        rule.onNodeWithText("[40] GET /api/chat 200 in 45ms").assertIsNotDisplayed()
        block.requestFocus().performKeyInput { pressKey(Key.MoveEnd) }
        rule.onNodeWithText("[40] GET /api/chat 200 in 45ms").assertIsDisplayed()
        block.performKeyInput { pressKey(Key.MoveHome) }
        rule.onNodeWithText("[01] GET /api/chat 200 in 12ms").assertIsDisplayed()
    }

    @Test fun showMoreClipsUntilExpandedThenShowsFullOutput() {
        show("Show More")
        val code = rule.onNodeWithTag("show-more-code")
        code.assertHeightIsEqualTo(256.dp)
        // Collapsed is overflow-y-hidden: the cap clips instead of scrolling.
        code.requestFocus().performKeyInput { pressKey(Key.MoveEnd) }
        rule.onNodeWithText("Send", substring = true).assertIsNotDisplayed()

        rule.onNodeWithText("Show more").performScrollTo().performClick()
        rule.waitForIdle()
        val expanded = rule.onNodeWithTag("show-more-code").fetchSemanticsNode().boundsInRoot.height
        assertTrue(expanded > 256.dp.value, "expanded height $expanded")
        rule.onNodeWithText("Show less").performScrollTo().assertIsDisplayed()
        rule.onNodeWithText("Show less").performClick()
        rule.waitForIdle()
        rule.onNodeWithTag("show-more-code").assertHeightIsEqualTo(256.dp)
        rule.onNodeWithText("Show more").performScrollTo().assertIsDisplayed()
    }

    @Test fun maxHeightScrollsAfterExpanding() {
        show("Max Height")
        val code = rule.onNodeWithTag("max-height-code")
        code.assertHeightIsEqualTo(256.dp)
        rule.onNodeWithText("Show more").performScrollTo().performClick()
        rule.waitForIdle()
        code.assertHeightIsEqualTo(384.dp)
        rule.onNodeWithText("disabled={status !== \"ready\"}", substring = true).assertIsNotDisplayed()
        code.requestFocus().performKeyInput { pressKey(Key.MoveEnd) }
        rule.onNodeWithText("disabled={status !== \"ready\"}", substring = true).assertIsDisplayed()
    }

    @Test fun tabsSwitchBetweenOfficialFileRecipes() {
        show("Tabs")
        rule.onNodeWithText("route.ts").assertIsDisplayed()
        rule.onNodeWithText("app/api/chat/route.ts").assertIsDisplayed()
        rule.onNodeWithText("chat.tsx").performClick()
        rule.waitForIdle()
        rule.onNodeWithText("components/chat.tsx").assertIsDisplayed()
        rule.onNodeWithText(".env.local").performClick()
        rule.waitForIdle()
        rule.onNodeWithText("AI_GATEWAY_API_KEY=your-api-key").assertIsDisplayed()
    }

    @Test fun rtlHeaderFollowsDirectionWhileCodeStaysLeftToRight() {
        show("RTL")
        rule.onNodeWithText("الطرفية").assertIsDisplayed()
        rule.onNodeWithText("ثبّت الحزم، ثم شغّل خادم التطوير.").assertIsDisplayed()
        val title = rule.onNodeWithText("الطرفية").fetchSemanticsNode().boundsInRoot
        val copy = rule.onNodeWithContentDescription("Copy").fetchSemanticsNode().boundsInRoot
        // Header follows the page direction: the title sits at the inline start (right).
        assertTrue(title.left > copy.left, "title ${title.left} should be right of copy ${copy.left}")
        // Code content stays dir="ltr": the number gutter remains on the left.
        val number = rule.onNodeWithText("1").fetchSemanticsNode().boundsInRoot
        val firstLine = rule.onNodeWithText("pnpm add ai @ai-sdk/react zod").fetchSemanticsNode().boundsInRoot
        assertTrue(number.left < firstLine.left, "number ${number.left} should be left of code ${firstLine.left}")
    }

    @Test fun traceRecipeShowsTerminalAndDestructiveOutput() {
        show("Trace")
        rule.onNodeWithText("Ran node --version").assertIsDisplayed()
        rule.onNodeWithText("Failed to run pnpm dev").assertIsDisplayed()
        rule.onNodeWithText("v24.18.0", substring = true).assertIsDisplayed()
        rule.onNodeWithText("EADDRINUSE", substring = true).assertIsDisplayed()
    }

    @Test fun messageRecipePlacesCodeBetweenParagraphs() {
        show("Message")
        rule.onNodeWithText("How do I stream a response?").assertIsDisplayed()
        rule.onNodeWithText("app/api/chat/route.ts").assertIsDisplayed()
        rule.onNodeWithText("The client reads it with useChat.").assertIsDisplayed()
    }

    @Test fun typesetRecipeKeepsParagraphRhythm() {
        show("Typeset")
        rule.onNodeWithText("Stream a response").assertIsDisplayed()
        rule.onNodeWithText("Create a route handler that calls", substring = true).assertIsDisplayed()
        rule.onNodeWithText("app/api/chat/route.ts").assertIsDisplayed()
    }

    @Test fun syntaxHighlightingRecipesRenderPlainTextAndSaySo() {
        show("Syntax Highlighting")
        rule.onNodeWithText("getWeather output").assertIsDisplayed()
        rule.onNodeWithText("\"location\": \"San Francisco\"", substring = true).assertIsDisplayed()
        val descriptions =
            listOf("Syntax Highlighting", "Languages", "Terminal Output")
                .map { title -> codeBlockDoc.examples.single { it.title == title }.descriptionMarkdown.orEmpty() }
        descriptions.forEach { description ->
            assertTrue(description.contains("out of scope"), description)
        }
    }

    @Test fun languagesAndTerminalOutputRecipesRenderOfficialSamples() {
        show("Languages")
        rule.onNodeWithText("main.py").assertIsDisplayed()
        rule.onNodeWithText("def greet(name: str) -> str:", substring = true).assertIsDisplayed()
        rule.onNodeWithText("notes.txt").assertIsDisplayed()
    }
}
