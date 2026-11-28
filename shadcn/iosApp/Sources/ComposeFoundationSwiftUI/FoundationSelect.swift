import SwiftUI

public struct FoundationSelect<Value: Hashable>: View {
    @Environment(\.foundationTheme) private var theme
    @Environment(\.colorScheme) private var colorScheme
    @Environment(\.foundationSurface) private var surface
    @ScaledMetric(relativeTo: .subheadline) private var novaFieldHeight: CGFloat = 32
    @ScaledMetric(relativeTo: .subheadline) private var novaFontSize: CGFloat = 14
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
        let nova = theme.preset == .nova
        let fieldHeight = max(32, novaFieldHeight)
        let fill = nova ? (colorScheme == .dark ? palette.novaDarkOutlineFill(in: surface) : .clear) : palette.card

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
            HStack(spacing: nova ? 6 : 8) {
                Text(chosen?.title ?? title)
                    .foregroundColor(chosen == nil ? palette.mutedForeground : palette.foreground)
                Spacer(minLength: 0)
                Image(systemName: "chevron.down")
                    .font(nova ? .system(size: 10, weight: .medium) : .caption)
                    .foregroundColor(palette.mutedForeground)
                    .accessibilityHidden(true)
            }
            .font(nova ? .system(size: novaFontSize) : .subheadline)
            .padding(.leading, nova ? 11 : 12)
            .padding(.trailing, 12)
            .frame(minHeight: nova ? max(44, fieldHeight) : 40)
            .background(shape.fill(fill).frame(height: nova ? fieldHeight : nil))
            .overlay(shape.strokeBorder(palette.input).frame(height: nova ? fieldHeight : nil).allowsHitTesting(false))
            .contentShape(Rectangle())
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
            .accessibilityAddTraits(choice.value == selection ? .isSelected : [])
        }
    }
}
