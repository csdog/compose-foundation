import AppKit
import SwiftUI

@MainActor
private final class HoverCardResult: ObservableObject {
    @Published var presented = false
    let interaction = FoundationHoverCardInteraction()

    var presentation: Binding<Bool> { Binding(get: { self.presented }, set: { self.presented = $0 }) }

    func hover(_ inside: Bool, trigger: Bool) {
        interaction.pointerChanged(inside, isTrigger: trigger, presented: presentation, closeDelay: 100_000_000)
    }
}

private struct HoverCardProbe: View {
    @ObservedObject var state: HoverCardResult

    var body: some View {
        Text("Hover Here")
            .frame(width: 100, height: 44)
            .modifier(FoundationHoverCard(presentation: state.presentation, interaction: state.interaction) {
                Text("Profile card").frame(height: 60)
            })
            .foundationTheme(FoundationTheme(preset: .nova))
            .frame(width: 420, height: 330)
    }
}

@main @MainActor
enum MacHoverCardAudit {
    static func main() {
        let app = NSApplication.shared
        app.setActivationPolicy(.regular)
        let state = HoverCardResult()
        let window = NSWindow(contentRect: NSRect(x: 0, y: 0, width: 420, height: 330),
                              styleMask: [.titled, .closable], backing: .buffered, defer: false)
        window.contentView = NSHostingView(rootView: HoverCardProbe(state: state))
        window.center()
        window.makeKeyAndOrderFront(nil)

        Task { @MainActor in
            app.activate()
            NSRunningApplication.current.activate(options: [.activateAllWindows])
            window.makeKeyAndOrderFront(nil)
            try? await Task.sleep(for: .milliseconds(380))
            require(window.isKeyWindow && !state.presented, "The parent starts key with no card")

            state.hover(true, trigger: true)
            state.hover(false, trigger: true)
            try? await Task.sleep(for: .milliseconds(300))
            require(!state.presented, "Leaving early cancels delayed open")

            state.hover(true, trigger: true)
            guard await waitUntil({ state.presented && window.childWindows?.first != nil }),
                  let card = window.childWindows?.first else { fail("The trigger hover handler did not open a card") }
            require(window.isKeyWindow && !card.isKeyWindow, "Pointer hover must keep the parent key")

            state.hover(false, trigger: true)
            try? await Task.sleep(for: .milliseconds(20))
            state.hover(true, trigger: false)
            try? await Task.sleep(for: .milliseconds(200))
            require(state.presented && card.isVisible && window.isKeyWindow, "Entering the card cancels delayed close")

            state.hover(false, trigger: false)
            require(await waitUntil({ !state.presented && window.childWindows?.isEmpty != false }),
                    "Leaving the card dismisses it")
            print("PASS: Hover Card handlers cancel stale transitions and preserve the parent window's key status")
            exit(0)
        }
        app.run()
    }

    private static func waitUntil(_ condition: () -> Bool) async -> Bool {
        for _ in 0..<30 {
            if condition() { return true }
            try? await Task.sleep(for: .milliseconds(40))
        }
        return condition()
    }

    private static func require(_ condition: Bool, _ message: String) {
        guard condition else { fail(message) }
    }

    private static func fail(_ message: String) -> Never {
        print("FAIL: \(message)")
        exit(1)
    }
}
