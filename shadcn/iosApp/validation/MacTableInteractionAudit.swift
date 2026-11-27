import AppKit
import SwiftUI

@MainActor
private final class TableAuditResult {
    var choices: [String] = []
}

private struct TableProbe: View {
    let result: TableAuditResult
    private struct Entry: Identifiable {
        let id: String
        let detail: String
    }

    var body: some View {
        FoundationTable(
            columns: [
                .init(id: "name", title: "Name", width: 100, order: { $0.id < $1.id }, value: { $0.id }),
                .init(id: "action", title: "Action", width: 52, value: { _ in "" }) { entry in
                    Button("Choose") { result.choices.append(entry.id) }
                        .buttonStyle(PlainButtonStyle())
                        .accessibilityLabel("Choose \(entry.id)")
                },
                .init(id: "detail", title: "Detail", width: 200, value: { $0.detail }),
            ],
            rows: [Entry(id: "Zulu", detail: "Second"), Entry(id: "Alpha", detail: "First")],
            caption: "Two rows",
            footer: Entry(id: "Total", detail: "2")
        )
        .foundationTheme(FoundationTheme(preset: .nova))
        .environment(\.colorScheme, .light)
        .frame(width: 160, height: 200)
        .background(Color.white)
    }
}

@main @MainActor
enum MacTableInteractionAudit {
    static func main() {
        let app = NSApplication.shared
        app.setActivationPolicy(.regular)
        let window = NSWindow(contentRect: NSRect(x: 0, y: 0, width: 160, height: 200),
                              styleMask: [.titled, .closable], backing: .buffered, defer: false)
        let result = TableAuditResult()
        let host = NSHostingView(rootView: TableProbe(result: result))
        window.contentView = host
        window.center()
        window.makeKeyAndOrderFront(nil)

        Task { @MainActor in
            app.activate()
            NSRunningApplication.current.activate(options: [.activateAllWindows])
            window.makeKeyAndOrderFront(nil)
            try? await Task.sleep(for: .milliseconds(450))
            require(window.isKeyWindow, "The audit window needs focus")
            await click(x: 144, y: 137, in: window)
            require(result.choices == ["Zulu"], "The first row's native action receives the click")
            let initial = pixels(in: host, rows: 46..<114)
            let footer = pixels(in: host, rows: 119..<151)
            require(Set(initial).count > 3, "The body has rendered text")
            require(Set(footer).count > 3, "The footer has rendered text")

            await click(x: 39, y: 178, in: window)
            require(pixels(in: host, rows: 46..<114) != initial, "First click sorts the body in ascending order")
            require(pixels(in: host, rows: 119..<151) == footer, "The footer stays below the sorted body")

            await click(x: 39, y: 178, in: window)
            require(pixels(in: host, rows: 46..<114) == initial, "Second click restores the descending body order")

            await click(x: 39, y: 178, in: window)
            await click(x: 144, y: 137, in: window)
            require(result.choices == ["Zulu", "Alpha"], "After sorting, the same action position targets the new row")

            guard let viewport = scrollView(in: host),
                  let wheel = CGEvent(scrollWheelEvent2Source: nil, units: .pixel, wheelCount: 2,
                                      wheel1: 0, wheel2: -90, wheel3: 0),
                  let event = NSEvent(cgEvent: wheel) else {
                fail("Expected a native scroll viewport and wheel event")
            }
            let before = viewport.contentView.bounds.origin
            viewport.scrollWheel(with: event)
            try? await Task.sleep(for: .milliseconds(300))
            let after = viewport.contentView.bounds.origin
            require(abs(after.x - before.x) > 20 && abs(after.y - before.y) < 1,
                    "The wide table scrolls horizontally in a narrow viewport")
            print("PASS: Table sorts data rows in both directions, preserves its footer and row actions, and scrolls horizontally")
            exit(0)
        }
        app.run()
    }

    private static func pixels(in view: NSView, rows: Range<Int>) -> [Int] {
        guard let bitmap = view.bitmapImageRepForCachingDisplay(in: view.bounds) else { return [] }
        view.cacheDisplay(in: view.bounds, to: bitmap)
        let scale = CGFloat(bitmap.pixelsWide) / view.bounds.width
        return rows.flatMap { y in
            (8..<72).map { x in
                let pixel = bitmap.colorAt(x: Int(CGFloat(x) * scale), y: Int(CGFloat(y) * scale))
                let color = pixel?.usingColorSpace(.sRGB)
                return Int((color?.redComponent ?? 1) * 255)
            }
        }
    }

    private static func scrollView(in view: NSView) -> NSScrollView? {
        if let viewport = view as? NSScrollView { return viewport }
        return view.subviews.lazy.compactMap { scrollView(in: $0) }.first
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

    private static func require(_ condition: Bool, _ message: String) {
        if !condition { fail(message) }
    }

    private static func fail(_ message: String) -> Never {
        print("FAIL: \(message)")
        exit(1)
    }
}
