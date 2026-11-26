import SwiftUI

public struct FoundationCodeBlock: View {
    @Environment(\.foundationTheme) private var theme
    @Environment(\.colorScheme) private var colorScheme

    private let code: String
    private let language: String?
    private let showLineNumbers: Bool

    public init(_ code: String, language: String? = nil, showLineNumbers: Bool = false) {
        self.code = code
        self.language = language
        self.showLineNumbers = showLineNumbers
    }

    public var body: some View {
        let palette = theme.palette(for: colorScheme)
        let shape = RoundedRectangle(cornerRadius: theme.cornerRadius)
        let lines = code.components(separatedBy: "\n")

        VStack(alignment: .leading, spacing: 8) {
            if let language {
                Text(language).font(.caption.weight(.medium)).foregroundColor(palette.mutedForeground)
            }
            ScrollView(.horizontal) {
                VStack(alignment: .leading, spacing: 2) {
                    ForEach(lines.indices, id: \.self) { index in
                        HStack(spacing: 12) {
                            if showLineNumbers {
                                Text(String(index + 1))
                                    .foregroundColor(palette.mutedForeground)
                                    .accessibilityHidden(true)
                            }
                            Text(lines[index].isEmpty ? " " : lines[index])
                                .foregroundColor(palette.foreground)
                        }
                    }
                }
                .font(.system(.footnote, design: .monospaced))
            }
        }
        .padding(12)
        .background(shape.fill(palette.secondary))
        .overlay(shape.strokeBorder(palette.border).allowsHitTesting(false))
    }
}
