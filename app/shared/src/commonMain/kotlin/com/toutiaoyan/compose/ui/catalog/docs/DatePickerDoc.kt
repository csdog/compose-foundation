@file:OptIn(androidx.compose.foundation.style.ExperimentalFoundationStyleApi::class)

package com.toutiaoyan.compose.ui.catalog.docs

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.style.Style
import androidx.compose.foundation.style.contentPadding
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.ColorFilter
import androidx.compose.ui.input.key.Key
import androidx.compose.ui.input.key.KeyEventType
import androidx.compose.ui.input.key.key
import androidx.compose.ui.input.key.onPreviewKeyEvent
import androidx.compose.ui.input.key.type
import androidx.compose.ui.platform.LocalLayoutDirection
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.unit.LayoutDirection
import androidx.compose.ui.unit.dp
import com.composables.icons.lucide.ChevronDown
import com.composables.icons.lucide.Lucide
import com.composables.icons.lucide.*
import com.toutiaoyan.compose.ui.catalog.catalogCalendarCjkFontFamily
import com.toutiaoyan.compose.ui.components.button.Button
import com.toutiaoyan.compose.ui.components.button.ButtonVariant
import com.toutiaoyan.compose.ui.components.calendar.Calendar
import com.toutiaoyan.compose.ui.components.calendar.CalendarCaptionLayout
import com.toutiaoyan.compose.ui.components.calendar.CalendarDate
import com.toutiaoyan.compose.ui.components.calendar.CalendarDateRange
import com.toutiaoyan.compose.ui.components.calendar.CalendarLocale
import com.toutiaoyan.compose.ui.components.calendar.LocalCalendarAutoFocus
import com.toutiaoyan.compose.ui.components.calendar.LocalCalendarLocale
import com.toutiaoyan.compose.ui.components.calendar.dayOfWeek
import com.toutiaoyan.compose.ui.components.field.Field
import com.toutiaoyan.compose.ui.components.field.FieldLabel
import com.toutiaoyan.compose.ui.components.input.Input
import com.toutiaoyan.compose.ui.components.inputgroup.InputGroup
import com.toutiaoyan.compose.ui.components.inputgroup.InputGroupAddon
import com.toutiaoyan.compose.ui.components.inputgroup.InputGroupAddonAlign
import com.toutiaoyan.compose.ui.components.inputgroup.InputGroupButton
import com.toutiaoyan.compose.ui.components.inputgroup.InputGroupInput
import com.toutiaoyan.compose.ui.components.overlay.PopupAlign
import com.toutiaoyan.compose.ui.components.popover.Popover
import com.toutiaoyan.compose.ui.components.popover.PopoverContent
import com.toutiaoyan.compose.ui.components.popover.PopoverScope
import com.toutiaoyan.compose.ui.components.popover.PopoverTrigger
import com.toutiaoyan.compose.ui.components.text.Text
import com.toutiaoyan.compose.ui.components.text.TextColors
import com.toutiaoyan.compose.ui.theme.Theme
import com.toutiaoyan.compose.ui.theme.currentTheme
import kotlinx.datetime.DateTimeUnit
import kotlinx.datetime.LocalDate
import kotlinx.datetime.plus
import kotlinx.datetime.number

private val monthNames =
    listOf(
        "January", "February", "March", "April", "May", "June",
        "July", "August", "September", "October", "November", "December",
    )

private fun formatDate(date: CalendarDate): String = "${monthNames[date.month - 1]} ${date.day}, ${date.year}"

private fun formatShortDate(date: CalendarDate): String =
    "${monthNames[date.month - 1].take(
        3,
    )} ${date.day}, ${date.year}"

private fun formatRange(range: CalendarDateRange): String {
    val from = range.from ?: return "Pick a date"
    val to = range.to ?: return formatShortDate(from)
    return "${formatShortDate(from)} - ${formatShortDate(to)}"
}

private fun formatDates(dates: Set<CalendarDate>): String =
    if (dates.isEmpty()) "Pick dates" else dates.sorted().joinToString { formatDate(it) }

private fun plusDays(
    date: CalendarDate,
    days: Int,
): CalendarDate {
    val next = LocalDate(date.year, date.month, date.day).plus(days, DateTimeUnit.DAY)
    return CalendarDate(next.year, next.month.number, next.day)
}

/** chrono-node is JavaScript-only; this Kotlin recipe parses the common relative phrases. */
private fun parseNaturalDate(
    value: String,
    today: CalendarDate,
): CalendarDate? {
    val lower = value.trim().lowercase()
    return when {
        lower == "tomorrow" -> plusDays(today, 1)
        lower == "next week" -> plusDays(today, 7)
        lower == "yesterday" -> plusDays(today, -1)
        else -> {
            Regex("in\\s+(\\d+)\\s+days?").matchEntire(lower)?.groupValues?.get(1)?.toIntOrNull()?.let {
                runCatching { plusDays(today, it) }.getOrNull()
            }
        }
    }
}

