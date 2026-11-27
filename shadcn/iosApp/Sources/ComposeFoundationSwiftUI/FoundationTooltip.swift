import SwiftUI

public struct FoundationTooltip: ViewModifier {
    @Environment(\.foundationTheme) private var theme
    @Environment(\.colorScheme) private var colorScheme
    @Environment(\.layoutDirection) private var direction
    @Environment(\.isEnabled) private var isEnabled
    @State private var isVisible = false
    @StateObject private var interaction: FoundationTooltipInteraction
    @ScaledMetric(relativeTo: .caption) private var novaFontSize: CGFloat = 12

    private let title: String
    private let side: Edge
    private let delay: UInt64
    private let presentation: Binding<Bool>?
    private let usesSystemHelp: Bool

    public init(_ title: String) {
        self.init(title, presentation: nil, side: .top, delay: 0, usesSystemHelp: true)
    }

    /// Places the hint on a logical edge. The hover delay is in seconds.
    public init(_ title: String, side: Edge, delay: TimeInterval = 0) {
        self.init(title, presentation: nil, side: side, delay: delay)
    }

    /// Binds the visible state so the caller can also present a hint from keyboard focus.
    public init(_ title: String, isPresented: Binding<Bool>, side: Edge = .top, delay: TimeInterval = 0) {
        self.init(title, presentation: isPresented, side: side, delay: delay)
    }

    init(_ title: String, presentation: Binding<Bool>?, side: Edge, delay: TimeInterval,
         usesSystemHelp: Bool = false, interaction: FoundationTooltipInteraction = FoundationTooltipInteraction()) {
        self.title = title
        self.presentation = presentation
        self.side = side
        let seconds = delay.isFinite ? min(max(0, delay), Double(UInt64.max / 1_000_000_000)) : 0
        self.delay = UInt64(seconds * 1_000_000_000)
        self.usesSystemHelp = usesSystemHelp
        self._interaction = StateObject(wrappedValue: interaction)
    }

    @ViewBuilder
    public func body(content: Content) -> some View {
        #if os(macOS)
        if theme.preset != .nova && usesSystemHelp {
            content.help(title)
        } else {
            hint(around: content)
        }
        #else
        hint(around: content)
        #endif
    }

    @ViewBuilder
    private func hint(around content: Content) -> some View {
        let presented = presentation ?? $isVisible
        let positionedBubble = bubble
            .fixedSize()
            .accessibilityHidden(true)
            .allowsHitTesting(false)
            .opacity(presented.wrappedValue && isEnabled ? 1 : 0)
            .alignmentGuide(.top) { $0[.bottom] }
            .alignmentGuide(.bottom) { $0[.top] }
            .alignmentGuide(.leading) { $0[.trailing] }
            .alignmentGuide(.trailing) { $0[.leading] }

        let trigger = content
            .accessibilityHint(Text(title))
            .onHover { interaction.pointerChanged($0 && isEnabled, presented: presented, delay: delay) }
            .overlay(positionedBubble, alignment: alignment)
            .onChange(of: isEnabled) { enabled in
                if !enabled { interaction.pointerChanged(false, presented: presented, delay: 0) }
            }
            .onDisappear { interaction.cancel() }
        #if os(macOS)
        trigger.onExitCommand { interaction.pointerChanged(false, presented: presented, delay: 0) }
        #else
        trigger.simultaneousGesture(LongPressGesture().onEnded { _ in
            if isEnabled { interaction.longPressed(presented: presented) }
        })
        #endif
    }

    private var alignment: Alignment {
        switch side {
        case .top: return .top
        case .bottom: return .bottom
        case .leading: return .leading
        case .trailing: return .trailing
        }
    }

    @ViewBuilder
    private var bubble: some View {
        let palette = theme.palette(for: colorScheme)
        let isNova = theme.preset == .nova
        let label = Text(title)
            .font(isNova ? .system(size: novaFontSize) : .caption)
            .multilineTextAlignment(.center)
            .foregroundColor(palette.background)
            .padding(.horizontal, isNova ? 12 : 7)
            .padding(.vertical, isNova ? 6 : 7)
            .frame(maxWidth: 288)
            .fixedSize(horizontal: true, vertical: true)
            .background(RoundedRectangle(cornerRadius: 6).fill(palette.foreground))
        if isNova {
            let vertical = side == .top || side == .bottom
            let arrow = TooltipArrow(side: side, direction: direction)
                .fill(palette.foreground)
                .frame(width: vertical ? 10 : 4, height: vertical ? 4 : 10)
            switch side {
            case .top:
                VStack(spacing: 0) {
                    label
                    arrow
                }
            case .bottom:
                VStack(spacing: 0) {
                    arrow
                    label
                }
            case .leading:
                HStack(spacing: 0) {
                    label
                    arrow
                }
            case .trailing:
                HStack(spacing: 0) {
                    arrow
                    label
                }
            }
        } else {
            label.padding(oppositeEdge, 6)
        }
    }

    private var oppositeEdge: Edge.Set {
        switch side {
        case .top: return .bottom
        case .bottom: return .top
        case .leading: return .trailing
        case .trailing: return .leading
        }
    }
}

private struct TooltipArrow: Shape {
    let side: Edge
    let direction: LayoutDirection

    func path(in rect: CGRect) -> Path {
        let origin = rect.origin
        let topRight = CGPoint(x: rect.maxX, y: rect.minY)
        let bottomLeft = CGPoint(x: rect.minX, y: rect.maxY)
        let bottomRight = CGPoint(x: rect.maxX, y: rect.maxY)
        var path = Path()
        switch side {
        case .top:
            path.move(to: origin)
            path.addLine(to: topRight)
            path.addLine(to: CGPoint(x: rect.midX, y: rect.maxY))
        case .bottom:
            path.move(to: bottomLeft)
            path.addLine(to: bottomRight)
            path.addLine(to: CGPoint(x: rect.midX, y: rect.minY))
        case .leading, .trailing:
            let pointsRight = (side == .leading) == (direction == .leftToRight)
            path.move(to: pointsRight ? origin : topRight)
            path.addLine(to: pointsRight ? bottomLeft : bottomRight)
            path.addLine(to: CGPoint(x: pointsRight ? rect.maxX : rect.minX, y: rect.midY))
        }
        path.closeSubpath()
        return path
    }
}

final class FoundationTooltipInteraction: ObservableObject {
    private var transition: Task<Void, Never>?

    @MainActor
    func pointerChanged(_ inside: Bool, presented: Binding<Bool>, delay: UInt64) {
        cancel()
        if inside && delay > 0 {
            schedule(true, presented: presented, delay: delay)
        } else if presented.wrappedValue != inside {
            presented.wrappedValue = inside
        }
    }

    @MainActor
    func longPressed(presented: Binding<Bool>) {
        presented.wrappedValue = true
        schedule(false, presented: presented, delay: 2_000_000_000)
    }

    @MainActor
    func cancel() {
        transition?.cancel()
        transition = nil
    }

    @MainActor
    private func schedule(_ show: Bool, presented: Binding<Bool>, delay: UInt64) {
        cancel()
        transition = Task { @MainActor [weak self] in
            do { try await Task.sleep(nanoseconds: delay) } catch { return }
            self?.transition = nil
            presented.wrappedValue = show
        }
    }
}
