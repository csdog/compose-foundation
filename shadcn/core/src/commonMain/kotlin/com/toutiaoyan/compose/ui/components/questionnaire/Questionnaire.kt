@file:OptIn(ExperimentalFoundationStyleApi::class, ExperimentalTypeInference::class)

package com.toutiaoyan.compose.ui.components.questionnaire

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.slideInVertically
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.focusable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ColumnScope
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.RowScope
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.selection.selectableGroup
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.style.ExperimentalFoundationStyleApi
import androidx.compose.foundation.style.MutableStyleState
import androidx.compose.foundation.style.Style
import androidx.compose.foundation.style.contentPadding
import androidx.compose.foundation.style.styleable
import androidx.compose.foundation.style.then
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.SideEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateMapOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberUpdatedState
import androidx.compose.runtime.key
import androidx.compose.ui.layout.Layout
import androidx.compose.ui.semantics.clearAndSetSemantics
import androidx.compose.runtime.setValue
import androidx.compose.runtime.staticCompositionLocalOf
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.focus.focusRequester
import androidx.compose.ui.focus.onFocusChanged
import androidx.compose.ui.input.key.Key
import androidx.compose.ui.input.key.KeyEvent
import androidx.compose.ui.input.key.KeyEventType
import androidx.compose.ui.input.key.isCtrlPressed
import androidx.compose.ui.input.key.isMetaPressed
import androidx.compose.ui.input.key.isAltPressed
import androidx.compose.ui.input.key.isShiftPressed
import androidx.compose.ui.input.key.key
import androidx.compose.ui.input.key.onPreviewKeyEvent
import androidx.compose.ui.input.key.type
import androidx.compose.ui.input.pointer.PointerIcon
import androidx.compose.ui.input.pointer.pointerHoverIcon
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.widthIn
import androidx.compose.ui.platform.LocalWindowInfo
import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.text.rememberTextMeasurer
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.sp
import com.toutiaoyan.compose.ui.components.icon.Icon
import com.toutiaoyan.compose.ui.icons.NovaCheck
import com.toutiaoyan.compose.ui.tokens.StylePreset
import androidx.compose.ui.semantics.LiveRegionMode
import androidx.compose.ui.semantics.ProgressBarRangeInfo
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.semantics.disabled
import androidx.compose.ui.semantics.hideFromAccessibility
import androidx.compose.ui.semantics.error
import androidx.compose.ui.semantics.heading
import androidx.compose.ui.semantics.liveRegion
import androidx.compose.ui.semantics.progressBarRangeInfo
import androidx.compose.ui.semantics.role
import androidx.compose.ui.semantics.selected
import androidx.compose.ui.semantics.toggleableState
import androidx.compose.ui.state.ToggleableState
import androidx.compose.ui.semantics.stateDescription
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.toutiaoyan.compose.tailwind.core.twSpacing
import com.toutiaoyan.compose.ui.components.button.Button
import com.toutiaoyan.compose.ui.components.button.ButtonSize
import com.toutiaoyan.compose.ui.components.button.ButtonVariant
import com.toutiaoyan.compose.ui.components.field.LocalFieldSetDisabled
import com.toutiaoyan.compose.ui.components.input.Input
import com.toutiaoyan.compose.ui.components.input.InputType
import com.toutiaoyan.compose.ui.components.kbd.Kbd
import com.toutiaoyan.compose.ui.components.text.Text
import com.toutiaoyan.compose.ui.components.text.TextColors
import com.toutiaoyan.compose.ui.components.text.TextVariant
import com.toutiaoyan.compose.ui.theme.currentTheme
import com.toutiaoyan.compose.ui.styles.cssStyleable
import com.toutiaoyan.compose.ui.utils.keyboardInputMode
import kotlin.experimental.ExperimentalTypeInference
import kotlin.jvm.JvmName

/**
 * Multi-step questionnaire matching real shadcn/ui's `questionnaire.tsx`:
 *
 * ```
 * Questionnaire
 * ├── QuestionnaireProgress
 * ├── QuestionnaireItem
 * │   ├── QuestionnaireTitle
 * │   ├── QuestionnaireDescription
 * │   ├── QuestionnaireChoices
 * │   │   ├── QuestionnaireChoice
 * │   │   │   ├── QuestionnaireChoiceInput
 * │   │   │   ├── QuestionnaireChoiceLabel
 * │   │   │   ├── QuestionnaireChoiceDescription
 * │   │   │   └── QuestionnaireChoiceShortcut
 * │   │   └── QuestionnaireInput
 * │   └── QuestionnaireError
 * └── QuestionnairePrevious / Skip / Next / Submit / Reset
 *     (QuestionnaireActions is layout-only; nav also works on Root)
 * ```
 *
 * One item is visible at a time. [value] / [onValueChange] / [defaultValue] control
 * the active item name (Radix-style). [defaultItem] is the official alias of [defaultValue].
 * [shortcuts] assigns A–Z or 1–9 to enabled
 * choices (does not auto-advance). [items] seeds order, progress, and shortcut
 * order (official Root `items`). [onSubmit] receives each item's answers
 * (selected choice values plus a non-blank freeform input, if any).
 * [disabled] is the official Root `disabled`: choices, freeform input, and
 * navigation are inert; shortcuts are ignored. ArrowLeft / ArrowRight move
 * between items when focus is outside a choice or text field; Ctrl/Cmd+Enter
 * validates and continues from anywhere. ArrowUp / ArrowDown move between
 * answers (radios also select).
 *
 * Usage:
 * ```
 * Questionnaire(onSubmit = { answers -> }) {
 *     QuestionnaireProgress()
 *     QuestionnaireItem("direction", required = true) {
 *         QuestionnaireTitle("What should we prototype next?")
 *         QuestionnaireChoices {
 *             QuestionnaireChoice("delegation", "Delegation")
 *             QuestionnaireChoice("questions", "Question prompts")
 *         }
 *         QuestionnaireError()
 *     }
 *     QuestionnaireActions {
 *         QuestionnairePrevious()
 *         QuestionnaireNext()
 *         QuestionnaireSubmit()
 *     }
 * }
 * ```
 */
@Composable
fun Questionnaire(
    modifier: Modifier = Modifier,
    value: String? = null,
    onValueChange: ((String) -> Unit)? = null,
    defaultValue: String? = null,
    defaultItem: String? = null,
    item: String? = null,
    onItemChange: ((String) -> Unit)? = null,
    onSubmit: (Map<String, List<String>>) -> Unit = {},
    items: List<QuestionnaireItemDefinition> = emptyList(),
    shortcuts: QuestionnaireShortcuts = QuestionnaireShortcuts.None,
    animate: Boolean = false,
    disabled: Boolean = false,
    style: Style = Style,
    content: @Composable QuestionnaireScope.() -> Unit,
) {
    val ctrl =
        remember {
            QuestionnaireController(
                initialValue = defaultItem ?: defaultValue,
                onValueChange = onValueChange,
                onSubmit = onSubmit,
            ).also { it.applyItems(items) }
        }
    val resolvedDisabled = disabled || LocalFieldSetDisabled.current
    SideEffect {
        ctrl.applyItems(items)
        val previousActive = ctrl.activeName
        ctrl.externalValue = value ?: item
        if (ctrl.externalValue != null && ctrl.activeName != previousActive && ctrl.pendingAnswerFocus == null) {
            ctrl.pendingItemFocus = ctrl.activeName
        }
        ctrl.onValueChange = onValueChange ?: onItemChange
        ctrl.onSubmit = onSubmit
        ctrl.shortcuts = shortcuts
        ctrl.animate = animate
        ctrl.disabled = resolvedDisabled
        val active = ctrl.activeName
        if (active != null && ctrl.disabledByName[active] == true && value == null) {
            ctrl.enabledNames().firstOrNull()?.let(ctrl::goTo)
        }
    }
    val styleState = remember { MutableStyleState(MutableInteractionSource()) }
    CompositionLocalProvider(
        LocalQuestionnaireController provides ctrl,
        LocalFieldSetDisabled provides resolvedDisabled,
    ) {
        Box {
            Column(
                modifier =
                    modifier
                        .fillMaxWidth()
                        .focusable()
                        .keyboardInputMode()
                        .onPreviewKeyEvent { handleQuestionnaireKey(ctrl, it) }
                        .semantics { if (resolvedDisabled) this.disabled() }
                        .styleable(styleState, style),
                verticalArrangement = Arrangement.spacedBy(twSpacing.lg),
            ) {
                QuestionnaireScope().content()
            }
            Layout(
                modifier = Modifier.clearAndSetSemantics { },
                content = {
                    ctrl.hiddenContents.forEach { (name, hiddenContent) ->
                        if (ctrl.activeName != name || ctrl.disabledByName[name] == true) {
                            key(name) { hiddenContent() }
                        }
                    }
                },
            ) { _, _ -> layout(0, 0) { } }
        }
    }
}

