import AppKit
import SwiftUI

@MainActor
private final class ToggleResult: ObservableObject {
    @Published var bookmark = false
    @Published var selection: Set<String> = []
    @Published var allowsMultiple = false
    @Published var isEnabled = true
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

@main @MainActor
enum MacToggleInteractionAudit {
    static func main() {
        let app = NSApplication.shared
        app.setActivationPolicy(.regular)
        let result = ToggleResult()
        let window = NSWindow(
            contentRect: NSRect(x: 0, y: 0, width: 360, height: 140),
            styleMask: [.titled, .closable], backing: .buffered, defer: false
        )
        window.contentView = NSHostingView(rootView: ToggleProbe(result: result))
        window.center()
        window.makeKeyAndOrderFront(nil)

        Task { @MainActor in
            app.activate(ignoringOtherApps: true)
            NSRunningApplication.current.activate(options: [.activateAllWindows])
            window.makeKeyAndOrderFront(nil)
            try? await Task.sleep(nanoseconds: 400_000_000)
            require(window.isKeyWindow, "The audit window needs focus")

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
