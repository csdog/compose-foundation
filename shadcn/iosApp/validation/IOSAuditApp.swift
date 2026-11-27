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
            "label": { AnyView(LabelFieldScreenshotFixture(example: .label)) },
            "badge": { AnyView(BadgeInitialScreenshotFixture()) },
            "button": { AnyView(ButtonInitialScreenshotFixture()) },
            "separator": { AnyView(SeparatorInitialScreenshotFixture()) },
            "kbd": { AnyView(StatusInitialScreenshotFixture(example: .kbd)) },
            "skeleton": { AnyView(StatusInitialScreenshotFixture(example: .skeleton)) },
            "spinner": { AnyView(StatusInitialScreenshotFixture(example: .spinner)) },
            "progress": { AnyView(StatusInitialScreenshotFixture(example: .progress)) },
            "tabs": { AnyView(TabsInitialScreenshotFixture()) },
        ]
        typealias Capture = (filename: String, content: (() -> AnyView)?)
        let variantFixtures: [String: [Capture]] = [
            "input-group": InputGroupScreenshotFixture.Example.allCases.map { example in
                ("input-group-\(example.rawValue)-initial", { AnyView(InputGroupScreenshotFixture(example: example)) })
            },
            "pagination": PaginationScreenshotFixture.Example.allCases.map { example in
                ("pagination-\(example.rawValue)-initial", { AnyView(PaginationScreenshotFixture(example: example)) })
            },
            "scroll-area": ScrollAreaScreenshotFixture.Example.allCases.map { example in
                ("scroll-area-\(example.rawValue)-initial", { AnyView(ScrollAreaScreenshotFixture(example: example)) })
            },
            "aspect-ratio": AspectRatioScreenshotFixture.Example.allCases.map { example in
                ("aspect-ratio-\(example.rawValue)-initial", { AnyView(AspectRatioScreenshotFixture(example: example)) })
            },
            "item": ItemScreenshotFixture.Example.allCases.map { example in
                ("item-\(example.rawValue)-initial", { AnyView(ItemScreenshotFixture(example: example)) })
            },
            "avatar": AvatarScreenshotFixture.Example.allCases.map { example in
                ("avatar-\(example.rawValue)-initial", { AnyView(AvatarScreenshotFixture(example: example)) })
            },
            "breadcrumb": BreadcrumbScreenshotFixture.Example.allCases.map { example in
                ("breadcrumb-\(example.rawValue)-initial", { AnyView(BreadcrumbScreenshotFixture(example: example)) })
            },
            "empty": [
                ("empty-default-initial", { AnyView(EmptyScreenshotFixture(outlined: false)) }),
                ("empty-outline-initial", { AnyView(EmptyScreenshotFixture(outlined: true)) }),
            ],
            "collapsible": [
                ("collapsible-default-initial", { AnyView(CollapsibleScreenshotFixture(expanded: false)) }),
                ("collapsible-default-open", { AnyView(CollapsibleScreenshotFixture(expanded: true)) }),
            ],
            "accordion": AccordionScreenshotFixture.Example.allCases.map { example in
                ("accordion-\(example.rawValue)", { AnyView(AccordionScreenshotFixture(example: example)) })
            },
            "alert": [
                ("alert-basic-initial", { AnyView(AlertScreenshotFixture(destructive: false)) }),
                ("alert-destructive-initial", { AnyView(AlertScreenshotFixture(destructive: true)) }),
            ],
            "card": [
                ("card-default-initial", { AnyView(CardScreenshotFixture(small: false)) }),
                ("card-small-initial", { AnyView(CardScreenshotFixture(small: true)) }),
            ],
            "field": [
                ("field-input-initial", { AnyView(LabelFieldScreenshotFixture(example: .input)) }),
                ("field-textarea-initial", { AnyView(LabelFieldScreenshotFixture(example: .textarea)) }),
            ],
            "button-group": [
                ("button-group-size-initial", { AnyView(ButtonGroupScreenshotFixture(vertical: false)) }),
                ("button-group-orientation-initial", { AnyView(ButtonGroupScreenshotFixture(vertical: true)) }),
            ],
            "switch": SwitchScreenshotFixture.Example.allCases.map { example in
                ("switch-\(example.rawValue)", { AnyView(SwitchScreenshotFixture(example: example)) })
            },
            "checkbox": CheckboxScreenshotFixture.Example.allCases.map { example in
                ("checkbox-\(example.rawValue)", { AnyView(CheckboxScreenshotFixture(example: example)) })
            },
            "radio-group": RadioGroupScreenshotFixture.Example.allCases.map { example in
                ("radio-group-\(example.rawValue)", { AnyView(RadioGroupScreenshotFixture(example: example)) })
            },
            "input": [
                ("input-default-initial", { AnyView(TextFieldScreenshotFixture(example: .input)) }),
                ("input-basic-initial", { AnyView(TextFieldScreenshotFixture(example: .basicInput)) }),
            ],
            "input-otp": InputOtpScreenshotFixture.Example.allCases.map { example in
                ("input-otp-\(example.rawValue)", { AnyView(InputOtpScreenshotFixture(example: example)) })
            },
            "slider": SliderScreenshotFixture.Example.allCases.map { example in
                ("slider-\(example.rawValue)", { AnyView(SliderScreenshotFixture(example: example)) })
            },
            "toggle": [
                ("toggle-default-initial", { AnyView(ToggleScreenshotFixture(grouped: false, selected: false)) }),
                ("toggle-default-pressed", { AnyView(ToggleScreenshotFixture(grouped: false, selected: true)) }),
            ],
            "toggle-group": [
                ("toggle-group-default-initial", { AnyView(ToggleScreenshotFixture(grouped: true, selected: false)) }),
                ("toggle-group-default-bold", { AnyView(ToggleScreenshotFixture(grouped: true, selected: true)) }),
            ],
            "textarea": [
                ("textarea-default-initial", { AnyView(TextFieldScreenshotFixture(example: .textarea)) }),
                ("textarea-field-initial", { AnyView(TextFieldScreenshotFixture(example: .textareaField)) }),
            ],
        ]
        let popoverCases = try names.contains("popover") ? loadPopoverCases() : []
        let useComparisonFixtures = (names.contains("hover-card")
            || !names.isDisjoint(with: Set(comparisonFixtures.keys).union(variantFixtures.keys)))
            && Bundle.main.url(forResource: "geist_medium", withExtension: "ttf") != nil
        if useComparisonFixtures || !popoverCases.isEmpty {
            try registerParityFonts()
        }
        if useComparisonFixtures, names.contains("scroll-area"),
           !(0..<3).allSatisfy({ Bundle.main.url(forResource: "scroll_area_art_\($0)", withExtension: "jpg") != nil }) {
            throw CocoaError(.fileNoSuchFile, userInfo: [NSLocalizedDescriptionKey: "Scroll Area artwork is missing"])
        }
        let viewports = useComparisonFixtures ? try loadComparisonViewports() : [:]
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
            if name == "hover-card", useComparisonFixtures {
                for isOpen in [false, true] {
                    let filename = "hover-card-default-\(isOpen ? "open" : "initial")"
                    guard let viewport = viewports[filename] else { throw CocoaError(.fileReadCorruptFile) }
                    for dark in [false, true] {
                        try await captureFloatingPanel(filename: filename, isOpen: isOpen, viewport: viewport,
                                                       dark: dark, root: root, window: window,
                                                       outputDirectory: outputDirectory,
                                                       content: HoverCardScreenshotFixture(isOpen: isOpen))
                        count += 1
                    }
                }
                continue
            }
            guard let fixture = fixtures[name] else { continue }
            let captures: [Capture]
            if useComparisonFixtures, let variants = variantFixtures[name] {
                captures = variants
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
                        if let viewport = viewports[filename] {
                            guard viewport.count == 2, viewport[0] > 0, viewport[1] > 0,
                                  CGFloat(viewport[0]) <= bounds.width, CGFloat(viewport[1]) <= bounds.height else {
                                throw CocoaError(.fileWriteUnknown, userInfo: [
                                    NSLocalizedDescriptionKey: "Viewport \(viewport) does not fit \(bounds.size) for \(filename)",
                                ])
                            }
                            bounds.size = CGSize(width: viewport[0], height: viewport[1])
                        } else {
                            bounds.size.height = min(bounds.height, 520)
                        }
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

    private func loadComparisonViewports() throws -> [String: [Int]] {
        guard let manifest = Bundle.main.url(forResource: "parity-plan", withExtension: "json") else {
            return [:]
        }
        struct Plan: Decodable {
            struct Component: Decodable {
                struct Variant: Decodable {
                    let variant: String
                    let status: String
                    let viewport: [Int]?
                }
                let shadcnComparisonViewport: [String: [Int]]?
                let platformCases: [String: [Variant]]?
            }
            let components: [String: Component]
        }
        let plan = try JSONDecoder().decode(Plan.self, from: Data(contentsOf: manifest))
        var viewports: [String: [Int]] = [:]
        for (component, config) in plan.components {
            if let viewport = config.shadcnComparisonViewport?["swiftui-ios"] {
                viewports[component] = viewport
            }
            for item in config.platformCases?["swiftui-ios"] ?? [] {
                viewports["\(component)-\(item.variant)-\(item.status)"] = item.viewport
            }
        }
        return viewports
    }

    private func loadPopoverCases() throws -> [PopoverAuditCase] {
        guard let manifest = Bundle.main.url(forResource: "parity-plan", withExtension: "json") else {
            return []
        }
        struct Plan: Decodable {
            struct Components: Decodable {
                struct Popover: Decodable { let platformCases: [String: [PopoverAuditCase]] }
                let popover: Popover
            }
            let components: Components
        }
        let plan = try JSONDecoder().decode(Plan.self, from: Data(contentsOf: manifest))
        guard let cases = plan.components.popover.platformCases["swiftui-ios"], !cases.isEmpty else {
            throw CocoaError(.fileReadCorruptFile)
        }
        return cases
    }

    private func capturePopover(
        _ item: PopoverAuditCase, dark: Bool, root: UIViewController, window: UIWindow, outputDirectory: URL
    ) async throws {
        try await captureFloatingPanel(filename: "popover-\(item.variant.rawValue)-\(item.status.rawValue)",
                                       isOpen: item.status.isOpen, viewport: item.viewport, dark: dark,
                                       root: root, window: window, outputDirectory: outputDirectory,
                                       content: PopoverScreenshotFixture(item: item))
    }

    private func captureFloatingPanel<Panel: View>(
        filename: String, isOpen: Bool, viewport: [Int], dark: Bool, root: UIViewController, window: UIWindow,
        outputDirectory: URL, content: Panel
    ) async throws {
        let name = "\(filename)-\(dark ? "dark" : "light")"
        let scheme: ColorScheme = dark ? .dark : .light
        window.overrideUserInterfaceStyle = dark ? .dark : .light
        let host = UIHostingController(rootView: content
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
        guard surfaces.count == (isOpen ? 1 : 0),
              viewport.count == 2,
              viewport[0] == Int(root.view.bounds.width),
              viewport[1] > 0, viewport[1] <= Int(root.view.bounds.height) else {
            throw CocoaError(.fileWriteUnknown, userInfo: [NSLocalizedDescriptionKey: "Unexpected viewport or panel for \(name)"])
        }
        let format = UIGraphicsImageRendererFormat()
        format.scale = 1
        format.preferredRange = .standard
        format.opaque = true
        let size = CGSize(width: CGFloat(viewport[0]), height: CGFloat(viewport[1]))
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

private struct InputGroupScreenshotFixture: View {
    enum Example: String, CaseIterable { case `default`, blockStart = "block-start", blockEnd = "block-end" }

    @Environment(\.colorScheme) private var scheme
    @State private var input = ""
    @State private var message = ""
    let example: Example

    var body: some View {
        var light = FoundationPalette.neutralLight
        light.foreground = .black
        light.primary = .black
        let theme = FoundationTheme(preset: .nova, light: light)
        let palette = theme.palette(for: scheme)
        return Group {
            if example == .default {
                FoundationInputGroup {
                    textField("Search...", palette: palette)
                } leading: {
                    Image(systemName: "magnifyingglass").font(.system(size: 14)).accessibilityHidden(true)
                } trailing: {
                    Text("12 results").font(.custom("Geist-Medium", size: 14))
                }
                .frame(width: 320)
                .padding(.top, 18)
            } else {
                blockExamples(palette: palette)
                    .frame(width: 384)
                    .padding(.top, 24)
            }
        }
        .foregroundColor(palette.foreground)
        .foundationTheme(theme)
        .padding(.leading, 24)
        .frame(maxWidth: .infinity, maxHeight: .infinity, alignment: .topLeading)
        .background(palette.background)
        .ignoresSafeArea()
    }

    private func textField(_ placeholder: String, palette: FoundationPalette) -> some View {
        Group {
            if #available(iOS 15, *) {
                TextField(placeholder, text: $input, prompt: Text(placeholder).foregroundColor(palette.mutedForeground))
            } else {
                TextField(placeholder, text: $input)
            }
        }
        .textFieldStyle(.plain)
        .font(.custom("Geist-Regular", size: 16))
        .frame(minHeight: 44)
    }

    private func blockExamples(palette: FoundationPalette) -> some View {
        let above = example == .blockStart
        return VStack(spacing: 24) {
            field("Input", description: "\(above ? "Header positioned above" : "Footer positioned below") the input.", palette: palette) {
                FoundationInputGroup(axis: .vertical) {
                    textField(above ? "Enter your name" : "Enter amount", palette: palette)
                } leading: {
                    if above { Text("Full Name").font(.custom("Geist-Medium", size: 14)) }
                } trailing: {
                    if !above { Text("USD").font(.custom("Geist-Medium", size: 14)) }
                }
            }
            field("Textarea", description: "\(above ? "Header positioned above" : "Footer positioned below") the textarea.", palette: palette) {
                FoundationInputGroup(axis: .vertical) {
                    editor(above ? "console.log('Hello, world!');" : "Write a comment...", monospaced: above, palette: palette)
                } leading: {
                    if above {
                        HStack(spacing: 8) {
                            Image(systemName: "doc.badge.gearshape").accessibilityHidden(true)
                            Text("script.js").font(.system(size: 14, design: .monospaced))
                            Spacer()
                            Button {} label: { Image(systemName: "doc.on.doc").foregroundColor(palette.mutedForeground) }
                                .accessibilityLabel(Text("Copy"))
                                .buttonStyle(FoundationButtonStyle(.ghost, size: .iconExtraSmall))
                        }
                    }
                } trailing: {
                    if !above {
                        HStack(spacing: 8) {
                            Text("0/280").font(.custom("Geist-Medium", size: 14))
                            Spacer()
                            Button("Post") {}
                                .buttonStyle(FoundationButtonStyle(.primary, size: .small))
                        }
                    }
                }
            }
        }
    }

    private func field<Content: View>(
        _ title: String, description: String, palette: FoundationPalette, @ViewBuilder content: () -> Content
    ) -> some View {
        VStack(alignment: .leading, spacing: 8) {
            Text(title).font(.custom("Geist-Medium", size: 14))
            content()
            Text(description).font(.custom("Geist-Regular", size: 14)).foregroundColor(palette.mutedForeground)
        }
    }

    private func editor(_ placeholder: String, monospaced: Bool, palette: FoundationPalette) -> some View {
        let font: Font = monospaced ? .system(size: 14, design: .monospaced) : .custom("Geist-Regular", size: 16)
        return Group {
            if #available(iOS 16, *) { TextEditor(text: $message).scrollContentBackground(.hidden) }
            else { TextEditor(text: $message) }
        }
        .font(font)
        .accessibilityLabel(Text(placeholder))
        .frame(height: 64)
        .overlay(Group {
            if message.isEmpty {
                Text(placeholder).font(font).foregroundColor(palette.mutedForeground)
                    .padding(.top, 8)
                    .accessibilityHidden(true)
                    .allowsHitTesting(false)
            }
        }, alignment: .topLeading)
    }
}

private struct HoverCardScreenshotFixture: View {
    @Environment(\.colorScheme) private var scheme
    let isOpen: Bool

    var body: some View {
        var light = FoundationPalette.neutralLight
        light.foreground = .black
        let theme = FoundationTheme(preset: .nova, light: light)
        let palette = theme.palette(for: scheme)
        return Button("Hover Here") {}
            .font(.custom("Geist-Medium", size: 14))
            .foregroundColor(palette.foreground)
            .buttonStyle(PlainButtonStyle())
            .frame(width: 96, height: 44)
            .modifier(FoundationHoverCard(isPresented: .constant(isOpen)) {
                VStack(alignment: .leading, spacing: 2) {
                    Text("@nextjs").font(.custom("Geist-Medium", size: 14))
                        .accessibilityAddTraits(.isHeader)
                    Text("The React Framework – created and maintained by @vercel.")
                        .font(.custom("Geist-Regular", size: 14))
                        .lineSpacing(3)
                        .fixedSize(horizontal: false, vertical: true)
                    Text("Joined December 2021")
                        .font(.custom("Geist-Regular", size: 12))
                        .foregroundColor(palette.mutedForeground)
                        .padding(.top, 4)
                }
                .multilineTextAlignment(.leading)
            })
            .foundationTheme(theme)
            .padding(.leading, 24)
            .padding(.top, 18)
            .frame(maxWidth: .infinity, maxHeight: .infinity, alignment: .topLeading)
            .background(palette.background)
            .ignoresSafeArea()
    }
}

private struct PaginationScreenshotFixture: View {
    enum Example: String, CaseIterable { case `default`, simple, rtl }

    @Environment(\.colorScheme) private var scheme
    @State private var page = 2
    let example: Example

    var body: some View {
        var light = FoundationPalette.neutralLight
        light.foreground = .black
        let theme = FoundationTheme(preset: .nova, light: light)
        let controls: FoundationPaginationControls = switch example {
        case .default: .labels(previous: "Previous", next: "Next")
        case .simple: .none
        case .rtl: .labels(previous: "السابق", next: "التالي")
        }

        return FoundationPagination(pageCount: example == .simple ? 5 : 10, page: $page,
                                    controls: controls, window: example == .simple ? .standard : .compact)
            .font(.custom("Geist-Medium", size: 14))
            .environment(\.layoutDirection, example == .rtl ? .rightToLeft : .leftToRight)
            .environment(\.locale, Locale(identifier: example == .rtl ? "ar@numbers=arab" : "en_US"))
            .foundationTheme(theme)
            .padding(.top, 18)
            .frame(maxWidth: .infinity, maxHeight: .infinity, alignment: .top)
            .background(theme.palette(for: scheme).background)
            .ignoresSafeArea()
    }
}

private struct ScrollAreaScreenshotFixture: View {
    enum Example: String, CaseIterable { case `default`, horizontal = "horizontal-demo", rtl }

    @Environment(\.colorScheme) private var scheme
    let example: Example

    var body: some View {
        var light = FoundationPalette.neutralLight
        light.foreground = .black
        let theme = FoundationTheme(preset: .nova, light: light)
        let palette = theme.palette(for: scheme)
        return Group {
            if example == .horizontal {
                artwork(palette: palette)
                    .frame(width: 384, height: 458)
            } else {
                tags(palette: palette)
                    .frame(width: 192, height: 288)
                    .environment(\.layoutDirection, example == .rtl ? .rightToLeft : .leftToRight)
            }
        }
        .foregroundColor(palette.foreground)
        .foundationTheme(theme)
        .padding(24)
        .frame(maxWidth: .infinity, maxHeight: .infinity, alignment: .topLeading)
        .background(palette.background)
        .ignoresSafeArea()
    }

    private func tags(palette: FoundationPalette) -> some View {
        FoundationScrollArea(showsBorder: true) {
            VStack(alignment: .leading, spacing: 0) {
                Text(example == .rtl ? "العلامات" : "Tags")
                    .font(.custom("Geist-Medium", size: 14))
                    .frame(height: 14)
                    .padding(.bottom, 16)
                    .accessibilityAddTraits(.isHeader)
                ForEach((1...50).reversed(), id: \.self) { tag in
                    Text("v1.2.0-beta.\(tag)")
                        .font(.custom("Geist-Regular", size: 14))
                        .frame(height: 20)
                    Rectangle().fill(palette.border)
                        .frame(height: 1)
                        .padding(.vertical, 8)
                        .accessibilityHidden(true)
                }
            }
            .frame(maxWidth: .infinity, alignment: .leading)
            .padding(16)
        }
    }

    private func artwork(palette: FoundationPalette) -> some View {
        let artists = ["Ornella Binni", "Tom Byrom", "Vladimir Malyavko"]
        return FoundationScrollArea(.horizontal, showsBorder: true) {
            HStack(spacing: 16) {
                ForEach(artists.indices, id: \.self) { index in
                    let artist = artists[index]
                    VStack(alignment: .leading, spacing: 8) {
                        if let photo = UIImage(named: "scroll_area_art_\(index).jpg") {
                            Image(uiImage: photo)
                                .resizable()
                                .scaledToFill()
                                .frame(width: 300, height: 400)
                                .clipShape(RoundedRectangle(cornerRadius: 8))
                                .accessibilityLabel(Text("Photo by \(artist)"))
                        }
                        (Text("Photo by ").foregroundColor(palette.mutedForeground)
                            + Text(artist).font(.custom("Geist-Medium", size: 12)))
                            .font(.custom("Geist-Regular", size: 12))
                            .frame(height: 16)
                    }
                    .frame(width: 300, alignment: .leading)
                }
            }
            .padding(16)
        }
    }
}

private struct AspectRatioScreenshotFixture: View {
    enum Example: String, CaseIterable {
        case `default`, square, portrait

        var ratio: CGFloat {
            switch self {
            case .default: 16 / 9
            case .square: 1
            case .portrait: 9 / 16
            }
        }

        var width: CGFloat {
            switch self {
            case .default: 384
            case .square: 192
            case .portrait: 160
            }
        }
    }

    @Environment(\.colorScheme) private var scheme
    let example: Example

    var body: some View {
        let side = max(example.width, example.width / example.ratio)
        let brightness = scheme == .dark ? 0.2 : 1.0
        let theme = FoundationTheme(preset: .nova)
        return FoundationAspectRatio(example.ratio) {
            LinearGradient(
                gradient: Gradient(colors: [Color(white: 214.0 / 255 * brightness), Color(white: 195.0 / 255 * brightness)]),
                startPoint: .topLeading, endPoint: .bottomTrailing
            )
            .frame(width: side, height: side)
            .accessibilityLabel(Text("Photo"))
        }
        .clipShape(RoundedRectangle(cornerRadius: theme.cornerRadius))
        .frame(width: example.width)
        .padding(24)
        .frame(maxWidth: .infinity, maxHeight: .infinity, alignment: .topLeading)
        .background(theme.palette(for: scheme).background)
        .ignoresSafeArea()
    }
}

private struct ItemScreenshotFixture: View {
    enum Example: String, CaseIterable { case `default`, variant, size }

    @Environment(\.colorScheme) private var scheme
    let example: Example

    var body: some View {
        var light = FoundationPalette.neutralLight
        light.foreground = .black
        let theme = FoundationTheme(preset: .nova, light: light)

        return VStack(spacing: 24) {
            switch example {
            case .default:
                FoundationItem("Basic Item", description: "A simple item with title and description.", variant: .outline) {
                    EmptyView()
                } actions: {
                    Button("Action") {}
                        .buttonStyle(FoundationButtonStyle(.outline, size: .small))
                }
                Button {} label: {
                    FoundationItem("Your profile has been verified.", variant: .outline, size: .small) {
                        Image(systemName: "checkmark.seal").font(.system(size: 20)).frame(width: 20, height: 20)
                            .accessibilityHidden(true)
                    } actions: {
                        Image(systemName: "chevron.forward").font(.system(size: 11)).frame(width: 16)
                            .accessibilityHidden(true)
                    }
                }
                .buttonStyle(PlainButtonStyle())
            case .variant:
                item("Default Variant", "Transparent background with no border.", variant: .standard)
                item("Outline Variant", "Outlined style with a visible border.", variant: .outline)
                item("Muted Variant", "Muted background for secondary content.", variant: .muted)
            case .size:
                item("Default Size", "The standard size for most use cases.", variant: .outline)
                item("Small Size", "A compact size for dense layouts.", variant: .outline, size: .small)
                item("Extra Small Size", "The most compact size available.", variant: .outline, size: .extraSmall)
            }
        }
        .foregroundColor(theme.palette(for: scheme).foreground)
        .foundationTheme(theme)
        .frame(width: 448)
        .padding(24)
        .frame(maxWidth: .infinity, maxHeight: .infinity, alignment: .topLeading)
        .background(theme.palette(for: scheme).background)
        .ignoresSafeArea()
    }

    private func item(
        _ title: String, _ description: String, variant: FoundationItemVariant, size: FoundationItemSize = .regular
    ) -> some View {
        FoundationItem(title, description: description, variant: variant, size: size) {
            Image(systemName: "tray").font(.system(size: 16)).frame(width: 16, height: 16)
                .accessibilityHidden(true)
        } actions: {
            EmptyView()
        }
    }
}

private struct AvatarScreenshotFixture: View {
    enum Example: String, CaseIterable {
        case `default`, size, badgeIcon = "badge-icon", groupCount = "group-count"
    }

    @Environment(\.colorScheme) private var scheme
    let example: Example

    var body: some View {
        var light = FoundationPalette.neutralLight
        light.primary = .black
        let theme = FoundationTheme(preset: .nova, light: light)
        let onlineColor = scheme == .dark
            ? Color(.sRGB, red: 1 / 255, green: 102 / 255, blue: 48 / 255, opacity: 1)
            : Color(.sRGB, red: 0, green: 166 / 255, blue: 62 / 255, opacity: 1)
        let online = FoundationAvatar.Badge("Online", color: onlineColor)

        return Group {
            switch example {
            case .default:
                HStack(spacing: 24) {
                    FoundationAvatar("@shadcn", size: .regular, fallback: "CN")
                    FoundationAvatar("@evilrabbit", size: .regular, fallback: "ER", badge: online)
                    avatarGroup
                }
            case .size:
                HStack(spacing: 8) {
                    FoundationAvatar("@shadcn", size: .small, fallback: "CN")
                    FoundationAvatar("@shadcn", size: .regular, fallback: "CN")
                    FoundationAvatar("@shadcn", size: .large, fallback: "CN")
                }
            case .badgeIcon:
                FoundationAvatar("@pranathip", size: .regular, fallback: "PP", badge: .init("Add", symbol: "plus"))
            case .groupCount:
                avatarGroup
            }
        }
        .foundationTheme(theme)
        .padding(24)
        .frame(maxWidth: .infinity, maxHeight: .infinity, alignment: .topLeading)
        .background(theme.palette(for: scheme).background)
        .ignoresSafeArea()
    }

    private var avatarGroup: some View {
        FoundationAvatarGroup {
            FoundationAvatar("@shadcn", size: .regular, fallback: "CN")
            FoundationAvatar("@maxleiter", size: .regular, fallback: "LR")
            FoundationAvatar("@evilrabbit", size: .regular, fallback: "ER")
            FoundationAvatarGroupCount(3)
        }
    }
}

private struct BreadcrumbScreenshotFixture: View {
    enum Example: String, CaseIterable { case `default`, ellipsis, separator }

    @Environment(\.colorScheme) private var scheme
    let example: Example

    var body: some View {
        var light = FoundationPalette.neutralLight
        light.foreground = .black
        let theme = FoundationTheme(preset: .nova, light: light)
        let separator = example == .separator
        return FoundationBreadcrumb(entries, separatorSymbol: separator ? "circle.fill" : "chevron.forward",
                                    separatorSize: separator ? 4 : nil)
            .foundationTheme(theme)
            .padding(24)
            .frame(maxWidth: .infinity, maxHeight: .infinity, alignment: .topLeading)
            .background(theme.palette(for: scheme).background)
            .ignoresSafeArea()
    }

    private var entries: [FoundationBreadcrumb.Entry] {
        var entries: [FoundationBreadcrumb.Entry] = [.init(id: "home", title: "Home", action: {})]
        switch example {
        case .default:
            entries.append(.ellipsis(id: "more", title: "Toggle menu", items: [
                .init(id: "docs", title: "Documentation", action: {}),
                .init(id: "themes", title: "Themes", action: {}),
                .init(id: "github", title: "GitHub", action: {}),
            ]))
        case .ellipsis:
            entries.append(.ellipsis(id: "more", title: "More"))
        case .separator:
            break
        }
        entries.append(.init(id: "components", title: "Components", action: {}))
        entries.append(.init(id: "current", title: "Breadcrumb"))
        return entries
    }
}

private struct EmptyScreenshotFixture: View {
    @Environment(\.colorScheme) private var scheme
    let outlined: Bool

    var body: some View {
        var light = FoundationPalette.neutralLight
        light.foreground = .black
        light.primary = .black
        let theme = FoundationTheme(preset: .nova, light: light)
        let palette = theme.palette(for: scheme)

        return FoundationEmpty(
            outlined ? "Cloud Storage Empty" : "No Projects Yet",
            message: outlined
                ? "Upload files to your cloud storage to access them anywhere."
                : "You haven't created any projects yet. Get started by creating your first project.",
            symbol: outlined ? "cloud" : "folder",
            variant: outlined ? .outline : .standard
        ) {
            if outlined {
                Button("Upload Files") {}
                    .buttonStyle(FoundationButtonStyle(.outline, size: .small))
            } else {
                VStack(spacing: 16) {
                    HStack(spacing: 8) {
                        Button("Create Project") {}
                            .buttonStyle(FoundationButtonStyle(.primary, size: .regular))
                        Button("Import Project") {}
                            .buttonStyle(FoundationButtonStyle(.outline, size: .regular))
                    }
                    Button {} label: {
                        HStack(spacing: 4) {
                            Text("Learn More")
                            Image(systemName: "arrow.up.right").accessibilityHidden(true)
                        }
                        .foregroundColor(palette.mutedForeground)
                    }
                    .buttonStyle(FoundationButtonStyle(.link, size: .small))
                }
            }
        }
        .font(.custom("Geist-Medium", size: 14))
        .foundationTheme(theme)
        .frame(width: 672)
        .padding(24)
        .frame(maxWidth: .infinity, maxHeight: .infinity, alignment: .topLeading)
        .background(palette.background)
        .ignoresSafeArea()
    }
}

private struct CollapsibleScreenshotFixture: View {
    @Environment(\.colorScheme) private var scheme
    @State var expanded: Bool

    var body: some View {
        var light = FoundationPalette.neutralLight
        light.foreground = .black
        let theme = FoundationTheme(preset: .nova, light: light)
        let palette = theme.palette(for: scheme)

        return VStack(alignment: .leading, spacing: 8) {
            HStack {
                Text("Order #4189").font(.custom("Geist-Medium", size: 14))
                    .accessibilityAddTraits(.isHeader)
                Spacer()
                FoundationCollapsibleTrigger(isExpanded: $expanded) { _ in
                    Image(systemName: "chevron.up.chevron.down").font(.system(size: 12))
                }
                .accessibilityLabel(Text("Toggle details"))
                .buttonStyle(FoundationButtonStyle(.ghost, size: .icon))
            }
            .padding(.leading, 16)
            .padding(.trailing, 10)

            HStack {
                Text("Status").foregroundColor(palette.mutedForeground)
                Spacer()
                Text("Shipped").font(.custom("Geist-Medium", size: 14))
            }
            .padding(.horizontal, 16)
            .frame(height: 38)
            .overlay(RoundedRectangle(cornerRadius: 6).strokeBorder(palette.border))

            FoundationCollapsibleContent(isExpanded: expanded) {
                VStack(spacing: 8) {
                    detail("Shipping address", value: "100 Market St, San Francisco", palette: palette)
                    detail("Items", value: "2x Studio Headphones", palette: palette)
                }
            }
        }
        .font(.custom("Geist-Regular", size: 14))
        .foregroundColor(palette.foreground)
        .foundationTheme(theme)
        .frame(width: 350)
        .padding(24)
        .frame(maxWidth: .infinity, maxHeight: .infinity, alignment: .topLeading)
        .background(palette.background)
        .ignoresSafeArea()
    }

    private func detail(_ title: String, value: String, palette: FoundationPalette) -> some View {
        VStack(alignment: .leading, spacing: 2) {
            Text(title).font(.custom("Geist-Medium", size: 14))
            Text(value).foregroundColor(palette.mutedForeground)
        }
        .padding(.horizontal, 16)
        .frame(maxWidth: .infinity, alignment: .leading)
        .frame(height: 58)
        .overlay(RoundedRectangle(cornerRadius: 6).strokeBorder(palette.border))
    }
}

private struct AccordionScreenshotFixture: View {
    enum Example: String, CaseIterable {
        case shipping = "default-initial"
        case returns = "default-returns-open"
        case disabled = "disabled-initial"
        case multiple = "multiple-initial"
        case rtl = "rtl-initial"
    }

    let example: Example
    @Environment(\.colorScheme) private var scheme

    private var headings: [String] {
        switch example {
        case .shipping, .returns:
            return ["What are your shipping options?", "What is your return policy?", "How can I contact customer support?"]
        case .disabled:
            return ["Can I access my account history?", "Premium feature information", "How do I update my email address?"]
        case .multiple:
            return ["Notification Settings", "Privacy & Security", "Billing & Subscription"]
        case .rtl:
            return ["كيف يمكنني إعادة تعيين كلمة المرور؟", "هل يمكنني تغيير خطة الاشتراك الخاصة بي؟", "ما هي طرق الدفع التي تقبلونها؟"]
        }
    }

    private var expanded: Set<Int> {
        switch example {
        case .shipping, .multiple, .rtl: return [0]
        case .returns: return [1]
        case .disabled: return []
        }
    }

    private var answer: String {
        switch example {
        case .shipping:
            return "We offer standard (5-7 days), express (2-3 days), and overnight shipping. Free shipping on international orders."
        case .returns:
            return "Returns accepted within 30 days. Items must be unused and in original packaging. Refunds processed within 5-7 business days."
        case .disabled:
            return ""
        case .multiple:
            return "Manage how you receive notifications. You can enable email alerts for updates or push notifications for mobile devices."
        case .rtl:
            return "انقر على 'نسيت كلمة المرور' في صفحة تسجيل الدخول، أدخل عنوان بريدك الإلكتروني، وسنرسل لك رابطًا لإعادة تعيين كلمة المرور. سينتهي صلاحية الرابط خلال 24 ساعة."
        }
    }

    var body: some View {
        var light = FoundationPalette.neutralLight
        light.foreground = .black
        let theme = FoundationTheme(preset: .nova, light: light)
        return FoundationAccordion(
            headings.enumerated().map { index, title in
                FoundationChoice(title, value: index, isEnabled: example != .disabled || index != 1)
            },
            expanded: .constant(expanded),
            allowsMultiple: example == .multiple
        ) { _ in
            Text(answer)
                .font(example == .rtl ? .system(size: 14) : .custom("Geist-Regular", size: 14))
                .lineSpacing(3)
                .fixedSize(horizontal: false, vertical: true)
        }
        .environment(\.layoutDirection, example == .rtl ? .rightToLeft : .leftToRight)
        .foundationTheme(theme)
        .frame(width: example == .disabled ? 672 : example == .rtl ? 448 : 512)
        .padding(24)
        .frame(maxWidth: .infinity, maxHeight: .infinity, alignment: .topLeading)
        .background(theme.palette(for: scheme).background)
        .ignoresSafeArea()
    }
}

private struct AlertScreenshotFixture: View {
    let destructive: Bool
    @Environment(\.colorScheme) private var scheme

    var body: some View {
        var light = FoundationPalette.neutralLight
        light.foreground = .black
        let theme = FoundationTheme(preset: .nova, light: light)
        return FoundationAlert(
            destructive ? "Payment failed" : "Account updated successfully",
            message: destructive
                ? "Your payment could not be processed. Please check your payment method and try again."
                : "Your profile information has been saved. Changes will be reflected immediately.",
            symbol: destructive ? "exclamationmark.circle" : "checkmark.circle",
            variant: destructive ? .destructive : .standard
        )
        .font(.custom("Geist-Regular", size: 14))
        .foundationTheme(theme)
        .frame(width: 448)
        .padding(24)
        .frame(maxWidth: .infinity, maxHeight: .infinity, alignment: .topLeading)
        .background(theme.palette(for: scheme).background)
        .ignoresSafeArea()
    }
}

private struct CardScreenshotFixture: View {
    let small: Bool
    @Environment(\.colorScheme) private var scheme
    @State private var email = ""
    @State private var password = ""

    var body: some View {
        var light = FoundationPalette.neutralLight
        light.foreground = .black
        light.primary = .black
        let theme = FoundationTheme(preset: .nova, light: light)
        let palette = theme.palette(for: scheme)
        return Group {
            if small {
                FoundationCard(size: .small) {
                    VStack(alignment: .leading, spacing: 12) {
                        VStack(alignment: .leading, spacing: 4) {
                            heading("Scheduled reports", size: 14)
                            subtitle("Weekly snapshots. No more manual exports.", palette: palette)
                        }
                        VStack(alignment: .leading, spacing: 8) {
                            feature("Choose a schedule (daily, or weekly).", palette: palette)
                            feature("Send to channels or specific teammates.", palette: palette)
                            feature("Include charts, tables, and key metrics.", palette: palette)
                        }
                        .padding(.vertical, 8)
                    }
                    .frame(maxWidth: .infinity, alignment: .leading)
                } footer: {
                    VStack(spacing: 8) {
                        fullWidthButton("Set up scheduled reports", variant: .primary, size: .small)
                        fullWidthButton("See what's new", variant: .outline, size: .small)
                    }
                }
                .frame(width: 320)
            } else {
                FoundationCard {
                    VStack(alignment: .leading, spacing: 16) {
                        VStack(alignment: .leading, spacing: 4) {
                            heading("Login to your account", size: 16)
                            subtitle("Enter your email below to login to your account", palette: palette)
                        }
                        .padding(.trailing, 62)
                        .frame(maxWidth: .infinity, alignment: .leading)
                        .overlay(
                            Button("Sign Up") {}
                                .buttonStyle(PlainButtonStyle())
                                .font(.custom("Geist-Medium", size: 14))
                                .frame(minHeight: 44, alignment: .topTrailing),
                            alignment: .topTrailing
                        )
                        VStack(alignment: .leading, spacing: 8) {
                            FoundationLabel("Email").font(.custom("Geist-Medium", size: 14))
                            FoundationInput("Email", text: $email, placeholder: "m@example.com")
                                .font(.custom("Geist-Regular", size: 16))
                        }
                        VStack(alignment: .leading, spacing: 8) {
                            HStack {
                                FoundationLabel("Password").font(.custom("Geist-Medium", size: 14))
                                Spacer()
                                Button("Forgot your password?") {}
                                    .buttonStyle(PlainButtonStyle())
                            }
                            FoundationInput("Password", text: $password, placeholder: "", secure: true)
                                .font(.custom("Geist-Regular", size: 16))
                        }
                        .padding(.top, 8)
                    }
                } footer: {
                    VStack(spacing: 8) {
                        fullWidthButton("Login", variant: .primary, size: .regular)
                        fullWidthButton("Login with Google", variant: .outline, size: .regular)
                    }
                }
                .frame(width: 384)
            }
        }
        .font(.custom("Geist-Regular", size: 14))
        .foundationTheme(theme)
        .padding(.leading, small ? 41 : 12)
        .padding(.top, 24)
        .frame(maxWidth: .infinity, maxHeight: .infinity, alignment: .topLeading)
        .background(palette.background)
        .ignoresSafeArea()
    }

    private func heading(_ title: String, size: CGFloat) -> some View {
        Text(title).font(.custom("Geist-Medium", size: size)).frame(minHeight: 20)
    }

    private func subtitle(_ text: String, palette: FoundationPalette) -> some View {
        Text(text).foregroundColor(palette.mutedForeground).frame(minHeight: 20)
    }

    private func feature(_ text: String, palette: FoundationPalette) -> some View {
        HStack(alignment: .top, spacing: 8) {
            Image(systemName: "chevron.right")
                .font(.system(size: 10, weight: .medium))
                .frame(width: 16, height: 16)
                .foregroundColor(palette.mutedForeground)
                .accessibilityHidden(true)
            Text(text).frame(minHeight: 20, alignment: .topLeading)
        }
    }

    private func fullWidthButton(
        _ title: String, variant: FoundationButtonStyle.Variant, size: FoundationButtonStyle.Size
    ) -> some View {
        Button {} label: {
            Text(title).frame(maxWidth: .infinity)
        }
        .buttonStyle(FoundationButtonStyle(variant, size: size))
        .font(.custom("Geist-Medium", size: size == .small ? 12.8 : 14))
    }
}

private struct LabelFieldScreenshotFixture: View {
    enum Example { case label, input, textarea }

    let example: Example
    @Environment(\.colorScheme) private var scheme
    @State private var accepted = false
    @State private var username = ""
    @State private var password = ""
    @State private var feedback = ""

    var body: some View {
        var light = FoundationPalette.neutralLight
        light.foreground = .black
        let theme = FoundationTheme(preset: .nova, light: light)

        return Group {
            switch example {
            case .label:
                Toggle(isOn: $accepted) {
                    FoundationLabel("Accept terms and conditions")
                }
                .toggleStyle(FoundationCheckbox())
                .font(.custom("Geist-Medium", size: 14))
            case .input:
                VStack(alignment: .leading, spacing: 20) {
                    FoundationField("Username", description: "Choose a unique username for your account.") {
                        FoundationInput("Username", text: $username, placeholder: "Max Leiter")
                            .font(editorFont)
                    }
                    FoundationField("Password", description: "Must be at least 8 characters long.",
                                    descriptionPlacement: .beforeContent) {
                        FoundationInput("Password", text: $password, placeholder: "••••••••", secure: true)
                            .font(editorFont)
                    }
                }
                .font(.custom("Geist-Regular", size: 14))
                .frame(width: 320)
            case .textarea:
                FoundationField("Feedback", description: "Share your thoughts about our service.") {
                    FoundationTextarea("Feedback", text: $feedback, placeholder: "Your feedback helps us improve...")
                        .font(editorFont)
                        .frame(height: 64)
                }
                .font(.custom("Geist-Regular", size: 14))
                .frame(width: 320)
            }
        }
        .foundationTheme(theme)
        .padding(.leading, 24)
        .padding(.top, example == .label ? 10 : 24)
        .frame(maxWidth: .infinity, maxHeight: .infinity, alignment: .topLeading)
        .background(theme.palette(for: scheme).background)
        .ignoresSafeArea()
    }

    private var editorFont: Font { .custom("Geist-Regular", size: 16) }
}

private struct ButtonGroupScreenshotFixture: View {
    let vertical: Bool
    @Environment(\.colorScheme) private var scheme

    var body: some View {
        var light = FoundationPalette.neutralLight
        light.foreground = .black
        let theme = FoundationTheme(preset: .nova, light: light)

        return Group {
            if vertical {
                FoundationButtonGroup(.vertical) {
                    iconButton("Increase", plus: true, size: .icon)
                    iconButton("Decrease", plus: false, size: .icon)
                }
                .padding(.top, 24)
            } else {
                VStack(alignment: .leading, spacing: 0) {
                    sizeRow("Small", size: .small, iconSize: .iconSmall)
                    sizeRow("Default", size: .regular, iconSize: .icon).padding(.top, 18)
                    sizeRow("Large", size: .large, iconSize: .iconLarge).padding(.top, 22)
                }
                .padding(.top, 16)
            }
        }
        .foundationTheme(theme)
        .padding(.leading, 24)
        .frame(maxWidth: .infinity, maxHeight: .infinity, alignment: .topLeading)
        .background(theme.palette(for: scheme).background)
        .ignoresSafeArea()
    }

    private func sizeRow(_ label: String, size: FoundationButtonStyle.Size, iconSize: FoundationButtonStyle.Size) -> some View {
        FoundationButtonGroup {
            Button(label) {}.buttonStyle(FoundationButtonStyle(.outline, size: size))
            Button("Button") {}.buttonStyle(FoundationButtonStyle(.outline, size: size))
            Button("Group") {}.buttonStyle(FoundationButtonStyle(.outline, size: size))
            iconButton("Add", plus: true, size: iconSize)
        }
        .font(.custom("Geist-Medium", size: size == .small ? 12.8 : 14))
    }

    private func iconButton(_ title: String, plus: Bool, size: FoundationButtonStyle.Size) -> some View {
        let side: CGFloat = size == .iconSmall ? 14 : 16
        return Button {} label: {
            Path { path in
                let start = side * 5 / 24
                let end = side - start
                path.move(to: CGPoint(x: start, y: side / 2))
                path.addLine(to: CGPoint(x: end, y: side / 2))
                if plus {
                    path.move(to: CGPoint(x: side / 2, y: start))
                    path.addLine(to: CGPoint(x: side / 2, y: end))
                }
            }
            .stroke(style: StrokeStyle(lineWidth: side / 12, lineCap: .round))
            .frame(width: side, height: side)
        }
        .buttonStyle(FoundationButtonStyle(.outline, size: size))
        .accessibilityLabel(Text(title))
    }
}

private struct ToggleScreenshotFixture: View {
    let grouped: Bool
    @State private var isOn: Bool
    @State private var selection: Set<ToggleScreenshotIcon>
    @Environment(\.colorScheme) private var scheme

    init(grouped: Bool, selected: Bool) {
        self.grouped = grouped
        _isOn = State(initialValue: selected)
        _selection = State(initialValue: selected ? [.bold] : [])
    }

    var body: some View {
        var light = FoundationPalette.neutralLight
        light.foreground = .black
        let theme = FoundationTheme(preset: .nova, light: light)
        let icons: [FoundationChoice<ToggleScreenshotIcon>] = [
            FoundationChoice("Bold", value: .bold),
            FoundationChoice("Italic", value: .italic),
            FoundationChoice("Underline", value: .underline),
        ]
        return Group {
            if grouped {
                FoundationToggleGroup(icons, selection: $selection, variant: .outline, allowsMultiple: true) { choice in
                    choice.value.stroke(style: StrokeStyle(lineWidth: 4.0 / 3, lineCap: .round, lineJoin: .round))
                        .frame(width: 16, height: 16)
                        .accessibilityHidden(true)
                }
                .frame(height: 44)
            } else {
                FoundationToggle(isOn: $isOn, variant: .outline, size: .small) {
                    HStack(spacing: 4) {
                        ToggleScreenshotIcon.bookmark
                            .fill(isOn ? theme.palette(for: scheme).foreground : .clear)
                            .overlay(ToggleScreenshotIcon.bookmark.stroke(
                                style: StrokeStyle(lineWidth: 7.0 / 6, lineCap: .round, lineJoin: .round)
                            ))
                            .frame(width: 14, height: 14)
                            .accessibilityHidden(true)
                        Text("Bookmark")
                    }
                }
                .font(.custom("Geist-Medium", size: 12.8))
            }
        }
        .foundationTheme(theme)
        .padding(.leading, grouped ? 21 : 24)
        .padding(.top, grouped ? 18 : 16)
        .frame(maxWidth: .infinity, maxHeight: .infinity, alignment: .topLeading)
        .background(theme.palette(for: scheme).background)
        .ignoresSafeArea()
    }
}

// Reference glyphs are drawn on Lucide's 24-point grid.
private enum ToggleScreenshotIcon: Shape, Hashable {
    case bookmark, bold, italic, underline

    func path(in rect: CGRect) -> Path {
        let path = Path { path in
            func move(_ x: CGFloat, _ y: CGFloat) { path.move(to: CGPoint(x: x, y: y)) }
            func line(_ x: CGFloat, _ y: CGFloat) { path.addLine(to: CGPoint(x: x, y: y)) }
            func curve(_ x: CGFloat, _ y: CGFloat, _ a: CGFloat, _ b: CGFloat, _ c: CGFloat, _ d: CGFloat) {
                path.addCurve(to: CGPoint(x: x, y: y), control1: CGPoint(x: a, y: b), control2: CGPoint(x: c, y: d))
            }
            switch self {
            case .bookmark:
                move(7, 3)
                line(17, 3)
                curve(19, 5, 18.1, 3, 19, 3.9)
                line(19, 20)
                curve(17.5, 20.87, 19, 20.8, 18.2, 21.3)
                line(13, 18.29)
                curve(11, 18.29, 12.4, 17.94, 11.6, 17.94)
                line(6.5, 20.87)
                curve(5, 20, 5.8, 21.3, 5, 20.8)
                line(5, 5)
                curve(7, 3, 5, 3.9, 5.9, 3)
                path.closeSubpath()
            case .bold:
                move(6, 12)
                line(14, 12)
                curve(18, 16, 16.21, 12, 18, 13.79)
                curve(14, 20, 18, 18.21, 16.21, 20)
                line(6, 20)
                line(6, 4)
                line(13, 4)
                curve(17, 8, 15.21, 4, 17, 5.79)
                curve(13, 12, 17, 10.21, 15.21, 12)
            case .italic:
                move(19, 4)
                line(10, 4)
                move(14, 20)
                line(5, 20)
                move(15, 4)
                line(9, 20)
            case .underline:
                move(6, 4)
                line(6, 10)
                curve(12, 16, 6, 13.31, 8.69, 16)
                curve(18, 10, 15.31, 16, 18, 13.31)
                line(18, 4)
                move(4, 20)
                line(20, 20)
            }
        }
        return path.applying(CGAffineTransform(
            a: rect.width / 24, b: 0, c: 0, d: rect.height / 24, tx: rect.minX, ty: rect.minY
        ))
    }
}

private struct SliderScreenshotFixture: View {
    enum Example: String, CaseIterable {
        case initial = "default-initial"
        case increased = "default-increased"
        case disabled = "disabled-initial"
        case rtl = "rtl-initial"
    }

    let example: Example
    @State private var value: Double
    @Environment(\.colorScheme) private var scheme

    init(example: Example) {
        self.example = example
        switch example {
        case .initial, .rtl: _value = State(initialValue: 75)
        case .increased: _value = State(initialValue: 76)
        case .disabled: _value = State(initialValue: 50)
        }
    }

    var body: some View {
        var light = FoundationPalette.neutralLight
        light.primary = .black
        let theme = FoundationTheme(preset: .nova, light: light)
        return FoundationSlider("Value", value: $value, in: 0...100)
            .disabled(example == .disabled)
            .environment(\.layoutDirection, example == .rtl ? .rightToLeft : .leftToRight)
            .foundationTheme(theme)
            .frame(width: 320)
            .padding(.top, 4)
            .frame(maxWidth: .infinity, maxHeight: .infinity, alignment: .top)
            .background(theme.palette(for: scheme).background)
            .ignoresSafeArea()
    }
}

private struct InputOtpScreenshotFixture: View {
    enum Example: String, CaseIterable {
        case complete = "default-initial"
        case fourDigits = "four-digits-initial"
        case disabled = "disabled-initial"
    }

    let example: Example
    @State private var code: String
    @Environment(\.colorScheme) private var scheme

    init(example: Example) {
        self.example = example
        _code = State(initialValue: example == .fourDigits ? "" : "123456")
    }

    var body: some View {
        var light = FoundationPalette.neutralLight
        light.foreground = .black
        let theme = FoundationTheme(preset: .nova, light: light)
        let disabled = example == .disabled
        return FoundationInputOtp(
            code: $code,
            length: example == .fourDigits ? 4 : 6,
            groupBreaks: disabled ? [3] : []
        )
        .disabled(disabled)
        .font(.custom("Geist-Regular", size: 14))
        .foundationTheme(theme)
        .padding(.horizontal, 24)
        .padding(.top, 18)
        .frame(maxWidth: .infinity, maxHeight: .infinity, alignment: .topLeading)
        .background(theme.palette(for: scheme).background)
        .ignoresSafeArea()
    }
}

private struct TextFieldScreenshotFixture: View {
    enum Example { case input, basicInput, textarea, textareaField }

    let example: Example
    @State private var value = ""
    @Environment(\.colorScheme) private var scheme

    var body: some View {
        var light = FoundationPalette.neutralLight
        light.foreground = .black
        let theme = FoundationTheme(preset: .nova, light: light)
        let palette = theme.palette(for: scheme)
        return VStack(alignment: .leading, spacing: 0) {
            switch example {
            case .input:
                fieldLabel("API Key")
                FoundationInput("API Key", text: $value, placeholder: "sk-...", secure: true)
                    .font(fieldFont)
                    .padding(.top, 2)
                description("Your API key is encrypted and stored securely.", palette: palette)
                    .padding(.top, 3)
            case .basicInput:
                FoundationInput("Text", text: $value, placeholder: "Enter text")
                    .font(fieldFont)
            case .textarea:
                FoundationTextarea("Message", text: $value, placeholder: "Type your message here.")
                    .font(fieldFont)
                    .frame(height: 64)
            case .textareaField:
                fieldLabel("Message")
                description("Enter your message below.", palette: palette)
                    .padding(.top, 5)
                FoundationTextarea("Message", text: $value, placeholder: "Type your message here.")
                    .font(fieldFont)
                    .frame(height: 64)
                    .padding(.top, 8)
            }
        }
        .font(.custom("Geist-Regular", size: 14))
        .foregroundColor(palette.foreground)
        .foundationTheme(theme)
        .frame(width: 672, alignment: .leading)
        .padding(.leading, 24)
        .padding(.top, example == .basicInput ? 18 : 24)
        .frame(maxWidth: .infinity, maxHeight: .infinity, alignment: .topLeading)
        .background(palette.background)
        .ignoresSafeArea()
    }

    private func fieldLabel(_ title: String) -> some View {
        Text(title).font(.custom("Geist-Medium", size: 14)).frame(height: 19)
    }

    private var fieldFont: Font { .custom("Geist-Regular", size: 16) }

    private func description(_ text: String, palette: FoundationPalette) -> some View {
        Text(text).foregroundColor(palette.mutedForeground).frame(height: 20)
    }
}

private struct RadioGroupScreenshotFixture: View {
    enum Example: String, CaseIterable {
        case initial = "default-initial"
        case compactSelected = "default-compact"
        case disabled = "disabled-initial"
    }

    @Environment(\.colorScheme) private var scheme
    @State private var selection: String?
    let example: Example

    init(example: Example) {
        self.example = example
        switch example {
        case .initial: _selection = State(initialValue: "comfortable")
        case .compactSelected: _selection = State(initialValue: "compact")
        case .disabled: _selection = State(initialValue: "option2")
        }
    }

    var body: some View {
        var light = FoundationPalette.neutralLight
        light.foreground = .black
        light.primary = .black
        let theme = FoundationTheme(preset: .nova, light: light)
        let disabled = example == .disabled
        let choices = disabled ? [
            FoundationChoice("Disabled", value: "option1", isEnabled: false),
            FoundationChoice("Option 2", value: "option2"),
            FoundationChoice("Option 3", value: "option3"),
        ] : [
            FoundationChoice("Default", value: "default"),
            FoundationChoice("Comfortable", value: "comfortable"),
            FoundationChoice("Compact", value: "compact"),
        ]
        return FoundationRadioGroup(choices, selection: $selection)
            .font(.custom(disabled ? "Geist-Regular" : "Geist-Medium", size: 14))
            .foundationTheme(theme)
            .padding(.horizontal, 24)
            .padding(.top, disabled ? 12 : 10)
            .frame(maxWidth: .infinity, maxHeight: .infinity, alignment: .topLeading)
            .background(theme.palette(for: scheme).background)
            .ignoresSafeArea()
    }
}

private struct CheckboxScreenshotFixture: View {
    enum Example: String, CaseIterable {
        case initial = "default-initial"
        case checked = "default-checked"
        case basic = "basic-initial"
        case disabled = "disabled-initial"
    }

    @Environment(\.colorScheme) private var scheme
    @State private var termsAccepted: Bool
    @State private var additionalTerms = true
    @State private var unavailableNotifications = false
    @State private var notifications = false
    let example: Example

    init(example: Example) {
        self.example = example
        _termsAccepted = State(initialValue: example == .checked)
    }

    var body: some View {
        var light = FoundationPalette.neutralLight
        light.foreground = .black
        light.primary = .black
        let theme = FoundationTheme(preset: .nova, light: light)
        let palette = theme.palette(for: scheme)
        let simple = example == .basic || example == .disabled
        return Group {
            if simple {
                if example == .basic {
                    Toggle("Accept terms and conditions", isOn: $termsAccepted)
                } else {
                    Toggle("Enable notifications", isOn: $unavailableNotifications).disabled(true)
                }
            } else {
                VStack(alignment: .leading, spacing: 0) {
                    Toggle("Accept terms and conditions", isOn: $termsAccepted)
                    Toggle(isOn: $additionalTerms) {
                        fieldLabel(
                            "Accept terms and conditions",
                            description: "By clicking this checkbox, you agree to the terms.",
                            palette: palette
                        )
                    }
                    .padding(.top, 5)
                    Toggle("Enable notifications", isOn: $unavailableNotifications)
                        .disabled(true)
                        .padding(.top, 7)
                    Toggle(isOn: $notifications) {
                        fieldLabel(
                            "Enable notifications",
                            description: "You can enable or disable notifications at any time.",
                            palette: palette
                        )
                    }
                    .padding(.horizontal, 11)
                    .frame(width: 384, height: 65, alignment: .leading)
                    .overlay(RoundedRectangle(cornerRadius: 10).strokeBorder(palette.border))
                    .padding(.top, 8)
                }
            }
        }
        .toggleStyle(FoundationCheckbox())
        .font(.custom("Geist-Medium", size: 14))
        .foundationTheme(theme)
        .padding(.leading, simple ? 89 : 12)
        .padding(.top, simple ? 12 : 10)
        .frame(maxWidth: .infinity, maxHeight: .infinity, alignment: .topLeading)
        .background(palette.background)
        .ignoresSafeArea()
    }

    private func fieldLabel(_ title: String, description: String, palette: FoundationPalette) -> some View {
        VStack(alignment: .leading, spacing: 4) {
            Text(title).frame(height: 18)
            Text(description)
                .font(.custom("Geist-Regular", size: 14))
                .foregroundColor(palette.mutedForeground)
                .frame(height: 20)
        }
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
