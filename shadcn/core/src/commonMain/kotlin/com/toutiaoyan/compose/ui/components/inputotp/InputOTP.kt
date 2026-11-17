@file:OptIn(ExperimentalFoundationStyleApi::class)

package com.toutiaoyan.compose.ui.components.inputotp

import androidx.compose.animation.animateColor
import androidx.compose.animation.core.CubicBezierEasing
import androidx.compose.animation.core.animateDp
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.keyframes
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.updateTransition
import androidx.compose.animation.core.tween
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.offset
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.zIndex
import androidx.compose.ui.text.TextRange
import androidx.compose.ui.text.input.TextFieldValue
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.unit.sp
import androidx.compose.ui.geometry.Rect
import androidx.compose.ui.layout.onGloballyPositioned
import androidx.compose.ui.layout.boundsInRoot
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.focus.focusRequester
import androidx.compose.ui.input.pointer.PointerEventPass
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.foundation.gestures.awaitEachGesture
import androidx.compose.foundation.gestures.awaitFirstDown
import androidx.compose.foundation.gestures.waitForUpOrCancellation
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import com.toutiaoyan.compose.ui.components.buttongroup.GroupChildRegistry
import com.toutiaoyan.compose.ui.components.buttongroup.groupItemCorners
import com.toutiaoyan.compose.ui.components.buttongroup.ButtonGroupOrientation
import com.toutiaoyan.compose.ui.components.buttongroup.register
import com.toutiaoyan.compose.ui.components.icon.Icon
import com.toutiaoyan.compose.ui.icons.NovaMinus
import com.toutiaoyan.compose.ui.tokens.StylePreset
import androidx.compose.ui.graphics.compositeOver
import androidx.compose.foundation.interaction.collectIsFocusedAsState
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.style.ExperimentalFoundationStyleApi
import androidx.compose.foundation.style.MutableStyleState
import androidx.compose.foundation.style.Style
import androidx.compose.foundation.style.styleable
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.SideEffect
import androidx.compose.runtime.mutableStateMapOf
import androidx.compose.ui.text.TextStyle
import com.toutiaoyan.compose.ui.styles.cssStyleable
import androidx.compose.runtime.Immutable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberUpdatedState
import androidx.compose.runtime.staticCompositionLocalOf
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.graphics.shadow.Shadow
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.semantics.disabled
import androidx.compose.ui.semantics.error
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import com.toutiaoyan.compose.tailwind.core.twRadius
import com.toutiaoyan.compose.tailwind.core.twSpacing
import com.toutiaoyan.compose.ui.components.field.LocalFieldSetDisabled
import com.toutiaoyan.compose.ui.components.input.NovaInputColorDurationMillis
import com.toutiaoyan.compose.ui.components.separator.Separator
import com.toutiaoyan.compose.ui.components.separator.SeparatorOrientation
import com.toutiaoyan.compose.ui.components.text.Text
import com.toutiaoyan.compose.ui.components.text.TextVariant
import com.toutiaoyan.compose.ui.styles.focusRingShadow
import com.toutiaoyan.compose.ui.theme.currentTheme

enum class InputOTPType { Text, Password }

@Immutable
internal class InputOTPController(
    val value: String,
    val maxLength: Int,
    val isFocused: Boolean,
    val masked: Boolean,
    val invalid: Boolean,
    val activeIndex: Int,
    val slots: MutableMap<Int, Rect>,
)

private val LocalOTPGroupRegistry = staticCompositionLocalOf<GroupChildRegistry?> { null }
private val LocalInputOTP = staticCompositionLocalOf<InputOTPController?> { null }

@Composable
private fun inputOTPController(): InputOTPController =
    checkNotNull(LocalInputOTP.current) {
        "InputOTPGroup / InputOTPSlot must be used inside InputOTP { … }"
    }

/** Value of the nearest [InputOTP]. Matches the input-otp context. */
class InputOTPState(
    val value: String,
    val maxLength: Int,
    val isFocused: Boolean,
    val masked: Boolean,
)

