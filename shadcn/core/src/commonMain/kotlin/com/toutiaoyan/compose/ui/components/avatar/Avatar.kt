@file:OptIn(ExperimentalFoundationStyleApi::class)

package com.toutiaoyan.compose.ui.components.avatar

import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxScope
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.style.ExperimentalFoundationStyleApi
import androidx.compose.foundation.style.MutableStyleState
import androidx.compose.foundation.style.Style
import androidx.compose.foundation.style.styleable
import androidx.compose.foundation.style.then
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.Stable
import androidx.compose.runtime.compositionLocalOf
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateMapOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.draw.drawWithCache
import androidx.compose.ui.geometry.CornerRadius
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.BlendMode
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.zIndex
import androidx.compose.ui.Alignment
import androidx.compose.foundation.layout.size
import androidx.compose.ui.unit.sp
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import com.toutiaoyan.compose.ui.styles.cssStyleable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import kotlinx.coroutines.delay
import com.toutiaoyan.compose.ui.components.text.Text
import com.toutiaoyan.compose.ui.components.text.TextColors
import com.toutiaoyan.compose.ui.components.text.TextVariant
import com.toutiaoyan.compose.ui.theme.LocalContentColor
import com.toutiaoyan.compose.ui.theme.currentTheme

val AvatarSize.textStyle: TextVariant
    get() =
        when (this) {
            AvatarSize.Sm -> TextVariant.BodySmall
            AvatarSize.Default, AvatarSize.Lg -> TextVariant.BodyMedium
        }

/** Lets [AvatarFallback]/[AvatarBadge] read their enclosing [Avatar]'s size. */
val LocalAvatarSize = compositionLocalOf { AvatarSize.Default }

@Stable
internal class AvatarGroupState {
    private val memberSizes = mutableStateMapOf<Any, AvatarSize>()

    val size: AvatarSize
        get() =
            when {
                memberSizes.values.any { it == AvatarSize.Sm } -> AvatarSize.Sm
                memberSizes.values.any { it == AvatarSize.Lg } -> AvatarSize.Lg
                else -> AvatarSize.Default
            }

    fun register(
        member: Any,
        size: AvatarSize,
    ) {
        memberSizes[member] = size
    }

    fun unregister(member: Any) {
        memberSizes.remove(member)
    }
}

private val LocalAvatarGroupState = compositionLocalOf<AvatarGroupState?> { null }

/** Official AvatarGroup `-space-x-2` overlap. Pass as [AvatarGroup] `spacing`. */
object AvatarGroupDefaults {
    val Overlap: Dp = (-8).dp
}

/** Load state of the sibling [AvatarImage], owned by [Avatar]. */
enum class AvatarImageLoadState {
    /** No [AvatarImage], or it left composition. */
    Idle,

    /** Remote/local model is loading. */
    Loading,

    /** Image displayed successfully ([ImageVector] counts as success immediately). */
    Success,

    /** Load failed, or [AvatarImage] model was null. */
    Error,
}

internal class AvatarImageLoadStateHolder {
    var state by mutableStateOf(AvatarImageLoadState.Idle)
}

internal val LocalAvatarImageLoadState =
    compositionLocalOf<AvatarImageLoadStateHolder?> { null }

/** Load state and size of the nearest [Avatar]. */
class AvatarState(
    val loadState: AvatarImageLoadState,
    val size: AvatarSize,
)

/** Reads the nearest [Avatar]. Matches Radix avatar image context. */
@Composable
fun avatarState(): AvatarState =
    AvatarState(
        loadState = LocalAvatarImageLoadState.current?.state ?: AvatarImageLoadState.Idle,
        size = LocalAvatarSize.current,
    )

/**
 * A circular user/entity image container. Matches real shadcn/ui's `avatar.tsx`
 * (`size-8` default, `size-6`/`size-10` for sm/lg, `rounded-full overflow-hidden`).
 *
 * Size / clip live in Style — override with [style].
 *
 * [AvatarFallback] is shown only while the sibling [AvatarImage] has not loaded
 * successfully (loading / error / missing), matching Radix/shadcn behavior.
 *
 * Usage:
 * ```
 * Avatar("CN")
 * Avatar {
 *     AvatarImage(model = "https://example.com/a.png", contentDescription = "Avatar")
 *     AvatarFallback("CN")
 * }
 * AvatarGroup {
 *     Avatar("CN")
 *     AvatarGroupCount("+3")
 * }
 * ```
 */
