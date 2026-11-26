import SwiftUI

public struct FoundationSkeleton: View {
    @Environment(\.foundationTheme) private var theme
    @Environment(\.colorScheme) private var colorScheme

    private let radius: CGFloat?

    public init(cornerRadius: CGFloat? = nil) {
        self.radius = cornerRadius
    }

    public var body: some View {
        RoundedRectangle(cornerRadius: radius ?? theme.cornerRadius)
            .fill(theme.palette(for: colorScheme).secondary)
            .accessibilityHidden(true)
    }
}
