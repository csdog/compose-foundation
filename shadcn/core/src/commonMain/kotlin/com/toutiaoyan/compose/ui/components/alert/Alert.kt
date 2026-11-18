@file:OptIn(ExperimentalFoundationStyleApi::class, ExperimentalTypeInference::class)

package com.toutiaoyan.compose.ui.components.alert

import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.style.ExperimentalFoundationStyleApi
import androidx.compose.foundation.style.MutableStyleState
import androidx.compose.foundation.style.Style
import androidx.compose.foundation.style.styleable
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberUpdatedState
import androidx.compose.runtime.setValue
import androidx.compose.runtime.staticCompositionLocalOf
import androidx.compose.ui.Alignment
import androidx.compose.ui.layout.Layout
import androidx.compose.ui.layout.MeasurePolicy
import androidx.compose.ui.layout.layoutId
import androidx.compose.ui.unit.Constraints
import androidx.compose.ui.unit.constrainWidth
import androidx.compose.ui.unit.constrainHeight
import androidx.compose.ui.unit.sp
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.style.LineHeightStyle
import androidx.compose.ui.text.font.FontWeight
import com.toutiaoyan.compose.ui.tokens.StylePreset
import com.toutiaoyan.compose.ui.components.icon.LocalInlineIconSize
import androidx.compose.foundation.style.then
import androidx.compose.ui.Modifier
import androidx.compose.ui.semantics.LiveRegionMode
import androidx.compose.ui.semantics.heading
import androidx.compose.ui.semantics.liveRegion
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.unit.dp
import com.toutiaoyan.compose.ui.components.text.Text
import com.toutiaoyan.compose.ui.components.text.TextVariant
import com.toutiaoyan.compose.ui.components.text.TextColors
import com.toutiaoyan.compose.ui.theme.LocalContentColor
import com.toutiaoyan.compose.ui.theme.currentTheme
import com.toutiaoyan.compose.tailwind.core.twSpacing
import kotlin.experimental.ExperimentalTypeInference
import kotlin.jvm.JvmName

private enum class AlertPart { Icon, Action }

private val LocalAlertVariant = staticCompositionLocalOf<AlertVariant> { AlertVariant.Default }

private class AlertActionHolder {
    var content by mutableStateOf<(@Composable () -> Unit)?>(null)
}

private val LocalAlertActionHolder = staticCompositionLocalOf<AlertActionHolder?> { null }

/**
 * A short, prominent callout. Matches real shadcn/ui's `alert.tsx` grid layout
 * (icon column + title/description column) and `role="alert"` (assertive live region).
 *
 * ```
 * Alert
 * ├── Icon
 * ├── AlertTitle
 * ├── AlertDescription
 * └── AlertAction
 * ```
 *
 * The [title]/[description] overload is a convenience that builds those slots.
 *
 * Usage:
 * ```
 * Alert(title = "Heads up!", description = "You can add components to your app.")
 * Alert {
 *     AlertTitle("Heads up!")
 *     AlertDescription("You can add components to your app.")
 *     AlertAction { Button("Enable", onClick = {}, variant = ButtonVariant.Outline) }
 * }
 * ```
 */
