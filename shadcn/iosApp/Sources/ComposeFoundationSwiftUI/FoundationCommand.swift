import SwiftUI
#if os(iOS)
import UIKit
#endif

public struct FoundationCommand<Value: Hashable>: View {
    public struct Section: Identifiable {
        public let id: String
        public let title: String?
        public let actions: [FoundationChoice<Value>]

        public init(id: String, title: String? = nil, actions: [FoundationChoice<Value>]) {
            self.id = id
            self.title = title
            self.actions = actions
        }
    }

    public struct ActionAppearance {
        public let systemImage: String?
        public let shortcutLabel: String?
        public let shortcut: KeyboardShortcut?

        public init(systemImage: String? = nil, shortcutLabel: String? = nil) {
            self.systemImage = systemImage
            self.shortcutLabel = shortcutLabel
            self.shortcut = nil
        }

        public init(systemImage: String? = nil, shortcutLabel: String? = nil, shortcut: KeyboardShortcut) {
            self.systemImage = systemImage
            self.shortcutLabel = shortcutLabel
            self.shortcut = shortcut
        }
    }

    private struct RowID: Hashable {
        let section: String
        let value: Value
    }

    @Environment(\.foundationTheme) private var theme
    @Environment(\.colorScheme) private var colorScheme
    @Environment(\.isEnabled) private var isEnabled
    @Environment(\.accessibilityReduceMotion) private var reduceMotion
    @ScaledMetric(relativeTo: .subheadline) private var novaFontSize: CGFloat = 14
    @ScaledMetric(relativeTo: .subheadline) private var novaRowHeight: CGFloat = 32
    @ScaledMetric(relativeTo: .caption) private var novaHeadingHeight: CGFloat = 28
    @State private var localQuery = ""
    @State private var activeRow: RowID?

    private let sections: [Section]
    private let boundQuery: Binding<String>?
    private let searchPrompt: String
    private let emptyMessage: String
    private let actionAppearance: [Value: ActionAppearance]
    private let onAction: (Value) -> Void
    private var focusSearchOnOpen = false
    private var dismiss: (() -> Void)?
    private var loadingMessage: String?
    private var footerContent: AnyView?

    public init(sections: [Section], onAction: @escaping (Value) -> Void) {
        self.init(sections: sections, query: nil, onAction: onAction)
    }

    public init(
        sections: [Section],
        query: Binding<String>?,
        searchPrompt: String = "Search commands",
        emptyMessage: String = "No matching commands",
        actionAppearance: [Value: ActionAppearance] = [:],
        onAction: @escaping (Value) -> Void
    ) {
        self.sections = sections
        self.boundQuery = query
        self.searchPrompt = searchPrompt
        self.emptyMessage = emptyMessage
        self.actionAppearance = actionAppearance
        self.onAction = onAction
    }

    func presentedInDialog(focusSearch: Bool, dismiss: @escaping () -> Void) -> Self {
        var command = self
        command.focusSearchOnOpen = focusSearch
        command.dismiss = dismiss
        return command
    }

    /// Shows loading alongside existing matches and suppresses the empty state until loading ends.
    public func loading(_ message: String?) -> Self {
        var command = self
        command.loadingMessage = message
        return command
    }

    /// Adds a footer below the results, outside the result list's scrolling region.
    public func footer<Footer: View>(@ViewBuilder _ content: () -> Footer) -> Self {
        settingFooter(AnyView(content()))
    }

    func settingFooter(_ content: AnyView?) -> Self {
        var command = self
        command.footerContent = content
        return command
    }

