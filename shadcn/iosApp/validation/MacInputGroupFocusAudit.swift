import AppKit
import SwiftUI

@MainActor
private final class InputGroupResult: ObservableObject {
    @Published var query = ""
    @Published var outside = ""
    var focused = false
    var actions = 0
}

private struct InputGroupProbe: View {
    private enum Field: Hashable { case query, outside }
    @ObservedObject var result: InputGroupResult
    @FocusState private var focus: Field?

    var body: some View {
        ZStack {
            Color.white
            FoundationInputGroup(axis: .horizontal, isFocused: focus == .query) {
                TextField("Search", text: $result.query).textFieldStyle(.plain)
                    .focused($focus, equals: .query)
            } leading: {
                Image(systemName: "magnifyingglass").accessibilityHidden(true)
            } trailing: {
                Button { result.actions += 1 } label: {
                    Text("Clear").frame(width: 48, height: 44).contentShape(Rectangle())
                }
                .buttonStyle(PlainButtonStyle())
            }
            .frame(width: 320)
            .position(x: 180, y: 72)
            TextField("Outside", text: $result.outside).textFieldStyle(.plain)
                .focused($focus, equals: .outside)
                .frame(width: 260, height: 44)
                .position(x: 180, y: 145)
        }
        .onChange(of: focus) { result.focused = $0 == .query }
        .environment(\.colorScheme, .light)
        .foundationTheme(FoundationTheme(preset: .nova))
        .frame(width: 360, height: 200)
    }
}

@main @MainActor
enum MacInputGroupFocusAudit {
    static func main() {
        let app = NSApplication.shared
        app.setActivationPolicy(.regular)
        let result = InputGroupResult()
        let window = NSWindow(contentRect: NSRect(x: 0, y: 0, width: 360, height: 200),
                              styleMask: [.titled, .closable], backing: .buffered, defer: false)
        let host = NSHostingView(rootView: InputGroupProbe(result: result))
        window.contentView = host
        window.center()
        window.makeKeyAndOrderFront(nil)

        Task { @MainActor in
            app.activate()
            NSRunningApplication.current.activate(options: [.activateAllWindows])
            window.makeKeyAndOrderFront(nil)
            try? await Task.sleep(for: .milliseconds(420))
            require(window.isKeyWindow, "The audit window needs focus")
            await click(x: 95, y: 55, in: window)
            let before = ringPixels(in: host)
            await click(x: 95, y: 128, in: window)
            require(result.focused, "Clicking the editor focuses it")
            require(ringPixels(in: host) > before + 8, "Focused editor draws a shared outer ring")
            typeLetter(in: window)
            try? await Task.sleep(for: .milliseconds(160))
            require(result.query == "h", "Native key events update the editor binding")
            await click(x: 305, y: 128, in: window)
            require(result.actions == 1, "The addon button runs independently")
            await click(x: 95, y: 55, in: window)
            require(!result.focused && ringPixels(in: host) == before, "Moving focus out clears the group ring")
            print("PASS: Input Group shows the editor focus ring and preserves text entry and independent addon actions")
            exit(0)
        }
        app.run()
    }

    private static func ringPixels(in view: NSView) -> Int {
        guard let bitmap = view.bitmapImageRepForCachingDisplay(in: view.bounds) else { return 0 }
        view.cacheDisplay(in: view.bounds, to: bitmap)
        let scale = CGFloat(bitmap.pixelsWide) / view.bounds.width
        let column = Int(18 * scale)
        return (0..<bitmap.pixelsHigh).filter { y in
            guard let color = bitmap.colorAt(x: column, y: y)?.usingColorSpace(.sRGB) else { return false }
            return color.redComponent < 0.97 && color.alphaComponent > 0.9
        }.count
    }

    private static func typeLetter(in window: NSWindow) {
        for type in [NSEvent.EventType.keyDown, .keyUp] {
            if let event = NSEvent.keyEvent(with: type, location: .zero, modifierFlags: [],
                                            timestamp: ProcessInfo.processInfo.systemUptime, windowNumber: window.windowNumber,
                                            context: nil, characters: "h", charactersIgnoringModifiers: "h",
                                            isARepeat: false, keyCode: 4) {
                NSApplication.shared.postEvent(event, atStart: false)
            }
        }
    }

    private static func require(_ condition: Bool, _ message: String) {
        guard condition else { print("FAIL: \(message)"); exit(1) }
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
}
