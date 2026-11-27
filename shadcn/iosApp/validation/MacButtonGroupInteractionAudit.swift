import AppKit
import SwiftUI

@MainActor
private final class ButtonGroupResult: ObservableObject {
    @Published var actions: [String] = []
    @Published var disableNext = false
}

private struct ButtonGroupProbe: View {
    @ObservedObject var result: ButtonGroupResult

    var body: some View {
        HStack(spacing: 32) {
            FoundationButtonGroup {
                icon("Back", systemImage: "arrow.left")
                icon("Next", systemImage: "arrow.right").disabled(result.disableNext)
            }
            FoundationButtonGroup(.vertical) {
                icon("Increase", systemImage: "plus")
                icon("Decrease", systemImage: "minus")
            }
        }
        .foundationTheme(FoundationTheme(preset: .nova))
        .frame(width: 320, height: 160)
    }

    private func icon(_ title: String, systemImage: String) -> some View {
        Button { result.actions.append(title) } label: {
            Image(systemName: systemImage).frame(width: 16, height: 16)
        }
        .buttonStyle(FoundationButtonStyle(.outline, size: .icon))
        .accessibilityLabel(Text(title))
    }
}

@main @MainActor
enum MacButtonGroupInteractionAudit {
    static func main() {
        let app = NSApplication.shared
        app.setActivationPolicy(.regular)
        let result = ButtonGroupResult()
        let window = NSWindow(
            contentRect: NSRect(x: 0, y: 0, width: 320, height: 160),
            styleMask: [.titled, .closable], backing: .buffered, defer: false
        )
        window.contentView = NSHostingView(rootView: ButtonGroupProbe(result: result))
        window.center()
        window.makeKeyAndOrderFront(nil)

        Task { @MainActor in
            app.activate()
            NSRunningApplication.current.activate(options: [.activateAllWindows])
            window.makeKeyAndOrderFront(nil)
            try? await Task.sleep(nanoseconds: 400_000_000)
            require(window.isKeyWindow, "The audit window needs focus")

            await click(121, 80, window: window)
            await click(123, 80, window: window)
            await click(220, 81, window: window)
            await click(220, 79, window: window)
            require(result.actions == ["Back", "Next", "Increase", "Decrease"],
                    "Each side of both seams should dispatch separately. Got \(result.actions)")

            result.disableNext = true
            try? await Task.sleep(nanoseconds: 150_000_000)
            await click(123, 80, window: window)
            await click(121, 80, window: window)
            require(result.actions == ["Back", "Next", "Increase", "Decrease", "Back"],
                    "Disabling Next should leave Back independently activatable. Got \(result.actions)")
            print("PASS: Nova Button Group horizontal and vertical seam interaction")
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
