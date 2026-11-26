import SwiftUI

public struct FoundationMarker: View {
    public enum Variant: Sendable { case plain, separator, border }

    @Environment(\.foundationTheme) private var theme
    @Environment(\.colorScheme) private var colorScheme

    private let title: String
    private let variant: Variant

    public init(_ title: String, variant: Variant = .plain) {
        self.title = title
        self.variant = variant
    }

    public var body: some View {
        let palette = theme.palette(for: colorScheme)
        let label = Text(title).font(.caption.weight(.medium)).foregroundColor(palette.mutedForeground)

        switch variant {
        case .plain:
            label.frame(maxWidth: .infinity)
        case .separator:
            HStack(spacing: 8) {
                rule(palette)
                label
                rule(palette)
            }
        case .border:
            label.padding(.horizontal, 9).padding(.vertical, 4)
                .overlay(Capsule().strokeBorder(palette.border).allowsHitTesting(false))
                .frame(maxWidth: .infinity)
        }
    }

    private func rule(_ palette: FoundationPalette) -> some View {
        Rectangle().fill(palette.border).frame(height: 1).accessibilityHidden(true)
    }
}
