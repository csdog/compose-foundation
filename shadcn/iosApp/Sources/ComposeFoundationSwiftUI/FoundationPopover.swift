import SwiftUI
#if os(iOS)
import UIKit
#elseif os(macOS)
import AppKit
#endif

public enum FoundationPopoverAlignment: Sendable {
    case start, center, end
}

public struct FoundationPopover<PopoverContent: View>: ViewModifier {
    @Environment(\.self) private var environment
    @Binding private var isPresented: Bool

    private let arrowEdge: Edge
    private let alignment: FoundationPopoverAlignment
    private let width: CGFloat?
    private let onDismiss: () -> Void
    private let popoverContent: () -> PopoverContent

    public init(
        isPresented: Binding<Bool>,
        arrowEdge: Edge = .top,
        @ViewBuilder content: @escaping () -> PopoverContent
    ) {
        self.init(isPresented: isPresented, arrowEdge: arrowEdge, onDismiss: {}, content: content)
    }

    public init(
        isPresented: Binding<Bool>,
        arrowEdge: Edge = .top,
        onDismiss: @escaping () -> Void,
        @ViewBuilder content: @escaping () -> PopoverContent
    ) {
        self.init(isPresented: isPresented, arrowEdge: arrowEdge, alignment: .center,
                  onDismiss: onDismiss, content: content)
    }

    /// Positions content at the start, center, or end of the trigger's cross axis.
    /// The width includes panel padding and is clamped to the available viewport.
    public init(
        isPresented: Binding<Bool>,
        arrowEdge: Edge = .top,
        alignment: FoundationPopoverAlignment,
        width: CGFloat? = nil,
        onDismiss: @escaping () -> Void = {},
        @ViewBuilder content: @escaping () -> PopoverContent
    ) {
        self._isPresented = isPresented
        self.arrowEdge = arrowEdge
        self.alignment = alignment
        self.width = width
        self.onDismiss = onDismiss
        self.popoverContent = content
    }

    public func body(content: Content) -> some View {
        #if os(iOS) || os(macOS)
        content.background(
            FoundationPopoverAnchor(
                isPresented: isPresented,
                placement: PopoverPlacement(edge: placementEdge, alignment: alignment,
                                            rightToLeft: environment.layoutDirection == .rightToLeft,
                                            width: width, usesSafeArea: !environment.foundationPopoverFullViewport),
                panel: panel,
                requestDismissal: { isPresented = false }, onDismiss: onDismiss
            )
                .allowsHitTesting(false)
                .accessibilityHidden(true)
        )
        #else
        content.popover(isPresented: $isPresented, arrowEdge: arrowEdge) {
            panel.onDisappear { if !isPresented { onDismiss() } }
        }
        #endif
    }

    private var placementEdge: Edge {
        guard environment.layoutDirection == .rightToLeft else { return arrowEdge }
        switch arrowEdge {
        case .leading: return .trailing
        case .trailing: return .leading
        default: return arrowEdge
        }
    }

    private var panel: some View {
        let theme = environment.foundationTheme
        let palette = theme.palette(for: environment.colorScheme)
        let shape = RoundedRectangle(cornerRadius: theme.popoverCornerRadius)
        return popoverContent()
            .padding(theme.preset?.metrics.popoverPadding ?? 12)
            .frame(maxWidth: width == nil ? nil : .infinity, alignment: .leading)
            .foregroundColor(palette.foreground)
            .background(shape.fill(palette.card))
            .overlay(shape.strokeBorder(theme.preset == .nova ? palette.foreground.opacity(0.10) : palette.border)
                .allowsHitTesting(false))
            .accentColor(palette.primary)
            .accessibilityAction(.escape) { isPresented = false }
            .environment(\.foundationTheme, theme)
            .environment(\.colorScheme, environment.colorScheme)
            .environment(\.layoutDirection, environment.layoutDirection)
            .environment(\.locale, environment.locale)
            .environment(\.sizeCategory, environment.sizeCategory)
    }
}

private struct PopoverFullViewportKey: EnvironmentKey {
    static let defaultValue = false
}

