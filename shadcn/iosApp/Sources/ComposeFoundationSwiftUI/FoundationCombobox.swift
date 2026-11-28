import SwiftUI
#if os(iOS)
import UIKit
#endif

public struct FoundationCombobox<Value: Hashable>: View {
    @Environment(\.foundationTheme) private var theme
    @Environment(\.colorScheme) private var colorScheme
    @Environment(\.font) private var inheritedFont
    @Environment(\.foundationSurface) private var surface
    @Environment(\.isEnabled) private var isEnabled
    @ScaledMetric(relativeTo: .subheadline) private var novaFieldHeight: CGFloat = 32
    @ScaledMetric(relativeTo: .body) private var novaFontSize: CGFloat = 16
    @ScaledMetric(relativeTo: .subheadline) private var novaRowFontSize: CGFloat = 14
    @State private var query = ""
    @State private var isPresented = false
    @State private var highlighted: Value?

    private let title: String
    private let groups: [FoundationChoiceGroup<Value>]
    private let selection: Selection

    private enum Selection {
        case single(Binding<Value?>)
        case multiple(Binding<Set<Value>>)
    }

    public init(_ title: String, choices: [FoundationChoice<Value>], selection: Binding<Value?>) {
        self.init(title, groups: [FoundationChoiceGroup(choices: choices)], selection: selection)
    }

    public init(_ title: String, choices: [FoundationChoice<Value>], selection: Binding<Set<Value>>) {
        self.init(title, groups: [FoundationChoiceGroup(choices: choices)], selection: selection)
    }

    public init(_ title: String, groups: [FoundationChoiceGroup<Value>], selection: Binding<Value?>) {
        self.title = title
        self.groups = groups
        self.selection = .single(selection)
    }

    public init(_ title: String, groups: [FoundationChoiceGroup<Value>], selection: Binding<Set<Value>>) {
        self.title = title
        self.groups = groups
        self.selection = .multiple(selection)
    }

    public var body: some View {
        let palette = theme.palette(for: colorScheme)
        let shape = RoundedRectangle(cornerRadius: theme.cornerRadius)
        let fieldHeight = max(32, novaFieldHeight)
        let fill = isNova ? novaTriggerFill(palette) : palette.card

        VStack(alignment: .leading, spacing: 8) {
            Button {
                query = ""
                highlighted = nil
                isPresented = true
            } label: {
                HStack {
                    Text(selectedTitle ?? title)
                        .foregroundColor(isNova && selectedTitle == nil ? palette.mutedForeground : palette.foreground)
                    Spacer(minLength: 0)
                    Image(systemName: isNova ? "chevron.down" : "chevron.up.chevron.down")
                        .font(isNova ? .system(size: 10, weight: .medium) : .caption)
                        .foregroundColor(isNova ? palette.mutedForeground : palette.foreground)
                        .accessibilityHidden(true)
                }
                .font(isNova ? .system(size: novaFontSize) : .subheadline)
                .opacity(isNova && !isEnabled ? 0.5 : 1)
                .padding(.horizontal, isNova ? 11 : 12)
                .frame(minHeight: isNova ? max(44, fieldHeight) : 44)
                .background(shape.fill(fill).frame(height: isNova ? fieldHeight : nil))
                .overlay(shape.strokeBorder(palette.input).frame(height: isNova ? fieldHeight : nil).allowsHitTesting(false))
                .contentShape(Rectangle())
            }
            .buttonStyle(PlainButtonStyle())
            .accessibilityLabel(Text(title))
            .accessibilityValue(Text(selectionDescription))
            .popover(isPresented: $isPresented) { results(palette: palette) }

            if case let .multiple(values) = selection {
                let selectedChoices = choices.filter { values.wrappedValue.contains($0.value) }
                if !selectedChoices.isEmpty {
                    ScrollView(.horizontal, showsIndicators: false) {
                        HStack(spacing: 6) {
                            ForEach(selectedChoices) { choice in
                                Button {
                                    values.wrappedValue.remove(choice.value)
                                } label: {
                                    HStack(spacing: 6) {
                                        Text(choice.title)
                                        Image(systemName: "xmark").accessibilityHidden(true)
                                    }
                                    .font(.subheadline)
                                    .foregroundColor(palette.secondaryForeground)
                                    .padding(.horizontal, 10)
                                    .frame(minHeight: 44)
                                    .background(Capsule().fill(palette.secondary))
                                }
                                .buttonStyle(PlainButtonStyle())
                                .accessibilityLabel(Text("Remove \(choice.title)"))
                            }
                        }
                    }
                }
            }
        }
    }

