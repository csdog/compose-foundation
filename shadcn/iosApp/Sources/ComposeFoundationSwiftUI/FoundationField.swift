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
