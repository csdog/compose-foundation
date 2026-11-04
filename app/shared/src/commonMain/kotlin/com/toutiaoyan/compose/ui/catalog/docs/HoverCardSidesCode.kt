package com.toutiaoyan.compose.ui.catalog.docs

internal val hoverCardSidesCode =
    """
    @Composable
    fun HoverCardSidesExample(modifier: Modifier = Modifier) {
        val sides = listOf(HoverCardSide.Left, HoverCardSide.Top, HoverCardSide.Bottom, HoverCardSide.Right)
        val measurer = rememberTextMeasurer()
        val labelStyle =
            TextStyle(
                fontFamily = currentTheme.fontFamily,
                fontSize = 14.sp,
                fontWeight = FontWeight.Medium,
                letterSpacing = 0.sp,
            )
        val density = LocalDensity.current
        val chrome = with(density) { 22.dp.toPx() } // px-2.5 plus the two 1dp borders, as in the source Md Outline buttons.
        val ideal = sides.map {
            val label = measurer.measure(it.name, labelStyle)
            // Intrinsic width is rounded up by Skia; cursor positions retain glyph advances.
            val advance = label.getHorizontalPosition(it.name.length, true) - label.getHorizontalPosition(0, true)
            kotlin.math.abs(advance) + chrome
        }
        val gap = with(density) { 8.dp.roundToPx() }
        Layout(
            modifier = modifier.fillMaxWidth(),
            content = {
                sides.forEach { side ->
                    HoverCard {
                        HoverCardTrigger(
                            delay = 100,
                            closeDelay = 100,
                        ) { Button(side.name, onClick = {}, variant = ButtonVariant.Outline) }
                        HoverCardContent(side = side) {
                            Column(verticalArrangement = Arrangement.spacedBy(4.dp)) {
                                Text(textStyle = TextStyle(fontWeight = FontWeight.Medium)) { "Hover Card" }
                                Text { "This hover card appears on the " + side.name.lowercase() + " side of the trigger." }
                            }
                        }
                    }
                }
            },
        ) { children, constraints ->
            val items = children.map { it.measure(Constraints()) }
            val rows = mutableListOf<MutableList<Int>>()
            var used = 0f
            items.indices.forEach { i ->
                val extra = ideal[i] + if (rows.lastOrNull()?.isNotEmpty() == true) gap else 0
                if (rows.isEmpty() || used + extra > constraints.maxWidth) {
                    rows.add(mutableListOf())
                    used = 0f
                }
                if (rows.last().isNotEmpty()) used += gap
                rows.last().add(i)
                used += ideal[i]
            }
            val rowWidths = rows.map { row -> row.sumOf { items[it].width } + gap * (row.size - 1) }
            val rowHeights = rows.map { row -> row.maxOf { items[it].height } }
            val width = constraints.constrainWidth(rowWidths.maxOrNull() ?: 0)
            val height = constraints.constrainHeight(rowHeights.sum() + gap * (rows.size - 1).coerceAtLeast(0))
            layout(width, height) {
                var y = 0
                rows.forEachIndexed { line, row ->
                    // A subpixel CSS fit may exceed integer layout by a pixel. Keep the complete line centered.
                    var x = (width - rowWidths[line]) / 2
                    row.forEach { i ->
                        items[i].placeRelative(x, y)
                        x += items[i].width + gap
                    }
                    y += rowHeights[line] + gap
                }
            }
        }
    }
    """.trimIndent()
