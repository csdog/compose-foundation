@file:OptIn(androidx.compose.foundation.style.ExperimentalFoundationStyleApi::class)

package com.toutiaoyan.compose.ui.modifiers

import androidx.compose.foundation.IndicationNodeFactory
import androidx.compose.foundation.background
import androidx.compose.foundation.interaction.InteractionSource
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.interaction.PressInteraction
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.GenericShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.style.Style
import androidx.compose.foundation.style.size
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Shape
import androidx.compose.ui.graphics.shadow.Shadow
import androidx.compose.ui.graphics.toPixelMap
import androidx.compose.ui.node.DelegatableNode
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.test.captureToImage
import androidx.compose.ui.test.junit4.v2.createComposeRule
import androidx.compose.ui.test.onNodeWithTag
import androidx.compose.ui.unit.dp
import com.toutiaoyan.compose.ui.components.button.Button
import com.toutiaoyan.compose.ui.components.button.ButtonSize
import com.toutiaoyan.compose.ui.components.button.ButtonVariant
import com.toutiaoyan.compose.ui.theme.Theme
import io.github.takahirom.roborazzi.captureRoboImage
import org.junit.Rule
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertNotEquals
import kotlin.test.assertTrue

class RippleShapeTest {
    @get:Rule
    val composeRule = createComposeRule()

    @Test
    fun circleLight() = assertRipple("circle", CircleShape, false)

    @Test
    fun circleDark() = assertRipple("circle", CircleShape, true)

    @Test
    fun asymmetricCorners() = assertRipple("asymmetric", RoundedCornerShape(topStart = 20.dp), false)

    @Test
    fun genericOutline() =
        assertRipple(
            "triangle",
            GenericShape { size, _ ->
                moveTo(size.width / 2, 0f)
                lineTo(size.width, size.height)
                lineTo(0f, size.height)
                close()
            },
            false,
        )

    @Test
    fun ringRemainsOutsideShape() = assertRipple("circle_ring", CircleShape, false, ring = true)

    private fun assertRipple(
        name: String,
        shape: Shape,
        dark: Boolean,
        ring: Boolean = false,
    ) {
        val source = MutableInteractionSource()
        val indication =
            object : IndicationNodeFactory {
                override fun equals(other: Any?) = this === other

                override fun hashCode() = 0

                override fun create(interactionSource: InteractionSource): DelegatableNode {
                    return ripple(color = Color.Red).create(source)
                }
            }
        composeRule.setContent {
            Theme(isDark = dark) {
                Box(
                    modifier =
                        Modifier.size(
                            80.dp,
                        ).background(if (dark) Color.Black else Color.White).testTag("surface"),
                    contentAlignment = Alignment.Center,
                ) {
                    Button(
                        size = ButtonSize.Icon,
                        variant = ButtonVariant.Ghost,
                        indication = indication,
                        style =
                            Style {
                                size(40.dp)
                                shape(shape)
                                if (ring) dropShadow(Shadow(radius = 0.dp, spread = 3.dp, color = Color.Blue))
                            },
                    ) { }
                }
            }
        }
        val before = composeRule.onNodeWithTag("surface").captureToImage().toPixelMap()
        composeRule.mainClock.autoAdvance = false
        composeRule.runOnIdle { assertTrue(source.tryEmit(PressInteraction.Press(Offset(20f, 20f)))) }
        composeRule.mainClock.advanceTimeBy(300)
        val after = composeRule.onNodeWithTag("surface").captureToImage().toPixelMap()
        val scale = after.width / 80f

        fun px(v: Int) = (v * scale).toInt()
        if (ring) assertEquals(Color.Blue, after[px(40), px(18)], "focus-ring shadow must remain outside the clip")
        assertNotEquals(before[px(40), px(40)], after[px(40), px(40)], "press must actually paint a ripple")
        assertEquals(before[px(22), px(22)], after[px(22), px(22)], "ripple must not paint outside the resolved shape")
        composeRule.onNodeWithTag("surface").captureRoboImage(
            "src/jvmTest/snapshots/ripple_${name}_${if (dark) "dark" else "light"}.png",
        )
    }
}
