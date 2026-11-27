import AppKit
import SwiftUI

@MainActor
private final class SliderResult: ObservableObject {
    @Published var value = 20.0
    @Published var isEnabled = true
}

@available(macOS 14, *)
private struct SliderProbe: View {
    @ObservedObject var result: SliderResult
    let rtl: Bool

    var body: some View {
        FoundationSlider("Value", value: $result.value, in: 0...100, step: 2)
            .disabled(!result.isEnabled)
            .foundationTheme(FoundationTheme(preset: .nova))
            .environment(\.layoutDirection, rtl ? .rightToLeft : .leftToRight)
            .frame(width: 320)
            .frame(width: 360, height: 120)
    }
}

@available(macOS 14, *)
@main @MainActor
enum MacSliderInteractionAudit {
    static func main() {
        let app = NSApplication.shared
        app.setActivationPolicy(.regular)
        let rtl = CommandLine.arguments.contains("--rtl")
        let result = SliderResult()
        let window = NSWindow(
            contentRect: NSRect(x: 0, y: 0, width: 360, height: 120),
            styleMask: [.titled, .closable], backing: .buffered, defer: false
        )
        window.contentView = NSHostingView(rootView: SliderProbe(result: result, rtl: rtl))
        window.center()
        window.makeKeyAndOrderFront(nil)

        Task { @MainActor in
            app.activate()
            NSRunningApplication.current.activate(options: [.activateAllWindows])
            window.makeKeyAndOrderFront(nil)
            try? await Task.sleep(for: .milliseconds(400))
            guard window.isKeyWindow else { fail("Window did not receive keyboard focus") }

            @MainActor func expect(_ value: Double, after action: String) {
                guard result.value == value else {
                    fail("\(action): expected \(value), got \(result.value)")
                }
            }

            await mouse(.leftMouseDown, at: 180, window: window)
            await mouse(.leftMouseUp, at: 180, window: window)
            expect(50, after: "Track click")
            await press(124, "\u{F703}", window: window)
            expect(rtl ? 48 : 52, after: "Right Arrow")
            await press(123, "\u{F702}", window: window)
            expect(50, after: "Left Arrow")
            await press(126, "\u{F700}", window: window)
            expect(52, after: "Up Arrow")
            await press(125, "\u{F701}", window: window)
            expect(50, after: "Down Arrow")
            await press(115, "\u{F729}", window: window)
            expect(0, after: "Home")
            await press(119, "\u{F72B}", window: window)
            expect(100, after: "End")

            await mouse(.leftMouseDown, at: 180, window: window)
            await mouse(.leftMouseDragged, at: 334, window: window)
            expect(rtl ? 0 : 100, after: "Drag while pressed")
            await mouse(.leftMouseUp, at: 334, window: window)

            result.isEnabled = false
            try? await Task.sleep(for: .milliseconds(150))
            let disabledValue = result.value
            await mouse(.leftMouseDown, at: 180, window: window)
            await mouse(.leftMouseUp, at: 180, window: window)
            await press(124, "\u{F703}", window: window)
            expect(disabledValue, after: "Disabled click and key press")
            print("PASS: Nova Slider mouse and keyboard \(rtl ? "RTL" : "LTR")")
            exit(0)
        }
        app.run()
    }

    private static func fail(_ message: String) -> Never {
        print("FAIL: \(message)")
        exit(1)
    }

    private static func mouse(_ type: NSEvent.EventType, at x: CGFloat, window: NSWindow) async {
        if let event = NSEvent.mouseEvent(
            with: type, location: NSPoint(x: x, y: 60), modifierFlags: [],
            timestamp: ProcessInfo.processInfo.systemUptime, windowNumber: window.windowNumber,
            context: nil, eventNumber: 0, clickCount: 1, pressure: type == .leftMouseUp ? 0 : 1
        ) {
            NSApplication.shared.postEvent(event, atStart: false)
        }
        try? await Task.sleep(for: .milliseconds(140))
    }

    private static func press(_ code: UInt16, _ characters: String, window: NSWindow) async {
        for type in [NSEvent.EventType.keyDown, .keyUp] {
            if let event = NSEvent.keyEvent(
                with: type, location: .zero, modifierFlags: [],
                timestamp: ProcessInfo.processInfo.systemUptime,
                windowNumber: window.windowNumber, context: nil,
                characters: characters, charactersIgnoringModifiers: characters,
                isARepeat: false, keyCode: code
            ) {
                NSApplication.shared.postEvent(event, atStart: false)
            }
        }
        try? await Task.sleep(for: .milliseconds(140))
    }
}
