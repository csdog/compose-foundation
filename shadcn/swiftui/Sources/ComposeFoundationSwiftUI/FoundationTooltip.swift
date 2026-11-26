import SwiftUI

public struct FoundationTooltip: ViewModifier {
    @Environment(\.foundationTheme) private var theme
    @Environment(\.colorScheme) private var colorScheme
    @State private var isVisible = false
    @State private var dismissal: Task<Void, Never>?

    private let title: String

    public init(_ title: String) {
        self.title = title
    }

    public func body(content: Content) -> some View {
        #if os(macOS)
        content.help(title)
        #else
        content
            .accessibilityHint(Text(title))
            .simultaneousGesture(LongPressGesture().onEnded { _ in
                dismissal?.cancel()
                isVisible = true
                dismissal = Task { @MainActor in
                    try? await Task.sleep(nanoseconds: 2_000_000_000)
                    guard !Task.isCancelled else { return }
                    isVisible = false
                }
            })
            .overlay(Group {
                if isVisible {
                    let palette = theme.palette(for: colorScheme)
                    Text(title)
                        .font(.caption)
                        .padding(7)
                        .foregroundColor(palette.background)
                        .background(RoundedRectangle(cornerRadius: 6).fill(palette.foreground))
                        .alignmentGuide(.top) { dimensions in dimensions[.bottom] + 6 }
                        .accessibilityHidden(true)
                        .allowsHitTesting(false)
                }
            }, alignment: .top)
            .onDisappear { dismissal?.cancel() }
        #endif
    }
}
