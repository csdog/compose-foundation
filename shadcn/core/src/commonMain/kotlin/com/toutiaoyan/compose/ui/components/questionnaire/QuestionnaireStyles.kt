@file:OptIn(ExperimentalFoundationStyleApi::class)

package com.toutiaoyan.compose.ui.components.questionnaire

import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.style.ExperimentalFoundationStyleApi
import androidx.compose.foundation.style.Style
import androidx.compose.foundation.style.contentPadding
import androidx.compose.foundation.style.hovered
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.style.LineHeightStyle
import androidx.compose.ui.unit.TextUnit
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.toutiaoyan.compose.ui.tokens.StylePreset
import com.toutiaoyan.compose.ui.styles.border
import com.toutiaoyan.compose.ui.styles.focusRing
import com.toutiaoyan.compose.ui.utils.rememberTheme

/** Choice chrome matching shadcn `cn-questionnaire-choice` (`min-h-11`, bordered row). */
@Composable
fun rememberQuestionnaireChoiceStyle(
    selected: Boolean,
    invalid: Boolean = false,
): Style {
    return rememberTheme(selected, invalid) {
        val nova = theme.activePreset == StylePreset.Nova
        Style {
            width(1f)
            val choiceShape = RoundedCornerShape(if (nova) radius.lg else radius.md)
            shape(choiceShape)
            if (nova) {
                contentPadding(horizontal = 12.dp, vertical = 10.dp)
                background(
                    if (selected) {
                        colors.muted
                    } else if (theme.isDark) {
                        colors.input.copy(alpha = colors.input.alpha * 0.2f)
                    } else {
                        Color.Transparent
                    },
                )
                border(
                    if (invalid) {
                        colors.destructive
                    } else if (selected) {
                        colors.primary.copy(alpha = colors.primary.alpha * 0.4f)
                    } else {
                        colors.input
                    },
                )
                hovered { background(colors.muted.copy(alpha = colors.muted.alpha * 0.5f)) }
                focusRing(choiceShape)
            } else if (selected) {
                background(colors.accent)
                border(colors.ring)
            } else {
                background(colors.card)
                border(colors.border)
            }
        }
    }
}

@Composable
internal fun rememberQuestionnaireTypography(
    size: TextUnit,
    lineHeight: TextUnit,
    weight: FontWeight =
        FontWeight.Normal,
): TextStyle =
    rememberTheme(size, lineHeight, weight) {
        TextStyle(
            fontSize = size,
            lineHeight = lineHeight,
            fontWeight = weight,
            lineHeightStyle = LineHeightStyle(LineHeightStyle.Alignment.Center, LineHeightStyle.Trim.None),
        )
    }

@Composable
internal fun rememberQuestionnaireShortcutStyle(): Style =
    rememberTheme {
        Style {
            width(20.dp)
            height(20.dp)
            shape(RoundedCornerShape(radius.md))
            contentPadding(0.dp)
            background(colors.background)
            contentColor(colors.mutedForeground)
            border(colors.input)
            dropShadow()
            fontSize(10.sp)
            lineHeight(10.sp)
            fontWeight(FontWeight.Medium)
            fontFamily(FontFamily.Monospace)
        }
    }
