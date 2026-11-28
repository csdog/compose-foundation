import SwiftUI

public struct FoundationField<Content: View>: View {
    public enum DescriptionPlacement: Sendable {
        case beforeContent, afterContent
    }

    @Environment(\.foundationTheme) private var theme
    @Environment(\.colorScheme) private var colorScheme
    @Environment(\.font) private var font
    @ScaledMetric(relativeTo: .subheadline) private var novaTextSize: CGFloat = 14

    private let title: String
    private let description: String?
    private let error: String?
    private let descriptionPlacement: DescriptionPlacement
    private let content: Content

    public init(
        _ title: String,
        description: String? = nil,
        error: String? = nil,
        @ViewBuilder content: () -> Content
    ) {
        self.init(title, description: description, error: error, descriptionPlacement: .afterContent, content: content)
    }

    public init(
        _ title: String,
        description: String? = nil,
        error: String? = nil,
        descriptionPlacement: DescriptionPlacement,
        @ViewBuilder content: () -> Content
    ) {
        self.title = title
        self.description = description
        self.error = error
        self.descriptionPlacement = descriptionPlacement
        self.content = content()
    }

    public var body: some View {
        let palette = theme.palette(for: colorScheme)
        let nova = theme.preset == .nova
        let supportFont = nova ? (font ?? .system(size: novaTextSize)) : .caption
        let supportHeight = nova ? novaTextSize * (20.0 / 14) : nil

        VStack(alignment: .leading, spacing: nova ? 8 : 6) {
            if nova {
                FoundationLabel(title)
                    .font(font?.weight(.medium))
                    .frame(minHeight: novaTextSize * (19.0 / 14))
            } else {
                Text(title).font(.subheadline.weight(.medium)).foregroundColor(palette.foreground)
            }
            if descriptionPlacement == .beforeContent, let description {
                supportText(description, color: palette.mutedForeground, font: supportFont, minHeight: supportHeight)
            }
            content
            if descriptionPlacement == .afterContent, let description {
                supportText(description, color: palette.mutedForeground, font: supportFont, minHeight: supportHeight)
            }
            if let error {
                supportText(error, color: palette.destructive, font: supportFont, minHeight: supportHeight)
            }
        }
        .accessibilityElement(children: .contain)
    }

    private func supportText(_ value: String, color: Color, font: Font, minHeight: CGFloat?) -> some View {
        Text(value)
            .font(font)
            .foregroundColor(color)
            .frame(minHeight: minHeight)
    }
}

public struct FoundationFieldLegend: View {
    public enum Variant: Sendable {
        case legend, label
    }

    @Environment(\.foundationTheme) private var theme
    @Environment(\.colorScheme) private var colorScheme
    @Environment(\.font) private var font
    @ScaledMetric(relativeTo: .body) private var legendSize: CGFloat = 16
    @ScaledMetric(relativeTo: .subheadline) private var labelSize: CGFloat = 14
    private let text: String
    private let variant: Variant

    public init(_ text: String, variant: Variant = .legend) {
        self.text = text
        self.variant = variant
    }

    public var body: some View {
        let size = variant == .legend ? legendSize : labelSize
        Text(text)
            .font(font?.weight(.medium) ?? .system(size: size, weight: .medium))
            .foregroundColor(theme.palette(for: colorScheme).foreground)
            .frame(minHeight: size * 1.5, alignment: .leading)
            .accessibilityAddTraits(.isHeader)
    }
}

/// Groups related fields under a visible legend and an optional explanation.
public struct FoundationFieldSet<Content: View>: View {
    @Environment(\.foundationTheme) private var theme
    @Environment(\.colorScheme) private var colorScheme
    @ScaledMetric(relativeTo: .subheadline) private var descriptionSize: CGFloat = 14
    private let legend: String
    private let description: String?
    private let variant: FoundationFieldLegend.Variant
    private let content: Content

    public init(
        _ legend: String,
        description: String? = nil,
        variant: FoundationFieldLegend.Variant = .legend,
        @ViewBuilder content: () -> Content
    ) {
        self.legend = legend
        self.description = description
        self.variant = variant
        self.content = content()
    }

    public var body: some View {
        let nova = theme.preset == .nova
        VStack(alignment: .leading, spacing: nova ? 16 : 12) {
            VStack(alignment: .leading, spacing: nova ? 2 : 4) {
                FoundationFieldLegend(legend, variant: variant)
                if let description {
                    Text(description)
                        .font(nova ? .system(size: descriptionSize) : .caption)
                        .foregroundColor(theme.palette(for: colorScheme).mutedForeground)
                        .frame(minHeight: nova ? descriptionSize * (20.0 / 14) : nil, alignment: .leading)
                }
            }
            content
        }
        .accessibilityElement(children: .contain)
    }
}

public struct FoundationFieldGroup<Content: View>: View {
    @Environment(\.foundationTheme) private var theme
    private let content: Content

    public init(@ViewBuilder content: () -> Content) {
        self.content = content()
    }

    public var body: some View {
        VStack(alignment: .leading, spacing: theme.preset == .nova ? 20 : 12) { content }
    }
}