    public var body: some View {
        let palette = theme.palette(for: colorScheme)
        let matches = filteredSections
        let enabled = enabledRows(in: matches)
        let highlighted = activeRow.flatMap { enabled.contains($0) ? $0 : nil }
            ?? (search.wrappedValue.isEmpty ? nil : enabled.first)
        let shape = RoundedRectangle(cornerRadius: nova ? theme.cardCornerRadius : theme.cornerRadius)

        VStack(alignment: .leading, spacing: nova ? 0 : 8) {
            searchField(palette, enabled: enabled, highlighted: highlighted)
            if let loadingMessage {
                loadingStatus(loadingMessage, palette: palette)
            }
            if matches.isEmpty {
                if loadingMessage == nil {
                    Text(emptyMessage)
                        .font(nova ? .system(size: novaFontSize) : .subheadline)
                        .foregroundColor(palette.mutedForeground)
                        .frame(maxWidth: nova ? .infinity : nil)
                        .padding(.vertical, nova ? 24 : 0)
                }
            } else {
                results(matches, highlighted: highlighted, palette: palette)
            }
            if let footerContent {
                VStack(spacing: 0) {
                    Rectangle().fill(palette.border).frame(height: 1).accessibilityHidden(true)
                    footerContent.frame(maxWidth: .infinity, alignment: .leading).padding(8)
                }
                .background(nova ? palette.novaCardFooterFill(for: colorScheme) : palette.secondary)
                .environment(\.foundationSurface, .cardFooter)
            }
        }
        .padding(nova ? 5 : 10)
        .foregroundColor(palette.foreground)
        .background(shape.fill(dismiss == nil ? palette.card : .clear))
        .overlay(shape.strokeBorder(dismiss == nil ? palette.border : .clear).allowsHitTesting(false))
        .onChange(of: search.wrappedValue) { _ in activeRow = nil }
    }

    private var nova: Bool { theme.preset == .nova }
    private var search: Binding<String> { boundQuery ?? $localQuery }

    private func loadingStatus(_ message: String, palette: FoundationPalette) -> some View {
        HStack(spacing: 8) {
            Group {
                if reduceMotion {
                    Image(systemName: "hourglass")
                } else {
                    ProgressView().scaleEffect(0.75).accentColor(palette.mutedForeground)
                }
            }
            .frame(width: 20, height: 20)
            .accessibilityHidden(true)
            Text(message)
        }
        .font(nova ? .system(size: novaFontSize) : .subheadline)
        .foregroundColor(palette.mutedForeground)
        .frame(maxWidth: .infinity)
        .padding(.vertical, 16)
    }

    private var rowHeight: CGFloat {
        #if os(iOS)
        return max(44, novaRowHeight)
        #else
        return max(32, novaRowHeight)
        #endif
    }

    private func searchField(_ palette: FoundationPalette, enabled: [RowID], highlighted: RowID?) -> some View {
        let submit = {
            guard isEnabled, let target = highlighted ?? enabled.first else { return }
            onAction(target.value)
        }
        let shape = RoundedRectangle(cornerRadius: nova ? theme.popoverCornerRadius : theme.cornerRadius)
        let fill = colorScheme == .dark ? palette.novaDarkOutlineFill(in: .card) : palette.secondary.opacity(0.8)
        return HStack(spacing: nova ? 7 : 8) {
            Image(systemName: "magnifyingglass")
                .foregroundColor(nova ? palette.mutedForeground.opacity(0.65) : palette.foreground)
                .accessibilityHidden(true)
            keyboardSearch(palette, enabled: enabled, highlighted: highlighted, submit: submit)
                .textFieldStyle(PlainTextFieldStyle())
        }
        .font(nova ? .system(size: novaFontSize) : nil)
        .padding(.horizontal, 8)
        .padding(.vertical, nova ? 0 : 8)
        .frame(minHeight: nova ? max(32, novaRowHeight) : nil)
        .background(shape.fill(nova ? fill : palette.secondary))
        .overlay(shape.strokeBorder(nova ? palette.input.opacity(0.3) : .clear).allowsHitTesting(false))
        .padding(.horizontal, nova ? 4 : 0)
        .padding(.top, nova ? 4 : 0)
    }

