@file:OptIn(ExperimentalFoundationStyleApi::class)

package com.toutiaoyan.compose.ui.components.accordion

import androidx.compose.animation.core.tween
import androidx.compose.animation.core.CubicBezierEasing
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.EnterTransition
import androidx.compose.animation.ExitTransition
import androidx.compose.animation.expandVertically
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.shrinkVertically
import androidx.compose.foundation.clickable
import androidx.compose.foundation.focusGroup
import androidx.compose.foundation.hoverable
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.interaction.collectIsHoveredAsState
import androidx.compose.foundation.interaction.collectIsFocusedAsState
import androidx.compose.ui.draw.drawWithContent
import androidx.compose.ui.geometry.CornerRadius
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.text.style.TextDecoration
import com.toutiaoyan.compose.ui.theme.LocalTextDecoration
import androidx.compose.ui.platform.LocalInputModeManager
import androidx.compose.ui.input.InputMode
import androidx.compose.ui.platform.LocalLayoutDirection
import androidx.compose.ui.unit.LayoutDirection
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ColumnScope
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.style.ExperimentalFoundationStyleApi
import androidx.compose.foundation.style.MutableStyleState
import androidx.compose.foundation.style.Style
import androidx.compose.foundation.style.rememberUpdatedStyleState
import androidx.compose.foundation.style.styleable
import androidx.compose.foundation.style.then
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.Immutable
import androidx.compose.runtime.SideEffect
import androidx.compose.runtime.compositionLocalOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateMapOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.runtime.staticCompositionLocalOf
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.focus.focusProperties
import androidx.compose.ui.focus.onFocusChanged
import androidx.compose.ui.layout.Layout
import androidx.compose.ui.layout.layout
import androidx.compose.ui.layout.layoutId
import androidx.compose.ui.unit.Constraints
import androidx.compose.ui.unit.constrainWidth
import androidx.compose.ui.unit.constrainHeight
import androidx.compose.ui.layout.onGloballyPositioned
import androidx.compose.ui.layout.positionInParent
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.semantics.clearAndSetSemantics
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.focus.focusRequester
import androidx.compose.ui.input.key.Key
import androidx.compose.ui.input.key.KeyEvent
import androidx.compose.ui.input.key.KeyEventType
import androidx.compose.ui.input.key.key
import androidx.compose.ui.input.key.onPreviewKeyEvent
import androidx.compose.ui.input.key.type
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.semantics.disabled
import androidx.compose.ui.semantics.heading
import androidx.compose.ui.semantics.role
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.semantics.stateDescription
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import com.toutiaoyan.compose.ui.tokens.StylePreset
import com.toutiaoyan.compose.tailwind.core.twSpacing
import com.toutiaoyan.compose.ui.components.separator.Separator
import com.toutiaoyan.compose.ui.components.text.Text
import com.toutiaoyan.compose.ui.components.text.TextVariant
import com.toutiaoyan.compose.ui.styles.rememberMinTouchTargetStyle
import com.toutiaoyan.compose.ui.styles.rememberPressMotionStyle
import com.toutiaoyan.compose.ui.theme.currentTheme

enum class AccordionType { Single, Multiple }

enum class AccordionOrientation { Vertical, Horizontal }

@Immutable
internal class AccordionController(
    val type: AccordionType,
    val value: Set<String>,
    val collapsible: Boolean,
    val orientation: AccordionOrientation,
    val loop: Boolean,
    val disabled: Boolean,
    val onValueChange: (Set<String>) -> Unit,
) {
    fun isOpen(itemValue: String) = itemValue in value

    fun toggle(itemValue: String) {
        if (disabled) return
        val open = isOpen(itemValue)
        val next =
            when {
                type == AccordionType.Single && open ->
                    if (collapsible) emptySet() else value
                type == AccordionType.Single -> setOf(itemValue)
                open -> value - itemValue
                else -> value + itemValue
            }
        onValueChange(next)
    }
}

private val LocalAccordion = staticCompositionLocalOf<AccordionController?> { null }

