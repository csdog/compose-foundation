import SwiftUI

public struct FoundationSkeleton: View {
    @Environment(\.foundationTheme) private var theme
    @Environment(\.colorScheme) private var colorScheme

    private let radius: CGFloat?

    public init(cornerRadius: CGFloat? = nil) {
        self.radius = cornerRadius
    }

    public var body: some View {
        let defaultRadius = theme.preset == .nova ? max(0, theme.cornerRadius - 2) : theme.cornerRadius
        return RoundedRectangle(cornerRadius: radius ?? defaultRadius)
            .fill(theme.palette(for: colorScheme).secondary)
            .accessibilityHidden(true)
    }
}
