@file:OptIn(androidx.compose.foundation.style.ExperimentalFoundationStyleApi::class)

package com.toutiaoyan.compose.ui.catalog.docs

import androidx.compose.foundation.text.BasicText
import androidx.compose.foundation.text.InlineTextContent
import androidx.compose.foundation.text.appendInlineContent
import androidx.compose.ui.draw.drawBehind
import androidx.compose.ui.geometry.CornerRadius
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.text.*
import androidx.compose.ui.text.style.LineHeightStyle
import com.toutiaoyan.compose.ui.catalog.catalogMonoFontFamily
import com.toutiaoyan.compose.ui.theme.currentTheme
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.wrapContentWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.style.Style
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.Layout
import androidx.compose.ui.unit.constrainHeight
import androidx.compose.ui.unit.Constraints
import androidx.compose.ui.unit.TextUnit
import androidx.compose.ui.platform.LocalLayoutDirection
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.LayoutDirection
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.composables.icons.lucide.Bold
import com.composables.icons.lucide.Italic
import com.composables.icons.lucide.Lucide
import com.composables.icons.lucide.Underline
import com.toutiaoyan.compose.ui.components.field.Field
import com.toutiaoyan.compose.ui.components.field.FieldLabel
import com.toutiaoyan.compose.ui.components.icon.Icon
import com.toutiaoyan.compose.ui.components.text.Text
import com.toutiaoyan.compose.ui.components.text.TextColors
import com.toutiaoyan.compose.ui.components.toggle.ToggleSize
import com.toutiaoyan.compose.ui.components.toggle.ToggleVariant
import com.toutiaoyan.compose.ui.components.togglegroup.ToggleGroup
import com.toutiaoyan.compose.ui.components.togglegroup.ToggleGroupDefaults
import com.toutiaoyan.compose.ui.components.togglegroup.ToggleGroupItem
import com.toutiaoyan.compose.ui.components.togglegroup.ToggleGroupOrientation
import com.toutiaoyan.compose.ui.components.togglegroup.ToggleGroupType

// Skia floors leading-none at natural font metrics. CSS keeps the declared line box and
// permits glyph overflow; measure unconstrained and center without clipping or scaling.
@Composable
private fun FontWeightLineBox(
    height: TextUnit,
    modifier: Modifier = Modifier,
    content: @Composable () -> Unit,
) {
    Layout(content = content, modifier = modifier) { measurables, constraints ->
        val text = measurables.single().measure(constraints.copy(minHeight = 0, maxHeight = Constraints.Infinity))
        val lineHeight = constraints.constrainHeight(height.roundToPx())
        layout(text.width, lineHeight) {
            text.placeRelative(0, (lineHeight - text.height) / 2)
        }
    }
}

@Composable
private fun FontWeightDescription(fontWeight: String) {
    val theme = currentTheme
    val density = LocalDensity.current
    val code = "font-$fontWeight"
    val proseStyle =
        theme.typography.bodyMedium.copy(
            fontSize = 14.sp,
            lineHeight = 21.sp,
            fontWeight = FontWeight.Normal,
            color = theme.colors.mutedForeground,
            lineHeightStyle = LineHeightStyle(LineHeightStyle.Alignment.Center, LineHeightStyle.Trim.None),
        )
    val codeStyle = proseStyle.copy(fontFamily = catalogMonoFontFamily())
    val measured = rememberTextMeasurer().measure(code, codeStyle, softWrap = false)
    val slotWidth = with(density) { (measured.size.width + 8.dp.toPx()).toSp() }
    BasicText(
        text =
            buildAnnotatedString {
                append("Use ")
                appendInlineContent("weight", code)
                append(" to set the font weight.")
            },
        modifier = Modifier.testTag("font-weight-description"),
        style = proseStyle,
        inlineContent =
            mapOf(
                "weight" to
                    InlineTextContent(
                        Placeholder(slotWidth, 21.sp, PlaceholderVerticalAlign.TextCenter),
                    ) {
                        FontWeightLineBox(21.sp, Modifier.testTag("font-weight-code")) {
                            BasicText(
                                code,
                                style = codeStyle,
                                softWrap = false,
                                modifier =
                                    Modifier.drawBehind {
                                        // CSS inline padding paints outside the unchanged 21px line box.
                                        drawRoundRect(
                                            color = theme.colors.muted,
                                            topLeft = Offset(0f, -2.dp.toPx()),
                                            size = Size(size.width, size.height + 4.dp.toPx()),
                                            cornerRadius = CornerRadius(8.dp.toPx()),
                                        )
                                    }.padding(horizontal = 4.dp),
                            )
                        }
                    },
            ),
    )
}

