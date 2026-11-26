import SwiftUI

public struct FoundationInputGroup<Leading: View, Content: View, Trailing: View>: View {
    @Environment(\.foundationTheme) private var theme
    @Environment(\.colorScheme) private var colorScheme

    private let leading: Leading
    private let content: Content
    private let trailing: Trailing

    public init(
        @ViewBuilder content: () -> Content,
        @ViewBuilder leading: () -> Leading,
        @ViewBuilder trailing: () -> Trailing
    ) {
        self.content = content()
        self.leading = leading()
        self.trailing = trailing()
    }

    public var body: some View {
        let palette = theme.palette(for: colorScheme)
        let shape = RoundedRectangle(cornerRadius: theme.cornerRadius)

        HStack(spacing: 8) {
            leading.foregroundColor(palette.mutedForeground)
            content
            trailing.foregroundColor(palette.mutedForeground)
        }
        .padding(.horizontal, 12)
        .frame(minHeight: 44)
        .background(shape.fill(palette.card))
        .overlay(shape.strokeBorder(palette.input).allowsHitTesting(false))
    }
}
