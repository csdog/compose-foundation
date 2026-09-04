@file:OptIn(ExperimentalFoundationStyleApi::class, ExperimentalTypeInference::class)

package com.toutiaoyan.compose.ui.components.calendar

import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.style.ExperimentalFoundationStyleApi
import androidx.compose.foundation.style.MutableStyleState
import androidx.compose.foundation.style.Style
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
import com.toutiaoyan.compose.ui.styles.rememberPressMotionStyle
import com.toutiaoyan.compose.ui.theme.currentTheme
import com.toutiaoyan.compose.ui.components.text.Text
import com.toutiaoyan.compose.ui.components.text.TextColors
import com.toutiaoyan.compose.ui.components.text.TextVariant
import kotlin.experimental.ExperimentalTypeInference
import kotlin.jvm.JvmName
import com.toutiaoyan.compose.tailwind.core.twSpacing
import com.toutiaoyan.compose.tailwind.core.twRadius

/** A plain Gregorian calendar date -- the library has no `kotlinx-datetime` dependency, so this stays a minimal value type. */
data class CalendarDate(val year: Int, val month: Int, val day: Int) : Comparable<CalendarDate> {
    override fun compareTo(other: CalendarDate): Int {
        if (year != other.year) return year - other.year
        if (month != other.month) return month - other.month
        return day - other.day
    }
}

/** Inclusive start/end for [Calendar] range mode. Matches react-day-picker / shadcn `DateRange`. */
data class CalendarDateRange(val from: CalendarDate?, val to: CalendarDate?)

private val MONTH_NAMES =
    listOf(
        "January", "February", "March", "April", "May", "June",
        "July", "August", "September", "October", "November", "December",
    )

/** react-day-picker `formatMonthDropdown`: `month: "short"`. */
private val SHORT_MONTH_NAMES =
    listOf("Jan", "Feb", "Mar", "Apr", "May", "Jun", "Jul", "Aug", "Sep", "Oct", "Nov", "Dec")
private val WEEKDAY_LABELS = listOf("Su", "Mo", "Tu", "We", "Th", "Fr", "Sa")

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
    val caption: String get() = captionFor(year, month)

    fun captionFor(
        year: Int,
        month: Int,
    ) = "${MONTH_NAMES[month - 1]} $year"

    fun isSelectedDay(date: CalendarDate) =
        date == selected || date == rangeFrom || date == rangeTo || date in selectedDates

    fun isDisabled(date: CalendarDate) = disabled(date)

    fun isRangeMiddle(date: CalendarDate): Boolean {
        val from = rangeFrom ?: return false
        val to = rangeTo ?: return false
        return date > from && date < to
    }

    fun goPrevious() {
        val delta = if (pagedNavigation) numberOfMonths else 1
        val (y, m) = addMonths(year, month, -delta)
        onMonthChange(y, m)
    }

    fun goNext() {
        val delta = if (pagedNavigation) numberOfMonths else 1
        val (y, m) = addMonths(year, month, delta)
        onMonthChange(y, m)
    }

    fun moveFocusTo(date: CalendarDate) {
        val inView =
            (0 until numberOfMonths).any { offset ->
                val (y, m) = addMonths(year, month, offset)
                date.year == y && date.month == m
            }
        if (!inView) {
            onMonthChange(date.year, date.month)
        }
        onFocusedDateChange(date)
    }

    fun moveFocusByDays(days: Int) {
        val start = focusedDate ?: today ?: CalendarDate(year, month, 1)
        var candidate = start.plusDays(days)
        val step = if (days >= 0) 1 else -1
        var hops = 0
        while (isDisabled(candidate) && hops < 400) {
            candidate = candidate.plusDays(step)
            hops++
        }
        if (!isDisabled(candidate)) moveFocusTo(candidate)
    }

    fun moveFocusToWeekEdge(end: Boolean) {
        val start = focusedDate ?: today ?: CalendarDate(year, month, 1)
        val index = (start.dayOfWeek() - weekStartsOn + 7) % 7
        val target =
            if (end) {
                start.plusDays(6 - index)
            } else {
                start.plusDays(-index)
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
        val date = CalendarDate(y, m, start.day.coerceAtMost(daysInMonth(y, m)))
        if (!isDisabled(date)) moveFocusTo(date)
    }

    fun selectFocused() {
        val date = focusedDate ?: return
        if (!isDisabled(date)) onSelectedChange(date)
    }
}

