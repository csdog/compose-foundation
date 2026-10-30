@file:OptIn(androidx.compose.foundation.style.ExperimentalFoundationStyleApi::class)

package com.toutiaoyan.compose.ui.catalog.docs

import androidx.compose.runtime.CompositionLocalProvider
import com.toutiaoyan.compose.ui.theme.Theme
import com.toutiaoyan.compose.ui.catalog.catalogCalendarCjkFontFamily
import com.toutiaoyan.compose.ui.components.calendar.LocalCalendarLocale
import com.toutiaoyan.compose.ui.components.calendar.CalendarLocale
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import com.toutiaoyan.compose.ui.components.button.Button
import com.toutiaoyan.compose.ui.components.button.ButtonVariant
import com.toutiaoyan.compose.ui.components.calendar.Calendar
import com.toutiaoyan.compose.ui.components.calendar.CalendarCaption
import com.toutiaoyan.compose.ui.components.calendar.CalendarDate
import com.toutiaoyan.compose.ui.components.calendar.CalendarDateRange
import com.toutiaoyan.compose.ui.components.calendar.CalendarDefaultContent
import com.toutiaoyan.compose.ui.components.calendar.CalendarGrid
import com.toutiaoyan.compose.ui.components.calendar.CalendarHeader
import com.toutiaoyan.compose.ui.components.calendar.CalendarMonths
import com.toutiaoyan.compose.ui.components.calendar.CalendarNext
import com.toutiaoyan.compose.ui.components.calendar.CalendarPrevious
import com.toutiaoyan.compose.ui.components.calendar.CalendarTitle
import com.toutiaoyan.compose.ui.components.calendar.CalendarWeekdays
import com.toutiaoyan.compose.ui.components.calendar.dayOfWeek
import com.toutiaoyan.compose.ui.components.popover.Popover
import com.toutiaoyan.compose.ui.components.popover.PopoverContent
import com.toutiaoyan.compose.ui.components.popover.PopoverTrigger
import com.toutiaoyan.compose.ui.components.text.Text
import com.toutiaoyan.compose.ui.components.text.TextColors
import com.toutiaoyan.compose.ui.theme.currentTheme
import androidx.compose.ui.text.TextStyle

private val monthNames =
    listOf(
        "January", "February", "March", "April", "May", "June",
        "July", "August", "September", "October", "November", "December",
    )

private fun formatDate(date: CalendarDate): String = "${monthNames[date.month - 1]} ${date.day}, ${date.year}"

private fun formatRange(range: CalendarDateRange): String {
    val from = range.from ?: return "Pick a date range"
    val to = range.to ?: return formatDate(from)
    return "${formatDate(from)} – ${formatDate(to)}"
}

private fun formatDates(dates: Set<CalendarDate>): String =
    if (dates.isEmpty()) "Pick dates" else dates.sorted().joinToString { formatDate(it) }

/**
 * Not a standalone registry component in real shadcn/ui either -- their own docs
 * describe it as *"built using a composition of the `<Popover />` and the `<Calendar />`
 * components"*. This page mirrors that recipe with this library's own Popover/Calendar.
 */
