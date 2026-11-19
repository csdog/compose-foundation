@file:OptIn(androidx.compose.foundation.style.ExperimentalFoundationStyleApi::class)

package com.toutiaoyan.compose.ui.catalog.create

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.style.MutableStyleState
import androidx.compose.foundation.style.Style
import androidx.compose.foundation.style.fillWidth
import androidx.compose.foundation.style.styleable
import androidx.compose.foundation.verticalScroll
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import com.composables.icons.lucide.Check
import com.composables.icons.lucide.Lock
import com.composables.icons.lucide.LockOpen
import com.composables.icons.lucide.Lucide
import com.composables.icons.lucide.Menu
import com.toutiaoyan.compose.tailwind.core.twSpacing
import com.toutiaoyan.compose.ui.catalog.docs.CopyLabel
import com.toutiaoyan.compose.ui.catalog.docs.DocIcon
import com.toutiaoyan.compose.ui.components.button.Button
import com.toutiaoyan.compose.ui.components.button.ButtonSize
import com.toutiaoyan.compose.ui.components.button.ButtonVariant
import com.toutiaoyan.compose.ui.components.card.Card
import com.toutiaoyan.compose.ui.components.card.CardSize
import com.toutiaoyan.compose.ui.components.dialog.Dialog
import com.toutiaoyan.compose.ui.components.dialog.DialogContent
import com.toutiaoyan.compose.ui.components.dialog.DialogDescription
import com.toutiaoyan.compose.ui.components.dialog.DialogFooter
import com.toutiaoyan.compose.ui.components.dialog.DialogHeader
import com.toutiaoyan.compose.ui.components.dialog.DialogTitle
import com.toutiaoyan.compose.ui.components.field.Field
import com.toutiaoyan.compose.ui.components.field.FieldError
import com.toutiaoyan.compose.ui.components.field.FieldGroup
import com.toutiaoyan.compose.ui.components.field.FieldLabel
import com.toutiaoyan.compose.ui.components.field.FieldSeparator
import com.toutiaoyan.compose.ui.components.icon.Icon
import com.toutiaoyan.compose.ui.components.icon.IconVariant
import com.toutiaoyan.compose.ui.components.input.Input
import com.toutiaoyan.compose.ui.components.overlay.AnchoredPopup
import com.toutiaoyan.compose.ui.components.select.SelectVariant
import com.toutiaoyan.compose.ui.components.select.rememberPanelStyle
import com.toutiaoyan.compose.ui.components.select.rememberSelectItemStyle
import com.toutiaoyan.compose.ui.components.text.Text
import com.toutiaoyan.compose.ui.components.text.TextColors
import com.toutiaoyan.compose.ui.components.text.TextVariant
import com.toutiaoyan.compose.ui.theme.Theme
import com.toutiaoyan.compose.ui.theme.currentTheme
import com.toutiaoyan.compose.ui.tokens.Accent
import com.toutiaoyan.compose.ui.tokens.BaseColor
import com.toutiaoyan.compose.ui.tokens.StylePreset
import com.toutiaoyan.compose.ui.utils.clipboard.rememberCopyToClipboard

/**
 * Customizer matching ui.shadcn.com/create. [docked] places a horizontal bottom bar
 * (narrow viewports); otherwise a vertical left rail (desktop).
 *
 * The compact and rail layouts share settings and actions but use different scrolling axes.
 */
