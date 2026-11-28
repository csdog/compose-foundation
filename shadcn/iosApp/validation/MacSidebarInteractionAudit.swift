import AppKit
import SwiftUI

private struct SidebarEdgeKey: PreferenceKey {
    static let defaultValue: CGFloat? = nil

    static func reduce(value: inout CGFloat?, nextValue: () -> CGFloat?) {
        value = nextValue() ?? value
    }
}

@MainActor
private final class SidebarAuditState: ObservableObject {
    @Published var isOpen = false
    var railEdge: CGFloat?
}

private struct SidebarInteractionFixture: View {
    @ObservedObject var state: SidebarAuditState
    let width: CGFloat
    let rightToLeft: Bool

    var body: some View {
        FoundationSidebar(isOpen: $state.isOpen, side: .trailing, width: 160, compactWidth: 400) {
            Text("Projects")
                .frame(maxWidth: .infinity, alignment: .leading)
                .background {
                    GeometryReader { geometry in
                        Color.clear.preference(key: SidebarEdgeKey.self,
                                               value: geometry.frame(in: .named("sidebar-audit")).minX)
                    }
                }
        } content: {
            FoundationSidebarTrigger(isOpen: $state.isOpen) { Text("Toggle Sidebar") }
        }
        .frame(width: width, height: 220)
        .coordinateSpace(name: "sidebar-audit")
        .onPreferenceChange(SidebarEdgeKey.self) { edge in
            Task { @MainActor in state.railEdge = edge }
        }
        .environment(\.layoutDirection, rightToLeft ? .rightToLeft : .leftToRight)
    }
}

@main @MainActor
struct MacSidebarInteractionAudit: App {
    @StateObject private var state = SidebarAuditState()
    private let compact = CommandLine.arguments.contains("--compact")
    private let rightToLeft = CommandLine.arguments.contains("--rtl")

    var body: some Scene {
        WindowGroup {
            SidebarInteractionFixture(state: state, width: compact ? 320 : 540, rightToLeft: rightToLeft)
                .task { await run() }
        }
        .windowResizability(.contentSize)
    }

    private func run() async {
        NSApp.activate(ignoringOtherApps: true)
        for _ in 0..<10 where NSApp.keyWindow == nil {
            try? await Task.sleep(for: .milliseconds(200))
        }
        guard let window = NSApp.keyWindow else { fail("The audit window needs focus") }
        await press(code: 11, text: "b", modifiers: .command, window: window)
        guard state.isOpen, let edge = state.railEdge else { fail("Command+B must open the sidebar") }
        guard rightToLeft ? edge < 40 : edge > 120 else { fail("Trailing sidebar appeared at x=\(edge)") }

        if compact {
            await press(code: 53, text: "\u{1B}", modifiers: [], window: window)
            guard !state.isOpen else { fail("Escape must close the compact sidebar") }
            await press(code: 11, text: "b", modifiers: .command, window: window)
            guard state.isOpen else { fail("Command+B must reopen the compact sidebar") }
        }

        await press(code: 11, text: "b", modifiers: .command, window: window)
        guard !state.isOpen else { fail("Command+B must close the sidebar") }
        print("PASS: Sidebar trailing edge and keyboard, compact=\(compact), RTL=\(rightToLeft)")
        exit(0)
    }

    private func press(code: UInt16, text: String, modifiers: NSEvent.ModifierFlags, window: NSWindow) async {
        for type in [NSEvent.EventType.keyDown, .keyUp] {
            if let event = NSEvent.keyEvent(with: type, location: .zero, modifierFlags: modifiers,
                                           timestamp: ProcessInfo.processInfo.systemUptime,
                                           windowNumber: window.windowNumber, context: nil,
                                           characters: text, charactersIgnoringModifiers: text,
                                           isARepeat: false, keyCode: code) { NSApp.sendEvent(event) }
        }
        try? await Task.sleep(for: .milliseconds(350))
    }

    private func fail(_ message: String) -> Never {
        print("FAIL: \(message)")
        exit(1)
    }
}