@Composable
fun Alert(
    modifier: Modifier = Modifier,
    variant: AlertVariant = AlertVariant.Default,
    icon: (@Composable () -> Unit)? = null,
    style: Style = Style,
    content: @Composable AlertScope.() -> Unit,
) {
    val styleState = remember { MutableStyleState(MutableInteractionSource()) }
    val actionHolder = remember { AlertActionHolder() }
    val nova = currentTheme.activePreset == StylePreset.Nova
    val contentColor =
        if (nova && variant == AlertVariant.Default) {
            currentTheme.colors.cardForeground
        } else {
            variant.contentColor(
                currentTheme.colors,
            )
        }
    CompositionLocalProvider(
        LocalContentColor provides contentColor,
        LocalAlertVariant provides variant,
        LocalInlineIconSize provides if (nova) 16.dp else LocalInlineIconSize.current,
        LocalAlertActionHolder provides actionHolder,
    ) {
        if (nova) {
            Layout(
                modifier =
                    modifier.semantics { liveRegion = LiveRegionMode.Assertive }
                        .styleable(styleState, variant.rememberStyle(), style)
                        .padding(horizontal = 10.dp, vertical = 8.dp),
                content = {
                    if (icon != null) Box(Modifier.layoutId(AlertPart.Icon)) { icon() }
                    AlertScope().content()
                },
                measurePolicy = NovaAlertMeasurePolicy,
            )
        } else {
            Box(
                modifier =
                    modifier
                        .semantics { liveRegion = LiveRegionMode.Assertive }
                        .styleable(styleState, variant.rememberStyle(), style)
                        .padding(horizontal = twSpacing.lg, vertical = twSpacing.md),
            ) {
                Row(horizontalArrangement = Arrangement.spacedBy(twSpacing.sm)) {
                    if (icon != null) {
                        Column(modifier = Modifier.padding(top = 2.dp)) { icon() }
                    }
                    Column(verticalArrangement = Arrangement.spacedBy(twSpacing.xs2)) {
                        AlertScope().content()
                    }
                }
                actionHolder.content?.let { action ->
                    Box(modifier = Modifier.align(Alignment.TopEnd), content = { action() })
                }
            }
        }
    }
}

private val NovaAlertMeasurePolicy =
    MeasurePolicy { measurables, constraints ->
        val loose = constraints.copy(minWidth = 0, minHeight = 0)
        val action = measurables.firstOrNull { it.layoutId == AlertPart.Action }?.measure(loose)
        val symbol = measurables.firstOrNull { it.layoutId == AlertPart.Icon }?.measure(loose)
        val start = if (symbol != null) symbol.width + 8.dp.roundToPx() else 0
        // CSS pr-18 replaces the usual 10px right padding: reserve 62px more.
        val end = if (action != null) 62.dp.roundToPx() else 0
        val bodyNodes = measurables.filter { it.layoutId != AlertPart.Action && it.layoutId != AlertPart.Icon }
        val width =
            if (constraints.hasBoundedWidth) {
                constraints.maxWidth
            } else {
                val natural = bodyNodes.maxOfOrNull { it.maxIntrinsicWidth(Constraints.Infinity) } ?: 0
                constraints.constrainWidth(
                    (natural.toLong() + start + end).coerceAtMost((Constraints.Infinity - 1).toLong()).toInt(),
                )
            }
        val bodyWidth = (width - start - end).coerceAtLeast(0)
        val body = bodyNodes.map { it.measure(loose.copy(maxWidth = bodyWidth)) }
        val gap = 2.dp.roundToPx()
        val bodyHeight = body.sumOf { it.height } + gap * (body.size - 1).coerceAtLeast(0)
        val height =
            constraints.constrainHeight(
                maxOf(bodyHeight, (symbol?.height ?: 0) + if (symbol != null) 2.dp.roundToPx() else 0),
            )
        layout(constraints.constrainWidth(width), height) {
            symbol?.placeRelative(0, 2.dp.roundToPx())
            var y = 0
            body.forEach { child ->
                child.placeRelative(start, y)
                y += child.height + gap
            }
            // CSS absolute offsets start inside the 1px border: top/right
            // 8px therefore place the action 9px from the outer bounds.
            action?.placeRelative(width - action.width + 2.dp.roundToPx(), 0)
        }
    }

/** Convenience: [title] + optional [description] instead of slots. */
@Composable
fun Alert(
    title: String,
    modifier: Modifier = Modifier,
    description: String? = null,
    variant: AlertVariant = AlertVariant.Default,
    icon: (@Composable () -> Unit)? = null,
    style: Style = Style,
) {
    Alert(modifier = modifier, variant = variant, icon = icon, style = style) {
        AlertTitle(title)
        if (description != null) {
            AlertDescription(description)
        }
    }
}

