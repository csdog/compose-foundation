import SwiftUI

public struct FoundationDropdownMenu<Trigger: View, Items: View>: View {
    @Environment(\.foundationTheme) private var theme
    @Environment(\.colorScheme) private var colorScheme

    private let trigger: Trigger
    private let items: Items

    public init(@ViewBuilder items: () -> Items, @ViewBuilder trigger: () -> Trigger) {
        self.trigger = trigger()
        self.items = items()
    }

    public var body: some View {
        let palette = theme.palette(for: colorScheme)
        let shape = RoundedRectangle(cornerRadius: theme.cornerRadius)

        Menu {
            items
        } label: {
            trigger
                .foregroundColor(palette.foreground)
                .padding(.horizontal, 10)
                .padding(.vertical, 6)
                .background(shape.fill(palette.card))
                .overlay(shape.strokeBorder(palette.border).allowsHitTesting(false))
        }
    }
}