private val toggleGroupRtlLabels =
    listOf(
        listOf("List", "Grid", "Cards"),
        listOf("قائمة", "شبكة", "بطاقات"),
        listOf("רשימה", "רשת", "כרטיסים"),
    )

@Composable
private fun ToggleGroupIconDemo(
    value: Set<String>,
    onValueChange: (Set<String>) -> Unit,
    vertical: Boolean = false,
    size: ToggleSize = ToggleSize.Md,
    spacing: androidx.compose.ui.unit.Dp = ToggleGroupDefaults.Spacing,
    disabled: Boolean = false,
    variant: ToggleVariant = ToggleVariant.Outline,
    type: ToggleGroupType = ToggleGroupType.Multiple,
) {
    ToggleGroup(
        value = value,
        onValueChange = onValueChange,
        variant = variant,
        size = size,
        spacing = spacing,
        disabled = disabled,
        type = type,
        orientation = if (vertical) ToggleGroupOrientation.Vertical else ToggleGroupOrientation.Horizontal,
        modifier = Modifier.testTag("toggle-group-recipe"),
    ) {
        ToggleGroupItem("bold", label = "Toggle bold") { Icon(Lucide.Bold) }
        ToggleGroupItem("italic", label = "Toggle italic") { Icon(Lucide.Italic) }
        ToggleGroupItem("underline", label = if (vertical) "Toggle underline" else "Toggle strikethrough") {
            Icon(Lucide.Underline)
        }
    }
}

@Composable
private fun ToggleGroupTextDemo(
    values: List<String>,
    active: Set<String>,
    onValueChange: (Set<String>) -> Unit,
    size: ToggleSize = ToggleSize.Md,
    spacing: androidx.compose.ui.unit.Dp = ToggleGroupDefaults.Spacing,
) {
    ToggleGroup(
        value = active,
        type = ToggleGroupType.Single,
        onValueChange = onValueChange,
        variant = ToggleVariant.Outline,
        size = size,
        spacing = spacing,
        modifier = Modifier.testTag("toggle-group-recipe"),
    ) {
        values.forEach { value ->
            ToggleGroupItem(value.lowercase(), label = "Toggle ${value.lowercase()}") { Text { value } }
        }
    }
}

@Composable
private fun ToggleGroupFontWeightDemo() {
    var fontWeight by remember { mutableStateOf("normal") }
    Field {
        FieldLabel("Font Weight")
        ToggleGroup(
            value = fontWeight,
            onValueChange = { fontWeight = it },
            variant = ToggleVariant.Outline,
            spacing = 8.dp,
            size = ToggleSize.Lg,
            modifier = Modifier.testTag("toggle-group-font-weight"),
        ) {
            listOf(
                "light" to FontWeight.Light,
                "normal" to FontWeight.Normal,
                "medium" to FontWeight.Medium,
                "bold" to FontWeight.Bold,
            ).forEach { (value, weight) ->
                ToggleGroupItem(
                    value,
                    label = value.replaceFirstChar { it.uppercase() },
                    style =
                        Style {
                            width(64.dp)
                            height(64.dp)
                            shape(RoundedCornerShape(12.dp))
                        },
                ) {
                    // CSS whitespace-nowrap lets this stack use its natural width inside the fixed card.
                    Column(
                        modifier = Modifier.wrapContentWidth(unbounded = true),
                        horizontalAlignment = Alignment.CenterHorizontally,
                        verticalArrangement = Arrangement.spacedBy(4.dp, Alignment.CenterVertically),
                    ) {
                        FontWeightLineBox(24.sp, Modifier.testTag("font-sample-$value")) {
                            Text(
                                textStyle =
                                    androidx.compose.ui.text.TextStyle(
                                        fontSize = 24.sp,
                                        lineHeight = 24.sp,
                                        fontWeight = weight,
                                    ),
                            ) { "Aa" }
                        }
                        FontWeightLineBox(16.sp, Modifier.testTag("font-caption-$value")) {
                            Text(
                                textStyle =
                                    TextColors.Muted.copy(
                                        fontSize = 12.sp,
                                        lineHeight = 16.sp,
                                    ),
                            ) {
                                value.replaceFirstChar { it.uppercase() }
                            }
                        }
                    }
                }
            }
        }
        FontWeightDescription(fontWeight)
    }
}

