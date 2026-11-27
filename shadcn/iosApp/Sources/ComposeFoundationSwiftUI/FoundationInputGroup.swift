import SwiftUI

public struct FoundationInputGroup<Leading: View, Content: View, Trailing: View>: View {
    @Environment(\.foundationTheme) private var theme
    @Environment(\.colorScheme) private var colorScheme
    @Environment(\.foundationSurface) private var surface

    private let axis: Axis
    private let isFocused: Bool
    private let isInvalid: Bool

    private let leading: Leading
    private let content: Content
    private let trailing: Trailing

    public init(
        @ViewBuilder content: () -> Content,
        @ViewBuilder leading: () -> Leading,
        @ViewBuilder trailing: () -> Trailing
    ) {
        self.init(axis: .horizontal, content: content, leading: leading, trailing: trailing)
    }

    /// Places the addons before and after the content, or above and below it on a vertical axis.
    /// Pass the native editor's focus state to draw the focus ring around the group.
    public init(
        axis: Axis,
        isFocused: Bool = false,
        isInvalid: Bool = false,
        @ViewBuilder content: () -> Content,
        @ViewBuilder leading: () -> Leading,
        @ViewBuilder trailing: () -> Trailing
    ) {
        self.axis = axis
        self.isFocused = isFocused
        self.isInvalid = isInvalid
        self.content = content()
        self.leading = leading()
        self.trailing = trailing()
    }

    public var body: some View {
        let palette = theme.palette(for: colorScheme)
        let shape = RoundedRectangle(cornerRadius: theme.cornerRadius)
        let nova = theme.preset == .nova
        let inset: CGFloat = nova && axis == .horizontal ? 6 : 0
        let border = isInvalid ? palette.destructive : isFocused ? palette.ring : palette.input
        let ring = isInvalid ? palette.destructive.opacity(0.2) : isFocused ? palette.ring.opacity(0.5) : .clear
        let fill = nova ? (colorScheme == .dark ? palette.novaDarkOutlineFill(in: surface) : .clear) : palette.card

        Group {
            if axis == .vertical {
                VStack(alignment: .leading, spacing: 6) {
                    leading.foregroundColor(palette.mutedForeground)
                    content
                    trailing.foregroundColor(palette.mutedForeground)
                }
                .frame(maxWidth: .infinity, alignment: .leading)
                .padding(.horizontal, 11)
                .padding(.vertical, 8)
            } else {
                HStack(spacing: 8) {
                    leading.foregroundColor(palette.mutedForeground)
                    content
                    trailing.foregroundColor(palette.mutedForeground)
                }
                .padding(.horizontal, nova ? 9 : 12)
            }
        }
        .frame(minHeight: 44)
        .background(shape.fill(fill).padding(.vertical, inset))
        .overlay(
            shape.strokeBorder(border)
                .background(shape.inset(by: -1.5).stroke(ring, lineWidth: 3))
                .padding(.vertical, inset)
                .allowsHitTesting(false)
                .accessibilityHidden(true)
        )
    }
}
