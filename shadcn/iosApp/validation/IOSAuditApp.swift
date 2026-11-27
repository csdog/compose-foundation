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
        let comparisonFixtures: [String: () -> AnyView] = [
            "badge": { AnyView(BadgeInitialScreenshotFixture()) },
            "button": { AnyView(ButtonInitialScreenshotFixture()) },
            "separator": { AnyView(SeparatorInitialScreenshotFixture()) },
            "kbd": { AnyView(StatusInitialScreenshotFixture(example: .kbd)) },
            "skeleton": { AnyView(StatusInitialScreenshotFixture(example: .skeleton)) },
            "spinner": { AnyView(StatusInitialScreenshotFixture(example: .spinner)) },
            "progress": { AnyView(StatusInitialScreenshotFixture(example: .progress)) },
            "tabs": { AnyView(TabsInitialScreenshotFixture()) },
        ]
        let popoverCases = try names.contains("popover") ? loadPopoverCases() : []
        let useComparisonFixtures = (names.contains("switch") || !names.isDisjoint(with: comparisonFixtures.keys))
            && Bundle.main.url(forResource: "geist_medium", withExtension: "ttf") != nil
        if useComparisonFixtures || !popoverCases.isEmpty {
            try registerParityFonts()
        }
        var count = 0
        for name in names.sorted() {
            if name == "popover", !popoverCases.isEmpty {
                for item in popoverCases {
                    for dark in [false, true] {
                        try await capturePopover(item, dark: dark, root: root, window: window,
                                                 outputDirectory: outputDirectory)
                        count += 1
                    }
                }
                continue
            }
            guard let fixture = fixtures[name] else { continue }
            let captures: [(filename: String, content: (() -> AnyView)?)]
            if useComparisonFixtures, name == "switch" {
                captures = SwitchScreenshotFixture.Example.allCases.map { example in
                    ("switch-\(example.rawValue)", { AnyView(SwitchScreenshotFixture(example: example)) })
                }
            } else {
                captures = [(name, useComparisonFixtures ? comparisonFixtures[name] : nil)]
            }
            for (filename, comparisonFixture) in captures {
                for dark in [false, true] {
                    let scheme: ColorScheme = dark ? .dark : .light
                    let palette = FoundationTheme().palette(for: scheme)
                    window.overrideUserInterfaceStyle = dark ? .dark : .light
                    let comparison = comparisonFixture?()
                    let content = comparison ?? AnyView(fixture().padding(16)
                        .frame(maxWidth: .infinity, maxHeight: .infinity)
                        .background(palette.background))
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
                    if comparison != nil {
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
                    try data.write(to: outputDirectory.appendingPathComponent("\(filename)-\(appearance).png"))
                    host.willMove(toParent: nil)
                    host.view.removeFromSuperview()
                    host.removeFromParent()
                    count += 1
                }
            }
        }
        window.rootViewController = UIHostingController(rootView: Text("Rendered \(count) audit fixtures"))
        return count
    }

    private func registerParityFonts() throws {
        for name in ["geist_regular", "geist_medium"] {
            guard let font = Bundle.main.url(forResource: name, withExtension: "ttf"),
                  CTFontManagerRegisterFontsForURL(font as CFURL, .process, nil) else {
                throw CocoaError(.fileReadCorruptFile, userInfo: [NSFilePathErrorKey: name])
            }
        }
    }

    private func loadPopoverCases() throws -> [PopoverAuditCase] {
        guard let manifest = Bundle.main.url(forResource: "parity-plan", withExtension: "json") else {
            return []
        }
        struct Plan: Decodable {
            struct Component: Decodable { let platformCases: [String: [PopoverAuditCase]]? }
            let components: [String: Component]
        }
        let plan = try JSONDecoder().decode(Plan.self, from: Data(contentsOf: manifest))
        guard let cases = plan.components["popover"]?.platformCases?["swiftui-ios"], !cases.isEmpty else {
            throw CocoaError(.fileReadCorruptFile)
        }
        return cases
    }

    private func capturePopover(
        _ item: PopoverAuditCase, dark: Bool, root: UIViewController, window: UIWindow, outputDirectory: URL
    ) async throws {
        let name = "popover-\(item.variant.rawValue)-\(item.status.rawValue)-\(dark ? "dark" : "light")"
        let scheme: ColorScheme = dark ? .dark : .light
        window.overrideUserInterfaceStyle = dark ? .dark : .light
        let host = UIHostingController(rootView: PopoverScreenshotFixture(item: item)
            .environment(\.colorScheme, scheme)
            .environment(\.layoutDirection, .leftToRight)
            .environment(\.locale, Locale(identifier: "en_US"))
            .environment(\.foundationPopoverFullViewport, true))
        root.addChild(host)
        host.view.frame = root.view.bounds
        root.view.addSubview(host.view)
        host.didMove(toParent: root)
        defer {
            host.willMove(toParent: nil)
            host.view.removeFromSuperview()
            host.removeFromParent()
        }
        root.view.layoutIfNeeded()
        try await Task.sleep(nanoseconds: 200_000_000)

        let surfaces = window.windowScene?.windows.filter {
            $0 !== window && !$0.isHidden && $0.windowLevel.rawValue == window.windowLevel.rawValue + 1
        } ?? []
        guard surfaces.count == (item.status.isOpen ? 1 : 0),
              item.viewport.count == 2,
              item.viewport[0] == Int(root.view.bounds.width),
              item.viewport[1] > 0, item.viewport[1] <= Int(root.view.bounds.height) else {
            throw CocoaError(.fileWriteUnknown, userInfo: [NSLocalizedDescriptionKey: "Unexpected viewport or panel for \(name)"])
        }
        let format = UIGraphicsImageRendererFormat()
        format.scale = 1
        format.preferredRange = .standard
        format.opaque = true
        let size = CGSize(width: CGFloat(item.viewport[0]), height: CGFloat(item.viewport[1]))
        var rendered = false
        let image = UIGraphicsImageRenderer(size: size, format: format).image { _ in
            rendered = root.view.drawHierarchy(in: root.view.bounds, afterScreenUpdates: true)
            for surface in surfaces {
                let overlayFormat = UIGraphicsImageRendererFormat()
                overlayFormat.scale = 1
                overlayFormat.preferredRange = .standard
                let overlay = UIGraphicsImageRenderer(size: size, format: overlayFormat).image { _ in
                    rendered = surface.drawHierarchy(in: surface.bounds, afterScreenUpdates: true) && rendered
                }
                overlay.draw(at: .zero)
            }
        }
        guard rendered, let data = image.pngData() else {
            throw CocoaError(.fileWriteUnknown, userInfo: [NSFilePathErrorKey: name])
        }
        try data.write(to: outputDirectory.appendingPathComponent(name + ".png"))
    }
}

