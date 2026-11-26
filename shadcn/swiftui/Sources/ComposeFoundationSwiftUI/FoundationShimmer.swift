import SwiftUI

public struct FoundationShimmer: ViewModifier {
    private let active: Bool

    public init(active: Bool = true) {
        self.active = active
    }

    public func body(content: Content) -> some View {
        content.overlay(Group {
            if active {
                ShimmerBand().clipped()
            }
        })
    }
}

private struct ShimmerBand: View {
    @Environment(\.accessibilityReduceMotion) private var reduceMotion
    @Environment(\.colorScheme) private var colorScheme
    @State private var traversing = false

    var body: some View {
        GeometryReader { geometry in
            let highlight = (colorScheme == .dark ? Color.white : Color.black).opacity(0.13)

            LinearGradient(
                gradient: Gradient(colors: [.clear, highlight, .clear]),
                startPoint: .leading,
                endPoint: .trailing
            )
            .frame(width: geometry.size.width * 0.65)
            .offset(x: offset(for: geometry.size.width))
        }
        .allowsHitTesting(false)
        .accessibilityHidden(true)
        .onAppear { start() }
        .foundationOnChange(of: reduceMotion) { _ in start() }
    }

    private func start() {
        // Recreate the moving band when the modifier is disabled or leaves the hierarchy.
        if !reduceMotion && !traversing {
            withAnimation(.linear(duration: 1.2).repeatForever(autoreverses: false)) {
                traversing = true
            }
        } else if reduceMotion {
            traversing = false
        }
    }

    private func offset(for width: CGFloat) -> CGFloat {
        if reduceMotion { return width * 0.175 }
        return traversing ? width : -width * 0.65
    }
}
