import SwiftUI

public struct FoundationTextarea: View {
    @Environment(\.foundationTheme) private var theme
    @Environment(\.colorScheme) private var colorScheme
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
        let shape = RoundedRectangle(cornerRadius: theme.cornerRadius)

        TextEditor(text: $text)
            .font(theme.font(.body))
            .foregroundColor(palette.foreground)
            .frame(minHeight: 80)
            .padding(5)
            .background(shape.fill(palette.card))
            .overlay(shape.strokeBorder(invalid ? palette.destructive : palette.input).allowsHitTesting(false))
            .clipShape(shape)
            .accessibilityLabel(Text(title))
    }
}
