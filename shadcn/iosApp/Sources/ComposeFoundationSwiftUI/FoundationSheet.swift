import SwiftUI
#if os(macOS)
import AppKit
#endif

public struct FoundationSheet<SheetContent: View>: ViewModifier {
    @Environment(\.foundationTheme) private var theme
    @Environment(\.colorScheme) private var colorScheme
    @Environment(\.accessibilityReduceMotion) private var reduceMotion
    @Environment(\.accessibilityReduceTransparency) private var reduceTransparency
    @ScaledMetric(relativeTo: .body) private var titleSize: CGFloat = 16
    @ScaledMetric(relativeTo: .subheadline) private var descriptionSize: CGFloat = 14
    @Binding private var isPresented: Bool

    private let title: String
    private let description: String?
    private let edge: Edge?
    private let onDismiss: () -> Void
    private let footer: AnyView?
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
        self.init(isPresented: isPresented, title: title, description: nil, edge: nil,
                  onDismiss: onDismiss, footer: nil, content: content)
    }

    /// Positions the sheet on a logical edge. Apply to a container that fills the presentation area.
    public init(
        isPresented: Binding<Bool>,
        title: String,
        description: String? = nil,
        side: Edge,
        onDismiss: @escaping () -> Void = {},
        @ViewBuilder content: @escaping () -> SheetContent
    ) {
        self.init(isPresented: isPresented, title: title, description: description, edge: side,
                  onDismiss: onDismiss, footer: nil, content: content)
    }

    /// Adds a footer that stays visible when the content scrolls.
    public init<Footer: View>(
        isPresented: Binding<Bool>,
        title: String,
        description: String? = nil,
        side: Edge,
        onDismiss: @escaping () -> Void = {},
        @ViewBuilder footer: () -> Footer,
        @ViewBuilder content: @escaping () -> SheetContent
    ) {
        self.init(isPresented: isPresented, title: title, description: description, edge: side,
                  onDismiss: onDismiss, footer: AnyView(footer()), content: content)
    }

    private init(
        isPresented: Binding<Bool>, title: String, description: String?, edge: Edge?,
        onDismiss: @escaping () -> Void, footer: AnyView?, @ViewBuilder content: @escaping () -> SheetContent
    ) {
        self._isPresented = isPresented
        self.title = title
        self.description = description
        self.edge = edge
        self.onDismiss = onDismiss
        self.footer = footer
        self.sheetContent = content
    }

    @ViewBuilder
    public func body(content: Content) -> some View {
        let palette = theme.palette(for: colorScheme)
        if let edge {
            content
                .disabled(isPresented)
                .allowsHitTesting(!isPresented)
                .accessibilityHidden(isPresented)
                .compositingGroup()
                .blur(radius: isPresented && !reduceTransparency ? 4 : 0)
                .overlay(Group {
                    if isPresented {
                        GeometryReader { geometry in
                            ZStack {
                                Color.black.opacity(0.10)
                                    .onTapGesture(perform: closeFromBackdrop)
                                    .accessibilityHidden(true)
                                positionedPanel(edge: edge, size: geometry.size, palette: palette)
                            }
                            .frame(maxWidth: .infinity, maxHeight: .infinity)
                        }
                        .transition(.opacity)
                        .onDisappear {
                            guard !isPresented else { return }
                            DispatchQueue.main.async {
                                if !isPresented { onDismiss() }
                            }
                        }
                    }
                })
                .animation(reduceMotion ? nil : .easeInOut(duration: 0.20), value: isPresented)
        } else {
            content.sheet(isPresented: $isPresented, onDismiss: onDismiss) {
                VStack(alignment: .leading, spacing: 12) {
                    FoundationPresentationHeader(
                        title: title, description: nil, alignment: .center,
                        close: Button("Done", action: close), dismiss: close
                    )
                    sheetContent()
                }
                .padding(16)
                .foregroundColor(palette.foreground)
                .background(palette.card)
                .accentColor(palette.primary)
                .accessibilityAction(.escape) { close() }
            }
        }
    }

    private func positionedPanel(edge: Edge, size: CGSize, palette: FoundationPalette) -> some View {
        let horizontal = edge == .leading || edge == .trailing
        let edgeWidth = size.width * 0.75
        let width = horizontal ? (size.width >= 640 ? min(edgeWidth, 384) : edgeWidth) : size.width

        return styledPanel(palette, fullHeight: horizontal)
            .frame(width: max(1, width), height: horizontal ? size.height : nil)
            .background(palette.card)
            .overlay(
                Rectangle().fill(palette.border)
                    .frame(width: horizontal ? 1 : nil, height: horizontal ? nil : 1)
                    .allowsHitTesting(false)
                    .accessibilityHidden(true),
                alignment: borderAlignment(for: edge)
            )
            .compositingGroup()
            .shadow(color: .black.opacity(0.12), radius: 8)
            .transition(.move(edge: edge))
            .frame(maxWidth: .infinity, maxHeight: .infinity, alignment: panelAlignment(for: edge))
    }

    private func panelAlignment(for edge: Edge) -> Alignment {
        switch edge {
        case .top: return .top
        case .bottom: return .bottom
        case .leading: return .leading
        case .trailing: return .trailing
        }
    }

    private func borderAlignment(for edge: Edge) -> Alignment {
        switch edge {
        case .top: return .bottom
        case .bottom: return .top
        case .leading: return .trailing
        case .trailing: return .leading
        }
    }

    private func styledPanel(_ palette: FoundationPalette, fullHeight: Bool) -> some View {
        let heading = Text(title)
            .font(.system(size: titleSize, weight: .medium))
            .accessibilityAddTraits(.isHeader)
        let closeControl = Button(action: close) {
            Image(systemName: "xmark")
                .font(.system(size: 12))
                .frame(width: 44, height: 44)
                .contentShape(Rectangle())
        }
        .buttonStyle(PlainButtonStyle())
        .foregroundColor(palette.mutedForeground)
        .accessibilityLabel(Text("Close"))
        .keyboardShortcut(.cancelAction)

        return Group {
            if #available(iOS 17, macOS 14, *) {
                FoundationPresentationFocus(heading: heading, close: closeControl, onActivate: close) { heading, close in
                    regions(heading: heading, close: close, palette: palette, fullHeight: fullHeight)
                }
            } else {
                regions(heading: heading, close: closeControl, palette: palette, fullHeight: fullHeight)
            }
        }
        .foregroundColor(palette.foreground)
        .environment(\.foundationSurface, .card)
        .accentColor(palette.primary)
        .accessibilityElement(children: .contain)
        .accessibilityAction(.escape) { close() }
    }

    private func regions<Heading: View, Close: View>(
        heading: Heading, close: Close, palette: FoundationPalette, fullHeight: Bool
    ) -> some View {
        VStack(spacing: 0) {
            VStack(alignment: .leading, spacing: 2) {
                heading.padding(.trailing, 28)
                if let description {
                    Text(description)
                        .font(.system(size: descriptionSize))
                        .foregroundColor(palette.mutedForeground)
                }
            }
            .frame(maxWidth: .infinity, alignment: .leading)
            .padding(16)
            .overlay(close, alignment: .topTrailing)
            .layoutPriority(1)

            let content = sheetContent()
                .frame(maxWidth: .infinity, alignment: .leading)
                .padding(16)
            if fullHeight {
                ScrollView { content }
            } else if #available(iOS 16, macOS 13, *) {
                ViewThatFits(in: .vertical) {
                    content.fixedSize(horizontal: false, vertical: true)
                    ScrollView { content }
                }
            } else {
                ScrollView { content }
            }

            if let footer {
                footer
                    .frame(maxWidth: .infinity, alignment: .leading)
                    .padding(16)
                    .layoutPriority(1)
            }
        }
    }

    private func closeFromBackdrop() {
        #if os(macOS)
        // Let the platform field editor relinquish focus before removing its sheet.
        if let window = NSApplication.shared.keyWindow,
           let editor = window.firstResponder as? NSTextView, editor.isFieldEditor {
            window.makeFirstResponder(nil)
        }
        #endif
        close()
    }

    private func close() {
        isPresented = false
    }
}
