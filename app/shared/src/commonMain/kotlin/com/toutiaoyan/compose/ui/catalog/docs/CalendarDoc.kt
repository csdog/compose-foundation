@file:OptIn(androidx.compose.foundation.style.ExperimentalFoundationStyleApi::class)

package com.toutiaoyan.compose.ui.catalog.docs

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.style.Style
import androidx.compose.foundation.style.fillWidth
import androidx.compose.foundation.style.contentPadding
import androidx.compose.runtime.*
import androidx.compose.ui.draw.drawBehind
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.ColorFilter
import androidx.compose.ui.platform.LocalLayoutDirection
import androidx.compose.ui.platform.LocalWindowInfo
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.unit.LayoutDirection
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.composables.icons.lucide.Clock2
import com.composables.icons.lucide.Lucide
import com.toutiaoyan.compose.ui.components.calendar.*
import com.toutiaoyan.compose.ui.components.button.*
import com.toutiaoyan.compose.ui.components.card.*
import com.toutiaoyan.compose.ui.components.field.*
import com.toutiaoyan.compose.ui.components.inputgroup.*
import com.toutiaoyan.compose.ui.components.text.Text
import com.toutiaoyan.compose.ui.theme.currentTheme
import com.toutiaoyan.compose.tailwind.core.twRadius
import kotlinx.datetime.DatePeriod
import kotlinx.datetime.LocalDate
import kotlinx.datetime.plus

private val calendarTitles =
    listOf(
        "Default", "Basic", "Range Calendar", "Month and Year Selector", "Presets",
        "Date and Time Picker", "Booked dates", "Custom Cell Size", "Week Numbers", "RTL",
        "Multiple", "Disabled", "Week starts on Monday", "Hide outside days", "Month bounds",
    )

private val calendarExtensionTitles =
    setOf(
        "Multiple",
        "Disabled",
        "Week starts on Monday",
        "Hide outside days",
        "Month bounds",
    )

private fun calendarPlusDays(
    date: CalendarDate,
    days: Int,
): CalendarDate {
    val result = LocalDate(date.year, date.month, date.day).plus(DatePeriod(days = days))
    return CalendarDate(result.year, result.month.ordinal + 1, result.day)
}

internal val calendarUsage =
    """
    val local = Clock.System.now().toLocalDateTime(TimeZone.currentSystemDefault()).date
    val today = CalendarDate(local.year, local.month.ordinal + 1, local.day)
    var year by remember { mutableStateOf(today.year) }
    var month by remember { mutableStateOf(today.month) }
    var date by remember { mutableStateOf<CalendarDate?>(today) }
    val border = currentTheme.colors.border
    val radius = twRadius.lg
    Calendar(
        year, month, { y, m -> year = y; month = m }, date, { date = if (date == it) null else it },
        today = today, captionLayout = CalendarCaptionLayout.Dropdown,
        style = Style { borderWidth(1.dp); borderColor(border); shape(RoundedCornerShape(radius)) },
    )
    """.trimIndent()

internal val calendarPersianCode =
    """
    val system = CalendarSystem.Persian
    val initial = system.fromGregorian(CalendarDate(2025, 6, 12))
    var year by remember { mutableStateOf(initial.year) }
    var month by remember { mutableStateOf(initial.month) }
    var selected by remember { mutableStateOf<CalendarDate?>(initial) }
    CompositionLocalProvider(
        LocalCalendarLocale provides CalendarLocale.Persian,
        LocalLayoutDirection provides LayoutDirection.Rtl,
    ) {
        Calendar(year, month, { y, m -> year = y; month = m }, selected,
            { selected = if (selected == it) null else it })
    }
    """.trimIndent()

