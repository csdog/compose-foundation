package com.toutiaoyan.compose.tailwind.style

import androidx.compose.foundation.style.StyleScope
import com.toutiaoyan.compose.tailwind.core.TwFontFamily
import com.toutiaoyan.compose.tailwind.core.TwFontSize
import com.toutiaoyan.compose.tailwind.core.TwFontWeight
import com.toutiaoyan.compose.tailwind.core.TwLineHeight
import com.toutiaoyan.compose.tailwind.core.TwTracking

/**
 * Tailwind's `text-*` utilities (font-size + default line-height) via Style
 * [fontSize] + [lineHeight], matching [com.toutiaoyan.compose.tailwind.core.TwTextSize]
 * and [com.toutiaoyan.compose.tailwind.modifiers.textSm].
 */

fun StyleScope.textXs() {
    fontSize(TwFontSize.xs)
    lineHeight(TwLineHeight.xs)
}

fun StyleScope.textSm() {
    fontSize(TwFontSize.sm)
    lineHeight(TwLineHeight.sm)
}

fun StyleScope.textBase() {
    fontSize(TwFontSize.base)
    lineHeight(TwLineHeight.base)
}

/** Alias for [textBase] (`text-base`). */

fun StyleScope.textMd() {
    textBase()
}

fun StyleScope.textLg() {
    fontSize(TwFontSize.lg)
    lineHeight(TwLineHeight.lg)
}

fun StyleScope.textXl() {
    fontSize(TwFontSize.xl)
    lineHeight(TwLineHeight.xl)
}

fun StyleScope.textXl2() {
    fontSize(TwFontSize.xl2)
    lineHeight(TwLineHeight.xl2)
}

fun StyleScope.textXl3() {
    fontSize(TwFontSize.xl3)
    lineHeight(TwLineHeight.xl3)
}

fun StyleScope.textXl4() {
    fontSize(TwFontSize.xl4)
    lineHeight(TwLineHeight.xl4)
}

fun StyleScope.textXl5() {
    fontSize(TwFontSize.xl5)
    lineHeight(TwLineHeight.xl5)
}

fun StyleScope.textXl6() {
    fontSize(TwFontSize.xl6)
    lineHeight(TwLineHeight.xl6)
}

fun StyleScope.textXl7() {
    fontSize(TwFontSize.xl7)
    lineHeight(TwLineHeight.xl7)
}

fun StyleScope.textXl8() {
    fontSize(TwFontSize.xl8)
    lineHeight(TwLineHeight.xl8)
}

fun StyleScope.textXl9() {
    fontSize(TwFontSize.xl9)
    lineHeight(TwLineHeight.xl9)
}

/**
 * Tailwind's font-size-only utilities via
 * [androidx.compose.foundation.style.FontSizeScope.fontSize] (no paired line-height).
 */

fun StyleScope.fontSizeXs() {
    fontSize(TwFontSize.xs)
}

fun StyleScope.fontSizeSm() {
    fontSize(TwFontSize.sm)
}

fun StyleScope.fontSizeBase() {
    fontSize(TwFontSize.base)
}

/** Alias for [fontSizeBase]. */

fun StyleScope.fontSizeMd() {
    fontSizeBase()
}

fun StyleScope.fontSizeLg() {
    fontSize(TwFontSize.lg)
}

fun StyleScope.fontSizeXl() {
    fontSize(TwFontSize.xl)
}

fun StyleScope.fontSizeXl2() {
    fontSize(TwFontSize.xl2)
}

fun StyleScope.fontSizeXl3() {
    fontSize(TwFontSize.xl3)
}

fun StyleScope.fontSizeXl4() {
    fontSize(TwFontSize.xl4)
}

fun StyleScope.fontSizeXl5() {
    fontSize(TwFontSize.xl5)
}

fun StyleScope.fontSizeXl6() {
    fontSize(TwFontSize.xl6)
}

fun StyleScope.fontSizeXl7() {
    fontSize(TwFontSize.xl7)
}

fun StyleScope.fontSizeXl8() {
    fontSize(TwFontSize.xl8)
}

fun StyleScope.fontSizeXl9() {
    fontSize(TwFontSize.xl9)
}

/** Tailwind's `leading-*` line-height utilities via [androidx.compose.foundation.style.LineHeightScope]. */

fun StyleScope.lineHeightXs() {
    lineHeight(TwLineHeight.xs)
}

fun StyleScope.lineHeightSm() {
    lineHeight(TwLineHeight.sm)
}

fun StyleScope.lineHeightBase() {
    lineHeight(TwLineHeight.base)
}

fun StyleScope.lineHeightLg() {
    lineHeight(TwLineHeight.lg)
}

fun StyleScope.lineHeightXl() {
    lineHeight(TwLineHeight.xl)
}

fun StyleScope.lineHeightXl2() {
    lineHeight(TwLineHeight.xl2)
}

fun StyleScope.lineHeightXl3() {
    lineHeight(TwLineHeight.xl3)
}

fun StyleScope.lineHeightXl4() {
    lineHeight(TwLineHeight.xl4)
}

fun StyleScope.lineHeightXl5() {
    lineHeight(TwLineHeight.xl5)
}

fun StyleScope.lineHeightXl6() {
    lineHeight(TwLineHeight.xl6)
}

fun StyleScope.lineHeightXl7() {
    lineHeight(TwLineHeight.xl7)
}

fun StyleScope.lineHeightXl8() {
    lineHeight(TwLineHeight.xl8)
}

fun StyleScope.lineHeightXl9() {
    lineHeight(TwLineHeight.xl9)
}

/** Tailwind's `font-*` weight utilities via [androidx.compose.foundation.style.FontWeightScope]. */

fun StyleScope.fontWeightNormal() {
    fontWeight(TwFontWeight.normal)
}

fun StyleScope.fontWeightMedium() {
    fontWeight(TwFontWeight.medium)
}

fun StyleScope.fontWeightBold() {
    fontWeight(TwFontWeight.bold)
}

/** Tailwind's `tracking-*` letter-spacing utilities via [androidx.compose.foundation.style.LetterSpacingScope]. */

fun StyleScope.letterSpacingTight() {
    letterSpacing(TwTracking.tight)
}

fun StyleScope.letterSpacingWide() {
    letterSpacing(TwTracking.wide)
}

/** Tailwind's `font-sans`/`font-serif`/`font-mono` utilities via [androidx.compose.foundation.style.FontFamilyScope]. */

fun StyleScope.fontFamilySans() {
    fontFamily(TwFontFamily.sans)
}

fun StyleScope.fontFamilySerif() {
    fontFamily(TwFontFamily.serif)
}

fun StyleScope.fontFamilyMono() {
    fontFamily(TwFontFamily.mono)
}