extension EnvironmentValues {
    var foundationPopoverFullViewport: Bool {
        get { self[PopoverFullViewportKey.self] }
        set { self[PopoverFullViewportKey.self] = newValue }
    }
}

private struct PopoverPlacement {
    let edge: Edge
    let alignment: FoundationPopoverAlignment
    let rightToLeft: Bool
    let width: CGFloat?
    let usesSafeArea: Bool

    func frame(size: CGSize, anchor: CGRect, viewport: CGRect) -> CGRect {
        let gap: CGFloat = 4
        let beforeX = anchor.minX - gap - size.width
        let afterX = anchor.maxX + gap
        let beforeY = anchor.minY - gap - size.height
        let afterY = anchor.maxY + gap
        var x = anchor.midX - size.width / 2
        var y = anchor.midY - size.height / 2
        switch alignment {
        case .center:
            break
        case .start, .end:
            if edge == .top || edge == .bottom {
                let physicalLeft = (alignment == .start) != rightToLeft
                x = physicalLeft ? anchor.minX : anchor.maxX - size.width
            } else {
                y = alignment == .start ? anchor.minY : anchor.maxY - size.height
            }
        }
        switch edge {
        case .top:
            y = afterY + size.height <= viewport.maxY ? afterY : beforeY
        case .bottom:
            y = beforeY >= viewport.minY ? beforeY : afterY
        case .leading:
            x = afterX + size.width <= viewport.maxX ? afterX : beforeX
        case .trailing:
            x = beforeX >= viewport.minX ? beforeX : afterX
        }
        x = min(max(x, viewport.minX), viewport.maxX - size.width)
        y = min(max(y, viewport.minY), viewport.maxY - size.height)
        return CGRect(origin: CGPoint(x: x, y: y), size: size)
    }
}

#if os(iOS)
private struct FoundationPopoverAnchor<Panel: View>: UIViewRepresentable {
    let isPresented: Bool
    let placement: PopoverPlacement
    let panel: Panel
    let requestDismissal: () -> Void
    let onDismiss: () -> Void

    func makeCoordinator() -> Coordinator { Coordinator() }

    func makeUIView(context: Context) -> Anchor {
        let view = Anchor()
        view.didChangeFrame = { [weak coordinator = context.coordinator] anchor in
            coordinator?.refresh(from: anchor)
        }
        return view
    }

    func updateUIView(_ view: Anchor, context: Context) {
        context.coordinator.configuration = self
        view.scheduleRefresh()
    }

    static func dismantleUIView(_ view: Anchor, coordinator: Coordinator) {
        view.didChangeFrame = nil
        coordinator.remove()
    }

    final class Anchor: UIView {
        var didChangeFrame: ((Anchor) -> Void)?

        override func didMoveToWindow() {
            super.didMoveToWindow()
            scheduleRefresh()
        }

        override func layoutSubviews() {
            super.layoutSubviews()
            scheduleRefresh()
        }

        func scheduleRefresh() {
            DispatchQueue.main.async { [weak self] in
                guard let self else { return }
                didChangeFrame?(self)
            }
        }
    }

    @MainActor final class Coordinator: NSObject {
        var configuration: FoundationPopoverAnchor?
        private var host: PopoverHost?
        private var sizingHost: UIHostingController<MeasuredPanel>?
        private var overlayWindow: UIWindow?
        private weak var returnWindow: UIWindow?

