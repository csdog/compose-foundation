import SwiftUI

public struct FoundationProgress: View {
    @Environment(\.foundationTheme) private var theme
    @Environment(\.colorScheme) private var colorScheme
    @Environment(\.accessibilityReduceMotion) private var reduceMotion

    private let fraction: Double
    private let label: String

    public init(value: Double, total: Double = 1, accessibilityLabel: String = "Progress") {
        self.fraction = value.isFinite && total.isFinite && total > 0
            ? min(max(value / total, 0), 1) : 0
        self.label = accessibilityLabel
    }

    public var body: some View {
        let palette = theme.palette(for: colorScheme)
        let nova = theme.preset == .nova

        return GeometryReader { geometry in
            ZStack(alignment: .leading) {
                Capsule().fill(palette.secondary)
                if nova {
                    Rectangle().fill(palette.primary)
                        .frame(width: geometry.size.width * fraction)
                } else {
                    Capsule().fill(palette.primary)
                        .frame(width: geometry.size.width * fraction)
                }
            }
        }
        .frame(height: nova ? 4 : 6)
        .clipShape(Capsule())
        .animation(reduceMotion ? nil : .easeInOut(duration: theme.preset?.metrics.transition ?? 0.15), value: fraction)
        .accessibilityLabel(Text(label))
        .accessibilityValue(Text(NumberFormatter.localizedString(from: NSNumber(value: fraction), number: .percent)))
    }
}
