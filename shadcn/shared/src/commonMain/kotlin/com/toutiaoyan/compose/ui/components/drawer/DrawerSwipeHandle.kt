@file:OptIn(ExperimentalFoundationStyleApi::class)

package com.toutiaoyan.compose.ui.components.drawer

import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.*
import androidx.compose.ui.AbsoluteAlignment
import androidx.compose.ui.Alignment
import androidx.compose.ui.unit.dp
import com.toutiaoyan.compose.ui.theme.currentTheme
import com.toutiaoyan.compose.ui.tokens.StylePreset
import androidx.compose.foundation.style.ExperimentalFoundationStyleApi
import androidx.compose.foundation.style.MutableStyleState
import androidx.compose.foundation.style.Style
import androidx.compose.foundation.style.styleable
import androidx.compose.foundation.style.then
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.semantics.clearAndSetSemantics

private const val DRAWER_HANDLE_FADE_MILLIS = 200

/**
 * Visual swipe affordance matching shadcn base-nova `DrawerSwipeHandle`.
 *
 * Painted automatically by [DrawerContent] when [Drawer] `showSwipeHandle` is true;
 * also exported so callers can place it manually in a custom Portal/Popup layout.
 */
@Composable
fun DrawerSwipeHandle(
    modifier: Modifier = Modifier,
    style: Style = Style,
) {
    val styleState = remember { MutableStyleState(MutableInteractionSource()) }
    val drawer = LocalDrawerState.current
    val nestHost = LocalDrawerNestHost.current
    val alpha =
        animateFloatAsState(
            if (drawer.nestedOpenCount > 0 && (nestHost?.nestedSwipeProgress ?: 0f) == 0f) 0f else 1f,
            tween(DRAWER_HANDLE_FADE_MILLIS),
            label = "Drawer covered handle",
        )
    val handleModifier = modifier.graphicsLayer { this.alpha = alpha.value }
    if (currentTheme.activePreset == StylePreset.Nova) {
        val direction = drawer.swipeDirection
        val vertical = direction.isVertical()
        Box(
            handleModifier.then(
                if (vertical) Modifier.fillMaxWidth().height(12.dp) else Modifier.width(12.dp).fillMaxHeight(),
            ).clearAndSetSemantics {
            },
            contentAlignment =
                when (direction) {
                    DrawerSwipeDirection.Down -> Alignment.BottomCenter
                    DrawerSwipeDirection.Up -> Alignment.TopCenter
                    DrawerSwipeDirection.Left -> AbsoluteAlignment.CenterLeft
                    DrawerSwipeDirection.Right -> AbsoluteAlignment.CenterRight
                },
        ) {
            Box(
                Modifier.styleable(
                    styleState,
                    rememberDrawerSwipeHandleStyle() then (
                        if (vertical) {
                            Style
                        } else {
                            Style {
                                width(4.dp)
                                height(96.dp)
                            }
                        }
                    ) then style,
                ),
            )
        }
        return
    }
    Box(
        modifier =
            handleModifier
                .clearAndSetSemantics { }
                .styleable(
                    styleState,
                    rememberDrawerSwipeHandleStyle() then style,
                ),
    )
}
