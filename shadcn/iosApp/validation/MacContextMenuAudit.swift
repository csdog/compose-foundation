import AppKit
import SwiftUI

@MainActor
private final class ContextMenuProbe: NSObject {
    var reloaded = 0
    var opened = false
    var disabledForward = false

    @objc func track(_ notification: Notification) {
        guard let menu = notification.object as? NSMenu,
              let reload = menu.items.firstIndex(where: { $0.title == "Reload" }) else { return }
        opened = true
        disabledForward = menu.items.contains { $0.title == "Forward" && !$0.isEnabled }
        menu.performActionForItem(at: reload)
        menu.cancelTracking()
    }
}

@main @MainActor
enum MacContextMenuAudit {
    static func main() {
        let app = NSApplication.shared
        let probe = ContextMenuProbe()
        let host = NSHostingView(rootView: Text("Right click here")
            .modifier(FoundationContextMenu(triggerStyle: .outline) {
                Button("Back") {}
                Button("Forward") {}.disabled(true)
                Button("Reload") { probe.reloaded += 1 }
            })
            .frame(width: 440, height: 300)
            .foundationTheme(FoundationTheme(preset: .nova)))
        let window = NSWindow(contentRect: NSRect(x: 0, y: 0, width: 440, height: 300),
                              styleMask: [.titled, .closable], backing: .buffered, defer: false)
        window.contentView = host
        app.setActivationPolicy(.regular)
        window.center()
        window.makeKeyAndOrderFront(nil)
        NotificationCenter.default.addObserver(probe, selector: #selector(ContextMenuProbe.track(_:)),
                                               name: NSMenu.didBeginTrackingNotification, object: nil)

        Task { @MainActor in
            app.activate()
            window.makeKeyAndOrderFront(nil)
            try? await Task.sleep(for: .milliseconds(400))
            guard window.isKeyWindow else { fail("The audit window needs focus") }
            let point = NSPoint(x: 104, y: 118)
            for type in [NSEvent.EventType.rightMouseDown, .rightMouseUp] {
                guard let event = NSEvent.mouseEvent(
                    with: type, location: point, modifierFlags: [],
                    timestamp: ProcessInfo.processInfo.systemUptime, windowNumber: window.windowNumber, context: nil,
                    eventNumber: 0, clickCount: 1, pressure: type == .rightMouseDown ? 1 : 0
                ) else { fail("Could not create the AppKit right-click event") }
                app.postEvent(event, atStart: false)
            }
            try? await Task.sleep(for: .milliseconds(800))
            guard probe.opened else { fail("The outline did not open a native menu after right-click") }
            guard probe.disabledForward else { fail("Forward was not marked disabled in the native menu") }
            guard probe.reloaded == 1 else { fail("The Reload menu action did not execute exactly once") }
            print("PASS: Context Menu opens from blank outline space and exposes native action state")
            exit(0)
        }
        app.run()
    }

    private static func fail(_ message: String) -> Never {
        print("FAIL: \(message)")
        exit(1)
    }
}
