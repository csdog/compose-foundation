package com.toutiaoyan.compose.ui.components.field

import androidx.compose.foundation.style.ExperimentalFoundationStyleApi
import androidx.compose.foundation.style.Style
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.TextStyle
import com.toutiaoyan.compose.ui.components.label.LabelImpl

/** A FieldLabel with explicit typography, including the source's font-normal variant. */
@OptIn(ExperimentalFoundationStyleApi::class)
@Composable
fun FieldLabel(
    text: String,
    textStyle: TextStyle,
    modifier: Modifier = Modifier,
    required: Boolean = false,
    disabled: Boolean = false,
    style: Style = Style,
) {
    LabelImpl(
        text = text,
        modifier = modifier,
        required = required,
        disabled = disabled || LocalFieldSetDisabled.current,
        style = style,
        fieldLabel = true,
        textStyle = textStyle,
    )
}
