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

    @ViewBuilder
    public var body: some View {
        let palette = theme.palette(for: colorScheme)
        let scale = SliderScale(bounds: bounds, step: step)
        if theme.preset == .nova, scale.span.isFinite, scale.span > 0,
           #available(iOS 17, macOS 14, *) {
            NovaSlider(title: title, value: $value, scale: scale, palette: palette)
        } else {
            Slider(value: $value, in: bounds, step: step) { Text(title) }
                .accentColor(palette.primary)
        }
    }
}

private struct SliderScale {
    let bounds: ClosedRange<Double>
    let step: Double

    var span: Double { bounds.upperBound - bounds.lowerBound }

    func clamped(_ value: Double) -> Double {
        value.isNaN ? bounds.lowerBound : min(max(value, bounds.lowerBound), bounds.upperBound)
    }

    func fraction(at value: Double) -> Double {
        (clamped(value) - bounds.lowerBound) / span
    }

    func value(at fraction: Double) -> Double {
        if fraction <= 0 { return bounds.lowerBound }
        if fraction >= 1 { return bounds.upperBound }
        let offset = fraction * span
        let steps = offset / step
        return clamped(bounds.lowerBound + (steps.isFinite ? steps.rounded() * step : offset))
    }
}

@available(iOS 17, macOS 14, *)
private struct NovaSlider: View {
    @Environment(\.layoutDirection) private var direction
    @Environment(\.isEnabled) private var isEnabled
    @FocusState private var focused: Bool
    @GestureState private var dragging = false

    let title: String
    @Binding var value: Double
    let scale: SliderScale
    let palette: FoundationPalette

    var body: some View {
        GeometryReader { geometry in
            let width = geometry.size.width
            let inset = min(6, width / 2)
            let travel = width - 2 * inset
            let rightToLeft = direction == .rightToLeft
            let filledWidth = inset + CGFloat(scale.fraction(at: value)) * travel
            let thumbX = rightToLeft ? width - filledWidth : filledWidth

            ZStack {
                Capsule().fill(palette.secondary)
                    .overlay(alignment: rightToLeft ? .trailing : .leading) {
                        Rectangle().fill(palette.primary).frame(width: filledWidth)
                    }
                    .clipShape(Capsule())
                    .frame(height: 4)

                Circle().fill(.white)
                    .overlay(Circle().strokeBorder(palette.ring))
                    .frame(width: 12, height: 12)
                    .background {
                        if isEnabled && (focused || dragging) {
                            Circle().strokeBorder(palette.ring.opacity(0.5), lineWidth: 3)
                                .frame(width: 18, height: 18)
                        }
                    }
                    .position(x: thumbX, y: geometry.size.height / 2)
            }
            .environment(\.layoutDirection, .leftToRight)
            .compositingGroup()
            .opacity(isEnabled ? 1 : 0.5)
            .contentShape(Rectangle())
            .gesture(
                DragGesture(minimumDistance: 0)
                    .updating($dragging) { _, state, _ in state = true }
                    .onChanged { event in
                        guard isEnabled, travel > 0 else { return }
                        if !focused { focused = true }
                        let x = rightToLeft ? width - event.location.x : event.location.x
                        value = scale.value(at: Double((x - inset) / travel))
                    },
                including: isEnabled ? .all : .none
            )
        }
        .frame(height: 44)
        .focusable(isEnabled)
        .focused($focused)
        .focusEffectDisabled()
        .onKeyPress(keys: [.leftArrow, .rightArrow, .upArrow, .downArrow, .home, .end], phases: [.down, .repeat]) {
            guard isEnabled else { return .ignored }
            switch $0.key {
            case .home: value = scale.bounds.lowerBound
            case .end: value = scale.bounds.upperBound
            case .leftArrow: adjust(increasing: direction == .rightToLeft)
            case .rightArrow: adjust(increasing: direction != .rightToLeft)
            case .upArrow: adjust(increasing: true)
            case .downArrow: adjust(increasing: false)
            default: return .ignored
            }
            return .handled
        }
        .accessibilityRepresentation {
            let accessibleValue = Binding(get: { scale.clamped(value) }, set: { value = $0 })
            Slider(value: accessibleValue, in: scale.bounds, step: scale.step) { Text(title) }
        }
    }

    private func adjust(increasing: Bool) {
        let current = scale.clamped(value)
        value = scale.value(at: scale.fraction(at: current + (increasing ? scale.step : -scale.step)))
    }
}
