@file:OptIn(androidx.compose.foundation.style.ExperimentalFoundationStyleApi::class)

package com.toutiaoyan.compose.ui.components.button

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.test.onNodeWithTag
import androidx.compose.ui.test.performTouchInput
import androidx.compose.ui.unit.dp
import com.toutiaoyan.compose.ui.ScreenshotTest
import com.toutiaoyan.compose.ui.icons.Check
import kotlin.test.Test
import com.toutiaoyan.compose.ui.components.icon.Icon
import com.toutiaoyan.compose.ui.components.text.Text

class ButtonScreenshotTest : ScreenshotTest() {
    private fun allVariants(
        darkTheme: Boolean,
        enabled: Boolean = true,
    ) {
        val suffix = if (enabled) "" else "_disabled"
        snapshot("button_variants$suffix", darkTheme = darkTheme) {
            Row(horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                listOf(
                    ButtonVariant.Default,
                    ButtonVariant.Secondary,
                    ButtonVariant.Outline,
                    ButtonVariant.Ghost,
                    ButtonVariant.Destructive,
                    ButtonVariant.Link,
                ).forEach { variant ->
                    Button(onClick = {}, variant = variant, enabled = enabled) {
                        Text { variant::class.simpleName ?: "?" }
                    }
                }
            }
        }
    }

    @Test
    fun variants_light() = allVariants(darkTheme = false)

    @Test
    fun variants_dark() = allVariants(darkTheme = true)

    @Test
    fun variants_disabled_light() = allVariants(darkTheme = false, enabled = false)

    @Test
    fun variants_disabled_dark() = allVariants(darkTheme = true, enabled = false)

    /**
     * [Icon] with no explicit `tint` -- confirms it picks up each variant's ambient
     * [com.toutiaoyan.compose.ui.theme.LocalContentColor] (provided by
     * [Button]) instead of defaulting to a fixed color regardless of variant/theme.
     */
    private fun iconVariants(darkTheme: Boolean) {
        snapshot("button_icon_variants", darkTheme = darkTheme) {
            Row(horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                listOf(
                    ButtonVariant.Default,
                    ButtonVariant.Secondary,
                    ButtonVariant.Outline,
                    ButtonVariant.Ghost,
                    ButtonVariant.Destructive,
                ).forEach { variant ->
                    Button(onClick = {}, variant = variant) {
                        Icon(Check)
                        Text { variant::class.simpleName ?: "?" }
                    }
                }
            }
        }
    }

    @Test
    fun icon_variants_light() = iconVariants(darkTheme = false)

    @Test
    fun icon_variants_dark() = iconVariants(darkTheme = true)

    /** Square icon sizes — real shadcn `icon-xs` / `icon-sm` / `icon` / `icon-lg`. */
    private fun iconSizes(darkTheme: Boolean) {
        snapshot("button_icon_sizes", darkTheme = darkTheme) {
            Row(horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                listOf(
                    ButtonSize.IconXs,
                    ButtonSize.IconSm,
                    ButtonSize.Icon,
                    ButtonSize.IconLg,
                ).forEach { size ->
                    Button(onClick = {}, size = size) { Icon(Check) }
                }
            }
        }
    }

    @Test
    fun icon_sizes_light() = iconSizes(darkTheme = false)

    @Test
    fun icon_sizes_dark() = iconSizes(darkTheme = true)

    @Test
    fun focused_light() {
        snapshotFocused("button_focused", focusTag = "btn", darkTheme = false) {
            Button("Focus me", onClick = {}, modifier = Modifier.testTag("btn"))
        }
    }

    @Test
    fun focused_dark() {
        snapshotFocused("button_focused", focusTag = "btn", darkTheme = true) {
            Button("Focus me", onClick = {}, modifier = Modifier.testTag("btn"))
        }
    }

    /**
     * [com.toutiaoyan.compose.ui.styles.pressedMoveDown] nudges the button
     * down while held down -- driven by a real `down()` (held, no matching `up()`) so the
     * `pressed { }` state predicate actually fires, not a forced visual stand-in. Pairs
     * with [unpressed_baseline_light], identical content with no touch, so the two goldens
     * are directly diffable pixel-for-pixel to confirm the downward shift (not just
     * eyeballed).
     */
    @Test
    fun pressed_light() {
        setThemedContent(darkTheme = false) {
            Button("Push me", onClick = {}, modifier = Modifier.testTag("btn"))
        }
        composeRule.waitForIdle()
        composeRule.onNodeWithTag("btn").performTouchInput { down(center) }
        composeRule.waitForIdle()
        captureNamed("button_pressed", darkTheme = false)
    }

    @Test
    fun unpressed_baseline_light() {
        snapshot("button_unpressed_baseline", darkTheme = false) {
            Button("Push me", onClick = {}, modifier = Modifier.testTag("btn"))
        }
    }
}
