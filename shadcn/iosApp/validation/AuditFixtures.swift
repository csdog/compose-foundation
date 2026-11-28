import SwiftUI

@MainActor
enum SwiftUIAuditFixtures {
    private struct Row: Identifiable {
        let id: Int
        let name: String
    }

    private static let options = [
        FoundationChoice("General", value: "general"),
        FoundationChoice("Advanced", value: "advanced"),
        FoundationChoice("Disabled", value: "disabled", isEnabled: false)
    ]
    private static let date = Date(timeIntervalSince1970: 1_750_000_000)

    static var cases: [String: () -> AnyView] {
        [
            "accordion": { AnyView(FoundationAccordion(options, expanded: .constant(["general"])) { Text($0) }) },
            "alert": { AnyView(FoundationAlert("Error", message: "Check the settings.", variant: .destructive)) },
            "alert-dialog": { AnyView(Button("Delete") {}.modifier(FoundationAlertDialog(
                isPresented: .constant(false), title: "Delete", message: "Confirm deletion", destructive: true
            ) {})) },
            "aspect-ratio": { AnyView(FoundationAspectRatio(16 / 9) { Text("16:9").padding() }.border(.gray)) },
            "attachment": { AnyView(FoundationAttachment("Report", detail: "12 KB", state: .uploading(0.5)) {
                Image(systemName: "doc")
            }) },
            "avatar": { AnyView(FoundationAvatar("Ada Lovelace")) },
            "badge": { AnyView(HStack {
                FoundationBadge { Text("Default") }
                FoundationBadge(.secondary) { Text("Secondary") }
                FoundationBadge(.outline) { Text("Outline") }
                FoundationBadge(.destructive) { Text("Error") }
            }) },
            "breadcrumb": { AnyView(FoundationBreadcrumb([
                .init(id: "home", title: "Home") {}, .init(id: "docs", title: "Docs")
            ])) },
            "bubble": { AnyView(FoundationBubble(.outgoing) { Text("Sent") }) },
            "button": { AnyView(VStack {
                Button("Primary") {}.buttonStyle(FoundationButtonStyle())
                Button("Secondary") {}.buttonStyle(FoundationButtonStyle(.secondary))
                Button("Outline") {}.buttonStyle(FoundationButtonStyle(.outline))
                Button("Ghost") {}.buttonStyle(FoundationButtonStyle(.ghost))
                Button("Delete") {}.buttonStyle(FoundationButtonStyle(.destructive)).disabled(true)
                Button("Link") {}.buttonStyle(FoundationButtonStyle(.link))
            }) },
            "button-group": { AnyView(FoundationButtonGroup {
                Button("Back") {}
                Button("Next") {}
            }) },
            "calendar": { AnyView(FoundationCalendar(
                range: .constant(FoundationCalendarRange(start: date, end: date.addingTimeInterval(86400 * 4))),
                in: date...date.addingTimeInterval(86400 * 12)
            )) },
            "card": { AnyView(FoundationCard { Text("Project details") }) },
            "carousel": { AnyView(FoundationCarousel(pageCount: 3, selection: .constant(1)) { Text("Page \($0 + 1)") }) },
            "chart": { AnyView(FoundationChart(labels: ["A", "B", "C"], series: [
                .init("Series", values: [1, -0.5, 2])
            ], variant: .area, selection: .constant(1)).frame(height: 160)) },
            "checkbox": { AnyView(VStack(alignment: .leading) {
                Toggle("Verified", isOn: .constant(true)).toggleStyle(FoundationCheckbox())
                FoundationTriStateCheckbox(state: .constant(.mixed)) { Text("Some options verified") }
                FoundationTriStateCheckbox(state: .constant(.unchecked)) { Text("Verification pending") }
                    .disabled(true)
            }) },
            "chip": { AnyView(FoundationChip("Selected", isSelected: .constant(true))) },
            "code-block": { AnyView(FoundationCodeBlock("let value = 1\nprint(value)", language: "swift", showLineNumbers: true)) },
            "collapsible": { AnyView(FoundationCollapsible("Details", isExpanded: .constant(true)) { Text("Expanded") }) },
            "combobox": { AnyView(FoundationCombobox("Choose a section", choices: options, selection: .constant("advanced"))) },
            "command": { AnyView(FoundationCommand(sections: [.init(id: "settings", title: "Settings", actions: options)]) { _ in }) },
            "context-menu": { AnyView(Text("Long press here").modifier(FoundationContextMenu(triggerStyle: .outline) {
                Button("Copy") {}
                Button("Forward") {}.disabled(true)
            })) },
            "dialog": { AnyView(Button("Open dialog") {}.modifier(FoundationDialog(
                isPresented: .constant(false), title: "Edit"
            ) { Text("Dialog content") })) },
            "drawer": { AnyView(Text("Background").frame(maxWidth: .infinity, maxHeight: .infinity)
                .modifier(FoundationDrawer(isPresented: .constant(true), title: "Edit") { Text("Drawer content") })) },
            "dropdown-menu": { AnyView(FoundationDropdownMenu { Button("Edit") {} } trigger: { Text("Actions") }) },
            "emoji-text": { AnyView(FoundationEmojiText("Hello 👋 👩‍💻")) },
            "empty": { AnyView(FoundationEmpty("No files", message: "Upload a file.", symbol: "tray")) },
            "field": { AnyView(FoundationField("Email", description: "Work address", error: "Required") {
                TextField("Email", text: .constant(""))
            }) },
            "hover-card": { AnyView(Text("Hover here").modifier(FoundationHoverCard { Text("Card") })) },
            "icon": { AnyView(FoundationIcon(systemName: "folder", accessibilityLabel: "Folder")) },
            "input": { AnyView(FoundationInput("Name", text: .constant("Ada"), invalid: true)) },
            "input-group": { AnyView(FoundationInputGroup { TextField("Search", text: .constant("Query")) }
                leading: { Image(systemName: "magnifyingglass") } trailing: { Text("⌘K") }) },
            "input-otp": { AnyView(FoundationInputOtp(code: .constant("123"))) },
            "item": { AnyView(FoundationItem("Report", description: "Ready for review") { Image(systemName: "doc") }
                actions: { Button("Open") {} }) },
            "kbd": { AnyView(FoundationKbd("⌘K")) },
            "label": { AnyView(FoundationLabel("Name")) },
            "marker": { AnyView(FoundationMarker("Today", variant: .separator)) },
            "menubar": { AnyView(FoundationMenubar { Menu("File") { Button("New") {} } }) },
            "message": { AnyView(FoundationMessage(alignment: .outgoing, author: "Ada", timestamp: date) { Text("Sent") }) },
            "message-scroller": { AnyView(FoundationMessageScroller(revision: 1, followsLatest: .constant(false)) {
                ForEach(0..<6) { Text("Message \($0 + 1)") }
            }.frame(height: 160)) },
            "navigation-menu": { AnyView(FoundationNavigationMenu {
                Button("Home") {}
                Button("Docs") {}
            }) },
            "pagination": { AnyView(FoundationPagination(pageCount: 100, page: .constant(50))) },
            "popover": { AnyView(Button("Open popover") {}.modifier(FoundationPopover(isPresented: .constant(false)) {
                Text("Popover body")
            })) },
            "progress": { AnyView(FoundationProgress(value: 0.45)) },
            "questionnaire": { AnyView(FoundationQuestionnaire([
                .init(id: "q1", title: "Choose an option", kind: .single(options))
            ], index: .constant(0), answers: .constant(["q1": .single("general")])) { _ in }) },
            "radio-group": { AnyView(FoundationRadioGroup(options, selection: .constant("general"))) },
            "resizable": { AnyView(FoundationResizable(fraction: .constant(0.4)) { Text("First") }
                second: { Text("Second") }.frame(height: 150)) },
            "scroll-area": { AnyView(FoundationScrollArea { ForEach(0..<20) { Text("Item \($0 + 1)") } }.frame(height: 130)) },
            "scroll-fade": { AnyView(Rectangle().fill(.gray).frame(height: 120).modifier(FoundationScrollFade())) },
            "select": { AnyView(FoundationSelect("Choose a section", choices: options, selection: .constant("advanced"))) },
            "separator": { AnyView(FoundationSeparator()) },
            "sheet": { AnyView(Button("Show sheet") {}.modifier(FoundationSheet(isPresented: .constant(false), title: "Edit") {
                Text("Sheet content")
            })) },
            "shimmer": { AnyView(Rectangle().fill(.gray).frame(height: 50).modifier(FoundationShimmer())) },
            "sidebar": { AnyView(FoundationSidebar(isOpen: .constant(true)) { Text("Navigation") }
                content: { Text("Main") }.frame(height: 200)) },
            "skeleton": { AnyView(FoundationSkeleton().frame(height: 28)) },
            "slider": { AnyView(FoundationSlider("Level", value: .constant(25), in: 0...100)) },
            "spinner": { AnyView(FoundationSpinner()) },
            "stepper": { AnyView(FoundationStepper(options, current: "advanced")) },
            "switch": { AnyView(Toggle("Preferences", isOn: .constant(true)).toggleStyle(FoundationSwitchStyle())) },
            "table": { AnyView(FoundationTable(columns: [.init(id: "name", title: "Name", value: \.name)],
                rows: [Row(id: 1, name: "Ada")])) },
            "tabs": { AnyView(FoundationTabs(options, selection: .constant("advanced")) { Text($0) }) },
            "text": { AnyView(FoundationText("Native typography", variant: .title)) },
            "textarea": { AnyView(FoundationTextarea("Notes", text: .constant("A note"), invalid: true).frame(height: 130)) },
            "theme": { AnyView(FoundationCard { FoundationText("Neutral palette", variant: .headline) }
                .foundationTheme(FoundationTheme(cornerRadius: 18))) },
            "toast": { AnyView(Color.clear.frame(height: 150).modifier(FoundationToast(
                isPresented: .constant(true), title: "Saved", message: "Changes are ready.", autoDismiss: false
            ))) },
            "toggle": { AnyView(FoundationToggle(isOn: .constant(true)) { Text("Bold") }) },
            "toggle-group": { AnyView(FoundationToggleGroup(options, selection: .constant(["advanced"]))) },
            "tooltip": { AnyView(Text("Tooltip target").modifier(FoundationTooltip("A hint"))) },
            "trace": { AnyView(FoundationTrace("Index documents", status: .succeeded, isExpanded: .constant(true)) {
                Text("12 indexed")
            }) }
        ]
    }

