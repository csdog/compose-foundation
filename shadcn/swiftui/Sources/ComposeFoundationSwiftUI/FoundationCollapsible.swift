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