private struct PopoverAuditCase: Decodable {
    enum Variant: String, Decodable { case `default`, basic, alignments, form, rtl }
    enum Status: String, Decodable {
        case initial, open
        case keyboardFocused = "keyboard-focused"
        case keyboardActivated = "keyboard-activated"

        var isOpen: Bool { self == .open || self == .keyboardActivated }
    }

    let variant: Variant
    let status: Status
    let viewport: [Int]
}

private struct PopoverScreenshotFixture: View {
    let item: PopoverAuditCase
    @Environment(\.colorScheme) private var scheme
    @State private var active: String?
    @State private var width = "100%"
    @State private var maxWidth = "300px"
    @State private var height = "25px"
    @State private var maxHeight = "none"

    init(item: PopoverAuditCase) {
        self.item = item
        let first = item.variant == .alignments ? "Start" : item.variant == .rtl ? "يسار" :
            item.variant == .default ? "Open popover" : "Open Popover"
        _active = State(initialValue: item.status.isOpen ? first : nil)
    }

    private var palette: FoundationPalette { FoundationTheme().palette(for: scheme) }
    private var dark: Bool { scheme == .dark }

    var body: some View {
        Group {
            switch item.variant {
            case .default:
                trigger("Open popover", width: 320, alignment: .center) { form(twoFields: false) }
            case .basic:
                trigger("Open Popover", width: 288, alignment: .start) { heading() }
            case .form:
                trigger("Open Popover", width: 256, alignment: .start) { form(twoFields: true) }
            case .alignments:
                HStack(spacing: 24) {
                    aligned("Start", alignment: .start)
                    aligned("Center", alignment: .center)
                    aligned("End", alignment: .end)
                }
            case .rtl:
                VStack(spacing: 16) {
                    HStack(spacing: 8) {
                        rtl("يسار", edge: .leading, triggerWidth: 48)
                        rtl("أعلى", edge: .bottom, triggerWidth: 47)
                        rtl("أسفل", edge: .top, triggerWidth: 53)
                        rtl("يمين", edge: .trailing, triggerWidth: 50)
                    }
                    HStack(spacing: 8) {
                        rtl("بداية السطر", edge: .trailing, triggerWidth: 85)
                        rtl("نهاية السطر", edge: .leading, triggerWidth: 85)
                    }
                }
                .frame(maxWidth: .infinity)
                // The open example is cropped farther right to keep its entire panel in frame.
                .offset(x: item.status.isOpen ? -40 : 0)
            }
        }
        .padding(24)
        .frame(maxWidth: .infinity, maxHeight: .infinity, alignment: .topLeading)
        .background(palette.background)
        .ignoresSafeArea()
        .foundationTheme(FoundationTheme(preset: .nova))
    }

