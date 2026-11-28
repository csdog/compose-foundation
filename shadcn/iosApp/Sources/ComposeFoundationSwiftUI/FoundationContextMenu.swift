import SwiftUI

public struct FoundationContextMenu<Actions: View>: ViewModifier {
    public enum TriggerStyle {
        case plain
        case outline
    }

    @Environment(\.foundationTheme) private var theme
    @Environment(\.colorScheme) private var colorScheme
    @Environment(\.font) private var font
    @ScaledMetric(relativeTo: .subheadline) private var labelSize: CGFloat = 14

    private let triggerStyle: TriggerStyle
    private let actions: () -> Actions

    public init(@ViewBuilder actions: @escaping () -> Actions) {
        self.init(triggerStyle: .plain, actions: actions)
    }

    /// The outline provides a full, capped 16:9 surface for long press or secondary click.
    public init(triggerStyle: TriggerStyle, @ViewBuilder actions: @escaping () -> Actions) {
        self.triggerStyle = triggerStyle
        self.actions = actions
    }

    @ViewBuilder
    public func body(content: Content) -> some View {
        switch triggerStyle {
        case .plain:
            content.contextMenu(menuItems: actions)
        case .outline:
            let palette = theme.palette(for: colorScheme)
            let shape = RoundedRectangle(cornerRadius: theme.cardCornerRadius)
            ZStack {
                shape.fill(palette.background)
                    .aspectRatio(16.0 / 9.0, contentMode: .fit)
                    .accessibilityHidden(true)
                content
                    .font(font ?? .system(size: labelSize))
                    .foregroundColor(palette.foreground)
                    .multilineTextAlignment(.center)
                    .padding(12)
            }
            .frame(maxWidth: 320)
            .overlay(shape.strokeBorder(palette.border, style: StrokeStyle(lineWidth: 1, dash: [3, 2]))
                .allowsHitTesting(false))
            .contentShape(shape)
            .contextMenu(menuItems: actions)
        }
    }
}
