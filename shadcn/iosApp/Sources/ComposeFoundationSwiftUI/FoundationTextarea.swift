import SwiftUI

public struct FoundationTextarea: View {
    @Environment(\.foundationTheme) private var theme
    @Environment(\.colorScheme) private var colorScheme
    @Environment(\.font) private var font
    @Environment(\.isEnabled) private var isEnabled
    @Environment(\.foundationSurface) private var surface
    @Environment(\.layoutDirection) private var layoutDirection
    @ScaledMetric(relativeTo: .body) private var novaFontSize: CGFloat = 16
    @State private var isFocused = false
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
        let fill = nova ? novaFill(palette) : palette.card
        let border = nova ? novaBorder(palette) : (invalid ? palette.destructive : palette.input)
        let ring = nova ? novaRing(palette) : Color.clear
        let editorFont = nova ? (font ?? .system(size: novaFontSize)) : theme.font(.body)

        focusTrackedEditor(nova: nova)
            .font(editorFont)
            .foregroundColor(palette.foreground)
            .frame(minHeight: nova ? 64 : 80)
            .padding(nova ? .horizontal : .all, 5)
            .background(shape.fill(fill))
            .overlay(
                Group {
                    if text.isEmpty && !placeholder.isEmpty {
                        Text(placeholder)
                            .font(editorFont)
                            .foregroundColor(palette.mutedForeground.opacity(nova && !isEnabled ? 0.5 : 1))
                            .padding(.leading, 11)
                            .padding(.top, nova ? 10 : 13)
                            .allowsHitTesting(false)
                            .accessibilityHidden(true)
                    }
                },
                alignment: .topLeading
            )
            .overlay(shape.strokeBorder(border).allowsHitTesting(false))
            .clipShape(shape)
            .overlay(RoundedRectangle(cornerRadius: theme.cornerRadius + 3)
                .strokeBorder(ring, lineWidth: 3)
                .padding(-3)
                .allowsHitTesting(false))
            .accessibilityLabel(Text(title))
    }

    private func novaFill(_ palette: FoundationPalette) -> Color {
        if !isEnabled { return colorScheme == .dark ? palette.secondary.opacity(0.5) : palette.input.opacity(0.27) }
        return colorScheme == .dark ? palette.novaDarkOutlineFill(in: surface) : .clear
    }

    private func novaBorder(_ palette: FoundationPalette) -> Color {
        if invalid { return palette.destructive.opacity(colorScheme == .dark ? 0.5 : 1) }
        if !isEnabled { return palette.input.opacity(colorScheme == .dark ? 0.5 : 0.36) }
        return isFocused ? palette.ring : palette.input
    }

    private func novaRing(_ palette: FoundationPalette) -> Color {
        if invalid { return palette.destructive.opacity(colorScheme == .dark ? 0.4 : 0.2) }
        return isEnabled && isFocused ? palette.ring.opacity(0.5) : .clear
    }

    @ViewBuilder
    private func focusTrackedEditor(nova: Bool) -> some View {
        if nova, #available(iOS 15, macOS 12, *) {
            FocusedTextareaEditor(editor: alignedEditor(nova: nova), isFocused: $isFocused)
        } else {
            alignedEditor(nova: nova)
        }
    }

    @ViewBuilder
    private func alignedEditor(nova: Bool) -> some View {
        #if compiler(>=6.2)
        if #available(iOS 26, macOS 26, *), nova && layoutDirection == .rightToLeft {
            editor(hideBackground: nova).multilineTextAlignment(strategy: .layoutBased)
        } else {
            editor(hideBackground: nova)
        }
        #else
        editor(hideBackground: nova)
        #endif
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

@available(iOS 15, macOS 12, *)
private struct FocusedTextareaEditor<Editor: View>: View {
    let editor: Editor
    @Binding var isFocused: Bool
    @FocusState private var focused: Bool

    var body: some View {
        editor.focused($focused)
            .foundationOnChange(of: focused) { isFocused = $0 }
    }
}
