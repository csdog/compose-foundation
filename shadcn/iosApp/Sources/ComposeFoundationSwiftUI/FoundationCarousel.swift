import SwiftUI
#if os(iOS)
import UIKit
#endif

public enum FoundationCarouselNavigationPlacement: Sendable {
    case below, sides
}

public struct FoundationCarousel<Page: View>: View {
    @Environment(\.foundationTheme) private var theme
    @Environment(\.colorScheme) private var colorScheme
    @Environment(\.layoutDirection) private var layoutDirection
    @Environment(\.accessibilityReduceMotion) private var reduceMotion
    @Binding private var selection: Int
    @GestureState private var tracking = false
    @State private var width: CGFloat = 0
    @State private var drag: PageDrag?

    private let pageCount: Int
    private let navigation: FoundationCarouselNavigationPlacement
    private let page: (Int) -> Page

    private struct PageDrag {
        let index: Int
        let horizontal: Bool
        var translation: CGFloat = 0
    }

    public init(pageCount: Int, selection: Binding<Int>, @ViewBuilder page: @escaping (Int) -> Page) {
        self.init(pageCount: pageCount, selection: selection, navigation: .below, page: page)
    }

    public init(
        pageCount: Int, selection: Binding<Int>, navigation: FoundationCarouselNavigationPlacement,
        @ViewBuilder page: @escaping (Int) -> Page
    ) {
        self.pageCount = max(0, pageCount)
        self._selection = selection
        self.navigation = navigation
        self.page = page
    }

    public var body: some View {
        if pageCount > 0 {
            let palette = theme.palette(for: colorScheme)
            let index = currentPage

            if navigation == .sides {
                HStack(spacing: 12) {
                    arrow("chevron.backward", label: "Previous", change: -1, enabled: index > 0)
                    viewport(at: index)
                        .accessibilityElement(children: .contain)
                        .accessibilityLabel(Text("Page \(index + 1) of \(pageCount)"))
                        .accessibilityAdjustableAction(adjustPage)
                    arrow("chevron.forward", label: "Next", change: 1, enabled: index + 1 < pageCount)
                }
            } else {
                VStack(spacing: 8) {
                    viewport(at: index)
                    HStack(spacing: 8) {
                        arrow("chevron.backward", label: "Previous", change: -1, enabled: index > 0)
                        Text("\(index + 1) / \(pageCount)")
                            .font(.caption.monospacedDigit())
                            .foregroundColor(palette.mutedForeground)
                            .accessibilityLabel(Text("Page \(index + 1) of \(pageCount)"))
                            .accessibilityAdjustableAction(adjustPage)
                        arrow("chevron.forward", label: "Next", change: 1, enabled: index + 1 < pageCount)
                    }
                }
            }
        }
    }

    private var currentPage: Int { min(max(0, selection), pageCount - 1) }
    private var direction: CGFloat { layoutDirection == .leftToRight ? 1 : -1 }
    private var settleAnimation: Animation? {
        reduceMotion ? nil : .easeOut(duration: theme.preset?.metrics.transition ?? 0.24)
    }

    private func viewport(at index: Int) -> some View {
        let visible = max(0, index - 1)...min(pageCount - 1, index + 1)
        let translation = drag?.index == index ? drag?.translation ?? 0 : 0
        let slides = ZStack(alignment: .top) {
            ForEach(visible, id: \.self) { item in
                page(item)
                    .frame(maxWidth: .infinity)
                    .layoutPriority(item == index ? 1 : 0)
                    // SwiftUI mirrors the offset in RTL. Convert the physical drag to its logical axis.
                    .offset(x: CGFloat(item - index) * width + translation * direction)
                    .opacity(item == index || width > 0 ? 1 : 0)
                    .accessibilityHidden(item != index)
                    .allowsHitTesting(item == index)
                    .transition(.identity)
            }
        }
        .background(GeometryReader { geometry in
            Color.clear
                .onAppear { width = geometry.size.width }
                .foundationOnChange(of: geometry.size.width) { width = $0 }
        })
        .clipped()
        .contentShape(Rectangle())
        return dragSurface(slides, at: index)
            .foundationOnChange(of: tracking) { isTracking in
                if !isTracking, drag != nil { move(by: 0) }
            }
            .animation(settleAnimation, value: index)
    }

