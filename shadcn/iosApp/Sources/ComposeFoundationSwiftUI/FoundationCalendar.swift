import SwiftUI

public struct FoundationCalendar: View {
    @Environment(\.calendar) private var calendar
    @Environment(\.locale) private var locale
    @Environment(\.foundationTheme) private var theme
    @Environment(\.colorScheme) private var colorScheme
    @ScaledMetric(relativeTo: .subheadline) private var cellTextSize: CGFloat = 14
    @ScaledMetric(relativeTo: .caption) private var weekdayTextSize: CGFloat = 12
    @State private var visibleDate: Date
    @State private var lastLocalSelection: FoundationCalendarSelection?

    private let selection: FoundationCalendarBinding
    private let bounds: ClosedRange<Date>?
    private let numberOfMonths: Int
    private let captionLayout: FoundationCalendarCaptionLayout
    private let referenceDate: Date?

    public init(selection: Binding<Date?>, in bounds: ClosedRange<Date>? = nil) {
        self.init(selection: selection, in: bounds, numberOfMonths: 1)
    }

    public init(
        selection: Binding<Date?>, in bounds: ClosedRange<Date>? = nil,
        numberOfMonths: Int = 1, captionLayout: FoundationCalendarCaptionLayout = .label
    ) {
        self.init(selection: .single(selection), bounds: bounds, numberOfMonths: numberOfMonths, captionLayout: captionLayout)
    }

    /// The first activation sets the start; the second completes the inclusive range.
    public init(range: Binding<FoundationCalendarRange?>, in bounds: ClosedRange<Date>? = nil) {
        self.init(range: range, in: bounds, numberOfMonths: 1)
    }

    public init(
        range: Binding<FoundationCalendarRange?>, in bounds: ClosedRange<Date>? = nil,
        numberOfMonths: Int = 1, captionLayout: FoundationCalendarCaptionLayout = .label
    ) {
        self.init(selection: .range(range), bounds: bounds, numberOfMonths: numberOfMonths, captionLayout: captionLayout)
    }

    /// Opens at `initialMonth` while retaining the bound selection.
    public init(
        range: Binding<FoundationCalendarRange?>, initialMonth: Date, in bounds: ClosedRange<Date>? = nil,
        numberOfMonths: Int = 1, captionLayout: FoundationCalendarCaptionLayout = .label
    ) {
        self.init(selection: .range(range), bounds: bounds, numberOfMonths: numberOfMonths,
                  captionLayout: captionLayout, initialMonth: initialMonth)
    }

    /// Activating an already selected day removes it from the selection.
    public init(selectedDates: Binding<Set<Date>>, in bounds: ClosedRange<Date>? = nil) {
        self.init(selectedDates: selectedDates, in: bounds, numberOfMonths: 1)
    }

    public init(
        selectedDates: Binding<Set<Date>>, in bounds: ClosedRange<Date>? = nil,
        numberOfMonths: Int = 1, captionLayout: FoundationCalendarCaptionLayout = .label
    ) {
        self.init(selection: .multiple(selectedDates), bounds: bounds, numberOfMonths: numberOfMonths, captionLayout: captionLayout)
    }

    init(selection: Binding<Date?>, captionLayout: FoundationCalendarCaptionLayout, referenceDate: Date) {
        self.init(selection: .single(selection), bounds: nil, numberOfMonths: 1,
                  captionLayout: captionLayout, referenceDate: referenceDate)
    }

    private init(
        selection: FoundationCalendarBinding, bounds: ClosedRange<Date>?,
        numberOfMonths: Int, captionLayout: FoundationCalendarCaptionLayout,
        initialMonth: Date? = nil, referenceDate: Date? = nil
    ) {
        self.selection = selection
        self._visibleDate = State(initialValue: initialMonth ?? selection.value.navigationDate ?? referenceDate ?? Date())
        self.bounds = bounds
        self.numberOfMonths = max(1, numberOfMonths)
        self.captionLayout = captionLayout
        self.referenceDate = referenceDate
    }

