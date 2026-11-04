package com.toutiaoyan.compose.ui.catalog.docs

internal val emptyRtlCode =
    """
    @Composable
    fun EmptyRtlExample() {
        var language by remember { mutableStateOf(1) }
        val inherited = currentTheme
        Column(Modifier.fillMaxWidth()) {
            // Connect language to your picker: 0 English, 1 Arabic, 2 Hebrew.
            Box(
                Modifier.fillMaxWidth().height(288.dp).padding(40.dp).wrapContentHeight(unbounded = true),
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
                        val t = emptyTranslations[language]
                        Empty {
                            EmptyHeader {
                                EmptyMedia(variant = EmptyMediaVariant.Icon) { EmptyAdditionalGlyph(EmptyFolderCode) }
                                EmptyTitle(t[0])
                                EmptyDescription(t[1])
                            }
                            EmptyContent {
                                Row(
                                    Modifier.wrapContentWidth(unbounded = true),
                                    horizontalArrangement = Arrangement.spacedBy(8.dp),
                                ) {
                                    Button(t[2], onClick = {})
                                    Button(t[3], onClick = {}, variant = ButtonVariant.Outline)
                                }
                            }
                            EmptyLearnMore(t[4], rtl = language != 0, inlineEnd = true)
                        }
                    }
                }
            }
        }
    }
    
    private val emptyTranslations =
        listOf(
            listOf(
                "No Projects Yet",
                "You haven't created any projects yet. Get started by creating your first project.",
                "Create Project",
                "Import Project",
                "Learn More",
            ),
            listOf(
                "لا توجد مشاريع بعد",
                "لم تقم بإنشاء أي مشاريع بعد. ابدأ بإنشاء مشروعك الأول.",
                "إنشاء مشروع",
                "استيراد مشروع",
                "تعرف على المزيد",
            ),
            listOf(
                "אין פרויקטים עדיין",
                "עדיין לא יצרת פרויקטים. התחל על ידי יצירת הפרויקט הראשון שלך.",
                "צור פרויקט",
                "ייבא פרויקט",
                "למד עוד",
            ),
        )
    
    @Composable
    private fun EmptyAdditionalGlyph(
        image: ImageVector,
        modifier: Modifier = Modifier,
        tint: Color = Color.Unspecified,
    ) {
        Image(
            image,
            null,
            modifier.size(16.dp),
            colorFilter =
                ColorFilter.tint(
                    if (tint.isSpecified) {
                        tint
                    } else {
                        LocalContentColor.current.takeIf { it.isSpecified }
                            ?: currentTheme.colors.foreground
                    },
                ),
        )
    }
    """.trimIndent()
