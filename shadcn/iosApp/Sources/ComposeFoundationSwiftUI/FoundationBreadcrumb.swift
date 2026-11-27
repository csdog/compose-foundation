import SwiftUI

public struct FoundationBreadcrumb: View {
    public struct MenuItem: Identifiable {
        public let id: String
        public let title: String
        public let isEnabled: Bool
        public let action: () -> Void

        public init(id: String, title: String, isEnabled: Bool = true, action: @escaping () -> Void) {
            self.id = id
            self.title = title
            self.isEnabled = isEnabled
            self.action = action
        }
    }

    public struct Entry: Identifiable {
        public let id: String
        public let title: String
        public let action: (() -> Void)?
        fileprivate let omittedItems: [MenuItem]?

        public init(id: String, title: String, action: (() -> Void)? = nil) {
            self.id = id
            self.title = title
            self.action = action
            self.omittedItems = nil
        }

        /// Use an empty list for a static indicator, or supply actions for a system menu.
        public static func ellipsis(id: String, title: String, items: [MenuItem] = []) -> Self {
            Self(id: id, title: title, omittedItems: items)
        }

        private init(id: String, title: String, omittedItems: [MenuItem]) {
            self.id = id
            self.title = title
            self.action = nil
            self.omittedItems = omittedItems
        }
    }

    @Environment(\.foundationTheme) private var theme
    @Environment(\.colorScheme) private var colorScheme
    @ScaledMetric(relativeTo: .subheadline) private var novaTextSize: CGFloat = 14
    private let entries: [Entry]
    private let separatorSymbol: String
    private let separatorSize: CGFloat?

    public init(_ entries: [Entry]) {
        self.init(entries, separatorSymbol: "chevron.forward")
    }

    public init(_ entries: [Entry], separatorSymbol: String, separatorSize: CGFloat? = nil) {
        self.entries = entries
        self.separatorSymbol = separatorSymbol
        self.separatorSize = separatorSize
    }

    public var body: some View {
        let palette = theme.palette(for: colorScheme)

        ScrollView(.horizontal, showsIndicators: false) {
            HStack(spacing: theme.preset == .nova ? 6 : 8) {
                ForEach(entries) { entry in
                    if entry.id != entries.first?.id {
                        Image(systemName: separatorSymbol)
                            .font(separatorSize.map { .system(size: $0) } ?? (theme.preset == .nova ? .system(size: 11) : .caption2))
                            .frame(width: theme.preset == .nova ? 14 : nil)
                            .accessibilityHidden(true)
                    }
                    crumb(entry, palette: palette)
                }
            }
            .font(theme.preset == .nova ? .system(size: novaTextSize) : .subheadline)
            .foregroundColor(palette.mutedForeground)
        }
    }

    @ViewBuilder
    private func crumb(_ entry: Entry, palette: FoundationPalette) -> some View {
        let isCurrent = entry.id == entries.last?.id
        if let items = entry.omittedItems {
            if items.isEmpty {
                ellipsisIcon.accessibilityLabel(Text(entry.title))
            } else {
                let menu = Menu {
                    ForEach(items) { item in
                        Button(item.title, action: item.action).disabled(!item.isEnabled)
                    }
                } label: {
                    ellipsisIcon
                        .frame(minWidth: 44, minHeight: 44)
                        .contentShape(Rectangle())
                }
                .accessibilityLabel(Text(entry.title))
                #if os(macOS)
                if #available(macOS 12, *) {
                    menu.menuStyle(BorderlessButtonMenuStyle()).menuIndicator(.hidden)
                } else {
                    menu.menuStyle(BorderlessButtonMenuStyle())
                }
                #else
                menu
                #endif
            }
        } else if let action = entry.action, !isCurrent {
            Button(action: action) {
                Text(entry.title)
                    .frame(minWidth: theme.preset == .nova ? 44 : nil, minHeight: theme.preset == .nova ? 44 : nil)
                    .contentShape(Rectangle())
            }
            .buttonStyle(PlainButtonStyle())
        } else {
            Text(entry.title)
                .foregroundColor(palette.foreground)
                .accessibilityValue(isCurrent ? Text("Current page") : Text(""))
        }
    }

    private var ellipsisIcon: some View {
        Image(systemName: "ellipsis")
            .font(.system(size: 14))
            .frame(width: 20, height: 20)
    }
}