/** Slot receiver for [AlertTitle] / [AlertDescription]. */
class AlertScope internal constructor()

/** Matches shadcn's `AlertTitle`. */
@Composable
fun AlertScope.AlertTitle(text: String) {
    AlertTitle { text }
}

@Composable
@JvmName("AlertTitleLambda")
@OverloadResolutionByLambdaReturnType
fun AlertScope.AlertTitle(
    modifier: Modifier = Modifier,
    style: Style = Style,
    text: () -> String,
) {
    Text(
        modifier = modifier.semantics { heading() },
        variant = TextVariant.LabelLarge,
        style =
            (
                if (currentTheme.activePreset == StylePreset.Nova) {
                    Style {
                        fontSize(14.sp)
                        lineHeight(20.sp)
                    }
                } else {
                    Style
                }
            ) then style,
        textStyle =
            if (currentTheme.activePreset == StylePreset.Nova) {
                TextStyle(
                    fontSize = 14.sp,
                    lineHeight = 20.sp,
                    lineHeightStyle = LineHeightStyle(LineHeightStyle.Alignment.Center, LineHeightStyle.Trim.None),
                    fontWeight = FontWeight.Medium,
                )
            } else {
                TextStyle.Default
            },
        text = text,
    )
}

/** Matches shadcn's `AlertDescription`. */
@Composable
fun AlertScope.AlertDescription(text: String) {
    AlertDescription { text }
}

@Composable
@JvmName("AlertDescriptionLambda")
@OverloadResolutionByLambdaReturnType
fun AlertScope.AlertDescription(
    modifier: Modifier = Modifier,
    style: Style = Style,
    text: () -> String,
) {
    Text(
        modifier = modifier,
        variant = TextVariant.BodySmall,
        textStyle =
            if (currentTheme.activePreset == StylePreset.Nova) {
                TextStyle(
                    fontSize = 14.sp,
                    lineHeight = 20.sp,
                    lineHeightStyle = LineHeightStyle(LineHeightStyle.Alignment.Center, LineHeightStyle.Trim.None),
                    fontWeight = FontWeight.Normal,
                    color =
                        if (LocalAlertVariant.current == AlertVariant.Destructive) {
                            currentTheme.colors.destructive.copy(
                                alpha = .9f,
                            )
                        } else {
                            currentTheme.colors.mutedForeground
                        },
                )
            } else {
                TextColors.Muted
            },
        style =
            (
                if (currentTheme.activePreset == StylePreset.Nova) {
                    Style {
                        fontSize(14.sp)
                        lineHeight(20.sp)
                    }
                } else {
                    Style
                }
            ) then style,
        text = text,
    )
}

/**
 * Official `AlertAction`: a button or other action at the top end of the alert.
 * Nova retains the official fixed 72dp end reserve. For wider content, add
 * matching end padding to a body Column; the action does not resize that reserve.
 * Multiple controls belong inside one action slot. RTL uses the logical end.
 * Omit it to keep existing Default/Destructive pixels.
 */
@Composable
fun AlertScope.AlertAction(
    modifier: Modifier = Modifier,
    content: @Composable () -> Unit,
) {
    if (currentTheme.activePreset == StylePreset.Nova) {
        Box(modifier.layoutId(AlertPart.Action)) { content() }
        return
    }
    val holder =
        checkNotNull(LocalAlertActionHolder.current) {
            "AlertAction must be used inside Alert { … }"
        }
    val currentAction =
        rememberUpdatedState<@Composable () -> Unit> {
            Box(modifier = modifier) { content() }
        }
    DisposableEffect(holder) {
        val registration: @Composable () -> Unit = { currentAction.value() }
        holder.content = registration
        onDispose {
            if (holder.content === registration) holder.content = null
        }
    }
}