private val LocalAccordionItem = staticCompositionLocalOf<AccordionItemScope?> { null }

private val LocalAccordionNav = compositionLocalOf<AccordionNavState?> { null }

private data class AccordionItemId(val value: String)

private class AccordionNavState {
    var measuredOrder: List<String> = emptyList()
    val itemOrder = mutableStateListOf<String>()
    val itemEnabled = mutableStateMapOf<String, Boolean>()
    private val focusRequesters = mutableMapOf<String, FocusRequester>()
    val positions = mutableMapOf<String, Offset>()

    fun requester(value: String): FocusRequester = focusRequesters.getOrPut(value) { FocusRequester() }

    fun unregister(value: String) {
        itemOrder.remove(value)
        itemEnabled.remove(value)
        focusRequesters.remove(value)
        positions.remove(value)
    }
}

private val LocalAccordionIcon =
    staticCompositionLocalOf<@Composable (isOpen: Boolean) -> Unit> {
        error("AccordionTrigger must be used inside Accordion { … }")
    }

@Composable
private fun accordionController(): AccordionController =
    checkNotNull(LocalAccordion.current) {
        "AccordionItem must be used inside Accordion { … }"
    }

/** Reads the nearest [Accordion] scope (open item ids). Matches Radix accordion context. */
@Composable
fun accordionState(): AccordionScope = AccordionScope(accordionController())

/** Reads the nearest [AccordionItem] scope. Matches Radix accordion item context. */
@Composable
fun accordionItemState(): AccordionItemScope =
    checkNotNull(LocalAccordionItem.current) {
        "accordionItemState() must be used inside AccordionItem { … }"
    }

/**
 * Slot receiver for [AccordionItem].
 *
 * [value] is the set of open item ids (Radix `value` as an array in both
 * single and multiple mode, matching Base UI).
 */
class AccordionScope internal constructor(
    private val controller: AccordionController,
) {
    val value: Set<String> get() = controller.value

    fun isOpen(itemValue: String) = controller.isOpen(itemValue)
}

/** Slot receiver for [AccordionTrigger] / [AccordionContent]. */
class AccordionItemScope internal constructor(
    private val controller: AccordionController,
    val value: String,
    val disabled: Boolean = false,
) {
    val open: Boolean get() = controller.isOpen(value)

    val toggle: () -> Unit get() = {
        if (!disabled) controller.toggle(value)
    }
}

/**
 * A vertically-stacked set of collapsible sections. Matches real shadcn/ui's
 * `accordion.tsx`:
 *
 * ```
 * Accordion
 * ├── AccordionItem
 * │   ├── AccordionHeader
 * │   │   └── AccordionTrigger
 * │   └── AccordionContent
 * └── AccordionItem
 *     ├── AccordionHeader
 *     │   └── AccordionTrigger
 *     └── AccordionContent
 * ```
 *
 * Each item is separated by a bottom border (shadcn `border-b`). The chevron
 * switches between down/up glyphs, and content expands/collapses with a height animation.
 *
 * Pass [value] + [onValueChange] for controlled state; omit both (or pass
 * [defaultValue]) for uncontrolled. [type] is Radix `type` (`single` /
 * `multiple`); [multiple] is the official shadcn boolean alias (`true` →
 * Multiple, `false` → Single, omit to keep [type]). [collapsible] is Radix's
 * single-mode `collapsible` (can the open item be closed). [orientation] is
 * the keyboard navigation axis (vertical default). Like shadcn, the default
 * visual layout stays vertically stacked for both orientations.
 * Arrow keys / Home / End move focus between triggers.
 *
 * Usage:
 * ```
 * var value by remember { mutableStateOf(setOf("item-1")) }
 * Accordion(type = AccordionType.Single, value = value, onValueChange = { value = it }) {
 *     AccordionItem("item-1", "Is it accessible?", "Yes. It adheres to the WAI-ARIA design pattern.")
 * }
 * ```
 */
