@file:OptIn(ExperimentalFoundationStyleApi::class)

package com.toutiaoyan.compose.ui.components.togglegroup

import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.style.contentPadding
import androidx.compose.runtime.DisposableEffect
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.focus.focusProperties
import androidx.compose.ui.focus.onFocusChanged
import androidx.compose.ui.focus.focusRequester
import androidx.compose.ui.input.key.*
import androidx.compose.ui.platform.LocalLayoutDirection
import androidx.compose.ui.unit.LayoutDirection
import com.toutiaoyan.compose.ui.components.field.LocalFieldSetDisabled
import com.toutiaoyan.compose.ui.theme.currentTheme
import com.toutiaoyan.compose.ui.tokens.StylePreset
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.selection.selectableGroup
import androidx.compose.foundation.shape.AbsoluteRoundedCornerShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.style.ExperimentalFoundationStyleApi
import androidx.compose.foundation.style.MutableStyleState
import androidx.compose.foundation.style.Style
import androidx.compose.foundation.style.styleable
import androidx.compose.foundation.style.then
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.Immutable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.runtime.staticCompositionLocalOf
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.BlendMode
import androidx.compose.ui.graphics.CompositeShader
import androidx.compose.ui.graphics.LinearGradientShader
import androidx.compose.ui.graphics.Shader
import androidx.compose.ui.graphics.ShaderBrush
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import com.toutiaoyan.compose.tailwind.core.twRadius
import com.toutiaoyan.compose.ui.components.buttongroup.ButtonGroupOrientation
import com.toutiaoyan.compose.ui.components.buttongroup.GroupChildRegistry
import com.toutiaoyan.compose.ui.components.buttongroup.LocalRaiseFocusZIndex
import com.toutiaoyan.compose.ui.components.buttongroup.groupItemCorners
import com.toutiaoyan.compose.ui.components.buttongroup.register
import com.toutiaoyan.compose.ui.components.text.Text
import com.toutiaoyan.compose.ui.components.toggle.Toggle
import com.toutiaoyan.compose.ui.components.toggle.ToggleSize
import com.toutiaoyan.compose.ui.components.toggle.ToggleVariant

/** Local change veto; DOM native-event payloads are intentionally not exposed. */
class ToggleGroupChangeDetails internal constructor() {
    var isCanceled: Boolean = false
        private set

    fun cancel() {
        isCanceled = true
    }
}

enum class ToggleGroupType { Single, Multiple }

enum class ToggleGroupOrientation { Horizontal, Vertical }

/** Official ToggleGroup tokens. [Spacing] is shadcn `spacing={2}` (8dp). */
object ToggleGroupDefaults {
    val Spacing = 8.dp
}

@Immutable
internal class ToggleGroupController(
    val type: ToggleGroupType,
    val value: Set<String>,
    val onValueChange: (Set<String>) -> Unit,
    val variant: ToggleVariant?,
    val size: ToggleSize?,
    val orientation: ToggleGroupOrientation,
    val spacing: Dp,
    val disabled: Boolean,
    val registry: GroupChildRegistry,
    val loopFocus: Boolean,
    val requestValueChange: (Set<String>, ToggleGroupChangeDetails) -> Unit,
) {
    fun isPressed(itemValue: String) = itemValue in value

    fun toggle(
        itemValue: String,
        details: ToggleGroupChangeDetails,
    ) {
        val pressed = isPressed(itemValue)
        val next =
            when {
                type == ToggleGroupType.Single && pressed -> emptySet()
                type == ToggleGroupType.Single -> setOf(itemValue)
                pressed -> value - itemValue
                else -> value + itemValue
            }
        requestValueChange(next, details)
    }
}

private class ToggleGroupNavigation {
    val items = androidx.compose.runtime.mutableStateMapOf<String, Pair<FocusRequester, Boolean>>()
    var focused by mutableStateOf<String?>(null)
    val order = androidx.compose.runtime.mutableStateListOf<String>()
}

private val LocalToggleGroupNavigation = staticCompositionLocalOf<ToggleGroupNavigation?> { null }
private val LocalToggleGroup = staticCompositionLocalOf<ToggleGroupController?> { null }

@Composable
private fun toggleGroupController(): ToggleGroupController =
    checkNotNull(LocalToggleGroup.current) {
        "ToggleGroupItem must be used inside ToggleGroup { … }"
    }

