package com.toutiaoyan.compose.ui.components.sheet

import android.graphics.Bitmap
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.ui.Modifier
import androidx.compose.ui.semantics.SemanticsActions
import androidx.compose.ui.test.junit4.v2.createComposeRule
import androidx.compose.ui.test.onNodeWithText
import androidx.compose.ui.test.performSemanticsAction
import androidx.test.platform.app.InstrumentationRegistry
import com.toutiaoyan.compose.ui.theme.Theme
import com.toutiaoyan.compose.ui.theme.currentTheme
import com.toutiaoyan.compose.ui.tokens.StylePreset
import java.io.File
import org.junit.Rule
import org.junit.Test

private const val CAPTURE_END_MILLIS = 480
private const val CAPTURE_STEP_MILLIS = 16
private const val FRAME_NUMBER_DIGITS = 3
private const val FULL_IMAGE_QUALITY = 100

class SheetAndroidAnimationTest {
    @get:Rule
    val compose = createComposeRule()

    @Test
    fun captureSheetOpening() {
        val instrumentation = InstrumentationRegistry.getInstrumentation()
        val storage = checkNotNull(instrumentation.targetContext.getExternalFilesDir(null))
        val output = File(storage, "sheet-animation")
        check(output.isDirectory || output.mkdirs())

        compose.setContent {
            Theme(preset = StylePreset.Nova, isDark = false) {
                Box(Modifier.fillMaxSize().background(currentTheme.colors.background)) {
                    Sheet {
                        SheetTrigger("Edit profile")
                        SheetContent(side = SheetSide.End) {
                            SheetHeader("Profile", description = "Update your profile details.")
                        }
                    }
                }
            }
        }
        compose.waitForIdle()

        fun capture(name: String) {
            compose.waitForIdle()
            val screenshot = checkNotNull(instrumentation.uiAutomation.takeScreenshot())
            try {
                output.resolve("$name.png").outputStream().use {
                    check(screenshot.compress(Bitmap.CompressFormat.PNG, FULL_IMAGE_QUALITY, it))
                }
            } finally {
                screenshot.recycle()
            }
        }

        val clock = compose.mainClock
        clock.autoAdvance = false
        try {
            capture("before")
            val start = clock.currentTime
            compose.onNodeWithText("Edit profile").performSemanticsAction(SemanticsActions.OnClick)
            for (time in 0..CAPTURE_END_MILLIS step CAPTURE_STEP_MILLIS) {
                clock.advanceTimeBy(start + time - clock.currentTime)
                capture(time.toString().padStart(FRAME_NUMBER_DIGITS, '0'))
            }
        } finally {
            clock.autoAdvance = true
        }
    }
}
