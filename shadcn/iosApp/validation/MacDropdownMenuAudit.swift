import AppKit
import SwiftUI

@MainActor
private final class DropdownMenuAuditState: ObservableObject {
    @Published var title = "Open"
    @Published var isEnabled = true
}

private struct DropdownMenuAuditFixture: View {
    @ObservedObject var state: DropdownMenuAuditState

    var body: some View {
        FoundationDropdownMenu {
            Section(header: Text("My Account")) {
                Button("Profile") {}
                Button("API") {}.disabled(true)
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
            exit(0)
        }
        app.run()
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