    private func aligned(_ title: String, alignment: FoundationPopoverAlignment) -> some View {
        trigger(title, width: 160, alignment: alignment, compact: true) {
            Text("Aligned to \(title.lowercased())")
                .font(.custom("Geist-Regular", size: 14))
                .frame(height: 20)
        }
    }

    private func rtl(_ title: String, edge: Edge, triggerWidth: CGFloat) -> some View {
        trigger(title, width: 288, alignment: .center, edge: edge, rightToLeft: true, minimumTriggerWidth: triggerWidth) {
            heading(rightToLeft: true)
        }
    }

    private func trigger<Panel: View>(
        _ title: String,
        width: CGFloat,
        alignment: FoundationPopoverAlignment,
        compact: Bool = false,
        edge: Edge = .top,
        rightToLeft: Bool = false,
        minimumTriggerWidth: CGFloat? = nil,
        @ViewBuilder panel: @escaping () -> Panel
    ) -> some View {
        let open = Binding(get: { active == title }, set: { shown in
            if shown { active = title } else if active == title { active = nil }
        })
        let focus = item.status == .keyboardFocused
        let height: CGFloat = compact ? 28 : 32
        let radius: CGFloat = compact ? 8 : 10
        let shape = RoundedRectangle(cornerRadius: radius)
        let mutedTrigger = open.wrappedValue && (item.variant != .default || item.status != .open)
        let fill = dark ? (mutedTrigger ? grey(28) : palette.novaDarkOutlineFill) : (open.wrappedValue ? grey(245) : .white)
        return Button {
            open.wrappedValue.toggle()
        } label: {
            Text(title)
                .font(rightToLeft ? .system(size: 14) : .custom("Geist-Medium", size: compact ? 12.8 : 14))
                .foregroundColor(dark ? palette.foreground : .black)
                .padding(.horizontal, 11)
                .frame(minWidth: minimumTriggerWidth)
                .frame(height: height)
                .background(shape.fill(fill))
                .overlay(shape.strokeBorder(focus && !dark ? grey(161) : dark ? grey(46) : grey(229)))
                .overlay(
                    RoundedRectangle(cornerRadius: radius + 1.5)
                        .strokeBorder(focus ? (dark ? grey(63) : grey(208)) : .clear, lineWidth: 3)
                        .padding(-3).allowsHitTesting(false)
                )
        }
        .buttonStyle(.plain)
        .modifier(PopoverAuditTriggerFocus(requestFocus: focus))
        .modifier(FoundationPopover(isPresented: open, arrowEdge: edge, alignment: alignment, width: width) {
            panel()
        })
        .environment(\.layoutDirection, rightToLeft ? .rightToLeft : .leftToRight)
    }

    private func heading(rightToLeft: Bool = false, compactTitle: Bool = false) -> some View {
        VStack(alignment: .leading, spacing: compactTitle ? 8 : 2) {
            Text(rightToLeft ? "الأبعاد" : "Dimensions")
                .font(rightToLeft ? .system(size: 14) : .custom("Geist-Medium", size: 14))
                .foregroundColor(dark ? palette.foreground : .black)
                .frame(height: compactTitle ? 14 : 20)
                .accessibilityAddTraits(.isHeader)
            Text(rightToLeft ? "تعيين الأبعاد للطبقة." : "Set the dimensions for the layer.")
                .font(rightToLeft ? .system(size: 14) : .custom("Geist-Regular", size: 14))
                .foregroundColor(palette.mutedForeground)
                .frame(height: 20)
        }
        .frame(maxWidth: .infinity, alignment: .leading)
    }

    private func form(twoFields: Bool) -> some View {
        VStack(alignment: .leading, spacing: twoFields ? 10 : 16) {
            heading(compactTitle: !twoFields)
            VStack(spacing: twoFields ? 16 : 8) {
                dimension("Width", value: $width, twoFields: twoFields, focused: true)
                if !twoFields {
                    dimension("Max. width", value: $maxWidth, twoFields: twoFields)
                }
                dimension("Height", value: $height, twoFields: twoFields)
                if !twoFields {
                    dimension("Max. height", value: $maxHeight, twoFields: twoFields)
                }
            }
        }
    }

