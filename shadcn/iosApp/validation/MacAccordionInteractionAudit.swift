import AppKit
import SwiftUI

@MainActor
private final class AccordionResult: ObservableObject {
    @Published var expanded: Set<Int> = []
    @Published var allowsMultiple = false
    @Published var isEnabled = true
    private(set) var writes = 0

    func update(_ values: Set<Int>) {
        writes += 1
        expanded = values
    }
}

private struct AccordionProbe: View {
    @ObservedObject var result: AccordionResult

    var body: some View {
        FoundationAccordion(
            [
                FoundationChoice("First", value: 1),
                FoundationChoice("Disabled", value: 2, isEnabled: false),
                FoundationChoice("Third", value: 3),
            ],
            expanded: Binding(get: { result.expanded }, set: { result.update($0) }),
            allowsMultiple: result.allowsMultiple
        ) { value in
            Text("Panel \(value)").frame(height: 30)
        }
        .disabled(!result.isEnabled)
        .foundationTheme(FoundationTheme(preset: .nova))
        .padding(20)
        .frame(width: 320, height: 280, alignment: .topLeading)
    }
}

@main @MainActor
enum MacAccordionInteractionAudit {
    static func main() {
        let app = NSApplication.shared
        app.setActivationPolicy(.regular)
        let result = AccordionResult()
        let window = NSWindow(
            contentRect: NSRect(x: 0, y: 0, width: 320, height: 280),
            styleMask: [.titled, .closable], backing: .buffered, defer: false
        )
        window.contentView = NSHostingView(rootView: AccordionProbe(result: result))
        window.center()
        window.makeKeyAndOrderFront(nil)

        Task { @MainActor in
            app.activate()
            NSRunningApplication.current.activate(options: [.activateAllWindows])
            window.makeKeyAndOrderFront(nil)
            try? await Task.sleep(for: .milliseconds(400))
            require(window.isKeyWindow, "The audit window needs focus")

            func expect(_ values: Set<Int>, writes: Int, after topOffset: CGFloat) async {
                await click(topOffset: topOffset, window: window)
                require(result.expanded == values && result.writes == writes,
                        "Click at \(topOffset): expected \(values) in \(writes) writes, got \(result.expanded) in \(result.writes)")
            }

            await expect([1], writes: 1, after: 42)
            await expect([1], writes: 1, after: 127)
            await expect([3], writes: 2, after: 172)
            await expect([], writes: 3, after: 132)

            result.allowsMultiple = true
            try? await Task.sleep(for: .milliseconds(150))
            await expect([1], writes: 4, after: 42)
            await expect([1, 3], writes: 5, after: 172)
            await expect([3], writes: 6, after: 42)

            result.isEnabled = false
            try? await Task.sleep(for: .milliseconds(150))
            await expect([3], writes: 6, after: 42)
            await expect([3], writes: 6, after: 132)
            print("PASS: Nova Accordion opens and closes independently, respects disabled states, and writes once per action")
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

    private static func click(topOffset: CGFloat, window: NSWindow) async {
        for type in [NSEvent.EventType.leftMouseDown, .leftMouseUp] {
            if let event = NSEvent.mouseEvent(
                with: type, location: NSPoint(x: 220, y: 280 - topOffset), modifierFlags: [],
                timestamp: ProcessInfo.processInfo.systemUptime, windowNumber: window.windowNumber,
                context: nil, eventNumber: 0, clickCount: 1, pressure: type == .leftMouseUp ? 0 : 1
            ) {
                NSApplication.shared.postEvent(event, atStart: false)
            }
            try? await Task.sleep(for: .milliseconds(100))
        }
        try? await Task.sleep(for: .milliseconds(240))
    }
}
