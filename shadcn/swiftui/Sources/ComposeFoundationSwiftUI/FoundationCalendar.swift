import SwiftUI

public struct FoundationCalendar: View {
    @Environment(\.calendar) private var calendar
    @Environment(\.locale) private var locale
    @Environment(\.foundationTheme) private var theme
    @Environment(\.colorScheme) private var colorScheme
    @State private var visibleDate: Date
    @State private var lastLocalSelection: FoundationCalendarSelection?

    private let selection: FoundationCalendarBinding
    private let bounds: ClosedRange<Date>?
    private let numberOfMonths: Int
    private let captionLayout: FoundationCalendarCaptionLayout

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

    private init(
        selection: FoundationCalendarBinding, bounds: ClosedRange<Date>?,
        numberOfMonths: Int, captionLayout: FoundationCalendarCaptionLayout
    ) {
        self.selection = selection
        self._visibleDate = State(initialValue: selection.value.navigationDate ?? Date())
        self.bounds = bounds
        self.numberOfMonths = max(1, numberOfMonths)
        self.captionLayout = captionLayout
    }

    public var body: some View {
        let palette = theme.palette(for: colorScheme)
        let months = navigation.visibleMonths(from: visibleDate)
        let selected = selection.value

        Group {
            if months.count == 1, let month = months.first {
                monthView(month, offset: 0, last: true, selection: selected, palette: palette)
            } else {
                LazyVGrid(columns: [GridItem(.adaptive(minimum: 264), spacing: 16)], alignment: .leading, spacing: 16) {
                    ForEach(months.indices, id: \.self) { index in
                        monthView(months[index], offset: index, last: index == months.count - 1,
                                  selection: selected, palette: palette)
                    }
                }
                .frame(maxWidth: CGFloat(months.count) * 344 - 16)
            }
        }
        .foregroundColor(palette.foreground)
        .padding(10)
        .background(RoundedRectangle(cornerRadius: theme.cornerRadius).fill(palette.card))
        .foundationOnChange(of: selected) { value in
            let initiatedHere = value == lastLocalSelection
            lastLocalSelection = nil
            if !initiatedHere, let date = value.navigationDate {
                visibleDate = navigation.anchor(for: date)
            }
        }
        .foundationOnChange(of: bounds) { _ in visibleDate = navigation.anchor(for: visibleDate) }
    }

    private var navigation: FoundationCalendarNavigation {
        FoundationCalendarNavigation(calendar: calendar, bounds: bounds, numberOfMonths: numberOfMonths)
    }