    struct Registry: Decodable {
        struct Item: Decodable { let name: String }
        let items: [Item]
    }

}

struct MenuGroupsInteractionFixture: View {
    @State private var result = "No action"

    var body: some View {
        VStack(alignment: .leading, spacing: 22) {
            FoundationMenubar(groups: [
                .init(id: "file", title: "File", actions: [
                    .init(id: "new", title: "New file") { result = "New file" },
                    .init(id: "locked", title: "Locked file", isEnabled: false) { result = "Locked file" },
                    .init(id: "save", title: "Save file") { result = "Save file" },
                ]),
                .init(id: "disabled", title: "Restricted menu", isEnabled: false, actions: []),
                .init(id: "edit", title: "Edit", actions: [
                    .init(id: "copy", title: "Copy") { result = "Copy" },
                    .init(id: "paste", title: "Paste") { result = "Paste" },
                ]),
            ])
            FoundationNavigationMenu(groups: [
                .init(id: "project", title: "Project", actions: [
                    .init(id: "overview", title: "Overview") { result = "Overview" },
                    .init(id: "settings", title: "Settings") { result = "Settings" },
                ]),
                .init(id: "resources", title: "Resources", actions: [
                    .init(id: "guide", title: "User guide") { result = "User guide" },
                ]),
            ])
            Text("Result: \(result)")
            Spacer()
        }
        .padding()
    }
}

