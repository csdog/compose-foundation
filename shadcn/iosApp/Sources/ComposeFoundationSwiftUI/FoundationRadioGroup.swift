import SwiftUI

public struct FoundationRadioGroup<Value: Hashable>: View {
    @Environment(\.foundationTheme) private var theme
    @Environment(\.colorScheme) private var colorScheme
    @Environment(\.isEnabled) private var isEnabled
    @Environment(\.font) private var font
    @ScaledMetric(relativeTo: .subheadline) private var labelSize: CGFloat = 14
    @Binding private var selection: Value?

    private let choices: [FoundationChoice<Value>]

    public init(_ choices: [FoundationChoice<Value>], selection: Binding<Value?>) {
        self.choices = choices
        self._selection = selection
    }

    public var body: some View {
        let palette = theme.palette(for: colorScheme)
        let nova = theme.preset == .nova
        let labelFont = nova ? (font ?? .system(size: labelSize, weight: .medium)) : .subheadline

        Group {
            if #available(iOS 17, macOS 14, *) {
                FoundationKeyboardRadioGroup(choices: choices, selection: $selection,
                                             usesCustomFocusEffect: nova) { choice, focused in
                    row(choice, labelFont: labelFont, nova: nova, palette: palette, focused: focused)
                }
            } else {
                VStack(alignment: .leading, spacing: 2) {
                    ForEach(choices) { choice in
                        row(choice, labelFont: labelFont, nova: nova, palette: palette, focused: false)
                    }
                }
            }
        }
        .accessibilityElement(children: .contain)
    }

    private func row(_ choice: FoundationChoice<Value>, labelFont: Font, nova: Bool, palette: FoundationPalette,
                     focused: Bool) -> some View {
        let selected = choice.value == selection
        let focusVisible = nova && focused && isEnabled && choice.isEnabled
        return Button {
            selection = choice.value
        } label: {
            HStack(spacing: nova ? 12 : 8) {
                indicator(selected: selected, nova: nova, palette: palette, focused: focusVisible)
                    .overlay(Group {
                        if focusVisible {
                            Circle().strokeBorder(palette.ring.opacity(0.5), lineWidth: 3)
                                .frame(width: 22, height: 22)
                                .allowsHitTesting(false)
                                .accessibilityHidden(true)
                        }
                    })
                Text(choice.title).font(labelFont)
            }
            .foregroundColor(palette.foreground)
            .frame(minWidth: nova ? 44 : nil, minHeight: 44, alignment: .leading)
            .contentShape(Rectangle())
        }
        .buttonStyle(PlainButtonStyle())
        .disabled(!choice.isEnabled)
        .opacity(nova || (isEnabled && choice.isEnabled) ? 1 : 0.5)
        .accessibilityAddTraits(selected ? .isSelected : [])
    }

    private func indicator(selected: Bool, nova: Bool, palette: FoundationPalette, focused: Bool) -> some View {
        let ring = focused ? palette.ring : selected ? palette.primary : palette.input
        let background = nova && colorScheme == .dark ? palette.novaDarkOutlineFill : Color.clear
        let fill = nova && selected ? palette.primary : background
        let dot = nova ? palette.primaryForeground : palette.primary

        return Circle().fill(fill)
            .overlay(Circle().strokeBorder(ring))
            .overlay(Circle().fill(selected ? dot : Color.clear).frame(width: 8, height: 8))
            .frame(width: 16, height: 16)
            .accessibilityHidden(true)
    }
}

@available(iOS 17, macOS 14, *)
private struct FoundationKeyboardRadioGroup<Value: Hashable, Row: View>: View {
    @Environment(\.isEnabled) private var isEnabled
    @Environment(\.layoutDirection) private var layoutDirection
    @FocusState private var focused: Value?
    @State private var cursor: Value?
    let choices: [FoundationChoice<Value>]
    @Binding var selection: Value?
    let usesCustomFocusEffect: Bool
    let row: (FoundationChoice<Value>, Bool) -> Row

    var body: some View {
        let navigation = FoundationTabsNavigation(enabledValues: isEnabled ? choices.filter(\.isEnabled).map(\.value) : [])
        let entry = selection.flatMap { navigation.entryFocus(for: $0) } ?? navigation.enabledValues.first
        let target = cursor.flatMap { navigation.enabledValues.contains($0) ? $0 : nil } ?? entry
        VStack(alignment: .leading, spacing: 2) {
            ForEach(choices) { choice in
                if target == choice.value {
                    row(choice, focused == choice.value)
                        .focusable()
                        .focused($focused, equals: choice.value)
                        .focusEffectDisabled(usesCustomFocusEffect)
                        .onKeyPress(keys: [.leftArrow, .rightArrow, .upArrow, .downArrow, .space],
                                    phases: [.down, .repeat]) { key in
                            handle(key, from: choice.value, navigation: navigation)
                        }
                } else {
                    row(choice, false).focusable(false)
                }
            }
        }
        .defaultFocus($focused, target)
        .onChange(of: selection) { _, selected in
            if selected != cursor { cursor = nil }
        }
        .task(id: cursor) {
            guard let cursor, cursor == target else { return }
            await Task.yield()
            guard !Task.isCancelled else { return }
            focused = cursor
        }
    }

    private func handle(_ key: KeyPress, from current: Value, navigation: FoundationTabsNavigation<Value>) -> KeyPress.Result {
        guard navigation.enabledValues.contains(current),
              key.modifiers.intersection([.command, .control, .option]).isEmpty else { return .ignored }
        let move: FoundationTabsNavigation<Value>.Move
        let rightToLeft: Bool
        switch key.key {
        case .leftArrow, .rightArrow:
            move = key.key == .leftArrow ? .left : .right
            rightToLeft = layoutDirection == .rightToLeft
        case .upArrow, .downArrow:
            move = key.key == .upArrow ? .left : .right
            rightToLeft = false
        case .space:
            if key.phase == .down, selection != current { selection = current }
            return .handled
        default:
            return .ignored
        }
        if let destination = navigation.destination(for: move, from: current, rightToLeft: rightToLeft),
           destination != current {
            cursor = destination
            if selection != destination { selection = destination }
        }
        return .handled
    }
}
