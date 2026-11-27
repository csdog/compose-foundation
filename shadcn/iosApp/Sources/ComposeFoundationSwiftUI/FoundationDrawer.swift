import SwiftUI

public struct FoundationDrawer<Panel: View>: ViewModifier {
    @Environment(\.foundationTheme) private var theme
    @Environment(\.colorScheme) private var colorScheme
    @Environment(\.accessibilityReduceMotion) private var reduceMotion
    @Environment(\.accessibilityReduceTransparency) private var reduceTransparency
    @Binding private var isPresented: Bool
    @GestureState private var drag: CGFloat = 0
    @ScaledMetric(relativeTo: .body) private var titleSize: CGFloat = 16
    @ScaledMetric(relativeTo: .subheadline) private var descriptionSize: CGFloat = 14

    private struct Styled {
        let description: String?
        let closeTitle: String
        let actions: AnyView?
    }

    private let title: String
    private let styled: Styled?
    private let onDismiss: () -> Void
    private let panel: () -> Panel

    public init(isPresented: Binding<Bool>, title: String, @ViewBuilder panel: @escaping () -> Panel) {
        self.init(isPresented: isPresented, title: title, onDismiss: {}, panel: panel)
    }

    public init(
        isPresented: Binding<Bool>, title: String, onDismiss: @escaping () -> Void,
        @ViewBuilder panel: @escaping () -> Panel
    ) {
        self.init(isPresented: isPresented, title: title, styled: nil, onDismiss: onDismiss, panel: panel)
    }

    /// Places the description in the header and the close button in the footer.
    /// Apply to a container that fills the intended presentation area.
    public init(
        isPresented: Binding<Bool>, title: String, description: String?, closeTitle: String = "Close",
        onDismiss: @escaping () -> Void = {}, @ViewBuilder panel: @escaping () -> Panel
    ) {
        self.init(isPresented: isPresented, title: title,
                  styled: Styled(description: description, closeTitle: closeTitle, actions: nil),
                  onDismiss: onDismiss, panel: panel)
    }

    /// Adds caller-supplied controls ahead of the close button in the footer.
    public init<Actions: View>(
        isPresented: Binding<Bool>, title: String, description: String?, closeTitle: String = "Close",
        onDismiss: @escaping () -> Void = {}, @ViewBuilder actions: () -> Actions,
        @ViewBuilder panel: @escaping () -> Panel
    ) {
        self.init(isPresented: isPresented, title: title,
                  styled: Styled(description: description, closeTitle: closeTitle, actions: AnyView(actions())),
                  onDismiss: onDismiss, panel: panel)
    }

    private init(
        isPresented: Binding<Bool>, title: String, styled: Styled?, onDismiss: @escaping () -> Void,
        @ViewBuilder panel: @escaping () -> Panel
    ) {
        self._isPresented = isPresented
        self.title = title
        self.styled = styled
        self.onDismiss = onDismiss
        self.panel = panel
    }

    @ViewBuilder
    public func body(content: Content) -> some View {
        let background = content
            .disabled(isPresented)
            .allowsHitTesting(!isPresented)
            .accessibilityHidden(isPresented)
        let palette = theme.palette(for: colorScheme)

        if let styled {
            let rhea = theme.preset == .rhea
            let inset: CGFloat = rhea ? 8 : 0
            background
                .compositingGroup()
                .blur(radius: isPresented && !reduceTransparency ? (rhea ? 8 : 4) : 0)
                .overlay(Group {
                    if isPresented {
                        GeometryReader { geometry in
                            ZStack(alignment: .bottom) {
                                backdrop(opacity: rhea ? 0.30 : 0.10)
                                presented(styledPanel(styled, palette: palette, rhea: rhea), palette: palette)
                                    .frame(maxHeight: max(1, geometry.size.height - 96), alignment: .bottom)
                                    .padding(inset)
                            }
                        }
                    }
                })
                .animation(reduceMotion ? nil : .easeInOut(duration: 0.2), value: isPresented)
        } else {
            ZStack(alignment: .bottom) {
                background
                if isPresented {
                    backdrop(opacity: 0.42).edgesIgnoringSafeArea(.all)
                    presented(VStack(alignment: .leading, spacing: 12) {
                        handle(width: 32, height: 4, color: palette.mutedForeground, inset: 0)
                        FoundationPresentationHeader(
                            title: title, description: nil, alignment: .center,
                            close: Button("Close", action: close), dismiss: close
                        )
                        panel()
                    }
                    .padding(16)
                    .background(RoundedRectangle(cornerRadius: theme.cornerRadius + 6).fill(palette.card)),
                              palette: palette)
                }
            }
            .animation(reduceMotion ? nil : .easeInOut(duration: 0.2), value: isPresented)
        }
    }

