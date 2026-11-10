package com.toutiaoyan.compose.ui.verification

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalLayoutDirection
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.unit.LayoutDirection
import androidx.compose.ui.unit.dp
import com.toutiaoyan.compose.ui.catalog.catalogFontFamily
import com.toutiaoyan.compose.ui.components.toggle.Toggle
import com.toutiaoyan.compose.ui.components.toggle.ToggleVariant
import com.toutiaoyan.compose.ui.components.togglegroup.ToggleGroup
import com.toutiaoyan.compose.ui.components.togglegroup.ToggleGroupItem
import com.toutiaoyan.compose.ui.components.togglegroup.ToggleGroupOrientation
import com.toutiaoyan.compose.ui.components.togglegroup.ToggleGroupType
import com.toutiaoyan.compose.ui.theme.Theme
import com.toutiaoyan.compose.ui.theme.currentTheme
import com.toutiaoyan.compose.ui.tokens.BaseColor
import com.toutiaoyan.compose.ui.tokens.Radius
import com.toutiaoyan.compose.ui.tokens.StylePreset

/** Dedicated browser-verification route; never registered as a catalog recipe. */
data class ToggleGroupConnectedFixtureConfig(
    val vertical: Boolean,
    val rtl: Boolean,
) {
    companion object {
        fun fromPath(path: String): ToggleGroupConnectedFixtureConfig? =
            when (path) {
                "/__verification/toggle-group-connected/horizontal-ltr" ->
                    ToggleGroupConnectedFixtureConfig(false, false)
                "/__verification/toggle-group-connected/horizontal-rtl" ->
                    ToggleGroupConnectedFixtureConfig(false, true)
                "/__verification/toggle-group-connected/vertical-ltr" ->
                    ToggleGroupConnectedFixtureConfig(true, false)
                "/__verification/toggle-group-connected/vertical-rtl" ->
                    ToggleGroupConnectedFixtureConfig(true, true)
                else -> null
            }
    }
}

/** Actual native Outline controls, including a disabled middle item and external focus stops. */
@Composable
fun ToggleGroupConnectedFixture(
    config: ToggleGroupConnectedFixtureConfig,
    isDark: Boolean,
) {
    Theme(
        preset = StylePreset.Nova,
        baseColor = BaseColor.Neutral,
        baseRadius = Radius(10.dp),
        isDark = isDark,
        fontFamily = catalogFontFamily(),
        minTouchTarget = null,
    ) {
        CompositionLocalProvider(
            LocalLayoutDirection provides if (config.rtl) LayoutDirection.Rtl else LayoutDirection.Ltr,
        ) {
            Column(
                modifier = Modifier.fillMaxSize().background(currentTheme.colors.background).padding(32.dp),
                verticalArrangement = Arrangement.spacedBy(16.dp),
            ) {
                Toggle("Before", pressed = false, onPressedChange = {})
                ToggleGroup(
                    type = ToggleGroupType.Multiple,
                    variant = ToggleVariant.Outline,
                    spacing = 0.dp,
                    orientation =
                        if (config.vertical) ToggleGroupOrientation.Vertical else ToggleGroupOrientation.Horizontal,
                    modifier = Modifier.testTag("connected-outline"),
                ) {
                    ToggleGroupItem("a", "A")
                    ToggleGroupItem("b", "B", disabled = true)
                    ToggleGroupItem("c", "C")
                }
                Toggle("After", pressed = false, onPressedChange = {})
            }
        }
    }
}