/** Selection of the nearest [ToggleGroup]. Matches Base UI toggle-group context. */
class ToggleGroupState(
    val type: ToggleGroupType,
    val value: Set<String>,
    val onValueChange: (Set<String>) -> Unit,
) {
    fun isPressed(itemValue: String) = itemValue in value
}

/** Reads the nearest [ToggleGroup] state. */
@Composable
fun toggleGroupState(): ToggleGroupState {
    val ctrl = toggleGroupController()
    return ToggleGroupState(type = ctrl.type, value = ctrl.value, onValueChange = ctrl.onValueChange)
}

class ToggleGroupScope internal constructor()

/**
 * A segmented row of [Toggle]s matching real shadcn/ui's `toggle-group.tsx`:
 *
 * ```
 * ToggleGroup
 * └── ToggleGroupItem
 * ```
 *
 * Nova strips each connected item's leading border paint while preserving its full
 * bounds and zero-gap arrangement. Historical presets with an explicit Outline root
 * draw a shared border and borderless items. Focused items raise `zIndex(10)` so the ring isn't clipped by a
 * neighbor (`focus-visible:z-10`). [type] is [ToggleGroupType.Multiple] (toggle membership) or
 * [ToggleGroupType.Single] (at most one selected).
 *
 * Root variant/size override item presets when supplied. Omitted root presets let
 * each item choose its own variant/size, falling back to Default/Md.
 *
 * Since 0.3.40, Set roots default to Single, matching Base UI. Pass
 * `type = ToggleGroupType.Multiple` to retain independent membership toggles.
 *
 * Item values must be nonempty and unique within this group; invalid identities throw
 * [IllegalArgumentException]. Empty children are supported. Empty visible labels require
 * a nonempty accessible [ToggleGroupItem] label (use its content slot for custom visuals).
 *
 * [loopFocus] defaults to true; set it to false to stop arrow focus at enabled endpoints.
 *
 * Usage:
 * ```
 * var value by remember { mutableStateOf(setOf("bold")) }
 * ToggleGroup(value = value, onValueChange = { value = it }) {
 *     ToggleGroupItem("bold", "B")
 *     ToggleGroupItem("italic", "I")
 * }
 * ToggleGroup(..., orientation = ToggleGroupOrientation.Vertical) { … }
 * ToggleGroup(..., spacing = ToggleGroupDefaults.Spacing, variant = ToggleVariant.Outline) { … }
 * ```
 */
@Composable
fun ToggleGroup(
    value: Set<String>? = null,
    onValueChange: ((Set<String>) -> Unit)? = null,
    modifier: Modifier = Modifier,
    type: ToggleGroupType = ToggleGroupType.Single,
    variant: ToggleVariant? = null,
    size: ToggleSize? = null,
    orientation: ToggleGroupOrientation = ToggleGroupOrientation.Horizontal,
    spacing: Dp = ToggleGroupDefaults.Spacing,
    defaultValue: Set<String> = emptySet(),
    disabled: Boolean = false,
    style: Style = Style,
    contentDescription: String? = null,
    loopFocus: Boolean,
    content: @Composable ToggleGroupScope.() -> Unit,
) {
    ToggleGroup(
        value = value,
        onValueChange = onValueChange,
        modifier = modifier,
        type = type,
        variant = variant,
        size = size,
        orientation = orientation,
        spacing = spacing,
        defaultValue = defaultValue,
        disabled = disabled,
        style = style,
        contentDescription = contentDescription,
        loopFocus = loopFocus,
        content = content,
        onValueChangeRequest = { _, _ -> },
    )
}

/**
 * Cancellable change overload. Call `details.cancel()` to stop the update.
 * Item callbacks precede the group request; accepted notifications precede internal commit.
 * String roots expose empty/singleton Sets in the request. DOM event payloads are not exposed.
 */