/** Official `shortcuts` — letters are A–Z, numbers are 1–9, in choice order (disabled skipped). */
enum class QuestionnaireShortcuts { None, Letters, Numbers }

/** Official Root `items` choice row. */
class QuestionnaireChoiceDefinition(
    val value: String,
    val label: String = value,
    val description: String? = null,
    val disabled: Boolean = false,
)

/** Official Root `items` freeform field. */
class QuestionnaireInputDefinition(
    val label: String? = null,
    val placeholder: String? = null,
)

/** Official Root `items` collection entry — order, progress, shortcuts. */
class QuestionnaireItemDefinition(
    val name: String,
    val required: Boolean = false,
    val multiple: Boolean = false,
    val disabled: Boolean = false,
    val prompt: String = "",
    val description: String? = null,
    val choices: List<QuestionnaireChoiceDefinition> = emptyList(),
    val input: QuestionnaireInputDefinition? = null,
)

/** Official Questionnaire.Item `status`. */
enum class QuestionnaireItemStatus { Unanswered, Answered, Skipped }

/** Official Progress render state — [current] is 1-based. */
class QuestionnaireProgressState(
    val current: Int,
    val total: Int,
    val first: Boolean,
    val last: Boolean,
)

/** Active item / answers of the nearest [Questionnaire]. */
class QuestionnaireState(
    val value: String?,
    val answers: Map<String, List<String>>,
    val index: Int,
    val count: Int,
    val error: String?,
    val isLast: Boolean,
    val canPrevious: Boolean,
    val canSkip: Boolean,
    val status: QuestionnaireItemStatus,
    val reset: () -> Unit,
)

private val LocalQuestionnaireController =
    staticCompositionLocalOf<QuestionnaireController?> { null }

