import SwiftUI

public enum FoundationSidebarSide: Sendable {
    case leading, trailing
}

public struct FoundationSidebar<Sidebar: View, Main: View>: View {
    @Environment(\.foundationTheme) private var theme
    @Environment(\.colorScheme) private var colorScheme
    @Environment(\.accessibilityReduceMotion) private var reduceMotion
    @Environment(\.layoutDirection) private var layoutDirection
    @Binding private var isOpen: Bool

    private let width: CGFloat
    private let compactWidth: CGFloat
    private let title: String
    private let side: FoundationSidebarSide
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
        self.init(title: title, isOpen: isOpen, side: .leading, width: width, compactWidth: compactWidth,
                  sidebar: sidebar, content: content)
    }

    public init(
        title: String = "Sidebar",
        isOpen: Binding<Bool>,
        side: FoundationSidebarSide,
        width: CGFloat = 240,
        compactWidth: CGFloat = 680,
        @ViewBuilder sidebar: () -> Sidebar,
        @ViewBuilder content: () -> Main
    ) {
        self.title = title
        self._isOpen = isOpen
        self.width = width.isFinite ? max(44, width) : 240
        self.compactWidth = compactWidth.isFinite ? max(0, compactWidth) : 680
        self.side = side
        self.sidebar = sidebar()
        self.main = content()
    }

    public var body: some View {
        let palette = theme.palette(for: colorScheme)

        GeometryReader { geometry in
            let compact = geometry.size.width < compactWidth
            if compact {
                ZStack(alignment: side == .leading ? .leading : .trailing) {
                    main.frame(maxWidth: .infinity, maxHeight: .infinity)
                        .accessibilityHidden(isOpen)
                    if isOpen {
                        Color.black.opacity(0.38).edgesIgnoringSafeArea(.all)
                            .onTapGesture { isOpen = false }
                            .accessibilityHidden(true)
                        rail(width: min(width, geometry.size.width), palette: palette, compact: true)
                            .transition(.move(edge: side == .leading ? .leading : .trailing))
                    }
                }
                .animation(reduceMotion ? nil : .easeInOut(duration: 0.18), value: isOpen)
            } else {
                HStack(spacing: 0) {
                    if isOpen {
                        rail(width: min(width, geometry.size.width * 0.8), palette: palette, compact: false)
                            .environment(\.layoutDirection, layoutDirection)
                    }
                    main.frame(maxWidth: .infinity, maxHeight: .infinity)
                        .environment(\.layoutDirection, layoutDirection)
                }
                .environment(\.layoutDirection, stackingDirection)
            }
        }
    }

    private var stackingDirection: LayoutDirection {
        guard side == .trailing else { return layoutDirection }
        return layoutDirection == .leftToRight ? .rightToLeft : .leftToRight
    }

    private func rail(width: CGFloat, palette: FoundationPalette, compact: Bool) -> some View {
        VStack(alignment: .leading, spacing: 8) {
            HStack {
                Text(title).font(.headline).accessibilityAddTraits(.isHeader)
                Spacer(minLength: 0)
                if compact {
                    closeButton.keyboardShortcut(KeyboardShortcut.cancelAction)
                } else {
                    closeButton
                }
            }
            ScrollView { sidebar.frame(maxWidth: .infinity, alignment: .leading) }
        }
        .padding(12)
        .frame(width: width)
        .frame(maxHeight: .infinity, alignment: .top)
        .foregroundColor(palette.foreground)
        .background(palette.card)
        .overlay(Rectangle().fill(palette.border).frame(width: 1).allowsHitTesting(false),
                 alignment: side == .leading ? .trailing : .leading)
        .accessibilityElement(children: .contain)
    }

    private var closeButton: some View {
        Button {
            isOpen = false
        } label: {
            Image(systemName: "xmark").padding(8)
        }
        .buttonStyle(PlainButtonStyle())
        .accessibilityLabel(Text("Close sidebar"))
    }
}

/// A visible toggle for a controlled sidebar. A nil shortcut disables the accelerator.
public struct FoundationSidebarTrigger<Label: View>: View {
    @Binding private var isOpen: Bool
    private let shortcut: KeyboardShortcut?
    private let label: Label

    public init(
        isOpen: Binding<Bool>,
        shortcut: KeyboardShortcut? = KeyboardShortcut("b", modifiers: .command),
        @ViewBuilder label: () -> Label
    ) {
        self._isOpen = isOpen
        self.shortcut = shortcut
        self.label = label()
    }

    @ViewBuilder
    public var body: some View {
        if let shortcut {
            button.keyboardShortcut(shortcut)
        } else {
            button
        }
    }

    private var button: some View {
        Button { isOpen.toggle() } label: { label }
            .accessibilityValue(isOpen ? Text("Expanded") : Text("Collapsed"))
    }
}