    private func dimension(_ label: String, value: Binding<String>, twoFields: Bool, focused: Bool = false) -> some View {
        let shape = RoundedRectangle(cornerRadius: 10)
        return HStack(spacing: 16) {
            Text(label)
                .font(.custom("Geist-Medium", size: 14))
                .foregroundColor(dark ? palette.foreground : .black)
                .frame(width: twoFields ? 64 : 89, alignment: .leading)
                .accessibilityHidden(true)
            PopoverAuditTextField(label: label, value: value, requestFocus: focused)
                .padding(.horizontal, 10)
                .frame(maxWidth: .infinity)
                .frame(height: 32)
                .background(shape.fill(dark ? grey(33) : .white))
                .overlay(shape.strokeBorder(focused ? (dark ? grey(69) : grey(161)) : dark ? grey(69) : grey(229)))
                .overlay(
                    RoundedRectangle(cornerRadius: 12)
                        .strokeBorder(focused ? (dark ? grey(66) : grey(208)) : .clear, lineWidth: 3)
                        .padding(-3).allowsHitTesting(false)
                )
        }
    }

    private func grey(_ value: Double) -> Color {
        Color(.sRGB, red: value / 255, green: value / 255, blue: value / 255, opacity: 1)
    }
}

private struct PopoverAuditTriggerFocus: ViewModifier {
    let requestFocus: Bool

    @ViewBuilder func body(content: Content) -> some View {
        if #available(iOS 17, *) {
            content.modifier(KeyboardFocus(requestFocus: requestFocus))
        } else {
            content
        }
    }

    @available(iOS 17, *)
    private struct KeyboardFocus: ViewModifier {
        let requestFocus: Bool
        @FocusState private var focused: Bool

        func body(content: Content) -> some View {
            content.focusable()
                .focused($focused)
                .focusEffectDisabled()
                .onAppear { if requestFocus { focused = true } }
        }
    }
}

private struct PopoverAuditTextField: UIViewRepresentable {
    let label: String
    @Binding var value: String
    let requestFocus: Bool

    func makeCoordinator() -> Coordinator { Coordinator(value: $value) }

