@file:OptIn(ExperimentalFoundationStyleApi::class)

package com.toutiaoyan.compose.ui.components.togglegroup

import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.selection.selectableGroup
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
    val variant: ToggleVariant,
    val size: ToggleSize,
    val orientation: ToggleGroupOrientation,
    val spacing: Dp,
    val disabled: Boolean,
    val registry: GroupChildRegistry,
) {
    fun isPressed(itemValue: String) = itemValue in value

    fun toggle(itemValue: String) {
        val pressed = isPressed(itemValue)
        val next =
            when {
                type == ToggleGroupType.Single && pressed -> value
                type == ToggleGroupType.Single -> setOf(itemValue)
                pressed -> value - itemValue
                else -> value + itemValue
            }
        onValueChange(next)
    }
}

private val LocalToggleGroup = staticCompositionLocalOf<ToggleGroupController?> { null }

@Composable
private fun toggleGroupController(): ToggleGroupController =
    checkNotNull(LocalToggleGroup.current) {
        "ToggleGroupItem must be used inside ToggleGroup { … }"
    }

/** Selection of the nearest [ToggleGroup]. Matches Radix toggle-group context. */
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
 * Real shadcn strips per-item corners/borders via CSS; here the group draws a
 * single shared border ([ToggleVariant.Outline] only) and each item is
 * borderless. Focused items raise `zIndex(10)` so the ring isn't clipped by a
 * neighbor (`focus-visible:z-10`). [type] is [ToggleGroupType.Multiple] (toggle membership) or
 * [ToggleGroupType.Single] (exactly one selected).
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
    type: ToggleGroupType = ToggleGroupType.Multiple,
    variant: ToggleVariant = ToggleVariant.Default,
    size: ToggleSize = ToggleSize.Md,
    orientation: ToggleGroupOrientation = ToggleGroupOrientation.Horizontal,
    spacing: Dp = ToggleGroupDefaults.Spacing,
    defaultValue: Set<String> = emptySet(),
    disabled: Boolean = false,
    style: Style = Style,
    contentDescription: String? = null,
    content: @Composable ToggleGroupScope.() -> Unit,
) {
    var internalValue by remember { mutableStateOf(defaultValue) }
    val current = value ?: internalValue
    val setValue: (Set<String>) -> Unit = { next ->
        if (value == null) internalValue = next
        onValueChange?.invoke(next)
    }
    val registry = remember { GroupChildRegistry() }
    val controller =
        ToggleGroupController(
            type = type,
            value = current,
            onValueChange = setValue,
            variant = variant,
            size = size,
            orientation = orientation,
            spacing = spacing,
            disabled = disabled,
            registry = registry,
        )
    val styleState = remember { MutableStyleState(MutableInteractionSource()) }
    val spaced = spacing > 0.dp
    val outerModifier =
        if (variant == ToggleVariant.Outline && !spaced) {
            modifier.styleable(
                styleState,
                rememberToggleGroupOutlineStyle() then style,
            )
        } else {
            modifier
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
        LocalRaiseFocusZIndex provides true,
    ) {
        val scope = ToggleGroupScope()
        when (orientation) {
            ToggleGroupOrientation.Horizontal ->
                Row(
                    modifier = labelledModifier,
                    horizontalArrangement = Arrangement.spacedBy(spacing),
                ) { scope.content() }
            ToggleGroupOrientation.Vertical ->
                Column(
                    modifier = labelledModifier,
                    verticalArrangement = Arrangement.spacedBy(spacing),
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
    variant: ToggleVariant = ToggleVariant.Default,
    size: ToggleSize = ToggleSize.Md,
    orientation: ToggleGroupOrientation = ToggleGroupOrientation.Horizontal,
    spacing: Dp = ToggleGroupDefaults.Spacing,
    disabled: Boolean = false,
    style: Style = Style,
    contentDescription: String? = null,
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
    variant: ToggleVariant = ToggleVariant.Default,
    size: ToggleSize = ToggleSize.Md,
    orientation: ToggleGroupOrientation = ToggleGroupOrientation.Horizontal,
    spacing: Dp = ToggleGroupDefaults.Spacing,
    disabled: Boolean = false,
    style: Style = Style,
    contentDescription: String? = null,
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
    content: @Composable () -> Unit,
) {
    val controller = toggleGroupController()
    val (index, count) = controller.registry.register()
    val rounded = twRadius.md
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
    val itemShape = RoundedCornerShape(corners.topStart, corners.topEnd, corners.bottomEnd, corners.bottomStart)
    Toggle(
        pressed = controller.isPressed(value),
        onPressedChange = { controller.toggle(value) },
        modifier = modifier,
        enabled = enabled && !disabled && !controller.disabled,
        variant = controller.variant,
        size = controller.size,
        label = label,
        style =
            Style {
                shape(itemShape)
                if (controller.variant == ToggleVariant.Outline && !spaced) borderWidth(0.dp)
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
) {
    ToggleGroupItem(
        value = value,
        modifier = modifier,
        enabled = enabled,
        disabled = disabled,
        label = label,
        style = style,
    ) {
        Text { label }
    }
}
