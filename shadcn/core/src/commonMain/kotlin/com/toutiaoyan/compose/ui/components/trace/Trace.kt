@file:OptIn(androidx.compose.foundation.style.ExperimentalFoundationStyleApi::class)

package com.toutiaoyan.compose.ui.components.trace

import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.animation.expandVertically
import androidx.compose.animation.shrinkVertically
import androidx.compose.foundation.clickable
import androidx.compose.foundation.hoverable
import androidx.compose.foundation.interaction.FocusInteraction
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.interaction.collectIsHoveredAsState
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.style.focused
import androidx.compose.foundation.style.Style
import androidx.compose.foundation.style.contentPadding
import androidx.compose.foundation.style.rememberUpdatedStyleState
import androidx.compose.foundation.style.styleable
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.drawBehind
import androidx.compose.ui.focus.focusRequester
import androidx.compose.ui.focus.onFocusChanged
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.input.key.Key
import androidx.compose.ui.input.key.KeyEventType
import androidx.compose.ui.input.key.key
import androidx.compose.ui.input.key.onPreviewKeyEvent
import androidx.compose.ui.input.key.type
import androidx.compose.ui.input.pointer.PointerIcon
import androidx.compose.ui.input.pointer.pointerHoverIcon
import androidx.compose.ui.platform.LocalLayoutDirection
import androidx.compose.ui.platform.LocalUriHandler
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.semantics.clearAndSetSemantics
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.semantics.role
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.LayoutDirection
import androidx.compose.ui.unit.dp
import com.toutiaoyan.compose.tailwind.core.twRadius
import com.toutiaoyan.compose.ui.components.collapsible.*
import com.toutiaoyan.compose.ui.components.icon.Icon
import com.toutiaoyan.compose.ui.components.icon.LocalInlineIconSize
import com.toutiaoyan.compose.ui.components.text.Text
import com.toutiaoyan.compose.ui.icons.ChevronRight
import com.toutiaoyan.compose.ui.styles.focusRingShadow
import com.toutiaoyan.compose.ui.styles.shimmer
import com.toutiaoyan.compose.ui.theme.LocalContentColor
import com.toutiaoyan.compose.ui.theme.currentTheme

sealed interface TraceStatus {
    data object Running : TraceStatus

    data object Error : TraceStatus

    data object Pending : TraceStatus
}

class TraceLineScope internal constructor(internal val row: RowScope, internal val status: TraceStatus?) {
    /**
     * Tracks that a [TraceTitle] was emitted earlier in this line so a following
     * [TraceIcon] can use real shadcn's `[[data-slot=trace-title]~&]:-ml-1` 4dp
     * trailing-icon gap instead of the line's 8dp gap.
     */
    internal var titleSeen: Boolean = false
}

@Composable
fun Trace(
    modifier: Modifier = Modifier,
    content: @Composable ColumnScope.() -> Unit,
) {
    CompositionLocalProvider(LocalContentColor provides currentTheme.colors.mutedForeground) {
        Column(modifier.fillMaxWidth(), verticalArrangement = Arrangement.spacedBy(8.dp), content = content)
    }
}

@Composable
fun TraceLine(
    modifier: Modifier = Modifier,
    status: TraceStatus? = null,
    content: @Composable TraceLineScope.() -> Unit,
) {
    val theme = currentTheme
    CompositionLocalProvider(
        LocalContentColor provides
            if (status == TraceStatus.Error) {
                theme.colors.destructive
            } else {
                LocalContentColor.current.let {
                    if (it == androidx.compose.ui.graphics.Color.Unspecified) theme.colors.mutedForeground else it
                }
            },
    ) {
        Row(
            modifier.alpha(if (status == TraceStatus.Pending) 0.6f else 1f),
            horizontalArrangement = Arrangement.spacedBy(8.dp),
            verticalAlignment = Alignment.CenterVertically,
        ) {
            TraceLineScope(this, status).content()
        }
    }
}

