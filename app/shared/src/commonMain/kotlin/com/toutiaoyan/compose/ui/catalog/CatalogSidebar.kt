@file:OptIn(androidx.compose.foundation.style.ExperimentalFoundationStyleApi::class)

package com.toutiaoyan.compose.ui.catalog

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.snapshotFlow
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.semantics.heading
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.unit.dp
import com.toutiaoyan.compose.ui.components.sidebar.SidebarCollapsible
import com.toutiaoyan.compose.ui.components.sidebar.SidebarMenuItem
import com.toutiaoyan.compose.ui.components.sidebar.SidebarMenu
import com.toutiaoyan.compose.ui.components.sidebar.SidebarProvider
import com.toutiaoyan.compose.ui.components.text.Text
import com.toutiaoyan.compose.ui.components.text.TextColors
import com.toutiaoyan.compose.ui.components.text.TextVariant
import com.toutiaoyan.compose.ui.navigation.RouteKey
import com.toutiaoyan.compose.ui.navigation.selectedComponentId
import com.toutiaoyan.compose.ui.theme.currentTheme
import kotlinx.coroutines.flow.first

/** Alphabetical navigation, preserving the current scroll position while it remains useful. */
@Composable
fun CatalogSidebar(
    currentRoute: RouteKey,
    onEntryClick: (RouteKey) -> Unit,
    modifier: Modifier = Modifier,
) {
    val listState = rememberLazyListState()
    val activeId = currentRoute.selectedComponentId
    LaunchedEffect(activeId) {
        val rows = catalogNavigationSections.flatMap { listOf<CatalogEntry?>(null) + it.entries }
        val index = rows.indexOfFirst { it?.route?.selectedComponentId == activeId }
        if (index >= 0) {
            snapshotFlow { listState.layoutInfo.totalItemsCount }.first { it > 0 }
            val layout = listState.layoutInfo
            val item = layout.visibleItemsInfo.firstOrNull { it.index == index }
            if (item == null ||
                item.offset < layout.viewportStartOffset ||
                item.offset +
                item.size > layout.viewportEndOffset
            ) {
                listState.scrollToItem((index - 5).coerceAtLeast(0))
            }
        }
    }
    SidebarProvider(
        collapsible =
            SidebarCollapsible.None,
        fillMaxSize =
        false,
        mobile =
        false,
        shortcutEnabled =
        false,
    ) {
        LazyColumn(
            state = listState,
            modifier =
                modifier.width(288.dp).fillMaxHeight()
                    .background(currentTheme.colors.background).testTag("catalog-sidebar"),
            contentPadding = PaddingValues(start = 26.dp, end = 24.dp, top = 32.dp, bottom = 32.dp),
            verticalArrangement = Arrangement.spacedBy(2.dp),
        ) {
            catalogNavigationSections.forEach { section ->
                item(key = section.title) {
                    Text(
                        variant = TextVariant.BodySmall,
                        textStyle = TextColors.Muted,
                        modifier = Modifier.padding(top = 16.dp, bottom = 10.dp).semantics { heading() },
                    ) { section.title }
                }
                section.entries.forEach { entry ->
                    item(key = entry.route.path) {
                        SidebarMenu {
                            SidebarMenuItem(
                                text = entry.title,
                                onClick = { onEntryClick(entry.route) },
                                isActive = entry.route.selectedComponentId == activeId,
                                badge = if (entry.isNew) "New" else null,
                            )
                        }
                    }
                }
            }
        }
    }
}
