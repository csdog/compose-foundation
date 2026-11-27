import SwiftUI

public struct FoundationAlertDialog: ViewModifier {
    public enum Size: Sendable {
        case regular, small
    }

    @Environment(\.foundationTheme) private var theme
    @Environment(\.colorScheme) private var colorScheme
    @Environment(\.accessibilityReduceMotion) private var reduceMotion
    @Environment(\.accessibilityReduceTransparency) private var reduceTransparency
    @Binding private var isPresented: Bool

    private let title: String
    private let message: String
    private let confirmTitle: String
    private let cancelTitle: String
    private let destructive: Bool
    private let size: Size?
    private let onCancel: () -> Void
    private let onConfirm: () -> Void

    public init(
        isPresented: Binding<Bool>,
        title: String,
        message: String,
        confirmTitle: String = "Continue",
        destructive: Bool = false,
        onConfirm: @escaping () -> Void
    ) {
        self.init(isPresented: isPresented, title: title, message: message, confirmTitle: confirmTitle,
                  cancelTitle: "Cancel", destructive: destructive, panelSize: nil, onCancel: {}, onConfirm: onConfirm)
    }

    /// Presents a styled dialog over the modified view. Apply to a container that fills the presentation area.
    public init(
        isPresented: Binding<Bool>,
        title: String,
        message: String,
        size: Size,
        confirmTitle: String = "Continue",
        cancelTitle: String = "Cancel",
        destructive: Bool = false,
        onCancel: @escaping () -> Void = {},
        onConfirm: @escaping () -> Void
    ) {
        self.init(isPresented: isPresented, title: title, message: message, confirmTitle: confirmTitle,
                  cancelTitle: cancelTitle, destructive: destructive, panelSize: size,
                  onCancel: onCancel, onConfirm: onConfirm)
    }

    private init(
        isPresented: Binding<Bool>, title: String, message: String, confirmTitle: String, cancelTitle: String,
        destructive: Bool, panelSize: Size?, onCancel: @escaping () -> Void, onConfirm: @escaping () -> Void
    ) {
        self._isPresented = isPresented
        self.title = title
        self.message = message
        self.confirmTitle = confirmTitle
        self.cancelTitle = cancelTitle
        self.destructive = destructive
        self.size = panelSize
        self.onCancel = onCancel
        self.onConfirm = onConfirm
    }

    @ViewBuilder
    public func body(content: Content) -> some View {
        if let size {
            content
                .disabled(isPresented)
                .allowsHitTesting(!isPresented)
                .accessibilityHidden(isPresented)
                .compositingGroup()
                .blur(radius: isPresented && !reduceTransparency ? 4 : 0)
                .overlay(Group {
                    if isPresented {
                        GeometryReader { geometry in
                            let wide = size == .regular && geometry.size.width >= 640
                            ZStack {
                                Color.black.opacity(0.10).accessibilityHidden(true)
                                panel(wide: wide, equalActions: size == .small)
                                    .frame(width: min(wide ? 384 : 320, max(1, geometry.size.width - 32)))
                                    .padding(.vertical, 16)
                            }
                            .frame(maxWidth: .infinity, maxHeight: .infinity)
                        }
                        .transition(.opacity)
                    }
                })
                .animation(reduceMotion ? nil : .easeOut(duration: 0.10), value: isPresented)
        } else {
            content.alert(isPresented: $isPresented) {
                let confirm: Alert.Button = destructive
                    ? .destructive(Text(confirmTitle), action: onConfirm)
                    : .default(Text(confirmTitle), action: onConfirm)
                return Alert(
                    title: Text(title),
                    message: Text(message),
                    primaryButton: confirm,
                    secondaryButton: .cancel()
                )
            }
        }
    }