/** Reads the nearest [InputOTP] state. */
@Composable
fun inputOTPState(): InputOTPState {
    val ctrl = inputOTPController()
    return InputOTPState(
        value = ctrl.value,
        maxLength = ctrl.maxLength,
        isFocused = ctrl.isFocused,
        masked = ctrl.masked,
    )
}

class InputOTPScope internal constructor()

class InputOTPGroupScope internal constructor(
    internal val invalidSlots: MutableMap<Any, Boolean>,
)

/**
 * A one-time-passcode input matching real shadcn/ui's `input-otp.tsx`:
 *
 * ```
 * InputOTP
 * └── InputOTPGroup
 *     ├── InputOTPSlot
 *     ├── InputOTPSeparator
 *     └── InputOTPSlot
 * ```
 *
 * One invisible [BasicTextField] captures typing/paste/backspace; the slots are
 * visual only. Pass [length] so paste is clipped.
 *
 * Usage:
 * ```
 * var code by remember { mutableStateOf("") }
 * InputOTP(value = code, onValueChange = { code = it }, length = 6) {
 *     InputOTPGroup {
 *         InputOTPSlot(0); InputOTPSlot(1); InputOTPSlot(2)
 *         InputOTPSeparator()
 *         InputOTPSlot(3); InputOTPSlot(4); InputOTPSlot(5)
 *     }
 * }
 * ```
 *
 * The [content] slot is optional: omit it for a flat row of [length] slots
 * (no separator). Pass [InputOTPGroup] / [InputOTPSlot] / [InputOTPSeparator]
 * for the full shadcn composition.
 *
 * [accept] matches shadcn's `pattern` — default digits only. Pass
 * `{ it.isLetterOrDigit() }` or [pattern] `Regex("[A-Za-z0-9]")` for alphanumeric
 * codes. [masked] shows a bullet per filled slot (password-style), matching
 * input-otp `type="password"`. Official [type] [InputOTPType.Password] is the
 * same as [masked] `true`. [maxLength] is the official name for [length].
 * [onComplete] fires once the value reaches [length] (paste included).
 */
