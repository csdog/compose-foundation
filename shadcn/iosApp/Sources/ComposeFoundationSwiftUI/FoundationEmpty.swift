import SwiftUI

public struct FoundationEmpty<Actions: View>: View {
    @Environment(\.foundationTheme) private var theme
    @Environment(\.colorScheme) private var colorScheme

    private let title: String
    private let message: String?
    private let symbol: String?
    private let actions: Actions

    public init(_ title: String, message: String? = nil, symbol: String? = nil, @ViewBuilder actions: () -> Actions) {
        self.title = title
        self.message = message
        self.symbol = symbol
        self.actions = actions()
    }

    public var body: some View {
        let palette = theme.palette(for: colorScheme)

        VStack(spacing: 8) {
            if let symbol {
                Image(systemName: symbol)
                    .font(.title3)
                    .padding(10)
                    .background(RoundedRectangle(cornerRadius: theme.cornerRadius).fill(palette.secondary))
                    .accessibilityHidden(true)
            }
            Text(title).font(.headline)
            if let message {
                Text(message).font(.subheadline).foregroundColor(palette.mutedForeground)
            }
            actions
        }
        .multilineTextAlignment(.center)
        .foregroundColor(palette.foreground)
        .frame(maxWidth: .infinity)
        .padding(24)
    }
}

public extension FoundationEmpty where Actions == EmptyView {
    init(_ title: String, message: String? = nil, symbol: String? = nil) {
        self.init(title, message: message, symbol: symbol) { EmptyView() }
    }
}
