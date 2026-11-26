import SwiftUI

public struct FoundationMessage<Content: View>: View {
    public enum Alignment: Sendable { case incoming, outgoing }

    @Environment(\.foundationTheme) private var theme
    @Environment(\.colorScheme) private var colorScheme

    private let alignment: Alignment
    private let author: String?
    private let timestamp: Date?
    private let content: Content

    public init(
        alignment: Alignment = .incoming,
        author: String? = nil,
        timestamp: Date? = nil,
        @ViewBuilder content: () -> Content
    ) {
        self.alignment = alignment
        self.author = author
        self.timestamp = timestamp
        self.content = content()
    }

    public var body: some View {
        let palette = theme.palette(for: colorScheme)

        VStack(alignment: alignment == .incoming ? .leading : .trailing, spacing: 4) {
            if author != nil || timestamp != nil {
                HStack(spacing: 6) {
                    if let author { Text(author).fontWeight(.semibold) }
                    if let timestamp { Text(timestamp, style: .time) }
                }
                .font(.caption)
                .foregroundColor(palette.mutedForeground)
            }
            content
        }
        .frame(maxWidth: .infinity, alignment: alignment == .incoming ? .leading : .trailing)
        .accessibilityElement(children: .contain)
    }
}
