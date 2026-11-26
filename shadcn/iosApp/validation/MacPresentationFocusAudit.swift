import AppKit
import SwiftUI

@MainActor
private final class PresentationResult: ObservableObject {
    @Published var presented = false
    var triggerFocused = false
    var dismissals = 0
    var backgroundActivations = 0
}

private enum PresentationKind: String {
    case drawer, dialog, sheet
}

@available(macOS 14, *)
private struct PresentationProbe: View {
    @ObservedObject var state: PresentationResult
    let kind: PresentationKind
    @FocusState private var focused: Bool

    @ViewBuilder var body: some View {
        switch kind {
        case .drawer:
            trigger.modifier(FoundationDrawer(isPresented: $state.presented, title: "Audit drawer", onDismiss: dismissed) {
                content
            })
        case .dialog:
            trigger.modifier(FoundationDialog(isPresented: $state.presented, title: "Audit dialog", onDismiss: dismissed) {
                content
            })
        case .sheet:
            trigger.modifier(FoundationSheet(isPresented: $state.presented, title: "Audit sheet", onDismiss: dismissed) {
                content
            })
        }
    }

    private var trigger: some View {
        VStack {
            Button("Open \(kind.rawValue)") { state.presented = true }
                .focusable(interactions: [.edit, .activate])
                .focused($focused)
                .onChange(of: focused) { _, value in state.triggerFocused = value }
                .onKeyPress(keys: ["d"], phases: .down) { _ in
                    state.presented = true
                    return .handled
                }
            Button("Background action") { state.backgroundActivations += 1 }
            Spacer()
        }
        .frame(width: 420, height: 330)
    }

    private var content: some View {
        Text("Presentation content").frame(height: 44)
    }

    private func dismissed() {
        state.dismissals += 1
        focused = true
    }
}

@available(macOS 14, *)
@main
struct MacPresentationFocusAudit {
    @MainActor
    static func main() {
        let app = NSApplication.shared
        app.setActivationPolicy(.regular)
        let state = PresentationResult()
        let kind = CommandLine.arguments.dropFirst().first.flatMap(PresentationKind.init(rawValue:)) ?? .drawer
        let window = NSWindow(
            contentRect: NSRect(x: 0, y: 0, width: 420, height: 330),
            styleMask: [.titled, .closable], backing: .buffered, defer: false
        )
        window.title = "\(kind.rawValue.capitalized) Focus Audit"
        window.contentView = NSHostingView(rootView: PresentationProbe(state: state, kind: kind))
        window.center()
        app.activate()
        window.makeKeyAndOrderFront(nil)

        Task { @MainActor in
            try? await Task.sleep(for: .milliseconds(240))
            app.activate()
            NSRunningApplication.current.activate(options: [.activateAllWindows])
            window.makeKeyAndOrderFront(nil)
            guard await waitUntil({ window.isKeyWindow }) else { fail("Parent window did not become key", state) }

            state.presented = true
            try? await Task.sleep(for: .milliseconds(350))
            press(36, "\r", app: app)
            guard await waitUntil({ !state.presented && state.dismissals == 1 && state.triggerFocused && window.isKeyWindow }) else {
                fail("Return did not dismiss from initial focus or did not restore the trigger", state)
            }
            print("PASS: \(kind.rawValue) Return dismisses from initial focus and restores the trigger")
            try? await Task.sleep(for: .milliseconds(230))
            guard window.isKeyWindow else { fail("Parent window lost focus before reopening", state) }
            press(2, "d", app: app)
            guard await waitUntil({ state.presented }) else { fail("Could not reopen with the focused trigger", state) }
            try? await Task.sleep(for: .milliseconds(350))
            if let location = ProcessInfo.processInfo.environment["PRESENTATION_CAPTURE"] {
                capture(app.keyWindow ?? window, to: location)
            }
            press(53, "\u{1b}", app: app)
            guard await waitUntil({ !state.presented && state.dismissals == 2 && state.triggerFocused && window.isKeyWindow }) else {
                fail("Escape did not dismiss the reopened presentation or restore focus", state)
            }
            guard state.backgroundActivations == 0 else { fail("Keyboard activated background content", state) }
            print("PASS: \(kind.rawValue) Escape dismisses the reopened presentation and restores the trigger")
            exit(0)
        }
        app.run()
    }

    @MainActor
    private static func capture(_ window: NSWindow, to path: String) {
        guard let host = window.contentView,
              let bitmap = host.bitmapImageRepForCachingDisplay(in: host.bounds) else { return }
        host.cacheDisplay(in: host.bounds, to: bitmap)
        try? bitmap.representation(using: .png, properties: [:])?.write(to: URL(fileURLWithPath: path))
    }

    @MainActor
    private static func waitUntil(_ condition: () -> Bool) async -> Bool {
        for _ in 0..<150 {
            if condition() { return true }
            try? await Task.sleep(for: .milliseconds(15))
        }
        return condition()
    }

    @MainActor
    private static func fail(_ message: String, _ state: PresentationResult) -> Never {
        print("FAIL \(message): presented=\(state.presented), triggerFocused=\(state.triggerFocused), dismissals=\(state.dismissals)")
        exit(1)
    }

    @MainActor
    private static func press(_ code: UInt16, _ characters: String, app: NSApplication) {
        guard let window = app.keyWindow else { return }
        for type in [NSEvent.EventType.keyDown, .keyUp] {
            guard let event = NSEvent.keyEvent(
                with: type, location: .zero, modifierFlags: [],
                timestamp: ProcessInfo.processInfo.systemUptime, windowNumber: window.windowNumber, context: nil,
                characters: characters, charactersIgnoringModifiers: characters, isARepeat: false, keyCode: code
            ) else { return }
            app.sendEvent(event)
        }
    }
}