@Composable
fun Accordion(
    type: AccordionType = AccordionType.Single,
    value: Set<String>? = null,
    onValueChange: ((Set<String>) -> Unit)? = null,
    modifier: Modifier = Modifier,
    defaultValue: Set<String> = emptySet(),
    collapsible: Boolean = true,
    orientation: AccordionOrientation = AccordionOrientation.Vertical,
    loop: Boolean = true,
    disabled: Boolean = false,
    multiple: Boolean? = null,
    style: Style = Style,
    /**
     * Default chevron for every [AccordionTrigger]. Matches Base UI: ChevronDown when
     * collapsed, ChevronUp when expanded (two glyphs, not a rotating ChevronRight).
     * Override to swap icon sets (catalog may pass Lucide). Receives [isOpen].
     */
    icon: @Composable (isOpen: Boolean) -> Unit = {
        AccordionItemIndicator()
    },
    content: @Composable AccordionScope.() -> Unit,
) {
    var internalValue by remember { mutableStateOf(defaultValue) }
    val controlled = value != null
    val current = value ?: internalValue
    val setValue: (Set<String>) -> Unit = { next ->
        if (!controlled) internalValue = next
        onValueChange?.invoke(next)
    }
    val resolvedType =
        when (multiple) {
            true -> AccordionType.Multiple
            false -> AccordionType.Single
            null -> type
        }
    val controller =
        AccordionController(
            type = resolvedType,
            value = current,
            collapsible = collapsible,
            orientation = orientation,
            loop = loop,
            disabled = disabled,
            onValueChange = setValue,
        )
    val rootStyleState = remember { MutableStyleState(MutableInteractionSource()) }
    val navState = remember { AccordionNavState() }
    CompositionLocalProvider(
        LocalAccordion provides controller,
        LocalAccordionIcon provides icon,
        LocalAccordionNav provides navState,
    ) {
        // shadcn's root remains flex-col for both keyboard orientations.
        if (currentTheme.activePreset == StylePreset.Nova) {
            Layout(
                modifier = modifier.styleable(rootStyleState, style),
                content = { AccordionScope(controller).content() },
            ) { measurables, constraints ->
                // Parent data follows Compose's current child order, including
                // keyed moves. Decide the last divider before measuring items.
                navState.measuredOrder = measurables.mapNotNull { (it.layoutId as? AccordionItemId)?.value }
                var usedHeight = 0
                val children =
                    measurables.map { measurable ->
                        val remaining =
                            if (constraints.maxHeight == Constraints.Infinity) {
                                Constraints.Infinity
                            } else {
                                (constraints.maxHeight - usedHeight).coerceAtLeast(0)
                            }
                        measurable.measure(constraints.copy(minWidth = 0, minHeight = 0, maxHeight = remaining)).also {
                            usedHeight += it.height
                        }
                    }
                val width = constraints.constrainWidth(children.maxOfOrNull { it.width } ?: 0)
                val height = constraints.constrainHeight(usedHeight)
                layout(width, height) {
                    var y = 0
                    children.forEach { child ->
                        child.placeRelative(0, y)
                        y += child.height
                    }
                }
            }
        } else {
            Column(modifier = modifier.styleable(rootStyleState, style)) {
                AccordionScope(controller).content()
            }
        }
    }
}

/**
 * Single-type [Accordion] using official string [value] / [onValueChange]
 * (`""` means all collapsed). Delegates to the Set-based root.
 */
@Composable
fun Accordion(
    value: String,
    onValueChange: (String) -> Unit,
    type: AccordionType = AccordionType.Single,
    modifier: Modifier = Modifier,
    collapsible: Boolean = true,
    orientation: AccordionOrientation = AccordionOrientation.Vertical,
    loop: Boolean = true,
    disabled: Boolean = false,
    style: Style = Style,
    icon: @Composable (isOpen: Boolean) -> Unit = {
        AccordionItemIndicator()
    },
    content: @Composable AccordionScope.() -> Unit,
) {
    Accordion(
        type = type,
        value = if (value.isEmpty()) emptySet() else setOf(value),
        onValueChange = { next -> onValueChange(next.firstOrNull().orEmpty()) },
        modifier = modifier,
        collapsible = collapsible,
        orientation = orientation,
        loop = loop,
        disabled = disabled,
        style = style,
        icon = icon,
        content = content,
    )
}

