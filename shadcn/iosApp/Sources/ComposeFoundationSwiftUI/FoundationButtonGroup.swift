import SwiftUI

public struct FoundationButtonGroup<Content: View>: View {
    @Environment(\.foundationTheme) private var theme
    @Environment(\.colorScheme) private var colorScheme

    private let axis: Axis
    private let content: Content

    public init(_ axis: Axis = .horizontal, @ViewBuilder content: () -> Content) {
        self.axis = axis
        self.content = content()
    }

    public var body: some View {
        let palette = theme.palette(for: colorScheme)
        let shape = RoundedRectangle(cornerRadius: theme.cornerRadius)

        Group {
            if axis == .horizontal {
                HStack(spacing: 0) { content }
            } else {
                VStack(spacing: 0) { content }
            }
        }
        .padding(2)
        .background(shape.fill(palette.secondary))
        .overlay(shape.strokeBorder(palette.border).allowsHitTesting(false))
        .accessibilityElement(children: .contain)
    }
}