        func refresh(from anchor: Anchor) {
            if let configuration, !configuration.isPresented {
                let wasPresented = overlayWindow != nil
                remove()
                if wasPresented {
                    // UIKit updates the presenting window's focus after making it key.
                    DispatchQueue.main.async { configuration.onDismiss() }
                }
                return
            }
            guard let configuration, let window = anchor.window,
                  let scene = window.windowScene else {
                remove()
                return
            }
            if returnWindow !== window { remove() }

            let placement = configuration.placement
            let viewport = placement.usesSafeArea
                ? window.bounds.inset(by: window.safeAreaInsets).insetBy(dx: 8, dy: 8)
                : window.bounds.insetBy(dx: 5, dy: 5)
            guard viewport.width > 0, viewport.height > 0 else { return }

            let width = placement.width.map { min(max(1, $0), viewport.width) }
            let panel = MeasuredPanel(content: configuration.panel, width: width)
            let measurement = sizingHost ?? UIHostingController(rootView: panel)
            sizingHost = measurement
            measurement.rootView = panel
            let proposal = CGSize(width: width ?? min(288, viewport.width), height: viewport.height)
            let measured: CGSize
            if #available(iOS 16, *) {
                measured = measurement.sizeThatFits(in: proposal)
            } else {
                measured = measurement.view.systemLayoutSizeFitting(
                    proposal, withHorizontalFittingPriority: .fittingSizeLevel,
                    verticalFittingPriority: .fittingSizeLevel
                )
            }
            let size = CGSize(
                width: width ?? min(max(1, measured.width), viewport.width),
                height: min(max(1, measured.height), viewport.height)
            )
            let anchorFrame = anchor.convert(anchor.bounds, to: window)
            let frame = placement.frame(size: size, anchor: anchorFrame, viewport: viewport)
            let surface = Surface(panel: configuration.panel, frame: frame) { [weak self] in self?.dismiss() }
            if let host, let overlayWindow {
                overlayWindow.frame = window.frame
                host.rootView = surface
            } else {
                let host = PopoverHost(rootView: surface)
                host.dismiss = surface.dismiss
                host.view.backgroundColor = .clear
                host.view.accessibilityViewIsModal = true
                let overlay = UIWindow(windowScene: scene)
                overlay.backgroundColor = .clear
                overlay.windowLevel = UIWindow.Level(rawValue: window.windowLevel.rawValue + 1)
                overlay.frame = window.frame
                overlay.rootViewController = host
                self.host = host
                overlayWindow = overlay
                returnWindow = window
                overlay.makeKeyAndVisible()
                if UIAccessibility.isVoiceOverRunning {
                    UIAccessibility.post(notification: .screenChanged, argument: nil)
                } else {
                    host.becomeFirstResponder()
                }
            }
        }

        private func dismiss() { configuration?.requestDismissal() }

