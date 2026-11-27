import AppKit
import SwiftUI

@MainActor
private final class CalendarAuditState: ObservableObject {
    @Published var selection: Date? = CalendarAuditState.date(11, 27)

    static let calendar: Calendar = {
        var value = Calendar(identifier: .gregorian)
        value.timeZone = TimeZone(secondsFromGMT: 0)!
        value.firstWeekday = 1
        return value
    }()

    static func date(_ month: Int, _ day: Int) -> Date {
        calendar.date(from: DateComponents(year: 2026, month: month, day: day))!
    }
}

private struct CalendarAuditFixture: View {
    @ObservedObject var state: CalendarAuditState

    var body: some View {
        VStack(spacing: 0) {
            FoundationCalendar(selection: $state.selection, in: CalendarAuditState.date(11, 4)...CalendarAuditState.date(12, 15))
                .frame(width: 324)
            Spacer(minLength: 0)
        }
        .frame(width: 360, height: 390)
        .environment(\.calendar, CalendarAuditState.calendar)
        .environment(\.locale, Locale(identifier: "en_US"))
        .foundationTheme(FoundationTheme(preset: .nova))
    }
}

@main @MainActor
enum MacCalendarAudit {
    static func main() {
        let app = NSApplication.shared
        let state = CalendarAuditState()
        app.setActivationPolicy(.regular)
        let window = NSWindow(contentRect: NSRect(x: 0, y: 0, width: 360, height: 390),
                              styleMask: [.titled, .closable], backing: .buffered, defer: false)
        window.contentView = NSHostingView(rootView: CalendarAuditFixture(state: state))
        window.center()
        window.makeKeyAndOrderFront(nil)

        Task { @MainActor in
            app.activate()
            NSRunningApplication.current.activate(options: [.activateAllWindows])
            window.makeKeyAndOrderFront(nil)
            try? await Task.sleep(for: .milliseconds(400))
            require(window.isKeyWindow, "The audit window needs focus")

            await click(x: 48, y: 284, in: window)
            require(state.selection == CalendarAuditState.date(11, 27), "A disabled day must not change the binding")
            await click(x: 180, y: 284, in: window)
            require(state.selection == CalendarAuditState.date(11, 4), "An enabled day updates the binding")
            await click(x: 312, y: 360, in: window)
            await click(x: 136, y: 284, in: window)
            require(state.selection == CalendarAuditState.date(12, 1), "Next month exposes its selectable dates")
            await click(x: 312, y: 360, in: window)
            await click(x: 180, y: 284, in: window)
            require(state.selection == CalendarAuditState.date(12, 2), "Paging stops at the upper bound")
            print("PASS: Calendar rejects disabled days, accepts selection, and stops at the month boundary")
            exit(0)
        }
        app.run()
    }

    private static func click(x: CGFloat, y: CGFloat, in window: NSWindow) async {
        for type in [NSEvent.EventType.leftMouseDown, .leftMouseUp] {
            if let event = NSEvent.mouseEvent(
                with: type, location: NSPoint(x: x, y: y), modifierFlags: [],
                timestamp: ProcessInfo.processInfo.systemUptime, windowNumber: window.windowNumber,
                context: nil, eventNumber: 0, clickCount: 1, pressure: type == .leftMouseUp ? 0 : 1
            ) {
                NSApplication.shared.postEvent(event, atStart: false)
            }
            try? await Task.sleep(for: .milliseconds(140))
        }
    }

    private static func require(_ condition: Bool, _ message: String) {
        if !condition {
            print("FAIL: \(message)")
            exit(1)
        }
    }
}
