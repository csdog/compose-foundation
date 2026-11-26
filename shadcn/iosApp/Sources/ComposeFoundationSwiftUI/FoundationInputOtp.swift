import SwiftUI

public struct FoundationInputOtp: View {
    @Environment(\.foundationTheme) private var theme
    @Environment(\.colorScheme) private var colorScheme
    @Binding private var code: String

    private let length: Int
    private let label: String

    public init(code: Binding<String>, length: Int = 6, accessibilityLabel: String = "Verification code") {
        self._code = code
        self.length = max(1, length)
        self.label = accessibilityLabel
    }

    public var body: some View {
        let palette = theme.palette(for: colorScheme)
        let digits = Array(sanitized(code))
        let shape = RoundedRectangle(cornerRadius: theme.cornerRadius)

        ZStack {
            field
                .foregroundColor(.clear)
                .accentColor(.clear)
                .textFieldStyle(PlainTextFieldStyle())
                .accessibilityLabel(Text(label))
            HStack(spacing: 5) {
                ForEach(0..<length, id: \.self) { index in
                    Text(index < digits.count ? String(digits[index]) : " ")
                        .font(.body.monospacedDigit())
                        .frame(maxWidth: .infinity, minHeight: 44)
                        .foregroundColor(palette.foreground)
                        .background(shape.fill(palette.card))
                        .overlay(shape.strokeBorder(index == digits.count ? palette.ring : palette.border))
                }
            }
            .allowsHitTesting(false)
            .accessibilityHidden(true)
        }
    }

    private var field: some View {
        let value = Binding(get: { sanitized(code) }, set: { code = sanitized($0) })
        #if os(iOS)
        return TextField("", text: value).keyboardType(.numberPad).textContentType(.oneTimeCode)
        #else
        return TextField("", text: value)
        #endif
    }

    private func sanitized(_ value: String) -> String {
        value.compactMap(\.wholeNumberValue).filter { (0...9).contains($0) }.prefix(length).map(String.init).joined()
    }
}
