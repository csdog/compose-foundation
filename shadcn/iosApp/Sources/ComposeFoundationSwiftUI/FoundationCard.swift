import SwiftUI

public enum FoundationCardSize: Sendable {
    case regular, small
}

public struct FoundationCard<Content: View>: View {
    @Environment(\.foundationTheme) private var theme
    @Environment(\.colorScheme) private var colorScheme
    @ScaledMetric(relativeTo: .body) private var padding: CGFloat = 16

    private let content: Content
    private let size: FoundationCardSize
    private let footer: AnyView?

    public init(@ViewBuilder content: () -> Content) {
        self.init(size: .regular, content: content)
    }

    public init(size: FoundationCardSize, @ViewBuilder content: () -> Content) {
        self.size = size
        self.content = content()
        self.footer = nil
    }

    public init<Footer: View>(
        size: FoundationCardSize = .regular,
        @ViewBuilder content: () -> Content,
        @ViewBuilder footer: () -> Footer
    ) {
        self.size = size
        self.content = content()
        self.footer = AnyView(footer())
    }

    @ViewBuilder
    public var body: some View {
        let palette = theme.palette(for: colorScheme)
        let shape = RoundedRectangle(cornerRadius: theme.cardCornerRadius)
        let inset = padding * (theme.preset?.metrics.cardPadding ?? 16) / 16 * (size == .small ? 0.75 : 1)

        if let footer {
            VStack(alignment: .leading, spacing: 0) {
                content.padding(inset)
                footer.padding(inset)
                    .frame(maxWidth: .infinity, alignment: .leading)
                    .background(palette.secondary.opacity(0.5))
                    .overlay(Rectangle().fill(palette.border).frame(height: 1).allowsHitTesting(false), alignment: .top)
            }
            .foregroundColor(palette.foreground)
            .background(shape.fill(palette.card))
            .clipShape(shape)
            .overlay(shape.strokeBorder(palette.border).allowsHitTesting(false))
        } else {
            content
                .foregroundColor(palette.foreground)
                .padding(inset)
                .background(shape.fill(palette.card))
                .overlay(shape.strokeBorder(palette.border).allowsHitTesting(false))
        }
    }
}
