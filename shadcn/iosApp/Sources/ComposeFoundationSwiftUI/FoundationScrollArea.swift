import SwiftUI

public struct FoundationScrollArea<Content: View>: View {
    @Environment(\.foundationTheme) private var theme
    @Environment(\.colorScheme) private var colorScheme
    private let axes: Axis.Set
    private let showsIndicators: Bool
    private let showsBorder: Bool
    private let content: Content

    public init(
        _ axes: Axis.Set = .vertical,
        showsIndicators: Bool = true,
        @ViewBuilder content: () -> Content
    ) {
        self.init(axes, showsIndicators: showsIndicators, showsBorder: false, content: content)
    }

    /// Adds a one-point themed border and clips the viewport to its rounded corners.
    public init(
        _ axes: Axis.Set = .vertical,
        showsIndicators: Bool = true,
        showsBorder: Bool,
        @ViewBuilder content: () -> Content
    ) {
        self.axes = axes
        self.showsIndicators = showsIndicators
        self.showsBorder = showsBorder
        self.content = content()
    }

    public var body: some View {
        let viewport = ScrollView(axes, showsIndicators: showsIndicators) { content }
        if showsBorder {
            let radius = theme.preset == .nova ? max(0, theme.cornerRadius - 2) : theme.cornerRadius
            let shape = RoundedRectangle(cornerRadius: radius)
            viewport
                .padding(1)
                .clipShape(shape)
                .contentShape(shape)
                .overlay(shape.strokeBorder(theme.palette(for: colorScheme).border)
                    .allowsHitTesting(false)
                    .accessibilityHidden(true))
        } else {
            viewport
        }
    }
}
