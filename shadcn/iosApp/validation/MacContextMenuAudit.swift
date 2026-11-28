import AppKit
import SwiftUI

@MainActor
private final class ContextMenuProbe: NSObject {
    var reloaded = 0
    var opened = false
    var disabledForward = false
    var windowNumber = 0

    @objc func track(_ notification: Notification) {
        guard let menu = notification.object as? NSMenu,
              menu.items.contains(where: { $0.title == "Reload" }) else { return }
        opened = true
        disabledForward = menu.items.contains { $0.title == "Forward" && !$0.isEnabled }
        let timeout = Timer(timeInterval: 1.5, target: self, selector: #selector(cancel(_:)),
                            userInfo: menu, repeats: false)
        RunLoop.main.add(timeout, forMode: .eventTracking)
        for (code, key): (UInt16, String) in [(125, "\u{F701}"), (125, "\u{F701}"), (36, "\r")] {
            for type in [NSEvent.EventType.keyDown, .keyUp] {
                if let event = NSEvent.keyEvent(
                    with: type, location: .zero, modifierFlags: [],
                    timestamp: ProcessInfo.processInfo.systemUptime, windowNumber: windowNumber, context: nil,
                    characters: key, charactersIgnoringModifiers: key, isARepeat: false, keyCode: code
                ) { NSApp.postEvent(event, atStart: false) }
            }
        }
    }

    @objc func cancel(_ timer: Timer) {
        (timer.userInfo as? NSMenu)?.cancelTracking()
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
        probe.windowNumber = window.windowNumber
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
            print("PASS: Context Menu opens from blank outline space and skips a disabled item with Down and Return")
            exit(0)
        }
        app.run()
    }

    private static func fail(_ message: String) -> Never {
        print("FAIL: \(message)")
        exit(1)
    }
}