    private func backdrop(opacity: Double) -> some View {
        Color.black.opacity(opacity)
            .onTapGesture(perform: close)
            .accessibilityHidden(true)
    }

    private func handle(width: CGFloat, height: CGFloat, color: Color, inset: CGFloat) -> some View {
        Capsule()
            .fill(color)
            .frame(width: width, height: height)
            .padding(.top, inset)
            .frame(maxWidth: .infinity, minHeight: 24, alignment: inset > 0 ? .top : .center)
            .contentShape(Rectangle())
            .gesture(
                DragGesture(coordinateSpace: .global)
                    .updating($drag) { value, offset, _ in offset = max(0, value.translation.height) }
                    .onEnded { value in
                        if value.predictedEndTranslation.height > 90 { close() }
                    }
            )
            .accessibilityHidden(true)
    }

    private func presented<Body: View>(_ body: Body, palette: FoundationPalette) -> some View {
        body
            .foregroundColor(palette.foreground)
            .offset(y: drag)
            .transition(.move(edge: .bottom))
            .accessibilityElement(children: .contain)
            .accessibilityAction(.escape) { close() }
            .onDisappear {
                if !isPresented { onDismiss() }
            }
    }

    @ViewBuilder
    private func styledPanel(_ config: Styled, palette: FoundationPalette, rhea: Bool) -> some View {
        let heading = Text(title)
            .font(.system(size: titleSize, weight: .medium))
            .accessibilityAddTraits(.isHeader)
        let closeButton = Button(action: close) {
            Text(config.closeTitle).frame(maxWidth: .infinity)
        }
        .buttonStyle(FoundationButtonStyle(config.actions == nil ? .primary : .outline))
        .keyboardShortcut(.cancelAction)
        let border = rhea && colorScheme == .light ? palette.card : palette.border

        let layout = Group {
            if #available(iOS 17, macOS 14, *) {
                FoundationPresentationFocus(heading: heading, close: closeButton, onActivate: close) { heading, close in
                    regions(config, heading: heading, close: close, palette: palette, rhea: rhea)
                }
            } else {
                regions(config, heading: heading, close: closeButton, palette: palette, rhea: rhea)
            }
        }
        if !rhea, #available(iOS 17, macOS 14, *) {
            let shape = UnevenRoundedRectangle(topLeadingRadius: theme.cornerRadius + 2,
                                               topTrailingRadius: theme.cornerRadius + 2)
            surface(layout, shape: shape, palette: palette, border: border)
        } else {
            surface(layout, shape: RoundedRectangle(cornerRadius: rhea ? theme.cardCornerRadius : theme.cornerRadius + 2),
                    palette: palette, border: border)
                .shadow(color: .black.opacity(rhea ? 0.14 : 0), radius: 12, y: 4)
        }
    }

    private func surface<Layout: View, Outline: InsettableShape>(
        _ layout: Layout, shape: Outline, palette: FoundationPalette, border: Color
    ) -> some View {
        layout.background(shape.fill(palette.card))
            .clipShape(shape)
            .overlay(shape.strokeBorder(border).allowsHitTesting(false))
    }

    private func regions<Heading: View, Close: View>(
        _ config: Styled, heading: Heading, close: Close, palette: FoundationPalette, rhea: Bool
    ) -> some View {
        VStack(spacing: 0) {
            handle(width: rhea ? 100 : 96, height: rhea ? 6 : 4, color: palette.secondary, inset: 6)
            VStack(spacing: 2) {
                heading
                if let description = config.description {
                    Text(description)
                        .font(.system(size: descriptionSize))
                        .foregroundColor(palette.mutedForeground)
                }
            }
            .multilineTextAlignment(.center)
            .padding(.horizontal, 16)
            .padding(.top, 8)
            .layoutPriority(1)

            let content = panel()
                .frame(maxWidth: .infinity)
                .padding(16)
            if #available(iOS 16, macOS 13, *) {
                ViewThatFits(in: .vertical) {
                    content.fixedSize(horizontal: false, vertical: true)
                    ScrollView { content }
                }
            } else {
                ScrollView { content }
            }

            VStack(spacing: 8) {
                config.actions
                close
            }
            .padding(.horizontal, 16)
            .padding(.bottom, 16)
            .layoutPriority(1)
        }
    }

    private func close() {
        isPresented = false
    }
}
