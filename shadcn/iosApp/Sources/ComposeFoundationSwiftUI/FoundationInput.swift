import SwiftUI

public struct FoundationInput: View {
    @Environment(\.foundationTheme) private var theme
    @Environment(\.colorScheme) private var colorScheme
    @Environment(\.isEnabled) private var isEnabled
    @Environment(\.font) private var font
    @ScaledMetric(relativeTo: .subheadline) private var novaFieldHeight: CGFloat = 32
    @ScaledMetric(relativeTo: .subheadline) private var novaFontSize: CGFloat = 14
    @State private var isEditing = false
    @Binding private var text: String

    private let title: String
    private let placeholder: String
    private let secure: Bool
    private let invalid: Bool

    public init(_ title: String, text: Binding<String>, invalid: Bool = false) {
        self.init(title, text: text, placeholder: title, invalid: invalid)
    }

    public init(
        _ title: String, text: Binding<String>, placeholder: String, secure: Bool = false, invalid: Bool = false
    ) {
        self.title = title
        self._text = text
        self.placeholder = placeholder
        self.secure = secure
        self.invalid = invalid
    }

    public var body: some View {
        let palette = theme.palette(for: colorScheme)
        let radius = theme.preset == nil ? theme.cornerRadius : min(theme.cornerRadius, 22)
        let shape = RoundedRectangle(cornerRadius: radius)
        let border = invalid ? palette.destructive : (isEditing ? palette.ring : palette.input)
        let underlined = theme.preset == .sera
        let nova = theme.preset == .nova
        let fieldHeight = max(32, novaFieldHeight)
        let fill = nova ? (colorScheme == .dark ? palette.novaDarkOutlineFill : .clear) : palette.card

        editor(nova ? "" : placeholder)
            .textFieldStyle(.plain)
            .background(Group {
                if nova && text.isEmpty {
                    Text(placeholder)
                        .foregroundColor(palette.mutedForeground)
                        .accessibilityHidden(true)
                }
            }, alignment: .leading)
            .font(nova ? (font ?? .system(size: novaFontSize)) : theme.font(.body))
            .foregroundColor(palette.foreground)
            .accessibilityLabel(Text(title))
            .padding(.horizontal, theme.preset?.metrics.inputPadding ?? 12)
            .padding(.vertical, nova ? 0 : 6)
            .frame(minHeight: nova ? max(44, fieldHeight) : 44)
            .background(shape.fill(underlined ? Color.clear : fill).frame(height: nova ? fieldHeight : nil))
            .overlay(
                Group {
                    if underlined {
                        Rectangle().fill(border).frame(height: isEditing ? 2 : 1)
                    } else {
                        shape.strokeBorder(border, lineWidth: isEditing ? 2 : 1)
                            .frame(height: nova ? fieldHeight : nil)
                            .frame(maxHeight: .infinity)
                    }
                }
                .allowsHitTesting(false),
                alignment: .bottom
            )
            .opacity(isEnabled ? 1 : 0.5)
    }

    @ViewBuilder
    private func editor(_ prompt: String) -> some View {
        if secure {
            if #available(iOS 15, macOS 12, *) {
                FocusedSecureInput(prompt: prompt, text: $text, isEditing: $isEditing)
            } else {
                SecureField(prompt, text: $text)
            }
        } else {
            TextField(prompt, text: $text, onEditingChanged: { isEditing = $0 })
        }
    }
}

@available(iOS 15, macOS 12, *)
private struct FocusedSecureInput: View {
    let prompt: String
    @Binding var text: String
    @Binding var isEditing: Bool
    @FocusState private var focused: Bool

    var body: some View {
        SecureField(prompt, text: $text)
            .focused($focused)
            .onChange(of: focused) { isEditing = $0 }
    }
}
