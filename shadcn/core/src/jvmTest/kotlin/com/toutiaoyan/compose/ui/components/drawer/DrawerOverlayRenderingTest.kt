package com.toutiaoyan.compose.ui.components.drawer

import androidx.compose.animation.EnterTransition
import androidx.compose.animation.ExitTransition
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.toPixelMap
import androidx.compose.ui.test.captureToImage
import androidx.compose.ui.test.isRoot
import androidx.compose.ui.test.onLast
import com.toutiaoyan.compose.ui.ScreenshotTest
import com.toutiaoyan.compose.ui.components.overlay.OverlayHost
import com.toutiaoyan.compose.ui.components.overlay.OverlayWithOpacity
import com.toutiaoyan.compose.ui.components.overlay.TeleportPrimitive
import com.toutiaoyan.compose.ui.tokens.StylePreset
import kotlin.math.abs
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertTrue

class DrawerOverlayRenderingTest : ScreenshotTest() {
    @Test fun opacityFadesTheCapturedBackdropAndScrimTogether() {
        var opacity by mutableFloatStateOf(1f)
        setThemedContent(stylePreset = StylePreset.Nova) {
            OverlayHost {
                Canvas(Modifier.fillMaxSize()) {
                    repeat((size.width / 4).toInt() + 1) { index ->
                        drawRect(
                            if (index % 2 == 0) Color.White else Color.Black,
                            topLeft = Offset(index * 4f, 0f),
                            size = Size(4f, size.height),
                        )
                    }
                }
                TeleportPrimitive(visible = true, onDismissRequest = {}) {
                    OverlayWithOpacity(
                        Color.Black.copy(alpha = 0.1f),
                        opacity = { opacity },
                        enter = EnterTransition.None,
                        exit = ExitTransition.None,
                    )
                }
            }
        }

        fun sample(): Color = composeRule.onAllNodes(isRoot()).onLast().captureToImage().toPixelMap()[30, 40]

        val opaque = sample()
        composeRule.runOnIdle { opacity = 0f }
        val clear = sample()
        assertTrue(abs(opaque.red - clear.red) > 0.1f, "Backdrop blur must change the sharp stripe sample")
        composeRule.runOnIdle { opacity = 0.25f }
        val quarter = sample()
        assertEquals(clear.red * 0.75f + opaque.red * 0.25f, quarter.red, 0.015f)
    }
}
