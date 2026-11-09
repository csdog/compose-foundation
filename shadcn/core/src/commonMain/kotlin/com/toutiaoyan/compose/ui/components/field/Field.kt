@file:OptIn(ExperimentalFoundationStyleApi::class, ExperimentalTypeInference::class)

package com.toutiaoyan.compose.ui.components.field

import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ColumnScope
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.RowScope
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.style.ExperimentalFoundationStyleApi
import androidx.compose.foundation.style.MutableStyleState
import androidx.compose.foundation.style.Style
import androidx.compose.foundation.style.styleable
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.Immutable
import androidx.compose.runtime.remember
import androidx.compose.runtime.staticCompositionLocalOf
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.semantics.LiveRegionMode
import androidx.compose.ui.semantics.heading
import androidx.compose.ui.semantics.liveRegion
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.layout.layout
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.toutiaoyan.compose.ui.tokens.StylePreset
import com.toutiaoyan.compose.ui.components.label.LabelImpl
import androidx.compose.ui.text.style.LineHeightStyle
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import com.toutiaoyan.compose.tailwind.core.TwBreakpoint
import com.toutiaoyan.compose.tailwind.core.dp
import com.toutiaoyan.compose.tailwind.core.spacing
import com.toutiaoyan.compose.tailwind.core.twSpacing
import com.toutiaoyan.compose.ui.components.label.Label
import com.toutiaoyan.compose.ui.components.separator.Separator
import com.toutiaoyan.compose.ui.components.text.Text
import com.toutiaoyan.compose.ui.components.text.TextColors
import com.toutiaoyan.compose.ui.components.text.TextVariant
import com.toutiaoyan.compose.ui.theme.LocalContentColor
import com.toutiaoyan.compose.ui.theme.currentTheme
import kotlin.experimental.ExperimentalTypeInference
import kotlin.jvm.JvmName

/**
 * Form-field primitives matching real shadcn/ui's `field.tsx`:
 * [FieldSet], [FieldLegend], [FieldGroup], [Field], [FieldContent], [FieldLabel],
 * [FieldTitle], [FieldDescription], [FieldError], [FieldSeparator].
 *
 * Real `form.tsx` wraps these around react-hook-form; this library has no form-state
 * dependency, so compose these directly with hoisted state.
 *
 * Usage:
 * ```
 * FieldGroup {
 *     Field("Email", description = "We'll never share your email.") {
 *         Input(value = email, onValueChange = { email = it })
 *     }
 * }
 * ```
 *
 * [LocalFieldSetDisabled] matches HTML `fieldset disabled` for nested controls.
 */
internal val LocalFieldSetDisabled = staticCompositionLocalOf { false }

/** FieldSet: Nova gap-4; legacy presets keep gap-6. [disabled] matches HTML `fieldset disabled`. */
@Composable
fun FieldSet(
    modifier: Modifier = Modifier,
    style: Style = Style,
    disabled: Boolean = false,
    content: @Composable ColumnScope.() -> Unit,
) {
    val styleState = remember { MutableStyleState(MutableInteractionSource()) }
    val nova = currentTheme.activePreset == StylePreset.Nova
    CompositionLocalProvider(LocalFieldSetDisabled provides (disabled || LocalFieldSetDisabled.current)) {
        if (nova) {
            FieldSetLayout(
                modifier = modifier.fillMaxWidth().styleable(styleState, style),
                spacing = 16.dp,
                content = content,
            )
        } else {
            Column(
                modifier = modifier.fillMaxWidth().styleable(styleState, style),
                verticalArrangement = Arrangement.spacedBy(twSpacing.xl2),
                content = content,
            )
        }
    }
}

/** Convenience: [legend] as [FieldLegend], then [content]. */
@Composable
fun FieldSet(
    legend: String,
    modifier: Modifier = Modifier,
    style: Style = Style,
    disabled: Boolean = false,
    legendVariant: FieldLegendVariant = FieldLegendVariant.Legend,
    content: @Composable ColumnScope.() -> Unit,
) {
    if (currentTheme.activePreset == StylePreset.Nova && legendVariant == FieldLegendVariant.Label) {
        // HTML legend sits outside the fieldset body's flex gap.
        val state = remember { MutableStyleState(MutableInteractionSource()) }
        CompositionLocalProvider(LocalFieldSetDisabled provides (disabled || LocalFieldSetDisabled.current)) {
            Column(modifier.fillMaxWidth().styleable(state, style)) {
                FieldLegend(legend, variant = legendVariant)
                FieldSet(content = content)
            }
        }
    } else {
        FieldSet(modifier = modifier, style = style, disabled = disabled) {
            FieldLegend(legend, variant = legendVariant)
            content()
        }
    }
}

