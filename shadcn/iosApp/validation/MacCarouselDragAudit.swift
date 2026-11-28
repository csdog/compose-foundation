import AppKit
import SwiftUI

@MainActor
private final class CarouselAuditState: ObservableObject {
    @Published var selection = 0
}

private struct CarouselAuditFixture: View {
    @ObservedObject var state: CarouselAuditState
    let rightToLeft: Bool

    var body: some View {
        FoundationCarousel(pageCount: 3, selection: $state.selection, navigation: .sides) { index in
            [Color.red, Color.blue, Color.green][index]
                .frame(height: 140)
        }
        .frame(width: 384)
        .environment(\.layoutDirection, rightToLeft ? .rightToLeft : .leftToRight)
        .frame(width: 500, height: 340)
        .foundationTheme(FoundationTheme(preset: .nova))
    }
}

@main @MainActor
enum MacCarouselDragAudit {
    static func main() {
        let app = NSApplication.shared
        let state = CarouselAuditState()
        let rtl = CommandLine.arguments.contains("--rtl")
        let host = NSHostingView(rootView: CarouselAuditFixture(state: state, rightToLeft: rtl))
        let window = NSWindow(contentRect: NSRect(x: 0, y: 0, width: 500, height: 340),
                              styleMask: [.titled, .closable], backing: .buffered, defer: false)
        window.contentView = host
        app.setActivationPolicy(.regular)
        window.center()
        window.makeKeyAndOrderFront(nil)

        Task { @MainActor in
            app.activate()
            NSRunningApplication.current.activate(options: [.activateAllWindows])
            window.makeKeyAndOrderFront(nil)
            try? await Task.sleep(for: .milliseconds(350))
            require(window.isKeyWindow, "The audit window needs focus")

            let direction: CGFloat = rtl ? 1 : -1
            let edge: CGFloat = rtl ? 132 : 368
            require(renderedNeighbor(in: host, x: edge) == false, "The viewport starts on the first page")

            await mouse(.leftMouseDown, x: 250, in: window)
            for step in 1...5 {
                await mouse(.leftMouseDragged, x: 250 + CGFloat(step) * 20 * direction, in: window)
            }
            require(state.selection == 0, "The binding should stay at page one during the drag")
            require(renderedNeighbor(in: host, x: edge), "The adjacent page should be visible before release")

            await mouse(.leftMouseUp, x: 250 + 100 * direction, in: window)
            try? await Task.sleep(for: .milliseconds(160))
            require(state.selection == 1, "The long drag should settle on page two")

            await click(x: rtl ? 420 : 80, in: window)
            require(state.selection == 0, "The previous side button should restore page one")
            await mouse(.leftMouseDown, x: 250, in: window)
            for step in 1...5 {
                await mouse(.leftMouseDragged, x: 250 - CGFloat(step) * 20 * direction, in: window)
            }
            await mouse(.leftMouseUp, x: 250 - 100 * direction, in: window)
            require(state.selection == 0, "The carousel must stop at its starting boundary")

            await click(x: rtl ? 80 : 420, in: window)
            require(state.selection == 1, "The next side button should advance the binding")
            print("PASS: \(rtl ? "RTL" : "LTR") Carousel exposes the neighboring page during drag, settles, and respects the boundary")
            exit(0)
        }
        app.run()
    }

    private static func renderedNeighbor(in view: NSView, x: CGFloat) -> Bool {
        view.layoutSubtreeIfNeeded()
        view.displayIfNeeded()
        guard let bitmap = view.bitmapImageRepForCachingDisplay(in: view.bounds) else { return false }
        view.cacheDisplay(in: view.bounds, to: bitmap)
        let pixelX = Int(x * CGFloat(bitmap.pixelsWide) / view.bounds.width)
        guard let color = bitmap.colorAt(x: pixelX, y: bitmap.pixelsHigh / 2)?.usingColorSpace(.deviceRGB) else { return false }
        return color.blueComponent > color.redComponent
    }

    private static func click(x: CGFloat, in window: NSWindow) async {
        await mouse(.leftMouseDown, x: x, in: window)
        await mouse(.leftMouseUp, x: x, in: window)
    }

    private static func mouse(_ type: NSEvent.EventType, x: CGFloat, in window: NSWindow) async {
        guard let event = NSEvent.mouseEvent(
            with: type, location: NSPoint(x: x, y: 170), modifierFlags: [],
            timestamp: ProcessInfo.processInfo.systemUptime, windowNumber: window.windowNumber,
            context: nil, eventNumber: 0, clickCount: 1, pressure: type == .leftMouseUp ? 0 : 1
        ) else { return }
        NSApplication.shared.postEvent(event, atStart: false)
        try? await Task.sleep(for: .milliseconds(120))
    }

    private static func require(_ condition: Bool, _ message: String) {
        if !condition {
            print("FAIL: \(message)")
            exit(1)
        }
    }
}
