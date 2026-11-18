package com.toutiaoyan.compose.ui.components.avatar

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.BoxScope
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.rememberUpdatedState
import androidx.compose.ui.Alignment
import androidx.compose.ui.draw.clip
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.ColorFilter
import androidx.compose.ui.graphics.DefaultAlpha
import androidx.compose.ui.graphics.FilterQuality
import androidx.compose.ui.graphics.drawscope.DrawScope.Companion.DefaultFilterQuality
import androidx.compose.ui.graphics.painter.Painter
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.layout.ContentScale
import coil3.ImageLoader
import coil3.compose.AsyncImage
import coil3.compose.AsyncImagePainter
import com.toutiaoyan.compose.ui.theme.LocalContentColor
import com.toutiaoyan.compose.ui.theme.currentTheme

/**
 * Coil3-backed image for use inside [Avatar] (shadcn `AvatarImage`).
 *
 * Reports load state to the enclosing [Avatar] so [AvatarFallback] only shows when this
 * image is not successfully loaded.
 *
 * For local vectors, use the [ImageVector] overload.
 *
 * Usage:
 * ```
 * Avatar {
 *     AvatarImage(model = "https://example.com/avatar.png", contentDescription = "Avatar")
 *     AvatarFallback {
 *         Text(typography = LocalAvatarSize.current.textStyle) { "CN" }
 *     }
 * }
 * ```
 */
@Composable
fun BoxScope.AvatarImage(
    model: Any?,
    contentDescription: String? = null,
    modifier: Modifier = Modifier,
    imageLoader: ImageLoader = rememberAvatarImageLoader(),
    placeholder: Painter? = null,
    error: Painter? = null,
    fallback: Painter? = error,
    onLoading: ((AsyncImagePainter.State.Loading) -> Unit)? = null,
    onSuccess: ((AsyncImagePainter.State.Success) -> Unit)? = null,
    onError: ((AsyncImagePainter.State.Error) -> Unit)? = null,
    onLoadingStatusChange: ((AvatarImageLoadState) -> Unit)? = null,
    alignment: Alignment = Alignment.Center,
    contentScale: ContentScale = ContentScale.Crop,
    alpha: Float = DefaultAlpha,
    colorFilter: ColorFilter? = null,
    filterQuality: FilterQuality = DefaultFilterQuality,
) {
    val loadHolder = LocalAvatarImageLoadState.current

    LaunchedEffect(model) {
        setAvatarLoadState(
            loadHolder,
            if (model == null) AvatarImageLoadState.Error else AvatarImageLoadState.Loading,
            onLoadingStatusChange,
        )
    }
    ResetAvatarLoadStateOnDispose(loadHolder, onLoadingStatusChange)

    AsyncImage(
        model = model,
        contentDescription = contentDescription,
        imageLoader = imageLoader,
        modifier = modifier.fillMaxSize().clip(CircleShape),
        placeholder = placeholder,
        error = error,
        fallback = fallback,
        onLoading = {
            setAvatarLoadState(loadHolder, AvatarImageLoadState.Loading, onLoadingStatusChange)
            onLoading?.invoke(it)
        },
        onSuccess = {
            setAvatarLoadState(loadHolder, AvatarImageLoadState.Success, onLoadingStatusChange)
            onSuccess?.invoke(it)
        },
        onError = {
            setAvatarLoadState(loadHolder, AvatarImageLoadState.Error, onLoadingStatusChange)
            onError?.invoke(it)
        },
        alignment = alignment,
        contentScale = contentScale,
        alpha = alpha,
        colorFilter = colorFilter,
        filterQuality = filterQuality,
    )
}

/**
 * [ImageVector] variant of [AvatarImage] for local icons (Lucide, custom vectors, …).
 *
 * Marks the avatar image as successfully loaded immediately so [AvatarFallback] is hidden.
 *
 * [tint] resolution matches [com.toutiaoyan.compose.ui.components.icon.Icon]: explicit [tint],
 * else [LocalContentColor], else theme `foreground`.
 *
 * Usage:
 * ```
 * Avatar {
 *     AvatarImage(imageVector = Lucide.User, contentDescription = "User")
 * }
 * ```
 */
@Composable
fun BoxScope.AvatarImage(
    imageVector: ImageVector,
    contentDescription: String? = null,
    modifier: Modifier = Modifier,
    alignment: Alignment = Alignment.Center,
    contentScale: ContentScale = ContentScale.Fit,
    alpha: Float = DefaultAlpha,
    tint: Color = Color.Unspecified,
    onLoadingStatusChange: ((AvatarImageLoadState) -> Unit)? = null,
) {
    val loadHolder = LocalAvatarImageLoadState.current
    LaunchedEffect(imageVector) {
        setAvatarLoadState(loadHolder, AvatarImageLoadState.Success, onLoadingStatusChange)
    }
    ResetAvatarLoadStateOnDispose(loadHolder, onLoadingStatusChange)

    val theme = currentTheme
    val resolvedTint =
        when {
            tint != Color.Unspecified -> tint
            LocalContentColor.current != Color.Unspecified -> LocalContentColor.current
            else -> theme.colors.foreground
        }
    Image(
        imageVector = imageVector,
        contentDescription = contentDescription,
        modifier = modifier.fillMaxSize().clip(CircleShape),
        alignment = alignment,
        contentScale = contentScale,
        alpha = alpha,
        colorFilter = ColorFilter.tint(resolvedTint),
    )
}

@Composable
private fun ResetAvatarLoadStateOnDispose(
    holder: AvatarImageLoadStateHolder?,
    onLoadingStatusChange: ((AvatarImageLoadState) -> Unit)?,
) {
    val currentObserver = rememberUpdatedState(onLoadingStatusChange)
    DisposableEffect(holder) {
        onDispose {
            setAvatarLoadState(holder, AvatarImageLoadState.Idle, currentObserver.value)
        }
    }
}

private fun setAvatarLoadState(
    holder: AvatarImageLoadStateHolder?,
    state: AvatarImageLoadState,
    onLoadingStatusChange: ((AvatarImageLoadState) -> Unit)?,
) {
    holder?.state = state
    onLoadingStatusChange?.invoke(state)
}

/** A decoded local image, without a network request. Shares the normal image/fallback state. */
@Composable
fun BoxScope.AvatarImage(
    painter: Painter,
    contentDescription: String? = null,
    modifier: Modifier = Modifier,
    contentScale: ContentScale = ContentScale.Crop,
    colorFilter: ColorFilter? = null,
    onLoadingStatusChange: ((AvatarImageLoadState) -> Unit)? = null,
) {
    val holder = LocalAvatarImageLoadState.current
    val currentObserver = rememberUpdatedState(onLoadingStatusChange)
    DisposableEffect(holder, painter) {
        setAvatarLoadState(holder, AvatarImageLoadState.Success, currentObserver.value)
        onDispose { setAvatarLoadState(holder, AvatarImageLoadState.Idle, currentObserver.value) }
    }
    Image(
        painter = painter,
        contentDescription = contentDescription,
        modifier = modifier.fillMaxSize().clip(CircleShape),
        contentScale = contentScale,
        colorFilter = colorFilter,
    )
}