@Composable
private fun TraceIconSlot(
    modifier: Modifier = Modifier,
    content: @Composable () -> Unit,
) {
    CompositionLocalProvider(LocalInlineIconSize provides 16.dp) {
        Box(modifier.size(16.dp).clearAndSetSemantics {}, contentAlignment = Alignment.Center) { content() }
    }
}

@Composable
fun TraceIcon(
    modifier: Modifier = Modifier,
    content: @Composable () -> Unit,
) {
    TraceIconSlot(modifier, content)
}

/**
 * A [TraceIcon] inside a line. Real shadcn pulls any icon that follows a
 * `TraceTitle` 4dp to the left (`[[data-slot=trace-title]~&]:-ml-1`), so the
 * trailing indicator sits 4dp after the title while leading icons keep 8dp.
 */
@Composable
fun TraceLineScope.TraceIcon(
    modifier: Modifier = Modifier,
    content: @Composable () -> Unit,
) {
    val trailingGap = if (titleSeen) Modifier.offset(x = (-4).dp) else Modifier
    TraceIconSlot(modifier.then(trailingGap), content)
}

@Composable
fun TraceLineScope.TraceTitle(
    text: String,
    modifier: Modifier = Modifier,
) {
    titleSeen = true
    val weighted = with(row) { modifier.weight(1f, fill = false) }
    Text(
        modifier = weighted.shimmer(enabled = status == TraceStatus.Running),
        textStyle = currentTheme.typography.bodyMedium,
        maxLines = 1,
        overflow = TextOverflow.Ellipsis,
    ) {
        text
    }
}

/**
 * Secondary text after the title. [mono] matches real shadcn's per-call
 * `className="font-mono"` for diff stats and counts; it is a Kotlin extension.
 */
@Composable
fun TraceDescription(
    text: String,
    modifier: Modifier = Modifier,
    mono: Boolean = false,
) {
    val theme = currentTheme
    val color = theme.colors.mutedForeground
    Text(
        modifier = modifier,
        textStyle =
            theme.typography.bodyMedium.copy(
                color = color.copy(alpha = color.alpha * 0.7f),
                fontFeatureSettings = "tnum",
                fontFamily = if (mono) FontFamily.Monospace else theme.typography.bodyMedium.fontFamily,
            ),
        maxLines = 1,
    ) {
        text
    }
}

@Composable
fun TraceCollapsible(
    modifier: Modifier = Modifier,
    open: Boolean? = null,
    onOpenChange: ((Boolean) -> Unit)? = null,
    defaultOpen: Boolean = false,
    enabled: Boolean = true,
    content: @Composable CollapsibleScope.() -> Unit,
) {
    Collapsible(
        modifier = modifier.fillMaxWidth(),
        open = open,
        onOpenChange = onOpenChange,
        defaultOpen = defaultOpen,
        enabled = enabled,
        content = content,
    )
}

@Composable
fun CollapsibleScope.TraceCollapsibleTrigger(
    modifier: Modifier = Modifier,
    content: @Composable () -> Unit,
) {
    TraceCollapsibleTrigger(status = null, modifier = modifier, content = content)
}

/**
 * Explicit status for the trigger's own chevron. Unlike CSS `:has`, Compose
 * cannot infer a nested line's status; pass the same status to the trigger and line.
 * The original overload remains available for existing callers.
 */
@Composable
fun CollapsibleScope.TraceCollapsibleTrigger(
    status: TraceStatus?,
    modifier: Modifier = Modifier,
    content: @Composable () -> Unit,
) {
    val theme = currentTheme
    val radius = twRadius.md
    val source = remember { MutableInteractionSource() }
    val hovered by source.collectIsHoveredAsState()
    val rotation = animateFloatAsState(if (open) 90f else 0f, tween(200), label = "trace-chevron")
    CollapsibleTrigger(
        modifier = modifier.hoverable(source),
        style =
            Style {
                shape(RoundedCornerShape(radius))
                focused { dropShadow(theme.focusRingShadow()) }
            },
    ) {
        CompositionLocalProvider(
            LocalContentColor provides
                when {
                    status == TraceStatus.Error -> theme.colors.destructive
                    hovered -> theme.colors.foreground
                    else -> theme.colors.mutedForeground
                },
        ) {
            Row(horizontalArrangement = Arrangement.spacedBy(4.dp), verticalAlignment = Alignment.CenterVertically) {
                Box(Modifier.weight(1f, fill = false)) { content() }
                TraceIcon(Modifier.graphicsLayer { rotationZ = rotation.value }) { Icon(ChevronRight) }
            }
        }
    }
}

