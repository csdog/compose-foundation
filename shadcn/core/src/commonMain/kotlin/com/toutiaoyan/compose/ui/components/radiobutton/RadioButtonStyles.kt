@file:OptIn(ExperimentalFoundationStyleApi::class)

package com.toutiaoyan.compose.ui.components.radiobutton
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.style.ExperimentalFoundationStyleApi
import androidx.compose.foundation.style.Style
import androidx.compose.foundation.style.selected
import androidx.compose.runtime.Composable
import com.toutiaoyan.compose.ui.tokens.StylePreset
import com.toutiaoyan.compose.ui.components.field.LocalFieldLabelChoice
import androidx.compose.ui.graphics.Color
import com.toutiaoyan.compose.ui.utils.rememberTheme
import com.toutiaoyan.compose.ui.styles.border
import com.toutiaoyan.compose.ui.styles.disabledDim
import com.toutiaoyan.compose.ui.styles.focusRing

// Matches shadcn/ui's real radio-group.tsx: border border-input (1.dp, constant,
// no hover state defined), focus-visible:border-ring focus-visible:ring-[3px]
// focus-visible:ring-ring/50.
@Composable
fun rememberRadioButtonStyle(): Style =
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
            selected {
                background(colors.primary)
                borderColor(colors.primary)
            }
            val controlShape = RoundedCornerShape(radius.full)
            if (theme.activePreset == StylePreset.Nova && LocalFieldLabelChoice.currentValue) {
                shape(controlShape)
            } else {
                focusRing(controlShape)
            }
            disabledDim()
        }
    }
