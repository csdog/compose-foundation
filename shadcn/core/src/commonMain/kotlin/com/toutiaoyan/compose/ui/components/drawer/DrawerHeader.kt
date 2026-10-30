@file:OptIn(ExperimentalFoundationStyleApi::class, ExperimentalTypeInference::class)

package com.toutiaoyan.compose.ui.components.drawer

import com.toutiaoyan.compose.ui.components.overlay.overlayTypography
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ColumnScope
import androidx.compose.foundation.style.ExperimentalFoundationStyleApi
import androidx.compose.foundation.style.MutableStyleState
import androidx.compose.foundation.style.Style
import androidx.compose.foundation.style.styleable
import androidx.compose.foundation.style.then
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.semantics.heading
import androidx.compose.ui.semantics.semantics
import com.toutiaoyan.compose.tailwind.core.twSpacing
import com.toutiaoyan.compose.ui.components.text.Text
import com.toutiaoyan.compose.ui.components.text.TextColors
import com.toutiaoyan.compose.ui.components.text.TextVariant
import kotlin.experimental.ExperimentalTypeInference
import kotlin.jvm.JvmName

/** Top block of a drawer — web `DrawerHeader`. */
@Composable
fun DrawerHeader(
    modifier: Modifier = Modifier,
    style: Style = Style,
    content: @Composable ColumnScope.() -> Unit,
) {
    val styleState = remember { MutableStyleState(MutableInteractionSource()) }
    Column(
        modifier = modifier.styleable(styleState, rememberDrawerHeaderStyle() then style),
        verticalArrangement = Arrangement.spacedBy(twSpacing.scale0p5),
    ) {
        content()
    }
}

/** Convenience: [title] + optional [description] instead of slots. */
@Composable
fun DrawerHeader(
    title: String,
    description: String? = null,
    modifier: Modifier = Modifier,
    style: Style = Style,
) {
    DrawerHeader(modifier = modifier, style = style) {
        DrawerTitle(title)
        if (description != null) {
            DrawerDescription(description)
        }
    }
}

@Composable
fun DrawerTitle(text: String) {
    DrawerTitle { text }
}

@Composable
@JvmName("DrawerTitleLambda")
@OverloadResolutionByLambdaReturnType
fun DrawerTitle(
    modifier: Modifier = Modifier,
    style: Style = Style,
    text: () -> String,
) {
    Text(
        modifier = modifier.semantics { heading() },
        variant = TextVariant.TitleMedium,
        textStyle = overlayTypography(16, 24, medium = true),
        style = style,
        text = text,
    )
}

@Composable
fun DrawerDescription(text: String) {
    DrawerDescription { text }
}

@Composable
@JvmName("DrawerDescriptionLambda")
@OverloadResolutionByLambdaReturnType
fun DrawerDescription(
    modifier: Modifier = Modifier,
    style: Style = Style,
    text: () -> String,
) {
    Text(
        modifier = modifier,
        variant = TextVariant.BodySmall,
        textStyle = overlayTypography(muted = true, fallback = TextColors.Muted),
        style = style,
        text = text,
    )
}
