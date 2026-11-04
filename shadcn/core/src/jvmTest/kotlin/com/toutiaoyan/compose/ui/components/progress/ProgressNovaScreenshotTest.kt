package com.toutiaoyan.compose.ui.components.progress

import androidx.compose.foundation.layout.width
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalLayoutDirection
import androidx.compose.ui.unit.LayoutDirection
import androidx.compose.ui.unit.dp
import com.toutiaoyan.compose.ui.ScreenshotTest
import com.toutiaoyan.compose.ui.tokens.StylePreset
import kotlin.test.Test

class ProgressNovaScreenshotTest : ScreenshotTest() {
    private fun label(
        dark: Boolean,
        rtl: Boolean,
    ) {
        snapshot("progress_nova_" + if (rtl) "rtl" else "label", darkTheme = dark, stylePreset = StylePreset.Nova) {
            CompositionLocalProvider(
                LocalLayoutDirection provides
                    if (rtl) {
                        LayoutDirection.Rtl
                    } else {
                        LayoutDirection.Ltr
                    },
            ) {
                Progress(0.56f, Modifier.width(320.dp)) {
                    ProgressLabel(if (rtl) "تقدم الرفع" else "Upload progress")
                    ProgressValue(if (rtl) "٥٦%" else "56%")
                }
            }
        }
    }

    @Test fun labelLight() = label(false, false)

    @Test fun labelDark() = label(true, false)

    @Test fun rtlLight() = label(false, true)

    @Test fun rtlDark() = label(true, true)
}
