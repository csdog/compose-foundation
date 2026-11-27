import SwiftUI

public enum FoundationPaginationControls: Sendable {
    case icons
    case labels(previous: String, next: String)
    case none
}

public enum FoundationPaginationWindow: Sendable {
    /// Keep the first and last pages visible, with up to two pages on each side of the current page.
    case standard
    /// Show up to three consecutive pages and an ellipsis on each side with omitted pages.
    case compact
}

public struct FoundationPagination: View {
    @Environment(\.foundationTheme) private var theme
    @Environment(\.colorScheme) private var colorScheme
    @Environment(\.locale) private var locale
    @Binding private var page: Int

    private let count: Int
    private let controls: FoundationPaginationControls
    private let window: FoundationPaginationWindow

    public init(pageCount: Int, page: Binding<Int>) {
        self.init(pageCount: pageCount, page: page, controls: .icons)
    }

    public init(
        pageCount: Int, page: Binding<Int>, controls: FoundationPaginationControls,
        window: FoundationPaginationWindow = .standard
    ) {
        self.count = max(pageCount, 0)
        self._page = page
        self.controls = controls
        self.window = window
    }

    public var body: some View {
        if count > 0 {
            let current = min(max(page, 1), count)
            Group {
                if #available(iOS 16, macOS 13, *), theme.preset == .nova {
                    ViewThatFits(in: .horizontal) {
                        row(current: current)
                        ScrollView(.horizontal, showsIndicators: false) { row(current: current) }
                    }
                } else {
                    ScrollView(.horizontal, showsIndicators: false) { row(current: current) }
                }
            }
            .accessibilityElement(children: .contain)
        }
    }

    private func row(current: Int) -> some View {
        let pages = pages(around: current)
        return HStack(spacing: 2) {
            step(current > 1 ? current - 1 : nil, previous: true)
            if let first = pages.first, first > 1 { ellipsis }
            ForEach(pages.indices, id: \.self) { index in
                let value = pages[index]
                if index > 0, value - pages[index - 1] > 1 { ellipsis }
                control(Text(pageTitle(value)), target: value, label: Text("Page \(value)"), selected: value == current)
            }
            if let last = pages.last, last < count { ellipsis }
            step(current < count ? current + 1 : nil, previous: false)
        }
    }

    private func pages(around current: Int) -> [Int] {
        switch window {
        case .standard:
            let lower = max(1, current - 2)
            let upper = current + min(2, count - current)
            return Set([1, count] + Array(lower...upper)).sorted()
        case .compact:
            let lower = min(max(1, current - 1), max(1, count - 2))
            return Array(lower...(lower + min(2, count - lower)))
        }
    }

    private func pageTitle(_ value: Int) -> String {
        guard theme.preset == .nova else { return String(value) }
        let formatter = NumberFormatter()
        formatter.locale = locale
        formatter.numberStyle = .decimal
        formatter.usesGroupingSeparator = false
        return formatter.string(from: NSNumber(value: value)) ?? String(value)
    }

    @ViewBuilder
    private var ellipsis: some View {
        if theme.preset == .nova {
            Image(systemName: "ellipsis").font(.system(size: 13)).frame(width: 32, height: 32)
                .foregroundColor(theme.palette(for: colorScheme).foreground)
                .accessibilityHidden(true)
        } else {
            Text("…").padding(.horizontal, 3).accessibilityHidden(true)
        }
    }

    @ViewBuilder
    private func step(_ target: Int?, previous: Bool) -> some View {
        let label = previous ? Text("Previous page") : Text("Next page")
        let icon = Image(systemName: previous ? "chevron.backward" : "chevron.forward")
        switch controls {
        case .none:
            EmptyView()
        case .icons:
            control(icon.font(theme.preset == .nova ? .system(size: 12) : nil), target: target, label: label)
        case let .labels(previousTitle, nextTitle):
            control(HStack(spacing: 6) {
                if previous { icon.font(.system(size: 11)) }
                Text(previous ? previousTitle : nextTitle)
                if !previous { icon.font(.system(size: 11)) }
            }, target: target, label: label, iconSize: false)
        }
    }

    @ViewBuilder
    private func control<Label: View>(
        _ content: Label, target: Int?, label: Text, selected: Bool = false, iconSize: Bool = true
    ) -> some View {
        let palette = theme.palette(for: colorScheme)
        let button = Button {
            if let target { page = target }
        } label: {
            if theme.preset == .nova {
                content
            } else {
                content
                    .font(.subheadline.monospacedDigit())
                    .foregroundColor(selected ? palette.primaryForeground : palette.foreground)
                    .frame(minWidth: 44, minHeight: 44)
                    .background(RoundedRectangle(cornerRadius: theme.cornerRadius).fill(selected ? palette.primary : .clear))
                    .contentShape(Rectangle())
            }
        }
        .disabled(target == nil)
        .accessibilityLabel(label)
        .accessibilityAddTraits(selected ? .isSelected : [])

        if theme.preset == .nova {
            button.buttonStyle(FoundationButtonStyle(selected ? .outline : .ghost, size: iconSize ? .icon : .regular))
        } else {
            button.buttonStyle(PlainButtonStyle()).opacity(target == nil ? 0.4 : 1)
        }
    }
}
