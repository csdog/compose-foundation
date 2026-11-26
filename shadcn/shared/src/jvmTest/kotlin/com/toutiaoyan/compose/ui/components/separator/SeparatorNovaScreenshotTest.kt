@file:OptIn(androidx.compose.foundation.style.ExperimentalFoundationStyleApi::class)

package com.toutiaoyan.compose.ui.components.separator

import androidx.compose.foundation.layout.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.toutiaoyan.compose.ui.ScreenshotTest
import com.toutiaoyan.compose.ui.components.text.Text
import com.toutiaoyan.compose.ui.tokens.StylePreset
import kotlin.test.Test

class SeparatorNovaScreenshotTest : ScreenshotTest() {
    @Composable private fun content(vertical: Boolean) {
        if (vertical) {
            Row(
                Modifier.height(20.dp),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(16.dp),
            ) {
                Text(textStyle = TextStyle(fontSize = 14.sp)) { "Blog" }
                Separator(orientation = SeparatorOrientation.Vertical)
                Text(textStyle = TextStyle(fontSize = 14.sp)) { "Docs" }
                Separator(orientation = SeparatorOrientation.Vertical)
                Text(textStyle = TextStyle(fontSize = 14.sp)) { "Source" }
            }
        } else {
            Column(Modifier.width(320.dp), verticalArrangement = Arrangement.spacedBy(16.dp)) {
                Text { "Content above" }
                Separator()
                Text { "Content below" }
            }
        }
    }

    @Test fun horizontalLight() =
        snapshot(
            "separator_nova_horizontal",
            stylePreset = StylePreset.Nova,
        ) { content(false) }

    @Test fun horizontalDark() =
        snapshot(
            "separator_nova_horizontal",
            darkTheme = true,
            stylePreset = StylePreset.Nova,
        ) { content(false) }

    @Test fun verticalLight() = snapshot("separator_nova_vertical", stylePreset = StylePreset.Nova) { content(true) }

    @Test fun verticalDark() =
        snapshot(
            "separator_nova_vertical",
            darkTheme = true,
            stylePreset = StylePreset.Nova,
        ) { content(true) }
}
