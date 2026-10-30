@file:OptIn(ExperimentalFoundationStyleApi::class)

package com.toutiaoyan.compose.ui.components.drawer

import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.*
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
    if (currentTheme.activePreset == StylePreset.Nova) {
        val direction = LocalDrawerState.current.swipeDirection
        val vertical = direction.isVertical()
        Box(
            modifier.then(
                if (vertical) Modifier.fillMaxWidth().height(12.dp) else Modifier.width(12.dp).fillMaxHeight(),
            ).clearAndSetSemantics {
            },
            contentAlignment =
                when (direction) {
                    DrawerSwipeDirection.Down -> Alignment.BottomCenter
                    DrawerSwipeDirection.Up -> Alignment.TopCenter
                    DrawerSwipeDirection.Left -> Alignment.CenterStart
                    DrawerSwipeDirection.Right -> Alignment.CenterEnd
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
            modifier
                .clearAndSetSemantics { }
                .styleable(
                    styleState,
                    rememberDrawerSwipeHandleStyle() then style,
                ),
    )
}