@Composable
fun InputOTP(
    value: String,
    onValueChange: (String) -> Unit,
    length: Int,
    modifier: Modifier = Modifier,
    maxLength: Int? = null,
    enabled: Boolean = true,
    disabled: Boolean = false,
    accept: (Char) -> Boolean = { it.isDigit() },
    pattern: Regex? = null,
    keyboardType: KeyboardType = KeyboardType.NumberPassword,
    masked: Boolean = false,
    readOnly: Boolean = false,
    invalid: Boolean = false,
    contentDescription: String? = null,
    type: InputOTPType? = null,
    onComplete: ((String) -> Unit)? = null,
    style: Style = Style,
    content: (@Composable InputOTPScope.() -> Unit)? = null,
) {
    val nova = currentTheme.activePreset == StylePreset.Nova
    var selection by remember { mutableStateOf(TextRange(value.length)) }
    val slots = remember { mutableMapOf<Int, Rect>() }
    val rootBounds = remember { mutableStateOf(Rect.Zero) }
    val focusRequester = remember { FocusRequester() }
    val interactionSource = remember { MutableInteractionSource() }
    val resolvedEnabled = enabled && !disabled && !LocalFieldSetDisabled.current
    val resolvedLength = maxLength ?: length
    require(resolvedLength > 0) { "InputOTP length must be positive" }
    val resolvedAccept: (Char) -> Boolean =
        if (pattern != null) {
            { char -> pattern.matches(char.toString()) }
        } else {
            accept
        }
    val resolvedMasked =
        when (type) {
            InputOTPType.Password -> true
            InputOTPType.Text -> false
            null -> masked
        }
    val isFocused by interactionSource.collectIsFocusedAsState()
    val onCompleteUpdated = rememberUpdatedState(onComplete)
    LaunchedEffect(value, resolvedLength) {
        if (resolvedLength > 0 && value.length >= resolvedLength) {
            onCompleteUpdated.value?.invoke(value)
        }
    }
    val styleState = remember { MutableStyleState(interactionSource) }
    val controller =
        InputOTPController(
            value = value,
            maxLength = resolvedLength,
            isFocused = isFocused,
            masked = resolvedMasked,
            invalid = invalid,
            activeIndex = selection.start.coerceIn(0, minOf(value.length, resolvedLength - 1)),
            slots = slots,
        )

    CompositionLocalProvider(LocalInputOTP provides controller) {
        Box(
            modifier =
                modifier
                    .onGloballyPositioned { rootBounds.value = it.boundsInRoot() }
                    .then(
                        if (nova) {
                            Modifier.alpha(
                                if (resolvedEnabled) 1f else 0.5f,
                            ).pointerInput(resolvedEnabled, value, readOnly) {
                                if (!resolvedEnabled) return@pointerInput
                                awaitEachGesture {
                                    val down =
                                        awaitFirstDown(requireUnconsumed = false, pass = PointerEventPass.Initial)
                                    val absolute = down.position + rootBounds.value.topLeft
                                    val index =
                                        slots.entries
                                            .minByOrNull { kotlin.math.abs(it.value.center.x - absolute.x) }
                                            ?.key
                                            ?: 0
                                    focusRequester.requestFocus()
                                    val cursor = index.coerceAtMost(value.length)
                                    selection = TextRange(cursor, (cursor + 1).coerceAtMost(value.length))
                                    down.consume()
                                    waitForUpOrCancellation()?.consume()
                                }
                            }
                        } else {
                            Modifier
                        },
                    )
                    .styleable(styleState, style),
        ) {
            val visualContent: @Composable () -> Unit = {
                if (content != null) {
                    InputOTPScope().content()
                } else {
                    InputOTPScope().InputOTPGroup {
                        repeat(resolvedLength) { InputOTPSlot(it) }
                    }
                }
            }
            if (nova) {
                Row(
                    horizontalArrangement = Arrangement.spacedBy(8.dp),
                    verticalAlignment = Alignment.CenterVertically,
                ) {
                    visualContent()
                }
            } else {
                visualContent()
            }
            BasicTextField(
                value =
                    TextFieldValue(
                        value,
                        TextRange(selection.start.coerceIn(0, value.length), selection.end.coerceIn(0, value.length)),
                    ),
                onValueChange = { next ->
                    val clean = next.text.filter(resolvedAccept).take(resolvedLength)

                    fun mapped(offset: Int) = next.text.take(offset).count(resolvedAccept).coerceAtMost(clean.length)
                    selection = TextRange(mapped(next.selection.start), mapped(next.selection.end))
                    onValueChange(clean)
                },
                enabled = resolvedEnabled,
                readOnly = readOnly,
                modifier =
                    Modifier.matchParentSize().focusRequester(focusRequester).semantics {
                        contentDescription?.let { this.contentDescription = it }
                        if (!resolvedEnabled) disabled()
                        if (invalid) error("Invalid one-time password")
                    },
                interactionSource = interactionSource,
                singleLine = true,
                textStyle = currentTheme.typography.bodyMedium.copy(color = Color.Transparent),
                cursorBrush = SolidColor(Color.Transparent),
                visualTransformation =
                    if (resolvedMasked) {
                        PasswordVisualTransformation()
                    } else {
                        VisualTransformation.None
                    },
                keyboardOptions = KeyboardOptions(keyboardType = keyboardType),
            )
        }
    }
}

/** Row of slots. Matches shadcn's `InputOTPGroup`. */
@Composable
fun InputOTPScope.InputOTPGroup(
    modifier: Modifier = Modifier,
    content: @Composable InputOTPGroupScope.() -> Unit,
) {
    val controller = inputOTPController()
    val theme = currentTheme
    val invalidSlots = remember { mutableStateMapOf<Any, Boolean>() }
    val radius = twRadius.lg
    val groupInvalid = controller.invalid || invalidSlots.values.any { it }
    val groupStyle =
        Style {
            shape(RoundedCornerShape(radius))
            if (theme.activePreset == StylePreset.Nova && groupInvalid) {
                dropShadow(
                    Shadow(
                        radius = 0.dp,
                        spread = 3.dp,
                        color = theme.colors.destructive.copy(alpha = if (theme.isDark) 0.4f else 0.2f),
                    ),
                )
            }
        }
    val nova = currentTheme.activePreset == StylePreset.Nova
    val registry = remember { GroupChildRegistry() }
    CompositionLocalProvider(LocalOTPGroupRegistry provides registry) {
        Row(
            modifier = modifier.height(if (nova) 32.dp else 36.dp).cssStyleable(null, groupStyle),
            horizontalArrangement = Arrangement.spacedBy(if (nova) (-1).dp else twSpacing.sm),
            verticalAlignment = Alignment.CenterVertically,
        ) {
            InputOTPGroupScope(invalidSlots).content()
        }
    }
}

