import SwiftUI

public struct FoundationPalette: Sendable {
    public var background: Color
    public var foreground: Color
    public var primary: Color
    public var primaryForeground: Color
    public var secondary: Color
    public var secondaryForeground: Color
    public var mutedForeground: Color
    public var card: Color
    public var border: Color
    public var input: Color
    public var ring: Color
    public var destructive: Color

    public static let neutralLight = Self(
        background: .white,
        foreground: Color(rgb: 0x0a0a0a),
        primary: Color(rgb: 0x171717),
        primaryForeground: Color(rgb: 0xfafafa),
        secondary: Color(rgb: 0xf5f5f5),
        secondaryForeground: Color(rgb: 0x171717),
        mutedForeground: Color(rgb: 0x737373),
        card: .white,
        border: Color(rgb: 0xe5e5e5),
        input: Color(rgb: 0xe5e5e5),
        ring: Color(rgb: 0xa3a3a3),
        destructive: Color(rgb: 0xe7000b)
    )

    public static let neutralDark = Self(
        background: Color(rgb: 0x0a0a0a),
        foreground: Color(rgb: 0xfafafa),
        primary: Color(rgb: 0xe5e5e5),
        primaryForeground: Color(rgb: 0x171717),
        secondary: Color(rgb: 0x262626),
        secondaryForeground: Color(rgb: 0xfafafa),
        mutedForeground: Color(rgb: 0xa3a3a3),
        card: Color(rgb: 0x171717),
        border: .white.opacity(0.10),
        input: .white.opacity(0.15),
        ring: Color(rgb: 0x737373),
        destructive: Color(rgb: 0xff6467)
    )

    var novaDarkOutlineFill: Color {
        novaDarkOutlineFill(in: .page)
    }

    func novaDarkOutlineFill(in surface: FoundationSurface) -> Color {
        if background == Self.neutralDark.background && input == Self.neutralDark.input {
            switch surface {
            case .page: return Color(rgb: 0x141414)
            case .card where card == Self.neutralDark.card: return Color(rgb: 0x212121)
            case .cardFooter where card == Self.neutralDark.card && secondary == Self.neutralDark.secondary:
                return Color(rgb: 0x272727)
            default: break
            }
        }
        return input.opacity(0.3)
    }

    func novaCardFooterFill(for scheme: ColorScheme) -> Color {
        if scheme == .dark && card == Self.neutralDark.card && secondary == Self.neutralDark.secondary {
            return Color(rgb: 0x1e1e1e)
        }
        return secondary.opacity(0.5)
    }
}

enum FoundationSurface: Sendable {
    case page, card, cardFooter
}

private struct FoundationSurfaceKey: EnvironmentKey {
    static let defaultValue = FoundationSurface.page
}

extension EnvironmentValues {
    var foundationSurface: FoundationSurface {
        get { self[FoundationSurfaceKey.self] }
        set { self[FoundationSurfaceKey.self] = newValue }
    }
}

public enum FoundationStylePreset: String, CaseIterable, Sendable {
    case vega, nova, maia, lyra, mira, luma, sera, rhea

    struct Metrics {
        let controlRadius: CGFloat
        let cardRadius: CGFloat
        let popoverRadius: CGFloat
        let buttonPadding: CGFloat
        let buttonHeight: CGFloat
        let inputPadding: CGFloat
        let cardPadding: CGFloat
        let popoverPadding: CGFloat
        let transition: Double
        let visibility: Double
    }

    var metrics: Metrics {
        switch self {
        case .vega:
            return Metrics(controlRadius: 8, cardRadius: 14, popoverRadius: 8,
                           buttonPadding: 10, buttonHeight: 36, inputPadding: 10, cardPadding: 24, popoverPadding: 16,
                           transition: 0.20, visibility: 0.15)
        case .nova:
            return Metrics(controlRadius: 10, cardRadius: 14, popoverRadius: 10,
                           buttonPadding: 10, buttonHeight: 32, inputPadding: 10, cardPadding: 16, popoverPadding: 10,
                           transition: 0.10, visibility: 0.075)
        case .maia:
            return Metrics(controlRadius: 32, cardRadius: 16, popoverRadius: 16,
                           buttonPadding: 12, buttonHeight: 36, inputPadding: 12, cardPadding: 24, popoverPadding: 16,
                           transition: 0.30, visibility: 0.20)
        case .lyra:
            return Metrics(controlRadius: 0, cardRadius: 0, popoverRadius: 0,
                           buttonPadding: 10, buttonHeight: 32, inputPadding: 10, cardPadding: 16, popoverPadding: 10,
                           transition: 0.15, visibility: 0.10)
        case .mira:
            return Metrics(controlRadius: 4, cardRadius: 6, popoverRadius: 6,
                           buttonPadding: 8, buttonHeight: 28, inputPadding: 8, cardPadding: 16, popoverPadding: 10,
                           transition: 0.12, visibility: 0.08)
        case .luma:
            return Metrics(controlRadius: 32, cardRadius: 32, popoverRadius: 24,
                           buttonPadding: 12, buttonHeight: 36, inputPadding: 12, cardPadding: 24, popoverPadding: 16,
                           transition: 0.40, visibility: 0.25)
        case .sera:
            return Metrics(controlRadius: 0, cardRadius: 0, popoverRadius: 0,
                           buttonPadding: 24, buttonHeight: 40, inputPadding: 0, cardPadding: 32, popoverPadding: 16,
                           transition: 0.20, visibility: 0.15)
        case .rhea:
            return Metrics(controlRadius: 16, cardRadius: 24, popoverRadius: 24,
                           buttonPadding: 12, buttonHeight: 32, inputPadding: 10, cardPadding: 20, popoverPadding: 16,
                           transition: 0.35, visibility: 0.20)
        }
    }
}

