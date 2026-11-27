import SwiftUI

public struct FoundationHoverCard<Card: View>: ViewModifier {
    @Environment(\.foundationTheme) private var theme
    @Environment(\.colorScheme) private var colorScheme
    @State private var isPresented = false
    @State private var openedByHover = false
    @State private var hoverTransition: Task<Void, Never>?

    private let presentation: Binding<Bool>?
    private let width: CGFloat?
    private let card: () -> Card

    public init(@ViewBuilder card: @escaping () -> Card) {
        self.presentation = nil
        self.width = nil
        self.card = card
    }

    /// Uses caller-controlled presentation. Width includes the card's padding.
    public init(isPresented: Binding<Bool>, width: CGFloat? = nil, @ViewBuilder card: @escaping () -> Card) {
        self.presentation = isPresented
        self.width = width
        self.card = card
    }

    public func body(content: Content) -> some View {
        let presented = presentation ?? $isPresented
        let trigger = content
            .onHover { hover($0, isTrigger: true, presented: presented) }
            .simultaneousGesture(LongPressGesture().onEnded { _ in
                hoverTransition?.cancel()
                openedByHover = false
                presented.wrappedValue = true
            })
            .onDisappear { hoverTransition?.cancel() }

        if theme.preset == .nova {
            trigger.modifier(FoundationPopover(
                isPresented: presented, width: width ?? 256, alignmentOffset: 4, takesFocus: !openedByHover,
                onPanelHover: { hover($0, isTrigger: false, presented: presented) },
                onDismiss: {
                    hoverTransition?.cancel()
                    openedByHover = false
                }, content: card
            ))
        } else {
            trigger.popover(isPresented: presented) {
                let palette = theme.palette(for: colorScheme)
                card().padding(12).foregroundColor(palette.foreground).background(palette.card)
                    .frame(width: width)
                    .onHover { hover($0, isTrigger: false, presented: presented) }
            }
        }
    }

    private func hover(_ inside: Bool, isTrigger: Bool, presented: Binding<Bool>) {
        hoverTransition?.cancel()
        if inside {
            if isTrigger && !presented.wrappedValue { schedulePresentation(true, presented: presented) }
        } else if presented.wrappedValue && openedByHover {
            schedulePresentation(false, presented: presented)
        }
    }

    private func schedulePresentation(_ show: Bool, presented: Binding<Bool>) {
        let delay: UInt64 = show ? 250_000_000 : theme.preset == .nova ? 100_000_000 : 180_000_000
        hoverTransition = Task { @MainActor in
            try? await Task.sleep(nanoseconds: delay)
            guard !Task.isCancelled else { return }
            if show || theme.preset != .nova { openedByHover = show }
            presented.wrappedValue = show
        }
    }
}
