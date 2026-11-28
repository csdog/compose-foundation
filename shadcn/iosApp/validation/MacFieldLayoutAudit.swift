import AppKit
import SwiftUI

private struct ControlGeometry: Equatable, Sendable {
    let identity: UUID
    let origin: CGPoint
}

private struct ControlGeometryKey: PreferenceKey {
    static let defaultValue: ControlGeometry? = nil
    static func reduce(value: inout ControlGeometry?, nextValue: () -> ControlGeometry?) {
        value = nextValue() ?? value
    }
}

@MainActor
private final class FieldLayoutAuditState: ObservableObject {
    @Published var width: CGFloat = 512
    @Published var rightToLeft = false
    var geometry: ControlGeometry?
}

private struct FieldLayoutAuditControl: View {
    @State private var identity = UUID()

    var body: some View {
        TextField("Name", text: .constant(""))
            .frame(width: 128, height: 28)
            .background {
                GeometryReader { geometry in
                    Color.clear.preference(key: ControlGeometryKey.self, value: ControlGeometry(
                        identity: identity,
                        origin: geometry.frame(in: .named("form-field")).origin
                    ))
                }
            }
    }
}

private struct FieldLayoutAuditFixture: View {
    @ObservedObject var state: FieldLayoutAuditState
    let horizontal: Bool

    var body: some View {
        FoundationField("Name", description: "Provide your full name for identification",
                        orientation: horizontal ? .horizontal : .responsive,
                        controlPlacement: horizontal ? .leading : .trailing) {
            FieldLayoutAuditControl()
        }
        .frame(width: state.width, alignment: .leading)
        .coordinateSpace(name: "form-field")
        .onPreferenceChange(ControlGeometryKey.self) { geometry in
            Task { @MainActor in state.geometry = geometry }
        }
        .environment(\.layoutDirection, state.rightToLeft ? .rightToLeft : .leftToRight)
        .foundationTheme(FoundationTheme(preset: .nova))
        .frame(width: 640, height: 200)
    }
}

@main @MainActor
enum MacFieldLayoutAudit {
    static func main() {
        let app = NSApplication.shared
        let state = FieldLayoutAuditState()
        let horizontal = CommandLine.arguments.contains("--horizontal")
        let window = NSWindow(contentRect: NSRect(x: 0, y: 0, width: 640, height: 200),
                              styleMask: [.titled, .closable], backing: .buffered, defer: false)
        window.contentView = NSHostingView(rootView: FieldLayoutAuditFixture(state: state, horizontal: horizontal))
        app.setActivationPolicy(.regular)
        window.center()
        window.makeKeyAndOrderFront(nil)

        Task { @MainActor in
            app.activate(ignoringOtherApps: true)
            NSRunningApplication.current.activate(options: [.activateAllWindows])
            window.makeKeyAndOrderFront(nil)
            let wide = await geometry(state)
            if horizontal {
                require(wide.origin.x < 4 && wide.origin.y < 4, "Leading horizontal control sits beside the label")
            } else {
                require(wide.origin.x > 70 && wide.origin.y < 4, "Wide responsive control sits beside the label")
                state.width = 320
                let compact = await geometry(state)
                require(compact.origin.x < 4 && compact.origin.y > 24, "Narrow responsive control moves below the label")
                require(compact.identity == wide.identity, "Responsive layout preserves control state")
                state.width = 512
                state.rightToLeft = true
                let rtl = await geometry(state)
                require(rtl.origin.x + 128 < 300 && rtl.origin.y < 4, "RTL moves the trailing control to the left")
                require(rtl.identity == wide.identity, "Returning to a wide layout preserves control state")
            }
            print("PASS: Field \(horizontal ? "horizontal" : "responsive width, RTL and state identity")")
            exit(0)
        }
        app.run()
    }

    private static func geometry(_ state: FieldLayoutAuditState) async -> ControlGeometry {
        try? await Task.sleep(for: .milliseconds(500))
        guard let geometry = state.geometry else {
            print("FAIL: missing control layout coordinates")
            exit(1)
        }
        return geometry
    }

    private static func require(_ condition: Bool, _ message: String) {
        guard condition else {
            print("FAIL: \(message)")
            exit(1)
        }
    }
}
