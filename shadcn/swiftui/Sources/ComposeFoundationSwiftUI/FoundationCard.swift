import SwiftUI

public struct FoundationCard<Content: View>: View {
    @Environment(\.foundationTheme) private var theme
    @Environment(\.colorScheme) private var colorScheme
    @ScaledMetric(relativeTo: .body) private var padding: CGFloat = 16

    private let content: Content

    public init(@ViewBuilder content: () -> Content) {
        self.content = content()
    }

    public var body: some View {
        let palette = theme.palette(for: colorScheme)
        let shape = RoundedRectangle(cornerRadius: theme.cardCornerRadius)

        content
            .foregroundColor(palette.foreground)
            .padding(padding * (theme.preset?.metrics.cardPadding ?? 16) / 16)
            .background(shape.fill(palette.card))
            .overlay(shape.strokeBorder(palette.border).allowsHitTesting(false))
    }
}
