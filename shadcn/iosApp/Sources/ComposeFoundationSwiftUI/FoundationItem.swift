import SwiftUI

public struct FoundationItem<Media: View, Actions: View>: View {
    @Environment(\.foundationTheme) private var theme
    @Environment(\.colorScheme) private var colorScheme

    private let title: String
    private let description: String?
    private let media: Media
    private let actions: Actions

    public init(
        _ title: String,
        description: String? = nil,
        @ViewBuilder media: () -> Media,
        @ViewBuilder actions: () -> Actions
    ) {
        self.title = title
        self.description = description
        self.media = media()
        self.actions = actions()
    }

    public var body: some View {
        let palette = theme.palette(for: colorScheme)
        let shape = RoundedRectangle(cornerRadius: theme.cornerRadius)

        HStack(alignment: .center, spacing: 10) {
            media
            VStack(alignment: .leading, spacing: 3) {
                Text(title).font(.subheadline.weight(.medium)).foregroundColor(palette.foreground)
                if let description {
                    Text(description).font(.caption).foregroundColor(palette.mutedForeground)
                }
            }
            Spacer(minLength: 0)
            actions
        }
        .padding(10)
        .background(shape.fill(palette.card))
        .overlay(shape.strokeBorder(palette.border).allowsHitTesting(false))
    }
}
