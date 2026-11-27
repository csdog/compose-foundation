import SwiftUI

public struct FoundationBadge<Label: View>: View {
    public enum Variant: Sendable {
        case primary
        case secondary
        case outline
        case destructive
    }

    @Environment(\.foundationTheme) private var theme
    @Environment(\.colorScheme) private var colorScheme
    @Environment(\.font) private var labelFont

    private let variant: Variant
    private let label: Label

    public init(_ variant: Variant = .primary, @ViewBuilder label: () -> Label) {
        self.variant = variant
        self.label = label()
    }

    public var body: some View {
        let colors = colors(for: theme.palette(for: colorScheme))
        let nova = theme.preset == .nova
        let font = nova ? labelFont ?? .system(size: 12, weight: .medium) : .caption.weight(.medium)

        return label
            .font(font)
            .foregroundColor(colors.foreground)
            .padding(.horizontal, nova ? 9 : 8)
            .padding(.vertical, nova ? 2 : 3)
            .frame(minHeight: nova ? 20 : nil)
            .background(Capsule().fill(colors.background))
            .overlay(Capsule().strokeBorder(colors.border).allowsHitTesting(false))
    }

    private func colors(for palette: FoundationPalette) -> (foreground: Color, background: Color, border: Color) {
        switch variant {
        case .primary:
            return (palette.primaryForeground, palette.primary, .clear)
        case .secondary:
            return (palette.secondaryForeground, palette.secondary, .clear)
        case .outline:
            return (palette.foreground, .clear, palette.border)
        case .destructive:
            let opacity = theme.preset == .nova ? (colorScheme == .dark ? 0.20 : 0.10) : 0.15
            return (palette.destructive, palette.destructive.opacity(opacity), .clear)
        }
    }
}
