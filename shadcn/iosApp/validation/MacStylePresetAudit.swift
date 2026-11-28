import AppKit
import SwiftUI

@MainActor
private final class StyleAuditState: ObservableObject {
    @Published var preset: FoundationStylePreset = .nova
    @Published var scheme: ColorScheme = .light
    @Published var name = ""
    @Published var saves = 0
}

private struct StyleAuditFixture: View {
    @ObservedObject var state: StyleAuditState

    var body: some View {
        let theme = FoundationTheme(preset: state.preset, light: marked(.neutralLight), dark: marked(.neutralDark))
        VStack(spacing: 12) {
            FoundationInput("Display name", text: $state.name)
                .frame(width: 240)
            Button("Save") { state.saves += 1 }
                .buttonStyle(FoundationButtonStyle())
        }
        .frame(width: 480, height: 260)
        .background(theme.palette(for: state.scheme).background)
        .environment(\.colorScheme, state.scheme)
        .foundationTheme(theme)
    }

    private func marked(_ palette: FoundationPalette) -> FoundationPalette {
        let marker = Color(red: 1, green: 0.05, blue: 0.9)
        var result = palette
        result.input = marker
        result.ring = marker
        return result
    }
}

@main @MainActor
enum MacStylePresetAudit {
    static func main() {
        let app = NSApplication.shared
        let state = StyleAuditState()
        let host = NSHostingView(rootView: StyleAuditFixture(state: state))
        let window = NSWindow(contentRect: NSRect(x: 0, y: 0, width: 480, height: 260),
                              styleMask: [.titled, .closable], backing: .buffered, defer: false)
        window.contentView = host
        app.setActivationPolicy(.regular)
        window.center()
        window.makeKeyAndOrderFront(nil)
        let heights: [FoundationStylePreset: CGFloat] = [
            .vega: 36, .nova: 32, .maia: 36, .lyra: 32,
            .mira: 28, .luma: 36, .sera: 40, .rhea: 32,
        ]

        Task { @MainActor in
            app.activate()
            NSRunningApplication.current.activate(options: [.activateAllWindows])
            window.makeKeyAndOrderFront(nil)
            try? await Task.sleep(for: .milliseconds(350))
            require(window.isKeyWindow, "The audit window needs focus")

            await click(x: 151, y: 158, in: window)
            typeLetter(in: window)
            try? await Task.sleep(for: .milliseconds(150))
            require(state.name == "h", "Typing must update the input binding")
            await click(x: 240, y: 102, in: window)
            require(state.saves == 1, "The Save button must use the live view state")

            for scheme in [ColorScheme.light, ColorScheme.dark] {
                state.scheme = scheme
                for preset in FoundationStylePreset.allCases {
                    state.preset = preset
                    try? await Task.sleep(for: .milliseconds(140))
                    let expected = heights[preset]!
                    require(preset.metrics.inputHeight == expected, "Unexpected \(preset) input height")
                    let stroke = strokeSpan(in: host)
                    if preset == .sera {
                        require(stroke > 0 && stroke <= 2.5, "Sera should draw only an underline, got \(stroke) pt")
                    } else {
                        require(abs(stroke - expected) <= 1.5, "\(preset) should render a \(expected) pt outline, got \(stroke) pt")
                    }
                    require(state.name == "h" && state.saves == 1, "Changing the preset or appearance must preserve form state")
                    print("Measured \(scheme) \(preset.rawValue): \(stroke) pt")
                }
            }
            print("PASS: Eight style presets in both themes preserve live input and save state")
            exit(0)
        }
        app.run()
    }

    private static func strokeSpan(in view: NSView) -> CGFloat {
        view.layoutSubtreeIfNeeded()
        view.displayIfNeeded()
        guard let image = view.bitmapImageRepForCachingDisplay(in: view.bounds) else { return 0 }
        view.cacheDisplay(in: view.bounds, to: image)
        let center = image.pixelsWide / 2
        let rows = (0..<image.pixelsHigh).filter { y in
            guard let color = image.colorAt(x: center, y: y)?.usingColorSpace(.sRGB) else { return false }
            return color.redComponent > 0.85 && color.blueComponent > 0.75 && color.greenComponent < 0.25
        }
        guard let first = rows.first, let last = rows.last else { return 0 }
        return CGFloat(last - first + 1) * view.bounds.height / CGFloat(image.pixelsHigh)
    }

    private static func typeLetter(in window: NSWindow) {
        for type in [NSEvent.EventType.keyDown, .keyUp] {
            guard let event = NSEvent.keyEvent(
                with: type, location: .zero, modifierFlags: [],
                timestamp: ProcessInfo.processInfo.systemUptime, windowNumber: window.windowNumber,
                context: nil, characters: "h", charactersIgnoringModifiers: "h", isARepeat: false, keyCode: 4
            ) else { continue }
            NSApplication.shared.postEvent(event, atStart: false)
        }
    }

    private static func click(x: CGFloat, y: CGFloat, in window: NSWindow) async {
        for type in [NSEvent.EventType.leftMouseDown, .leftMouseUp] {
            guard let event = NSEvent.mouseEvent(
                with: type, location: NSPoint(x: x, y: y), modifierFlags: [],
                timestamp: ProcessInfo.processInfo.systemUptime, windowNumber: window.windowNumber,
                context: nil, eventNumber: 0, clickCount: 1, pressure: type == .leftMouseUp ? 0 : 1
            ) else { continue }
            NSApplication.shared.postEvent(event, atStart: false)
            try? await Task.sleep(for: .milliseconds(120))
        }
    }

    private static func require(_ condition: Bool, _ message: String) {
        if !condition {
            print("FAIL: \(message)")
            exit(1)
        }
    }
}
