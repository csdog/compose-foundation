@file:OptIn(ExperimentalFoundationStyleApi::class, ExperimentalTypeInference::class)

package com.toutiaoyan.compose.ui.components.calendar

import androidx.compose.foundation.hoverable
import androidx.compose.foundation.layout.ColumnScope
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.wrapContentSize
import androidx.compose.foundation.layout.padding
import androidx.compose.ui.focus.onFocusChanged
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.LineHeightStyle
import com.toutiaoyan.compose.ui.styles.cssStyleable

import com.toutiaoyan.compose.ui.tokens.StylePreset
import com.toutiaoyan.compose.ui.icons.NovaChevronRight
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.interaction.collectIsHoveredAsState
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.style.ExperimentalFoundationStyleApi
import androidx.compose.foundation.style.MutableStyleState
import androidx.compose.foundation.style.Style
import androidx.compose.foundation.style.hovered
import androidx.compose.foundation.style.rememberUpdatedStyleState
import androidx.compose.foundation.style.styleable
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.Immutable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.runtime.staticCompositionLocalOf
import androidx.compose.foundation.style.then
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.withFrameNanos
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.focus.focusRequester
import androidx.compose.ui.focus.focusProperties
import androidx.compose.ui.platform.LocalLayoutDirection
import androidx.compose.ui.platform.LocalWindowInfo
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.unit.LayoutDirection
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.background
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.rotate
import androidx.compose.ui.input.key.Key
import androidx.compose.ui.input.key.KeyEvent
import androidx.compose.ui.input.key.KeyEventType
import androidx.compose.ui.input.key.key
import androidx.compose.ui.input.key.onPreviewKeyEvent
import androidx.compose.ui.input.key.type
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.semantics.disabled
import androidx.compose.ui.semantics.heading
import androidx.compose.ui.semantics.role
import androidx.compose.ui.semantics.selected
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.unit.dp
import com.toutiaoyan.compose.ui.icons.ChevronRight
import com.toutiaoyan.compose.ui.components.icon.Icon
import com.toutiaoyan.compose.ui.components.icon.IconVariant
import com.toutiaoyan.compose.ui.components.dropdownmenu.DropdownMenu
import com.toutiaoyan.compose.ui.components.dropdownmenu.DropdownMenuContent
import com.toutiaoyan.compose.ui.components.dropdownmenu.DropdownMenuItem
import com.toutiaoyan.compose.ui.components.dropdownmenu.DropdownMenuTrigger
import com.toutiaoyan.compose.ui.styles.focusRingShadow
import com.toutiaoyan.compose.ui.styles.rememberMinTouchTargetStyle
import com.toutiaoyan.compose.ui.theme.currentTheme
import com.toutiaoyan.compose.ui.components.text.Text
import com.toutiaoyan.compose.ui.components.text.TextVariant
import kotlin.experimental.ExperimentalTypeInference
import kotlin.jvm.JvmName
import com.toutiaoyan.compose.tailwind.core.twSpacing
import com.toutiaoyan.compose.tailwind.core.twRadius

/** Civil week length used by grid, focus, week-number, and locale weekday tables. */
internal const val DAYS_IN_WEEK = 7

/** Cap when skipping disabled dates during keyboard day-focus moves. */
private const val DISABLED_DATE_FOCUS_HOP_LIMIT = 400

/** react-day-picker `fixedWeeks` always fills a 6-row month grid. */
private const val FIXED_WEEKS_PER_MONTH = 6

/** Months in a civil year (locale month-label tables). */
internal const val MONTHS_IN_YEAR = 12

/** Whole-control dim matching CSS `disabled:opacity-50`. */
private const val DISABLED_OPACITY = 0.5f

/** A civil calendar date (Gregorian by default; see CalendarLocale.calendarSystem) -- the library has no `kotlinx-datetime` dependency,
 so this stays a minimal value type. */
data class CalendarDate(val year: Int, val month: Int, val day: Int) : Comparable<CalendarDate> {
    override fun compareTo(other: CalendarDate): Int {
        if (year != other.year) return year - other.year
        if (month != other.month) return month - other.month
        return day - other.day
    }
}

/** Inclusive start/end for [Calendar] range mode. Matches react-day-picker / shadcn `DateRange`. */
data class CalendarDateRange(val from: CalendarDate?, val to: CalendarDate?)

/** react-day-picker / shadcn `captionLayout`. [Label] is the default caption text. */
enum class CalendarCaptionLayout {
    Label,
    Dropdown,
    DropdownMonths,
    DropdownYears,
}