/** Kotlin input recipe accepts the displayed English date or an ISO calendar date. */
private fun parseInputDate(value: String): CalendarDate? =
    runCatching {
        val text = value.trim()
        val date =
            if (Regex("\\d{4}-\\d{2}-\\d{2}").matches(text)) {
                LocalDate.parse(text)
            } else {
                val match = Regex("([A-Za-z]+) (\\d{1,2}), (\\d{4})").matchEntire(text) ?: return null
                val month = monthNames.indexOfFirst { it.equals(match.groupValues[1], ignoreCase = true) } + 1
                LocalDate(match.groupValues[3].toInt(), month, match.groupValues[2].toInt())
            }
        CalendarDate(date.year, date.month.number, date.day)
    }.getOrNull()

private fun formatInputDate(date: CalendarDate): String =
    "${monthNames[date.month - 1]} ${date.day.toString().padStart(2, '0')}, ${date.year}"

private fun Modifier.openDatePicker(onOpen: () -> Unit): Modifier =
    onPreviewKeyEvent {
        if (it.type == KeyEventType.KeyDown && it.key == Key.DirectionDown) {
            onOpen()
            true
        } else {
            false
        }
    }

private val arabicMonthNames =
    listOf(
        "يناير", "فبراير", "مارس", "أبريل", "مايو", "يونيو",
        "يوليو", "أغسطس", "سبتمبر", "أكتوبر", "نوفمبر", "ديسمبر",
    )

private val arabicLocale =
    CalendarLocale(
        months = arabicMonthNames,
        shortMonths =
            listOf(
                "يناير", "فبراير", "مارس", "أبريل", "مايو", "يونيو",
                "يوليو", "أغسطس", "سبتمبر", "أكتوبر", "نوفمبر", "ديسمبر",
            ),
        weekdays =
            listOf("الأحد", "الاثنين", "الثلاثاء", "الأربعاء", "الخميس", "الجمعة", "السبت"),
        previousMonth = "الشهر السابق",
        nextMonth = "الشهر التالي",
        firstDayOfWeek = 6,
        pickDate = "اختر تاريخًا",
        formatDate = { date -> "${date.day} ${arabicMonthNames[date.month - 1]} ${date.year}" },
        formatAccessibleDate = { date -> "${date.year}-${date.month}-${date.day}" },
    )

/** One popover + calendar pair; each recipe only varies the trigger and calendar options. */
@Composable
private fun DatePickerRecipe(
    trigger: @Composable () -> Unit,
    content: @Composable () -> Unit,
) {
    var open by remember { mutableStateOf(false) }
    Popover(open = open, onOpenChange = { open = it }) {
        PopoverTrigger { trigger() }
        PopoverContent(align = PopupAlign.Start, style = Style { contentPadding(0.dp) }) { content() }
    }
}

@Composable
private fun PopoverScope.DateTrigger(
    text: String,
    empty: Boolean,
    width: androidx.compose.ui.unit.Dp? = null,
    trailingChevron: Boolean = true,
    leadingCalendar: Boolean = false,
) {
    Button(
        onClick = toggle,
        variant = ButtonVariant.Outline,
        modifier = width?.let { Modifier.width(it) } ?: Modifier,
    ) {
        Row(Modifier.fillMaxWidth(), verticalAlignment = Alignment.CenterVertically) {
            if (leadingCalendar) {
                Image(
                    Lucide.Calendar,
                    null,
                    Modifier.size(16.dp),
                    colorFilter = ColorFilter.tint(currentTheme.colors.mutedForeground),
                )
            }
            Text(
                Modifier.weight(1f),
                textStyle = if (empty) TextColors.Muted else TextStyle.Default,
            ) { text }
            if (trailingChevron) {
                Image(
                    Lucide.ChevronDown,
                    null,
                    Modifier.size(16.dp),
                    colorFilter = ColorFilter.tint(currentTheme.colors.mutedForeground),
                )
            }
        }
    }
}

@Composable
private fun DateCalendar(
    year: Int,
    month: Int,
    onMonthChange: (Int, Int) -> Unit,
    selected: CalendarDate?,
    onSelectedChange: (CalendarDate) -> Unit,
    captionLayout: CalendarCaptionLayout = CalendarCaptionLayout.Label,
    numberOfMonths: Int = 1,
) {
    val locale = LocalCalendarLocale.current
    val today = remember { currentCalendarDate() }
    CompositionLocalProvider(LocalCalendarAutoFocus provides true) {
        Calendar(
            year = year,
            month = month,
            onMonthChange = onMonthChange,
            selected = selected,
            onSelectedChange = onSelectedChange,
            today = today,
            fixedWeeks = false,
            captionLayout = captionLayout,
            numberOfMonths = numberOfMonths,
        )
    }
}

private val datePickerUsageCode =
    """
    val today = remember { currentCalendarDate() }
    val locale = LocalCalendarLocale.current
    var open by remember { mutableStateOf(false) }
    var year by remember { mutableStateOf(today.year) }
    var month by remember { mutableStateOf(today.month) }
    var selected by remember { mutableStateOf<CalendarDate?>(null) }
    Popover(open = open, onOpenChange = { open = it }) {
        PopoverTrigger {
            Button(onClick = toggle, variant = ButtonVariant.Outline) {
                Text { selected?.let(locale.formatDate) ?: locale.pickDate }
            }
        }
        PopoverContent(align = PopupAlign.Start, style = Style { contentPadding(0.dp) }) {
            Calendar(
                year = year, month = month,
                onMonthChange = { y, m -> year = y; month = m },
                selected = selected,
                onSelectedChange = { selected = it },
            )
        }
    }
    """.trimIndent()

