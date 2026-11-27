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

    @ViewBuilder
    public var body: some View {
        let palette = theme.palette(for: colorScheme)
        let shape = RoundedRectangle(cornerRadius: theme.cornerRadius)

        if theme.preset == .nova, #available(iOS 18, macOS 15, *) {
            Group(subviews: content) { subviews in
                let entries = Array(subviews.enumerated())
                stack {
                    ForEach(entries, id: \.element.id) { entry in
                        entry.element.environment(\.foundationButtonGroupPosition, FoundationButtonGroupPosition(
                            axis: axis, first: entry.offset == 0, last: entry.offset == entries.count - 1
                        ))
                    }
                }
            }
            .buttonStyle(FoundationButtonStyle(.outline, size: .regular))
            .accessibilityElement(children: .contain)
        } else {
            stack { content }
                .padding(2)
                .background(shape.fill(palette.secondary))
                .overlay(shape.strokeBorder(palette.border).allowsHitTesting(false))
                .accessibilityElement(children: .contain)
        }
    }

    @ViewBuilder
    private func stack<Children: View>(@ViewBuilder content: () -> Children) -> some View {
        if axis == .horizontal {
            HStack(spacing: 0, content: content)
        } else {
            VStack(spacing: 0, content: content)
        }
    }
}
