@file:OptIn(ExperimentalFoundationStyleApi::class)

package com.toutiaoyan.compose.ui.components.checkbox
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.style.ExperimentalFoundationStyleApi
import androidx.compose.foundation.style.Style
import androidx.compose.foundation.style.triStateToggleIndeterminate
import androidx.compose.foundation.style.triStateToggleOn
import androidx.compose.runtime.Composable
import androidx.compose.ui.unit.dp
import com.toutiaoyan.compose.ui.tokens.StylePreset
import androidx.compose.ui.graphics.Color
import com.toutiaoyan.compose.ui.utils.rememberTheme
import com.toutiaoyan.compose.ui.components.field.LocalFieldLabelChoice
import com.toutiaoyan.compose.ui.styles.border
import com.toutiaoyan.compose.ui.styles.disabledDim
import com.toutiaoyan.compose.ui.styles.focusRing

// Matches shadcn/ui's real checkbox.tsx: border border-input (1.dp, constant),
// data-[state=checked]:border-primary data-[state=checked]:bg-primary,
// focus-visible:border-ring focus-visible:ring-[3px] focus-visible:ring-ring/50.
@Composable
fun rememberCheckboxStyle(): Style =
    rememberTheme {
        Style {
            background(
                if (theme.activePreset == StylePreset.Nova) {
                    if (theme.isDark) colors.input.copy(alpha = colors.input.alpha * 0.3f) else Color.Transparent
                } else {
                    Color.Transparent
                },
            )
            border(if (theme.activePreset == StylePreset.Nova) colors.input else colors.border)
            triStateToggleOn {
                background(colors.primary)
                borderColor(colors.primary)
            }
            triStateToggleIndeterminate {
                background(colors.primary)
                borderColor(colors.primary)
            }
            val controlShape = RoundedCornerShape(if (theme.activePreset == StylePreset.Nova) 4.dp else radius.xs)
            if (theme.activePreset == StylePreset.Nova && LocalFieldLabelChoice.currentValue) {
                // The wrapping label owns the focus-visible ring, as in group-has-focus-visible/field-label.
                shape(controlShape)
            } else {
                focusRing(controlShape)
            }
            disabledDim()
        }
    }
