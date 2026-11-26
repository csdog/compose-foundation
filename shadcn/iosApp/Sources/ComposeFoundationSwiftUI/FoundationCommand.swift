import SwiftUI

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

    @Environment(\.foundationTheme) private var theme
    @Environment(\.colorScheme) private var colorScheme
    @State private var query = ""
    private let sections: [Section]
    private let onAction: (Value) -> Void

    public init(sections: [Section], onAction: @escaping (Value) -> Void) {
        self.sections = sections
        self.onAction = onAction
    }

    public var body: some View {
        let palette = theme.palette(for: colorScheme)
        let matches = filteredSections
        let shape = RoundedRectangle(cornerRadius: theme.cornerRadius)

        VStack(alignment: .leading, spacing: 8) {
            HStack {
                Image(systemName: "magnifyingglass").accessibilityHidden(true)
                TextField("Search commands", text: $query, onCommit: activateFirstMatch)
                    .textFieldStyle(PlainTextFieldStyle())
            }
            .padding(8)
            .background(shape.fill(palette.secondary))
            if matches.isEmpty {
                Text("No matching commands").font(.subheadline).foregroundColor(palette.mutedForeground)
            } else {
                ScrollView {
                    LazyVStack(alignment: .leading, spacing: 3) {
                        ForEach(matches) { section in
                            if let title = section.title {
                                Text(title).font(.caption).foregroundColor(palette.mutedForeground)
                                    .accessibilityAddTraits(.isHeader)
                            }
                            ForEach(section.actions) { choice in
                                Button {
                                    onAction(choice.value)
                                } label: {
                                    Text(choice.title)
                                        .frame(maxWidth: .infinity, minHeight: 44, alignment: .leading)
                                        .contentShape(Rectangle())
                                }
                                .buttonStyle(PlainButtonStyle())
                                .disabled(!choice.isEnabled)
                            }
                        }
                    }
                }
                .frame(maxHeight: 280)
            }
        }
        .padding(10)
        .foregroundColor(palette.foreground)
        .background(shape.fill(palette.card))
        .overlay(shape.strokeBorder(palette.border).allowsHitTesting(false))
    }

    private var filteredSections: [Section] {
        sections.compactMap { section in
            let actions = section.actions.filter { query.isEmpty || $0.title.localizedStandardContains(query) }
            guard !actions.isEmpty else { return nil }
            return Section(id: section.id, title: section.title, actions: actions)
        }
    }

    private func activateFirstMatch() {
        if let match = filteredSections.lazy.flatMap(\.actions).first(where: \.isEnabled) {
            onAction(match.value)
        }
    }
}
