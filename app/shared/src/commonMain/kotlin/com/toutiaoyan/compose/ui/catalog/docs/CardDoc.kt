@file:OptIn(androidx.compose.foundation.style.ExperimentalFoundationStyleApi::class)

package com.toutiaoyan.compose.ui.catalog.docs

import androidx.compose.ui.unit.dp

val cardDoc =
    ComponentDoc(
        id = "card",
        title = "Card",
        description = "Displays a card with header, content, and footer.",
        usageImportsCode = "import com.toutiaoyan.compose.ui.components.card.*",
        usageCode =
            """
            Card {
                CardHeader {
                    CardTitle("Card Title")
                    CardDescription("Card Description")
                    CardAction { Text { "Card Action" } }
                }
                CardContent { Text { "Card Content" } }
                CardFooter { Text { "Card Footer" } }
            }
            """.trimIndent(),
        compositionCode =
            """
            Card
            ├── CardHeader
            │   ├── CardTitle
            │   ├── CardDescription
            │   └── CardAction
            ├── CardContent
            └── CardFooter
            """.trimIndent(),
        examplesHeading = null,
        apiSections =
            listOf(
                ComponentApiSection(
                    "Card",
                    "Root container. Nova uses vertical inset and a shared spacing token; Header/Content " +
                        "own horizontal inset, and Footer owns its padding. A first CardImage removes the " +
                        "default top inset; a Footer removes the default bottom inset. Explicit caller Style " +
                        "padding wins. Filled and Elevated are Kotlin extensions.",
                    listOf(
                        ApiProperty("modifier", "Modifier", "Modifier"),
                        ApiProperty("variant", "CardVariant", "Default"),
                        ApiProperty("size", "CardSize", "Default"),
                        ApiProperty("spacing", "Dp?", "null (16.dp / 12.dp for Sm)"),
                        ApiProperty("style", "Style", "Style"),
                        ApiProperty("content", "@Composable ColumnScope.() -> Unit", "Required"),
                    ),
                ),
                ComponentApiSection(
                    "CardHeader",
                    "Used for a title, description, and optional action. Nova places the action at the " +
                        "logical end (top-right in LTR); modifier and Style customize the section.",
                    listOf(
                        ApiProperty("modifier", "Modifier", "Modifier"),
                        ApiProperty("style", "Style", "Style"),
                        ApiProperty("content", "@Composable ColumnScope.() -> Unit", "Required"),
                        ApiProperty("title / description", "String / String?", "Convenience overload"),
                    ),
                ),
                ComponentApiSection(
                    "CardTitle",
                    "Card title with heading semantics. String and annotated-string overloads share the " +
                        "current card size.",
                    listOf(
                        ApiProperty("text", "String or () -> String/AnnotatedString", "Required"),
                        ApiProperty("modifier", "Modifier", "Modifier"),
                        ApiProperty("style", "Style", "Style"),
                    ),
                ),
                ComponentApiSection(
                    "CardDescription",
                    "Used for helper text under the title.",
                    listOf(
                        ApiProperty("text", "String or () -> String/AnnotatedString", "Required"),
                        ApiProperty("modifier", "Modifier", "Modifier"),
                        ApiProperty("style", "Style", "Style"),
                    ),
                ),
                ComponentApiSection(
                    "CardAction",
                    "Places content in the top-right of the header (logical end in RTL), such as a button " +
                        "or badge.",
                    listOf(
                        ApiProperty("modifier", "Modifier", "Modifier"),
                        ApiProperty("content", "@Composable () -> Unit", "Required"),
                    ),
                ),
                ComponentApiSection(
                    "CardContent",
                    "Used for the main card body. Override horizontal padding with Style for edge-to-edge " +
                        "content.",
                    listOf(
                        ApiProperty("modifier", "Modifier", "Modifier"),
                        ApiProperty("style", "Style", "Style"),
                        ApiProperty("content", "@Composable ColumnScope.() -> Unit", "Required"),
                        ApiProperty("text", "String", "Convenience overload"),
                    ),
                ),
                ComponentApiSection(
                    "CardFooter",
                    "Actions and secondary content at the bottom. The footer keeps its full measured " +
                        "bounds; the card root removes its default bottom inset.",
                    listOf(
                        ApiProperty("modifier", "Modifier", "Modifier"),
                        ApiProperty("style", "Style", "Style"),
                        ApiProperty("content", "@Composable RowScope.() -> Unit", "Required"),
                    ),
                ),
                ComponentApiSection(
                    "CardImage (Kotlin extension)",
                    "Kotlin-only image region corresponding to a leading HTML image. Supply the painter, " +
                        "description, aspect ratio, and overlays in the slot; a first image removes the " +
                        "root top inset.",
                    listOf(
                        ApiProperty("modifier", "Modifier", "Modifier"),
                        ApiProperty("content", "@Composable BoxScope.() -> Unit", "Required"),
                    ),
                ),
            ),
        examples =
            listOf(
                cardExample(0, "Default", 480),
                cardExample(
                    1,
                    "Size",
                    384,
                    "Use the `size = CardSize.Sm` parameter to set the size of the card to small. " +
                        "The small size variant uses smaller spacing.",
                ),
                cardExample(
                    2,
                    "Spacing",
                    544,
                    "In addition to the `size` parameter, you can use the `spacing` parameter to control " +
                        "the spacing between sections and the inset of card parts.",
                ),
                cardExample(
                    3,
                    "Kotlin extension: Edge to Edge",
                    384,
                    "Use `contentPadding(0.dp)` on `CardContent` to make content go edge to edge while " +
                        "keeping it aligned with the card inset. When the edge-to-edge content sits above a " +
                        "footer, reclaim the section gap with the layout modifier shown below.",
                ),
                cardExample(4, "Image", 512, "Add an image before the card header to create a card with an image."),
                cardExample(
                    5,
                    "RTL",
                    544,
                    "To enable RTL support in shadcn/ui, see the " +
                        "[RTL configuration guide](https://ui.shadcn.com/docs/rtl).",
                ),
            ),
    )

private fun cardExample(
    index: Int,
    title: String,
    height: Int,
    description: String? = null,
) = ComponentExample(
    title = title,
    code = cardRecipeCode(index),
    previewHeight = height.dp,
    minPreviewHeight = height.dp,
    previewPadding = if (index == 5) 0.dp else 40.dp,
    descriptionMarkdown = description,
    showHeading = index != 3,
    preview = { CardRecipe(index) },
)
