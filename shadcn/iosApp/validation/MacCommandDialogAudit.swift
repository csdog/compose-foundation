import AppKit
import SwiftUI

@MainActor
private final class CommandDialogState: ObservableObject {
    @Published var presented = false
    var triggerFocused = false
    var dismissals = 0
    var actions: [String] = []
}

@available(macOS 14, *)
private struct CommandDialogFixture: View {
    @ObservedObject var state: CommandDialogState
    @FocusState private var triggerFocused: Bool

    var body: some View {
        VStack {
            Button("Open command palette") { state.presented = true }
                .keyboardShortcut("j")
                .focusable(interactions: [.edit, .activate])
                .focused($triggerFocused)
                .onChange(of: triggerFocused) { _, value in state.triggerFocused = value }
                .onKeyPress("d") {
                    state.presented = true
                    return .handled
                }
            Spacer()
        }
        .padding(20)
        .frame(width: 480, height: 420)
        .modifier(FoundationCommandDialog(isPresented: $state.presented, sections: [
            .init(id: "suggestions", title: "Suggestions", actions: [.init("Calendar", value: "calendar")]),
            .init(id: "settings", title: "Settings", actions: [.init("Billing", value: "billing")]),
        ], onDismiss: {
            state.dismissals += 1
            triggerFocused = true
        }) { state.actions.append($0) })
        .foundationTheme(FoundationTheme(preset: .nova))
    }
}

@available(macOS 14, *)
@main @MainActor
enum MacCommandDialogAudit {
    static func main() {
        let app = NSApplication.shared
        let state = CommandDialogState()
        let window = NSWindow(contentRect: NSRect(x: 0, y: 0, width: 480, height: 420),
                              styleMask: [.titled, .closable], backing: .buffered, defer: false)
        window.contentView = NSHostingView(rootView: CommandDialogFixture(state: state))
        app.setActivationPolicy(.regular)
        window.center()
        window.makeKeyAndOrderFront(nil)

        Task { @MainActor in
            app.activate()
            NSRunningApplication.current.activate(options: [.activateAllWindows])
            window.makeKeyAndOrderFront(nil)
            guard await waitUntil({ window.isKeyWindow }) else { fail("Window is not active", state) }

            press(38, "j", flags: [.command], window: window)
            guard await waitUntil({
                state.presented && (window.firstResponder as? NSTextView)?.isFieldEditor == true
            }) else { fail("Command-J did not open with search focused", state) }
            press(11, "b", window: window)
            try? await Task.sleep(for: .milliseconds(80))
            press(34, "i", window: window)
            try? await Task.sleep(for: .milliseconds(120))
            press(36, "\r", window: window)
            guard await waitUntil({
                !state.presented && state.triggerFocused && state.dismissals == 1 && state.actions == ["billing"]
            }) else { fail("Return did not choose Billing, dismiss, and return focus", state) }

            press(2, "d", window: window)
            guard await waitUntil({
                state.presented && (window.firstResponder as? NSTextView)?.isFieldEditor == true
            }) else { fail("The restored trigger did not reopen with search focused", state) }
            press(53, "\u{1b}", window: window)
            guard await waitUntil({
                !state.presented && state.triggerFocused && state.dismissals == 2 && state.actions == ["billing"]
            }) else { fail("Escape did not dismiss and return focus without selecting", state) }

            press(2, "d", window: window)
            guard await waitUntil({
                state.presented && (window.firstResponder as? NSTextView)?.isFieldEditor == true
            }) else { fail("Could not reopen and focus search after Escape", state) }
            state.presented = false
            guard await waitUntil({ !state.presented && state.triggerFocused && state.dismissals == 3 }) else {
                fail("External dismissal did not return focus", state)
            }

            press(2, "d", window: window)
            guard await waitUntil({
                state.presented && (window.firstResponder as? NSTextView)?.isFieldEditor == true
            }) else { fail("Could not reopen after binding dismissal", state) }
            click(NSPoint(x: 12, y: 12), window: window)
            guard await waitUntil({
                !state.presented && state.triggerFocused && state.dismissals == 4 && state.actions == ["billing"]
            }) else { fail("Clicking the backdrop did not close and restore focus", state) }
            print("PASS: Command-J opens with search focused and each dismissal restores the trigger")
            exit(0)
        }
        app.run()
    }

    private static func waitUntil(_ condition: () -> Bool) async -> Bool {
        for _ in 0..<150 {
            if condition() { return true }
            try? await Task.sleep(for: .milliseconds(15))
        }
        return condition()
    }

    private static func press(
        _ code: UInt16, _ characters: String, flags: NSEvent.ModifierFlags = [], window: NSWindow
    ) {
        for type in [NSEvent.EventType.keyDown, .keyUp] {
            guard let event = NSEvent.keyEvent(
                with: type, location: .zero, modifierFlags: flags,
                timestamp: ProcessInfo.processInfo.systemUptime, windowNumber: window.windowNumber, context: nil,
                characters: characters, charactersIgnoringModifiers: characters, isARepeat: false, keyCode: code
            ) else { continue }
            NSApp.sendEvent(event)
        }
    }

    private static func click(_ point: NSPoint, window: NSWindow) {
        for type in [NSEvent.EventType.leftMouseDown, .leftMouseUp] {
            guard let event = NSEvent.mouseEvent(
                with: type, location: point, modifierFlags: [],
                timestamp: ProcessInfo.processInfo.systemUptime, windowNumber: window.windowNumber, context: nil,
                eventNumber: 0, clickCount: 1, pressure: type == .leftMouseUp ? 0 : 1
            ) else { continue }
            NSApp.postEvent(event, atStart: false)
        }
    }

    private static func fail(_ message: String, _ state: CommandDialogState) -> Never {
        print("FAIL: \(message). Presented \(state.presented), trigger focused \(state.triggerFocused), dismissals \(state.dismissals), actions \(state.actions)")
        exit(1)
    }
}
