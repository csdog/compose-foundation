@file:OptIn(ExperimentalFoundationStyleApi::class)

package com.toutiaoyan.compose.ui.components.questionnaire

import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.style.ExperimentalFoundationStyleApi
import androidx.compose.foundation.style.Style
import androidx.compose.runtime.Composable
import com.toutiaoyan.compose.ui.styles.border
import com.toutiaoyan.compose.ui.utils.rememberTheme

/** Choice chrome matching shadcn `cn-questionnaire-choice` (`min-h-11`, bordered row). */
@Composable
fun rememberQuestionnaireChoiceStyle(selected: Boolean): Style {
    return rememberTheme(selected) {
        Style {
            width(1f)
            shape(RoundedCornerShape(radius.md))
            contentPadding(spacing.md)
            if (selected) {
                background(colors.accent)
                border(colors.ring)
            } else {
                background(colors.card)
                border(colors.border)
            }
        }
    }
}
