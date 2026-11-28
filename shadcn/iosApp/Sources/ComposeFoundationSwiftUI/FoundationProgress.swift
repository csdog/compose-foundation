import SwiftUI

public struct FoundationProgress: View {
    @Environment(\.foundationTheme) private var theme
    @Environment(\.colorScheme) private var colorScheme
    @Environment(\.accessibilityReduceMotion) private var reduceMotion
    @Environment(\.font) private var font
    @ScaledMetric(relativeTo: .subheadline) private var labelSize: CGFloat = 14

    private let fraction: Double
    private let label: String
    private let visibleLabel: String?
    private let visibleValue: String?

    public init(value: Double, total: Double = 1, accessibilityLabel: String = "Progress") {
        self.init(value: value, total: total, accessibilityLabel: accessibilityLabel, visibleLabel: nil, visibleValue: nil)
    }

    /// Displays a heading and a percentage above the track.
    public init(_ title: String, value: Double, total: Double = 1, valueLabel: String? = nil) {
        self.init(value: value, total: total, accessibilityLabel: title, visibleLabel: title, visibleValue: valueLabel)
    }

    private init(value: Double, total: Double, accessibilityLabel: String, visibleLabel: String?, visibleValue: String?) {
        self.fraction = value.isFinite && total.isFinite && total > 0
            ? min(max(value / total, 0), 1) : 0
        self.label = accessibilityLabel
        self.visibleLabel = visibleLabel
        self.visibleValue = visibleValue
    }

    public var body: some View {
        let palette = theme.palette(for: colorScheme)
        let nova = theme.preset == .nova
        let value = visibleValue ?? NumberFormatter.localizedString(from: NSNumber(value: fraction), number: .percent)

        Group {
            if let visibleLabel {
                let labelFont = font ?? .system(size: labelSize)
                VStack(alignment: .leading, spacing: nova ? 12 : 8) {
                    HStack(spacing: 12) {
                        Text(visibleLabel)
                            .font(labelFont.weight(.medium))
                            .foregroundColor(palette.foreground)
                        Spacer(minLength: 0)
                        Text(value)
                            .font(labelFont.monospacedDigit())
                            .foregroundColor(palette.mutedForeground)
                    }
                    .frame(minHeight: nova ? labelSize * (20.0 / 14) : nil)
                    track(palette: palette, nova: nova)
                }
                .accessibilityElement(children: .ignore)
            } else {
                track(palette: palette, nova: nova)
            }
        }
        .accessibilityLabel(Text(label))
        .accessibilityValue(Text(value))
    }

    private func track(palette: FoundationPalette, nova: Bool) -> some View {
        GeometryReader { geometry in
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
    }
}
