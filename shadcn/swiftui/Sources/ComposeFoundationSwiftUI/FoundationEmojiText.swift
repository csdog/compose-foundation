import SwiftUI

public struct FoundationEmojiText: View {
    @Environment(\.foundationTheme) private var theme
    @Environment(\.colorScheme) private var colorScheme

    private let content: String
    private let font: Font

    public init(_ content: String, font: Font = .body) {
        self.content = content
        self.font = font
    }

    public var body: some View {
        // Text preserves joined emoji sequences and uses the platform's color emoji font.
        Text(verbatim: content)
            .font(font)
            .foregroundColor(theme.palette(for: colorScheme).foreground)
    }
}
