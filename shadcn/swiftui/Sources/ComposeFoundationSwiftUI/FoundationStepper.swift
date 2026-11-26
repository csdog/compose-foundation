import SwiftUI

public struct FoundationStepper<Value: Hashable>: View {
    @Environment(\.foundationTheme) private var theme
    @Environment(\.colorScheme) private var colorScheme

    private let steps: [FoundationChoice<Value>]
    private let current: Value
    private let onSelect: ((Value) -> Void)?

    public init(_ steps: [FoundationChoice<Value>], current: Value, onSelect: ((Value) -> Void)? = nil) {
        self.steps = steps
        self.current = current
        self.onSelect = onSelect
    }

    public var body: some View {
        let palette = theme.palette(for: colorScheme)
        let position = steps.firstIndex { $0.value == current }

        VStack(alignment: .leading, spacing: 5) {
            ForEach(steps.indices, id: \.self) { index in
                let choice = steps[index]
                let completed = position.map { index < $0 } ?? false
                let label = step(choice, number: index + 1, completed: completed, palette: palette)

                if let onSelect {
                    Button { onSelect(choice.value) } label: { label }
                        .buttonStyle(PlainButtonStyle())
                        .disabled(!choice.isEnabled)
                } else {
                    label
                }
            }
        }
        .accessibilityElement(children: .contain)
    }

    private func step(_ choice: FoundationChoice<Value>, number: Int, completed: Bool, palette: FoundationPalette) -> some View {
        let selected = choice.value == current

        return HStack(spacing: 8) {
            Group {
                if completed { Image(systemName: "checkmark") } else { Text(String(number)) }
            }
            .font(.caption.weight(.semibold))
            .frame(width: 24, height: 24)
            .foregroundColor(selected ? palette.primaryForeground : palette.foreground)
            .background(Circle().fill(selected ? palette.primary : palette.secondary))
            .accessibilityHidden(true)
            Text(choice.title).font(.subheadline.weight(selected ? .semibold : .regular))
        }
        .foregroundColor(palette.foreground)
        .frame(minHeight: onSelect == nil ? 26 : 44)
        .accessibilityValue(Text(completed ? "Completed" : ""))
        .accessibilityAddTraits(selected ? .isSelected : [])
    }
}
