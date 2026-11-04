package com.toutiaoyan.compose.ui.catalog.docs

internal fun calendarRecipeCode(title: String): String {
    if (title == "Default") return calendarUsage
    val initial =
        when (title) {
            "Range Calendar", "Week Numbers" -> "CalendarDate(today.year, 1, 12)"
            "Booked dates" -> "CalendarDate(today.year, 1, 6)"
            "Custom Cell Size" -> "CalendarDate(today.year, 12, 8)"
            "Presets" -> "CalendarDate(today.year, 2, 12)"
            "Date and Time Picker" -> "today.copy(day = 12)"
            else -> "today"
        }
    val range = title in listOf("Range Calendar", "Custom Cell Size")
    val card = title in listOf("Presets", "Date and Time Picker", "Booked dates", "Custom Cell Size", "Week Numbers")
    val compact = title in listOf("Presets", "Date and Time Picker")
    val selection =
        when {
            range -> "selectedRange = range, onSelectedRangeChange = { range = it },"
            title == "Multiple" -> "selectedDates = dates, onSelectedDatesChange = { dates = it },"
            else -> "selected = date, onSelectedChange = { date = if (date == it) null else it },"
        }
    val dateState =
        when {
            range ->
                "var range by remember { mutableStateOf(CalendarDateRange(initial, plusDays(initial, " +
                    (if (title == "Custom Cell Size") "10" else "30") + "))) }"
            title == "Multiple" -> "var dates by remember { mutableStateOf(setOf(initial, plusDays(initial, 4))) }"
            else
            ->
                "var date by remember { mutableStateOf<CalendarDate?>(" +
                    (
                        if (title == "Basic") {
                            "null"
                        } else {
                            "initial"
                        }
                    ) +
                    ") }"
        }
    val visible = if (title == "Presets") "today" else "initial"
    val properties =
        buildList {
            add("today = today,")
            when (title) {
                "Month and Year Selector",
                "Custom Cell Size",
                "RTL",
                ->
                    add("captionLayout = CalendarCaptionLayout.Dropdown,")
            }
            when (title) {
                "Range Calendar" -> add("numberOfMonths = 2,")
                "Week Numbers" -> add("showWeekNumber = true,")
                "Week starts on Monday" -> add("weekStartsOn = 1,")
                "Hide outside days" -> add("showOutsideDays = false,")
                "Disabled" -> add("disabled = { it.dayOfWeek() in listOf(0, 6) },")
                "Booked dates" -> add("disabled = { it.month == 1 && it.day in 12..26 },")
                "Month bounds" -> add("startMonth = today.copy(day = 1), endMonth = plusDays(today.copy(day = 1), 62),")
                "Custom Cell Size" -> add("cellSize = if (wide) 48.dp else 40.dp,")
                "Presets" -> add("cellSize = 38.dp, fixedWeeks = true,")
                "RTL" -> add("cellSize = 36.dp,")
            }
            add(
                if (compact) {
                    (
                        "style = Style { contentPadding(0.dp); " +
                            "background(Color.Transparent) },"
                    )
                } else if (!card) {
                    "style = surface,"
                } else {
                    ""
                },
            )
        }.filter { it.isNotEmpty() }.joinToString("\n")
    val custom =
        when (title) {
            "Custom Cell Size" -> customCalendarDaysCode
            "Booked dates" -> bookedCalendarDaysCode
            else -> ""
        }
    val body =
        "Calendar(\n    year = year, month = month,\n" +
            "    onMonthChange = { y, m -> year = y; month = m },\n    " + selection + "\n" +
            properties.prependIndent("    ") +
            "\n)" +
            (
                if (custom.isEmpty()) {
                    ""
                } else {
                    " {\n" +
                        custom.prependIndent("    ") +
                        "\n}"
                }
            )
    val footer =
        when (title) {
            "Presets" -> calendarPresetFooterCode
            "Date and Time Picker" -> calendarTimeFooterCode
            else -> ""
        }
    val framed =
        if (card) {
            val cardProps = if (compact) "size = CardSize.Sm, spacing = 12.dp" else "spacing = 0.dp"
            val width =
                when (title) {
                    "Presets" -> ", modifier = Modifier.widthIn(max = 300.dp)"
                    "Date and Time Picker" -> ", modifier = Modifier.width(220.dp)"
                    "Week Numbers" -> ", modifier = Modifier.width(240.dp)"
                    "Custom Cell Size" -> ", modifier = Modifier.width((if (wide) 48.dp else 40.dp) * 7 + 16.dp)"
                    else -> ", modifier = Modifier.width(212.dp)"
                }
            "Card(" + cardProps + width + ") {\n    CardContent {\n" + body.prependIndent("        ") +
                "\n    }\n" + footer.prependIndent("    ") + "\n}"
        } else {
            body
        }
    val setup =
        """
        val local = Clock.System.now().toLocalDateTime(TimeZone.currentSystemDefault()).date
        val today = CalendarDate(local.year, local.month.ordinal + 1, local.day)
        fun plusDays(date: CalendarDate, days: Int): CalendarDate {
            val result = LocalDate(date.year, date.month, date.day).plus(DatePeriod(days = days))
            return CalendarDate(result.year, result.month.ordinal + 1, result.day)
        }
        val initial = $initial
        var year by remember { mutableStateOf($visible.year) }
        var month by remember { mutableStateOf($visible.month) }
        $dateState
        val colors = currentTheme.colors
        val radius = twRadius.lg
        val surface = Style {
            borderWidth(1.dp); borderColor(colors.border); shape(RoundedCornerShape(radius))
        }
        """.trimIndent()
    val responsive =
        if (title == "Custom Cell Size") {
            (
                "\nval wide = with(LocalDensity.current) { " +
                    "LocalWindowInfo.current.containerSize.width.toDp() } >= 768.dp"
            )
        } else {
            ""
        }
    val timeState =
        if (title == "Date and Time Picker") {
            (
                "\nvar startTime by remember { mutableStateOf(\"10:30:00\") " +
                    "}\nvar endTime by remember { mutableStateOf(\"12:30:00\") }"
            )
        } else {
            ""
        }
    val rtl =
        if (title == "RTL") {
            "var language by remember { mutableStateOf(0) }\n" +
                (
                    "RtlPreview(language, listOf(\"Arabic (العربية)\", " +
                        "\"English\", \"Hebrew (עברית)\"), { language = it }) {\n"
                ) +
                "    CompositionLocalProvider(\n" +
                (
                    "        LocalLayoutDirection provides if (language == 1) " +
                        "LayoutDirection.Ltr else LayoutDirection.Rtl,\n"
                ) +
                "        LocalCalendarLocale provides calendarRtlLocale(language),\n" +
                "    ) {\n" + framed.prependIndent("        ") + "\n    }\n}\n\n" + calendarRtlLocaleCode
        } else {
            framed
        }
    return setup + responsive + timeState + "\n" + rtl
}

