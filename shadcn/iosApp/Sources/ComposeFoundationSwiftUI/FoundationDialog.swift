import SwiftUI

public struct FoundationDialog<DialogContent: View>: ViewModifier {
    public enum Presentation: Sendable {
        case sheet, centered
    }

    @Environment(\.foundationTheme) private var theme
    @Environment(\.colorScheme) private var colorScheme
    @Environment(\.accessibilityReduceMotion) private var reduceMotion
    @Environment(\.accessibilityReduceTransparency) private var reduceTransparency
    @ScaledMetric(relativeTo: .body) private var titleSize: CGFloat = 16
    @ScaledMetric(relativeTo: .subheadline) private var descriptionSize: CGFloat = 14
    @Binding private var isPresented: Bool
    @State private var presentedPanel: UUID?

    private let title: String
    private let description: String?
    private let presentation: Presentation
    private let onDismiss: () -> Void
    private let footer: AnyView?
    private let dialogContent: () -> DialogContent
    private var commandFocusSearchOnOpen: Bool?

    public init(
        isPresented: Binding<Bool>,
        title: String,
        description: String? = nil,
        @ViewBuilder content: @escaping () -> DialogContent
    ) {
        self.init(isPresented: isPresented, title: title, description: description, onDismiss: {}, content: content)
    }

    public init(
        isPresented: Binding<Bool>,
        title: String,
        description: String? = nil,
        onDismiss: @escaping () -> Void,
        @ViewBuilder content: @escaping () -> DialogContent
    ) {
        self.init(isPresented: isPresented, title: title, description: description,
                  presentation: .sheet, onDismiss: onDismiss, footer: nil, content: content)
    }

    /// A centered presentation covers the modified view. Use a container that fills the presentation area.
    public init(
        isPresented: Binding<Bool>,
        title: String,
        description: String? = nil,
        presentation: Presentation,
        onDismiss: @escaping () -> Void = {},
        @ViewBuilder content: @escaping () -> DialogContent
    ) {
        self.init(isPresented: isPresented, title: title, description: description,
                  presentation: presentation, onDismiss: onDismiss, footer: nil, content: content)
    }

    /// Adds a footer that stays visible while the centered dialog content scrolls.
    public init<Footer: View>(
        isPresented: Binding<Bool>,
        title: String,
        description: String? = nil,
        presentation: Presentation,
        onDismiss: @escaping () -> Void = {},
        @ViewBuilder footer: () -> Footer,
        @ViewBuilder content: @escaping () -> DialogContent
    ) {
        self.init(isPresented: isPresented, title: title, description: description,
                  presentation: presentation, onDismiss: onDismiss, footer: AnyView(footer()), content: content)
    }

    private init(
        isPresented: Binding<Bool>, title: String, description: String?, presentation: Presentation,
        onDismiss: @escaping () -> Void, footer: AnyView?, @ViewBuilder content: @escaping () -> DialogContent
    ) {
        self._isPresented = isPresented
        self.title = title
        self.description = description
        self.presentation = presentation
        self.onDismiss = onDismiss
        self.footer = footer
        self.dialogContent = content
    }

    func commandPresentation(focusSearchOnOpen: Bool) -> Self {
        var dialog = self
        dialog.commandFocusSearchOnOpen = focusSearchOnOpen
        return dialog
    }