private val toggleGroupDefaultCode =
    """
    var value by remember { mutableStateOf(emptySet<String>()) }
    ToggleGroup(type = ToggleGroupType.Multiple, value = value, onValueChange = { value = it }, variant = ToggleVariant.Outline) {
        ToggleGroupItem("bold", label = "Toggle bold") { Icon(Lucide.Bold) }
        ToggleGroupItem("italic", label = "Toggle italic") { Icon(Lucide.Italic) }
        ToggleGroupItem("underline", label = "Toggle strikethrough") { Icon(Lucide.Underline) }
    }
    """.trimIndent()

private val toggleGroupOutlineCode =
    """
    var value by remember { mutableStateOf(setOf("all")) }
    ToggleGroup(type = ToggleGroupType.Single, value = value, onValueChange = { value = it }, variant = ToggleVariant.Outline) {
        ToggleGroupItem("all") { Text { "All" } }
        ToggleGroupItem("missed") { Text { "Missed" } }
    }
    """.trimIndent()

private val toggleGroupSizesCode =
    """
    ToggleGroup(type = ToggleGroupType.Single, defaultValue = setOf("top"), variant = ToggleVariant.Outline, size = ToggleSize.Sm) {
        listOf("top", "bottom", "left", "right").forEach { value ->
            ToggleGroupItem(value) { Text { value.replaceFirstChar { it.uppercase() } } }
        }
    }
    ToggleGroup(type = ToggleGroupType.Single, defaultValue = setOf("top"), variant = ToggleVariant.Outline) {
        listOf("top", "bottom", "left", "right").forEach { value ->
            ToggleGroupItem(value) { Text { value.replaceFirstChar { it.uppercase() } } }
        }
    }
    """.trimIndent()

private val toggleGroupSpacingCode =
    """
    ToggleGroup(type = ToggleGroupType.Single, defaultValue = setOf("top"), variant = ToggleVariant.Outline, size = ToggleSize.Sm, spacing = 8.dp) {
        listOf("top", "bottom", "left", "right").forEach { value ->
            ToggleGroupItem(value) { Text { value.replaceFirstChar { it.uppercase() } } }
        }
    }
    """.trimIndent()

private val toggleGroupVerticalCode =
    """
    var value by remember { mutableStateOf(setOf("bold", "italic")) }
    ToggleGroup(
        value = value,
        onValueChange = { value = it },
        type = ToggleGroupType.Multiple,
        orientation = ToggleGroupOrientation.Vertical,
        spacing = 4.dp,
    ) {
        ToggleGroupItem("bold", label = "Toggle bold") { Icon(Lucide.Bold) }
        ToggleGroupItem("italic", label = "Toggle italic") { Icon(Lucide.Italic) }
        ToggleGroupItem("underline", label = "Toggle underline") { Icon(Lucide.Underline) }
    }
    """.trimIndent()

private val toggleGroupDisabledCode =
    """
    ToggleGroup(type = ToggleGroupType.Single, disabled = true, variant = ToggleVariant.Default) {
        ToggleGroupItem("bold", label = "Toggle bold") { Icon(Lucide.Bold) }
        ToggleGroupItem("italic", label = "Toggle italic") { Icon(Lucide.Italic) }
        ToggleGroupItem("underline", label = "Toggle strikethrough") { Icon(Lucide.Underline) }
    }
    """.trimIndent()

