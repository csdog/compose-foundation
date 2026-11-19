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
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.movableContentOf
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberUpdatedState
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import com.toutiaoyan.compose.tailwind.core.twSpacing
import com.toutiaoyan.compose.ui.catalog.create.CreateConfiguration
import com.toutiaoyan.compose.ui.catalog.create.CreateCustomizer
import com.toutiaoyan.compose.ui.catalog.create.CreateDialog
import com.toutiaoyan.compose.ui.catalog.create.CreatePreviewCanvas
import com.toutiaoyan.compose.ui.catalog.create.CreatePreviewTheme
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

/**
 * Full recreate of [ui.shadcn.com/create](https://ui.shadcn.com/create)
 * ([create/page.tsx](https://github.com/shadcn-ui/ui/blob/main/apps/v4/app/(app)/(create)/create/page.tsx)):
 * designer row with gap/padding tokens, preview + customizer, welcome dialog, command palette.
 *
 * Official `md:flex-row-reverse` with Preview then Customizer in DOM → visual
 * Customizer (menu rail) on the **left**, Preview on the right.
 *
 * Theme options remain hoisted to the catalog. This screen owns only its preview and dialogs.
 */
@Suppress("LongParameterList", "LongMethod")
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
    onToggleTheme: () -> Unit = {},
) {
    var welcomeOpen by remember { mutableStateOf(true) }
    var dialog by remember { mutableStateOf<CreateDialog?>(null) }
    var previewIndex by remember { mutableIntStateOf(0) }
    var choices by remember { mutableStateOf(CreateConfiguration(baseColor = baseColor)) }
    val configuration = choices.changeBase(baseColor).copy(style = stylePreset, accent = accent)
    val updateConfiguration: (CreateConfiguration) -> Unit = { next ->
        choices = next
        if (next.style != stylePreset) onStylePresetChange(next.style)
        if (next.baseColor != baseColor) onBaseColorChange(next.baseColor)
        if (next.accent != accent) onAccentChange(next.accent)
    }
    LaunchedEffect(getCodeRequest) {
        if (getCodeRequest > 0) dialog = CreateDialog.GetCode
    }

    val currentConfiguration by rememberUpdatedState(configuration)
    val preview =
        remember {
            movableContentOf<Modifier> { placement ->
                CreatePreviewTheme(currentConfiguration) {
                    CreatePreviewCanvas(
                        chartColor = currentConfiguration.chartColor,
                        previewIndex = previewIndex,
                        onPreviewChange = { previewIndex = it },
                        modifier = placement,
                    )
                }
            }
        }
    val customizerContent by rememberUpdatedState<@Composable (Modifier, Boolean, Dp) -> Unit>(
        { placement, docked, railWidth ->
            // Keep the rail and its dialog anchors in one slot of the designer layout.
            Box {
                CreateCustomizer(
                    configuration = configuration,
                    onConfigurationChange = updateConfiguration,
                    dialog = dialog,
                    onDialogChange = { dialog = it },
                    onToggleTheme = onToggleTheme,
                    docked = docked,
                    railWidth = railWidth,
                    modifier = placement,
                )
            }
        },
    )
    val customizer =
        remember {
            movableContentOf<Modifier, Boolean, Dp> { placement, docked, railWidth ->
                customizerContent(placement, docked, railWidth)
            }
        }

    Box(modifier = modifier.fillMaxSize()) {
        BoxWithConstraints(modifier = Modifier.fillMaxSize()) {
            // page.tsx: [--gap:--spacing(4)] md:[--gap:--spacing(6)], p-(--gap), pt-[calc(var(--gap)*0.25)]
            val gap = if (maxWidth < 768.dp) twSpacing.scale4 else twSpacing.scale6
            val docked = maxWidth < 768.dp
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
                    customizer(Modifier.fillMaxWidth(), true, railWidth)
                }
            } else {
                Row(
                    modifier = designerModifier,
                    horizontalArrangement = Arrangement.spacedBy(gap),
                ) {
                    // Left rail = Customizer (official flex-row-reverse visual).
                    customizer(Modifier.fillMaxHeight(), false, railWidth)
                    preview(Modifier.weight(1f).fillMaxHeight())
                }
            }
        }

        CreateWelcomeDialog(
            visible = welcomeOpen,
            onDismissRequest = { welcomeOpen = false },
        )

        Dialog(open = dialog == CreateDialog.Commands, onOpenChange = { if (!it) dialog = null }) {
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
                            listOf("Preview 01", "Preview 02").forEachIndexed { index, label ->
                                CommandItem(
                                    label,
                                    onSelect = {
                                        previewIndex = index
                                        dialog = null
                                    },
                                )
                            }
                        }
                        CommandSeparator()
                        CommandGroup(heading = "Actions") {
                            CommandItem(
                                "Shuffle",
                                onSelect = {
                                    updateConfiguration(CreateConfiguration.shuffled())
                                    dialog = null
                                },
                            )
                            CommandItem(
                                "Reset",
                                onSelect = {
                                    updateConfiguration(CreateConfiguration())
                                    dialog = null
                                },
                            )
                            CommandItem("Open Preset", onSelect = { dialog = CreateDialog.OpenPreset })
                            CommandItem("Get Code", onSelect = { dialog = CreateDialog.GetCode })
                        }
                    }
                }
            }
        }
    }
}
