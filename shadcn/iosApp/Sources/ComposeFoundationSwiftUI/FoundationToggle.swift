import SwiftUI

public struct FoundationToggle<Label: View>: View {
    @Environment(\.foundationTheme) private var theme
    @Environment(\.colorScheme) private var colorScheme
    @Environment(\.isEnabled) private var isEnabled
    @Binding private var isOn: Bool

    private let label: Label

    public init(isOn: Binding<Bool>, @ViewBuilder label: () -> Label) {
        self._isOn = isOn
        self.label = label()
    }

    public var body: some View {
        let palette = theme.palette(for: colorScheme)
        let shape = RoundedRectangle(cornerRadius: theme.cornerRadius)

        Button {
            isOn.toggle()
        } label: {
            label
                .font(.subheadline.weight(.medium))
                .foregroundColor(isOn ? palette.primaryForeground : palette.foreground)
                .padding(.horizontal, 10)
                .padding(.vertical, 6)
                .background(shape.fill(isOn ? palette.primary : Color.clear))
                .overlay(shape.strokeBorder(isOn ? palette.primary : palette.border))
                .frame(minHeight: 44)
                .contentShape(Rectangle())
                .opacity(isEnabled ? 1 : 0.5)
        }
        .buttonStyle(PlainButtonStyle())
        .accessibilityAddTraits(isOn ? .isSelected : [])
    }
}
