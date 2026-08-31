package com.toutiaoyan.compose.tailwind.modifiers

import androidx.compose.ui.text.TextStyle
import com.toutiaoyan.compose.tailwind.core.TwTextSize

/**
 * Tailwind's `text-*` font-size utilities as [TextStyle] factories for
 * `Text(style = textSm())` (Compose has no font-size [androidx.compose.ui.Modifier.Modifier]).
 *
 * Each step is [TwTextSize] ( [com.toutiaoyan.compose.tailwind.core.TwFontSize] +
 * [com.toutiaoyan.compose.tailwind.core.TwLineHeight] ). Style-API analogues live in
 * [com.toutiaoyan.compose.tailwind.style.textSm] et al.
 */
fun textXs(): TextStyle = TwTextSize.xs

fun textSm(): TextStyle = TwTextSize.sm

fun textBase(): TextStyle = TwTextSize.base

/** Alias for [textBase] (`text-base`). */
fun textMd(): TextStyle = TwTextSize.md

fun textLg(): TextStyle = TwTextSize.lg

fun textXl(): TextStyle = TwTextSize.xl

fun textXl2(): TextStyle = TwTextSize.xl2

fun textXl3(): TextStyle = TwTextSize.xl3

fun textXl4(): TextStyle = TwTextSize.xl4

fun textXl5(): TextStyle = TwTextSize.xl5

fun textXl6(): TextStyle = TwTextSize.xl6

fun textXl7(): TextStyle = TwTextSize.xl7

fun textXl8(): TextStyle = TwTextSize.xl8

fun textXl9(): TextStyle = TwTextSize.xl9
