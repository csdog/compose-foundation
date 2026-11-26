import SwiftUI

public struct FoundationText: View {
    public enum Variant: Sendable { case title, headline, body, caption, muted }

    @Environment(\.foundationTheme) private var theme
    @Environment(\.colorScheme) private var colorScheme

    private let content: String
    private let variant: Variant

    public init(_ content: String, variant: Variant = .body) {
        self.content = content
        self.variant = variant
    }

    public var body: some View {
        let palette = theme.palette(for: colorScheme)
        Text(content)
            .font(font)
            .foregroundColor(variant == .muted ? palette.mutedForeground : palette.foreground)
    }

    private var font: Font {
        switch variant {
        case .title: .title.weight(.semibold)
        case .headline: .headline
        case .body: .body
        case .caption, .muted: .caption
        }
    }
}
