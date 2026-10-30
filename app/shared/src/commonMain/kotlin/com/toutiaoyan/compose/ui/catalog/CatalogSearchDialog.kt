@file:OptIn(androidx.compose.foundation.style.ExperimentalFoundationStyleApi::class)

package com.toutiaoyan.compose.ui.catalog

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.style.Style
import androidx.compose.foundation.style.contentPadding
import androidx.compose.foundation.style.fillWidth
import androidx.compose.foundation.verticalScroll
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.focus.focusRequester
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.shadow.Shadow
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.unit.IntOffset
import androidx.compose.ui.unit.IntSize
import androidx.compose.ui.unit.LayoutDirection
import androidx.compose.ui.unit.dp
import com.composables.icons.lucide.ArrowRight
import com.composables.icons.lucide.Lucide
import com.composables.icons.lucide.Search
import com.toutiaoyan.compose.tailwind.modifiers.shadowXl2
import com.toutiaoyan.compose.ui.catalog.docs.DocIcon
import com.toutiaoyan.compose.ui.components.command.Command
import com.toutiaoyan.compose.ui.components.command.CommandEmpty
import com.toutiaoyan.compose.ui.components.command.CommandGroup
import com.toutiaoyan.compose.ui.components.command.CommandInput
import com.toutiaoyan.compose.ui.components.command.CommandItem
import com.toutiaoyan.compose.ui.components.command.CommandList
import com.toutiaoyan.compose.ui.components.command.commandState
import com.toutiaoyan.compose.ui.components.inputgroup.InputGroup
import com.toutiaoyan.compose.ui.components.inputgroup.InputGroupAddon
import com.toutiaoyan.compose.ui.components.inputgroup.InputGroupAddonAlign
import com.toutiaoyan.compose.ui.components.kbd.Kbd
import com.toutiaoyan.compose.ui.components.overlay.Content
import com.toutiaoyan.compose.ui.components.overlay.Overlay
import com.toutiaoyan.compose.ui.components.overlay.TeleportPrimitive
import com.toutiaoyan.compose.ui.components.text.Text
import com.toutiaoyan.compose.ui.components.text.TextColors
import com.toutiaoyan.compose.ui.components.text.TextVariant
import com.toutiaoyan.compose.ui.navigation.RouteKey
import com.toutiaoyan.compose.ui.theme.currentTheme

/** Search is a site-specific Command composition, matching upstream command-menu.tsx. */
@Composable
fun CatalogSearchDialog(
    onDismiss: () -> Unit,
    onNavigate: (RouteKey) -> Unit,
) {
    val colors = currentTheme.colors
    val dark = currentTheme.isDark
    val shape = RoundedCornerShape(14.dp)
    TeleportPrimitive(visible = true, onDismissRequest = onDismiss) {
        // The upstream site's search has no visible scrim, but outside clicks still dismiss.
        Overlay(color = Color.Transparent)
        Content(contentAlignment = SearchPanelAlignment) {
            BoxWithConstraints(Modifier.padding(horizontal = 16.dp)) {
                val listHeight = minOf(324.dp, maxHeight * 0.6f)
                Command(
                    label = "Search documentation",
                    modifier =
                        Modifier.widthIn(max = 512.dp).fillMaxWidth().shadowXl2(shape)
                            .semantics { contentDescription = "Search documentation" },
                    style =
                        Style {
                            fillWidth()
                            shape(shape)
                            background(if (dark) colors.popover else colors.background)
                            borderWidth(0.dp)
                            contentPadding(0.dp)
                            dropShadow(Shadow(radius = 0.dp, spread = 4.dp, color = colors.border.copy(alpha = 0.8f)))
                        },
                ) {
                    val inputFocus = remember { FocusRequester() }
                    Box(Modifier.fillMaxWidth().padding(start = 8.dp, top = 8.dp, end = 8.dp)) {
                        InputGroup(
                            style =
                                Style {
                                    height(36.dp)
                                    shape(RoundedCornerShape(8.dp))
                                    background(colors.input.copy(alpha = 0.5f))
                                    borderColor(colors.input)
                                    dropShadow()
                                },
                        ) {
                            InputGroupAddon(
                                InputGroupAddonAlign.InlineStart,
                            ) { DocIcon(Lucide.Search, tint = colors.mutedForeground) }
                            CommandInput(
                                placeholder = "Search documentation...",
                                modifier = Modifier.focusRequester(inputFocus).testTag("catalog-search-input"),
                            )
                        }
                    }
                    LaunchedEffect(inputFocus) { inputFocus.requestFocus() }
                    val state = commandState()
                    CommandList(
                        Modifier.fillMaxWidth().heightIn(
                            max = listHeight,
                        ).verticalScroll(rememberScrollState()).padding(horizontal = 8.dp),
                    ) {
                        CommandEmpty()
                        catalogNavigationSections.forEach { section ->
                            val entries =
                                section.entries.filter {
                                    state.matches(it.title) ||
                                        state.matches(it.route.path) ||
                                        state.matches(section.title)
                                }
                            if (entries.isNotEmpty()) {
                                CommandGroup(heading = section.title) {
                                    entries.forEach { entry ->
                                        CommandItem(
                                            label = entry.title,
                                            modifier = Modifier.height(36.dp),
                                            value = entry.title,
                                            keywords = listOf(entry.route.path, section.title),
                                            onSelect = { onNavigate(entry.route) },
                                        ) {
                                            Row(
                                                horizontalArrangement = Arrangement.spacedBy(8.dp),
                                                verticalAlignment = Alignment.CenterVertically,
                                            ) {
                                                DocIcon(Lucide.ArrowRight, tint = colors.mutedForeground)
                                                Text { entry.title }
                                            }
                                        }
                                    }
                                }
                            }
                        }
                    }
                    Row(
                        modifier =
                            Modifier.fillMaxWidth().height(40.dp)
                                .background(colors.muted, RoundedCornerShape(bottomStart = 14.dp, bottomEnd = 14.dp))
                                .padding(horizontal = 16.dp),
                        horizontalArrangement = Arrangement.spacedBy(8.dp),
                        verticalAlignment = Alignment.CenterVertically,
                    ) {
                        Kbd("↵")
                        Text(variant = TextVariant.BodySmall, textStyle = TextColors.Muted) { "Go to Page" }
                    }
                }
            }
        }
    }
}

private object SearchPanelAlignment : Alignment {
    override fun align(
        size: IntSize,
        space: IntSize,
        layoutDirection: LayoutDirection,
    ): IntOffset =
        IntOffset(
            (space.width - size.width) / 2,
            minOf((space.height * 0.15f).toInt(), space.height - size.height).coerceAtLeast(0),
        )
}
