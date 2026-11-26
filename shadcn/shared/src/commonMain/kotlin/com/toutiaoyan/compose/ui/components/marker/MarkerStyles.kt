@file:OptIn(ExperimentalFoundationStyleApi::class)

package com.toutiaoyan.compose.ui.components.marker

import androidx.compose.foundation.style.ExperimentalFoundationStyleApi
import androidx.compose.foundation.style.Style
import androidx.compose.runtime.Composable
import com.toutiaoyan.compose.ui.utils.rememberTheme
import com.toutiaoyan.compose.ui.tokens.StylePreset
import androidx.compose.ui.unit.dp

internal val StylePreset.usesNovaMarkerLayout: Boolean
    get() = this == StylePreset.Nova || this == StylePreset.Rhea

@Composable
fun MarkerVariant.rememberStyle(): Style {
    val receiver = this
    return rememberTheme(receiver) {
        val isNovaOrRhea = theme.activePreset.usesNovaMarkerLayout
        Style {
            if (isNovaOrRhea) {
                width(1f)
                minHeight(16.dp)
                contentColor(colors.mutedForeground)
            }
            if (receiver == MarkerVariant.Border) contentPaddingBottom(if (isNovaOrRhea) 8.dp else spacing.sm)
        }
    }
}

enum class MarkerVariant { Default, Separator, Border }
