import SwiftUI
import UIKit

@main
final class RadioGroupHIDAudit: NSObject, UIApplicationDelegate {}

@objc(IOSAuditSceneDelegate)
final class RadioGroupHIDScene: NSObject, UIWindowSceneDelegate {
    var window: UIWindow?

    func scene(_ scene: UIScene, willConnectTo session: UISceneSession, options: UIScene.ConnectionOptions) {
        guard let scene = scene as? UIWindowScene else { return }
        let window = UIWindow(windowScene: scene)
        self.window = window
        window.rootViewController = UIHostingController(rootView: RadioGroupHIDFixture(
            rightToLeft: ProcessInfo.processInfo.arguments.contains("--rtl")
        ))
        window.makeKeyAndVisible()
    }
}

private struct RadioGroupHIDFixture: View {
    let rightToLeft: Bool
    @State private var selection: String?

    var body: some View {
        VStack(alignment: .leading, spacing: 12) {
            Text("Selected: \(selection ?? "none")")
            FoundationRadioGroup([
                FoundationChoice("Regular", value: "regular"),
                FoundationChoice("Restricted", value: "restricted", isEnabled: false),
                FoundationChoice("Compact", value: "compact"),
            ], selection: $selection)
        }
        .padding(24)
        .foundationTheme(FoundationTheme(preset: .nova))
        .environment(\.layoutDirection, rightToLeft ? .rightToLeft : .leftToRight)
    }
}
