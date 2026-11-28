import SwiftUI

public struct FoundationDropdownMenu<Trigger: View, Items: View>: View {
    @Environment(\.foundationTheme) private var theme
    @Environment(\.colorScheme) private var colorScheme
    @Environment(\.foundationSurface) private var surface
    @Environment(\.font) private var font
    #if os(iOS)
    @Environment(\.isEnabled) private var isEnabled
    #endif
    @ScaledMetric(relativeTo: .subheadline) private var novaHeight: CGFloat = 32
    @ScaledMetric(relativeTo: .subheadline) private var novaFontSize: CGFloat = 14

    private let trigger: Trigger
    private let items: Items

    public init(@ViewBuilder items: () -> Items, @ViewBuilder trigger: () -> Trigger) {
        self.trigger = trigger()
        self.items = items()
    }

    public var body: some View {
        let palette = theme.palette(for: colorScheme)
        let shape = RoundedRectangle(cornerRadius: theme.cornerRadius)
        let nova = theme.preset == .nova
        let height = max(32, novaHeight)
        let outlineFill = colorScheme == .dark ? palette.novaDarkOutlineFill(in: surface) : Color.clear
        let outlineBorder = colorScheme == .dark ? palette.input : palette.border

        Menu {
            items
        } label: {
            if nova {
                trigger
                    .font(font ?? .system(size: novaFontSize, weight: .medium))
                    .foregroundColor(palette.foreground)
                    .padding(.horizontal, 11)
                    .frame(minHeight: height)
                    .background(shape.fill(outlineFill))
                    .overlay(shape.strokeBorder(outlineBorder).allowsHitTesting(false))
                    .frame(minWidth: 44, minHeight: 44)
                    .contentShape(Rectangle())
            } else {
                trigger
                    .foregroundColor(palette.foreground)
                    .padding(.horizontal, 10)
                    .padding(.vertical, 6)
                    .background(shape.fill(palette.card))
                    .overlay(shape.strokeBorder(palette.border).allowsHitTesting(false))
            }
        }
        #if os(iOS)
        .opacity(nova && !isEnabled ? 0.5 : 1)
        #endif
    }
}
