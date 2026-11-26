import SwiftUI

public struct FoundationKbd: View {
    @Environment(\.foundationTheme) private var theme
    @Environment(\.colorScheme) private var colorScheme

    private let key: String

    public init(_ key: String) {
        self.key = key
    }

    public var body: some View {
        let palette = theme.palette(for: colorScheme)
        let shape = RoundedRectangle(cornerRadius: theme.cornerRadius / 2)

        Text(key)
            .font(.system(.caption, design: .monospaced).weight(.medium))
            .foregroundColor(palette.mutedForeground)
            .padding(.horizontal, 5)
            .padding(.vertical, 3)
            .background(shape.fill(palette.secondary))
            .overlay(shape.strokeBorder(palette.border))
    }
}
