@file:OptIn(androidx.compose.foundation.style.ExperimentalFoundationStyleApi::class)

package com.toutiaoyan.compose.ui.catalog

import com.composables.icons.lucide.Lucide
import com.composables.icons.lucide.Github
import com.composables.icons.lucide.Plus
import androidx.compose.foundation.background
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalUriHandler
import androidx.compose.ui.unit.dp
import com.toutiaoyan.compose.ui.catalog.docs.DocIcon
import com.toutiaoyan.compose.ui.components.button.Button
import com.toutiaoyan.compose.ui.components.button.ButtonSize
import com.toutiaoyan.compose.ui.components.button.ButtonVariant
import com.toutiaoyan.compose.ui.components.text.Text
import com.toutiaoyan.compose.ui.components.text.TextColors
import com.toutiaoyan.compose.ui.components.text.TextVariant
import com.toutiaoyan.compose.ui.theme.currentTheme
import com.toutiaoyan.compose.tailwind.core.twSpacing

private const val GITHUB_URL = "https://github.com/ronjunevaldoz/shadcn-compose"

/** Display-only star count — mirrors [GitHubLink](https://github.com/shadcn-ui/ui/blob/main/apps/v4/components/github-link.tsx). */
private const val GITHUB_STARS = "1.2k"

/**
 * App chrome matching [site-header.tsx](https://github.com/shadcn-ui/ui/blob/main/apps/v4/components/site-header.tsx).
 *
 * Leading: Home · ghost MainNav (Components / Create).
 * Trailing: GitHub · ModeSwitcher · Create-route Open in / Get Code, else New.
 */
@Composable
fun CatalogTopBar(
    isDarkMode: Boolean,
    onToggleDarkMode: (Boolean) -> Unit,
    isOnCreateRoute: Boolean = false,
    onNavigateHome: () -> Unit = {},
    onNavigateToComponents: () -> Unit = {},
    onNavigateToCreate: () -> Unit = {},
    onGetCode: () -> Unit = {},
) {
    val uriHandler = LocalUriHandler.current

    Row(
        modifier =
            Modifier
                .fillMaxWidth()
                .height(56.dp)
                .background(currentTheme.colors.background)
                .padding(horizontal = twSpacing.lg),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically,
    ) {
        Row(
            horizontalArrangement = Arrangement.spacedBy(twSpacing.sm),
            verticalAlignment = Alignment.CenterVertically,
        ) {
            Button(
                onClick = onNavigateHome,
                variant = ButtonVariant.Ghost,
                size = ButtonSize.Sm,
            ) {
                Text { "Home" }
            }
            Button(
                onClick = onNavigateToComponents,
                variant = ButtonVariant.Ghost,
                size = ButtonSize.Sm,
            ) {
                Text { "Components" }
            }
            Button(
                onClick = onNavigateToCreate,
                variant = ButtonVariant.Ghost,
                size = ButtonSize.Sm,
            ) {
                Text { "Create" }
            }
        }

        Row(
            modifier = Modifier.horizontalScroll(rememberScrollState()),
            horizontalArrangement = Arrangement.spacedBy(twSpacing.sm),
            verticalAlignment = Alignment.CenterVertically,
        ) {
            // GitHubLink: icon + muted tabular stars.
            Button(
                onClick = { uriHandler.openUri(GITHUB_URL) },
                variant = ButtonVariant.Ghost,
                size = ButtonSize.Sm,
            ) {
                DocIcon(Lucide.Github)
                Text(
                    variant = TextVariant.BodySmall,
                    textStyle = TextColors.Muted,
                ) { GITHUB_STARS }
            }

            HeaderSeparator()

            // ModeSwitcher: half-circle glyph (mode-switcher.tsx), not Sun/Moon swap.
            Button(
                onClick = { onToggleDarkMode(!isDarkMode) },
                variant = ButtonVariant.Ghost,
                size = ButtonSize.IconSm,
            ) {
                ModeSwitcherIcon()
            }

            HeaderSeparator()

            if (isOnCreateRoute) {
                // DesignerActions: V0 outline + Get Code default (site-header + v0-button.tsx).
                Button(
                    onClick = onNavigateToCreate,
                    variant = ButtonVariant.Outline,
                    size = ButtonSize.Sm,
                ) {
                    Text { "Open in" }
                }
                Button(
                    onClick = onGetCode,
                    variant = ButtonVariant.Default,
                    size = ButtonSize.Sm,
                ) {
                    Text { "Get Code" }
                }
            } else {
                // Non-designer: New → /create (site-header).
                Button(
                    onClick = onNavigateToCreate,
                    variant = ButtonVariant.Default,
                    size = ButtonSize.Sm,
                ) {
                    DocIcon(Lucide.Plus, tint = currentTheme.colors.primaryForeground)
                    Text { "New" }
                }
            }
        }
    }

    Box(
        modifier =
            Modifier
                .fillMaxWidth()
                .height(1.dp)
                .background(currentTheme.colors.border),
    )
}

@Composable
private fun HeaderSeparator() {
    Box(
        modifier =
            Modifier
                .padding(horizontal = twSpacing.xs2)
                .width(1.dp)
                .height(16.dp)
                .background(currentTheme.colors.border),
    )
}