@Immutable
class CalendarState(
    val year: Int,
    val month: Int,
    val selected: CalendarDate?,
    val rangeFrom: CalendarDate?,
    val rangeTo: CalendarDate?,
    val selectedDates: Set<CalendarDate>,
    val today: CalendarDate?,
    val focusedDate: CalendarDate?,
    val disabled: (CalendarDate) -> Boolean,
    val onMonthChange: (year: Int, month: Int) -> Unit,
    val onSelectedChange: (CalendarDate) -> Unit,
    val onFocusedDateChange: (CalendarDate) -> Unit,
    val numberOfMonths: Int,
    val weekStartsOn: Int,
    val showOutsideDays: Boolean,
    val fixedWeeks: Boolean,
    val hideNavigation: Boolean,
    val disableNavigation: Boolean,
    val pagedNavigation: Boolean,
    val reverseMonths: Boolean,
    val captionLayout: CalendarCaptionLayout,
    val fromYear: Int,
    val toYear: Int,
) {
    internal var locale: CalendarLocale = CalendarLocale.English
    internal val system get() = locale.calendarSystem
    internal var minMonth: CalendarDate? = null
    internal var maxMonth: CalendarDate? = null
    internal val canGoPrevious get() = minMonth?.let { CalendarDate(year, month, 1) > it } ?: true
    internal val canGoNext get() =
        maxMonth?.let {
            val end = addMonths(year, month, numberOfMonths - 1)
            CalendarDate(end.first, end.second, 1) < it
        } ?: true

    internal fun goToMonth(
        year: Int,
        month: Int,
    ) {
        var target = CalendarDate(year, month, 1)
        val lastStart =
            maxMonth?.let {
                addMonths(it.year, it.month, 1 - numberOfMonths).let {
                        (y, m) ->
                    CalendarDate(y, m, 1)
                }
            }
        if (lastStart != null && target > lastStart) target = lastStart
        if (minMonth != null && target < minMonth!!) target = minMonth!!
        onMonthChange(target.year, target.month)
    }

    val caption: String get() = captionFor(year, month)

    fun captionFor(
        year: Int,
        month: Int,
    ) = locale.formatCaption(year, month)

    fun isSelectedDay(date: CalendarDate) =
        date == selected || date == rangeFrom || date == rangeTo || date in selectedDates

    fun isDisabled(date: CalendarDate) = !system.supports(date) || disabled(date)

    fun isRangeMiddle(date: CalendarDate): Boolean {
        val from = rangeFrom ?: return false
        val to = rangeTo ?: return false
        return date > from && date < to
    }

    fun goPrevious() {
        val delta = if (pagedNavigation) numberOfMonths else 1
        val (y, m) = addMonths(year, month, -delta)
        goToMonth(y, m)
    }

    fun goNext() {
        val delta = if (pagedNavigation) numberOfMonths else 1
        val (y, m) = addMonths(year, month, delta)
        goToMonth(y, m)
    }

    fun moveFocusTo(date: CalendarDate) {
        if (!system.supports(date)) return
        val targetMonth = date.copy(day = 1)
        if (minMonth?.let { targetMonth < it } == true || maxMonth?.let { targetMonth > it } == true) return
        val inView =
            (0 until numberOfMonths).any { offset ->
                val (y, m) = addMonths(year, month, offset)
                date.year == y && date.month == m
            }
        if (!inView) {
            goToMonth(date.year, date.month)
        }
        onFocusedDateChange(date)
    }

    fun moveFocusByDays(days: Int) {
        val start = focusedDate ?: today ?: CalendarDate(year, month, 1)
        var candidate = start.plusDays(days, system)
        val step = if (days >= 0) 1 else -1
        var hops = 0
        while (isDisabled(candidate) && hops < DISABLED_DATE_FOCUS_HOP_LIMIT) {
            candidate = candidate.plusDays(step, system)
            hops++
        }
        if (!isDisabled(candidate)) moveFocusTo(candidate)
    }

    fun moveFocusToWeekEdge(end: Boolean) {
        val start = focusedDate ?: today ?: CalendarDate(year, month, 1)
        val index = weekDayIndex(system.dayOfWeek(start), weekStartsOn)
        val target =
            if (end) {
                start.plusDays(DAYS_IN_WEEK - 1 - index, system)
            } else {
                start.plusDays(-index, system)
            }
        if (!isDisabled(target)) moveFocusTo(target)
    }

    fun moveFocusByMonths(delta: Int) {
        val start = focusedDate ?: CalendarDate(year, month, 1)
        var y = start.year
        var m = start.month
        repeat(kotlin.math.abs(delta)) {
            val next = if (delta > 0) nextMonth(y, m) else previousMonth(y, m)
            y = next.first
            m = next.second
        }
        if (!system.supports(CalendarDate(y, m, 1))) return
        val date = CalendarDate(y, m, start.day.coerceAtMost(system.daysInMonth(y, m)))
        if (!isDisabled(date)) moveFocusTo(date)
    }

    fun selectFocused() {
        val date = focusedDate ?: return
        if (!isDisabled(date)) onSelectedChange(date)
    }
}

private class CalendarFocusRegistry {
    val items = mutableMapOf<CalendarDate, MutableList<Pair<Boolean, FocusRequester>>>()

    fun focus(date: CalendarDate?) {
        val entries = items[date] ?: return
        (entries.firstOrNull { it.first } ?: entries.firstOrNull())?.second?.requestFocus()
    }
}

private val LocalCalendarFocus = staticCompositionLocalOf<CalendarFocusRegistry?> { null }

private val LocalCalendar = staticCompositionLocalOf<CalendarState?> { null }

/** Reads the nearest [Calendar]. Matches react-day-picker / shadcn calendar context. */
@Composable
fun calendarState(): CalendarState =
    checkNotNull(LocalCalendar.current) {
        "calendarState() must be used inside Calendar { … }"
    }

class CalendarScope internal constructor()

// Sakamoto's algorithm: 0 = Sunday .. 6 = Saturday. Offsets stay literal (source table).
private val SAKAMOTO_OFFSETS = intArrayOf(0, 3, 2, 5, 0, 3, 5, 1, 4, 6, 2, 4)

private fun sakamotoDayOfWeek(
    year: Int,
    month: Int,
    day: Int,
): Int {
    val y = if (month < 3) year - 1 else year
    return (y + y / 4 - y / 100 + y / 400 + SAKAMOTO_OFFSETS[month - 1] + day) % DAYS_IN_WEEK
}

/** Index of [dayOfWeek] within a week that starts on [weekStartsOn] (both 0=Sun..6=Sat). */
private fun weekDayIndex(
    dayOfWeek: Int,
    weekStartsOn: Int,
): Int = (dayOfWeek - weekStartsOn + DAYS_IN_WEEK) % DAYS_IN_WEEK

/** 0 = Sunday .. 6 = Saturday, matching JS `Date#getDay()` / react-day-picker. */
fun CalendarDate.dayOfWeek(): Int = sakamotoDayOfWeek(year, month, day)

private fun previousMonth(
    year: Int,
    month: Int,
): Pair<Int, Int> = if (month == 1) year - 1 to MONTHS_IN_YEAR else year to month - 1

private fun nextMonth(
    year: Int,
    month: Int,
): Pair<Int, Int> = if (month == MONTHS_IN_YEAR) year + 1 to 1 else year to month + 1

private fun addMonths(
    year: Int,
    month: Int,
    delta: Int,
): Pair<Int, Int> {
    var y = year
    var m = month
    repeat(kotlin.math.abs(delta)) {
        val next = if (delta > 0) nextMonth(y, m) else previousMonth(y, m)
        y = next.first
        m = next.second
    }
    return y to m
}

private fun CalendarDate.plusDays(
    days: Int,
    system: CalendarSystem = CalendarSystem.Gregorian,
): CalendarDate {
    var y = year
    var m = month
    var d = day + days
    while (true) {
        if (!system.supports(CalendarDate(y, m, 1))) return system.clamp(CalendarDate(y, m, 1))
        val dim = system.daysInMonth(y, m)
        when {
            d > dim -> {
                d -= dim
                val next = nextMonth(y, m)
                y = next.first
                m = next.second
            }
            d < 1 -> {
                val prev = previousMonth(y, m)
                y = prev.first
                m = prev.second
                if (!system.supports(CalendarDate(y, m, 1))) return system.clamp(CalendarDate(y, m, 1))
                d += system.daysInMonth(y, m)
            }
            else -> return CalendarDate(y, m, d)
        }
    }
}

