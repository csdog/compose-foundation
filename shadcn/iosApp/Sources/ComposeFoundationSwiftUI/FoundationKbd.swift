import SwiftUI

public struct FoundationKbd: View {
    @Environment(\.foundationTheme) private var theme
    @Environment(\.colorScheme) private var colorScheme
    @Environment(\.font) private var labelFont

    private let key: String

    public init(_ key: String) {
        self.key = key
    }

    public var body: some View {
        let palette = theme.palette(for: colorScheme)
        let nova = theme.preset == .nova
        let shape = RoundedRectangle(cornerRadius: theme.cornerRadius / 2)
        let font = nova ? labelFont ?? .system(size: 12, weight: .medium) : .system(.caption, design: .monospaced).weight(.medium)

        return Text(key)
            .font(font)
            .foregroundColor(palette.mutedForeground)
            .padding(.horizontal, nova ? 4 : 5)
            .padding(.vertical, nova ? 0 : 3)
            .frame(minWidth: nova ? 20 : nil, minHeight: nova ? 20 : nil)
            .background(shape.fill(palette.secondary))
            .overlay(shape.strokeBorder(nova ? .clear : palette.border))
    }
}

/// Present the keycaps and separators of a keyboard shortcut as one group.
public struct FoundationKbdGroup<Content: View>: View {
    private let content: Content

    public init(@ViewBuilder content: () -> Content) {
        self.content = content()
    }

    public var body: some View {
        HStack(spacing: 4) { content }
            .accessibilityElement(children: .combine)
    }
}
