package com.toutiaoyan.compose.ui.tokens

import androidx.compose.animation.core.TweenSpec
import androidx.compose.runtime.Immutable

@Immutable
data class Animations(
    val defaultTransition: TweenSpec<Float>,
    val visibilityTransition: TweenSpec<Float>,
)