/**
 * Uncontrolled single-type [Accordion] using official string [defaultValue]
 * (`""` means all collapsed). Delegates to the Set-based root.
 */
@Composable
fun Accordion(
    defaultValue: String,
    type: AccordionType = AccordionType.Single,
    modifier: Modifier = Modifier,
    collapsible: Boolean = true,
    orientation: AccordionOrientation = AccordionOrientation.Vertical,
    loop: Boolean = true,
    disabled: Boolean = false,
    style: Style = Style,
    icon: @Composable (isOpen: Boolean) -> Unit = {
        AccordionItemIndicator()
    },
    content: @Composable AccordionScope.() -> Unit,
) {
    Accordion(
        type = type,
        defaultValue = if (defaultValue.isEmpty()) emptySet() else setOf(defaultValue),
        modifier = modifier,
        collapsible = collapsible,
        orientation = orientation,
        loop = loop,
        disabled = disabled,
        style = style,
        icon = icon,
        content = content,
    )
}

/** One section in a [Accordion]. [value] is the item id (Radix `value`). */
@Composable
fun AccordionScope.AccordionItem(
    value: String,
    modifier: Modifier = Modifier,
    disabled: Boolean = false,
    style: Style = Style,
    content: @Composable AccordionItemScope.() -> Unit,
) {
    val controller = accordionController()
    val nav = LocalAccordionNav.current
    val styleState = remember { MutableStyleState(MutableInteractionSource()) }
    val resolvedDisabled = disabled || controller.disabled
    val item = AccordionItemScope(controller, value, resolvedDisabled)
    DisposableEffect(value) {
        if (nav != null && value !in nav.itemOrder) nav.itemOrder.add(value)
        onDispose { nav?.unregister(value) }
    }
    SideEffect {
        nav?.itemEnabled?.set(value, !resolvedDisabled)
    }
    val itemContent = @Composable {
        CompositionLocalProvider(LocalAccordionItem provides item) {
            item.content()
        }
    }
    Column(
        modifier =
            modifier.layoutId(AccordionItemId(value)).onGloballyPositioned {
                nav?.positions?.set(value, it.positionInParent())
            }.styleable(styleState, style),
    ) {
        itemContent()
        val isNova = currentTheme.activePreset == StylePreset.Nova
        Separator(
            Modifier.layout { measurable, constraints ->
                val last = isNova && nav?.measuredOrder?.lastOrNull() == value
                val placeable =
                    measurable.measure(
                        if (last) constraints.copy(minHeight = 0, maxHeight = 0) else constraints,
                    )
                layout(placeable.width, placeable.height) { if (!last) placeable.place(0, 0) }
            },
        )
    }
}

/**
 * Convenience: [title] as [AccordionTrigger] plus [content] as [AccordionContent].
 * Matches a typical shadcn FAQ item without spelling out the inner slots.
 */
@Composable
fun AccordionScope.AccordionItem(
    value: String,
    title: String,
    modifier: Modifier = Modifier,
    disabled: Boolean = false,
    style: Style = Style,
    content: @Composable ColumnScope.() -> Unit,
) {
    AccordionItem(value = value, modifier = modifier, disabled = disabled, style = style) {
        AccordionTrigger(title)
        AccordionContent(content = content)
    }
}

/** Convenience: [title] + string body instead of slots. */
@Composable
fun AccordionScope.AccordionItem(
    value: String,
    title: String,
    content: String,
    modifier: Modifier = Modifier,
    disabled: Boolean = false,
    style: Style = Style,
) {
    AccordionItem(value = value, title = title, modifier = modifier, disabled = disabled, style = style) {
        Text(
            style =
                if (currentTheme.activePreset == StylePreset.Nova) {
                    Style {
                        fontSize(14.sp)
                        lineHeight(20.sp)
                    }
                } else {
                    Style
                },
            textStyle =
                if (currentTheme.activePreset == StylePreset.Nova) {
                    TextStyle(
                        color = currentTheme.colors.foreground,
                        fontSize = 14.sp,
                        lineHeight = 20.sp,
                        fontWeight = FontWeight.Normal,
                    )
                } else {
                    TextStyle.Default
                },
        ) { content }
    }
}