    @ViewBuilder
    private func keyboardSearch(
        _ palette: FoundationPalette, enabled: [RowID], highlighted: RowID?, submit: @escaping () -> Void
    ) -> some View {
        if #available(iOS 17, macOS 14, *) {
            let move: (Int) -> Bool = { offset in
                guard isEnabled, !enabled.isEmpty else { return false }
                if let highlighted, let index = enabled.firstIndex(of: highlighted) {
                    activeRow = enabled[(index + offset + enabled.count) % enabled.count]
                } else {
                    activeRow = offset > 0 ? enabled.first : enabled.last
                }
                return true
            }
            #if os(iOS)
            FoundationCommandSearch(prompt: searchPrompt, query: search, fontSize: nova ? novaFontSize : nil,
                                    foreground: palette.foreground, focusSearchOnOpen: focusSearchOnOpen,
                                    move: move, submit: submit, dismiss: dismiss)
            #else
            FoundationCommandSearch(prompt: searchPrompt, query: search, focusSearchOnOpen: focusSearchOnOpen,
                                    move: move, submit: submit, dismiss: dismiss)
            #endif
        } else {
            TextField(searchPrompt, text: search, onCommit: submit)
        }
    }

    private func results(_ matches: [Section], highlighted: RowID?, palette: FoundationPalette) -> some View {
        ScrollViewReader { scroll in
            ScrollView(showsIndicators: !nova) {
                if actionAppearance.values.contains(where: { $0.shortcut != nil }) {
                    // Keep native key equivalents registered for matching rows outside the viewport.
                    VStack(alignment: .leading, spacing: nova ? 0 : 3) {
                        resultSections(matches, highlighted: highlighted, palette: palette)
                    }
                } else {
                    LazyVStack(alignment: .leading, spacing: nova ? 0 : 3) {
                        resultSections(matches, highlighted: highlighted, palette: palette)
                    }
                }
            }
            .frame(height: nova ? min(288, resultsHeight(matches)) : nil)
            .frame(maxHeight: nova ? nil : 280)
            .onChange(of: highlighted) { target in
                if let target { scroll.scrollTo(target) }
            }
        }
    }

    @ViewBuilder
    private func resultSections(_ matches: [Section], highlighted: RowID?, palette: FoundationPalette) -> some View {
        ForEach(matches) { section in
            if nova && section.id != matches.first?.id {
                Rectangle().fill(palette.border).frame(height: 1).accessibilityHidden(true)
            }
            VStack(alignment: .leading, spacing: nova ? 0 : 3) {
                if let title = section.title {
                    Text(title)
                        .font(.caption.weight(nova ? .medium : .regular))
                        .foregroundColor(palette.mutedForeground)
                        .padding(.horizontal, nova ? 8 : 0)
                        .frame(minHeight: nova ? novaHeadingHeight : nil)
                        .accessibilityAddTraits(.isHeader)
                }
                ForEach(section.actions) { choice in
                    let id = RowID(section: section.id, value: choice.value)
                    row(choice, highlighted: highlighted == id, palette: palette).id(id)
                }
            }
            .padding(nova ? 4 : 0)
        }
    }

    private func row(_ choice: FoundationChoice<Value>, highlighted: Bool, palette: FoundationPalette) -> some View {
        let appearance = actionAppearance[choice.value]
        let button = Button {
            onAction(choice.value)
        } label: {
            HStack(spacing: 8) {
                if let image = appearance?.systemImage {
                    Image(systemName: image).font(.system(size: 14))
                        .frame(width: 16)
                        .accessibilityHidden(true)
                }
                Text(choice.title).lineLimit(nova ? 1 : nil)
                Spacer(minLength: 0)
                if let shortcut = appearance?.shortcutLabel {
                    Text(shortcut).font(.caption)
                        .foregroundColor(highlighted ? palette.foreground : palette.mutedForeground)
                        .accessibilityHidden(true)
                }
            }
            .font(nova ? .system(size: novaFontSize) : nil)
            .padding(.horizontal, nova ? 8 : 0)
            .frame(minHeight: nova ? rowHeight : 44)
            .background(
                RoundedRectangle(cornerRadius: max(0, theme.cornerRadius - 4))
                    .fill(highlighted ? palette.secondary : .clear)
                    .frame(height: nova ? max(32, novaRowHeight) : nil)
            )
            .contentShape(Rectangle())
        }
        .buttonStyle(PlainButtonStyle())
        .disabled(!choice.isEnabled)
        .opacity(nova && !choice.isEnabled ? 0.5 : 1)
        .accessibilityAddTraits(highlighted ? .isSelected : [])
        return withShortcut(isEnabled && choice.isEnabled ? appearance?.shortcut : nil, on: button)
    }

    @ViewBuilder
    private func withShortcut<Row: View>(_ shortcut: KeyboardShortcut?, on row: Row) -> some View {
        if let shortcut {
            if #available(iOS 15.4, macOS 12.3, *) {
                row.keyboardShortcut(shortcut)
            } else {
                row.keyboardShortcut(shortcut.key, modifiers: shortcut.modifiers)
            }
        } else {
            row
        }
    }

    private func resultsHeight(_ matches: [Section]) -> CGFloat {
        matches.reduce(CGFloat(max(0, matches.count - 1))) { height, section in
            height + rowHeight * CGFloat(section.actions.count) + (section.title == nil ? 0 : novaHeadingHeight) + 8
        }
    }

    private var filteredSections: [Section] {
        let query = search.wrappedValue
        return sections.compactMap { section in
            let actions = section.actions.filter { query.isEmpty || $0.title.localizedStandardContains(query) }
            guard !actions.isEmpty else { return nil }
            return Section(id: section.id, title: section.title, actions: actions)
        }
    }

    private func enabledRows(in sections: [Section]) -> [RowID] {
        sections.flatMap { section in
            section.actions.filter(\.isEnabled).map { RowID(section: section.id, value: $0.value) }
        }
    }
}

