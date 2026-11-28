import AppKit
import SwiftUI

@MainActor
private final class ChoiceAuditState: ObservableObject {
    @Published var selection: String?
}

private struct ChoiceAuditFixture: View {
    @ObservedObject var state: ChoiceAuditState

    var body: some View {
        VStack(spacing: 0) {
            FoundationCombobox("Choose produce", groups: [
                .init("Fruit", choices: [.init("Apple", value: "apple"), .init("Apricot", value: "apricot", isEnabled: false)]),
                .init("Empty group", choices: []),
                .init("Vegetables", choices: [.init("Carrot", value: "carrot"), .init("Celery", value: "celery")]),
            ], selection: $state.selection)
            .frame(width: 280)
            Spacer(minLength: 0)
        }
        .padding(.top, 70)
        .frame(width: 360, height: 400)
        .foundationTheme(FoundationTheme(preset: .nova))
    }
}

@main @MainActor
enum MacGroupedComboboxAudit {
    static func main() {
        let app = NSApplication.shared
        let state = ChoiceAuditState()
        let window = NSWindow(contentRect: NSRect(x: 0, y: 0, width: 360, height: 400),
                              styleMask: [.titled, .closable], backing: .buffered, defer: false)
        window.contentView = NSHostingView(rootView: ChoiceAuditFixture(state: state))
        app.setActivationPolicy(.regular)
        window.center()
        window.makeKeyAndOrderFront(nil)

        Task { @MainActor in
            app.activate()
            NSRunningApplication.current.activate(options: [.activateAllWindows])
            window.makeKeyAndOrderFront(nil)
            try? await Task.sleep(for: .milliseconds(400))
            require(window.isKeyWindow, "The audit window needs focus")

            await openCombobox(in: window)
            await press(125, "\u{F701}")
            await press(125, "\u{F701}")
            await press(36, "\r")
            require(state.selection == "carrot", "Down should cross an empty group and a disabled choice before Return")

            await openCombobox(in: window)
            await press(126, "\u{F700}")
            await press(53, "\u{1B}")
            require(state.selection == "carrot", "Escape should preserve the selected value")

            await openCombobox(in: window)
            await press(126, "\u{F700}")
            await press(36, "\r")
            require(state.selection == "celery", "Up should wrap to the last enabled choice after reopening")
            print("PASS: Grouped Combobox skips disabled choices, selects across groups, and preserves selection on Escape")
            exit(0)
        }
        app.run()
    }

    private static func openCombobox(in window: NSWindow) async {
        for type in [NSEvent.EventType.leftMouseDown, .leftMouseUp] {
            guard let event = NSEvent.mouseEvent(
                with: type, location: NSPoint(x: 180, y: 308), modifierFlags: [],
                timestamp: ProcessInfo.processInfo.systemUptime, windowNumber: window.windowNumber,
                context: nil, eventNumber: 0, clickCount: 1, pressure: type == .leftMouseUp ? 0 : 1
            ) else { continue }
            NSApplication.shared.postEvent(event, atStart: false)
            try? await Task.sleep(for: .milliseconds(100))
        }
        try? await Task.sleep(for: .milliseconds(300))
    }

    private static func press(_ code: UInt16, _ characters: String) async {
        let app = NSApplication.shared
        guard let window = app.keyWindow else { return }
        for type in [NSEvent.EventType.keyDown, .keyUp] {
            guard let event = NSEvent.keyEvent(
                with: type, location: .zero, modifierFlags: [],
                timestamp: ProcessInfo.processInfo.systemUptime, windowNumber: window.windowNumber,
                context: nil, characters: characters, charactersIgnoringModifiers: characters,
                isARepeat: false, keyCode: code
            ) else { continue }
            app.sendEvent(event)
        }
        try? await Task.sleep(for: .milliseconds(140))
    }

    private static func require(_ condition: Bool, _ message: String) {
        if !condition {
            print("FAIL: \(message)")
            exit(1)
        }
    }
}