/**
 * Optional wrapper around [AccordionTrigger]. Base UI does not export
 * `AccordionHeader` (Header is composed inside Trigger).
 * Layout-passthrough so existing [AccordionItem] screenshots stay unchanged.
 */
@Composable
internal fun AccordionItemScope.AccordionHeader(content: @Composable AccordionItemScope.() -> Unit) {
    content()
}

/**
 * Toggles the parent [AccordionItem]. Matches real shadcn's `AccordionTrigger`.
 * Put the title (or any chrome) in [content]; the chevron is appended.
 */
@Composable
fun AccordionItemScope.AccordionTrigger(
    modifier: Modifier = Modifier,
    style: Style = Style,
    disabled: Boolean = false,
    icon: (@Composable (isOpen: Boolean) -> Unit)? = null,
    content: @Composable () -> Unit,
) {
    val resolvedDisabled = this.disabled || disabled
    val interactionSource = remember { MutableInteractionSource() }
    val styleState = rememberUpdatedStyleState(interactionSource)
    val hovered by interactionSource.collectIsHoveredAsState()
    val focused = interactionSource.collectIsFocusedAsState()
    val theme = currentTheme
    val inputMode = LocalInputModeManager.current
    val focusModifier =
        if (theme.activePreset == StylePreset.Nova) {
            Modifier.drawWithContent {
                drawContent()
                if (focused.value && !resolvedDisabled && inputMode.inputMode == InputMode.Keyboard) {
                    val spread = 3.dp.toPx()
                    val radius = theme.baseRadius.value.toPx() + spread / 2
                    drawRoundRect(
                        color = theme.colors.ring,
                        topLeft = Offset(0.5.dp.toPx(), 0.5.dp.toPx()),
                        size =
                            Size(
                                (size.width - 1.dp.toPx()).coerceAtLeast(0f),
                                (size.height - 1.dp.toPx()).coerceAtLeast(0f),
                            ),
                        cornerRadius = CornerRadius(theme.baseRadius.value.toPx()),
                        style = Stroke(1.dp.toPx()),
                    )
                    drawRoundRect(
                        color = theme.colors.ring.copy(alpha = 0.5f),
                        topLeft = Offset(-spread / 2, -spread / 2),
                        size = Size(size.width + spread, size.height + spread),
                        cornerRadius = CornerRadius(radius),
                        style = Stroke(spread),
                    )
                }
            }
        } else {
            Modifier
        }
    val direction = LocalLayoutDirection.current
    val orientation = accordionController().orientation
    val pressStyle = rememberPressMotionStyle()
    val touchTargetStyle = rememberMinTouchTargetStyle()
    val resolvedIcon = icon ?: LocalAccordionIcon.current
    val nav = LocalAccordionNav.current
    val requester = nav?.requester(value)
    val loop = accordionController().loop
    Row(
        modifier =
            modifier.then(focusModifier)
                .then(if (requester != null) Modifier.focusRequester(requester) else Modifier)
                .semantics(mergeDescendants = true) {
                    role = Role.Button
                    heading()
                    stateDescription = if (open) "Expanded" else "Collapsed"
                    if (resolvedDisabled) disabled()
                }
                .hoverable(interactionSource, enabled = !resolvedDisabled)
                .clickable(
                    interactionSource = interactionSource,
                    indication = null,
                    enabled = !resolvedDisabled,
                    role = Role.Button,
                    onClick = toggle,
                )
                .onPreviewKeyEvent { event ->
                    if (event.type == KeyEventType.KeyDown) inputMode.requestInputMode(InputMode.Keyboard)
                    if (nav == null) false else handleAccordionKey(nav, value, event, loop, orientation, direction)
                }
                .then(if (resolvedDisabled) Modifier.alpha(0.5f) else Modifier)
                .styleable(
                    styleState,
                    touchTargetStyle then pressStyle then rememberAccordionTriggerStyle() then style,
                ),
        horizontalArrangement = Arrangement.spacedBy(twSpacing.sm),
        verticalAlignment = Alignment.Top,
    ) {
        Box(modifier = Modifier.weight(1f)) {
            CompositionLocalProvider(
                LocalTextDecoration provides if (hovered && !resolvedDisabled) TextDecoration.Underline else null,
            ) {
                content()
            }
        }
        resolvedIcon(open)
    }
}

