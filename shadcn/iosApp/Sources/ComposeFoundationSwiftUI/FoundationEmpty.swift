import SwiftUI

public enum FoundationEmptyVariant: Sendable {
    case standard
    case outline
}

public struct FoundationEmpty<Actions: View>: View {
    @Environment(\.foundationTheme) private var theme
    @Environment(\.colorScheme) private var colorScheme
    @ScaledMetric(relativeTo: .subheadline) private var novaTextSize: CGFloat = 14

    private let title: String
    private let message: String?
    private let symbol: String?
    private let variant: FoundationEmptyVariant
    private let actions: Actions

    public init(_ title: String, message: String? = nil, symbol: String? = nil, @ViewBuilder actions: () -> Actions) {
        self.init(title, message: message, symbol: symbol, variant: .standard, actions: actions)
    }

    public init(
        _ title: String, message: String? = nil, symbol: String? = nil,
        variant: FoundationEmptyVariant, @ViewBuilder actions: () -> Actions
    ) {
        self.title = title
        self.message = message
        self.symbol = symbol
        self.variant = variant
        self.actions = actions()
    }

    public var body: some View {
        let palette = theme.palette(for: colorScheme)

        Group {
            if theme.preset == .nova {
                VStack(spacing: 16) {
                    novaHeader(palette: palette)
                    actions
                }
                .frame(maxWidth: 384)
            } else {
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
            }
        }
        .multilineTextAlignment(.center)
        .foregroundColor(palette.foreground)
        .frame(maxWidth: .infinity)
        .padding(24)
        .overlay(Group {
            if variant == .outline {
                RoundedRectangle(cornerRadius: theme.cardCornerRadius)
                    .strokeBorder(palette.border, style: StrokeStyle(lineWidth: 1, dash: [4, 4]))
                    .allowsHitTesting(false)
                    .accessibilityHidden(true)
            }
        })
    }

    private func novaHeader(palette: FoundationPalette) -> some View {
        VStack(spacing: 16) {
            if let symbol {
                Image(systemName: symbol)
                    .font(.system(size: 16))
                    .frame(width: 32, height: 32)
                    .background(RoundedRectangle(cornerRadius: theme.cornerRadius).fill(palette.secondary))
                    .accessibilityHidden(true)
            }
            VStack(spacing: 8) {
                Text(title)
                    .font(.system(size: novaTextSize, weight: .medium))
                    .tracking(-0.35)
                    .accessibilityAddTraits(.isHeader)
                if let message {
                    Text(message)
                        .font(.system(size: novaTextSize))
                        .lineSpacing(5)
                        .foregroundColor(palette.mutedForeground)
                }
            }
            .fixedSize(horizontal: false, vertical: true)
        }
    }
}

public extension FoundationEmpty where Actions == EmptyView {
    init(_ title: String, message: String? = nil, symbol: String? = nil) {
        self.init(title, message: message, symbol: symbol) { EmptyView() }
    }

    init(_ title: String, message: String? = nil, symbol: String? = nil, variant: FoundationEmptyVariant) {
        self.init(title, message: message, symbol: symbol, variant: variant) { EmptyView() }
    }
}
