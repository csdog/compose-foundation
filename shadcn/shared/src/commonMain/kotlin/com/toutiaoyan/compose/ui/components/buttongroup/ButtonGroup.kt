@file:OptIn(ExperimentalFoundationStyleApi::class, ExperimentalTypeInference::class)

package com.toutiaoyan.compose.ui.components.buttongroup

import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.IntrinsicSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.style.ExperimentalFoundationStyleApi
import androidx.compose.foundation.style.MutableStyleState
import androidx.compose.foundation.style.Style
import androidx.compose.foundation.style.styleable
import androidx.compose.foundation.style.then
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.Immutable
import androidx.compose.runtime.compositionLocalOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.staticCompositionLocalOf
import com.toutiaoyan.compose.ui.theme.currentTheme
import com.toutiaoyan.compose.ui.tokens.StylePreset
import androidx.compose.ui.layout.Layout
import androidx.compose.ui.layout.onGloballyPositioned
import androidx.compose.ui.layout.positionInRoot
import androidx.compose.ui.platform.LocalLayoutDirection
import androidx.compose.ui.layout.ParentDataModifier
import androidx.compose.ui.unit.Density
import androidx.compose.ui.unit.constrainHeight
import androidx.compose.ui.unit.constrainWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.semantics.isTraversalGroup
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.tooling.preview.AndroidUiModes.UI_MODE_NIGHT_YES
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import com.toutiaoyan.compose.tailwind.core.twRadius
import com.toutiaoyan.compose.ui.components.button.Button
import com.toutiaoyan.compose.ui.components.button.ButtonSize
import com.toutiaoyan.compose.ui.components.button.ButtonVariant
import com.toutiaoyan.compose.ui.components.separator.Separator
import com.toutiaoyan.compose.ui.components.separator.SeparatorOrientation
import com.toutiaoyan.compose.ui.components.text.Text
import com.toutiaoyan.compose.ui.components.text.TextVariant
import kotlin.experimental.ExperimentalTypeInference
import kotlin.jvm.JvmName

enum class ButtonGroupOrientation { Horizontal, Vertical }

@Immutable
internal class ButtonGroupController(
    val orientation: ButtonGroupOrientation,
    val registry: GroupChildRegistry,
    val spacing: Dp,
    val disabled: Boolean = false,
)

private val LocalButtonGroup = staticCompositionLocalOf<ButtonGroupController?> { null }

internal val LocalButtonGroupOrientation = compositionLocalOf { ButtonGroupOrientation.Horizontal }

/** Orientation of the nearest [ButtonGroup]. */
class ButtonGroupState(val orientation: ButtonGroupOrientation)

/** Reads the nearest [ButtonGroup]. */
@Composable
fun buttonGroupState(): ButtonGroupState {
    val controller =
        checkNotNull(LocalButtonGroup.current) {
            "buttonGroupState() must be used inside ButtonGroup { … }"
        }
    return ButtonGroupState(orientation = controller.orientation)
}

class ButtonGroupScope internal constructor()

/**
 * Visually joins a row (or column) of buttons matching real shadcn/ui's
 * `button-group.tsx`:
 *
 * ```
 * ButtonGroup
 * ├── ButtonGroupItem
 * ├── ButtonGroupSeparator
 * └── ButtonGroupText
 * ```
 *
 * [ButtonGroupItem] computes per-position corners (and the 1dp overlap for
 * [ButtonVariant.Outline]). Mixed children (`Input`, [Button], separator) skip
 * auto-corners — round the outer pieces yourself. Focused [Button] children raise
 * `zIndex(10)` (`[&>*]:focus-visible:z-10`). Pass
 * [rememberButtonGroupOutlineStyle] as [style] for a shared group border
 * (label + input compositions). [contentDescription] is the official `aria-label`.
 *
 * Usage:
 * ```
 * ButtonGroup(contentDescription = "Button group") {
 *     ButtonGroupItem("Copy", onClick = {})
 *     ButtonGroupItem("Share", onClick = {})
 * }
 * ```
 */