    public var body: some View {
        let palette = theme.palette(for: colorScheme)
        let months = navigation.visibleMonths(from: visibleDate)
        let selected = selection.value
        let shape = RoundedRectangle(cornerRadius: theme.cornerRadius)

        Group {
            if #available(iOS 17, macOS 14, *) {
                let entry = navigation.focusEntry(in: months, selected: selected.navigationDate,
                                                  today: referenceDate ?? Date())
                FoundationCalendarKeyboardGrid(entry: entry, acceptsFocus: { date in
                    months.contains { calendar.isDate($0, equalTo: date, toGranularity: .month) }
                        && FoundationCalendarSelection.isSelectable(date, calendar: calendar, bounds: bounds)
                }) { focus, tabStop, requestFocus in
                    monthGrid(months, selection: selected, palette: palette) { date, enabled in
                        FoundationCalendarKeyboardDay(date: date, enabled: enabled, focus: focus,
                                                      isTabStop: date == tabStop,
                                                      ring: isNova ? palette.ring : nil, ringSize: dayHighlightSize,
                                                      cornerRadius: max(0, theme.cornerRadius - 2),
                                                      activate: { activate(date) }) { move in
                            guard let destination = navigation.focusDestination(from: date, move: move) else { return }
                            visibleDate = navigation.anchor(containing: destination, from: visibleDate)
                            requestFocus(destination)
                        }
                    }
                }
            } else {
                monthGrid(months, selection: selected, palette: palette) { _, _ in
                    EmptyModifier()
                }
            }
        }
        .foregroundColor(palette.foreground)
        .padding(isNova ? 8 : 10)
        .background(shape.fill(isNova ? palette.background : palette.card))
        .overlay(shape.strokeBorder(isNova ? palette.border : Color.clear).allowsHitTesting(false))
        .foundationOnChange(of: selected) { value in
            let initiatedHere = value == lastLocalSelection
            lastLocalSelection = nil
            if !initiatedHere, let date = value.navigationDate {
                visibleDate = navigation.anchor(for: date)
            }
        }
        .foundationOnChange(of: bounds) { _ in visibleDate = navigation.anchor(for: visibleDate) }
    }

    private var isNova: Bool { theme.preset == .nova }

    private var dayHighlightSize: CGFloat? { isNova ? max(28, cellTextSize * 2) : nil }

    private var dayRowHeight: CGFloat { max(44, dayHighlightSize ?? 0) }

    private var navigation: FoundationCalendarNavigation {
        FoundationCalendarNavigation(calendar: calendar, bounds: bounds, numberOfMonths: numberOfMonths)
    }

    @ViewBuilder
    private func monthGrid<Day: ViewModifier>(
        _ months: [Date], selection: FoundationCalendarSelection, palette: FoundationPalette,
        decorate: @escaping (Date, Bool) -> Day
    ) -> some View {
        if months.count == 1, let month = months.first {
            monthView(month, offset: 0, last: true, selection: selection, palette: palette, decorate: decorate)
        } else {
            LazyVGrid(columns: [GridItem(.adaptive(minimum: 264), spacing: 16)], alignment: .leading, spacing: 16) {
                ForEach(months.indices, id: \.self) { index in
                    monthView(months[index], offset: index, last: index == months.count - 1,
                              selection: selection, palette: palette, decorate: decorate)
                }
            }
            .frame(maxWidth: CGFloat(months.count) * 344 - 16)
        }
    }

    private func monthView<Day: ViewModifier>(
        _ month: Date, offset: Int, last: Bool, selection: FoundationCalendarSelection, palette: FoundationPalette,
        decorate: @escaping (Date, Bool) -> Day
    ) -> some View {
        let weekdays = weekdayNames
        let days = navigation.gridDays(in: month, includesOutsideDays: isNova)
        let dayNumbers = formatter(template: "d")
        let dayLabels = formatter(template: "yMMMMd")
        let columns = Array(repeating: GridItem(.flexible(), spacing: isNova ? 0 : 2), count: 7)

        return VStack(spacing: isNova ? 12 : 8) {
            HStack {
                if offset == 0 {
                    monthButton("chevron.backward", label: "Previous month", offset: -1)
                } else {
                    Color.clear.frame(width: 44, height: 44).accessibilityHidden(true)
                }
                Spacer(minLength: 0)
                caption(for: month, offset: offset)
                Spacer(minLength: 0)
                if last {
                    monthButton("chevron.forward", label: "Next month", offset: 1)
                } else {
                    Color.clear.frame(width: 44, height: 44).accessibilityHidden(true)
                }
            }
            LazyVGrid(columns: columns, spacing: 2) {
                ForEach(weekdays.indices, id: \.self) { index in
                    Text(weekdays[index])
                        .font(isNova ? .system(size: weekdayTextSize) : .caption)
                        .foregroundColor(palette.mutedForeground)
                        .frame(minHeight: isNova ? 18 : nil)
                }
                ForEach(days.indices, id: \.self) { index in
                    if let date = days[index] {
                        let number = dayNumbers.string(from: date)
                        if calendar.isDate(date, equalTo: month, toGranularity: .month) {
                            let focusDay = calendar.startOfDay(for: date)
                            let enabled = FoundationCalendarSelection.isSelectable(date, calendar: calendar, bounds: bounds)
                            dayButton(
                                date, title: number, accessibilityLabel: dayLabels.string(from: date),
                                column: index % 7, selection: selection, palette: palette, enabled: enabled
                            )
                            .modifier(decorate(focusDay, enabled))
                            .id(focusDay)
                        } else {
                            Text(number)
                                .font(.system(size: cellTextSize))
                                .foregroundColor(palette.mutedForeground)
                                .frame(maxWidth: .infinity, minHeight: dayRowHeight)
                                .accessibilityHidden(true)
                        }
                    } else {
                        Color.clear.frame(height: dayRowHeight).accessibilityHidden(true)
                    }
                }
            }
        }
        .frame(maxWidth: isNova ? 308 : nil)
    }

    @ViewBuilder
    private func caption(for month: Date, offset: Int) -> some View {
        let title = formatter(template: "yMMMM").string(from: month)
        if captionLayout == .label {
            Text(title)
                .font(isNova ? .system(size: cellTextSize, weight: .medium) : .subheadline.weight(.semibold))
                .accessibilityAddTraits(.isHeader)
        } else {
            let monthText = formatter(template: "MMM")
            let yearText = formatter(template: "y")
            let pattern = formatter(template: "yMMM").dateFormat ?? "MMM y"
            let yearFirst = pattern.first(where: { "yYuML".contains($0) }).map { "yYu".contains($0) } ?? false
            let chooseMonth = captionLayout.selectsMonth
            let chooseYear = captionLayout.selectsYear
            let monthSelection = Binding(
                get: { month },
                set: { visibleDate = navigation.anchor(showing: $0, at: offset) }
            )
            let yearSelection = Binding(
                get: { calendar.dateInterval(of: .year, for: month)?.start ?? month },
                set: { year in
                    if let destination = navigation.month(inYearOf: year, matching: month) {
                        visibleDate = navigation.anchor(showing: destination, at: offset)
                    }
                }
            )
            let monthControl = captionControl(
                monthText.string(from: month), label: Text("Month for \(title)"), isMenu: chooseMonth,
                selection: monthSelection
            ) {
                ForEach(navigation.months(inYearOf: month), id: \.self) { destination in
                    Text(monthText.string(from: destination)).tag(destination)
                }
            }
            let yearControl = captionControl(
                yearText.string(from: month), label: Text("Year for \(title)"), isMenu: chooseYear,
                selection: yearSelection
            ) {
                ForEach(navigation.yearOptions(including: month), id: \.self) { year in
                    Text(yearText.string(from: year)).tag(year)
                }
            }
            HStack(spacing: isNova ? 6 : 8) {
                if yearFirst {
                    yearControl
                    monthControl
                } else {
                    monthControl
                    yearControl
                }
            }
            .font(isNova ? .system(size: cellTextSize, weight: .medium) : .subheadline.weight(.semibold))
        }
    }

    @ViewBuilder
    private func captionControl<Options: View>(
        _ title: String, label: Text, isMenu: Bool, selection: Binding<Date>,
        @ViewBuilder options: () -> Options
    ) -> some View {
        if isMenu {
            Menu {
                Picker(selection: selection, content: options) { label }
                    .pickerStyle(InlinePickerStyle())
            } label: {
                HStack(spacing: 4) {
                    Text(title)
                    #if os(iOS)
                    Image(systemName: "chevron.down")
                        .font(isNova ? .system(size: 10, weight: .medium) : .caption2)
                        .foregroundColor(isNova ? theme.palette(for: colorScheme).mutedForeground : nil)
                        .frame(width: isNova ? 14 : nil)
                        .accessibilityHidden(true)
                    #endif
                }
                .frame(minHeight: 44)
                .contentShape(Rectangle())
            }
            .accessibilityLabel(label)
            .accessibilityValue(Text(title))
        } else {
            Text(title).accessibilityAddTraits(.isHeader)
        }
    }

    private var weekdayNames: [String] {
        let dateFormatter = DateFormatter()
        dateFormatter.locale = locale
        dateFormatter.calendar = calendar
        let names = dateFormatter.shortStandaloneWeekdaySymbols ?? []
        guard !names.isEmpty else { return [] }
        let first = (calendar.firstWeekday - 1) % names.count
        let ordered = Array(names[first...]) + Array(names[..<first])
        return isNova && locale.languageCode == "en" ? ordered.map { String($0.prefix(2)) } : ordered
    }

    private func monthButton(_ symbol: String, label: String, offset: Int) -> some View {
        let destination = navigation.step(from: visibleDate, by: offset)
        return Button {
            if let destination { visibleDate = destination }
        } label: {
            Image(systemName: symbol)
                .font(isNova ? .system(size: 12, weight: .medium) : nil)
                .frame(minWidth: 44, minHeight: 44)
                .contentShape(Rectangle())
        }
        .buttonStyle(PlainButtonStyle())
        .accessibilityLabel(Text(label))
        .disabled(destination == nil)
        .opacity(destination == nil ? 0.4 : 1)
    }

    private func dayButton(
        _ date: Date,
        title: String,
        accessibilityLabel: String,
        column: Int,
        selection value: FoundationCalendarSelection,
        palette: FoundationPalette,
        enabled: Bool
    ) -> some View {
        let presentation = value.presentation(of: date, calendar: calendar)
        let connection = isNova ? value.endpointConnection(for: presentation, on: date, column: column, calendar: calendar) : nil
        let today = isNova && calendar.isDate(date, inSameDayAs: referenceDate ?? Date())

        return Button {
            activate(date)
        } label: {
            Text(title)
                .font(isNova ? .system(size: cellTextSize) : .subheadline)
                .foregroundColor(presentation.hasPrimaryFill ? palette.primaryForeground : palette.foreground)
                .frame(maxWidth: .infinity, minHeight: dayRowHeight)
                .background(dayBackground(presentation, connection: connection, today: today, palette: palette))
                .contentShape(Rectangle())
        }
        .buttonStyle(PlainButtonStyle())
        .disabled(!enabled)
        .opacity(enabled ? 1 : 0.4)
        .accessibilityLabel(Text(accessibilityLabel))
        .accessibilityAddTraits(presentation != .ordinary ? .isSelected : [])
    }

    private func activate(_ date: Date) {
        let previous = selection.value
        let next = previous.selecting(date, calendar: calendar, bounds: bounds)
        if next != previous {
            selection.write(next)
            lastLocalSelection = selection.value == next ? next : nil
        }
    }

    @ViewBuilder
    private func dayBackground(
        _ presentation: FoundationCalendarSelection.DayPresentation,
        connection: FoundationCalendarSelection.RangeConnection?, today: Bool, palette: FoundationPalette
    ) -> some View {
        if presentation == .rangeInterior {
            Rectangle().fill(palette.secondary).frame(height: dayHighlightSize)
        } else if presentation.hasPrimaryFill || today {
            ZStack {
                if let connection {
                    HStack(spacing: 0) {
                        if connection == .next { Color.clear }
                        palette.secondary
                        if connection == .previous { Color.clear }
                    }
                    .frame(height: dayHighlightSize)
                }
                RoundedRectangle(cornerRadius: isNova ? max(0, theme.cornerRadius - 2) : theme.cornerRadius)
                    .fill(presentation.hasPrimaryFill ? palette.primary : palette.secondary)
                    .frame(maxWidth: dayHighlightSize, maxHeight: dayHighlightSize)
            }
        }
    }

    private func formatter(template: String) -> DateFormatter {
        let formatter = DateFormatter()
        formatter.locale = locale
        formatter.calendar = calendar
        formatter.timeZone = calendar.timeZone
        formatter.setLocalizedDateFormatFromTemplate(template)
        return formatter
    }
}