enum class FieldLegendVariant {
    /** Real `data-variant=legend` — `text-base`. */
    Legend,

    /** Real `data-variant=label` — `text-sm`. */
    Label,
}

/** Real shadcn `FieldLegend` — section title inside a [FieldSet]. */
@Composable
fun FieldLegend(
    text: String,
    modifier: Modifier = Modifier,
    variant: FieldLegendVariant = FieldLegendVariant.Legend,
    style: Style = Style,
) {
    val styleState = remember { MutableStyleState(MutableInteractionSource()) }
    val nova = currentTheme.activePreset == StylePreset.Nova
    Text(
        modifier =
            modifier.semantics {
                heading()
            }.then(
                if (nova) {
                    Modifier.fieldSetChild(
                        kind =
                            if (variant == FieldLegendVariant.Legend) {
                                FieldSetChildKind.Legend
                            } else {
                                FieldSetChildKind.LegendLabel
                            },
                    )
                } else {
                    Modifier
                },
            ).padding(
                bottom = if (nova) 6.dp else twSpacing.md,
            ).styleable(styleState, style),
        variant =
            when (variant) {
                FieldLegendVariant.Legend -> TextVariant.BodyLarge
                FieldLegendVariant.Label -> TextVariant.BodySmall
            },
        textStyle =
            if (currentTheme.activePreset == StylePreset.Nova) {
                currentTheme.typography.bodyMedium.copy(
                    fontWeight = FontWeight.Medium,
                    fontSize = if (variant == FieldLegendVariant.Legend) 16.sp else 14.sp,
                    lineHeight = if (variant == FieldLegendVariant.Legend) 24.sp else 20.sp,
                    lineHeightStyle =
                        LineHeightStyle(
                            LineHeightStyle.Alignment.Center,
                            LineHeightStyle.Trim.None,
                            LineHeightStyle.Mode.Tight,
                        ),
                )
            } else {
                TextStyle(fontWeight = FontWeight.Medium)
            },
    ) { text }
}

@Composable
@JvmName("FieldLegendLambda")
@OverloadResolutionByLambdaReturnType
fun FieldLegend(
    modifier: Modifier = Modifier,
    variant: FieldLegendVariant = FieldLegendVariant.Legend,
    style: Style = Style,
    text: () -> String,
) {
    FieldLegend(text = text(), modifier = modifier, variant = variant, style = style)
}

/**
 * FieldGroup: Nova gap-5 (20dp); legacy gap-7 (28dp).
 *
 * [dense] mirrors real shadcn's `data-slot="checkbox-group"` marker: it selects the
 * `gap-3` (12dp) group spacing and lets the parent [FieldSet] use its 12dp gap variant.
 */
@Composable
fun FieldGroup(
    modifier: Modifier = Modifier,
    style: Style = Style,
    spacing: Dp? = null,
    dense: Boolean = false,
    content: @Composable ColumnScope.() -> Unit,
) {
    val styleState = remember { MutableStyleState(MutableInteractionSource()) }
    val nova = currentTheme.activePreset == StylePreset.Nova
    val effectiveSpacing = spacing ?: if (nova) (if (dense) 12.dp else 20.dp) else 7.spacing.dp
    Column(
        modifier =
            modifier
                .then(
                    if (nova) {
                        Modifier.fieldSetChild(
                            kind = FieldSetChildKind.Group,
                            dense = dense,
                        )
                    } else {
                        Modifier
                    },
                )
                .fillMaxWidth()
                .styleable(styleState, style),
        verticalArrangement = Arrangement.spacedBy(effectiveSpacing),
        content = content,
    )
}

/**
 * Real shadcn `Field` orientation. [Responsive] is vertical below the field-group
 * `@md` container width and horizontal at/above it (approximated with [BoxWithConstraints]
 * + [TwBreakpoint.md]).
 */
enum class FieldOrientation {
    Vertical,
    Horizontal,
    Responsive,
}