/**
 * A single-month date picker grid, matching real shadcn/ui's `calendar.tsx` layout
 * (month caption with prev/next chevrons, weekday row, 6x7 day grid with outside-month
 * days shown muted). Real shadcn wraps `react-day-picker`; since there is no KMP
 * equivalent and no `kotlinx-datetime` dependency in this library, the month grid is
 * computed here directly via Sakamoto's algorithm -- plain Gregorian arithmetic, no
 * external date library needed for what is fundamentally a fixed 42-cell grid.
 *
 * [year]/[month] (the *displayed* month, 1-indexed) are hoisted separately from
 * [selected] so navigating months doesn't require a selection.
 *
 * Usage:
 * ```
 * var year by remember { mutableStateOf(2026) }
 * var month by remember { mutableStateOf(3) }
 * var selected by remember { mutableStateOf<CalendarDate?>(null) }
 * Calendar(
 *     year = year, month = month,
 *     onMonthChange = { y, m -> year = y; month = m },
 *     selected = selected, onSelectedChange = { selected = it },
 * )
 * Calendar(..., disabled = { it.dayOfWeek() == 0 || it.dayOfWeek() == 6 })
 * Calendar(..., numberOfMonths = 2)
 * Calendar(..., weekStartsOn = 1)
 * Calendar(..., showOutsideDays = false)
 * Calendar(..., startMonth = CalendarDate(2026, 3, 5), endMonth = CalendarDate(2026, 3, 20))
 * Calendar(..., fixedWeeks = false)
 * ```
 *
 * Omit [content] for the default caption + weekday row + month grid. Pass
 * [CalendarHeader] / [CalendarWeekdays] / [CalendarGrid] to reassemble, matching
 * react-day-picker's replaceable parts.
 *
 * Range mode matches shadcn `mode="range"`:
 * ```
 * var range by remember { mutableStateOf(CalendarDateRange(from = null, to = null)) }
 * Calendar(
 *     year = year, month = month,
 *     onMonthChange = { y, m -> year = y; month = m },
 *     selectedRange = range, onSelectedRangeChange = { range = it },
 * )
 * ```
 */
@Composable
fun Calendar(
    year: Int,
    month: Int,
    onMonthChange: (year: Int, month: Int) -> Unit,
    selected: CalendarDate?,
    onSelectedChange: (CalendarDate) -> Unit,
    modifier: Modifier = Modifier,
    today: CalendarDate? = null,
    disabled: (CalendarDate) -> Boolean = { false },
    numberOfMonths: Int = 1,
    weekStartsOn: Int = LocalCalendarLocale.current.firstDayOfWeek,
    showOutsideDays: Boolean = true,
    fromDate: CalendarDate? = null,
    toDate: CalendarDate? = null,
    startMonth: CalendarDate? = null,
    endMonth: CalendarDate? = null,
    fromMonth: CalendarDate? = null,
    toMonth: CalendarDate? = null,
    fixedWeeks: Boolean = false,
    hideNavigation: Boolean = false,
    disableNavigation: Boolean = false,
    pagedNavigation: Boolean = false,
    reverseMonths: Boolean = false,
    captionLayout: CalendarCaptionLayout = CalendarCaptionLayout.Label,
    style: Style = Style,
    cellSize: Dp? = null,
    showWeekNumber: Boolean = false,
    content: @Composable CalendarScope.() -> Unit = { CalendarDefaultContent() },
) {
    CalendarImpl(
        year = year,
        month = month,
        onMonthChange = onMonthChange,
        selected = selected,
        rangeFrom = null,
        rangeTo = null,
        selectedDates = emptySet(),
        onDayClick = onSelectedChange,
        modifier = modifier,
        today = today,
        disabled = disabled,
        numberOfMonths = numberOfMonths,
        weekStartsOn = weekStartsOn,
        showOutsideDays = showOutsideDays,
        fromDate = (startMonth ?: fromMonth)?.copy(day = 1) ?: fromDate,
        toDate =
            (endMonth ?: toMonth)?.let {
                it.copy(day = LocalCalendarLocale.current.calendarSystem.daysInMonth(it.year, it.month))
            } ?: toDate,
        fixedWeeks = fixedWeeks,
        hideNavigation = hideNavigation,
        disableNavigation = disableNavigation,
        pagedNavigation = pagedNavigation,
        reverseMonths = reverseMonths,
        captionLayout = captionLayout,
        style = style,
        cellSize = cellSize,
        showWeekNumber = showWeekNumber,
        content = content,
    )
}

/** Range-selection overload. Matches shadcn Calendar `mode="range"`. */
@Composable
fun Calendar(
    year: Int,
    month: Int,
    onMonthChange: (year: Int, month: Int) -> Unit,
    selectedRange: CalendarDateRange?,
    onSelectedRangeChange: (CalendarDateRange) -> Unit,
    modifier: Modifier = Modifier,
    today: CalendarDate? = null,
    disabled: (CalendarDate) -> Boolean = { false },
    numberOfMonths: Int = 1,
    weekStartsOn: Int = LocalCalendarLocale.current.firstDayOfWeek,
    showOutsideDays: Boolean = true,
    fromDate: CalendarDate? = null,
    toDate: CalendarDate? = null,
    startMonth: CalendarDate? = null,
    endMonth: CalendarDate? = null,
    fromMonth: CalendarDate? = null,
    toMonth: CalendarDate? = null,
    fixedWeeks: Boolean = false,
    hideNavigation: Boolean = false,
    disableNavigation: Boolean = false,
    pagedNavigation: Boolean = false,
    reverseMonths: Boolean = false,
    captionLayout: CalendarCaptionLayout = CalendarCaptionLayout.Label,
    style: Style = Style,
    cellSize: Dp? = null,
    showWeekNumber: Boolean = false,
    content: @Composable CalendarScope.() -> Unit = { CalendarDefaultContent() },
) {
    CalendarImpl(
        year = year,
        month = month,
        onMonthChange = onMonthChange,
        selected = null,
        rangeFrom = selectedRange?.from,
        rangeTo = selectedRange?.to,
        selectedDates = emptySet(),
        onDayClick = { date ->
            val from = selectedRange?.from
            val to = selectedRange?.to
            val next =
                when {
                    from == null || to != null -> CalendarDateRange(from = date, to = null)
                    date < from -> CalendarDateRange(from = date, to = from)
                    else -> CalendarDateRange(from = from, to = date)
                }
            onSelectedRangeChange(next)
        },
        modifier = modifier,
        today = today,
        disabled = disabled,
        numberOfMonths = numberOfMonths,
        weekStartsOn = weekStartsOn,
        showOutsideDays = showOutsideDays,
        fromDate = (startMonth ?: fromMonth)?.copy(day = 1) ?: fromDate,
        toDate =
            (endMonth ?: toMonth)?.let {
                it.copy(day = LocalCalendarLocale.current.calendarSystem.daysInMonth(it.year, it.month))
            } ?: toDate,
        fixedWeeks = fixedWeeks,
        hideNavigation = hideNavigation,
        disableNavigation = disableNavigation,
        pagedNavigation = pagedNavigation,
        reverseMonths = reverseMonths,
        captionLayout = captionLayout,
        style = style,
        cellSize = cellSize,
        showWeekNumber = showWeekNumber,
        content = content,
    )
}

