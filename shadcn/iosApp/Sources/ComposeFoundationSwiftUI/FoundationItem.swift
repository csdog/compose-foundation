import SwiftUI

public enum FoundationItemVariant: Sendable {
    case standard, outline, muted
}

public enum FoundationItemSize: Sendable {
    case regular, small, extraSmall
}

public struct FoundationItem<Media: View, Actions: View>: View {
    @Environment(\.foundationTheme) private var theme
    @Environment(\.colorScheme) private var colorScheme
    @ScaledMetric(relativeTo: .subheadline) private var textSize: CGFloat = 14

    private let title: String
    private let description: String?
    private let variant: FoundationItemVariant?
    private let size: FoundationItemSize
    private let media: Media
    private let actions: Actions

    public init(
        _ title: String,
        description: String? = nil,
        @ViewBuilder media: () -> Media,
        @ViewBuilder actions: () -> Actions
    ) {
        self.title = title
        self.description = description
        self.variant = nil
        self.size = .regular
        self.media = media()
        self.actions = actions()
    }

    public init(
        _ title: String,
        description: String? = nil,
        variant: FoundationItemVariant,
        size: FoundationItemSize = .regular,
        @ViewBuilder media: () -> Media,
        @ViewBuilder actions: () -> Actions
    ) {
        self.title = title
        self.description = description
        self.variant = variant
        self.size = size
        self.media = media()
        self.actions = actions()
    }

    public var body: some View {
        let palette = theme.palette(for: colorScheme)
        let shape = RoundedRectangle(cornerRadius: theme.cornerRadius)

        if theme.preset == .nova || variant != nil {
            let fill: Color = switch variant {
            case nil: palette.card
            case .muted: palette.secondary.opacity(0.5)
            default: .clear
            }
            styledContent(palette: palette)
                .background(shape.fill(fill))
                .overlay(shape.strokeBorder(variant == nil || variant == .outline ? palette.border : .clear)
                    .allowsHitTesting(false))
                .contentShape(Rectangle())
        } else {
            HStack(spacing: 10) {
                media
                textContent(palette: palette, titleFont: .subheadline.weight(.medium), descriptionFont: .caption, spacing: 3)
                Spacer(minLength: 0)
                actions
            }
            .padding(10)
            .background(shape.fill(palette.card))
            .overlay(shape.strokeBorder(palette.border).allowsHitTesting(false))
        }
    }

    private func styledContent(palette: FoundationPalette) -> some View {
        let compact = size == .extraSmall
        let gap: CGFloat = compact ? 8 : 10
        return HStack(alignment: description == nil ? .center : .top, spacing: gap) {
            if Media.self != EmptyView.self {
                media.padding(.top, description == nil ? 0 : 2)
            }
            HStack(spacing: gap) {
                textContent(
                    palette: palette, titleFont: .system(size: textSize, weight: .medium),
                    descriptionFont: .system(size: textSize * (compact ? 12.0 / 14.0 : 1)), spacing: compact ? 0 : 4
                )
                .multilineTextAlignment(.leading)
                .fixedSize(horizontal: false, vertical: true)
                .frame(maxWidth: .infinity, alignment: .leading)
                if Actions.self != EmptyView.self {
                    actions.layoutPriority(1)
                }
            }
        }
        .padding(.horizontal, compact ? 11 : 13)
        .padding(.vertical, compact ? 9 : 11)
    }

    private func textContent(
        palette: FoundationPalette, titleFont: Font, descriptionFont: Font, spacing: CGFloat
    ) -> some View {
        VStack(alignment: .leading, spacing: spacing) {
            Text(title).font(titleFont).foregroundColor(palette.foreground)
            if let description {
                Text(description).font(descriptionFont).foregroundColor(palette.mutedForeground)
            }
        }
    }
}
