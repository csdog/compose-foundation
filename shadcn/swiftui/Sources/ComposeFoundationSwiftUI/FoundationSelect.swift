import SwiftUI

public struct FoundationSelect<Value: Hashable>: View {
    @Environment(\.foundationTheme) private var theme
    @Environment(\.colorScheme) private var colorScheme
    @Binding private var selection: Value?

    private let title: String
    private let groups: [FoundationChoiceGroup<Value>]

    public init(_ title: String, choices: [FoundationChoice<Value>], selection: Binding<Value?>) {
        self.init(title, groups: [FoundationChoiceGroup(choices: choices)], selection: selection)
    }

    public init(_ title: String, groups: [FoundationChoiceGroup<Value>], selection: Binding<Value?>) {
        self.title = title
        self.groups = groups.filter { !$0.choices.isEmpty }
        self._selection = selection
    }

    public var body: some View {
        let palette = theme.palette(for: colorScheme)
        let shape = RoundedRectangle(cornerRadius: theme.cornerRadius)
        let chosen = groups.lazy.flatMap(\.choices).first { $0.value == selection }

        Menu {
            ForEach(groups.indices, id: \.self) { index in
                let group = groups[index]
                if let title = group.title {
                    Section(header: Text(title)) {
                        menuChoices(group.choices)
                    }
                } else {
                    menuChoices(group.choices)
                }
            }
        } label: {
            HStack(spacing: 8) {
                Text(chosen?.title ?? title)
                    .foregroundColor(chosen == nil ? palette.mutedForeground : palette.foreground)
                Spacer(minLength: 0)
                Image(systemName: "chevron.down").font(.caption).foregroundColor(palette.mutedForeground)
            }
            .font(.subheadline)
            .padding(.horizontal, 12)
            .frame(minHeight: 40)
            .background(shape.fill(palette.card))
            .overlay(shape.strokeBorder(palette.input).allowsHitTesting(false))
        }
        .accessibilityLabel(Text(title))
        .accessibilityValue(Text(chosen?.title ?? ""))
        .disabled(groups.isEmpty)
    }

    private func menuChoices(_ choices: [FoundationChoice<Value>]) -> some View {
        ForEach(choices) { choice in
            Button {
                selection = choice.value
            } label: {
                if choice.value == selection {
                    Label(choice.title, systemImage: "checkmark")
                } else {
                    Text(choice.title)
                }
            }
            .disabled(!choice.isEnabled)
        }
    }
}