internal class QuestionnaireController(
    private val initialValue: String?,
    var onValueChange: ((String) -> Unit)?,
    var onSubmit: (Map<String, List<String>>) -> Unit,
) {
    var externalValue by mutableStateOf<String?>(null)
    private var uncontrolled by mutableStateOf(initialValue)
    val names = mutableStateListOf<String>()
    val requiredByName = mutableStateMapOf<String, Boolean>()
    val multipleByName = mutableStateMapOf<String, Boolean>()
    private val registeredMultiple = mutableMapOf<String, Boolean>()
    val requiredMessageByName = mutableStateMapOf<String, String>()
    val disabledByName = mutableStateMapOf<String, Boolean>()
    val choices = mutableStateMapOf<String, MutableList<String>>()
    val choiceOrder = mutableStateMapOf<String, MutableList<String>>()
    val choiceEnabled = mutableStateMapOf<String, Boolean>()
    val inputs = mutableStateMapOf<String, String>()
    private val inputSelected = mutableStateMapOf<String, Boolean>()
    val errors = mutableStateMapOf<String, String>()
    val invalidByName = mutableStateMapOf<String, Boolean>()
    val validators = mutableStateMapOf<String, (List<String>) -> String?>()
    val skippedByName = mutableStateMapOf<String, Boolean>()
    var shortcuts by mutableStateOf(QuestionnaireShortcuts.None)
    var animate by mutableStateOf(false)
    var disabled by mutableStateOf(false)
    var inputFocused by mutableStateOf(false)
    var choiceFocused by mutableStateOf(false)
    var focusedChoiceValue by mutableStateOf<String?>(null)
    var pendingAnswerFocus by mutableStateOf<String?>(null)
    var pendingItemFocus by mutableStateOf<String?>(null)
    val choiceInteractions = mutableMapOf<String, () -> Unit>()
    val hiddenContents = mutableStateMapOf<String, @Composable () -> Unit>()
    private val inputPresentByName = mutableStateMapOf<String, Boolean>()
    private var itemsLocked = false
    private val itemsNames = mutableSetOf<String>()
    private val itemsChoiceKeys = mutableSetOf<String>()

    val activeName: String?
        get() = externalValue ?: uncontrolled ?: enabledNames().firstOrNull()

    fun enabledNames(): List<String> = names.filter { disabledByName[it] != true }

    val index: Int
        get() = enabledNames().indexOf(activeName).coerceAtLeast(0)

    val isLast: Boolean
        get() {
            val enabled = enabledNames()
            return enabled.isNotEmpty() && index >= enabled.lastIndex
        }

    val canPrevious: Boolean
        get() = index > 0

    val canSkip: Boolean
        get() {
            val name = activeName ?: return false
            return requiredByName[name] != true
        }

    fun answers(): Map<String, List<String>> =
        names.filter { disabledByName[it] != true }.associateWith { name ->
            val order = choiceOrder[name].orEmpty()
            val selected =
                choices[name].orEmpty().toList().sortedBy {
                    order.indexOf(it).takeIf { index ->
                        index >= 0
                    }
                        ?: Int.MAX_VALUE
                }
            val typed = if (inputSelected[name] == true) inputs[name]?.trim().orEmpty() else ""
            if (typed.isNotEmpty()) selected + typed else selected
        }

    fun hasAnswer(name: String): Boolean {
        if (choices[name].orEmpty().isNotEmpty()) return true
        return inputSelected[name] == true && inputs[name].orEmpty().isNotBlank()
    }

    fun applyItems(defs: List<QuestionnaireItemDefinition>) {
        if (defs.isEmpty()) {
            itemsLocked = false
            itemsNames.clear()
            itemsChoiceKeys.clear()
            return
        }
        itemsLocked = true
        itemsNames.clear()
        itemsNames.addAll(defs.map { it.name })
        itemsChoiceKeys.clear()
        val desiredNames = defs.map { it.name }
        val extras = names.filter { it !in itemsNames }
        if (names.toList() != desiredNames + extras) {
            names.clear()
            names.addAll(desiredNames)
            names.addAll(extras)
        }
        defs.forEach { def ->
            requiredByName[def.name] = def.required
            updateMultiple(def.name, def.multiple)
            disabledByName[def.name] = def.disabled
            def.choices.forEach { ch ->
                itemsChoiceKeys.add("${def.name}/${ch.value}")
                choiceEnabled["${def.name}/${ch.value}"] = !ch.disabled
            }
            if (def.choices.isNotEmpty()) {
                val desired = def.choices.map { it.value }
                val order = choiceOrder.getOrPut(def.name) { mutableStateListOf() }
                val extraChoices = order.filter { it !in desired }
                if (order.toList() != desired + extraChoices) {
                    order.clear()
                    order.addAll(desired)
                    order.addAll(extraChoices)
                }
            }
        }
        if (uncontrolled == null && externalValue == null) {
            uncontrolled = enabledNames().firstOrNull()
        }
    }

    fun register(
        name: String,
        required: Boolean,
        multiple: Boolean,
        requiredMessage: String?,
        disabled: Boolean,
        invalid: Boolean = false,
        validator: ((List<String>) -> String?)? = null,
    ) {
        if (name !in names) names.add(name)
        requiredByName[name] = required
        updateMultiple(name, multiple, fromItem = true)
        if (requiredMessage == null) {
            requiredMessageByName.remove(name)
        } else {
            requiredMessageByName[name] =
                requiredMessage
        }
        disabledByName[name] = disabled
        invalidByName[name] = invalid
        if (validator != null) {
            validators[name] = validator
        } else {
            validators.remove(name)
        }
        if (uncontrolled == null && externalValue == null) {
            uncontrolled = enabledNames().firstOrNull()
        }
    }

    private fun updateMultiple(
        name: String,
        multiple: Boolean,
        fromItem: Boolean = false,
    ) {
        if (!fromItem && name in registeredMultiple) return
        val previous = registeredMultiple[name] ?: multipleByName[name]
        if (fromItem) registeredMultiple[name] = multiple
        if (previous == true && !multiple) {
            val selected = choices[name].orEmpty()
            val first = choiceOrder[name].orEmpty().firstOrNull { it in selected }
            choices[name]?.apply {
                clear()
                if (first != null) add(first)
            }
            if (first != null) inputSelected[name] = false
        }
        multipleByName[name] = multiple
    }

    fun interactWithChoice(
        name: String,
        value: String,
    ) {
        choiceInteractions["$name/$value"]?.invoke() ?: selectChoice(name, value)
    }

    fun unregister(name: String) {
        if (itemsLocked && name in itemsNames) return
        names.remove(name)
        registeredMultiple.remove(name)
        requiredByName.remove(name)
        multipleByName.remove(name)
        requiredMessageByName.remove(name)
        disabledByName.remove(name)
        invalidByName.remove(name)
        validators.remove(name)
        skippedByName.remove(name)
        choices.remove(name)
        choiceOrder.remove(name)
        inputs.remove(name)
        inputSelected.remove(name)
        errors.remove(name)
        inputPresentByName.remove(name)
    }

    fun registerChoice(
        item: String,
        value: String,
        enabled: Boolean,
        defaultChecked: Boolean = false,
    ) {
        val order = choiceOrder.getOrPut(item) { mutableStateListOf() }
        if (value !in order) order.add(value)
        choiceEnabled["$item/$value"] = enabled
        applyChoiceDefault(item, value, defaultChecked)
    }

    private val choiceDefaultApplied = mutableStateMapOf<String, Boolean>()

    private fun applyChoiceDefault(
        item: String,
        value: String,
        defaultChecked: Boolean,
    ) {
        val key = "$item/$value"
        if (choiceDefaultApplied[key] == true) return
        choiceDefaultApplied[key] = true
        if (!defaultChecked) return
        val already = choices[item].orEmpty()
        if (multipleByName[item] != true && (already.isNotEmpty() || inputSelected[item] == true)) return
        selectChoice(item, value)
    }

    private val inputDefaultApplied = mutableStateMapOf<String, Boolean>()

    fun applyInputDefault(
        name: String,
        defaultValue: String,
    ) {
        if (inputDefaultApplied[name] == true) return
        inputDefaultApplied[name] = true
        if (defaultValue.isNotEmpty() && inputs[name].isNullOrEmpty()) {
            inputs[name] = defaultValue
            inputSelected[name] = defaultValue.isNotBlank() &&
                (multipleByName[name] == true || choices[name].isNullOrEmpty())
        }
    }

    fun unregisterChoice(
        item: String,
        value: String,
    ) {
        choiceInteractions.remove("$item/$value")
        if (itemsLocked && "$item/$value" in itemsChoiceKeys) return
        choiceOrder[item]?.remove(value)
        choiceEnabled.remove("$item/$value")
    }

    fun shortcutFor(
        item: String,
        value: String,
    ): String? {
        if (shortcuts == QuestionnaireShortcuts.None) return null
        if (choiceEnabled["$item/$value"] == false) return null
        val ordered =
            choiceOrder[item].orEmpty().filter { choiceEnabled["$item/$it"] != false }
        val i = ordered.indexOf(value)
        if (i < 0) return null
        return when (shortcuts) {
            QuestionnaireShortcuts.Letters -> if (i < 26) ('A' + i).toString() else null
            QuestionnaireShortcuts.Numbers -> if (i < 9) (i + 1).toString() else null
            QuestionnaireShortcuts.None -> null
        }
    }

    fun selectByShortcut(label: String): Boolean {
        if (disabled) return false
        val name = activeName ?: return false
        val ordered = choiceOrder[name].orEmpty().filter { choiceEnabled["$name/$it"] != false }
        val index =
            when (shortcuts) {
                QuestionnaireShortcuts.Letters -> label.uppercase().firstOrNull()?.minus('A') ?: -1
                QuestionnaireShortcuts.Numbers -> (label.toIntOrNull() ?: 0) - 1
                QuestionnaireShortcuts.None -> return false
            }
        if (index == ordered.size && inputPresentByName[name] == true) {
            pendingAnswerFocus = InputAnswerKey
            return true
        }
        val value = ordered.getOrNull(index) ?: return false
        interactWithChoice(name, value)
        pendingAnswerFocus = value
        return true
    }

    fun goTo(name: String) {
        onValueChange?.invoke(name)
        if (externalValue == null) uncontrolled = name
        choiceFocused = false
        focusedChoiceValue = null
        pendingAnswerFocus = null
        pendingItemFocus = name
    }

    fun registerInput(
        name: String,
        enabled: Boolean = true,
    ) {
        inputPresentByName[name] = enabled
    }

    fun unregisterInput(name: String) {
        inputPresentByName.remove(name)
        if (inputFocused) inputFocused = false
    }

    fun moveAnswer(delta: Int): Boolean {
        val name = activeName ?: return false
        val keys =
            buildList {
                addAll(
                    choiceOrder[name].orEmpty().filter { choiceEnabled["$name/$it"] != false },
                )
                if (inputPresentByName[name] == true) add(InputAnswerKey)
            }
        if (keys.isEmpty()) return false
        val focused = focusedChoiceValue
        val current =
            when {
                inputFocused -> InputAnswerKey
                focused != null && focused in keys -> focused
                else -> null
            }
        val nextIdx =
            if (current == null) {
                if (delta > 0) 0 else keys.lastIndex
            } else {
                val i = keys.indexOf(current)
                if (i < 0) {
                    if (delta > 0) 0 else keys.lastIndex
                } else {
                    (i + delta).mod(keys.size)
                }
            }
        val next = keys[nextIdx]
        pendingAnswerFocus = next
        if (next != InputAnswerKey && multipleByName[name] != true && !isSelected(name, next)) {
            interactWithChoice(name, next)
        }
        return true
    }

    fun selectChoice(
        name: String,
        value: String,
    ) {
        val current = choices.getOrPut(name) { mutableStateListOf() }
        if (multipleByName[name] == true) {
            if (value in current) current.remove(value) else current.add(value)
        } else {
            current.clear()
            current.add(value)
            inputSelected[name] = false
        }
        skippedByName[name] = false
        errors.remove(name)
    }

    fun setChoice(
        name: String,
        value: String,
        selected: Boolean,
    ) {
        val current = choices.getOrPut(name) { mutableStateListOf() }
        val isOn = value in current
        if (selected == isOn) return
        if (multipleByName[name] == true) {
            if (selected) current.add(value) else current.remove(value)
        } else if (selected) {
            current.clear()
            current.add(value)
            inputSelected[name] = false
        } else {
            current.remove(value)
        }
        if (selected) {
            skippedByName[name] = false
            errors.remove(name)
        }
    }

    fun isSelected(
        name: String,
        value: String,
    ): Boolean = value in choices[name].orEmpty()

    fun setInput(
        name: String,
        text: String,
    ) {
        inputs[name] = text
        inputSelected[name] = text.isNotBlank()
        if (text.isNotBlank()) {
            if (multipleByName[name] != true) choices[name]?.clear()
            skippedByName[name] = false
            errors.remove(name)
        }
    }

    fun previous() {
        if (disabled) return
        val enabled = enabledNames()
        val i = enabled.indexOf(activeName)
        if (i > 0) goTo(enabled[i - 1])
    }

    fun skip() {
        if (disabled) return
        if (!canSkip) return
        val name = activeName ?: return
        skippedByName[name] = true
        choices[name]?.clear()
        inputSelected[name] = false
        errors.remove(name)
        val enabled = enabledNames()
        val i = enabled.indexOf(name)
        if (i >= 0 && i < enabled.lastIndex) {
            goTo(enabled[i + 1])
        } else if (isLast) {
            submit()
        }
    }

    fun next() {
        if (disabled) return
        val name = activeName ?: return
        if (!validate(name)) {
            focusInvalid(name)
            return
        }
        if (isLast) return
        val enabled = enabledNames()
        val i = enabled.indexOf(name)
        if (i >= 0 && i < enabled.lastIndex) goTo(enabled[i + 1])
    }

    fun submit() {
        if (activeName == null || disabled) return
        val firstInvalid = enabledNames().firstOrNull { !validate(it) }
        if (firstInvalid != null) {
            goTo(firstInvalid)
            focusInvalid(firstInvalid)
            return
        }
        onSubmit(answers())
    }

    private fun focusInvalid(name: String) {
        pendingItemFocus = null
        pendingAnswerFocus =
            if (inputSelected[name] == true && inputPresentByName[name] == true) {
                InputAnswerKey
            } else {
                choiceOrder[name]?.firstOrNull { choiceEnabled["$name/$it"] != false } ?: InputAnswerKey
            }
    }

    fun continueOrSubmit() {
        if (isLast) submit() else next()
    }

    private fun fallbackError(name: String): String =
        requiredMessageByName[name]
            ?: if (requiredByName[name] == true) {
                "Choose an answer to continue."
            } else {
                "Choose an answer or skip this question."
            }

    private fun validate(name: String): Boolean {
        if (disabledByName[name] == true) return true
        if (skippedByName[name] == true && requiredByName[name] != true) {
            errors.remove(name)
            return true
        }
        if (invalidByName[name] == true || !hasAnswer(name)) {
            errors[name] = fallbackError(name)
            return false
        }
        val custom = validators[name]?.invoke(answers()[name].orEmpty())
        if (custom != null) {
            errors[name] = custom
            return false
        }
        errors.remove(name)
        return true
    }

    fun errorMessage(name: String): String? {
        if (skippedByName[name] == true && requiredByName[name] != true) return null
        if (invalidByName[name] == true) {
            return fallbackError(name)
        }
        return errors[name]
    }

    fun itemStatus(name: String): QuestionnaireItemStatus =
        when {
            skippedByName[name] == true -> QuestionnaireItemStatus.Skipped
            hasAnswer(name) -> QuestionnaireItemStatus.Answered
            else -> QuestionnaireItemStatus.Unanswered
        }

    /** Official form `type="reset"` — restores `defaultItem` / `defaultChecked` / `defaultValue`. */
    fun reset() {
        choices.values.forEach { it.clear() }
        inputs.clear()
        inputSelected.clear()
        errors.clear()
        skippedByName.clear()
        choiceDefaultApplied.clear()
        inputDefaultApplied.clear()
        if (externalValue == null) {
            uncontrolled = initialValue ?: enabledNames().firstOrNull()
            uncontrolled?.let { onValueChange?.invoke(it) }
        }
    }
}