private val toggleGroupFontWeightCode =
    """
    @Composable
    private fun FontWeightLineBox(
        height: TextUnit,
        modifier: Modifier = Modifier,
        content: @Composable () -> Unit,
    ) {
        Layout(content = content, modifier = modifier) { measurables, constraints ->
            val text = measurables.single().measure(constraints.copy(minHeight = 0, maxHeight = Constraints.Infinity))
            val lineHeight = constraints.constrainHeight(height.roundToPx())
            layout(text.width, lineHeight) {
                text.placeRelative(0, (lineHeight - text.height) / 2)
            }
        }
    }

    @Composable
    private fun FontWeightDescription(fontWeight: String) {
        val theme = currentTheme
        val density = LocalDensity.current
        val code = "font-${'$'}fontWeight"
        val proseStyle = theme.typography.bodyMedium.copy(
            fontSize = 14.sp,
            lineHeight = 21.sp,
            fontWeight = FontWeight.Normal,
            color = theme.colors.mutedForeground,
            lineHeightStyle = LineHeightStyle(LineHeightStyle.Alignment.Center, LineHeightStyle.Trim.None),
        )
        val codeStyle = proseStyle.copy(fontFamily = catalogMonoFontFamily())
        val measured = rememberTextMeasurer().measure(code, codeStyle, softWrap = false)
        val slotWidth = with(density) { (measured.size.width + 8.dp.toPx()).toSp() }
        BasicText(
            text = buildAnnotatedString {
                append("Use ")
                appendInlineContent("weight", code)
                append(" to set the font weight.")
            },
            modifier = Modifier.testTag("font-weight-description"),
            style = proseStyle,
            inlineContent = mapOf(
                "weight" to InlineTextContent(
                    Placeholder(slotWidth, 21.sp, PlaceholderVerticalAlign.TextCenter),
                ) {
                    FontWeightLineBox(21.sp, Modifier.testTag("font-weight-code")) {
                        BasicText(
                            code,
                            style = codeStyle,
                            softWrap = false,
                            modifier = Modifier.drawBehind {
                                // CSS inline padding paints outside the unchanged 21px line box.
                                drawRoundRect(
                                    color = theme.colors.muted,
                                    topLeft = Offset(0f, -2.dp.toPx()),
                                    size = Size(size.width, size.height + 4.dp.toPx()),
                                    cornerRadius = CornerRadius(8.dp.toPx()),
                                )
                            }.padding(horizontal = 4.dp),
                        )
                    }
                },
            ),
        )
    }


    var fontWeight by remember { mutableStateOf("normal") }
    Field {
        FieldLabel("Font Weight")
        ToggleGroup(value = fontWeight, onValueChange = { fontWeight = it },
            variant = ToggleVariant.Outline, spacing = 8.dp, size = ToggleSize.Lg) {
            listOf("light" to FontWeight.Light, "normal" to FontWeight.Normal,
                "medium" to FontWeight.Medium, "bold" to FontWeight.Bold).forEach { (value, weight) ->
                ToggleGroupItem(value, label = value.replaceFirstChar { it.uppercase() },
                    style = Style { width(64.dp); height(64.dp); shape(RoundedCornerShape(12.dp)) }) {
                    Column(modifier = Modifier.wrapContentWidth(unbounded = true),
                        verticalArrangement = Arrangement.spacedBy(4.dp, Alignment.CenterVertically),
                        horizontalAlignment = Alignment.CenterHorizontally) {
                        FontWeightLineBox(24.sp) {
                        Text(textStyle = TextStyle(fontSize = 24.sp, lineHeight = 24.sp,
                                fontWeight = weight)) { "Aa" }
                        }
                        FontWeightLineBox(16.sp) {
                        Text(
                            textStyle =
                                TextColors.Muted.copy(
                                    fontSize = 12.sp,
                                    lineHeight = 16.sp,
                                ),
                        ) {
                            value.replaceFirstChar { it.uppercase() }
                        }
                        }
                    }
                }
            }
        }
        FontWeightDescription(fontWeight)
    }
    """.trimIndent()

private val toggleGroupRtlCode =
    """
    val translations = listOf(
        listOf("List", "Grid", "Cards"),
        listOf("قائمة", "شبكة", "بطاقات"),
        listOf("רשימה", "רשת", "כרטיסים"),
    )
    var language by remember { mutableStateOf(1) }
    var value by remember { mutableStateOf(setOf("list")) }
    Column {
        Row {
            listOf("English", "Arabic (العربية)", "Hebrew (עברית)").forEachIndexed { index, name ->
                Button(onClick = { language = index }) { Text { name } }
            }
        }
        CompositionLocalProvider(LocalLayoutDirection provides if (language == 0) LayoutDirection.Ltr else LayoutDirection.Rtl) {
            ToggleGroup(type = ToggleGroupType.Single, value = value, onValueChange = { value = it }, variant = ToggleVariant.Outline) {
                listOf("list", "grid", "cards").forEachIndexed { index, key ->
                    val label = translations[language][index]
                    ToggleGroupItem(key, label = label) { Text { label } }
                }
            }
        }
    }
    """.trimIndent()