@Composable
fun ToggleGroup(
    value: Set<String>? = null,
    onValueChange: ((Set<String>) -> Unit)? = null,
    modifier: Modifier = Modifier,
    type: ToggleGroupType = ToggleGroupType.Single,
    variant: ToggleVariant? = null,
    size: ToggleSize? = null,
    orientation: ToggleGroupOrientation = ToggleGroupOrientation.Horizontal,
    spacing: Dp = ToggleGroupDefaults.Spacing,
    defaultValue: Set<String> = emptySet(),
    disabled: Boolean = false,
    style: Style = Style,
    contentDescription: String? = null,
    loopFocus: Boolean = true,
    onValueChangeRequest: (Set<String>, ToggleGroupChangeDetails) -> Unit,
    content: @Composable ToggleGroupScope.() -> Unit,
) {
    var internalValue by remember { mutableStateOf(defaultValue) }
    val current = value ?: internalValue
    val requestValueChange: (Set<String>, ToggleGroupChangeDetails) -> Unit = { next, details ->
        onValueChangeRequest(next, details)
        if (!details.isCanceled) {
            onValueChange?.invoke(next)
            if (value == null) internalValue = next
        }
    }
    val registry = remember { GroupChildRegistry() }
    val navigation = remember { ToggleGroupNavigation() }
    val nova = currentTheme.activePreset == StylePreset.Nova
    val controller =
        ToggleGroupController(
            type = type,
            value = current,
            onValueChange = { requestValueChange(it, ToggleGroupChangeDetails()) },
            requestValueChange = requestValueChange,
            variant = variant,
            size = size,
            orientation = orientation,
            spacing = spacing,
            disabled = disabled || LocalFieldSetDisabled.current,
            registry = registry,
            loopFocus = loopFocus,
        )
    val styleState = remember { MutableStyleState(MutableInteractionSource()) }
    val spaced = spacing > 0.dp
    val outerModifier =
        if (variant == ToggleVariant.Outline && !spaced && !nova) {
            modifier.styleable(
                styleState,
                rememberToggleGroupOutlineStyle() then style,
            )
        } else {
            modifier.styleable(styleState, style)
        }
    val groupedModifier =
        if (type == ToggleGroupType.Single) outerModifier.selectableGroup() else outerModifier
    val labelledModifier =
        groupedModifier.then(
            if (contentDescription != null) {
                Modifier.semantics { this.contentDescription = contentDescription }
            } else {
                Modifier
            },
        )

    CompositionLocalProvider(
        LocalToggleGroup provides controller,
        LocalToggleGroupNavigation provides navigation,
        LocalRaiseFocusZIndex provides true,
    ) {
        val scope = ToggleGroupScope()
        when (orientation) {
            ToggleGroupOrientation.Horizontal ->
                Row(
                    modifier = labelledModifier,
                    horizontalArrangement =
                        Arrangement.spacedBy(spacing),
                ) { scope.content() }
            ToggleGroupOrientation.Vertical ->
                Column(
                    modifier = labelledModifier,
                    verticalArrangement =
                        Arrangement.spacedBy(spacing),
                ) { scope.content() }
        }
    }
}

/**
 * Single-type [ToggleGroup] using official string [value] / [onValueChange]
 * (`""` means none selected). Delegates to the Set-based root.
 */
@Composable
fun ToggleGroup(
    value: String,
    onValueChange: (String) -> Unit,
    modifier: Modifier = Modifier,
    type: ToggleGroupType = ToggleGroupType.Single,
    variant: ToggleVariant? = null,
    size: ToggleSize? = null,
    orientation: ToggleGroupOrientation = ToggleGroupOrientation.Horizontal,
    spacing: Dp = ToggleGroupDefaults.Spacing,
    disabled: Boolean = false,
    style: Style = Style,
    contentDescription: String? = null,
    loopFocus: Boolean,
    content: @Composable ToggleGroupScope.() -> Unit,
) {
    ToggleGroup(
        value = value,
        onValueChange = onValueChange,
        modifier = modifier,
        type = type,
        variant = variant,
        size = size,
        orientation = orientation,
        spacing = spacing,
        disabled = disabled,
        style = style,
        contentDescription = contentDescription,
        loopFocus = loopFocus,
        content = content,
        onValueChangeRequest = { _, _ -> },
    )
}

/**
 * Cancellable change overload. Call `details.cancel()` to stop the update.
 * Item callbacks precede the group request; accepted notifications precede internal commit.
 * String roots expose empty/singleton Sets in the request. DOM event payloads are not exposed.
 */
