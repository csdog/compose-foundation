package com.toutiaoyan.compose.ui.components.select

import androidx.compose.runtime.Composable
import androidx.compose.ui.geometry.Rect

/** On Web, suspend page scrolling while preserving the aligned popup's own scroller. */
@Composable
internal expect fun SelectAlignedScrollLock(popupBounds: () -> Rect?)