val calendarDoc =
    ComponentDoc(
        id = "calendar", title = "Calendar",
        description = "A calendar component that allows users to select a date or a range of dates.",
        usageImportsCode = "import com.toutiaoyan.compose.ui.components.calendar.*",
        usageCode = calendarUsage,
        compositionCode = (
            "Calendar\n├── CalendarHeader\n│   ├── CalendarPrevious\n│   " +
                "├── CalendarCaption\n│   └── CalendarNext\n├── " +
                "CalendarWeekdays\n└── CalendarGrid\n    └── CalendarDayButton"
        ),
        examplesHeading = null,
        beforeExamples =
            listOf(
                ComponentGuideSection(
                    "About",
                    listOf(
                        ComponentGuideBlock.Paragraph(
                            "The Kotlin component defaults to civil Gregorian dates and caller-owned state. " +
                                (
                                    "It follows the official Base UI Calendar recipes without " +
                                        "adding a date-library dependency to core."
                                ),
                        ),
                    ),
                ),
                ComponentGuideSection(
                    "Date Picker",
                    listOf(
                        ComponentGuideBlock.Paragraph(
                            "Compose Calendar with Popover and a trigger. See [Date Picker](/date-picker).",
                        ),
                    ),
                ),
                ComponentGuideSection(
                    "Persian / Hijri / Jalali Calendar",
                    listOf(
                        ComponentGuideBlock.Paragraph(
                            "Use CalendarLocale.Persian with LocalLayoutDirection.Rtl. All CalendarDate " +
                                "fields and month callbacks then use solar Jalali dates. Convert with " +
                                "CalendarSystem.Persian.fromGregorian / toGregorian at your application boundary. " +
                                "Supported Jalali years: 1–3176. This is not an Islamic lunar calendar.",
                        ),
                    ),
                ),
                ComponentGuideSection(
                    "Selected Date (With TimeZone)",
                    listOf(
                        ComponentGuideBlock.Paragraph(
                            (
                                "Convert an instant into a civil date in the intended time " +
                                    "zone at the application boundary. "
                            ) +
                                (
                                    "Do not derive a local selection from UTC midnight; that can " +
                                        "move it to the previous day. "
                                ) +
                                (
                                    "These examples use currentCalendarDate() from the catalog, " +
                                        "backed by the system time zone."
                                ),
                        ),
                        ComponentGuideBlock.Code(
                            "val today = calendarDateAt(Clock.System.now(), TimeZone.currentSystemDefault())",
                        ),
                    ),
                ),
            ),
        examples =
            (
                listOf(
                    ComponentExample(
                        title = "Persian / Hijri / Jalali Calendar",
                        code = calendarPersianCode,
                        preview = { CalendarPersianPreview() },
                    ),
                ) +
                    calendarTitles.map { title ->
                        ComponentExample(
                            title = if (title in calendarExtensionTitles) "Kotlin extension: $title" else title,
                            code = calendarRecipeCode(title),
                            minPreviewHeight =
                                when (title) {
                                    "Presets" -> 650.dp
                                    "Date and Time Picker" -> 600.dp
                                    "Custom Cell Size" -> 560.dp
                                    else -> 384.dp
                                },
                            mobileMinPreviewHeight = if (title == "Range Calendar") 576.dp else null,
                            descriptionMarkdown =
                                when (title) {
                                    "Basic" ->
                                        "A basic calendar component. The preview adds a rounded border " +
                                            "to style the calendar."
                                    "Range Calendar" -> "Use range selection mode to enable range selection."
                                    "Month and Year Selector" ->
                                        "Use the dropdown caption layout to show month and year menus."
                                    "Week Numbers" -> "Use `showWeekNumber` to show week numbers."
                                    "RTL" ->
                                        "To enable RTL support in shadcn/ui, see the " +
                                            "[RTL configuration guide](https://ui.shadcn.com/docs/rtl). " +
                                            "See also the [Hijri Guide]" +
                                            "(https://ui.shadcn.com/docs/components/base/calendar" +
                                            "#persian--hijri--jalali-calendar) for enabling the " +
                                            "Persian / Hijri / Jalali calendar."
                                    else -> null
                                },
                            preview = { CalendarRecipe(title) },
                        )
                    }
            ).let { listOf(it[1]) + it.take(1) + it.drop(2) },
        afterExamples =
            listOf(
                ComponentGuideSection(
                    "Changelog",
                    listOf(
                        ComponentGuideBlock.Paragraph("RTL Support"),
                        ComponentGuideBlock.Paragraph(
                            "When migrating an RTL calendar, provide both CalendarLocale and " +
                                "LocalLayoutDirection. A locale controls labels and the first weekday; " +
                                "layout direction controls row order and navigation arrows. Remove manual " +
                                "arrow mirroring and directional padding overrides; Calendar handles these " +
                                "through Compose layout direction. Keep stored dates Gregorian unless you " +
                                "explicitly select CalendarLocale.Persian and convert at the boundary.",
                        ),
                        ComponentGuideBlock.Code(
                            "CompositionLocalProvider(LocalCalendarLocale provides CalendarLocale.Persian, " +
                                "LocalLayoutDirection provides LayoutDirection.Rtl) { /* Calendar(...) */ }",
                        ),
                    ),
                ),
                ComponentGuideSection(
                    "Keyboard and accessibility",
                    listOf(
                        ComponentGuideBlock.Bullets(
                            listOf(
                                (
                                    "Arrow keys move the day cursor; Home/End move to the week " +
                                        "edges; PageUp/PageDown change months."
                                ),
                                (
                                    "Enter or Space selects a focused day. Month navigation and " +
                                        "caption controls keep their own keyboard behavior."
                                ),
                                (
                                    "Disabled days cannot be selected. Hidden outside days do not " +
                                        "enter the focus or accessibility tree."
                                ),
                                (
                                    "Inline examples do not steal initial focus. Popups can opt in " +
                                        "with LocalCalendarAutoFocus."
                                ),
                            ),
                        ),
                    ),
                ),
            ),
        apiSections =
            listOf(
                ComponentApiSection(
                    "Calendar",
                    "Single, range, or multiple selection with caller-owned month and dates.",
                    listOf(
                        ApiProperty("year / month", "Int", "Required"),
                        ApiProperty("onMonthChange", "(Int, Int) -> Unit", "Required"),
                        ApiProperty(
                            "selected / selectedRange / selectedDates",
                            "CalendarDate? / CalendarDateRange? / Set<CalendarDate>",
                            "Required",
                        ),
                        ApiProperty(
                            "onSelectedChange / onSelectedRangeChange / onSelectedDatesChange",
                            "Callback",
                            "Required",
                        ),
                        ApiProperty("cellSize", "Dp?", "null: Nova 28.dp, other presets 36.dp"),
                        ApiProperty("showWeekNumber", "Boolean", "false"),
                        ApiProperty("fixedWeeks", "Boolean", "false"),
                        ApiProperty("captionLayout", "CalendarCaptionLayout", "Label"),
                        ApiProperty("numberOfMonths", "Int", "1"),
                        ApiProperty("today", "CalendarDate?", "null"),
                        ApiProperty("disabled", "(CalendarDate) -> Boolean", "{ false }"),
                        ApiProperty("startMonth / endMonth", "CalendarDate?", "null"),
                        ApiProperty("fromDate / toDate", "CalendarDate?", "null"),
                        ApiProperty("weekStartsOn", "Int", "Locale first day"),
                        ApiProperty("showOutsideDays", "Boolean", "true"),
                        ApiProperty("modifier / style", "Modifier / Style", "Default"),
                    ),
                ),
                ComponentApiSection(
                    "CalendarGrid",
                    (
                        "Default days or caller-provided day content and style. " +
                            "Selection and focus remain owned by Calendar."
                    ),
                    listOf(
                        ApiProperty("year / month", "Int?", "Current context"),
                        ApiProperty("dayStyle", "@Composable (CalendarCell) -> Style", "{ Style }"),
                        ApiProperty("dayContent", "@Composable ColumnScope.(CalendarCell) -> Unit", "Day number"),
                    ),
                ),
                ComponentApiSection(
                    "CalendarDayButton",
                    "A day action with native focus, disabled state, selection and optional content.",
                    listOf(
                        ApiProperty("cell", "CalendarCell", "Required"),
                        ApiProperty("style", "Style", "Style"),
                        ApiProperty("content", "@Composable ColumnScope.() -> Unit", "Day number"),
                    ),
                ),
                ComponentApiSection(
                    "CalendarLocale",
                    "Provide month, weekday, caption and accessible-date formatters via LocalCalendarLocale.",
                    listOf(
                        ApiProperty("LocalCalendarLocale", "CalendarLocale", "English"),
                        ApiProperty("LocalCalendarAutoFocus", "Boolean", "false"),
                    ),
                ),
            ),
    )

