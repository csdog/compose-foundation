@file:OptIn(ExperimentalFoundationStyleApi::class)

package com.toutiaoyan.compose.ui.components.breadcrumb

import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.tween
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.RowScope
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.style.ExperimentalFoundationStyleApi
import androidx.compose.foundation.style.Style
import androidx.compose.foundation.style.animate
import androidx.compose.foundation.style.focused
import androidx.compose.foundation.style.hovered
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.toutiaoyan.compose.ui.styles.focusRingShadow
import com.toutiaoyan.compose.ui.theme.currentTheme
import com.toutiaoyan.compose.ui.utils.rememberTheme

private val BreadcrumbListGap = 6.dp
private const val BREADCRUMB_HOVER_MILLIS = 150

@Composable
internal fun BreadcrumbFlow(
    modifier: Modifier,
    content: @Composable RowScope.() -> Unit,
) {
    FlowRow(
        modifier = modifier,
        horizontalArrangement = Arrangement.spacedBy(BreadcrumbListGap),
        verticalArrangement = Arrangement.spacedBy(BreadcrumbListGap),
        itemVerticalAlignment = Alignment.CenterVertically,
    ) { content() }
}

@Composable
internal fun rememberBreadcrumbLinkStyle(enabled: Boolean): Style {
    val focusRing = currentTheme.focusRingShadow()
    return rememberTheme(enabled, focusRing) {
        Style {
            shape(RoundedCornerShape(2.dp))
            contentColor(colors.mutedForeground)
            focused { dropShadow(focusRing) }
            animate(
                toSpec = tween(BREADCRUMB_HOVER_MILLIS, easing = FastOutSlowInEasing),
                fromSpec = tween(BREADCRUMB_HOVER_MILLIS, easing = FastOutSlowInEasing),
            ) {
                if (enabled) hovered { contentColor(colors.foreground) }
            }
        }
    }
}
