import AppKit
import SwiftUI

@MainActor
private final class ResizableAuditState: ObservableObject {
    @Published var fraction: CGFloat = 0.25
    @Published var enabled = true
}

private struct ResizableAuditFixture: View {
    @ObservedObject var state: ResizableAuditState
    let vertical: Bool
    let rightToLeft: Bool

    var body: some View {
        FoundationResizable(vertical ? .vertical : .horizontal, fraction: $state.fraction, limits: 0.2...0.8) {
            Text("First")
        } second: {
            Text("Second")
        }
        .disabled(!state.enabled)
        .foundationTheme(FoundationTheme(preset: .nova))
        .environment(\.layoutDirection, rightToLeft ? .rightToLeft : .leftToRight)
    }
}

@main @MainActor
enum MacResizableInteractionAudit {
    static func main() {
        let app = NSApplication.shared
        let state = ResizableAuditState()
        let vertical = CommandLine.arguments.contains("--vertical")
        let rightToLeft = CommandLine.arguments.contains("--rtl")
        let size = CGSize(width: 320, height: 180)
        let window = NSWindow(contentRect: NSRect(origin: .zero, size: size),
                              styleMask: [.titled, .closable], backing: .buffered, defer: false)
        window.contentView = NSHostingView(rootView: ResizableAuditFixture(
            state: state, vertical: vertical, rightToLeft: rightToLeft
        ))
        app.setActivationPolicy(.regular)
        window.center()
        window.makeKeyAndOrderFront(nil)

        Task { @MainActor in
            app.activate()
            NSRunningApplication.current.activate(options: [.activateAllWindows])
            window.makeKeyAndOrderFront(nil)
            try? await Task.sleep(for: .milliseconds(450))
            guard window.isKeyWindow else { fail("The audit window needs focus") }

            let forward: (UInt16, String) = vertical ? (125, "\u{F701}") :
                rightToLeft ? (123, "\u{F702}") : (124, "\u{F703}")
            let backward: (UInt16, String) = vertical ? (126, "\u{F700}") :
                rightToLeft ? (124, "\u{F703}") : (123, "\u{F702}")
            await press(forward, window)
            require(state, 0.35, "Forward arrow resizes by one step")
            await press(backward, window)
            require(state, 0.25, "Backward arrow returns to the original size")
            await press((115, "\u{F729}"), window)
            require(state, 0.2, "Home clamps to the lower bound")
            await press((119, "\u{F72B}"), window)
            require(state, 0.8, "End clamps to the upper bound")
            await press(forward, window)
            require(state, 0.8, "The bound remains stable on another key press")

            state.fraction = 0.25
            try? await Task.sleep(for: .milliseconds(250))
            let available = (vertical ? size.height : size.width) - 1
            let divider = available * 0.25 + 0.5
            let offset: CGFloat = 14
            let start = vertical
                ? CGPoint(x: size.width / 2, y: size.height - divider - offset)
                : CGPoint(x: (rightToLeft ? size.width - divider : divider) + offset, y: size.height / 2)
            let delta = available * 0.1
            let end = vertical ? CGPoint(x: start.x, y: start.y - delta)
                : CGPoint(x: start.x + (rightToLeft ? -delta : delta), y: start.y)
            await drag(start, end, window)
            require(state, 0.35, "Dragging inside the hit target expands the first panel")

            state.enabled = false
            try? await Task.sleep(for: .milliseconds(250))
            await press(forward, window)
            require(state, 0.35, "Disabled keyboard resizing leaves the fraction unchanged")
            print("PASS: Resizable keyboard, bounds and divider drag, vertical=\(vertical), RTL=\(rightToLeft)")
            exit(0)
        }
        app.run()
    }

    private static func press(_ key: (UInt16, String), _ window: NSWindow) async {
        for type in [NSEvent.EventType.keyDown, .keyUp] {
            if let event = NSEvent.keyEvent(with: type, location: .zero, modifierFlags: [],
                                           timestamp: ProcessInfo.processInfo.systemUptime,
                                           windowNumber: window.windowNumber, context: nil,
                                           characters: key.1, charactersIgnoringModifiers: key.1,
                                           isARepeat: false, keyCode: key.0) { NSApp.sendEvent(event) }
        }
        try? await Task.sleep(for: .milliseconds(170))
    }

    private static func drag(_ start: CGPoint, _ end: CGPoint, _ window: NSWindow) async {
        let middle = CGPoint(x: (start.x + end.x) / 2, y: (start.y + end.y) / 2)
        let events: [(NSEvent.EventType, CGPoint)] = [
            (.leftMouseDown, start), (.leftMouseDragged, middle), (.leftMouseDragged, end), (.leftMouseUp, end),
        ]
        for (type, point) in events {
            if let event = NSEvent.mouseEvent(with: type, location: point, modifierFlags: [],
                                             timestamp: ProcessInfo.processInfo.systemUptime,
                                             windowNumber: window.windowNumber, context: nil, eventNumber: 0,
                                             clickCount: 1, pressure: type == .leftMouseUp ? 0 : 1) {
                NSApp.postEvent(event, atStart: false)
            }
            try? await Task.sleep(for: .milliseconds(130))
        }
    }

    private static func require(_ state: ResizableAuditState, _ expected: CGFloat, _ message: String) {
        guard abs(state.fraction - expected) < 0.015 else {
            fail("\(message). Expected \(expected), got \(state.fraction)")
        }
    }

    private static func fail(_ message: String) -> Never {
        print("FAIL: \(message)")
        exit(1)
    }
}
