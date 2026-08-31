@file:OptIn(ExperimentalFoundationStyleApi::class)

package com.toutiaoyan.compose.ui.components.drawer

import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.RowScope
import androidx.compose.foundation.style.ExperimentalFoundationStyleApi
import androidx.compose.foundation.style.MutableStyleState
import androidx.compose.foundation.style.Style
import androidx.compose.foundation.style.styleable
import androidx.compose.foundation.style.then
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import com.toutiaoyan.compose.tailwind.core.twSpacing

/** Action row at the bottom of a drawer — web `DrawerFooter`. */
@Composable
fun DrawerFooter(
    modifier: Modifier = Modifier,
    style: Style = Style,
    content: @Composable RowScope.() -> Unit,
) {
    val styleState = remember { MutableStyleState(MutableInteractionSource()) }
    Row(
        modifier = modifier.styleable(styleState, rememberDrawerFooterStyle() then style),
        horizontalArrangement = Arrangement.spacedBy(twSpacing.scale2, Alignment.End),
    ) {
        content()
    }
}
