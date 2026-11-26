import SwiftUI

public struct FoundationToast: ViewModifier {
    @Environment(\.foundationTheme) private var theme
    @Environment(\.accessibilityReduceMotion) private var reduceMotion
    @Binding private var isPresented: Bool
    @State private var dismissal: Task<Void, Never>?

    private let title: String
    private let message: String?
    private let autoDismiss: Bool

    public init(isPresented: Binding<Bool>, title: String, message: String? = nil, autoDismiss: Bool = true) {
        self._isPresented = isPresented
        self.title = title
        self.message = message
        self.autoDismiss = autoDismiss
    }

    public func body(content: Content) -> some View {
        content.overlay(Group {
            if isPresented {
                FoundationToastCard(title: title, message: message) { isPresented = false }
                    .padding(16)
                    .transition(.move(edge: .bottom).combined(with: .opacity))
                    .onAppear {
                        dismissal?.cancel()
                        if autoDismiss {
                            dismissal = Task { @MainActor in
                                try? await Task.sleep(nanoseconds: 4_000_000_000)
                                guard !Task.isCancelled else { return }
                                isPresented = false
                            }
                        }
                    }
                    .onDisappear { dismissal?.cancel() }
            }
        }, alignment: .bottom)
        .animation(reduceMotion ? nil : .easeInOut(duration: theme.preset?.metrics.visibility ?? 0.15), value: isPresented)
    }
}

public enum FoundationToastVariant: Sendable {
    case success, error, warning, info, loading

    fileprivate var icon: String {
        switch self {
        case .success: return "checkmark.circle"
        case .error: return "xmark.circle"
        case .warning: return "exclamationmark.triangle"
        case .info: return "info.circle"
        case .loading: return "clock"
        }
    }
}

public struct FoundationToastAction {
    public let title: String
    fileprivate let handler: @MainActor () -> Void

    public init(_ title: String, perform handler: @escaping @MainActor () -> Void) {
        self.title = title
        self.handler = handler
    }
}

/// Own one state object per notification viewport.
@MainActor public final class FoundationToastState: ObservableObject {
    public struct Message: Identifiable {
        public let id: UUID
        public let title: String
        public let message: String?
        public let variant: FoundationToastVariant?
        fileprivate let action: FoundationToastAction?
    }

    @Published public private(set) var messages: [Message] = []
    private var dismissals: [UUID: Task<Void, Never>] = [:]

    public init() {}

    deinit {
        for task in dismissals.values { task.cancel() }
    }

    /// Set durationMilliseconds to zero for a toast that stays until dismissed.
    @discardableResult
    public func show(
        _ title: String, message: String? = nil, variant: FoundationToastVariant? = nil,
        durationMilliseconds: UInt64 = 5_000, action: FoundationToastAction? = nil
    ) -> UUID {
        let id = UUID()
        messages.append(Message(id: id, title: title, message: message, variant: variant, action: action))
        if durationMilliseconds > 0 {
            let nanosecondsPerMillisecond: UInt64 = 1_000_000
            let duration = min(durationMilliseconds, UInt64.max / nanosecondsPerMillisecond)
            dismissals[id] = Task { [weak self] in
                do {
                    try await Task.sleep(nanoseconds: duration * nanosecondsPerMillisecond)
                } catch {
                    return
                }
                self?.dismiss(id)
            }
        }
        return id
    }

    public func dismiss(_ id: UUID) {
        dismissals.removeValue(forKey: id)?.cancel()
        messages.removeAll { $0.id == id }
    }

    public func dismissAll() {
        for task in dismissals.values { task.cancel() }
        dismissals.removeAll()
        messages.removeAll()
    }

    fileprivate func performAction(for id: UUID) {
        guard let action = messages.first(where: { $0.id == id })?.action else { return }
        dismiss(id)
        action.handler()
    }

    func visibleMessages(limit: Int) -> ArraySlice<Message> {
        messages.suffix(max(0, limit))
    }
}

/// Attach this modifier to the root of the area that displays notifications.
public struct FoundationToaster: ViewModifier {
    @ObservedObject private var state: FoundationToastState
    @Environment(\.foundationTheme) private var theme
    @Environment(\.accessibilityReduceMotion) private var reduceMotion

    private let visibleToasts: Int
    private let alignment: Alignment

    public init(
        state: FoundationToastState, visibleToasts: Int = 3, alignment: Alignment = .bottomTrailing
    ) {
        self.state = state
        self.visibleToasts = visibleToasts
        self.alignment = alignment
    }

    public func body(content: Content) -> some View {
        let messages = state.visibleMessages(limit: visibleToasts)
        content.overlay(
            VStack(spacing: 8) {
                ForEach(messages) { toast in
                    FoundationToastCard(
                        title: toast.title, message: toast.message, variant: toast.variant,
                        actionTitle: toast.action?.title,
                        closeLabel: Text("Dismiss \(toast.title)"),
                        performAction: { state.performAction(for: toast.id) }
                    ) {
                        state.dismiss(toast.id)
                    }
                    .transition(.opacity)
                }
            }
            .frame(maxWidth: 384)
            .padding(16)
            .animation(
                reduceMotion ? nil : .easeInOut(duration: theme.preset?.metrics.visibility ?? 0.15),
                value: messages.map(\.id)
            ),
            alignment: alignment
        )
    }
}

private struct FoundationToastCard: View {
    @Environment(\.foundationTheme) private var theme
    @Environment(\.colorScheme) private var colorScheme

    let title: String
    let message: String?
    var variant: FoundationToastVariant? = nil
    var actionTitle: String? = nil
    var closeLabel = Text("Dismiss")
    var performAction: () -> Void = {}
    var dismiss: () -> Void

    var body: some View {
        let palette = theme.palette(for: colorScheme)
        let shape = RoundedRectangle(cornerRadius: theme.cornerRadius)
        HStack(spacing: 12) {
            if let variant {
                Image(systemName: variant.icon)
                    .foregroundColor(variant == .error ? palette.destructive : palette.foreground)
                    .accessibilityHidden(true)
            }
            VStack(alignment: .leading, spacing: 3) {
                Text(title).font(theme.font(.subheadline, weight: .semibold))
                if let message {
                    Text(message).font(theme.font(.caption)).foregroundColor(palette.mutedForeground)
                }
            }
            Spacer(minLength: 0)
            if let actionTitle {
                Button(actionTitle, action: performAction)
                    .buttonStyle(FoundationButtonStyle(.outline))
            }
            Button(action: dismiss) {
                Image(systemName: "xmark").padding(7)
            }
            .buttonStyle(PlainButtonStyle())
            .accessibilityLabel(closeLabel)
        }
        .foregroundColor(palette.foreground)
        .padding(12)
        .background(shape.fill(palette.card))
        .overlay(shape.strokeBorder(palette.border).allowsHitTesting(false))
        .shadow(color: Color.black.opacity(0.12), radius: 6, y: 2)
    }
}
