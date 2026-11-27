import Foundation
import SwiftUI

/// A calendar range. A nil end means the user has started choosing a range.
public struct FoundationCalendarRange: Equatable, Sendable {
    public let start: Date
    public let end: Date?

    public init(start: Date, end: Date? = nil) {
        if let end, end < start {
            self.start = end
            self.end = start
        } else {
            self.start = start
            self.end = end
        }
    }
}

enum FoundationCalendarSelection: Equatable {
    case single(Date?)
    case range(FoundationCalendarRange?)
    case multiple(Set<Date>)

    enum DayPresentation: Equatable {
        case ordinary
        case selected
        case rangeStart
        case rangeInterior
        case rangeEnd
        case singleDayRange

        var hasPrimaryFill: Bool { self != .ordinary && self != .rangeInterior }
    }

    enum RangeConnection: Equatable { case previous, next }

    var navigationDate: Date? {
        switch self {
        case let .single(date): date
        case let .range(range): range?.end ?? range?.start
        case let .multiple(dates): dates.min()
        }
    }

    func selecting(_ date: Date, calendar: Calendar, bounds: ClosedRange<Date>?) -> Self {
        let day = calendar.startOfDay(for: date)
        guard Self.isSelectable(day, calendar: calendar, bounds: bounds) else { return self }
        switch self {
        case .single:
            return .single(day)
        case let .range(range):
            guard let range, range.end == nil else {
                return .range(FoundationCalendarRange(start: day))
            }
            return .range(FoundationCalendarRange(start: calendar.startOfDay(for: range.start), end: day))
        case let .multiple(dates):
            let matches = dates.filter { calendar.isDate($0, inSameDayAs: day) }
            return .multiple(matches.isEmpty ? dates.union([day]) : dates.subtracting(matches))
        }
    }

    func presentation(of date: Date, calendar: Calendar) -> DayPresentation {
        switch self {
        case let .single(selected):
            return selected.map { calendar.isDate($0, inSameDayAs: date) } == true ? .selected : .ordinary
        case let .multiple(dates):
            return dates.contains { calendar.isDate($0, inSameDayAs: date) } ? .selected : .ordinary
        case let .range(range):
            guard let range else { return .ordinary }
            let isStart = calendar.isDate(range.start, inSameDayAs: date)
            guard let end = range.end else { return isStart ? .rangeStart : .ordinary }
            let isEnd = calendar.isDate(end, inSameDayAs: date)
            if isStart && isEnd { return .singleDayRange }
            if isStart { return .rangeStart }
            if isEnd { return .rangeEnd }
            return date > calendar.startOfDay(for: range.start) && date < calendar.startOfDay(for: end)
                ? .rangeInterior : .ordinary
        }
    }

    func endpointConnection(
        for presentation: DayPresentation, on date: Date, column: Int, calendar: Calendar
    ) -> RangeConnection? {
        guard case let .range(range?) = self, range.end != nil else { return nil }
        let offset: Int
        switch presentation {
        case .rangeStart where column < 6: offset = 1
        case .rangeEnd where column > 0: offset = -1
        default: return nil
        }
        guard let neighbor = calendar.date(byAdding: .day, value: offset, to: date),
              calendar.isDate(neighbor, equalTo: date, toGranularity: .month) else { return nil }
        return offset == 1 ? .next : .previous
    }

    static func isSelectable(_ date: Date, calendar: Calendar, bounds: ClosedRange<Date>?) -> Bool {
        guard let bounds else { return true }
        let day = calendar.startOfDay(for: date)
        return (calendar.startOfDay(for: bounds.lowerBound)...calendar.startOfDay(for: bounds.upperBound)).contains(day)
    }
}

@MainActor
enum FoundationCalendarBinding {
    case single(Binding<Date?>)
    case range(Binding<FoundationCalendarRange?>)
    case multiple(Binding<Set<Date>>)

    var value: FoundationCalendarSelection {
        switch self {
        case let .single(binding): .single(binding.wrappedValue)
        case let .range(binding): .range(binding.wrappedValue)
        case let .multiple(binding): .multiple(binding.wrappedValue)
        }
    }

    func write(_ value: FoundationCalendarSelection) {
        switch (self, value) {
        case let (.single(binding), .single(date)): binding.wrappedValue = date
        case let (.range(binding), .range(range)): binding.wrappedValue = range
        case let (.multiple(binding), .multiple(dates)): binding.wrappedValue = dates
        default: preconditionFailure("Calendar selection and binding modes must match")
        }
    }
}
