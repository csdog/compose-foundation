@file:OptIn(ExperimentalFoundationStyleApi::class, ExperimentalTypeInference::class)

package com.toutiaoyan.compose.ui.components.kbd

import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.style.ExperimentalFoundationStyleApi
import androidx.compose.foundation.style.MutableStyleState
import androidx.compose.foundation.style.Style
import androidx.compose.foundation.style.styleable
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.foundation.text.selection.DisableSelection
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.style.LineHeightStyle
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.toutiaoyan.compose.ui.components.icon.LocalInlineIconSize
import com.toutiaoyan.compose.ui.components.text.LocalDefaultTextStyle
import com.toutiaoyan.compose.ui.components.tooltip.LocalInsideTooltipContent
import com.toutiaoyan.compose.ui.theme.LocalContentColor
import com.toutiaoyan.compose.ui.theme.currentTheme
import com.toutiaoyan.compose.ui.tokens.StylePreset
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
    Kbd(modifier = modifier, style = style, content = {
        if (currentTheme.activePreset == StylePreset.Nova) {
            Text { text }
        } else {
            Text(variant = TextVariant.LabelSmall, textStyle = TextColors.Muted) { text }
        }
    })
}

/** Free-form key content, including icons. Does not create a button or shortcut handler. */
@Composable
@JvmName("KbdContent")
@OverloadResolutionByLambdaReturnType
fun Kbd(
    content: @Composable () -> Unit,
    modifier: Modifier = Modifier,
    style: Style = Style,
) {
    val styleState = remember { MutableStyleState(MutableInteractionSource()) }
    val nova = currentTheme.activePreset == StylePreset.Nova
    val inTooltip = LocalInsideTooltipContent.current
    val color = if (nova && inTooltip) currentTheme.colors.background else currentTheme.colors.mutedForeground
    val body = @Composable {
        Row(
            modifier = modifier.styleable(styleState, rememberKbdStyle(), style),
            horizontalArrangement =
                if (nova) {
                    Arrangement.spacedBy(
                        4.dp,
                        Alignment.CenterHorizontally,
                    )
                } else {
                    Arrangement.Center
                },
            verticalAlignment = Alignment.CenterVertically,
        ) {
            CompositionLocalProvider(
                LocalContentColor provides color,
                LocalInlineIconSize provides if (nova) 12.dp else LocalInlineIconSize.current,
                LocalDefaultTextStyle provides
                    if (nova) {
                        TextStyle(
                            fontFamily = currentTheme.fontFamily,
                            fontSize = 12.sp, lineHeight = 16.sp, fontWeight = FontWeight.Medium,
                            lineHeightStyle =
                                LineHeightStyle(
                                    LineHeightStyle.Alignment.Center,
                                    LineHeightStyle.Trim.None,
                                ),
                        )
                    } else {
                        LocalDefaultTextStyle.current
                    },
            ) { content() }
        }
    }
    if (nova) DisableSelection { body() } else body()
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
        horizontalArrangement =
            Arrangement.spacedBy(
                if (currentTheme.activePreset == StylePreset.Nova) {
                    4.dp
                } else {
                    twSpacing.xs2
                },
            ),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        CompositionLocalProvider(
            LocalDefaultTextStyle provides
                if (currentTheme.activePreset == StylePreset.Nova) {
                    LocalDefaultTextStyle.current.copy(fontFamily = FontFamily.Monospace)
                } else {
                    LocalDefaultTextStyle.current
                },
        ) { content() }
    }
}