/** Small helper shared by the page previews and their exported code. */
@Composable
private fun DatePickerField(
    label: String,
    width: androidx.compose.ui.unit.Dp = 320.dp,
    content: @Composable () -> Unit,
) {
    Field(Modifier.widthIn(max = width)) {
        FieldLabel(label)
        content()
    }
}

private fun datePickerExampleCode(
    label: String,
    body: String,
): String =
    """
    @Composable
    fun Example() {
    $body
    }
    """.trimIndent()

val datePickerDoc =
    ComponentDoc(
        id = "date-picker",
        title = "Date Picker",
        description = "A date picker component with range and presets.",
        usageCode = datePickerUsageCode,
        introMarkdown =
            "The Date Picker is built using a composition of the `Popover` and the `Calendar` components. " +
                "See installation instructions for [Popover](/popover) and [Calendar](/calendar).",
        compositionMarkdown =
            "See the [React DayPicker documentation](https://react-day-picker.js.org) for the upstream behavior. " +
                "The Kotlin input recipe accepts English month dates (June 01, 2025) or ISO dates (2025-06-01). " +
                "The natural-language recipe supports tomorrow, yesterday, next week and in N days.",
        compositionCode =
            """
            Popover
            ├── PopoverTrigger
            └── PopoverContent
                └── Calendar
            """.trimIndent(),
        examplesHeading = null,
        examples =
            listOf(
                ComponentExample(
                    title = "Default",
                    code =
                        """
                        @Composable
                        fun DatePickerDemo() {
                            var open by remember { mutableStateOf(false) }
                            val locale = LocalCalendarLocale.current
                            val today = remember { currentCalendarDate() }
                            var year by remember { mutableStateOf(today.year) }
                            var month by remember { mutableStateOf(today.month) }
                            var selected by remember { mutableStateOf<CalendarDate?>(null) }
                            Popover(open = open, onOpenChange = { open = it }) {
                                PopoverTrigger {
                                    Button(onClick = toggle, variant = ButtonVariant.Outline, modifier = Modifier.width(212.dp)) {
                                        Row(Modifier.fillMaxWidth(), verticalAlignment = Alignment.CenterVertically) {
                                            Text(Modifier.weight(1f), textStyle = if (selected == null) TextColors.Muted else TextStyle.Default) {
                                                selected?.let(locale.formatDate) ?: locale.pickDate
                                            }
                                            Image(Lucide.ChevronDown, null, Modifier.size(16.dp))
                                        }
                                    }
                                }
                                PopoverContent(
                                    align = PopupAlign.Start,
                                    style = Style { width(214.dp); contentPadding(0.dp) },
                                ) {
                                    CompositionLocalProvider(LocalCalendarAutoFocus provides true) {
                                        Calendar(
                                            year = year, month = month,
                                            onMonthChange = { y, m -> year = y; month = m },
                                            selected = selected, today = today,
                                            onSelectedChange = { selected = it },
                                        )
                                    }
                                }
                            }
                        }
                        """.trimIndent(),
                    preview = { DatePickerParityExample() },
                ),
                ComponentExample(
                    title = "Basic",
                    descriptionMarkdown = "A basic date picker component.",
                    code =
                        """
                        var open by remember { mutableStateOf(false) }
                        val today = remember { currentCalendarDate() }
                        var year by remember { mutableStateOf(today.year) }
                        var month by remember { mutableStateOf(today.month) }
                        var selected by remember { mutableStateOf<CalendarDate?>(null) }
                        Field(Modifier.widthIn(max = 176.dp)) {
                            FieldLabel("Date")
                            Popover(open = open, onOpenChange = { open = it }) {
                                PopoverTrigger {
                                    Button(onClick = toggle, variant = ButtonVariant.Outline) {
                                        Text { selected?.let(::formatDate) ?: "Pick a date" }
                                    }
                                }
                                PopoverContent(align = PopupAlign.Start, style = Style { contentPadding(0.dp) }) {
                                    Calendar(
                                        year = year, month = month,
                                        onMonthChange = { y, m -> year = y; month = m },
                                        selected = selected,
                                        onSelectedChange = { selected = it },
                                    )
                                }
                            }
                        }
                        """.trimIndent(),
                    preview = {
                        var open by remember { mutableStateOf(false) }
                        val today = remember { currentCalendarDate() }
                        var year by remember { mutableStateOf(today.year) }
                        var month by remember { mutableStateOf(today.month) }
                        var selected by remember { mutableStateOf<CalendarDate?>(null) }
                        DatePickerField("Date", 176.dp) {
                            Popover(open = open, onOpenChange = { open = it }) {
                                PopoverTrigger {
                                    DateTrigger(
                                        text = selected?.let(::formatDate) ?: "Pick a date",
                                        empty = selected == null,
                                        trailingChevron = false,
                                    )
                                }
                                PopoverContent(align = PopupAlign.Start, style = Style { contentPadding(0.dp) }) {
                                    DateCalendar(
                                        year = year,
                                        month = month,
                                        onMonthChange = { y, m ->
                                            year = y
                                            month = m
                                        },
                                        selected = selected,
                                        onSelectedChange = { selected = it },
                                    )
                                }
                            }
                        }
                    },
                ),
                ComponentExample(
                    title = "Range Picker",
                    descriptionMarkdown = "A date picker component for selecting a range of dates.",
                    code =
                        """
                        var open by remember { mutableStateOf(false) }
                        val today = remember { currentCalendarDate() }
                        var year by remember { mutableStateOf(today.year) }
                        var month by remember { mutableStateOf(1) }
                        var range by remember {
                            mutableStateOf(CalendarDateRange(CalendarDate(today.year, 1, 20), CalendarDate(today.year, 2, 9)))
                        }
                        Field(Modifier.widthIn(max = 240.dp)) {
                            FieldLabel("Date Picker Range")
                            Popover(open = open, onOpenChange = { open = it }) {
                                PopoverTrigger {
                                    Button(onClick = toggle, variant = ButtonVariant.Outline) {
                                        Text { formatRange(range) }
                                    }
                                }
                                PopoverContent(align = PopupAlign.Start, style = Style { contentPadding(0.dp) }) {
                                    Calendar(
                                        year = year, month = month,
                                        onMonthChange = { y, m -> year = y; month = m },
                                        selectedRange = range,
                                        numberOfMonths = 2,
                                        onSelectedRangeChange = { range = it },
                                    )
                                }
                            }
                        }
                        """.trimIndent(),
                    preview = {
                        var open by remember { mutableStateOf(false) }
                        val today = remember { currentCalendarDate() }
                        var year by remember { mutableStateOf(today.year) }
                        var month by remember { mutableStateOf(1) }
                        var range by remember {
                            mutableStateOf(
                                CalendarDateRange(
                                    CalendarDate(today.year, 1, 20),
                                    CalendarDate(today.year, 2, 9),
                                ),
                            )
                        }
                        DatePickerField("Date Picker Range", 240.dp) {
                            Popover(open = open, onOpenChange = { open = it }) {
                                PopoverTrigger {
                                    DateTrigger(
                                        text = formatRange(range),
                                        empty = range.from == null,
                                        leadingCalendar = true,
                                        trailingChevron = false,
                                    )
                                }
                                PopoverContent(align = PopupAlign.Start, style = Style { contentPadding(0.dp) }) {
                                    val locale = LocalCalendarLocale.current
                                    val now = remember { currentCalendarDate() }
                                    CompositionLocalProvider(LocalCalendarAutoFocus provides true) {
                                        Calendar(
                                            year = year,
                                            month = month,
                                            onMonthChange = { y, m ->
                                                year = y
                                                month = m
                                            },
                                            selectedRange = range,
                                            today = now,
                                            numberOfMonths = 2,
                                            onSelectedRangeChange = { range = it },
                                        )
                                    }
                                }
                            }
                        }
                    },
                ),
                ComponentExample(
                    title = "Date of Birth",
                    descriptionMarkdown =
                        "A date picker component for selecting a date of birth. This component " +
                            "includes a dropdown caption layout for date and month selection.",
                    code =
                        """
                        var open by remember { mutableStateOf(false) }
                        val today = remember { currentCalendarDate() }
                        var year by remember { mutableStateOf(today.year) }
                        var month by remember { mutableStateOf(today.month) }
                        var selected by remember { mutableStateOf<CalendarDate?>(null) }
                        Field(Modifier.widthIn(max = 176.dp)) {
                            FieldLabel("Date of birth")
                            Popover(open = open, onOpenChange = { open = it }) {
                                PopoverTrigger {
                                    Button(onClick = toggle, variant = ButtonVariant.Outline) {
                                        Text { selected?.let(::formatDate) ?: "Select date" }
                                    }
                                }
                                PopoverContent(align = PopupAlign.Start, style = Style { contentPadding(0.dp) }) {
                                    Calendar(
                                        year = year, month = month,
                                        onMonthChange = { y, m -> year = y; month = m },
                                        selected = selected,
                                        captionLayout = CalendarCaptionLayout.Dropdown,
                                        onSelectedChange = { selected = it; open = false },
                                    )
                                }
                            }
                        }
                        """.trimIndent(),
                    preview = {
                        var open by remember { mutableStateOf(false) }
                        val today = remember { currentCalendarDate() }
                        var year by remember { mutableStateOf(today.year) }
                        var month by remember { mutableStateOf(today.month) }
                        var selected by remember { mutableStateOf<CalendarDate?>(null) }
                        DatePickerField("Date of birth", 176.dp) {
                            Popover(open = open, onOpenChange = { open = it }) {
                                PopoverTrigger {
                                    DateTrigger(
                                        text = selected?.let(::formatDate) ?: "Select date",
                                        empty = selected == null,
                                        trailingChevron = false,
                                    )
                                }
                                PopoverContent(align = PopupAlign.Start, style = Style { contentPadding(0.dp) }) {
                                    DateCalendar(
                                        year = year,
                                        month = month,
                                        onMonthChange = { y, m ->
                                            year = y
                                            month = m
                                        },
                                        selected = selected,
                                        captionLayout = CalendarCaptionLayout.Dropdown,
                                        onSelectedChange = {
                                            selected = it
                                            open = false
                                        },
                                    )
                                }
                            }
                        }
                    },
                ),
                ComponentExample(
                    title = "Input",
                    descriptionMarkdown = "A date picker component with an input field for selecting a date.",
                    code =
                        """
                        var open by remember { mutableStateOf(false) }
                        var value by remember { mutableStateOf("June 01, 2025") }
                        var selected by remember { mutableStateOf(CalendarDate(2025, 6, 1)) }
                        var year by remember { mutableStateOf(2025) }
                        var month by remember { mutableStateOf(6) }
                        Field(Modifier.widthIn(max = 192.dp)) {
                            FieldLabel("Subscription Date")
                            InputGroup {
                                InputGroupInput(
                                    value = value,
                                    onValueChange = {
                                        value = it
                                        parseInputDate(it)?.let { parsed ->
                                            selected = parsed
                                            year = parsed.year
                                            month = parsed.month
                                        }
                                    },
                                    modifier = Modifier.openDatePicker { open = true },
                                    placeholder = "June 01, 2025",
                                )
                                InputGroupAddon(align = InputGroupAddonAlign.InlineEnd) {
                                    Popover(open = open, onOpenChange = { open = it }) {
                                        PopoverTrigger {
                                            InputGroupButton(onClick = toggle, label = "Select date") {
                                                Image(Lucide.Calendar, null, Modifier.size(16.dp))
                                            }
                                        }
                                        PopoverContent(
                                            align = PopupAlign.End, alignOffset = (-8).dp, sideOffset = 10.dp,
                                        ) {
                                            Calendar(
                                                year = year, month = month,
                                                onMonthChange = { y, m -> year = y; month = m },
                                                selected = selected,
                                                onSelectedChange = {
                                                    selected = it
                                                    value = formatInputDate(it)
                                                    open = false
                                                },
                                            )
                                        }
                                    }
                                }
                            }
                        }
                        """.trimIndent(),
                    preview = {
                        var open by remember { mutableStateOf(false) }
                        var value by remember { mutableStateOf("June 01, 2025") }
                        var selected by remember { mutableStateOf<CalendarDate?>(CalendarDate(2025, 6, 1)) }
                        var year by remember { mutableStateOf(2025) }
                        var month by remember { mutableStateOf(6) }
                        DatePickerField("Subscription Date", 192.dp) {
                            InputGroup {
                                InputGroupInput(
                                    value = value,
                                    onValueChange = {
                                        value = it
                                        parseInputDate(it)?.let { parsed ->
                                            selected = parsed
                                            year = parsed.year
                                            month = parsed.month
                                        }
                                    },
                                    modifier = Modifier.openDatePicker { open = true },
                                    placeholder = "June 01, 2025",
                                )
                                InputGroupAddon(align = InputGroupAddonAlign.InlineEnd) {
                                    Popover(open = open, onOpenChange = { open = it }) {
                                        PopoverTrigger {
                                            InputGroupButton(
                                                onClick = { open = true },
                                                label = "Select date",
                                            ) {
                                                Image(
                                                    Lucide.Calendar,
                                                    null,
                                                    Modifier.size(16.dp),
                                                    colorFilter = ColorFilter.tint(currentTheme.colors.mutedForeground),
                                                )
                                            }
                                        }
                                        PopoverContent(
                                            align = PopupAlign.End,
                                            alignOffset = (-8).dp,
                                            sideOffset = 10.dp,
                                            style = Style { contentPadding(0.dp) },
                                        ) {
                                            DateCalendar(
                                                year = year,
                                                month = month,
                                                onMonthChange = { y, m ->
                                                    year = y
                                                    month = m
                                                },
                                                selected = selected,
                                                onSelectedChange = {
                                                    selected = it
                                                    value = formatInputDate(it)
                                                    open = false
                                                },
                                            )
                                        }
                                    }
                                }
                            }
                        }
                    },
                ),
                ComponentExample(
                    title = "Time Picker",
                    descriptionMarkdown = "A date picker component with a time input field for selecting a time.",
                    code =
                        """
                        // Kotlin note: Compose has no native <input type="time">. The Time field is a
                        // plain 24h text field that keeps the official layout and copy.
                        var open by remember { mutableStateOf(false) }
                        val today = remember { currentCalendarDate() }
                        var year by remember { mutableStateOf(today.year) }
                        var month by remember { mutableStateOf(today.month) }
                        var selected by remember { mutableStateOf<CalendarDate?>(null) }
                        var time by remember { mutableStateOf("10:30:00") }
                        Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                            Field(Modifier.width(128.dp)) {
                                FieldLabel("Date")
                                Popover(open = open, onOpenChange = { open = it }) {
                                    PopoverTrigger {
                                        Button(onClick = toggle, variant = ButtonVariant.Outline) {
                                            Text { selected?.let(::formatDate) ?: "Select date" }
                                        }
                                    }
                                    PopoverContent(align = PopupAlign.Start, style = Style { contentPadding(0.dp) }) {
                                        Calendar(
                                            year = year, month = month,
                                            onMonthChange = { y, m -> year = y; month = m },
                                            selected = selected,
                                            captionLayout = CalendarCaptionLayout.Dropdown,
                                            onSelectedChange = { selected = it; open = false },
                                        )
                                    }
                                }
                            }
                            Field(Modifier.width(128.dp)) {
                                FieldLabel("Time")
                                Input(value = time, onValueChange = { time = it })
                            }
                        }
                        """.trimIndent(),
                    preview = {
                        var open by remember { mutableStateOf(false) }
                        val today = remember { currentCalendarDate() }
                        var year by remember { mutableStateOf(today.year) }
                        var month by remember { mutableStateOf(today.month) }
                        var selected by remember { mutableStateOf<CalendarDate?>(null) }
                        var time by remember { mutableStateOf("10:30:00") }
                        Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                            Field(Modifier.width(128.dp)) {
                                FieldLabel("Date")
                                Popover(open = open, onOpenChange = { open = it }) {
                                    PopoverTrigger {
                                        DateTrigger(
                                            text = selected?.let(::formatDate) ?: "Select date",
                                            empty = selected == null,
                                        )
                                    }
                                    PopoverContent(
                                        align = PopupAlign.Start,
                                        style = Style { contentPadding(0.dp) },
                                    ) {
                                        DateCalendar(
                                            year = year,
                                            month = month,
                                            onMonthChange = { y, m ->
                                                year = y
                                                month = m
                                            },
                                            selected = selected,
                                            captionLayout = CalendarCaptionLayout.Dropdown,
                                            onSelectedChange = {
                                                selected = it
                                                open = false
                                            },
                                        )
                                    }
                                }
                            }
                            Field(Modifier.width(128.dp)) {
                                FieldLabel("Time")
                                Input(value = time, onValueChange = { time = it })
                            }
                        }
                    },
                ),
                ComponentExample(
                    title = "Natural Language Picker",
                    descriptionMarkdown =
                        "This component uses the `chrono-node` library to parse natural language dates.",
                    code =
                        """
                        // Kotlin note: chrono-node is JavaScript-only; this recipe replaces it with a
                        // small parser for "In N days", "tomorrow", "next week" and "yesterday".
                        var open by remember { mutableStateOf(false) }
                        var value by remember { mutableStateOf("In 2 days") }
                        val today = remember { currentCalendarDate() }
                        var selected by remember { mutableStateOf(parseNaturalDate("In 2 days", today)) }
                        var year by remember { mutableStateOf(selected?.year ?: today.year) }
                        var month by remember { mutableStateOf(selected?.month ?: today.month) }
                        Field(Modifier.widthIn(max = 320.dp)) {
                            FieldLabel("Schedule Date")
                            InputGroup {
                                InputGroupInput(
                                    value = value,
                                    onValueChange = {
                                        value = it
                                        parseNaturalDate(it, today)?.let { parsed ->
                                            selected = parsed
                                            year = parsed.year
                                            month = parsed.month
                                        }
                                    },
                                    modifier = Modifier.openDatePicker { open = true },
                                    placeholder = "Tomorrow or next week",
                                )
                                InputGroupAddon(align = InputGroupAddonAlign.InlineEnd) {
                                    Popover(open = open, onOpenChange = { open = it }) {
                                        PopoverTrigger {
                                            InputGroupButton(onClick = toggle, label = "Select date") {
                                                Image(Lucide.Calendar, null, Modifier.size(16.dp))
                                            }
                                        }
                                        PopoverContent(align = PopupAlign.End, sideOffset = 8.dp) {
                                            Calendar(
                                                year = year, month = month,
                                                onMonthChange = { y, m -> year = y; month = m },
                                                selected = selected,
                                                captionLayout = CalendarCaptionLayout.Dropdown,
                                                onSelectedChange = {
                                                    selected = it
                                                    value = formatInputDate(it)
                                                    open = false
                                                },
                                            )
                                        }
                                    }
                                }
                            }
                            Text(textStyle = TextColors.Muted) {
                                "Your post will be published on " +
                                    "${'$'}{selected?.let(::formatInputDate).orEmpty()}."
                            }
                        }
                        """.trimIndent(),
                    preview = {
                        var open by remember { mutableStateOf(false) }
                        var value by remember { mutableStateOf("In 2 days") }
                        val today = remember { currentCalendarDate() }
                        var selected by remember { mutableStateOf(parseNaturalDate("In 2 days", today)) }
                        var year by remember { mutableStateOf(selected?.year ?: today.year) }
                        var month by remember { mutableStateOf(selected?.month ?: today.month) }
                        DatePickerField("Schedule Date") {
                            InputGroup {
                                InputGroupInput(
                                    value = value,
                                    onValueChange = {
                                        value = it
                                        parseNaturalDate(it, today)?.let { parsed ->
                                            selected = parsed
                                            year = parsed.year
                                            month = parsed.month
                                        }
                                    },
                                    modifier = Modifier.openDatePicker { open = true },
                                    placeholder = "Tomorrow or next week",
                                )
                                InputGroupAddon(align = InputGroupAddonAlign.InlineEnd) {
                                    Popover(open = open, onOpenChange = { open = it }) {
                                        PopoverTrigger {
                                            InputGroupButton(
                                                onClick = { open = true },
                                                label = "Select date",
                                            ) {
                                                Image(
                                                    Lucide.Calendar,
                                                    null,
                                                    Modifier.size(16.dp),
                                                    colorFilter = ColorFilter.tint(currentTheme.colors.mutedForeground),
                                                )
                                            }
                                        }
                                        PopoverContent(
                                            align = PopupAlign.End,
                                            sideOffset = 8.dp,
                                            style = Style { contentPadding(0.dp) },
                                        ) {
                                            DateCalendar(
                                                year = year,
                                                month = month,
                                                onMonthChange = { y, m ->
                                                    year = y
                                                    month = m
                                                },
                                                selected = selected,
                                                captionLayout = CalendarCaptionLayout.Dropdown,
                                                onSelectedChange = {
                                                    selected = it
                                                    value = formatInputDate(it)
                                                    open = false
                                                },
                                            )
                                        }
                                    }
                                }
                            }
                            Text(textStyle = TextColors.Muted) {
                                "Your post will be published on " +
                                    "${selected?.let(::formatInputDate).orEmpty()}."
                            }
                        }
                    },
                ),
                ComponentExample(
                    title = "RTL",
                    descriptionMarkdown =
                        "To enable RTL support in shadcn/ui, see the " +
                            "[RTL configuration guide](https://ui.shadcn.com/docs/rtl).",
                    code =
                        """
                        // Kotlin note: the catalog ships Arabic month/weekday names instead of a
                        // locale bundle. Wrap the picker in RTL and provide the locale.
                        CompositionLocalProvider(
                            LocalCalendarLocale provides arabicLocale,
                            LocalLayoutDirection provides LayoutDirection.Rtl,
                        ) {
                            var open by remember { mutableStateOf(false) }
                            var selected by remember { mutableStateOf<CalendarDate?>(null) }
                            val today = remember { currentCalendarDate() }
                            var year by remember { mutableStateOf(today.year) }
                            var month by remember { mutableStateOf(today.month) }
                            Popover(open = open, onOpenChange = { open = it }) {
                                PopoverTrigger {
                                    Button(
                                        onClick = toggle,
                                        variant = ButtonVariant.Outline,
                                        modifier = Modifier.width(212.dp),
                                    ) {
                                        Row(Modifier.fillMaxWidth(), verticalAlignment = Alignment.CenterVertically) {
                                            Text(Modifier.weight(1f)) {
                                                selected?.let(arabicLocale.formatDate) ?: arabicLocale.pickDate
                                            }
                                            Image(Lucide.ChevronDown, null, Modifier.size(16.dp))
                                        }
                                    }
                                }
                                PopoverContent(align = PopupAlign.Start, style = Style { contentPadding(0.dp) }) {
                                    Calendar(
                                        year = year, month = month,
                                        onMonthChange = { y, m -> year = y; month = m },
                                        selected = selected,
                                        onSelectedChange = { selected = it },
                                    )
                                }
                            }
                        }
                        """.trimIndent(),
                    preview = {
                        CompositionLocalProvider(
                            LocalCalendarLocale provides arabicLocale,
                            LocalLayoutDirection provides LayoutDirection.Rtl,
                        ) {
                            var open by remember { mutableStateOf(false) }
                            var selected by remember { mutableStateOf<CalendarDate?>(null) }
                            val today = remember { currentCalendarDate() }
                            var year by remember { mutableStateOf(today.year) }
                            var month by remember { mutableStateOf(today.month) }
                            Popover(open = open, onOpenChange = { open = it }) {
                                PopoverTrigger {
                                    DateTrigger(
                                        text = selected?.let(arabicLocale.formatDate) ?: arabicLocale.pickDate,
                                        empty = selected == null,
                                        width = 212.dp,
                                    )
                                }
                                PopoverContent(
                                    align = PopupAlign.Start,
                                    style = Style { contentPadding(0.dp) },
                                ) {
                                    CompositionLocalProvider(LocalCalendarAutoFocus provides true) {
                                        Calendar(
                                            year = year,
                                            month = month,
                                            onMonthChange = { y, m ->
                                                year = y
                                                month = m
                                            },
                                            selected = selected,
                                            today = today,
                                            onSelectedChange = { selected = it },
                                        )
                                    }
                                }
                            }
                        }
                    },
                ),
                ComponentExample(
                    title = "Kotlin extension: Localized date picker",
                    code =
                        """
                        CompositionLocalProvider(LocalCalendarLocale provides CalendarLocale.Chinese) {
                            DatePickerParityExample()
                        }
                        """.trimIndent(),
                    preview = {
                        val theme = currentTheme
                        Theme(
                            preset = theme.activePreset,
                            baseColor = theme.activeBaseColor,
                            accent = theme.activeAccent,
                            isDark = theme.isDark,
                            baseRadius = theme.baseRadius,
                            ring = theme.ring,
                            minTouchTarget = theme.minTouchTarget,
                            fontFamily = catalogCalendarCjkFontFamily(),
                        ) {
                            CompositionLocalProvider(
                                LocalCalendarLocale provides CalendarLocale.Chinese,
                            ) { DatePickerParityExample() }
                        }
                    },
                ),
                ComponentExample(
                    title = "Kotlin extension: Multiple dates",
                    code =
                        """
                        var dates by remember { mutableStateOf(setOf<CalendarDate>()) }
                        Calendar(
                            year = year, month = month,
                            onMonthChange = { y, m -> year = y; month = m },
                            selectedDates = dates,
                            onSelectedDatesChange = { dates = it },
                        )
                        """.trimIndent(),
                    preview = {
                        var open by remember { mutableStateOf(false) }
                        val today = remember { currentCalendarDate() }
                        var year by remember { mutableStateOf(today.year) }
                        var month by remember { mutableStateOf(today.month) }
                        var dates by remember { mutableStateOf(setOf<CalendarDate>()) }
                        Popover(open = open, onOpenChange = { open = it }) {
                            PopoverTrigger {
                                DateTrigger(
                                    text = formatDates(dates),
                                    empty = dates.isEmpty(),
                                    trailingChevron = false,
                                )
                            }
                            PopoverContent(align = PopupAlign.Start, style = Style { contentPadding(0.dp) }) {
                                val now = remember { currentCalendarDate() }
                                Calendar(
                                    year = year,
                                    month = month,
                                    onMonthChange = { y, m ->
                                        year = y
                                        month = m
                                    },
                                    selectedDates = dates,
                                    today = now,
                                    onSelectedDatesChange = { dates = it },
                                )
                            }
                        }
                    },
                ),
                ComponentExample(
                    title = "Kotlin extension: Disabled weekends",
                    code =
                        """
                        Calendar(
                            year = year, month = month,
                            onMonthChange = { y, m -> year = y; month = m },
                            selected = selected,
                            onSelectedChange = { selected = it },
                            disabled = { date -> date.dayOfWeek() == 0 || date.dayOfWeek() == 6 },
                        )
                        """.trimIndent(),
                    preview = {
                        var open by remember { mutableStateOf(false) }
                        val today = remember { currentCalendarDate() }
                        var year by remember { mutableStateOf(today.year) }
                        var month by remember { mutableStateOf(today.month) }
                        var selected by remember { mutableStateOf<CalendarDate?>(null) }
                        Popover(open = open, onOpenChange = { open = it }) {
                            PopoverTrigger {
                                DateTrigger(
                                    text = selected?.let(::formatDate) ?: "Pick a date",
                                    empty = selected == null,
                                    trailingChevron = false,
                                )
                            }
                            PopoverContent(align = PopupAlign.Start, style = Style { contentPadding(0.dp) }) {
                                val now = remember { currentCalendarDate() }
                                Calendar(
                                    year = year,
                                    month = month,
                                    onMonthChange = { y, m ->
                                        year = y
                                        month = m
                                    },
                                    selected = selected,
                                    today = now,
                                    onSelectedChange = { selected = it },
                                    disabled = { it.dayOfWeek() == 0 || it.dayOfWeek() == 6 },
                                )
                            }
                        }
                    },
                ),
                ComponentExample(
                    title = "Kotlin extension: Min and max date",
                    code =
                        """
                        Calendar(
                            year = year, month = month,
                            onMonthChange = { y, m -> year = y; month = m },
                            selected = selected,
                            onSelectedChange = { selected = it },
                            fromDate = CalendarDate(2026, 3, 5),
                            toDate = CalendarDate(2026, 6, 20),
                        )
                        """.trimIndent(),
                    preview = {
                        var open by remember { mutableStateOf(false) }
                        var year by remember { mutableStateOf(2026) }
                        var month by remember { mutableStateOf(3) }
                        var selected by remember { mutableStateOf<CalendarDate?>(null) }
                        Popover(open = open, onOpenChange = { open = it }) {
                            PopoverTrigger {
                                DateTrigger(
                                    text = selected?.let(::formatDate) ?: "Pick a date",
                                    empty = selected == null,
                                    trailingChevron = false,
                                )
                            }
                            PopoverContent(align = PopupAlign.Start, style = Style { contentPadding(0.dp) }) {
                                val now = remember { currentCalendarDate() }
                                Calendar(
                                    year = year,
                                    month = month,
                                    onMonthChange = { y, m ->
                                        year = y
                                        month = m
                                    },
                                    selected = selected,
                                    today = now,
                                    onSelectedChange = { selected = it },
                                    fromDate = CalendarDate(2026, 3, 5),
                                    toDate = CalendarDate(2026, 6, 20),
                                )
                            }
                        }
                    },
                ),
            ),
        apiSections =
            listOf(
                ComponentApiSection(
                    "Popover + Calendar",
                    "A date picker is a Popover containing a Calendar; there is no DatePicker root component.",
                    listOf(
                        ApiProperty(
                            "selected / onSelectedChange",
                            "CalendarDate? / (CalendarDate) -> Unit",
                            "Required",
                        ),
                        ApiProperty(
                            "selectedRange / onSelectedRangeChange",
                            "CalendarDateRange? / (CalendarDateRange) -> Unit",
                            "range mode",
                        ),
                        ApiProperty(
                            "selectedDates / onSelectedDatesChange",
                            "Set<CalendarDate> / (Set<CalendarDate>) -> Unit",
                            "multiple mode",
                        ),
                        ApiProperty("captionLayout", "CalendarCaptionLayout", "Label (Dropdown for month/year)"),
                        ApiProperty("numberOfMonths", "Int", "1"),
                        ApiProperty("weekStartsOn", "Int", "locale firstDayOfWeek"),
                        ApiProperty("fromDate / toDate", "CalendarDate?", "null (inclusive exact-day limits)"),
                        ApiProperty(
                            "today / disabled / startMonth / endMonth",
                            "CalendarDate? / (CalendarDate) -> Boolean / CalendarDate?",
                            "null / none / null (month-level bounds)",
                        ),
                    ),
                ),
            ),
    )
