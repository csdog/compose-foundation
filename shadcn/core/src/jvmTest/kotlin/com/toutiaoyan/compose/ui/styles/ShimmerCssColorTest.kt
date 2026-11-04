@file:OptIn(androidx.compose.foundation.style.ExperimentalFoundationStyleApi::class)

package com.toutiaoyan.compose.ui.styles

import java.io.File
import javax.imageio.ImageIO
import androidx.compose.ui.graphics.toAwtImage
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.toPixelMap
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.test.*
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.unit.sp
import com.toutiaoyan.compose.ui.ScreenshotTest
import com.toutiaoyan.compose.ui.components.text.Text
import kotlin.test.*

class ShimmerCssColorTest : ScreenshotTest() {
    private fun image() = composeRule.onNodeWithTag("text", useUnmergedTree = true).captureToImage().toPixelMap()

    private fun capture(name: String) {
        val file = File("build/reports/shimmer-color-r1/$name.png")
        file.parentFile.mkdirs()
        ImageIO.write(
            composeRule.onNodeWithTag(
                "text",
                useUnmergedTree =
                true,
            ).captureToImage().toAwtImage(),
            "png",
            file,
        )
    }

    private fun centerMinimum(channel: (Color) -> Float): Float {
        val image = image()
        return (image.width / 2 - 6..image.width / 2 + 6).minOf { x ->
            (2 until image.height - 2).minOf { y -> channel(image[x, y]) }
        }
    }

    private fun show(
        base: Color?,
        textColor: Color,
        highlight: Color? = null,
        dark: Boolean = false,
    ) {
        composeRule.mainClock.autoAdvance = false
        setThemedContent(darkTheme = dark) {
            Text(
                Modifier.testTag("text"),
                textStyle = TextStyle(color = textColor, fontFamily = FontFamily.Monospace, fontSize = 16.sp),
                style = rememberShimmerStyle(baseColor = base, color = highlight),
            ) { "MMMMMMMMMMMMMMMM" }
        }
    }

    @Test fun defaultBaseIsTheActualTextColor() {
        show(null, Color.Red)
        composeRule.mainClock.advanceTimeBy(64)
        capture("current-red-start")
        val image = image()
        assertTrue(
            (0 until image.width).any { x ->
                (0 until image.height).any { y -> image[x, y].run { red > .9f && green < .1f && blue < .1f } }
            },
        )
    }

    @Test fun lightDefaultPeakUsesCurrentColorAtTwentyPercentAlpha() {
        show(Color.Black, Color.Black)
        composeRule.mainClock.advanceTimeBy(1024)
        capture("light-alpha-peak")
        val gray = centerMinimum { it.red }
        assertTrue(gray in 0.68f..0.92f, "Black at20% alpha over white should be pale, actual=$gray")
    }

    @Test fun darkDefaultPeakUsesRelativeOklchLightness() {
        val gray = Color(.3f, .3f, .3f)
        show(gray, gray, dark = true)
        composeRule.mainClock.advanceTimeBy(1024)
        capture("dark-oklch-peak")
        val image = image()
        val peak =
            (image.width / 2 - 6..image.width / 2 + 6).maxOf { x ->
                (2 until image.height - 2).maxOf { y -> image[x, y].red }
            }
        assertTrue(peak > .6f, "Relative OKLCH L+0.4/min0.8 should visibly brighten, actual=$peak")
    }

    @Test fun explicitTranslucentPeakKeepsItsAlphaInsteadOfCompositingOverBase() {
        show(Color.Red, Color.Red, Color.Blue.copy(alpha = .5f))
        composeRule.mainClock.advanceTimeBy(1024)
        capture("explicit-blue-half-peak")
        val green = centerMinimum { it.green }
        assertTrue(green > .3f, "Half-blue peak must reveal white surface, not become opaque purple: $green")
    }
}