/** Multi-date overload. Matches shadcn Calendar `mode="multiple"`. */
@Composable
fun Calendar(
    year: Int,
    month: Int,
    onMonthChange: (year: Int, month: Int) -> Unit,
    selectedDates: Set<CalendarDate>,
    onSelectedDatesChange: (Set<CalendarDate>) -> Unit,
    modifier: Modifier = Modifier,
    today: CalendarDate? = null,
    disabled: (CalendarDate) -> Boolean = { false },
    numberOfMonths: Int = 1,
    weekStartsOn: Int = LocalCalendarLocale.current.firstDayOfWeek,
    showOutsideDays: Boolean = true,
    fromDate: CalendarDate? = null,
    toDate: CalendarDate? = null,
    startMonth: CalendarDate? = null,
    endMonth: CalendarDate? = null,
    fromMonth: CalendarDate? = null,
    toMonth: CalendarDate? = null,
    fixedWeeks: Boolean = false,
    hideNavigation: Boolean = false,
    disableNavigation: Boolean = false,
    pagedNavigation: Boolean = false,
    reverseMonths: Boolean = false,
    captionLayout: CalendarCaptionLayout = CalendarCaptionLayout.Label,
    style: Style = Style,
    cellSize: Dp? = null,
    showWeekNumber: Boolean = false,
    content: @Composable CalendarScope.() -> Unit = { CalendarDefaultContent() },
) {
    CalendarImpl(
        year = year,
        month = month,
        onMonthChange = onMonthChange,
        selected = null,
        rangeFrom = null,
        rangeTo = null,
        selectedDates = selectedDates,
        onDayClick = { date ->
            onSelectedDatesChange(
                if (date in selectedDates) selectedDates - date else selectedDates + date,
            )
        },
        modifier = modifier,
        today = today,
        disabled = disabled,
        numberOfMonths = numberOfMonths,
        weekStartsOn = weekStartsOn,
        showOutsideDays = showOutsideDays,
        fromDate = (startMonth ?: fromMonth)?.copy(day = 1) ?: fromDate,
        toDate =
            (endMonth ?: toMonth)?.let {
                it.copy(day = LocalCalendarLocale.current.calendarSystem.daysInMonth(it.year, it.month))
            } ?: toDate,
        fixedWeeks = fixedWeeks,
        hideNavigation = hideNavigation,
        disableNavigation = disableNavigation,
        pagedNavigation = pagedNavigation,
        reverseMonths = reverseMonths,
        captionLayout = captionLayout,
        style = style,
        cellSize = cellSize,
        showWeekNumber = showWeekNumber,
        content = content,
    )
}