struct PopoverInteractionFixture: View {
    @State private var expanded = false

    var body: some View {
        VStack(spacing: 0) {
            Button("Open popover") { expanded.toggle() }
                .padding(.horizontal, 14)
                .padding(.vertical, 10)
                .modifier(FoundationPopover(isPresented: $expanded, arrowEdge: .top) {
                    VStack(alignment: .leading, spacing: 6) {
                        Text("Popover content").font(.headline)
                        Text("Anchored to the button above.").font(.subheadline)
                        Button("Close popover") { expanded = false }
                    }
                    .frame(width: 190, alignment: .leading)
                })
            Spacer()
        }
        .frame(maxWidth: .infinity)
        .padding(.top, 70)
    }
}

@available(iOS 17, macOS 14, *)
struct PopoverFocusInteractionFixture: View {
    @FocusState private var triggerFocused: Bool
    @State private var expanded = false
    @State private var dismissals = 0

    var body: some View {
        VStack(spacing: 16) {
            Button("Open popover") { expanded = true }
                .focusable(interactions: .activate)
                .focused($triggerFocused)
                .onKeyPress(keys: ["p"], phases: .down) { _ in
                    expanded = true
                    return .handled
                }
                .padding(10)
                .modifier(FoundationPopover(isPresented: $expanded, onDismiss: {
                    dismissals += 1
                    triggerFocused = true
                }) {
                    Panel(expanded: $expanded)
                })
            Text("Popover dismissals: \(dismissals)")
            Text(triggerFocused ? "Trigger focused" : "Trigger unfocused")
            Spacer()
        }
        .frame(maxWidth: .infinity)
        .padding(.top, 50)
    }

