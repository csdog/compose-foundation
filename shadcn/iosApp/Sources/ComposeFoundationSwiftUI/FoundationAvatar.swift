import SwiftUI

public struct FoundationAvatar: View {
    @Environment(\.foundationTheme) private var theme
    @Environment(\.colorScheme) private var colorScheme
    @ScaledMetric(relativeTo: .caption) private var size: CGFloat = 32

    private let name: String
    private let image: Image?

    public init(_ name: String, image: Image? = nil) {
        self.name = name
        self.image = image
    }

    public var body: some View {
        let palette = theme.palette(for: colorScheme)

        ZStack {
            Circle().fill(palette.secondary)
            if let image {
                image.resizable().scaledToFill()
            } else {
                Text(initials)
                    .font(.caption.weight(.semibold))
                    .foregroundColor(palette.secondaryForeground)
                    .lineLimit(1)
            }
        }
        .frame(width: size, height: size)
        .clipShape(Circle())
        .accessibilityElement(children: .ignore)
        .accessibilityLabel(Text(name))
    }

    private var initials: String {
        name.split(whereSeparator: \.isWhitespace).prefix(2).compactMap(\.first).map(String.init).joined().uppercased()
    }
}