@Composable
fun ButtonGroup(
    modifier: Modifier = Modifier,
    orientation: ButtonGroupOrientation = ButtonGroupOrientation.Horizontal,
    spacing: Dp = 0.dp,
    style: Style = Style,
    disabled: Boolean = false,
    contentDescription: String? = null,
    content: @Composable ButtonGroupScope.() -> Unit,
) {
    val registry = remember { GroupChildRegistry() }
    val controller =
        ButtonGroupController(
            orientation = orientation,
            registry = registry,
            spacing = spacing,
            disabled = disabled,
        )
    val styleState = remember { MutableStyleState(MutableInteractionSource()) }
    val groupModifier =
        modifier
            .semantics {
                isTraversalGroup = true
                if (contentDescription != null) this.contentDescription = contentDescription
            }
            .styleable(styleState, style)

    CompositionLocalProvider(
        LocalButtonGroup provides controller,
        LocalButtonGroupOrientation provides orientation,
        LocalRaiseFocusZIndex provides true,
    ) {
        val scope = ButtonGroupScope()
        when (orientation) {
            ButtonGroupOrientation.Horizontal ->
                if (currentTheme.activePreset == StylePreset.Nova) {
                    NovaButtonGroupRow(groupModifier, spacing) { scope.content() }
                } else {
                    Row(
                        modifier = groupModifier.height(IntrinsicSize.Min),
                        horizontalArrangement =
                            if (spacing > 0.dp) Arrangement.spacedBy(spacing) else Arrangement.Start,
                    ) { scope.content() }
                }
            ButtonGroupOrientation.Vertical ->
                Column(
                    modifier = groupModifier.width(IntrinsicSize.Min),
                    verticalArrangement =
                        if (spacing > 0.dp) Arrangement.spacedBy(spacing) else Arrangement.Top,
                ) { scope.content() }
        }
    }
}

private object GroupSeparatorParentData : ParentDataModifier {
    override fun Density.modifyParentData(parentData: Any?): Any = this@GroupSeparatorParentData
}

/** Measure controls at their real height before stretching separators. Intrinsic
 * measurement sees icon ink bounds, which can otherwise squeeze a 32dp button to 18dp. */
@Composable
private fun NovaButtonGroupRow(
    modifier: Modifier,
    spacing: Dp,
    content: @Composable () -> Unit,
) {
    Layout(modifier = modifier, content = content) { measurables, constraints ->
        val children = arrayOfNulls<androidx.compose.ui.layout.Placeable>(measurables.size)
        val loose = constraints.copy(minWidth = 0, minHeight = 0)
        measurables.forEachIndexed { index, measurable ->
            if (measurable.parentData !== GroupSeparatorParentData) children[index] = measurable.measure(loose)
        }
        val height = constraints.constrainHeight(children.filterNotNull().maxOfOrNull { it.height } ?: 0)
        measurables.forEachIndexed { index, measurable ->
            if (children[index] == null) {
                children[index] =
                    measurable.measure(
                        loose.copy(
                            minHeight =
                            height,
                            maxHeight =
                            height,
                        ),
                    )
            }
        }
        val gap = spacing.roundToPx()
        val width =
            constraints.constrainWidth(
                children.filterNotNull().sumOf {
                    it.width
                } + gap * (children.size - 1).coerceAtLeast(0),
            )
        layout(width, height) {
            var x = 0
            children.filterNotNull().forEach { child ->
                child.placeRelative(x, (height - child.height) / 2)
                x += child.width + gap
            }
        }
    }
}

/**
 * A joined button. Matches the old `items` overload: per-position corners, and
 * a 1dp overlap between [ButtonVariant.Outline] neighbors.
 *
 * Call this inside [ButtonGroup]. Prefer [ButtonVariant.Ghost] (the default)
 * so neighbors share a flush edge without stacking outlines.
 */
@Composable
fun ButtonGroupScope.ButtonGroupItem(
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    enabled: Boolean = true,
    disabled: Boolean = false,
    variant: ButtonVariant = ButtonVariant.Ghost,
    size: ButtonSize = ButtonSize.Md,
    style: Style = Style,
    content: @Composable () -> Unit,
) {
    val controller =
        checkNotNull(LocalButtonGroup.current) {
            "ButtonGroupItem must be used inside ButtonGroup { … }"
        }
    val key = controller.registry.registerKey()
    val index = controller.registry.visualIndex(key, controller.orientation, LocalLayoutDirection.current)
    val count = controller.registry.keys.size
    val nova = currentTheme.activePreset == StylePreset.Nova
    val rounded = if (nova) twRadius.lg else twRadius.md
    val spaced = controller.spacing > 0.dp
    val corners =
        if (spaced) {
            groupItemCorners(0, 1, ButtonGroupOrientation.Horizontal, rounded)
        } else {
            groupItemCorners(index, count, controller.orientation, rounded)
        }
    val itemShape = RoundedCornerShape(corners.topStart, corners.topEnd, corners.bottomEnd, corners.bottomStart)
    val overlapModifier =
        if (!spaced && index > 0 && variant == ButtonVariant.Outline) {
            if (nova) {
                Modifier.groupBorderOverlap(controller.orientation)
            } else if (controller.orientation == ButtonGroupOrientation.Horizontal) {
                Modifier.offset(x = (-1).dp)
            } else {
                Modifier.offset(y = (-1).dp)
            }
        } else {
            Modifier
        }

    Button(
        onClick = onClick,
        enabled = enabled,
        disabled = disabled || controller.disabled,
        variant = variant,
        size = size,
        style = Style { shape(itemShape) } then style,
        modifier =
            modifier.onGloballyPositioned {
                controller.registry.updatePosition(key, it.positionInRoot())
            }.then(overlapModifier),
        content = content,
    )
}

