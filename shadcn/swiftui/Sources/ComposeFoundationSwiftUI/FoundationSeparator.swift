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
        Rectangle()
            .fill(theme.palette(for: colorScheme).border)
            .frame(width: axis == .vertical ? 1 / displayScale : nil,
                   height: axis == .horizontal ? 1 / displayScale : nil)
            .accessibilityHidden(true)
    }
}
