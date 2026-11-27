import AppKit
import SwiftUI

@MainActor
private final class EmptyResult: ObservableObject {
    @Published var importDisabled = false
    var actions: [String] = []
}

private struct EmptyProbe: View {
    @ObservedObject var result: EmptyResult

    var body: some View {
        FoundationEmpty("No Projects Yet", variant: .outline) {
            HStack(spacing: 8) {
                action("Create")
                action("Import").disabled(result.importDisabled)
            }
        }
        .foundationTheme(FoundationTheme(preset: .nova))
        .frame(width: 300, height: 220)
    }

    private func action(_ title: String) -> some View {
        Button { result.actions.append(title) } label: {
            Text(title).frame(width: 80, height: 44).contentShape(Rectangle())
        }
        .buttonStyle(PlainButtonStyle())
    }
}

@main @MainActor
enum MacEmptyInteractionAudit {
    static func main() {
        let application = NSApplication.shared
        application.setActivationPolicy(.regular)
        let result = EmptyResult()
        let window = NSWindow(
            contentRect: NSRect(x: 0, y: 0, width: 300, height: 220),
            styleMask: [.titled, .closable], backing: .buffered, defer: false
        )
        window.contentView = NSHostingView(rootView: EmptyProbe(result: result))
        window.center()
        window.makeKeyAndOrderFront(nil)

        Task { @MainActor in
            application.activate()
            NSRunningApplication.current.activate(options: [.activateAllWindows])
            window.makeKeyAndOrderFront(nil)
            try? await Task.sleep(for: .milliseconds(400))
            require(window.isKeyWindow, "The audit window needs focus")

            await click(x: 106, in: window)
            await click(x: 194, in: window)
            require(result.actions == ["Create", "Import"], "The outline leaves both actions independently clickable")

            result.importDisabled = true
            try? await Task.sleep(for: .milliseconds(180))
            await click(x: 194, in: window)
            await click(x: 106, in: window)
            require(result.actions == ["Create", "Import", "Create"], "Disabling import leaves create usable")
            print("PASS: Empty outline preserves independent action hit targets and disabled behavior")
            exit(0)
        }
        application.run()
    }

    private static func require(_ condition: Bool, _ message: String) {
        guard condition else {
            print("FAIL: \(message)")
            exit(1)
        }
    }

    private static func click(x: CGFloat, in window: NSWindow) async {
        for type in [NSEvent.EventType.leftMouseDown, .leftMouseUp] {
            if let event = NSEvent.mouseEvent(
                with: type, location: NSPoint(x: x, y: 94), modifierFlags: [],
                timestamp: ProcessInfo.processInfo.systemUptime, windowNumber: window.windowNumber,
                context: nil, eventNumber: 0, clickCount: 1, pressure: type == .leftMouseUp ? 0 : 1
            ) {
                NSApplication.shared.postEvent(event, atStart: false)
            }
            try? await Task.sleep(for: .milliseconds(140))
        }
    }
}