@Composable
private fun questionnaireController(): QuestionnaireController =
    checkNotNull(LocalQuestionnaireController.current) {
        "Questionnaire slots must be used inside Questionnaire { … }"
    }

/** Reads the nearest [Questionnaire]. */
@Composable
fun questionnaireState(): QuestionnaireState {
    val ctrl = questionnaireController()
    val name = ctrl.activeName
    return QuestionnaireState(
        value = name,
        answers = ctrl.answers(),
        index = ctrl.index,
        count = ctrl.enabledNames().size,
        error = name?.let { ctrl.errorMessage(it) },
        isLast = ctrl.isLast,
        canPrevious = ctrl.canPrevious,
        canSkip = ctrl.canSkip,
        status = name?.let { ctrl.itemStatus(it) } ?: QuestionnaireItemStatus.Unanswered,
        reset = ctrl::reset,
    )
}

class QuestionnaireScope internal constructor()

private val LocalQuestionnaireItemName = staticCompositionLocalOf<String?> { null }

private val LocalQuestionnaireChoiceValue = staticCompositionLocalOf<String?> { null }

private val LocalQuestionnaireChoiceSelected = staticCompositionLocalOf { false }

private val LocalQuestionnaireChoiceMultiple = staticCompositionLocalOf { false }

private const val InputAnswerKey = "\u0000input"

private val LetterKeys =
    listOf(
        Key.A, Key.B, Key.C, Key.D, Key.E, Key.F, Key.G, Key.H, Key.I, Key.J,
        Key.K, Key.L, Key.M, Key.N, Key.O, Key.P, Key.Q, Key.R, Key.S, Key.T,
        Key.U, Key.V, Key.W, Key.X, Key.Y, Key.Z,
    )

private val NumberKeys =
    listOf(
        Key.One, Key.Two, Key.Three, Key.Four, Key.Five,
        Key.Six, Key.Seven, Key.Eight, Key.Nine,
    )

private val NumPadKeys =
    listOf(
        Key.NumPad1, Key.NumPad2, Key.NumPad3, Key.NumPad4, Key.NumPad5,
        Key.NumPad6, Key.NumPad7, Key.NumPad8, Key.NumPad9,
    )

private fun handleQuestionnaireKey(
    ctrl: QuestionnaireController,
    event: KeyEvent,
): Boolean {
    if (event.type != KeyEventType.KeyDown) return false
    if (ctrl.disabled) return false
    val enter = event.key == Key.Enter || event.key == Key.NumPadEnter
    if (enter && (event.isCtrlPressed || event.isMetaPressed) && !event.isAltPressed && !event.isShiftPressed) {
        ctrl.continueOrSubmit()
        return true
    }
    if (event.isCtrlPressed || event.isMetaPressed || event.isAltPressed) return false
    if (enter && ctrl.choiceFocused) {
        val name = ctrl.activeName
        val choice = ctrl.focusedChoiceValue
        if (name != null && choice != null && ctrl.isSelected(name, choice)) ctrl.continueOrSubmit()
        return true
    }
    if (ctrl.inputFocused) {
        val name = ctrl.activeName
        if (enter && name != null && ctrl.inputs[name].orEmpty().isNotBlank()) {
            ctrl.continueOrSubmit()
            return true
        }
        val empty = name == null || ctrl.inputs[name].isNullOrBlank()
        if (empty) {
            when (event.key) {
                Key.DirectionUp -> return ctrl.moveAnswer(-1)
                Key.DirectionDown -> return ctrl.moveAnswer(1)
                else -> Unit
            }
        }
        return false
    }
    when (event.key) {
        Key.DirectionUp -> return ctrl.moveAnswer(-1)
        Key.DirectionDown -> return ctrl.moveAnswer(1)
        Key.DirectionLeft -> {
            if (ctrl.choiceFocused) return false
            ctrl.previous()
            return true
        }
        Key.DirectionRight -> {
            if (ctrl.choiceFocused) return false
            val name = ctrl.activeName ?: return false
            if (ctrl.itemStatus(name) != QuestionnaireItemStatus.Unanswered) {
                ctrl.next()
            }
            return true
        }
        else -> Unit
    }
    val label =
        when (ctrl.shortcuts) {
            QuestionnaireShortcuts.Letters -> {
                val i = LetterKeys.indexOf(event.key)
                if (i >= 0) ('A' + i).toString() else null
            }
            QuestionnaireShortcuts.Numbers -> {
                val i = NumberKeys.indexOf(event.key).takeIf { it >= 0 } ?: NumPadKeys.indexOf(event.key)
                if (i >= 0) (i + 1).toString() else null
            }
            QuestionnaireShortcuts.None -> null
        } ?: return false
    return ctrl.selectByShortcut(label)
}