/** String title overload of [AccordionTrigger]. */
@Composable
fun AccordionItemScope.AccordionTrigger(
    text: String,
    modifier: Modifier = Modifier,
    style: Style = Style,
    disabled: Boolean = false,
    icon: (@Composable (isOpen: Boolean) -> Unit)? = null,
) {
    AccordionTrigger(modifier = modifier, style = style, disabled = disabled, icon = icon) {
        Text(
            variant = TextVariant.LabelLarge,
            style =
                if (currentTheme.activePreset == StylePreset.Nova) {
                    Style {
                        fontSize(14.sp)
                        lineHeight(20.sp)
                    }
                } else {
                    Style
                },
            textStyle =
                if (currentTheme.activePreset == StylePreset.Nova) {
                    TextStyle(
                        color = currentTheme.colors.foreground,
                        fontSize = 14.sp,
                        lineHeight = 20.sp,
                        fontWeight = FontWeight.Medium,
                    )
                } else {
                    TextStyle.Default
                },
        ) { text }
    }
}

/**
 * Chevron inside [AccordionTrigger]. Base UI does not export an indicator slot —
 * AccordionTrigger paints ChevronDown / ChevronUp with CSS show/hide.
 */
@Composable
internal fun AccordionItemIndicator(
    isOpen: Boolean =
        LocalAccordionItem.current?.open ?: error(
            "AccordionItemIndicator must be used inside AccordionItem, or pass isOpen=",
        ),
    modifier: Modifier = Modifier,
) {
    val tint = currentTheme.colors.mutedForeground
    Canvas(modifier.size(16.dp)) {
        val path =
            Path().apply {
                moveTo(size.width * .25f, size.height * if (isOpen) .625f else .375f)
                lineTo(size.width * .5f, size.height * if (isOpen) .375f else .625f)
                lineTo(size.width * .75f, size.height * if (isOpen) .625f else .375f)
            }
        drawPath(path, tint, style = Stroke(width = size.width / 12f, cap = StrokeCap.Round))
    }
}

/** The expandable body of an [AccordionItem]. Matches real shadcn's `AccordionContent`. */
@Composable
fun AccordionItemScope.AccordionContent(
    modifier: Modifier = Modifier,
    style: Style = Style,
    enter: EnterTransition =
        if (currentTheme.activePreset == StylePreset.Nova) {
            expandVertically(
                tween(200, easing = CubicBezierEasing(0f, 0f, 0.58f, 1f)),
            )
        } else {
            fadeIn() + expandVertically()
        },
    exit: ExitTransition =
        if (currentTheme.activePreset == StylePreset.Nova) {
            shrinkVertically(
                tween(200, easing = CubicBezierEasing(0f, 0f, 0.58f, 1f)),
            )
        } else {
            fadeOut() + shrinkVertically()
        },
    forceMount: Boolean = false,
    keepMounted: Boolean? = null,
    content: @Composable ColumnScope.() -> Unit,
) {
    val nav = LocalAccordionNav.current
    var contentHasFocus by remember { mutableStateOf(false) }
    LaunchedEffect(open) {
        if (!open && contentHasFocus) nav?.requester(value)?.requestFocus()
    }
    val body: @Composable () -> Unit = {
        val contentStyleState = remember { MutableStyleState(MutableInteractionSource()) }
        Column(
            modifier =
                modifier.onFocusChanged { contentHasFocus = it.hasFocus }.focusGroup()
                    .styleable(contentStyleState, style)
                    .padding(bottom = if (currentTheme.activePreset == StylePreset.Nova) 10.dp else twSpacing.md),
            content = content,
        )
    }
    if (keepMounted ?: forceMount) {
        Box(
            modifier =
                if (open) {
                    Modifier
                } else {
                    Modifier.size(0.dp).clearAndSetSemantics {
                    }.focusProperties { onEnter = { cancelFocusChange() } }.focusGroup()
                },
        ) {
            body()
        }
    } else {
        AnimatedVisibility(
            visible = open,
            enter = enter,
            exit = exit,
        ) {
            body()
        }
    }
}

