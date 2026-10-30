package com.toutiaoyan.compose.ui.catalog.docs

import com.toutiaoyan.compose.ui.components.calendar.CalendarDate
import kotlinx.datetime.number
import kotlinx.datetime.TimeZone
import kotlinx.datetime.toLocalDateTime
import kotlin.time.Clock
import kotlin.time.Instant

internal fun calendarDateAt(
    instant: Instant,
    zone: TimeZone,
): CalendarDate {
    val date = instant.toLocalDateTime(zone).date
    return CalendarDate(date.year, date.month.number, date.day)
}

internal fun currentCalendarDate(): CalendarDate = calendarDateAt(Clock.System.now(), TimeZone.currentSystemDefault())
