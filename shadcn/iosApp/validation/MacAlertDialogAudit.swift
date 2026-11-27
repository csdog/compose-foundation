import AppKit
import SwiftUI

@MainActor
private final class AlertDialogAuditState: ObservableObject {
    @Published var visible = false
    var cancelled = 0
    var confirmed = 0
    var backgroundClicks = 0
}

private struct AlertDialogAuditFixture: View {
    @ObservedObject var state: AlertDialogAuditState

    var body: some View {
        ZStack(alignment: .topLeading) {
            Button("Open") {
                state.backgroundClicks += 1
                state.visible = true
            }
            .frame(width: 100, height: 44)
            .padding(20)
        }
        .frame(width: 720, height: 400, alignment: .topLeading)
        .background(Color.white)
        .modifier(FoundationAlertDialog(
            isPresented: $state.visible, title: "Are you absolutely sure?",
            message: "This action cannot be undone. This will permanently delete your account from our servers.",
            size: .regular, onCancel: { state.cancelled += 1 }, onConfirm: { state.confirmed += 1 }
        ))
        .foundationTheme(FoundationTheme(preset: .nova))
        .environment(\.colorScheme, .light)
    }
}

@main @MainActor
enum MacAlertDialogAudit {
    static func main() {
        let app = NSApplication.shared
        app.setActivationPolicy(.regular)
        let state = AlertDialogAuditState()
        let window = NSWindow(contentRect: NSRect(x: 0, y: 0, width: 720, height: 400),
                              styleMask: [.titled, .closable], backing: .buffered, defer: false)
        window.contentView = NSHostingView(rootView: AlertDialogAuditFixture(state: state))
        window.center()
        window.makeKeyAndOrderFront(nil)

        Task { @MainActor in
            app.activate()
            NSRunningApplication.current.activate(options: [.activateAllWindows])
            window.makeKeyAndOrderFront(nil)
            try? await Task.sleep(for: .milliseconds(420))
            require(window.isKeyWindow, "The audit window needs focus")

            await click(x: 70, y: 358, in: window)
            require(state.visible && state.backgroundClicks == 1, "The trigger opens the modal")
            await click(x: 70, y: 358, in: window)
            require(state.visible && state.backgroundClicks == 1, "Outside clicks leave the modal open and block its trigger")

            await press("\r", code: 36, in: window)
            require(!state.visible && state.cancelled == 1 && state.confirmed == 0, "Initial Return activates Cancel")

            await click(x: 70, y: 358, in: window)
            require(state.visible, "The trigger opens again")
            await press("\u{1b}", code: 53, in: window)
            require(!state.visible && state.cancelled == 2 && state.confirmed == 0, "Escape cancels exactly once")

            await click(x: 70, y: 358, in: window)
            require(state.visible, "The dialog can open after keyboard dismissal")
            await click(x: 495, y: 139, in: window)
            require(!state.visible && state.cancelled == 2 && state.confirmed == 1, "Confirm runs exactly once")
            print("PASS: Alert Dialog blocks background clicks, defaults to Cancel, dismisses on Escape, and confirms once")
            exit(0)
        }
        app.run()
    }

    private static func click(x: CGFloat, y: CGFloat, in window: NSWindow) async {
        for type in [NSEvent.EventType.leftMouseDown, .leftMouseUp] {
            if let event = NSEvent.mouseEvent(
                with: type, location: NSPoint(x: x, y: y), modifierFlags: [],
                timestamp: ProcessInfo.processInfo.systemUptime, windowNumber: window.windowNumber,
                context: nil, eventNumber: 0, clickCount: 1, pressure: type == .leftMouseUp ? 0 : 1
            ) {
                NSApplication.shared.postEvent(event, atStart: false)
            }
            try? await Task.sleep(for: .milliseconds(160))
        }
    }

    private static func press(_ key: String, code: UInt16, in window: NSWindow) async {
        for type in [NSEvent.EventType.keyDown, .keyUp] {
            if let event = NSEvent.keyEvent(with: type, location: .zero, modifierFlags: [],
                                            timestamp: ProcessInfo.processInfo.systemUptime, windowNumber: window.windowNumber,
                                            context: nil, characters: key, charactersIgnoringModifiers: key,
                                            isARepeat: false, keyCode: code) {
                NSApplication.shared.postEvent(event, atStart: false)
            }
        }
        try? await Task.sleep(for: .milliseconds(200))
    }

    private static func require(_ condition: Bool, _ message: String) {
        if !condition {
            print("FAIL: \(message)")
            exit(1)
        }
    }
}
