import SwiftUI

public struct FoundationBubble<Content: View>: View {
    public enum Variant: Sendable { case incoming, outgoing, muted }

    @Environment(\.foundationTheme) private var theme
    @Environment(\.colorScheme) private var colorScheme

    private let variant: Variant
    private let content: Content

    public init(_ variant: Variant = .incoming, @ViewBuilder content: () -> Content) {
        self.variant = variant
        self.content = content()
    }

    public var body: some View {
        let palette = theme.palette(for: colorScheme)
        let shape = RoundedRectangle(cornerRadius: theme.cornerRadius + 5)

        content
            .foregroundColor(variant == .outgoing ? palette.primaryForeground : palette.foreground)
            .padding(.horizontal, 12)
            .padding(.vertical, 8)
            .background(shape.fill(background(palette)))
    }

    private func background(_ palette: FoundationPalette) -> Color {
        switch variant {
        case .incoming: palette.secondary
        case .outgoing: palette.primary
        case .muted: palette.secondary.opacity(0.55)
        }
    }
}
