import SwiftUI

public struct FoundationResizable<First: View, Second: View>: View {
    @Environment(\.foundationTheme) private var theme
    @Environment(\.colorScheme) private var colorScheme
    @Environment(\.layoutDirection) private var layoutDirection
    @Binding private var fraction: CGFloat
    @GestureState private var displacement: CGFloat = 0

    private let axis: Axis
    private let limits: ClosedRange<CGFloat>
    private let first: First
    private let second: Second

    public init(
        _ axis: Axis = .horizontal,
        fraction: Binding<CGFloat>,
        limits: ClosedRange<CGFloat> = 0.15...0.85,
        @ViewBuilder first: () -> First,
        @ViewBuilder second: () -> Second
    ) {
        self.axis = axis
        self._fraction = fraction
        self.limits = limits.lowerBound.isFinite && limits.upperBound.isFinite
            ? min(max(limits.lowerBound, 0), 1)...min(max(limits.upperBound, 0), 1)
            : 0.15...0.85
        self.first = first()
        self.second = second()
    }

    public var body: some View {
        GeometryReader { geometry in
            let horizontal = axis == .horizontal
            let full = horizontal ? geometry.size.width : geometry.size.height
            let handle = min(16, full)
            let available = max(full - handle, 0)
            let firstSize = available * clamped(clamped(fraction) + delta(displacement, available: available))

            if horizontal {
                HStack(spacing: 0) {
                    first.frame(width: firstSize).clipped()
                    separator(available: available).frame(width: handle)
                    second.frame(width: available - firstSize).clipped()
                }
            } else {
                VStack(spacing: 0) {
                    first.frame(height: firstSize).clipped()
                    separator(available: available).frame(height: handle)
                    second.frame(height: available - firstSize).clipped()
                }
            }
        }
    }

    private func separator(available: CGFloat) -> some View {
        let palette = theme.palette(for: colorScheme)
        let horizontal = axis == .horizontal

        return ZStack {
            Rectangle().fill(palette.border)
                .frame(width: horizontal ? 1 : nil, height: horizontal ? nil : 1)
            Image(systemName: "ellipsis")
                .font(.caption2)
                .foregroundColor(palette.mutedForeground)
                .rotationEffect(.degrees(horizontal ? 90 : 0))
                .padding(2)
                .background(palette.card)
        }
        .frame(maxWidth: .infinity, maxHeight: .infinity)
        .contentShape(Rectangle())
        .gesture(
            DragGesture(coordinateSpace: .global)
                .updating($displacement) { event, value, _ in
                    value = horizontal ? event.translation.width : event.translation.height
                }
                .onEnded { event in
                    let translation = horizontal ? event.translation.width : event.translation.height
                    fraction = clamped(clamped(fraction) + delta(translation, available: available))
                }
        )
        .accessibilityLabel(Text("Resize panels"))
        .accessibilityValue(Text(NumberFormatter.localizedString(from: NSNumber(value: Double(clamped(fraction))), number: .percent)))
        .accessibilityAdjustableAction { direction in
            switch direction {
            case .increment: fraction = clamped(clamped(fraction) + 0.05)
            case .decrement: fraction = clamped(clamped(fraction) - 0.05)
            @unknown default: break
            }
        }
    }

    private func delta(_ displacement: CGFloat, available: CGFloat) -> CGFloat {
        guard available > 0 else { return 0 }
        let sign: CGFloat = axis == .horizontal && layoutDirection == .rightToLeft ? -1 : 1
        return sign * displacement / available
    }

    private func clamped(_ value: CGFloat) -> CGFloat {
        min(max(value.isFinite ? value : 0.5, limits.lowerBound), limits.upperBound)
    }
}
