import SwiftUI

public enum FoundationAvatarSize: Sendable {
    case small
    case regular
    case large

    fileprivate var diameter: CGFloat {
        switch self {
        case .small: 24
        case .regular: 32
        case .large: 40
        }
    }

    fileprivate var badgeDiameter: CGFloat {
        switch self {
        case .small: 8
        case .regular: 10
        case .large: 12
        }
    }
}

public struct FoundationAvatar: View {
    public struct Badge {
        public let label: String
        public let color: Color?
        public let symbol: String?

        public init(_ label: String, color: Color? = nil, symbol: String? = nil) {
            self.label = label
            self.color = color
            self.symbol = symbol
        }
    }

    @Environment(\.foundationTheme) private var theme
    @Environment(\.colorScheme) private var colorScheme
    @Environment(\.foundationAvatarGrouped) private var grouped
    @ScaledMetric(relativeTo: .caption) private var regularDiameter: CGFloat = 32

    private let name: String
    private let image: Image?
    private let imageURL: URL?
    private let size: FoundationAvatarSize
    private let fallback: String?
    private let badge: Badge?

    public init(_ name: String, image: Image? = nil) {
        self.init(name, image: image, size: .regular)
    }

    public init(
        _ name: String, image: Image? = nil, size: FoundationAvatarSize, fallback: String? = nil, badge: Badge? = nil
    ) {
        self.name = name
        self.image = image
        self.imageURL = nil
        self.size = size
        self.fallback = fallback
        self.badge = badge
    }

    @available(iOS 15, macOS 12, *)
    public init(
        _ name: String, imageURL: URL?, size: FoundationAvatarSize = .regular, fallback: String? = nil, badge: Badge? = nil
    ) {
        self.name = name
        self.image = nil
        self.imageURL = imageURL
        self.size = size
        self.fallback = fallback
        self.badge = badge
    }

    public var body: some View {
        let palette = theme.palette(for: colorScheme)
        Group {
            if #available(iOS 15, macOS 12, *), let imageURL {
                AsyncImage(url: imageURL) { phase in
                    avatar(image: phase.image, palette: palette)
                }
            } else {
                avatar(image: image, palette: palette)
            }
        }
        .accessibilityElement(children: .ignore)
        .accessibilityLabel(Text(name))
        .accessibilityValue(Text(badge?.label ?? ""))
    }

    private func avatar(image: Image?, palette: FoundationPalette) -> some View {
        let nova = theme.preset == .nova
        let scale = regularDiameter / 32
        let diameter = size.diameter * scale
        return ZStack {
            Circle().fill(palette.secondary)
            if let image {
                image.resizable().scaledToFill()
            } else {
                Text(fallback ?? initials)
                    .font(nova ? .system(size: (size == .small ? 12 : 14) * scale) : .caption.weight(.semibold))
                    .foregroundColor(nova ? palette.mutedForeground : palette.secondaryForeground)
                    .lineLimit(1)
            }
        }
        .frame(width: diameter, height: diameter)
        .clipShape(Circle())
        .overlay(Circle().strokeBorder(nova ? palette.border : .clear))
        .overlay(Circle().strokeBorder(grouped ? palette.background : .clear, lineWidth: 2).padding(-2))
        .overlay(BadgeView(badge: badge, size: size, scale: scale, palette: palette), alignment: .bottomTrailing)
    }

    private var initials: String {
        name.split(whereSeparator: \.isWhitespace).prefix(2).compactMap(\.first).map(String.init).joined().uppercased()
    }

    private struct BadgeView: View {
        let badge: Badge?
        let size: FoundationAvatarSize
        let scale: CGFloat
        let palette: FoundationPalette

        var body: some View {
            if let badge {
                let diameter = size.badgeDiameter * scale
                ZStack {
                    Circle().fill(badge.color ?? palette.primary)
                    if let symbol = badge.symbol, size != .small {
                        Image(systemName: symbol)
                            .font(.system(size: 8 * scale))
                            .foregroundColor(palette.primaryForeground)
                    }
                }
                .frame(width: diameter, height: diameter)
                .overlay(Circle().strokeBorder(palette.background, lineWidth: 2).padding(-2))
                .accessibilityHidden(true)
            }
        }
    }
}

public struct FoundationAvatarGroup<Content: View>: View {
    private let content: Content

    public init(@ViewBuilder content: () -> Content) {
        self.content = content()
    }

    public var body: some View {
        HStack(spacing: -8) { content }
            .environment(\.foundationAvatarGrouped, true)
    }
}

public struct FoundationAvatarGroupCount: View {
    @Environment(\.foundationTheme) private var theme
    @Environment(\.colorScheme) private var colorScheme
    @ScaledMetric(relativeTo: .caption) private var regularDiameter: CGFloat = 32
    private let count: Int
    private let size: FoundationAvatarSize

    public init(_ count: Int, size: FoundationAvatarSize = .regular) {
        self.count = count
        self.size = size
    }

    public var body: some View {
        let palette = theme.palette(for: colorScheme)
        let scale = regularDiameter / 32
        let diameter = size.diameter * scale
        return Text("+\(count)")
            .font(.system(size: 14 * scale))
            .lineLimit(1)
            .minimumScaleFactor(0.5)
            .foregroundColor(palette.mutedForeground)
            .frame(width: diameter, height: diameter)
            .background(Circle().fill(palette.secondary))
            .overlay(Circle().strokeBorder(palette.background, lineWidth: 2).padding(-2))
    }
}

private struct FoundationAvatarGroupedKey: EnvironmentKey {
    static let defaultValue = false
}

private extension EnvironmentValues {
    var foundationAvatarGrouped: Bool {
        get { self[FoundationAvatarGroupedKey.self] }
        set { self[FoundationAvatarGroupedKey.self] = newValue }
    }
}