    private struct Panel: View {
        @Binding var expanded: Bool
        @FocusState private var closeFocused: Bool

        var body: some View {
            VStack(alignment: .leading, spacing: 12) {
                Text("Popover content").accessibilityAddTraits(.isHeader)
                Button("Close popover") { expanded = false }
                    .focusable(interactions: .activate)
                    .focused($closeFocused)
                    .onKeyPress(keys: [.return], phases: .down) { _ in
                        expanded = false
                        return .handled
                    }
                Text(closeFocused ? "Panel focused" : "Panel unfocused")
            }
            .frame(width: 195, alignment: .leading)
            .defaultFocus($closeFocused, true)
            .task {
                await Task.yield()
                closeFocused = true
            }
        }
    }
}

struct ThemePresetSample: View {
    @Environment(\.foundationTheme) private var theme
    @State private var name = "Taylor"
    @State private var saves = 0

    var body: some View {
        FoundationCard {
            VStack(alignment: .leading, spacing: 12) {
                Text("Account")
                    .font(theme.font(.title2, weight: .semibold, heading: true))
                    .textCase(theme.preset == .sera ? .uppercase : nil)
                FoundationLabel("Display name")
                FoundationInput("Display name", text: $name)
                HStack {
                    Button("Save changes") { saves += 1 }.buttonStyle(FoundationButtonStyle())
                    Button("Reset name") { name = "Taylor" }.buttonStyle(FoundationButtonStyle(.outline))
                }
                Text("Saves: \(saves)").font(theme.font(.caption))
            }
        }
    }
}

struct ThemePresetsInteractionFixture: View {
    @State private var preset: FoundationStylePreset = .nova
    @State private var dark = false

    var body: some View {
        let scheme: ColorScheme = dark ? .dark : .light
        let theme = FoundationTheme(preset: preset)

        VStack(alignment: .leading, spacing: 16) {
            HStack {
                Menu {
                    ForEach(FoundationStylePreset.allCases, id: \.self) { style in
                        Button(style.rawValue.capitalized) { preset = style }
                    }
                } label: {
                    Label(preset.rawValue.capitalized, systemImage: "chevron.down")
                }
                .accessibilityLabel(Text("Style preset"))
                .accessibilityValue(Text(preset.rawValue.capitalized))
                Spacer()
                Button(dark ? "Use light appearance" : "Use dark appearance") { dark.toggle() }
            }
            ThemePresetSample()
            Spacer(minLength: 0)
        }
        .padding(20)
        .foregroundColor(theme.palette(for: scheme).foreground)
        .background(theme.palette(for: scheme).background.ignoresSafeArea())
        .environment(\.colorScheme, scheme)
        .foundationTheme(theme)
    }
}

struct CarouselInteractionFixture: View {
    let rightToLeft: Bool

    @State private var selection = 0
    @State private var activated = 0

    private let heights: [CGFloat] = [132, 184, 154]
    private let colors: [Color] = [
        Color(red: 0.66, green: 0.12, blue: 0.24),
        Color(red: 0.14, green: 0.30, blue: 0.82),
        Color(red: 0.11, green: 0.39, blue: 0.30),
    ]

    var body: some View {
        ScrollView {
            VStack(spacing: 12) {
                Text("Bound selection: \(selection + 1)")
                FoundationCarousel(pageCount: heights.count, selection: $selection) { index in
                    ZStack {
                        colors[index].accessibilityHidden(true)
                        VStack(spacing: 8) {
                            Text("Slide \(index + 1)").font(.headline)
                            Button("Activate slide \(index + 1)") { activated = index + 1 }
                        }
                        .foregroundColor(.white)
                    }
                    .frame(height: heights[index])
                }
                .environment(\.layoutDirection, rightToLeft ? .rightToLeft : .leftToRight)
                Text("Activated slide: \(activated)")
                Button("Jump to last slide") { selection = heights.count - 1 }
                Color.clear.frame(height: 700).accessibilityHidden(true)
            }
            .padding(24)
        }
    }
}

