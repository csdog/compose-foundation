package com.toutiaoyan.compose.ui.styles

import androidx.compose.foundation.style.ExperimentalFoundationStyleApi
import androidx.compose.foundation.style.Style
import androidx.compose.foundation.style.StyleScope
import androidx.compose.foundation.style.pressed
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import com.toutiaoyan.compose.ui.components.button.ButtonAnimation

/**
 * Scale-down press affordance (RikkaUi [ButtonAnimation.Scale]).
 *
 * Uses Style's `animate { }` so press eases in and springs back on release.
 *
 * @param scale Target uniform scale while pressed (e.g. `0.97` subtle).
 */
@OptIn(ExperimentalFoundationStyleApi::class)
fun StyleScope.pressedScale(scale: Float = 0.97f) {
    animate {
        pressed {
            scale(scale)
        }
    }
}

/** Shared press-motion Style for interactive controls (chips, toggles, menu rows, …). */
@OptIn(ExperimentalFoundationStyleApi::class)
@Composable
fun rememberPressMotionStyle(animation: ButtonAnimation = ButtonAnimation.Scale): Style =
    remember(animation) {
        when (animation) {
            ButtonAnimation.None -> Style
            ButtonAnimation.Scale ->
                Style {
                    pressedScale(0.97f)
                }
            ButtonAnimation.Bounce ->
                Style {
                    pressedBounce()
                }
            ButtonAnimation.MoveDown ->
                Style {
                    pressedMoveDown()
                }
        }
    }
