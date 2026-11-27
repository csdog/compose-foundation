import AppKit
import SwiftUI

@MainActor
private final class CollapsibleResult: ObservableObject {
    @Published var expanded = false
    @Published var isEnabled = true
    private(set) var writes = 0
    var panelActions = 0

    func update(_ next: Bool) {
        writes += 1
        expanded = next
    }
}

private struct CollapsibleProbe: View {
    @ObservedObject var result: CollapsibleResult

    var body: some View {
        VStack(spacing: 8) {
            FoundationCollapsibleTrigger(
                isExpanded: Binding(get: { result.expanded }, set: { result.update($0) })
            ) { expanded in
                Text(expanded ? "Hide details" : "Show details")
                    .frame(width: 120, height: 44)
                    .contentShape(Rectangle())
            }
            .disabled(!result.isEnabled)

            Text("Order status").frame(height: 44)

            FoundationCollapsibleContent(isExpanded: result.expanded) {
                Button { result.panelActions += 1 } label: {
                    Text("Panel action").frame(width: 120, height: 44).contentShape(Rectangle())
                }
            }
        }
        .buttonStyle(PlainButtonStyle())
        .frame(width: 280, height: 200, alignment: .top)
    }
}

@main @MainActor
enum MacCollapsibleInteractionAudit {
    static func main() {
        let app = NSApplication.shared
        app.setActivationPolicy(.regular)
        let result = CollapsibleResult()
        let window = NSWindow(
            contentRect: NSRect(x: 0, y: 0, width: 280, height: 200),
            styleMask: [.titled, .closable], backing: .buffered, defer: false
        )
        window.contentView = NSHostingView(rootView: CollapsibleProbe(result: result))
        window.center()
        window.makeKeyAndOrderFront(nil)

        Task { @MainActor in
            app.activate()
            NSRunningApplication.current.activate(options: [.activateAllWindows])
            window.makeKeyAndOrderFront(nil)
            try? await Task.sleep(for: .milliseconds(400))
            require(window.isKeyWindow, "The audit window needs focus")

            await click(y: 74, in: window)
            require(result.panelActions == 0, "A collapsed panel is unreachable")
            await click(y: 178, in: window)
            require(result.expanded && result.writes == 1, "The trigger opens the panel once")
            await click(y: 74, in: window)
            require(result.panelActions == 1 && result.writes == 1, "A panel button operates independently")
            await click(y: 178, in: window)
            require(!result.expanded && result.writes == 2, "The trigger closes the panel once")
            await click(y: 74, in: window)
            require(result.panelActions == 1, "The closed panel no longer receives clicks")

            result.expanded = true
            result.isEnabled = false
            try? await Task.sleep(for: .milliseconds(180))
            await click(y: 178, in: window)
            require(result.expanded && result.writes == 2, "A disabled trigger does not change externally supplied state")
            await click(y: 74, in: window)
            require(result.panelActions == 2, "Disabling the trigger leaves the open panel actionable")
            print("PASS: Collapsible slots share bound state and respect disabled and hidden hit targets")
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

    private static func click(y: CGFloat, in window: NSWindow) async {
        for type in [NSEvent.EventType.leftMouseDown, .leftMouseUp] {
            if let event = NSEvent.mouseEvent(
                with: type, location: NSPoint(x: 140, y: y), modifierFlags: [],
                timestamp: ProcessInfo.processInfo.systemUptime, windowNumber: window.windowNumber,
                context: nil, eventNumber: 0, clickCount: 1, pressure: type == .leftMouseUp ? 0 : 1
            ) {
                NSApplication.shared.postEvent(event, atStart: false)
            }
            try? await Task.sleep(for: .milliseconds(140))
        }
    }
}
