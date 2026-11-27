import AppKit
import SwiftUI

@MainActor
private final class LabelResult: ObservableObject {
    @Published var accepted = false
    @Published var isEnabled = true
}

private struct LabelProbe: View {
    @ObservedObject var result: LabelResult

    var body: some View {
        Toggle(isOn: $result.accepted) {
            FoundationLabel("Accept terms and conditions")
        }
        .toggleStyle(FoundationCheckbox())
        .disabled(!result.isEnabled)
        .foundationTheme(FoundationTheme(preset: .nova))
        .padding(.leading, 24)
        .frame(width: 320, height: 100, alignment: .leading)
    }
}

@main @MainActor
enum MacLabelInteractionAudit {
    static func main() {
        let app = NSApplication.shared
        app.setActivationPolicy(.regular)
        let result = LabelResult()
        let window = NSWindow(
            contentRect: NSRect(x: 0, y: 0, width: 320, height: 100),
            styleMask: [.titled, .closable], backing: .buffered, defer: false
        )
        window.contentView = NSHostingView(rootView: LabelProbe(result: result))
        window.center()
        window.makeKeyAndOrderFront(nil)

        Task { @MainActor in
            app.activate()
            NSRunningApplication.current.activate(options: [.activateAllWindows])
            window.makeKeyAndOrderFront(nil)
            try? await Task.sleep(nanoseconds: 400_000_000)
            require(window.isKeyWindow, "The audit window needs focus")
            await click(x: 100, window: window)
            require(result.accepted, "The label should check the box")
            await click(x: 32, window: window)
            require(!result.accepted, "The box and label should update the same binding")
            result.isEnabled = false
            try? await Task.sleep(nanoseconds: 150_000_000)
            await click(x: 100, window: window)
            require(!result.accepted, "A disabled checkbox should ignore the label click")
            print("PASS: Nova Label and Checkbox share one mouse target")
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

    private static func click(x: CGFloat, window: NSWindow) async {
        for type in [NSEvent.EventType.leftMouseDown, .leftMouseUp] {
            if let event = NSEvent.mouseEvent(
                with: type, location: NSPoint(x: x, y: 50), modifierFlags: [],
                timestamp: ProcessInfo.processInfo.systemUptime, windowNumber: window.windowNumber,
                context: nil, eventNumber: 0, clickCount: 1, pressure: type == .leftMouseUp ? 0 : 1
            ) {
                NSApplication.shared.postEvent(event, atStart: false)
            }
            try? await Task.sleep(nanoseconds: 100_000_000)
        }
    }
}
