import SwiftUI

public struct FoundationResizable<First: View, Second: View>: View {
    @Environment(\.foundationTheme) private var theme
    @Environment(\.colorScheme) private var colorScheme
    @Environment(\.layoutDirection) private var layoutDirection
    @Environment(\.isEnabled) private var isEnabled
    @Binding private var fraction: CGFloat
    @GestureState private var displacement: CGFloat = 0

    private let axis: Axis
    private let limits: ClosedRange<CGFloat>
    private let showsHandle: Bool
    private let first: First
    private let second: Second

    public init(
        _ axis: Axis = .horizontal,
        fraction: Binding<CGFloat>,
        limits: ClosedRange<CGFloat> = 0.15...0.85,
        @ViewBuilder first: () -> First,
        @ViewBuilder second: () -> Second
    ) {
        self.init(axis, fraction: fraction, limits: limits, showsHandle: true, first: first, second: second)
    }

    public init(
        _ axis: Axis = .horizontal,
        fraction: Binding<CGFloat>,
        limits: ClosedRange<CGFloat> = 0.15...0.85,
        showsHandle: Bool,
        @ViewBuilder first: () -> First,
        @ViewBuilder second: () -> Second
    ) {
        self.axis = axis
        self._fraction = fraction
        self.limits = limits.lowerBound.isFinite && limits.upperBound.isFinite
            ? min(max(limits.lowerBound, 0), 1)...min(max(limits.upperBound, 0), 1)
            : 0.15...0.85
        self.showsHandle = showsHandle
        self.first = first()
        self.second = second()
    }

    public var body: some View {
        GeometryReader { geometry in
            let horizontal = axis == .horizontal
            let full = horizontal ? geometry.size.width : geometry.size.height
            let nova = theme.preset == .nova
            let handle = min(nova ? 1 : 16, full)
            let available = max(full - handle, 0)
            let firstSize = available * clamped(clamped(fraction) + delta(displacement, available: available))

            if nova {
                novaSplit(size: geometry.size, firstSize: firstSize, available: available, divider: handle)
            } else if horizontal {
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

    private func novaSplit(size: CGSize, firstSize: CGFloat, available: CGFloat, divider: CGFloat) -> some View {
        let horizontal = axis == .horizontal
        let position = firstSize + divider / 2
        return ZStack {
            if horizontal {
                HStack(spacing: 0) {
                    first.frame(width: firstSize, height: size.height).clipped()
                    Color.clear.frame(width: divider).accessibilityHidden(true)
                    second.frame(width: available - firstSize, height: size.height).clipped()
                }
                separator(available: available)
                    .frame(width: min(44, size.width), height: size.height)
                    .position(x: position, y: size.height / 2)
            } else {
                VStack(spacing: 0) {
                    first.frame(width: size.width, height: firstSize).clipped()
                    Color.clear.frame(height: divider).accessibilityHidden(true)
                    second.frame(width: size.width, height: available - firstSize).clipped()
                }
                separator(available: available)
                    .frame(width: size.width, height: min(44, size.height))
                    .position(x: size.width / 2, y: position)
            }
        }
    }

    private func separator(available: CGFloat) -> some View {
        let palette = theme.palette(for: colorScheme)
        let horizontal = axis == .horizontal
        let surface = ZStack {
            Rectangle().fill(palette.border)
                .frame(width: horizontal ? 1 : nil, height: horizontal ? nil : 1)
            if showsHandle {
                if theme.preset == .nova {
                    Capsule().fill(palette.border)
                        .frame(width: horizontal ? 4 : 24, height: horizontal ? 24 : 4)
                } else {
                    Image(systemName: "ellipsis")
                        .font(.caption2)
                        .foregroundColor(palette.mutedForeground)
                        .rotationEffect(.degrees(horizontal ? 90 : 0))
                        .padding(2)
                        .background(palette.card)
                }
            }
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
                },
            including: isEnabled ? .all : .none
        )
        .accessibilityLabel(Text("Resize panels"))
        .accessibilityValue(Text(NumberFormatter.localizedString(from: NSNumber(value: Double(clamped(fraction))), number: .percent)))
        .accessibilityAdjustableAction { direction in
            guard isEnabled, available > 0 else { return }
            switch direction {
            case .increment: adjust(by: 0.05)
            case .decrement: adjust(by: -0.05)
            @unknown default: break
            }
        }
        return keyboardResize(surface, available: available)
    }

    @ViewBuilder
    private func keyboardResize<Content: View>(_ content: Content, available: CGFloat) -> some View {
        if #available(iOS 17, macOS 14, *) {
            content
                .focusable(isEnabled && available > 0)
                .onKeyPress(keys: [.leftArrow, .rightArrow, .upArrow, .downArrow, .home, .end],
                            phases: [.down, .repeat]) { press in
                    guard isEnabled, available > 0,
                          press.modifiers.intersection([.control, .command, .option, .shift]).isEmpty else {
                        return .ignored
                    }
                    let forward: CGFloat = layoutDirection == .rightToLeft ? -0.1 : 0.1
                    switch press.key {
                    case .leftArrow where axis == .horizontal: adjust(by: -forward)
                    case .rightArrow where axis == .horizontal: adjust(by: forward)
                    case .upArrow where axis == .vertical: adjust(by: -0.1)
                    case .downArrow where axis == .vertical: adjust(by: 0.1)
                    case .home: fraction = limits.lowerBound
                    case .end: fraction = limits.upperBound
                    default: return .ignored
                    }
                    return .handled
                }
        } else {
            content
        }
    }

    private func adjust(by delta: CGFloat) {
        fraction = clamped(clamped(fraction) + delta)
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
