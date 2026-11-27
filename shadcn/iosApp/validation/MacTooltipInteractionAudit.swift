import AppKit
import SwiftUI

@MainActor
private final class TooltipAuditState: ObservableObject {
    @Published var visible = false
    @Published var enabled = true
}

private struct TooltipAuditFixture: View {
    @ObservedObject var state: TooltipAuditState
    let interaction: FoundationTooltipInteraction

    var body: some View {
        Button("Hover") {}
            .frame(width: 64, height: 32)
            .modifier(FoundationTooltip("Add to library", presentation: $state.visible, side: .bottom,
                                        delay: 0.12, interaction: interaction))
            .disabled(!state.enabled)
            .position(x: 100, y: 56)
            .environment(\.colorScheme, .light)
            .foundationTheme(FoundationTheme(preset: .nova))
            .frame(width: 200, height: 150)
            .background(Color.white)
    }
}

@main @MainActor
enum MacTooltipInteractionAudit {
    static func main() {
        let app = NSApplication.shared
        app.setActivationPolicy(.regular)
        let state = TooltipAuditState()
        let interaction = FoundationTooltipInteraction()
        let binding = Binding(get: { state.visible }, set: { state.visible = $0 })
        let window = NSWindow(contentRect: NSRect(x: 0, y: 0, width: 200, height: 150),
                              styleMask: [.titled, .closable], backing: .buffered, defer: false)
        let host = NSHostingView(rootView: TooltipAuditFixture(state: state, interaction: interaction))
        window.contentView = host
        window.center()
        window.makeKeyAndOrderFront(nil)

        Task { @MainActor in
            app.activate()
            NSRunningApplication.current.activate(options: [.activateAllWindows])
            window.makeKeyAndOrderFront(nil)
            try? await Task.sleep(for: .milliseconds(400))
            require(window.isKeyWindow, "Expected the parent window to have focus")
            let closed = darkPixels(in: host)
            let delay: UInt64 = 120_000_000

            interaction.pointerChanged(true, presented: binding, delay: delay)
            require(!state.visible, "The hint waits for the configured pointer delay")
            interaction.pointerChanged(false, presented: binding, delay: delay)
            try? await Task.sleep(for: .milliseconds(180))
            require(!state.visible, "Moving away cancels the pending reveal")

            interaction.pointerChanged(true, presented: binding, delay: delay)
            try? await Task.sleep(for: .milliseconds(230))
            let opened = darkPixels(in: host)
            require(state.visible && opened > closed + 500,
                    "Hover presents the bubble below the trigger (visible: \(state.visible), pixels: \(closed) → \(opened))")
            require(window.isKeyWindow, "Showing the hint leaves its parent focused")

            interaction.pointerChanged(false, presented: binding, delay: delay)
            try? await Task.sleep(for: .milliseconds(100))
            require(!state.visible && darkPixels(in: host) == closed,
                    "Leaving the trigger clears the displayed hint")

            state.visible = true
            try? await Task.sleep(for: .milliseconds(100))
            require(darkPixels(in: host) > closed + 500, "The presentation binding displays the same bubble")
            state.enabled = false
            try? await Task.sleep(for: .milliseconds(100))
            require(!state.visible && darkPixels(in: host) == closed,
                    "Disabling the trigger dismisses and hides its hint")

            print("PASS: Tooltip delays hover, cancels on exit, preserves focus and hides on disable")
            exit(0)
        }
        app.run()
    }

    private static func darkPixels(in view: NSView) -> Int {
        guard let bitmap = view.bitmapImageRepForCachingDisplay(in: view.bounds) else { return 0 }
        view.cacheDisplay(in: view.bounds, to: bitmap)
        let scale = CGFloat(bitmap.pixelsWide) / view.bounds.width
        var count = 0
        for y in 76..<104 {
            for x in 56..<144 {
                let pixel = bitmap.colorAt(x: Int(CGFloat(x) * scale), y: Int(CGFloat(y) * scale))
                if let color = pixel?.usingColorSpace(.sRGB), color.redComponent < 0.2 { count += 1 }
            }
        }
        return count
    }

    private static func require(_ condition: Bool, _ message: String) {
        if !condition {
            print("FAIL: \(message)")
            exit(1)
        }
    }
}