    func makeUIView(context: Context) -> UITextField {
        let field = UITextField()
        field.font = UIFont(name: "Geist-Regular", size: 16) ?? UIFont.systemFont(ofSize: 16)
        field.textColor = .label
        field.accessibilityLabel = label
        field.tintColor = .clear
        // The audit viewport represents hardware-keyboard entry and keeps the software keyboard out of frame.
        field.inputView = UIView()
        field.addTarget(context.coordinator, action: #selector(Coordinator.changed(_:)), for: .editingChanged)
        if requestFocus {
            DispatchQueue.main.async { [weak field] in
                if field?.window != nil { field?.becomeFirstResponder() }
            }
        }
        return field
    }

    func updateUIView(_ field: UITextField, context: Context) {
        context.coordinator.value = $value
        if field.text != value { field.text = value }
    }

    @MainActor final class Coordinator: NSObject {
        var value: Binding<String>
        init(value: Binding<String>) { self.value = value }

        @objc func changed(_ field: UITextField) { value.wrappedValue = field.text ?? "" }
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

private struct BadgeInitialScreenshotFixture: View {
    @Environment(\.colorScheme) private var scheme

    var body: some View {
        var light = FoundationPalette.neutralLight
        light.primary = .black
        light.foreground = .black
        let theme = FoundationTheme(preset: .nova, light: light)
        return HStack(spacing: 8) {
            FoundationBadge { Text("Badge") }
            FoundationBadge(.secondary) { Text("Secondary") }
            FoundationBadge(.destructive) { Text("Destructive") }
            FoundationBadge(.outline) { Text("Outline") }
        }
        .environment(\.font, .custom("Geist-Medium", size: 12))
        .foundationTheme(theme)
        .padding(.top, 24)
        .frame(maxWidth: .infinity, maxHeight: .infinity, alignment: .top)
        .background(theme.palette(for: scheme).background)
        .ignoresSafeArea()
    }
}

private struct SeparatorInitialScreenshotFixture: View {
    @Environment(\.colorScheme) private var scheme

    var body: some View {
        let theme = FoundationTheme(preset: .nova)
        let palette = theme.palette(for: scheme)
        let foreground: Color = scheme == .light ? .black : palette.foreground
        return VStack(alignment: .leading, spacing: 16) {
            VStack(alignment: .leading, spacing: 6) {
                Text("shadcn/ui")
                    .font(.custom("Geist-Medium", size: 14))
                    .foregroundColor(foreground)
                    .frame(height: 14)
                Text("The Foundation for your Design System")
                    .foregroundColor(palette.mutedForeground)
                    .frame(height: 20)
            }
            FoundationSeparator()
            Text("A set of beautifully designed components that you can customize, extend, and build on.")
                .foregroundColor(foreground)
                .lineSpacing(3)
                .fixedSize(horizontal: false, vertical: true)
        }
        .font(.custom("Geist-Regular", size: 14))
        .frame(width: 384, alignment: .leading)
        .foundationTheme(theme)
        .padding(.leading, 18)
        .padding(.top, 24)
        .frame(maxWidth: .infinity, maxHeight: .infinity, alignment: .topLeading)
        .background(palette.background)
        .ignoresSafeArea()
    }
}

private struct SwitchScreenshotFixture: View {
    enum Example: String, CaseIterable {
        case off = "default-initial"
        case on = "default-on"
        case disabled = "disabled-initial"
    }

    @Environment(\.colorScheme) private var scheme
    @State private var isOn: Bool
    let example: Example

    init(example: Example) {
        self.example = example
        _isOn = State(initialValue: example == .on)
    }

    var body: some View {
        var light = FoundationPalette.neutralLight
        light.foreground = .black
        light.primary = .black
        let theme = FoundationTheme(preset: .nova, light: light)
        let disabled = example == .disabled
        return Toggle(disabled ? "Disabled" : "Airplane Mode", isOn: $isOn)
            .toggleStyle(FoundationSwitchStyle())
            .disabled(disabled)
            .font(.custom("Geist-Medium", size: 14))
            .foundationTheme(theme)
            .padding(.horizontal, 24)
            .padding(.top, 11)
            .frame(maxWidth: .infinity, maxHeight: .infinity, alignment: .topLeading)
            .background(theme.palette(for: scheme).background)
            .ignoresSafeArea()
    }
}

private struct StatusInitialScreenshotFixture: View {
    enum Example { case kbd, skeleton, spinner, progress }

    let example: Example
    @Environment(\.colorScheme) private var scheme

    var body: some View {
        var light = FoundationPalette.neutralLight
        light.foreground = .black
        light.primary = .black
        let theme = FoundationTheme(preset: .nova, light: light)
        let palette = theme.palette(for: scheme)
        return Group {
            switch example {
            case .kbd:
                VStack(spacing: 18) {
                    HStack(spacing: 4) {
                        ForEach(["⌘", "⇧", "⌥", "⌃"], id: \.self) { FoundationKbd($0) }
                    }
                    HStack(spacing: 4) {
                        FoundationKbd("Ctrl")
                        Text("+").font(.custom("Geist-Regular", size: 14))
                        FoundationKbd("B")
                    }
                }
                .environment(\.font, .custom("Geist-Medium", size: 12))
                .frame(maxWidth: .infinity)
            case .skeleton:
                HStack(spacing: 16) {
                    FoundationSkeleton(cornerRadius: 24).frame(width: 48, height: 48)
                    VStack(alignment: .leading, spacing: 8) {
                        FoundationSkeleton().frame(width: 250, height: 16)
                        FoundationSkeleton().frame(width: 200, height: 16)
                    }
                }
            case .progress:
                FoundationProgress(value: 0.66)
                    .frame(width: 403.2)
            case .spinner:
                HStack(spacing: 10) {
                    FoundationSpinner(accessibilityLabel: "Processing payment", animates: false)
                    Text("Processing payment...")
                        .font(.custom("Geist-Medium", size: 14))
                    Spacer(minLength: 0)
                    Text("$100.00")
                        .font(.custom("Geist-Regular", size: 14).monospacedDigit())
                }
                .padding(.horizontal, 13)
                .frame(width: 320, height: 42)
                .background(RoundedRectangle(cornerRadius: 16).fill(palette.secondary.opacity(0.5)))
            }
        }
        .foregroundColor(palette.foreground)
        .foundationTheme(theme)
        .padding([.leading, .top, .bottom], 24)
        .padding(.trailing, example == .progress ? 0 : 24)
        .frame(maxWidth: .infinity, maxHeight: .infinity, alignment: .topLeading)
        .background(palette.background)
        .ignoresSafeArea()
    }
}
