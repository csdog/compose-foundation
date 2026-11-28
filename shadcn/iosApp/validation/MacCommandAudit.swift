import AppKit
import SwiftUI

@MainActor
private final class CommandAuditState: ObservableObject {
    @Published var query = ""
    var actions: [String] = []
}

private struct CommandAuditFixture: View {
    @ObservedObject var state: CommandAuditState

    var body: some View {
        VStack {
            FoundationCommand(sections: [
                .init(id: "suggestions", title: "Suggestions", actions: [
                    .init("Calendar", value: "calendar"),
                    .init("Calculator", value: "calculator", isEnabled: false),
                ]),
                .init(id: "settings", title: "Settings", actions: [
                    .init("Profile", value: "profile"), .init("Billing", value: "billing"),
                ]),
            ], query: $state.query) { state.actions.append($0) }
            .frame(width: 360)
            Spacer()
        }
        .padding(.top, 24)
        .frame(width: 480, height: 400)
        .foundationTheme(FoundationTheme(preset: .nova))
    }
}

@main @MainActor
enum MacCommandAudit {
    static func main() {
        let app = NSApplication.shared
        let state = CommandAuditState()
        let window = NSWindow(contentRect: NSRect(x: 0, y: 0, width: 480, height: 400),
                              styleMask: [.titled, .closable], backing: .buffered, defer: false)
        window.contentView = NSHostingView(rootView: CommandAuditFixture(state: state))
        app.setActivationPolicy(.regular)
        window.center()
        window.makeKeyAndOrderFront(nil)

        Task { @MainActor in
            app.activate()
            NSRunningApplication.current.activate(options: [.activateAllWindows])
            window.makeKeyAndOrderFront(nil)
            try? await Task.sleep(for: .milliseconds(350))
            require(window.isKeyWindow, "Audit window is not active")
            for type in [NSEvent.EventType.leftMouseDown, .leftMouseUp] {
                guard let event = NSEvent.mouseEvent(
                    with: type, location: NSPoint(x: 240, y: 351), modifierFlags: [],
                    timestamp: ProcessInfo.processInfo.systemUptime, windowNumber: window.windowNumber, context: nil,
                    eventNumber: 0, clickCount: 1, pressure: type == .leftMouseUp ? 0 : 1
                ) else { continue }
                app.postEvent(event, atStart: false)
            }
            try? await Task.sleep(for: .milliseconds(250))
            require((window.firstResponder as? NSTextView)?.isFieldEditor == true, "Search did not receive native focus")
            await press(125, "\u{F701}", in: window)
            await press(125, "\u{F701}", in: window)
            await press(36, "\r", in: window)
            require(state.actions == ["profile"], "Down did not skip the disabled row and cross the group")

            await press(11, "b", in: window)
            await press(34, "i", in: window)
            require(state.query == "bi", "Native text editing did not update the query binding")
            await press(36, "\r", in: window)
            require(state.actions == ["profile", "billing"], "Return did not activate the filtered result exactly once")

            state.query = "no results"
            try? await Task.sleep(for: .milliseconds(180))
            await press(36, "\r", in: window)
            require(state.actions == ["profile", "billing"], "Escape or an empty result must leave the action list unchanged")
            state.query = ""
            try? await Task.sleep(for: .milliseconds(180))
            await press(126, "\u{F700}", in: window)
            await press(36, "\r", in: window)
            require(state.actions == ["profile", "billing", "billing"], "Up did not wrap to the last enabled result")
            print("PASS: Command filters live, skips disabled rows, wraps, and activates one eligible result")
            exit(0)
        }
        app.run()
    }

    private static func press(_ code: UInt16, _ characters: String, in window: NSWindow) async {
        for type in [NSEvent.EventType.keyDown, .keyUp] {
            guard let event = NSEvent.keyEvent(
                with: type, location: .zero, modifierFlags: [],
                timestamp: ProcessInfo.processInfo.systemUptime, windowNumber: window.windowNumber, context: nil,
                characters: characters, charactersIgnoringModifiers: characters, isARepeat: false, keyCode: code
            ) else { continue }
            NSApp.sendEvent(event)
        }
        try? await Task.sleep(for: .milliseconds(120))
    }

    private static func require(_ result: Bool, _ message: String) {
        if !result {
            print("FAIL: \(message)")
            exit(1)
        }
    }
}
