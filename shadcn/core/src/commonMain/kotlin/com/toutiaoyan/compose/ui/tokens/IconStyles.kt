package com.toutiaoyan.compose.ui.tokens

import androidx.compose.runtime.Immutable
import androidx.compose.ui.unit.Dp

@Immutable
data class IconStyles(
    val standardSize: Dp,
    val smallSize: Dp,
    val strokeWidth: Float,
)