@Suppress("LongParameterList", "LongMethod")
@Composable
internal fun CreateCustomizer(
    configuration: CreateConfiguration,
    onConfigurationChange: (CreateConfiguration) -> Unit,
    dialog: CreateDialog?,
    onDialogChange: (CreateDialog?) -> Unit,
    onToggleTheme: () -> Unit,
    modifier: Modifier = Modifier,
    docked: Boolean = false,
    railWidth: Dp = 192.dp,
) {
    var presetInput by remember { mutableStateOf("") }
    var invalidPreset by remember { mutableStateOf(false) }
    val presetCode = "--preset ${configuration.toPresetCode()}"
    val snippet =
        """
        Theme(
            preset = StylePreset.${configuration.style.name},
            baseColor = BaseColor.${configuration.baseColor.name},
            accent = Accent.${configuration.accent.name},
            baseRadius = Radius(${configuration.radiusDp}.dp),
        ) {
            // your content
        }
        """.trimIndent()
    val presetCopy = rememberCopyToClipboard(presetCode)
    val codeCopy = rememberCopyToClipboard(snippet)
    val shuffle = { onConfigurationChange(CreateConfiguration.shuffled()) }
    val reset = { onConfigurationChange(CreateConfiguration()) }

    Theme(
        preset = configuration.style,
        baseColor = configuration.baseColor,
        accent = configuration.accent,
        isDark = true,
    ) {
        // customizer.tsx: dark Card size=sm rounded-2xl bg-card/90
        val glassCard = currentTheme.colors.card.copy(alpha = 0.9f)
        Card(
            modifier =
                if (docked) {
                    modifier.fillMaxWidth()
                } else {
                    // --customizer-width: --spacing(48|56)
                    modifier.width(railWidth)
                },
            size = CardSize.Sm,
            style =
                Style {
                    shape(RoundedCornerShape(16.dp))
                    background(glassCard)
                },
        ) {
            if (docked) {
                Column(
                    modifier =
                        Modifier
                            .fillMaxWidth()
                            .padding(twSpacing.md),
                    verticalArrangement = Arrangement.spacedBy(twSpacing.sm),
                ) {
                    Row(
                        modifier =
                            Modifier
                                .fillMaxWidth()
                                .horizontalScroll(rememberScrollState()),
                        horizontalArrangement = Arrangement.spacedBy(twSpacing.sm),
                    ) {
                        Button(
                            onClick = { onDialogChange(CreateDialog.Commands) },
                            variant = ButtonVariant.Outline,
                            size = ButtonSize.Sm,
                        ) {
                            Text { "Actions" }
                        }
                        CreateCustomizerPickers(
                            configuration = configuration,
                            onConfigurationChange = onConfigurationChange,
                            compact = true,
                        )
                    }
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(twSpacing.sm),
                        verticalAlignment = Alignment.CenterVertically,
                    ) {
                        // copy-preset / open-preset / random-button: compact outline density
                        Button(
                            onClick = presetCopy.copy,
                            variant = ButtonVariant.Outline,
                            size = ButtonSize.Sm,
                            modifier = Modifier.weight(1f),
                        ) {
                            CopyLabel(presetCopy.justCopied, "Copy Preset")
                        }
                        Button(
                            onClick = { onDialogChange(CreateDialog.OpenPreset) },
                            variant = ButtonVariant.Outline,
                            size = ButtonSize.Sm,
                        ) {
                            Text { "Open" }
                        }
                        Button(
                            onClick = shuffle,
                            variant = ButtonVariant.Outline,
                            size = ButtonSize.Sm,
                        ) {
                            Text { "Shuffle" }
                        }
                        Button(
                            onClick = { onDialogChange(CreateDialog.GetCode) },
                            size = ButtonSize.Sm,
                        ) {
                            Text { "Get Code" }
                        }
                    }
                }
            } else {
                Column(
                    modifier =
                        Modifier
                            .fillMaxWidth()
                            .verticalScroll(rememberScrollState())
                            .padding(twSpacing.md),
                    verticalArrangement = Arrangement.spacedBy(twSpacing.sm),
                ) {
                    // main-menu.tsx in CardHeader with border-b
                    var menuOpen by remember { mutableStateOf(false) }
                    Column {
                        Box(
                            modifier =
                                Modifier
                                    .fillMaxWidth()
                                    .padding(bottom = twSpacing.sm),
                        ) {
                            Row(
                                modifier =
                                    Modifier
                                        .fillMaxWidth()
                                        .border(
                                            1.dp,
                                            currentTheme.colors.foreground.copy(alpha = 0.10f),
                                            RoundedCornerShape(8.dp),
                                        )
                                        .clickable(
                                            indication = null,
                                            interactionSource = remember { MutableInteractionSource() },
                                        ) { menuOpen = true }
                                        .padding(horizontal = 7.dp, vertical = twSpacing.sm),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically,
                            ) {
                                Text(variant = TextVariant.TitleSmall) { "Menu" }
                                DocIcon(Lucide.Menu)
                            }
                            AnchoredPopup(open = menuOpen, onDismissRequest = { menuOpen = false }) {
                                val panelStyleState = remember { MutableStyleState(MutableInteractionSource()) }
                                Column(
                                    modifier =
                                        Modifier
                                            .widthIn(min = 200.dp, max = 260.dp)
                                            .background(
                                                currentTheme.colors.popover.copy(alpha = 0.92f),
                                                RoundedCornerShape(12.dp),
                                            )
                                            .border(
                                                1.dp,
                                                currentTheme.colors.border.copy(alpha = 0.5f),
                                                RoundedCornerShape(12.dp),
                                            )
                                            .styleable(panelStyleState, SelectVariant.Default.rememberPanelStyle())
                                            .padding(6.dp),
                                ) {
                                    MainMenuItem("Navigate...", "⌘P") {
                                        menuOpen = false
                                        onDialogChange(CreateDialog.Commands)
                                    }
                                    MainMenuItem("Open Preset...", "O") {
                                        menuOpen = false
                                        onDialogChange(CreateDialog.OpenPreset)
                                    }
                                    MainMenuItem("Shuffle", "R") {
                                        menuOpen = false
                                        shuffle()
                                    }
                                    MainMenuItem("Light/Dark", "D") {
                                        menuOpen = false
                                        onToggleTheme()
                                    }
                                    FieldSeparator()
                                    MainMenuItem("Reset", "⇧R") {
                                        menuOpen = false
                                        reset()
                                    }
                                }
                            }
                        }
                        Box(
                            modifier =
                                Modifier
                                    .fillMaxWidth()
                                    .height(1.dp)
                                    .background(currentTheme.colors.border),
                        )
                    }

                    CreateCustomizerPickers(
                        configuration = configuration,
                        onConfigurationChange = onConfigurationChange,
                        compact = false,
                    )

                    Column(
                        modifier = Modifier.padding(top = twSpacing.sm),
                        verticalArrangement = Arrangement.spacedBy(twSpacing.sm),
                    ) {
                        // Outer fillMaxWidth grows the min-touch hit box; chrome needs
                        // Style { fillWidth() } so the painted background matches.
                        val fullWidth = Style { fillWidth() }
                        Button(
                            onClick = presetCopy.copy,
                            variant = ButtonVariant.Outline,
                            size = ButtonSize.Sm,
                            modifier = Modifier.fillMaxWidth(),
                            style = fullWidth,
                        ) {
                            CopyLabel(presetCopy.justCopied, "Copy Preset")
                        }
                        Button(
                            onClick = { onDialogChange(CreateDialog.OpenPreset) },
                            variant = ButtonVariant.Outline,
                            size = ButtonSize.Sm,
                            modifier = Modifier.fillMaxWidth(),
                            style = fullWidth,
                        ) {
                            Text { "Open Preset" }
                        }
                        Button(
                            onClick = shuffle,
                            variant = ButtonVariant.Outline,
                            size = ButtonSize.Sm,
                            modifier = Modifier.fillMaxWidth(),
                            style = fullWidth,
                        ) {
                            Text { "Shuffle" }
                        }
                        Button(
                            onClick = reset,
                            variant = ButtonVariant.Ghost,
                            size = ButtonSize.Sm,
                            modifier = Modifier.fillMaxWidth(),
                            style = fullWidth,
                        ) {
                            Text { "Reset" }
                        }
                        Button(
                            onClick = { onDialogChange(CreateDialog.Commands) },
                            variant = ButtonVariant.Outline,
                            size = ButtonSize.Sm,
                            modifier = Modifier.fillMaxWidth(),
                            style = fullWidth,
                        ) {
                            Text { "Command Palette" }
                        }
                        Button(
                            onClick = { onDialogChange(CreateDialog.GetCode) },
                            size = ButtonSize.Sm,
                            modifier = Modifier.fillMaxWidth(),
                            style = fullWidth,
                        ) {
                            Text { "Get Code" }
                        }
                    }
                }
            }
        }
    }

    Dialog(
        open = dialog == CreateDialog.OpenPreset,
        onOpenChange = {
            if (!it) {
                presetInput = ""
                invalidPreset = false
                onDialogChange(null)
            }
        },
    ) {
        DialogContent {
            DialogHeader {
                DialogTitle("Open Preset")
                DialogDescription("Paste a preset code to load a saved configuration.")
            }
            FieldGroup(modifier = Modifier.padding(top = twSpacing.md)) {
                Field(invalid = invalidPreset) {
                    FieldLabel("Preset")
                    Input(
                        value = presetInput,
                        onValueChange = {
                            presetInput = it
                            invalidPreset = false
                        },
                        invalid = invalidPreset,
                        placeholder = "Paste a Compose Foundation preset",
                        modifier = Modifier.fillMaxWidth(),
                    )
                    if (invalidPreset) FieldError("Enter a valid Compose Foundation preset code.")
                }
            }
            DialogFooter(modifier = Modifier.padding(top = twSpacing.md)) {
                Button(
                    onClick = {
                        presetInput = ""
                        invalidPreset = false
                        onDialogChange(null)
                    },
                    variant = ButtonVariant.Outline,
                ) {
                    Text { "Cancel" }
                }
                Button(
                    onClick = {
                        val next = CreateConfiguration.fromPresetCode(presetInput)
                        if (next == null) {
                            invalidPreset = true
                        } else {
                            onConfigurationChange(next)
                            onDialogChange(null)
                            presetInput = ""
                        }
                    },
                ) {
                    Text { "Open" }
                }
            }
        }
    }

    Dialog(open = dialog == CreateDialog.GetCode, onOpenChange = { if (!it) onDialogChange(null) }) {
        DialogContent {
            DialogHeader {
                DialogTitle("Get Code")
                DialogDescription("Copy the theme setup or a preset for the current configuration.")
            }
            Text(
                variant = TextVariant.BodySmall,
                textStyle = TextColors.Muted,
                modifier = Modifier.padding(top = twSpacing.md),
            ) { snippet }
            DialogFooter(modifier = Modifier.padding(top = twSpacing.md)) {
                Button(onClick = { onDialogChange(null) }, variant = ButtonVariant.Outline) {
                    Text { "Close" }
                }
                Button(onClick = presetCopy.copy, variant = ButtonVariant.Outline) {
                    CopyLabel(presetCopy.justCopied, "Copy Preset")
                }
                Button(onClick = codeCopy.copy) {
                    CopyLabel(codeCopy.justCopied, "Copy Code")
                }
            }
        }
    }
}