@available(iOS 17, macOS 14, *)
private struct FoundationCalendarKeyboardGrid<Content: View>: View {
    @FocusState private var focused: Date?
    @State private var cursor: Date?
    let entry: Date?
    let acceptsFocus: (Date) -> Bool
    let content: (FocusState<Date?>.Binding, Date?, @escaping (Date) -> Void) -> Content

    var body: some View {
        let target = cursor.flatMap { acceptsFocus($0) ? $0 : nil } ?? entry
        content($focused, target, { cursor = $0 })
            .defaultFocus($focused, target, priority: .userInitiated)
            .task(id: cursor) {
                guard let cursor, cursor == target else { return }
                // The destination becomes focusable on this update, including dates in a newly shown month.
                await Task.yield()
                guard !Task.isCancelled else { return }
                focused = cursor
            }
    }
}

@available(iOS 17, macOS 14, *)
private struct FoundationCalendarKeyboardDay: ViewModifier {
    @Environment(\.isEnabled) private var isEnabled
    @Environment(\.layoutDirection) private var layoutDirection
    let date: Date
    let enabled: Bool
    let focus: FocusState<Date?>.Binding
    let isTabStop: Bool
    let ring: Color?
    let ringSize: CGFloat?
    let cornerRadius: CGFloat
    let activate: () -> Void
    let move: (FoundationCalendarNavigation.FocusMove) -> Void

