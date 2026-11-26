import SwiftUI

public struct FoundationIcon: View {
    @Environment(\.foundationTheme) private var theme
    @Environment(\.colorScheme) private var colorScheme
    @ScaledMetric(relativeTo: .body) private var size: CGFloat = 16

    private let systemName: String
    private let label: String?

    public init(systemName: String, accessibilityLabel: String? = nil) {
        self.systemName = systemName
        self.label = accessibilityLabel
    }

    public var body: some View {
        Image(systemName: systemName)
            .font(.system(size: size, weight: .medium))
            .foregroundColor(theme.palette(for: colorScheme).foreground)
            .accessibilityLabel(Text(label ?? ""))
            .accessibilityHidden(label == nil)
    }
}
