import AppKit
import SwiftUI

@MainActor
private final class RadioGroupAuditState: ObservableObject {
    @Published var selection: String?
    @Published var enabled = true
    var writes = 0
    var outsideFocused = false

    func select(_ value: String?) {
        selection = value
        writes += 1
    }
}

private struct RadioGroupAuditFixture: View {
    @ObservedObject var state: RadioGroupAuditState
    @FocusState private var outsideFocused: Bool
    let rightToLeft: Bool

    var body: some View {
        let descriptions = CommandLine.arguments.contains("--descriptions")
            ? ["regular": "Standard spacing", "compact": "Dense spacing"] : [:]
        VStack(alignment: .leading, spacing: 10) {
            FoundationRadioGroup([
                FoundationChoice("Regular", value: "regular"),
                FoundationChoice("Restricted", value: "restricted", isEnabled: false),
                FoundationChoice("Compact", value: "compact"),
            ], selection: Binding(get: { state.selection }, set: { value in state.select(value) }),
               descriptions: descriptions)
                .disabled(!state.enabled)
            Text("After radio group")
                .focusable()
                .focused($outsideFocused)
                .onChange(of: outsideFocused) { _, focused in state.outsideFocused = focused }
        }
        .padding(24)
        .frame(width: 300, height: 230)
        .foundationTheme(FoundationTheme(preset: .nova))
        .environment(\.layoutDirection, rightToLeft ? .rightToLeft : .leftToRight)
    }
}

@main @MainActor
enum MacRadioGroupKeyboardAudit {
    static func main() {
        let app = NSApplication.shared
        let state = RadioGroupAuditState()
        let rightToLeft = CommandLine.arguments.contains("--rtl")
        app.setActivationPolicy(.regular)
        let window = NSWindow(contentRect: NSRect(x: 0, y: 0, width: 300, height: 230),
                              styleMask: [.titled, .closable], backing: .buffered, defer: false)
        window.contentView = NSHostingView(rootView: RadioGroupAuditFixture(state: state, rightToLeft: rightToLeft))
        window.center()
        window.makeKeyAndOrderFront(nil)

        Task { @MainActor in
            app.activate()
            NSRunningApplication.current.activate(options: [.activateAllWindows])
            window.makeKeyAndOrderFront(nil)
            try? await Task.sleep(for: .milliseconds(400))
            require(window.isKeyWindow, "The audit window needs focus")
            require(state.selection == nil && state.writes == 0, "Entering the group should not select a value")

            let forward: (UInt16, String) = rightToLeft ? (123, "\u{F702}") : (124, "\u{F703}")
            let space: (UInt16, String) = (49, " ")
            await press(space, in: window)
            require(state.selection == "regular" && state.writes == 1, "Space selects the first enabled option")
            await press(forward, in: window)
            require(state.selection == "compact" && state.writes == 2, "Inline forward skips the disabled option")
            await press(forward, in: window)
            require(state.selection == "regular" && state.writes == 3, "Inline forward wraps selection")
            await press((125, "\u{F701}"), in: window)
            require(state.selection == "compact" && state.writes == 4, "Down works in either layout direction")
            await press((126, "\u{F700}"), in: window)
            require(state.selection == "regular" && state.writes == 5, "Up moves to the previous enabled option")
            await press((48, "\t"), in: window)
            require(state.outsideFocused, "Tab leaves the group after one stop")
            await press((48, "\u{19}"), modifiers: .shift, in: window)
            require(!state.outsideFocused && state.writes == 5, "Reverse Tab returns without writing the binding")
            state.enabled = false
            try? await Task.sleep(for: .milliseconds(180))
            await press(forward, in: window)
            await press(space, in: window)
            require(state.selection == "regular" && state.writes == 5, "A disabled group does not write the binding")
            print("PASS: \(rightToLeft ? "RTL" : "LTR") Radio Group selects by arrow, skips disabled options and exits with Tab")
            exit(0)
        }
        app.run()
    }

    private static func press(_ key: (UInt16, String), modifiers: NSEvent.ModifierFlags = [], in window: NSWindow) async {
        for type in [NSEvent.EventType.keyDown, .keyUp] {
            if let event = NSEvent.keyEvent(
                with: type, location: .zero, modifierFlags: modifiers,
                timestamp: ProcessInfo.processInfo.systemUptime, windowNumber: window.windowNumber, context: nil,
                characters: key.1, charactersIgnoringModifiers: key.1, isARepeat: false, keyCode: key.0
            ) { NSApp.sendEvent(event) }
        }
        try? await Task.sleep(for: .milliseconds(180))
    }

    private static func require(_ condition: Bool, _ message: String) {
        if !condition {
            print("FAIL: \(message)")
            exit(1)
        }
    }
}
