import AppKit
import SwiftUI

@MainActor
private final class ItemResult: ObservableObject {
    @Published var approveDisabled = false
    @Published var rightToLeft = false
    var actions: [String] = []
}

private struct ItemProbe: View {
    @ObservedObject var result: ItemResult

    var body: some View {
        FoundationItem("Report", description: "Ready for review", variant: .outline) {
            Image(systemName: "doc").accessibilityHidden(true)
        } actions: {
            HStack(spacing: 8) {
                action("Open")
                action("Approve").disabled(result.approveDisabled)
            }
        }
        .foundationTheme(FoundationTheme(preset: .nova))
        .environment(\.layoutDirection, result.rightToLeft ? .rightToLeft : .leftToRight)
        .frame(width: 320, height: 140)
    }

    private func action(_ name: String) -> some View {
        Button { result.actions.append(name) } label: {
            Text(name).frame(width: 64, height: 44).contentShape(Rectangle())
        }
        .buttonStyle(PlainButtonStyle())
    }
}

@main @MainActor
enum MacItemInteractionAudit {
    static func main() {
        let application = NSApplication.shared
        application.setActivationPolicy(.regular)
        let result = ItemResult()
        let window = NSWindow(
            contentRect: NSRect(x: 0, y: 0, width: 320, height: 140),
            styleMask: [.titled, .closable], backing: .buffered, defer: false
        )
        window.contentView = NSHostingView(rootView: ItemProbe(result: result))
        window.center()
        window.makeKeyAndOrderFront(nil)

        Task { @MainActor in
            application.activate()
            NSRunningApplication.current.activate(options: [.activateAllWindows])
            window.makeKeyAndOrderFront(nil)
            try? await Task.sleep(for: .milliseconds(400))
            require(window.isKeyWindow, "The audit window needs focus")

            await click(x: 203, in: window)
            await click(x: 275, in: window)
            require(result.actions == ["Open", "Approve"], "The two actions respond independently")

            result.approveDisabled = true
            result.rightToLeft = true
            try? await Task.sleep(for: .milliseconds(180))
            await click(x: 45, in: window)
            await click(x: 117, in: window)
            require(result.actions == ["Open", "Approve", "Open"], "RTL mirrors the controls and preserves disabled behavior")
            print("PASS: Item actions remain independent, honor disabling, and mirror in RTL")
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
                with: type, location: NSPoint(x: x, y: 70), modifierFlags: [],
                timestamp: ProcessInfo.processInfo.systemUptime, windowNumber: window.windowNumber,
                context: nil, eventNumber: 0, clickCount: 1, pressure: type == .leftMouseUp ? 0 : 1
            ) {
                NSApplication.shared.postEvent(event, atStart: false)
            }
            try? await Task.sleep(for: .milliseconds(140))
        }
    }
}
