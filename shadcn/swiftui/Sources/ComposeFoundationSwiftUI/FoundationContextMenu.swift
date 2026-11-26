import SwiftUI

public struct FoundationContextMenu<Actions: View>: ViewModifier {
    private let actions: () -> Actions

    public init(@ViewBuilder actions: @escaping () -> Actions) {
        self.actions = actions
    }

    public func body(content: Content) -> some View {
        content.contextMenu(menuItems: actions)
    }
}
