import SwiftUI

public struct FoundationAlert<Actions: View>: View {
    public enum Variant: Sendable { case standard, destructive }

    @Environment(\.foundationTheme) private var theme
    @Environment(\.colorScheme) private var colorScheme
    @Environment(\.font) private var font
    @ScaledMetric(relativeTo: .subheadline) private var novaTextSize: CGFloat = 14

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
        let nova = theme.preset == .nova
        let messageColor = nova && variant == .destructive ? palette.destructive.opacity(0.9) : palette.mutedForeground
        let titleFont = nova ? (font?.weight(.medium) ?? .system(size: novaTextSize, weight: .medium)) : .subheadline.weight(.semibold)
        let messageFont = nova ? (font ?? .system(size: novaTextSize)) : .subheadline

        HStack(alignment: .top, spacing: nova ? 8 : 10) {
            Image(systemName: symbol)
                .font(nova ? .system(size: 14) : nil)
                .foregroundColor(foreground)
                .frame(width: nova ? 16 : nil, height: nova ? 16 : nil)
                .padding(.top, nova ? 2 : 0)
                .accessibilityHidden(true)
            VStack(alignment: .leading, spacing: nova ? 2 : 4) {
                Text(title).font(titleFont).foregroundColor(foreground)
                    .frame(minHeight: nova ? novaTextSize * (20.0 / 14) : nil)
                if let message {
                    Text(message).font(messageFont).foregroundColor(messageColor)
                        .lineSpacing(nova ? 3 : 0)
                        .fixedSize(horizontal: false, vertical: nova)
                }
                actions
            }
            Spacer(minLength: 0)
        }
        .padding(.horizontal, nova ? 11 : 12)
        .padding(.vertical, nova ? 9 : 12)
        .background(shape.fill(palette.card))
        .overlay(shape.strokeBorder(palette.border).allowsHitTesting(false))
    }
}

public extension FoundationAlert where Actions == EmptyView {
    init(_ title: String, message: String? = nil, symbol: String = "info.circle", variant: Variant = .standard) {
        self.init(title, message: message, symbol: symbol, variant: variant) { EmptyView() }
    }
}
