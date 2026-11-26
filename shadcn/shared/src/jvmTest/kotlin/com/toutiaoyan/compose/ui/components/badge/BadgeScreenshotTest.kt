@file:OptIn(androidx.compose.foundation.style.ExperimentalFoundationStyleApi::class)

package com.toutiaoyan.compose.ui.components.badge

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.unit.dp
import com.toutiaoyan.compose.ui.ScreenshotTest
import kotlin.test.Test
import com.toutiaoyan.compose.ui.components.text.Text

class BadgeScreenshotTest : ScreenshotTest() {
    private fun allVariants(darkTheme: Boolean) {
        snapshot("badge_variants", darkTheme = darkTheme) {
            Row(horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                listOf(
                    BadgeVariant.Default,
                    BadgeVariant.Secondary,
                    BadgeVariant.Destructive,
                    BadgeVariant.Outline,
                    BadgeVariant.Ghost,
                    BadgeVariant.Link,
                ).forEach { variant ->
                    Badge(variant = variant) { Text { variant::class.simpleName ?: "?" } }
                }
            }
        }
    }

    @Test
    fun variants_light() = allVariants(darkTheme = false)

    @Test
    fun variants_dark() = allVariants(darkTheme = true)

    @Test
    fun focused_light() {
        snapshotFocused("badge_focused", focusTag = "badge", darkTheme = false) {
            Badge("Focus me", onClick = {}, modifier = Modifier.testTag("badge"))
        }
    }

    @Test
    fun focused_dark() {
        snapshotFocused("badge_focused", focusTag = "badge", darkTheme = true) {
            Badge("Focus me", onClick = {}, modifier = Modifier.testTag("badge"))
        }
    }
}
