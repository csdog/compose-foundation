import SwiftUI

public struct FoundationMenubar<Menus: View>: View {
    @Environment(\.foundationTheme) private var theme
    @Environment(\.colorScheme) private var colorScheme

    private let menus: Menus

    public init(@ViewBuilder menus: () -> Menus) {
        self.menus = menus()
    }

    public var body: some View {
        let palette = theme.palette(for: colorScheme)
        let shape = RoundedRectangle(cornerRadius: theme.cornerRadius)

        HStack(spacing: 6) { menus }
            .font(.subheadline.weight(.medium))
            .padding(5)
            .background(shape.fill(palette.card))
            .overlay(shape.strokeBorder(palette.border).allowsHitTesting(false))
            .accessibilityElement(children: .contain)
    }
}

public extension FoundationMenubar where Menus == AnyView {
    init(groups: [FoundationMenuGroup]) {
        self.init { AnyView(FoundationMenuGroups(groups: groups, spacing: 6)) }
    }
}
