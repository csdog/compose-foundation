import SwiftUI

public struct FoundationTextarea: View {
    @Environment(\.foundationTheme) private var theme
    @Environment(\.colorScheme) private var colorScheme
    @Environment(\.font) private var font
    @ScaledMetric(relativeTo: .subheadline) private var novaFontSize: CGFloat = 14
    @Binding private var text: String

    private let title: String
    private let placeholder: String
    private let invalid: Bool

    public init(_ title: String, text: Binding<String>, invalid: Bool = false) {
        self.init(title, text: text, placeholder: "", invalid: invalid)
    }

    public init(_ title: String, text: Binding<String>, placeholder: String, invalid: Bool = false) {
        self.title = title
        self._text = text
        self.placeholder = placeholder
        self.invalid = invalid
    }

    public var body: some View {
        let palette = theme.palette(for: colorScheme)
        let shape = RoundedRectangle(cornerRadius: theme.cornerRadius)
        let nova = theme.preset == .nova
        let fill = nova ? (colorScheme == .dark ? palette.novaDarkOutlineFill : .clear) : palette.card

        editor(hideBackground: nova)
            .font(nova ? (font ?? .system(size: novaFontSize)) : theme.font(.body))
            .foregroundColor(palette.foreground)
            .frame(minHeight: nova ? 64 : 80)
            .padding(nova ? .horizontal : .all, 5)
            .background(shape.fill(fill))
            .overlay(
                Group {
                    if text.isEmpty && !placeholder.isEmpty {
                        Text(placeholder)
                            .font(nova ? (font ?? .system(size: novaFontSize)) : theme.font(.body))
                            .foregroundColor(palette.mutedForeground)
                            .padding(.leading, 11)
                            .padding(.top, nova ? 8 : 13)
                            .allowsHitTesting(false)
                            .accessibilityHidden(true)
                    }
                },
                alignment: .topLeading
            )
            .overlay(shape.strokeBorder(invalid ? palette.destructive : palette.input).allowsHitTesting(false))
            .clipShape(shape)
            .accessibilityLabel(Text(title))
    }

    @ViewBuilder
    private func editor(hideBackground: Bool) -> some View {
        if hideBackground, #available(iOS 16, macOS 13, *) {
            TextEditor(text: $text).scrollContentBackground(.hidden)
        } else {
            TextEditor(text: $text)
        }
    }
}