val toggleGroupDoc =
    ComponentDoc(
        id = "toggle-group",
        title = "Toggle Group",
        description = "A set of two-state buttons that can be toggled on or off.",
        usageCode =
            """
            import com.toutiaoyan.compose.ui.components.togglegroup.ToggleGroup
            import com.toutiaoyan.compose.ui.components.togglegroup.ToggleGroupItem

            var value by remember { mutableStateOf(setOf("bold")) }
            ToggleGroup(value = value, onValueChange = { value = it }) {
                ToggleGroupItem("bold", "B")
                ToggleGroupItem("italic", "I")
            }
            """.trimIndent(),
        compositionCode =
            """
            ToggleGroup
            ├── ToggleGroupItem
            └── ToggleGroupItem
            """.trimIndent(),
        examplesHeading = null,
        afterApiSections =
            listOf(
                ComponentGuideSection("Changelog", emptyList()),
                ComponentGuideSection(
                    "2026-05-17 Default Spacing",
                    listOf(
                        ComponentGuideBlock.Paragraph(
                            "Changed the default `spacing` from `0` to `2` (8.dp) so toggle groups render with space " +
                                "between items by default. Use `spacing = 0.dp` for connected items.",
                        ),
                    ),
                    level = 3,
                ),
            ),
        apiSections =
            listOf(
                ComponentApiSection(
                    "ToggleGroup",
                    "Connected (spacing 0) or spaced two-state buttons; single or multiple selection.",
                    listOf(
                        ApiProperty("value / defaultValue", "Set<String>? / Set<String>", "null / emptySet()"),
                        ApiProperty("type", "ToggleGroupType", "Single"),
                        ApiProperty("variant / size", "ToggleVariant / ToggleSize", "Default / Md"),
                        ApiProperty("spacing", "Dp", "8.dp (2 in CSS units)"),
                        ApiProperty("orientation / disabled", "ToggleGroupOrientation / Boolean", "Horizontal / false"),
                    ),
                ),
                ComponentApiSection(
                    "ToggleGroupItem",
                    "One toggle inside the group; the root owns the selection state.",
                    listOf(
                        ApiProperty("value", "String", "Required"),
                        ApiProperty("label / content", "String / @Composable () -> Unit", "value / Required"),
                        ApiProperty("enabled / disabled", "Boolean", "true / false"),
                    ),
                ),
            ),
        examples =
            listOf(
                ComponentExample(
                    title = "Default",
                    code = toggleGroupDefaultCode,
                    preview = {
                        var value by remember { mutableStateOf(emptySet<String>()) }
                        ToggleGroupIconDemo(value, { value = it })
                    },
                ),
                ComponentExample(
                    title = "Outline",
                    descriptionMarkdown = "Use `variant=\"outline\"` for an outline style.",
                    code = toggleGroupOutlineCode,
                    preview = {
                        var value by remember { mutableStateOf(setOf("all")) }
                        ToggleGroupTextDemo(listOf("All", "Missed"), value, { value = it })
                    },
                ),
                ComponentExample(
                    title = "Size",
                    descriptionMarkdown = "Use the `size` prop to change the size of the toggle group.",
                    code = toggleGroupSizesCode,
                    preview = {
                        var small by remember { mutableStateOf(setOf("top")) }
                        var medium by remember { mutableStateOf(setOf("top")) }
                        Column(verticalArrangement = Arrangement.spacedBy(16.dp)) {
                            ToggleGroupTextDemo(
                                listOf("Top", "Bottom", "Left", "Right"),
                                small,
                                { small = it },
                                size = ToggleSize.Sm,
                            )
                            ToggleGroupTextDemo(
                                listOf("Top", "Bottom", "Left", "Right"),
                                medium,
                                { medium = it },
                            )
                        }
                    },
                ),
                ComponentExample(
                    title = "Spacing",
                    descriptionMarkdown = "Use `spacing` to add spacing between toggle group items.",
                    code = toggleGroupSpacingCode,
                    preview = {
                        var value by remember { mutableStateOf(setOf("top")) }
                        ToggleGroupTextDemo(
                            listOf("Top", "Bottom", "Left", "Right"),
                            value,
                            { value = it },
                            size = ToggleSize.Sm,
                            spacing = 8.dp,
                        )
                    },
                ),
                ComponentExample(
                    title = "Vertical",
                    descriptionMarkdown = "Use `orientation=\"vertical\"` for vertical toggle groups.",
                    code = toggleGroupVerticalCode,
                    preview = {
                        var value by remember { mutableStateOf(setOf("bold", "italic")) }
                        ToggleGroupIconDemo(
                            value,
                            { value = it },
                            vertical = true,
                            spacing = 4.dp,
                            variant = ToggleVariant.Default,
                        )
                    },
                ),
                ComponentExample(
                    title = "Disabled",
                    code = toggleGroupDisabledCode,
                    preview = {
                        ToggleGroupIconDemo(
                            emptySet(),
                            {},
                            disabled = true,
                            variant = ToggleVariant.Default,
                            type = ToggleGroupType.Single,
                        )
                    },
                ),
                ComponentExample(
                    title = "Custom",
                    // Keep the phone preview wide enough for four 64dp cards and three 8dp gaps.
                    previewPadding = 24.dp,
                    descriptionMarkdown = "A custom toggle group example.",
                    code = toggleGroupFontWeightCode,
                    preview = { ToggleGroupFontWeightDemo() },
                ),
                ComponentExample(
                    title = "RTL",
                    descriptionMarkdown = "To enable RTL support in shadcn/ui, see the RTL configuration guide.",
                    previewPadding = 0.dp,
                    code = toggleGroupRtlCode,
                    preview = {
                        var language by remember { mutableStateOf(1) }
                        var value by remember { mutableStateOf(setOf("list")) }
                        Column(Modifier.fillMaxWidth()) {
                            RtlPreviewToolbar(
                                language,
                                listOf("English", "Arabic (العربية)", "Hebrew (עברית)"),
                                { language = it },
                            )
                            CompositionLocalProvider(
                                LocalLayoutDirection provides
                                    if (language == 0) LayoutDirection.Ltr else LayoutDirection.Rtl,
                            ) {
                                Column(
                                    Modifier.fillMaxWidth().padding(40.dp),
                                    horizontalAlignment = Alignment.CenterHorizontally,
                                ) {
                                    ToggleGroup(
                                        type = ToggleGroupType.Single,
                                        value = value,
                                        onValueChange = { value = it },
                                        variant = ToggleVariant.Outline,
                                    ) {
                                        listOf("List", "Grid", "Cards").forEachIndexed { index, english ->
                                            val label = toggleGroupRtlLabels[language][index]
                                            ToggleGroupItem(english.lowercase(), label = label) {
                                                Text { label }
                                            }
                                        }
                                    }
                                }
                            }
                        }
                    },
                ),
                ComponentExample(
                    title = "Kotlin extension: Single select",
                    code =
                        """
                        var value by remember { mutableStateOf("italic") }
                        ToggleGroup(value = value, onValueChange = { value = it }) {
                            ToggleGroupItem("bold", "B")
                            ToggleGroupItem("italic", "I")
                            ToggleGroupItem("underline", "U")
                        }
                        // Clicking the active item deselects it; at most one item is active.
                        """.trimIndent(),
                    preview = {
                        var value by remember { mutableStateOf("italic") }
                        ToggleGroup(value = value, onValueChange = { value = it }) {
                            ToggleGroupItem("bold", "B")
                            ToggleGroupItem("italic", "I")
                            ToggleGroupItem("underline", "U")
                        }
                    },
                ),
                ComponentExample(
                    title = "Kotlin extension: Connected",
                    code =
                        """
                        var value by remember { mutableStateOf(setOf("bold")) }
                        ToggleGroup(type = ToggleGroupType.Multiple, value = value, onValueChange = { value = it }, spacing = 0.dp) {
                            ToggleGroupItem("bold", "B")
                            ToggleGroupItem("italic", "I")
                            ToggleGroupItem("underline", "U")
                        }
                        """.trimIndent(),
                    preview = {
                        var value by remember { mutableStateOf(setOf("bold")) }
                        ToggleGroup(
                            type = ToggleGroupType.Multiple,
                            value = value,
                            onValueChange = { value = it },
                            spacing = 0.dp,
                        ) {
                            ToggleGroupItem("bold", "B")
                            ToggleGroupItem("italic", "I")
                            ToggleGroupItem("underline", "U")
                        }
                    },
                ),
            ),
    )
