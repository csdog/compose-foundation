@file:OptIn(androidx.compose.foundation.style.ExperimentalFoundationStyleApi::class)

package com.toutiaoyan.compose.ui.catalog

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.widthIn
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.movableContentOf
import androidx.compose.runtime.rememberUpdatedState
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.toutiaoyan.compose.ui.catalog.create.CreateCustomizer
import com.toutiaoyan.compose.ui.catalog.create.CreatePreviewCanvas
import com.toutiaoyan.compose.ui.catalog.create.CreateWelcomeDialog
import com.toutiaoyan.compose.ui.components.command.Command
import com.toutiaoyan.compose.ui.components.command.CommandGroup
import com.toutiaoyan.compose.ui.components.command.CommandInput
import com.toutiaoyan.compose.ui.components.command.CommandItem
import com.toutiaoyan.compose.ui.components.command.CommandList
import com.toutiaoyan.compose.ui.components.command.CommandSeparator
import com.toutiaoyan.compose.ui.components.dialog.Dialog
import com.toutiaoyan.compose.ui.components.dialog.DialogContent
import com.toutiaoyan.compose.ui.tokens.Accent
import com.toutiaoyan.compose.ui.tokens.BaseColor
import com.toutiaoyan.compose.ui.tokens.StylePreset
import com.toutiaoyan.compose.tailwind.core.twSpacing

/**
 * Full recreate of [ui.shadcn.com/create](https://ui.shadcn.com/create)
 * ([create/page.tsx](https://github.com/shadcn-ui/ui/blob/main/apps/v4/app/(app)/(create)/create/page.tsx)):
 * designer row with gap/padding tokens, preview + customizer, welcome dialog, command palette.
 *
 * Official `md:flex-row-reverse` with Preview then Customizer in DOM → visual
 * Customizer (menu rail) on the **left**, Preview on the right.
 */
@Composable
fun CreatePage(
    stylePreset: StylePreset,
    onStylePresetChange: (StylePreset) -> Unit,
    baseColor: BaseColor,
    onBaseColorChange: (BaseColor) -> Unit,
    accent: Accent,
    onAccentChange: (Accent) -> Unit,
    modifier: Modifier = Modifier,
    /** Bumped by header Get Code (DesignerActions). */
    getCodeRequest: Int = 0,
) {
    var welcomeOpen by remember { mutableStateOf(true) }
    var commandOpen by remember { mutableStateOf(false) }
    var chartColor by remember { mutableStateOf(baseColor.label) }
    val currentChartColor by rememberUpdatedState(chartColor)
    val preview =
        remember {
            movableContentOf<Modifier> { placement ->
                CreatePreviewCanvas(chartColor = currentChartColor, modifier = placement)
            }
        }
    val changeBaseColor: (BaseColor) -> Unit = { next ->
        // Official normalization keeps a colored chart theme, but replaces a base
        // chart palette that is no longer available under the new base.
        if (BaseColor.entries.any { it.label == chartColor }) chartColor = next.label
        onBaseColorChange(next)
    }

    Box(modifier = modifier.fillMaxSize()) {
        BoxWithConstraints(modifier = Modifier.fillMaxSize()) {
            // page.tsx: [--gap:--spacing(4)] md:[--gap:--spacing(6)], p-(--gap), pt-[calc(var(--gap)*0.25)]
            val gap = if (maxWidth < 768.dp) twSpacing.md else twSpacing.lg
            val docked = maxWidth < 900.dp
            // 2xl:[--customizer-width:--spacing(56)] → 224dp
            val railWidth = if (maxWidth >= 1536.dp) 224.dp else 192.dp
            val designerModifier =
                Modifier
                    .fillMaxSize()
                    .padding(start = gap, end = gap, bottom = gap, top = gap / 4)

            if (docked) {
                Column(
                    modifier = designerModifier,
                    verticalArrangement = Arrangement.spacedBy(gap),
                ) {
                    preview(Modifier.weight(1f).fillMaxWidth())
                    CreateCustomizer(
                        stylePreset = stylePreset,
                        onStylePresetChange = onStylePresetChange,
                        baseColor = baseColor,
                        onBaseColorChange = changeBaseColor,
                        accent = accent,
                        onAccentChange = onAccentChange,
                        chartColor = chartColor,
                        onChartColorChange = { chartColor = it },
                        onOpenCommandPalette = { commandOpen = true },
                        getCodeRequest = getCodeRequest,
                        docked = true,
                        modifier = Modifier.fillMaxWidth(),
                    )
                }
            } else {
                Row(
                    modifier = designerModifier,
                    horizontalArrangement = Arrangement.spacedBy(gap),
                ) {
                    // Left rail = Customizer (official flex-row-reverse visual).
                    CreateCustomizer(
                        stylePreset = stylePreset,
                        onStylePresetChange = onStylePresetChange,
                        baseColor = baseColor,
                        onBaseColorChange = changeBaseColor,
                        accent = accent,
                        onAccentChange = onAccentChange,
                        chartColor = chartColor,
                        onChartColorChange = { chartColor = it },
                        onOpenCommandPalette = { commandOpen = true },
                        getCodeRequest = getCodeRequest,
                        docked = false,
                        railWidth = railWidth,
                        modifier = Modifier.fillMaxHeight(),
                    )
                    preview(Modifier.weight(1f).fillMaxHeight())
                }
            }
        }

        CreateWelcomeDialog(
            visible = welcomeOpen,
            onDismissRequest = { welcomeOpen = false },
        )

        Dialog(open = commandOpen, onOpenChange = { commandOpen = it }) {
            DialogContent(showCloseButton = false) {
                Command(
                    modifier =
                        Modifier
                            .fillMaxWidth()
                            .widthIn(max = 480.dp),
                ) {
                    // action-menu.tsx CommandInput placeholder
                    CommandInput(placeholder = "Search")
                    CommandList {
                        CommandGroup(heading = "Previews") {
                            CommandItem("Preview 01", onSelect = { commandOpen = false })
                            CommandItem("Preview 02", onSelect = { commandOpen = false })
                        }
                        CommandSeparator()
                        CommandGroup(heading = "Actions") {
                            CommandItem("Shuffle", onSelect = { commandOpen = false })
                            CommandItem("Reset", onSelect = { commandOpen = false })
                            CommandItem("Open Preset", onSelect = { commandOpen = false })
                            CommandItem("Get Code", onSelect = { commandOpen = false })
                        }
                    }
                }
            }
        }
    }
}
