import SwiftUI

public struct FoundationNavigationMenu<Items: View>: View {
    @Environment(\.foundationTheme) private var theme
    @Environment(\.colorScheme) private var colorScheme
    @ScaledMetric(relativeTo: .subheadline) private var novaFontSize: CGFloat = 14

    private let items: Items

    public init(@ViewBuilder items: () -> Items) {
        self.items = items()
    }

    public var body: some View {
        let palette = theme.palette(for: colorScheme)
        let nova = theme.preset == .nova

        ScrollView(.horizontal, showsIndicators: false) {
            HStack(spacing: nova ? 0 : 16) { items }
                .font(nova ? .system(size: novaFontSize, weight: .medium) : .subheadline.weight(.medium))
                .padding(.horizontal, nova ? 0 : 8)
        }
        .foregroundColor(palette.foreground)
        .accentColor(palette.primary)
        .accessibilityElement(children: .contain)
    }
}

public extension FoundationNavigationMenu where Items == AnyView {
    init(groups: [FoundationMenuGroup]) {
        self.init { AnyView(FoundationMenuGroups(groups: groups, spacing: 16, novaStyle: .novaNavigation)) }
    }
}
