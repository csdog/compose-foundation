import SwiftUI

public struct FoundationHoverCard<Card: View>: ViewModifier {
    @Environment(\.foundationTheme) private var theme
    @Environment(\.colorScheme) private var colorScheme
    @State private var isPresented = false
    @StateObject private var interaction: FoundationHoverCardInteraction

    private let presentation: Binding<Bool>?
    private let width: CGFloat?
    private let card: () -> Card

    public init(@ViewBuilder card: @escaping () -> Card) {
        self.init(presentation: nil, width: nil, interaction: FoundationHoverCardInteraction(), card: card)
    }

    /// Uses caller-controlled presentation. Width includes the card's padding.
    public init(isPresented: Binding<Bool>, width: CGFloat? = nil, @ViewBuilder card: @escaping () -> Card) {
        self.init(presentation: isPresented, width: width, interaction: FoundationHoverCardInteraction(), card: card)
    }

    init(presentation: Binding<Bool>?, width: CGFloat? = nil, interaction: FoundationHoverCardInteraction,
         @ViewBuilder card: @escaping () -> Card) {
        self.presentation = presentation
        self.width = width
        self._interaction = StateObject(wrappedValue: interaction)
        self.card = card
    }

    public func body(content: Content) -> some View {
        let presented = presentation ?? $isPresented
        let delay: UInt64 = theme.preset == .nova ? 100_000_000 : 180_000_000
        let trigger = content
            .onHover { interaction.pointerChanged($0, isTrigger: true, presented: presented, closeDelay: delay) }
            .simultaneousGesture(LongPressGesture().onEnded { _ in interaction.openFromTouch(presented) })
            .onDisappear { interaction.cancel() }

        if theme.preset == .nova {
            trigger.modifier(FoundationPopover(
                isPresented: presented, width: width ?? 256, alignmentOffset: 4, takesFocus: !interaction.openedByHover,
                onPanelHover: { interaction.pointerChanged($0, isTrigger: false, presented: presented, closeDelay: delay) },
                onDismiss: { interaction.reset() }, content: card
            ))
        } else {
            trigger.popover(isPresented: presented) {
                let palette = theme.palette(for: colorScheme)
                card().padding(12).foregroundColor(palette.foreground).background(palette.card)
                    .frame(width: width)
                    .onHover { interaction.pointerChanged($0, isTrigger: false, presented: presented, closeDelay: delay) }
            }
        }
    }
}

final class FoundationHoverCardInteraction: ObservableObject {
    @Published private(set) var openedByHover = false
    private var transition: Task<Void, Never>?

    @MainActor
    func pointerChanged(_ inside: Bool, isTrigger: Bool, presented: Binding<Bool>, closeDelay: UInt64) {
        cancel()
        if inside {
            if isTrigger && !presented.wrappedValue { schedule(true, presented: presented, delay: 250_000_000) }
        } else if presented.wrappedValue && openedByHover {
            schedule(false, presented: presented, delay: closeDelay)
        }
    }

    @MainActor
    func openFromTouch(_ presented: Binding<Bool>) {
        reset()
        presented.wrappedValue = true
    }

    @MainActor
    func reset() {
        cancel()
        if openedByHover { openedByHover = false }
    }

    @MainActor
    func cancel() {
        transition?.cancel()
        transition = nil
    }

    @MainActor
    private func schedule(_ show: Bool, presented: Binding<Bool>, delay: UInt64) {
        transition = Task { @MainActor [weak self] in
            do { try await Task.sleep(nanoseconds: delay) } catch { return }
            if show { self?.openedByHover = true }
            presented.wrappedValue = show
            if !show { self?.openedByHover = false }
            self?.transition = nil
        }
    }
}