// Each section of the configuration is declared in its navigation order.
@Suppress("LongMethod", "CyclomaticComplexMethod")
@Composable
private fun CreateCustomizerPickers(
    configuration: CreateConfiguration,
    onConfigurationChange: (CreateConfiguration) -> Unit,
    compact: Boolean,
) {
    val stylePreset = configuration.style
    val baseColor = configuration.baseColor
    val accent = configuration.accent
    val chartColor = configuration.chartColor
    val radius = configuration.radius
    val rowModifier = if (compact) Modifier.width(144.dp) else Modifier.fillMaxWidth()

    PresetSelectorRow(
        label = "Style",
        value = stylePreset.label,
        options = StylePreset.entries,
        onValueChange = { onConfigurationChange(configuration.copy(style = it)) },
        optionLabel = { it.label },
        modifier = rowModifier,
        swatch = {
            Box(
                modifier =
                    Modifier
                        .size(20.dp)
                        .background(
                            currentTheme.colors.foreground,
                            RoundedCornerShape(stylePreset.radius.sm),
                        ),
            )
        },
    )
    if (!compact) FieldSeparator()
    PresetSelectorRow(
        label = "Base Color",
        value = baseColor.label,
        options = BaseColor.entries,
        onValueChange = { onConfigurationChange(configuration.changeBase(it)) },
        optionLabel = { it.label },
        modifier = rowModifier,
        swatch = {
            Box(modifier = Modifier.size(16.dp).background(baseColor.dark.mutedForeground, CircleShape))
        },
    )
    PresetSelectorRow(
        label = "Theme",
        value = if (accent == Accent.Base) baseColor.label else accent.label,
        options = Accent.entries,
        onValueChange = { onConfigurationChange(configuration.copy(accent = it)) },
        optionLabel = { if (it == Accent.Base) baseColor.label else it.label },
        modifier = rowModifier,
        swatch = {
            val swatch = chartColorSwatch(if (accent == Accent.Base) baseColor.label else accent.label)
            Box(modifier = Modifier.size(16.dp).background(swatch, CircleShape))
        },
    )
    PresetSelectorRow(
        label = "Chart Color",
        value = chartColor,
        options = chartColors(baseColor),
        onValueChange = { onConfigurationChange(configuration.copy(chartColor = it)) },
        optionLabel = { it },
        modifier = rowModifier,
        swatch = {
            Box(modifier = Modifier.size(16.dp).background(chartColorSwatch(chartColor), CircleShape))
        },
    )
    if (!compact) FieldSeparator()
    PresetSelectorRow(
        label = "Heading",
        value = configuration.headingFont,
        options = HEADING_FONTS,
        onValueChange = { onConfigurationChange(configuration.copy(headingFont = it)) },
        optionLabel = { it },
        modifier = rowModifier,
        swatch = {
            Text(variant = TextVariant.LabelLarge, textStyle = TextColors.Muted) { "Aa" }
        },
    )
    PresetSelectorRow(
        label = "Font",
        value = configuration.bodyFont,
        options = BODY_FONTS,
        onValueChange = { onConfigurationChange(configuration.copy(bodyFont = it)) },
        optionLabel = { it },
        modifier = rowModifier,
        swatch = {
            Text(variant = TextVariant.LabelLarge, textStyle = TextColors.Muted) { "Aa" }
        },
    )
    if (!compact) FieldSeparator()
    PresetSelectorRow(
        label = "Icon Library",
        value = configuration.iconLibrary,
        options = ICON_LIBRARIES,
        onValueChange = { onConfigurationChange(configuration.copy(iconLibrary = it)) },
        optionLabel = { it },
        modifier = rowModifier,
        swatch = {},
    )
    PresetSelectorRow(
        label = "Radius",
        value = radius,
        options = RADII,
        onValueChange = { onConfigurationChange(configuration.copy(radius = it)) },
        optionLabel = { it },
        modifier = rowModifier,
        swatch = {
            Box(
                modifier =
                    Modifier
                        .size(20.dp)
                        .background(
                            currentTheme.colors.foreground,
                            RoundedCornerShape(
                                when (radius) {
                                    "None" -> 0.dp
                                    "Small" -> 4.dp
                                    "Medium" -> 8.dp
                                    "Large" -> 12.dp
                                    "Full" -> 999.dp
                                    else -> 8.dp // Default ≈ TwRadiusScale.md at --radius 10px
                                },
                            ),
                        ),
            )
        },
    )
    if (!compact) FieldSeparator()
    PresetSelectorRow(
        label = "Menu",
        value = configuration.menuColor,
        options = MENU_COLORS,
        onValueChange = { onConfigurationChange(configuration.copy(menuColor = it)) },
        optionLabel = { it },
        modifier = rowModifier,
        swatch = {},
    )
    PresetSelectorRow(
        label = "Menu Accent",
        value = configuration.menuAccent,
        options = MENU_ACCENTS,
        onValueChange = { onConfigurationChange(configuration.copy(menuAccent = it)) },
        optionLabel = { it },
        modifier = rowModifier,
        swatch = {},
    )
    PresetSelectorRow(
        label = "Base",
        value = configuration.componentBase,
        options = COMPONENT_BASES,
        onValueChange = { onConfigurationChange(configuration.copy(componentBase = it)) },
        optionLabel = { it },
        modifier = rowModifier,
        swatch = {},
    )
}

