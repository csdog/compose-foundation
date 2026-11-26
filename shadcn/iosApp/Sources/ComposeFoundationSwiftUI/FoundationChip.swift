import SwiftUI

public struct FoundationChip: View {
    @Environment(\.foundationTheme) private var theme
    @Environment(\.colorScheme) private var colorScheme
    @Binding private var isSelected: Bool

    private let title: String

    public init(_ title: String, isSelected: Binding<Bool>) {
        self.title = title
        self._isSelected = isSelected
    }

    public var body: some View {
        let palette = theme.palette(for: colorScheme)

        Button {
            isSelected.toggle()
        } label: {
            Text(title)
                .font(.subheadline)
                .foregroundColor(isSelected ? palette.primaryForeground : palette.foreground)
                .padding(.horizontal, 12)
                .padding(.vertical, 5)
                .background(Capsule().fill(isSelected ? palette.primary : palette.secondary))
                .frame(minHeight: 44)
                .contentShape(Rectangle())
        }
        .buttonStyle(PlainButtonStyle())
        .accessibilityAddTraits(isSelected ? .isSelected : [])
    }
}
