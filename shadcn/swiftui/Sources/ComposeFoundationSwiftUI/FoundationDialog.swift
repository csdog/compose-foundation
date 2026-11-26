import SwiftUI

public struct FoundationDialog<DialogContent: View>: ViewModifier {
    @Environment(\.foundationTheme) private var theme
    @Environment(\.colorScheme) private var colorScheme
    @Binding private var isPresented: Bool

    private let title: String
    private let description: String?
    private let onDismiss: () -> Void
    private let dialogContent: () -> DialogContent

    public init(
        isPresented: Binding<Bool>,
        title: String,
        description: String? = nil,
        @ViewBuilder content: @escaping () -> DialogContent
    ) {
        self.init(isPresented: isPresented, title: title, description: description, onDismiss: {}, content: content)
    }

    public init(
        isPresented: Binding<Bool>,
        title: String,
        description: String? = nil,
        onDismiss: @escaping () -> Void,
        @ViewBuilder content: @escaping () -> DialogContent
    ) {
        self._isPresented = isPresented
        self.title = title
        self.description = description
        self.onDismiss = onDismiss
        self.dialogContent = content
    }

    public func body(content: Content) -> some View {
        content.sheet(isPresented: $isPresented, onDismiss: onDismiss) {
            let palette = theme.palette(for: colorScheme)

            VStack(alignment: .leading, spacing: 14) {
                FoundationPresentationHeader(
                    title: title,
                    description: description.map { Text($0).font(.subheadline).foregroundColor(palette.mutedForeground) },
                    alignment: .top,
                    close: Button {
                        isPresented = false
                    } label: {
                        Image(systemName: "xmark").frame(minWidth: 44, minHeight: 44)
                    }
                    .buttonStyle(PlainButtonStyle())
                    .accessibilityLabel(Text("Close")),
                    dismiss: { isPresented = false }
                )
                dialogContent()
            }
            .foregroundColor(palette.foreground)
            .padding(20)
            .background(palette.card)
            .accessibilityAction(.escape) { isPresented = false }
        }
    }
}
