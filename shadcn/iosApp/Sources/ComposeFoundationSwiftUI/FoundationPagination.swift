import SwiftUI

public struct FoundationPagination: View {
    @Environment(\.foundationTheme) private var theme
    @Environment(\.colorScheme) private var colorScheme
    @Binding private var page: Int

    private let count: Int

    public init(pageCount: Int, page: Binding<Int>) {
        self.count = max(pageCount, 0)
        self._page = page
    }

    public var body: some View {
        if count > 0 {
            let current = min(max(page, 1), count)
            let buttons = pages(around: current)

            ScrollView(.horizontal, showsIndicators: false) {
                HStack(spacing: 2) {
                    control(Image(systemName: "chevron.backward"), page: current - 1, label: "Previous page")
                    ForEach(buttons.indices, id: \.self) { index in
                        let value = buttons[index]
                        if index > 0, value - buttons[index - 1] > 1 {
                            Text("…").padding(.horizontal, 3).accessibilityHidden(true)
                        }
                        control(Text(String(value)), page: value, label: "Page \(value)", selected: value == current)
                    }
                    control(Image(systemName: "chevron.forward"), page: current < count ? current + 1 : nil,
                            label: "Next page")
                }
            }
            .accessibilityElement(children: .contain)
        }
    }

    private func pages(around current: Int) -> [Int] {
        let lower = current > 2 ? current - 2 : 1
        let upper = current < count - 2 ? current + 2 : count
        return Set([1, count] + Array(lower...upper)).sorted()
    }

    private func control<Label: View>(_ content: Label, page value: Int?, label: String, selected: Bool = false) -> some View {
        let palette = theme.palette(for: colorScheme)
        let target = value.flatMap { (1...count).contains($0) ? $0 : nil }

        return Button {
            if let target { page = target }
        } label: {
            content
                .font(.subheadline.monospacedDigit())
                .foregroundColor(selected ? palette.primaryForeground : palette.foreground)
                .frame(minWidth: 44, minHeight: 44)
                .background(RoundedRectangle(cornerRadius: theme.cornerRadius).fill(selected ? palette.primary : .clear))
                .contentShape(Rectangle())
        }
        .buttonStyle(PlainButtonStyle())
        .disabled(target == nil)
        .opacity(target == nil ? 0.4 : 1)
        .accessibilityLabel(Text(label))
        .accessibilityAddTraits(selected ? .isSelected : [])
    }
}