/** "Question X of Y" — shadcn `QuestionnaireProgress` (`text-muted-foreground tabular-nums`). */
@Composable
fun QuestionnaireScope.QuestionnaireProgress(
    modifier: Modifier = Modifier,
    style: Style = Style,
    /** Official Progress `aria-label`. */
    contentDescription: String = "Questionnaire progress",
    text: (QuestionnaireProgressState) ->
    String =
        {
            if (it.total > 0) {
                "Question ${it.current} of ${it.total}"
            } else {
                ""
            }
        },
) {
    val ctrl = questionnaireController()
    val total = ctrl.enabledNames().size
    val current = if (total > 0) ctrl.index + 1 else 0
    val state =
        QuestionnaireProgressState(
            current = current,
            total = total,
            first = total > 0 && ctrl.index == 0,
            last = total > 0 && ctrl.isLast,
        )
    val label = text(state)
    val progressName = contentDescription
    val styleState = remember { MutableStyleState(MutableInteractionSource()) }
    val nova = currentTheme.activePreset == StylePreset.Nova
    val typography =
        if (nova) {
            TextColors.Muted.merge(
                rememberQuestionnaireTypography(12.sp, 16.sp, FontWeight.Medium),
            ).copy(fontFeatureSettings = "tnum")
        } else {
            TextColors.Muted.merge(TextStyle(fontWeight = FontWeight.Medium))
        }
    val measurer = rememberTextMeasurer()
    val minimumWidth =
        if (nova) {
            with(LocalDensity.current) {
                measurer.measure(
                    AnnotatedString("00000000000000"),
                    style =
                        typography.copy(
                            fontFamily =
                                currentTheme.fontFamily,
                        ),
                ).size.width.toDp()
            }
        } else {
            0.dp
        }
    Text(
        modifier =
            modifier
                .widthIn(min = minimumWidth)
                .heightIn(min = if (nova) with(LocalDensity.current) { 16.sp.toDp() } else 0.dp)
                .semantics {
                    liveRegion = LiveRegionMode.Polite
                    if (total > 0) {
                        stateDescription = label
                        progressBarRangeInfo =
                            ProgressBarRangeInfo(
                                current = current.toFloat(),
                                range = 1f..total.toFloat(),
                                steps = (total - 2).coerceAtLeast(0),
                            )
                    }
                    this.contentDescription = progressName
                }
                .styleable(styleState, style),
        variant = TextVariant.BodySmall,
        textStyle = typography,
    ) { label }
}

@Composable
fun QuestionnaireScope.QuestionnaireItem(
    name: String,
    modifier: Modifier = Modifier,
    required: Boolean = false,
    multiple: Boolean = false,
    requiredMessage: String? = null,
    disabled: Boolean = false,
    invalid: Boolean = false,
    validate: ((List<String>) -> String?)? = null,
    onStatusChange: ((QuestionnaireItemStatus) -> Unit)? = null,
    animate: Boolean? = null,
    style: Style = Style,
    content: @Composable ColumnScope.() -> Unit,
) {
    val ctrl = questionnaireController()
    val status = ctrl.itemStatus(name)
    var previousStatus by remember(name) { mutableStateOf<QuestionnaireItemStatus?>(null) }
    SideEffect {
        ctrl.register(name, required, multiple, requiredMessage, disabled, invalid, validate)
        val previous = previousStatus
        if (previous != null && previous != status) {
            onStatusChange?.invoke(status)
        }
        previousStatus = status
    }
    val currentContent = rememberUpdatedState(content)
    DisposableEffect(name) {
        ctrl.hiddenContents[name] = {
            CompositionLocalProvider(LocalQuestionnaireItemName provides name) {
                Column(content = currentContent.value)
            }
        }
        onDispose {
            ctrl.hiddenContents.remove(name)
            ctrl.unregister(name)
        }
    }
    if (disabled || ctrl.activeName != name) return
    val styleState = remember { MutableStyleState(MutableInteractionSource()) }
    val shouldAnimate = animate ?: ctrl.animate
    val itemError = ctrl.errorMessage(name)
    val itemFocusRequester = remember { FocusRequester() }
    LaunchedEffect(ctrl.activeName, ctrl.pendingItemFocus) {
        if (ctrl.pendingItemFocus == name) {
            itemFocusRequester.requestFocus()
            ctrl.pendingItemFocus = null
        }
    }
    val itemModifier =
        modifier
            .fillMaxWidth()
            .focusRequester(itemFocusRequester)
            .focusable()
            .semantics { if (itemError != null) error(itemError) }
            .styleable(styleState, style)
    CompositionLocalProvider(LocalQuestionnaireItemName provides name) {
        if (shouldAnimate) {
            val slidePx = with(LocalDensity.current) { 8.dp.roundToPx() }
            AnimatedVisibility(
                visible = true,
                enter = fadeIn(tween(300)) + slideInVertically(tween(300)) { slidePx },
            ) {
                Column(
                    modifier = itemModifier,
                    verticalArrangement =
                        Arrangement.spacedBy(
                            if (currentTheme.activePreset == StylePreset.Nova) {
                                16.dp
                            } else {
                                twSpacing.md
                            },
                        ),
                    content = content,
                )
            }
        } else {
            Column(
                modifier = itemModifier,
                verticalArrangement =
                    Arrangement.spacedBy(
                        if (currentTheme.activePreset == StylePreset.Nova) {
                            16.dp
                        } else {
                            twSpacing.md
                        },
                    ),
                content = content,
            )
        }
    }
}

@Composable
fun QuestionnaireScope.QuestionnaireTitle(
    text: String,
    modifier: Modifier = Modifier,
    style: Style = Style,
) {
    val styleState = remember { MutableStyleState(MutableInteractionSource()) }
    Text(
        modifier = modifier.semantics { heading() }.styleable(styleState, style),
        variant = TextVariant.TitleMedium,
        textStyle =
            if (currentTheme.activePreset == StylePreset.Nova) {
                rememberQuestionnaireTypography(16.sp, 22.sp, FontWeight.Medium)
            } else {
                TextStyle(fontWeight = FontWeight.Medium)
            },
    ) { text }
}

@Composable
@JvmName("QuestionnaireTitleLambda")
@OverloadResolutionByLambdaReturnType
fun QuestionnaireScope.QuestionnaireTitle(
    modifier: Modifier = Modifier,
    style: Style = Style,
    text: () -> String,
) {
    QuestionnaireTitle(text = text(), modifier = modifier, style = style)
}

@Composable
fun QuestionnaireScope.QuestionnaireDescription(
    text: String,
    modifier: Modifier = Modifier,
    style: Style = Style,
) {
    val styleState = remember { MutableStyleState(MutableInteractionSource()) }
    Text(
        modifier = modifier.styleable(styleState, style),
        variant = TextVariant.BodySmall,
        textStyle =
            if (currentTheme.activePreset == StylePreset.Nova) {
                TextColors.Muted.merge(rememberQuestionnaireTypography(14.sp, 20.sp))
            } else {
                TextColors.Muted
            },
    ) { text }
}