private val LocalCalendar = staticCompositionLocalOf<CalendarState?> { null }

/** Reads the nearest [Calendar]. Matches react-day-picker / shadcn calendar context. */
@Composable
fun calendarState(): CalendarState =
    checkNotNull(LocalCalendar.current) {
        "calendarState() must be used inside Calendar { … }"
    }

class CalendarScope internal constructor()

private fun isLeapYear(year: Int) = (year % 4 == 0 && year % 100 != 0) || (year % 400 == 0)

private fun daysInMonth(
    year: Int,
    month: Int,
): Int =
    when (month) {
        1, 3, 5, 7, 8, 10, 12 -> 31
        4, 6, 9, 11 -> 30
        2 -> if (isLeapYear(year)) 29 else 28
        else -> error("month must be 1..12, was $month")
    }

// Sakamoto's algorithm: 0 = Sunday .. 6 = Saturday.
private val SAKAMOTO_OFFSETS = intArrayOf(0, 3, 2, 5, 0, 3, 5, 1, 4, 6, 2, 4)

private fun sakamotoDayOfWeek(
    year: Int,
    month: Int,
    day: Int,
): Int {
    val y = if (month < 3) year - 1 else year
    return (y + y / 4 - y / 100 + y / 400 + SAKAMOTO_OFFSETS[month - 1] + day) % 7
}

/** 0 = Sunday .. 6 = Saturday, matching JS `Date#getDay()` / react-day-picker. */
fun CalendarDate.dayOfWeek(): Int = sakamotoDayOfWeek(year, month, day)

private fun previousMonth(
    year: Int,
    month: Int,
): Pair<Int, Int> = if (month == 1) year - 1 to 12 else year to month - 1

private fun nextMonth(
    year: Int,
    month: Int,
): Pair<Int, Int> = if (month == 12) year + 1 to 1 else year to month + 1

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

