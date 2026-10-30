package com.toutiaoyan.compose.ui.components.buttongroup

import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.runtime.staticCompositionLocalOf
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.layout
import androidx.compose.ui.focus.onFocusEvent
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.zIndex

/** shadcn `[&>*]:focus-visible:z-10` / `focus-visible:z-10` — raise a focused sibling's ring. */
internal val LocalRaiseFocusZIndex = staticCompositionLocalOf { false }

@Composable
internal fun Modifier.raiseFocusedSiblingZIndex(): Modifier {
    if (!LocalRaiseFocusZIndex.current) return this
    var focused by remember { mutableStateOf(false) }
    return onFocusEvent { focused = it.hasFocus }
        .then(if (focused) Modifier.zIndex(10f) else Modifier)
}

data class GroupCorners(
    val topStart: Dp = 0.dp,
    val topEnd: Dp = 0.dp,
    val bottomEnd: Dp = 0.dp,
    val bottomStart: Dp = 0.dp,
)

internal class GroupChildRegistry {
    val keys = mutableStateListOf<Any>()
}

@Composable
internal fun GroupChildRegistry.register(): Pair<Int, Int> {
    val key = remember { Any() }
    DisposableEffect(key) {
        keys.add(key)
        onDispose { keys.remove(key) }
    }
    val index = keys.indexOf(key)
    return (if (index < 0) 0 else index) to keys.size.coerceAtLeast(1)
}

internal fun groupItemCorners(
    index: Int,
    count: Int,
    orientation: ButtonGroupOrientation,
    rounded: Dp,
): GroupCorners {
    val none = 0.dp
    return when {
        count <= 1 -> GroupCorners(rounded, rounded, rounded, rounded)
        orientation == ButtonGroupOrientation.Horizontal ->
            when (index) {
                0 -> GroupCorners(topStart = rounded, topEnd = none, bottomEnd = none, bottomStart = rounded)
                count - 1 ->
                    GroupCorners(topStart = none, topEnd = rounded, bottomEnd = rounded, bottomStart = none)
                else -> GroupCorners(none, none, none, none)
            }
        else ->
            when (index) {
                0 -> GroupCorners(topStart = rounded, topEnd = rounded, bottomEnd = none, bottomStart = none)
                count - 1 ->
                    GroupCorners(topStart = none, topEnd = none, bottomEnd = rounded, bottomStart = rounded)
                else -> GroupCorners(none, none, none, none)
            }
    }
}

/** A CSS negative start margin: remove the occupied pixel as well as moving paint. */
internal fun Modifier.groupBorderOverlap(orientation: ButtonGroupOrientation): Modifier =
    layout { measurable, constraints ->
        val child = measurable.measure(constraints)
        val overlap = 1.dp.roundToPx()
        val horizontal = orientation == ButtonGroupOrientation.Horizontal
        layout(
            if (horizontal) (child.width - overlap).coerceAtLeast(0) else child.width,
            if (horizontal) child.height else (child.height - overlap).coerceAtLeast(0),
        ) {
            child.placeRelative(if (horizontal) -overlap else 0, if (horizontal) 0 else -overlap)
        }
    }
