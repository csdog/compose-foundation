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
        let popoverCases = try names.contains("popover") ? loadPopoverCases() : []
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
            for dark in [false, true] {
                let scheme: ColorScheme = dark ? .dark : .light
                let palette = FoundationTheme().palette(for: scheme)
                window.overrideUserInterfaceStyle = dark ? .dark : .light
                let content = fixture().padding(16)
                    .frame(maxWidth: .infinity, maxHeight: .infinity)
                    .background(palette.background)
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

    private func loadPopoverCases() throws -> [PopoverAuditCase] {
        guard let manifest = Bundle.main.url(forResource: "parity-plan", withExtension: "json") else {
            return []
        }
        for name in ["geist_regular", "geist_medium"] {
            guard let font = Bundle.main.url(forResource: name, withExtension: "ttf"),
                  CTFontManagerRegisterFontsForURL(font as CFURL, .process, nil) else {
                throw CocoaError(.fileReadCorruptFile, userInfo: [NSFilePathErrorKey: name])
            }
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
                rendered = surface.drawHierarchy(in: surface.bounds, afterScreenUpdates: true) && rendered
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
                        rtl("يسار", edge: .leading)
                        rtl("أعلى", edge: .bottom)
                        rtl("أسفل", edge: .top)
                        rtl("يمين", edge: .trailing)
                    }
                    HStack(spacing: 8) {
                        rtl("بداية السطر", edge: .trailing)
                        rtl("نهاية السطر", edge: .leading)
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

    private func rtl(_ title: String, edge: Edge) -> some View {
        trigger(title, width: 288, alignment: .center, edge: edge, rightToLeft: true) {
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
        Button {
            open.wrappedValue.toggle()
        } label: {
            Text(title)
                .font(.custom(rightToLeft ? "ArialMT" : "Geist-Medium", size: compact ? 13 : 14))
                .foregroundColor(dark ? palette.foreground : .black)
                .padding(.horizontal, 11)
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
                .font(.custom(rightToLeft ? "ArialMT" : "Geist-Medium", size: 14))
                .foregroundColor(dark ? palette.foreground : .black)
                .frame(height: compactTitle ? 14 : 20)
                .accessibilityAddTraits(.isHeader)
            Text(rightToLeft ? "تعيين الأبعاد للطبقة." : "Set the dimensions for the layer.")
                .font(.custom(rightToLeft ? "ArialMT" : "Geist-Regular", size: 14))
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

    final class Coordinator: NSObject {
        var value: Binding<String>
        init(value: Binding<String>) { self.value = value }

        @objc func changed(_ field: UITextField) { value.wrappedValue = field.text ?? "" }
    }
}