/** One digit box. [index] is 0-based. Matches shadcn's `InputOTPSlot`. */
@Composable
fun InputOTPGroupScope.InputOTPSlot(
    index: Int,
    modifier: Modifier = Modifier,
    invalid: Boolean = false,
) {
    InputOTPSlotContent(index, modifier, invalid, TextStyle.Default)
}

/** Slot typography override, for recipes such as the larger verification form. */
@Composable
fun InputOTPGroupScope.InputOTPSlot(
    index: Int,
    textStyle: TextStyle,
    modifier: Modifier = Modifier,
    invalid: Boolean = false,
) {
    InputOTPSlotContent(index, modifier, invalid, textStyle)
}

@Composable
private fun InputOTPGroupScope.InputOTPSlotContent(
    index: Int,
    modifier: Modifier,
    invalid: Boolean,
    textStyle: TextStyle,
) {
    val ctrl = inputOTPController()
    val resolvedInvalid = invalid || ctrl.invalid
    val slotKey = remember { Any() }
    DisposableEffect(invalidSlots, slotKey) {
        onDispose { invalidSlots.remove(slotKey) }
    }
    SideEffect { invalidSlots[slotKey] = resolvedInvalid }
    val char = ctrl.value.getOrNull(index)
    val nova = currentTheme.activePreset == StylePreset.Nova
    val (position, count) = checkNotNull(LocalOTPGroupRegistry.current).register()
    val corners = groupItemCorners(position, count, ButtonGroupOrientation.Horizontal, twRadius.lg)
    val shape =
        if (nova) {
            RoundedCornerShape(
                corners.topStart,
                corners.topEnd,
                corners.bottomEnd,
                corners.bottomStart,
            )
        } else {
            RoundedCornerShape(twRadius.md)
        }
    val isActive = ctrl.isFocused && index == if (nova) ctrl.activeIndex else ctrl.value.length
    OtpSlot(
        char = char,
        isActive = isActive,
        masked = ctrl.masked,
        modifier =
            modifier.onGloballyPositioned {
                ctrl.slots[index] = it.boundsInRoot()
            },
        shape = shape,
        overlap = nova && position > 0,
        invalid = resolvedInvalid,
        textStyle = textStyle,
    )
}

/** Vertical divider between slot groups. Matches shadcn's `InputOTPSeparator`. */
@Composable
fun InputOTPGroupScope.InputOTPSeparator(
    modifier: Modifier = Modifier,
    style: Style = Style,
) {
    if (currentTheme.activePreset == StylePreset.Nova) {
        OTPMinus(modifier, style)
        return
    }
    Separator(
        modifier = modifier.fillMaxHeight(),
        orientation = SeparatorOrientation.Vertical,
        style = style,
    )
}

