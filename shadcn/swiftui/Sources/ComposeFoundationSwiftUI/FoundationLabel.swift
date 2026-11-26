import SwiftUI

public struct FoundationLabel: View {
    @Environment(\.foundationTheme) private var theme
    @Environment(\.colorScheme) private var colorScheme
    @Environment(\.isEnabled) private var isEnabled

    private let title: String

    public init(_ title: String) {
        self.title = title
    }

    public var body: some View {
        Text(title)
            .font(theme.font(.subheadline, weight: .medium))
            .foregroundColor(theme.palette(for: colorScheme).foreground)
            .opacity(isEnabled ? 1 : 0.5)
    }
}
