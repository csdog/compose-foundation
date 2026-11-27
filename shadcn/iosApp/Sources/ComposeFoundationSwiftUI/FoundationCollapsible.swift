import SwiftUI

public struct FoundationCollapsible<Content: View>: View {
    @Environment(\.foundationTheme) private var theme
    @Environment(\.colorScheme) private var colorScheme
    @Binding private var isExpanded: Bool

    private let title: String
    private let content: Content

    public init(_ title: String, isExpanded: Binding<Bool>, @ViewBuilder content: () -> Content) {
        self.title = title
        self._isExpanded = isExpanded
        self.content = content()
    }

    public var body: some View {
        let palette = theme.palette(for: colorScheme)

        DisclosureGroup(isExpanded: $isExpanded) {
            content.padding(.top, 4)
        } label: {
            Text(title).font(.subheadline.weight(.medium))
        }
        .foregroundColor(palette.foreground)
        .accentColor(palette.primary)
    }
}

/// A button that toggles a shared expansion binding. Use a button style to customize its appearance.
public struct FoundationCollapsibleTrigger<Label: View>: View {
    @Binding private var isExpanded: Bool
    private let label: (Bool) -> Label

    public init(isExpanded: Binding<Bool>, @ViewBuilder label: @escaping (Bool) -> Label) {
        self._isExpanded = isExpanded
        self.label = label
    }

    public var body: some View {
        Button {
            isExpanded.toggle()
        } label: {
            label(isExpanded)
        }
        .accessibilityValue(isExpanded ? Text("Expanded") : Text("Collapsed"))
    }
}

/// An optional panel controlled by the same expansion state as its trigger.
public struct FoundationCollapsibleContent<Content: View>: View {
    private let isExpanded: Bool
    private let content: () -> Content

    public init(isExpanded: Bool, @ViewBuilder content: @escaping () -> Content) {
        self.isExpanded = isExpanded
        self.content = content
    }

    public var body: some View {
        if isExpanded {
            content()
        }
    }
}
