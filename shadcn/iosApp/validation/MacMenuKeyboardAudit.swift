import AppKit
import SwiftUI

@MainActor
private final class MenuResult: ObservableObject {
    @Published var actions: [String] = []
    @Published var isEnabled = true
}

@available(macOS 14, *)
private struct MenuProbe: View {
    @ObservedObject var result: MenuResult
    let navigation: Bool
    let rightToLeft: Bool

    var body: some View {
        let groups: [FoundationMenuGroup] = [
            .init(id: "first", title: "File", actions: [
                action("New"),
                action("Restricted", enabled: false),
                action("Save"),
            ]),
            .init(id: "disabled", title: "Unavailable", isEnabled: false, actions: []),
            .init(id: "last", title: "Edit", actions: [action("Copy"), action("Paste")]),
        ]
        Group {
            if navigation {
                FoundationNavigationMenu(groups: groups)
            } else {
                FoundationMenubar(groups: groups)
            }
        }
        .disabled(!result.isEnabled)
        .foundationTheme(navigation ? FoundationTheme() : FoundationTheme(preset: .nova))
        .environment(\.layoutDirection, rightToLeft ? .rightToLeft : .leftToRight)
        .frame(width: 400, height: 240)
    }

    private func action(_ title: String, enabled: Bool = true) -> FoundationMenuAction {
        FoundationMenuAction(id: title, title: title, isEnabled: enabled) {
            result.actions.append(title)
        }
    }
}