@Immutable
class FieldState(
    val orientation: FieldOrientation,
    val invalid: Boolean,
)

internal val LocalField = staticCompositionLocalOf<FieldState?> { null }

/** Reads the nearest [Field]. Matches shadcn `useFieldContext` / `data-invalid`. */
@Composable
fun fieldState(): FieldState =
    checkNotNull(LocalField.current) {
        "fieldState() must be used inside Field { … }"
    }

/**
 * One labeled control + description/error, matching real shadcn's `Field`
 * (Nova gap-2, optional `data-invalid` → destructive content color).
 */
@Composable
fun Field(
    modifier: Modifier = Modifier,
    orientation: FieldOrientation = FieldOrientation.Vertical,
    invalid: Boolean = false,
    disabled: Boolean = false,
    style: Style = Style,
    content: @Composable () -> Unit,
) {
    val styleState = remember { MutableStyleState(MutableInteractionSource()) }
    val destructive = currentTheme.colors.destructive
    val fieldState = FieldState(orientation = orientation, invalid = invalid)
    val resolvedDisabled = disabled || LocalFieldSetDisabled.current
    val body: @Composable () -> Unit = {
        CompositionLocalProvider(
            LocalField provides fieldState,
            LocalFieldSetDisabled provides resolvedDisabled,
        ) {
            if (invalid) {
                CompositionLocalProvider(LocalContentColor provides destructive) {
                    content()
                }
            } else {
                content()
            }
        }
    }

    when (orientation) {
        FieldOrientation.Vertical -> {
            Column(
                modifier = modifier.fillMaxWidth().styleable(styleState, style),
                // Nova gap-2 = 8; legacy spacing remains unchanged.
                verticalArrangement =
                    Arrangement.spacedBy(
                        if (currentTheme.activePreset == StylePreset.Nova) 8.dp else twSpacing.md,
                    ),
            ) { body() }
        }

        FieldOrientation.Horizontal -> {
            if (currentTheme.activePreset == StylePreset.Nova) {
                FieldHorizontalLayout(modifier.fillMaxWidth().styleable(styleState, style)) { body() }
            } else {
                Row(
                    modifier = modifier.fillMaxWidth().styleable(styleState, style),
                    horizontalArrangement =
                        Arrangement.spacedBy(
                            if (currentTheme.activePreset == StylePreset.Nova) 8.dp else twSpacing.md,
                        ),
                    verticalAlignment = Alignment.CenterVertically,
                ) { body() }
            }
        }

        FieldOrientation.Responsive -> {
            BoxWithConstraints(modifier = modifier.fillMaxWidth()) {
                val horizontal =
                    maxWidth >=
                        if (currentTheme.activePreset == StylePreset.Nova) {
                            448.dp
                        } else {
                            TwBreakpoint.md
                        }
                if (horizontal) {
                    if (currentTheme.activePreset == StylePreset.Nova) {
                        FieldHorizontalLayout(Modifier.fillMaxWidth().styleable(styleState, style)) { body() }
                    } else {
                        Row(
                            modifier = Modifier.fillMaxWidth().styleable(styleState, style),
                            horizontalArrangement =
                                Arrangement.spacedBy(
                                    if (currentTheme.activePreset == StylePreset.Nova) 8.dp else twSpacing.md,
                                ),
                            verticalAlignment = Alignment.CenterVertically,
                        ) { body() }
                    }
                } else {
                    Column(
                        modifier = Modifier.fillMaxWidth().styleable(styleState, style),
                        verticalArrangement =
                            Arrangement.spacedBy(
                                if (currentTheme.activePreset == StylePreset.Nova) 8.dp else twSpacing.md,
                            ),
                    ) { body() }
                }
            }
        }
    }
}

/**
 * Convenience: [label] as [FieldLabel], optional [description]/[error], and [content]
 * as the control. Matches a typical shadcn `Field` without spelling out the inner slots.
 */
@Composable
fun Field(
    label: String,
    modifier: Modifier = Modifier,
    description: String? = null,
    error: String? = null,
    required: Boolean = false,
    disabled: Boolean = false,
    orientation: FieldOrientation = FieldOrientation.Vertical,
    invalid: Boolean = error != null,
    style: Style = Style,
    content: @Composable () -> Unit,
) {
    Field(
        modifier = modifier,
        orientation = orientation,
        invalid = invalid,
        disabled = disabled,
        style = style,
    ) {
        FieldLabel(label, required = required, disabled = disabled)
        content()
        if (description != null) {
            FieldDescription(description)
        }
        if (error != null) {
            FieldError(error)
        }
    }
}

