import SwiftUI

public struct FoundationInputOtp: View {
    public enum Pattern: Sendable {
        case digits, alphanumeric
    }

    @Environment(\.foundationTheme) private var theme
    @Environment(\.colorScheme) private var colorScheme
    @Environment(\.isEnabled) private var isEnabled
    @Environment(\.font) private var font
    @State private var isEditing = false
    @Binding private var code: String

    private let length: Int
    private let groupBreaks: [Int]
    private let pattern: Pattern
    private let label: String

    public init(code: Binding<String>, length: Int = 6, accessibilityLabel: String = "Verification code") {
        self.init(code: code, length: length, groupBreaks: [], accessibilityLabel: accessibilityLabel)
    }

    /// Inserts a separator before each valid zero-based slot index in `groupBreaks`.
    public init(
        code: Binding<String>,
        length: Int = 6,
        groupBreaks: [Int],
        accessibilityLabel: String = "Verification code"
    ) {
        self.init(code: code, length: length, groupBreaks: groupBreaks, pattern: .digits, accessibilityLabel: accessibilityLabel)
    }

    public init(
        code: Binding<String>,
        length: Int = 6,
        groupBreaks: [Int] = [],
        pattern: Pattern,
        accessibilityLabel: String = "Verification code"
    ) {
        let limit = max(1, length)
        self._code = code
        self.length = limit
        self.groupBreaks = Set(groupBreaks.filter { $0 > 0 && $0 < limit }).sorted()
        self.pattern = pattern
        self.label = accessibilityLabel
    }

    @ViewBuilder
    public var body: some View {
        let palette = theme.palette(for: colorScheme)
        let characters = Array(sanitized(code))
        let shape = RoundedRectangle(cornerRadius: theme.cornerRadius)

        if theme.preset == .nova {
            HStack(spacing: 0) {
                ForEach(groups, id: \.lowerBound) { group in
                    if group.lowerBound > 0 { separator(palette: palette) }
                    novaGroup(group, characters: characters, palette: palette, shape: shape)
                }
            }
            .compositingGroup()
            .opacity(isEnabled ? 1 : 0.5)
            .allowsHitTesting(false)
            .accessibilityHidden(true)
            .frame(minHeight: 44)
            .background(field)
        } else {
            ZStack {
                field
                HStack(spacing: 5) {
                    ForEach(groups, id: \.lowerBound) { group in
                        if group.lowerBound > 0 { separator(palette: palette) }
                        HStack(spacing: 5) {
                            ForEach(group, id: \.self) { index in
                                Text(index < characters.count ? String(characters[index]) : " ")
                                    .font(.body.monospacedDigit())
                                    .frame(maxWidth: .infinity, minHeight: 44)
                                    .foregroundColor(palette.foreground)
                                    .background(shape.fill(palette.card))
                                    .overlay(shape.strokeBorder(index == characters.count ? palette.ring : palette.border))
                            }
                        }
                    }
                }
                .allowsHitTesting(false)
                .accessibilityHidden(true)
            }
        }
    }

    private var groups: [Range<Int>] {
        let boundaries = [0] + groupBreaks + [length]
        return zip(boundaries, boundaries.dropFirst()).map { $0..<$1 }
    }

    private func separator(palette: FoundationPalette) -> some View {
        Capsule().fill(palette.foreground).frame(width: 10, height: 1.33)
            .frame(width: 16)
    }

    private func novaGroup(
        _ group: Range<Int>, characters: [Character], palette: FoundationPalette, shape: RoundedRectangle
    ) -> some View {
        let fill = colorScheme == .dark ? palette.novaDarkOutlineFill : Color.clear
        let active = isEnabled && isEditing ? min(characters.count, length - 1) : nil
        return HStack(spacing: 0) {
            ForEach(group, id: \.self) { index in
                Text(index < characters.count ? String(characters[index]) : " ")
                    .font(font ?? .system(size: 14).monospacedDigit())
                    .foregroundColor(palette.foreground)
                    .frame(minWidth: 0, idealWidth: 32, maxWidth: 32, minHeight: 32, maxHeight: 32)
                    .overlay(
                        Group {
                            if index < group.upperBound - 1 {
                                Rectangle().fill(palette.input).frame(width: 1)
                            }
                        },
                        alignment: .trailing
                    )
                    .overlay(
                        Rectangle().strokeBorder(active == index ? palette.ring : Color.clear)
                    )
            }
        }
        .background(shape.fill(fill))
        .overlay(shape.strokeBorder(palette.input))
        .clipShape(shape)
    }

    @ViewBuilder
    private var field: some View {
        let value = Binding(get: { sanitized(code) }, set: { code = sanitized($0) })
        let field = TextField("", text: value, onEditingChanged: { isEditing = $0 })
            .foregroundColor(.clear)
            .accentColor(.clear)
            .textFieldStyle(PlainTextFieldStyle())
            .accessibilityLabel(Text(label))
        #if os(iOS)
        if pattern == .alphanumeric {
            field.keyboardType(.asciiCapable)
                .autocapitalization(.none)
                .disableAutocorrection(true)
                .textContentType(.oneTimeCode)
        } else {
            field.keyboardType(.numberPad).textContentType(.oneTimeCode)
        }
        #else
        if pattern == .alphanumeric {
            field.disableAutocorrection(true)
        } else {
            field
        }
        #endif
    }

    private func sanitized(_ value: String) -> String {
        switch pattern {
        case .digits:
            return value.compactMap(\.wholeNumberValue).filter { (0...9).contains($0) }.prefix(length).map(String.init).joined()
        case .alphanumeric:
            let accepted = value.utf8.lazy.filter { byte in
                (48...57).contains(byte) || (65...90).contains(byte) || (97...122).contains(byte)
            }
            return String(decoding: accepted.prefix(length), as: UTF8.self)
        }
    }
}
