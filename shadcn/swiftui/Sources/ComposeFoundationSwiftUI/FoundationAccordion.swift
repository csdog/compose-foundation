import SwiftUI

public struct FoundationAccordion<Value: Hashable, Panel: View>: View {
    @Environment(\.foundationTheme) private var theme
    @Environment(\.colorScheme) private var colorScheme
    @Binding private var expanded: Set<Value>

    private let choices: [FoundationChoice<Value>]
    private let allowsMultiple: Bool
    private let panel: (Value) -> Panel

    public init(
        _ choices: [FoundationChoice<Value>],
        expanded: Binding<Set<Value>>,
        allowsMultiple: Bool = false,
        @ViewBuilder panel: @escaping (Value) -> Panel
    ) {
        self.choices = choices
        self._expanded = expanded
        self.allowsMultiple = allowsMultiple
        self.panel = panel
    }

    public var body: some View {
        let palette = theme.palette(for: colorScheme)

        VStack(alignment: .leading, spacing: 0) {
            ForEach(choices) { choice in
                DisclosureGroup(isExpanded: expansion(for: choice.value)) {
                    panel(choice.value).padding(.vertical, 8)
                } label: {
                    Text(choice.title).font(.subheadline.weight(.medium))
                }
                .padding(.vertical, 9)
                .disabled(!choice.isEnabled)

                if choice.id != choices.last?.id {
                    Rectangle().fill(palette.border).frame(height: 1).accessibilityHidden(true)
                }
            }
        }
        .foregroundColor(palette.foreground)
        .accentColor(palette.primary)
    }

    private func expansion(for value: Value) -> Binding<Bool> {
        Binding(
            get: { expanded.contains(value) },
            set: { isExpanded in
                if isExpanded {
                    if !allowsMultiple { expanded.removeAll() }
                    expanded.insert(value)
                } else {
                    expanded.remove(value)
                }
            }
        )
    }
}
