@file:OptIn(androidx.compose.foundation.style.ExperimentalFoundationStyleApi::class)

package com.toutiaoyan.compose.ui.components.checkbox

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.unit.dp
import com.toutiaoyan.compose.ui.ScreenshotTest
import com.toutiaoyan.compose.ui.theme.Theme
import com.toutiaoyan.compose.ui.tokens.BaseColor
import com.toutiaoyan.compose.ui.tokens.Radius
import com.toutiaoyan.compose.ui.tokens.StylePreset
import kotlin.test.Test

class CheckboxScreenshotTest : ScreenshotTest() {
    private fun states(darkTheme: Boolean) {
        snapshot("checkbox_states", darkTheme = darkTheme, stylePreset = StylePreset.Nova) {
            CheckboxTheme(darkTheme) {
                Row(horizontalArrangement = Arrangement.spacedBy(16.dp)) {
                    Checkbox(checked = false, onCheckedChange = {})
                    Checkbox(checked = true, onCheckedChange = {})
                    Checkbox(checked = false, indeterminate = true, onCheckedChange = {})
                    Checkbox(checked = false, onCheckedChange = {}, enabled = false)
                    Checkbox(checked = true, onCheckedChange = {}, enabled = false)
                }
            }
        }
    }

    @Test
    fun states_light() = states(darkTheme = false)

    @Test
    fun states_dark() = states(darkTheme = true)

    @Test
    fun focused_light() = focused(darkTheme = false)

    @Test
    fun focused_dark() = focused(darkTheme = true)

    private fun focused(darkTheme: Boolean) {
        snapshotFocused("checkbox_focused", focusTag = "cb", darkTheme = darkTheme, stylePreset = StylePreset.Nova) {
            CheckboxTheme(darkTheme) {
                Checkbox(checked = false, onCheckedChange = {}, modifier = Modifier.testTag("cb"))
            }
        }
    }
}

@Composable
private fun CheckboxTheme(
    darkTheme: Boolean,
    content: @Composable () -> Unit,
) {
    Theme(
        preset = StylePreset.Nova,
        baseColor = BaseColor.Neutral,
        isDark = darkTheme,
        baseRadius = Radius(10.dp),
        minTouchTarget = null,
        content = content,
    )
}
