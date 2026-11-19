package com.toutiaoyan.compose.ui.components.calendar

internal const val PERSIAN_LAST_SUPPORTED_YEAR = 3176
private const val PERSIAN_TERMINAL_START_YEAR = PERSIAN_LAST_SUPPORTED_YEAR + 1
internal const val GREGORIAN_LEAP_INTERVAL_YEARS = 4
internal const val GREGORIAN_CENTURY_YEARS = 100
internal const val GREGORIAN_CYCLE_YEARS = 400

/** CalendarDate fields use this system inside Calendar; convert at application boundaries.
 * Persian uses Borkowski's break-year algorithm (Jalaali years 1..PERSIAN_LAST_SUPPORTED_YEAR).
 * It is the solar Jalali calendar, not the Islamic lunar calendar.
 */
enum class CalendarSystem {
    Gregorian,
    Persian,
    ;

    fun daysInMonth(
        year: Int,
        month: Int,
    ): Int {
        require(month in 1..MONTHS_IN_YEAR)
        return if (this == Gregorian) {
            when (month) {
                2 -> if (isGregorianLeapYear(year)) 29 else 28
                4, 6, 9, 11 -> 30
                else -> 31
            }
        } else {
            require(year in 1..PERSIAN_LAST_SUPPORTED_YEAR)
            when {
                month <= 6 -> 31
                month <= 11 -> 30
                else -> persianStart(year + 1) - persianStart(year) - 336
            }
        }
    }

    fun toGregorian(date: CalendarDate): CalendarDate {
        if (this == Gregorian) return date
        require(date.day in 1..daysInMonth(date.year, date.month))
        val ordinal =
            persianStart(date.year) + (1 until date.month).sumOf { daysInMonth(date.year, it) } + date.day - 1
        var year = date.year + 621
        if (ordinal >= gregorianOrdinal(CalendarDate(year + 1, 1, 1))) year++
        var day = ordinal - gregorianOrdinal(CalendarDate(year, 1, 1)) + 1
        var month = 1
        while (day > Gregorian.daysInMonth(year, month)) {
            day -= Gregorian.daysInMonth(year, month++)
        }
        return CalendarDate(year, month, day)
    }

    fun fromGregorian(date: CalendarDate): CalendarDate {
        if (this == Gregorian) return date
        val ordinal = gregorianOrdinal(date)
        require(ordinal >= persianStart(1) && ordinal < persianStart(PERSIAN_TERMINAL_START_YEAR))
        var year = (date.year - 621).coerceIn(1, PERSIAN_LAST_SUPPORTED_YEAR)
        if (ordinal < persianStart(year)) year--
        require(year in 1..PERSIAN_LAST_SUPPORTED_YEAR)
        var day = ordinal - persianStart(year) + 1
        var month = 1
        while (day > daysInMonth(year, month)) day -= daysInMonth(year, month++)
        return CalendarDate(year, month, day)
    }

    internal fun supports(date: CalendarDate): Boolean =
        this == Gregorian || date.year in 1..PERSIAN_LAST_SUPPORTED_YEAR

    internal fun precedingMonthLength(
        year: Int,
        month: Int,
    ): Int =
        if (this == Persian && year == 0 && month == MONTHS_IN_YEAR) {
            persianStart(1) - persianStart(0) - 336
        } else {
            daysInMonth(year, month)
        }

    internal fun clamp(date: CalendarDate): CalendarDate =
        if (this == Gregorian) {
            date
        } else {
            val lastDay = daysInMonth(PERSIAN_LAST_SUPPORTED_YEAR, MONTHS_IN_YEAR)
            val last = CalendarDate(PERSIAN_LAST_SUPPORTED_YEAR, MONTHS_IN_YEAR, lastDay)
            date.coerceIn(CalendarDate(1, 1, 1), last)
        }

    fun dayOfWeek(date: CalendarDate): Int = toGregorian(date).dayOfWeek()
}

private fun isGregorianLeapYear(year: Int): Boolean =
    year % GREGORIAN_LEAP_INTERVAL_YEARS == 0 &&
        (year % GREGORIAN_CENTURY_YEARS != 0 || year % GREGORIAN_CYCLE_YEARS == 0)

private fun gregorianOrdinal(date: CalendarDate): Int {
    val y = date.year - 1
    return 365 * y + y / GREGORIAN_LEAP_INTERVAL_YEARS - y / GREGORIAN_CENTURY_YEARS + y / GREGORIAN_CYCLE_YEARS +
        (1 until date.month).sumOf { CalendarSystem.Gregorian.daysInMonth(date.year, it) } + date.day
}

// Adapted from MIT-licensed jalaali-js jalCalCore; source/license archived in implementation evidence.
private fun persianStart(year: Int): Int {
    require(year in 0..PERSIAN_TERMINAL_START_YEAR)
    val breaks =
        intArrayOf(
            -61, 9, 38, 199, 426, 686, 756, 818, 1111, 1181,
            1210, 1635, 2060, 2097, 2192, 2262, 2324, 2394, 2456, 3178,
        )
    var leapJ = -14
    var previous = breaks[0]
    var jump = 0
    for (next in breaks.drop(1)) {
        jump = next - previous
        if (year < next) break
        leapJ += jump / 33 * 8 + jump % 33 / 4
        previous = next
    }
    val n = year - previous
    leapJ += n / 33 * 8 + (n % 33 + 3) / 4
    if (jump % 33 == 4 && jump - n == 4) leapJ++
    val gy = year + 621
    val leapG = gy / 4 - ((gy / 100 + 1) * 3) / 4 - 150
    return gregorianOrdinal(CalendarDate(gy, 3, 20 + leapJ - leapG))
}