@Composable
private fun CalendarImpl(
    year: Int,
    month: Int,
    onMonthChange: (year: Int, month: Int) -> Unit,
    selected: CalendarDate?,
    rangeFrom: CalendarDate?,
    rangeTo: CalendarDate?,
    selectedDates: Set<CalendarDate>,
    onDayClick: (CalendarDate) -> Unit,
    modifier: Modifier,
    today: CalendarDate?,
    disabled: (CalendarDate) -> Boolean,
    numberOfMonths: Int,
    weekStartsOn: Int,
    showOutsideDays: Boolean,
    fromDate: CalendarDate?,
    toDate: CalendarDate?,
    fixedWeeks: Boolean,
    hideNavigation: Boolean,
    disableNavigation: Boolean,
    pagedNavigation: Boolean,
    reverseMonths: Boolean,
    captionLayout: CalendarCaptionLayout,
    style: Style,
    cellSize: Dp?,
    showWeekNumber: Boolean,
    content: @Composable CalendarScope.() -> Unit,
) {
    require(cellSize == null || (cellSize.value.isFinite() && cellSize > 0.dp))
    val system = LocalCalendarLocale.current.calendarSystem
    require(system.supports(CalendarDate(year, month, 1)))
    val lastMonth = addMonths(year, month, numberOfMonths.coerceAtLeast(1) - 1)
    require(system.supports(CalendarDate(lastMonth.first, lastMonth.second, 1)))
    val referenceYear = remember(today?.year) { today?.year ?: year }
    // Real shadcn's calendar (react-day-picker) tracks a roving keyboard-focus cursor
    // separately from the selected date -- the ring you see on a freshly-opened
    // calendar is that cursor, not a "selected" style (`data-selected` is a plain
    // `bg-primary` fill with no ring at all; the ring is `group-data-[focused=true]/day:
    // ring-[3px] ring-ring/50`). DayPicker seeds the cursor at the selected date (or
    // today) on mount via its own `useEffect(() => { if (modifiers.focused)
    // ref.current?.focus() })`, and moves it whenever a day is clicked. `remember(selected)`
    // re-seeds this on every external `selected` change, matching that mount effect.
    // Arrow keys / Home / End / PageUp / PageDown move the cursor; Enter / Space select.
    var focusedDate by remember(selected, rangeFrom, selectedDates) {
        mutableStateOf(selected ?: rangeFrom ?: selectedDates.minOrNull() ?: today)
    }
    val focusRegistry = remember { CalendarFocusRegistry() }
    var focusRevision by remember { mutableIntStateOf(0) }
    val rootStyleState = remember { MutableStyleState(MutableInteractionSource()) }
    val focusCandidates =
        listOfNotNull(focusedDate, selected, rangeFrom, selectedDates.minOrNull(), today) +
            (1..system.daysInMonth(year, month)).map { CalendarDate(year, month, it) }
    val resolvedFocus =
        focusCandidates.firstOrNull { date ->
            (0 until numberOfMonths.coerceAtLeast(1)).any { offset ->
                val visible = addMonths(year, month, offset)
                date.year == visible.first && date.month == visible.second
            } && !disabled(date) && (fromDate == null || date >= fromDate) && (toDate == null || date <= toDate)
        }
    val state =
        CalendarState(
            year = year,
            month = month,
            selected = selected,
            rangeFrom = rangeFrom,
            rangeTo = rangeTo,
            selectedDates = selectedDates,
            today = today,
            focusedDate = resolvedFocus,
            disabled = { date ->
                (fromDate != null && date < fromDate) ||
                    (toDate != null && date > toDate) ||
                    disabled(date)
            },
            onMonthChange = onMonthChange,
            onSelectedChange = onDayClick,
            onFocusedDateChange = {
                focusedDate = it
                focusRevision++
            },
            numberOfMonths = numberOfMonths.coerceAtLeast(1),
            weekStartsOn = weekStartsOn.coerceIn(0, DAYS_IN_WEEK - 1),
            showOutsideDays = showOutsideDays,
            fixedWeeks = fixedWeeks,
            hideNavigation = hideNavigation,
            disableNavigation = disableNavigation,
            pagedNavigation = pagedNavigation,
            reverseMonths = reverseMonths,
            captionLayout = captionLayout,
            fromYear = system.clamp(CalendarDate(fromDate?.year ?: (referenceYear - 100), 1, 1)).year,
            toYear = system.clamp(CalendarDate(toDate?.year ?: referenceYear, 12, 1)).year,
        )

    LaunchedEffect(focusedDate, focusRevision) { if (focusRevision > 0) focusRegistry.focus(resolvedFocus) }
    state.locale = LocalCalendarLocale.current
    state.minMonth = fromDate?.copy(day = 1)
        ?: if (captionLayout != CalendarCaptionLayout.Label) CalendarDate(referenceYear - 100, 1, 1) else null
    state.maxMonth = toDate?.copy(day = 1)
        ?: if (captionLayout != CalendarCaptionLayout.Label) CalendarDate(referenceYear, 12, 1) else null
    if (system == CalendarSystem.Persian) {
        state.minMonth = maxOf(state.minMonth ?: CalendarDate(1, 1, 1), CalendarDate(1, 1, 1))
        state.maxMonth = minOf(state.maxMonth ?: CalendarDate(3176, 12, 1), CalendarDate(3176, 12, 1))
    }
    val autoFocus = LocalCalendarAutoFocus.current
    LaunchedEffect(autoFocus) {
        if (autoFocus) {
            withFrameNanos { }
            val target =
                (
                    listOfNotNull(focusedDate, today) +
                        (1..system.daysInMonth(year, month)).map {
                            CalendarDate(year, month, it)
                        }
                ).firstOrNull { focusRegistry.items.containsKey(it) && !state.isDisabled(it) }
            if (target != null && target != focusedDate) {
                focusedDate = target
                focusRevision++
            } else {
                focusRegistry.focus(target)
            }
        }
    }
    CompositionLocalProvider(
        LocalCalendar provides state,
        LocalCalendarFocus provides focusRegistry,
        LocalCalendarCellSize provides cellSize,
        LocalCalendarWeekNumbers provides showWeekNumber,
    ) {
        Column(
            modifier =
                modifier
                    .wrapContentSize(Alignment.TopStart, unbounded = true)
                    .styleable(
                        rootStyleState,
                        rememberCalendarRootStyle() then style,
                    ),
            verticalArrangement = Arrangement.spacedBy(twSpacing.sm),
        ) {
            CalendarScope().content()
        }
    }
}

private fun handleCalendarKey(
    state: CalendarState,
    event: KeyEvent,
    rtl: Boolean,
): Boolean {
    if (event.type != KeyEventType.KeyDown) return false
    when (event.key) {
        Key.DirectionLeft -> state.moveFocusByDays(if (rtl) 1 else -1)
        Key.DirectionRight -> state.moveFocusByDays(if (rtl) -1 else 1)
        Key.DirectionUp -> state.moveFocusByDays(-DAYS_IN_WEEK)
        Key.DirectionDown -> state.moveFocusByDays(DAYS_IN_WEEK)
        Key.MoveHome -> state.moveFocusToWeekEdge(end = false)
        Key.MoveEnd -> state.moveFocusToWeekEdge(end = true)
        Key.PageUp -> state.moveFocusByMonths(-1)
        Key.PageDown -> state.moveFocusByMonths(1)
        Key.Enter, Key.NumPadEnter, Key.Spacebar -> state.selectFocused()
        else -> return false
    }
    return true
}

private val LocalCalendarCellSize = staticCompositionLocalOf<Dp?> { null }
private val LocalCalendarWeekNumbers = staticCompositionLocalOf { false }

private val CELL_SIZE: Dp
    @Composable get() =
        LocalCalendarCellSize.current
            ?: if (currentTheme.activePreset == StylePreset.Nova) {
                28.dp
            } else {
                36.dp
            }

private val CALENDAR_WIDTH: Dp
    @Composable get() =
        CELL_SIZE *
            if (LocalCalendarWeekNumbers.current) {
                DAYS_IN_WEEK + 1
            } else {
                DAYS_IN_WEEK
            }

/** One cell in a month grid (`date` + whether it falls in the displayed month). */
data class CalendarCell(val date: CalendarDate, val inCurrentMonth: Boolean)

internal fun buildMonthGrid(
    year: Int,
    month: Int,
    weekStartsOn: Int = 0,
    fixedWeeks: Boolean = false,
    system: CalendarSystem = CalendarSystem.Gregorian,
): List<List<CalendarCell>> {
    val firstWeekday = weekDayIndex(system.dayOfWeek(CalendarDate(year, month, 1)), weekStartsOn)
    val daysThisMonth = system.daysInMonth(year, month)
    val (prevYear, prevMonth) = previousMonth(year, month)
    val daysPrevMonth = system.precedingMonthLength(prevYear, prevMonth)
    val (nextYear, nextMonth) = nextMonth(year, month)

    val cells = mutableListOf<CalendarCell>()
    for (i in firstWeekday downTo 1) {
        cells += CalendarCell(CalendarDate(prevYear, prevMonth, daysPrevMonth - i + 1), inCurrentMonth = false)
    }
    for (day in 1..daysThisMonth) {
        cells += CalendarCell(CalendarDate(year, month, day), inCurrentMonth = true)
    }
    var nextDay = 1
    while (cells.size % DAYS_IN_WEEK != 0) {
        cells += CalendarCell(CalendarDate(nextYear, nextMonth, nextDay), inCurrentMonth = false)
        nextDay++
    }
    if (fixedWeeks) {
        while (cells.size < FIXED_WEEKS_PER_MONTH * DAYS_IN_WEEK) {
            cells += CalendarCell(CalendarDate(nextYear, nextMonth, nextDay), inCurrentMonth = false)
            nextDay++
        }
    }
    return cells.chunked(DAYS_IN_WEEK)
}

