import AppKit
import SwiftUI

@MainActor
private final class DropdownMenuAuditState: ObservableObject {
    @Published var title = "Open"
    @Published var isEnabled = true
    @Published var action = ""
    var opened = 0
    var closed = 0
}

private struct DropdownMenuAuditFixture: View {
    @ObservedObject var state: DropdownMenuAuditState

    var body: some View {
        FoundationDropdownMenu {
            Section(header: Text("My Account")) {
                Button("Profile") { state.action = "Profile" }
                Button("API") { state.action = "API" }.disabled(true)
                Button("Settings") { state.action = "Settings" }
            }
            Menu("Invite users") {
                Button("Email") {}
            }
        } trigger: {
            Text(state.title)
        }
        .disabled(!state.isEnabled)
        .frame(width: 360, height: 180)
        .foundationTheme(FoundationTheme(preset: .nova))
    }
}

@main @MainActor
enum MacDropdownMenuAudit {
    static func main() {
        let app = NSApplication.shared
        let state = DropdownMenuAuditState()
        let host = NSHostingView(rootView: DropdownMenuAuditFixture(state: state))
        let window = NSWindow(contentRect: NSRect(x: 0, y: 0, width: 360, height: 180),
                              styleMask: [.titled, .closable], backing: .buffered, defer: false)
        window.contentView = host
        window.autorecalculatesKeyViewLoop = true
        app.setActivationPolicy(.regular)
        window.center()
        window.makeKeyAndOrderFront(nil)

        Task { @MainActor in
            app.activate()
            NSRunningApplication.current.activate(options: [.activateAllWindows])
            window.makeKeyAndOrderFront(nil)
            let initial = await render(host)
            guard window.isKeyWindow else { fail("The audit window needs focus") }
            state.title = "Account actions"
            let renamed = await render(host)
            guard renamed != initial else { fail("The caller's label change did not reach the trigger") }
            state.isEnabled = false
            let disabled = await render(host)
            guard disabled != renamed else { fail("The disabled state did not change the trigger") }
            state.isEnabled = true
            let reenabled = await render(host)
            guard reenabled == renamed else { fail("The trigger did not return to its enabled appearance") }
            print("PASS: Dropdown Menu renders live caller labels and reversible disabled appearance")
            let start = NotificationCenter.default.addObserver(forName: NSMenu.didBeginTrackingNotification, object: nil, queue: nil) { _ in
                print("TRACE: menu began tracking")
                MainActor.assumeIsolated { state.opened += 1 }
            }
            let end = NotificationCenter.default.addObserver(forName: NSMenu.didEndTrackingNotification, object: nil, queue: nil) { _ in
                MainActor.assumeIsolated { state.closed += 1 }
            }
            defer {
                NotificationCenter.default.removeObserver(start)
                NotificationCenter.default.removeObserver(end)
            }
            print("TRACE: CG input access \(CGPreflightPostEventAccess()), key window \(window.isKeyWindow)")
            window.selectNextKeyView(nil)
            print("TRACE: focused responder \(String(describing: window.firstResponder))")
            await press(49)
            print("TRACE: after Space starts=\(state.opened) ends=\(state.closed)")
            if state.opened == 0 {
                let center = window.convertPoint(toScreen: NSPoint(x: 180, y: 90))
                let point = CGPoint(x: center.x, y: CGDisplayBounds(CGMainDisplayID()).height - center.y)
                for type in [CGEventType.leftMouseDown, .leftMouseUp] {
                    CGEvent(mouseEventSource: nil, mouseType: type, mouseCursorPosition: point, mouseButton: .left)?
                        .postToPid(getpid())
                }
                try? await Task.sleep(for: .milliseconds(350))
            }
            guard state.opened == 1 && state.closed == 0 else { fail("Could not open the native Menu with synthetic input") }
            await press(125)
            await press(125)
            await press(36)
            guard state.closed == 1, state.action == "Settings" else {
                fail("Down and Return did not select Settings. Actual action: \(state.action)")
            }
            await press(49)
            guard state.opened == 2 else { fail("Keyboard did not reopen Menu after selection") }
            await press(53)
            guard state.closed == 2, state.action == "Settings" else { fail("Escape did not dismiss without an action") }
            await press(49)
            guard state.opened == 3 else { fail("Keyboard did not reopen Menu after Escape") }
            await press(53)
            print("PASS: Native Dropdown Menu skips disabled actions and restores keyboard activation")
            exit(0)
        }
        app.run()
    }

    private static func press(_ key: CGKeyCode) async {
        for down in [true, false] {
            CGEvent(keyboardEventSource: nil, virtualKey: key, keyDown: down)?.postToPid(getpid())
        }
        try? await Task.sleep(for: .milliseconds(400))
    }

    private static func render(_ view: NSView) async -> Data {
        try? await Task.sleep(for: .milliseconds(250))
        view.layoutSubtreeIfNeeded()
        view.displayIfNeeded()
        let bounds = view.bounds
        guard let bitmap = view.bitmapImageRepForCachingDisplay(in: bounds) else {
            fail("Could not allocate the live window buffer")
        }
        view.cacheDisplay(in: bounds, to: bitmap)
        guard let drawing = bitmap.representation(using: .png, properties: [:]) else {
            fail("Could not read the live window buffer")
        }
        return drawing
    }

    private static func fail(_ message: String) -> Never {
        print("FAIL: \(message)")
        exit(1)
    }
}
