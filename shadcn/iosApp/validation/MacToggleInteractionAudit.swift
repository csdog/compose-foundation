import AppKit
import SwiftUI

@MainActor
private final class ToggleResult: ObservableObject {
    @Published var bookmark = false
    @Published var selection: Set<String> = []
    @Published var allowsMultiple = false
    @Published var isEnabled = true
    var outsideFocused = false
}

private struct ToggleProbe: View {
    @ObservedObject var result: ToggleResult

    var body: some View {
        VStack(spacing: 16) {
            FoundationToggle(isOn: $result.bookmark, variant: .outline) {
                Text("Bookmark")
            }
            FoundationToggleGroup(
                [
                    FoundationChoice("Bold", value: "bold"),
                    FoundationChoice("Italic", value: "italic"),
                    FoundationChoice("Underline", value: "underline", isEnabled: false),
                ],
                selection: $result.selection,
                variant: .outline,
                allowsMultiple: result.allowsMultiple,
                allowsEmpty: false
            ) { _ in
                Rectangle().frame(width: 16, height: 16).accessibilityHidden(true)
            }
            .frame(width: 136, height: 44)
        }
        .disabled(!result.isEnabled)
        .foundationTheme(FoundationTheme(preset: .nova))
        .frame(width: 360, height: 140)
    }
}

private struct ToggleKeyboardProbe: View {
    @ObservedObject var result: ToggleResult
    @FocusState private var outsideFocused: Bool
    let vertical: Bool
    let rightToLeft: Bool

    var body: some View {
        VStack(spacing: 12) {
            FoundationToggleGroup([
                FoundationChoice("Bold", value: "bold"),
                FoundationChoice("Italic", value: "italic", isEnabled: false),
                FoundationChoice("Underline", value: "underline"),
            ], selection: $result.selection, orientation: vertical ? .vertical : .horizontal,
               allowsMultiple: true)
                .disabled(!result.isEnabled)
            Text("After toggle group")
                .focusable()
                .focused($outsideFocused)
                .onChange(of: outsideFocused) { _, focused in result.outsideFocused = focused }
        }
        .foundationTheme(FoundationTheme(preset: .nova))
        .environment(\.layoutDirection, rightToLeft ? .rightToLeft : .leftToRight)
        .frame(width: 360, height: 210)
    }
}

@main @MainActor
enum MacToggleInteractionAudit {
    static func main() {
        let app = NSApplication.shared
        app.setActivationPolicy(.regular)
        let result = ToggleResult()
        let keyboard = CommandLine.arguments.contains("--keyboard")
        let vertical = CommandLine.arguments.contains("--vertical")
        let rightToLeft = CommandLine.arguments.contains("--rtl")
        let window = NSWindow(
            contentRect: NSRect(x: 0, y: 0, width: 360, height: keyboard ? 210 : 140),
            styleMask: [.titled, .closable], backing: .buffered, defer: false
        )
        if keyboard {
            window.contentView = NSHostingView(rootView: ToggleKeyboardProbe(result: result, vertical: vertical,
                                                                           rightToLeft: rightToLeft))
        } else {
            window.contentView = NSHostingView(rootView: ToggleProbe(result: result))
        }
        window.center()
        window.makeKeyAndOrderFront(nil)

        Task { @MainActor in
            app.activate(ignoringOtherApps: true)
            NSRunningApplication.current.activate(options: [.activateAllWindows])
            window.makeKeyAndOrderFront(nil)
            try? await Task.sleep(nanoseconds: 400_000_000)
            require(window.isKeyWindow, "The audit window needs focus")

            if keyboard {
                await verifyKeyboard(result, in: window, vertical: vertical, rightToLeft: rightToLeft)
                print("PASS: Nova Toggle Group keyboard navigation, vertical=\(vertical), RTL=\(rightToLeft)")
                exit(0)
            }

            await click(180, 100, window: window)
            require(result.bookmark, "Clicking Bookmark selects it")
            await click(180, 100, window: window)
            require(!result.bookmark, "Clicking Bookmark again clears it")

            await click(134, 40, window: window)
            require(result.selection == ["bold"], "Clicking Bold selects it")
            await click(134, 40, window: window)
            require(result.selection == ["bold"], "Required selection keeps the final item")
            await click(180, 40, window: window)
            require(result.selection == ["italic"], "Single selection replaces Bold")
            await click(224, 40, window: window)
            require(result.selection == ["italic"], "Disabled Underline leaves the binding alone")

            result.allowsMultiple = true
            try? await Task.sleep(nanoseconds: 180_000_000)
            await click(134, 40, window: window)
            require(result.selection == ["bold", "italic"], "Multiple selection keeps Italic")
            await click(180, 40, window: window)
            require(result.selection == ["bold"], "Removing Italic preserves Bold")

            result.isEnabled = false
            try? await Task.sleep(nanoseconds: 180_000_000)
            await click(180, 100, window: window)
            await click(180, 40, window: window)
            require(!result.bookmark && result.selection == ["bold"], "Disabled controls never write")
            print("PASS: Nova Toggle and Toggle Group mouse selection")
            exit(0)
        }
        app.run()
    }

    private static func verifyKeyboard(_ result: ToggleResult, in window: NSWindow, vertical: Bool, rightToLeft: Bool) async {
        let forward: (UInt16, String) = vertical ? (125, "\u{F701}") : rightToLeft ? (123, "\u{F702}") : (124, "\u{F703}")
        let space: (UInt16, String) = (49, " ")
        await press(forward, in: window)
        require(result.selection.isEmpty, "An arrow should move focus without pressing an item")
        await press(space, in: window)
        require(result.selection == ["underline"], "The first arrow skips disabled Italic before activation")
        await press(forward, in: window)
        await press((36, "\r"), in: window)
        require(result.selection == ["bold", "underline"], "Focus wraps and Return presses Bold")
        await press((119, "\u{F72B}"), in: window)
        await press(space, in: window)
        require(result.selection == ["bold"], "End moves to Underline and Space releases it")
        await press((115, "\u{F729}"), in: window)
        await press(space, in: window)
        require(result.selection.isEmpty, "Home moves to Bold and Space releases it")
        await press((48, "\t"), in: window)
        require(result.outsideFocused, "Tab exits the group after one tab stop")
        await press((48, "\u{19}"), modifiers: .shift, in: window)
        require(!result.outsideFocused && result.selection.isEmpty, "Shift-Tab returns without pressing an item")
        result.isEnabled = false
        try? await Task.sleep(for: .milliseconds(180))
        await press(forward, in: window)
        await press(space, in: window)
        require(result.selection.isEmpty, "A disabled group leaves the selection alone")
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
        guard condition else {
            print("FAIL: \(message)")
            exit(1)
        }
    }

    private static func click(_ x: CGFloat, _ y: CGFloat, window: NSWindow) async {
        for type in [NSEvent.EventType.leftMouseDown, .leftMouseUp] {
            if let event = NSEvent.mouseEvent(
                with: type, location: NSPoint(x: x, y: y), modifierFlags: [],
                timestamp: ProcessInfo.processInfo.systemUptime, windowNumber: window.windowNumber,
                context: nil, eventNumber: 0, clickCount: 1, pressure: type == .leftMouseUp ? 0 : 1
            ) {
                NSApplication.shared.postEvent(event, atStart: false)
            }
            try? await Task.sleep(nanoseconds: 100_000_000)
        }
    }
}
