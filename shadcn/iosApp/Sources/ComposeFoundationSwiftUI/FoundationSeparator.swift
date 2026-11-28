import SwiftUI

public struct FoundationSeparator: View {
    @Environment(\.foundationTheme) private var theme
    @Environment(\.colorScheme) private var colorScheme
    @Environment(\.displayScale) private var displayScale

    private let axis: Axis

    public init(_ axis: Axis = .horizontal) {
        self.axis = axis
    }

    public var body: some View {
        let thickness = theme.preset == .nova ? 1 : 1 / displayScale
        return Rectangle()
            .fill(theme.palette(for: colorScheme).border)
            .frame(width: axis == .vertical ? thickness : nil,
                   height: axis == .horizontal ? thickness : nil)
            .accessibilityHidden(true)
    }
}
