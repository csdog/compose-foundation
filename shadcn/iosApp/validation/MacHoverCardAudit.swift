import AppKit
import SwiftUI

@MainActor
private final class HoverCardResult: ObservableObject {
    @Published var presented = false
}

private struct HoverCardProbe: View {
    @ObservedObject var state: HoverCardResult

    var body: some View {
        Text("Hover Here")
            .frame(width: 100, height: 44)
            .contentShape(Rectangle())
            .modifier(FoundationHoverCard(isPresented: $state.presented) {
                Text("Profile card").frame(height: 60)
            })
            .foundationTheme(FoundationTheme(preset: .nova))
            .frame(width: 420, height: 330)
    }
}

@main @MainActor
enum MacHoverCardAudit {
    static func main() {
        let app = NSApplication.shared
        app.setActivationPolicy(.regular)
        let state = HoverCardResult()
        let window = NSWindow(contentRect: NSRect(x: 0, y: 0, width: 420, height: 330),
                              styleMask: [.titled, .closable], backing: .buffered, defer: false)
        window.acceptsMouseMovedEvents = true
        window.contentView = NSHostingView(rootView: HoverCardProbe(state: state))
        window.center()
        window.makeKeyAndOrderFront(nil)

        Task { @MainActor in
            app.activate()
            NSRunningApplication.current.activate(options: [.activateAllWindows])
            window.makeKeyAndOrderFront(nil)
            let outside = window.convertPoint(toScreen: NSPoint(x: 25, y: 25))
            let trigger = window.convertPoint(toScreen: NSPoint(x: 210, y: 165))
            move(to: outside)
            try? await Task.sleep(for: .milliseconds(380))
            require(window.isKeyWindow && !state.presented, "The parent starts key with no card")

            move(to: trigger)
            guard await waitUntil({ state.presented && window.childWindows?.first != nil }),
                  let card = window.childWindows?.first else { fail("Hovering over the trigger did not open a card") }
            require(window.isKeyWindow && !card.isKeyWindow, "Pointer hover must keep the parent key")

            move(to: NSPoint(x: card.frame.midX, y: card.frame.midY))
            try? await Task.sleep(for: .milliseconds(300))
            require(state.presented && card.isVisible && window.isKeyWindow, "The card remains open under its pointer")

            move(to: outside)
            require(await waitUntil({ !state.presented && window.childWindows?.isEmpty != false }),
                    "Moving out of both regions dismisses the card")
            print("PASS: Hover Card opens on pointer entry, remains open across the gap, and leaves the parent key")
            exit(0)
        }
        app.run()
    }

    private static func move(to point: NSPoint) {
        guard let top = NSScreen.screens.first?.frame.maxY else { fail("A display is required") }
        let location = CGPoint(x: point.x, y: top - point.y)
        CGWarpMouseCursorPosition(location)
        CGEvent(mouseEventSource: nil, mouseType: .mouseMoved, mouseCursorPosition: location, mouseButton: .left)?
            .post(tap: .cghidEventTap)
    }

    private static func waitUntil(_ condition: () -> Bool) async -> Bool {
        for _ in 0..<30 {
            if condition() { return true }
            try? await Task.sleep(for: .milliseconds(40))
        }
        return condition()
    }

    private static func require(_ condition: Bool, _ message: String) {
        guard condition else { fail(message) }
    }

    private static func fail(_ message: String) -> Never {
        print("FAIL: \(message)")
        exit(1)
    }
}
