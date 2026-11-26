import SwiftUI

public struct FoundationAspectRatio<Content: View>: View {
    private let ratio: CGFloat
    private let content: Content

    public init(_ ratio: CGFloat = 1, @ViewBuilder content: () -> Content) {
        self.ratio = ratio.isFinite && ratio > 0 ? ratio : 1
        self.content = content()
    }

    public var body: some View {
        Color.clear.aspectRatio(ratio, contentMode: .fit)
            .overlay(content)
            .clipped()
    }
}