public struct FoundationTheme: Sendable {
    public var light: FoundationPalette
    public var dark: FoundationPalette
    public var cornerRadius: CGFloat
    public let preset: FoundationStylePreset?

    public init(
        light: FoundationPalette = .neutralLight,
        dark: FoundationPalette = .neutralDark,
        cornerRadius: CGFloat = 8
    ) {
        self.light = light
        self.dark = dark
        self.cornerRadius = cornerRadius
        self.preset = nil
    }

    public init(
        preset: FoundationStylePreset,
        light: FoundationPalette = .neutralLight,
        dark: FoundationPalette = .neutralDark,
        cornerRadius: CGFloat? = nil
    ) {
        self.light = light
        self.dark = dark
        self.cornerRadius = cornerRadius ?? preset.metrics.controlRadius
        self.preset = preset
    }

    public func palette(for colorScheme: ColorScheme) -> FoundationPalette {
        colorScheme == .dark ? dark : light
    }

    var cardCornerRadius: CGFloat {
        guard let metrics = preset?.metrics else { return cornerRadius + 2 }
        return max(0, cornerRadius + metrics.cardRadius - metrics.controlRadius)
    }

    var popoverCornerRadius: CGFloat {
        guard let metrics = preset?.metrics else { return cornerRadius }
        return max(0, cornerRadius + metrics.popoverRadius - metrics.controlRadius)
    }

    func font(_ style: Font.TextStyle, weight: Font.Weight? = nil, heading: Bool = false) -> Font {
        let design: Font.Design
        switch preset {
        case .lyra: design = .monospaced
        case .sera where heading: design = .serif
        default: design = .default
        }
        let font = Font.system(style, design: design)
        return weight.map { font.weight($0) } ?? font
    }
}

public struct FoundationChoice<Value: Hashable>: Identifiable {
    public var id: Value { value }
    public let value: Value
    public let title: String
    public let isEnabled: Bool

    public init(_ title: String, value: Value, isEnabled: Bool = true) {
        self.title = title
        self.value = value
        self.isEnabled = isEnabled
    }
}

/// Choices share a selection model across groups. Each value should be unique within the control.
public struct FoundationChoiceGroup<Value: Hashable> {
    public let title: String?
    public let choices: [FoundationChoice<Value>]

    public init(_ title: String? = nil, choices: [FoundationChoice<Value>]) {
        self.title = title
        self.choices = choices
    }
}

private struct FoundationThemeKey: EnvironmentKey {
    static let defaultValue = FoundationTheme()
}

public extension EnvironmentValues {
    var foundationTheme: FoundationTheme {
        get { self[FoundationThemeKey.self] }
        set { self[FoundationThemeKey.self] = newValue }
    }
}

public extension View {
    func foundationTheme(_ theme: FoundationTheme) -> some View {
        environment(\.foundationTheme, theme)
    }
}

extension View {
    @ViewBuilder
    func foundationOnChange<Value: Equatable>(
        of value: Value, perform action: @escaping (Value) -> Void
    ) -> some View {
        if #available(iOS 17, macOS 14, *) {
            onChange(of: value) { _, newValue in action(newValue) }
        } else {
            onChange(of: value, perform: action)
        }
    }
}

private extension Color {
    init(rgb: UInt32) {
        self.init(
            .sRGB,
            red: Double((rgb >> 16) & 0xff) / 255,
            green: Double((rgb >> 8) & 0xff) / 255,
            blue: Double(rgb & 0xff) / 255
        )
    }
}
