package com.toutiaoyan.compose.ui.catalog.docs

internal val hoverCardRtlCode =
    """
    @Composable
    fun HoverCardRtlExample() {
        var language by remember { mutableStateOf(1) }
        val inherited = currentTheme
        Column(Modifier.fillMaxWidth()) {
            // Connect language to a picker: 0 English, 1 Arabic, 2 Hebrew.
            Box(
                Modifier.fillMaxWidth().height(320.dp).padding(40.dp),
                contentAlignment = Alignment.Center,
            ) {
                val font =
                    when (language) {
                        1 -> FontFamily(Font(Res.font.catalog_arabic_regular, FontWeight.Normal), Font(Res.font.catalog_arabic_medium, FontWeight.Medium))
                        2 -> FontFamily(Font(Res.font.catalog_hebrew_regular, FontWeight.Normal), Font(Res.font.catalog_hebrew_medium, FontWeight.Medium))
                        else -> inherited.fontFamily
                    }
                CompositionLocalProvider(
                    LocalLayoutDirection provides if (language == 0) LayoutDirection.Ltr else LayoutDirection.Rtl,
                ) {
                    Theme(
                        preset = inherited.activePreset,
                        baseColor = inherited.activeBaseColor,
                        accent = inherited.activeAccent,
                        isDark = inherited.isDark, baseRadius = inherited.baseRadius, ring = inherited.ring,
                        unitSpacing = LocalUnitSpacing.current,
                        minTouchTarget = inherited.minTouchTarget,
                        fontFamily = font,
                    ) {
                        val t = hoverCardTranslations[language]
                        Column(Modifier.fillMaxWidth(), verticalArrangement = Arrangement.spacedBy(16.dp)) {
                            listOf(
                                listOf(
                                    HoverCardSide.Left to "left",
                                    HoverCardSide.Top to "top",
                                    HoverCardSide.Bottom to "bottom",
                                    HoverCardSide.Right to "right",
                                ),
                                listOf(
                                    HoverCardSide.InlineStart to "inline-start",
                                    HoverCardSide.InlineEnd to "inline-end",
                                ),
                            ).forEach { sides ->
                                FlowRow(
                                    Modifier.fillMaxWidth(),
                                    horizontalArrangement = Arrangement.spacedBy(8.dp, Alignment.CenterHorizontally),
                                    verticalArrangement = Arrangement.spacedBy(8.dp),
                                ) {
                                    sides.forEach { (side, label) ->
                                        HoverCard {
                                            HoverCardTrigger(delay = 10, closeDelay = 100) {
                                                Button(t.getValue(label), onClick = {}, variant = ButtonVariant.Outline)
                                            }
                                            HoverCardContent(side = side) {
                                                Column(verticalArrangement = Arrangement.spacedBy(4.dp)) {
                                                    Text(
                                                        textStyle = TextStyle(fontWeight = FontWeight.SemiBold),
                                                    ) { t.getValue("name") }
                                                    Text(
                                                        textStyle = TextStyle(color = currentTheme.colors.mutedForeground),
                                                    ) { t.getValue("price") }
                                                }
                                            }
                                        }
                                    }
                                }
                            }
                        }
                    }
                }
            }
        }
    }
    
    private val hoverCardTranslations =
        listOf(
            mapOf(
                "trigger" to "Wireless Headphones",
                "name" to "Wireless Headphones",
                "price" to "$99.99",
                "inline-start" to "Inline Start",
                "left" to "Left",
                "top" to "Top",
                "bottom" to "Bottom",
                "right" to "Right",
                "inline-end" to "Inline End",
            ),
            mapOf(
                "trigger" to "سماعات لاسلكية",
                "name" to "سماعات لاسلكية",
                "price" to "٩٩.٩٩ $",
                "inline-start" to "بداية السطر",
                "left" to "يسار",
                "top" to "أعلى",
                "bottom" to "أسفل",
                "right" to "يمين",
                "inline-end" to "نهاية السطر",
            ),
            mapOf(
                "trigger" to "אוזניות אלחוטיות",
                "name" to "אוזניות אלחוטיות",
                "price" to "99.99 $",
                "inline-start" to "תחילת השורה",
                "left" to "שמאל",
                "top" to "למעלה",
                "bottom" to "למטה",
                "right" to "ימין",
                "inline-end" to "סוף השורה",
            ),
        )
    """.trimIndent()