#if os(iOS)
@available(iOS 17, *)
private struct FoundationCommandSearch: UIViewRepresentable {
    @Environment(\.isEnabled) private var isEnabled
    let prompt: String
    @Binding var query: String
    let fontSize: CGFloat?
    let foreground: Color
    let focusSearchOnOpen: Bool
    let move: (Int) -> Bool
    let submit: () -> Void
    let dismiss: (() -> Void)?

    func makeUIView(context: Context) -> FoundationSearchTextField {
        let field = FoundationSearchTextField()
        field.activatesOnAttach = focusSearchOnOpen
        field.borderStyle = .none
        field.autocapitalizationType = .none
        field.returnKeyType = .search
        field.delegate = field
        field.addTarget(field, action: #selector(FoundationSearchTextField.updateQuery), for: .editingChanged)
        return field
    }

    func updateUIView(_ field: FoundationSearchTextField, context: Context) {
        if field.text != query { field.text = query }
        field.font = fontSize.map { UIFont.systemFont(ofSize: $0) } ?? .preferredFont(forTextStyle: .body)
        field.adjustsFontForContentSizeCategory = fontSize == nil
        field.textColor = UIColor(foreground)
        field.placeholder = prompt
        field.accessibilityLabel = prompt
        field.isEnabled = isEnabled
        field.writeQuery = { query = $0 }
        field.move = move
        field.submit = submit
        field.dismiss = dismiss
    }

    func sizeThatFits(_ proposal: ProposedViewSize, uiView: FoundationSearchTextField, context: Context) -> CGSize? {
        guard let width = proposal.width else { return nil }
        return CGSize(width: width, height: uiView.intrinsicContentSize.height)
    }
}
#else
@available(macOS 14, *)
private struct FoundationCommandSearch: View {
    @Environment(\.accessibilityVoiceOverEnabled) private var voiceOverEnabled
    @FocusState private var focused: Bool
    let prompt: String
    @Binding var query: String
    let focusSearchOnOpen: Bool
    let move: (Int) -> Bool
    let submit: () -> Void
    let dismiss: (() -> Void)?

    var body: some View {
        TextField(prompt, text: $query)
            .focused($focused)
            .onSubmit(submit)
            .onKeyPress(keys: [.upArrow, .downArrow, .return, .escape], phases: [.down, .repeat]) { press in
                guard press.modifiers.intersection([.control, .command, .option, .shift]).isEmpty else { return .ignored }
                if press.key == .escape {
                    guard let dismiss else { return .ignored }
                    if press.phase == .down { dismiss() }
                    return .handled
                }
                if press.key == .return {
                    if press.phase == .down { submit() }
                    return .handled
                }
                return move(press.key == .downArrow ? 1 : -1) ? .handled : .ignored
            }
            .task {
                guard focusSearchOnOpen, !voiceOverEnabled else { return }
                await Task.yield()
                if !Task.isCancelled { focused = true }
            }
    }
}
#endif
