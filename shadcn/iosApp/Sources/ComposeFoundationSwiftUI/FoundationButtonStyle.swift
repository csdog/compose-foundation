import SwiftUI

public struct FoundationButtonStyle: ButtonStyle {
    public enum Variant: Sendable {
        case primary
        case secondary
        case outline
        case ghost
        case destructive
        case link
    }

    @Environment(\.foundationTheme) private var theme
    @Environment(\.colorScheme) private var colorScheme
    @Environment(\.isEnabled) private var isEnabled
    @Environment(\.accessibilityReduceMotion) private var reduceMotion
    @Environment(\.textCase) private var textCase
    @ScaledMetric(relativeTo: .subheadline) private var horizontalPadding: CGFloat = 12

    private let variant: Variant

    public init(_ variant: Variant = .primary) {
        self.variant = variant
    }

    public func makeBody(configuration: Configuration) -> some View {
        let palette = theme.palette(for: colorScheme)
        let colors = colors(for: palette)
        let metrics = theme.preset?.metrics
        let height = metrics?.buttonHeight ?? 32
        let radius = metrics == nil ? theme.cornerRadius : min(theme.cornerRadius, height / 2)
        let shape = RoundedRectangle(cornerRadius: radius)
        let editorial = theme.preset == .sera
        let pressed = isEnabled && configuration.isPressed
        let novaOutline = theme.preset == .nova && variant == .outline
        let darkNovaOutline = novaOutline && colorScheme == .dark

        configuration.label
            .font(theme.font(editorial ? .caption : .subheadline, weight: editorial ? .semibold : .medium))
            .textCase(editorial ? .uppercase : textCase)
            .foregroundColor(colors.foreground)
            .padding(.horizontal, horizontalPadding * (metrics?.buttonPadding ?? 12) / 12 + (novaOutline ? 1 : 0))
            .padding(.vertical, 6)
            .frame(minHeight: height)
            .background(Group {
                if darkNovaOutline {
                    shape.inset(by: 1).fill(palette.novaDarkOutlineFill)
                } else {
                    shape.fill(colors.background)
                }
            })
            .overlay(shape.strokeBorder(darkNovaOutline ? palette.input : colors.border))
            .opacity(isEnabled ? 1 : 0.5)
            .offset(y: pressed ? 1 : 0)
            .animation(reduceMotion ? nil : .timingCurve(0.4, 0, 0.2, 1, duration: 0.15), value: pressed)
            .frame(minHeight: 44)
            .contentShape(Rectangle())
    }

    private func colors(for palette: FoundationPalette) -> (foreground: Color, background: Color, border: Color) {
        switch variant {
        case .primary:
            (palette.primaryForeground, palette.primary, .clear)
        case .secondary:
            (palette.secondaryForeground, palette.secondary, .clear)
        case .outline:
            (palette.foreground, .clear, palette.border)
        case .ghost:
            (palette.foreground, .clear, .clear)
        case .destructive:
            (palette.destructive, palette.destructive.opacity(0.15), .clear)
        case .link:
            (palette.primary, .clear, .clear)
        }
    }
}
