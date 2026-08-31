package com.toutiaoyan.compose.ui.components.badge
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.style.ExperimentalFoundationStyleApi
import androidx.compose.foundation.style.Style
import androidx.compose.foundation.style.rememberUpdatedStyleState
import androidx.compose.foundation.style.styleable
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.semantics.Role
import com.toutiaoyan.compose.ui.theme.LocalContentColor
import com.toutiaoyan.compose.ui.theme.currentTheme
import com.toutiaoyan.compose.ui.components.text.Text

/**
 * Label/tag component. Matches real shadcn badge.tsx: every variant reserves a 1dp
 * border (transparent except Outline), and [onClick] enables the link/`asChild`
 * focus-ring path.
 *
 * Usage:
 * ```
 * Badge("New")
 * Badge("Error", variant = BadgeVariant.Destructive)
 * Badge("Docs", onClick = { /* navigate */ })
 * ```
 */
@OptIn(ExperimentalFoundationStyleApi::class)
@Composable
fun Badge(
    modifier: Modifier = Modifier,
    variant: BadgeVariant = BadgeVariant.Default,
    onClick: (() -> Unit)? = null,
    style: Style = Style,
    content: @Composable () -> Unit,
) {
    val interactionSource = remember { MutableInteractionSource() }
    val styleState = rememberUpdatedStyleState(interactionSource)

    Box(
        modifier =
            modifier
                .then(
                    if (onClick != null) {
                        Modifier.clickable(
                            interactionSource = interactionSource,
                            indication = null,
                            role = Role.Button,
                            onClick = onClick,
                        )
                    } else {
                        Modifier
                    },
                )
                .styleable(styleState, variant.rememberStyle(), style),
        contentAlignment = Alignment.Center,
    ) {
        CompositionLocalProvider(
            LocalContentColor provides variant.contentColor(currentTheme.colors),
        ) {
            content()
        }
    }
}

@OptIn(ExperimentalFoundationStyleApi::class)
@Composable
fun Badge(
    text: String,
    modifier: Modifier = Modifier,
    variant: BadgeVariant = BadgeVariant.Default,
    onClick: (() -> Unit)? = null,
    style: Style = Style,
) {
    Badge(
        modifier = modifier,
        variant = variant,
        onClick = onClick,
        style = style,
        content = { Text { text } },
    )
}
