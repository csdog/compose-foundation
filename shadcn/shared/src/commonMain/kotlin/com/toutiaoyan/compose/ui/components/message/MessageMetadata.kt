@file:OptIn(ExperimentalFoundationStyleApi::class)

package com.toutiaoyan.compose.ui.components.message

import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.ColumnScope
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.RowScope
import androidx.compose.foundation.style.ExperimentalFoundationStyleApi
import androidx.compose.foundation.style.MutableStyleState
import androidx.compose.foundation.style.Style
import androidx.compose.foundation.style.styleable
import androidx.compose.foundation.style.then
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.LineHeightStyle
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.toutiaoyan.compose.tailwind.core.twSpacing
import com.toutiaoyan.compose.ui.components.text.LocalDefaultTextStyle
import com.toutiaoyan.compose.ui.theme.LocalContentColor
import com.toutiaoyan.compose.ui.theme.currentTheme
import com.toutiaoyan.compose.ui.tokens.StylePreset

@Composable
internal fun ColumnScope.MessageMetadata(
    modifier: Modifier,
    style: Style,
    content: @Composable RowScope.() -> Unit,
) {
    val align = LocalMessageAlign.current
    val novaOrRhea =
        currentTheme.activePreset == StylePreset.Nova || currentTheme.activePreset == StylePreset.Rhea
    val styleState = remember { MutableStyleState(MutableInteractionSource()) }
    Row(
        modifier =
            modifier
                .align(if (align == MessageAlign.End) Alignment.End else Alignment.Start)
                .styleable(styleState, rememberMessageMetadataStyle() then style),
        horizontalArrangement = Arrangement.spacedBy(if (novaOrRhea) 0.dp else twSpacing.xs),
        verticalAlignment = Alignment.CenterVertically,
        content = {
            CompositionLocalProvider(
                LocalContentColor provides
                    if (novaOrRhea) currentTheme.colors.mutedForeground else LocalContentColor.current,
                LocalDefaultTextStyle provides
                    if (novaOrRhea) {
                        TextStyle(
                            fontFamily = currentTheme.fontFamily, fontSize = 12.sp, lineHeight = 16.sp,
                            fontWeight = FontWeight.Medium,
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
        },
    )
}
