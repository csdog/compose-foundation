@file:OptIn(androidx.compose.foundation.style.ExperimentalFoundationStyleApi::class)

package com.toutiaoyan.compose.ui.catalog.docs

import androidx.compose.foundation.background
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.widthIn
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.dropShadow
import androidx.compose.ui.graphics.shadow.Shadow
import com.toutiaoyan.compose.tailwind.core.twRadius
import androidx.compose.ui.draw.rotate
import androidx.compose.ui.platform.LocalLayoutDirection
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.semantics.stateDescription
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.withStyle
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.LayoutDirection
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.composables.icons.lucide.ChevronDown
import com.composables.icons.lucide.Download
import com.composables.icons.lucide.FileCode
import com.composables.icons.lucide.Lucide
import com.composables.icons.lucide.Terminal
import com.composables.icons.lucide.TextWrap
import com.toutiaoyan.compose.tailwind.core.LocalUnitSpacing
import com.toutiaoyan.compose.ui.catalog.catalogArabicFontFamily
import com.toutiaoyan.compose.ui.catalog.catalogHebrewFontFamily
import com.toutiaoyan.compose.ui.catalog.catalogMonoFontFamily
import com.toutiaoyan.compose.ui.components.bubble.Bubble
import com.toutiaoyan.compose.ui.components.bubble.BubbleVariant
import com.toutiaoyan.compose.ui.components.button.Button
import com.toutiaoyan.compose.ui.components.button.ButtonSize
import com.toutiaoyan.compose.ui.components.button.ButtonVariant
import com.toutiaoyan.compose.ui.components.codeblock.CodeBlockActions
import com.toutiaoyan.compose.ui.components.codeblock.CodeBlockContent
import com.toutiaoyan.compose.ui.components.codeblock.CodeBlockContentScope
import com.toutiaoyan.compose.ui.components.codeblock.CodeBlockCopyButton
import com.toutiaoyan.compose.ui.components.codeblock.CodeBlockFooter
import com.toutiaoyan.compose.ui.components.codeblock.CodeBlockHeader
import com.toutiaoyan.compose.ui.components.codeblock.CodeBlockLine
import com.toutiaoyan.compose.ui.components.codeblock.CodeBlockLineVariant
import com.toutiaoyan.compose.ui.components.codeblock.CodeBlockPartsScope
import com.toutiaoyan.compose.ui.components.codeblock.CodeBlockSize
import com.toutiaoyan.compose.ui.components.codeblock.CodeBlockTitle
import com.toutiaoyan.compose.ui.components.codeblock.CodeBlockVariant
import com.toutiaoyan.compose.ui.components.codeblock.CodeBlock as CodeBlockPanel
import com.toutiaoyan.compose.ui.components.icon.Icon
import com.toutiaoyan.compose.ui.components.message.Message
import com.toutiaoyan.compose.ui.components.message.MessageAlign
import com.toutiaoyan.compose.ui.components.tabs.Tabs
import com.toutiaoyan.compose.ui.components.tabs.TabsContent
import com.toutiaoyan.compose.ui.components.tabs.TabsList
import com.toutiaoyan.compose.ui.components.tabs.TabsTrigger
import com.toutiaoyan.compose.ui.components.text.Text
import com.toutiaoyan.compose.ui.components.trace.Trace
import com.toutiaoyan.compose.ui.components.trace.TraceCollapsible
import com.toutiaoyan.compose.ui.components.trace.TraceCollapsibleContent
import com.toutiaoyan.compose.ui.components.trace.TraceCollapsibleTrigger
import com.toutiaoyan.compose.ui.components.trace.TraceIcon
import com.toutiaoyan.compose.ui.components.trace.TraceLine
import com.toutiaoyan.compose.ui.components.trace.TraceStatus
import com.toutiaoyan.compose.ui.components.trace.TraceTitle
import com.toutiaoyan.compose.ui.theme.Theme
import com.toutiaoyan.compose.ui.theme.currentTheme

/**
 * Official code-block copy, current at revision 8a3536678a528cc012b9e62484290e79d0be410f.
 * The dollar sign is a Kotlin-template sentinel so the terminal copy can keep its literal `$`.
 */
private const val D = "$"

