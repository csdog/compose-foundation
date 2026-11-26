import Foundation

public enum FoundationCalendarCaptionLayout: Sendable {
    case label
    case dropdown
    case dropdownMonths
    case dropdownYears

    var selectsMonth: Bool { self == .dropdown || self == .dropdownMonths }
    var selectsYear: Bool { self == .dropdown || self == .dropdownYears }
}

struct FoundationCalendarNavigation {
    let calendar: Calendar
    let bounds: ClosedRange<Date>?
    let numberOfMonths: Int

    private var firstAllowedMonth: Date? { bounds.map { monthStart($0.lowerBound) } }
    private var lastAllowedMonth: Date? { bounds.map { monthStart($0.upperBound) } }

    func anchor(for date: Date) -> Date {
        var month = monthStart(date)
        if let last = lastAllowedMonth {
            let lastStart = addingMonths(1 - numberOfMonths, to: last) ?? last
            month = min(month, lastStart)
        }
        if let first = firstAllowedMonth { month = max(month, first) }
        return month
    }

    func visibleMonths(from date: Date) -> [Date] {
        var months = [anchor(for: date)]
        while months.count < numberOfMonths, let last = months.last,
              let next = addingMonths(1, to: last), next > last {
            if let limit = lastAllowedMonth, next > limit { break }
            months.append(next)
        }
        return months
    }

    func step(from date: Date, by offset: Int) -> Date? {
        let current = anchor(for: date)
        guard let candidate = addingMonths(offset, to: current) else { return nil }
        let destination = anchor(for: candidate)
        return destination == current ? nil : destination
    }

    func anchor(showing month: Date, at offset: Int) -> Date {
        anchor(for: addingMonths(-offset, to: month) ?? month)
    }

    func months(inYearOf date: Date) -> [Date] {
        guard let year = calendar.dateInterval(of: .year, for: date) else { return [monthStart(date)] }
        var months: [Date] = []
        var month = monthStart(year.start)
        while month < year.end {
            if firstAllowedMonth.map({ month >= $0 }) != false,
               lastAllowedMonth.map({ month <= $0 }) != false {
                months.append(month)
            }
            guard let next = addingMonths(1, to: month), next > month else { break }
            month = next
        }
        return months
    }

    func yearOptions(including date: Date, today: Date = Date()) -> [Date] {
        let first = bounds?.lowerBound ?? min(
            date, calendar.date(byAdding: .year, value: -100, to: today) ?? today
        )
        let last = bounds?.upperBound ?? max(date, today)
        let firstYear = calendar.dateInterval(of: .year, for: first)
        let lastYear = calendar.dateInterval(of: .year, for: last)
        guard var year = firstYear, let end = lastYear?.end else { return [date] }
        var years: [Date] = []
        while year.start < end {
            years.append(year.start)
            guard let next = calendar.dateInterval(of: .year, for: year.end),
                  next.start > year.start else { break }
            year = next
        }
        return years
    }

    func month(inYearOf year: Date, matching date: Date) -> Date? {
        let target = calendar.dateComponents([.month], from: date)
        let months = months(inYearOf: year)
        return months.first { calendar.dateComponents([.month], from: $0) == target }
            ?? months.min {
                abs(calendar.component(.month, from: $0) - (target.month ?? 1))
                    < abs(calendar.component(.month, from: $1) - (target.month ?? 1))
            }
    }

    private func monthStart(_ date: Date) -> Date {
        calendar.dateInterval(of: .month, for: date)?.start ?? date
    }

    private func addingMonths(_ offset: Int, to date: Date) -> Date? {
        calendar.date(byAdding: .month, value: offset, to: date).map(monthStart)
    }
}