/**
 * Real shadcn `FieldContent` — stacks control + description/error with `gap-1.5`
 * (used inside horizontal fields so the label sits beside a vertical stack).
 */
@Composable
fun FieldContent(
    modifier: Modifier = Modifier,
    style: Style = Style,
    content: @Composable ColumnScope.() -> Unit,
) {
    val styleState = remember { MutableStyleState(MutableInteractionSource()) }
    Column(
        modifier =
            modifier.then(
                if (LocalField.current != null) Modifier.fieldChild(FieldChild.Content) else Modifier,
            ).styleable(styleState, style),
        verticalArrangement =
            Arrangement.spacedBy(
                if (currentTheme.activePreset == StylePreset.Nova) 2.dp else 1.5.spacing.dp,
            ),
        content = content,
    )
}

/** Real shadcn `FieldLabel` — thin alias over [Label]. */
@Composable
fun FieldLabel(
    text: String,
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
    )
}

@Composable
@JvmName("FieldLabelLambda")
@OverloadResolutionByLambdaReturnType
fun FieldLabel(
    modifier: Modifier = Modifier,
    required: Boolean = false,
    disabled: Boolean = false,
    style: Style = Style,
    text: () -> String,
) {
    FieldLabel(text = text(), modifier = modifier, required = required, disabled = disabled, style = style)
}

/** Real shadcn `FieldTitle` — non-`<label>` title (`text-sm font-medium`). */
@Composable
fun FieldTitle(
    text: String,
    modifier: Modifier = Modifier,
    style: Style = Style,
) {
    val styleState = remember { MutableStyleState(MutableInteractionSource()) }
    val nova = currentTheme.activePreset == StylePreset.Nova
    val disabled = nova && LocalFieldSetDisabled.current
    Text(
        modifier =
            modifier.then(
                if (nova) {
                    Modifier.alpha(if (disabled) 0.5f else 1f)
                } else {
                    Modifier
                },
            ).then(
                if (nova) {
                    Modifier
                } else {
                    Modifier.semantics {
                        heading()
                    }
                },
            ).styleable(styleState, style),
        variant = TextVariant.BodySmall,
        textStyle =
            if (nova) {
                currentTheme.typography.bodyMedium.copy(
                    fontWeight = FontWeight.Medium,
                    lineHeight = 20.sp,
                    lineHeightStyle =
                        LineHeightStyle(
                            LineHeightStyle.Alignment.Center,
                            LineHeightStyle.Trim.None,
                            LineHeightStyle.Mode.Tight,
                        ),
                )
            } else {
                TextStyle(fontWeight = FontWeight.Medium)
            },
    ) { text }
}

@Composable
@JvmName("FieldTitleLambda")
@OverloadResolutionByLambdaReturnType
fun FieldTitle(
    modifier: Modifier = Modifier,
    style: Style = Style,
    text: () -> String,
) {
    FieldTitle(text = text(), modifier = modifier, style = style)
}

/** Helper text under a control, matching real shadcn's `FieldDescription`. */
@Composable
fun FieldDescription(
    text: String,
    modifier: Modifier = Modifier,
    style: Style = Style,
) {
    val styleState = remember { MutableStyleState(MutableInteractionSource()) }
    val nova = currentTheme.activePreset == StylePreset.Nova
    Text(
        variant = TextVariant.BodySmall,
        textStyle =
            if (nova) {
                currentTheme.typography.bodyMedium.merge(TextColors.Muted).copy(
                    lineHeight = 21.sp,
                    lineHeightStyle =
                        LineHeightStyle(
                            LineHeightStyle.Alignment.Center,
                            LineHeightStyle.Trim.None,
                            LineHeightStyle.Mode.Tight,
                        ),
                )
            } else {
                TextColors.Muted
            },
        style = style,
        modifier =
            modifier.then(
                if (nova) Modifier.fieldSetChild(kind = FieldSetChildKind.Description) else Modifier,
            ).styleable(styleState, style),
    ) { text }
}

