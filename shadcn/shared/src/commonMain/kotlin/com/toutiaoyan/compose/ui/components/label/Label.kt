@file:OptIn(ExperimentalFoundationStyleApi::class, ExperimentalTypeInference::class)

package com.toutiaoyan.compose.ui.components.label

import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.style.ExperimentalFoundationStyleApi
import androidx.compose.foundation.style.MutableStyleState
import androidx.compose.foundation.style.Style
import androidx.compose.foundation.style.styleable
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.unit.sp
import com.toutiaoyan.compose.ui.tokens.StylePreset
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.layout.FirstBaseline
import androidx.compose.ui.layout.LastBaseline
import androidx.compose.ui.layout.AlignmentLine
import androidx.compose.ui.layout.layout
import androidx.compose.ui.unit.TextUnit
import androidx.compose.ui.unit.constrainHeight
import androidx.compose.ui.Modifier
import com.toutiaoyan.compose.ui.theme.currentTheme
import com.toutiaoyan.compose.ui.components.text.Text
import com.toutiaoyan.compose.ui.components.text.TextColors
import com.toutiaoyan.compose.ui.components.text.TextVariant
import androidx.compose.ui.text.style.LineHeightStyle
import androidx.compose.ui.text.TextStyle
import kotlin.experimental.ExperimentalTypeInference
import kotlin.jvm.JvmName
import kotlin.math.roundToInt

private const val NOVA_DISABLED_LABEL_ALPHA = 0.5f

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
    LabelImpl(text, modifier, required, disabled, style)
}

@Composable
internal fun LabelImpl(
    text: String,
    modifier: Modifier,
    required: Boolean,
    disabled: Boolean,
    style: Style,
    fieldLabel: Boolean = false,
    textStyle: TextStyle = TextStyle.Default,
) {
    val theme = currentTheme
    val nova = theme.activePreset == StylePreset.Nova
    val typography =
        if (nova) {
            theme.typography.labelLarge.copy(
                lineHeight = if (fieldLabel) 19.25.sp else 14.sp,
                letterSpacing = 0.sp,
                lineHeightStyle =
                    LineHeightStyle(
                        LineHeightStyle.Alignment.Center,
                        LineHeightStyle.Trim.None,
                        LineHeightStyle.Mode.Tight,
                    ),
            )
        } else {
            TextStyle.Default
        }
    val resolvedTypography = typography.merge(textStyle)
    val lineHeight = resolvedTypography.lineHeight
    val textModifier =
        if (nova && lineHeight.isSp && lineHeight.value > 0f) Modifier.labelLineBox(lineHeight) else Modifier
    val styleState = remember { MutableStyleState(MutableInteractionSource()) }
    Row(
        modifier =
            modifier.alpha(if (nova && disabled) NOVA_DISABLED_LABEL_ALPHA else 1f)
                .styleable(styleState, style),
    ) {
        Text(
            modifier = textModifier,
            variant = TextVariant.LabelLarge,
            textStyle = if (!nova && disabled) TextColors.Muted.merge(resolvedTypography) else resolvedTypography,
        ) { text }
        if (required) {
            Text(
                modifier = textModifier,
                variant = TextVariant.LabelLarge,
                textStyle = resolvedTypography.copy(color = currentTheme.colors.destructive),
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

// Compose 1.12.2's Skia ParagraphBuilder uses font ascent/descent at the boundaries
// when lineHeight <= fontSize, even with Tight. Keep the CSS line boxes and center
// the ink within them; baseline distance preserves wrapping and explicit newlines.
private fun Modifier.labelLineBox(lineHeight: TextUnit): Modifier =
    layout { measurable, constraints ->
        val child = measurable.measure(constraints)
        val first = child[FirstBaseline]
        val last = child[LastBaseline]
        val linePixels = lineHeight.toPx()
        val hasBaselines = first != AlignmentLine.Unspecified && last != AlignmentLine.Unspecified
        val target =
            if (hasBaselines && linePixels.isFinite() && linePixels > 0f) {
                // Baselines are integer pixels; infer the count before rounding the total
                // height so fractional line heights do not accumulate one pixel per line.
                val lines = ((last - first) / linePixels).roundToInt() + 1
                constraints.constrainHeight((linePixels * lines).roundToInt())
            } else {
                child.height
            }
        layout(child.width, target) { child.placeRelative(0, (target - child.height) / 2) }
    }
