import SwiftUI

public struct FoundationToggleGroup<Value: Hashable>: View {
    @Environment(\.foundationTheme) private var theme
    @Environment(\.colorScheme) private var colorScheme
    @Environment(\.isEnabled) private var isEnabled
    @Binding private var selection: Set<Value>

    private let choices: [FoundationChoice<Value>]
    private let allowsMultiple: Bool
    private let allowsEmpty: Bool
    private let variant: FoundationToggleVariant?
    private let size: FoundationToggleSize
    private let label: ((FoundationChoice<Value>) -> AnyView)?

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
        self.variant = nil
        self.size = .regular
        self.label = nil
    }

    public init<Label: View>(
        _ choices: [FoundationChoice<Value>],
        selection: Binding<Set<Value>>,
        variant: FoundationToggleVariant,
        size: FoundationToggleSize = .regular,
        allowsMultiple: Bool = false,
        allowsEmpty: Bool = true,
        @ViewBuilder label: @escaping (FoundationChoice<Value>) -> Label
    ) {
        self.choices = choices
        self._selection = selection
        self.allowsMultiple = allowsMultiple
        self.allowsEmpty = allowsEmpty
        self.variant = variant
        self.size = size
        self.label = { AnyView(label($0)) }
    }

    public var body: some View {
        let palette = theme.palette(for: colorScheme)
        let shape = RoundedRectangle(cornerRadius: theme.cornerRadius)
        let appearance = variant ?? (theme.preset == .nova ? .plain : nil)

        ScrollView(.horizontal, showsIndicators: false) {
            HStack(spacing: 2) {
                ForEach(choices) { choice in
                    if let appearance {
                        let pressed = Binding(
                            get: { selection.contains(choice.value) },
                            set: { if $0 != selection.contains(choice.value) { toggle(choice.value) } }
                        )
                        FoundationToggle(isOn: pressed, variant: appearance, size: size) {
                            if let label { label(choice) } else { Text(choice.title) }
                        }
                        .accessibilityLabel(Text(choice.title))
                        .disabled(!choice.isEnabled)
                    } else {
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
            }
            .padding(appearance == nil ? 3 : 0)
            .background(shape.fill(appearance == nil ? palette.secondary : .clear))
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
