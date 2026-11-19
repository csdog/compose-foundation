package com.toutiaoyan.compose.ui.components.buttongroup

import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateMapOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.runtime.staticCompositionLocalOf
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.unit.LayoutDirection
import androidx.compose.ui.layout.layout
import androidx.compose.ui.focus.onFocusEvent
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.zIndex

private const val FOCUSED_GROUP_ITEM_Z_INDEX = 10f

/** shadcn `[&>*]:focus-visible:z-10` / `focus-visible:z-10` — raise a focused sibling's ring. */
internal val LocalRaiseFocusZIndex = staticCompositionLocalOf { false }

@Composable
internal fun Modifier.raiseFocusedSiblingZIndex(): Modifier {
    if (!LocalRaiseFocusZIndex.current) return this
    var focused by remember { mutableStateOf(false) }
    return onFocusEvent { focused = it.hasFocus }
        .then(if (focused) Modifier.zIndex(FOCUSED_GROUP_ITEM_Z_INDEX) else Modifier)
}

data class GroupCorners(
    val topStart: Dp = 0.dp,
    val topEnd: Dp = 0.dp,
    val bottomEnd: Dp = 0.dp,
    val bottomStart: Dp = 0.dp,
)

internal class GroupChildRegistry {
    val keys = mutableStateListOf<Any>()
    private val positions = mutableStateMapOf<Any, Offset>()

    fun updatePosition(
        key: Any,
        position: Offset,
    ) {
        if (key in keys) positions[key] = position
    }

    fun remove(key: Any) {
        keys.remove(key)
        positions.remove(key)
    }

    fun visualIndex(
        key: Any,
        orientation: ButtonGroupOrientation,
        direction: LayoutDirection,
    ): Int {
        if (!keys.all { it in positions }) return keys.indexOf(key).coerceAtLeast(0)
        // Compare placed positions, since keyed children can move without recomposing or registering again.
        val ordered =
            keys.sortedBy {
                val position = positions.getValue(it)
                when {
                    orientation == ButtonGroupOrientation.Vertical -> position.y
                    direction == LayoutDirection.Rtl -> -position.x
                    else -> position.x
                }
            }
        return ordered.indexOf(key).coerceAtLeast(0)
    }
}

@Composable
internal fun GroupChildRegistry.registerKey(): Any {
    val key = remember { Any() }
    DisposableEffect(this, key) {
        keys.add(key)
        onDispose { remove(key) }
    }
    return key
}

@Composable
internal fun GroupChildRegistry.register(): Pair<Int, Int> {
    val key = registerKey()
    return keys.indexOf(key).coerceAtLeast(0) to keys.size.coerceAtLeast(1)
}

internal fun groupItemCorners(
    index: Int,
    count: Int,
    orientation: ButtonGroupOrientation,
    rounded: Dp,
): GroupCorners {
    if (count <= 1) return GroupCorners(rounded, rounded, rounded, rounded)
    val start = if (index == 0) rounded else 0.dp
    val end = if (index == count - 1) rounded else 0.dp
    return if (orientation == ButtonGroupOrientation.Horizontal) {
        GroupCorners(start, end, end, start)
    } else {
        GroupCorners(start, start, end, end)
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
