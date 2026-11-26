import SwiftUI

public struct FoundationSheet<SheetContent: View>: ViewModifier {
    @Environment(\.foundationTheme) private var theme
    @Environment(\.colorScheme) private var colorScheme
    @Binding private var isPresented: Bool

    private let title: String
    private let onDismiss: () -> Void
    private let sheetContent: () -> SheetContent

    public init(
        isPresented: Binding<Bool>,
        title: String,
        @ViewBuilder content: @escaping () -> SheetContent
    ) {
        self.init(isPresented: isPresented, title: title, onDismiss: {}, content: content)
    }

    public init(
        isPresented: Binding<Bool>,
        title: String,
        onDismiss: @escaping () -> Void,
        @ViewBuilder content: @escaping () -> SheetContent
    ) {
        self._isPresented = isPresented
        self.title = title
        self.onDismiss = onDismiss
        self.sheetContent = content
    }

    public func body(content: Content) -> some View {
        content.sheet(isPresented: $isPresented, onDismiss: onDismiss) {
            let palette = theme.palette(for: colorScheme)

            VStack(alignment: .leading, spacing: 12) {
                FoundationPresentationHeader(
                    title: title, description: nil, alignment: .center,
                    close: Button("Done") { isPresented = false },
                    dismiss: { isPresented = false }
                )
                sheetContent()
            }
            .padding(16)
            .foregroundColor(palette.foreground)
            .background(palette.card)
            .accentColor(palette.primary)
            .accessibilityAction(.escape) { isPresented = false }
        }
    }
}