@Composable
@JvmName("QuestionnaireDescriptionLambda")
@OverloadResolutionByLambdaReturnType
fun QuestionnaireScope.QuestionnaireDescription(
    modifier: Modifier = Modifier,
    style: Style = Style,
    text: () -> String,
) {
    QuestionnaireDescription(text = text(), modifier = modifier, style = style)
}

@Composable
fun QuestionnaireScope.QuestionnaireChoices(
    modifier: Modifier = Modifier,
    style: Style = Style,
    content: @Composable ColumnScope.() -> Unit,
) {
    val ctrl = questionnaireController()
    val name = LocalQuestionnaireItemName.current
    val multiple = name != null && ctrl.multipleByName[name] == true
    val styleState = remember { MutableStyleState(MutableInteractionSource()) }
    Column(
        modifier =
            modifier
                .fillMaxWidth()
                .then(if (!multiple) Modifier.selectableGroup() else Modifier)
                .styleable(styleState, style),
        verticalArrangement = Arrangement.spacedBy(twSpacing.sm),
        content = content,
    )
}

@Composable
fun QuestionnaireScope.QuestionnaireChoice(
    value: String,
    modifier: Modifier = Modifier,
    enabled: Boolean = true,
    disabled: Boolean = false,
    defaultChecked: Boolean = false,
    checked: Boolean? = null,
    onCheckedChange: ((Boolean) -> Unit)? = null,
    style: Style = Style,
    content: @Composable ColumnScope.() -> Unit,
) {
    val ctrl = questionnaireController()
    val name =
        checkNotNull(LocalQuestionnaireItemName.current) {
            "QuestionnaireChoice must be used inside QuestionnaireItem { … }"
        }
    val resolvedEnabled = enabled && !disabled && !LocalFieldSetDisabled.current
    val selected = checked ?: ctrl.isSelected(name, value)
    val multiple = ctrl.multipleByName[name] == true
    SideEffect {
        ctrl.registerChoice(
            name,
            value,
            resolvedEnabled,
            defaultChecked = checked == null && defaultChecked,
        )
        if (checked != null) ctrl.setChoice(name, value, checked)
        ctrl.choiceInteractions["$name/$value"] = {
            val next = if (multiple) !selected else true
            if (checked != null) {
                onCheckedChange?.invoke(next)
            } else {
                ctrl.selectChoice(name, value)
                onCheckedChange?.invoke(ctrl.isSelected(name, value))
            }
        }
    }
    DisposableEffect(name, value) {
        onDispose { ctrl.unregisterChoice(name, value) }
    }
    if (ctrl.activeName != name || ctrl.disabledByName[name] == true) return
    val interactionSource = remember { MutableInteractionSource() }
    val styleState = remember { MutableStyleState(interactionSource) }
    val itemError = ctrl.errorMessage(name)
    val focusRequester = remember { FocusRequester() }
    val choiceSelected = selected
    LaunchedEffect(ctrl.pendingAnswerFocus, value) {
        if (ctrl.activeName == name && ctrl.pendingAnswerFocus == value) {
            runCatching { focusRequester.requestFocus() }
            if (ctrl.pendingAnswerFocus == value) ctrl.pendingAnswerFocus = null
        }
    }
    CompositionLocalProvider(
        LocalQuestionnaireChoiceValue provides value,
        LocalQuestionnaireChoiceSelected provides selected,
        LocalQuestionnaireChoiceMultiple provides multiple,
    ) {
        Row(
            modifier =
                modifier
                    .heightIn(min = 44.dp)
                    .then(if (resolvedEnabled) Modifier else Modifier.alpha(0.5f))
                    .focusRequester(focusRequester)
                    .onFocusChanged {
                        if (it.isFocused) {
                            ctrl.choiceFocused = true
                            ctrl.focusedChoiceValue = value
                        } else if (ctrl.focusedChoiceValue == value) {
                            ctrl.choiceFocused = false
                            ctrl.focusedChoiceValue = null
                        }
                    }
                    .semantics {
                        role = if (multiple) Role.Checkbox else Role.RadioButton
                        if (multiple) {
                            toggleableState = if (choiceSelected) ToggleableState.On else ToggleableState.Off
                        } else {
                            this.selected = choiceSelected
                        }
                        if (!resolvedEnabled) disabled()
                        if (itemError != null) error(itemError)
                    }
                    .clickable(
                        interactionSource = interactionSource,
                        indication = null,
                        enabled = resolvedEnabled,
                        role = if (multiple) Role.Checkbox else Role.RadioButton,
                        onClick = {
                            val next = if (multiple) !selected else true
                            if (checked != null) {
                                onCheckedChange?.invoke(next)
                            } else {
                                ctrl.selectChoice(name, value)
                                onCheckedChange?.invoke(ctrl.isSelected(name, value))
                            }
                        },
                    )
                    .then(if (resolvedEnabled) Modifier.pointerHoverIcon(PointerIcon.Hand) else Modifier)
                    .cssStyleable(
                        styleState,
                        rememberQuestionnaireChoiceStyle(
                            selected,
                            itemError != null ||
                                ctrl.invalidByName[name] == true,
                        ) then
                            style,
                    ),
            verticalAlignment = Alignment.Top,
            horizontalArrangement =
                Arrangement.spacedBy(
                    if (currentTheme.activePreset == StylePreset.Nova) {
                        10.dp
                    } else {
                        8.dp
                    },
                ),
        ) {
            QuestionnaireChoiceInput(
                Modifier.offset(
                    y =
                        if (currentTheme.activePreset == StylePreset.Nova) {
                            1.8.dp
                        } else {
                            0.dp
                        },
                ),
            )
            Column(
                modifier = Modifier.weight(1f),
                verticalArrangement = Arrangement.spacedBy(twSpacing.xs2),
                content = content,
            )
            QuestionnaireChoiceShortcut()
        }
    }
}

/** Official `QuestionnaireChoiceShortcut` — hidden unless [Questionnaire.shortcuts] assigned a key. */
@Composable
fun QuestionnaireScope.QuestionnaireChoiceShortcut(modifier: Modifier = Modifier) {
    val ctrl = questionnaireController()
    val name = LocalQuestionnaireItemName.current ?: return
    val value = LocalQuestionnaireChoiceValue.current ?: return
    val label = ctrl.shortcutFor(name, value) ?: return
    if (currentTheme.activePreset == StylePreset.Nova) {
        Kbd(
            label,
            modifier =
                modifier.offset(
                    y =
                        1.8.dp,
                ).semantics { hideFromAccessibility() },
            style =
                rememberQuestionnaireShortcutStyle(),
        )
    } else {
        Kbd(label, modifier = modifier)
    }
}

/** Official `QuestionnaireChoiceInput` — native radio/checkbox stand-in (16dp, not separately clickable). */
@Composable
fun QuestionnaireScope.QuestionnaireChoiceInput(modifier: Modifier = Modifier) {
    val selected = LocalQuestionnaireChoiceSelected.current
    val multiple = LocalQuestionnaireChoiceMultiple.current
    val colors = currentTheme.colors
    val nova = currentTheme.activePreset == StylePreset.Nova
    val shape = if (multiple) RoundedCornerShape(if (nova) 4.dp else 2.dp) else CircleShape
    val stroke =
        if (selected) {
            colors.primary
        } else if (nova) {
            colors.input
        } else {
            colors.border
        }
    val background =
        when {
            selected && (nova || multiple) -> colors.primary
            nova && currentTheme.isDark -> colors.input.copy(alpha = colors.input.alpha * 0.3f)
            else -> Color.Transparent
        }
    Box(
        modifier =
            modifier.size(16.dp).semantics { hideFromAccessibility() }
                .background(background, shape).border(1.dp, stroke, shape),
        contentAlignment = Alignment.Center,
    ) {
        if (!multiple && selected) {
            Box(Modifier.size(8.dp).background(if (nova) colors.primaryForeground else colors.primary, CircleShape))
        } else if (multiple && selected && nova) {
            Icon(NovaCheck, Modifier.size(14.dp), tint = colors.primaryForeground)
        }
    }
}

