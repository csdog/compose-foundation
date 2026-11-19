@file:OptIn(ExperimentalFoundationStyleApi::class)

package com.toutiaoyan.compose.ui.components.checkbox

import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.tween
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.style.ExperimentalFoundationStyleApi
import androidx.compose.foundation.style.Style
import androidx.compose.foundation.style.animate
import androidx.compose.foundation.style.then
import androidx.compose.foundation.style.triStateToggleIndeterminate
import androidx.compose.foundation.style.triStateToggleOn
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.state.ToggleableState
import androidx.compose.ui.unit.dp
import com.toutiaoyan.compose.ui.components.field.LocalFieldLabelChoice
import com.toutiaoyan.compose.ui.styles.border
import com.toutiaoyan.compose.ui.styles.disabledDim
import com.toutiaoyan.compose.ui.styles.focusRing
import com.toutiaoyan.compose.ui.theme.ThemeData
import com.toutiaoyan.compose.ui.theme.currentTheme
import com.toutiaoyan.compose.ui.tokens.StylePreset
import com.toutiaoyan.compose.ui.utils.rememberTheme

private val checkboxColorTransition = tween<Color>(CheckboxDefaults.ColorTransitionMillis, easing = FastOutSlowInEasing)

private fun ThemeData.uncheckedFill(): Color =
    if (activePreset == StylePreset.Nova && isDark) {
        colors.input.copy(alpha = colors.input.alpha * 0.3f)
    } else {
        Color.Transparent
    }

private fun ThemeData.uncheckedBorder(): Color = if (activePreset == StylePreset.Nova) colors.input else colors.border

@Composable
fun rememberCheckboxStyle(): Style {
    val colorStyle =
        rememberTheme {
            Style {
                animate(tween(CheckboxDefaults.ColorTransitionMillis, easing = FastOutSlowInEasing)) {
                    background(theme.uncheckedFill())
                    border(theme.uncheckedBorder())
                    triStateToggleOn {
                        background(colors.primary)
                        borderColor(colors.primary)
                    }
                    triStateToggleIndeterminate {
                        background(colors.primary)
                        borderColor(colors.primary)
                    }
                }
            }
        }
    return rememberCheckboxStyle(colorStyle)
}

@Composable
internal fun rememberCheckboxStyle(toggleState: ToggleableState): Style {
    val theme = currentTheme
    val hasSelection = toggleState != ToggleableState.Off
    val targetFill = if (hasSelection) theme.colors.primary else theme.uncheckedFill()
    val targetBorder = if (hasSelection) theme.colors.primary else theme.uncheckedBorder()
    // Style observes these values while resolving, so the animation schedules its own redraws.
    val fill = animateColorAsState(targetFill, checkboxColorTransition)
    val outline = animateColorAsState(targetBorder, checkboxColorTransition)
    val colorStyle =
        remember(fill, outline) {
            Style {
                background(fill.value)
                border(outline.value)
            }
        }
    return rememberCheckboxStyle(colorStyle)
}

@Composable
private fun rememberCheckboxStyle(colorStyle: Style): Style =
    rememberTheme(colorStyle) {
        val nova = theme.activePreset == StylePreset.Nova
        val controlShape = RoundedCornerShape(if (nova) 4.dp else radius.xs)
        colorStyle then
            Style {
                if (nova && LocalFieldLabelChoice.currentValue) {
                    // The wrapping label owns the focus-visible ring.
                    shape(controlShape)
                } else {
                    focusRing(controlShape)
                }
                disabledDim()
            }
    }
