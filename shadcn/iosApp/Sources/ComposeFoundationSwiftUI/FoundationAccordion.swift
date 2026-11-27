import SwiftUI

public struct FoundationAccordion<Value: Hashable, Panel: View>: View {
    @Environment(\.foundationTheme) private var theme
    @Environment(\.colorScheme) private var colorScheme
    @Environment(\.accessibilityReduceMotion) private var reduceMotion
    @ScaledMetric(relativeTo: .subheadline) private var fontSize = 14.0
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
                if theme.preset == .nova {
                    novaItem(choice, palette: palette)
                } else {
                    DisclosureGroup(isExpanded: expansion(for: choice.value)) {
                        panel(choice.value).padding(.vertical, 8)
                    } label: {
                        Text(choice.title).font(.subheadline.weight(.medium))
                    }
                    .padding(.vertical, 9)
                    .disabled(!choice.isEnabled)
                }

                if choice.id != choices.last?.id {
                    Rectangle().fill(palette.border).frame(height: 1).accessibilityHidden(true)
                }
            }
        }
        .foregroundColor(palette.foreground)
        .accentColor(palette.primary)
    }

    private func novaItem(_ choice: FoundationChoice<Value>, palette: FoundationPalette) -> some View {
        let expansion = expansion(for: choice.value)
        let isOpen = expansion.wrappedValue

        return VStack(alignment: .leading, spacing: 0) {
            Button {
                withAnimation(reduceMotion ? nil : .easeInOut(duration: 0.2)) {
                    expansion.wrappedValue.toggle()
                }
            } label: {
                HStack(spacing: 12) {
                    Text(choice.title)
                        .font(.system(size: fontSize, weight: .medium))
                        .multilineTextAlignment(.leading)
                        .frame(maxWidth: .infinity, alignment: .leading)
                    Image(systemName: isOpen ? "chevron.up" : "chevron.down")
                        .font(.system(size: 11, weight: .medium))
                        .foregroundColor(palette.mutedForeground)
                        .frame(width: 16, height: 16)
                        .accessibilityHidden(true)
                }
                .padding(.horizontal, 1)
                .padding(.vertical, 10)
                .frame(minHeight: 44)
                .contentShape(Rectangle())
            }
            .buttonStyle(PlainButtonStyle())
            .accessibilityAddTraits(.isHeader)
            .accessibilityValue(isOpen ? Text("Expanded") : Text("Collapsed"))

            if isOpen {
                panel(choice.value)
                    .font(.system(size: fontSize))
                    .padding(.horizontal, 1)
                    .padding(.bottom, 10)
                    .frame(maxWidth: .infinity, alignment: .leading)
                    .transition(.opacity)
            }
        }
        .disabled(!choice.isEnabled)
    }

    private func expansion(for value: Value) -> Binding<Bool> {
        Binding(
            get: { expanded.contains(value) },
            set: { isExpanded in
                var next = expanded
                if isExpanded {
                    next = allowsMultiple ? next.union([value]) : [value]
                } else {
                    next.remove(value)
                }
                expanded = next
            }
        )
    }
}