@available(macOS 14, *)
@main
struct MacMenuKeyboardAudit {
    @MainActor
    static func main() {
        let app = NSApplication.shared
        app.setActivationPolicy(.regular)
        let navigation = CommandLine.arguments.contains("--navigation")
        let rightToLeft = CommandLine.arguments.contains("--rtl")
        let forward: (UInt16, String) = rightToLeft ? (123, "\u{F702}") : (124, "\u{F703}")
        let backward: (UInt16, String) = rightToLeft ? (124, "\u{F703}") : (123, "\u{F702}")
        let actionsOnly = CommandLine.arguments.contains("--actions-only")
        let result = MenuResult()
        let window = NSWindow(
            contentRect: NSRect(x: 0, y: 0, width: 400, height: 240),
            styleMask: [.titled, .closable], backing: .buffered, defer: false
        )
        window.title = navigation ? "Navigation Menu Keyboard Audit" : "Menubar Keyboard Audit"
        window.contentView = NSHostingView(rootView: MenuProbe(result: result, navigation: navigation,
                                                               rightToLeft: rightToLeft))
        window.center()
        app.activate(ignoringOtherApps: true)
        window.makeKeyAndOrderFront(nil)

        Task { @MainActor in
            try? await Task.sleep(for: .milliseconds(240))
            app.activate()
            NSRunningApplication.current.activate(options: [.activateAllWindows])
            window.makeKeyAndOrderFront(nil)
            guard await waitUntil({ window.isKeyWindow }) else {
                fail("Parent window did not become active", result)
            }

            // Start with the first trigger, even if SwiftUI already focused the bar.
            press(115, "\u{F729}", app)
            try? await Task.sleep(for: .milliseconds(100))
            press(125, "\u{F701}", app)
            try? await Task.sleep(for: .milliseconds(350))
            if window.isKeyWindow {
                press(48, "\t", app)
                try? await Task.sleep(for: .milliseconds(100))
                press(115, "\u{F729}", app)
                try? await Task.sleep(for: .milliseconds(100))
                press(125, "\u{F701}", app)
                try? await Task.sleep(for: .milliseconds(350))
            }
            guard app.keyWindow is NSPanel else { fail("Menu did not become the key window", result) }
            press(125, "\u{F701}", app)
            try? await Task.sleep(for: .milliseconds(160))
            press(36, "\r", app)
            guard await waitUntil({ result.actions == ["Save"] }) else {
                fail("Down and Return did not choose Save", result)
            }
            if actionsOnly {
                print("PASS \(navigation ? "Navigation Menu" : "Menubar"): Down skips the disabled item and Return executes Save")
                exit(0)
            }
            guard await waitUntil({ window.isKeyWindow }) else {
                fail("Parent did not regain the key window after selection", result)
            }

            // Down reopens File without another Tab. The forward arrow skips the disabled
            // trigger. Escape restores Edit so Down and Return choose Copy.
            press(125, "\u{F701}", app)
            guard await waitUntil({ app.keyWindow is NSPanel }) else { fail("Could not reopen File", result) }
            try? await Task.sleep(for: .milliseconds(160))
            press(forward.0, forward.1, app)
            try? await Task.sleep(for: .milliseconds(330))
            press(53, "\u{1B}", app)
            guard await waitUntil({ window.isKeyWindow }) else { fail("Escape did not close the menu", result) }
            guard result.actions == ["Save"] else { fail("Escape invoked an action", result) }
            press(125, "\u{F701}", app)
            guard await waitUntil({ app.keyWindow is NSPanel }) else { fail("Could not reopen Edit", result) }
            try? await Task.sleep(for: .milliseconds(160))
            press(36, "\r", app)
            guard await waitUntil({ result.actions == ["Save", "Copy"] }) else {
                fail("Escape did not return focus to the switched menu", result)
            }
            guard await waitUntil({ window.isKeyWindow }) else { fail("Selection did not close Edit", result) }

            press(125, "\u{F701}", app)
            guard await waitUntil({ app.keyWindow is NSPanel }) else { fail("Could not reopen Edit after selection", result) }
            try? await Task.sleep(for: .milliseconds(160))
            press(backward.0, backward.1, app)
            try? await Task.sleep(for: .milliseconds(330))
            press(36, "\r", app)
            guard await waitUntil({ result.actions == ["Save", "Copy", "New"] }) else {
                fail("Return did not execute the first item in the switched menu", result)
            }
            guard await waitUntil({ window.isKeyWindow }) else { fail("Selection did not close File", result) }
            press(125, "\u{F701}", app)
            guard await waitUntil({ app.keyWindow is NSPanel }) else { fail("Could not reopen File", result) }
            result.isEnabled = false
            guard await waitUntil({ window.isKeyWindow }) else { fail("Disabling the bar did not close File", result) }
            press(125, "\u{F701}", app)
            press(36, "\r", app)
            try? await Task.sleep(for: .milliseconds(350))
            guard window.isKeyWindow, result.actions == ["Save", "Copy", "New"] else {
                fail("Keyboard input opened a disabled bar or executed an action", result)
            }
            let direction = rightToLeft ? "RTL" : "LTR"
            print("PASS \(navigation ? "Navigation Menu" : "Menubar") \(direction): skips disabled items, executes across groups, restores focus, and closes when disabled")
            exit(0)
        }
        app.run()
    }

    @MainActor
    private static func fail(_ message: String, _ result: MenuResult) -> Never {
        print("FAIL \(message). Actions: \(result.actions)")
        exit(1)
    }

    @MainActor
    private static func waitUntil(_ condition: () -> Bool) async -> Bool {
        for _ in 0..<140 {
            if condition() { return true }
            try? await Task.sleep(for: .milliseconds(20))
        }
        return condition()
    }

    @MainActor
    private static func press(_ code: UInt16, _ characters: String, _ app: NSApplication) {
        guard let window = app.keyWindow else { return }
        for type in [NSEvent.EventType.keyDown, .keyUp] {
            if let event = NSEvent.keyEvent(
                with: type, location: .zero, modifierFlags: [],
                timestamp: ProcessInfo.processInfo.systemUptime,
                windowNumber: window.windowNumber, context: nil,
                characters: characters, charactersIgnoringModifiers: characters,
                isARepeat: false, keyCode: code
            ) {
                app.sendEvent(event)
            }
        }
    }
}
