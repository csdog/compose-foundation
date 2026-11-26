import SwiftUI

public struct FoundationField<Content: View>: View {
    @Environment(\.foundationTheme) private var theme
    @Environment(\.colorScheme) private var colorScheme

    private let title: String
    private let description: String?
    private let error: String?
    private let content: Content

    public init(
        _ title: String,
        description: String? = nil,
        error: String? = nil,
        @ViewBuilder content: () -> Content
    ) {
        self.title = title
        self.description = description
        self.error = error
        self.content = content()
    }

    public var body: some View {
        let palette = theme.palette(for: colorScheme)

        VStack(alignment: .leading, spacing: 6) {
            Text(title).font(.subheadline.weight(.medium)).foregroundColor(palette.foreground)
            content
            if let description {
                Text(description).font(.caption).foregroundColor(palette.mutedForeground)
            }
            if let error {
                Text(error).font(.caption).foregroundColor(palette.destructive)
            }
        }
        .accessibilityElement(children: .contain)
    }
}
