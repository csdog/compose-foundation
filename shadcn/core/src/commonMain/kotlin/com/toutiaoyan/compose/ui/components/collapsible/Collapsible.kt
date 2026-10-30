@file:OptIn(ExperimentalFoundationStyleApi::class)

package com.toutiaoyan.compose.ui.components.collapsible

import androidx.compose.ui.semantics.clearAndSetSemantics
import androidx.compose.ui.focus.focusProperties
import androidx.compose.animation.core.tween
import com.toutiaoyan.compose.ui.theme.currentTheme
import com.toutiaoyan.compose.ui.tokens.StylePreset
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.EnterTransition
import androidx.compose.animation.ExitTransition
import androidx.compose.animation.expandVertically
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.shrinkVertically
import androidx.compose.foundation.clickable
import androidx.compose.foundation.focusGroup
import com.toutiaoyan.compose.ui.components.field.LocalFieldSetDisabled
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ColumnScope
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
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.focus.focusRequester
import androidx.compose.ui.focus.onFocusChanged
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.runtime.staticCompositionLocalOf
import androidx.compose.ui.unit.dp
import androidx.compose.ui.Modifier
import androidx.compose.ui.Alignment
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.semantics.role
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.semantics.stateDescription
import com.toutiaoyan.compose.ui.components.button.Button
import com.toutiaoyan.compose.ui.components.button.ButtonSize
import com.toutiaoyan.compose.ui.components.button.ButtonVariant
import com.toutiaoyan.compose.ui.components.text.Text

internal class CollapsibleFocus {
    var returnTo: FocusRequester? = null
}

internal class CollapsibleController(
    val open: Boolean,
    val enabled: Boolean,
    val onOpenChange: (Boolean) -> Unit,
    val focus: CollapsibleFocus,
) {
    fun toggle() {
        if (enabled) onOpenChange(!open)
    }
}

private val LocalCollapsible = staticCompositionLocalOf<CollapsibleController?> { null }

@Composable
private fun collapsibleController(): CollapsibleController =
    checkNotNull(LocalCollapsible.current) {
        "CollapsibleTrigger / CollapsibleContent must be used inside Collapsible { … }"
    }

@Composable
private fun Modifier.collapsibleTriggerFocus(focus: CollapsibleFocus): Modifier {
    val requester = remember { FocusRequester() }
    return focusRequester(requester).onFocusChanged { if (it.isFocused) focus.returnTo = requester }
}

/** Reads the nearest [Collapsible] scope. Matches Radix `useCollapsible`. */
@Composable
fun collapsibleState(): CollapsibleScope = CollapsibleScope(collapsibleController())

/**
 * Slot receiver for [CollapsibleTrigger] / [CollapsibleContent].
 *
 * [open] / [toggle] match the state Radix exposes on the trigger
 * (`data-state="open"`), so a chevron inside the trigger can rotate without
 * the caller threading state by hand.
 */
class CollapsibleScope internal constructor(
    private val controller: CollapsibleController,
) {
    val open: Boolean get() = controller.open

    val toggle: () -> Unit get() = controller::toggle
}

/**
 * An expand/collapse primitive matching real shadcn/ui's `collapsible.tsx`:
 *
 * ```
 * Collapsible
 * ├── CollapsibleTrigger
 * └── CollapsibleContent
 * ```
 *
 * Bare state/animation — no chrome of its own (real shadcn's version has none
 * either; visuals come from what you put in the trigger / content). Compose
 * [CollapsibleTrigger] and [CollapsibleContent] in any order, with optional
 * always-visible siblings in between — unlike a single trigger+content lambda,
 * this is what lets the classic "@peduarte starred 3 repositories" layout keep
 * the first row visible while extra rows live in [CollapsibleContent].
 *
 * Pass [open] + [onOpenChange] for controlled state; omit both (or pass
 * [defaultOpen]) for uncontrolled, matching Radix `open` / `defaultOpen` /
 * `onOpenChange`.
 *
 * Nova content has no intrinsic top gap; callers own their layout spacing.
 * Other presets retain the historical content gap.
 *
 * Usage:
 * ```
 * var open by remember { mutableStateOf(false) }
 * Collapsible(open = open, onOpenChange = { open = it }) {
 *     CollapsibleTrigger(if (open) "Hide" else "Show", variant = ButtonVariant.Outline)
 *     CollapsibleContent("Hidden content")
 * }
 * ```
 */
