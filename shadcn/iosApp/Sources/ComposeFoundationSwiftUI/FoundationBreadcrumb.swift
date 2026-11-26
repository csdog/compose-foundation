import SwiftUI

public struct FoundationBreadcrumb: View {
    public struct Entry: Identifiable {
        public let id: String
        public let title: String
        public let action: (() -> Void)?

        public init(id: String, title: String, action: (() -> Void)? = nil) {
            self.id = id
            self.title = title
            self.action = action
        }
    }

    @Environment(\.foundationTheme) private var theme
    @Environment(\.colorScheme) private var colorScheme
    private let entries: [Entry]

    public init(_ entries: [Entry]) {
        self.entries = entries
    }

    public var body: some View {
        let palette = theme.palette(for: colorScheme)

        ScrollView(.horizontal, showsIndicators: false) {
            HStack(spacing: 8) {
                ForEach(entries) { entry in
                    if entry.id != entries.first?.id {
                        Image(systemName: "chevron.forward")
                            .font(.caption2)
                            .accessibilityHidden(true)
                    }
                    if let action = entry.action, entry.id != entries.last?.id {
                        Button(entry.title, action: action).buttonStyle(PlainButtonStyle())
                    } else {
                        Text(entry.title).foregroundColor(palette.foreground)
                    }
                }
            }
            .font(.subheadline)
            .foregroundColor(palette.mutedForeground)
        }
    }
}
