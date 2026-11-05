@file:OptIn(androidx.compose.foundation.style.ExperimentalFoundationStyleApi::class)

package com.toutiaoyan.compose.ui.catalog.docs

import androidx.compose.ui.unit.dp

private val buttonApiProperties =
    listOf(
        ApiProperty("onClick", "() -> Unit", "{}"),
        ApiProperty("modifier", "Modifier", "Modifier"),
        ApiProperty("enabled", "Boolean", "true"),
        ApiProperty("disabled", "Boolean", "false"),
        ApiProperty("variant", "ButtonVariant", "Default"),
        ApiProperty("size", "ButtonSize", "Md"),
        ApiProperty("indication", "Indication?", "null"),
        ApiProperty("label", "String?", "null"),
        ApiProperty("style", "Style", "Style"),
        ApiProperty("iconPlacement", "ButtonIconPlacement?", "null"),
        ApiProperty("invalid", "Boolean", "false"),
        ApiProperty("content", "@Composable () -> Unit", "required"),
    )

val buttonDoc =
    ComponentDoc(
        id = "button",
        title = "Button",
        description = "Displays a button or a component that looks like a button.",
        usageImportsCode = "import com.toutiaoyan.compose.ui.components.button.*",
        usageCode = "Button(\"Button\", variant = ButtonVariant.Outline)",
        compositionCode = "Button(onClick = { /* perform action */ }) { Text { \"Continue\" } }",
        examplesHeading = null,
        beforeExamples =
            listOf(
                ComponentGuideSection(
                    "Cursor",
                    listOf(
                        ComponentGuideBlock.Paragraph(
                            "Nova uses the default arrow cursor, following Tailwind v4. " +
                                "Opt into a hand cursor with " +
                                "`Modifier.pointerHoverIcon(PointerIcon.Hand, overrideDescendants = true)` " +
                                "when your application needs it.",
                        ),
                    ),
                ),
            ),
        apiSections =
            listOf(
                ComponentApiSection(
                    "Button",
                    "An action control with button semantics. Invalid state exposes an error and " +
                        "shows its border and ring even without focus; caller Style overrides remain last.",
                    buttonApiProperties,
                ),
                ComponentApiSection(
                    "buttonStyle",
                    "Applies the same variant/size chrome to another composable without " +
                        "assigning button semantics or a click handler. The caller owns link behavior and content.",
                    listOf(
                        ApiProperty("variant", "ButtonVariant", "Default"),
                        ApiProperty("size", "ButtonSize", "Md"),
                        ApiProperty("style", "Style", "Style"),
                        ApiProperty("styleState", "StyleState?", "null"),
                    ),
                ),
            ),
        examples =
            listOf(
                ComponentExample(
                    title = "Default",
                    code =
                        """
                        Row(Modifier.wrapContentWidth(unbounded = true), horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                            Button("Button", variant = ButtonVariant.Outline)
                            Button(variant = ButtonVariant.Outline, size = ButtonSize.Icon, label = "Submit") { Icon(Lucide.ArrowUp) }
                        }
                        """.trimIndent(),
                    preview = { ButtonRecipe(0) },
                ),
                ComponentExample(
                    title = "Size",
                    descriptionMarkdown =
                        "Use `size` to change the button dimensions. The four pairs stack " +
                            "below 640dp.",
                    code =
                        """
                        val pairs: @Composable () -> Unit = {
                            listOf("Extra Small" to ButtonSize.Xs, "Small" to ButtonSize.Sm, "Default" to ButtonSize.Md, "Large" to ButtonSize.Lg).forEachIndexed { index, (label, size) ->
                                Row(Modifier.wrapContentWidth(unbounded = true), horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                                    Button(label, size = size, variant = ButtonVariant.Outline)
                                    Button(size = listOf(ButtonSize.IconXs, ButtonSize.IconSm, ButtonSize.Icon, ButtonSize.IconLg)[index], variant = ButtonVariant.Outline, label = "Submit") { Icon(Lucide.ArrowUpRight) }
                                }
                            }
                        }
                        val narrow = with(LocalDensity.current) { LocalWindowInfo.current.containerSize.width.toDp() < 640.dp }
                        if (narrow) Column(verticalArrangement = Arrangement.spacedBy(32.dp)) { pairs() }
                        else Row(horizontalArrangement = Arrangement.spacedBy(32.dp)) { pairs() }
                        """.trimIndent(),
                    preview = { ButtonRecipe(1) },
                ),
                ComponentExample(
                    title = "Default",
                    code =
                        """
                        Button("Button")
                        """.trimIndent(),
                    preview = { ButtonRecipe(2) },
                ),
                ComponentExample(
                    title = "Outline",
                    code =
                        """
                        Button("Outline", variant = ButtonVariant.Outline)
                        """.trimIndent(),
                    preview = { ButtonRecipe(3) },
                ),
                ComponentExample(
                    title = "Secondary",
                    code =
                        """
                        Button("Secondary", variant = ButtonVariant.Secondary)
                        """.trimIndent(),
                    preview = { ButtonRecipe(4) },
                ),
                ComponentExample(
                    title = "Ghost",
                    code =
                        """
                        Button("Ghost", variant = ButtonVariant.Ghost)
                        """.trimIndent(),
                    preview = { ButtonRecipe(5) },
                ),
                ComponentExample(
                    title = "Destructive",
                    code =
                        """
                        Button("Destructive", variant = ButtonVariant.Destructive)
                        """.trimIndent(),
                    preview = { ButtonRecipe(6) },
                ),
                ComponentExample(
                    title = "Link",
                    code =
                        """
                        Button("Link", variant = ButtonVariant.Link)
                        """.trimIndent(),
                    preview = { ButtonRecipe(7) },
                ),
                ComponentExample(
                    title = "Icon",
                    code =
                        """
                        Button(variant = ButtonVariant.Outline, size = ButtonSize.Icon, label = "Upload") { Icon(Lucide.CircleFadingArrowUp) }
                        """.trimIndent(),
                    preview = { ButtonRecipe(8) },
                ),
                ComponentExample(
                    title = "With Icon",
                    descriptionMarkdown =
                        "Set `iconPlacement` to `Start` or `End` for the matching logical " +
                            "edge spacing.",
                    code =
                        """
                        Row(Modifier.wrapContentWidth(unbounded = true), horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                            Button(variant = ButtonVariant.Outline, iconPlacement = ButtonIconPlacement.Start) {
                                Icon(Lucide.GitBranch)
                                Text(maxLines = 1) { "New Branch" }
                            }
                            Button(variant = ButtonVariant.Outline, iconPlacement = ButtonIconPlacement.End) {
                                Text(maxLines = 1) { "Fork" }
                                Icon(Lucide.GitFork)
                            }
                        }
                        """.trimIndent(),
                    preview = { ButtonRecipe(9) },
                ),
                ComponentExample(
                    title = "Rounded",
                    descriptionMarkdown = "Override the component shape with `CircleShape` for fully rounded buttons.",
                    code =
                        """
                        Row(Modifier.wrapContentWidth(unbounded = true), horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                            Button("Get Started", style = Style { shape(CircleShape) })
                            Button(variant = ButtonVariant.Outline, size = ButtonSize.Icon, label = "Submit", style = Style { shape(CircleShape) }) { Icon(Lucide.ArrowUp) }
                        }
                        """.trimIndent(),
                    preview = { ButtonRecipe(10) },
                ),
                ComponentExample(
                    title = "Spinner",
                    descriptionMarkdown =
                        "Render `Spinner` inside the button and declare its logical icon " +
                            "edge. Both loading buttons are disabled.",
                    code =
                        """
                        Row(Modifier.wrapContentWidth(unbounded = true), horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                            Button(variant = ButtonVariant.Outline, disabled = true, iconPlacement = ButtonIconPlacement.Start) {
                                Spinner()
                                Text(maxLines = 1) { "Generating" }
                            }
                            Button(variant = ButtonVariant.Secondary, disabled = true, iconPlacement = ButtonIconPlacement.Start) {
                                Text(maxLines = 1) { "Downloading" }
                                Spinner()
                            }
                        }
                        """.trimIndent(),
                    preview = { ButtonRecipe(11) },
                ),
                ComponentExample(
                    title = "Button Group",
                    descriptionMarkdown =
                        "Use `ButtonGroup` to group related actions. See the Button Group " +
                            "documentation for the complete API.",
                    code = buttonGroupDoc.examples.first().code,
                    preview = { ButtonRecipe(12) },
                ),
                ComponentExample(
                    title = "As Link",
                    descriptionMarkdown =
                        "Use a LinkAnnotation URL and the public Button variant/size styles. " +
                            "The link owns keyboard and accessibility behavior; padding is clickable too. " +
                            "Do not use Button for links because it declares Role.Button.",
                    code = buttonAsLinkCode,
                    preview = { ButtonAsLinkRecipe() },
                ),
                ComponentExample(
                    title = "RTL",
                    descriptionMarkdown =
                        "To enable RTL support in shadcn/ui, see the [RTL configuration guide](https://ui.shadcn.com/docs/rtl).",
                    previewPadding = 0.dp,
                    code =
                        """
                        // Supply Noto Sans Arabic / Hebrew through Theme(fontFamily = ...) for these languages.
                        CompositionLocalProvider(LocalLayoutDirection provides LayoutDirection.Rtl) {
                            ButtonRtlFlow {
                                Button("زر", variant = ButtonVariant.Outline)
                                Button("حذف", variant = ButtonVariant.Destructive)
                                Button(variant = ButtonVariant.Outline, iconPlacement = ButtonIconPlacement.End) {
                                    Text(maxLines = 1) { "إرسال" }
                                    Icon(Lucide.ArrowRight, modifier = Modifier.graphicsLayer { rotationZ = 180f })
                                }
                                Button(variant = ButtonVariant.Outline, size = ButtonSize.Icon, label = "Add") { Icon(Lucide.Plus) }
                                Button(variant = ButtonVariant.Secondary, disabled = true, iconPlacement = ButtonIconPlacement.Start) {
                                    Spinner()
                                    Text(maxLines = 1) { "جاري التحميل" }
                                }
                            }
                        }

                        @Composable
                        private fun ButtonRtlFlow(content: @Composable () -> Unit) {
                            Layout(content = content) { measurables, constraints ->
                                val gap = 8.dp.roundToPx()
                                val children = measurables.map { it.measure(Constraints(maxHeight = constraints.maxHeight)) }
                                val width = constraints.constrainWidth(children.sumOf { it.width } + gap * (children.size - 1).coerceAtLeast(0))
                                var x = 0
                                var y = 0
                                var lineHeight = 0
                                val positions = children.map { child ->
                                    if (x > 0 && x + child.width > width) {
                                        x = 0
                                        y += lineHeight + gap
                                        lineHeight = 0
                                    }
                                    val position = x to y
                                    x += child.width + gap
                                    lineHeight = maxOf(lineHeight, child.height)
                                    position
                                }
                                layout(width, constraints.constrainHeight(y + lineHeight)) {
                                    children.forEachIndexed { index, child ->
                                        val (left, top) = positions[index]
                                        child.placeRelative(left, top)
                                    }
                                }
                            }
                        }

                        """.trimIndent(),
                    preview = { ButtonRtlRecipe() },
                ),
            ),
    )
