package com.toutiaoyan.compose.ui.components.calendar

import androidx.compose.runtime.staticCompositionLocalOf

/** Supply labels/formatters for any locale without adding a date library to core. */
class CalendarLocale(
    val months: List<String>,
    val shortMonths: List<String>,
    val weekdays: List<String>,
    val previousMonth: String = "Previous month",
    val nextMonth: String = "Next month",
    val formatCaption: (year: Int, month: Int) -> String = { year, month -> "${months[month - 1]} $year" },
    val formatDate: (CalendarDate) -> String = { date -> "${months[date.month - 1]} ${date.day}, ${date.year}" },
    val firstDayOfWeek: Int = 0,
    val pickDate: String = "Pick a date",
    val formatAccessibleDate: (CalendarDate) -> String = { "${it.year}-${it.month}-${it.day}" },
) {
    init {
        require(months.size == 12 && shortMonths.size == 12 && weekdays.size == 7)
    }

    companion object {
        val English: CalendarLocale =
            CalendarLocale(
                listOf(
                    "January",
                    "February",
                    "March",
                    "April",
                    "May",
                    "June",
                    "July",
                    "August",
                    "September",
                    "October",
                    "November",
                    "December",
                ),
                listOf("Jan", "Feb", "Mar", "Apr", "May", "Jun", "Jul", "Aug", "Sep", "Oct", "Nov", "Dec"),
                listOf("Su", "Mo", "Tu", "We", "Th", "Fr", "Sa"),
                formatDate = { date ->
                    val suffix =
                        if (date.day % 100 in 11..13) {
                            "th"
                        } else {
                            when (date.day % 10) {
                                1 -> "st"
                                2 -> "nd"
                                3 -> "rd"
                                else -> "th"
                            }
                        }
                    "${English.months[date.month - 1]} ${date.day}$suffix, ${date.year}"
                },
            )
        val Chinese: CalendarLocale =
            CalendarLocale(
                (1..12).map { "${it}月" }, (1..12).map { "${it}月" },
                listOf("日", "一", "二", "三", "四", "五", "六"), "上个月", "下个月",
                formatCaption = { year, month -> "${year}年${month}月" },
                formatDate = { "${it.year}年${it.month}月${it.day}日" },
                firstDayOfWeek = 1, pickDate = "选择日期",
                formatAccessibleDate = { "${it.year}年${it.month}月${it.day}日" },
            )
    }
}

val LocalCalendarLocale = staticCompositionLocalOf { CalendarLocale.English }

/** Opt in when Calendar is mounted inside a popup; inline examples do not steal focus. */
val LocalCalendarAutoFocus = staticCompositionLocalOf { false }
