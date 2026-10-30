package com.toutiaoyan.compose.ui.catalog.docs

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import com.composables.icons.lucide.Lucide
import com.composables.icons.lucide.TextWrap
import com.toutiaoyan.compose.ui.components.icon.Icon
import com.toutiaoyan.compose.ui.components.button.Button
import com.toutiaoyan.compose.ui.components.button.ButtonSize
import com.toutiaoyan.compose.ui.components.button.ButtonVariant
import androidx.compose.foundation.layout.widthIn
import com.toutiaoyan.compose.ui.catalog.catalogMonoFontFamily
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.toutiaoyan.compose.ui.components.codeblock.*
import com.toutiaoyan.compose.ui.components.codeblock.CodeBlock as CodeBlockPanel
import com.toutiaoyan.compose.ui.components.text.Text

private val chatCode =
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
val codeBlockDoc =
    ComponentDoc(
        id = "code-block",
        title = "Code Block",
        description = (
            "Displays code, terminal output and diffs with an optional " +
                "header, line numbers and a copy button."
        ),
        usageCode =
            """
            import com.toutiaoyan.compose.ui.components.codeblock.*

            CodeBlock {
                CodeBlockHeader {
                    CodeBlockTitle("terminal")
                    CodeBlockActions { CodeBlockCopyButton(value = code) }
                }
                CodeBlockContent(code, lineNumbers = true)
            }
            """.trimIndent(),
        apiProperties =
            listOf(
                ApiProperty("variant", "CodeBlockVariant", "Default"),
                ApiProperty("size", "CodeBlockSize", "Default"),
                ApiProperty("wrap", "Boolean", "false"),
                ApiProperty("lineNumbers", "Boolean", "false"),
                ApiProperty("startLine", "Int", "1"),
            ),
        examples =
            listOf(
                ComponentExample(
                    "Default",
                    """
                    var wrap by remember { mutableStateOf(false) }
                    CodeBlock {
                        CodeBlockHeader {
                            CodeBlockTitle("app/api/chat/route.ts")
                            CodeBlockActions {
                                Button(onClick = { wrap = !wrap }, variant = ButtonVariant.Ghost,
                                    size = ButtonSize.IconXs, label = "Wrap lines") { Icon(Lucide.TextWrap) }
                                CodeBlockCopyButton(code)
                            }
                        }
                        CodeBlockContent(code, wrap = wrap, lineNumbers = true)
                    }
                    """.trimIndent(),
                ) {
                    var wrap by remember { mutableStateOf(false) }
                    CodeBlockPanel(Modifier.widthIn(max = 576.dp)) {
                        CodeBlockHeader {
                            CodeBlockTitle("app/api/chat/route.ts")
                            CodeBlockActions {
                                Button(onClick = {
                                    wrap = !wrap
                                }, variant = ButtonVariant.Ghost, size = ButtonSize.IconXs, label = "Wrap lines") {
                                    Icon(
                                        Lucide.TextWrap,
                                    )
                                }
                                CodeBlockCopyButton(chatCode)
                            }
                        }
                        CodeBlockContent(
                            chatCode,
                            wrap = wrap,
                            lineNumbers = true,
                            textStyle = TextStyle(fontFamily = catalogMonoFontFamily()),
                        )
                    }
                },
                ComponentExample(
                    "Line Numbers",
                    (
                        "CodeBlock { CodeBlockContent(code, lineNumbers = true, " +
                            "startLine = 10, textStyle = TextStyle(fontFamily = " +
                            "catalogMonoFontFamily())) }"
                    ),
                ) {
                    CodeBlockPanel(Modifier.widthIn(max = 448.dp)) {
                        CodeBlockContent(
                            "fun main() {\n\n    println(\"Hello, world!\")\n}",
                            lineNumbers = true,
                            startLine = 10,
                            textStyle = TextStyle(fontFamily = catalogMonoFontFamily()),
                        )
                    }
                },
                ComponentExample(
                    "Diff",
                    """
                    CodeBlock {
                        CodeBlockContent(lineNumbers = true, textStyle = TextStyle(fontFamily = catalogMonoFontFamily())) {
                            CodeBlockLine("val count = 1", variant = CodeBlockLineVariant.Removed, lineNumber = 1)
                            CodeBlockLine("val count = 2", variant = CodeBlockLineVariant.Added, lineNumber = 2)
                        }
                    }
                    """.trimIndent(),
                ) {
                    CodeBlockPanel(Modifier.widthIn(max = 448.dp)) {
                        CodeBlockContent(
                            lineNumbers = true,
                            textStyle = TextStyle(fontFamily = catalogMonoFontFamily()),
                        ) {
                            CodeBlockLine("val count = 1", variant = CodeBlockLineVariant.Removed, lineNumber = 1)
                            CodeBlockLine("val count = 2", variant = CodeBlockLineVariant.Added, lineNumber = 2)
                            CodeBlockLine("println(count)", variant = CodeBlockLineVariant.Highlighted, lineNumber = 3)
                        }
                        CodeBlockFooter { Text { "3 lines changed" } }
                    }
                },
                ComponentExample("Wrap", "CodeBlock { CodeBlockContent(longCode, wrap = true) }") {
                    CodeBlockPanel(Modifier.widthIn(max = 320.dp), size = CodeBlockSize.Sm) {
                        CodeBlockContent(
                            (
                                "val message = \"Long lines wrap inside the available width " +
                                    "instead of overflowing the code block.\""
                            ),
                            wrap = true,
                            textStyle = TextStyle(fontFamily = catalogMonoFontFamily()),
                        )
                    }
                },
                ComponentExample(
                    "Destructive",
                    "CodeBlock(variant = CodeBlockVariant.Destructive) { CodeBlockContent(error) }",
                ) {
                    CodeBlockPanel(Modifier.widthIn(max = 448.dp), variant = CodeBlockVariant.Destructive) {
                        CodeBlockContent(
                            "Error: Unable to resolve dependency",
                            textStyle = TextStyle(fontFamily = catalogMonoFontFamily()),
                        )
                    }
                },
            ),
    )
