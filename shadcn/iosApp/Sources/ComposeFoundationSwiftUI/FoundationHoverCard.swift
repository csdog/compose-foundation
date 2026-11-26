import SwiftUI

public struct FoundationHoverCard<Card: View>: ViewModifier {
    @Environment(\.foundationTheme) private var theme
    @Environment(\.colorScheme) private var colorScheme
    @State private var isPresented = false
    @State private var openedByHover = false
    @State private var hoverTransition: Task<Void, Never>?

    private let card: () -> Card

    public init(@ViewBuilder card: @escaping () -> Card) {
        self.card = card
    }

    public func body(content: Content) -> some View {
        content
            .onHover { inside in
                if inside {
                    hoverTransition?.cancel()
                    if !isPresented { schedulePresentation(true) }
                } else {
                    endHover()
                }
            }
            .simultaneousGesture(LongPressGesture().onEnded { _ in
                hoverTransition?.cancel()
                openedByHover = false
                isPresented = true
            })
            .popover(isPresented: $isPresented) {
                let palette = theme.palette(for: colorScheme)
                card().padding(12).foregroundColor(palette.foreground).background(palette.card)
                    .onHover { inside in
                        if inside {
                            hoverTransition?.cancel()
                        } else {
                            endHover()
                        }
                    }
            }
            .onDisappear { hoverTransition?.cancel() }
    }

    private func endHover() {
        hoverTransition?.cancel()
        if isPresented && openedByHover { schedulePresentation(false) }
    }

    private func schedulePresentation(_ presented: Bool) {
        hoverTransition = Task { @MainActor in
            try? await Task.sleep(nanoseconds: presented ? 250_000_000 : 180_000_000)
            guard !Task.isCancelled else { return }
            openedByHover = presented
            isPresented = presented
        }
    }
}
