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
import androidx.compose.runtime.remember
import androidx.compose.runtime.staticCompositionLocalOf
import androidx.compose.ui.Alignment
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

private class AlertActionHolder {
    var content: (@Composable () -> Unit)? = null
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
    actionHolder.content = null
    CompositionLocalProvider(
        LocalContentColor provides variant.contentColor(currentTheme.colors),
        LocalAlertActionHolder provides actionHolder,
    ) {
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
fun AlertScope.AlertTitle(
    text: String,
) {
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
        style = style,
        text = text,
    )
}

/** Matches shadcn's `AlertDescription`. */
@Composable
fun AlertScope.AlertDescription(
    text: String,
) {
    Text(variant = TextVariant.BodySmall, textStyle = TextColors.Muted) { text }
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
        textStyle = TextColors.Muted,
        style = style,
        text = text,
    )
}

/**
 * Official `AlertAction`: a button or other action in the top-right of the alert.
 * Omit it to keep existing Default/Destructive pixels.
 */
@Composable
fun AlertScope.AlertAction(
    modifier: Modifier = Modifier,
    content: @Composable () -> Unit,
) {
    val holder =
        checkNotNull(LocalAlertActionHolder.current) {
            "AlertAction must be used inside Alert { … }"
        }
    holder.content = { Box(modifier = modifier, content = { content() }) }
}
