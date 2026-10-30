@file:OptIn(ExperimentalFoundationStyleApi::class)

package com.toutiaoyan.compose.ui.components.avatar
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.style.ExperimentalFoundationStyleApi
import androidx.compose.foundation.style.Style
import androidx.compose.runtime.Composable
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.graphics.shadow.Shadow
import com.toutiaoyan.compose.ui.utils.rememberTheme

enum class AvatarSize(
    val boxSize: Dp,
) {
    Sm(24.dp),
    Default(32.dp),
    Lg(40.dp),
}

@Composable
fun AvatarSize.rememberStyle(): Style =
    rememberTheme(this) {
        Style {
            width(boxSize)
            height(boxSize)
            shape(CircleShape)
        }
    }

@Composable
fun rememberAvatarFallbackStyle(): Style =
    rememberTheme {
        Style {
            width(1f)
            height(1f)
            shape(CircleShape)
            background(colors.muted)
            contentColor(colors.mutedForeground)
        }
    }

@Composable
fun AvatarSize.rememberBadgeStyle(): Style =
    rememberTheme(this) {
        val badgeSize =
            when (this@rememberBadgeStyle) {
                AvatarSize.Sm -> 8.dp
                AvatarSize.Default -> 10.dp
                AvatarSize.Lg -> 12.dp
            }
        Style {
            width(badgeSize)
            height(badgeSize)
            shape(CircleShape)
            background(colors.primary)
            dropShadow(Shadow(radius = 0.dp, spread = 2.dp, color = colors.background))
        }
    }

/** Official group member `ring-2 ring-background` when [AvatarGroup] overlaps. */
@Composable
fun rememberAvatarGroupMemberRingStyle(): Style =
    rememberTheme {
        Style {
            dropShadow(Shadow(radius = 0.dp, spread = 2.dp, color = colors.background))
        }
    }

/** Official `AvatarGroupCount`: muted circle with `ring-2 ring-background`. */
@Composable
fun rememberAvatarGroupCountStyle(size: AvatarSize = AvatarSize.Default): Style =
    rememberTheme(size) {
        Style {
            width(size.boxSize)
            height(size.boxSize)
            shape(CircleShape)
            background(colors.muted)
            contentColor(colors.mutedForeground)
            dropShadow(Shadow(radius = 0.dp, spread = 2.dp, color = colors.background))
        }
    }
