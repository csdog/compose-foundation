import AppKit
import SwiftUI

@MainActor
private final class PaginationResult: ObservableObject {
    @Published var page = 1
    @Published var rightToLeft = false
    var writes: [Int] = []
}

private struct PaginationProbe: View {
    @ObservedObject var result: PaginationResult

    var body: some View {
        FoundationPagination(pageCount: 5, page: Binding(get: { result.page }, set: { value in
            result.writes.append(value)
            result.page = value
        }))
        .foundationTheme(FoundationTheme(preset: .nova))
        .environment(\.layoutDirection, result.rightToLeft ? .rightToLeft : .leftToRight)
        .frame(width: 360, height: 120)
    }
}

@main @MainActor
enum MacPaginationAudit {
    static func main() {
        let app = NSApplication.shared
        app.setActivationPolicy(.regular)
        let result = PaginationResult()
        let window = NSWindow(contentRect: NSRect(x: 0, y: 0, width: 360, height: 120),
                              styleMask: [.titled, .closable], backing: .buffered, defer: false)
        window.contentView = NSHostingView(rootView: PaginationProbe(result: result))
        window.center()
        window.makeKeyAndOrderFront(nil)

        Task { @MainActor in
            app.activate()
            NSRunningApplication.current.activate(options: [.activateAllWindows])
            window.makeKeyAndOrderFront(nil)
            try? await Task.sleep(for: .milliseconds(400))
            require(window.isKeyWindow, "The audit window needs focus")

            await click(x: 42, in: window)
            require(result.writes.isEmpty, "Previous is disabled on page one")
            await click(x: 318, in: window)
            require(result.writes == [2], "Next advances once")
            await click(x: 272, in: window)
            require(result.writes == [2, 5], "The direct page button reaches the end")
            await click(x: 318, in: window)
            require(result.writes == [2, 5], "Next is disabled on the last page")

            result.rightToLeft = true
            try? await Task.sleep(for: .milliseconds(180))
            await click(x: 318, in: window)
            await click(x: 42, in: window)
            require(result.writes == [2, 5, 4, 5], "RTL mirrors Previous and Next: \(result.writes)")
            print("PASS: Pagination bounds writes, selects pages directly, and mirrors step controls in RTL")
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

    private static func click(x: CGFloat, in window: NSWindow) async {
        for type in [NSEvent.EventType.leftMouseDown, .leftMouseUp] {
            if let event = NSEvent.mouseEvent(
                with: type, location: NSPoint(x: x, y: 60), modifierFlags: [],
                timestamp: ProcessInfo.processInfo.systemUptime, windowNumber: window.windowNumber,
                context: nil, eventNumber: 0, clickCount: 1, pressure: type == .leftMouseUp ? 0 : 1
            ) {
                NSApplication.shared.postEvent(event, atStart: false)
            }
            try? await Task.sleep(for: .milliseconds(140))
        }
    }
}
