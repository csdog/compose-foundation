package com.toutiaoyan.compose.ui.components.calendar

import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFailsWith

class CalendarSystemTest {
    private val system = CalendarSystem.Persian

    @Test fun knownConversionsAndWeekdays() {
        assertEquals(CalendarDate(1404, 3, 22), system.fromGregorian(CalendarDate(2025, 6, 12)))
        assertEquals(CalendarDate(2024, 3, 20), system.toGregorian(CalendarDate(1403, 1, 1)))
        assertEquals(3, system.dayOfWeek(CalendarDate(1403, 1, 1)))
        assertEquals(CalendarDate(2025, 3, 20), system.toGregorian(CalendarDate(1403, 12, 30)))
    }

    @Test fun monthLengthsAndLeapBoundaries() {
        assertEquals(31, system.daysInMonth(1403, 6))
        assertEquals(30, system.daysInMonth(1403, 7))
        assertEquals(30, system.daysInMonth(1403, 12))
        assertEquals(29, system.daysInMonth(1404, 12))
        assertEquals(CalendarDate(1404, 1, 1), system.fromGregorian(CalendarDate(2025, 3, 21)))
    }

    @Test fun allDatesAcrossModernLeapCyclesRoundTrip() {
        for (year in 1370..1430) {
            for (month in 1..12) {
                for (day in 1..system.daysInMonth(year, month)) {
                    val date = CalendarDate(year, month, day)
                    assertEquals(date, system.fromGregorian(system.toGregorian(date)))
                }
            }
        }
    }

    @Test fun supportedBoundaryYearsRoundTripAndRejectOutsideDates() {
        for (year in listOf(1, 3176)) {
            for (month in 1..12) {
                for (day in 1..system.daysInMonth(year, month)) {
                    val date = CalendarDate(year, month, day)
                    assertEquals(date, system.fromGregorian(system.toGregorian(date)))
                }
            }
        }
        assertFailsWith<IllegalArgumentException> { system.fromGregorian(CalendarDate(622, 1, 1)) }
        assertFailsWith<IllegalArgumentException> { system.fromGregorian(CalendarDate(3799, 1, 1)) }
    }

    @Test fun boundaryGridsAndWeekNumbersRemainValid() {
        for ((year, month) in listOf(1 to 1, 3176 to 12)) {
            for (weekStart in 0..6) {
                val weeks = buildMonthGrid(year, month, weekStart, fixedWeeks = true, system = system)
                assertEquals(42, weeks.flatten().size)
                assertEquals(system.daysInMonth(year, month), weeks.flatten().count { it.inCurrentMonth })
                weeks.forEach { calendarWeekNumber(it.first().date, weekStart, system) }
            }
        }
    }

    @Test fun localeKeepsTrailingLambdaAndPositionalConstructorCalls() {
        val labels = CalendarLocale.English
        val trailing = CalendarLocale(labels.months, labels.shortMonths, labels.weekdays) { "accessible" }
        val positional =
            CalendarLocale(
                labels.months, labels.shortMonths, labels.weekdays, "Previous", "Next",
                { _, _ -> "caption" }, { "date" }, 0, "Pick", { "accessible" },
            )
        for (locale in listOf(trailing, positional)) {
            assertEquals(CalendarSystem.Gregorian, locale.calendarSystem)
            assertEquals("accessible", locale.formatAccessibleDate(CalendarDate(2026, 1, 1)))
        }
    }
}
