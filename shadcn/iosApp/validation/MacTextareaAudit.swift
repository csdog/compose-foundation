import AppKit
import SwiftUI

@MainActor
private final class TextareaAuditState: ObservableObject {
    @Published var value = ""
    @Published var other = ""
    @Published var invalid = false
}

private struct TextareaAuditFixture: View {
    @ObservedObject var state: TextareaAuditState

    var body: some View {
        var light = FoundationPalette.neutralLight
        light.ring = .red
        let theme = FoundationTheme(preset: .nova, light: light)
        return VStack(spacing: 24) {
            FoundationTextarea("Message", text: $state.value, placeholder: "Enter a message", invalid: state.invalid)
                .frame(width: 240, height: 80)
            TextField("Other field", text: $state.other)
                .frame(width: 180, height: 44)
        }
        .frame(width: 400, height: 260)
        .background(.white)
        .environment(\.colorScheme, .light)
        .foundationTheme(theme)
    }
}

@main @MainActor
enum MacTextareaAudit {
    static func main() {
        let app = NSApplication.shared
        let state = TextareaAuditState()
        let host = NSHostingView(rootView: TextareaAuditFixture(state: state))
        let window = NSWindow(contentRect: NSRect(x: 0, y: 0, width: 400, height: 260),
                              styleMask: [.titled, .closable], backing: .buffered, defer: false)
        window.contentView = host
        app.setActivationPolicy(.regular)
        window.center()
        window.makeKeyAndOrderFront(nil)

        Task { @MainActor in
            app.activate()
            NSRunningApplication.current.activate(options: [.activateAllWindows])
            window.makeKeyAndOrderFront(nil)
            try? await Task.sleep(for: .milliseconds(350))
            require(window.isKeyWindow, "The audit window needs focus")
            let idle = ringPixels(in: host)

            await click(x: 130, y: 164, in: window)
            require(ringPixels(in: host) > idle + 10, "The editor must draw a focus ring")
            for (keyCode, text) in [(UInt16(4), "h"), (UInt16(36), "\r"), (UInt16(34), "i")] {
                press(keyCode, text, in: window)
                try? await Task.sleep(for: .milliseconds(120))
            }
            require(state.value == "h\ni", "Native keyboard events must update the multiline binding")

            await click(x: 200, y: 78, in: window)
            require(ringPixels(in: host) == idle, "Moving focus to another field must clear the ring")
            state.invalid = true
            try? await Task.sleep(for: .milliseconds(160))
            require(ringPixels(in: host) > idle + 10, "The invalid ring must appear without focus")
            require(state.value == "h\ni", "Changing validation state must preserve the text")
            print("PASS: Textarea focus, multiline editing, focus removal, and invalid state")
            exit(0)
        }
        app.run()
    }

    private static func ringPixels(in view: NSView) -> Int {
        view.layoutSubtreeIfNeeded()
        view.displayIfNeeded()
        guard let bitmap = view.bitmapImageRepForCachingDisplay(in: view.bounds) else { return 0 }
        view.cacheDisplay(in: view.bounds, to: bitmap)
        let x = Int(78 * CGFloat(bitmap.pixelsWide) / view.bounds.width)
        return (0..<bitmap.pixelsHigh).filter { y in
            guard let color = bitmap.colorAt(x: x, y: y)?.usingColorSpace(.sRGB) else { return false }
            return color.redComponent - color.greenComponent > 0.13
        }.count
    }

    private static func click(x: CGFloat, y: CGFloat, in window: NSWindow) async {
        for type in [NSEvent.EventType.leftMouseDown, .leftMouseUp] {
            guard let event = NSEvent.mouseEvent(
                with: type, location: NSPoint(x: x, y: y), modifierFlags: [],
                timestamp: ProcessInfo.processInfo.systemUptime, windowNumber: window.windowNumber,
                context: nil, eventNumber: 0, clickCount: 1, pressure: type == .leftMouseUp ? 0 : 1
            ) else { continue }
            NSApplication.shared.postEvent(event, atStart: false)
            try? await Task.sleep(for: .milliseconds(130))
        }
    }

    private static func press(_ keyCode: UInt16, _ text: String, in window: NSWindow) {
        for type in [NSEvent.EventType.keyDown, .keyUp] {
            guard let event = NSEvent.keyEvent(
                with: type, location: .zero, modifierFlags: [],
                timestamp: ProcessInfo.processInfo.systemUptime, windowNumber: window.windowNumber,
                context: nil, characters: text, charactersIgnoringModifiers: text,
                isARepeat: false, keyCode: keyCode
            ) else { continue }
            NSApplication.shared.postEvent(event, atStart: false)
        }
    }

    private static func require(_ condition: Bool, _ message: String) {
        if !condition {
            print("FAIL: \(message)")
            exit(1)
        }
    }
}
