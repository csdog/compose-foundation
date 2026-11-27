import AppKit
import SwiftUI

@MainActor
private final class TabsResult: ObservableObject {
    @Published var selection = "Overview"
    @Published var isEnabled = true
    private(set) var selectionWrites = 0

    func select(_ value: String) {
        selectionWrites += 1
        selection = value
    }
}

@available(macOS 14, *)
private struct TabsProbe: View {
    @ObservedObject var result: TabsResult
    let manual: Bool
    let rtl: Bool

    var body: some View {
        FoundationTabs(
            [
                FoundationChoice("Overview", value: "Overview"),
                FoundationChoice("Restricted", value: "Restricted", isEnabled: false),
                FoundationChoice("Activity", value: "Activity"),
                FoundationChoice("Settings", value: "Settings"),
            ],
            selection: Binding(get: { result.selection }, set: { result.select($0) }),
            activationMode: manual ? .manual : .automatic
        ) { choice in
            Text("Panel: \(choice)")
        }
        .disabled(!result.isEnabled)
        .padding()
        .environment(\.layoutDirection, rtl ? .rightToLeft : .leftToRight)
        .frame(width: 420, height: 160)
    }
}

@available(macOS 14, *)
@main
struct MacTabsKeyboardAudit {
    @MainActor
    static func main() {
        let app = NSApplication.shared
        app.setActivationPolicy(.regular)
        let manual = CommandLine.arguments.contains("--manual")
        let rtl = CommandLine.arguments.contains("--rtl")
        let result = TabsResult()
        let window = NSWindow(
            contentRect: NSRect(x: 0, y: 0, width: 420, height: 160),
            styleMask: [.titled, .closable], backing: .buffered, defer: false
        )
        window.title = "Tabs Keyboard Audit"
        window.contentView = NSHostingView(rootView: TabsProbe(result: result, manual: manual, rtl: rtl))
        window.center()
        app.activate(ignoringOtherApps: true)
        window.makeKeyAndOrderFront(nil)

        Task { @MainActor in
            try? await Task.sleep(for: .milliseconds(240))
            app.activate()
            NSRunningApplication.current.activate(options: [.activateAllWindows])
            window.makeKeyAndOrderFront(nil)
            try? await Task.sleep(for: .milliseconds(240))
            guard window.isKeyWindow else { fail("Window did not receive keyboard focus") }

            let forward: (UInt16, String) = rtl ? (123, "\u{F702}") : (124, "\u{F703}")
            let backward: (UInt16, String) = rtl ? (124, "\u{F703}") : (123, "\u{F702}")
            func send(_ key: (UInt16, String)) async {
                press(key.0, key.1, in: window, app: app)
                try? await Task.sleep(for: .milliseconds(180))
            }
            func expectSelection(_ value: String, after action: String) {
                guard result.selection == value else {
                    fail("\(action): expected \(value), got \(result.selection)")
                }
            }

            await send((115, "\u{F729}")) // Home
            expectSelection("Overview", after: "Home")
            let writesBeforeForward = result.selectionWrites
            await send(forward)
            expectSelection(manual ? "Overview" : "Activity", after: "Forward skips Restricted")
            guard result.selectionWrites - writesBeforeForward == (manual ? 0 : 1) else {
                fail("Forward produced an unexpected number of selection writes")
            }
            if manual {
                await send((49, " "))
                expectSelection("Activity", after: "Space activates the focused tab")
                guard result.selectionWrites == writesBeforeForward + 1 else {
                    fail("Space did not write the selection exactly once")
                }
            }
            await send((119, "\u{F72B}")) // End
            expectSelection(manual ? "Activity" : "Settings", after: "End")
            if manual {
                await send((36, "\r"))
                expectSelection("Settings", after: "Return activates the focused tab")
            }
            await send(forward)
            expectSelection(manual ? "Settings" : "Overview", after: "Forward wraps from last to first")
            if manual {
                await send((36, "\r"))
                expectSelection("Overview", after: "Return activates the wrapped tab")
            }
            await send(backward)
            expectSelection(manual ? "Overview" : "Settings", after: "Backward wraps from first to last")
            if manual {
                await send((36, "\r"))
                expectSelection("Settings", after: "Return activates the last tab")
            }
            await send((115, "\u{F729}"))
            expectSelection(manual ? "Settings" : "Overview", after: "Home moves from last to first")
            if manual {
                await send((36, "\r"))
                expectSelection("Overview", after: "Return activates the first tab after Home")
            }

            result.isEnabled = false
            try? await Task.sleep(for: .milliseconds(180))
            let writesBeforeDisabledInput = result.selectionWrites
            await send(forward)
            await send((119, "\u{F72B}")) // End
            await send((49, " "))
            await send((36, "\r"))
            expectSelection("Overview", after: "Keyboard input with the tab strip disabled")
            guard result.selectionWrites == writesBeforeDisabledInput else {
                fail("Disabled tabs wrote to the selection binding")
            }
            print("PASS: Tabs \(manual ? "manual" : "automatic") \(rtl ? "RTL" : "LTR")")
            exit(0)
        }
        app.run()
    }

    private static func fail(_ message: String) -> Never {
        print("FAIL: \(message)")
        exit(1)
    }

    @MainActor
    private static func press(_ code: UInt16, _ characters: String, in window: NSWindow, app: NSApplication) {
        for type in [NSEvent.EventType.keyDown, .keyUp] {
            if let event = NSEvent.keyEvent(
                with: type, location: .zero, modifierFlags: [],
                timestamp: ProcessInfo.processInfo.systemUptime,
                windowNumber: window.windowNumber, context: nil,
                characters: characters, charactersIgnoringModifiers: characters,
                isARepeat: false, keyCode: code
            ) {
                app.sendEvent(event)
            }
        }
    }
}