/** Default caption + weekday row + month grid. Multi-month uses [CalendarMonths]. */
@Composable
fun CalendarDefaultContent() {
    if (calendarState().numberOfMonths <= 1) {
        Column(verticalArrangement = Arrangement.spacedBy(16.dp)) {
            CalendarHeader()
            Column {
                CalendarWeekdays()
                CalendarGrid()
            }
        }
    } else {
        CalendarMonths()
    }
}

/** Side-by-side months for [Calendar] `numberOfMonths` > 1. Matches react-day-picker. */
@Composable
fun CalendarMonths() {
    val state = calendarState()
    val count = state.numberOfMonths.coerceAtLeast(1)
    val offsets =
        if (state.reverseMonths) {
            (count - 1 downTo 0).toList()
        } else {
            (0 until count).toList()
        }
    val windowWidth = with(LocalDensity.current) { LocalWindowInfo.current.containerSize.width.toDp() }
    FlowRow(
        horizontalArrangement = Arrangement.spacedBy(twSpacing.lg),
        verticalArrangement = Arrangement.spacedBy(twSpacing.lg),
        maxItemsInEachRow =
            if (currentTheme.activePreset == StylePreset.Nova &&
                windowWidth < 768.dp
            ) {
                1
            } else {
                count
            },
    ) {
        offsets.forEachIndexed { visualIndex, offset ->
            val (y, m) = addMonths(state.year, state.month, offset)
            Column(verticalArrangement = Arrangement.spacedBy(16.dp)) {
                Row(
                    modifier = Modifier.size(CALENDAR_WIDTH, CELL_SIZE),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically,
                ) {
                    if (visualIndex == 0 && !state.hideNavigation) {
                        CalendarPrevious()
                    } else {
                        Box(modifier = Modifier.size(CELL_SIZE))
                    }
                    if (state.captionLayout == CalendarCaptionLayout.Label) {
                        CalendarTitle(state.captionFor(y, m))
                    } else {
                        CalendarCaptionDropdowns(year = y, month = m, monthOffset = offset)
                    }
                    if (visualIndex == count - 1 && !state.hideNavigation) {
                        CalendarNext()
                    } else {
                        Box(modifier = Modifier.size(CELL_SIZE))
                    }
                }
                Column {
                    CalendarWeekdays()
                    CalendarGrid(year = y, month = m)
                }
            }
        }
    }
}

@Composable
fun CalendarHeader() {
    if (calendarState().hideNavigation) {
        Box(
            modifier = Modifier.size(CALENDAR_WIDTH, CELL_SIZE),
            contentAlignment = Alignment.Center,
        ) {
            CalendarCaption()
        }
        return
    }
    Row(
        modifier = Modifier.size(CALENDAR_WIDTH, CELL_SIZE),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically,
    ) {
        CalendarPrevious()
        CalendarCaption()
        CalendarNext()
    }
}

/** Weekday labels row. Matches react-day-picker / shadcn calendar weekdays. */
@Composable
fun CalendarWeekdays() {
    val weekStartsOn = calendarState().weekStartsOn
    val weekdays = LocalCalendarLocale.current.weekdays
    val labels = weekdays.drop(weekStartsOn) + weekdays.take(weekStartsOn)
    val size = CELL_SIZE
    val height = if (currentTheme.activePreset == StylePreset.Nova) 22.4.dp else size
    Row(modifier = Modifier.size(CALENDAR_WIDTH, height)) {
        if (LocalCalendarWeekNumbers.current) Spacer(Modifier.size(size, height))
        labels.forEach { label ->
            Box(Modifier.size(size, height), contentAlignment = Alignment.Center) {
                Text(
                    textStyle =
                        TextStyle(
                            fontSize = 12.8.sp,
                            lineHeight = 22.4.sp,
                            fontWeight = FontWeight.Normal,
                            color = currentTheme.colors.mutedForeground,
                            lineHeightStyle =
                                LineHeightStyle(
                                    LineHeightStyle.Alignment.Center,
                                    LineHeightStyle.Trim.None,
                                ),
                        ),
                ) { label }
            }
        }
    }
}

/** Variable-week day grid of the displayed month. Matches react-day-picker month grid. */
@Composable
fun CalendarGrid(
    year: Int? = null,
    month: Int? = null,
    modifier: Modifier = Modifier,
    dayStyle: @Composable (CalendarCell) -> Style = { Style },
    dayContent: (@Composable ColumnScope.(CalendarCell) -> Unit)? = null,
) {
    val state = calendarState()
    val y = year ?: state.year
    val m = month ?: state.month
    val weeks =
        remember(y, m, state.weekStartsOn, state.fixedWeeks, state.system) {
            buildMonthGrid(y, m, state.weekStartsOn, state.fixedWeeks, state.system)
        }
    Column(modifier.padding(top = 8.dp), verticalArrangement = Arrangement.spacedBy(8.dp)) {
        weeks.forEach { week ->
            Row {
                if (LocalCalendarWeekNumbers.current) {
                    Box(Modifier.size(CELL_SIZE), contentAlignment = Alignment.Center) {
                        Text(textStyle = TextStyle(fontSize = 12.8.sp, color = currentTheme.colors.mutedForeground)) {
                            calendarWeekNumber(
                                week.first().date,
                                state.weekStartsOn,
                                state.system,
                            ).toString().padStart(2, '0')
                        }
                    }
                }
                week.forEach { cell ->
                    CalendarDayButton(
                        cell = cell,
                        isSelected = state.isSelectedDay(cell.date),
                        isRangeMiddle = state.isRangeMiddle(cell.date),
                        isToday = state.today != null && cell.date == state.today,
                        isFocused = cell.date == state.focusedDate,
                        isDisabled = state.isDisabled(cell.date),
                        outsideHidden =
                            !state.system.supports(cell.date) || (!state.showOutsideDays && !cell.inCurrentMonth),
                        onClick = {
                            state.onSelectedChange(cell.date)
                            state.onFocusedDateChange(cell.date)
                        },
                        style = dayStyle(cell),
                        content =
                            if (dayContent == null) {
                                null
                            } else {
                                { dayContent(cell) }
                            },
                    )
                }
            }
        }
    }
}

