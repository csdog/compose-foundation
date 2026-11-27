import SwiftUI

public struct FoundationLabel: View {
    @Environment(\.foundationTheme) private var theme
    @Environment(\.colorScheme) private var colorScheme
    @Environment(\.isEnabled) private var isEnabled
    @Environment(\.font) private var font
    @ScaledMetric(relativeTo: .subheadline) private var novaSize: CGFloat = 14

    private let title: String

    public init(_ title: String) {
        self.title = title
    }

    public var body: some View {
        let labelFont = theme.preset == .nova
            ? (font ?? .system(size: novaSize, weight: .medium))
            : theme.font(.subheadline, weight: .medium)

        Text(title)
            .font(labelFont)
            .foregroundColor(theme.palette(for: colorScheme).foreground)
            .opacity(isEnabled ? 1 : 0.5)
    }
}