@OptIn(ExperimentalFoundationStyleApi::class)
@Composable
private fun OtpSlot(
    char: Char?,
    isActive: Boolean,
    masked: Boolean,
    modifier: Modifier = Modifier,
    shape: RoundedCornerShape,
    overlap: Boolean,
    invalid: Boolean = false,
    textStyle: TextStyle = TextStyle.Default,
) {
    val theme = currentTheme
    val nova = theme.activePreset == StylePreset.Nova
    val fill =
        if (nova && theme.isDark) {
            theme.colors.input.copy(alpha = theme.colors.input.alpha * 0.3f).compositeOver(theme.colors.background)
        } else {
            theme.colors.background
        }
    val border =
        when {
            invalid -> theme.colors.destructive
            isActive -> theme.colors.ring
            nova -> theme.colors.input
            else -> theme.colors.border
        }
    val focusRing = theme.focusRingShadow()
    val errorRing =
        Shadow(
            radius = 0.dp,
            spread = 3.dp,
            color = theme.colors.destructive.copy(alpha = if (theme.isDark) 0.4f else 0.2f),
        )
    val slotStyle =
        if (nova) {
            val ring = if (invalid) errorRing else focusRing
            val paint =
                animateOtpSlot(
                    OtpSlotPaint(
                        fill = fill,
                        border = border,
                        ring = if (isActive) ring.color else ring.color.copy(alpha = 0f),
                        spread = if (isActive) ring.spread else 0.dp,
                    ),
                )
            Style {
                background(paint.fill)
                borderWidth(1.dp)
                borderColor(paint.border)
                shape(shape)
                if (paint.spread > 0.dp && paint.ring.alpha > 0f) {
                    dropShadow(Shadow(radius = 0.dp, spread = paint.spread, color = paint.ring))
                }
            }
        } else {
            Style {
                background(fill)
                borderWidth(1.dp)
                borderColor(border)
                shape(shape)
                if (isActive) dropShadow(focusRing)
                if (invalid) dropShadow(errorRing)
            }
        }

    val slotTextStyle =
        if (nova) {
            theme.typography.bodyMedium.copy(
                fontFamily = theme.fontFamily,
                fontSize = 14.sp,
                lineHeight = 20.sp,
            )
        } else {
            TextStyle.Default
        }

    Box(
        modifier =
            modifier
                .size(
                    width =
                        if (nova) {
                            if (overlap) 33.dp else 32.dp
                        } else {
                            36.dp
                        },
                    height = if (nova) 32.dp else 36.dp,
                )
                .zIndex(if (isActive) 10f else 0f)
                .styleable(remember { MutableStyleState(MutableInteractionSource()) }, slotStyle),
        contentAlignment = Alignment.Center,
    ) {
        Text(
            variant = if (nova) TextVariant.BodySmall else TextVariant.TitleMedium,
            textStyle = slotTextStyle.merge(textStyle),
        ) {
            when {
                char == null -> ""
                masked -> "•"
                else -> char.toString()
            }
        }
        if (nova && isActive && char == null) {
            val opacity =
                rememberInfiniteTransition(label = "OTP caret").animateFloat(
                    initialValue = 1f,
                    targetValue = 1f,
                    animationSpec = infiniteRepeatable(OtpCaretBlink),
                    label = "opacity",
                )
            Box(
                Modifier.width(1.dp).height(16.dp)
                    .graphicsLayer { alpha = opacity.value }
                    .background(theme.colors.foreground),
            )
        }
    }
}

private data class OtpSlotPaint(
    val fill: Color,
    val border: Color,
    val ring: Color,
    val spread: Dp,
)

@Composable
private fun animateOtpSlot(target: OtpSlotPaint): OtpSlotPaint {
    val transition = updateTransition(targetState = target, label = "OTP slot")
    val fill by transition.animateColor(transitionSpec = { tween(NovaInputColorDurationMillis) }, label = "fill") {
        it.fill
    }
    val border by transition.animateColor(transitionSpec = { tween(NovaInputColorDurationMillis) }, label = "border") {
        it.border
    }
    val ring by transition.animateColor(transitionSpec = { tween(NovaInputColorDurationMillis) }, label = "ring") {
        it.ring
    }
    val spread by transition.animateDp(transitionSpec = { tween(NovaInputColorDurationMillis) }, label = "spread") {
        it.spread
    }
    return OtpSlotPaint(fill, border, ring, spread)
}

private val OtpCaretBlink =
    keyframes {
        val easeOut = CubicBezierEasing(0f, 0f, .58f, 1f)
        durationMillis = 1000
        1f at 0 using easeOut
        0f at 200
        0f at 500 using easeOut
        1f at 700
        1f at 1000
    }

/** Horizontal minus between separate OTP groups. */
@Composable
fun InputOTPScope.InputOTPSeparator(
    modifier: Modifier = Modifier,
    style: Style = Style,
) {
    OTPMinus(modifier, style)
}

@Composable
private fun OTPMinus(
    modifier: Modifier,
    style: Style,
) {
    Icon(NovaMinus, modifier = modifier.size(16.dp), style = style)
}
