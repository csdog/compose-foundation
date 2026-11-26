import AppKit
import SwiftUI

@MainActor
private final class PopoverFocusState: ObservableObject {
    @Published var presented = false
    var panelFocused = false
    var triggerFocused = false
    var dismissals = 0
}

@available(macOS 14, *)
private struct PopoverProbe: View {
    @ObservedObject var state: PopoverFocusState
    @FocusState private var focused: Bool

    var body: some View {
        Button("Open popover") { state.presented = true }
            .focusable(interactions: [.edit, .activate])
            .focused($focused)
            .onChange(of: focused) { _, newValue in state.triggerFocused = newValue }
            .onKeyPress(keys: ["p"], phases: .down) { _ in
                state.presented = true
                return .handled
            }
            .modifier(FoundationPopover(isPresented: $state.presented, onDismiss: {
                state.dismissals += 1
                focused = true
            }) {
                PopoverProbeContent(state: state)
            })
            .frame(width: 420, height: 330)
    }
}

@available(macOS 14, *)
private struct PopoverProbeContent: View {
    @ObservedObject var state: PopoverFocusState
    @FocusState private var focused: Bool

    var body: some View {
        Button("Close popover") { state.presented = false }
            .focusable(interactions: [.edit, .activate])
            .focused($focused)
            .onChange(of: focused) { _, newValue in state.panelFocused = newValue }
            .onDisappear { state.panelFocused = false }
            .onKeyPress(keys: [.return], phases: .down) { _ in
                state.presented = false
                return .handled
            }
            .defaultFocus($focused, true)
            .task {
                await Task.yield()
                focused = true
            }
    }
}

@available(macOS 14, *)
@main
struct MacPopoverFocusAudit {
    @MainActor
    static func main() {
        let app = NSApplication.shared
        app.setActivationPolicy(.regular)
        let returnOnly = CommandLine.arguments.contains("--return-only")
        let state = PopoverFocusState()
        let window = NSWindow(contentRect: NSRect(x: 0, y: 0, width: 420, height: 330),
                              styleMask: [.titled, .closable], backing: .buffered, defer: false)
        window.title = "Popover Focus Audit"
        window.contentView = NSHostingView(rootView: PopoverProbe(state: state))
        window.center()
        app.activate(ignoringOtherApps: true)
        window.makeKeyAndOrderFront(nil)

        Task { @MainActor in
            try? await Task.sleep(for: .milliseconds(240))
            NSRunningApplication.current.activate(options: [.activateAllWindows])
            window.makeKeyAndOrderFront(nil)
            guard await waitUntil({ app.isActive && window.isKeyWindow }) else {
                report("parent window did not become key", app: app, window: window, state: state)
                exit(2)
            }

            state.presented = true
            guard await waitUntil({ state.presented && state.panelFocused && app.keyWindow is NSPanel }) else {
                report("failed to open", app: app, window: window, state: state)
                exit(3)
            }
            report("opened", app: app, window: window, state: state)
            if returnOnly {
                press(36, characters: "\r", app: app)
                guard await waitUntil({ !state.presented && state.dismissals == 1 && state.triggerFocused && window.isKeyWindow }) else {
                    report("Return failed", app: app, window: window, state: state)
                    exit(7)
                }
                report("Return", app: app, window: window, state: state)
            } else {
                press(53, characters: "\u{1b}", app: app)
                guard await waitUntil({ !state.presented && state.dismissals == 1 && state.triggerFocused && window.isKeyWindow }) else {
                    report("Escape failed", app: app, window: window, state: state)
                    exit(4)
                }
                report("Escape", app: app, window: window, state: state)
                press(35, characters: "p", app: app)
                guard await waitUntil({ state.presented && state.panelFocused && app.keyWindow is NSPanel }) else {
                    report("failed to reopen", app: app, window: window, state: state)
                    exit(5)
                }
                report("reopened", app: app, window: window, state: state)
            }
            print(returnOnly ? "PASS: Return dismisses the Popover and restores focus"
                             : "PASS: Escape dismisses the Popover and the focused trigger reopens it")
            exit(0)
        }
        app.run()
    }

    @MainActor
    private static func press(_ code: UInt16, characters: String, app: NSApplication) {
        guard let key = app.keyWindow else { return }
        for type in [NSEvent.EventType.keyDown, .keyUp] {
            guard let event = NSEvent.keyEvent(
                with: type, location: .zero, modifierFlags: [],
                timestamp: ProcessInfo.processInfo.systemUptime,
                windowNumber: key.windowNumber, context: nil,
                characters: characters, charactersIgnoringModifiers: characters,
                isARepeat: false, keyCode: code
            ) else { return }
            app.sendEvent(event)
        }
    }

    @MainActor
    private static func waitUntil(_ expected: () -> Bool) async -> Bool {
        for _ in 0..<70 {
            if expected() { return true }
            try? await Task.sleep(for: .milliseconds(15))
        }
        return expected()
    }

    @MainActor
    private static func report(_ step: String, app: NSApplication, window: NSWindow, state: PopoverFocusState) {
        print(step, "presented=\(state.presented)", "panelFocused=\(state.panelFocused)",
              "triggerFocused=\(state.triggerFocused)", "dismissals=\(state.dismissals)",
              "parentKey=\(window.isKeyWindow)", "panelKey=\(app.keyWindow is NSPanel)",
              "active=\(app.isActive)")
    }
}