struct CalendarNavigationFixture: View {
    private static var calendar: Calendar {
        var value = Calendar(identifier: .gregorian)
        value.timeZone = TimeZone(secondsFromGMT: 0)!
        value.firstWeekday = 1
        return value
    }

    private static func date(_ year: Int, _ month: Int, _ day: Int) -> Date {
        calendar.date(from: DateComponents(year: year, month: month, day: day))!
    }

    @State private var range: FoundationCalendarRange? = .init(
        start: Self.date(2025, 1, 20), end: Self.date(2025, 1, 20)
    )
    @State private var selected: Date? = Self.date(2025, 1, 31)

    var body: some View {
        VStack(spacing: 8) {
            if ProcessInfo.processInfo.arguments.contains("--calendar-keys") {
                Text("Selected: \(dayLabel(selected))").accessibilityIdentifier("calendar-selection")
                FoundationCalendar(selection: $selected, in: Self.date(2025, 1, 20)...Self.date(2025, 2, 15))
            } else {
                Text("Range: \(dayLabel(range?.start)) to \(dayLabel(range?.end))")
                FoundationCalendar(
                    range: $range, in: Self.date(2025, 1, 20)...Self.date(2026, 3, 10),
                    numberOfMonths: 2, captionLayout: .dropdown
                )
            }
        }
        .padding(12)
        .environment(\.calendar, Self.calendar)
        .environment(\.locale, Locale(identifier: "en_US"))
    }

    private func dayLabel(_ date: Date?) -> String {
        guard let date else { return "none" }
        let formatter = DateFormatter()
        formatter.locale = Locale(identifier: "en_US_POSIX")
        formatter.calendar = Self.calendar
        formatter.timeZone = Self.calendar.timeZone
        formatter.dateFormat = "yyyy-MM-dd"
        return formatter.string(from: date)
    }
}

struct ChoiceGroupsInteractionFixture: View {
    @State private var combo: String?
    @State private var multi: Set<String> = []
    @State private var selected: String?

    private let groups = [
        FoundationChoiceGroup("Fruit", choices: [
            FoundationChoice("Apple", value: "Apple"),
            FoundationChoice("Apricot", value: "Apricot", isEnabled: false),
        ]),
        FoundationChoiceGroup("Empty group", choices: []),
        FoundationChoiceGroup("Vegetables", choices: [
            FoundationChoice("Carrot", value: "Carrot"),
            FoundationChoice("Celery", value: "Celery"),
        ]),
    ]

    var body: some View {
        VStack(alignment: .leading, spacing: 14) {
            FoundationCombobox("Grouped Combobox", groups: groups, selection: $combo)
            FoundationCombobox("Grouped multi-select", groups: groups, selection: $multi)
            FoundationSelect("Grouped Select", groups: groups, selection: $selected)
            Text("Combobox: \(combo ?? "none")")
            Text("Select: \(selected ?? "none")")
            Spacer()
        }
        .frame(maxWidth: 320)
        .padding()
    }
}

struct ToastInteractionFixture: View {
    @StateObject private var toasts = FoundationToastState()
    @State private var undoCount = 0

    var body: some View {
        VStack(spacing: 16) {
            Button("Add four notifications") {
                toasts.show("First notice", durationMilliseconds: 0)
                toasts.show("Second notice", variant: .info, durationMilliseconds: 0)
                toasts.show("Third notice", variant: .warning, durationMilliseconds: 0)
                toasts.show(
                    "Saved", variant: .success, durationMilliseconds: 0,
                    action: FoundationToastAction("Undo") { undoCount += 1 }
                )
            }
            Button("Add timed notification") {
                toasts.show("Timed notice", durationMilliseconds: 1800)
            }
            Button("Clear notifications") { toasts.dismissAll() }
            Text("Undo count: \(undoCount)")
            Spacer()
        }
        .padding(.top, 36)
        .frame(maxWidth: .infinity, maxHeight: .infinity)
        .modifier(FoundationToaster(state: toasts))
    }
}

@available(iOS 17, macOS 14, *)
struct OverlayFocusInteractionFixture: View {
    private enum Trigger: Hashable { case dialog, sheet }

