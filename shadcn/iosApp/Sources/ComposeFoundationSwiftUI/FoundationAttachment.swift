import SwiftUI

public struct FoundationAttachment<Media: View>: View {
    public enum State: Equatable {
        case ready, uploading(Double), processing, failed(String), complete
    }

    @Environment(\.foundationTheme) private var theme
    @Environment(\.colorScheme) private var colorScheme

    private let title: String
    private let detail: String?
    private let state: State
    private let onRemove: (() -> Void)?
    private let media: Media

    public init(
        _ title: String,
        detail: String? = nil,
        state: State = .ready,
        onRemove: (() -> Void)? = nil,
        @ViewBuilder media: () -> Media
    ) {
        self.title = title
        self.detail = detail
        self.state = state
        self.onRemove = onRemove
        self.media = media()
    }

    public var body: some View {
        let palette = theme.palette(for: colorScheme)
        let shape = RoundedRectangle(cornerRadius: theme.cornerRadius)

        HStack(spacing: 9) {
            media.scaledToFill()
                .frame(width: 32, height: 32)
                .background(palette.secondary)
                .clipShape(RoundedRectangle(cornerRadius: theme.cornerRadius / 2))
                .accessibilityHidden(true)
            VStack(alignment: .leading, spacing: 4) {
                Text(title).font(.subheadline.weight(.medium))
                if let detail {
                    Text(detail).font(.caption).foregroundColor(palette.mutedForeground)
                }
                status(palette: palette)
            }
            Spacer(minLength: 0)
            if let onRemove {
                Button(action: onRemove) {
                    Image(systemName: "xmark").frame(minWidth: 44, minHeight: 44).contentShape(Rectangle())
                }
                .buttonStyle(PlainButtonStyle())
                .accessibilityLabel(Text("Remove \(title)"))
            }
        }
        .foregroundColor(palette.foreground)
        .padding(.horizontal, 10)
        .padding(.vertical, 5)
        .background(shape.fill(palette.card))
        .overlay(shape.strokeBorder(palette.border).allowsHitTesting(false))
    }

    @ViewBuilder
    private func status(palette: FoundationPalette) -> some View {
        switch state {
        case .ready:
            EmptyView()
        case let .uploading(value):
            ProgressView(value: min(max(value.isFinite ? value : 0, 0), 1)) {
                Text("Uploading").font(.caption)
            }
            .accentColor(palette.primary)
        case .processing:
            ProgressView().accessibilityLabel(Text("Processing"))
        case let .failed(message):
            Text(message).font(.caption).foregroundColor(palette.destructive)
        case .complete:
            Label("Complete", systemImage: "checkmark.circle").font(.caption)
        }
    }
}