private val demoCode =
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
    """.trimIndent()

private val basicOutput =
    """
    $D pnpm build
    > next build

     ✓ Compiled successfully in 4.2s
     ✓ Generating static pages (12/12)

    Route (app)                Size  First Load JS
    ┌ ○ /                    1.9 kB         112 kB
    ├ ○ /_not-found           993 B         103 kB
    └ ƒ /api/chat             141 B         102 kB
    """.trimIndent()

private const val headerCommand = "pnpm add ai @ai-sdk/react zod"

private val headerOutput =
    """
    $D $headerCommand
    Packages: +14
    Progress: resolved 412, reused 398, downloaded 14, added 14, done
    Done in 2.8s using pnpm v10.18.0
    """.trimIndent()

private val actionsOutput =
    """
    $D pnpm lint
    apps/web/components/message-list.tsx
      18:22  error  'messages' is possibly 'undefined'. Add a guard before calling map  @typescript-eslint/no-unsafe-call
      42:7   warning  React Hook React.useEffect has a missing dependency: 'scrollToBottom'  react-hooks/exhaustive-deps

    ✖ 2 problems (1 error, 1 warning)
    """.trimIndent()

private val floatingOutput =
    """
    $D pnpm add ai @ai-sdk/react zod
    Progress: resolved 412, reused 398, downloaded 14, added 14, done
    dependencies:
    + @ai-sdk/react 3.0.12
    + ai 6.0.12
    + zod 4.1.12
    Done in 2.8s using pnpm v10.18.0
    """.trimIndent()

private val lineNumbersCode =
    """
    {
      "name": "chat",
      "private": true,
      "scripts": {
        "dev": "next dev",
        "build": "next build"
      }
    }
    """.trimIndent()

private val startLineCode =
    """
    |  if (!session) {
    |    redirect("/login")
    |  }
    |
    |  const projects = await db.project.findMany({
    |    where: { ownerId: session.user.id },
    |    orderBy: { updatedAt: "desc" },
    |  })
    |
    |  return <ProjectList projects={projects} />
    |}
    """.trimMargin()

private val wrapOutput =
    "Error: The model \"anthropic/claude-sonnet-4.5\" returned an error: Request too large. The prompt is " +
        "212,431 tokens and the context window is 200,000 tokens. Remove some messages or summarize the " +
        "conversation and try again."

private val highlightedCode =
    """
    export default {
      experimental: {
        reactCompiler: true,
        viewTransition: true,
      },
      images: {
        remotePatterns: [{ hostname: "avatars.githubusercontent.com" }],
      },
    }
    """.trimIndent()

private val diffLines =
    listOf(
        " export function Greeting({ name }: { name: string }) {",
        "-  return <p>Hello, {name}</p>",
        "+  return <p className=\"text-sm text-muted-foreground\">",
        "+    Hello, {name}!",
        "+  </p>",
        " }",
    )

private val diffPatch = diffLines.joinToString("\n")

private val heightLogs =
    (1..40).joinToString("\n") { index ->
        "[" + index.toString().padStart(2, '0') + "] GET /api/chat 200 in " +
            (12 + ((index - 1) * 7) % 40) + "ms"
    }

private val showMoreCode =
    """
    "use client"

    import * as React from "react"
    import { useChat } from "@ai-sdk/react"

    import { Button } from "@/components/ui/button"
    import { Input } from "@/components/ui/input"

    export function Chat() {
      const [input, setInput] = React.useState("")
      const { messages, sendMessage, status } = useChat()

      return (
        <div className="flex flex-col gap-4">
          {messages.map((message) => (
            <div key={message.id}>
              {message.parts.map((part, index) =>
                part.type === "text" ? <p key={index}>{part.text}</p> : null
              )}
            </div>
          ))}
          <form
            onSubmit={(event) => {
              event.preventDefault()
              sendMessage({ text: input })
              setInput("")
            }}
          >
            <Input value={input} onChange={(event) => setInput(event.target.value)} />
            <Button disabled={status !== "ready"}>Send</Button>
          </form>
        </div>
      )
    }
    """.trimIndent()

private val destructiveError =
    """
    TypeError: Cannot read properties of undefined (reading 'map')
        at MessageList (components/message-list.tsx:18:22)
        at renderWithHooks (react-dom.development.js:16305:18)
    """.trimIndent()

private val traceOutput =
    """
    $D node --version
    v24.18.0
    """.trimIndent()

private val traceError =
    """
    $D pnpm dev
    Error: listen EADDRINUSE: address already in use :::3000
        at Server.setupListenHandle [as _listen2] (node:net:1908:16)
    """.trimIndent()

private class TabFile(val id: String, val title: String, val code: String)

private val tabFiles =
    listOf(
        TabFile(
            "route",
            "app/api/chat/route.ts",
            """
            import { streamText } from "ai"

            export async function POST(request: Request) {
              const { messages } = await request.json()
              const result = streamText({ model: "anthropic/claude-sonnet-4.5", messages })

              return result.toUIMessageStreamResponse()
            }
            """.trimIndent(),
        ),
        TabFile(
            "chat",
            "components/chat.tsx",
            """
            "use client"

            import { useChat } from "@ai-sdk/react"

            export function Chat() {
              const { messages } = useChat()

              return <MessageList messages={messages} />
            }
            """.trimIndent(),
        ),
        TabFile("env", ".env.local", "AI_GATEWAY_API_KEY=your-api-key"),
    )

private val chatCode =
    """
    import { streamText } from "ai"

    export async function POST(request: Request) {
      const { messages } = await request.json()

      const result = streamText({
        model: "anthropic/claude-sonnet-4.5",
        messages,
      })

      return result.toUIMessageStreamResponse()
    }
    """.trimIndent()

private val typesetCode =
    """
    import { streamText } from "ai"

    export async function POST(request: Request) {
      const { messages } = await request.json()
      const result = streamText({ model: "anthropic/claude-sonnet-4.5", messages })

      return result.toUIMessageStreamResponse()
    }
    """.trimIndent()

private val rtlCode = "pnpm add ai @ai-sdk/react zod\npnpm dev"

private val highlightingCode =
    """
    {
      "location": "San Francisco",
      "temperature": 18,
      "unit": "celsius",
      "conditions": ["fog", "wind"]
    }
    """.trimIndent()

private val cssSample =
    """
    @import "tailwindcss";

    :root {
      --radius: 0.625rem;
    }
    """.trimIndent()

private val pythonSample =
    """
    def greet(name: str) -> str:
        return f"Hello, {name}!"
    """.trimIndent()

private const val notesSample = "A language that is not in the list renders as plain text."

private val ansiOutput =
    """
    $D pnpm test
    ✓ src/format.test.ts (4 tests)
    ✓ src/parse.test.ts (7 tests)
    ✗ src/stream.test.ts (3 tests | 1 failed)

    AssertionError: expected 'hello' to be 'hello world'

    Tests  1 failed | 13 passed (14)
    """.trimIndent()

private val rtlLabels = listOf("Arabic (العربية)", "English", "Hebrew (עברית)")

private class RtlCopy(val description: String, val title: String)

private val rtlCopy =
    listOf(
        RtlCopy("ثبّت الحزم، ثم شغّل خادم التطوير.", "الطرفية"),
        RtlCopy("Install the packages, then start the dev server.", "Terminal"),
        RtlCopy("התקן את החבילות, ואז הפעל את שרת הפיתוח.", "מסוף"),
    )

@Composable
private fun monoStyle(): TextStyle = TextStyle(fontFamily = catalogMonoFontFamily())

@Composable
private fun wrapToggleButton(
    wrap: Boolean,
    onToggle: () -> Unit,
    modifier: Modifier = Modifier,
) {
    Button(
        onClick = onToggle,
        modifier =
            modifier.semantics {
                // aria-pressed: announce the ongoing wrap state.
                stateDescription = if (wrap) "Pressed" else "Not pressed"
            },
        variant = ButtonVariant.Ghost,
        size = ButtonSize.IconXs,
        label = "Wrap lines",
    ) { Icon(Lucide.TextWrap) }
}

@Composable
private fun downloadButton(
    value: String,
    filename: String,
    modifier: Modifier = Modifier,
) {
    val download = LocalTextDownloader.current
    Button(
        onClick = { download?.invoke(value, filename) },
        enabled = download != null,
        modifier = modifier,
        variant = ButtonVariant.Ghost,
        size = ButtonSize.IconXs,
        label = if (download != null) "Download" else "Download unavailable on this platform",
    ) { Icon(Lucide.Download) }
}

@Composable
private fun CodeBlockContentScope.RichLines(
    code: String,
    highlighted: Set<Int> = emptySet(),
) {
    code.removeSuffix("\n").split("\n").forEachIndexed { index, line ->
        CodeBlockLine(
            text = line,
            variant = if (index in highlighted) CodeBlockLineVariant.Highlighted else CodeBlockLineVariant.Default,
            lineNumber = index + 1,
        )
    }
}

@Composable
private fun CodeBlockDemoPreview() {
    var wrap by remember { mutableStateOf(false) }
    Box(Modifier.widthIn(max = 512.dp).fillMaxWidth()) {
        CodeBlockPanel {
            CodeBlockHeader {
                CodeBlockTitle {
                    Icon(Lucide.FileCode, Modifier.size(14.dp))
                    Text(maxLines = 1, overflow = TextOverflow.Ellipsis) { "app/api/chat/route.ts" }
                }
                CodeBlockActions {
                    wrapToggleButton(wrap, { wrap = !wrap })
                    downloadButton(demoCode, "route.ts")
                    CodeBlockCopyButton(demoCode)
                }
            }
            CodeBlockContent(
                wrap = wrap,
                lineNumbers = true,
                textStyle = monoStyle(),
                modifier = Modifier.testTag("code-demo"),
            ) {
                RichLines(demoCode, highlighted = (5..9).toSet())
            }
        }
    }
}

@Composable
private fun CodeBlockBasicPreview() {
    Box(Modifier.widthIn(max = 448.dp).fillMaxWidth()) {
        CodeBlockPanel { CodeBlockContent(basicOutput, textStyle = monoStyle()) }
    }
}

@Composable
private fun CodeBlockHeaderPreview() {
    Box(Modifier.widthIn(max = 448.dp).fillMaxWidth()) {
        CodeBlockPanel {
            CodeBlockHeader {
                CodeBlockTitle {
                    Icon(Lucide.Terminal, Modifier.size(14.dp))
                    Text { "Terminal" }
                }
                CodeBlockActions { CodeBlockCopyButton(headerCommand) }
            }
            CodeBlockContent(headerOutput, textStyle = monoStyle())
        }
    }
}

@Composable
private fun CodeBlockActionsPreview() {
    var wrap by remember { mutableStateOf(true) }
    Box(Modifier.widthIn(max = 448.dp).fillMaxWidth()) {
        CodeBlockPanel {
            CodeBlockHeader {
                CodeBlockTitle {
                    Icon(Lucide.Terminal, Modifier.size(14.dp))
                    Text { "Terminal" }
                }
                CodeBlockActions {
                    wrapToggleButton(wrap, { wrap = !wrap })
                    downloadButton(actionsOutput, "lint.log")
                    CodeBlockCopyButton(actionsOutput)
                }
            }
            CodeBlockContent(
                actionsOutput,
                modifier = Modifier.testTag("actions-code"),
                wrap = wrap,
                textStyle = monoStyle(),
            )
        }
    }
}

@Composable
private fun CodeBlockFloatingActionsPreview() {
    Box(Modifier.widthIn(max = 448.dp).fillMaxWidth()) {
        CodeBlockPanel {
            CodeBlockActions(modifier = Modifier.testTag("floating-actions")) {
                val theme = currentTheme
                val shape = RoundedCornerShape(minOf(twRadius.md, 10.dp))
                CodeBlockCopyButton(
                    floatingOutput,
                    modifier =
                        Modifier
                            .dropShadow(
                                shape,
                                Shadow(radius = 0.dp, spread = 1.dp, color = theme.colors.foreground.copy(alpha = .1f)),
                            )
                            .background(theme.colors.background, shape),
                )
            }
            CodeBlockContent(floatingOutput, textStyle = monoStyle())
        }
    }
}

@Composable
private fun CodeBlockLineNumbersPreview() {
    Box(Modifier.widthIn(max = 448.dp).fillMaxWidth()) {
        CodeBlockPanel { CodeBlockContent(lineNumbersCode, lineNumbers = true, textStyle = monoStyle()) }
    }
}

@Composable
private fun CodeBlockStartLinePreview() {
    Box(Modifier.widthIn(max = 448.dp).fillMaxWidth()) {
        CodeBlockPanel {
            CodeBlockHeader { CodeBlockTitle("app/dashboard/page.tsx") }
            CodeBlockContent(startLineCode, lineNumbers = true, startLine = 995, textStyle = monoStyle())
        }
    }
}

@Composable
private fun CodeBlockWrapPreview() {
    Column(Modifier.widthIn(max = 448.dp).fillMaxWidth(), verticalArrangement = Arrangement.spacedBy(16.dp)) {
        CodeBlockPanel { CodeBlockContent(wrapOutput, textStyle = monoStyle()) }
        CodeBlockPanel { CodeBlockContent(wrapOutput, wrap = true, textStyle = monoStyle()) }
    }
}

@Composable
private fun CodeBlockHighlightedLinesPreview() {
    Box(Modifier.widthIn(max = 448.dp).fillMaxWidth()) {
        CodeBlockPanel {
            CodeBlockContent(lineNumbers = true, textStyle = monoStyle()) {
                RichLines(highlightedCode, highlighted = setOf(2, 3))
            }
        }
    }
}

@Composable
private fun CodeBlockDiffPreview() {
    Box(Modifier.widthIn(max = 448.dp).fillMaxWidth()) {
        CodeBlockPanel {
            CodeBlockHeader {
                CodeBlockTitle("components/greeting.tsx")
                CodeBlockActions { CodeBlockCopyButton(diffPatch) }
            }
            CodeBlockContent(textStyle = monoStyle()) {
                diffLines.forEachIndexed { index, marked ->
                    val variant =
                        when (marked.firstOrNull()) {
                            '+' -> CodeBlockLineVariant.Added
                            '-' -> CodeBlockLineVariant.Removed
                            else -> CodeBlockLineVariant.Default
                        }
                    CodeBlockLine(
                        text = marked.drop(1),
                        variant = variant,
                        lineNumber = index + 1,
                    )
                }
            }
        }
    }
}

@Composable
private fun CodeBlockHeightPreview() {
    Box(Modifier.widthIn(max = 448.dp).fillMaxWidth()) {
        CodeBlockPanel {
            CodeBlockHeader {
                CodeBlockTitle("Logs")
                CodeBlockActions { CodeBlockCopyButton(heightLogs) }
            }
            CodeBlockContent(
                heightLogs,
                modifier = Modifier.testTag("code-height"),
                lineNumbers = true,
                maxHeight = 192.dp,
                textStyle = monoStyle(),
            )
        }
    }
}

@Composable
private fun CodeBlockPartsScope.ShowMoreFooter(
    expanded: Boolean,
    onToggle: () -> Unit,
    modifier: Modifier = Modifier,
) {
    CodeBlockFooter {
        Box(Modifier.fillMaxWidth(), contentAlignment = Alignment.Center) {
            Button(
                onClick = onToggle,
                modifier =
                    modifier.semantics {
                        // aria-expanded: announce the expanded state.
                        stateDescription = if (expanded) "Expanded" else "Collapsed"
                    },
                variant = ButtonVariant.Ghost,
                size = ButtonSize.Xs,
            ) {
                Icon(
                    Lucide.ChevronDown,
                    Modifier.size(16.dp).rotate(if (expanded) 180f else 0f),
                    contentDescription = null,
                )
                Text { if (expanded) "Show less" else "Show more" }
            }
        }
    }
}

@Composable
private fun CodeBlockShowMorePreview() {
    // Official Show More raises the cap instead of removing it (the Max Height recipe), while
    // this recipe removes it entirely when expanded.
    var expanded by remember { mutableStateOf(false) }
    Box(Modifier.widthIn(max = 448.dp).fillMaxWidth()) {
        CodeBlockPanel {
            CodeBlockHeader {
                CodeBlockTitle("components/chat.tsx")
                CodeBlockActions { CodeBlockCopyButton(showMoreCode) }
            }
            CodeBlockContent(
                showMoreCode,
                modifier = Modifier.testTag("show-more-code"),
                lineNumbers = true,
                maxHeight = if (expanded) null else 256.dp,
                scrollable = false,
                textStyle = monoStyle(),
            )
            ShowMoreFooter(expanded, onToggle = {
                expanded = !expanded
            }, modifier = Modifier.testTag("show-more-toggle"))
        }
    }
}

@Composable
private fun CodeBlockMaxHeightPreview() {
    var expanded by remember { mutableStateOf(false) }
    Box(Modifier.widthIn(max = 448.dp).fillMaxWidth()) {
        CodeBlockPanel {
            CodeBlockHeader {
                CodeBlockTitle("components/chat.tsx")
                CodeBlockActions { CodeBlockCopyButton(showMoreCode) }
            }
            CodeBlockContent(
                showMoreCode,
                modifier = Modifier.testTag("max-height-code"),
                lineNumbers = true,
                maxHeight = if (expanded) 384.dp else 256.dp,
                scrollable = expanded,
                textStyle = monoStyle(),
            )
            ShowMoreFooter(expanded, onToggle = {
                expanded = !expanded
            }, modifier = Modifier.testTag("max-height-toggle"))
        }
    }
}

@Composable
private fun SizeSample(small: Boolean) {
    CodeBlockPanel(
        size = if (small) CodeBlockSize.Sm else CodeBlockSize.Default,
    ) {
        CodeBlockHeader {
            CodeBlockTitle {
                Icon(Lucide.Terminal, Modifier.size(if (small) 12.dp else 14.dp))
                Text { "Terminal" }
            }
            CodeBlockActions { CodeBlockCopyButton(floatingOutput) }
        }
        CodeBlockContent(floatingOutput, wrap = true, textStyle = monoStyle())
    }
}

@Composable
private fun CodeBlockSizePreview() {
    Column(Modifier.widthIn(max = 448.dp).fillMaxWidth(), verticalArrangement = Arrangement.spacedBy(16.dp)) {
        SizeSample(small = false)
        SizeSample(small = true)
    }
}

@Composable
private fun CodeBlockDestructivePreview() {
    Box(Modifier.widthIn(max = 448.dp).fillMaxWidth()) {
        CodeBlockPanel(variant = CodeBlockVariant.Destructive) {
            CodeBlockHeader {
                CodeBlockTitle("Error")
                CodeBlockActions { CodeBlockCopyButton(destructiveError) }
            }
            CodeBlockContent(destructiveError, wrap = true, textStyle = monoStyle())
        }
    }
}

@Composable
private fun CodeBlockTracePreview() {
    Box(Modifier.widthIn(max = 448.dp).fillMaxWidth()) {
        Trace {
            TraceCollapsible(defaultOpen = true) {
                TraceCollapsibleTrigger {
                    TraceLine {
                        TraceIcon { Icon(Lucide.Terminal) }
                        TraceTitle("Ran node --version")
                    }
                }
                TraceCollapsibleContent {
                    CodeBlockPanel(size = CodeBlockSize.Sm) {
                        CodeBlockContent(traceOutput, wrap = true, maxHeight = 256.dp, textStyle = monoStyle())
                    }
                }
            }
            TraceCollapsible(defaultOpen = true) {
                TraceCollapsibleTrigger {
                    TraceLine(status = TraceStatus.Error) {
                        TraceIcon { Icon(Lucide.Terminal) }
                        TraceTitle("Failed to run pnpm dev")
                    }
                }
                TraceCollapsibleContent {
                    CodeBlockPanel(size = CodeBlockSize.Sm, variant = CodeBlockVariant.Destructive) {
                        CodeBlockContent(traceError, wrap = true, maxHeight = 256.dp, textStyle = monoStyle())
                    }
                }
            }
        }
    }
}

@Composable
private fun CodeBlockTabsPreview() {
    Tabs(defaultValue = "route", modifier = Modifier.widthIn(max = 448.dp).fillMaxWidth()) {
        TabsList {
            tabFiles.forEach { file ->
                TabsTrigger(file.id, file.title.split("/").last())
            }
        }
        tabFiles.forEach { file ->
            TabsContent(file.id) {
                CodeBlockPanel {
                    CodeBlockHeader {
                        CodeBlockTitle(file.title)
                        CodeBlockActions { CodeBlockCopyButton(file.code) }
                    }
                    CodeBlockContent(file.code, lineNumbers = true, textStyle = monoStyle())
                }
            }
        }
    }
}

@Composable
private fun CodeBlockMessagePreview() {
    Column(Modifier.widthIn(max = 448.dp).fillMaxWidth(), verticalArrangement = Arrangement.spacedBy(16.dp)) {
        Message(modifier = Modifier.fillMaxWidth(), align = MessageAlign.End) {
            Bubble("How do I stream a response?", variant = BubbleVariant.Muted, align = MessageAlign.End)
        }
        Message(modifier = Modifier.fillMaxWidth()) {
            Text { "Create a route handler that returns the stream:" }
            CodeBlockPanel {
                CodeBlockHeader {
                    CodeBlockTitle("app/api/chat/route.ts")
                    CodeBlockActions { CodeBlockCopyButton(chatCode) }
                }
                CodeBlockContent(chatCode, textStyle = monoStyle())
            }
            Text { "The client reads it with useChat." }
        }
    }
}

@Composable
private fun TypesetParagraph(
    prefix: String,
    inlineCode: String,
    suffix: String,
) {
    val theme = currentTheme
    Text(
        textStyle =
            theme.typography.bodyMedium.copy(
                fontFamily = theme.fontFamily,
                fontSize = 15.sp,
                lineHeight = 26.25.sp,
            ),
    ) {
        buildAnnotatedString {
            append(prefix)
            withStyle(
                SpanStyle(
                    fontFamily = FontFamily.Monospace,
                    fontSize = 13.sp,
                    background = theme.colors.muted,
                ),
            ) { append(inlineCode) }
            append(suffix)
        }
    }
}

@Composable
private fun CodeBlockTypesetPreview() {
    // The catalog has no `typeset` utility; heading/paragraph typography stands in for it so the
    // block's own styles and the paragraph rhythm stay visible.
    val theme = currentTheme
    Column(
        Modifier.widthIn(max = 448.dp).fillMaxWidth(),
        verticalArrangement = Arrangement.spacedBy(20.dp),
    ) {
        Text(
            textStyle =
                TextStyle(
                    fontFamily = theme.fontFamily,
                    fontSize = 20.sp,
                    lineHeight = 28.sp,
                    fontWeight = FontWeight.SemiBold,
                ),
        ) { "Stream a response" }
        TypesetParagraph(
            prefix = "Create a route handler that calls ",
            inlineCode = "streamText",
            suffix = " and returns the stream to the client.",
        )
        CodeBlockPanel {
            CodeBlockHeader {
                CodeBlockTitle {
                    Icon(Lucide.FileCode, Modifier.size(14.dp))
                    Text(maxLines = 1, overflow = TextOverflow.Ellipsis) { "app/api/chat/route.ts" }
                }
                CodeBlockActions { CodeBlockCopyButton(typesetCode) }
            }
            CodeBlockContent(typesetCode, lineNumbers = true, textStyle = monoStyle())
        }
        TypesetParagraph(
            prefix = "The ",
            inlineCode = "useChat",
            suffix = " hook reads the stream on the client and renders the answer while it is written.",
        )
    }
}

@Composable
private fun CodeBlockRtlPreview() {
    var language by remember { mutableStateOf(0) }
    RtlPreview(language, rtlLabels, { language = it }) {
        val inherited = currentTheme
        val fontFamily =
            when (language) {
                0 -> catalogArabicFontFamily()
                2 -> catalogHebrewFontFamily()
                else -> inherited.fontFamily
            }
        CompositionLocalProvider(
            LocalLayoutDirection provides if (language == 1) LayoutDirection.Ltr else LayoutDirection.Rtl,
        ) {
            Theme(
                preset = inherited.activePreset,
                baseColor = inherited.activeBaseColor,
                accent = inherited.activeAccent,
                isDark = inherited.isDark,
                baseRadius = inherited.baseRadius,
                ring = inherited.ring,
                unitSpacing = LocalUnitSpacing.current,
                minTouchTarget = inherited.minTouchTarget,
                fontFamily = fontFamily,
            ) {
                val copy = rtlCopy[language]
                Column(
                    Modifier.widthIn(max = 448.dp).fillMaxWidth(),
                    verticalArrangement = Arrangement.spacedBy(12.dp),
                ) {
                    Text { copy.description }
                    CodeBlockPanel {
                        CodeBlockHeader {
                            CodeBlockTitle {
                                Icon(Lucide.Terminal, Modifier.size(14.dp))
                                Text(maxLines = 1, overflow = TextOverflow.Ellipsis) { copy.title }
                            }
                            CodeBlockActions { CodeBlockCopyButton(rtlCode) }
                        }
                        CodeBlockContent(
                            rtlCode,
                            modifier = Modifier.testTag("rtl-code"),
                            lineNumbers = true,
                            textStyle = monoStyle(),
                        )
                    }
                }
            }
        }
    }
}

@Composable
private fun CodeBlockHighlightingPreview() {
    Box(Modifier.widthIn(max = 448.dp).fillMaxWidth()) {
        CodeBlockPanel {
            CodeBlockHeader { CodeBlockTitle("getWeather output") }
            CodeBlockContent(highlightingCode, textStyle = monoStyle())
        }
    }
}

@Composable
private fun CodeBlockLanguagesPreview() {
    Column(Modifier.widthIn(max = 448.dp).fillMaxWidth(), verticalArrangement = Arrangement.spacedBy(16.dp)) {
        listOf(
            "app/globals.css" to cssSample,
            "main.py" to pythonSample,
            "notes.txt" to notesSample,
        ).forEach { (title, code) ->
            CodeBlockPanel {
                CodeBlockHeader { CodeBlockTitle(title) }
                CodeBlockContent(code, textStyle = monoStyle())
            }
        }
    }
}

@Composable
private fun CodeBlockAnsiPreview() {
    Box(Modifier.widthIn(max = 448.dp).fillMaxWidth()) {
        CodeBlockPanel {
            CodeBlockHeader {
                CodeBlockTitle {
                    Icon(Lucide.Terminal, Modifier.size(14.dp))
                    Text { "Terminal" }
                }
                CodeBlockActions { CodeBlockCopyButton(ansiOutput) }
            }
            CodeBlockContent(ansiOutput, wrap = true, textStyle = monoStyle())
        }
    }
}

val codeBlockDoc =
    ComponentDoc(
        id = "code-block",
        title = "Code Block",
        description =
            "Displays code, terminal output and diffs with an optional header, line numbers and a copy button.",
        introMarkdown =
            "`CodeBlock` displays code, terminal output, logs and diffs. It has a header with a title " +
                "and actions, line numbers, wrapping, diff and highlighted lines, and a small size for tool " +
                "output in a [Trace](/trace).\n\n" +
                "The official component offers optional syntax highlighting through a separate hook. " +
                "This Compose port renders plain text; syntax highlighting is out of scope.",
        compositionMarkdown =
            "`CodeBlockContent` takes a string or a list of `CodeBlockLine`. Use lines when you need line " +
                "variants or highlighted tokens. In Compose, supply lines through the content lambda; " +
                "highlighted token rendering is caller-provided.",
        usageCode =
            """
            CodeBlock {
                CodeBlockHeader {
                    CodeBlockTitle("Terminal")
                    CodeBlockActions { CodeBlockCopyButton(value = output) }
                }
                CodeBlockContent(output)
            }
            """.trimIndent(),
        usageImportsCode = "import com.toutiaoyan.compose.ui.components.codeblock.*",
        compositionCode =
            """
            CodeBlock
            ├── CodeBlockHeader
            │   ├── CodeBlockTitle
            │   └── CodeBlockActions
            │       └── CodeBlockCopyButton
            ├── CodeBlockActions (floating, when there is no header)
            ├── CodeBlockContent
            │   └── CodeBlockLine
            └── CodeBlockFooter
            """.trimIndent(),
        beforeExamples =
            listOf(
                ComponentGuideSection(
                    title = "Features",
                    blocks =
                        listOf(
                            ComponentGuideBlock.Bullets(
                                listOf(
                                    "It accepts changing output. Strings render as individual code lines in this " +
                                        "Compose port; no incremental syntax highlighter is provided.",
                                    "It copies cleanly. Line numbers and diff markers are left out of a selection, " +
                                        "and blank lines survive the copy.",
                                    "It reads left to right. The content is always `dir=\"ltr\"`. The header " +
                                        "follows the page direction.",
                                    "It renders plain text. Syntax highlighting is out of scope in this " +
                                        "Compose port, so `CodeBlockContent` never loads a highlighter.",
                                    "It fits where code shows up. Use it in a message, a Trace line, Tabs, a " +
                                        "Dialog or a `typeset` container.",
                                    "It scrolls by keyboard. The content is focusable, and floating actions " +
                                        "show on focus as well as on hover.",
                                ),
                            ),
                        ),
                ),
            ),
        examplesHeading = null,
        examples =
            listOf(
                ComponentExample(
                    title = "Default",
                    showHeading = false,
                    minPreviewHeight = 0.dp,
                    code =
                        """
                        @Composable
                        fun RouteCodeSample(code: String, onDownload: (String, String) -> Unit) {
                            // Supply onDownload(value, filename) using your platform's file-save API.
                            var wrap by remember { mutableStateOf(false) }
                            CodeBlock {
                                CodeBlockHeader {
                                    CodeBlockTitle {
                                        Icon(Lucide.FileCode)
                                        Text(maxLines = 1, overflow = TextOverflow.Ellipsis) { "app/api/chat/route.ts" }
                                    }
                                    CodeBlockActions {
                                        Button(onClick = { wrap = !wrap }, variant = ButtonVariant.Ghost,
                                            size = ButtonSize.IconXs, label = "Wrap lines") { Icon(Lucide.TextWrap) }
                                        Button(onClick = { onDownload(code, "route.ts") }, variant = ButtonVariant.Ghost,
                                            size = ButtonSize.IconXs, label = "Download") { Icon(Lucide.Download) }
                                        CodeBlockCopyButton(routeCode)
                                    }
                                }
                                CodeBlockContent(wrap = wrap, lineNumbers = true, textStyle = codeFont) {
                                    routeLines.forEachIndexed { index, line ->
                                        CodeBlockLine(
                                            text = line,
                                            variant = if (index in 5..9) CodeBlockLineVariant.Highlighted
                                                else CodeBlockLineVariant.Default,
                                            lineNumber = index + 1,
                                        )
                                    }
                                }
                            }
                        }
                        """.trimIndent(),
                    preview = { CodeBlockDemoPreview() },
                ),
                ComponentExample(
                    title = "Basic",
                    minPreviewHeight = 0.dp,
                    descriptionMarkdown =
                        "Pass a string to `CodeBlockContent`. This Compose port renders it as individual code lines.",
                    code =
                        """
                        CodeBlock {
                            CodeBlockContent(output, textStyle = TextStyle(fontFamily = catalogMonoFontFamily()))
                        }
                        """.trimIndent(),
                    preview = { CodeBlockBasicPreview() },
                ),
                ComponentExample(
                    title = "Header",
                    minPreviewHeight = 0.dp,
                    descriptionMarkdown =
                        "`CodeBlockHeader` holds a `CodeBlockTitle` and `CodeBlockActions`. " +
                            "`CodeBlockCopyButton` copies its `value`. A long title truncates. To link the title " +
                            "to a file, wrap `CodeBlockTitle` in an anchor.",
                    code =
                        """
                        CodeBlock {
                            CodeBlockHeader {
                                CodeBlockTitle {
                                    Icon(Lucide.Terminal)
                                    Text { "Terminal" }
                                }
                                CodeBlockActions { CodeBlockCopyButton(value = command) }
                            }
                            CodeBlockContent(output)
                        }
                        """.trimIndent(),
                    preview = { CodeBlockHeaderPreview() },
                ),
                ComponentExample(
                    title = "Actions",
                    minPreviewHeight = 0.dp,
                    descriptionMarkdown =
                        "`CodeBlockActions` holds any number of buttons. For a wrap toggle, keep the state " +
                            "yourself and pass it to `wrap`. Supply a platform file-save callback as `onDownload` " +
                            "for the download action; this catalog supports Web and Desktop.",
                    code =
                        """
                        @Composable
                        fun OutputCodeSample(output: String, onDownload: (String, String) -> Unit) {
                            var wrap by remember { mutableStateOf(true) }
                            CodeBlock {
                                CodeBlockHeader {
                                    CodeBlockTitle { Icon(Lucide.Terminal); Text { "Terminal" } }
                                    CodeBlockActions {
                                        Button(onClick = { wrap = !wrap }, variant = ButtonVariant.Ghost,
                                            size = ButtonSize.IconXs, label = "Wrap lines") { Icon(Lucide.TextWrap) }
                                        Button(onClick = { onDownload(output, "lint.log") }, variant = ButtonVariant.Ghost,
                                            size = ButtonSize.IconXs, label = "Download") { Icon(Lucide.Download) }
                                        CodeBlockCopyButton(output)
                                    }
                                }
                                CodeBlockContent(output, wrap = wrap)
                            }
                        }
                        """.trimIndent(),
                    preview = { CodeBlockActionsPreview() },
                ),
                ComponentExample(
                    title = "Floating Actions",
                    minPreviewHeight = 0.dp,
                    descriptionMarkdown =
                        "Without a header, put `CodeBlockActions` directly inside `CodeBlock`. The actions " +
                            "float at the top end and stay hidden until the block is hovered or focused. They " +
                            "always show on touch screens. In Compose, give each action its floating surface " +
                            "and ring through its modifier, as shown below.",
                    code =
                        """
                        CodeBlock {
                            CodeBlockActions {
                                val theme = currentTheme
                                val shape = RoundedCornerShape(minOf(twRadius.md, 10.dp))
                                CodeBlockCopyButton(output, modifier = Modifier
                                    .dropShadow(shape, Shadow(radius = 0.dp, spread = 1.dp,
                                        color = theme.colors.foreground.copy(alpha = .1f)))
                                    .background(theme.colors.background, shape))
                            }
                            CodeBlockContent(output)
                        }
                        """.trimIndent(),
                    preview = { CodeBlockFloatingActionsPreview() },
                ),
                ComponentExample(
                    title = "Line Numbers",
                    minPreviewHeight = 0.dp,
                    descriptionMarkdown =
                        "Add `lineNumbers` to `CodeBlockContent`. A string child is split into lines, and the " +
                            "numbers are not copied with a selection.",
                    code =
                        """
                        CodeBlock {
                            CodeBlockContent(code, lineNumbers = true)
                        }
                        """.trimIndent(),
                    preview = { CodeBlockLineNumbersPreview() },
                ),
                ComponentExample(
                    title = "Start Line",
                    minPreviewHeight = 0.dp,
                    descriptionMarkdown =
                        "Set `startLine` to number lines from a given line, for a snippet from the middle of a " +
                            "file. The gutter grows to fit the widest number.",
                    code =
                        """
                        CodeBlock {
                            CodeBlockHeader { CodeBlockTitle("app/dashboard/page.tsx") }
                            CodeBlockContent(code, lineNumbers = true, startLine = 995)
                        }
                        """.trimIndent(),
                    preview = { CodeBlockStartLinePreview() },
                ),
                ComponentExample(
                    title = "Wrap",
                    minPreviewHeight = 0.dp,
                    descriptionMarkdown =
                        "Long lines scroll by default. Add `wrap` to wrap them, which suits tool output and " +
                            "error messages. To let the reader toggle it, see [Actions](#actions).",
                    code =
                        """
                        CodeBlock {
                            CodeBlockContent(output)
                        }
                        CodeBlock {
                            CodeBlockContent(output, wrap = true)
                        }
                        """.trimIndent(),
                    preview = { CodeBlockWrapPreview() },
                ),
                ComponentExample(
                    title = "Highlighted Lines",
                    minPreviewHeight = 0.dp,
                    descriptionMarkdown =
                        "Render each line as a `CodeBlockLine` and set `variant = CodeBlockLineVariant.Highlighted` " +
                            "on the lines to call out.",
                    code =
                        """
                        CodeBlock {
                            CodeBlockContent(lineNumbers = true) {
                                lines.forEachIndexed { index, line ->
                                    CodeBlockLine(
                                        text = line,
                                        variant = if (index in 2..3) CodeBlockLineVariant.Highlighted
                                            else CodeBlockLineVariant.Default,
                                        lineNumber = index + 1,
                                    )
                                }
                            }
                        }
                        """.trimIndent(),
                    preview = { CodeBlockHighlightedLinesPreview() },
                ),
                ComponentExample(
                    title = "Diff",
                    minPreviewHeight = 0.dp,
                    descriptionMarkdown =
                        "Set `variant = CodeBlockLineVariant.Added` or `CodeBlockLineVariant.Removed` on a " +
                            "`CodeBlockLine`. Each one adds a `+` or `−` marker.",
                    code =
                        """
                        CodeBlock {
                            CodeBlockHeader {
                                CodeBlockTitle("components/greeting.tsx")
                                CodeBlockActions { CodeBlockCopyButton(patch) }
                            }
                            CodeBlockContent {
                                patch.split("\n").forEachIndexed { index, line ->
                                    CodeBlockLine(
                                        text = line.drop(1),
                                        variant = when (line.firstOrNull()) {
                                            '+' -> CodeBlockLineVariant.Added
                                            '-' -> CodeBlockLineVariant.Removed
                                            else -> CodeBlockLineVariant.Default
                                        },
                                        lineNumber = index + 1,
                                    )
                                }
                            }
                        }
                        """.trimIndent(),
                    preview = { CodeBlockDiffPreview() },
                ),
                ComponentExample(
                    title = "Height",
                    minPreviewHeight = 0.dp,
                    descriptionMarkdown =
                        "Cap the height with `maxHeight` on `CodeBlockContent`. The content scrolls inside the " +
                            "block. `CodeBlockContent` fades its edges as it scrolls: left and right by " +
                            "default, top and bottom when the block is capped.",
                    code =
                        """
                        CodeBlock {
                            CodeBlockHeader {
                                CodeBlockTitle("Logs")
                                CodeBlockActions { CodeBlockCopyButton(logs) }
                            }
                            CodeBlockContent(code = logs, lineNumbers = true, maxHeight = 192.dp)
                        }
                        """.trimIndent(),
                    preview = { CodeBlockHeightPreview() },
                ),
                ComponentExample(
                    title = "Show More",
                    minPreviewHeight = 0.dp,
                    descriptionMarkdown =
                        "`CodeBlockFooter` is a slot below the content. For Show More, cap the content while it " +
                            "is collapsed and put a ghost `xs` `Button` in the footer.",
                    code =
                        """
                        var expanded by remember { mutableStateOf(false) }
                        CodeBlock {
                            CodeBlockHeader {
                                CodeBlockTitle("components/chat.tsx")
                                CodeBlockActions { CodeBlockCopyButton(code) }
                            }
                            CodeBlockContent(
                                code = code,
                                lineNumbers = true,
                                maxHeight = if (expanded) null else 256.dp,
                                scrollable = false,
                            )
                            CodeBlockFooter {
                                Box(Modifier.fillMaxWidth(), contentAlignment = Alignment.Center) {
                                    Button(
                                        onClick = { expanded = !expanded },
                                        variant = ButtonVariant.Ghost,
                                        size = ButtonSize.Xs,
                                    ) {
                                        Icon(Lucide.ChevronDown)
                                        Text { if (expanded) "Show less" else "Show more" }
                                    }
                                }
                            }
                        }
                        """.trimIndent(),
                    preview = { CodeBlockShowMorePreview() },
                ),
                ComponentExample(
                    title = "Max Height",
                    headingLevel = 3,
                    minPreviewHeight = 0.dp,
                    descriptionMarkdown =
                        "To keep a long block from taking over the page, raise the cap instead of removing it. " +
                            "The expanded content scrolls, and the edges fade as you scroll.",
                    code =
                        """
                        var expanded by remember { mutableStateOf(false) }
                        CodeBlock {
                            CodeBlockContent(
                                code = code,
                                lineNumbers = true,
                                maxHeight = if (expanded) 384.dp else 256.dp,
                                scrollable = expanded,
                            )
                            CodeBlockFooter {
                                Button(onClick = { expanded = !expanded }, variant = ButtonVariant.Ghost,
                                    size = ButtonSize.Xs) { Text { if (expanded) "Show less" else "Show more" } }
                            }
                        }
                        """.trimIndent(),
                    preview = { CodeBlockMaxHeightPreview() },
                ),
                ComponentExample(
                    title = "Size",
                    minPreviewHeight = 0.dp,
                    descriptionMarkdown =
                        "Use `size = CodeBlockSize.Sm` for tool and [Trace](#trace) output. It has tighter " +
                            "padding and smaller code text.",
                    code =
                        """
                        CodeBlock {
                            CodeBlockHeader { CodeBlockTitle("Terminal") }
                            CodeBlockContent(output, wrap = true)
                        }
                        CodeBlock(size = CodeBlockSize.Sm) {
                            CodeBlockHeader { CodeBlockTitle("Terminal") }
                            CodeBlockContent(output, wrap = true)
                        }
                        """.trimIndent(),
                    preview = { CodeBlockSizePreview() },
                ),
                ComponentExample(
                    title = "Destructive",
                    minPreviewHeight = 0.dp,
                    descriptionMarkdown =
                        "Use `variant = CodeBlockVariant.Destructive` for errors and failed output.",
                    code =
                        """
                        CodeBlock(variant = CodeBlockVariant.Destructive) {
                            CodeBlockHeader {
                                CodeBlockTitle("Error")
                                CodeBlockActions { CodeBlockCopyButton(error) }
                            }
                            CodeBlockContent(error, wrap = true)
                        }
                        """.trimIndent(),
                    preview = { CodeBlockDestructivePreview() },
                ),
                ComponentExample(
                    title = "Trace",
                    minPreviewHeight = 0.dp,
                    descriptionMarkdown =
                        "Put a `CodeBlock` with `size = CodeBlockSize.Sm` in `TraceCollapsibleContent` for " +
                            "tool and terminal output. Add `wrap` and a `maxHeight` so long output wraps and " +
                            "scrolls inside the trace. The block sets its own text color, so " +
                            "`TraceCollapsibleContent` does not mute it.",
                    code =
                        """
                        Trace {
                            TraceCollapsible(defaultOpen = true) {
                                TraceCollapsibleTrigger {
                                    TraceLine {
                                        TraceIcon { Icon(Lucide.Terminal) }
                                        TraceTitle("Ran node --version")
                                    }
                                }
                                TraceCollapsibleContent {
                                    CodeBlock(size = CodeBlockSize.Sm) {
                                        CodeBlockContent(code = "$D node --version\nv24.18.0", wrap = true)
                                    }
                                }
                            }
                        }
                        """.trimIndent(),
                    preview = { CodeBlockTracePreview() },
                ),
                ComponentExample(
                    title = "Tabs",
                    minPreviewHeight = 0.dp,
                    descriptionMarkdown =
                        "Show several files with [Tabs](#tabs), one `CodeBlock` in each `TabsContent`.",
                    code =
                        """
                        Tabs(defaultValue = "route") {
                            TabsList {
                                files.forEach { TabsTrigger(it.id, it.title.split("/").last()) }
                            }
                            files.forEach { file ->
                                TabsContent(file.id) {
                                    CodeBlock {
                                        CodeBlockHeader {
                                            CodeBlockTitle(file.title)
                                            CodeBlockActions { CodeBlockCopyButton(file.code) }
                                        }
                                        CodeBlockContent(file.code, lineNumbers = true)
                                    }
                                }
                            }
                        }
                        """.trimIndent(),
                    preview = { CodeBlockTabsPreview() },
                ),
                ComponentExample(
                    title = "Message",
                    minPreviewHeight = 0.dp,
                    descriptionMarkdown = "Put a `CodeBlock` between paragraphs inside a [Message](#message).",
                    code =
                        """
                        Column(Modifier.widthIn(max = 448.dp), verticalArrangement = Arrangement.spacedBy(16.dp)) {
                            Message(align = MessageAlign.End) {
                                Bubble("How do I stream a response?", variant = BubbleVariant.Muted,
                                    align = MessageAlign.End)
                            }
                            Message {
                                Text { "Create a route handler that returns the stream:" }
                                CodeBlock {
                                    CodeBlockHeader {
                                        CodeBlockTitle("app/api/chat/route.ts")
                                        CodeBlockActions { CodeBlockCopyButton(code) }
                                    }
                                    CodeBlockContent(code)
                                }
                                Text { "The client reads it with useChat." }
                            }
                        }
                        """.trimIndent(),
                    preview = { CodeBlockMessagePreview() },
                ),
                ComponentExample(
                    title = "Typeset",
                    minPreviewHeight = 0.dp,
                    descriptionMarkdown =
                        "A `CodeBlock` inside a `typeset` container keeps its own styles and gets the same " +
                            "space above as a paragraph. This catalog has no `typeset` utility; the preview uses " +
                            "heading and body text to show the same rhythm.",
                    code =
                        """
                        // The catalog has no `typeset` utility; heading and body text stand in for it.
                        Column(Modifier.widthIn(max = 448.dp), verticalArrangement = Arrangement.spacedBy(20.dp)) {
                            Text(textStyle = headingStyle) { "Stream a response" }
                            Paragraph("Create a route handler that calls streamText and returns the stream.")
                            CodeBlock {
                                CodeBlockHeader {
                                    CodeBlockTitle("app/api/chat/route.ts")
                                    CodeBlockActions { CodeBlockCopyButton(code) }
                                }
                                CodeBlockContent(code, lineNumbers = true)
                            }
                            Paragraph("The useChat hook renders the answer while it is written.")
                        }
                        """.trimIndent(),
                    preview = { CodeBlockTypesetPreview() },
                ),
                ComponentExample(
                    title = "RTL",
                    minPreviewHeight = 0.dp,
                    previewPadding = 0.dp,
                    descriptionMarkdown =
                        "To enable RTL support in shadcn/ui, see the " +
                            "[RTL configuration guide](https://ui.shadcn.com/docs/rtl). Code always reads left " +
                            "to right. The header follows the page direction.",
                    code =
                        """
                        var language by remember { mutableStateOf(0) }
                        RtlPreview(language, listOf("Arabic (العربية)", "English", "Hebrew (עברית)"), { language = it }) {
                            CompositionLocalProvider(
                                LocalLayoutDirection provides if (language == 1) LayoutDirection.Ltr else LayoutDirection.Rtl,
                            ) {
                                Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
                                    Text { copy.description }
                                    CodeBlock {
                                        CodeBlockHeader {
                                            CodeBlockTitle {
                                                Icon(Lucide.Terminal)
                                                Text(maxLines = 1, overflow = TextOverflow.Ellipsis) { copy.title }
                                            }
                                            CodeBlockActions { CodeBlockCopyButton(code) }
                                        }
                                        CodeBlockContent(code, lineNumbers = true)
                                    }
                                }
                            }
                        }
                        """.trimIndent(),
                    preview = { CodeBlockRtlPreview() },
                ),
                ComponentExample(
                    title = "Syntax Highlighting",
                    minPreviewHeight = 0.dp,
                    descriptionMarkdown =
                        "`useHighlightedCode` highlights with [Shiki](https://shiki.style) and returns one line per " +
                            "`CodeBlockLine`. Syntax highlighting is out of scope in this Compose port: " +
                            "`CodeBlockContent` renders plain text, and no highlighter is bundled. The official " +
                            "server rendering, streaming and Markdown recipes depend on the same tokenizer and on " +
                            "Streamdown, so they are not reproduced here.",
                    code =
                        """
                        // Syntax highlighting is out of scope: plain text renders without token colors.
                        CodeBlock {
                            CodeBlockContent(code)
                        }
                        """.trimIndent(),
                    preview = { CodeBlockHighlightingPreview() },
                ),
                ComponentExample(
                    title = "Languages",
                    headingLevel = 3,
                    minPreviewHeight = 0.dp,
                    descriptionMarkdown =
                        "Each language loads the first time it is used. A language that is not in the list renders " +
                            "as plain text. The list ships with bash, css, diff, html, javascript, json, jsx, " +
                            "markdown, python, sql, tsx, typescript and yaml, plus aliases such as ts, js, sh and " +
                            "yml. Language loading is out of scope in this Compose port; every language " +
                            "renders as plain text.",
                    code =
                        """
                        samples.forEach { sample ->
                            CodeBlock {
                                CodeBlockHeader { CodeBlockTitle(sample.title) }
                                CodeBlockContent(sample.code)
                            }
                        }
                        """.trimIndent(),
                    preview = { CodeBlockLanguagesPreview() },
                ),
                ComponentExample(
                    title = "Terminal Output",
                    headingLevel = 3,
                    minPreviewHeight = 0.dp,
                    descriptionMarkdown =
                        "Pass `\"ansi\"` as the language to render the colors in terminal output. Without it, escape " +
                            "codes show as text. ANSI colors do not carry from one line to the next. ANSI " +
                            "interpretation is out of scope in this Compose port, so escape codes are not rendered.",
                    code =
                        """
                        // ANSI interpretation is out of scope: rendered as plain terminal text.
                        CodeBlock {
                            CodeBlockHeader { CodeBlockTitle("Terminal") }
                            CodeBlockContent(output, wrap = true)
                        }
                        """.trimIndent(),
                    preview = { CodeBlockAnsiPreview() },
                ),
            ),
        afterExamples =
            listOf(
                ComponentGuideSection(
                    title = "Accessibility",
                    blocks =
                        listOf(
                            ComponentGuideBlock.Bullets(
                                listOf(
                                    "`CodeBlockContent` always uses left-to-right layout, so code reads left to " +
                                        "right on an RTL page.",
                                    "`CodeBlockContent` is focusable so keyboard users can scroll it.",
                                    "Floating actions show when focus is inside the block, not only on hover, " +
                                        "so keyboard users can reach them.",
                                    "Line numbers are hidden from screen readers.",
                                    "Diff lines carry a `+` or `−` marker as well as a tint, so the change does " +
                                        "not rely on color.",
                                    "`CodeBlockCopyButton` is labelled \"Copy\", and a polite live region " +
                                        "announces \"Copied\" after a successful copy.",
                                    "Give a wrap toggle `aria-pressed` so its state is announced.",
                                    "Give a Show More button `aria-expanded` and point `aria-controls` at the " +
                                        "content.",
                                ),
                            ),
                        ),
                ),
            ),
        apiSections =
            listOf(
                ComponentApiSection(
                    title = "CodeBlock",
                    descriptionMarkdown = "The surface that holds the header, the content and the footer.",
                    properties =
                        listOf(
                            ApiProperty("variant", "CodeBlockVariant", "Default"),
                            ApiProperty("size", "CodeBlockSize", "Default"),
                            ApiProperty("style", "Style", "Style"),
                            ApiProperty("modifier", "Modifier", "Modifier"),
                        ),
                ),
                ComponentApiSection(
                    title = "CodeBlockHeader",
                    descriptionMarkdown = "A row above the content for a title and actions.",
                    properties = listOf(ApiProperty("modifier", "Modifier", "Modifier")),
                ),
                ComponentApiSection(
                    title = "CodeBlockTitle",
                    descriptionMarkdown = "A filename or a label, with an optional icon. It truncates.",
                    properties =
                        listOf(
                            ApiProperty("text", "String", "-"),
                            ApiProperty("modifier", "Modifier", "Modifier"),
                        ),
                ),
                ComponentApiSection(
                    title = "CodeBlockActions",
                    descriptionMarkdown =
                        "Holds buttons at the end of the header. As a direct child of `CodeBlock`, it floats " +
                            "at the top end and reveals on hover or focus.",
                    properties = listOf(ApiProperty("modifier", "Modifier", "Modifier")),
                ),
                ComponentApiSection(
                    title = "CodeBlockCopyButton",
                    descriptionMarkdown =
                        "A ghost icon button that copies `value` to the clipboard and confirms for two seconds.",
                    properties =
                        listOf(
                            ApiProperty("value", "String", "-"),
                            ApiProperty("variant", "ButtonVariant", "Ghost"),
                            ApiProperty("size", "ButtonSize", "IconXs"),
                            ApiProperty("copyLabel", "String", "\"Copy\""),
                            ApiProperty("copiedLabel", "String", "\"Copied\""),
                        ),
                ),
                ComponentApiSection(
                    title = "CodeBlockContent",
                    descriptionMarkdown = "The content block that scrolls vertically or horizontally.",
                    properties =
                        listOf(
                            ApiProperty("code", "String", "-"),
                            ApiProperty("wrap", "Boolean", "false"),
                            ApiProperty("lineNumbers", "Boolean", "false"),
                            ApiProperty("startLine", "Int", "1"),
                            ApiProperty("maxHeight", "Dp?", "null"),
                            ApiProperty("scrollable", "Boolean", "true"),
                            ApiProperty("textStyle", "TextStyle", "TextStyle.Default"),
                        ),
                ),
                ComponentApiSection(
                    title = "CodeBlockLine",
                    descriptionMarkdown = "One line of code.",
                    properties =
                        listOf(
                            ApiProperty("text", "String", "-"),
                            ApiProperty("variant", "CodeBlockLineVariant", "Default"),
                            ApiProperty("lineNumber", "Int?", "null"),
                        ),
                ),
                ComponentApiSection(
                    title = "CodeBlockFooter",
                    descriptionMarkdown = "A row below the content, such as for a Show More button.",
                    properties = listOf(ApiProperty("modifier", "Modifier", "Modifier")),
                ),
                ComponentApiSection(
                    title = "useHighlightedCode",
                    descriptionMarkdown =
                        "Not provided: syntax highlighting is out of scope in this Compose port. " +
                            "`CodeBlockContent` renders plain text.",
                    properties =
                        listOf(
                            ApiProperty("code", "String", "-"),
                            ApiProperty("language", "String", "-"),
                        ),
                ),
            ),
    )
