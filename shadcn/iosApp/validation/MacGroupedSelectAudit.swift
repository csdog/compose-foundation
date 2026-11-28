import AppKit
import SwiftUI

@MainActor
private final class SelectAuditState: ObservableObject {
    @Published var selection: String?
    @Published var isEnabled = true
}

private struct SelectAuditFixture: View {
    @ObservedObject var state: SelectAuditState

    var body: some View {
        FoundationSelect("Choose produce", groups: [
            .init("Fruit", choices: [.init("Apple", value: "apple"), .init("Apricot", value: "apricot", isEnabled: false)]),
            .init("Empty group", choices: []),
            .init("Vegetables", choices: [.init("Carrot", value: "carrot")]),
        ], selection: $state.selection)
        .disabled(!state.isEnabled)
        .frame(width: 192)
        .frame(width: 360, height: 180)
        .foundationTheme(FoundationTheme(preset: .nova))
    }
}

@main @MainActor
enum MacGroupedSelectAudit {
    static func main() {
        let app = NSApplication.shared
        let state = SelectAuditState()
        let host = NSHostingView(rootView: SelectAuditFixture(state: state))
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
            try? await Task.sleep(for: .milliseconds(250))
            require(window.isKeyWindow, "The audit window needs focus")

            let placeholder = await render(host)
            state.selection = "carrot"
            let vegetable = await render(host)
            require(vegetable != placeholder, "The selected vegetable should replace the placeholder")

            state.selection = "apple"
            let fruit = await render(host)
            require(fruit != vegetable && fruit != placeholder, "Selection should update across groups")

            state.selection = "missing"
            let missing = await render(host)
            require(missing == placeholder, "An unavailable selection should use the placeholder")

            state.isEnabled = false
            let disabled = await render(host)
            require(disabled != placeholder, "A disabled control should update its appearance")
            print("PASS: Grouped Select renders bound selections, missing-value fallback, and disabled appearance")
            exit(0)
        }
        app.run()
    }

    private static func render(_ view: NSView) async -> Data {
        try? await Task.sleep(for: .milliseconds(180))
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

    private static func require(_ condition: Bool, _ message: String) {
        if !condition { fail(message) }
    }

    private static func fail(_ message: String) -> Never {
        print("FAIL: \(message)")
        exit(1)
    }
}