// DayPicker's default locale uses a Sunday-start week containing January 1.
internal fun calendarWeekNumber(
    date: CalendarDate,
    weekStartsOn: Int,
    system: CalendarSystem = CalendarSystem.Gregorian,
): Int {
    val supportedDate = system.clamp(date)
    val end =
        supportedDate.plusDays(
            DAYS_IN_WEEK - 1 - weekDayIndex(system.dayOfWeek(supportedDate), weekStartsOn),
            system,
        )
    val dayOfYear = (1 until end.month).sumOf { system.daysInMonth(end.year, it) } + end.day
    return (dayOfYear - 1) / DAYS_IN_WEEK + 1
}

/** Previous-month chevron. Matches react-day-picker / shadcn calendar nav. */
@Composable
fun CalendarPrevious(
    onClick: (() -> Unit)? = null,
    disabled: Boolean = false,
) {
    val resolvedOnClick = onClick ?: calendarState()::goPrevious
    CalendarNavButton(pointingLeft = true, onClick = resolvedOnClick, disabled = disabled)
}

/** Next-month chevron. Matches react-day-picker / shadcn calendar nav. */
@Composable
fun CalendarNext(
    onClick: (() -> Unit)? = null,
    disabled: Boolean = false,
) {
    val resolvedOnClick = onClick ?: calendarState()::goNext
    CalendarNavButton(pointingLeft = false, onClick = resolvedOnClick, disabled = disabled)
}

/** Month + year label in the calendar header. Matches react-day-picker caption. */
@Composable
fun CalendarTitle(
    text: String = calendarState().caption,
    modifier: Modifier = Modifier,
) {
    Text(modifier = modifier.semantics { heading() }, variant = TextVariant.LabelLarge) { text }
}

@Composable
@JvmName("CalendarTitleLambda")
@OverloadResolutionByLambdaReturnType
fun CalendarTitle(
    modifier: Modifier = Modifier,
    text: () -> String,
) {
    CalendarTitle(text = text(), modifier = modifier)
}

/** Alias for [CalendarTitle] matching react-day-picker / shadcn `CalendarCaption`.
 * When [CalendarCaptionLayout] is not [CalendarCaptionLayout.Label], renders month/year
 * dropdowns instead of the plain caption (react-day-picker `captionLayout`). */
@Composable
fun CalendarCaption(
    text: String = calendarState().caption,
    modifier: Modifier = Modifier,
) {
    val layout = calendarState().captionLayout
    if (layout == CalendarCaptionLayout.Label) {
        CalendarTitle(text = text, modifier = modifier)
    } else {
        CalendarCaptionDropdowns(modifier = modifier)
    }
}

@Composable
@JvmName("CalendarCaptionLambda")
@OverloadResolutionByLambdaReturnType
fun CalendarCaption(
    modifier: Modifier = Modifier,
    text: () -> String,
) {
    CalendarCaption(text = text(), modifier = modifier)
}

@Composable
private fun CalendarCaptionDropdowns(
    modifier: Modifier = Modifier,
    year: Int = calendarState().year,
    month: Int = calendarState().month,
    monthOffset: Int = 0,
) {
    val state = calendarState()

    fun changeMonth(
        year: Int,
        month: Int,
    ) {
        val (firstYear, firstMonth) = addMonths(year, month, -monthOffset)
        state.goToMonth(firstYear, firstMonth)
    }
    Row(
        modifier = modifier,
        horizontalArrangement = Arrangement.spacedBy(twSpacing.xs),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        if (state.captionLayout == CalendarCaptionLayout.DropdownYears) {
            CalendarTitle(LocalCalendarLocale.current.months[month - 1])
        } else {
            CalendarCaptionSelect(
                label = LocalCalendarLocale.current.shortMonths[month - 1],
                options = LocalCalendarLocale.current.shortMonths,
                onSelect = { changeMonth(year, it + 1) },
            )
        }
        if (state.captionLayout == CalendarCaptionLayout.DropdownMonths) {
            CalendarTitle(year.toString())
        } else {
            val years = (state.fromYear.coerceAtMost(state.toYear)..state.toYear.coerceAtLeast(state.fromYear)).toList()
            CalendarCaptionSelect(
                label = year.toString(),
                options = years.map { it.toString() },
                onSelect = { changeMonth(years[it], month) },
            )
        }
    }
}

@Composable
private fun CalendarCaptionSelect(
    label: String,
    options: List<String>,
    onSelect: (Int) -> Unit,
) {
    DropdownMenu {
        DropdownMenuTrigger(style = rememberCalendarCaptionDropdownStyle()) {
            Row(
                horizontalArrangement = Arrangement.spacedBy(twSpacing.xs2),
                verticalAlignment = Alignment.CenterVertically,
            ) {
                Text(variant = TextVariant.LabelSmall) { label }
                Icon(
                    if (currentTheme.activePreset == StylePreset.Nova) NovaChevronRight else ChevronRight,
                    tint = currentTheme.colors.mutedForeground,
                    modifier = Modifier.rotate(90f),
                    variant =
                        if (currentTheme.activePreset == StylePreset.Nova) {
                            IconVariant.Default
                        } else {
                            IconVariant.Small
                        },
                )
            }
        }
        DropdownMenuContent {
            options.forEachIndexed { index, option ->
                DropdownMenuItem(
                    label = option,
                    onClick = { onSelect(index) },
                )
            }
        }
    }
}

@Composable
private fun CalendarNavButton(
    pointingLeft: Boolean,
    onClick: () -> Unit,
    disabled: Boolean = false,
) {
    val locale = LocalCalendarLocale.current
    val rtl = LocalLayoutDirection.current == LayoutDirection.Rtl
    val enabled =
        !calendarState().disableNavigation &&
            !disabled &&
            (
                if (pointingLeft) {
                    calendarState().canGoPrevious
                } else {
                    calendarState().canGoNext
                }
            )
    val interactionSource = remember { MutableInteractionSource() }
    val styleState =
        rememberUpdatedStyleState(interactionSource) {
            it.isEnabled = enabled
        }
    val pressStyle = rememberCalendarPressMotionStyle(interactionSource)
    val touchTargetStyle = if (currentTheme.activePreset == StylePreset.Nova) Style else rememberMinTouchTargetStyle()
    Box(
        modifier =
            Modifier
                .semantics {
                    role = Role.Button
                    contentDescription = if (pointingLeft) locale.previousMonth else locale.nextMonth
                    if (!enabled) disabled()
                }
                .clickable(
                    interactionSource = interactionSource,
                    indication = null,
                    enabled = enabled,
                    role = Role.Button,
                    onClick = onClick,
                )
                .then(if (enabled) Modifier else Modifier.alpha(DISABLED_OPACITY))
                .styleable(styleState, touchTargetStyle then pressStyle),
        contentAlignment = Alignment.Center,
    ) {
        Box(
            modifier = Modifier.styleable(styleState, rememberCalendarNavButtonStyle(CELL_SIZE)),
            contentAlignment = Alignment.Center,
        ) {
            Icon(
                if (currentTheme.activePreset == StylePreset.Nova) NovaChevronRight else ChevronRight,
                tint = currentTheme.colors.foreground,
                modifier = if (pointingLeft != rtl) Modifier.rotate(180f) else Modifier,
                variant = if (currentTheme.activePreset == StylePreset.Nova) IconVariant.Default else IconVariant.Small,
            )
        }
    }
}

