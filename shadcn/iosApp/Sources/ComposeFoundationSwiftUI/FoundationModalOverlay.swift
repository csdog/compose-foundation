import SwiftUI

/// An in-place presentation whose parent owns the size of the covered area.
struct FoundationModalOverlay<Panel: View>: ViewModifier {
    @Environment(\.accessibilityReduceMotion) private var reduceMotion
    @Environment(\.accessibilityReduceTransparency) private var reduceTransparency

    @Binding var isPresented: Bool
    @State private var dismissal: Task<Void, Never>?
    let duration: TimeInterval
    let animation: (TimeInterval) -> Animation
    let onDismiss: () -> Void
    @ViewBuilder let panel: (CGSize) -> Panel

    func body(content: Content) -> some View {
        content
            .disabled(isPresented)
            .allowsHitTesting(!isPresented)
            .accessibilityHidden(isPresented)
            .compositingGroup()
            .blur(radius: isPresented && !reduceTransparency ? 4 : 0)
            .overlay(Group {
                if isPresented {
                    GeometryReader { geometry in
                        ZStack {
                            Color.black.opacity(0.10)
                                .onTapGesture { isPresented = false }
                                .accessibilityHidden(true)
                            panel(geometry.size)
                        }
                        .frame(maxWidth: .infinity, maxHeight: .infinity)
                    }
                    .transition(.opacity)
                    .onDisappear {
                        scheduleDismissal()
                    }
                }
            })
            .animation(reduceMotion ? nil : animation(duration), value: isPresented)
            .onChange(of: isPresented) { presented in
                if presented { dismissal?.cancel() }
            }
            .onDisappear { dismissal?.cancel() }
    }

    private func scheduleDismissal() {
        guard !isPresented else { return }
        dismissal?.cancel()
        // The platform text editor can resign focus after SwiftUI begins removing the overlay.
        let delay = reduceMotion ? 0 : UInt64(duration * 1_000_000_000)
        dismissal = Task { @MainActor in
            do { try await Task.sleep(nanoseconds: delay) } catch { return }
            if !isPresented { onDismiss() }
        }
    }
}
