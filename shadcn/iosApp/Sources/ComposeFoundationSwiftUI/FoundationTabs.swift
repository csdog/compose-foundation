import SwiftUI

public struct FoundationTabs<Value: Hashable, Panel: View>: View {
    @Environment(\.foundationTheme) private var theme
    @Environment(\.colorScheme) private var colorScheme
    @Binding private var selection: Value

    private let choices: [FoundationChoice<Value>]
    private let activationMode: FoundationTabsActivationMode
    private let panel: (Value) -> Panel

    public init(
        _ choices: [FoundationChoice<Value>],
        selection: Binding<Value>,
        @ViewBuilder panel: @escaping (Value) -> Panel
    ) {
        self.init(choices, selection: selection, activationMode: .automatic, panel: panel)
    }

    public init(
        _ choices: [FoundationChoice<Value>],
        selection: Binding<Value>,
        activationMode: FoundationTabsActivationMode,
        @ViewBuilder panel: @escaping (Value) -> Panel
    ) {
        self.choices = choices
        self._selection = selection
        self.activationMode = activationMode
        self.panel = panel
    }

    public var body: some View {
        let palette = theme.palette(for: colorScheme)
        let shape = RoundedRectangle(cornerRadius: theme.cornerRadius)

        VStack(alignment: .leading, spacing: 10) {
            ScrollViewReader { scroll in
                ScrollView(.horizontal, showsIndicators: false) {
                    Group {
                        if #available(iOS 17, macOS 14, *) {
                            FoundationKeyboardTabs(
                                choices: choices, selection: $selection, activationMode: activationMode,
                                scrollTo: { scroll.scrollTo($0) }
                            ) { choice, focused in
                                tab(choice, focused: focused, palette: palette, shape: shape)
                            }
                        } else {
                            HStack(spacing: 2) {
                                ForEach(choices) { choice in
                                    tab(choice, focused: false, palette: palette, shape: shape)
                                }
                            }
                        }
                    }
                    .padding(.horizontal, 3)
                    .background(shape.fill(palette.secondary))
                }
            }
            if choices.contains(where: { $0.value == selection }) {
                panel(selection)
            }
        }
    }

    private func tab(
        _ choice: FoundationChoice<Value>, focused: Bool, palette: FoundationPalette, shape: RoundedRectangle
    ) -> some View {
        let selected = choice.value == selection
        return Button {
            selection = choice.value
        } label: {
            Text(choice.title)
                .font(.subheadline.weight(selected ? .semibold : .medium))
                .foregroundColor(selected ? palette.foreground : palette.mutedForeground)
                .padding(.horizontal, 12)
                .padding(.vertical, 6)
                .background(shape.fill(selected ? palette.card : Color.clear))
                .overlay(shape.strokeBorder(focused ? palette.ring : .clear, lineWidth: 2).allowsHitTesting(false))
                .frame(minHeight: 44)
                .contentShape(Rectangle())
        }
        .buttonStyle(PlainButtonStyle())
        .disabled(!choice.isEnabled)
        .opacity(choice.isEnabled ? 1 : 0.5)
        .accessibilityAddTraits(selected ? .isSelected : [])
    }
}

@available(iOS 17, macOS 14, *)
private struct FoundationKeyboardTabs<Value: Hashable, Row: View>: View {
    @Environment(\.isEnabled) private var isEnabled
    @Environment(\.layoutDirection) private var layoutDirection
    @FocusState private var focused: Value?
    let choices: [FoundationChoice<Value>]
    @Binding var selection: Value
    let activationMode: FoundationTabsActivationMode
    let scrollTo: (Value) -> Void
    let row: (FoundationChoice<Value>, Bool) -> Row

    private var navigation: FoundationTabsNavigation<Value> {
        FoundationTabsNavigation(enabledValues: isEnabled ? choices.filter(\.isEnabled).map(\.value) : [])
    }

    var body: some View {
        HStack(spacing: 2) {
            ForEach(choices) { choice in
                row(choice, focused == choice.value)
                    .focusable(isEnabled && choice.isEnabled)
                    .focused($focused, equals: choice.value)
                    .id(choice.value)
                    .onKeyPress(keys: [.leftArrow, .rightArrow, .home, .end, .return, .space]) {
                        handle($0, from: choice)
                    }
            }
        }
        .defaultFocus($focused, navigation.entryFocus(for: selection), priority: .userInitiated)
        .onChange(of: focused) { previous, value in
            let navigation = self.navigation
            guard let value, navigation.enabledValues.contains(value) else { return }
            scrollTo(value)
            updateSelection(for: value, previousFocus: previous, navigation: navigation)
        }
    }

    private func handle(_ press: KeyPress, from choice: FoundationChoice<Value>) -> KeyPress.Result {
        let navigation = self.navigation
        guard choice.isEnabled, navigation.enabledValues.contains(choice.value),
              press.modifiers.intersection([.command, .control, .option, .shift]).isEmpty else {
            return .ignored
        }
        if press.key == .return || press.key == .space {
            if press.phase == .down,
               let value = navigation.selection(afterActivating: choice.value, current: selection) {
                selection = value
            }
            return .handled
        }

        let move: FoundationTabsNavigation<Value>.Move
        switch press.key {
        case .home: move = .first
        case .end: move = .last
        case .leftArrow: move = .left
        case .rightArrow: move = .right
        default: return .ignored
        }
        guard let next = navigation.destination(
            for: move, from: choice.value, rightToLeft: layoutDirection == .rightToLeft
        ) else { return .ignored }
        focused = next
        updateSelection(for: next, previousFocus: choice.value, navigation: navigation)
        return .handled
    }

    private func updateSelection(
        for value: Value, previousFocus: Value?, navigation: FoundationTabsNavigation<Value>
    ) {
        if let value = navigation.selection(
            afterFocusing: value, previousFocus: previousFocus, current: selection, mode: activationMode
        ) {
            selection = value
        }
    }
}