    @ViewBuilder
    func body(content: Content) -> some View {
        if enabled && isEnabled && isTabStop {
            content
                .focusable()
                .focused(focus, equals: date)
                .focusEffectDisabled(ring != nil)
                .overlay {
                    if let ring, focus.wrappedValue == date {
                        RoundedRectangle(cornerRadius: cornerRadius)
                            .strokeBorder(ring.opacity(0.55), lineWidth: 3)
                            .frame(width: ringSize, height: ringSize)
                            .allowsHitTesting(false)
                            .accessibilityHidden(true)
                    }
                }
                .onKeyPress(keys: [.leftArrow, .rightArrow, .upArrow, .downArrow, .home, .end,
                                  .pageUp, .pageDown, .return, .space], phases: [.down, .repeat], action: handle)
        } else {
            content.focusable(false)
        }
    }

    private func handle(_ key: KeyPress) -> KeyPress.Result {
        guard enabled, isEnabled, key.modifiers.intersection([.command, .control, .option]).isEmpty else {
            return .ignored
        }
        let shift = key.modifiers.contains(.shift)
        switch key.key {
        case .leftArrow, .rightArrow:
            let forward = (key.key == .rightArrow) == (layoutDirection == .leftToRight)
            move(.offset(shift ? .month : .day, forward ? 1 : -1))
        case .upArrow, .downArrow:
            move(.offset(shift ? .year : .weekOfYear, key.key == .downArrow ? 1 : -1))
        case .pageUp, .pageDown:
            move(.offset(shift ? .year : .month, key.key == .pageDown ? 1 : -1))
        case .home, .end:
            move(.weekEdge(last: key.key == .end))
        case .return, .space:
            if key.phase == .down { activate() }
        default:
            return .ignored
        }
        return .handled
    }
}