    private var isNova: Bool { theme.preset == .nova }

    private func novaTriggerFill(_ palette: FoundationPalette) -> Color {
        if !isEnabled {
            return colorScheme == .dark ? palette.secondary : palette.input.opacity(0.55)
        }
        if colorScheme == .dark { return palette.novaDarkOutlineFill(in: surface) }
        return .clear
    }

    private var selectedTitle: String? {
        guard case .single = selection else { return nil }
        return choices.first { isSelected($0.value) }?.title
    }

    private var selectionDescription: String {
        choices.filter { isSelected($0.value) }.map(\.title).joined(separator: ", ")
    }

    private var choices: [FoundationChoice<Value>] {
        groups.flatMap(\.choices)
    }

    private var matchingChoices: [[FoundationChoice<Value>]] {
        groups.map { group in
            group.choices.filter { query.isEmpty || $0.title.localizedStandardContains(query) }
        }
    }

    private var enabledMatches: [FoundationChoice<Value>] {
        matchingChoices.flatMap { $0 }.filter(\.isEnabled)
    }

    private func isSelected(_ value: Value) -> Bool {
        switch selection {
        case let .single(binding): binding.wrappedValue == value
        case let .multiple(binding): binding.wrappedValue.contains(value)
        }
    }

    private func select(_ choice: FoundationChoice<Value>) {
        guard choice.isEnabled else { return }
        switch selection {
        case let .single(binding):
            binding.wrappedValue = choice.value
            isPresented = false
        case let .multiple(binding):
            if binding.wrappedValue.contains(choice.value) {
                binding.wrappedValue.remove(choice.value)
            } else {
                binding.wrappedValue.insert(choice.value)
            }
        }
    }

    private func moveHighlight(by offset: Int) -> Bool {
        let enabled = enabledMatches
        guard !enabled.isEmpty else { return false }
        if let index = enabled.firstIndex(where: { $0.value == highlighted }) {
            let next = (index + offset + enabled.count) % enabled.count
            highlighted = enabled[next].value
        } else {
            highlighted = (offset > 0 ? enabled.first : enabled.last)?.value
        }
        return true
    }

    private func submit() {
        let enabled = enabledMatches
        if let choice = enabled.first(where: { $0.value == highlighted }) ?? enabled.first {
            select(choice)
        }
    }