    private func panel(wide: Bool, equalActions: Bool) -> some View {
        let palette = theme.palette(for: colorScheme)
        let shape = RoundedRectangle(cornerRadius: theme.popoverCornerRadius + 2)
        let heading = Text(title)
            .font(theme.font(.body, weight: .medium, heading: true))
            .accessibilityAddTraits(.isHeader)
        let cancel = Button { finish(with: onCancel) } label: {
            actionLabel(cancelTitle, flexible: !wide)
        }
        .buttonStyle(FoundationButtonStyle(.outline))
        .keyboardShortcut(.cancelAction)
        let confirm = confirmationButton(flexible: !wide)
            .buttonStyle(FoundationButtonStyle(destructive ? .destructive : .primary))

        return VStack(spacing: 0) {
            if #available(iOS 17, macOS 14, *) {
                FoundationAlertDialogFocus(
                    heading: heading, cancel: cancel, dismiss: { finish(with: onCancel) }
                ) { heading, cancel in
                    sections(heading: heading, actions: footer(cancel: cancel, confirm: confirm,
                                                                wide: wide, equalActions: equalActions),
                             palette: palette, wide: wide)
                }
            } else {
                sections(heading: heading, actions: footer(cancel: cancel, confirm: confirm,
                                                            wide: wide, equalActions: equalActions),
                         palette: palette, wide: wide)
            }
        }
        .foregroundColor(palette.foreground)
        .background(shape.fill(palette.card))
        .clipShape(shape)
        .overlay(shape.strokeBorder(palette.foreground.opacity(0.10)).allowsHitTesting(false))
        .accessibilityElement(children: .contain)
        .accessibilityAction(.escape) { finish(with: onCancel) }
    }

    private func sections<Heading: View, Actions: View>(
        heading: Heading, actions: Actions, palette: FoundationPalette, wide: Bool
    ) -> some View {
        VStack(spacing: 0) {
            let header = VStack(alignment: wide ? .leading : .center, spacing: 6) {
                heading
                Text(message)
                    .font(theme.font(.subheadline))
                    .foregroundColor(palette.mutedForeground)
                    .multilineTextAlignment(wide ? .leading : .center)
                    .fixedSize(horizontal: false, vertical: true)
            }
            .frame(maxWidth: .infinity, alignment: wide ? .leading : .center)
            .padding(16)
            if #available(iOS 16, macOS 13, *) {
                ViewThatFits(in: .vertical) {
                    header
                    ScrollView { header }
                }
            } else {
                header
            }

            VStack(spacing: 0) {
                Rectangle().fill(palette.border).frame(height: 1).accessibilityHidden(true)
                actions.padding(16)
            }
            .background(palette.novaCardFooterFill(for: colorScheme))
            .environment(\.foundationSurface, .cardFooter)
        }
    }

    @ViewBuilder
    private func footer<Cancel: View, Confirm: View>(
        cancel: Cancel, confirm: Confirm, wide: Bool, equalActions: Bool
    ) -> some View {
        if wide || equalActions {
            HStack(spacing: 8) {
                cancel
                confirm
            }
            .frame(maxWidth: .infinity, alignment: .trailing)
        } else {
            VStack(spacing: 8) {
                confirm
                cancel
            }
        }
    }

    @ViewBuilder
    private func confirmationButton(flexible: Bool) -> some View {
        if #available(iOS 15, macOS 12, *) {
            Button(role: destructive ? .destructive : nil) { finish(with: onConfirm) } label: {
                actionLabel(confirmTitle, flexible: flexible)
            }
        } else {
            Button { finish(with: onConfirm) } label: {
                actionLabel(confirmTitle, flexible: flexible)
            }
        }
    }

    private func actionLabel(_ title: String, flexible: Bool) -> some View {
        Text(title).frame(maxWidth: flexible ? .infinity : nil)
    }

    private func finish(with action: () -> Void) {
        guard isPresented else { return }
        isPresented = false
        action()
    }
}

@available(iOS 17, macOS 14, *)
private struct FoundationAlertDialogFocus<Heading: View, Cancel: View, Layout: View>: View {
    @Environment(\.accessibilityVoiceOverEnabled) private var voiceOverEnabled
    @FocusState private var cancelFocused: Bool
    @AccessibilityFocusState(for: .voiceOver) private var headingFocused: Bool

    let heading: Heading
    let cancel: Cancel
    let dismiss: () -> Void
    @ViewBuilder let layout: (AnyView, AnyView) -> Layout

    var body: some View {
        #if os(macOS)
        let interactions: FocusInteractions = [.edit, .activate]
        #else
        let interactions: FocusInteractions = .activate
        #endif
        layout(
            AnyView(heading.accessibilityFocused($headingFocused)),
            AnyView(cancel
                .focusable(interactions: interactions)
                .focused($cancelFocused)
                .onKeyPress(keys: [.return, .space, .escape], phases: .down) { press in
                    guard press.modifiers.intersection([.command, .control, .option, .shift]).isEmpty else {
                        return .ignored
                    }
                    dismiss()
                    return .handled
                })
        )
        .defaultFocus($cancelFocused, !voiceOverEnabled)
        .task {
            await Task.yield()
            guard !Task.isCancelled else { return }
            if voiceOverEnabled {
                headingFocused = true
            } else {
                cancelFocused = true
            }
        }
    }
}
