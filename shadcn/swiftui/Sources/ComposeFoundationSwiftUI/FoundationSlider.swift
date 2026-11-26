import SwiftUI

public struct FoundationSlider: View {
    @Environment(\.foundationTheme) private var theme
    @Environment(\.colorScheme) private var colorScheme
    @Binding private var value: Double

    private let title: String
    private let bounds: ClosedRange<Double>
    private let step: Double

    public init(_ title: String, value: Binding<Double>, in bounds: ClosedRange<Double>, step: Double = 1) {
        self.title = title
        self._value = value
        self.bounds = bounds
        self.step = step.isFinite && step > 0 ? step : 1
    }

    public var body: some View {
        Slider(value: $value, in: bounds, step: step) { Text(title) }
            .accentColor(theme.palette(for: colorScheme).primary)
    }
}
