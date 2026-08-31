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
import androidx.compose.runtime.LaunchedEffect
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
import com.toutiaoyan.compose.ui.utils.clipboard.rememberCopyToClipboard
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
import kotlin.random.Random

private val HEADING_FONTS = listOf("Inter", "Geist", "Geist Mono", "System", "Serif")
private val BODY_FONTS = listOf("Inter", "Geist", "Geist Mono", "System", "Mono")
private val ICON_LIBRARIES = listOf("Lucide", "Hugeicons", "Tabler", "Phosphor", "Remix Icon")
private val RADII = listOf("Default", "None", "Small", "Medium", "Large", "Full")
private val MENU_COLORS =
    listOf(
        "Default / Solid",
        "Default / Translucent",
        "Inverted / Solid",
        "Inverted / Translucent",
    )
private val MENU_ACCENTS = listOf("Subtle", "Bold", "Primary", "Muted")
private val COMPONENT_BASES =
    listOf(
        "Base UI" to "base",
        "Radix" to "radix",
        "React Aria" to "aria",
    )

/**
 * Customizer matching ui.shadcn.com/create. [docked] places a horizontal bottom bar
 * (narrow viewports); otherwise a vertical left rail (desktop).
 */
@Composable
fun CreateCustomizer(
    stylePreset: StylePreset,
    onStylePresetChange: (StylePreset) -> Unit,
    baseColor: BaseColor,
    onBaseColorChange: (BaseColor) -> Unit,
    accent: Accent,
    onAccentChange: (Accent) -> Unit,
    modifier: Modifier = Modifier,
    docked: Boolean = false,
    /** Official `--customizer-width`: 48→192dp, 2xl 56→224dp. */
    railWidth: Dp = 192.dp,
    /** When incremented, opens the Get Code dialog (header DesignerActions). */
    getCodeRequest: Int = 0,
    onOpenCommandPalette: () -> Unit = {},
) {
    var chartColor by remember { mutableStateOf(baseColor.label) }
    var headingFont by remember { mutableStateOf(HEADING_FONTS.first()) }
    var bodyFont by remember { mutableStateOf(BODY_FONTS.first()) }
    var iconLibrary by remember { mutableStateOf(ICON_LIBRARIES.first()) }
    var radius by remember { mutableStateOf(RADII.first()) }
    var menuColor by remember { mutableStateOf(MENU_COLORS.first()) }
    var menuAccent by remember { mutableStateOf(MENU_ACCENTS.first()) }
    var componentBase by remember { mutableStateOf(COMPONENT_BASES.first().first) }
    var openPresetVisible by remember { mutableStateOf(false) }
    var getCodeVisible by remember { mutableStateOf(false) }
    var presetInput by remember { mutableStateOf("") }

    LaunchedEffect(getCodeRequest) {
        if (getCodeRequest > 0) getCodeVisible = true
    }

    val presetCode =
        remember(stylePreset, baseColor, accent, chartColor, headingFont, bodyFont, iconLibrary, radius) {
            buildPresetCode(
                stylePreset = stylePreset,
                baseColor = baseColor,
                accent = accent,
                chartColor = chartColor,
                headingFont = headingFont,
                bodyFont = bodyFont,
                iconLibrary = iconLibrary,
                radius = radius,
            )
        }
    val snippet =
        remember(stylePreset, baseColor, accent) {
            """
            Theme(
                preset = StylePreset.${stylePreset.name},
                baseColor = BaseColor.${baseColor.name},
                accent = Accent.${accent.name},
            ) {
                // your content
            }
            """.trimIndent()
        }
    val presetCopy = rememberCopyToClipboard("--preset $presetCode")
    val codeCopy = rememberCopyToClipboard(snippet)

    Theme(
        preset = stylePreset,
        baseColor = baseColor,
        accent = accent,
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
                        CreateCustomizerPickers(
                            stylePreset = stylePreset,
                            onStylePresetChange = onStylePresetChange,
                            baseColor = baseColor,
                            onBaseColorChange = {
                                onBaseColorChange(it)
                                chartColor = it.label
                            },
                            accent = accent,
                            onAccentChange = onAccentChange,
                            chartColor = chartColor,
                            onChartColorChange = { chartColor = it },
                            headingFont = headingFont,
                            onHeadingFontChange = { headingFont = it },
                            bodyFont = bodyFont,
                            onBodyFontChange = { bodyFont = it },
                            iconLibrary = iconLibrary,
                            onIconLibraryChange = { iconLibrary = it },
                            radius = radius,
                            onRadiusChange = { radius = it },
                            menuColor = menuColor,
                            onMenuColorChange = { menuColor = it },
                            menuAccent = menuAccent,
                            onMenuAccentChange = { menuAccent = it },
                            componentBase = componentBase,
                            onComponentBaseChange = { componentBase = it },
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
                            CopyLabel(presetCopy.justCopied, "--preset $presetCode")
                        }
                        Button(
                            onClick = { openPresetVisible = true },
                            variant = ButtonVariant.Outline,
                            size = ButtonSize.Sm,
                        ) {
                            Text { "Open" }
                        }
                        Button(
                            onClick = {
                                onStylePresetChange(StylePreset.entries.random())
                                onBaseColorChange(BaseColor.entries.random())
                                onAccentChange(Accent.entries.random())
                                chartColor = BaseColor.entries.random().label
                                headingFont = HEADING_FONTS.random()
                                bodyFont = BODY_FONTS.random()
                                iconLibrary = ICON_LIBRARIES.random()
                                radius = RADII.random()
                                menuColor = MENU_COLORS.random()
                                menuAccent = MENU_ACCENTS.random()
                                componentBase = COMPONENT_BASES.random().first
                            },
                            variant = ButtonVariant.Outline,
                            size = ButtonSize.Sm,
                        ) {
                            Text { "Shuffle" }
                        }
                        Button(
                            onClick = { getCodeVisible = true },
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
                                        onOpenCommandPalette()
                                    }
                                    MainMenuItem("Open Preset...", "O") {
                                        menuOpen = false
                                        openPresetVisible = true
                                    }
                                    MainMenuItem("Shuffle", "R") {
                                        menuOpen = false
                                        onStylePresetChange(StylePreset.entries.random())
                                        onBaseColorChange(BaseColor.entries.random())
                                        onAccentChange(Accent.entries.random())
                                    }
                                    MainMenuItem("Light/Dark", "D") {
                                        menuOpen = false
                                    }
                                    FieldSeparator()
                                    MainMenuItem("Reset", "⇧R") {
                                        menuOpen = false
                                        onStylePresetChange(StylePreset.Nova)
                                        onBaseColorChange(BaseColor.Neutral)
                                        onAccentChange(Accent.Base)
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
                        stylePreset = stylePreset,
                        onStylePresetChange = onStylePresetChange,
                        baseColor = baseColor,
                        onBaseColorChange = {
                            onBaseColorChange(it)
                            chartColor = it.label
                        },
                        accent = accent,
                        onAccentChange = onAccentChange,
                        chartColor = chartColor,
                        onChartColorChange = { chartColor = it },
                        headingFont = headingFont,
                        onHeadingFontChange = { headingFont = it },
                        bodyFont = bodyFont,
                        onBodyFontChange = { bodyFont = it },
                        iconLibrary = iconLibrary,
                        onIconLibraryChange = { iconLibrary = it },
                        radius = radius,
                        onRadiusChange = { radius = it },
                        menuColor = menuColor,
                        onMenuColorChange = { menuColor = it },
                        menuAccent = menuAccent,
                        onMenuAccentChange = { menuAccent = it },
                        componentBase = componentBase,
                        onComponentBaseChange = { componentBase = it },
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
                            CopyLabel(presetCopy.justCopied, "--preset $presetCode")
                        }
                        Button(
                            onClick = { openPresetVisible = true },
                            variant = ButtonVariant.Outline,
                            size = ButtonSize.Sm,
                            modifier = Modifier.fillMaxWidth(),
                            style = fullWidth,
                        ) {
                            Text { "Open Preset" }
                        }
                        Button(
                            onClick = {
                                onStylePresetChange(StylePreset.entries.random())
                                onBaseColorChange(BaseColor.entries.random())
                                onAccentChange(Accent.entries.random())
                                chartColor = BaseColor.entries.random().label
                                headingFont = HEADING_FONTS.random()
                                bodyFont = BODY_FONTS.random()
                                iconLibrary = ICON_LIBRARIES.random()
                                radius = RADII.random()
                                menuColor = MENU_COLORS.random()
                                menuAccent = MENU_ACCENTS.random()
                                componentBase = COMPONENT_BASES.random().first
                            },
                            variant = ButtonVariant.Outline,
                            size = ButtonSize.Sm,
                            modifier = Modifier.fillMaxWidth(),
                            style = fullWidth,
                        ) {
                            Text { "Shuffle" }
                        }
                        Button(
                            onClick = {
                                onStylePresetChange(StylePreset.Nova)
                                onBaseColorChange(BaseColor.Neutral)
                                onAccentChange(Accent.Base)
                                chartColor = BaseColor.Neutral.label
                                headingFont = HEADING_FONTS.first()
                                bodyFont = BODY_FONTS.first()
                                iconLibrary = ICON_LIBRARIES.first()
                                radius = RADII.first()
                                menuColor = MENU_COLORS.first()
                                menuAccent = MENU_ACCENTS.first()
                                componentBase = COMPONENT_BASES.first().first
                            },
                            variant = ButtonVariant.Ghost,
                            size = ButtonSize.Sm,
                            modifier = Modifier.fillMaxWidth(),
                            style = fullWidth,
                        ) {
                            Text { "Reset" }
                        }
                        Button(
                            onClick = onOpenCommandPalette,
                            variant = ButtonVariant.Outline,
                            size = ButtonSize.Sm,
                            modifier = Modifier.fillMaxWidth(),
                            style = fullWidth,
                        ) {
                            Text { "Command Palette" }
                        }
                        Button(
                            onClick = { getCodeVisible = true },
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

    Dialog(open = openPresetVisible, onOpenChange = { openPresetVisible = it }) {
        DialogContent {
        DialogHeader {
            DialogTitle("Open Preset")
            DialogDescription("Paste a preset code to load a saved configuration.")
        }
        FieldGroup(modifier = Modifier.padding(top = twSpacing.md)) {
            Field {
                FieldLabel("Preset")
                Input(
                    value = presetInput,
                    onValueChange = { presetInput = it },
                    placeholder = "e.g. b2D0wqNxT",
                    modifier = Modifier.fillMaxWidth(),
                )
            }
        }
        DialogFooter(modifier = Modifier.padding(top = twSpacing.md)) {
            Button(onClick = { openPresetVisible = false }, variant = ButtonVariant.Outline) {
                Text { "Cancel" }
            }
            Button(
                onClick = {
                    // Accept any non-empty code as a no-op apply for catalog demo purposes.
                    if (presetInput.isNotBlank()) {
                        openPresetVisible = false
                        presetInput = ""
                    }
                },
            ) {
                Text { "Open" }
            }
        }
        }
    }

    Dialog(open = getCodeVisible, onOpenChange = { getCodeVisible = it }) {
        DialogContent {
        DialogHeader {
            DialogTitle("Get Code")
            DialogDescription("Copy the Theme() setup for your current Style, Base Color, and Accent.")
        }
        Text(
            variant = TextVariant.BodySmall,
            textStyle = TextColors.Muted,
            modifier = Modifier.padding(top = twSpacing.md),
        ) { snippet }
        DialogFooter(modifier = Modifier.padding(top = twSpacing.md)) {
            Button(onClick = { getCodeVisible = false }, variant = ButtonVariant.Outline) {
                Text { "Close" }
            }
            Button(onClick = codeCopy.copy) {
                CopyLabel(codeCopy.justCopied, "Copy")
            }
        }
        }
    }
}

@Composable
private fun CreateCustomizerPickers(
    stylePreset: StylePreset,
    onStylePresetChange: (StylePreset) -> Unit,
    baseColor: BaseColor,
    onBaseColorChange: (BaseColor) -> Unit,
    accent: Accent,
    onAccentChange: (Accent) -> Unit,
    chartColor: String,
    onChartColorChange: (String) -> Unit,
    headingFont: String,
    onHeadingFontChange: (String) -> Unit,
    bodyFont: String,
    onBodyFontChange: (String) -> Unit,
    iconLibrary: String,
    onIconLibraryChange: (String) -> Unit,
    radius: String,
    onRadiusChange: (String) -> Unit,
    menuColor: String,
    onMenuColorChange: (String) -> Unit,
    menuAccent: String,
    onMenuAccentChange: (String) -> Unit,
    componentBase: String,
    onComponentBaseChange: (String) -> Unit,
    compact: Boolean,
) {
    val rowModifier = if (compact) Modifier.width(144.dp) else Modifier.fillMaxWidth()

    PresetSelectorRow(
        label = "Style",
        value = stylePreset.label,
        options = StylePreset.entries,
        onValueChange = onStylePresetChange,
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
        onValueChange = onBaseColorChange,
        optionLabel = { it.label },
        modifier = rowModifier,
        swatch = {
            Box(modifier = Modifier.size(20.dp).background(baseColor.light.primary, CircleShape))
        },
        optionSwatch = { option ->
            Box(modifier = Modifier.size(14.dp).background(option.light.primary, CircleShape))
        },
    )
    PresetSelectorRow(
        label = "Theme",
        value = if (accent == Accent.Base) baseColor.label else accent.label,
        options = Accent.entries,
        onValueChange = onAccentChange,
        optionLabel = { if (it == Accent.Base) baseColor.label else it.label },
        modifier = rowModifier,
        swatch = {
            val swatch =
                remember(accent, baseColor) {
                    accent.applyTo(baseColor.light, dark = false).primary
                }
            Box(modifier = Modifier.size(20.dp).background(swatch, CircleShape))
        },
        optionSwatch = { option ->
            val swatch =
                remember(option, baseColor) {
                    option.applyTo(baseColor.light, dark = false).primary
                }
            Box(modifier = Modifier.size(14.dp).background(swatch, CircleShape))
        },
    )
    PresetSelectorRow(
        label = "Chart Color",
        value = chartColor,
        options = BaseColor.entries.map { it.label } + Accent.entries.filter { it != Accent.Base }.map { it.label },
        onValueChange = onChartColorChange,
        optionLabel = { it },
        modifier = rowModifier,
        swatch = {
            Box(modifier = Modifier.size(20.dp).background(currentTheme.colors.chart1, CircleShape))
        },
    )
    if (!compact) FieldSeparator()
    PresetSelectorRow(
        label = "Heading",
        value = headingFont,
        options = HEADING_FONTS,
        onValueChange = onHeadingFontChange,
        optionLabel = { it },
        modifier = rowModifier,
        swatch = {
            Text(variant = TextVariant.LabelLarge, textStyle = TextColors.Muted) { "Aa" }
        },
    )
    PresetSelectorRow(
        label = "Font",
        value = bodyFont,
        options = BODY_FONTS,
        onValueChange = onBodyFontChange,
        optionLabel = { it },
        modifier = rowModifier,
        swatch = {
            Text(variant = TextVariant.LabelLarge, textStyle = TextColors.Muted) { "Aa" }
        },
    )
    if (!compact) FieldSeparator()
    PresetSelectorRow(
        label = "Icon Library",
        value = iconLibrary,
        options = ICON_LIBRARIES,
        onValueChange = onIconLibraryChange,
        optionLabel = { it },
        modifier = rowModifier,
        swatch = {},
    )
    PresetSelectorRow(
        label = "Radius",
        value = radius,
        options = RADII,
        onValueChange = onRadiusChange,
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
        value = menuColor,
        options = MENU_COLORS,
        onValueChange = onMenuColorChange,
        optionLabel = { it },
        modifier = rowModifier,
        swatch = {},
    )
    PresetSelectorRow(
        label = "Menu Accent",
        value = menuAccent,
        options = MENU_ACCENTS,
        onValueChange = onMenuAccentChange,
        optionLabel = { it },
        modifier = rowModifier,
        swatch = {},
    )
    PresetSelectorRow(
        label = "Base",
        value = componentBase,
        options = COMPONENT_BASES.map { it.first },
        onValueChange = onComponentBaseChange,
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

private fun buildPresetCode(
    stylePreset: StylePreset,
    baseColor: BaseColor,
    accent: Accent,
    chartColor: String,
    headingFont: String,
    bodyFont: String,
    iconLibrary: String,
    radius: String,
): String {
    val seed =
        listOf(
            stylePreset.name,
            baseColor.name,
            accent.name,
            chartColor,
            headingFont,
            bodyFont,
            iconLibrary,
            radius,
        ).joinToString("|").hashCode()
    val alphabet = "abcdefghijklmnopqrstuvwxyzABCDEFGHIJKLMNOPQRSTUVWXYZ0123456789"
    val random = Random(seed)
    return buildString {
        append('b')
        repeat(8) { append(alphabet[random.nextInt(alphabet.length)]) }
    }
}
