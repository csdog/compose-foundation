import SwiftUI

public struct FoundationField<Content: View>: View {
    public enum DescriptionPlacement: Sendable {
        case beforeContent, afterContent
    }

    public enum Orientation: Sendable {
        case vertical, horizontal, responsive
    }

    public enum ControlPlacement: Sendable {
        case leading, trailing
    }

    @Environment(\.foundationTheme) private var theme
    @Environment(\.colorScheme) private var colorScheme
    @Environment(\.font) private var font
    @ScaledMetric(relativeTo: .subheadline) private var novaTextSize: CGFloat = 14

    private let title: String
    private let description: String?
    private let error: String?
    private let descriptionPlacement: DescriptionPlacement
    private let orientation: Orientation
    private let controlPlacement: ControlPlacement
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
        self.init(title, description: description, error: error, orientation: .vertical,
                  descriptionPlacement: descriptionPlacement, content: content)
    }

    public init(
        _ title: String,
        description: String? = nil,
        error: String? = nil,
        orientation: Orientation,
        controlPlacement: ControlPlacement = .trailing,
        descriptionPlacement: DescriptionPlacement = .afterContent,
        @ViewBuilder content: () -> Content
    ) {
        self.title = title
        self.description = description
        self.error = error
        self.descriptionPlacement = descriptionPlacement
        self.orientation = orientation
        self.controlPlacement = controlPlacement
        self.content = content()
    }

    public var body: some View {
        let palette = theme.palette(for: colorScheme)
        let nova = theme.preset == .nova
        let spacing: CGFloat = nova ? 8 : 6
        let supportFont = nova ? (font ?? .system(size: novaTextSize)) : .caption
        let supportHeight = nova ? novaTextSize * (20.0 / 14) : nil

        VStack(alignment: .leading, spacing: spacing) {
            if orientation == .vertical {
                fieldLabel(nova: nova, palette: palette)
                if descriptionPlacement == .beforeContent, let description {
                    supportText(description, color: palette.mutedForeground, font: supportFont, minHeight: supportHeight)
                }
                content
                if descriptionPlacement == .afterContent, let description {
                    supportText(description, color: palette.mutedForeground, font: supportFont, minHeight: supportHeight)
                }
            } else {
                controlRow(spacing: spacing) {
                    if controlPlacement == .leading { content }
                    VStack(alignment: .leading, spacing: 2) {
                        fieldLabel(nova: nova, palette: palette)
                        if let description {
                            supportText(description, color: palette.mutedForeground,
                                        font: supportFont, minHeight: supportHeight)
                        }
                    }
                    .layoutPriority(1)
                    if controlPlacement == .trailing { content }
                }
            }
            if let error {
                supportText(error, color: palette.destructive, font: supportFont, minHeight: supportHeight)
            }
        }
        .accessibilityElement(children: .contain)
    }

    @ViewBuilder
    private func fieldLabel(nova: Bool, palette: FoundationPalette) -> some View {
        if nova {
            FoundationLabel(title)
                .font(font?.weight(.medium))
                .frame(minHeight: novaTextSize * (19.0 / 14))
        } else {
            Text(title).font(.subheadline.weight(.medium)).foregroundColor(palette.foreground)
        }
    }

    @ViewBuilder
    private func controlRow<Row: View>(spacing: CGFloat, @ViewBuilder row: () -> Row) -> some View {
        let alignment: VerticalAlignment = description == nil ? .center : .top
        if orientation == .horizontal {
            HStack(alignment: alignment, spacing: spacing, content: row)
        } else if #available(iOS 16, macOS 13, *) {
            FoundationResponsiveFieldLayout(alignment: alignment, spacing: spacing) { row() }
        } else {
            VStack(alignment: .leading, spacing: spacing, content: row)
        }
    }

    private func supportText(_ value: String, color: Color, font: Font, minHeight: CGFloat?) -> some View {
        Text(value)
            .font(font)
            .foregroundColor(color)
            .frame(minHeight: minHeight)
    }
}

@available(iOS 16, macOS 13, *)
private struct FoundationResponsiveFieldLayout: Layout {
    struct Cache {
        var row: HStackLayout.Cache
        var column: VStackLayout.Cache
    }

    let alignment: VerticalAlignment
    let spacing: CGFloat

    private var row: HStackLayout { HStackLayout(alignment: alignment, spacing: spacing) }
    private var column: VStackLayout { VStackLayout(alignment: .leading, spacing: spacing) }

    func makeCache(subviews: Subviews) -> Cache {
        Cache(row: row.makeCache(subviews: subviews), column: column.makeCache(subviews: subviews))
    }

    func sizeThatFits(proposal: ProposedViewSize, subviews: Subviews, cache: inout Cache) -> CGSize {
        if isWide(proposal) {
            row.sizeThatFits(proposal: proposal, subviews: subviews, cache: &cache.row)
        } else {
            column.sizeThatFits(proposal: proposal, subviews: subviews, cache: &cache.column)
        }
    }

    func placeSubviews(in bounds: CGRect, proposal: ProposedViewSize, subviews: Subviews, cache: inout Cache) {
        if isWide(proposal) {
            row.placeSubviews(in: bounds, proposal: proposal, subviews: subviews, cache: &cache.row)
        } else {
            column.placeSubviews(in: bounds, proposal: proposal, subviews: subviews, cache: &cache.column)
        }
    }

    private func isWide(_ proposal: ProposedViewSize) -> Bool {
        (proposal.width ?? 0) >= 448
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
