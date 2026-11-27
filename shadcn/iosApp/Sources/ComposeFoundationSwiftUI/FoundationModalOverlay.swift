import SwiftUI

/// An in-place presentation whose parent owns the size of the covered area.
struct FoundationModalOverlay<Panel: View>: ViewModifier {
    @Environment(\.accessibilityReduceMotion) private var reduceMotion
    @Environment(\.accessibilityReduceTransparency) private var reduceTransparency

    @Binding var isPresented: Bool
    let animation: Animation
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
                                .transition(.opacity)
                            panel(geometry.size)
                        }
                        .frame(maxWidth: .infinity, maxHeight: .infinity)
                    }
                    .transition(.opacity)
                    .onDisappear {
                        guard !isPresented else { return }
                        DispatchQueue.main.async {
                            if !isPresented { onDismiss() }
                        }
                    }
                }
            })
            .animation(reduceMotion ? nil : animation, value: isPresented)
    }
}