    private func monthView(
        _ month: Date, offset: Int, last: Bool, selection: FoundationCalendarSelection, palette: FoundationPalette
    ) -> some View {
        let weekdays = weekdayNames
        let days = gridDays(for: month)
        let dayNumbers = formatter(template: "d")
        let dayLabels = formatter(template: "yMMMMd")
        let columns = Array(repeating: GridItem(.flexible(), spacing: 2), count: 7)

        return VStack(spacing: 8) {
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
                    Text(weekdays[index]).font(.caption).foregroundColor(palette.mutedForeground)
                }
                ForEach(days.indices, id: \.self) { index in
                    if let date = days[index] {
                        dayButton(
                            date,
                            title: dayNumbers.string(from: date),
                            accessibilityLabel: dayLabels.string(from: date),
                            selection: selection,
                            palette: palette
                        )
                    } else {
                        Color.clear.frame(height: 44).accessibilityHidden(true)
                    }
                }
            }
        }
    }

    @ViewBuilder
    private func caption(for month: Date, offset: Int) -> some View {
        let title = formatter(template: "yMMMM").string(from: month)
        if captionLayout == .label {
            Text(title)
                .font(.subheadline.weight(.semibold))
                .accessibilityAddTraits(.isHeader)
        } else {
            let monthText = formatter(template: "MMM")
            let yearText = formatter(template: "y")
            let pattern = formatter(template: "yMMM").dateFormat ?? "MMM y"
            let yearFirst = pattern.first(where: { "yYuML".contains($0) }).map { "yYu".contains($0) } ?? false
            let chooseMonth = captionLayout.selectsMonth
            let chooseYear = captionLayout.selectsYear
            let monthControl = captionControl(
                monthText.string(from: month), label: Text("Month for \(title)"), isMenu: chooseMonth
            ) {
                ForEach(navigation.months(inYearOf: month), id: \.self) { destination in
                    Button(monthText.string(from: destination)) {
                        visibleDate = navigation.anchor(showing: destination, at: offset)
                    }
                }
            }
            let yearControl = captionControl(
                yearText.string(from: month), label: Text("Year for \(title)"), isMenu: chooseYear
            ) {
                ForEach(navigation.yearOptions(including: month), id: \.self) { year in
                    Button(yearText.string(from: year)) {
                        if let destination = navigation.month(inYearOf: year, matching: month) {
                            visibleDate = navigation.anchor(showing: destination, at: offset)
                        }
                    }
                }
            }
            HStack(spacing: 8) {
                if yearFirst {
                    yearControl
                    monthControl
                } else {
                    monthControl
                    yearControl
                }
            }
            .font(.subheadline.weight(.semibold))
        }
    }

    @ViewBuilder
    private func captionControl<Options: View>(
        _ title: String, label: Text, isMenu: Bool, @ViewBuilder options: () -> Options
    ) -> some View {
        if isMenu {
            Menu(content: options) {
                HStack(spacing: 4) {
                    Text(title)
                    #if !os(macOS)
                    Image(systemName: "chevron.down").font(.caption2).accessibilityHidden(true)
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
        return Array(names[first...]) + Array(names[..<first])
    }

    private func gridDays(for month: Date) -> [Date?] {
        let weekday = calendar.component(.weekday, from: month)
        let leading = (weekday - calendar.firstWeekday + 7) % 7
        let count = calendar.range(of: .day, in: .month, for: month)?.count ?? 0
        return Array(repeating: nil, count: leading)
            + (0..<count).map { calendar.date(byAdding: .day, value: $0, to: month) }
    }

    private func monthButton(_ symbol: String, label: String, offset: Int) -> some View {
        let destination = navigation.step(from: visibleDate, by: offset)
        return Button {
            if let destination { visibleDate = destination }
        } label: {
            Image(systemName: symbol).frame(minWidth: 44, minHeight: 44).contentShape(Rectangle())
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
        selection value: FoundationCalendarSelection,
        palette: FoundationPalette
    ) -> some View {
        let presentation = value.presentation(of: date, calendar: calendar)
        let enabled = FoundationCalendarSelection.isSelectable(date, calendar: calendar, bounds: bounds)

        return Button {
            let previous = selection.value
            let next = previous.selecting(date, calendar: calendar, bounds: bounds)
            if next != previous {
                selection.write(next)
                lastLocalSelection = selection.value == next ? next : nil
            }
        } label: {
            Text(title)
                .font(.subheadline)
                .foregroundColor(presentation.hasPrimaryFill ? palette.primaryForeground : palette.foreground)
                .frame(maxWidth: .infinity, minHeight: 44)
                .background(dayBackground(presentation, palette: palette))
                .contentShape(Rectangle())
        }
        .buttonStyle(PlainButtonStyle())
        .disabled(!enabled)
        .opacity(enabled ? 1 : 0.4)
        .accessibilityLabel(Text(accessibilityLabel))
        .accessibilityAddTraits(presentation != .ordinary ? .isSelected : [])
    }

    @ViewBuilder
    private func dayBackground(_ presentation: FoundationCalendarSelection.DayPresentation, palette: FoundationPalette) -> some View {
        if presentation == .rangeInterior {
            Rectangle().fill(palette.secondary)
        } else if presentation.hasPrimaryFill {
            RoundedRectangle(cornerRadius: theme.cornerRadius).fill(palette.primary)
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