@Composable
fun ToggleGroup(
    value: String,
    onValueChange: (String) -> Unit,
    modifier: Modifier = Modifier,
    type: ToggleGroupType = ToggleGroupType.Single,
    variant: ToggleVariant? = null,
    size: ToggleSize? = null,
    orientation: ToggleGroupOrientation = ToggleGroupOrientation.Horizontal,
    spacing: Dp = ToggleGroupDefaults.Spacing,
    disabled: Boolean = false,
    style: Style = Style,
    contentDescription: String? = null,
    loopFocus: Boolean = true,
    onValueChangeRequest: (Set<String>, ToggleGroupChangeDetails) -> Unit,
    content: @Composable ToggleGroupScope.() -> Unit,
) {
    ToggleGroup(
        value = if (value.isEmpty()) emptySet() else setOf(value),
        onValueChange = { next -> onValueChange(next.firstOrNull().orEmpty()) },
        modifier = modifier,
        type = type,
        variant = variant,
        size = size,
        orientation = orientation,
        spacing = spacing,
        disabled = disabled,
        style = style,
        contentDescription = contentDescription,
        loopFocus = loopFocus,
        onValueChangeRequest = onValueChangeRequest,
        content = content,
    )
}

/**
 * Uncontrolled single-type [ToggleGroup] using official string [defaultValue]
 * (`""` means none selected). Delegates to the Set-based root.
 */
@Composable
fun ToggleGroup(
    defaultValue: String,
    modifier: Modifier = Modifier,
    type: ToggleGroupType = ToggleGroupType.Single,
    variant: ToggleVariant? = null,
    size: ToggleSize? = null,
    orientation: ToggleGroupOrientation = ToggleGroupOrientation.Horizontal,
    spacing: Dp = ToggleGroupDefaults.Spacing,
    disabled: Boolean = false,
    style: Style = Style,
    contentDescription: String? = null,
    loopFocus: Boolean,
    content: @Composable ToggleGroupScope.() -> Unit,
) {
    ToggleGroup(
        defaultValue = defaultValue,
        modifier = modifier,
        type = type,
        variant = variant,
        size = size,
        orientation = orientation,
        spacing = spacing,
        disabled = disabled,
        style = style,
        contentDescription = contentDescription,
        loopFocus = loopFocus,
        content = content,
        onValueChangeRequest = { _, _ -> },
    )
}

/**
 * Cancellable change overload. Call `details.cancel()` to stop the update.
 * Item callbacks precede the group request; accepted notifications precede internal commit.
 * String roots expose empty/singleton Sets in the request. DOM event payloads are not exposed.
 */
@Composable
fun ToggleGroup(
    defaultValue: String,
    modifier: Modifier = Modifier,
    type: ToggleGroupType = ToggleGroupType.Single,
    variant: ToggleVariant? = null,
    size: ToggleSize? = null,
    orientation: ToggleGroupOrientation = ToggleGroupOrientation.Horizontal,
    spacing: Dp = ToggleGroupDefaults.Spacing,
    disabled: Boolean = false,
    style: Style = Style,
    contentDescription: String? = null,
    loopFocus: Boolean = true,
    onValueChangeRequest: (Set<String>, ToggleGroupChangeDetails) -> Unit,
    content: @Composable ToggleGroupScope.() -> Unit,
) {
    ToggleGroup(
        defaultValue = if (defaultValue.isEmpty()) emptySet() else setOf(defaultValue),
        modifier = modifier,
        type = type,
        variant = variant,
        size = size,
        orientation = orientation,
        spacing = spacing,
        disabled = disabled,
        style = style,
        contentDescription = contentDescription,
        loopFocus = loopFocus,
        onValueChangeRequest = onValueChangeRequest,
        content = content,
    )
}

/** One toggle in a [ToggleGroup]. Matches shadcn's `ToggleGroupItem`. */
@Composable
fun ToggleGroupScope.ToggleGroupItem(
    value: String,
    modifier: Modifier = Modifier,
    enabled: Boolean = true,
    disabled: Boolean = false,
    label: String = value,
    style: Style = Style,
    variant: ToggleVariant? = null,
    size: ToggleSize? = null,
    content: @Composable () -> Unit,
) {
    ToggleGroupItem(
        value = value,
        modifier = modifier,
        enabled = enabled,
        disabled = disabled,
        label = label,
        style = style,
        variant = variant,
        size = size,
        content = content,
        onPressedChange = { _, _ -> },
    )
}

/**
 * Cancellable change overload. Call `details.cancel()` to stop the update.
 * Item callbacks precede the group request; accepted notifications precede internal commit.
 * String roots expose empty/singleton Sets in the request. DOM event payloads are not exposed.
 */