/** String-body overload of [AccordionContent]. */
@Composable
fun AccordionItemScope.AccordionContent(
    text: String,
    modifier: Modifier = Modifier,
    style: Style = Style,
    enter: EnterTransition =
        if (currentTheme.activePreset == StylePreset.Nova) {
            expandVertically(
                tween(200, easing = CubicBezierEasing(0f, 0f, 0.58f, 1f)),
            )
        } else {
            fadeIn() + expandVertically()
        },
    exit: ExitTransition =
        if (currentTheme.activePreset == StylePreset.Nova) {
            shrinkVertically(
                tween(200, easing = CubicBezierEasing(0f, 0f, 0.58f, 1f)),
            )
        } else {
            fadeOut() + shrinkVertically()
        },
    forceMount: Boolean = false,
    keepMounted: Boolean? = null,
) {
    AccordionContent(
        modifier = modifier,
        style = style,
        enter = enter,
        exit = exit,
        forceMount = forceMount,
        keepMounted = keepMounted,
    ) {
        Text(
            style =
                if (currentTheme.activePreset == StylePreset.Nova) {
                    Style {
                        fontSize(14.sp)
                        lineHeight(20.sp)
                    }
                } else {
                    Style
                },
            textStyle =
                if (currentTheme.activePreset == StylePreset.Nova) {
                    TextStyle(
                        color = currentTheme.colors.foreground,
                        fontSize = 14.sp,
                        lineHeight = 20.sp,
                        fontWeight = FontWeight.Normal,
                    )
                } else {
                    TextStyle.Default
                },
        ) { text }
    }
}

private fun handleAccordionKey(
    nav: AccordionNavState,
    current: String,
    event: KeyEvent,
    loop: Boolean,
    orientation: AccordionOrientation,
    direction: LayoutDirection,
): Boolean {
    if (event.type != KeyEventType.KeyDown) return false
    val enabled =
        nav.itemOrder.filter { nav.itemEnabled[it] != false }.sortedBy {
            val position = nav.positions[it] ?: Offset.Zero
            if (orientation == AccordionOrientation.Vertical) {
                position.y
            } else if (direction == LayoutDirection.Rtl) {
                -position.x
            } else {
                position.x
            }
        }
    if (enabled.isEmpty()) return false
    val idx = enabled.indexOf(current)
    if (idx < 0) return false
    val last = enabled.lastIndex
    val forward =
        if (orientation == AccordionOrientation.Vertical) {
            Key.DirectionDown
        } else if (direction == LayoutDirection.Rtl) {
            Key.DirectionLeft
        } else {
            Key.DirectionRight
        }
    val backward =
        if (orientation == AccordionOrientation.Vertical) {
            Key.DirectionUp
        } else if (direction == LayoutDirection.Rtl) {
            Key.DirectionRight
        } else {
            Key.DirectionLeft
        }
    val next =
        when (event.key) {
            forward ->
                when {
                    idx < last -> enabled[idx + 1]
                    loop -> enabled[0]
                    else -> return false
                }
            backward ->
                when {
                    idx > 0 -> enabled[idx - 1]
                    loop -> enabled[last]
                    else -> return false
                }
            Key.MoveHome -> enabled.first()
            Key.MoveEnd -> enabled[last]
            else -> return false
        }
    nav.requester(next).requestFocus()
    return true
}
