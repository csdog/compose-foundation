@file:OptIn(androidx.compose.foundation.style.ExperimentalFoundationStyleApi::class)

package com.toutiaoyan.compose.ui.catalog.docs

import androidx.compose.ui.unit.dp

private val badgeRecipeCodes =
    listOf(
        """
            FlowRow(
            Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(8.dp, Alignment.CenterHorizontally),
            verticalArrangement = Arrangement.spacedBy(8.dp),
        ) {
                Badge("Badge")
                Badge("Secondary", variant = BadgeVariant.Secondary)
                Badge("Destructive", variant = BadgeVariant.Destructive)
                Badge("Outline", variant = BadgeVariant.Outline)
            }
        """.trimIndent(),
        """
            FlowRow(
            Modifier.width(IntrinsicSize.Max),
            horizontalArrangement = Arrangement.spacedBy(8.dp),
            verticalArrangement = Arrangement.spacedBy(8.dp),
        ) {
                Badge("Default")
                Badge("Secondary", variant = BadgeVariant.Secondary)
                Badge("Destructive", variant = BadgeVariant.Destructive)
                Badge("Outline", variant = BadgeVariant.Outline)
                Badge("Ghost", variant = BadgeVariant.Ghost)
            }
        """.trimIndent(),
        """
            FlowRow(
            Modifier.width(IntrinsicSize.Max),
            horizontalArrangement = Arrangement.spacedBy(8.dp),
            verticalArrangement = Arrangement.spacedBy(8.dp),
        ) {
                Badge(variant = BadgeVariant.Secondary, iconPlacement = BadgeIconPlacement.Start) {
                    Icon(Lucide.BadgeCheck)
                    Text(maxLines = 1) { "Verified" }
                }
                Badge(variant = BadgeVariant.Outline, iconPlacement = BadgeIconPlacement.End) {
                    Text(maxLines = 1) { "Bookmark" }
                    Icon(Lucide.Bookmark)
                }
            }
        """.trimIndent(),
        """
            FlowRow(
            Modifier.width(IntrinsicSize.Max),
            horizontalArrangement = Arrangement.spacedBy(8.dp),
            verticalArrangement = Arrangement.spacedBy(8.dp),
        ) {
                Badge(variant = BadgeVariant.Destructive, iconPlacement = BadgeIconPlacement.Start) {
                    Spinner()
                    Text(maxLines = 1) { "Deleting" }
                }
                Badge(variant = BadgeVariant.Secondary, iconPlacement = BadgeIconPlacement.End) {
                    Text(maxLines = 1) { "Generating" }
                    Spinner()
                }
            }
        """.trimIndent(),
        """
        Badge(href = "#link", iconPlacement = BadgeIconPlacement.End) {
            Text(maxLines = 1) { "Open Link" }
            Icon(Lucide.ArrowUpRight)
        }
        """.trimIndent(),
        """
            val dark = currentTheme.isDark
            FlowRow(
            Modifier.width(IntrinsicSize.Max),
            horizontalArrangement = Arrangement.spacedBy(8.dp),
            verticalArrangement = Arrangement.spacedBy(8.dp),
        ) {
                Badge("Blue", style = Style {
                    background(if (dark) TwColors.blue950 else TwColors.blue50)
                    contentColor(if (dark) TwColors.blue300 else TwColors.blue700)
                })
                Badge("Green", style = Style {
                    background(if (dark) TwColors.green950 else TwColors.green50)
                    contentColor(if (dark) TwColors.green300 else TwColors.green700)
                })
                Badge("Sky", style = Style {
                    background(if (dark) TwColors.sky950 else TwColors.sky50)
                    contentColor(if (dark) TwColors.sky300 else TwColors.sky700)
                })
                Badge("Purple", style = Style {
                    background(if (dark) TwColors.purple950 else TwColors.purple50)
                    contentColor(if (dark) TwColors.purple300 else TwColors.purple700)
                })
                Badge("Red", style = Style {
                    background(if (dark) TwColors.red950 else TwColors.red50)
                    contentColor(if (dark) TwColors.red300 else TwColors.red700)
                })
            }
        """.trimIndent(),
        """
            CompositionLocalProvider(LocalLayoutDirection provides LayoutDirection.Rtl) {
                // Supply Noto Sans Arabic / Hebrew via Theme(fontFamily = ...) for these languages.
                FlowRow(
            Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(8.dp, Alignment.CenterHorizontally),
            verticalArrangement = Arrangement.spacedBy(8.dp),
        ) {
                    Badge("شارة")
                    Badge("ثانوي", variant = BadgeVariant.Secondary)
                    Badge("مدمر", variant = BadgeVariant.Destructive)
                    Badge("مخطط", variant = BadgeVariant.Outline)
                    Badge(variant = BadgeVariant.Secondary, iconPlacement = BadgeIconPlacement.Start) {
                        Icon(Lucide.BadgeCheck)
                        Text(maxLines = 1) { "متحقق" }
                    }
                    Badge(variant = BadgeVariant.Outline, iconPlacement = BadgeIconPlacement.End) {
                        Text(maxLines = 1) { "إشارة مرجعية" }
                        Icon(Lucide.Bookmark)
                    }
                }
            }
        """.trimIndent(),
    )