@Composable
fun ToggleGroupScope.ToggleGroupItem(
    value: String,
    modifier: Modifier = Modifier,
    enabled: Boolean = true,
    disabled: Boolean = false,
    label: String = value,
    style: Style = Style,
    variant: ToggleVariant? = null,
    size: ToggleSize? = null,
    onPressedChange: (Boolean, ToggleGroupChangeDetails) -> Unit,
    content: @Composable () -> Unit,
) {
    require(value.isNotEmpty()) { "ToggleGroupItem value must be nonempty" }
    val controller = toggleGroupController()
    val resolvedVariant = controller.variant ?: variant ?: ToggleVariant.Default
    val resolvedSize = controller.size ?: size ?: ToggleSize.Md
    val (index, count) = controller.registry.register()
    val nova = currentTheme.activePreset == StylePreset.Nova
    val rounded = if (nova) twRadius.lg else twRadius.md
    val spaced = controller.spacing > 0.dp
    val corners =
        if (spaced) {
            groupItemCorners(0, 1, ButtonGroupOrientation.Horizontal, rounded)
        } else {
            groupItemCorners(
                index,
                count,
                when (controller.orientation) {
                    ToggleGroupOrientation.Horizontal -> ButtonGroupOrientation.Horizontal
                    ToggleGroupOrientation.Vertical -> ButtonGroupOrientation.Vertical
                },
                rounded,
            )
        }
    val itemShape =
        if (nova && !spaced) {
            // Nova's first/last corner utilities address physical edges, even in RTL.
            AbsoluteRoundedCornerShape(corners.topStart, corners.topEnd, corners.bottomEnd, corners.bottomStart)
        } else {
            RoundedCornerShape(corners.topStart, corners.topEnd, corners.bottomEnd, corners.bottomStart)
        }
    val navigation = checkNotNull(LocalToggleGroupNavigation.current)
    val requester = remember { FocusRequester() }
    val resolvedEnabled = enabled && !disabled && !controller.disabled && !LocalFieldSetDisabled.current
    DisposableEffect(navigation, value) {
        require(value !in navigation.order) { "ToggleGroupItem values must be unique within a group: $value" }
        navigation.order.add(value)
        onDispose {
            navigation.order.remove(value)
            navigation.items.remove(value)
        }
    }
    androidx.compose.runtime.SideEffect { navigation.items[value] = requester to resolvedEnabled }
    val rtl = LocalLayoutDirection.current == LayoutDirection.Rtl
    val borderColor = if (nova) currentTheme.colors.input else currentTheme.colors.border
    val borderPixels = with(androidx.compose.ui.platform.LocalDensity.current) { 1.dp.toPx() }
    val horizontal = controller.orientation == ToggleGroupOrientation.Horizontal
    val removeRight = horizontal && rtl && !nova
    val connectedBorder =
        remember(borderColor, borderPixels, horizontal, removeRight) {
            ConnectedItemBorder(
                borderColor,
                borderPixels,
                horizontal,
                removeRight,
            )
        }
    val navigationModifier =
        Modifier.focusRequester(requester).focusProperties {
            val available = navigation.order.filter { navigation.items[it]?.second == true }
            val entry =
                navigation.focused?.takeIf { it in available }
                    ?: available.firstOrNull { controller.isPressed(it) } ?: available.firstOrNull()
            canFocus = resolvedEnabled && value == entry
        }.onFocusChanged {
            if (it.isFocused) navigation.focused = value
        }.onPreviewKeyEvent { event ->
            if (event.type != KeyEventType.KeyDown || !resolvedEnabled) return@onPreviewKeyEvent false
            val horizontal = controller.orientation == ToggleGroupOrientation.Horizontal
            val forward =
                if (horizontal) {
                    if (rtl) Key.DirectionLeft else Key.DirectionRight
                } else {
                    Key.DirectionDown
                }
            val backward =
                if (horizontal) {
                    if (rtl) Key.DirectionRight else Key.DirectionLeft
                } else {
                    Key.DirectionUp
                }
            val available = navigation.order.filter { navigation.items[it]?.second == true }
            if (available.isEmpty()) return@onPreviewKeyEvent false
            val current = available.indexOf(value).coerceAtLeast(0)
            val target =
                when (event.key) {
                    forward ->
                        if (controller.loopFocus) {
                            (current + 1) % available.size
                        } else {
                            (current + 1).coerceAtMost(available.lastIndex)
                        }
                    backward ->
                        if (controller.loopFocus) {
                            (current - 1 + available.size) % available.size
                        } else {
                            (current - 1).coerceAtLeast(0)
                        }
                    Key.MoveHome -> 0
                    Key.MoveEnd -> available.lastIndex
                    else -> return@onPreviewKeyEvent false
                }
            navigation.focused = available[target]
            navigation.items[available[target]]?.first?.requestFocus()
            true
        }
    Toggle(
        pressed = controller.isPressed(value),
        onPressedChange = {
            navigation.focused = value
            requester.requestFocus()
            val details = ToggleGroupChangeDetails()
            onPressedChange(!controller.isPressed(value), details)
            if (!details.isCanceled) controller.toggle(value, details)
        },
        modifier = modifier.then(navigationModifier),
        enabled = resolvedEnabled,
        variant = resolvedVariant,
        size = resolvedSize,
        label = label,
        style =
            Style {
                if (!nova || !spaced) shape(itemShape)
                if (controller.variant == ToggleVariant.Outline && !spaced && !nova) borderWidth(0.dp)
                // Remove only this item's leading border paint. Background, focus shadow,
                // measurement and hit bounds retain the complete resolved item shape.
                if (!spaced && (nova || controller.variant == null) &&
                    resolvedVariant == ToggleVariant.Outline && index > 0
                ) {
                    borderBrush(connectedBorder)
                }
                if (nova && !spaced) contentPadding(horizontal = 8.dp, vertical = 0.dp)
            } then style,
        content = content,
    )
}