@Composable
fun Collapsible(
    open: Boolean? = null,
    onOpenChange: ((Boolean) -> Unit)? = null,
    modifier: Modifier = Modifier,
    defaultOpen: Boolean = false,
    enabled: Boolean = true,
    disabled: Boolean = false,
    style: Style = Style,
    content: @Composable CollapsibleScope.() -> Unit,
) {
    var internalOpen by remember { mutableStateOf(defaultOpen) }
    val controlled = open != null
    val isOpen = open ?: internalOpen
    val setOpen: (Boolean) -> Unit = { next ->
        if (!controlled) internalOpen = next
        onOpenChange?.invoke(next)
    }
    val focus = remember { CollapsibleFocus() }
    val controller =
        CollapsibleController(
            open = isOpen,
            enabled = enabled && !disabled && !LocalFieldSetDisabled.current,
            onOpenChange = setOpen,
            focus = focus,
        )
    val rootStyleState = remember { MutableStyleState(MutableInteractionSource()) }
    CompositionLocalProvider(LocalCollapsible provides controller) {
        Column(modifier = modifier.styleable(rootStyleState, style)) {
            CollapsibleScope(controller).content()
        }
    }
}

/**
 * Convenience: [title] as [CollapsibleTrigger] plus [content] as [CollapsibleContent].
 * Matches a typical shadcn collapsible without spelling out the inner slots.
 */
@Composable
fun Collapsible(
    title: String,
    open: Boolean? = null,
    onOpenChange: ((Boolean) -> Unit)? = null,
    modifier: Modifier = Modifier,
    defaultOpen: Boolean = false,
    enabled: Boolean = true,
    disabled: Boolean = false,
    style: Style = Style,
    content: @Composable ColumnScope.() -> Unit,
) {
    Collapsible(
        open = open,
        onOpenChange = onOpenChange,
        modifier = modifier,
        defaultOpen = defaultOpen,
        enabled = enabled,
        disabled = disabled,
        style = style,
    ) {
        CollapsibleTrigger(title)
        CollapsibleContent(content = content)
    }
}

/** Convenience: [title] + string body instead of slots. */
@Composable
fun Collapsible(
    title: String,
    content: String,
    open: Boolean? = null,
    onOpenChange: ((Boolean) -> Unit)? = null,
    modifier: Modifier = Modifier,
    defaultOpen: Boolean = false,
    enabled: Boolean = true,
    disabled: Boolean = false,
    style: Style = Style,
) {
    Collapsible(
        title = title,
        open = open,
        onOpenChange = onOpenChange,
        modifier = modifier,
        defaultOpen = defaultOpen,
        enabled = enabled,
        disabled = disabled,
        style = style,
    ) {
        Text { content }
    }
}

/**
 * Toggles the parent [Collapsible]. Matches real shadcn's `CollapsibleTrigger`
 * (unstyled button primitive). Put any chrome inside — a [com.toutiaoyan.compose.ui.components.button.Button],
 * a row of label + chevron, etc.
 *
 * When the child is already clickable (e.g. a Button wired to [CollapsibleScope.toggle]),
 * that inner click wins; this wrapper still covers the rest of the hit target and
 * supplies button semantics.
 */
@Composable
fun CollapsibleScope.CollapsibleTrigger(
    modifier: Modifier = Modifier,
    enabled: Boolean = true,
    disabled: Boolean = false,
    style: Style = Style,
    content: @Composable () -> Unit,
) {
    val controller = collapsibleController()
    val interactionSource = remember { MutableInteractionSource() }
    val triggerEnabled = enabled && !disabled && controller.enabled
    val styleState = rememberUpdatedStyleState(interactionSource) { it.isEnabled = triggerEnabled }
    Box(
        modifier =
            modifier.collapsibleTriggerFocus(controller.focus)
                .semantics {
                    role = Role.Button
                    stateDescription = if (controller.open) "Expanded" else "Collapsed"
                }
                .clickable(
                    interactionSource = interactionSource,
                    enabled = triggerEnabled,
                    role = Role.Button,
                    onClick = controller::toggle,
                )
                .styleable(styleState, style),
        contentAlignment = if (currentTheme.activePreset == StylePreset.Nova) Alignment.Center else Alignment.TopStart,
    ) {
        content()
    }
}

