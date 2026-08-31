@file:OptIn(androidx.compose.foundation.style.ExperimentalFoundationStyleApi::class)

package com.toutiaoyan.compose.ui.catalog.docs

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import com.toutiaoyan.compose.ui.components.calendar.Calendar
import com.toutiaoyan.compose.ui.components.calendar.CalendarDate
import com.toutiaoyan.compose.ui.components.calendar.CalendarDateRange
import com.toutiaoyan.compose.ui.components.calendar.CalendarDefaultContent
import com.toutiaoyan.compose.ui.components.calendar.CalendarGrid
import com.toutiaoyan.compose.ui.components.calendar.CalendarHeader
import com.toutiaoyan.compose.ui.components.calendar.CalendarMonths
import com.toutiaoyan.compose.ui.components.calendar.CalendarTitle
import com.toutiaoyan.compose.ui.components.calendar.CalendarCaption
import com.toutiaoyan.compose.ui.components.calendar.CalendarCaptionLayout
import com.toutiaoyan.compose.ui.components.calendar.CalendarWeekdays
import com.toutiaoyan.compose.ui.components.calendar.dayOfWeek

val calendarDoc =
    ComponentDoc(
        id = "calendar",
        title = "Calendar",
        description = "A single-month date picker grid with a highlighted selection and today marker.",
        usageCode =
            """
            import com.toutiaoyan.compose.ui.components.calendar.Calendar

            var year by remember { mutableStateOf(2026) }
            var month by remember { mutableStateOf(3) }
            var selected by remember { mutableStateOf<CalendarDate?>(null) }
            Calendar(
                year = year, month = month,
                onMonthChange = { y, m -> year = y; month = m },
                selected = selected, onSelectedChange = { selected = it },
                today = CalendarDate(2026, 3, 15),
            )
            """.trimIndent(),
        examples =
            listOf(
                ComponentExample(
                    title = "Default",
                    code =
                        """
                        var year by remember { mutableStateOf(2026) }
                        var month by remember { mutableStateOf(3) }
                        var selected by remember { mutableStateOf<CalendarDate?>(CalendarDate(2026, 3, 10)) }
                        Calendar(
                            year = year, month = month,
                            onMonthChange = { y, m -> year = y; month = m },
                            selected = selected, onSelectedChange = { selected = it },
                            today = CalendarDate(2026, 3, 15),
                        )
                        """.trimIndent(),
                    preview = {
                        var year by remember { mutableStateOf(2026) }
                        var month by remember { mutableStateOf(3) }
                        var selected by
                            remember { mutableStateOf<CalendarDate?>(CalendarDate(2026, 3, 10)) }
                        Calendar(
                            year = year,
                            month = month,
                            onMonthChange = { y, m ->
                                year = y
                                month = m
                            },
                            selected = selected,
                            onSelectedChange = { selected = it },
                            today = CalendarDate(2026, 3, 15),
                        )
                    },
                ),
                ComponentExample(
                    title = "Dropdown caption",
                    code =
                        """
                        Calendar(
                            captionLayout = CalendarCaptionLayout.Dropdown,
                            year = year, month = month,
                            onMonthChange = { y, m -> year = y; month = m },
                            selected = selected, onSelectedChange = { selected = it },
                        )
                        """.trimIndent(),
                    preview = {
                        var year by remember { mutableStateOf(2026) }
                        var month by remember { mutableStateOf(3) }
                        var selected by remember { mutableStateOf<CalendarDate?>(CalendarDate(2026, 3, 10)) }
                        Calendar(
                            year = year,
                            month = month,
                            onMonthChange = { y, m ->
                                year = y
                                month = m
                            },
                            selected = selected,
                            onSelectedChange = { selected = it },
                            today = CalendarDate(2026, 3, 15),
                            captionLayout = CalendarCaptionLayout.Dropdown,
                        )
                    },
                ),
                ComponentExample(
                    title = "Range",
                    code =
                        """
                        var year by remember { mutableStateOf(2026) }
                        var month by remember { mutableStateOf(3) }
                        var range by remember {
                            mutableStateOf(
                                CalendarDateRange(
                                    from = CalendarDate(2026, 3, 8),
                                    to = CalendarDate(2026, 3, 14),
                                )
                            )
                        }
                        Calendar(
                            year = year, month = month,
                            onMonthChange = { y, m -> year = y; month = m },
                            selectedRange = range, onSelectedRangeChange = { range = it },
                            today = CalendarDate(2026, 3, 15),
                        )
                        """.trimIndent(),
                    preview = {
                        var year by remember { mutableStateOf(2026) }
                        var month by remember { mutableStateOf(3) }
                        var range by remember {
                            mutableStateOf(
                                CalendarDateRange(
                                    from = CalendarDate(2026, 3, 8),
                                    to = CalendarDate(2026, 3, 14),
                                ),
                            )
                        }
                        Calendar(
                            year = year,
                            month = month,
                            onMonthChange = { y, m ->
                                year = y
                                month = m
                            },
                            selectedRange = range,
                            onSelectedRangeChange = { range = it },
                            today = CalendarDate(2026, 3, 15),
                        )
                    },
                ),
                ComponentExample(
                    title = "Multiple",
                    code =
                        """
                        var year by remember { mutableStateOf(2026) }
                        var month by remember { mutableStateOf(3) }
                        var dates by remember {
                            mutableStateOf(setOf(CalendarDate(2026, 3, 8), CalendarDate(2026, 3, 14)))
                        }
                        Calendar(
                            year = year, month = month,
                            onMonthChange = { y, m -> year = y; month = m },
                            selectedDates = dates, onSelectedDatesChange = { dates = it },
                            today = CalendarDate(2026, 3, 15),
                        )
                        """.trimIndent(),
                    preview = {
                        var year by remember { mutableStateOf(2026) }
                        var month by remember { mutableStateOf(3) }
                        var dates by remember {
                            mutableStateOf(setOf(CalendarDate(2026, 3, 8), CalendarDate(2026, 3, 14)))
                        }
                        Calendar(
                            year = year,
                            month = month,
                            onMonthChange = { y, m ->
                                year = y
                                month = m
                            },
                            selectedDates = dates,
                            onSelectedDatesChange = { dates = it },
                            today = CalendarDate(2026, 3, 15),
                        )
                    },
                ),
                ComponentExample(
                    title = "Disabled",
                    code =
                        """
                        var year by remember { mutableStateOf(2026) }
                        var month by remember { mutableStateOf(3) }
                        var selected by remember { mutableStateOf<CalendarDate?>(CalendarDate(2026, 3, 10)) }
                        Calendar(
                            year = year, month = month,
                            onMonthChange = { y, m -> year = y; month = m },
                            selected = selected, onSelectedChange = { selected = it },
                            today = CalendarDate(2026, 3, 15),
                            disabled = { it.dayOfWeek() == 0 || it.dayOfWeek() == 6 },
                        )
                        """.trimIndent(),
                    preview = {
                        var year by remember { mutableStateOf(2026) }
                        var month by remember { mutableStateOf(3) }
                        var selected by
                            remember { mutableStateOf<CalendarDate?>(CalendarDate(2026, 3, 10)) }
                        Calendar(
                            year = year,
                            month = month,
                            onMonthChange = { y, m ->
                                year = y
                                month = m
                            },
                            selected = selected,
                            onSelectedChange = { selected = it },
                            today = CalendarDate(2026, 3, 15),
                            disabled = { it.dayOfWeek() == 0 || it.dayOfWeek() == 6 },
                        )
                    },
                ),
                ComponentExample(
                    title = "Two months",
                    code =
                        """
                        var year by remember { mutableStateOf(2026) }
                        var month by remember { mutableStateOf(3) }
                        var selected by remember { mutableStateOf<CalendarDate?>(CalendarDate(2026, 3, 10)) }
                        Calendar(
                            year = year, month = month,
                            onMonthChange = { y, m -> year = y; month = m },
                            selected = selected, onSelectedChange = { selected = it },
                            today = CalendarDate(2026, 3, 15),
                            numberOfMonths = 2,
                        )
                        """.trimIndent(),
                    preview = {
                        var year by remember { mutableStateOf(2026) }
                        var month by remember { mutableStateOf(3) }
                        var selected by
                            remember { mutableStateOf<CalendarDate?>(CalendarDate(2026, 3, 10)) }
                        Calendar(
                            year = year,
                            month = month,
                            onMonthChange = { y, m ->
                                year = y
                                month = m
                            },
                            selected = selected,
                            onSelectedChange = { selected = it },
                            today = CalendarDate(2026, 3, 15),
                            numberOfMonths = 2,
                        )
                    },
                ),
                ComponentExample(
                    title = "Default months",
                    code =
                        """
                        var year by remember { mutableStateOf(2026) }
                        var month by remember { mutableStateOf(3) }
                        var selected by remember { mutableStateOf<CalendarDate?>(CalendarDate(2026, 3, 10)) }
                        Calendar(
                            year = year, month = month,
                            onMonthChange = { y, m -> year = y; month = m },
                            selected = selected, onSelectedChange = { selected = it },
                            today = CalendarDate(2026, 3, 15),
                            numberOfMonths = 1,
                        )
                        """.trimIndent(),
                    preview = {
                        var year by remember { mutableStateOf(2026) }
                        var month by remember { mutableStateOf(3) }
                        var selected by
                            remember { mutableStateOf<CalendarDate?>(CalendarDate(2026, 3, 10)) }
                        Calendar(
                            year = year,
                            month = month,
                            onMonthChange = { y, m ->
                                year = y
                                month = m
                            },
                            selected = selected,
                            onSelectedChange = { selected = it },
                            today = CalendarDate(2026, 3, 15),
                            numberOfMonths = 1,
                        )
                    },
                ),
                ComponentExample(
                    title = "Week starts on Monday",
                    code =
                        """
                        var year by remember { mutableStateOf(2026) }
                        var month by remember { mutableStateOf(3) }
                        var selected by remember { mutableStateOf<CalendarDate?>(CalendarDate(2026, 3, 10)) }
                        Calendar(
                            year = year, month = month,
                            onMonthChange = { y, m -> year = y; month = m },
                            selected = selected, onSelectedChange = { selected = it },
                            today = CalendarDate(2026, 3, 15),
                            weekStartsOn = 1,
                        )
                        """.trimIndent(),
                    preview = {
                        var year by remember { mutableStateOf(2026) }
                        var month by remember { mutableStateOf(3) }
                        var selected by
                            remember { mutableStateOf<CalendarDate?>(CalendarDate(2026, 3, 10)) }
                        Calendar(
                            year = year,
                            month = month,
                            onMonthChange = { y, m ->
                                year = y
                                month = m
                            },
                            selected = selected,
                            onSelectedChange = { selected = it },
                            today = CalendarDate(2026, 3, 15),
                            weekStartsOn = 1,
                        )
                    },
                ),
                ComponentExample(
                    title = "Default week start",
                    code =
                        """
                        var year by remember { mutableStateOf(2026) }
                        var month by remember { mutableStateOf(3) }
                        var selected by remember { mutableStateOf<CalendarDate?>(CalendarDate(2026, 3, 10)) }
                        Calendar(
                            year = year, month = month,
                            onMonthChange = { y, m -> year = y; month = m },
                            selected = selected, onSelectedChange = { selected = it },
                            today = CalendarDate(2026, 3, 15),
                            weekStartsOn = 0,
                        )
                        """.trimIndent(),
                    preview = {
                        var year by remember { mutableStateOf(2026) }
                        var month by remember { mutableStateOf(3) }
                        var selected by
                            remember { mutableStateOf<CalendarDate?>(CalendarDate(2026, 3, 10)) }
                        Calendar(
                            year = year,
                            month = month,
                            onMonthChange = { y, m ->
                                year = y
                                month = m
                            },
                            selected = selected,
                            onSelectedChange = { selected = it },
                            today = CalendarDate(2026, 3, 15),
                            weekStartsOn = 0,
                        )
                    },
                ),
                ComponentExample(
                    title = "Hide outside days",
                    code =
                        """
                        var year by remember { mutableStateOf(2026) }
                        var month by remember { mutableStateOf(3) }
                        var selected by remember { mutableStateOf<CalendarDate?>(CalendarDate(2026, 3, 10)) }
                        Calendar(
                            year = year, month = month,
                            onMonthChange = { y, m -> year = y; month = m },
                            selected = selected, onSelectedChange = { selected = it },
                            today = CalendarDate(2026, 3, 15),
                            showOutsideDays = false,
                        )
                        """.trimIndent(),
                    preview = {
                        var year by remember { mutableStateOf(2026) }
                        var month by remember { mutableStateOf(3) }
                        var selected by
                            remember { mutableStateOf<CalendarDate?>(CalendarDate(2026, 3, 10)) }
                        Calendar(
                            year = year,
                            month = month,
                            onMonthChange = { y, m ->
                                year = y
                                month = m
                            },
                            selected = selected,
                            onSelectedChange = { selected = it },
                            today = CalendarDate(2026, 3, 15),
                            showOutsideDays = false,
                        )
                    },
                ),
                ComponentExample(
                    title = "Outside days",
                    code =
                        """
                        var year by remember { mutableStateOf(2026) }
                        var month by remember { mutableStateOf(3) }
                        var selected by remember { mutableStateOf<CalendarDate?>(CalendarDate(2026, 3, 10)) }
                        Calendar(
                            year = year, month = month,
                            onMonthChange = { y, m -> year = y; month = m },
                            selected = selected, onSelectedChange = { selected = it },
                            today = CalendarDate(2026, 3, 15),
                            showOutsideDays = true,
                        )
                        """.trimIndent(),
                    preview = {
                        var year by remember { mutableStateOf(2026) }
                        var month by remember { mutableStateOf(3) }
                        var selected by
                            remember { mutableStateOf<CalendarDate?>(CalendarDate(2026, 3, 10)) }
                        Calendar(
                            year = year,
                            month = month,
                            onMonthChange = { y, m ->
                                year = y
                                month = m
                            },
                            selected = selected,
                            onSelectedChange = { selected = it },
                            today = CalendarDate(2026, 3, 15),
                            showOutsideDays = true,
                        )
                    },
                ),
                ComponentExample(
                    title = "Default outside days",
                    code =
                        """
                        var year by remember { mutableStateOf(2026) }
                        var month by remember { mutableStateOf(3) }
                        var selected by remember { mutableStateOf<CalendarDate?>(CalendarDate(2026, 3, 10)) }
                        Calendar(
                            year = year, month = month,
                            onMonthChange = { y, m -> year = y; month = m },
                            selected = selected, onSelectedChange = { selected = it },
                            today = CalendarDate(2026, 3, 15),
                            showOutsideDays = true,
                        )
                        """.trimIndent(),
                    preview = {
                        var year by remember { mutableStateOf(2026) }
                        var month by remember { mutableStateOf(3) }
                        var selected by
                            remember { mutableStateOf<CalendarDate?>(CalendarDate(2026, 3, 10)) }
                        Calendar(
                            year = year,
                            month = month,
                            onMonthChange = { y, m ->
                                year = y
                                month = m
                            },
                            selected = selected,
                            onSelectedChange = { selected = it },
                            today = CalendarDate(2026, 3, 15),
                            showOutsideDays = true,
                        )
                    },
                ),
                ComponentExample(
                    title = "Min and max date",
                    code =
                        """
                        var year by remember { mutableStateOf(2026) }
                        var month by remember { mutableStateOf(3) }
                        var selected by remember { mutableStateOf<CalendarDate?>(CalendarDate(2026, 3, 10)) }
                        Calendar(
                            year = year, month = month,
                            onMonthChange = { y, m -> year = y; month = m },
                            selected = selected, onSelectedChange = { selected = it },
                            today = CalendarDate(2026, 3, 15),
                            startMonth = CalendarDate(2026, 3, 5),
                            endMonth = CalendarDate(2026, 3, 20),
                        )
                        """.trimIndent(),
                    preview = {
                        var year by remember { mutableStateOf(2026) }
                        var month by remember { mutableStateOf(3) }
                        var selected by
                            remember { mutableStateOf<CalendarDate?>(CalendarDate(2026, 3, 10)) }
                        Calendar(
                            year = year,
                            month = month,
                            onMonthChange = { y, m ->
                                year = y
                                month = m
                            },
                            selected = selected,
                            onSelectedChange = { selected = it },
                            today = CalendarDate(2026, 3, 15),
                            startMonth = CalendarDate(2026, 3, 5),
                            endMonth = CalendarDate(2026, 3, 20),
                        )
                    },
                ),
                ComponentExample(
                    title = "From month",
                    code =
                        """
                        var year by remember { mutableStateOf(2026) }
                        var month by remember { mutableStateOf(3) }
                        var selected by remember { mutableStateOf<CalendarDate?>(CalendarDate(2026, 3, 10)) }
                        Calendar(
                            year = year, month = month,
                            onMonthChange = { y, m -> year = y; month = m },
                            selected = selected, onSelectedChange = { selected = it },
                            today = CalendarDate(2026, 3, 15),
                            fromMonth = CalendarDate(2026, 3, 5),
                            endMonth = CalendarDate(2026, 3, 20),
                        )
                        """.trimIndent(),
                    preview = {
                        var year by remember { mutableStateOf(2026) }
                        var month by remember { mutableStateOf(3) }
                        var selected by
                            remember { mutableStateOf<CalendarDate?>(CalendarDate(2026, 3, 10)) }
                        Calendar(
                            year = year,
                            month = month,
                            onMonthChange = { y, m ->
                                year = y
                                month = m
                            },
                            selected = selected,
                            onSelectedChange = { selected = it },
                            today = CalendarDate(2026, 3, 15),
                            fromMonth = CalendarDate(2026, 3, 5),
                            endMonth = CalendarDate(2026, 3, 20),
                        )
                    },
                ),
                ComponentExample(
                    title = "To month",
                    code =
                        """
                        var year by remember { mutableStateOf(2026) }
                        var month by remember { mutableStateOf(3) }
                        var selected by remember { mutableStateOf<CalendarDate?>(CalendarDate(2026, 3, 10)) }
                        Calendar(
                            year = year, month = month,
                            onMonthChange = { y, m -> year = y; month = m },
                            selected = selected, onSelectedChange = { selected = it },
                            today = CalendarDate(2026, 3, 15),
                            fromMonth = CalendarDate(2026, 3, 5),
                            toMonth = CalendarDate(2026, 3, 20),
                        )
                        """.trimIndent(),
                    preview = {
                        var year by remember { mutableStateOf(2026) }
                        var month by remember { mutableStateOf(3) }
                        var selected by
                            remember { mutableStateOf<CalendarDate?>(CalendarDate(2026, 3, 10)) }
                        Calendar(
                            year = year,
                            month = month,
                            onMonthChange = { y, m ->
                                year = y
                                month = m
                            },
                            selected = selected,
                            onSelectedChange = { selected = it },
                            today = CalendarDate(2026, 3, 15),
                            fromMonth = CalendarDate(2026, 3, 5),
                            toMonth = CalendarDate(2026, 3, 20),
                        )
                    },
                ),
                ComponentExample(
                    title = "From date",
                    code =
                        """
                        var year by remember { mutableStateOf(2026) }
                        var month by remember { mutableStateOf(3) }
                        var selected by remember { mutableStateOf<CalendarDate?>(CalendarDate(2026, 3, 10)) }
                        Calendar(
                            year = year, month = month,
                            onMonthChange = { y, m -> year = y; month = m },
                            selected = selected, onSelectedChange = { selected = it },
                            today = CalendarDate(2026, 3, 15),
                            fromDate = CalendarDate(2026, 3, 5),
                            toDate = CalendarDate(2026, 3, 20),
                        )
                        """.trimIndent(),
                    preview = {
                        var year by remember { mutableStateOf(2026) }
                        var month by remember { mutableStateOf(3) }
                        var selected by
                            remember { mutableStateOf<CalendarDate?>(CalendarDate(2026, 3, 10)) }
                        Calendar(
                            year = year,
                            month = month,
                            onMonthChange = { y, m ->
                                year = y
                                month = m
                            },
                            selected = selected,
                            onSelectedChange = { selected = it },
                            today = CalendarDate(2026, 3, 15),
                            fromDate = CalendarDate(2026, 3, 5),
                            toDate = CalendarDate(2026, 3, 20),
                        )
                    },
                ),
                ComponentExample(
                    title = "To date",
                    code =
                        """
                        var year by remember { mutableStateOf(2026) }
                        var month by remember { mutableStateOf(3) }
                        var selected by remember { mutableStateOf<CalendarDate?>(CalendarDate(2026, 3, 10)) }
                        Calendar(
                            year = year, month = month,
                            onMonthChange = { y, m -> year = y; month = m },
                            selected = selected, onSelectedChange = { selected = it },
                            today = CalendarDate(2026, 3, 15),
                            fromDate = CalendarDate(2026, 3, 5),
                            toDate = CalendarDate(2026, 3, 20),
                        )
                        """.trimIndent(),
                    preview = {
                        var year by remember { mutableStateOf(2026) }
                        var month by remember { mutableStateOf(3) }
                        var selected by
                            remember { mutableStateOf<CalendarDate?>(CalendarDate(2026, 3, 10)) }
                        Calendar(
                            year = year,
                            month = month,
                            onMonthChange = { y, m ->
                                year = y
                                month = m
                            },
                            selected = selected,
                            onSelectedChange = { selected = it },
                            today = CalendarDate(2026, 3, 15),
                            fromDate = CalendarDate(2026, 3, 5),
                            toDate = CalendarDate(2026, 3, 20),
                        )
                    },
                ),
                ComponentExample(
                    title = "Start month",
                    code =
                        """
                        var year by remember { mutableStateOf(2026) }
                        var month by remember { mutableStateOf(3) }
                        var selected by remember { mutableStateOf<CalendarDate?>(CalendarDate(2026, 3, 10)) }
                        Calendar(
                            year = year, month = month,
                            onMonthChange = { y, m -> year = y; month = m },
                            selected = selected, onSelectedChange = { selected = it },
                            today = CalendarDate(2026, 3, 15),
                            startMonth = CalendarDate(2026, 3, 5),
                            endMonth = CalendarDate(2026, 3, 20),
                        )
                        """.trimIndent(),
                    preview = {
                        var year by remember { mutableStateOf(2026) }
                        var month by remember { mutableStateOf(3) }
                        var selected by
                            remember { mutableStateOf<CalendarDate?>(CalendarDate(2026, 3, 10)) }
                        Calendar(
                            year = year,
                            month = month,
                            onMonthChange = { y, m ->
                                year = y
                                month = m
                            },
                            selected = selected,
                            onSelectedChange = { selected = it },
                            today = CalendarDate(2026, 3, 15),
                            startMonth = CalendarDate(2026, 3, 5),
                            endMonth = CalendarDate(2026, 3, 20),
                        )
                    },
                ),
                ComponentExample(
                    title = "End month",
                    code =
                        """
                        var year by remember { mutableStateOf(2026) }
                        var month by remember { mutableStateOf(3) }
                        var selected by remember { mutableStateOf<CalendarDate?>(CalendarDate(2026, 3, 10)) }
                        Calendar(
                            year = year, month = month,
                            onMonthChange = { y, m -> year = y; month = m },
                            selected = selected, onSelectedChange = { selected = it },
                            today = CalendarDate(2026, 3, 15),
                            startMonth = CalendarDate(2026, 3, 5),
                            endMonth = CalendarDate(2026, 3, 20),
                        )
                        """.trimIndent(),
                    preview = {
                        var year by remember { mutableStateOf(2026) }
                        var month by remember { mutableStateOf(3) }
                        var selected by
                            remember { mutableStateOf<CalendarDate?>(CalendarDate(2026, 3, 10)) }
                        Calendar(
                            year = year,
                            month = month,
                            onMonthChange = { y, m ->
                                year = y
                                month = m
                            },
                            selected = selected,
                            onSelectedChange = { selected = it },
                            today = CalendarDate(2026, 3, 15),
                            startMonth = CalendarDate(2026, 3, 5),
                            endMonth = CalendarDate(2026, 3, 20),
                        )
                    },
                ),
                ComponentExample(
                    title = "Variable weeks",
                    code =
                        """
                        var year by remember { mutableStateOf(2026) }
                        var month by remember { mutableStateOf(3) }
                        var selected by remember { mutableStateOf<CalendarDate?>(CalendarDate(2026, 3, 10)) }
                        Calendar(
                            year = year, month = month,
                            onMonthChange = { y, m -> year = y; month = m },
                            selected = selected, onSelectedChange = { selected = it },
                            today = CalendarDate(2026, 3, 15),
                            fixedWeeks = false,
                        )
                        """.trimIndent(),
                    preview = {
                        var year by remember { mutableStateOf(2026) }
                        var month by remember { mutableStateOf(3) }
                        var selected by
                            remember { mutableStateOf<CalendarDate?>(CalendarDate(2026, 3, 10)) }
                        Calendar(
                            year = year,
                            month = month,
                            onMonthChange = { y, m ->
                                year = y
                                month = m
                            },
                            selected = selected,
                            onSelectedChange = { selected = it },
                            today = CalendarDate(2026, 3, 15),
                            fixedWeeks = false,
                        )
                    },
                ),
                ComponentExample(
                    title = "Default fixed weeks",
                    code =
                        """
                        var year by remember { mutableStateOf(2026) }
                        var month by remember { mutableStateOf(3) }
                        var selected by remember { mutableStateOf<CalendarDate?>(CalendarDate(2026, 3, 10)) }
                        Calendar(
                            year = year, month = month,
                            onMonthChange = { y, m -> year = y; month = m },
                            selected = selected, onSelectedChange = { selected = it },
                            today = CalendarDate(2026, 3, 15),
                            fixedWeeks = true,
                        )
                        """.trimIndent(),
                    preview = {
                        var year by remember { mutableStateOf(2026) }
                        var month by remember { mutableStateOf(3) }
                        var selected by
                            remember { mutableStateOf<CalendarDate?>(CalendarDate(2026, 3, 10)) }
                        Calendar(
                            year = year,
                            month = month,
                            onMonthChange = { y, m ->
                                year = y
                                month = m
                            },
                            selected = selected,
                            onSelectedChange = { selected = it },
                            today = CalendarDate(2026, 3, 15),
                            fixedWeeks = true,
                        )
                    },
                ),
                ComponentExample(
                    title = "Without navigation",
                    code =
                        """
                        var selected by remember { mutableStateOf<CalendarDate?>(CalendarDate(2026, 3, 10)) }
                        Calendar(
                            year = 2026, month = 3,
                            onMonthChange = { _, _ -> },
                            selected = selected, onSelectedChange = { selected = it },
                            today = CalendarDate(2026, 3, 15),
                            hideNavigation = true,
                        )
                        """.trimIndent(),
                    preview = {
                        var selected by
                            remember { mutableStateOf<CalendarDate?>(CalendarDate(2026, 3, 10)) }
                        Calendar(
                            year = 2026,
                            month = 3,
                            onMonthChange = { _, _ -> },
                            selected = selected,
                            onSelectedChange = { selected = it },
                            today = CalendarDate(2026, 3, 15),
                            hideNavigation = true,
                        )
                    },
                ),
                ComponentExample(
                    title = "Default hide navigation",
                    code =
                        """
                        var year by remember { mutableStateOf(2026) }
                        var month by remember { mutableStateOf(3) }
                        var selected by remember { mutableStateOf<CalendarDate?>(CalendarDate(2026, 3, 10)) }
                        Calendar(
                            year = year, month = month,
                            onMonthChange = { y, m -> year = y; month = m },
                            selected = selected, onSelectedChange = { selected = it },
                            today = CalendarDate(2026, 3, 15),
                            hideNavigation = false,
                        )
                        """.trimIndent(),
                    preview = {
                        var year by remember { mutableStateOf(2026) }
                        var month by remember { mutableStateOf(3) }
                        var selected by
                            remember { mutableStateOf<CalendarDate?>(CalendarDate(2026, 3, 10)) }
                        Calendar(
                            year = year,
                            month = month,
                            onMonthChange = { y, m ->
                                year = y
                                month = m
                            },
                            selected = selected,
                            onSelectedChange = { selected = it },
                            today = CalendarDate(2026, 3, 15),
                            hideNavigation = false,
                        )
                    },
                ),
                ComponentExample(
                    title = "Disabled navigation",
                    code =
                        """
                        var selected by remember { mutableStateOf<CalendarDate?>(CalendarDate(2026, 3, 10)) }
                        Calendar(
                            year = 2026, month = 3,
                            onMonthChange = { _, _ -> },
                            selected = selected, onSelectedChange = { selected = it },
                            today = CalendarDate(2026, 3, 15),
                            disableNavigation = true,
                        )
                        """.trimIndent(),
                    preview = {
                        var selected by
                            remember { mutableStateOf<CalendarDate?>(CalendarDate(2026, 3, 10)) }
                        Calendar(
                            year = 2026,
                            month = 3,
                            onMonthChange = { _, _ -> },
                            selected = selected,
                            onSelectedChange = { selected = it },
                            today = CalendarDate(2026, 3, 15),
                            disableNavigation = true,
                        )
                    },
                ),
                ComponentExample(
                    title = "Default disable navigation",
                    code =
                        """
                        var year by remember { mutableStateOf(2026) }
                        var month by remember { mutableStateOf(3) }
                        var selected by remember { mutableStateOf<CalendarDate?>(CalendarDate(2026, 3, 10)) }
                        Calendar(
                            year = year, month = month,
                            onMonthChange = { y, m ->
                                year = y
                                month = m
                            },
                            selected = selected, onSelectedChange = { selected = it },
                            today = CalendarDate(2026, 3, 15),
                            disableNavigation = false,
                        )
                        """.trimIndent(),
                    preview = {
                        var year by remember { mutableStateOf(2026) }
                        var month by remember { mutableStateOf(3) }
                        var selected by
                            remember { mutableStateOf<CalendarDate?>(CalendarDate(2026, 3, 10)) }
                        Calendar(
                            year = year,
                            month = month,
                            onMonthChange = { y, m ->
                                year = y
                                month = m
                            },
                            selected = selected,
                            onSelectedChange = { selected = it },
                            today = CalendarDate(2026, 3, 15),
                            disableNavigation = false,
                        )
                    },
                ),
                ComponentExample(
                    title = "Paged navigation",
                    code =
                        """
                        var month by remember { mutableStateOf(3) }
                        var selected by remember { mutableStateOf<CalendarDate?>(CalendarDate(2026, 3, 10)) }
                        Calendar(
                            year = 2026, month = month,
                            onMonthChange = { _, m -> month = m },
                            selected = selected, onSelectedChange = { selected = it },
                            today = CalendarDate(2026, 3, 15),
                            numberOfMonths = 2,
                            pagedNavigation = true,
                        )
                        """.trimIndent(),
                    preview = {
                        var month by remember { mutableStateOf(3) }
                        var selected by
                            remember { mutableStateOf<CalendarDate?>(CalendarDate(2026, 3, 10)) }
                        Calendar(
                            year = 2026,
                            month = month,
                            onMonthChange = { _, m -> month = m },
                            selected = selected,
                            onSelectedChange = { selected = it },
                            today = CalendarDate(2026, 3, 15),
                            numberOfMonths = 2,
                            pagedNavigation = true,
                        )
                    },
                ),
                ComponentExample(
                    title = "Default paged navigation",
                    code =
                        """
                        var year by remember { mutableStateOf(2026) }
                        var month by remember { mutableStateOf(3) }
                        var selected by remember { mutableStateOf<CalendarDate?>(CalendarDate(2026, 3, 10)) }
                        Calendar(
                            year = year, month = month,
                            onMonthChange = { y, m -> year = y; month = m },
                            selected = selected, onSelectedChange = { selected = it },
                            today = CalendarDate(2026, 3, 15),
                            pagedNavigation = false,
                        )
                        """.trimIndent(),
                    preview = {
                        var year by remember { mutableStateOf(2026) }
                        var month by remember { mutableStateOf(3) }
                        var selected by
                            remember { mutableStateOf<CalendarDate?>(CalendarDate(2026, 3, 10)) }
                        Calendar(
                            year = year,
                            month = month,
                            onMonthChange = { y, m ->
                                year = y
                                month = m
                            },
                            selected = selected,
                            onSelectedChange = { selected = it },
                            today = CalendarDate(2026, 3, 15),
                            pagedNavigation = false,
                        )
                    },
                ),
                ComponentExample(
                    title = "Default reverse months",
                    code =
                        """
                        var selected by remember { mutableStateOf<CalendarDate?>(CalendarDate(2026, 3, 10)) }
                        Calendar(
                            year = 2026, month = 3,
                            onMonthChange = { _, _ -> },
                            selected = selected, onSelectedChange = { selected = it },
                            today = CalendarDate(2026, 3, 15),
                            numberOfMonths = 2,
                            reverseMonths = false,
                        )
                        """.trimIndent(),
                    preview = {
                        var selected by
                            remember { mutableStateOf<CalendarDate?>(CalendarDate(2026, 3, 10)) }
                        Calendar(
                            year = 2026,
                            month = 3,
                            onMonthChange = { _, _ -> },
                            selected = selected,
                            onSelectedChange = { selected = it },
                            today = CalendarDate(2026, 3, 15),
                            numberOfMonths = 2,
                            reverseMonths = false,
                        )
                    },
                ),
                ComponentExample(
                    title = "Reversed months",
                    code =
                        """
                        var selected by remember { mutableStateOf<CalendarDate?>(CalendarDate(2026, 3, 10)) }
                        Calendar(
                            year = 2026, month = 3,
                            onMonthChange = { _, _ -> },
                            selected = selected, onSelectedChange = { selected = it },
                            today = CalendarDate(2026, 3, 15),
                            numberOfMonths = 2,
                            reverseMonths = true,
                        )
                        """.trimIndent(),
                    preview = {
                        var selected by
                            remember { mutableStateOf<CalendarDate?>(CalendarDate(2026, 3, 10)) }
                        Calendar(
                            year = 2026,
                            month = 3,
                            onMonthChange = { _, _ -> },
                            selected = selected,
                            onSelectedChange = { selected = it },
                            today = CalendarDate(2026, 3, 15),
                            numberOfMonths = 2,
                            reverseMonths = true,
                        )
                    },
                ),
                ComponentExample(
                    title = "Header",
                    code =
                        """
                        var year by remember { mutableStateOf(2026) }
                        var month by remember { mutableStateOf(3) }
                        var selected by remember { mutableStateOf<CalendarDate?>(CalendarDate(2026, 3, 10)) }
                        Calendar(
                            year = year, month = month,
                            onMonthChange = { y, m -> year = y; month = m },
                            selected = selected, onSelectedChange = { selected = it },
                            today = CalendarDate(2026, 3, 15),
                        ) {
                            CalendarHeader()
                            CalendarWeekdays()
                            CalendarGrid()
                        }
                        """.trimIndent(),
                    preview = {
                        var year by remember { mutableStateOf(2026) }
                        var month by remember { mutableStateOf(3) }
                        var selected by
                            remember { mutableStateOf<CalendarDate?>(CalendarDate(2026, 3, 10)) }
                        Calendar(
                            year = year,
                            month = month,
                            onMonthChange = { y, m ->
                                year = y
                                month = m
                            },
                            selected = selected,
                            onSelectedChange = { selected = it },
                            today = CalendarDate(2026, 3, 15),
                        ) {
                            CalendarHeader()
                            CalendarWeekdays()
                            CalendarGrid()
                        }
                    },
                ),
                ComponentExample(
                    title = "Weekdays",
                    code =
                        """
                        var year by remember { mutableStateOf(2026) }
                        var month by remember { mutableStateOf(3) }
                        var selected by remember { mutableStateOf<CalendarDate?>(CalendarDate(2026, 3, 10)) }
                        Calendar(
                            year = year, month = month,
                            onMonthChange = { y, m -> year = y; month = m },
                            selected = selected, onSelectedChange = { selected = it },
                            today = CalendarDate(2026, 3, 15),
                        ) {
                            CalendarHeader()
                            CalendarWeekdays()
                            CalendarGrid()
                        }
                        """.trimIndent(),
                    preview = {
                        var year by remember { mutableStateOf(2026) }
                        var month by remember { mutableStateOf(3) }
                        var selected by
                            remember { mutableStateOf<CalendarDate?>(CalendarDate(2026, 3, 10)) }
                        Calendar(
                            year = year,
                            month = month,
                            onMonthChange = { y, m ->
                                year = y
                                month = m
                            },
                            selected = selected,
                            onSelectedChange = { selected = it },
                            today = CalendarDate(2026, 3, 15),
                        ) {
                            CalendarHeader()
                            CalendarWeekdays()
                            CalendarGrid()
                        }
                    },
                ),
                ComponentExample(
                    title = "Grid",
                    code =
                        """
                        var year by remember { mutableStateOf(2026) }
                        var month by remember { mutableStateOf(3) }
                        var selected by remember { mutableStateOf<CalendarDate?>(CalendarDate(2026, 3, 10)) }
                        Calendar(
                            year = year, month = month,
                            onMonthChange = { y, m -> year = y; month = m },
                            selected = selected, onSelectedChange = { selected = it },
                            today = CalendarDate(2026, 3, 15),
                        ) {
                            CalendarHeader()
                            CalendarWeekdays()
                            CalendarGrid()
                        }
                        """.trimIndent(),
                    preview = {
                        var year by remember { mutableStateOf(2026) }
                        var month by remember { mutableStateOf(3) }
                        var selected by
                            remember { mutableStateOf<CalendarDate?>(CalendarDate(2026, 3, 10)) }
                        Calendar(
                            year = year,
                            month = month,
                            onMonthChange = { y, m ->
                                year = y
                                month = m
                            },
                            selected = selected,
                            onSelectedChange = { selected = it },
                            today = CalendarDate(2026, 3, 15),
                        ) {
                            CalendarHeader()
                            CalendarWeekdays()
                            CalendarGrid()
                        }
                    },
                ),
                ComponentExample(
                    title = "Title",
                    code =
                        """
                        var year by remember { mutableStateOf(2026) }
                        var month by remember { mutableStateOf(3) }
                        var selected by remember { mutableStateOf<CalendarDate?>(CalendarDate(2026, 3, 10)) }
                        Calendar(
                            year = year, month = month,
                            onMonthChange = { y, m -> year = y; month = m },
                            selected = selected, onSelectedChange = { selected = it },
                            today = CalendarDate(2026, 3, 15),
                        ) {
                            CalendarTitle()
                            CalendarWeekdays()
                            CalendarGrid()
                        }
                        """.trimIndent(),
                    preview = {
                        var year by remember { mutableStateOf(2026) }
                        var month by remember { mutableStateOf(3) }
                        var selected by
                            remember { mutableStateOf<CalendarDate?>(CalendarDate(2026, 3, 10)) }
                        Calendar(
                            year = year,
                            month = month,
                            onMonthChange = { y, m ->
                                year = y
                                month = m
                            },
                            selected = selected,
                            onSelectedChange = { selected = it },
                            today = CalendarDate(2026, 3, 15),
                        ) {
                            CalendarTitle()
                            CalendarWeekdays()
                            CalendarGrid()
                        }
                    },
                ),
                ComponentExample(
                    title = "Caption",
                    code =
                        """
                        var year by remember { mutableStateOf(2026) }
                        var month by remember { mutableStateOf(3) }
                        var selected by remember { mutableStateOf<CalendarDate?>(CalendarDate(2026, 3, 10)) }
                        Calendar(
                            year = year, month = month,
                            onMonthChange = { y, m -> year = y; month = m },
                            selected = selected, onSelectedChange = { selected = it },
                            today = CalendarDate(2026, 3, 15),
                        ) {
                            CalendarCaption()
                            CalendarWeekdays()
                            CalendarGrid()
                        }
                        """.trimIndent(),
                    preview = {
                        var year by remember { mutableStateOf(2026) }
                        var month by remember { mutableStateOf(3) }
                        var selected by
                            remember { mutableStateOf<CalendarDate?>(CalendarDate(2026, 3, 10)) }
                        Calendar(
                            year = year,
                            month = month,
                            onMonthChange = { y, m ->
                                year = y
                                month = m
                            },
                            selected = selected,
                            onSelectedChange = { selected = it },
                            today = CalendarDate(2026, 3, 15),
                        ) {
                            CalendarCaption()
                            CalendarWeekdays()
                            CalendarGrid()
                        }
                    },
                ),
                ComponentExample(
                    title = "Previous",
                    code =
                        """
                        var year by remember { mutableStateOf(2026) }
                        var month by remember { mutableStateOf(3) }
                        var selected by remember { mutableStateOf<CalendarDate?>(CalendarDate(2026, 3, 10)) }
                        Calendar(
                            year = year, month = month,
                            onMonthChange = { y, m -> year = y; month = m },
                            selected = selected, onSelectedChange = { selected = it },
                            today = CalendarDate(2026, 3, 15),
                        ) {
                            CalendarHeader()
                            CalendarWeekdays()
                            CalendarGrid()
                        }
                        """.trimIndent(),
                    preview = {
                        var year by remember { mutableStateOf(2026) }
                        var month by remember { mutableStateOf(3) }
                        var selected by
                            remember { mutableStateOf<CalendarDate?>(CalendarDate(2026, 3, 10)) }
                        Calendar(
                            year = year,
                            month = month,
                            onMonthChange = { y, m ->
                                year = y
                                month = m
                            },
                            selected = selected,
                            onSelectedChange = { selected = it },
                            today = CalendarDate(2026, 3, 15),
                        ) {
                            CalendarHeader()
                            CalendarWeekdays()
                            CalendarGrid()
                        }
                    },
                ),
                ComponentExample(
                    title = "Next",
                    code =
                        """
                        var year by remember { mutableStateOf(2026) }
                        var month by remember { mutableStateOf(3) }
                        var selected by remember { mutableStateOf<CalendarDate?>(CalendarDate(2026, 3, 10)) }
                        Calendar(
                            year = year, month = month,
                            onMonthChange = { y, m -> year = y; month = m },
                            selected = selected, onSelectedChange = { selected = it },
                            today = CalendarDate(2026, 3, 15),
                        ) {
                            CalendarHeader()
                            CalendarWeekdays()
                            CalendarGrid()
                        }
                        """.trimIndent(),
                    preview = {
                        var year by remember { mutableStateOf(2026) }
                        var month by remember { mutableStateOf(3) }
                        var selected by
                            remember { mutableStateOf<CalendarDate?>(CalendarDate(2026, 3, 10)) }
                        Calendar(
                            year = year,
                            month = month,
                            onMonthChange = { y, m ->
                                year = y
                                month = m
                            },
                            selected = selected,
                            onSelectedChange = { selected = it },
                            today = CalendarDate(2026, 3, 15),
                        ) {
                            CalendarHeader()
                            CalendarWeekdays()
                            CalendarGrid()
                        }
                    },
                ),
                ComponentExample(
                    title = "Default content",
                    code =
                        """
                        var year by remember { mutableStateOf(2026) }
                        var month by remember { mutableStateOf(3) }
                        var selected by remember { mutableStateOf<CalendarDate?>(CalendarDate(2026, 3, 10)) }
                        Calendar(
                            year = year, month = month,
                            onMonthChange = { y, m -> year = y; month = m },
                            selected = selected, onSelectedChange = { selected = it },
                            today = CalendarDate(2026, 3, 15),
                        ) {
                            CalendarDefaultContent()
                        }
                        """.trimIndent(),
                    preview = {
                        var year by remember { mutableStateOf(2026) }
                        var month by remember { mutableStateOf(3) }
                        var selected by
                            remember { mutableStateOf<CalendarDate?>(CalendarDate(2026, 3, 10)) }
                        Calendar(
                            year = year,
                            month = month,
                            onMonthChange = { y, m ->
                                year = y
                                month = m
                            },
                            selected = selected,
                            onSelectedChange = { selected = it },
                            today = CalendarDate(2026, 3, 15),
                        ) {
                            CalendarDefaultContent()
                        }
                    },
                ),
                ComponentExample(
                    title = "Months",
                    code =
                        """
                        var year by remember { mutableStateOf(2026) }
                        var month by remember { mutableStateOf(3) }
                        var selected by remember { mutableStateOf<CalendarDate?>(CalendarDate(2026, 3, 10)) }
                        Calendar(
                            year = year, month = month,
                            onMonthChange = { y, m -> year = y; month = m },
                            selected = selected, onSelectedChange = { selected = it },
                            today = CalendarDate(2026, 3, 15),
                            numberOfMonths = 2,
                        ) {
                            CalendarMonths()
                        }
                        """.trimIndent(),
                    preview = {
                        var year by remember { mutableStateOf(2026) }
                        var month by remember { mutableStateOf(3) }
                        var selected by
                            remember { mutableStateOf<CalendarDate?>(CalendarDate(2026, 3, 10)) }
                        Calendar(
                            year = year,
                            month = month,
                            onMonthChange = { y, m ->
                                year = y
                                month = m
                            },
                            selected = selected,
                            onSelectedChange = { selected = it },
                            today = CalendarDate(2026, 3, 15),
                            numberOfMonths = 2,
                        ) {
                            CalendarMonths()
                        }
                    },
                ),
            ),
    )