@Composable
fun Avatar(
    modifier: Modifier = Modifier,
    size: AvatarSize = AvatarSize.Default,
    style: Style = Style,
    content: @Composable BoxScope.() -> Unit,
) {
    val styleState = remember { MutableStyleState(MutableInteractionSource()) }
    val imageLoadState = remember { AvatarImageLoadStateHolder() }
    val groupState = LocalAvatarGroupState.current
    val groupMember = remember(groupState) { Any() }
    DisposableEffect(groupState, groupMember, size) {
        groupState?.register(groupMember, size)
        onDispose { groupState?.unregister(groupMember) }
    }
    val overlapRing =
        if (groupState != null) rememberAvatarGroupMemberRingStyle() else Style
    CompositionLocalProvider(
        LocalAvatarSize provides size,
        LocalAvatarImageLoadState provides imageLoadState,
    ) {
        Box(
            modifier =
                modifier.cssStyleable(
                    styleState,
                    size.rememberStyle() then overlapRing then style,
                ),
            contentAlignment = Alignment.Center,
        ) {
            content()
            AvatarBorder()
        }
    }
}

/** The official rounded-full ::after border blends with the image, without reserving space. */
@Composable
private fun BoxScope.AvatarBorder() {
    val theme = currentTheme
    val color = theme.colors.border
    val blendMode = if (theme.isDark) BlendMode.Lighten else BlendMode.Darken
    Box(
        Modifier.matchParentSize().drawWithCache {
            val width = 1.dp.toPx()
            val inset = width / 2f
            val radius = (size.minDimension / 2f - inset).coerceAtLeast(0f)
            onDrawBehind {
                drawRoundRect(
                    color = color,
                    topLeft = Offset(inset, inset),
                    size = Size((size.width - width).coerceAtLeast(0f), (size.height - width).coerceAtLeast(0f)),
                    cornerRadius = CornerRadius(radius),
                    style = Stroke(width),
                    blendMode = blendMode,
                )
            }
        },
    )
}

/** Convenience: initials [text] as [AvatarFallback]. */
@Composable
fun Avatar(
    text: String,
    modifier: Modifier = Modifier,
    size: AvatarSize = AvatarSize.Default,
    style: Style = Style,
) {
    Avatar(modifier = modifier, size = size, style = style) {
        AvatarFallback(text)
    }
}

/**
 * Initials/icon fallback shown only when [AvatarImage] is not in [AvatarImageLoadState.Success]
 * (loading, error, null model, or no image at all).
 *
 * [delayMillis] / official [delayMs] match Radix `Avatar.Fallback` `delayMs` —
 * while the image is [AvatarImageLoadState.Loading], wait this long before
 * showing initials. Idle / error still show immediately. Default `0`.
 *
 * Pass any content (typically [com.toutiaoyan.compose.ui.components.text.Text] or an Icon).
 * Ambient [LocalAvatarSize] and muted `contentColor` are provided for you.
 */
@Composable
fun AvatarFallback(
    modifier: Modifier = Modifier,
    style: Style = Style,
    delayMillis: Int = 0,
    delayMs: Int? = null,
    content: @Composable BoxScope.() -> Unit,
) {
    val resolvedDelay = delayMs ?: delayMillis
    val loadState = LocalAvatarImageLoadState.current?.state ?: AvatarImageLoadState.Idle
    if (loadState == AvatarImageLoadState.Success) return
    var delayElapsed by remember(resolvedDelay, loadState) {
        mutableStateOf(resolvedDelay <= 0 || loadState != AvatarImageLoadState.Loading)
    }
    LaunchedEffect(loadState, resolvedDelay) {
        if (resolvedDelay <= 0 || loadState != AvatarImageLoadState.Loading) {
            delayElapsed = true
        } else {
            delayElapsed = false
            delay(resolvedDelay.toLong())
            delayElapsed = true
        }
    }
    if (loadState == AvatarImageLoadState.Loading && !delayElapsed) return

    val styleState = remember { MutableStyleState(MutableInteractionSource()) }
    Box(
        modifier =
            modifier.styleable(
                styleState,
                rememberAvatarFallbackStyle() then style,
            ),
        contentAlignment = Alignment.Center,
    ) {
        CompositionLocalProvider(
            LocalContentColor provides currentTheme.colors.mutedForeground,
        ) {
            content()
        }
    }
}

