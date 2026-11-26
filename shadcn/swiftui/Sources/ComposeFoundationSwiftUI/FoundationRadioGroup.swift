import SwiftUI

public struct FoundationRadioGroup<Value: Hashable>: View {
    @Environment(\.foundationTheme) private var theme
    @Environment(\.colorScheme) private var colorScheme
    @Environment(\.isEnabled) private var isEnabled
    @Binding private var selection: Value?

    private let choices: [FoundationChoice<Value>]

    public init(_ choices: [FoundationChoice<Value>], selection: Binding<Value?>) {
        self.choices = choices
        self._selection = selection
    }

    public var body: some View {
        let palette = theme.palette(for: colorScheme)

        VStack(alignment: .leading, spacing: 2) {
            ForEach(choices) { choice in
                let selected = choice.value == selection
                Button {
                    selection = choice.value
                } label: {
                    HStack(spacing: 8) {
                        Circle().strokeBorder(selected ? palette.primary : palette.input)
                            .overlay(Circle().fill(selected ? palette.primary : Color.clear).padding(4))
                            .frame(width: 16, height: 16)
                            .accessibilityHidden(true)
                        Text(choice.title).font(.subheadline)
                    }
                    .foregroundColor(palette.foreground)
                    .frame(minHeight: 44)
                    .contentShape(Rectangle())
                }
                .buttonStyle(PlainButtonStyle())
                .disabled(!choice.isEnabled)
                .opacity(isEnabled && choice.isEnabled ? 1 : 0.5)
                .accessibilityAddTraits(selected ? .isSelected : [])
            }
        }
        .accessibilityElement(children: .contain)
    }
}