@Composable
private fun CalendarRecipe(title: String) {
    if (title == "RTL") {
        var language by remember { mutableStateOf(0) }
        RtlPreview(language, listOf("Arabic (العربية)", "English", "Hebrew (עברית)"), { language = it }) {
            CompositionLocalProvider(
                LocalLayoutDirection provides if (language == 1) LayoutDirection.Ltr else LayoutDirection.Rtl,
                LocalCalendarLocale provides calendarRtlLocale(language),
            ) { CalendarRecipeBody(title) }
        }
    } else {
        CalendarRecipeBody(title)
    }
}

@Composable
private fun CalendarRecipeBody(title: String) {
    val today = remember { currentCalendarDate() }
    val initial =
        when (title) {
            "Range Calendar", "Week Numbers" -> CalendarDate(today.year, 1, 12)
            "Booked dates" -> CalendarDate(today.year, 1, 6)
            "Custom Cell Size" -> CalendarDate(today.year, 12, 8)
            "Presets" -> CalendarDate(today.year, 2, 12)
            "Date and Time Picker" -> today.copy(day = 12)
            else -> today
        }
    var year by remember { mutableStateOf(if (title == "Presets") today.year else initial.year) }
    var month by remember { mutableStateOf(if (title == "Presets") today.month else initial.month) }
    var date by remember { mutableStateOf<CalendarDate?>(if (title == "Basic") null else initial) }
    var range by remember {
        mutableStateOf(
            CalendarDateRange(initial, calendarPlusDays(initial, if (title == "Custom Cell Size") 10 else 30)),
        )
    }
    var dates by remember { mutableStateOf(setOf(initial, calendarPlusDays(initial, 4))) }
    val wide = with(LocalDensity.current) { LocalWindowInfo.current.containerSize.width.toDp() } >= 768.dp
    val size =
        when (title) {
            "Custom Cell Size" -> if (wide) 48.dp else 40.dp
            "Presets" -> 38.dp
            "RTL" -> 36.dp
            else -> 28.dp
        }
    val colors = currentTheme.colors
    val radius = twRadius.lg
    val surface =
        Style {
            borderWidth(1.dp)
            borderColor(colors.border)
            shape(RoundedCornerShape(radius))
        }
    val noPadding =
        Style {
            contentPadding(0.dp)
            background(Color.Transparent)
        }
    val inCard = title in listOf("Presets", "Date and Time Picker", "Booked dates", "Custom Cell Size", "Week Numbers")
    val caption =
        if (title in
            listOf(
                "Default",
                "Month and Year Selector",
                "Custom Cell Size",
                "RTL",
            )
        ) {
            CalendarCaptionLayout.Dropdown
        } else {
            CalendarCaptionLayout.Label
        }
    val move: (Int, Int) -> Unit = { y, m ->
        year = y
        month = m
    }
    val select: (CalendarDate) -> Unit = { date = if (date == it) null else it }
    val booked: (CalendarDate) -> Boolean = { title == "Booked dates" && it.month == 1 && it.day in 12..26 }
    val calendar: @Composable () -> Unit = {
        when (title) {
            "Range Calendar", "Custom Cell Size" ->
                Calendar(
                    year, month, move, range, { range = it }, today = today,
                    numberOfMonths = if (title == "Range Calendar") 2 else 1,
                    captionLayout = caption, cellSize = size, style = if (inCard) Style else surface,
                ) {
                    if (title == "Custom Cell Size") {
                        Column(verticalArrangement = Arrangement.spacedBy(16.dp)) {
                            CalendarHeader()
                            Column {
                                CalendarWeekdays()
                                CalendarGrid(dayContent = { cell ->
                                    val selected = calendarState().isSelectedDay(cell.date)
                                    val ink =
                                        if (selected) {
                                            colors.primaryForeground
                                        } else if (cell.inCurrentMonth) {
                                            colors.foreground
                                        } else {
                                            colors.mutedForeground
                                        }
                                    Text(
                                        textStyle = TextStyle(fontSize = 14.sp, lineHeight = 14.sp, color = ink),
                                    ) { cell.date.day.toString() }
                                    if (cell.inCurrentMonth) {
                                        Text(
                                            textStyle =
                                                TextStyle(
                                                    fontSize = 12.sp,
                                                    lineHeight = 12.sp,
                                                    color = ink.copy(alpha = .7f),
                                                ),
                                        ) {
                                            if (cell.date.dayOfWeek() in listOf(0, 6)) "$120" else "$100"
                                        }
                                    }
                                })
                            }
                        }
                    } else {
                        CalendarDefaultContent()
                    }
                }
            "Multiple" -> Calendar(year, month, move, dates, { dates = it }, today = today, style = surface)
            else ->
                Calendar(
                    year, month, move, date, select, today = today, captionLayout = caption,
                    cellSize = size, showWeekNumber = title == "Week Numbers", fixedWeeks = title == "Presets",
                    weekStartsOn =
                        if (title == "Week starts on Monday") {
                            1
                        } else {
                            LocalCalendarLocale.current.firstDayOfWeek
                        },
                    showOutsideDays = title != "Hide outside days",
                    startMonth = if (title == "Month bounds") today.copy(day = 1) else null,
                    endMonth = if (title == "Month bounds") calendarPlusDays(today.copy(day = 1), 62) else null,
                    disabled = { booked(it) || (title == "Disabled" && it.dayOfWeek() in listOf(0, 6)) },
                    style =
                        if (title in listOf("Presets", "Date and Time Picker")) {
                            noPadding
                        } else if (inCard) {
                            Style
                        } else {
                            surface
                        },
                ) {
                    if (title == "Booked dates") {
                        Column(verticalArrangement = Arrangement.spacedBy(16.dp)) {
                            CalendarHeader()
                            Column {
                                CalendarWeekdays()
                                CalendarGrid(
                                    dayStyle = { cell -> Style { if (booked(cell.date)) alpha(0.5f) } },
                                    dayContent = { cell ->
                                        Text(
                                            textStyle =
                                                TextStyle(
                                                    fontSize = 14.sp,
                                                    lineHeight = 14.sp,
                                                    color =
                                                        if (date == cell.date) {
                                                            colors.primaryForeground
                                                        } else if (cell.inCurrentMonth && !booked(cell.date)) {
                                                            colors.foreground
                                                        } else {
                                                            colors.mutedForeground
                                                        },
                                                    textDecoration =
                                                        if (booked(
                                                                cell.date,
                                                            )
                                                        ) {
                                                            TextDecoration.LineThrough
                                                        } else {
                                                            null
                                                        },
                                                ),
                                        ) { cell.date.day.toString() }
                                    },
                                )
                            }
                        }
                    } else {
                        CalendarDefaultContent()
                    }
                }
        }
    }
    if (!inCard) {
        calendar()
        return
    }
    Card(
        size = if (title in listOf("Presets", "Date and Time Picker")) CardSize.Sm else CardSize.Default,
        spacing = if (title in listOf("Presets", "Date and Time Picker")) 12.dp else 0.dp,
        modifier =
            if (title == "Presets") {
                Modifier.widthIn(max = 300.dp)
            } else {
                Modifier.width(
                    when (title) {
                        "Date and Time Picker" -> 220.dp
                        "Week Numbers" -> 240.dp
                        "Custom Cell Size" -> size * 7 + 16.dp
                        else -> 212.dp
                    },
                )
            },
    ) {
        CardContent { calendar() }
        if (title == "Presets") {
            CardFooter(
                modifier =
                    Modifier.drawBehind {
                        drawLine(
                            colors.border,
                            Offset(0f, .5.dp.toPx()),
                            Offset(this.size.width, .5.dp.toPx()),
                            1.dp.toPx(),
                        )
                    },
            ) {
                FlowRow(
                    horizontalArrangement = Arrangement.spacedBy(8.dp),
                    verticalArrangement = Arrangement.spacedBy(8.dp),
                ) {
                    listOf(
                        "Today" to 0,
                        "Tomorrow" to 1,
                        "In 3 days" to 3,
                        "In a week" to 7,
                        "In 2 weeks" to 14,
                    ).forEach {
                            (label, days) ->
                        Button(
                            label,
                            onClick = {
                                val next = calendarPlusDays(today, days)
                                date = next
                                move(next.year, next.month)
                            },
                            modifier = Modifier.widthIn(min = 78.dp).weight(1f),
                            style = Style { fillWidth() },
                            variant = ButtonVariant.Outline,
                            size = ButtonSize.Sm,
                        )
                    }
                }
            }
        }
        if (title == "Date and Time Picker") {
            CardFooter(
                modifier =
                    Modifier.drawBehind {
                        drawLine(
                            colors.border,
                            Offset(0f, .5.dp.toPx()),
                            Offset(this.size.width, .5.dp.toPx()),
                            1.dp.toPx(),
                        )
                    },
            ) {
                FieldGroup {
                    CalendarTimeField("Start Time", "10:30:00")
                    CalendarTimeField("End Time", "12:30:00")
                }
            }
        }
    }
}

