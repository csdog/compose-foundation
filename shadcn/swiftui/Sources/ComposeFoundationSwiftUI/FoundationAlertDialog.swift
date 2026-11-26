import SwiftUI

public struct FoundationAlertDialog: ViewModifier {
    @Binding private var isPresented: Bool

    private let title: String
    private let message: String
    private let confirmTitle: String
    private let destructive: Bool
    private let onConfirm: () -> Void

    public init(
        isPresented: Binding<Bool>,
        title: String,
        message: String,
        confirmTitle: String = "Continue",
        destructive: Bool = false,
        onConfirm: @escaping () -> Void
    ) {
        self._isPresented = isPresented
        self.title = title
        self.message = message
        self.confirmTitle = confirmTitle
        self.destructive = destructive
        self.onConfirm = onConfirm
    }

    public func body(content: Content) -> some View {
        content.alert(isPresented: $isPresented) {
            let confirm: Alert.Button = destructive
                ? .destructive(Text(confirmTitle), action: onConfirm)
                : .default(Text(confirmTitle), action: onConfirm)
            return Alert(
                title: Text(title),
                message: Text(message),
                primaryButton: confirm,
                secondaryButton: .cancel()
            )
        }
    }
}
