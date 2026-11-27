import CoreText
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

        let requested = ProcessInfo.processInfo.arguments.compactMap { argument -> String? in
            let prefix = "--audit-component="
            return argument.hasPrefix(prefix) ? String(argument.dropFirst(prefix.count)) : nil
        }
        let names = requested.isEmpty ? Set(fixtures.keys) : Set(requested)
        guard names.isSubset(of: fixtures.keys) else {
            throw CocoaError(.fileReadCorruptFile, userInfo: [NSLocalizedDescriptionKey: "Unknown audit component"])
        }

        let outputDirectory = try prepareOutputDirectory()
        let parityFont = !names.isDisjoint(with: ["popover", "tabs", "button"])
            ? Bundle.main.url(forResource: "geist_medium", withExtension: "ttf") : nil
        if let parityFont, !CTFontManagerRegisterFontsForURL(parityFont as CFURL, .process, nil) {
            throw CocoaError(.fileReadCorruptFile, userInfo: [NSFilePathErrorKey: parityFont.path])
        }
        var count = 0
        for name in names.sorted() {
            guard let fixture = fixtures[name] else { continue }
            for dark in [false, true] {
                let scheme: ColorScheme = dark ? .dark : .light
                let palette = FoundationTheme().palette(for: scheme)
                window.overrideUserInterfaceStyle = dark ? .dark : .light
                let useComparisonViewport = parityFont != nil && ["popover", "tabs", "button"].contains(name)
                let content: AnyView
                if useComparisonViewport && name == "popover" {
                    content = AnyView(PopoverInitialScreenshotFixture(dark: dark))
                } else if useComparisonViewport && name == "tabs" {
                    content = AnyView(TabsInitialScreenshotFixture())
                } else if useComparisonViewport {
                    content = AnyView(ButtonInitialScreenshotFixture())
                } else {
                    content = AnyView(fixture()
                        .padding(16)
                        .frame(maxWidth: .infinity, maxHeight: .infinity)
                        .background(palette.background))
                }
                let host = UIHostingController(rootView: content
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
                var bounds = root.view.bounds
                if useComparisonViewport {
                    // Compare the shared viewport at one pixel per layout point.
                    bounds.size.height = min(bounds.height, 520)
                    format.preferredRange = .standard
                    format.opaque = true
                }
                var captureSucceeded = false
                let image = UIGraphicsImageRenderer(bounds: bounds, format: format).image { _ in
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

private struct PopoverInitialScreenshotFixture: View {
    let dark: Bool
    @State private var isPresented = false
    @State private var width = "100%"
    @State private var maxWidth = "300px"
    @State private var height = "25px"
    @State private var maxHeight = "none"

    var body: some View {
        // The hosted light example uses black text. Use Geist for the audit label.
        let theme = FoundationTheme(preset: .nova)
        let palette = theme.palette(for: dark ? .dark : .light)
        Button {
            isPresented.toggle()
        } label: {
            Text("Open popover")
                .font(.custom("Geist-Medium", size: 14, relativeTo: .subheadline))
                .foregroundColor(dark ? palette.foreground : .black)
        }
        .buttonStyle(FoundationButtonStyle(.outline))
        .modifier(FoundationPopover(isPresented: $isPresented) {
            VStack(alignment: .leading, spacing: 16) {
                VStack(alignment: .leading, spacing: 8) {
                    Text("Dimensions").font(.headline)
                    Text("Set the dimensions for the layer.")
                        .font(.subheadline)
                        .foregroundColor(palette.mutedForeground)
                }
                dimension("Width", value: $width)
                dimension("Max. width", value: $maxWidth)
                dimension("Height", value: $height)
                dimension("Max. height", value: $maxHeight)
            }
        })
        .foundationTheme(theme)
        .padding(.horizontal, 24)
        // The Nova style centers its 32-point border in a 44-point hit target.
        .padding(.top, 24 - (44 - 32) / 2)
        .frame(maxWidth: .infinity, maxHeight: .infinity, alignment: .topLeading)
        .background(palette.background)
        .ignoresSafeArea()
    }

    private func dimension(_ label: String, value: Binding<String>) -> some View {
        HStack {
            Text(label).frame(width: 84, alignment: .leading).accessibilityHidden(true)
            FoundationInput(label, text: value)
        }
    }
}

private struct TabsInitialScreenshotFixture: View {
    @Environment(\.colorScheme) private var scheme
    @State private var selection = "home"

    var body: some View {
        let theme = novaTheme
        FoundationTabs([
            FoundationChoice("Home", value: "home"),
            FoundationChoice("Disabled", value: "settings", isEnabled: false),
        ], selection: $selection) { _ in
            EmptyView()
        }
        .environment(\.font, .custom("Geist-Medium", size: 14))
        .foundationTheme(theme)
        .padding(.horizontal, 24)
        // The 32-point rail is centered inside its 44-point touch targets.
        .padding(.top, 18)
        .frame(maxWidth: .infinity, maxHeight: .infinity, alignment: .topLeading)
        .background(theme.palette(for: scheme).background)
        .ignoresSafeArea()
    }

    private var novaTheme: FoundationTheme {
        var light = FoundationPalette.neutralLight
        light.foreground = .black
        return FoundationTheme(preset: .nova, light: light)
    }
}

private struct ButtonInitialScreenshotFixture: View {
    @Environment(\.colorScheme) private var scheme

    var body: some View {
        var light = FoundationPalette.neutralLight
        light.foreground = .black
        let theme = FoundationTheme(preset: .nova, light: light)
        return HStack(spacing: 2) {
            Button("Button") {}
                .buttonStyle(FoundationButtonStyle(.outline))
            Button {} label: {
                Path { path in
                    path.move(to: CGPoint(x: 8, y: 12.67))
                    path.addLine(to: CGPoint(x: 8, y: 3.33))
                    path.move(to: CGPoint(x: 3.33, y: 8))
                    path.addLine(to: CGPoint(x: 8, y: 3.33))
                    path.addLine(to: CGPoint(x: 12.67, y: 8))
                }
                .stroke(style: StrokeStyle(lineWidth: 1.33, lineCap: .round, lineJoin: .round))
                .frame(width: 16, height: 16)
            }
            .buttonStyle(FoundationButtonStyle(.outline, size: .icon))
            .accessibilityLabel("Arrow up")
        }
        .environment(\.font, .custom("Geist-Medium", size: 14))
        .foundationTheme(theme)
        .padding(.horizontal, 24)
        .padding(.top, 18)
        .frame(maxWidth: .infinity, maxHeight: .infinity, alignment: .topLeading)
        .background(theme.palette(for: scheme).background)
        .ignoresSafeArea()
    }
}
