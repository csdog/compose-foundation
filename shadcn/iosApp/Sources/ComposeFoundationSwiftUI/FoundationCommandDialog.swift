import SwiftUI

/// Presents a command palette in the modified container or its macOS sheet.
public struct FoundationCommandDialog<Value: Hashable>: ViewModifier {
    @Binding private var isPresented: Bool
    @State private var query = ""
    private let title: String
    private let sections: [FoundationCommand<Value>.Section]
    private let searchPrompt: String
    private let emptyMessage: String
    private let actionAppearance: [Value: FoundationCommand<Value>.ActionAppearance]
    private let focusSearchOnOpen: Bool
    private let onDismiss: () -> Void
    private let onAction: (Value) -> Void

    public init(
        isPresented: Binding<Bool>,
        title: String = "Command palette",
        sections: [FoundationCommand<Value>.Section],
        searchPrompt: String = "Search commands",
        emptyMessage: String = "No matching commands",
        actionAppearance: [Value: FoundationCommand<Value>.ActionAppearance] = [:],
        focusSearchOnOpen: Bool = true,
        onDismiss: @escaping () -> Void = {},
        onAction: @escaping (Value) -> Void
    ) {
        self._isPresented = isPresented
        self.title = title
        self.sections = sections
        self.searchPrompt = searchPrompt
        self.emptyMessage = emptyMessage
        self.actionAppearance = actionAppearance
        self.focusSearchOnOpen = focusSearchOnOpen
        self.onDismiss = onDismiss
        self.onAction = onAction
    }

    public func body(content: Content) -> some View {
        let presentation: FoundationDialog<FoundationCommand<Value>>.Presentation
        #if os(macOS)
        presentation = .sheet
        #else
        presentation = .centered
        #endif
        return content.modifier(FoundationDialog(isPresented: $isPresented, title: title, presentation: presentation,
                                                 onDismiss: onDismiss) {
            FoundationCommand(sections: sections, query: $query, searchPrompt: searchPrompt,
                              emptyMessage: emptyMessage, actionAppearance: actionAppearance) { value in
                close()
                onAction(value)
            }
            .presentedInDialog(focusSearch: focusSearchOnOpen, dismiss: close)
        }.commandPresentation(focusSearchOnOpen: focusSearchOnOpen))
        .onChange(of: isPresented) { presented in
            if !presented { query = "" }
        }
    }

    private func close() {
        isPresented = false
    }
}
