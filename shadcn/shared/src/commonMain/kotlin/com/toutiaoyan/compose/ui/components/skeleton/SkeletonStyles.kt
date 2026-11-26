@file:OptIn(ExperimentalFoundationStyleApi::class)

package com.toutiaoyan.compose.ui.components.skeleton

import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.runtime.remember
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.style.ExperimentalFoundationStyleApi
import androidx.compose.foundation.style.Style
import androidx.compose.foundation.style.then
import androidx.compose.runtime.Composable
import com.toutiaoyan.compose.tailwind.core.TwAnimate
import com.toutiaoyan.compose.ui.utils.rememberTheme

/**
 * Style "classes" for [Skeleton], composed like Tailwind utilities.
 *
 * Default [rememberSkeletonStyle] ≈ base-nova `rounded-md bg-muted animate-pulse`
 * (`.cn-skeleton` + `animate-pulse` on the component).
 * Drop pulse the CSS way — omit that utility:
 * `rememberSkeletonChrome()` ≈ `rounded-md bg-muted`.
 */
@Composable
fun rememberSkeletonChrome(): Style =
    rememberTheme {
        Style {
            shape(RoundedCornerShape(radius.md))
            background(colors.muted)
        }
    }

/** `animate-pulse` — opacity `1 ↔ 0.5`. */
@Composable
fun rememberSkeletonPulse(): Style {
    val pulseAlpha =
        rememberInfiniteTransition(label = "skeleton-pulse").animateFloat(
            initialValue = 1f,
            targetValue = 0.5f,
            animationSpec = TwAnimate.pulse,
            label = "skeleton-opacity",
        )
    return remember(pulseAlpha) { Style { alpha(pulseAlpha.value) } }
}

/** `rounded-md` + fill + `animate-pulse` — full default class set. */
@Composable
fun rememberSkeletonStyle(): Style = rememberSkeletonChrome() then rememberSkeletonPulse()