@Composable
internal fun <T> PresetSelectorRow(
    label: String,
    value: String,
    options: List<T>,
    onValueChange: (T) -> Unit,
    optionLabel: (T) -> String,
    modifier: Modifier = Modifier,
    swatch: @Composable () -> Unit,
    optionSwatch: (@Composable (T) -> Unit)? = null,
) {
    var open by remember { mutableStateOf(false) }
    var locked by remember { mutableStateOf(false) }
    val interactionSource = remember { MutableInteractionSource() }
    // picker.tsx PickerTrigger: rounded-lg + ring-foreground/10; lock absolute (style-picker.tsx)
    val triggerShape = RoundedCornerShape(10.dp)

    Box(modifier = modifier.fillMaxWidth()) {
        Row(
            modifier =
                Modifier
                    .fillMaxWidth()
                    .border(1.dp, currentTheme.colors.foreground.copy(alpha = 0.10f), triggerShape)
                    .clickable(
                        enabled = !locked,
                        interactionSource = interactionSource,
                        indication = null,
                    ) { open = true }
                    // room for lock (right-8) + swatch (right-2.5)
                    .padding(start = 10.dp, end = 36.dp, top = 8.dp, bottom = 8.dp),
            verticalAlignment = Alignment.CenterVertically,
        ) {
            Column(modifier = Modifier.weight(1f)) {
                Text(variant = TextVariant.LabelSmall, textStyle = TextColors.Muted) { label }
                Text(variant = TextVariant.TitleSmall) { value }
            }
        }

        // style-picker: absolute top-1/2 right-2.5 swatch
        Box(
            modifier =
                Modifier
                    .align(Alignment.CenterEnd)
                    .padding(end = 10.dp),
        ) {
            swatch()
        }

        // lock-button.tsx: absolute top-1/2 right-8, size-4 hit target
        Button(
            onClick = { locked = !locked },
            variant = ButtonVariant.Ghost,
            size = ButtonSize.IconXs,
            modifier =
                Modifier
                    .align(Alignment.CenterEnd)
                    .padding(end = 32.dp),
        ) {
            Icon(
                if (locked) Lucide.Lock else Lucide.LockOpen,
                variant = IconVariant.Small,
                contentDescription = if (locked) "Unlock" else "Lock",
            )
        }

        AnchoredPopup(open = open && !locked, onDismissRequest = { open = false }) {
            val panelStyleState = remember { MutableStyleState(MutableInteractionSource()) }
            Column(
                modifier =
                    Modifier
                        .widthIn(min = 140.dp, max = 280.dp)
                        .background(
                            currentTheme.colors.popover.copy(alpha = 0.92f),
                            RoundedCornerShape(12.dp),
                        )
                        .border(
                            1.dp,
                            currentTheme.colors.border.copy(alpha = 0.5f),
                            RoundedCornerShape(12.dp),
                        )
                        .styleable(panelStyleState, SelectVariant.Default.rememberPanelStyle())
                        .verticalScroll(rememberScrollState()),
            ) {
                options.forEach { option ->
                    val isSelected = optionLabel(option) == value
                    val itemInteractionSource = remember { MutableInteractionSource() }
                    val itemStyleState = remember { MutableStyleState(itemInteractionSource) }
                    Row(
                        modifier =
                            Modifier
                                .fillMaxWidth()
                                .clickable(interactionSource = itemInteractionSource, indication = null) {
                                    onValueChange(option)
                                    open = false
                                }
                                .styleable(itemStyleState, rememberSelectItemStyle()),
                        horizontalArrangement = Arrangement.spacedBy(twSpacing.sm),
                        verticalAlignment = Alignment.CenterVertically,
                    ) {
                        if (optionSwatch != null) {
                            optionSwatch(option)
                        }
                        Text(
                            modifier = Modifier.weight(1f),
                            variant = TextVariant.BodySmall,
                        ) { optionLabel(option) }
                        Box(modifier = Modifier.size(currentTheme.icons.smallSize)) {
                            if (isSelected) {
                                DocIcon(Lucide.Check)
                            }
                        }
                    }
                }
            }
        }
    }
}

@Composable
private fun MainMenuItem(
    label: String,
    shortcut: String,
    onClick: () -> Unit,
) {
    Row(
        modifier =
            Modifier
                .fillMaxWidth()
                .clickable(
                    indication = null,
                    interactionSource = remember { MutableInteractionSource() },
                    onClick = onClick,
                )
                .padding(horizontal = twSpacing.sm, vertical = twSpacing.xs),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically,
    ) {
        Text(variant = TextVariant.BodyMedium) { label }
        Text(variant = TextVariant.BodySmall, textStyle = TextColors.Muted) { shortcut }
    }
}
