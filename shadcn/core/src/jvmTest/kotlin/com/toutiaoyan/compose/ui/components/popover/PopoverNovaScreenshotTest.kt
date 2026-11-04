package com.toutiaoyan.compose.ui.components.popover

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.size
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.toutiaoyan.compose.ui.ScreenshotTest
import com.toutiaoyan.compose.ui.components.button.ButtonVariant
import com.toutiaoyan.compose.ui.components.overlay.PopupAlign
import com.toutiaoyan.compose.ui.tokens.StylePreset
import kotlin.test.Test

class PopoverNovaScreenshotTest : ScreenshotTest() {
    private fun basic(dark: Boolean) {
        snapshot("popover_nova_basic", darkTheme = dark, stylePreset = StylePreset.Nova) {
            Box(Modifier.size(360.dp, 180.dp)) {
                Popover(open = true) {
                    PopoverTrigger("Open Popover", variant = ButtonVariant.Outline)
                    PopoverContent(align = PopupAlign.Start) {
                        PopoverHeader("Dimensions", "Set the dimensions for the layer.")
                    }
                }
            }
        }
    }

    @Test fun basicLight() = basic(false)

    @Test fun basicDark() = basic(true)
}
