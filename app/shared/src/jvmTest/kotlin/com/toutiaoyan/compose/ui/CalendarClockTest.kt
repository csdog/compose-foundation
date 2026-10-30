package com.toutiaoyan.compose.ui

import com.toutiaoyan.compose.ui.catalog.docs.calendarDateAt
import com.toutiaoyan.compose.ui.components.calendar.CalendarDate
import kotlinx.datetime.TimeZone
import kotlin.time.Instant
import kotlin.test.*

class CalendarClockTest {
    @Test fun dateFollowsSelectedZoneAcrossMidnight() {
        val instant = Instant.parse("2026-10-29T00:30:00Z")
        assertEquals(CalendarDate(2026, 10, 28), calendarDateAt(instant, TimeZone.of("America/Los_Angeles")))
        assertEquals(CalendarDate(2026, 10, 29), calendarDateAt(instant, TimeZone.of("Asia/Shanghai")))
    }

    @Test fun dateHandlesNextYearInEastAsia() {
        assertEquals(
            CalendarDate(2027, 1, 1),
            calendarDateAt(Instant.parse("2026-12-31T20:00:00Z"), TimeZone.of("Asia/Shanghai")),
        )
    }
}
