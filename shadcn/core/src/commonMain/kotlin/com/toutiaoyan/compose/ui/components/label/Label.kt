@file:OptIn(ExperimentalFoundationStyleApi::class, ExperimentalTypeInference::class)

package com.toutiaoyan.compose.ui.components.label

import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.style.ExperimentalFoundationStyleApi
import androidx.compose.foundation.style.MutableStyleState
import androidx.compose.foundation.style.Style
import androidx.compose.foundation.style.styleable
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import com.toutiaoyan.compose.ui.theme.currentTheme
import com.toutiaoyan.compose.ui.components.text.Text
import com.toutiaoyan.compose.ui.components.text.TextColors
import com.toutiaoyan.compose.ui.components.text.TextVariant
import androidx.compose.ui.text.TextStyle
import kotlin.experimental.ExperimentalTypeInference
import kotlin.jvm.JvmName

/**
 * A form-field label. Purely presentational -- Compose has no HTML `for` attribute
 * to wire up, so association with a field is just visual (place it directly above
 * the field it describes).
 *
 * Usage:
 * ```
 * Label("Email", required = true)
 * Input(value = email, onValueChange = { email = it })
 * ```
 */
@Composable
fun Label(
    text: String,
    modifier: Modifier = Modifier,
    required: Boolean = false,
    disabled: Boolean = false,
    style: Style = Style,
) {
    val styleState = remember { MutableStyleState(MutableInteractionSource()) }
    Row(modifier = modifier.styleable(styleState, style)) {
        Text(
            variant = TextVariant.LabelLarge,
            textStyle = if (disabled) TextColors.Muted else TextStyle.Default
        ) { text }
        if (required) {
            Text(
                variant = TextVariant.LabelLarge,
                textStyle = TextStyle(color = currentTheme.colors.destructive),
            ) { " *" }
        }
    }
}

@Composable
@JvmName("LabelLambda")
@OverloadResolutionByLambdaReturnType
fun Label(
    modifier: Modifier = Modifier,
    required: Boolean = false,
    disabled: Boolean = false,
    style: Style = Style,
    text: () -> String,
) {
    Label(text = text(), modifier = modifier, required = required, disabled = disabled, style = style)
}
