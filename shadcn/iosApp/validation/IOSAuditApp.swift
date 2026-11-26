import SwiftUI
import UIKit

@main
final class IOSAuditApp: NSObject, UIApplicationDelegate {}

@objc(IOSAuditSceneDelegate)
final class IOSAuditSceneDelegate: NSObject, UIWindowSceneDelegate {
    var window: UIWindow?

    func scene(_ scene: UIScene, willConnectTo session: UISceneSession, options: UIScene.ConnectionOptions) {
        guard let windowScene = scene as? UIWindowScene else { return }
        let window = UIWindow(windowScene: windowScene)
        self.window = window

        if ProcessInfo.processInfo.arguments.contains("--combobox-interaction") {
            window.rootViewController = UIHostingController(rootView: ComboboxInteractionFixture())
            window.makeKeyAndVisible()
            return
        }

        if ProcessInfo.processInfo.arguments.contains("--choice-groups-interaction") {
            window.rootViewController = UIHostingController(rootView: ChoiceGroupsInteractionFixture())
            window.makeKeyAndVisible()
            return
        }

        if ProcessInfo.processInfo.arguments.contains("--calendar-navigation") {
            window.rootViewController = UIHostingController(rootView: ScrollView { CalendarNavigationFixture() })
            window.makeKeyAndVisible()
            return
        }

        if ProcessInfo.processInfo.arguments.contains("--carousel-interaction") {
            let rightToLeft = ProcessInfo.processInfo.arguments.contains("--carousel-rtl")
            window.rootViewController = UIHostingController(rootView: CarouselInteractionFixture(rightToLeft: rightToLeft))
            window.makeKeyAndVisible()
            return
        }

        if ProcessInfo.processInfo.arguments.contains("--theme-presets") {
            window.rootViewController = UIHostingController(rootView: ThemePresetsInteractionFixture())
            window.makeKeyAndVisible()
            return
        }

        if ProcessInfo.processInfo.arguments.contains("--menus-interaction") {
            window.rootViewController = UIHostingController(rootView: MenuGroupsInteractionFixture())
            window.makeKeyAndVisible()
            return
        }

        if #available(iOS 17, *), ProcessInfo.processInfo.arguments.contains("--overlay-focus") {
            window.rootViewController = UIHostingController(rootView: OverlayFocusInteractionFixture())
            window.makeKeyAndVisible()
            return
        }

        if #available(iOS 17, *), ProcessInfo.processInfo.arguments.contains("--drawer-focus") {
            window.rootViewController = UIHostingController(rootView: DrawerFocusInteractionFixture())
            window.makeKeyAndVisible()
            return
        }

        if #available(iOS 17, *), ProcessInfo.processInfo.arguments.contains("--popover-focus") {
            window.rootViewController = UIHostingController(rootView: PopoverFocusInteractionFixture())
            window.makeKeyAndVisible()
            return
        }

        if ProcessInfo.processInfo.arguments.contains("--popover-interaction") {
            window.rootViewController = UIHostingController(rootView: PopoverInteractionFixture())
            window.makeKeyAndVisible()
            return
        }

        if ProcessInfo.processInfo.arguments.contains("--toast-interaction") {
            window.rootViewController = UIHostingController(rootView: ToastInteractionFixture())
            window.makeKeyAndVisible()
            return
        }

        let isManualTabsAudit = ProcessInfo.processInfo.arguments.contains("--tabs-manual-interaction")
        if isManualTabsAudit || ProcessInfo.processInfo.arguments.contains("--tabs-interaction") {
            window.rootViewController = UIHostingController(
                rootView: TabsInteractionFixture(showAutomatic: !isManualTabsAudit)
            )
            window.makeKeyAndVisible()
            return
        }

        let root = UIViewController()
        window.rootViewController = root
        window.makeKeyAndVisible()

        Task { @MainActor in
            do {
                let count = try await captureFixtures(in: root, window: window)
                try saveSummary("PASS \(count) iOS screenshots")
            } catch {
                try? saveSummary("FAIL \(error.localizedDescription)")
            }
        }
    }

    private func prepareOutputDirectory() throws -> URL {
        let files = FileManager.default
        let documents = try files.url(for: .documentDirectory, in: .userDomainMask, appropriateFor: nil, create: true)
        let directory = documents.appendingPathComponent("ParityAudit", isDirectory: true)
        try files.createDirectory(at: directory, withIntermediateDirectories: true)
        return directory
    }

    private func saveSummary(_ result: String) throws {
        let outputDirectory = try prepareOutputDirectory()
        try result.write(to: outputDirectory.appendingPathComponent("result.txt"), atomically: true, encoding: .utf8)
    }

    private func captureFixtures(in root: UIViewController, window: UIWindow) async throws -> Int {
        let fixtures = SwiftUIAuditFixtures.cases
        guard let manifest = Bundle.main.url(forResource: "registry", withExtension: "json") else {
            throw CocoaError(.fileNoSuchFile)
        }
        let catalog = try JSONDecoder().decode(SwiftUIAuditFixtures.Registry.self, from: Data(contentsOf: manifest))
        guard Set(catalog.items.map(\.name)) == Set(fixtures.keys) else {
            throw CocoaError(.fileReadCorruptFile)
        }

        let outputDirectory = try prepareOutputDirectory()
        var count = 0
        for name in fixtures.keys.sorted() {
            guard let fixture = fixtures[name] else { continue }
            for dark in [false, true] {
                let scheme: ColorScheme = dark ? .dark : .light
                let palette = FoundationTheme().palette(for: scheme)
                window.overrideUserInterfaceStyle = dark ? .dark : .light
                let host = UIHostingController(rootView: fixture()
                    .padding(16)
                    .frame(maxWidth: .infinity, maxHeight: .infinity)
                    .background(palette.background)
                    .environment(\.colorScheme, scheme)
                    .environment(\.locale, Locale(identifier: "en_US")))
                root.addChild(host)
                host.view.frame = root.view.bounds
                root.view.addSubview(host.view)
                host.didMove(toParent: root)
                root.view.layoutIfNeeded()
                try await Task.sleep(nanoseconds: 32_000_000)
                let format = UIGraphicsImageRendererFormat()
                format.scale = 1
                var captureSucceeded = false
                let image = UIGraphicsImageRenderer(bounds: root.view.bounds, format: format).image { _ in
                    captureSucceeded = root.view.drawHierarchy(in: root.view.bounds, afterScreenUpdates: true)
                }
                guard captureSucceeded, let data = image.pngData() else {
                    throw CocoaError(.fileWriteUnknown, userInfo: [NSFilePathErrorKey: name])
                }
                let appearance = dark ? "dark" : "light"
                try data.write(to: outputDirectory.appendingPathComponent("\(name)-\(appearance).png"))
                host.willMove(toParent: nil)
                host.view.removeFromSuperview()
                host.removeFromParent()
                count += 1
            }
        }
        window.rootViewController = UIHostingController(rootView: Text("Rendered \(count) audit fixtures"))
        return count
    }
}