/**
 * Day cell button inside [CalendarGrid]. Matches Base UI / shadcn `CalendarDayButton`
 * (react-day-picker `DayButton` styling surface).
 */
@Composable
fun CalendarDayButton(
    cell: CalendarCell,
    isSelected: Boolean,
    isRangeMiddle: Boolean,
    isToday: Boolean,
    isFocused: Boolean,
    isDisabled: Boolean,
    outsideHidden: Boolean,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    style: Style = Style,
    content: (@Composable ColumnScope.() -> Unit)? = null,
) {
    if (outsideHidden) {
        Box(modifier.size(CELL_SIZE))
        return
    }
    var hasFocus by remember { mutableStateOf(false) }
    val rtl = LocalLayoutDirection.current == LayoutDirection.Rtl
    val accessibleDate = LocalCalendarLocale.current.formatAccessibleDate(cell.date)
    val focusRegistry = LocalCalendarFocus.current
    val requester = remember { FocusRequester() }
    DisposableEffect(focusRegistry, cell.date, cell.inCurrentMonth) {
        val entry = cell.inCurrentMonth to requester
        focusRegistry?.items?.getOrPut(cell.date) { mutableListOf() }?.add(entry)
        onDispose {
            focusRegistry?.items?.get(cell.date)?.let {
                it.remove(entry)
                if (it.isEmpty()) focusRegistry.items.remove(cell.date)
            }
        }
    }
    val range = LocalCalendar.current
    val nova = currentTheme.activePreset == StylePreset.Nova
    val start = range?.rangeFrom == cell.date && range.rangeTo != null
    val end = range?.rangeTo == cell.date && range.rangeFrom != null
    val middle = nova && isRangeMiddle
    val radius = twRadius.md
    val bridge =
        if (nova && (middle || start || end) && range?.rangeFrom != range?.rangeTo) {
            Modifier.background(
                currentTheme.colors.muted,
                RoundedCornerShape(
                    topStart = if (start) radius else 0.dp,
                    bottomStart = if (start) radius else 0.dp,
                    topEnd = if (end) radius else 0.dp,
                    bottomEnd = if (end) radius else 0.dp,
                ),
            )
        } else {
            Modifier
        }
    val theme = currentTheme
    val interactionSource = remember { MutableInteractionSource() }
    val styleState = rememberUpdatedStyleState(interactionSource) { it.isEnabled = !isDisabled && !outsideHidden }
    val isHovered by interactionSource.collectIsHoveredAsState()
    val pressStyle = rememberCalendarPressMotionStyle(interactionSource)
    val touchTargetStyle = if (currentTheme.activePreset == StylePreset.Nova) Style else rememberMinTouchTargetStyle()
    val cellStyle =
        Style {
            shape(RoundedCornerShape(twRadius.md))
            if (!outsideHidden) {
                if (isSelected) {
                    background(theme.colors.primary)
                } else if (isRangeMiddle) {
                    background(theme.colors.muted)
                } else if (isToday) {
                    if (theme.activePreset == StylePreset.Nova) {
                        background(theme.colors.muted)
                    } else {
                        borderWidth(1.dp)
                        borderColor(theme.colors.border)
                    }
                }
                if (hasFocus && !isDisabled) dropShadow(theme.focusRingShadow())
                hovered {
                    background(
                        if (nova && isSelected && !theme.isDark) {
                            theme.colors.primary
                        } else {
                            theme.colors.muted.copy(alpha = if (theme.isDark) 0.5f else 1f)
                        },
                    )
                }
                if (isDisabled) {
                    alpha(DISABLED_OPACITY)
                }
            }
        }
    Box(
        modifier =
            modifier.then(bridge).focusRequester(requester)
                .onFocusChanged { hasFocus = it.isFocused }
                .onPreviewKeyEvent { event -> range?.let { handleCalendarKey(it, event, rtl) } ?: false }
                .hoverable(interactionSource, enabled = !isDisabled && !outsideHidden)
                .focusProperties {
                    canFocus =
                        !isDisabled &&
                        !outsideHidden &&
                        (
                            range?.focusedDate == null ||
                                isFocused
                        )
                }
                .semantics {
                    role = Role.Button
                    contentDescription = accessibleDate
                    selected = isSelected
                    if (isDisabled) disabled()
                }
                .clickable(
                    interactionSource = interactionSource,
                    indication = null,
                    enabled = !isDisabled && !outsideHidden,
                    role = Role.Button,
                    onClick = onClick,
                )
                .styleable(styleState, touchTargetStyle then pressStyle),
        contentAlignment = Alignment.Center,
    ) {
        Box(
            modifier =
                Modifier
                    .size(CELL_SIZE)
                    .cssStyleable(
                        styleState,
                        rememberCalendarDayCellChromeStyle(CELL_SIZE) then cellStyle then style,
                    ),
            contentAlignment = Alignment.Center,
        ) {
            Column(
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.spacedBy(4.dp),
            ) {
                if (content != null) content() else CalendarDayText(cell, isSelected, nova && theme.isDark && isHovered)
            }
        }
    }
}

@Composable
private fun CalendarDayText(
    cell: CalendarCell,
    selected: Boolean,
    darkNovaHover: Boolean,
) {
    val color =
        when {
            darkNovaHover -> currentTheme.colors.foreground
            selected -> currentTheme.colors.primaryForeground
            !cell.inCurrentMonth ||
                LocalCalendar.current?.isDisabled(cell.date) == true ->
                currentTheme.colors.mutedForeground
            else -> currentTheme.colors.foreground
        }
    Text(textStyle = TextStyle(fontSize = 14.sp, lineHeight = 14.sp, fontWeight = FontWeight.Normal, color = color)) {
        cell.date.day.toString()
    }
}