@Composable
@JvmName("FieldDescriptionLambda")
@OverloadResolutionByLambdaReturnType
fun FieldDescription(
    modifier: Modifier = Modifier,
    style: Style = Style,
    text: () -> String,
) {
    FieldDescription(text = text(), modifier = modifier, style = style)
}

/**
 * Validation message matching real shadcn `FieldError` (`role=alert`, destructive).
 *
 * Pass [text] and/or [errors]. Renders nothing when both are empty (same as real
 * returning `null`). Multiple [errors] become a bulleted list.
 */
@Composable
fun FieldError(
    text: String? = null,
    modifier: Modifier = Modifier,
    errors: List<String> = emptyList(),
    style: Style = Style,
) {
    val unique =
        (listOfNotNull(text?.takeIf { it.isNotBlank() }) + errors.map { it.trim() }.filter { it.isNotEmpty() })
            .distinct()
    if (unique.isEmpty()) return

    val styleState = remember { MutableStyleState(MutableInteractionSource()) }
    val destructive = currentTheme.colors.destructive
    val message =
        if (unique.size == 1) {
            unique.first()
        } else {
            unique.joinToString(separator = "\n") { "• $it" }
        }
    Text(
        variant = TextVariant.BodySmall,
        textStyle =
            if (currentTheme.activePreset == StylePreset.Nova) {
                currentTheme.typography.bodyMedium.copy(
                    color = destructive,
                )
            } else {
                TextStyle(color = destructive)
            },
        style = style,
        modifier =
            modifier
                .semantics { liveRegion = LiveRegionMode.Assertive }
                .styleable(styleState, style),
    ) { message }
}

@Composable
@JvmName("FieldErrorLambda")
@OverloadResolutionByLambdaReturnType
fun FieldError(
    modifier: Modifier = Modifier,
    errors: List<String> = emptyList(),
    style: Style = Style,
    text: () -> String,
) {
    FieldError(text = text(), modifier = modifier, errors = errors, style = style)
}

/** Horizontal divider between fields, with optional centered label (real `children`). */
@Composable
fun FieldSeparator(
    modifier: Modifier = Modifier,
    label: String? = null,
    style: Style = Style,
) {
    val styleState = remember { MutableStyleState(MutableInteractionSource()) }
    if (currentTheme.activePreset != StylePreset.Nova) {
        if (label == null) {
            Separator(modifier = modifier, style = style)
        } else {
            Row(
                modifier = modifier.fillMaxWidth().styleable(styleState, style),
                horizontalArrangement = Arrangement.spacedBy(twSpacing.sm),
                verticalAlignment = Alignment.CenterVertically,
            ) {
                SeparatorLine()
                Text(variant = TextVariant.LabelSmall, textStyle = TextColors.Muted) { label }
                SeparatorLine()
            }
        }
        return
    }
    // Nova `.cn-field-separator` — `-my-2 h-5 text-sm`, with the 1dp line centered
    // behind an optional background-masked label (`px-2`).
    Box(
        modifier =
            modifier.fillMaxWidth().height(20.dp).fieldSeparatorOverlap()
                .styleable(styleState, style),
        contentAlignment = Alignment.Center,
    ) {
        Box(Modifier.fillMaxWidth().height(1.dp).background(currentTheme.colors.border))
        if (label != null) {
            Text(
                modifier =
                    Modifier.background(currentTheme.colors.background).padding(horizontal = 8.dp),
                textStyle =
                    currentTheme.typography.bodyMedium.copy(
                        color = currentTheme.colors.mutedForeground,
                        lineHeight = 20.sp,
                    ),
            ) { label }
        }
    }
}

/** CSS `-my-2`: remove the occupied height as well as moving paint. */
internal fun Modifier.fieldSeparatorOverlap(): Modifier =
    layout { measurable, constraints ->
        val child = measurable.measure(constraints)
        val overlap = 8.dp.roundToPx()
        layout(child.width, (child.height - overlap * 2).coerceAtLeast(0)) {
            child.placeRelative(0, -overlap)
        }
    }

@Composable
private fun RowScope.SeparatorLine() {
    val styleState = remember { MutableStyleState(MutableInteractionSource()) }
    Box(
        modifier =
            Modifier
                .weight(1f)
                .styleable(styleState, rememberFieldSeparatorLineStyle()),
    )
}
