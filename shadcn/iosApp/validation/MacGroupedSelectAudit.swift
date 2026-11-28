import AppKit
import SwiftUI

@MainActor
private final class SelectAuditState: NSObject, ObservableObject {
    @Published var selection: String?
    var presentationCount = 0
    private var trackedMenu: NSMenu?

    @objc func menuDidOpen(_ notification: Notification) {
        guard let menu = notification.object as? NSMenu,
              menu.item(withTitle: "Carrot") != nil else { return }
        trackedMenu = menu
        presentationCount += 1
        print("Tracking native menu \(presentationCount): \(menu.items.map(\.title))")
        let timer = Timer(timeInterval: 0.12, target: self, selector: #selector(resolveMenu(_:)),
                          userInfo: nil, repeats: false)
        RunLoop.main.add(timer, forMode: .eventTracking)
        RunLoop.main.add(timer, forMode: .default)
    }

    @objc private func resolveMenu(_ timer: Timer) {
        guard let menu = trackedMenu else { return }
        defer {
            menu.cancelTracking()
            trackedMenu = nil
        }
        guard presentationCount == 1 else { return }

        let titles = menu.items.map(\.title)
        require(titles.contains("Fruit") && titles.contains("Vegetables") && !titles.contains("Empty group"),
                "Expected only nonempty native section labels, found \(titles)")
        require(menu.item(withTitle: "Apricot")?.isEnabled == false,
                "The disabled option must stay disabled in the native menu")
        guard let item = menu.item(withTitle: "Carrot"), item.isEnabled, let action = item.action else {
            require(false, "The enabled option must have a native action")
            return
        }
        require(NSApplication.shared.sendAction(action, to: item.target, from: item), "The native action must dispatch")
    }
}

private struct SelectAuditFixture: View {
    @ObservedObject var state: SelectAuditState

    var body: some View {
        VStack(spacing: 0) {
            FoundationSelect("Choose produce", groups: [
                .init("Fruit", choices: [.init("Apple", value: "apple"), .init("Apricot", value: "apricot", isEnabled: false)]),
                .init("Empty group", choices: []),
                .init("Vegetables", choices: [.init("Carrot", value: "carrot")]),
            ], selection: $state.selection)
            .frame(width: 192)
            Spacer(minLength: 0)
        }
        .padding(.top, 70)
        .frame(width: 360, height: 400)
        .foundationTheme(FoundationTheme(preset: .nova))
    }
}

@main @MainActor
enum MacGroupedSelectAudit {
    static func main() {
        let app = NSApplication.shared
        let state = SelectAuditState()
        let window = NSWindow(contentRect: NSRect(x: 0, y: 0, width: 360, height: 400),
                              styleMask: [.titled, .closable], backing: .buffered, defer: false)
        window.contentView = NSHostingView(rootView: SelectAuditFixture(state: state))
        app.setActivationPolicy(.regular)
        window.center()
        window.makeKeyAndOrderFront(nil)
        NotificationCenter.default.addObserver(state, selector: #selector(SelectAuditState.menuDidOpen(_:)),
                                               name: NSMenu.didBeginTrackingNotification, object: nil)

        Task { @MainActor in
            app.activate()
            NSRunningApplication.current.activate(options: [.activateAllWindows])
            window.makeKeyAndOrderFront(nil)
            try? await Task.sleep(for: .milliseconds(400))
            require(window.isKeyWindow, "The audit window needs focus")

            await openSelect(in: window)
            try? await Task.sleep(for: .milliseconds(600))
            require(state.selection == "carrot", "The native action must update the binding after \(state.presentationCount) menu openings")

            await openSelect(in: window)
            try? await Task.sleep(for: .milliseconds(600))
            require(state.presentationCount == 2, "The native menu must reopen after selection")
            require(state.selection == "carrot", "Canceling the menu must preserve the binding")
            print("PASS: Grouped Select preserves native section labels, disabled choices, and the selected value on cancel")
            exit(0)
        }
        app.run()
    }

    private static func openSelect(in window: NSWindow) async {
        let location = NSPoint(x: 250, y: 308)
        let global = window.convertPoint(toScreen: location)
        let screenTop = NSScreen.screens.first?.frame.maxY ?? 0
        let pointer = CGPoint(x: global.x, y: screenTop - global.y)
        require(CGWarpMouseCursorPosition(pointer) == .success, "The pointer must move to the Select control")
        try? await Task.sleep(for: .milliseconds(100))

        for type in [NSEvent.EventType.leftMouseDown, .leftMouseUp] {
            guard let event = NSEvent.mouseEvent(
                with: type, location: location, modifierFlags: [],
                timestamp: ProcessInfo.processInfo.systemUptime, windowNumber: window.windowNumber,
                context: nil, eventNumber: 0, clickCount: 1, pressure: type == .leftMouseUp ? 0 : 1
            ) else { continue }
            NSApplication.shared.postEvent(event, atStart: false)
            try? await Task.sleep(for: .milliseconds(140))
        }
    }
}

@MainActor
private func require(_ condition: Bool, _ message: String) {
    if !condition {
        print("FAIL: \(message)")
        exit(1)
    }
}
