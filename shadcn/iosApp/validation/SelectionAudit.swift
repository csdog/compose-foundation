import Foundation
import SwiftUI

@main @MainActor
enum SelectionAudit {
    private struct Failure: Error {
        let message: String
    }

    private static func expect(_ condition: @autoclosure () -> Bool, _ message: String) throws {
        guard condition() else { throw Failure(message: message) }
    }

    static func main() throws {
        var calendar = Calendar(identifier: .gregorian)
        calendar.timeZone = try require(TimeZone(identifier: "America/New_York"))
        // March 8 crosses the spring daylight-saving transition in this zone.
        let first = try date(6, calendar: calendar)
        let middle = try date(8, calendar: calendar)
        let last = try date(10, calendar: calendar)
        let laterOnMiddle = middle.addingTimeInterval(12 * 3600)
        let outside = try date(11, calendar: calendar)
        let bounds = first...last

        var selectedRange: FoundationCalendarRange?
        let range = FoundationCalendarBinding.range(Binding(get: { selectedRange }, set: { selectedRange = $0 }))
        func selectRange(_ date: Date) {
            range.write(range.value.selecting(date, calendar: calendar, bounds: bounds))
        }
        selectRange(last)
        try expect(selectedRange == FoundationCalendarRange(start: last), "First click anchors the range")
        selectRange(outside)
        try expect(selectedRange?.end == nil, "A disabled day cannot complete the range")
        selectRange(first)
        try expect(selectedRange == FoundationCalendarRange(start: first, end: last), "Reverse selection is sorted")
        try expect(range.value.presentation(of: first, calendar: calendar) == .rangeStart, "Start has endpoint styling")
        try expect(range.value.presentation(of: middle, calendar: calendar) == .rangeInterior, "Interior is highlighted across DST")
        try expect(range.value.presentation(of: last, calendar: calendar) == .rangeEnd, "End has endpoint styling")
        try expect(range.value.presentation(of: outside, calendar: calendar) == .ordinary, "Outside days are not highlighted")
        try expect(range.value.endpointConnection(for: .rangeStart, on: first, column: 5, calendar: calendar) == .next,
                   "A range start connects to the following date in its week")
        try expect(range.value.endpointConnection(for: .rangeEnd, on: last, column: 2, calendar: calendar) == .previous,
                   "A range end connects to the previous date in its week")
        let sundayEnd = FoundationCalendarSelection.range(.init(start: first, end: middle))
        try expect(sundayEnd.endpointConnection(for: .rangeEnd, on: middle, column: 0, calendar: calendar) == nil,
                   "A Sunday endpoint does not connect across week rows")
        selectRange(laterOnMiddle)
        try expect(selectedRange == FoundationCalendarRange(start: middle), "A completed range starts over on the next click")
        try expect(range.value.endpointConnection(for: .rangeStart, on: middle, column: 0, calendar: calendar) == nil,
                   "An unfinished range has no connection")
        selectRange(middle)
        try expect(range.value.presentation(of: middle, calendar: calendar) == .singleDayRange, "Same-day range is complete")

        let multiple = FoundationCalendarSelection.multiple([first, laterOnMiddle])
        try expect(multiple.selecting(middle, calendar: calendar, bounds: bounds) == .multiple([first]), "Multiple selection removes by day")
        try expect(multiple.selecting(last, calendar: calendar, bounds: bounds) == .multiple([first, laterOnMiddle, last]), "Multiple selection preserves unrelated dates")
        try expect(multiple.selecting(outside, calendar: calendar, bounds: bounds) == multiple, "Multiple selection enforces bounds")

        let single = FoundationCalendarSelection.single(first)
        try expect(single.selecting(laterOnMiddle, calendar: calendar, bounds: bounds) == .single(middle), "Single selection writes the new day")
        try expect(single.selecting(outside, calendar: calendar, bounds: bounds) == single, "Single selection enforces bounds")

        let january = try monthDate(2025, 1, calendar: calendar)
        let february = try monthDate(2025, 2, calendar: calendar)
        let december = try monthDate(2025, 12, calendar: calendar)
        let januaryNextYear = try monthDate(2026, 1, calendar: calendar)
        let marchNextYear = try monthDate(2026, 3, calendar: calendar)
        let februaryNextYear = try monthDate(2026, 2, calendar: calendar)
        let navigation = FoundationCalendarNavigation(
            calendar: calendar, bounds: january...marchNextYear, numberOfMonths: 2
        )
        try expect(navigation.visibleMonths(from: january) == [january, february], "Shows consecutive months")
        try expect(navigation.visibleMonths(from: december) == [december, januaryNextYear], "Window crosses year boundary")
        try expect(navigation.visibleMonths(from: marchNextYear) == [februaryNextYear, marchNextYear], "Upper bound keeps the window filled")
        try expect(navigation.step(from: january, by: -1) == nil, "Previous stops at lower bound")
        try expect(navigation.step(from: marchNextYear, by: 1) == nil, "Next stops at upper bound")
        try expect(navigation.anchor(showing: januaryNextYear, at: 1) == december, "Second caption preserves its month offset")
        try expect(navigation.yearOptions(including: december) == [january, januaryNextYear], "Years include both boundaries")
        try expect(navigation.month(inYearOf: januaryNextYear, matching: december) == marchNextYear, "Year change finds the nearest eligible month")
        let short = FoundationCalendarNavigation(calendar: calendar, bounds: january...february, numberOfMonths: 3)
        try expect(short.visibleMonths(from: february) == [january, february], "Short bounds trim the window")

        var mondayCalendar = calendar
        mondayCalendar.firstWeekday = 2
        let mondayGrid = FoundationCalendarNavigation(calendar: mondayCalendar, bounds: nil, numberOfMonths: 1)
        let november = try monthDate(2026, 11, calendar: calendar)
        let october26 = try require(calendar.date(from: DateComponents(year: 2026, month: 10, day: 26)))
        let december6 = try require(calendar.date(from: DateComponents(year: 2026, month: 12, day: 6)))
        let padded = mondayGrid.gridDays(in: november, includesOutsideDays: true)
        let legacy = mondayGrid.gridDays(in: november, includesOutsideDays: false)
        try expect(padded.count == 42 && padded[0] == october26 && padded[41] == december6,
                   "Outside dates complete the first and last weeks")
        try expect(legacy.count == 36 && legacy.prefix(6).allSatisfy { $0 == nil },
                   "The original grid preserves leading placeholders")

        try expect(FoundationCheckboxState.mixed.activated == .checked, "Mixed activation checks")
        try expect(FoundationCheckboxState.checked.activated == .unchecked, "Checked activation clears")
        try expect(FoundationCheckboxState.unchecked.activated == .checked, "Unchecked activation checks")
        print("PASS Calendar selection, month navigation, and Checkbox three-state transitions")
    }

    private static func require<T>(_ value: T?) throws -> T {
        guard let value else { throw Failure(message: "Could not build calendar fixture") }
        return value
    }

    private static func date(_ day: Int, calendar: Calendar) throws -> Date {
        try require(calendar.date(from: DateComponents(year: 2026, month: 3, day: day)))
    }

    private static func monthDate(_ year: Int, _ month: Int, calendar: Calendar) throws -> Date {
        try require(calendar.date(from: DateComponents(year: year, month: month, day: 1)))
    }
}
