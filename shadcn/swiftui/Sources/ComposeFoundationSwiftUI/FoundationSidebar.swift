import SwiftUI

public struct FoundationSidebar<Sidebar: View, Main: View>: View {
    @Environment(\.foundationTheme) private var theme
    @Environment(\.colorScheme) private var colorScheme
    @Environment(\.accessibilityReduceMotion) private var reduceMotion
    @Binding private var isOpen: Bool

    private let width: CGFloat
    private let compactWidth: CGFloat
    private let title: String
    private let sidebar: Sidebar
    private let main: Main

    public init(
        title: String = "Sidebar",
        isOpen: Binding<Bool>,
        width: CGFloat = 240,
        compactWidth: CGFloat = 680,
        @ViewBuilder sidebar: () -> Sidebar,
        @ViewBuilder content: () -> Main
    ) {
        self.title = title
        self._isOpen = isOpen
        self.width = width.isFinite ? max(44, width) : 240
        self.compactWidth = compactWidth.isFinite ? max(0, compactWidth) : 680
        self.sidebar = sidebar()
        self.main = content()
    }

    public var body: some View {
        let palette = theme.palette(for: colorScheme)

        GeometryReader { geometry in
            let compact = geometry.size.width < compactWidth
            if compact {
                ZStack(alignment: .leading) {
                    main.frame(maxWidth: .infinity, maxHeight: .infinity)
                        .accessibilityHidden(isOpen)
                    if isOpen {
                        Color.black.opacity(0.38).edgesIgnoringSafeArea(.all)
                            .onTapGesture { isOpen = false }
                            .accessibilityHidden(true)
                        rail(width: min(width, geometry.size.width), palette: palette)
                            .transition(.move(edge: .leading))
                    }
                }
                .animation(reduceMotion ? nil : .easeInOut(duration: 0.18), value: isOpen)
            } else {
                HStack(spacing: 0) {
                    if isOpen { rail(width: min(width, geometry.size.width * 0.8), palette: palette) }
                    main.frame(maxWidth: .infinity, maxHeight: .infinity)
                }
            }
        }
    }

    private func rail(width: CGFloat, palette: FoundationPalette) -> some View {
        VStack(alignment: .leading, spacing: 8) {
            HStack {
                Text(title).font(.headline).accessibilityAddTraits(.isHeader)
                Spacer(minLength: 0)
                Button {
                    isOpen = false
                } label: {
                    Image(systemName: "xmark").padding(8)
                }
                .buttonStyle(PlainButtonStyle())
                .accessibilityLabel(Text("Close sidebar"))
            }
            ScrollView { sidebar.frame(maxWidth: .infinity, alignment: .leading) }
        }
        .padding(12)
        .frame(width: width)
        .frame(maxHeight: .infinity, alignment: .top)
        .foregroundColor(palette.foreground)
        .background(palette.card)
        .overlay(Rectangle().fill(palette.border).frame(width: 1).allowsHitTesting(false), alignment: .trailing)
        .accessibilityElement(children: .contain)
    }
}