@Composable
fun CollapsibleScope.TraceCollapsibleContent(
    modifier: Modifier = Modifier,
    keepMounted: Boolean = false,
    content: @Composable ColumnScope.() -> Unit,
) {
    val theme = currentTheme
    val rtl = LocalLayoutDirection.current == LayoutDirection.Rtl
    CollapsibleContent(
        style = Style { contentPadding(0.dp) },
        enter = expandVertically(tween(200)),
        exit = shrinkVertically(tween(200)),
        keepMounted = keepMounted,
    ) {
        CompositionLocalProvider(LocalContentColor provides theme.colors.mutedForeground) {
            Column(
                modifier.padding(start = 7.dp, top = 8.dp, bottom = 4.dp).drawBehind {
                    val x = if (rtl) size.width - 0.5.dp.toPx() else 0.5.dp.toPx()
                    drawLine(theme.colors.border, Offset(x, 0f), Offset(x, size.height), 1.dp.toPx())
                }.padding(start = 16.dp, end = 4.dp),
                content = content,
            )
        }
    }
}

/**
 * Kotlin extension: the anchor that wraps a [TraceLine] in real shadcn has no
 * Compose equivalent, so [TraceLink] owns the same chrome: rounded focus ring,
 * hover text color, pointer cursor and keyboard activation. Web anchors become
 * a real link, so it also takes a [contentDescription] for the sr-only
 * "(opens in a new tab)" suffix.
 */
@Composable
fun TraceLink(
    href: String,
    modifier: Modifier = Modifier,
    contentDescription: String? = null,
    content: @Composable () -> Unit,
) {
    val theme = currentTheme
    val uri = LocalUriHandler.current
    val radius = twRadius.md
    val source = remember { MutableInteractionSource() }
    val hovered by source.collectIsHoveredAsState()
    val focusInteraction = remember { FocusInteraction.Focus() }
    val styleState = rememberUpdatedStyleState(source)
    val focusRequester = remember { androidx.compose.ui.focus.FocusRequester() }
    val open: () -> Unit = { uri.openUri(href) }
    Box(
        modifier
            .clip(RoundedCornerShape(radius))
            .hoverable(source)
            .pointerHoverIcon(PointerIcon.Hand)
            .focusRequester(focusRequester)
            .onFocusChanged {
                source.tryEmit(
                    if (it.hasFocus) focusInteraction else FocusInteraction.Unfocus(focusInteraction),
                )
            }
            .onPreviewKeyEvent { event ->
                if (event.type == KeyEventType.KeyDown && (event.key == Key.Enter || event.key == Key.Spacebar)) {
                    open()
                    true
                } else {
                    false
                }
            }
            .clickable(
                interactionSource = source,
                indication = null,
                role = Role.Button,
                onClick = open,
            )
            .styleable(
                styleState,
                Style {
                    shape(RoundedCornerShape(radius))
                    focused { dropShadow(theme.focusRingShadow()) }
                },
            )
            .then(
                if (contentDescription == null) {
                    Modifier
                } else {
                    Modifier.semantics {
                        this.contentDescription = contentDescription
                        role = Role.Button
                    }
                },
            ),
    ) {
        CompositionLocalProvider(
            LocalContentColor provides if (hovered) theme.colors.foreground else theme.colors.mutedForeground,
        ) {
            content()
        }
    }
}
