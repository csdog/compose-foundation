package com.toutiaoyan.compose.ui.catalog.docs

internal val emptyLearnMoreCode =
    """
    @Composable
    private fun EmptyLearnMore(
        text: String,
        rtl: Boolean = false,
        inlineEnd: Boolean = false,
    ) {
        val theme = currentTheme
        val uri = LocalUriHandler.current
        val open by rememberUpdatedState<() -> Unit>({ uri.openUri("#") })
        val interactions = remember { MutableInteractionSource() }
        val hovered by interactions.collectIsHoveredAsState()
        val focusInteraction = remember { FocusInteraction.Focus() }
        val focus = remember { FocusRequester() }
        val mode = LocalInputModeManager.current
        val styleState = rememberUpdatedStyleState(interactions)
        Row(
            Modifier
                .semantics(mergeDescendants = true) {
                    role = Role.Button
                    onClick {
                        open()
                        true
                    }
                }
                .focusRequester(focus)
                .onFocusChanged {
                    interactions.tryEmit(if (it.hasFocus) focusInteraction else FocusInteraction.Unfocus(focusInteraction))
                }
                .onPreviewKeyEvent {
                    mode.requestInputMode(InputMode.Keyboard)
                    false
                }
                .hoverable(interactions)
                .pointerHoverIcon(PointerIcon.Hand)
                .pointerInput(Unit) {
                    detectTapGestures {
                        mode.requestInputMode(InputMode.Touch)
                        focus.requestFocus()
                        open()
                    }
                }
                .buttonStyle(
                    variant = ButtonVariant.Link,
                    size = ButtonSize.Sm,
                    styleState = styleState,
                    style = if (inlineEnd) Style { contentPadding(PaddingValues(start = 10.dp, end = 6.dp)) } else Style,
                ),
            horizontalArrangement = Arrangement.spacedBy(4.dp),
            verticalAlignment = Alignment.CenterVertically,
        ) {
            DisableSelection {
                Text(
                    maxLines = 1,
                    textStyle =
                        TextStyle(
                            fontFamily = theme.fontFamily,
                            fontSize = 12.8.sp,
                            lineHeight = 20.sp,
                            lineHeightStyle =
                                LineHeightStyle(
                                    LineHeightStyle.Alignment.Center,
                                    LineHeightStyle.Trim.None,
                                    LineHeightStyle.Mode.Tight,
                                ),
                            fontWeight = FontWeight.Medium,
                            color = theme.colors.mutedForeground,
                        ),
                ) {
                    buildAnnotatedString {
                        withLink(
                            LinkAnnotation.Url(
                                "#",
                                TextLinkStyles(
                                    style =
                                        SpanStyle(
                                            color = theme.colors.mutedForeground,
                                            textDecoration = if (hovered) TextDecoration.Underline else TextDecoration.None,
                                        ),
                                ),
                                linkInteractionListener = {
                                    open()
                                },
                            ),
                        ) { append(text) }
                    }
                }
            }
            Image(
                Lucide.ArrowUpRight,
                null,
                Modifier.size(14.dp).rotate(if (rtl) 270f else 0f),
                colorFilter = ColorFilter.tint(theme.colors.mutedForeground),
            )
        }
    }
    """.trimIndent()
