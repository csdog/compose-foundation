import AppKit
import SwiftUI

@MainActor
private final class ToastAuditState {
    let queue = FoundationToastState()
    var undoCount = 0
}

private struct ToastAuditFixture: View {
    let queue: FoundationToastState

    var body: some View {
        Color.clear
            .frame(width: 540, height: 340)
            .modifier(FoundationToaster(state: queue))
            .transaction { $0.disablesAnimations = true }
            .foundationTheme(FoundationTheme(preset: .nova))
    }
}

@main @MainActor
enum MacToastQueueAudit {
    static func main() {
        let app = NSApplication.shared
        let state = ToastAuditState()
        let queue = state.queue
        let first = queue.show("First notice", durationMilliseconds: 0)
        let second = queue.show("Second notice", variant: .info, durationMilliseconds: 0)
        let third = queue.show("Third notice", variant: .warning, durationMilliseconds: 0)
        let saved = queue.show("Saved", variant: .success, durationMilliseconds: 0,
                               action: FoundationToastAction("Undo") { state.undoCount += 1 })
        require(queue.visibleMessages(limit: 3).map(\.id) == [second, third, saved], "The queue shows the latest three")

        let window = NSWindow(contentRect: NSRect(x: 0, y: 0, width: 540, height: 340),
                              styleMask: [.titled, .closable], backing: .buffered, defer: false)
        window.contentView = NSHostingView(rootView: ToastAuditFixture(queue: queue))
        app.setActivationPolicy(.regular)
        window.center()
        window.makeKeyAndOrderFront(nil)

        Task { @MainActor in
            app.activate()
            NSRunningApplication.current.activate(options: [.activateAllWindows])
            window.makeKeyAndOrderFront(nil)
            try? await Task.sleep(for: .milliseconds(400))
            require(window.isKeyWindow, "The audit window needs focus")

            await click(x: 434, y: 46, in: window)
            require(state.undoCount == 1, "The visible Undo control must execute once")
            require(queue.visibleMessages(limit: 3).map(\.id) == [first, second, third],
                    "Undo must remove its own toast and backfill the oldest one")

            await click(x: 486, y: 46, in: window)
            require(queue.messages.map(\.id) == [first, second], "The close control must dismiss only its toast")

            let timed = queue.show("Timed notification", durationMilliseconds: 150)
            require(queue.messages.last?.id == timed, "The timed notification must enter the queue")
            try? await Task.sleep(for: .milliseconds(420))
            require(queue.messages.map(\.id) == [first, second], "Expiry must leave other notifications in place")

            queue.show("Canceled timer", durationMilliseconds: 150)
            queue.dismissAll()
            let replacement = queue.show("Replacement", durationMilliseconds: 0)
            try? await Task.sleep(for: .milliseconds(420))
            require(queue.messages.map(\.id) == [replacement], "A timer from a cleared queue must not affect later entries")
            print("PASS: Toast Undo and close clicks, queue backfill, expiry, and timer cancellation")
            exit(0)
        }
        app.run()
    }

    private static func click(x: CGFloat, y: CGFloat, in window: NSWindow) async {
        for type in [NSEvent.EventType.leftMouseDown, .leftMouseUp] {
            guard let event = NSEvent.mouseEvent(
                with: type, location: NSPoint(x: x, y: y), modifierFlags: [],
                timestamp: ProcessInfo.processInfo.systemUptime, windowNumber: window.windowNumber,
                context: nil, eventNumber: 0, clickCount: 1, pressure: type == .leftMouseUp ? 0 : 1
            ) else { continue }
            NSApplication.shared.postEvent(event, atStart: false)
            try? await Task.sleep(for: .milliseconds(125))
        }
    }

    private static func require(_ condition: Bool, _ message: String) {
        if !condition {
            print("FAIL: \(message)")
            exit(1)
        }
    }
}
