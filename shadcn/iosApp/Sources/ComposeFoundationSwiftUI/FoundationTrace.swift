import SwiftUI

public struct FoundationTrace<Content: View>: View {
    public enum Status: Sendable { case pending, running, succeeded, failed }

    @Environment(\.foundationTheme) private var theme
    @Environment(\.colorScheme) private var colorScheme
    @Binding private var isExpanded: Bool

    private let title: String
    private let status: Status
    private let content: Content

    public init(
        _ title: String,
        status: Status,
        isExpanded: Binding<Bool>,
        @ViewBuilder content: () -> Content
    ) {
        self.title = title
        self.status = status
        self._isExpanded = isExpanded
        self.content = content()
    }

    public var body: some View {
        let palette = theme.palette(for: colorScheme)
        let shape = RoundedRectangle(cornerRadius: theme.cornerRadius)

        DisclosureGroup(isExpanded: $isExpanded) {
            content.padding(.top, 6)
        } label: {
            HStack(spacing: 8) {
                if status == .running {
                    ProgressView().accessibilityLabel(Text("Running"))
                } else {
                    Image(systemName: icon)
                        .foregroundColor(status == .failed ? palette.destructive : palette.mutedForeground)
                        .accessibilityLabel(Text(statusLabel))
                }
                Text(title).font(.subheadline.weight(.medium))
            }
        }
        .padding(10)
        .foregroundColor(palette.foreground)
        .background(shape.fill(palette.secondary))
        .overlay(shape.strokeBorder(palette.border).allowsHitTesting(false))
    }

    private var icon: String {
        switch status {
        case .pending, .running: "clock"
        case .succeeded: "checkmark.circle"
        case .failed: "exclamationmark.circle"
        }
    }

    private var statusLabel: String {
        switch status {
        case .pending: "Pending"
        case .running: "Running"
        case .succeeded: "Succeeded"
        case .failed: "Failed"
        }
    }
}
