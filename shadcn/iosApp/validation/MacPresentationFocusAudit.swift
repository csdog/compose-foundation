import AppKit
import SwiftUI

@MainActor
private final class PresentationResult: ObservableObject {
    @Published var presented = false
    @Published var name = "Pedro"
    var triggerFocused = false
    var dismissals = 0
    var backgroundActivations = 0
}

private enum PresentationKind: String {
    case drawer, dialog, sheet
    case drawerNova = "drawer-nova"
    case drawerRhea = "drawer-rhea"
    case dialogCentered = "dialog-centered"
    case sheetTrailing = "sheet-trailing"
    case sheetLeading = "sheet-leading"
    case sheetTop = "sheet-top"
    case sheetBottom = "sheet-bottom"
    case sheetRTL = "sheet-rtl"

    var sheetEdge: Edge {
        switch self {
        case .sheetLeading: return .leading
        case .sheetTop: return .top
        case .sheetBottom: return .bottom
        default: return .trailing
        }
    }
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
        case .drawerNova, .drawerRhea:
            trigger.modifier(FoundationDrawer(
                isPresented: $state.presented, title: "Audit drawer", description: "A footer-close layout",
                closeTitle: "Cancel", onDismiss: dismissed
            ) {
                content
            })
            .foundationTheme(FoundationTheme(preset: kind == .drawerNova ? .nova : .rhea))
        case .dialog:
            trigger.modifier(FoundationDialog(isPresented: $state.presented, title: "Audit dialog", onDismiss: dismissed) {
                content
            })
        case .dialogCentered:
            trigger.modifier(FoundationDialog(
                isPresented: $state.presented, title: "Audit dialog", description: "A centered layout",
                presentation: .centered, onDismiss: dismissed
            ) {
                TextField("Name", text: $state.name)
                    .textFieldStyle(.plain)
                    .frame(height: 44)
            })
        case .sheetTrailing, .sheetLeading, .sheetTop, .sheetBottom, .sheetRTL:
            trigger.modifier(FoundationSheet(
                isPresented: $state.presented, title: "Audit sheet", description: "An edge presentation",
                side: kind.sheetEdge, onDismiss: dismissed
            ) {
                if kind == .sheetRTL {
                    FoundationInput("الاسم", text: $state.name)
                        .foundationTheme(FoundationTheme(preset: .nova))
                } else {
                    TextField("Name", text: $state.name)
                        .textFieldStyle(.plain)
                        .frame(height: 44)
                }
            }.maximumPanelHeight(kind == .sheetTop || kind == .sheetBottom ? 220 : 330))
            .environment(\.layoutDirection, kind == .sheetRTL ? .rightToLeft : .leftToRight)
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
        let reduceMotion = CommandLine.arguments.contains("--reduce-motion")
        window.contentView = NSHostingView(rootView: PresentationProbe(state: state, kind: kind)
            .environment(\.accessibilityReduceMotion, reduceMotion))
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
            if kind == .dialogCentered || kind == .sheetTrailing || kind == .sheetRTL {
                press(2, "d", app: app)
                guard await waitUntil({ state.presented }) else { fail("Could not reopen the presentation", state) }
                try? await Task.sleep(for: .milliseconds(300))
                let field = kind == .dialogCentered ? NSPoint(x: 85, y: 134) : NSPoint(x: 205, y: 225)
                click(field, app: app)
                try? await Task.sleep(for: .milliseconds(120))
                if kind == .sheetRTL {
                    guard let editor = window.firstResponder as? NSTextView, editor.isFieldEditor else {
                        fail("The RTL input did not activate its native field editor", state)
                    }
                    editor.setSelectedRange(NSRange(location: state.name.utf16.count, length: 0))
                    press(123, String(UnicodeScalar(NSLeftArrowFunctionKey)!), app: app)
                }
                press(0, "a", app: app)
                guard await waitUntil({
                    state.presented && (kind == .sheetRTL ? state.name == "Pedrao" : state.name.contains("a"))
                }) else {
                    fail("The form did not accept typed text at the expected cursor position", state)
                }
                click(NSPoint(x: kind == .sheetRTL ? 410 : 10, y: 10), app: app)
                guard await waitUntil({ !state.presented && state.dismissals == 3 && state.triggerFocused }) else {
                    fail("The backdrop did not dismiss and restore the trigger", state)
                }
                press(2, "d", app: app)
                guard await waitUntil({ state.presented }) else {
                    fail("The trigger did not receive the keyboard after backdrop dismissal", state)
                }
                try? await Task.sleep(for: .milliseconds(350))
                press(53, "\u{1b}", app: app)
                guard await waitUntil({ !state.presented && state.dismissals == 4 && state.triggerFocused }) else {
                    fail("The presentation did not restore focus after reopening and pressing Escape", state)
                }
                print("PASS: \(kind.rawValue) accepts form input and backdrop dismissal")
            }
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
        print("FAIL \(message): presented=\(state.presented), triggerFocused=\(state.triggerFocused), dismissals=\(state.dismissals), firstResponder=\(String(describing: NSApp.keyWindow?.firstResponder))")
        exit(1)
    }

    @MainActor
    private static func click(_ point: NSPoint, app: NSApplication) {
        guard let window = app.keyWindow else { return }
        for type in [NSEvent.EventType.leftMouseDown, .leftMouseUp] {
            if let event = NSEvent.mouseEvent(
                with: type, location: point, modifierFlags: [],
                timestamp: ProcessInfo.processInfo.systemUptime, windowNumber: window.windowNumber,
                context: nil, eventNumber: 0, clickCount: 1, pressure: type == .leftMouseUp ? 0 : 1
            ) {
                app.postEvent(event, atStart: false)
            }
        }
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