/** String-label overload of [CollapsibleTrigger], rendering a [Button] wired to [toggle]. */
@Composable
fun CollapsibleScope.CollapsibleTrigger(
    text: String,
    modifier: Modifier = Modifier,
    enabled: Boolean = true,
    disabled: Boolean = false,
    variant: ButtonVariant = ButtonVariant.Default,
    size: ButtonSize = ButtonSize.Md,
    style: Style = Style,
) {
    if (currentTheme.activePreset == StylePreset.Nova) {
        val ctrl = collapsibleController()
        Button(
            text,
            onClick = toggle,
            enabled = enabled && ctrl.enabled,
            disabled = disabled,
            variant = variant,
            size = size,
            modifier =
                modifier.collapsibleTriggerFocus(ctrl.focus).semantics {
                    stateDescription = if (ctrl.open) "Expanded" else "Collapsed"
                },
            style = style,
        )
    } else {
        CollapsibleTrigger(modifier = modifier, enabled = enabled, disabled = disabled, style = style) {
            Button(
                text,
                onClick = toggle,
                enabled = enabled && collapsibleController().enabled,
                disabled = disabled,
                variant = variant,
                size = size,
            )
        }
    }
}

/**
 * Optional wrapper around [CollapsibleTrigger]. Base UI does not export
 * `CollapsibleHeader`. Layout-passthrough so existing screenshots stay unchanged.
 */
@Composable
internal fun CollapsibleScope.CollapsibleHeader(content: @Composable CollapsibleScope.() -> Unit) {
    content()
}

/**
 * The show/hide body of a [Collapsible]. Matches real shadcn's
 * `CollapsibleContent` (height animation + overflow clip). Override [enter] /
 * [exit] to drop the default fade, or [style] to drop the baked-in top gap.
 */
@Composable
fun CollapsibleScope.CollapsibleContent(
    modifier: Modifier = Modifier,
    style: Style = Style,
    enter: EnterTransition =
        if (currentTheme.activePreset == StylePreset.Nova) {
            expandVertically(
                tween(200),
            )
        } else {
            fadeIn() + expandVertically()
        },
    exit: ExitTransition =
        if (currentTheme.activePreset == StylePreset.Nova) {
            shrinkVertically(
                tween(200),
            )
        } else {
            fadeOut() + shrinkVertically()
        },
    forceMount: Boolean = false,
    keepMounted: Boolean? = null,
    content: @Composable ColumnScope.() -> Unit,
) {
    val controller = collapsibleController()
    var contentHasFocus by remember { mutableStateOf(false) }
    LaunchedEffect(controller.open) {
        if (!controller.open && contentHasFocus) controller.focus.returnTo?.requestFocus()
    }
    val contentStyleState = remember { MutableStyleState(MutableInteractionSource()) }
    // The gap lives *inside* AnimatedVisibility, not as Arrangement.spacedBy on
    // the root Column: spacedBy would reserve its gap unconditionally based on
    // child count, leaving a dangling empty gap below the trigger even fully
    // collapsed (AnimatedVisibility measures 0x0 once its exit animation
    // settles, but it's still "a child" as far as spacedBy is concerned).
    // Padding inside the animating subtree collapses away together with the
    // content instead.
    val body: @Composable () -> Unit = {
        Column(
            modifier =
                modifier.onFocusChanged { contentHasFocus = it.hasFocus }.focusGroup().styleable(
                    contentStyleState,
                    rememberCollapsibleContentStyle() then style,
                ),
            content = content,
        )
    }
    if (keepMounted ?: forceMount) {
        Box(
            modifier =
                if (controller.open) {
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
            visible = controller.open,
            enter = enter,
            exit = exit,
        ) {
            body()
        }
    }
}

/** String-body overload of [CollapsibleContent]. */
@Composable
fun CollapsibleScope.CollapsibleContent(
    text: String,
    modifier: Modifier = Modifier,
    style: Style = Style,
    enter: EnterTransition =
        if (currentTheme.activePreset == StylePreset.Nova) {
            expandVertically(
                tween(200),
            )
        } else {
            fadeIn() + expandVertically()
        },
    exit: ExitTransition =
        if (currentTheme.activePreset == StylePreset.Nova) {
            shrinkVertically(
                tween(200),
            )
        } else {
            fadeOut() + shrinkVertically()
        },
    forceMount: Boolean = false,
    keepMounted: Boolean? = null,
) {
    CollapsibleContent(
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
