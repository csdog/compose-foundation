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

    public enum Size: Sendable {
        case extraSmall, small, regular, large
        case iconExtraSmall, iconSmall, icon, iconLarge

        var height: CGFloat {
            switch self {
            case .extraSmall, .iconExtraSmall: 24
            case .small, .iconSmall: 28
            case .regular, .icon: 32
            case .large, .iconLarge: 36
            }
        }

        var textSize: CGFloat {
            switch self {
            case .extraSmall, .iconExtraSmall: 12
            case .small, .iconSmall: 12.8
            default: 14
            }
        }

        var horizontalPadding: CGFloat {
            switch self {
            case .extraSmall: 8
            case .small, .regular, .large: 10
            default: 0
            }
        }

        var iconOnly: Bool { horizontalPadding == 0 }
    }

    @Environment(\.foundationTheme) private var theme
    @Environment(\.colorScheme) private var colorScheme
    @Environment(\.isEnabled) private var isEnabled
    @Environment(\.accessibilityReduceMotion) private var reduceMotion
    @Environment(\.textCase) private var textCase
    @Environment(\.font) private var labelFont
    @ScaledMetric(relativeTo: .subheadline) private var horizontalPadding: CGFloat = 12

    private let variant: Variant
    private let size: Size?

    public init(_ variant: Variant = .primary) {
        self.variant = variant
        self.size = nil
    }

    public init(_ variant: Variant = .primary, size: Size) {
        self.variant = variant
        self.size = size
    }

    public func makeBody(configuration: Configuration) -> some View {
        let palette = theme.palette(for: colorScheme)
        let colors = colors(for: palette)
        let metrics = theme.preset?.metrics
        let height = size?.height ?? metrics?.buttonHeight ?? 32
        let radius = metrics == nil ? theme.cornerRadius : min(theme.cornerRadius, height / 2)
        let shape = RoundedRectangle(cornerRadius: radius)
        let editorial = theme.preset == .sera
        let pressed = isEnabled && configuration.isPressed
        let novaOutline = theme.preset == .nova && variant == .outline
        let darkNovaOutline = novaOutline && colorScheme == .dark
        let font = labelFont ?? size.map { Font.system(size: $0.textSize, weight: .medium) }
            ?? theme.font(editorial ? .caption : .subheadline, weight: editorial ? .semibold : .medium)
        let padding = size?.horizontalPadding ?? metrics?.buttonPadding ?? 12
        let iconOnly = size?.iconOnly == true

        return configuration.label
            .font(font)
            .textCase(editorial ? .uppercase : textCase)
            .foregroundColor(colors.foreground)
            .padding(.horizontal, horizontalPadding * padding / 12 + (novaOutline && !iconOnly ? 1 : 0))
            .padding(.vertical, size == nil ? 6 : 2)
            .frame(minHeight: height)
            .frame(width: iconOnly ? height : nil)
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
            .frame(minWidth: iconOnly ? 44 : nil, minHeight: 44)
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