@Composable
private fun CalendarTimeField(
    label: String,
    initial: String,
) {
    var time by remember { mutableStateOf(initial) }
    Field {
        FieldLabel(label)
        InputGroup {
            InputGroupInput(time, { time = it }, Modifier.width(150.dp))
            InputGroupAddon {
                Image(
                    Lucide.Clock2,
                    label,
                    Modifier.size(16.dp),
                    colorFilter = ColorFilter.tint(currentTheme.colors.mutedForeground),
                )
            }
        }
    }
}

private fun calendarRtlLocale(language: Int): CalendarLocale =
    when (language) {
        0 ->
            CalendarLocale(
                listOf(
                    "يناير",
                    "فبراير",
                    "مارس",
                    "أبريل",
                    "مايو",
                    "يونيو",
                    "يوليو",
                    "أغسطس",
                    "سبتمبر",
                    "أكتوبر",
                    "نوفمبر",
                    "ديسمبر",
                ),
                listOf(
                    "يناير",
                    "فبراير",
                    "مارس",
                    "أبريل",
                    "مايو",
                    "يونيو",
                    "يوليو",
                    "أغسطس",
                    "سبتمبر",
                    "أكتوبر",
                    "نوفمبر",
                    "ديسمبر",
                ),
                listOf("أحد", "اثنين", "ثلاثاء", "أربعاء", "خميس", "جمعة", "سبت"),
                "الشهر السابق",
                "الشهر التالي",
                firstDayOfWeek = 0,
            )
        2 ->
            CalendarLocale(
                listOf(
                    "ינואר",
                    "פברואר",
                    "מרץ",
                    "אפריל",
                    "מאי",
                    "יוני",
                    "יולי",
                    "אוגוסט",
                    "ספטמבר",
                    "אוקטובר",
                    "נובמבר",
                    "דצמבר",
                ),
                listOf("ינו׳", "פבר׳", "מרץ", "אפר׳", "מאי", "יוני", "יולי", "אוג׳", "ספט׳", "אוק׳", "נוב׳", "דצמ׳"),
                listOf("א׳", "ב׳", "ג׳", "ד׳", "ה׳", "ו׳", "ש׳"),
                "החודש הקודם",
                "החודש הבא",
                firstDayOfWeek = 0,
            )
        else -> CalendarLocale.English
    }

@Composable
internal fun CalendarPersianPreview() {
    val initial = CalendarSystem.Persian.fromGregorian(CalendarDate(2025, 6, 12))
    var year by remember { mutableStateOf(initial.year) }
    var month by remember { mutableStateOf(initial.month) }
    var selected by remember { mutableStateOf<CalendarDate?>(initial) }
    val border = currentTheme.colors.border
    val radius = twRadius.lg
    CompositionLocalProvider(
        LocalCalendarLocale provides CalendarLocale.Persian,
        LocalLayoutDirection provides LayoutDirection.Rtl,
    ) {
        Calendar(
            year,
            month,
            { y, m ->
                year = y
                month = m
            },
            selected,
            { selected = if (selected == it) null else it },
            style =
                Style {
                    borderWidth(1.dp)
                    borderColor(border)
                    shape(RoundedCornerShape(radius))
                },
        )
    }
}