private fun CalendarDate.plusDays(days: Int): CalendarDate {
    var y = year
    var m = month
    var d = day + days
    while (true) {
        val dim = daysInMonth(y, m)
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
                d += daysInMonth(y, m)
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
    weekStartsOn: Int = 0,
    showOutsideDays: Boolean = true,
    fromDate: CalendarDate? = null,
    toDate: CalendarDate? = null,
    startMonth: CalendarDate? = null,
    endMonth: CalendarDate? = null,
    fromMonth: CalendarDate? = null,
    toMonth: CalendarDate? = null,
    fixedWeeks: Boolean = true,
    hideNavigation: Boolean = false,
    disableNavigation: Boolean = false,
    pagedNavigation: Boolean = false,
    reverseMonths: Boolean = false,
    captionLayout: CalendarCaptionLayout = CalendarCaptionLayout.Label,
    style: Style = Style,
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
        fromDate = startMonth ?: fromMonth ?: fromDate,
        toDate = endMonth ?: toMonth ?: toDate,
        fixedWeeks = fixedWeeks,
        hideNavigation = hideNavigation,
        disableNavigation = disableNavigation,
        pagedNavigation = pagedNavigation,
        reverseMonths = reverseMonths,
        captionLayout = captionLayout,
        style = style,
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
    weekStartsOn: Int = 0,
    showOutsideDays: Boolean = true,
    fromDate: CalendarDate? = null,
    toDate: CalendarDate? = null,
    startMonth: CalendarDate? = null,
    endMonth: CalendarDate? = null,
    fromMonth: CalendarDate? = null,
    toMonth: CalendarDate? = null,
    fixedWeeks: Boolean = true,
    hideNavigation: Boolean = false,
    disableNavigation: Boolean = false,
    pagedNavigation: Boolean = false,
    reverseMonths: Boolean = false,
    captionLayout: CalendarCaptionLayout = CalendarCaptionLayout.Label,
    style: Style = Style,
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
        fromDate = startMonth ?: fromMonth ?: fromDate,
        toDate = endMonth ?: toMonth ?: toDate,
        fixedWeeks = fixedWeeks,
        hideNavigation = hideNavigation,
        disableNavigation = disableNavigation,
        pagedNavigation = pagedNavigation,
        reverseMonths = reverseMonths,
        captionLayout = captionLayout,
        style = style,
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
    weekStartsOn: Int = 0,
    showOutsideDays: Boolean = true,
    fromDate: CalendarDate? = null,
    toDate: CalendarDate? = null,
    startMonth: CalendarDate? = null,
    endMonth: CalendarDate? = null,
    fromMonth: CalendarDate? = null,
    toMonth: CalendarDate? = null,
    fixedWeeks: Boolean = true,
    hideNavigation: Boolean = false,
    disableNavigation: Boolean = false,
    pagedNavigation: Boolean = false,
    reverseMonths: Boolean = false,
    captionLayout: CalendarCaptionLayout = CalendarCaptionLayout.Label,
    style: Style = Style,
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
        fromDate = startMonth ?: fromMonth ?: fromDate,
        toDate = endMonth ?: toMonth ?: toDate,
        fixedWeeks = fixedWeeks,
        hideNavigation = hideNavigation,
        disableNavigation = disableNavigation,
        pagedNavigation = pagedNavigation,
        reverseMonths = reverseMonths,
        captionLayout = captionLayout,
        style = style,
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
    content: @Composable CalendarScope.() -> Unit,
) {
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
    val rootStyleState = remember { MutableStyleState(MutableInteractionSource()) }
    val state =
        CalendarState(
            year = year,
            month = month,
            selected = selected,
            rangeFrom = rangeFrom,
            rangeTo = rangeTo,
            selectedDates = selectedDates,
            today = today,
            focusedDate = focusedDate,
            disabled = { date ->
                (fromDate != null && date < fromDate) ||
                    (toDate != null && date > toDate) ||
                    disabled(date)
            },
            onMonthChange = onMonthChange,
            onSelectedChange = onDayClick,
            onFocusedDateChange = { focusedDate = it },
            numberOfMonths = numberOfMonths.coerceAtLeast(1),
            weekStartsOn = weekStartsOn.coerceIn(0, 6),
            showOutsideDays = showOutsideDays,
            fixedWeeks = fixedWeeks,
            hideNavigation = hideNavigation,
            disableNavigation = disableNavigation,
            pagedNavigation = pagedNavigation,
            reverseMonths = reverseMonths,
            captionLayout = captionLayout,
            fromYear = fromDate?.year ?: (year - 10),
            toYear = toDate?.year ?: (year + 10),
        )

    CompositionLocalProvider(LocalCalendar provides state) {
        Column(
            modifier =
                modifier
                    .onPreviewKeyEvent { handleCalendarKey(state, it) }
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
): Boolean {
    if (event.type != KeyEventType.KeyDown) return false
    when (event.key) {
        Key.DirectionLeft -> state.moveFocusByDays(-1)
        Key.DirectionRight -> state.moveFocusByDays(1)
        Key.DirectionUp -> state.moveFocusByDays(-7)
        Key.DirectionDown -> state.moveFocusByDays(7)
        Key.MoveHome -> state.moveFocusToWeekEdge(end = false)
        Key.MoveEnd -> state.moveFocusToWeekEdge(end = true)
        Key.PageUp -> state.moveFocusByMonths(-1)
        Key.PageDown -> state.moveFocusByMonths(1)
        Key.Enter, Key.NumPadEnter, Key.Spacebar -> state.selectFocused()
        else -> return false
    }
    return true
}

private val CELL_SIZE = 36.dp

/** One cell in a month grid (`date` + whether it falls in the displayed month). */
data class CalendarCell(val date: CalendarDate, val inCurrentMonth: Boolean)

private fun buildMonthGrid(
    year: Int,
    month: Int,
    weekStartsOn: Int = 0,
    fixedWeeks: Boolean = true,
): List<List<CalendarCell>> {
    val firstWeekday = (sakamotoDayOfWeek(year, month, 1) - weekStartsOn + 7) % 7
    val daysThisMonth = daysInMonth(year, month)
    val (prevYear, prevMonth) = previousMonth(year, month)
    val daysPrevMonth = daysInMonth(prevYear, prevMonth)
    val (nextYear, nextMonth) = nextMonth(year, month)

    val cells = mutableListOf<CalendarCell>()
    for (i in firstWeekday downTo 1) {
        cells += CalendarCell(CalendarDate(prevYear, prevMonth, daysPrevMonth - i + 1), inCurrentMonth = false)
    }
    for (day in 1..daysThisMonth) {
        cells += CalendarCell(CalendarDate(year, month, day), inCurrentMonth = true)
    }
    var nextDay = 1
    while (cells.size % 7 != 0) {
        cells += CalendarCell(CalendarDate(nextYear, nextMonth, nextDay), inCurrentMonth = false)
        nextDay++
    }
    if (fixedWeeks) {
        while (cells.size < 42) {
            cells += CalendarCell(CalendarDate(nextYear, nextMonth, nextDay), inCurrentMonth = false)
            nextDay++
        }
    }
    return cells.chunked(7)
}

/** Default caption + weekday row + month grid. Multi-month uses [CalendarMonths]. */
@Composable
fun CalendarDefaultContent() {
    if (calendarState().numberOfMonths <= 1) {
        CalendarHeader()
        CalendarWeekdays()
        CalendarGrid()
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
    Row(horizontalArrangement = Arrangement.spacedBy(twSpacing.lg)) {
        offsets.forEachIndexed { visualIndex, offset ->
            val (y, m) = addMonths(state.year, state.month, offset)
            Column(verticalArrangement = Arrangement.spacedBy(twSpacing.sm)) {
                Row(
                    modifier = Modifier.size(CELL_SIZE * 7, CELL_SIZE),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically,
                ) {
                    if (visualIndex == 0 && !state.hideNavigation) {
                        CalendarPrevious()
                    } else {
                        Box(modifier = Modifier.size(CELL_SIZE))
                    }
                    CalendarTitle(state.captionFor(y, m))
                    if (visualIndex == count - 1 && !state.hideNavigation) {
                        CalendarNext()
                    } else {
                        Box(modifier = Modifier.size(CELL_SIZE))
                    }
                }
                CalendarWeekdays()
                CalendarGrid(year = y, month = m)
            }
        }
    }
}

@Composable
fun CalendarHeader() {
    if (calendarState().hideNavigation) {
        Box(
            modifier = Modifier.size(CELL_SIZE * 7, CELL_SIZE),
            contentAlignment = Alignment.Center,
        ) {
            CalendarCaption()
        }
        return
    }
    Row(
        modifier = Modifier.size(CELL_SIZE * 7, CELL_SIZE),
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
    val labels = WEEKDAY_LABELS.drop(weekStartsOn) + WEEKDAY_LABELS.take(weekStartsOn)
    Row(horizontalArrangement = Arrangement.SpaceBetween, modifier = Modifier.size(CELL_SIZE * 7, CELL_SIZE)) {
        labels.forEach { label ->
            Box(modifier = Modifier.size(CELL_SIZE), contentAlignment = Alignment.Center) {
                Text(variant = TextVariant.LabelSmall, textStyle = TextColors.Muted) { label }
            }
        }
    }
}

/** 6×7 day grid of the displayed month. Matches react-day-picker month grid. */
@Composable
fun CalendarGrid(
    year: Int? = null,
    month: Int? = null,
) {
    val state = calendarState()
    val y = year ?: state.year
    val m = month ?: state.month
    val weeks =
        remember(y, m, state.weekStartsOn, state.fixedWeeks) {
            buildMonthGrid(y, m, state.weekStartsOn, state.fixedWeeks)
        }
    weeks.forEach { week ->
        Row {
            week.forEach { cell ->
                CalendarDayButton(
                    cell = cell,
                    isSelected = state.isSelectedDay(cell.date),
                    isRangeMiddle = state.isRangeMiddle(cell.date),
                    isToday = state.today != null && cell.date == state.today,
                    isFocused = cell.date == state.focusedDate,
                    isDisabled = state.isDisabled(cell.date),
                    outsideHidden = !state.showOutsideDays && !cell.inCurrentMonth,
                    onClick = {
                        state.onSelectedChange(cell.date)
                        state.onFocusedDateChange(cell.date)
                    },
                )
            }
        }
    }
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
private fun CalendarCaptionDropdowns(modifier: Modifier = Modifier) {
    val state = calendarState()
    Row(
        modifier = modifier,
        horizontalArrangement = Arrangement.spacedBy(twSpacing.xs),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        if (state.captionLayout == CalendarCaptionLayout.DropdownYears) {
            CalendarTitle(MONTH_NAMES[state.month - 1])
        } else {
            CalendarCaptionSelect(
                label = SHORT_MONTH_NAMES[state.month - 1],
                options = SHORT_MONTH_NAMES,
                onSelect = { state.onMonthChange(state.year, it + 1) },
            )
        }
        if (state.captionLayout == CalendarCaptionLayout.DropdownMonths) {
            CalendarTitle(state.year.toString())
        } else {
            val years = (state.fromYear.coerceAtMost(state.toYear)..state.toYear.coerceAtLeast(state.fromYear)).toList()
            CalendarCaptionSelect(
                label = state.year.toString(),
                options = years.map { it.toString() },
                onSelect = { state.onMonthChange(years[it], state.month) },
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
                    ChevronRight,
                    tint = currentTheme.colors.mutedForeground,
                    modifier = Modifier.rotate(90f),
                    variant = IconVariant.Small,
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
    val enabled = !calendarState().disableNavigation && !disabled
    val interactionSource = remember { MutableInteractionSource() }
    val styleState =
        rememberUpdatedStyleState(interactionSource) {
            it.isEnabled = enabled
        }
    val pressStyle = rememberPressMotionStyle()
    val touchTargetStyle = rememberMinTouchTargetStyle()
    Box(
        modifier =
            Modifier
                .semantics {
                    role = Role.Button
                    contentDescription = if (pointingLeft) "Previous month" else "Next month"
                    if (!enabled) disabled()
                }
                .clickable(
                    interactionSource = interactionSource,
                    indication = null,
                    enabled = enabled,
                    role = Role.Button,
                    onClick = onClick,
                )
                .then(if (enabled) Modifier else Modifier.alpha(0.5f))
                .styleable(styleState, touchTargetStyle then pressStyle),
        contentAlignment = Alignment.Center,
    ) {
        Box(
            modifier = Modifier.styleable(styleState, rememberCalendarNavButtonStyle()),
            contentAlignment = Alignment.Center,
        ) {
            Icon(
                ChevronRight,
                tint = currentTheme.colors.foreground,
                modifier = if (pointingLeft) Modifier.rotate(180f) else Modifier,
                variant = IconVariant.Small,
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
) {
    val theme = currentTheme
    val interactionSource = remember { MutableInteractionSource() }
    val styleState = remember { MutableStyleState(interactionSource) }
    val pressStyle = rememberPressMotionStyle()
    val touchTargetStyle = rememberMinTouchTargetStyle()
    val cellStyle =
        Style {
            shape(RoundedCornerShape(twRadius.md))
            if (!outsideHidden) {
                if (isSelected) {
                    background(theme.colors.primary)
                } else if (isRangeMiddle) {
                    background(theme.colors.accent)
                } else if (isToday) {
                    borderWidth(1.dp)
                    borderColor(theme.colors.border)
                }
                if (isFocused && !isDisabled) {
                    borderWidth(1.dp)
                    borderColor(theme.colors.ring)
                    dropShadow(theme.focusRingShadow())
                }
                if (isDisabled) {
                    alpha(0.5f)
                }
            }
        }
    Box(
        modifier =
            Modifier
                .semantics {
                    role = Role.Button
                    contentDescription = "${cell.date.year}-${cell.date.month}-${cell.date.day}"
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
                    .styleable(
                        styleState,
                        rememberCalendarDayCellChromeStyle() then cellStyle,
                    ),
            contentAlignment = Alignment.Center,
        ) {
            Text(
                variant = TextVariant.BodySmall,
                textStyle =
                    when {
                        isSelected -> TextStyle(color = currentTheme.colors.primaryForeground)
                        !cell.inCurrentMonth -> TextColors.Muted
                        else -> TextStyle.Default
                    },
            ) { if (outsideHidden) "" else cell.date.day.toString() }
        }
    }
}