    @ViewBuilder
    private func dragSurface<Slides: View>(_ slides: Slides, at index: Int) -> some View {
        #if os(iOS)
        if #available(iOS 18, *) {
            slides.gesture(FoundationCarouselPan(
                changed: { updateDrag(at: index, translation: $0) },
                ended: { finishDrag(at: index, projected: $0) },
                cancelled: { move(by: 0) }
            ))
        } else {
            slides.simultaneousGesture(pageDrag(at: index))
        }
        #else
        slides.simultaneousGesture(pageDrag(at: index))
        #endif
    }

    private func pageDrag(at index: Int) -> some Gesture {
        // Use viewport coordinates so moving a slide cannot change the drag's origin.
        DragGesture(minimumDistance: 12)
            .updating($tracking) { _, tracking, _ in tracking = true }
            .onChanged { value in
                updateDrag(at: index, translation: value.translation)
            }
            .onEnded { value in
                finishDrag(at: index, projected: value.predictedEndTranslation.width)
            }
    }

    private func updateDrag(at index: Int, translation: CGSize) {
        guard translation != .zero || drag != nil else { return }
        if drag == nil {
            drag = PageDrag(index: index, horizontal: abs(translation.width) > abs(translation.height))
        }
        guard drag?.horizontal == true, drag?.index == index, width > 0 else { return }
        let displacement = translation.width
        let delta = displacement * direction < 0 ? 1 : -1
        let hasNeighbor = (0..<pageCount).contains(index + delta)
        let limit = width * (hasNeighbor ? 1 : 0.18)
        let offset = hasNeighbor ? displacement : displacement * 0.26
        drag?.translation = min(max(-limit, offset), limit)
    }

    private func finishDrag(at index: Int, projected: CGFloat) {
        let horizontal = drag?.horizontal == true && drag?.index == index
        let logical = projected * direction
        let threshold = max(30, width * 0.25)
        let delta = horizontal && abs(logical) > threshold ? (logical < 0 ? 1 : -1) : 0
        move(by: delta)
    }

    private func move(by delta: Int) {
        withAnimation(settleAnimation) {
            drag = nil
            if delta != 0 { selection = min(max(0, currentPage + delta), pageCount - 1) }
        }
    }

    private func arrow(_ icon: String, label: String, change: Int, enabled: Bool) -> some View {
        let nova = theme.preset == .nova
        let palette = theme.palette(for: colorScheme)
        let fill = colorScheme == .dark ? palette.novaDarkOutlineFill : Color.clear
        return Button {
            move(by: change)
        } label: {
            if nova {
                Image(systemName: icon)
                    .font(.system(size: 10, weight: .semibold))
                    .foregroundColor(palette.foreground)
                    .frame(width: 28, height: 28)
                    .background(Circle().fill(fill))
                    .overlay(Circle().strokeBorder(palette.input).allowsHitTesting(false))
                    .frame(width: 44, height: 44)
                    .contentShape(Rectangle())
            } else {
                Image(systemName: icon).frame(minWidth: 44, minHeight: 44).contentShape(Rectangle())
            }
        }
        .buttonStyle(PlainButtonStyle())
        .disabled(!enabled)
        .opacity(enabled ? 1 : (nova ? 0.5 : 0.4))
        .accessibilityLabel(Text(label))
    }

    private func adjustPage(_ direction: AccessibilityAdjustmentDirection) {
        switch direction {
        case .increment: move(by: 1)
        case .decrement: move(by: -1)
        @unknown default: break
        }
    }
}

#if os(iOS)
@available(iOS 18, *)
private struct FoundationCarouselPan: UIGestureRecognizerRepresentable {
    let changed: (CGSize) -> Void
    let ended: (CGFloat) -> Void
    let cancelled: () -> Void

    func makeCoordinator(converter: CoordinateSpaceConverter) -> Coordinator { Coordinator() }

    func makeUIGestureRecognizer(context: Context) -> UIPanGestureRecognizer {
        let pan = UIPanGestureRecognizer()
        pan.delegate = context.coordinator
        pan.maximumNumberOfTouches = 1
        return pan
    }

    func handleUIGestureRecognizerAction(_ pan: UIPanGestureRecognizer, context: Context) {
        let translation = pan.translation(in: pan.view)
        switch pan.state {
        case .began, .changed: changed(CGSize(width: translation.x, height: translation.y))
        case .ended: ended(translation.x + pan.velocity(in: pan.view).x * 0.16)
        case .cancelled, .failed: cancelled()
        default: break
        }
    }

    final class Coordinator: NSObject, UIGestureRecognizerDelegate {
        func gestureRecognizerShouldBegin(_ gestureRecognizer: UIGestureRecognizer) -> Bool {
            guard let pan = gestureRecognizer as? UIPanGestureRecognizer else { return false }
            let velocity = pan.velocity(in: pan.view)
            return abs(velocity.x) > abs(velocity.y)
        }
    }
}
#endif
