import SwiftUI

public struct FoundationMenubar<Menus: View>: View {
    @Environment(\.foundationTheme) private var theme
    @Environment(\.colorScheme) private var colorScheme
    @ScaledMetric(relativeTo: .subheadline) private var novaFontSize: CGFloat = 14

    private let menus: Menus

    public init(@ViewBuilder menus: () -> Menus) {
        self.menus = menus()
    }

    public var body: some View {
        let palette = theme.palette(for: colorScheme)
        let shape = RoundedRectangle(cornerRadius: theme.cornerRadius)
        let nova = theme.preset == .nova

        HStack(spacing: nova ? 2 : 6) { menus }
            .font(nova ? .system(size: novaFontSize, weight: .medium) : .subheadline.weight(.medium))
            .padding(.horizontal, nova ? 3 : 5)
            .padding(.vertical, nova ? 0 : 5)
            .frame(minHeight: nova ? 32 : nil)
            .background(shape.fill(nova ? palette.background : palette.card))
            .overlay(shape.strokeBorder(palette.border).allowsHitTesting(false))
            .accessibilityElement(children: .contain)
    }
}

public extension FoundationMenubar where Menus == AnyView {
    init(groups: [FoundationMenuGroup]) {
        self.init { AnyView(FoundationMenuGroups(groups: groups, spacing: 6, isMenubar: true)) }
    }
}
