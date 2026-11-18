package com.toutiaoyan.compose.ui.components.dropdownmenu

import androidx.compose.animation.EnterTransition
import androidx.compose.animation.ExitTransition
import androidx.compose.runtime.Composable
import androidx.compose.ui.unit.Dp
import com.toutiaoyan.compose.ui.components.overlay.MenuDefaultEnter
import com.toutiaoyan.compose.ui.components.overlay.MenuDefaultExit
import com.toutiaoyan.compose.ui.components.overlay.MenuPopup
import com.toutiaoyan.compose.ui.components.overlay.PopupAlign
import com.toutiaoyan.compose.ui.components.overlay.PopupPlacement

internal val DropdownDefaultEnter = MenuDefaultEnter
internal val DropdownDefaultExit = MenuDefaultExit

@Composable
internal fun DropdownMenuPopup(
    open: Boolean,
    onDismissRequest: () -> Unit,
    placement: PopupPlacement,
    offset: Dp,
    align: PopupAlign,
    alignOffset: Dp,
    dismissOnClickOutside: Boolean,
    focusable: Boolean,
    enter: EnterTransition,
    exit: ExitTransition,
    owningNova: Boolean,
    content: @Composable () -> Unit,
) {
    MenuPopup(
        open, onDismissRequest, placement, offset, align, alignOffset,
        dismissOnClickOutside, focusable, enter, exit, owningNova, content = content,
    )
}
