import SwiftUI

public struct FoundationRadioGroup<Value: Hashable>: View {
    @Environment(\.foundationTheme) private var theme
    @Environment(\.colorScheme) private var colorScheme
    @Environment(\.isEnabled) private var isEnabled
    @Environment(\.font) private var font
    @ScaledMetric(relativeTo: .subheadline) private var labelSize: CGFloat = 14
    @Binding private var selection: Value?

    private let choices: [FoundationChoice<Value>]

    public init(_ choices: [FoundationChoice<Value>], selection: Binding<Value?>) {
        self.choices = choices
        self._selection = selection
    }

    public var body: some View {
        let palette = theme.palette(for: colorScheme)
        let nova = theme.preset == .nova
        let labelFont = nova ? (font ?? .system(size: labelSize, weight: .medium)) : .subheadline

        VStack(alignment: .leading, spacing: 2) {
            ForEach(choices) { choice in
                let selected = choice.value == selection
                Button {
                    selection = choice.value
                } label: {
                    HStack(spacing: nova ? 12 : 8) {
                        indicator(selected: selected, nova: nova, palette: palette)
                        Text(choice.title).font(labelFont)
                    }
                    .foregroundColor(palette.foreground)
                    .frame(minWidth: nova ? 44 : nil, minHeight: 44, alignment: .leading)
                    .contentShape(Rectangle())
                }
                .buttonStyle(PlainButtonStyle())
                .disabled(!choice.isEnabled)
                .opacity(nova || (isEnabled && choice.isEnabled) ? 1 : 0.5)
                .accessibilityAddTraits(selected ? .isSelected : [])
            }
        }
        .accessibilityElement(children: .contain)
    }

    private func indicator(selected: Bool, nova: Bool, palette: FoundationPalette) -> some View {
        let ring = selected ? palette.primary : palette.input
        let background = nova && colorScheme == .dark ? palette.novaDarkOutlineFill : Color.clear
        let fill = nova && selected ? palette.primary : background
        let dot = nova ? palette.primaryForeground : palette.primary

        return Circle().fill(fill)
            .overlay(Circle().strokeBorder(ring))
            .overlay(Circle().fill(selected ? dot : Color.clear).frame(width: 8, height: 8))
            .frame(width: 16, height: 16)
            .accessibilityHidden(true)
    }
}