@Composable
fun AvatarFallback(
    text: String,
    modifier: Modifier = Modifier,
    style: Style = Style,
    delayMillis: Int = 0,
    delayMs: Int? = null,
) {
    AvatarFallback(
        modifier = modifier,
        style = style,
        delayMillis = delayMillis,
        delayMs = delayMs,
        content = {
            Text(
                variant = LocalAvatarSize.current.textStyle,
                textStyle =
                    TextStyle(
                        fontWeight = FontWeight.Normal,
                        letterSpacing = 0.sp,
                    ),
            ) { text }
        },
    )
}

/** Bottom-end status indicator (mirrored in RTL). Small avatars hide icon content. */
@Composable
fun BoxScope.AvatarBadge(
    modifier: Modifier = Modifier,
    style: Style = Style,
    content: (@Composable () -> Unit)? = null,
) {
    val size = LocalAvatarSize.current
    val styleState = remember { MutableStyleState(MutableInteractionSource()) }
    // StyleModifier in Compose 1.12.2 leaves zIndex as TODO; use the working parent modifier.
    Box(
        modifier =
            Modifier.align(Alignment.BottomEnd).then(modifier).zIndex(10f).cssStyleable(
                styleState,
                size.rememberBadgeStyle() then style,
            ),
        contentAlignment = Alignment.Center,
    ) {
        if (size != AvatarSize.Sm && content != null) {
            CompositionLocalProvider(LocalContentColor provides currentTheme.colors.primaryForeground) {
                Box(Modifier.size(8.dp), contentAlignment = Alignment.Center) { content() }
            }
        }
    }
}

/**
 * A row of [Avatar]s (e.g. "who's in this conversation"). Official shadcn uses
 * `-space-x-2` plus `ring-2 ring-background`. [spacing] defaults to
 * [AvatarGroupDefaults.Overlap].
 */
@Composable
fun AvatarGroup(
    modifier: Modifier = Modifier,
    style: Style = Style,
    spacing: Dp = AvatarGroupDefaults.Overlap,
    content: @Composable () -> Unit,
) {
    val styleState = remember { MutableStyleState(MutableInteractionSource()) }
    val groupState = remember { AvatarGroupState() }
    CompositionLocalProvider(LocalAvatarGroupState provides groupState) {
        Row(
            modifier = modifier.styleable(styleState, style),
            horizontalArrangement = Arrangement.spacedBy(spacing),
            verticalAlignment = Alignment.CenterVertically,
        ) { content() }
    }
}

/**
 * Official `AvatarGroupCount`: a trailing muted circle for leftover members (`+3`)
 * or a plus icon. Without an explicit [size], it follows the enclosing group using
 * the official CSS precedence: any [AvatarSize.Sm] -> [AvatarSize.Sm], otherwise
 * any [AvatarSize.Lg] -> [AvatarSize.Lg], otherwise [AvatarSize.Default].
 */
@Composable
fun AvatarGroupCount(
    modifier: Modifier = Modifier,
    size: AvatarSize? = null,
    style: Style = Style,
    content: @Composable BoxScope.() -> Unit,
) {
    val resolvedSize =
        size ?: LocalAvatarGroupState.current?.size ?: AvatarSize.Default
    val styleState = remember { MutableStyleState(MutableInteractionSource()) }
    CompositionLocalProvider(
        LocalAvatarSize provides resolvedSize,
        LocalContentColor provides currentTheme.colors.mutedForeground,
    ) {
        Box(
            modifier =
                modifier.cssStyleable(
                    styleState,
                    rememberAvatarGroupCountStyle(resolvedSize) then style,
                ),
            contentAlignment = Alignment.Center,
            content = content,
        )
    }
}

/** Convenience: [text] such as `+3`. */
@Composable
fun AvatarGroupCount(
    text: String,
    modifier: Modifier = Modifier,
    size: AvatarSize? = null,
    style: Style = Style,
) {
    AvatarGroupCount(modifier = modifier, size = size, style = style) {
        Text(
            variant = TextVariant.BodyMedium,
            textStyle =
                TextColors.Muted.merge(
                    TextStyle(
                        fontWeight = FontWeight.Normal,
                        letterSpacing = 0.sp,
                    ),
                ),
        ) { text }
    }
}