/** Convenience item that takes a string label. */
@Composable
fun ToggleGroupScope.ToggleGroupItem(
    value: String,
    label: String,
    modifier: Modifier = Modifier,
    enabled: Boolean = true,
    disabled: Boolean = false,
    style: Style = Style,
    variant: ToggleVariant? = null,
    size: ToggleSize? = null,
) {
    ToggleGroupItem(
        value = value,
        label = label,
        modifier = modifier,
        enabled = enabled,
        disabled = disabled,
        style = style,
        variant = variant,
        size = size,
        onPressedChange = { _, _ -> },
    )
}

/**
 * Cancellable change overload. Call `details.cancel()` to stop the update.
 * Item callbacks precede the group request; accepted notifications precede internal commit.
 * String roots expose empty/singleton Sets in the request. DOM event payloads are not exposed.
 */
@Composable
fun ToggleGroupScope.ToggleGroupItem(
    value: String,
    label: String,
    modifier: Modifier = Modifier,
    enabled: Boolean = true,
    disabled: Boolean = false,
    style: Style = Style,
    variant: ToggleVariant? = null,
    size: ToggleSize? = null,
    onPressedChange: (Boolean, ToggleGroupChangeDetails) -> Unit,
) {
    ToggleGroupItem(
        value = value,
        modifier = modifier,
        enabled = enabled,
        disabled = disabled,
        label = label,
        style = style,
        variant = variant,
        size = size,
        onPressedChange = onPressedChange,
    ) {
        Text { label }
    }
}

/** Compatibility overload retaining the original positional and trailing-lambda API. */
@Composable
fun ToggleGroup(
    value: Set<String>? = null,
    onValueChange: ((Set<String>) -> Unit)? = null,
    modifier: Modifier = Modifier,
    type: ToggleGroupType = ToggleGroupType.Single,
    variant: ToggleVariant? = null,
    size: ToggleSize? = null,
    orientation: ToggleGroupOrientation = ToggleGroupOrientation.Horizontal,
    spacing: Dp = ToggleGroupDefaults.Spacing,
    defaultValue: Set<String> = emptySet(),
    disabled: Boolean = false,
    style: Style = Style,
    contentDescription: String? = null,
    content: @Composable ToggleGroupScope.() -> Unit,
) {
    ToggleGroup(
        value = value,
        onValueChange = onValueChange,
        modifier = modifier,
        type = type,
        variant = variant,
        size = size,
        orientation = orientation,
        spacing = spacing,
        defaultValue = defaultValue,
        disabled = disabled,
        style = style,
        contentDescription = contentDescription,
        loopFocus = true,
        content = content,
    )
}

