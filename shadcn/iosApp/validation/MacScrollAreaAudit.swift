import AppKit
import SwiftUI

private struct ScrollAreaProbe: View {
    var body: some View {
        HStack {
            FoundationScrollArea(showsIndicators: false, showsBorder: true) {
                VStack(spacing: 0) {
                    ForEach(0..<40) { row in Text("Row \(row)").frame(width: 110, height: 24) }
                }
            }
            .frame(width: 120, height: 120)

            FoundationScrollArea(.horizontal, showsIndicators: false, showsBorder: true) {
                HStack(spacing: 0) {
                    ForEach(0..<40) { item in Text("Item \(item)").frame(width: 65, height: 110) }
                }
            }
            .frame(width: 120, height: 120)
        }
        .foundationTheme(FoundationTheme(preset: .nova))
        .frame(width: 300, height: 180)
    }
}

@main @MainActor
enum MacScrollAreaAudit {
    static func main() {
        let app = NSApplication.shared
        app.setActivationPolicy(.regular)
        let window = NSWindow(contentRect: NSRect(x: 0, y: 0, width: 300, height: 180),
                              styleMask: [.titled, .closable], backing: .buffered, defer: false)
        let host = NSHostingView(rootView: ScrollAreaProbe())
        window.contentView = host
        window.center()
        window.makeKeyAndOrderFront(nil)

        Task { @MainActor in
            app.activate()
            try? await Task.sleep(for: .milliseconds(450))
            let viewports = scrollViews(in: host)
            guard viewports.count == 2,
                  let vertical = viewports.first(where: { ($0.documentView?.frame.height ?? 0) > 200 }),
                  let horizontal = viewports.first(where: { ($0.documentView?.frame.width ?? 0) > 200 }) else {
                fail("Expected native vertical and horizontal NSScrollView instances")
            }
            let startVertical = vertical.contentView.bounds.origin
            let startHorizontal = horizontal.contentView.bounds.origin
            vertical.scrollWheel(with: wheel(dx: 0, dy: -90))
            horizontal.scrollWheel(with: wheel(dx: -90, dy: 0))
            try? await Task.sleep(for: .milliseconds(300))
            let endVertical = vertical.contentView.bounds.origin
            let endHorizontal = horizontal.contentView.bounds.origin
            guard abs(endVertical.y - startVertical.y) > 20, abs(endVertical.x - startVertical.x) < 1 else {
                fail("Vertical wheel did not scroll only vertically: \(startVertical) → \(endVertical)")
            }
            guard abs(endHorizontal.x - startHorizontal.x) > 20, abs(endHorizontal.y - startHorizontal.y) < 1 else {
                fail("Horizontal wheel did not scroll only horizontally: \(startHorizontal) → \(endHorizontal)")
            }
            print("PASS: Bordered Scroll Area preserves native wheel scrolling on both axes")
            exit(0)
        }
        app.run()
    }

    private static func scrollViews(in view: NSView) -> [NSScrollView] {
        if let scrollView = view as? NSScrollView { return [scrollView] }
        return view.subviews.flatMap { scrollViews(in: $0) }
    }

    private static func wheel(dx: Int32, dy: Int32) -> NSEvent {
        guard let event = CGEvent(scrollWheelEvent2Source: nil, units: .pixel, wheelCount: 2,
                                  wheel1: dy, wheel2: dx, wheel3: 0),
              let native = NSEvent(cgEvent: event) else { fail("Could not create a wheel event") }
        return native
    }

    private static func fail(_ message: String) -> Never {
        print("FAIL: \(message)")
        exit(1)
    }
}
