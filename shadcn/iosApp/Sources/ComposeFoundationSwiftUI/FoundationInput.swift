import SwiftUI

public struct FoundationInput: View {
    @Environment(\.foundationTheme) private var theme
    @Environment(\.colorScheme) private var colorScheme
    @Environment(\.isEnabled) private var isEnabled
    @State private var isEditing = false
    @Binding private var text: String

    private let title: String
    private let invalid: Bool

    public init(_ title: String, text: Binding<String>, invalid: Bool = false) {
        self.title = title
        self._text = text
        self.invalid = invalid
    }

    public var body: some View {
        let palette = theme.palette(for: colorScheme)
        let radius = theme.preset == nil ? theme.cornerRadius : min(theme.cornerRadius, 22)
        let shape = RoundedRectangle(cornerRadius: radius)
        let border = invalid ? palette.destructive : (isEditing ? palette.ring : palette.input)
        let underlined = theme.preset == .sera

        TextField(title, text: $text, onEditingChanged: { isEditing = $0 })
            .textFieldStyle(.plain)
            .font(theme.font(.body))
            .foregroundColor(palette.foreground)
            .padding(.horizontal, theme.preset?.metrics.inputPadding ?? 12)
            .padding(.vertical, 6)
            .frame(minHeight: 44)
            .background(shape.fill(underlined ? Color.clear : palette.card))
            .overlay(
                Group {
                    if underlined {
                        Rectangle().fill(border).frame(height: isEditing ? 2 : 1)
                    } else {
                        shape.strokeBorder(border, lineWidth: isEditing ? 2 : 1)
                    }
                }
                .allowsHitTesting(false),
                alignment: .bottom
            )
            .opacity(isEnabled ? 1 : 0.5)
    }
}