/** Compatibility overload retaining the original positional and trailing-lambda API. */
@Composable
fun ToggleGroup(
    value: String,
    onValueChange: (String) -> Unit,
    modifier: Modifier = Modifier,
    type: ToggleGroupType = ToggleGroupType.Single,
    variant: ToggleVariant? = null,
    size: ToggleSize? = null,
    orientation: ToggleGroupOrientation = ToggleGroupOrientation.Horizontal,
    spacing: Dp = ToggleGroupDefaults.Spacing,
    disabled: Boolean = false,
    style: Style = Style,
    contentDescription: String? = null,
    content: @Composable ToggleGroupScope.() -> Unit,
) {
    ToggleGroup(
        value = value,
        onValueChange = onValueChange,
        modifier = modifier,
        type = type,
        variant = variant,
        size = size,
        orientation = orientation,
        spacing = spacing,
        disabled = disabled,
        style = style,
        contentDescription = contentDescription,
        loopFocus = true,
        content = content,
    )
}

/** Compatibility overload retaining the original positional and trailing-lambda API. */
@Composable
fun ToggleGroup(
    defaultValue: String,
    modifier: Modifier = Modifier,
    type: ToggleGroupType = ToggleGroupType.Single,
    variant: ToggleVariant? = null,
    size: ToggleSize? = null,
    orientation: ToggleGroupOrientation = ToggleGroupOrientation.Horizontal,
    spacing: Dp = ToggleGroupDefaults.Spacing,
    disabled: Boolean = false,
    style: Style = Style,
    contentDescription: String? = null,
    content: @Composable ToggleGroupScope.() -> Unit,
) {
    ToggleGroup(
        defaultValue = defaultValue,
        modifier = modifier,
        type = type,
        variant = variant,
        size = size,
        orientation = orientation,
        spacing = spacing,
        disabled = disabled,
        style = style,
        contentDescription = contentDescription,
        loopFocus = true,
        content = content,
    )
}

/** Compatibility overload for the original positional content API. */
@Composable
fun ToggleGroupScope.ToggleGroupItem(
    value: String,
    modifier: Modifier = Modifier,
    enabled: Boolean = true,
    disabled: Boolean = false,
    label: String = value,
    style: Style = Style,
    content: @Composable () -> Unit,
) {
    ToggleGroupItem(value, modifier, enabled, disabled, label, style, null, null, content)
}

/** Compatibility overload for the original label API. */
@Composable
fun ToggleGroupScope.ToggleGroupItem(
    value: String,
    label: String,
    modifier: Modifier = Modifier,
    enabled: Boolean = true,
    disabled: Boolean = false,
    style: Style = Style,
) {
    ToggleGroupItem(value, label, modifier, enabled, disabled, style, null, null)
}

/** Masks the joined edge while retaining both perpendicular borders. */
private class ConnectedItemBorder(
    private val color: Color,
    private val width: Float,
    private val horizontal: Boolean,
    private val removeRight: Boolean,
) : ShaderBrush() {
    override fun createShader(size: Size): Shader {
        val extent = if (horizontal) size.width else size.height
        val edge = (width / extent.coerceAtLeast(1f)).coerceAtMost(1f)
        val end = if (horizontal) Offset(size.width, 0f) else Offset(0f, size.height)
        val leadingSide =
            LinearGradientShader(
                from = if (removeRight) end else Offset.Zero,
                to = if (removeRight) Offset.Zero else end,
                colors = listOf(Color.Transparent, Color.Transparent, Color.White, Color.White),
                colorStops = listOf(0f, edge, edge, 1f),
            )
        val crossExtent = if (horizontal) size.height else size.width
        val crossEdge = (width / crossExtent.coerceAtLeast(1f)).coerceIn(0f, .5f)
        val perimeter =
            LinearGradientShader(
                from = Offset.Zero,
                to = if (horizontal) Offset(0f, size.height) else Offset(size.width, 0f),
                colors =
                    listOf(
                        Color.White,
                        Color.White,
                        Color.Transparent,
                        Color.Transparent,
                        Color.White,
                        Color.White,
                    ),
                colorStops = listOf(0f, crossEdge, crossEdge, 1f - crossEdge, 1f - crossEdge, 1f),
            )
        val mask = CompositeShader(leadingSide, perimeter, BlendMode.SrcOver)
        val tint = LinearGradientShader(Offset.Zero, Offset(1f, 0f), listOf(color, color))
        return CompositeShader(mask, tint, BlendMode.SrcIn)
    }
}
