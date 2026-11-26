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

    private let variant: Variant
    private let label: Label

    public init(_ variant: Variant = .primary, @ViewBuilder label: () -> Label) {
        self.variant = variant
        self.label = label()
    }

    public var body: some View {
        let colors = colors(for: theme.palette(for: colorScheme))

        label
            .font(.caption.weight(.medium))
            .foregroundColor(colors.foreground)
            .padding(.horizontal, 8)
            .padding(.vertical, 3)
            .background(Capsule().fill(colors.background))
            .overlay(Capsule().strokeBorder(colors.border).allowsHitTesting(false))
    }

    private func colors(for palette: FoundationPalette) -> (foreground: Color, background: Color, border: Color) {
        switch variant {
        case .primary:
            (palette.primaryForeground, palette.primary, .clear)
        case .secondary:
            (palette.secondaryForeground, palette.secondary, .clear)
        case .outline:
            (palette.foreground, .clear, palette.border)
        case .destructive:
            (palette.destructive, palette.destructive.opacity(0.15), .clear)
        }
    }
}
