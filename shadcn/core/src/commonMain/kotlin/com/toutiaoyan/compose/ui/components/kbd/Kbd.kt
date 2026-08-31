@file:OptIn(ExperimentalFoundationStyleApi::class, ExperimentalTypeInference::class)

package com.toutiaoyan.compose.ui.components.kbd

import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.style.ExperimentalFoundationStyleApi
import androidx.compose.foundation.style.MutableStyleState
import androidx.compose.foundation.style.Style
import androidx.compose.foundation.style.styleable
import androidx.compose.foundation.style.then
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import com.toutiaoyan.compose.ui.components.text.Text
import com.toutiaoyan.compose.ui.components.text.TextVariant
import com.toutiaoyan.compose.ui.components.text.TextColors
import com.toutiaoyan.compose.tailwind.core.twSpacing
import kotlin.experimental.ExperimentalTypeInference
import kotlin.jvm.JvmName

/**
 * A keyboard-shortcut label, e.g. `⌘K`. Matches real shadcn/ui's `kbd.tsx`
 * (`h-5 w-fit min-w-5 rounded-sm bg-muted px-1 text-xs font-medium`).
 *
 * Chrome is Style-driven — override with [style].
 *
 * Usage:
 * ```
 * Kbd("⌘")
 * KbdGroup { Kbd("Ctrl"); Kbd("K") }
 * ```
 */
@Composable
fun Kbd(
    text: String,
    modifier: Modifier = Modifier,
    style: Style = Style,
) {
    val styleState = remember { MutableStyleState(MutableInteractionSource()) }
    Row(
        modifier =
            modifier.styleable(
                styleState,
                rememberKbdStyle() then style,
            ),
        horizontalArrangement = Arrangement.Center,
        verticalAlignment = Alignment.CenterVertically,
    ) {
        Text(variant = TextVariant.LabelSmall, textStyle = TextColors.Muted) { text }
    }
}

@Composable
@JvmName("KbdLambda")
@OverloadResolutionByLambdaReturnType
fun Kbd(
    modifier: Modifier = Modifier,
    style: Style = Style,
    text: () -> String,
) {
    Kbd(text = text(), modifier = modifier, style = style)
}

/** Groups multiple [Kbd] labels for a multi-key shortcut, e.g. `Ctrl` `K`. */
@Composable
fun KbdGroup(
    modifier: Modifier = Modifier,
    style: Style = Style,
    content: @Composable () -> Unit,
) {
    val styleState = remember { MutableStyleState(MutableInteractionSource()) }
    Row(
        modifier = modifier.styleable(styleState, style),
        horizontalArrangement = Arrangement.spacedBy(twSpacing.xs2),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        content()
    }
}
