package com.toutiaoyan.compose.ui.components.drawer

import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.mutableStateMapOf
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.snapshotFlow
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.setValue
import kotlinx.coroutines.flow.collectLatest
import androidx.compose.runtime.remember
import androidx.compose.runtime.staticCompositionLocalOf
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.unit.dp
import com.toutiaoyan.compose.ui.theme.currentTheme

internal class DrawerIndentState {
    private val drawers = mutableStateMapOf<Any, () -> Float?>()
    val active: Boolean get() = drawers.isNotEmpty()
    val progress: Float get() = drawers.values.maxOfOrNull { (it() ?: 1f).coerceIn(0f, 1f) } ?: 0f
    val followsGesture: Boolean get() = drawers.values.any { it() != null }

    fun register(
        key: Any,
        progress: () -> Float? = { null },
    ) {
        drawers[key] = progress
    }

    fun unregister(key: Any) {
        drawers.remove(key)
    }
}

internal class DrawerIndentGesture {
    var progress by mutableStateOf<() -> Float?>({ null })
}

internal val LocalDrawerIndentState = staticCompositionLocalOf<DrawerIndentState?> { null }

/**
 * Wrap the application surface inside OverlayHost, so portals remain full size.
 * Drawers opt in with `indentBackground = true`. Multiple open drawers keep the
 * surface indented until the last opted-in drawer closes or leaves composition.
 * Scale and corner radius follow the panel's drag and settle progress directly.
 * Snap drawers restore the surface only between the lowest snap and dismissal.
 */
@Composable
fun DrawerIndentHost(
    modifier: Modifier = Modifier,
    content: @Composable () -> Unit,
) {
    val state = remember { DrawerIndentState() }
    val progress = remember { Animatable(0f) }
    LaunchedEffect(state) {
        snapshotFlow { Triple(state.active, state.progress, state.followsGesture) }.collectLatest {
                (active, amount, followsGesture) ->
            if (active && followsGesture) {
                // The anchored draggable already owns drag and spring frames.
                progress.snapTo(amount)
            } else {
                progress.animateTo(if (active) 1f else 0f, tween(DrawerMotionMillis, easing = DrawerMotionEasing))
            }
        }
    }
    val surface = currentTheme.colors.background
    CompositionLocalProvider(LocalDrawerIndentState provides state) {
        Box(modifier.background(Color.Black)) {
            Box(
                Modifier.fillMaxSize().graphicsLayer {
                    // Read anchored motion in the draw phase: snapshotFlow keeps the
                    // animation's starting value current, but must not gate drag frames.
                    val gestureProgress = state.progress
                    val p = if (state.active && state.followsGesture) gestureProgress else progress.value
                    val shortest = minOf(size.width, size.height)
                    val scale = if (shortest > 0f) (1f - 24.dp.toPx() / shortest).coerceIn(0f, 1f) else 1f
                    scaleX = 1f + (scale - 1f) * p
                    scaleY = scaleX
                    shape = RoundedCornerShape(24.dp * p)
                    clip = p > 0f
                }.background(surface),
            ) { content() }
        }
    }
}
