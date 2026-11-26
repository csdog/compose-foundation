import SwiftUI

public struct FoundationNavigationMenu<Items: View>: View {
    @Environment(\.foundationTheme) private var theme
    @Environment(\.colorScheme) private var colorScheme

    private let items: Items

    public init(@ViewBuilder items: () -> Items) {
        self.items = items()
    }

    public var body: some View {
        let palette = theme.palette(for: colorScheme)

        ScrollView(.horizontal, showsIndicators: false) {
            HStack(spacing: 16) { items }
                .font(.subheadline.weight(.medium))
                .padding(.horizontal, 8)
        }
        .foregroundColor(palette.foreground)
        .accentColor(palette.primary)
        .accessibilityElement(children: .contain)
    }
}

public extension FoundationNavigationMenu where Items == AnyView {
    init(groups: [FoundationMenuGroup]) {
        self.init { AnyView(FoundationMenuGroups(groups: groups, spacing: 16)) }
    }
}