/** Official `QuestionnaireChoiceLabel` — medium-weight choice title. */
@Composable
fun QuestionnaireScope.QuestionnaireChoiceLabel(
    text: String,
    modifier: Modifier = Modifier,
    style: Style = Style,
) {
    val styleState = remember { MutableStyleState(MutableInteractionSource()) }
    Text(
        modifier = modifier.styleable(styleState, style),
        variant = TextVariant.BodySmall,
        textStyle =
            if (currentTheme.activePreset == StylePreset.Nova) {
                rememberQuestionnaireTypography(14.sp, 19.25.sp)
            } else {
                TextStyle(fontWeight = FontWeight.Medium)
            },
    ) { text }
}

@Composable
fun QuestionnaireScope.QuestionnaireChoice(
    value: String,
    text: String,
    modifier: Modifier = Modifier,
    description: String? = null,
    enabled: Boolean = true,
    disabled: Boolean = false,
    defaultChecked: Boolean = false,
    checked: Boolean? = null,
    onCheckedChange: ((Boolean) -> Unit)? = null,
    style: Style = Style,
) {
    QuestionnaireChoice(
        value = value,
        modifier = modifier,
        enabled = enabled,
        disabled = disabled,
        defaultChecked = defaultChecked,
        checked = checked,
        onCheckedChange = onCheckedChange,
        style = style,
    ) {
        QuestionnaireChoiceLabel(
            text,
            style =
                if (description != null &&
                    currentTheme.activePreset == StylePreset.Nova
                ) {
                    Style { fontWeight(FontWeight.Medium) }
                } else {
                    Style
                },
        )
        if (description != null) {
            QuestionnaireChoiceDescription(description)
        }
    }
}

/** Official `QuestionnaireChoiceDescription` — muted supporting text under a choice label. */
@Composable
fun QuestionnaireScope.QuestionnaireChoiceDescription(
    text: String,
    modifier: Modifier = Modifier,
    style: Style = Style,
) {
    val styleState = remember { MutableStyleState(MutableInteractionSource()) }
    Text(
        modifier = modifier.styleable(styleState, style),
        variant = TextVariant.BodySmall,
        textStyle =
            if (currentTheme.activePreset == StylePreset.Nova) {
                TextColors.Muted.merge(rememberQuestionnaireTypography(14.sp, 19.25.sp))
            } else {
                TextColors.Muted
            },
    ) { text }
}

@Composable
fun QuestionnaireScope.QuestionnaireInput(
    modifier: Modifier = Modifier,
    placeholder: String? = null,
    /** Official `aria-label`. Placeholders are not an accessible name. */
    contentDescription: String? = null,
    defaultValue: String = "",
    type: InputType = InputType.Text,
    enabled: Boolean = true,
    disabled: Boolean = false,
    style: Style = Style,
) {
    val ctrl = questionnaireController()
    val name =
        checkNotNull(LocalQuestionnaireItemName.current) {
            "QuestionnaireInput must be used inside QuestionnaireItem { … }"
        }
    val focusRequester = remember { FocusRequester() }
    SideEffect { ctrl.applyInputDefault(name, defaultValue) }
    val resolvedInputEnabled = enabled && !disabled && !LocalFieldSetDisabled.current
    DisposableEffect(name, resolvedInputEnabled) {
        ctrl.registerInput(name, resolvedInputEnabled)
        onDispose { ctrl.unregisterInput(name) }
    }
    if (ctrl.activeName != name || ctrl.disabledByName[name] == true) return
    LaunchedEffect(ctrl.pendingAnswerFocus) {
        if (ctrl.activeName == name && ctrl.pendingAnswerFocus == InputAnswerKey) {
            runCatching { focusRequester.requestFocus() }
            if (ctrl.pendingAnswerFocus == InputAnswerKey) ctrl.pendingAnswerFocus = null
        }
    }
    Input(
        value = ctrl.inputs[name].orEmpty(),
        onValueChange = { ctrl.setInput(name, it) },
        modifier =
            modifier
                .heightIn(min = questionnaireActionMinHeight())
                .fillMaxWidth()
                .focusRequester(focusRequester)
                .then(
                    if (contentDescription != null) {
                        Modifier.semantics { this.contentDescription = contentDescription }
                    } else {
                        Modifier
                    },
                )
                .onFocusChanged { ctrl.inputFocused = it.isFocused },
        enabled = enabled,
        disabled = disabled,
        invalid = ctrl.errorMessage(name) != null,
        type = type,
        placeholder = placeholder,
        style =
            (
                if (currentTheme.activePreset == StylePreset.Nova) {
                    Style { contentPadding(horizontal = 10.dp, vertical = 4.dp) }
                } else {
                    Style
                }
            ) then style,
    )
}

@Composable
fun QuestionnaireScope.QuestionnaireError(
    modifier: Modifier = Modifier,
    text: String? = null,
    style: Style = Style,
) {
    val ctrl = questionnaireController()
    val name = LocalQuestionnaireItemName.current ?: return
    val fallback = ctrl.errorMessage(name) ?: return
    val message = text ?: fallback
    val styleState = remember { MutableStyleState(MutableInteractionSource()) }
    val destructive = currentTheme.colors.destructive
    Text(
        modifier =
            modifier
                .then(if (currentTheme.activePreset == StylePreset.Nova) Modifier.padding(top = 8.dp) else Modifier)
                .semantics { liveRegion = LiveRegionMode.Assertive }
                .styleable(styleState, style),
        variant = TextVariant.BodySmall,
        textStyle =
            if (currentTheme.activePreset == StylePreset.Nova) {
                rememberQuestionnaireTypography(14.sp, 20.sp).copy(color = destructive)
            } else {
                TextStyle(color = destructive)
            },
    ) { message }
}

@Composable
fun QuestionnaireScope.QuestionnaireActions(
    modifier: Modifier = Modifier,
    style: Style = Style,
    content: @Composable RowScope.() -> Unit,
) {
    val styleState = remember { MutableStyleState(MutableInteractionSource()) }
    Row(
        modifier = modifier.heightIn(min = questionnaireActionMinHeight()).fillMaxWidth().styleable(styleState, style),
        horizontalArrangement = Arrangement.spacedBy(twSpacing.sm),
        verticalAlignment = Alignment.CenterVertically,
        content = content,
    )
}

@Composable
private fun QuestionnairePreviousButton(
    text: String,
    modifier: Modifier,
    enabled: Boolean,
    disabled: Boolean,
    variant: ButtonVariant,
    size: ButtonSize,
    style: Style,
) {
    val ctrl = questionnaireController()
    if (!ctrl.canPrevious) return
    Button(
        text = text,
        onClick = ctrl::previous,
        modifier = modifier.heightIn(min = questionnaireActionMinHeight()),
        enabled = enabled && ctrl.canPrevious,
        disabled = disabled,
        variant = variant,
        size = size,
        style = style,
    )
}

@Composable
fun RowScope.QuestionnairePrevious(
    text: String = "Previous",
    modifier: Modifier = Modifier,
    enabled: Boolean = true,
    disabled: Boolean = false,
    variant: ButtonVariant = ButtonVariant.Outline,
    size: ButtonSize = ButtonSize.Md,
    style: Style = Style,
) {
    Box(modifier = modifier.weight(1f), contentAlignment = Alignment.CenterStart) {
        QuestionnairePreviousButton(
            text = text,
            modifier = Modifier,
            enabled = enabled,
            disabled = disabled,
            variant = variant,
            size = size,
            style = style,
        )
    }
}