    @FocusState private var focused: Trigger?
    @State private var dialog = false
    @State private var sheet = false
    @State private var dialogDismissals = 0
    @State private var sheetDismissals = 0

    var body: some View {
        VStack(alignment: .leading, spacing: 20) {
            Button("Open dialog") { dialog = true }
                .focusable()
                .focused($focused, equals: .dialog)
                .modifier(FoundationDialog(isPresented: $dialog, title: "Audit dialog", onDismiss: {
                    dialogDismissals += 1
                    focused = .dialog
                }) {
                    Text("Dialog content")
                })
            Button("Open sheet") { sheet = true }
                .focusable()
                .focused($focused, equals: .sheet)
                .modifier(FoundationSheet(isPresented: $sheet, title: "Audit sheet", onDismiss: {
                    sheetDismissals += 1
                    focused = .sheet
                }) {
                    Text("Sheet content")
                })
            Text("Dialog dismissals: \(dialogDismissals)")
            Text("Sheet dismissals: \(sheetDismissals)")
            Text(focusDescription)
            Spacer()
        }
        .padding()
    }

    private var focusDescription: String {
        switch focused {
        case .dialog: "Trigger focus: Dialog"
        case .sheet: "Trigger focus: Sheet"
        case nil: "Trigger focus: None"
        }
    }
}

@available(iOS 17, macOS 14, *)
struct DrawerFocusInteractionFixture: View {
    @FocusState private var triggerFocused: Bool
    @State private var drawer = false
    @State private var dismissals = 0
    @State private var backgroundActivations = 0

    var body: some View {
        VStack(alignment: .leading, spacing: 16) {
            Button("Open drawer") { drawer = true }
                .focusable(interactions: .activate)
                .focused($triggerFocused)
                .onKeyPress(keys: ["d"], phases: .down) { _ in
                    drawer = true
                    return .handled
                }
            Button("Background action") { backgroundActivations += 1 }
            Text("Drawer dismissals: \(dismissals)")
            Text("Background activations: \(backgroundActivations)")
            Text(triggerFocused ? "Trigger focused" : "Trigger unfocused")
            Spacer()
        }
        .padding()
        .frame(maxWidth: .infinity, maxHeight: .infinity, alignment: .topLeading)
        .modifier(FoundationDrawer(isPresented: $drawer, title: "Audit drawer", onDismiss: {
            dismissals += 1
            triggerFocused = true
        }) {
            Text("Drawer content")
        })
    }
}

struct ComboboxInteractionFixture: View {
    @State private var single: String?
    @State private var multiple: Set<String> = ["general"]

    private let choices = [
        FoundationChoice("General", value: "general"),
        FoundationChoice("Restricted", value: "restricted", isEnabled: false),
        FoundationChoice("Advanced", value: "advanced"),
        FoundationChoice("Network", value: "network"),
    ]

    var body: some View {
        VStack(spacing: 24) {
            FoundationCombobox("Single section", choices: choices, selection: $single)
            FoundationCombobox("Multiple sections", choices: choices, selection: $multiple)
            Spacer()
        }
        .padding()
    }
}

struct TabsInteractionFixture: View {
    @State private var automatic = "Overview"
    @State private var manual = "Overview"
    var showAutomatic = true

    private var choices: [FoundationChoice<String>] {
        var choices = [
            FoundationChoice("Overview", value: "Overview"),
            FoundationChoice("Restricted", value: "Restricted", isEnabled: false),
            FoundationChoice("Activity", value: "Activity"),
            FoundationChoice("Settings", value: "Settings"),
        ]
        if ProcessInfo.processInfo.arguments.contains("--tabs-overflow") {
            choices += (1...8).map { FoundationChoice("Extra section \($0)", value: "Extra \($0)") }
        }
        return choices
    }

    var body: some View {
        VStack(alignment: .leading, spacing: 24) {
            if showAutomatic {
                FoundationTabs(choices, selection: $automatic) { value in
                    Text("Automatic panel: \(value)")
                }
            }
            FoundationTabs(choices, selection: $manual, activationMode: .manual) { value in
                Text("Manual panel: \(value)")
            }
            Spacer()
        }
        .disabled(ProcessInfo.processInfo.arguments.contains("--tabs-disabled"))
        .padding()
        .environment(
            \.layoutDirection,
            ProcessInfo.processInfo.arguments.contains("--tabs-rtl") ? .rightToLeft : .leftToRight
        )
    }
}
