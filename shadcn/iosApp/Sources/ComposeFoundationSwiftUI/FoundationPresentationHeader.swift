import SwiftUI
#if os(macOS)
import AppKit
#endif

@MainActor
func foundationEndEditingBeforeBackdropDismissal() {
    #if os(macOS)
    // The SwiftUI focus target can only regain focus after the AppKit field editor resigns.
    if let window = NSApplication.shared.keyWindow,
       let editor = window.firstResponder as? NSTextView, editor.isFieldEditor {
        window.makeFirstResponder(nil)
    }
    #endif
}

struct FoundationPresentationHeader<Close: View>: View {
    @Environment(\.foundationTheme) private var theme
    @Environment(\.textCase) private var textCase
    let title: String
    let description: Text?
    let alignment: VerticalAlignment
    let close: Close
    let dismiss: () -> Void

    var body: some View {
        let heading = Text(title)
            .font(theme.font(.headline, heading: true))
            .textCase(theme.preset == .sera ? .uppercase : textCase)
            .accessibilityAddTraits(.isHeader)
        let dismissButton = close.keyboardShortcut(.cancelAction)
        if #available(iOS 17, macOS 14, *) {
            FoundationPresentationFocus(heading: heading, close: dismissButton, onActivate: dismiss) { heading, close in
                FoundationPresentationHeaderRow(
                    heading: heading, description: description, alignment: alignment, close: close
                )
            }
        } else {
            FoundationPresentationHeaderRow(
                heading: heading, description: description, alignment: alignment, close: dismissButton
            )
        }
    }
}

@available(iOS 17, macOS 14, *)
struct FoundationPresentationFocus<Heading: View, Close: View, Layout: View>: View {
    @Environment(\.accessibilityVoiceOverEnabled) private var voiceOverEnabled
    @FocusState private var closeFocused: Bool
    @AccessibilityFocusState(for: .voiceOver) private var headingFocused: Bool

    let heading: Heading
    let close: Close
    let onActivate: () -> Void
    var initiallyFocusClose = true
    @ViewBuilder let layout: (AnyView, AnyView) -> Layout

    private var closeFocusInteractions: FocusInteractions {
        #if os(macOS)
        [.edit, .activate]
        #else
        .activate
        #endif
    }

    var body: some View {
        layout(
            AnyView(heading.accessibilityFocused($headingFocused)),
            AnyView(close.focusable(interactions: closeFocusInteractions)
                .focused($closeFocused)
                .onKeyPress(keys: [.return, .space], phases: .down) { press in
                    guard press.modifiers.intersection([.command, .control, .option, .shift]).isEmpty else {
                        return .ignored
                    }
                    onActivate()
                    return .handled
                })
        )
        .defaultFocus($closeFocused, initiallyFocusClose && !voiceOverEnabled)
        .task {
            await Task.yield()
            guard !Task.isCancelled else { return }
            if voiceOverEnabled {
                headingFocused = true
            } else if initiallyFocusClose {
                closeFocused = true
            }
        }
    }
}

private struct FoundationPresentationHeaderRow<Heading: View, Close: View>: View {
    let heading: Heading
    let description: Text?
    let alignment: VerticalAlignment
    let close: Close

    var body: some View {
        HStack(alignment: alignment) {
            VStack(alignment: .leading, spacing: 5) {
                heading
                description
            }
            Spacer()
            close
        }
    }
}
