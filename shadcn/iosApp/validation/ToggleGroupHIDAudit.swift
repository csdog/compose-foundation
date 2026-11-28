import SwiftUI
import UIKit

@main
final class ToggleGroupHIDAudit: NSObject, UIApplicationDelegate {}

@objc(IOSAuditSceneDelegate)
final class ToggleGroupHIDScene: NSObject, UIWindowSceneDelegate {
    var window: UIWindow?

    func scene(_ scene: UIScene, willConnectTo session: UISceneSession, options: UIScene.ConnectionOptions) {
        guard let scene = scene as? UIWindowScene else { return }
        let arguments = ProcessInfo.processInfo.arguments
        let window = UIWindow(windowScene: scene)
        self.window = window
        if #available(iOS 17, *) {
            window.rootViewController = UIHostingController(rootView: ToggleGroupHIDFixture(
                vertical: arguments.contains("--vertical"),
                rightToLeft: arguments.contains("--rtl")
            ))
        } else {
            window.rootViewController = UIViewController()
        }
        window.makeKeyAndVisible()
    }
}

@available(iOS 17, *)
private struct ToggleGroupHIDFixture: View {
    let vertical: Bool
    let rightToLeft: Bool
    @State private var selection: Set<String> = []
    @State private var afterActions = 0
    @FocusState private var afterFocused: Bool

    var body: some View {
        VStack(alignment: .leading, spacing: 12) {
            Text("Selected: \(selection.isEmpty ? "none" : selection.sorted().joined(separator: ", "))")
            FoundationToggleGroup([
                FoundationChoice("Bold", value: "bold"),
                FoundationChoice("Italic", value: "italic", isEnabled: false),
                FoundationChoice("Underline", value: "underline"),
            ], selection: $selection, orientation: vertical ? .vertical : .horizontal,
               variant: .outline, allowsMultiple: true) { choice in
                Text(choice.title)
            }
            Button("After group: \(afterActions)") { afterActions += 1 }
                .buttonStyle(PlainButtonStyle())
                .focusable()
                .focused($afterFocused)
                .onKeyPress(.space) {
                    afterActions += 1
                    return .handled
                }
        }
        .padding(24)
        .foundationTheme(FoundationTheme(preset: .nova))
        .environment(\.layoutDirection, rightToLeft ? .rightToLeft : .leftToRight)
    }
}