/** Convenience item that takes a string label. */
@Composable
fun ButtonGroupScope.ButtonGroupItem(
    label: String,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    enabled: Boolean = true,
    disabled: Boolean = false,
    variant: ButtonVariant = ButtonVariant.Ghost,
    size: ButtonSize = ButtonSize.Md,
    style: Style = Style,
) {
    ButtonGroupItem(
        onClick = onClick,
        modifier = modifier,
        enabled = enabled,
        disabled = disabled,
        variant = variant,
        size = size,
        style = style,
    ) {
        Text { label }
    }
}

/** A thin divider between items in a [ButtonGroup]; insert manually where wanted. */
@Composable
fun ButtonGroupSeparator(
    modifier: Modifier = Modifier,
    style: Style = Style,
) {
    val orientation = LocalButtonGroupOrientation.current
    val nova = currentTheme.activePreset == StylePreset.Nova
    val groupSeparatorModifier =
        if (nova && orientation == ButtonGroupOrientation.Horizontal) {
            modifier.then(
                GroupSeparatorParentData,
            ).padding(vertical = 1.dp)
        } else {
            modifier
        }
    val separatorColor = currentTheme.colors.input
    val separatorStyle = if (nova) Style { background(separatorColor) } else Style
    when (orientation) {
        ButtonGroupOrientation.Horizontal ->
            Separator(
                modifier = groupSeparatorModifier.fillMaxHeight(),
                orientation = SeparatorOrientation.Vertical,
                style = separatorStyle then style,
            )
        ButtonGroupOrientation.Vertical ->
            Separator(
                modifier = modifier.fillMaxWidth(),
                orientation = SeparatorOrientation.Horizontal,
                style = style,
            )
    }
}

/** A static, non-interactive label chip for prefixing/suffixing a [ButtonGroup]. */
@Composable
fun ButtonGroupText(
    text: String,
    modifier: Modifier = Modifier,
    topStart: Dp = 0.dp,
    topEnd: Dp = 0.dp,
    bottomEnd: Dp = 0.dp,
    bottomStart: Dp = 0.dp,
    style: Style = Style,
) {
    val styleState = remember { MutableStyleState(MutableInteractionSource()) }
    Box(
        modifier =
            modifier.styleable(
                styleState,
                rememberButtonGroupTextStyle(topStart, topEnd, bottomEnd, bottomStart) then style,
            ),
        contentAlignment =
            if (currentTheme.activePreset == StylePreset.Nova) {
                Alignment.CenterStart
            } else {
                Alignment.TopStart
            },
    ) {
        Text(variant = TextVariant.LabelLarge, modifier = Modifier) { text }
    }
}

@Composable
@JvmName("ButtonGroupTextLambda")
@OverloadResolutionByLambdaReturnType
fun ButtonGroupText(
    modifier: Modifier = Modifier,
    topStart: Dp = 0.dp,
    topEnd: Dp = 0.dp,
    bottomEnd: Dp = 0.dp,
    bottomStart: Dp = 0.dp,
    style: Style = Style,
    text: () -> String,
) {
    ButtonGroupText(
        text = text(),
        modifier = modifier,
        topStart = topStart,
        topEnd = topEnd,
        bottomEnd = bottomEnd,
        bottomStart = bottomStart,
        style = style,
    )
}

@OptIn(ExperimentalFoundationStyleApi::class)
@Preview(showBackground = true, uiMode = UI_MODE_NIGHT_YES)
@Composable
fun ButtonGroupDocPreview() {
    val rounded = twRadius.md
    ButtonGroup(style = rememberButtonGroupOutlineStyle()) {
        ButtonGroupText("https://", topStart = rounded, bottomStart = rounded)
        ButtonGroupSeparator()
        Button(
            "example.com",
            onClick = {},
            variant = ButtonVariant.Ghost,
            style =
                Style {
                    shape(RoundedCornerShape(0.dp, rounded, rounded, 0.dp))
                },
        )
    }
}