        func remove() {
            let returnsKey = overlayWindow?.isKeyWindow == true
            overlayWindow?.isHidden = true
            overlayWindow?.rootViewController = nil
            if returnsKey { returnWindow?.makeKey() }
            host = nil
            sizingHost = nil
            overlayWindow = nil
            returnWindow = nil
        }
    }

    private struct MeasuredPanel: View {
        let content: Panel
        let width: CGFloat?

        var body: some View { content.frame(width: width) }
    }

    struct Surface: View {
        let panel: Panel
        let frame: CGRect
        let dismiss: () -> Void

        var body: some View {
            ZStack(alignment: .topLeading) {
                Color.clear.contentShape(Rectangle()).onTapGesture(perform: dismiss).accessibilityHidden(true)
                panel
                    .frame(width: frame.width, height: frame.height)
                    .shadow(color: .black.opacity(0.16), radius: 10, y: 4)
                    .position(x: frame.midX, y: frame.midY)
            }
            .ignoresSafeArea()
        }
    }

    final class PopoverHost: UIHostingController<Surface> {
        var dismiss: (() -> Void)?
        override var canBecomeFirstResponder: Bool { true }

        override var keyCommands: [UIKeyCommand]? {
            [UIKeyCommand(input: UIKeyCommand.inputEscape, modifierFlags: [], action: #selector(close))]
        }

        @objc private func close() { dismiss?() }

        override func accessibilityPerformEscape() -> Bool {
            close()
            return true
        }
    }
}
#elseif os(macOS)
private struct FoundationPopoverAnchor<Panel: View>: NSViewRepresentable {
    let isPresented: Bool
    let placement: PopoverPlacement
    let panel: Panel
    let requestDismissal: () -> Void
    let onDismiss: () -> Void

    func makeCoordinator() -> Coordinator { Coordinator() }

    func makeNSView(context: Context) -> Anchor {
        let view = Anchor()
        view.didChangeFrame = { [weak coordinator = context.coordinator] anchor in
            coordinator?.refresh(from: anchor)
        }
        return view
    }

    func updateNSView(_ view: Anchor, context: Context) {
        context.coordinator.configuration = self
        view.scheduleRefresh()
    }

    static func dismantleNSView(_ view: Anchor, coordinator: Coordinator) {
        view.didChangeFrame = nil
        coordinator.remove()
    }

    final class Anchor: NSView {
        var didChangeFrame: ((Anchor) -> Void)?

        override func viewDidMoveToWindow() {
            super.viewDidMoveToWindow()
            scheduleRefresh()
        }

        override func layout() {
            super.layout()
            scheduleRefresh()
        }

        func scheduleRefresh() {
            DispatchQueue.main.async { [weak self] in
                guard let self else { return }
                didChangeFrame?(self)
            }
        }
    }

    @MainActor final class Coordinator: NSObject, NSWindowDelegate {
        var configuration: FoundationPopoverAnchor?
        var host: NSHostingView<MeasuredPanel>?
        private var popover: PopoverPanel?
        private var mouseMonitor: Any?

        func refresh(from anchor: Anchor) {
            if let configuration, !configuration.isPresented {
                let wasPresented = popover != nil
                remove()
                if wasPresented { configuration.onDismiss() }
                return
            }
            guard let configuration, let parent = anchor.window,
                  let screen = parent.screen else {
                remove()
                return
            }
            if popover?.parent !== parent { remove() }
            let viewport = screen.visibleFrame
            let bounds = CGRect(origin: .zero, size: viewport.size).insetBy(dx: 8, dy: 8)

            let placement = configuration.placement
            let width = placement.width.map { min(max(1, $0), bounds.width) }
            let panel = MeasuredPanel(content: configuration.panel, width: width)
            let isOpening = popover == nil
            if isOpening {
                let host = NSHostingView(rootView: panel)
                let popover = PopoverPanel(contentRect: .zero, styleMask: [.borderless], backing: .buffered, defer: false)
                popover.isOpaque = false
                popover.backgroundColor = .clear
                popover.hasShadow = true
                popover.contentView = host
                popover.delegate = self
                popover.dismiss = { [weak self] in self?.dismiss() }
                self.host = host
                self.popover = popover
                parent.addChildWindow(popover, ordered: .above)

                mouseMonitor = NSEvent.addLocalMonitorForEvents(matching: [.leftMouseDown, .rightMouseDown, .otherMouseDown]) {
                    [weak self, weak popover] event in
                    if event.window !== popover { self?.dismiss() }
                    return event
                }
            }

            guard let host, let popover else { return }
            host.rootView = panel
            let measured = host.fittingSize
            let size = CGSize(
                width: min(max(1, measured.width), bounds.width),
                height: min(max(1, measured.height), bounds.height)
            )
            let trigger = parent.convertToScreen(anchor.convert(anchor.bounds, to: nil))
            let localAnchor = CGRect(x: trigger.minX - viewport.minX, y: viewport.maxY - trigger.maxY,
                                     width: trigger.width, height: trigger.height)
            let frame = placement.frame(size: size, anchor: localAnchor, viewport: bounds)
            popover.setFrame(
                CGRect(x: viewport.minX + frame.minX, y: viewport.maxY - frame.maxY,
                       width: frame.width, height: frame.height),
                display: true
            )
            // Adding a child window can make it visible before it becomes key.
            if isOpening { popover.makeKeyAndOrderFront(nil) }
        }

        func windowDidResignKey(_ notification: Notification) { dismiss() }

        private func dismiss() { configuration?.requestDismissal() }

        func remove() {
            if let mouseMonitor { NSEvent.removeMonitor(mouseMonitor) }
            mouseMonitor = nil
            if let popover {
                let parent = popover.parent
                let returnsKey = popover.isKeyWindow
                popover.delegate = nil
                parent?.removeChildWindow(popover)
                popover.orderOut(nil)
                if returnsKey { parent?.makeKey() }
            }
            popover = nil
            host = nil
        }
    }

    struct MeasuredPanel: View {
        let content: Panel
        let width: CGFloat?

        var body: some View { content.frame(width: width) }
    }

    final class PopoverPanel: NSPanel {
        var dismiss: (() -> Void)?
        override var canBecomeKey: Bool { true }
        override func cancelOperation(_ sender: Any?) { dismiss?() }
    }
}
#endif
