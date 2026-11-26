import SwiftUI

public struct FoundationAlert<Actions: View>: View {
    public enum Variant: Sendable { case standard, destructive }

    @Environment(\.foundationTheme) private var theme
    @Environment(\.colorScheme) private var colorScheme

    private let title: String
    private let message: String?
    private let symbol: String
    private let variant: Variant
    private let actions: Actions

    public init(
        _ title: String,
        message: String? = nil,
        symbol: String = "info.circle",
        variant: Variant = .standard,
        @ViewBuilder actions: () -> Actions
    ) {
        self.title = title
        self.message = message
        self.symbol = symbol
        self.variant = variant
        self.actions = actions()
    }

    public var body: some View {
        let palette = theme.palette(for: colorScheme)
        let foreground = variant == .destructive ? palette.destructive : palette.foreground
        let shape = RoundedRectangle(cornerRadius: theme.cornerRadius)

        HStack(alignment: .top, spacing: 10) {
            Image(systemName: symbol)
                .foregroundColor(foreground)
                .accessibilityHidden(true)
            VStack(alignment: .leading, spacing: 4) {
                Text(title).font(.subheadline.weight(.semibold)).foregroundColor(foreground)
                if let message {
                    Text(message).font(.subheadline).foregroundColor(palette.mutedForeground)
                }
                actions
            }
            Spacer(minLength: 0)
        }
        .padding(12)
        .background(shape.fill(palette.card))
        .overlay(shape.strokeBorder(palette.border).allowsHitTesting(false))
    }
}

public extension FoundationAlert where Actions == EmptyView {
    init(_ title: String, message: String? = nil, symbol: String = "info.circle", variant: Variant = .standard) {
        self.init(title, message: message, symbol: symbol, variant: variant) { EmptyView() }
    }
}
