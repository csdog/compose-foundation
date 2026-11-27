import AppKit
import SwiftUI

@MainActor
private final class BreadcrumbResult: ObservableObject {
    var actions: [String] = []
}

private struct BreadcrumbProbe: View {
    let result: BreadcrumbResult

    var body: some View {
        FoundationBreadcrumb([
            .init(id: "home", title: "Home", action: { result.actions.append("Home") }),
            .init(id: "components", title: "Components", action: { result.actions.append("Components") }),
            .init(id: "current", title: "Breadcrumb", action: { result.actions.append("Current") }),
        ], separatorSymbol: "circle.fill", separatorSize: 4)
        .foundationTheme(FoundationTheme(preset: .nova))
        .frame(height: 44)
        .padding(.horizontal, 20)
        .frame(width: 360, height: 140)
    }
}

@main @MainActor
enum MacBreadcrumbInteractionAudit {
    static func main() {
        let application = NSApplication.shared
        application.setActivationPolicy(.regular)
        let result = BreadcrumbResult()
        let window = NSWindow(
            contentRect: NSRect(x: 0, y: 0, width: 360, height: 140),
            styleMask: [.titled, .closable], backing: .buffered, defer: false
        )
        window.contentView = NSHostingView(rootView: BreadcrumbProbe(result: result))
        window.center()
        window.makeKeyAndOrderFront(nil)

        Task { @MainActor in
            application.activate()
            NSRunningApplication.current.activate(options: [.activateAllWindows])
            window.makeKeyAndOrderFront(nil)
            try? await Task.sleep(for: .milliseconds(400))
            require(window.isKeyWindow, "The audit window needs focus")

            await click(x: 42, in: window)
            require(result.actions == ["Home"], "Home activates once")
            await click(x: 78, in: window)
            require(result.actions == ["Home"], "The separator is decorative")
            await click(x: 125, in: window)
            require(result.actions == ["Home", "Components"], "Components activates independently")
            await click(x: 225, in: window)
            require(result.actions == ["Home", "Components"], "The current page does not invoke its stored action")
            print("PASS: Breadcrumb activates ancestor links and keeps separators and current page inert")
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