/** Official anatomy: Previous/Skip/Next/Submit may sit on Root, not only in [QuestionnaireActions]. */
@Composable
fun QuestionnairePrevious(
    text: String = "Previous",
    modifier: Modifier = Modifier,
    enabled: Boolean = true,
    disabled: Boolean = false,
    variant: ButtonVariant = ButtonVariant.Outline,
    size: ButtonSize = ButtonSize.Md,
    style: Style = Style,
) {
    QuestionnairePreviousButton(
        text = text,
        modifier = modifier.heightIn(min = questionnaireActionMinHeight()),
        enabled = enabled,
        disabled = disabled,
        variant = variant,
        size = size,
        style = style,
    )
}

@Composable
fun RowScope.QuestionnaireSkip(
    text: String = "Skip",
    modifier: Modifier = Modifier,
    enabled: Boolean = true,
    disabled: Boolean = false,
    variant: ButtonVariant = ButtonVariant.Outline,
    size: ButtonSize = ButtonSize.Md,
    style: Style = Style,
) {
    val ctrl = questionnaireController()
    if (!ctrl.canSkip) return
    Button(
        text = text,
        onClick = ctrl::skip,
        modifier = modifier.heightIn(min = questionnaireActionMinHeight()),
        enabled = enabled && ctrl.canSkip,
        disabled = disabled,
        variant = variant,
        size = size,
        style = style,
    )
}

@Composable
fun QuestionnaireSkip(
    text: String = "Skip",
    modifier: Modifier = Modifier,
    enabled: Boolean = true,
    disabled: Boolean = false,
    variant: ButtonVariant = ButtonVariant.Outline,
    size: ButtonSize = ButtonSize.Md,
    style: Style = Style,
) {
    val ctrl = questionnaireController()
    if (!ctrl.canSkip) return
    Button(
        text = text,
        onClick = ctrl::skip,
        modifier = modifier.heightIn(min = questionnaireActionMinHeight()),
        enabled = enabled && ctrl.canSkip,
        disabled = disabled,
        variant = variant,
        size = size,
        style = style,
    )
}

@Composable
private fun QuestionnaireNextButton(
    text: String,
    modifier: Modifier,
    enabled: Boolean,
    disabled: Boolean,
    variant: ButtonVariant,
    size: ButtonSize,
    style: Style,
) {
    val ctrl = questionnaireController()
    if (ctrl.isLast || ctrl.enabledNames().size <= 1) return
    Button(
        text = text,
        onClick = ctrl::next,
        modifier = modifier.heightIn(min = questionnaireActionMinHeight()),
        enabled = enabled,
        disabled = disabled,
        variant = variant,
        size = size,
        style = style,
    )
}

@Composable
fun RowScope.QuestionnaireNext(
    text: String = "Next",
    modifier: Modifier = Modifier,
    enabled: Boolean = true,
    disabled: Boolean = false,
    variant: ButtonVariant = ButtonVariant.Default,
    size: ButtonSize = ButtonSize.Md,
    style: Style = Style,
) {
    val ctrl = questionnaireController()
    if (ctrl.isLast || ctrl.enabledNames().size <= 1) return
    Box(modifier = modifier, contentAlignment = Alignment.CenterEnd) {
        QuestionnaireNextButton(
            text = text,
            modifier = Modifier,
            enabled = enabled,
            disabled = disabled,
            variant = variant,
            size = size,
            style = style,
        )
    }
}

@Composable
fun QuestionnaireNext(
    text: String = "Next",
    modifier: Modifier = Modifier,
    enabled: Boolean = true,
    disabled: Boolean = false,
    variant: ButtonVariant = ButtonVariant.Default,
    size: ButtonSize = ButtonSize.Md,
    style: Style = Style,
) {
    QuestionnaireNextButton(
        text = text,
        modifier = modifier.heightIn(min = questionnaireActionMinHeight()),
        enabled = enabled,
        disabled = disabled,
        variant = variant,
        size = size,
        style = style,
    )
}

@Composable
private fun QuestionnaireSubmitButton(
    text: String,
    modifier: Modifier,
    enabled: Boolean,
    disabled: Boolean,
    variant: ButtonVariant,
    size: ButtonSize,
    style: Style,
) {
    val ctrl = questionnaireController()
    if (!ctrl.isLast) return
    Button(
        text = text,
        onClick = ctrl::submit,
        modifier = modifier.heightIn(min = questionnaireActionMinHeight()),
        enabled = enabled,
        disabled = disabled,
        variant = variant,
        size = size,
        style = style,
    )
}

@Composable
fun RowScope.QuestionnaireSubmit(
    text: String = "Submit",
    modifier: Modifier = Modifier,
    enabled: Boolean = true,
    disabled: Boolean = false,
    variant: ButtonVariant = ButtonVariant.Default,
    size: ButtonSize = ButtonSize.Md,
    style: Style = Style,
) {
    val ctrl = questionnaireController()
    if (!ctrl.isLast) return
    Box(modifier = modifier, contentAlignment = Alignment.CenterEnd) {
        QuestionnaireSubmitButton(
            text = text,
            modifier = Modifier,
            enabled = enabled,
            disabled = disabled,
            variant = variant,
            size = size,
            style = style,
        )
    }
}

@Composable
fun QuestionnaireSubmit(
    text: String = "Submit",
    modifier: Modifier = Modifier,
    enabled: Boolean = true,
    disabled: Boolean = false,
    variant: ButtonVariant = ButtonVariant.Default,
    size: ButtonSize = ButtonSize.Md,
    style: Style = Style,
) {
    QuestionnaireSubmitButton(
        text = text,
        modifier = modifier.heightIn(min = questionnaireActionMinHeight()),
        enabled = enabled,
        disabled = disabled,
        variant = variant,
        size = size,
        style = style,
    )
}

@Composable
fun RowScope.QuestionnaireReset(
    text: String = "Reset changes",
    modifier: Modifier = Modifier,
    enabled: Boolean = true,
    disabled: Boolean = false,
    variant: ButtonVariant = ButtonVariant.Outline,
    size: ButtonSize = ButtonSize.Md,
    style: Style = Style,
) {
    val ctrl = questionnaireController()
    Button(
        text = text,
        onClick = ctrl::reset,
        modifier = modifier.heightIn(min = questionnaireActionMinHeight()),
        enabled = enabled,
        disabled = disabled,
        variant = variant,
        size = size,
        style = style,
    )
}

/**
 * Form `type="reset"` — restores `defaultItem` / `defaultChecked` / `defaultValue`.
 * Compose helper: Base `questionnaire.tsx` does not export Reset.
 */
@Composable
fun QuestionnaireReset(
    text: String = "Reset changes",
    modifier: Modifier = Modifier,
    enabled: Boolean = true,
    disabled: Boolean = false,
    variant: ButtonVariant = ButtonVariant.Outline,
    size: ButtonSize = ButtonSize.Md,
    style: Style = Style,
) {
    val ctrl = questionnaireController()
    Button(
        text = text,
        onClick = ctrl::reset,
        modifier = modifier.heightIn(min = questionnaireActionMinHeight()),
        enabled = enabled,
        disabled = disabled,
        variant = variant,
        size = size,
        style = style,
    )
}

@Composable
private fun questionnaireActionMinHeight(): Dp =
    if (currentTheme.activePreset == StylePreset.Nova &&
        with(LocalDensity.current) { LocalWindowInfo.current.containerSize.width < 640.dp.roundToPx() }
    ) {
        44.dp
    } else {
        0.dp
    }