    @ViewBuilder
    public func body(content: Content) -> some View {
        let palette = theme.palette(for: colorScheme)
        if presentation == .centered {
            animateCentered(content
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
                                    .onTapGesture {
                                        foundationEndEditingBeforeBackdropDismissal()
                                        close()
                                    }
                                    .accessibilityHidden(true)
                                centeredPanel(palette)
                                    .frame(width: min(384, max(1, geometry.size.width - 32)))
                                    .padding(.vertical, 16)
                            }
                            .frame(maxWidth: .infinity, maxHeight: .infinity)
                        }
                        .transition(.opacity)
                        .onAppear {
                            if waitsForAnimationCompletion { presentedPanel = UUID() }
                        }
                        .onDisappear {
                            guard !waitsForAnimationCompletion, !isPresented else { return }
                            DispatchQueue.main.async {
                                if !isPresented { onDismiss() }
                            }
                        }
                    }
                }))
        } else {
            content.sheet(isPresented: $isPresented, onDismiss: onDismiss) {
                VStack(alignment: .leading, spacing: 14) {
                    FoundationPresentationHeader(
                        title: title,
                        description: description.map {
                            Text($0).font(.subheadline).foregroundColor(palette.mutedForeground)
                        },
                        alignment: .top, close: closeButton, dismiss: close
                    )
                    dialogContent()
                    footer
                }
                .foregroundColor(palette.foreground)
                .padding(20)
                .background(palette.card)
                .accessibilityAction(.escape) { close() }
            }
        }
    }

    private var waitsForAnimationCompletion: Bool {
        if #available(iOS 17, macOS 14, *) { return commandFocusSearchOnOpen != nil }
        return false
    }

    @ViewBuilder
    private func animateCentered<Panel: View>(_ content: Panel) -> some View {
        let animated = content.animation(reduceMotion ? nil : .easeOut(duration: 0.10), value: isPresented)
        if #available(iOS 17, macOS 14, *), waitsForAnimationCompletion {
            animated.transaction(value: isPresented) { transaction in
                guard !isPresented, let panel = presentedPanel else { return }
                transaction.addAnimationCompletion(criteria: .removed) {
                    DispatchQueue.main.async {
                        guard !isPresented, presentedPanel == panel else { return }
                        presentedPanel = nil
                        onDismiss()
                    }
                }
            }
        } else {
            animated
        }
    }

    private var closeButton: some View {
        Button(action: close) {
            Image(systemName: "xmark")
                .frame(minWidth: 44, minHeight: 44)
                .contentShape(Rectangle())
        }
        .buttonStyle(PlainButtonStyle())
        .accessibilityLabel(Text("Close"))
    }

    private func centeredPanel(_ palette: FoundationPalette) -> some View {
        let shape = RoundedRectangle(cornerRadius: theme.popoverCornerRadius + 2)
        let heading = Text(title)
            .font(.system(size: titleSize, weight: .medium))
            .accessibilityAddTraits(.isHeader)
        let closeControl = closeButton
            .font(.system(size: 12))
            .foregroundColor(palette.mutedForeground)
            .keyboardShortcut(.cancelAction)

        return Group {
            if #available(iOS 17, macOS 14, *) {
                FoundationPresentationFocus(
                    heading: heading, close: closeControl, onActivate: close, initiallyFocusClose: commandFocusSearchOnOpen != true
                ) { heading, close in
                    regions(heading: heading, close: close, palette: palette)
                }
            } else {
                regions(heading: heading, close: closeControl, palette: palette)
            }
        }
        .foregroundColor(palette.foreground)
        .environment(\.foundationSurface, .card)
        .background(shape.fill(palette.card))
        .clipShape(shape)
        .overlay(shape.strokeBorder(palette.foreground.opacity(0.10)).allowsHitTesting(false))
        .accessibilityElement(children: .contain)
        .accessibilityAction(.escape) { close() }
    }

    private func regions<Heading: View, Close: View>(heading: Heading, close: Close, palette: FoundationPalette) -> some View {
        VStack(spacing: 0) {
            VStack(alignment: .leading, spacing: 8) {
                heading.padding(.trailing, 32)
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

            let content = dialogContent()
                .frame(maxWidth: .infinity, alignment: .leading)
                .padding([.horizontal, .bottom], 16)
            if #available(iOS 16, macOS 13, *) {
                ViewThatFits(in: .vertical) {
                    content.fixedSize(horizontal: false, vertical: true)
                    ScrollView { content }
                }
            } else {
                ScrollView { content }
            }

            if let footer {
                VStack(spacing: 0) {
                    Rectangle().fill(palette.border).frame(height: 1).accessibilityHidden(true)
                    footer.frame(maxWidth: .infinity, alignment: .trailing).padding(16)
                }
                .background(palette.novaCardFooterFill(for: colorScheme))
                .environment(\.foundationSurface, .cardFooter)
                .layoutPriority(1)
            }
        }
    }

    private func close() {
        isPresented = false
    }
}
