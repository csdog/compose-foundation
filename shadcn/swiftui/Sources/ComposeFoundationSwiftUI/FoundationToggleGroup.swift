import SwiftUI

public struct FoundationToggleGroup<Value: Hashable>: View {
    @Environment(\.foundationTheme) private var theme
    @Environment(\.colorScheme) private var colorScheme
    @Environment(\.isEnabled) private var isEnabled
    @Binding private var selection: Set<Value>

    private let choices: [FoundationChoice<Value>]
    private let allowsMultiple: Bool
    private let allowsEmpty: Bool

    public init(
        _ choices: [FoundationChoice<Value>],
        selection: Binding<Set<Value>>,
        allowsMultiple: Bool = false,
        allowsEmpty: Bool = true
    ) {
        self.choices = choices
        self._selection = selection
        self.allowsMultiple = allowsMultiple
        self.allowsEmpty = allowsEmpty
    }

    public var body: some View {
        let palette = theme.palette(for: colorScheme)
        let shape = RoundedRectangle(cornerRadius: theme.cornerRadius)

        ScrollView(.horizontal, showsIndicators: false) {
            HStack(spacing: 2) {
                ForEach(choices) { choice in
                    let selected = selection.contains(choice.value)
                    Button {
                        toggle(choice.value)
                    } label: {
                        Text(choice.title)
                            .font(.subheadline.weight(.medium))
                            .foregroundColor(selected ? palette.primaryForeground : palette.foreground)
                            .padding(.horizontal, 10)
                            .frame(minHeight: 44)
                            .background(shape.fill(selected ? palette.primary : .clear))
                            .contentShape(Rectangle())
                    }
                    .buttonStyle(PlainButtonStyle())
                    .disabled(!choice.isEnabled)
                    .opacity(isEnabled && choice.isEnabled ? 1 : 0.5)
                    .accessibilityAddTraits(selected ? .isSelected : [])
                }
            }
            .padding(3)
            .background(shape.fill(palette.secondary))
        }
    }

    private func toggle(_ value: Value) {
        if selection.contains(value) {
            if allowsEmpty || selection.count > 1 { selection.remove(value) }
        } else if allowsMultiple {
            selection.insert(value)
        } else {
            selection = [value]
        }
    }
}
