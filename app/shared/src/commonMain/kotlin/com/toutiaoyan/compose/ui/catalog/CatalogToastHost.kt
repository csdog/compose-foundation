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
import com.toutiaoyan.compose.ui.components.toast.ToastState
import com.toutiaoyan.compose.ui.components.toast.Toaster
import com.toutiaoyan.compose.ui.components.toast.rememberToastState

internal val LocalCatalogToastState = staticCompositionLocalOf<ToastState?> { null }

/** Window-level notifications shared by interactive catalog recipes. */
@Composable
internal fun CatalogToastHost(
    modifier: Modifier = Modifier,
    content: @Composable () -> Unit,
) {
    val state = rememberToastState()
    CompositionLocalProvider(LocalCatalogToastState provides state) {
        Box(modifier.fillMaxSize()) {
            content()
            Toaster(state, Modifier.align(Alignment.BottomEnd).padding(16.dp))
        }
    }
}
