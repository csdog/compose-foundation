@file:OptIn(androidx.compose.foundation.style.ExperimentalFoundationStyleApi::class)

package com.toutiaoyan.compose.ui.components.avatar

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.padding
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
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

    private fun nova(dark: Boolean) =
        snapshot(
            "avatar_nova",
            darkTheme = dark,
            stylePreset = com.toutiaoyan.compose.ui.tokens.StylePreset.Nova,
        ) {
            Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                AvatarSize.entries.forEach { size ->
                    Avatar(size = size) {
                        AvatarFallback("CN")
                        AvatarBadge()
                    }
                }
                AvatarGroup {
                    Avatar("CN")
                    Avatar("LR")
                    AvatarGroupCount("+3")
                }
            }
        }

    @Test fun nova_light() = nova(false)

    @Test fun nova_dark() = nova(true)

    private fun groupSpacedAndMixed(darkTheme: Boolean) {
        snapshot("avatar_group_spaced_and_mixed", darkTheme = darkTheme) {
            Box(Modifier.background(Color(0xFFF4C7CC)).padding(12.dp)) {
                Column(verticalArrangement = Arrangement.spacedBy(16.dp)) {
                    AvatarGroup(spacing = 8.dp) {
                        Avatar("CN")
                        Avatar("LR")
                    }
                    AvatarGroup(spacing = 4.dp) {
                        Avatar(size = AvatarSize.Sm) { AvatarFallback("SM") }
                        Avatar(size = AvatarSize.Lg) { AvatarFallback("LG") }
                        AvatarGroupCount("+3")
                    }
                }
            }
        }
    }

    @Test fun group_spaced_and_mixed_light() = groupSpacedAndMixed(darkTheme = false)

    @Test fun group_spaced_and_mixed_dark() = groupSpacedAndMixed(darkTheme = true)
}
