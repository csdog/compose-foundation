import SwiftUI

public struct FoundationScrollFade: ViewModifier {
    private let edges: Edge.Set
    private let distance: CGFloat

    public init(edges: Edge.Set = [.top, .bottom], distance: CGFloat = 16) {
        self.edges = edges
        self.distance = distance.isFinite ? max(0, distance) : 0
    }

    public func body(content: Content) -> some View {
        content.mask(
            GeometryReader { geometry in
                gradient(length: geometry.size.height, start: .top, end: .bottom)
                    .mask(gradient(length: geometry.size.width, start: .leading, end: .trailing))
            }
        )
    }

    private func gradient(length: CGFloat, start: Edge, end: Edge) -> LinearGradient {
        let fraction = length > 0 ? min(distance / length, 0.5) : 0
        let fadesStart = fraction > 0 && edges.contains(Edge.Set(start))
        let fadesEnd = fraction > 0 && edges.contains(Edge.Set(end))
        let vertical = start == .top

        return LinearGradient(
            gradient: Gradient(stops: [
                .init(color: fadesStart ? .clear : .white, location: 0),
                .init(color: .white, location: fadesStart ? fraction : 0),
                .init(color: .white, location: fadesEnd ? 1 - fraction : 1),
                .init(color: fadesEnd ? .clear : .white, location: 1)
            ]),
            startPoint: vertical ? .top : .leading,
            endPoint: vertical ? .bottom : .trailing
        )
    }
}
