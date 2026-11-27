import SwiftUI

public enum FoundationToggleVariant: Sendable {
    case plain, outline
}

public enum FoundationToggleSize: Sendable {
    case small, regular, large

    fileprivate var height: CGFloat {
        switch self {
        case .small: 28
        case .regular: 32
        case .large: 36
        }
    }

    fileprivate var fontSize: CGFloat { self == .small ? 12.8 : 14 }
}

public struct FoundationToggle<Label: View>: View {
    @Environment(\.foundationTheme) private var theme
    @Environment(\.colorScheme) private var colorScheme
    @Environment(\.isEnabled) private var isEnabled
    @Environment(\.font) private var font
    @Binding private var isOn: Bool

    private let label: Label
    private let variant: FoundationToggleVariant?
    private let size: FoundationToggleSize

    public init(isOn: Binding<Bool>, @ViewBuilder label: () -> Label) {
        self._isOn = isOn
        self.label = label()
        self.variant = nil
        self.size = .regular
    }

    public init(
        isOn: Binding<Bool>,
        variant: FoundationToggleVariant,
        size: FoundationToggleSize = .regular,
        @ViewBuilder label: () -> Label
    ) {
        self._isOn = isOn
        self.label = label()
        self.variant = variant
        self.size = size
    }

    public var body: some View {
        let palette = theme.palette(for: colorScheme)
        let shape = RoundedRectangle(cornerRadius: theme.cornerRadius)
        let appearance = variant ?? (theme.preset == .nova ? .plain : nil)

        Button {
            isOn.toggle()
        } label: {
            if let appearance {
                let outline = appearance == .outline
                let styledShape = RoundedRectangle(cornerRadius: size == .small ? min(theme.cornerRadius, 8) : theme.cornerRadius)
                label
                    .font(font ?? .system(size: size.fontSize, weight: .medium))
                    .foregroundColor(palette.foreground)
                    .padding(.horizontal, outline ? 11 : 10)
                    .frame(minWidth: size.height, minHeight: size.height)
                    .background(styledShape.fill(isOn ? palette.secondary : .clear))
                    .overlay(styledShape.strokeBorder(outline ? palette.input : .clear))
                    .frame(minWidth: 44, minHeight: 44)
                    .contentShape(Rectangle())
            } else {
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
        }
        .buttonStyle(PlainButtonStyle())
        .accessibilityAddTraits(isOn ? .isSelected : [])
    }
}
