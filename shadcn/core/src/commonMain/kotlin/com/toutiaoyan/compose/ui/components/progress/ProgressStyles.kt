@file:OptIn(ExperimentalFoundationStyleApi::class)

package com.toutiaoyan.compose.ui.components.progress

import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.style.ExperimentalFoundationStyleApi
import androidx.compose.foundation.style.Style
import androidx.compose.runtime.Composable
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import com.toutiaoyan.compose.ui.utils.rememberTheme

/** base-nova Progress track tokens (`.cn-progress-track`). */
object ProgressDefaults {
    /** Official track `h-1`. */
    val TrackHeight = 4.dp
}

@Composable
fun rememberProgressTrackStyle(height: Dp = ProgressDefaults.TrackHeight): Style =
    rememberTheme(height) {
        Style {
            width(1f)
            height(height)
            shape(RoundedCornerShape(radius.full))
            background(colors.muted)
        }
    }