val badgeDoc =
    ComponentDoc(
        id = "badge",
        title = "Badge",
        description = "Displays a badge or a component that looks like a badge.",
        usageImportsCode =
            """
            import com.toutiaoyan.compose.ui.components.badge.Badge
            import com.toutiaoyan.compose.ui.components.badge.BadgeVariant
            """.trimIndent(),
        usageCode = "Badge(\"Badge\", variant = BadgeVariant.Default)",
        compositionCode = "Badge {\n    Text { \"Badge\" }\n}",
        examplesHeading = null,
        examples =
            listOf("Default", "Variants", "With Icon", "With Spinner", "Link", "Custom Colors", "RTL").mapIndexed {
                    index,
                    title,
                ->
                ComponentExample(
                    title = title,
                    code = badgeRecipeCodes[index],
                    previewPadding = if (index == 6) 0.dp else 40.dp,
                    descriptionMarkdown =
                        when (index) {
                            1 -> "Use the `variant` parameter to change the variant of the badge."
                            2 ->
                                "Render icons inside the badge. Set `iconPlacement` to `Start` or `End` " +
                                    "for the matching logical edge spacing."
                            3 ->
                                "Render a spinner inside the badge. Set `iconPlacement` to `Start` or `End` " +
                                    "to match the spinner position."
                            4 ->
                                "Use `href` to render a link as a badge. `onClick` can override navigation, " +
                                    "as this catalog does for its section anchor."
                            5 ->
                                "Customize colors with `Style`, using the light and dark palette steps " +
                                    "appropriate to the current theme."
                            6 ->
                                "Set `LocalLayoutDirection` to enable RTL layout, " +
                                    "and supply the appropriate font for the language."
                            else -> null
                        },
                    preview = { if (index == 6) BadgeRtlRecipe() else BadgeRecipe(index) },
                )
            },
        apiSections =
            listOf(
                ComponentApiSection(
                    "Badge",
                    "Displays a badge. Variants are Default, Secondary, Destructive, Outline, Ghost and Link. " +
                        "Both a text overload and a content slot are available.",
                    listOf(
                        ApiProperty("modifier", "Modifier", "Modifier"),
                        ApiProperty("variant", "BadgeVariant", "Default"),
                        ApiProperty("href", "String?", "null"),
                        ApiProperty("onClick", "(() -> Unit)?", "null; overrides href navigation"),
                        ApiProperty("invalid", "Boolean", "false"),
                        ApiProperty("iconPlacement", "BadgeIconPlacement?", "null"),
                        ApiProperty("style", "Style", "Style"),
                        ApiProperty("content", "@Composable () -> Unit", "required for slot overload"),
                    ),
                ),
                ComponentApiSection(
                    "BadgeIconPlacement",
                    "Logical icon edges reduce Nova padding from 8dp to 6dp. " +
                        "The caller owns the icon and its order in the content slot.",
                    listOf(
                        ApiProperty("Start", "BadgeIconPlacement", "leading icon"),
                        ApiProperty("End", "BadgeIconPlacement", "trailing icon"),
                        ApiProperty("Both", "BadgeIconPlacement", "icons at both edges"),
                    ),
                ),
            ),
    )
