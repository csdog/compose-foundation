import AppKit
import SwiftUI

@MainActor
private final class AspectRatioResult {
    var targets: [String] = []
}

private struct AspectRatioProbe: View {
    let result: AspectRatioResult

    var body: some View {
        ZStack {
            Button { result.targets.append("background") } label: {
                Color.white.frame(width: 320, height: 260).contentShape(Rectangle())
            }
            FoundationAspectRatio(16.0 / 9.0) {
                Button { result.targets.append("content") } label: {
                    Color.red.frame(width: 320, height: 260).contentShape(Rectangle())
                }
            }
            .frame(width: 160)
        }
        .buttonStyle(PlainButtonStyle())
    }
}

@main @MainActor
enum MacAspectRatioAudit {
    static func main() {
        assertSize(16.0 / 9.0, width: 160, height: 90)
        assertSize(1, width: 160, height: 160)
        assertSize(9.0 / 16.0, width: 90, height: 160)
        assertSize(0, width: 80, height: 80)
        assertSize(-2, width: 80, height: 80)
        assertSize(.infinity, width: 80, height: 80)
        assertSize(.nan, width: 80, height: 80)

        let app = NSApplication.shared
        app.setActivationPolicy(.regular)
        let result = AspectRatioResult()
        let window = NSWindow(contentRect: NSRect(x: 0, y: 0, width: 320, height: 260),
                              styleMask: [.titled, .closable], backing: .buffered, defer: false)
        window.contentView = NSHostingView(rootView: AspectRatioProbe(result: result))
        window.center()
        window.makeKeyAndOrderFront(nil)

        Task { @MainActor in
            app.activate()
            NSRunningApplication.current.activate(options: [.activateAllWindows])
            window.makeKeyAndOrderFront(nil)
            try? await Task.sleep(for: .milliseconds(400))
            require(window.isKeyWindow, "The audit window needs focus")
            await click(x: 160, y: 130, in: window)
            await click(x: 160, y: 205, in: window)
            await click(x: 40, y: 130, in: window)
            require(result.targets == ["content", "background", "background"],
                    "Only clicks in the visible rectangle reach the clipped content: \(result.targets)")
            print("PASS: AspectRatio measures exact dimensions and clips overflow hit targets")
            exit(0)
        }
        app.run()
    }

    private static func assertSize(_ ratio: CGFloat, width: Int, height: Int) {
        let renderer = ImageRenderer(content: FoundationAspectRatio(ratio) {
            Color.red.frame(width: 320, height: 320)
        }.frame(width: CGFloat(width)))
        renderer.scale = 1
        guard let image = renderer.cgImage else { fail("Rasterization failed for \(ratio)") }
        require(image.width == width && image.height == height,
                "Ratio \(ratio) at \(width) pt produced \(image.width) × \(image.height), expected \(width) × \(height)")
    }

    private static func require(_ condition: Bool, _ message: String) {
        guard condition else { fail(message) }
    }

    private static func fail(_ message: String) -> Never {
        print("FAIL: \(message)")
        exit(1)
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
            try? await Task.sleep(for: .milliseconds(140))
        }
    }
}
