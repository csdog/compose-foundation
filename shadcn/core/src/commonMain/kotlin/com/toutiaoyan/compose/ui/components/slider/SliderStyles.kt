@file:OptIn(ExperimentalFoundationStyleApi::class)

package com.toutiaoyan.compose.ui.components.slider
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.style.ExperimentalFoundationStyleApi
import androidx.compose.foundation.style.Style
import androidx.compose.foundation.style.hovered
import androidx.compose.runtime.Composable
import com.toutiaoyan.compose.ui.utils.rememberTheme
import com.toutiaoyan.compose.ui.styles.border
import com.toutiaoyan.compose.ui.styles.focusRing
import com.toutiaoyan.compose.ui.styles.focusRingShadow

// Matches base-nova slider: track bg-muted h-1, range bg-primary, thumb
// size-3 border-ring bg-background, ring on hover and focus.
// disabled dimming is a single Modifier.alpha() on the whole slider in Slider.kt,
// not a per-style disabledDim() here -- the track/range/thumb must stay fully opaque
// relative to *each other* (so the thumb still cleanly occludes the fill beneath it)
// and only the whole composited control fades against the page behind it, matching
// how CSS's disabled:opacity-50 behaves on a real DOM subtree.
@Composable
fun rememberSliderTrackStyle(): Style =
    rememberTheme {
        Style {
            background(colors.muted)
            shape(RoundedCornerShape(radius.full))
        }
    }

@Composable
fun rememberSliderRangeStyle(): Style =
    rememberTheme {
        Style {
            background(colors.primary)
            shape(RoundedCornerShape(radius.full))
        }
    }

@Composable
fun rememberSliderThumbStyle(): Style =
    rememberTheme {
        Style {
            background(colors.background)
            border(colors.ring)
            hovered { dropShadow(focusRingShadow()) }
            focusRing(RoundedCornerShape(radius.full))
        }
    }
