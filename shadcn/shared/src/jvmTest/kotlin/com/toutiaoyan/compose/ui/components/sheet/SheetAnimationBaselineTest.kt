@file:OptIn(androidx.compose.foundation.style.ExperimentalFoundationStyleApi::class)

package com.toutiaoyan.compose.ui.components.sheet

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.toAwtImage
import androidx.compose.ui.semantics.SemanticsActions
import androidx.compose.ui.test.captureToImage
import androidx.compose.ui.test.onNodeWithText
import androidx.compose.ui.test.performSemanticsAction
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.platform.Font
import com.toutiaoyan.compose.ui.ScreenshotTest
import com.toutiaoyan.compose.ui.theme.Theme
import com.toutiaoyan.compose.ui.tokens.StylePreset
import java.io.File
import javax.imageio.ImageIO
import kotlin.test.Test

class SheetAnimationBaselineTest : ScreenshotTest() {
    @Test
    fun capturesOpeningTimeline() {
        setThemedContent(stylePreset = StylePreset.Nova) {
            Theme(preset = StylePreset.Nova, fontFamily = FontFamily(Font("fonts/noto_sans_arabic.ttf"))) {
                Box(Modifier.fillMaxSize()) {
                    Sheet {
                        SheetTrigger("Edit profile")
                        SheetContent(side = SheetSide.End) {
                            SheetHeader("Profile", description = "Update your profile details.")
                        }
                    }
                }
            }
        }
        composeRule.waitForIdle()

        val output = File("build/reports/animation-baselines/cmp-desktop/sheet-open/actual")
        check(output.isDirectory || output.mkdirs()) { "Cannot create $output" }
        val clock = composeRule.mainClock
        clock.autoAdvance = false
        try {
            captureFrame(output, "before")
            val startTime = clock.currentTime
            composeRule.onNodeWithText("Edit profile").performSemanticsAction(SemanticsActions.OnClick)
            // Include a settled tail so a slower candidate stays visible on the same timeline.
            for (elapsed in 0..480 step 16) {
                clock.advanceTimeBy(startTime + elapsed - clock.currentTime)
                captureFrame(output, elapsed.toString().padStart(3, '0'))
            }
        } finally {
            clock.autoAdvance = true
        }
    }

    private fun captureFrame(output: File, name: String) {
        val image = captureCompositeRoot().captureToImage().toAwtImage()
        check(ImageIO.write(image, "png", output.resolve("$name.png")))
    }
}
