@file:OptIn(androidx.compose.foundation.style.ExperimentalFoundationStyleApi::class)

package com.toutiaoyan.compose.ui.components.avatar

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.ui.unit.dp
import com.toutiaoyan.compose.ui.ScreenshotTest
import com.toutiaoyan.compose.ui.components.text.Text
import kotlin.test.Test

class AvatarScreenshotTest : ScreenshotTest() {
    private fun states(darkTheme: Boolean) {
        snapshot("avatar_states", darkTheme = darkTheme) {
            Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                Avatar(size = AvatarSize.Sm) {
                    AvatarFallback {
                        Text(variant = LocalAvatarSize.current.textStyle) { "SM" }
                    }
                }
                Avatar(size = AvatarSize.Default) {
                    AvatarFallback {
                        Text(variant = LocalAvatarSize.current.textStyle) { "MD" }
                    }
                }
                Avatar(size = AvatarSize.Lg) {
                    AvatarFallback {
                        Text(variant = LocalAvatarSize.current.textStyle) { "LG" }
                    }
                }
            }
        }
    }

    @Test
    fun states_light() = states(darkTheme = false)

    @Test
    fun states_dark() = states(darkTheme = true)

    private fun groupCount(darkTheme: Boolean) {
        snapshot("avatar_group_count", darkTheme = darkTheme) {
            AvatarGroup {
                Avatar("CN")
                Avatar("LR")
                Avatar("ER")
                AvatarGroupCount("+3")
            }
        }
    }

    @Test
    fun group_count_light() = groupCount(darkTheme = false)

    @Test
    fun group_count_dark() = groupCount(darkTheme = true)

    private fun overlap(darkTheme: Boolean) {
        snapshot("avatar_group_overlap", darkTheme = darkTheme) {
            AvatarGroup(spacing = AvatarGroupDefaults.Overlap) {
                Avatar("CN")
                Avatar("LR")
                Avatar("ER")
                AvatarGroupCount("+3")
            }
        }
    }

    @Test
    fun group_overlap_light() = overlap(darkTheme = false)

    @Test
    fun group_overlap_dark() = overlap(darkTheme = true)
}