val datePickerDoc =
    ComponentDoc(
        id = "date-picker",
        title = "Date Picker",
        description = "A date picker component with range and presets.",
        usageCode =
            """
            import com.toutiaoyan.compose.ui.components.*

            var open by remember { mutableStateOf(false) }
            var selected by remember { mutableStateOf<CalendarDate?>(null) }
            Popover(open = open, onOpenChange = { open = it }) {
                PopoverTrigger {
                    Button(onClick = toggle, variant = ButtonVariant.Outline) {
                        Text(textStyle = if (selected == null) TextStyles.Muted else TextStyle.Default) {
                            selected?.let(::formatDate) ?: "Pick a date"
                        }
                    }
                }
                PopoverContent {
                    Calendar(
                        year = year, month = month, onMonthChange = { y, m -> year = y; month = m },
                        selected = selected, onSelectedChange = { selected = it; open = false },
                    )
                }
            }
            """.trimIndent(),
        examples =
            listOf(
                ComponentExample(
                    title = "Default",
                    code =
                        """
                        @Composable
                        fun DatePickerParityExample() {
                            var open by remember { mutableStateOf(false) }
                            val locale = LocalCalendarLocale.current
                            val today = remember {
                                currentCalendarDate()
                            }
                            var year by remember { mutableStateOf(today.year) }
                            var month by remember { mutableStateOf(today.month) }
                            var selected by remember { mutableStateOf<CalendarDate?>(null) }
                            Popover(open = open, onOpenChange = { open = it }) {
                                PopoverTrigger {
                                    Button(onClick = toggle, variant = ButtonVariant.Outline, modifier = Modifier.width(212.dp)) {
                                        Row(Modifier.fillMaxWidth(), verticalAlignment = Alignment.CenterVertically) {
                                        Text(Modifier.weight(1f), textStyle = if (selected == null) TextColors.Muted else androidx.compose.ui.text.TextStyle.Default) {
                                            selected?.let(locale.formatDate) ?: locale.pickDate
                                        }
                                        Image(Lucide.ChevronDown, null, Modifier.size(16.dp), colorFilter = ColorFilter.tint(currentTheme.colors.mutedForeground))
                                        }
                                    }
                                }
                                PopoverContent(style = Style { width(214.dp); contentPadding(0.dp) }) {
                                    CompositionLocalProvider(LocalCalendarAutoFocus provides true) {
                                    Calendar(year = year, month = month, onMonthChange = { y, m -> year = y; month = m }, selected = selected,
                                        today = today, fixedWeeks = false, onSelectedChange = { selected = it })
                                    }
                                }
                            }
                        }

                        fun calendarDateAt(instant: Instant, zone: TimeZone): CalendarDate {
                            val date = instant.toLocalDateTime(zone).date
                            return CalendarDate(date.year, date.month.number, date.day)
                        }
                        fun currentCalendarDate(): CalendarDate = calendarDateAt(Clock.System.now(), TimeZone.currentSystemDefault())

                        """.trimIndent(),
                    preview = { DatePickerParityExample() },
                ),
                ComponentExample(
                    title = "Localized date picker",
                    code =
                        """
                        // Wasm requires a bundled CJK-capable font; this catalog uses a small licensed date-glyph subset.
                        val theme = currentTheme
                        Theme(preset = theme.activePreset, baseColor = theme.activeBaseColor, accent = theme.activeAccent,
                            isDark = theme.isDark, baseRadius = theme.baseRadius, ring = theme.ring,
                            fontFamily = catalogCalendarCjkFontFamily(), minTouchTarget = theme.minTouchTarget) {
                            CompositionLocalProvider(LocalCalendarLocale provides CalendarLocale.Chinese) { DatePickerParityExample() }
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
                    title = "Range",
                    code =
                        """
                        var open by remember { mutableStateOf(false) }
                        var year by remember { mutableStateOf(2026) }
                        var month by remember { mutableStateOf(1) }
                        var range by remember { mutableStateOf(CalendarDateRange(from = null, to = null)) }
                        Popover(open = open, onOpenChange = { open = it }) {
                            PopoverTrigger {
                                Button(onClick = toggle, variant = ButtonVariant.Outline) {
                                    Text(textStyle = if (range.from == null) TextStyles.Muted else TextStyle.Default) {
                                        formatRange(range)
                                    }
                                }
                            }
                            PopoverContent {
                                Calendar(
                                    year = year, month = month,
                                    onMonthChange = { y, m -> year = y; month = m },
                                    selectedRange = range,
                                    onSelectedRangeChange = {
                                        range = it
                                        if (it.from != null && it.to != null) open = false
                                    },
                                )
                            }
                        }
                        """.trimIndent(),
                    preview = {
                        var open by remember { mutableStateOf(false) }
                        var year by remember { mutableStateOf(2026) }
                        var month by remember { mutableStateOf(1) }
                        var range by remember { mutableStateOf(CalendarDateRange(from = null, to = null)) }
                        Popover(open = open, onOpenChange = { open = it }) {
                            PopoverTrigger {
                                Button(onClick = toggle, variant = ButtonVariant.Outline) {
                                    Text(textStyle = if (range.from == null) TextColors.Muted else TextStyle.Default) {
                                        formatRange(range)
                                    }
                                }
                            }
                            PopoverContent {
                                Calendar(
                                    year = year,
                                    month = month,
                                    onMonthChange = { y, m ->
                                        year = y
                                        month = m
                                    },
                                    selectedRange = range,
                                    onSelectedRangeChange = {
                                        range = it
                                        if (it.from != null && it.to != null) open = false
                                    },
                                )
                            }
                        }
                    },
                ),
                ComponentExample(
                    title = "Multiple",
                    code =
                        """
                        var open by remember { mutableStateOf(false) }
                        var year by remember { mutableStateOf(2026) }
                        var month by remember { mutableStateOf(1) }
                        var dates by remember { mutableStateOf(setOf<CalendarDate>()) }
                        Popover(open = open, onOpenChange = { open = it }) {
                            PopoverTrigger {
                                Button(onClick = toggle, variant = ButtonVariant.Outline) {
                                    Text(textStyle = if (dates.isEmpty()) TextStyles.Muted else TextStyle.Default) {
                                        formatDates(dates)
                                    }
                                }
                            }
                            PopoverContent {
                                Calendar(
                                    year = year, month = month,
                                    onMonthChange = { y, m -> year = y; month = m },
                                    selectedDates = dates, onSelectedDatesChange = { dates = it },
                                )
                            }
                        }
                        """.trimIndent(),
                    preview = {
                        var open by remember { mutableStateOf(false) }
                        var year by remember { mutableStateOf(2026) }
                        var month by remember { mutableStateOf(1) }
                        var dates by remember { mutableStateOf(setOf<CalendarDate>()) }
                        Popover(open = open, onOpenChange = { open = it }) {
                            PopoverTrigger {
                                Button(onClick = toggle, variant = ButtonVariant.Outline) {
                                    Text(textStyle = if (dates.isEmpty()) TextColors.Muted else TextStyle.Default) {
                                        formatDates(dates)
                                    }
                                }
                            }
                            PopoverContent {
                                Calendar(
                                    year = year,
                                    month = month,
                                    onMonthChange = { y, m ->
                                        year = y
                                        month = m
                                    },
                                    selectedDates = dates,
                                    onSelectedDatesChange = { dates = it },
                                )
                            }
                        }
                    },
                ),
                ComponentExample(
                    title = "Disabled",
                    code =
                        """
                        var open by remember { mutableStateOf(false) }
                        var year by remember { mutableStateOf(2026) }
                        var month by remember { mutableStateOf(1) }
                        var selected by remember { mutableStateOf<CalendarDate?>(null) }
                        Popover(open = open, onOpenChange = { open = it }) {
                            PopoverTrigger {
                                Button(onClick = toggle, variant = ButtonVariant.Outline) {
                                    Text(textStyle = if (selected == null) TextStyles.Muted else TextStyle.Default) {
                                        selected?.let(::formatDate) ?: "Pick a date"
                                    }
                                }
                            }
                            PopoverContent {
                                Calendar(
                                    year = year, month = month,
                                    onMonthChange = { y, m -> year = y; month = m },
                                    selected = selected, onSelectedChange = {
                                        selected = it
                                        open = false
                                    },
                                    disabled = { it.dayOfWeek() == 0 || it.dayOfWeek() == 6 },
                                )
                            }
                        }
                        """.trimIndent(),
                    preview = {
                        var open by remember { mutableStateOf(false) }
                        var year by remember { mutableStateOf(2026) }
                        var month by remember { mutableStateOf(1) }
                        var selected by remember { mutableStateOf<CalendarDate?>(null) }
                        Popover(open = open, onOpenChange = { open = it }) {
                            PopoverTrigger {
                                Button(onClick = toggle, variant = ButtonVariant.Outline) {
                                    Text(textStyle = if (selected == null) TextColors.Muted else TextStyle.Default) {
                                        selected?.let(::formatDate) ?: "Pick a date"
                                    }
                                }
                            }
                            PopoverContent {
                                Calendar(
                                    year = year,
                                    month = month,
                                    onMonthChange = { y, m ->
                                        year = y
                                        month = m
                                    },
                                    selected = selected,
                                    onSelectedChange = {
                                        selected = it
                                        open = false
                                    },
                                    disabled = { it.dayOfWeek() == 0 || it.dayOfWeek() == 6 },
                                )
                            }
                        }
                    },
                ),
                ComponentExample(
                    title = "Two months",
                    code =
                        """
                        var open by remember { mutableStateOf(false) }
                        var year by remember { mutableStateOf(2026) }
                        var month by remember { mutableStateOf(1) }
                        var selected by remember { mutableStateOf<CalendarDate?>(null) }
                        Popover(open = open, onOpenChange = { open = it }) {
                            PopoverTrigger {
                                Button(onClick = toggle, variant = ButtonVariant.Outline) {
                                    Text(textStyle = if (selected == null) TextStyles.Muted else TextStyle.Default) {
                                        selected?.let(::formatDate) ?: "Pick a date"
                                    }
                                }
                            }
                            PopoverContent {
                                Calendar(
                                    year = year, month = month,
                                    onMonthChange = { y, m -> year = y; month = m },
                                    selected = selected, onSelectedChange = {
                                        selected = it
                                        open = false
                                    },
                                    numberOfMonths = 2,
                                )
                            }
                        }
                        """.trimIndent(),
                    preview = {
                        var open by remember { mutableStateOf(false) }
                        var year by remember { mutableStateOf(2026) }
                        var month by remember { mutableStateOf(1) }
                        var selected by remember { mutableStateOf<CalendarDate?>(null) }
                        Popover(open = open, onOpenChange = { open = it }) {
                            PopoverTrigger {
                                Button(onClick = toggle, variant = ButtonVariant.Outline) {
                                    Text(textStyle = if (selected == null) TextColors.Muted else TextStyle.Default) {
                                        selected?.let(::formatDate) ?: "Pick a date"
                                    }
                                }
                            }
                            PopoverContent {
                                Calendar(
                                    year = year,
                                    month = month,
                                    onMonthChange = { y, m ->
                                        year = y
                                        month = m
                                    },
                                    selected = selected,
                                    onSelectedChange = {
                                        selected = it
                                        open = false
                                    },
                                    numberOfMonths = 2,
                                )
                            }
                        }
                    },
                ),
                ComponentExample(
                    title = "Default months",
                    code =
                        """
                        var open by remember { mutableStateOf(false) }
                        var year by remember { mutableStateOf(2026) }
                        var month by remember { mutableStateOf(3) }
                        var selected by remember { mutableStateOf<CalendarDate?>(null) }
                        Popover(open = open, onOpenChange = { open = it }) {
                            PopoverTrigger {
                                Button(onClick = toggle, variant = ButtonVariant.Outline) {
                                    Text(textStyle = if (selected == null) TextColors.Muted else TextStyle.Default) {
                                        selected?.let(::formatDate) ?: "Pick a date"
                                    }
                                }
                            }
                            PopoverContent {
                                Calendar(
                                    year = year, month = month,
                                    onMonthChange = { y, m -> year = y; month = m },
                                    selected = selected, onSelectedChange = {
                                        selected = it
                                        open = false
                                    },
                                    numberOfMonths = 1,
                                )
                            }
                        }
                        """.trimIndent(),
                    preview = {
                        var open by remember { mutableStateOf(false) }
                        var year by remember { mutableStateOf(2026) }
                        var month by remember { mutableStateOf(3) }
                        var selected by remember { mutableStateOf<CalendarDate?>(null) }
                        Popover(open = open, onOpenChange = { open = it }) {
                            PopoverTrigger {
                                Button(onClick = toggle, variant = ButtonVariant.Outline) {
                                    Text(textStyle = if (selected == null) TextColors.Muted else TextStyle.Default) {
                                        selected?.let(::formatDate) ?: "Pick a date"
                                    }
                                }
                            }
                            PopoverContent {
                                Calendar(
                                    year = year,
                                    month = month,
                                    onMonthChange = { y, m ->
                                        year = y
                                        month = m
                                    },
                                    selected = selected,
                                    onSelectedChange = {
                                        selected = it
                                        open = false
                                    },
                                    numberOfMonths = 1,
                                )
                            }
                        }
                    },
                ),
                ComponentExample(
                    title = "Week starts on Monday",
                    code =
                        """
                        var open by remember { mutableStateOf(false) }
                        var year by remember { mutableStateOf(2026) }
                        var month by remember { mutableStateOf(1) }
                        var selected by remember { mutableStateOf<CalendarDate?>(null) }
                        Popover(open = open, onOpenChange = { open = it }) {
                            PopoverTrigger {
                                Button(onClick = toggle, variant = ButtonVariant.Outline) {
                                    Text(textStyle = if (selected == null) TextStyles.Muted else TextStyle.Default) {
                                        selected?.let(::formatDate) ?: "Pick a date"
                                    }
                                }
                            }
                            PopoverContent {
                                Calendar(
                                    year = year, month = month,
                                    onMonthChange = { y, m -> year = y; month = m },
                                    selected = selected, onSelectedChange = {
                                        selected = it
                                        open = false
                                    },
                                    weekStartsOn = 1,
                                )
                            }
                        }
                        """.trimIndent(),
                    preview = {
                        var open by remember { mutableStateOf(false) }
                        var year by remember { mutableStateOf(2026) }
                        var month by remember { mutableStateOf(1) }
                        var selected by remember { mutableStateOf<CalendarDate?>(null) }
                        Popover(open = open, onOpenChange = { open = it }) {
                            PopoverTrigger {
                                Button(onClick = toggle, variant = ButtonVariant.Outline) {
                                    Text(textStyle = if (selected == null) TextColors.Muted else TextStyle.Default) {
                                        selected?.let(::formatDate) ?: "Pick a date"
                                    }
                                }
                            }
                            PopoverContent {
                                Calendar(
                                    year = year,
                                    month = month,
                                    onMonthChange = { y, m ->
                                        year = y
                                        month = m
                                    },
                                    selected = selected,
                                    onSelectedChange = {
                                        selected = it
                                        open = false
                                    },
                                    weekStartsOn = 1,
                                )
                            }
                        }
                    },
                ),
                ComponentExample(
                    title = "Default week start",
                    code =
                        """
                        var open by remember { mutableStateOf(false) }
                        var year by remember { mutableStateOf(2026) }
                        var month by remember { mutableStateOf(3) }
                        var selected by remember { mutableStateOf<CalendarDate?>(null) }
                        Popover(open = open, onOpenChange = { open = it }) {
                            PopoverTrigger {
                                Button(onClick = toggle, variant = ButtonVariant.Outline) {
                                    Text(textStyle = if (selected == null) TextColors.Muted else TextStyle.Default) {
                                        selected?.let(::formatDate) ?: "Pick a date"
                                    }
                                }
                            }
                            PopoverContent {
                                Calendar(
                                    year = year, month = month,
                                    onMonthChange = { y, m -> year = y; month = m },
                                    selected = selected, onSelectedChange = {
                                        selected = it
                                        open = false
                                    },
                                    weekStartsOn = 0,
                                )
                            }
                        }
                        """.trimIndent(),
                    preview = {
                        var open by remember { mutableStateOf(false) }
                        var year by remember { mutableStateOf(2026) }
                        var month by remember { mutableStateOf(3) }
                        var selected by remember { mutableStateOf<CalendarDate?>(null) }
                        Popover(open = open, onOpenChange = { open = it }) {
                            PopoverTrigger {
                                Button(onClick = toggle, variant = ButtonVariant.Outline) {
                                    Text(textStyle = if (selected == null) TextColors.Muted else TextStyle.Default) {
                                        selected?.let(::formatDate) ?: "Pick a date"
                                    }
                                }
                            }
                            PopoverContent {
                                Calendar(
                                    year = year,
                                    month = month,
                                    onMonthChange = { y, m ->
                                        year = y
                                        month = m
                                    },
                                    selected = selected,
                                    onSelectedChange = {
                                        selected = it
                                        open = false
                                    },
                                    weekStartsOn = 0,
                                )
                            }
                        }
                    },
                ),
                ComponentExample(
                    title = "Without navigation",
                    code =
                        """
                        var open by remember { mutableStateOf(false) }
                        var year by remember { mutableStateOf(2026) }
                        var month by remember { mutableStateOf(3) }
                        var selected by remember { mutableStateOf<CalendarDate?>(null) }
                        Popover(open = open, onOpenChange = { open = it }) {
                            PopoverTrigger {
                                Button(onClick = toggle, variant = ButtonVariant.Outline) {
                                    Text(textStyle = if (selected == null) TextColors.Muted else TextStyle.Default) {
                                        selected?.let(::formatDate) ?: "Pick a date"
                                    }
                                }
                            }
                            PopoverContent {
                                Calendar(
                                    year = year, month = month,
                                    onMonthChange = { y, m -> year = y; month = m },
                                    selected = selected, onSelectedChange = {
                                        selected = it
                                        open = false
                                    },
                                    hideNavigation = true,
                                )
                            }
                        }
                        """.trimIndent(),
                    preview = {
                        var open by remember { mutableStateOf(false) }
                        var year by remember { mutableStateOf(2026) }
                        var month by remember { mutableStateOf(3) }
                        var selected by remember { mutableStateOf<CalendarDate?>(null) }
                        Popover(open = open, onOpenChange = { open = it }) {
                            PopoverTrigger {
                                Button(onClick = toggle, variant = ButtonVariant.Outline) {
                                    Text(textStyle = if (selected == null) TextColors.Muted else TextStyle.Default) {
                                        selected?.let(::formatDate) ?: "Pick a date"
                                    }
                                }
                            }
                            PopoverContent {
                                Calendar(
                                    year = year,
                                    month = month,
                                    onMonthChange = { y, m ->
                                        year = y
                                        month = m
                                    },
                                    selected = selected,
                                    onSelectedChange = {
                                        selected = it
                                        open = false
                                    },
                                    hideNavigation = true,
                                )
                            }
                        }
                    },
                ),
                ComponentExample(
                    title = "Default hide navigation",
                    code =
                        """
                        var open by remember { mutableStateOf(false) }
                        var year by remember { mutableStateOf(2026) }
                        var month by remember { mutableStateOf(3) }
                        var selected by remember { mutableStateOf<CalendarDate?>(null) }
                        Popover(open = open, onOpenChange = { open = it }) {
                            PopoverTrigger {
                                Button(onClick = toggle, variant = ButtonVariant.Outline) {
                                    Text(textStyle = if (selected == null) TextColors.Muted else TextStyle.Default) {
                                        selected?.let(::formatDate) ?: "Pick a date"
                                    }
                                }
                            }
                            PopoverContent {
                                Calendar(
                                    year = year, month = month,
                                    onMonthChange = { y, m -> year = y; month = m },
                                    selected = selected, onSelectedChange = {
                                        selected = it
                                        open = false
                                    },
                                    hideNavigation = false,
                                )
                            }
                        }
                        """.trimIndent(),
                    preview = {
                        var open by remember { mutableStateOf(false) }
                        var year by remember { mutableStateOf(2026) }
                        var month by remember { mutableStateOf(3) }
                        var selected by remember { mutableStateOf<CalendarDate?>(null) }
                        Popover(open = open, onOpenChange = { open = it }) {
                            PopoverTrigger {
                                Button(onClick = toggle, variant = ButtonVariant.Outline) {
                                    Text(textStyle = if (selected == null) TextColors.Muted else TextStyle.Default) {
                                        selected?.let(::formatDate) ?: "Pick a date"
                                    }
                                }
                            }
                            PopoverContent {
                                Calendar(
                                    year = year,
                                    month = month,
                                    onMonthChange = { y, m ->
                                        year = y
                                        month = m
                                    },
                                    selected = selected,
                                    onSelectedChange = {
                                        selected = it
                                        open = false
                                    },
                                    hideNavigation = false,
                                )
                            }
                        }
                    },
                ),
                ComponentExample(
                    title = "Hide outside days",
                    code =
                        """
                        var open by remember { mutableStateOf(false) }
                        var year by remember { mutableStateOf(2026) }
                        var month by remember { mutableStateOf(3) }
                        var selected by remember { mutableStateOf<CalendarDate?>(null) }
                        Popover(open = open, onOpenChange = { open = it }) {
                            PopoverTrigger {
                                Button(onClick = toggle, variant = ButtonVariant.Outline) {
                                    Text(textStyle = if (selected == null) TextColors.Muted else TextStyle.Default) {
                                        selected?.let(::formatDate) ?: "Pick a date"
                                    }
                                }
                            }
                            PopoverContent {
                                Calendar(
                                    year = year, month = month,
                                    onMonthChange = { y, m -> year = y; month = m },
                                    selected = selected, onSelectedChange = {
                                        selected = it
                                        open = false
                                    },
                                    showOutsideDays = false,
                                )
                            }
                        }
                        """.trimIndent(),
                    preview = {
                        var open by remember { mutableStateOf(false) }
                        var year by remember { mutableStateOf(2026) }
                        var month by remember { mutableStateOf(3) }
                        var selected by remember { mutableStateOf<CalendarDate?>(null) }
                        Popover(open = open, onOpenChange = { open = it }) {
                            PopoverTrigger {
                                Button(onClick = toggle, variant = ButtonVariant.Outline) {
                                    Text(textStyle = if (selected == null) TextColors.Muted else TextStyle.Default) {
                                        selected?.let(::formatDate) ?: "Pick a date"
                                    }
                                }
                            }
                            PopoverContent {
                                Calendar(
                                    year = year,
                                    month = month,
                                    onMonthChange = { y, m ->
                                        year = y
                                        month = m
                                    },
                                    selected = selected,
                                    onSelectedChange = {
                                        selected = it
                                        open = false
                                    },
                                    showOutsideDays = false,
                                )
                            }
                        }
                    },
                ),
                ComponentExample(
                    title = "Outside days",
                    code =
                        """
                        var open by remember { mutableStateOf(false) }
                        var year by remember { mutableStateOf(2026) }
                        var month by remember { mutableStateOf(3) }
                        var selected by remember { mutableStateOf<CalendarDate?>(null) }
                        Popover(open = open, onOpenChange = { open = it }) {
                            PopoverTrigger {
                                Button(onClick = toggle, variant = ButtonVariant.Outline) {
                                    Text(textStyle = if (selected == null) TextColors.Muted else TextStyle.Default) {
                                        selected?.let(::formatDate) ?: "Pick a date"
                                    }
                                }
                            }
                            PopoverContent {
                                Calendar(
                                    year = year, month = month,
                                    onMonthChange = { y, m -> year = y; month = m },
                                    selected = selected, onSelectedChange = {
                                        selected = it
                                        open = false
                                    },
                                    showOutsideDays = true,
                                )
                            }
                        }
                        """.trimIndent(),
                    preview = {
                        var open by remember { mutableStateOf(false) }
                        var year by remember { mutableStateOf(2026) }
                        var month by remember { mutableStateOf(3) }
                        var selected by remember { mutableStateOf<CalendarDate?>(null) }
                        Popover(open = open, onOpenChange = { open = it }) {
                            PopoverTrigger {
                                Button(onClick = toggle, variant = ButtonVariant.Outline) {
                                    Text(textStyle = if (selected == null) TextColors.Muted else TextStyle.Default) {
                                        selected?.let(::formatDate) ?: "Pick a date"
                                    }
                                }
                            }
                            PopoverContent {
                                Calendar(
                                    year = year,
                                    month = month,
                                    onMonthChange = { y, m ->
                                        year = y
                                        month = m
                                    },
                                    selected = selected,
                                    onSelectedChange = {
                                        selected = it
                                        open = false
                                    },
                                    showOutsideDays = true,
                                )
                            }
                        }
                    },
                ),
                ComponentExample(
                    title = "Default outside days",
                    code =
                        """
                        var open by remember { mutableStateOf(false) }
                        var year by remember { mutableStateOf(2026) }
                        var month by remember { mutableStateOf(3) }
                        var selected by remember { mutableStateOf<CalendarDate?>(null) }
                        Popover(open = open, onOpenChange = { open = it }) {
                            PopoverTrigger {
                                Button(onClick = toggle, variant = ButtonVariant.Outline) {
                                    Text(textStyle = if (selected == null) TextColors.Muted else TextStyle.Default) {
                                        selected?.let(::formatDate) ?: "Pick a date"
                                    }
                                }
                            }
                            PopoverContent {
                                Calendar(
                                    year = year, month = month,
                                    onMonthChange = { y, m -> year = y; month = m },
                                    selected = selected, onSelectedChange = {
                                        selected = it
                                        open = false
                                    },
                                    showOutsideDays = true,
                                )
                            }
                        }
                        """.trimIndent(),
                    preview = {
                        var open by remember { mutableStateOf(false) }
                        var year by remember { mutableStateOf(2026) }
                        var month by remember { mutableStateOf(3) }
                        var selected by remember { mutableStateOf<CalendarDate?>(null) }
                        Popover(open = open, onOpenChange = { open = it }) {
                            PopoverTrigger {
                                Button(onClick = toggle, variant = ButtonVariant.Outline) {
                                    Text(textStyle = if (selected == null) TextColors.Muted else TextStyle.Default) {
                                        selected?.let(::formatDate) ?: "Pick a date"
                                    }
                                }
                            }
                            PopoverContent {
                                Calendar(
                                    year = year,
                                    month = month,
                                    onMonthChange = { y, m ->
                                        year = y
                                        month = m
                                    },
                                    selected = selected,
                                    onSelectedChange = {
                                        selected = it
                                        open = false
                                    },
                                    showOutsideDays = true,
                                )
                            }
                        }
                    },
                ),
                ComponentExample(
                    title = "Disabled navigation",
                    code =
                        """
                        var open by remember { mutableStateOf(false) }
                        var selected by remember { mutableStateOf<CalendarDate?>(null) }
                        Popover(open = open, onOpenChange = { open = it }) {
                            PopoverTrigger {
                                Button(onClick = toggle, variant = ButtonVariant.Outline) {
                                    Text(textStyle = if (selected == null) TextColors.Muted else TextStyle.Default) {
                                        selected?.let(::formatDate) ?: "Pick a date"
                                    }
                                }
                            }
                            PopoverContent {
                                Calendar(
                                    year = 2026, month = 3,
                                    onMonthChange = { _, _ -> },
                                    selected = selected, onSelectedChange = {
                                        selected = it
                                        open = false
                                    },
                                    disableNavigation = true,
                                )
                            }
                        }
                        """.trimIndent(),
                    preview = {
                        var open by remember { mutableStateOf(false) }
                        var selected by remember { mutableStateOf<CalendarDate?>(null) }
                        Popover(open = open, onOpenChange = { open = it }) {
                            PopoverTrigger {
                                Button(onClick = toggle, variant = ButtonVariant.Outline) {
                                    Text(textStyle = if (selected == null) TextColors.Muted else TextStyle.Default) {
                                        selected?.let(::formatDate) ?: "Pick a date"
                                    }
                                }
                            }
                            PopoverContent {
                                Calendar(
                                    year = 2026,
                                    month = 3,
                                    onMonthChange = { _, _ -> },
                                    selected = selected,
                                    onSelectedChange = {
                                        selected = it
                                        open = false
                                    },
                                    disableNavigation = true,
                                )
                            }
                        }
                    },
                ),
                ComponentExample(
                    title = "Default disable navigation",
                    code =
                        """
                        var open by remember { mutableStateOf(false) }
                        var year by remember { mutableStateOf(2026) }
                        var month by remember { mutableStateOf(3) }
                        var selected by remember { mutableStateOf<CalendarDate?>(null) }
                        Popover(open = open, onOpenChange = { open = it }) {
                            PopoverTrigger {
                                Button(onClick = toggle, variant = ButtonVariant.Outline) {
                                    Text(textStyle = if (selected == null) TextColors.Muted else TextStyle.Default) {
                                        selected?.let(::formatDate) ?: "Pick a date"
                                    }
                                }
                            }
                            PopoverContent {
                                Calendar(
                                    year = year, month = month,
                                    onMonthChange = { y, m -> year = y; month = m },
                                    selected = selected, onSelectedChange = {
                                        selected = it
                                        open = false
                                    },
                                    disableNavigation = false,
                                )
                            }
                        }
                        """.trimIndent(),
                    preview = {
                        var open by remember { mutableStateOf(false) }
                        var year by remember { mutableStateOf(2026) }
                        var month by remember { mutableStateOf(3) }
                        var selected by remember { mutableStateOf<CalendarDate?>(null) }
                        Popover(open = open, onOpenChange = { open = it }) {
                            PopoverTrigger {
                                Button(onClick = toggle, variant = ButtonVariant.Outline) {
                                    Text(textStyle = if (selected == null) TextColors.Muted else TextStyle.Default) {
                                        selected?.let(::formatDate) ?: "Pick a date"
                                    }
                                }
                            }
                            PopoverContent {
                                Calendar(
                                    year = year,
                                    month = month,
                                    onMonthChange = { y, m ->
                                        year = y
                                        month = m
                                    },
                                    selected = selected,
                                    onSelectedChange = {
                                        selected = it
                                        open = false
                                    },
                                    disableNavigation = false,
                                )
                            }
                        }
                    },
                ),
                ComponentExample(
                    title = "Paged navigation",
                    code =
                        """
                        var open by remember { mutableStateOf(false) }
                        var month by remember { mutableStateOf(3) }
                        var selected by remember { mutableStateOf<CalendarDate?>(null) }
                        Popover(open = open, onOpenChange = { open = it }) {
                            PopoverTrigger {
                                Button(onClick = toggle, variant = ButtonVariant.Outline) {
                                    Text(textStyle = if (selected == null) TextColors.Muted else TextStyle.Default) {
                                        selected?.let(::formatDate) ?: "Pick a date"
                                    }
                                }
                            }
                            PopoverContent {
                                Calendar(
                                    year = 2026, month = month,
                                    onMonthChange = { _, m -> month = m },
                                    selected = selected, onSelectedChange = {
                                        selected = it
                                        open = false
                                    },
                                    numberOfMonths = 2,
                                    pagedNavigation = true,
                                )
                            }
                        }
                        """.trimIndent(),
                    preview = {
                        var open by remember { mutableStateOf(false) }
                        var month by remember { mutableStateOf(3) }
                        var selected by remember { mutableStateOf<CalendarDate?>(null) }
                        Popover(open = open, onOpenChange = { open = it }) {
                            PopoverTrigger {
                                Button(onClick = toggle, variant = ButtonVariant.Outline) {
                                    Text(textStyle = if (selected == null) TextColors.Muted else TextStyle.Default) {
                                        selected?.let(::formatDate) ?: "Pick a date"
                                    }
                                }
                            }
                            PopoverContent {
                                Calendar(
                                    year = 2026,
                                    month = month,
                                    onMonthChange = { _, m -> month = m },
                                    selected = selected,
                                    onSelectedChange = {
                                        selected = it
                                        open = false
                                    },
                                    numberOfMonths = 2,
                                    pagedNavigation = true,
                                )
                            }
                        }
                    },
                ),
                ComponentExample(
                    title = "Default paged navigation",
                    code =
                        """
                        var open by remember { mutableStateOf(false) }
                        var year by remember { mutableStateOf(2026) }
                        var month by remember { mutableStateOf(3) }
                        var selected by remember { mutableStateOf<CalendarDate?>(null) }
                        Popover(open = open, onOpenChange = { open = it }) {
                            PopoverTrigger {
                                Button(onClick = toggle, variant = ButtonVariant.Outline) {
                                    Text(textStyle = if (selected == null) TextColors.Muted else TextStyle.Default) {
                                        selected?.let(::formatDate) ?: "Pick a date"
                                    }
                                }
                            }
                            PopoverContent {
                                Calendar(
                                    year = year, month = month,
                                    onMonthChange = { y, m -> year = y; month = m },
                                    selected = selected, onSelectedChange = {
                                        selected = it
                                        open = false
                                    },
                                    pagedNavigation = false,
                                )
                            }
                        }
                        """.trimIndent(),
                    preview = {
                        var open by remember { mutableStateOf(false) }
                        var year by remember { mutableStateOf(2026) }
                        var month by remember { mutableStateOf(3) }
                        var selected by remember { mutableStateOf<CalendarDate?>(null) }
                        Popover(open = open, onOpenChange = { open = it }) {
                            PopoverTrigger {
                                Button(onClick = toggle, variant = ButtonVariant.Outline) {
                                    Text(textStyle = if (selected == null) TextColors.Muted else TextStyle.Default) {
                                        selected?.let(::formatDate) ?: "Pick a date"
                                    }
                                }
                            }
                            PopoverContent {
                                Calendar(
                                    year = year,
                                    month = month,
                                    onMonthChange = { y, m ->
                                        year = y
                                        month = m
                                    },
                                    selected = selected,
                                    onSelectedChange = {
                                        selected = it
                                        open = false
                                    },
                                    pagedNavigation = false,
                                )
                            }
                        }
                    },
                ),
                ComponentExample(
                    title = "Min and max date",
                    code =
                        """
                        var open by remember { mutableStateOf(false) }
                        var year by remember { mutableStateOf(2026) }
                        var month by remember { mutableStateOf(3) }
                        var selected by remember { mutableStateOf<CalendarDate?>(null) }
                        Popover(open = open, onOpenChange = { open = it }) {
                            PopoverTrigger {
                                Button(onClick = toggle, variant = ButtonVariant.Outline) {
                                    Text(textStyle = if (selected == null) TextColors.Muted else TextStyle.Default) {
                                        selected?.let(::formatDate) ?: "Pick a date"
                                    }
                                }
                            }
                            PopoverContent {
                                Calendar(
                                    year = year, month = month,
                                    onMonthChange = { y, m -> year = y; month = m },
                                    selected = selected, onSelectedChange = {
                                        selected = it
                                        open = false
                                    },
                                    startMonth = CalendarDate(2026, 3, 5),
                                    endMonth = CalendarDate(2026, 3, 20),
                                )
                            }
                        }
                        """.trimIndent(),
                    preview = {
                        var open by remember { mutableStateOf(false) }
                        var year by remember { mutableStateOf(2026) }
                        var month by remember { mutableStateOf(3) }
                        var selected by remember { mutableStateOf<CalendarDate?>(null) }
                        Popover(open = open, onOpenChange = { open = it }) {
                            PopoverTrigger {
                                Button(onClick = toggle, variant = ButtonVariant.Outline) {
                                    Text(textStyle = if (selected == null) TextColors.Muted else TextStyle.Default) {
                                        selected?.let(::formatDate) ?: "Pick a date"
                                    }
                                }
                            }
                            PopoverContent {
                                Calendar(
                                    year = year,
                                    month = month,
                                    onMonthChange = { y, m ->
                                        year = y
                                        month = m
                                    },
                                    selected = selected,
                                    onSelectedChange = {
                                        selected = it
                                        open = false
                                    },
                                    startMonth = CalendarDate(2026, 3, 5),
                                    endMonth = CalendarDate(2026, 3, 20),
                                )
                            }
                        }
                    },
                ),
                ComponentExample(
                    title = "Variable weeks",
                    code =
                        """
                        var open by remember { mutableStateOf(false) }
                        var year by remember { mutableStateOf(2026) }
                        var month by remember { mutableStateOf(3) }
                        var selected by remember { mutableStateOf<CalendarDate?>(null) }
                        Popover(open = open, onOpenChange = { open = it }) {
                            PopoverTrigger {
                                Button(onClick = toggle, variant = ButtonVariant.Outline) {
                                    Text(textStyle = if (selected == null) TextColors.Muted else TextStyle.Default) {
                                        selected?.let(::formatDate) ?: "Pick a date"
                                    }
                                }
                            }
                            PopoverContent {
                                Calendar(
                                    year = year, month = month,
                                    onMonthChange = { y, m -> year = y; month = m },
                                    selected = selected, onSelectedChange = {
                                        selected = it
                                        open = false
                                    },
                                    fixedWeeks = false,
                                )
                            }
                        }
                        """.trimIndent(),
                    preview = {
                        var open by remember { mutableStateOf(false) }
                        var year by remember { mutableStateOf(2026) }
                        var month by remember { mutableStateOf(3) }
                        var selected by remember { mutableStateOf<CalendarDate?>(null) }
                        Popover(open = open, onOpenChange = { open = it }) {
                            PopoverTrigger {
                                Button(onClick = toggle, variant = ButtonVariant.Outline) {
                                    Text(textStyle = if (selected == null) TextColors.Muted else TextStyle.Default) {
                                        selected?.let(::formatDate) ?: "Pick a date"
                                    }
                                }
                            }
                            PopoverContent {
                                Calendar(
                                    year = year,
                                    month = month,
                                    onMonthChange = { y, m ->
                                        year = y
                                        month = m
                                    },
                                    selected = selected,
                                    onSelectedChange = {
                                        selected = it
                                        open = false
                                    },
                                    fixedWeeks = false,
                                )
                            }
                        }
                    },
                ),
                ComponentExample(
                    title = "Default fixed weeks",
                    code =
                        """
                        var open by remember { mutableStateOf(false) }
                        var year by remember { mutableStateOf(2026) }
                        var month by remember { mutableStateOf(3) }
                        var selected by remember { mutableStateOf<CalendarDate?>(null) }
                        Popover(open = open, onOpenChange = { open = it }) {
                            PopoverTrigger {
                                Button(onClick = toggle, variant = ButtonVariant.Outline) {
                                    Text(textStyle = if (selected == null) TextColors.Muted else TextStyle.Default) {
                                        selected?.let(::formatDate) ?: "Pick a date"
                                    }
                                }
                            }
                            PopoverContent {
                                Calendar(
                                    year = year, month = month,
                                    onMonthChange = { y, m -> year = y; month = m },
                                    selected = selected, onSelectedChange = {
                                        selected = it
                                        open = false
                                    },
                                    fixedWeeks = true,
                                )
                            }
                        }
                        """.trimIndent(),
                    preview = {
                        var open by remember { mutableStateOf(false) }
                        var year by remember { mutableStateOf(2026) }
                        var month by remember { mutableStateOf(3) }
                        var selected by remember { mutableStateOf<CalendarDate?>(null) }
                        Popover(open = open, onOpenChange = { open = it }) {
                            PopoverTrigger {
                                Button(onClick = toggle, variant = ButtonVariant.Outline) {
                                    Text(textStyle = if (selected == null) TextColors.Muted else TextStyle.Default) {
                                        selected?.let(::formatDate) ?: "Pick a date"
                                    }
                                }
                            }
                            PopoverContent {
                                Calendar(
                                    year = year,
                                    month = month,
                                    onMonthChange = { y, m ->
                                        year = y
                                        month = m
                                    },
                                    selected = selected,
                                    onSelectedChange = {
                                        selected = it
                                        open = false
                                    },
                                    fixedWeeks = true,
                                )
                            }
                        }
                    },
                ),
                ComponentExample(
                    title = "Reversed months",
                    code =
                        """
                        var open by remember { mutableStateOf(false) }
                        var selected by remember { mutableStateOf<CalendarDate?>(null) }
                        Popover(open = open, onOpenChange = { open = it }) {
                            PopoverTrigger {
                                Button(onClick = toggle, variant = ButtonVariant.Outline) {
                                    Text(textStyle = if (selected == null) TextColors.Muted else TextStyle.Default) {
                                        selected?.let(::formatDate) ?: "Pick a date"
                                    }
                                }
                            }
                            PopoverContent {
                                Calendar(
                                    year = 2026, month = 3,
                                    onMonthChange = { _, _ -> },
                                    selected = selected, onSelectedChange = {
                                        selected = it
                                        open = false
                                    },
                                    numberOfMonths = 2,
                                    reverseMonths = true,
                                )
                            }
                        }
                        """.trimIndent(),
                    preview = {
                        var open by remember { mutableStateOf(false) }
                        var selected by remember { mutableStateOf<CalendarDate?>(null) }
                        Popover(open = open, onOpenChange = { open = it }) {
                            PopoverTrigger {
                                Button(onClick = toggle, variant = ButtonVariant.Outline) {
                                    Text(textStyle = if (selected == null) TextColors.Muted else TextStyle.Default) {
                                        selected?.let(::formatDate) ?: "Pick a date"
                                    }
                                }
                            }
                            PopoverContent {
                                Calendar(
                                    year = 2026,
                                    month = 3,
                                    onMonthChange = { _, _ -> },
                                    selected = selected,
                                    onSelectedChange = {
                                        selected = it
                                        open = false
                                    },
                                    numberOfMonths = 2,
                                    reverseMonths = true,
                                )
                            }
                        }
                    },
                ),
                ComponentExample(
                    title = "Default reverse months",
                    code =
                        """
                        var open by remember { mutableStateOf(false) }
                        var selected by remember { mutableStateOf<CalendarDate?>(null) }
                        Popover(open = open, onOpenChange = { open = it }) {
                            PopoverTrigger {
                                Button(onClick = toggle, variant = ButtonVariant.Outline) {
                                    Text(textStyle = if (selected == null) TextColors.Muted else TextStyle.Default) {
                                        selected?.let(::formatDate) ?: "Pick a date"
                                    }
                                }
                            }
                            PopoverContent {
                                Calendar(
                                    year = 2026, month = 3,
                                    onMonthChange = { _, _ -> },
                                    selected = selected, onSelectedChange = {
                                        selected = it
                                        open = false
                                    },
                                    reverseMonths = false,
                                )
                            }
                        }
                        """.trimIndent(),
                    preview = {
                        var open by remember { mutableStateOf(false) }
                        var selected by remember { mutableStateOf<CalendarDate?>(null) }
                        Popover(open = open, onOpenChange = { open = it }) {
                            PopoverTrigger {
                                Button(onClick = toggle, variant = ButtonVariant.Outline) {
                                    Text(textStyle = if (selected == null) TextColors.Muted else TextStyle.Default) {
                                        selected?.let(::formatDate) ?: "Pick a date"
                                    }
                                }
                            }
                            PopoverContent {
                                Calendar(
                                    year = 2026,
                                    month = 3,
                                    onMonthChange = { _, _ -> },
                                    selected = selected,
                                    onSelectedChange = {
                                        selected = it
                                        open = false
                                    },
                                    reverseMonths = false,
                                )
                            }
                        }
                    },
                ),
                ComponentExample(
                    title = "From month",
                    code =
                        """
                        var open by remember { mutableStateOf(false) }
                        var year by remember { mutableStateOf(2026) }
                        var month by remember { mutableStateOf(3) }
                        var selected by remember { mutableStateOf<CalendarDate?>(null) }
                        Popover(open = open, onOpenChange = { open = it }) {
                            PopoverTrigger {
                                Button(onClick = toggle, variant = ButtonVariant.Outline) {
                                    Text(textStyle = if (selected == null) TextColors.Muted else TextStyle.Default) {
                                        selected?.let(::formatDate) ?: "Pick a date"
                                    }
                                }
                            }
                            PopoverContent {
                                Calendar(
                                    year = year, month = month,
                                    onMonthChange = { y, m -> year = y; month = m },
                                    selected = selected, onSelectedChange = {
                                        selected = it
                                        open = false
                                    },
                                    fromMonth = CalendarDate(2026, 3, 5),
                                    endMonth = CalendarDate(2026, 3, 20),
                                )
                            }
                        }
                        """.trimIndent(),
                    preview = {
                        var open by remember { mutableStateOf(false) }
                        var year by remember { mutableStateOf(2026) }
                        var month by remember { mutableStateOf(3) }
                        var selected by remember { mutableStateOf<CalendarDate?>(null) }
                        Popover(open = open, onOpenChange = { open = it }) {
                            PopoverTrigger {
                                Button(onClick = toggle, variant = ButtonVariant.Outline) {
                                    Text(textStyle = if (selected == null) TextColors.Muted else TextStyle.Default) {
                                        selected?.let(::formatDate) ?: "Pick a date"
                                    }
                                }
                            }
                            PopoverContent {
                                Calendar(
                                    year = year,
                                    month = month,
                                    onMonthChange = { y, m ->
                                        year = y
                                        month = m
                                    },
                                    selected = selected,
                                    onSelectedChange = {
                                        selected = it
                                        open = false
                                    },
                                    fromMonth = CalendarDate(2026, 3, 5),
                                    endMonth = CalendarDate(2026, 3, 20),
                                )
                            }
                        }
                    },
                ),
                ComponentExample(
                    title = "To month",
                    code =
                        """
                        var open by remember { mutableStateOf(false) }
                        var year by remember { mutableStateOf(2026) }
                        var month by remember { mutableStateOf(3) }
                        var selected by remember { mutableStateOf<CalendarDate?>(null) }
                        Popover(open = open, onOpenChange = { open = it }) {
                            PopoverTrigger {
                                Button(onClick = toggle, variant = ButtonVariant.Outline) {
                                    Text(textStyle = if (selected == null) TextColors.Muted else TextStyle.Default) {
                                        selected?.let(::formatDate) ?: "Pick a date"
                                    }
                                }
                            }
                            PopoverContent {
                                Calendar(
                                    year = year, month = month,
                                    onMonthChange = { y, m -> year = y; month = m },
                                    selected = selected, onSelectedChange = {
                                        selected = it
                                        open = false
                                    },
                                    fromMonth = CalendarDate(2026, 3, 5),
                                    toMonth = CalendarDate(2026, 3, 20),
                                )
                            }
                        }
                        """.trimIndent(),
                    preview = {
                        var open by remember { mutableStateOf(false) }
                        var year by remember { mutableStateOf(2026) }
                        var month by remember { mutableStateOf(3) }
                        var selected by remember { mutableStateOf<CalendarDate?>(null) }
                        Popover(open = open, onOpenChange = { open = it }) {
                            PopoverTrigger {
                                Button(onClick = toggle, variant = ButtonVariant.Outline) {
                                    Text(textStyle = if (selected == null) TextColors.Muted else TextStyle.Default) {
                                        selected?.let(::formatDate) ?: "Pick a date"
                                    }
                                }
                            }
                            PopoverContent {
                                Calendar(
                                    year = year,
                                    month = month,
                                    onMonthChange = { y, m ->
                                        year = y
                                        month = m
                                    },
                                    selected = selected,
                                    onSelectedChange = {
                                        selected = it
                                        open = false
                                    },
                                    fromMonth = CalendarDate(2026, 3, 5),
                                    toMonth = CalendarDate(2026, 3, 20),
                                )
                            }
                        }
                    },
                ),
                ComponentExample(
                    title = "From date",
                    code =
                        """
                        var open by remember { mutableStateOf(false) }
                        var year by remember { mutableStateOf(2026) }
                        var month by remember { mutableStateOf(3) }
                        var selected by remember { mutableStateOf<CalendarDate?>(null) }
                        Popover(open = open, onOpenChange = { open = it }) {
                            PopoverTrigger {
                                Button(onClick = toggle, variant = ButtonVariant.Outline) {
                                    Text(textStyle = if (selected == null) TextColors.Muted else TextStyle.Default) {
                                        selected?.let(::formatDate) ?: "Pick a date"
                                    }
                                }
                            }
                            PopoverContent {
                                Calendar(
                                    year = year, month = month,
                                    onMonthChange = { y, m -> year = y; month = m },
                                    selected = selected, onSelectedChange = {
                                        selected = it
                                        open = false
                                    },
                                    fromDate = CalendarDate(2026, 3, 5),
                                    toDate = CalendarDate(2026, 3, 20),
                                )
                            }
                        }
                        """.trimIndent(),
                    preview = {
                        var open by remember { mutableStateOf(false) }
                        var year by remember { mutableStateOf(2026) }
                        var month by remember { mutableStateOf(3) }
                        var selected by remember { mutableStateOf<CalendarDate?>(null) }
                        Popover(open = open, onOpenChange = { open = it }) {
                            PopoverTrigger {
                                Button(onClick = toggle, variant = ButtonVariant.Outline) {
                                    Text(textStyle = if (selected == null) TextColors.Muted else TextStyle.Default) {
                                        selected?.let(::formatDate) ?: "Pick a date"
                                    }
                                }
                            }
                            PopoverContent {
                                Calendar(
                                    year = year,
                                    month = month,
                                    onMonthChange = { y, m ->
                                        year = y
                                        month = m
                                    },
                                    selected = selected,
                                    onSelectedChange = {
                                        selected = it
                                        open = false
                                    },
                                    fromDate = CalendarDate(2026, 3, 5),
                                    toDate = CalendarDate(2026, 3, 20),
                                )
                            }
                        }
                    },
                ),
                ComponentExample(
                    title = "To date",
                    code =
                        """
                        var open by remember { mutableStateOf(false) }
                        var year by remember { mutableStateOf(2026) }
                        var month by remember { mutableStateOf(3) }
                        var selected by remember { mutableStateOf<CalendarDate?>(null) }
                        Popover(open = open, onOpenChange = { open = it }) {
                            PopoverTrigger {
                                Button(onClick = toggle, variant = ButtonVariant.Outline) {
                                    Text(textStyle = if (selected == null) TextColors.Muted else TextStyle.Default) {
                                        selected?.let(::formatDate) ?: "Pick a date"
                                    }
                                }
                            }
                            PopoverContent {
                                Calendar(
                                    year = year, month = month,
                                    onMonthChange = { y, m -> year = y; month = m },
                                    selected = selected, onSelectedChange = {
                                        selected = it
                                        open = false
                                    },
                                    fromDate = CalendarDate(2026, 3, 5),
                                    toDate = CalendarDate(2026, 3, 20),
                                )
                            }
                        }
                        """.trimIndent(),
                    preview = {
                        var open by remember { mutableStateOf(false) }
                        var year by remember { mutableStateOf(2026) }
                        var month by remember { mutableStateOf(3) }
                        var selected by remember { mutableStateOf<CalendarDate?>(null) }
                        Popover(open = open, onOpenChange = { open = it }) {
                            PopoverTrigger {
                                Button(onClick = toggle, variant = ButtonVariant.Outline) {
                                    Text(textStyle = if (selected == null) TextColors.Muted else TextStyle.Default) {
                                        selected?.let(::formatDate) ?: "Pick a date"
                                    }
                                }
                            }
                            PopoverContent {
                                Calendar(
                                    year = year,
                                    month = month,
                                    onMonthChange = { y, m ->
                                        year = y
                                        month = m
                                    },
                                    selected = selected,
                                    onSelectedChange = {
                                        selected = it
                                        open = false
                                    },
                                    fromDate = CalendarDate(2026, 3, 5),
                                    toDate = CalendarDate(2026, 3, 20),
                                )
                            }
                        }
                    },
                ),
                ComponentExample(
                    title = "Start month",
                    code =
                        """
                        var open by remember { mutableStateOf(false) }
                        var year by remember { mutableStateOf(2026) }
                        var month by remember { mutableStateOf(3) }
                        var selected by remember { mutableStateOf<CalendarDate?>(null) }
                        Popover(open = open, onOpenChange = { open = it }) {
                            PopoverTrigger {
                                Button(onClick = toggle, variant = ButtonVariant.Outline) {
                                    Text(textStyle = if (selected == null) TextColors.Muted else TextStyle.Default) {
                                        selected?.let(::formatDate) ?: "Pick a date"
                                    }
                                }
                            }
                            PopoverContent {
                                Calendar(
                                    year = year, month = month,
                                    onMonthChange = { y, m -> year = y; month = m },
                                    selected = selected, onSelectedChange = {
                                        selected = it
                                        open = false
                                    },
                                    startMonth = CalendarDate(2026, 3, 5),
                                    endMonth = CalendarDate(2026, 3, 20),
                                )
                            }
                        }
                        """.trimIndent(),
                    preview = {
                        var open by remember { mutableStateOf(false) }
                        var year by remember { mutableStateOf(2026) }
                        var month by remember { mutableStateOf(3) }
                        var selected by remember { mutableStateOf<CalendarDate?>(null) }
                        Popover(open = open, onOpenChange = { open = it }) {
                            PopoverTrigger {
                                Button(onClick = toggle, variant = ButtonVariant.Outline) {
                                    Text(textStyle = if (selected == null) TextColors.Muted else TextStyle.Default) {
                                        selected?.let(::formatDate) ?: "Pick a date"
                                    }
                                }
                            }
                            PopoverContent {
                                Calendar(
                                    year = year,
                                    month = month,
                                    onMonthChange = { y, m ->
                                        year = y
                                        month = m
                                    },
                                    selected = selected,
                                    onSelectedChange = {
                                        selected = it
                                        open = false
                                    },
                                    startMonth = CalendarDate(2026, 3, 5),
                                    endMonth = CalendarDate(2026, 3, 20),
                                )
                            }
                        }
                    },
                ),
                ComponentExample(
                    title = "End month",
                    code =
                        """
                        var open by remember { mutableStateOf(false) }
                        var year by remember { mutableStateOf(2026) }
                        var month by remember { mutableStateOf(3) }
                        var selected by remember { mutableStateOf<CalendarDate?>(null) }
                        Popover(open = open, onOpenChange = { open = it }) {
                            PopoverTrigger {
                                Button(onClick = toggle, variant = ButtonVariant.Outline) {
                                    Text(textStyle = if (selected == null) TextColors.Muted else TextStyle.Default) {
                                        selected?.let(::formatDate) ?: "Pick a date"
                                    }
                                }
                            }
                            PopoverContent {
                                Calendar(
                                    year = year, month = month,
                                    onMonthChange = { y, m -> year = y; month = m },
                                    selected = selected, onSelectedChange = {
                                        selected = it
                                        open = false
                                    },
                                    startMonth = CalendarDate(2026, 3, 5),
                                    endMonth = CalendarDate(2026, 3, 20),
                                )
                            }
                        }
                        """.trimIndent(),
                    preview = {
                        var open by remember { mutableStateOf(false) }
                        var year by remember { mutableStateOf(2026) }
                        var month by remember { mutableStateOf(3) }
                        var selected by remember { mutableStateOf<CalendarDate?>(null) }
                        Popover(open = open, onOpenChange = { open = it }) {
                            PopoverTrigger {
                                Button(onClick = toggle, variant = ButtonVariant.Outline) {
                                    Text(textStyle = if (selected == null) TextColors.Muted else TextStyle.Default) {
                                        selected?.let(::formatDate) ?: "Pick a date"
                                    }
                                }
                            }
                            PopoverContent {
                                Calendar(
                                    year = year,
                                    month = month,
                                    onMonthChange = { y, m ->
                                        year = y
                                        month = m
                                    },
                                    selected = selected,
                                    onSelectedChange = {
                                        selected = it
                                        open = false
                                    },
                                    startMonth = CalendarDate(2026, 3, 5),
                                    endMonth = CalendarDate(2026, 3, 20),
                                )
                            }
                        }
                    },
                ),
                ComponentExample(
                    title = "Header",
                    code =
                        """
                        var open by remember { mutableStateOf(false) }
                        var year by remember { mutableStateOf(2026) }
                        var month by remember { mutableStateOf(3) }
                        var selected by remember { mutableStateOf<CalendarDate?>(null) }
                        Popover(open = open, onOpenChange = { open = it }) {
                            PopoverTrigger {
                                Button(onClick = toggle, variant = ButtonVariant.Outline) {
                                    Text(textStyle = if (selected == null) TextColors.Muted else TextStyle.Default) {
                                        selected?.let(::formatDate) ?: "Pick a date"
                                    }
                                }
                            }
                            PopoverContent {
                                Calendar(
                                    year = year, month = month,
                                    onMonthChange = { y, m -> year = y; month = m },
                                    selected = selected, onSelectedChange = {
                                        selected = it
                                        open = false
                                    },
                                ) {
                                    CalendarHeader()
                                    CalendarWeekdays()
                                    CalendarGrid()
                                }
                            }
                        }
                        """.trimIndent(),
                    preview = {
                        var open by remember { mutableStateOf(false) }
                        var year by remember { mutableStateOf(2026) }
                        var month by remember { mutableStateOf(3) }
                        var selected by remember { mutableStateOf<CalendarDate?>(null) }
                        Popover(open = open, onOpenChange = { open = it }) {
                            PopoverTrigger {
                                Button(onClick = toggle, variant = ButtonVariant.Outline) {
                                    Text(textStyle = if (selected == null) TextColors.Muted else TextStyle.Default) {
                                        selected?.let(::formatDate) ?: "Pick a date"
                                    }
                                }
                            }
                            PopoverContent {
                                Calendar(
                                    year = year,
                                    month = month,
                                    onMonthChange = { y, m ->
                                        year = y
                                        month = m
                                    },
                                    selected = selected,
                                    onSelectedChange = {
                                        selected = it
                                        open = false
                                    },
                                ) {
                                    CalendarHeader()
                                    CalendarWeekdays()
                                    CalendarGrid()
                                }
                            }
                        }
                    },
                ),
                ComponentExample(
                    title = "Default content",
                    code =
                        """
                        var open by remember { mutableStateOf(false) }
                        var year by remember { mutableStateOf(2026) }
                        var month by remember { mutableStateOf(3) }
                        var selected by remember { mutableStateOf<CalendarDate?>(null) }
                        Popover(open = open, onOpenChange = { open = it }) {
                            PopoverTrigger {
                                Button(onClick = toggle, variant = ButtonVariant.Outline) {
                                    Text(textStyle = if (selected == null) TextColors.Muted else TextStyle.Default) {
                                        selected?.let(::formatDate) ?: "Pick a date"
                                    }
                                }
                            }
                            PopoverContent {
                                Calendar(
                                    year = year, month = month,
                                    onMonthChange = { y, m -> year = y; month = m },
                                    selected = selected, onSelectedChange = {
                                        selected = it
                                        open = false
                                    },
                                ) {
                                    CalendarDefaultContent()
                                }
                            }
                        }
                        """.trimIndent(),
                    preview = {
                        var open by remember { mutableStateOf(false) }
                        var year by remember { mutableStateOf(2026) }
                        var month by remember { mutableStateOf(3) }
                        var selected by remember { mutableStateOf<CalendarDate?>(null) }
                        Popover(open = open, onOpenChange = { open = it }) {
                            PopoverTrigger {
                                Button(onClick = toggle, variant = ButtonVariant.Outline) {
                                    Text(textStyle = if (selected == null) TextColors.Muted else TextStyle.Default) {
                                        selected?.let(::formatDate) ?: "Pick a date"
                                    }
                                }
                            }
                            PopoverContent {
                                Calendar(
                                    year = year,
                                    month = month,
                                    onMonthChange = { y, m ->
                                        year = y
                                        month = m
                                    },
                                    selected = selected,
                                    onSelectedChange = {
                                        selected = it
                                        open = false
                                    },
                                ) {
                                    CalendarDefaultContent()
                                }
                            }
                        }
                    },
                ),
                ComponentExample(
                    title = "Weekdays",
                    code =
                        """
                        var open by remember { mutableStateOf(false) }
                        var year by remember { mutableStateOf(2026) }
                        var month by remember { mutableStateOf(3) }
                        var selected by remember { mutableStateOf<CalendarDate?>(null) }
                        Popover(open = open, onOpenChange = { open = it }) {
                            PopoverTrigger {
                                Button(onClick = toggle, variant = ButtonVariant.Outline) {
                                    Text(textStyle = if (selected == null) TextColors.Muted else TextStyle.Default) {
                                        selected?.let(::formatDate) ?: "Pick a date"
                                    }
                                }
                            }
                            PopoverContent {
                                Calendar(
                                    year = year, month = month,
                                    onMonthChange = { y, m -> year = y; month = m },
                                    selected = selected, onSelectedChange = {
                                        selected = it
                                        open = false
                                    },
                                ) {
                                    CalendarHeader()
                                    CalendarWeekdays()
                                    CalendarGrid()
                                }
                            }
                        }
                        """.trimIndent(),
                    preview = {
                        var open by remember { mutableStateOf(false) }
                        var year by remember { mutableStateOf(2026) }
                        var month by remember { mutableStateOf(3) }
                        var selected by remember { mutableStateOf<CalendarDate?>(null) }
                        Popover(open = open, onOpenChange = { open = it }) {
                            PopoverTrigger {
                                Button(onClick = toggle, variant = ButtonVariant.Outline) {
                                    Text(textStyle = if (selected == null) TextColors.Muted else TextStyle.Default) {
                                        selected?.let(::formatDate) ?: "Pick a date"
                                    }
                                }
                            }
                            PopoverContent {
                                Calendar(
                                    year = year,
                                    month = month,
                                    onMonthChange = { y, m ->
                                        year = y
                                        month = m
                                    },
                                    selected = selected,
                                    onSelectedChange = {
                                        selected = it
                                        open = false
                                    },
                                ) {
                                    CalendarHeader()
                                    CalendarWeekdays()
                                    CalendarGrid()
                                }
                            }
                        }
                    },
                ),
                ComponentExample(
                    title = "Caption",
                    code =
                        """
                        var open by remember { mutableStateOf(false) }
                        var year by remember { mutableStateOf(2026) }
                        var month by remember { mutableStateOf(3) }
                        var selected by remember { mutableStateOf<CalendarDate?>(null) }
                        Popover(open = open, onOpenChange = { open = it }) {
                            PopoverTrigger {
                                Button(onClick = toggle, variant = ButtonVariant.Outline) {
                                    Text(textStyle = if (selected == null) TextColors.Muted else TextStyle.Default) {
                                        selected?.let(::formatDate) ?: "Pick a date"
                                    }
                                }
                            }
                            PopoverContent {
                                Calendar(
                                    year = year, month = month,
                                    onMonthChange = { y, m -> year = y; month = m },
                                    selected = selected, onSelectedChange = {
                                        selected = it
                                        open = false
                                    },
                                ) {
                                    CalendarCaption()
                                    CalendarWeekdays()
                                    CalendarGrid()
                                }
                            }
                        }
                        """.trimIndent(),
                    preview = {
                        var open by remember { mutableStateOf(false) }
                        var year by remember { mutableStateOf(2026) }
                        var month by remember { mutableStateOf(3) }
                        var selected by remember { mutableStateOf<CalendarDate?>(null) }
                        Popover(open = open, onOpenChange = { open = it }) {
                            PopoverTrigger {
                                Button(onClick = toggle, variant = ButtonVariant.Outline) {
                                    Text(textStyle = if (selected == null) TextColors.Muted else TextStyle.Default) {
                                        selected?.let(::formatDate) ?: "Pick a date"
                                    }
                                }
                            }
                            PopoverContent {
                                Calendar(
                                    year = year,
                                    month = month,
                                    onMonthChange = { y, m ->
                                        year = y
                                        month = m
                                    },
                                    selected = selected,
                                    onSelectedChange = {
                                        selected = it
                                        open = false
                                    },
                                ) {
                                    CalendarCaption()
                                    CalendarWeekdays()
                                    CalendarGrid()
                                }
                            }
                        }
                    },
                ),
                ComponentExample(
                    title = "Title",
                    code =
                        """
                        var open by remember { mutableStateOf(false) }
                        var year by remember { mutableStateOf(2026) }
                        var month by remember { mutableStateOf(3) }
                        var selected by remember { mutableStateOf<CalendarDate?>(null) }
                        Popover(open = open, onOpenChange = { open = it }) {
                            PopoverTrigger {
                                Button(onClick = toggle, variant = ButtonVariant.Outline) {
                                    Text(textStyle = if (selected == null) TextColors.Muted else TextStyle.Default) {
                                        selected?.let(::formatDate) ?: "Pick a date"
                                    }
                                }
                            }
                            PopoverContent {
                                Calendar(
                                    year = year, month = month,
                                    onMonthChange = { y, m -> year = y; month = m },
                                    selected = selected, onSelectedChange = {
                                        selected = it
                                        open = false
                                    },
                                ) {
                                    CalendarTitle()
                                    CalendarWeekdays()
                                    CalendarGrid()
                                }
                            }
                        }
                        """.trimIndent(),
                    preview = {
                        var open by remember { mutableStateOf(false) }
                        var year by remember { mutableStateOf(2026) }
                        var month by remember { mutableStateOf(3) }
                        var selected by remember { mutableStateOf<CalendarDate?>(null) }
                        Popover(open = open, onOpenChange = { open = it }) {
                            PopoverTrigger {
                                Button(onClick = toggle, variant = ButtonVariant.Outline) {
                                    Text(textStyle = if (selected == null) TextColors.Muted else TextStyle.Default) {
                                        selected?.let(::formatDate) ?: "Pick a date"
                                    }
                                }
                            }
                            PopoverContent {
                                Calendar(
                                    year = year,
                                    month = month,
                                    onMonthChange = { y, m ->
                                        year = y
                                        month = m
                                    },
                                    selected = selected,
                                    onSelectedChange = {
                                        selected = it
                                        open = false
                                    },
                                ) {
                                    CalendarTitle()
                                    CalendarWeekdays()
                                    CalendarGrid()
                                }
                            }
                        }
                    },
                ),
                ComponentExample(
                    title = "Grid",
                    code =
                        """
                        var open by remember { mutableStateOf(false) }
                        var year by remember { mutableStateOf(2026) }
                        var month by remember { mutableStateOf(3) }
                        var selected by remember { mutableStateOf<CalendarDate?>(null) }
                        Popover(open = open, onOpenChange = { open = it }) {
                            PopoverTrigger {
                                Button(onClick = toggle, variant = ButtonVariant.Outline) {
                                    Text(textStyle = if (selected == null) TextColors.Muted else TextStyle.Default) {
                                        selected?.let(::formatDate) ?: "Pick a date"
                                    }
                                }
                            }
                            PopoverContent {
                                Calendar(
                                    year = year, month = month,
                                    onMonthChange = { y, m -> year = y; month = m },
                                    selected = selected, onSelectedChange = {
                                        selected = it
                                        open = false
                                    },
                                ) {
                                    CalendarHeader()
                                    CalendarWeekdays()
                                    CalendarGrid()
                                }
                            }
                        }
                        """.trimIndent(),
                    preview = {
                        var open by remember { mutableStateOf(false) }
                        var year by remember { mutableStateOf(2026) }
                        var month by remember { mutableStateOf(3) }
                        var selected by remember { mutableStateOf<CalendarDate?>(null) }
                        Popover(open = open, onOpenChange = { open = it }) {
                            PopoverTrigger {
                                Button(onClick = toggle, variant = ButtonVariant.Outline) {
                                    Text(textStyle = if (selected == null) TextColors.Muted else TextStyle.Default) {
                                        selected?.let(::formatDate) ?: "Pick a date"
                                    }
                                }
                            }
                            PopoverContent {
                                Calendar(
                                    year = year,
                                    month = month,
                                    onMonthChange = { y, m ->
                                        year = y
                                        month = m
                                    },
                                    selected = selected,
                                    onSelectedChange = {
                                        selected = it
                                        open = false
                                    },
                                ) {
                                    CalendarHeader()
                                    CalendarWeekdays()
                                    CalendarGrid()
                                }
                            }
                        }
                    },
                ),
                ComponentExample(
                    title = "Previous",
                    code =
                        """
                        var open by remember { mutableStateOf(false) }
                        var year by remember { mutableStateOf(2026) }
                        var month by remember { mutableStateOf(3) }
                        var selected by remember { mutableStateOf<CalendarDate?>(null) }
                        Popover(open = open, onOpenChange = { open = it }) {
                            PopoverTrigger {
                                Button(onClick = toggle, variant = ButtonVariant.Outline) {
                                    Text(textStyle = if (selected == null) TextColors.Muted else TextStyle.Default) {
                                        selected?.let(::formatDate) ?: "Pick a date"
                                    }
                                }
                            }
                            PopoverContent {
                                Calendar(
                                    year = year, month = month,
                                    onMonthChange = { y, m -> year = y; month = m },
                                    selected = selected, onSelectedChange = {
                                        selected = it
                                        open = false
                                    },
                                ) {
                                    CalendarPrevious()
                                    CalendarCaption()
                                    CalendarNext()
                                    CalendarWeekdays()
                                    CalendarGrid()
                                }
                            }
                        }
                        """.trimIndent(),
                    preview = {
                        var open by remember { mutableStateOf(false) }
                        var year by remember { mutableStateOf(2026) }
                        var month by remember { mutableStateOf(3) }
                        var selected by remember { mutableStateOf<CalendarDate?>(null) }
                        Popover(open = open, onOpenChange = { open = it }) {
                            PopoverTrigger {
                                Button(onClick = toggle, variant = ButtonVariant.Outline) {
                                    Text(textStyle = if (selected == null) TextColors.Muted else TextStyle.Default) {
                                        selected?.let(::formatDate) ?: "Pick a date"
                                    }
                                }
                            }
                            PopoverContent {
                                Calendar(
                                    year = year,
                                    month = month,
                                    onMonthChange = { y, m ->
                                        year = y
                                        month = m
                                    },
                                    selected = selected,
                                    onSelectedChange = {
                                        selected = it
                                        open = false
                                    },
                                ) {
                                    CalendarPrevious()
                                    CalendarCaption()
                                    CalendarNext()
                                    CalendarWeekdays()
                                    CalendarGrid()
                                }
                            }
                        }
                    },
                ),
                ComponentExample(
                    title = "Next",
                    code =
                        """
                        var open by remember { mutableStateOf(false) }
                        var year by remember { mutableStateOf(2026) }
                        var month by remember { mutableStateOf(3) }
                        var selected by remember { mutableStateOf<CalendarDate?>(null) }
                        Popover(open = open, onOpenChange = { open = it }) {
                            PopoverTrigger {
                                Button(onClick = toggle, variant = ButtonVariant.Outline) {
                                    Text(textStyle = if (selected == null) TextColors.Muted else TextStyle.Default) {
                                        selected?.let(::formatDate) ?: "Pick a date"
                                    }
                                }
                            }
                            PopoverContent {
                                Calendar(
                                    year = year, month = month,
                                    onMonthChange = { y, m -> year = y; month = m },
                                    selected = selected, onSelectedChange = {
                                        selected = it
                                        open = false
                                    },
                                ) {
                                    CalendarPrevious()
                                    CalendarCaption()
                                    CalendarNext()
                                    CalendarWeekdays()
                                    CalendarGrid()
                                }
                            }
                        }
                        """.trimIndent(),
                    preview = {
                        var open by remember { mutableStateOf(false) }
                        var year by remember { mutableStateOf(2026) }
                        var month by remember { mutableStateOf(3) }
                        var selected by remember { mutableStateOf<CalendarDate?>(null) }
                        Popover(open = open, onOpenChange = { open = it }) {
                            PopoverTrigger {
                                Button(onClick = toggle, variant = ButtonVariant.Outline) {
                                    Text(textStyle = if (selected == null) TextColors.Muted else TextStyle.Default) {
                                        selected?.let(::formatDate) ?: "Pick a date"
                                    }
                                }
                            }
                            PopoverContent {
                                Calendar(
                                    year = year,
                                    month = month,
                                    onMonthChange = { y, m ->
                                        year = y
                                        month = m
                                    },
                                    selected = selected,
                                    onSelectedChange = {
                                        selected = it
                                        open = false
                                    },
                                ) {
                                    CalendarPrevious()
                                    CalendarCaption()
                                    CalendarNext()
                                    CalendarWeekdays()
                                    CalendarGrid()
                                }
                            }
                        }
                    },
                ),
                ComponentExample(
                    title = "Months",
                    code =
                        """
                        var open by remember { mutableStateOf(false) }
                        var year by remember { mutableStateOf(2026) }
                        var month by remember { mutableStateOf(3) }
                        var selected by remember { mutableStateOf<CalendarDate?>(null) }
                        Popover(open = open, onOpenChange = { open = it }) {
                            PopoverTrigger {
                                Button(onClick = toggle, variant = ButtonVariant.Outline) {
                                    Text(textStyle = if (selected == null) TextColors.Muted else TextStyle.Default) {
                                        selected?.let(::formatDate) ?: "Pick a date"
                                    }
                                }
                            }
                            PopoverContent {
                                Calendar(
                                    year = year, month = month,
                                    onMonthChange = { y, m -> year = y; month = m },
                                    selected = selected, onSelectedChange = {
                                        selected = it
                                        open = false
                                    },
                                    numberOfMonths = 2,
                                ) {
                                    CalendarMonths()
                                }
                            }
                        }
                        """.trimIndent(),
                    preview = {
                        var open by remember { mutableStateOf(false) }
                        var year by remember { mutableStateOf(2026) }
                        var month by remember { mutableStateOf(3) }
                        var selected by remember { mutableStateOf<CalendarDate?>(null) }
                        Popover(open = open, onOpenChange = { open = it }) {
                            PopoverTrigger {
                                Button(onClick = toggle, variant = ButtonVariant.Outline) {
                                    Text(textStyle = if (selected == null) TextColors.Muted else TextStyle.Default) {
                                        selected?.let(::formatDate) ?: "Pick a date"
                                    }
                                }
                            }
                            PopoverContent {
                                Calendar(
                                    year = year,
                                    month = month,
                                    onMonthChange = { y, m ->
                                        year = y
                                        month = m
                                    },
                                    selected = selected,
                                    onSelectedChange = {
                                        selected = it
                                        open = false
                                    },
                                    numberOfMonths = 2,
                                ) {
                                    CalendarMonths()
                                }
                            }
                        }
                    },
                ),
            ),
    )
