@file:OptIn(androidx.compose.foundation.style.ExperimentalFoundationStyleApi::class)

package com.toutiaoyan.compose.ui.catalog

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.composables.icons.lucide.Lucide
import com.composables.icons.lucide.Search
import com.toutiaoyan.compose.tailwind.core.twSpacing
import com.toutiaoyan.compose.ui.catalog.docs.DocIcon
import com.toutiaoyan.compose.ui.components.input.Input
import com.toutiaoyan.compose.ui.components.inputgroup.InputGroup
import com.toutiaoyan.compose.ui.components.inputgroup.InputGroupAddon
import com.toutiaoyan.compose.ui.components.inputgroup.InputGroupAddonAlign
import com.toutiaoyan.compose.ui.components.sidebar.SidebarCollapsible
import com.toutiaoyan.compose.ui.components.sidebar.SidebarGroup
import com.toutiaoyan.compose.ui.components.sidebar.SidebarMenu
import com.toutiaoyan.compose.ui.components.sidebar.SidebarMenuItem
import com.toutiaoyan.compose.ui.components.sidebar.SidebarProvider
import com.toutiaoyan.compose.ui.components.text.Text
import com.toutiaoyan.compose.ui.components.text.TextColors
import com.toutiaoyan.compose.ui.components.text.TextVariant
import com.toutiaoyan.compose.ui.navigation.RouteKey
import com.toutiaoyan.compose.ui.theme.currentTheme

/**
 * Persistent left-hand nav rail listing every catalog entry, grouped by category --
 * stays on screen while [com.toutiaoyan.compose.ui.navigation.CatalogNavDisplay]
 * swaps only the content pane. This is the shadcn/ui docs-site pattern, not a
 * push/pop full-screen list.
 *
 * Built from this library's own [SidebarGroup]/[SidebarMenu] rather than a
 * hand-rolled list -- the catalog app should dogfood its own component library for its
 * chrome, not just document it.
 *
 * [query]/[onQueryChange] are hoisted by the caller (not a local `remember` here) since
 * [com.toutiaoyan.compose.ui.navigation.CatalogNavDisplay] composes two separate
 * instances of this composable (a persistent desktop sidebar and a mobile drawer overlay)
 * that need to share one query rather than each starting from a fresh empty string.
 */
@Composable
fun CatalogSidebar(
    currentRoute: RouteKey,
    onEntryClick: (route: RouteKey) -> Unit,
    query: String,
    onQueryChange: (String) -> Unit,
    modifier: Modifier = Modifier,
) {
    // Two-step "filter items, then drop empty groups" idiom, matching
    // Command's own filtering (query matches item labels; empty groups drop)
    // so the catalog's own chrome stays consistent with the library component.
    val filteredEntriesByCategory =
        remember(query) {
            catalogEntriesByCategory
                .mapValues { (_, entries) -> entries.filter { it.title.contains(query, ignoreCase = true) } }
                .filterValues { it.isNotEmpty() }
        }

    // SidebarGroup* need LocalSidebarState. fillMaxSize=false: this rail is a fixed
    // 240dp sibling in CatalogNavHost's Row; default fillMaxSize would steal width.
    SidebarProvider(collapsible = SidebarCollapsible.None, fillMaxSize = false) {
        Column(
            modifier =
                modifier
                    .width(240.dp)
                    .fillMaxHeight()
                    // colors.sidebar/sidebarAccent, matching Sidebar/SidebarMenu's
                    // own semantic tokens -- not muted/secondary, which weren't
                    // designed to contrast against each other the way this pair is.
                    .background(currentTheme.colors.sidebar)
                    .verticalScroll(rememberScrollState())
                    .padding(twSpacing.md),
            verticalArrangement = Arrangement.spacedBy(twSpacing.md),
        ) {
            InputGroup(modifier = Modifier.fillMaxWidth()) {
                InputGroupAddon(InputGroupAddonAlign.InlineStart) {
                    DocIcon(Lucide.Search)
                }
                Input(
                    value = query,
                    onValueChange = onQueryChange,
                    placeholder = "Search components...",
                )
            }
            if (filteredEntriesByCategory.isEmpty()) {
                Text(variant = TextVariant.BodySmall, textStyle = TextColors.Muted) { "No components found." }
            } else {
                filteredEntriesByCategory.forEach { (category, entries) ->
                    SidebarGroup(label = category.title) {
                        SidebarMenu {
                            entries.forEach { entry ->
                                SidebarMenuItem(
                                    text = entry.title,
                                    onClick = { onEntryClick(entry.route) },
                                    isActive = entry.route == currentRoute,
                                    badge = if (entry.isNew) "New" else null,
                                )
                            }
                        }
                    }
                }
            }
        }
    }
}
