import AppKit
import SwiftUI

@MainActor
private final class CardResult: ObservableObject {
    @Published var actions: [String] = []
    @Published var footerDisabled = false
}

private struct CardProbe: View {
    @ObservedObject var result: CardResult

    var body: some View {
        FoundationCard {
            Button { result.actions.append("body") } label: {
                Text("Body action").frame(width: 140, height: 44).contentShape(Rectangle())
            }
            .buttonStyle(PlainButtonStyle())
            .frame(maxWidth: .infinity)
        } footer: {
            Button { result.actions.append("footer") } label: {
                Text("Footer action").frame(width: 140, height: 44).contentShape(Rectangle())
            }
            .buttonStyle(PlainButtonStyle())
            .disabled(result.footerDisabled)
            .frame(maxWidth: .infinity)
        }
        .foundationTheme(FoundationTheme(preset: .nova))
        .frame(width: 240)
        .frame(width: 320, height: 220)
    }
}

@main @MainActor
enum MacCardInteractionAudit {
    static func main() {
        let app = NSApplication.shared
        app.setActivationPolicy(.regular)
        let result = CardResult()
        let window = NSWindow(
            contentRect: NSRect(x: 0, y: 0, width: 320, height: 220),
            styleMask: [.titled, .closable], backing: .buffered, defer: false
        )
        window.contentView = NSHostingView(rootView: CardProbe(result: result))
        window.center()
        window.makeKeyAndOrderFront(nil)

        Task { @MainActor in
            app.activate()
            NSRunningApplication.current.activate(options: [.activateAllWindows])
            window.makeKeyAndOrderFront(nil)
            try? await Task.sleep(nanoseconds: 400_000_000)
            require(window.isKeyWindow, "The audit window needs focus")
            await click(y: 148, window: window)
            await click(y: 72, window: window)
            require(result.actions == ["body", "footer"], "The body and footer should respond separately")

            result.footerDisabled = true
            try? await Task.sleep(nanoseconds: 150_000_000)
            await click(y: 72, window: window)
            await click(y: 148, window: window)
            require(result.actions == ["body", "footer", "body"], "Disabling the footer should preserve the body action")
            print("PASS: Nova Card body and footer controls remain independently actionable")
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

    private static func click(y: CGFloat, window: NSWindow) async {
        for type in [NSEvent.EventType.leftMouseDown, .leftMouseUp] {
            if let event = NSEvent.mouseEvent(
                with: type, location: NSPoint(x: 160, y: y), modifierFlags: [],
                timestamp: ProcessInfo.processInfo.systemUptime, windowNumber: window.windowNumber,
                context: nil, eventNumber: 0, clickCount: 1, pressure: type == .leftMouseUp ? 0 : 1
            ) {
                NSApplication.shared.postEvent(event, atStart: false)
            }
            try? await Task.sleep(nanoseconds: 100_000_000)
        }
    }
}