private val customCalendarDaysCode =
    """
    Column(verticalArrangement = Arrangement.spacedBy(16.dp)) {
        CalendarHeader()
        Column {
            CalendarWeekdays()
            CalendarGrid(dayContent = { cell ->
                val selected = calendarState().isSelectedDay(cell.date)
                val ink = if (selected) colors.primaryForeground else if (cell.inCurrentMonth) colors.foreground else colors.mutedForeground
                Text(textStyle = TextStyle(fontSize = 14.sp, lineHeight = 14.sp, color = ink)) { cell.date.day.toString() }
                if (cell.inCurrentMonth) Text(textStyle = TextStyle(fontSize = 12.sp, lineHeight = 12.sp, color = ink.copy(alpha = .7f))) {
                    if (cell.date.dayOfWeek() in listOf(0, 6)) "$120" else "$100"
                }
            })
        }
    }
    """.trimIndent()

private val bookedCalendarDaysCode =
    """
    Column(verticalArrangement = Arrangement.spacedBy(16.dp)) {
        CalendarHeader()
        Column {
            CalendarWeekdays()
            CalendarGrid(
                dayStyle = { cell -> Style { if (cell.date.month == 1 && cell.date.day in 12..26) alpha(0.5f) } },
                dayContent = { cell ->
                    val booked = cell.date.month == 1 && cell.date.day in 12..26
                    Text(textStyle = TextStyle(
                        fontSize = 14.sp, lineHeight = 14.sp,
                        color = if (date == cell.date) colors.primaryForeground else if (cell.inCurrentMonth && !booked) colors.foreground else colors.mutedForeground,
                        textDecoration = if (booked) TextDecoration.LineThrough else null,
                    )) { cell.date.day.toString() }
                },
            )
        }
    }
    """.trimIndent()

private val calendarPresetFooterCode =
    """
    CardFooter(modifier = Modifier.drawBehind {
        drawLine(colors.border, Offset(0f, .5.dp.toPx()), Offset(this.size.width, .5.dp.toPx()), 1.dp.toPx())
    }) {
        FlowRow(horizontalArrangement = Arrangement.spacedBy(8.dp), verticalArrangement = Arrangement.spacedBy(8.dp)) {
            listOf("Today" to 0, "Tomorrow" to 1, "In 3 days" to 3, "In a week" to 7, "In 2 weeks" to 14).forEach { (label, days) ->
                Button(label, onClick = {
                    val next = plusDays(today, days)
                    date = next; year = next.year; month = next.month
                }, modifier = Modifier.widthIn(min = 78.dp).weight(1f), style = Style { fillWidth() }, variant = ButtonVariant.Outline, size = ButtonSize.Sm)
            }
        }
    }
    """.trimIndent()

private val calendarTimeFooterCode =
    """
    CardFooter(modifier = Modifier.drawBehind {
        drawLine(colors.border, Offset(0f, .5.dp.toPx()), Offset(this.size.width, .5.dp.toPx()), 1.dp.toPx())
    }) {
        FieldGroup {
            Field {
                FieldLabel("Start Time")
                InputGroup {
                    InputGroupInput(startTime, { startTime = it }, Modifier.width(150.dp))
                    InputGroupAddon {
                        Image(Lucide.Clock2, null, Modifier.size(16.dp), colorFilter = ColorFilter.tint(colors.mutedForeground))
                    }
                }
            }
            Field {
                FieldLabel("End Time")
                InputGroup {
                    InputGroupInput(endTime, { endTime = it }, Modifier.width(150.dp))
                    InputGroupAddon {
                        Image(Lucide.Clock2, null, Modifier.size(16.dp), colorFilter = ColorFilter.tint(colors.mutedForeground))
                    }
                }
            }
        }
    }
    """.trimIndent()

private val calendarRtlLocaleCode =
    """
    fun calendarRtlLocale(language: Int): CalendarLocale =
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
    """.trimIndent()
