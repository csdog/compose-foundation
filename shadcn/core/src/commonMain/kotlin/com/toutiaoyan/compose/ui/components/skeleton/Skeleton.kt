@file:OptIn(ExperimentalFoundationStyleApi::class)

package com.toutiaoyan.compose.ui.components.skeleton

import androidx.compose.foundation.background
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.style.ExperimentalFoundationStyleApi
import androidx.compose.foundation.style.MutableStyleState
import androidx.compose.foundation.style.Style
import androidx.compose.foundation.style.styleable
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import com.toutiaoyan.compose.ui.styles.shimmer
import com.toutiaoyan.compose.ui.theme.Theme
import com.toutiaoyan.compose.ui.theme.currentTheme
import com.toutiaoyan.compose.ui.tokens.BaseColor
import com.toutiaoyan.compose.tailwind.core.dp
import com.toutiaoyan.compose.tailwind.core.spacing

/**
 * A placeholder block for loading content.
 *
 * Matches real shadcn/ui base-nova `skeleton.tsx`:
 * `cn("cn-skeleton animate-pulse", className)` → `rounded-md bg-muted animate-pulse`.
 *
 * Default [style] is [rememberSkeletonStyle] (`chrome + pulse`). Passing [style]
 * **replaces that whole class set** — same as swapping `className` instead of
 * fighting utilities with a later `opacity-100`:
 *
 * ```
 * // omit animate-pulse
 * Skeleton(style = rememberSkeletonChrome(), modifier = Modifier.size(200.dp, 20.dp).shimmer())
 * // keep pulse, tweak shape
 * Skeleton(style = rememberSkeletonStyle() then Style { shape(CircleShape) })
 * ```
 *
 * Size stays on [modifier].
 */
@Composable
fun Skeleton(
    modifier: Modifier = Modifier,
    style: Style? = null,
) {
    val styleState = remember { MutableStyleState(MutableInteractionSource()) }
    Box(
        modifier =
            modifier.styleable(
                styleState,
                style ?: rememberSkeletonStyle(),
            ),
    )
}
