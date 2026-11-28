import AppKit
import SwiftUI

@MainActor
private final class CalendarAuditState: ObservableObject {
    @Published var selection: Date? = CalendarAuditState.date(11, 27)
    @Published var enabled = true
    var afterActivated = false

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
    let rightToLeft: Bool
    let keyboard: Bool

    var body: some View {
        VStack(spacing: 0) {
            FoundationCalendar(selection: $state.selection, in: CalendarAuditState.date(11, 4)...CalendarAuditState.date(12, 15))
                .frame(width: 324)
                .disabled(!state.enabled)
            if keyboard {
                Button("After calendar") { state.afterActivated = true }.focusable()
            }
            Spacer(minLength: 0)
        }
        .frame(width: 360, height: 390)
        .environment(\.calendar, CalendarAuditState.calendar)
        .environment(\.locale, Locale(identifier: rightToLeft ? "ar_SA" : "en_US"))
        .environment(\.layoutDirection, rightToLeft ? .rightToLeft : .leftToRight)
        .foundationTheme(FoundationTheme(preset: .nova))
    }
}

@main @MainActor
enum MacCalendarAudit {
    static func main() {
        let app = NSApplication.shared
        let state = CalendarAuditState()
        let rightToLeft = CommandLine.arguments.contains("--rtl")
        let keyboard = CommandLine.arguments.contains("--keyboard")
        if keyboard { state.selection = CalendarAuditState.date(11, 30) }
        app.setActivationPolicy(.regular)
        let window = NSWindow(contentRect: NSRect(x: 0, y: 0, width: 360, height: 390),
                              styleMask: [.titled, .closable], backing: .buffered, defer: false)
        window.contentView = NSHostingView(rootView: CalendarAuditFixture(state: state, rightToLeft: rightToLeft,
                                                                         keyboard: keyboard))
        window.center()
        window.makeKeyAndOrderFront(nil)

        Task { @MainActor in
            app.activate()
            NSRunningApplication.current.activate(options: [.activateAllWindows])
            window.makeKeyAndOrderFront(nil)
            try? await Task.sleep(for: .milliseconds(400))
            require(window.isKeyWindow, "The audit window needs focus")

            if keyboard {
                await verifyKeyboard(state: state, window: window, rightToLeft: rightToLeft)
                exit(0)
            }

            @MainActor func clickDateOrArrow(x: CGFloat, y: CGFloat) async {
                let position = rightToLeft ? window.contentLayoutRect.width - x : x
                await click(x: position, y: y, in: window)
            }

            await clickDateOrArrow(x: 48, y: 284)
            require(state.selection == CalendarAuditState.date(11, 27), "A disabled day must not change the binding")
            await clickDateOrArrow(x: 180, y: 284)
            require(state.selection == CalendarAuditState.date(11, 4), "An enabled day updates the binding")
            await clickDateOrArrow(x: 312, y: 360)
            await clickDateOrArrow(x: 136, y: 284)
            require(state.selection == CalendarAuditState.date(12, 1), "Next month exposes its selectable dates")
            await clickDateOrArrow(x: 312, y: 360)
            await clickDateOrArrow(x: 180, y: 284)
            require(state.selection == CalendarAuditState.date(12, 2), "Paging stops at the upper bound")
            let direction = rightToLeft ? "RTL" : "LTR"
            print("PASS: \(direction) Calendar rejects disabled days, accepts selection, and stops at the month boundary")
            exit(0)
        }
        app.run()
    }

    private static func verifyKeyboard(state: CalendarAuditState, window: NSWindow, rightToLeft: Bool) async {
        let forward: (UInt16, String) = rightToLeft ? (123, "\u{F702}") : (124, "\u{F703}")
        let backward: (UInt16, String) = rightToLeft ? (124, "\u{F703}") : (123, "\u{F702}")
        let enter: (UInt16, String) = (36, "\r")
        let space: (UInt16, String) = (49, " ")
        func send(_ key: (UInt16, String), modifiers: NSEvent.ModifierFlags = []) async {
            for type in [NSEvent.EventType.keyDown, .keyUp] {
                if let event = NSEvent.keyEvent(
                    with: type, location: .zero, modifierFlags: modifiers,
                    timestamp: ProcessInfo.processInfo.systemUptime, windowNumber: window.windowNumber, context: nil,
                    characters: key.1, charactersIgnoringModifiers: key.1, isARepeat: false, keyCode: key.0
                ) { NSApp.sendEvent(event) }
            }
            try? await Task.sleep(for: .milliseconds(180))
        }
        func expect(_ month: Int, _ day: Int, _ scenario: String) {
            require(state.selection == CalendarAuditState.date(month, day),
                    "\(scenario): wanted \(month)/\(day), got \(String(describing: state.selection))")
        }

        await send(forward)
        expect(11, 30, "Focus must not write the binding")
        await send(enter)
        expect(12, 1, "Forward should cross the month boundary and Return should commit")
        await send((125, "\u{F701}"))
        await send(space)
        expect(12, 8, "Down should move one week and Space should commit")
        await send((119, "\u{F72B}"))
        await send(enter)
        expect(12, 12, "End moves to the end of the week")
        await send((115, "\u{F729}"))
        await send(enter)
        expect(12, 6, "Home moves to the beginning of the week")
        await send((116, "\u{F72C}"))
        await send(enter)
        expect(11, 6, "Page Up moves one month")
        await send(forward, modifiers: .shift)
        await send(enter)
        expect(12, 6, "Shift plus forward moves one month")
        await send(backward, modifiers: .shift)
        await send((126, "\u{F700}"))
        await send(backward)
        await send(enter)
        expect(11, 4, "Navigation stops on the first selectable date")
        await send((48, "\t"))
        await send(enter)
        require(state.afterActivated, "Tab should leave the date grid for the next control")
        await send((48, "\u{19}"), modifiers: .shift)
        state.enabled = false
        try? await Task.sleep(for: .milliseconds(180))
        await send(forward)
        await send(enter)
        expect(11, 4, "A disabled grid ignores navigation and activation")
        print("PASS: \(rightToLeft ? "RTL" : "LTR") Calendar keyboard moves focus independently from selection")
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
