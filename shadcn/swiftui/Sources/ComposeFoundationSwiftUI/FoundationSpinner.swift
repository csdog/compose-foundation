import SwiftUI

public struct FoundationSpinner: View {
    @Environment(\.foundationTheme) private var theme
    @Environment(\.colorScheme) private var colorScheme

    private let label: String

    public init(accessibilityLabel: String = "Loading") {
        self.label = accessibilityLabel
    }

    public var body: some View {
        ProgressView()
            .progressViewStyle(CircularProgressViewStyle(tint: theme.palette(for: colorScheme).primary))
            .accessibilityLabel(Text(label))
    }
}
