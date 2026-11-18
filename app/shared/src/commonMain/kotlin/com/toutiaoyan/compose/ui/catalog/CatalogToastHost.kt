@file:OptIn(androidx.compose.foundation.style.ExperimentalFoundationStyleApi::class)

package com.toutiaoyan.compose.ui.catalog

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.staticCompositionLocalOf
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.toutiaoyan.compose.ui.components.toast.Toaster
import com.toutiaoyan.compose.ui.components.toast.ToastState
import com.toutiaoyan.compose.ui.components.toast.rememberToastState
import com.toutiaoyan.compose.ui.theme.Theme
import com.toutiaoyan.compose.ui.theme.currentTheme
import com.toutiaoyan.compose.ui.tokens.Accent
import com.toutiaoyan.compose.ui.tokens.BaseColor
import com.toutiaoyan.compose.ui.tokens.StylePreset

internal val LocalCatalogToastState = staticCompositionLocalOf<ToastState?> { null }

/** Window-level notifications shared by interactive catalog recipes. */
@Composable
internal fun CatalogToastHost(
    modifier: Modifier = Modifier,
    toastPreset: StylePreset? = null,
    toastBaseColor: BaseColor = currentTheme.activeBaseColor,
    toastAccent: Accent = currentTheme.activeAccent,
    content: @Composable () -> Unit,
) {
    val state = rememberToastState()
    Box(modifier.fillMaxSize()) {
        CompositionLocalProvider(LocalCatalogToastState provides state, content = content)
        val viewportModifier = Modifier.align(Alignment.BottomEnd).padding(16.dp)
        if (toastPreset == null) {
            Toaster(state, viewportModifier)
        } else {
            val chrome = currentTheme
            Theme(
                preset = toastPreset,
                baseColor = toastBaseColor,
                accent = toastAccent,
                baseRadius = chrome.baseRadius,
                isDark = chrome.isDark,
                fontFamily = chrome.fontFamily,
                minTouchTarget = chrome.minTouchTarget,
            ) {
                Toaster(state, viewportModifier)
            }
        }
    }
}
