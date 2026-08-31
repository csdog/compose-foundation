package com.toutiaoyan.compose.tailwind.core

import androidx.compose.runtime.Immutable
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.unit.TextUnit
import com.toutiaoyan.compose.tailwind.core.TwTextSize.base

/**
 * Tailwind `text-*` as ready-made [TextStyle]s for `Text(style = …)`.
 *
 * Built from [TwFontSize] + [TwLineHeight] (single source of truth). Prefer
 * [com.toutiaoyan.compose.tailwind.modifiers.textSm] / Style `textSm()` for call sites.
 * Independent of [LocalUnitSpacing] / Theme `unitSpacing`. Role-based styles stay in
 * shadcn Typography.
 */
@Immutable
object TwTextSize {
    private fun size(
        font: TextUnit,
        line: TextUnit,
    ): TextStyle = TextStyle(fontSize = font, lineHeight = line)

    val xs = size(TwFontSize.xs, TwLineHeight.xs)
    val sm = size(TwFontSize.sm, TwLineHeight.sm)
    val base = size(TwFontSize.base, TwLineHeight.base)

    /** Alias for [base] (`text-base`). */
    val md = base
    val lg = size(TwFontSize.lg, TwLineHeight.lg)
    val xl = size(TwFontSize.xl, TwLineHeight.xl)
    val xl2 = size(TwFontSize.xl2, TwLineHeight.xl2)
    val xl3 = size(TwFontSize.xl3, TwLineHeight.xl3)
    val xl4 = size(TwFontSize.xl4, TwLineHeight.xl4)
    val xl5 = size(TwFontSize.xl5, TwLineHeight.xl5)
    val xl6 = size(TwFontSize.xl6, TwLineHeight.xl6)
    val xl7 = size(TwFontSize.xl7, TwLineHeight.xl7)
    val xl8 = size(TwFontSize.xl8, TwLineHeight.xl8)
    val xl9 = size(TwFontSize.xl9, TwLineHeight.xl9)
}
