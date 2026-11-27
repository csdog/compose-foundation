import SwiftUI

public struct FoundationSpinner: View {
    @Environment(\.foundationTheme) private var theme
    @Environment(\.colorScheme) private var colorScheme
    @Environment(\.accessibilityReduceMotion) private var reduceMotion
    @State private var rotating = false

    private let label: String
    private let animates: Bool

    public init(accessibilityLabel: String = "Loading") {
        self.init(accessibilityLabel: accessibilityLabel, animates: true)
    }

    public init(accessibilityLabel: String = "Loading", animates: Bool) {
        self.label = accessibilityLabel
        self.animates = animates
    }

    public var body: some View {
        let palette = theme.palette(for: colorScheme)
        if theme.preset == .nova || !animates {
            Circle()
                .inset(by: 1.33)
                .trim(from: 0, to: 0.75)
                .stroke(palette.foreground, style: StrokeStyle(lineWidth: 1.33, lineCap: .round))
                .frame(width: 16, height: 16)
                .rotationEffect(.degrees(rotating ? 360 : 0))
                .onAppear(perform: updateAnimation)
                .onChange(of: reduceMotion) { _ in updateAnimation() }
                .onChange(of: animates) { _ in updateAnimation() }
                .accessibilityElement(children: .ignore)
                .accessibilityLabel(Text(label))
        } else {
            ProgressView()
                .progressViewStyle(CircularProgressViewStyle(tint: palette.primary))
                .accessibilityLabel(Text(label))
        }
    }

    private func updateAnimation() {
        withAnimation(animates && !reduceMotion ? .linear(duration: 1).repeatForever(autoreverses: false) : nil) {
            rotating = animates && !reduceMotion
        }
    }
}
