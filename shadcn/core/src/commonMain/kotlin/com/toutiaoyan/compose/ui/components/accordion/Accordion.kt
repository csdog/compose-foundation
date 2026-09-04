@file:OptIn(ExperimentalFoundationStyleApi::class)

package com.toutiaoyan.compose.ui.components.accordion

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.EnterTransition
import androidx.compose.animation.ExitTransition
import androidx.compose.animation.expandVertically
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.shrinkVertically
import androidx.compose.foundation.clickable
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
import com.toutiaoyan.compose.tailwind.core.twSpacing
import com.toutiaoyan.compose.ui.components.icon.Icon
import com.toutiaoyan.compose.ui.components.icon.IconVariant
import com.toutiaoyan.compose.ui.components.separator.Separator
import com.toutiaoyan.compose.ui.components.separator.SeparatorOrientation
import com.toutiaoyan.compose.ui.components.text.Text
import com.toutiaoyan.compose.ui.components.text.TextVariant
import com.toutiaoyan.compose.ui.icons.ChevronDown
import com.toutiaoyan.compose.ui.icons.ChevronUp
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

private class AccordionNavState {
    val itemOrder = mutableStateListOf<String>()
    val itemEnabled = mutableStateMapOf<String, Boolean>()
    private val focusRequesters = mutableMapOf<String, FocusRequester>()

    fun requester(value: String): FocusRequester = focusRequesters.getOrPut(value) { FocusRequester() }

    fun unregister(value: String) {
        itemOrder.remove(value)
        itemEnabled.remove(value)
        focusRequesters.remove(value)
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
 * rotates when open, and content expands/collapses with a height animation.
 *
 * Pass [value] + [onValueChange] for controlled state; omit both (or pass
 * [defaultValue]) for uncontrolled. [type] is Radix `type` (`single` /
 * `multiple`); [multiple] is the official shadcn boolean alias (`true` →
 * Multiple, `false` → Single, omit to keep [type]). [collapsible] is Radix's
 * single-mode `collapsible` (can the open item be closed). [orientation] is
 * Radix `orientation` (vertical default).
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
        if (orientation == AccordionOrientation.Horizontal) {
            Row(modifier = modifier.styleable(rootStyleState, style)) {
                AccordionScope(controller).content()
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
    if (controller.orientation == AccordionOrientation.Horizontal) {
        Row(modifier = modifier.styleable(styleState, style)) {
            Column { itemContent() }
            Separator(orientation = SeparatorOrientation.Vertical)
        }
    } else {
        Column(modifier = modifier.styleable(styleState, style)) {
            itemContent()
            Separator()
        }
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
        Text { content }
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
    val pressStyle = rememberPressMotionStyle()
    val touchTargetStyle = rememberMinTouchTargetStyle()
    val resolvedIcon = icon ?: LocalAccordionIcon.current
    val nav = LocalAccordionNav.current
    val requester = nav?.requester(value)
    val loop = accordionController().loop
    Row(
        modifier =
            modifier
                .then(if (requester != null) Modifier.focusRequester(requester) else Modifier)
                .semantics(mergeDescendants = true) {
                    role = Role.Button
                    heading()
                    stateDescription = if (open) "Expanded" else "Collapsed"
                    if (resolvedDisabled) disabled()
                }
                .clickable(
                    interactionSource = interactionSource,
                    indication = null,
                    enabled = !resolvedDisabled,
                    role = Role.Button,
                    onClick = toggle,
                )
                .onPreviewKeyEvent { event ->
                    if (nav == null) false else handleAccordionKey(nav, value, event, loop)
                }
                .then(if (resolvedDisabled) Modifier.alpha(0.5f) else Modifier)
                .styleable(
                    styleState,
                    touchTargetStyle then pressStyle then rememberAccordionTriggerStyle() then style,
                ),
        horizontalArrangement = Arrangement.spacedBy(twSpacing.sm),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        Box(modifier = Modifier.weight(1f)) {
            content()
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
        Text(variant = TextVariant.LabelLarge) { text }
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
    // Stack both glyphs like Base `group-aria-expanded:hidden` / `:inline`.
    Box(modifier = modifier, contentAlignment = Alignment.Center) {
        Icon(
            ChevronDown,
            tint = tint,
            modifier = Modifier.alpha(if (isOpen) 0f else 1f),
            variant = IconVariant.Small,
        )
        Icon(
            ChevronUp,
            tint = tint,
            modifier = Modifier.alpha(if (isOpen) 1f else 0f),
            variant = IconVariant.Small,
        )
    }
}

/** The expandable body of an [AccordionItem]. Matches real shadcn's `AccordionContent`. */
@Composable
fun AccordionItemScope.AccordionContent(
    modifier: Modifier = Modifier,
    style: Style = Style,
    enter: EnterTransition = fadeIn() + expandVertically(),
    exit: ExitTransition = fadeOut() + shrinkVertically(),
    forceMount: Boolean = false,
    keepMounted: Boolean? = null,
    content: @Composable ColumnScope.() -> Unit,
) {
    val body: @Composable () -> Unit = {
        val contentStyleState = remember { MutableStyleState(MutableInteractionSource()) }
        Column(
            modifier =
                modifier
                    .styleable(contentStyleState, style)
                    .padding(bottom = twSpacing.md),
            content = content,
        )
    }
    if (keepMounted ?: forceMount) {
        Box(modifier = if (open) Modifier else Modifier.size(0.dp)) {
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
    enter: EnterTransition = fadeIn() + expandVertically(),
    exit: ExitTransition = fadeOut() + shrinkVertically(),
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
        Text { text }
    }
}

private fun handleAccordionKey(
    nav: AccordionNavState,
    current: String,
    event: KeyEvent,
    loop: Boolean,
): Boolean {
    if (event.type != KeyEventType.KeyDown) return false
    val enabled = nav.itemOrder.filter { nav.itemEnabled[it] != false }
    if (enabled.isEmpty()) return false
    val idx = enabled.indexOf(current)
    if (idx < 0) return false
    val last = enabled.lastIndex
    val next =
        when (event.key) {
            Key.DirectionDown, Key.DirectionRight ->
                when {
                    idx < last -> enabled[idx + 1]
                    loop -> enabled[0]
                    else -> return false
                }
            Key.DirectionUp, Key.DirectionLeft ->
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