    @ViewBuilder
    private var searchField: some View {
        #if os(iOS)
        FoundationComboboxSearchField(
            query: $query,
            move: moveHighlight(by:),
            submit: submit,
            dismiss: { isPresented = false }
        )
        .fixedSize(horizontal: false, vertical: true)
        .frame(minHeight: 44)
        #else
        if #available(macOS 14, *) {
            FoundationComboboxSearchField(
                query: $query,
                move: moveHighlight(by:),
                submit: submit,
                dismiss: { isPresented = false }
            )
        } else {
            TextField("Search", text: $query, onCommit: submit)
        }
        #endif
    }

    private func results(palette: FoundationPalette) -> some View {
        let matches = matchingChoices
        let visibleGroups = matches.indices.filter { !matches[$0].isEmpty }
        return VStack(alignment: .leading, spacing: 8) {
            HStack {
                searchField.textFieldStyle(RoundedBorderTextFieldStyle())
                if case .multiple = selection {
                    Button("Done") { isPresented = false }
                        .frame(minWidth: 44, minHeight: 44)
                }
            }
            if visibleGroups.isEmpty {
                Text("No results").font(.subheadline).foregroundColor(palette.mutedForeground)
            } else {
                ScrollViewReader { scroll in
                    ScrollView {
                        VStack(alignment: .leading, spacing: 2) {
                            ForEach(visibleGroups, id: \.self) { index in
                                if isNova && index != visibleGroups.first {
                                    Rectangle().fill(palette.border).frame(height: 1)
                                        .padding(.vertical, 4).accessibilityHidden(true)
                                }
                                if let title = groups[index].title {
                                    Text(title)
                                        .font(isNova ? .caption : .caption.weight(.medium))
                                        .foregroundColor(palette.mutedForeground)
                                        .padding(.horizontal, 8)
                                        .padding(.top, 6)
                                        .padding(.bottom, isNova ? 4 : 0)
                                        .accessibilityAddTraits(.isHeader)
                                }
                                ForEach(matches[index]) { choice in
                                    resultRow(choice, palette: palette).id(choice.value)
                                }
                            }
                        }
                    }
                    .frame(maxHeight: 240)
                    .foundationOnChange(of: highlighted) { value in
                        if let value { scroll.scrollTo(value) }
                    }
                }
            }
        }
        .frame(minWidth: 200)
        .padding(12)
        .foregroundColor(palette.foreground)
        .background(palette.card)
        .foundationOnChange(of: query) { _ in highlighted = nil }
        .accessibilityAction(.escape) { isPresented = false }
    }

    private func resultRow(_ choice: FoundationChoice<Value>, palette: FoundationPalette) -> some View {
        #if os(macOS)
        let height: CGFloat = isNova ? 28 : 44
        #else
        let height: CGFloat = 44
        #endif
        return Button {
            select(choice)
        } label: {
            HStack {
                Text(choice.title)
                Spacer(minLength: 0)
                if isSelected(choice.value) {
                    Image(systemName: "checkmark").accessibilityHidden(true)
                }
            }
            .font(isNova ? .system(size: novaRowFontSize) : inheritedFont)
            .padding(.horizontal, 8)
            .frame(minHeight: height)
            .background(
                RoundedRectangle(cornerRadius: isNova ? max(0, theme.cornerRadius - 4) : theme.cornerRadius)
                    .fill(highlighted == choice.value ? palette.secondary : .clear)
            )
            .contentShape(Rectangle())
        }
        .buttonStyle(PlainButtonStyle())
        .disabled(!choice.isEnabled)
        .accessibilityAddTraits(isSelected(choice.value) ? .isSelected : [])
    }
}

#if os(iOS)
private struct FoundationComboboxSearchField: UIViewRepresentable {
    @Binding var query: String
    let move: (Int) -> Bool
    let submit: () -> Void
    let dismiss: () -> Void

    func makeUIView(context: Context) -> FoundationSearchTextField {
        let field = FoundationSearchTextField()
        field.activatesOnAttach = true
        field.placeholder = NSLocalizedString("Search", comment: "Combobox search prompt")
        field.borderStyle = .roundedRect
        field.font = .preferredFont(forTextStyle: .body)
        field.adjustsFontForContentSizeCategory = true
        field.autocapitalizationType = .none
        field.returnKeyType = .search
        field.delegate = field
        field.addTarget(field, action: #selector(FoundationSearchTextField.updateQuery), for: .editingChanged)
        return field
    }

    func updateUIView(_ field: FoundationSearchTextField, context: Context) {
        if field.text != query { field.text = query }
        field.writeQuery = { query = $0 }
        field.move = move
        field.submit = submit
        field.dismiss = dismiss
    }
}
#else
@available(macOS 14, *)
private struct FoundationComboboxSearchField: View {
    @Binding var query: String
    @Environment(\.accessibilityVoiceOverEnabled) private var voiceOverEnabled
    @FocusState private var isFocused: Bool
    let move: (Int) -> Bool
    let submit: () -> Void
    let dismiss: () -> Void

    var body: some View {
        TextField("Search", text: $query)
            .focused($isFocused)
            .onAppear { isFocused = !voiceOverEnabled }
            .onSubmit(submit)
            .onKeyPress(.upArrow) { move(-1) ? .handled : .ignored }
            .onKeyPress(.downArrow) { move(1) ? .handled : .ignored }
            .onKeyPress(.return) {
                submit()
                return .handled
            }
            .onKeyPress(.escape) {
                dismiss()
                return .handled
            }
    }

}
#endif
