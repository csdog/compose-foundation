import SwiftUI

public struct FoundationToggleGroup<Value: Hashable>: View {
    @Environment(\.foundationTheme) private var theme
    @Environment(\.colorScheme) private var colorScheme
    @Environment(\.isEnabled) private var isEnabled
    @Binding private var selection: Set<Value>

    private let choices: [FoundationChoice<Value>]
    private let allowsMultiple: Bool
    private let allowsEmpty: Bool
    private let orientation: Axis
    private let variant: FoundationToggleVariant?
    private let size: FoundationToggleSize
    private let label: ((FoundationChoice<Value>) -> AnyView)?

    public init(
        _ choices: [FoundationChoice<Value>],
        selection: Binding<Set<Value>>,
        allowsMultiple: Bool = false,
        allowsEmpty: Bool = true
    ) {
        self.init(choices, selection: selection, orientation: .horizontal, allowsMultiple: allowsMultiple,
                  allowsEmpty: allowsEmpty)
    }

    public init(
        _ choices: [FoundationChoice<Value>],
        selection: Binding<Set<Value>>,
        orientation: Axis,
        allowsMultiple: Bool = false,
        allowsEmpty: Bool = true
    ) {
        self.choices = choices
        self._selection = selection
        self.allowsMultiple = allowsMultiple
        self.allowsEmpty = allowsEmpty
        self.orientation = orientation
        self.variant = nil
        self.size = .regular
        self.label = nil
    }

    public init<Label: View>(
        _ choices: [FoundationChoice<Value>],
        selection: Binding<Set<Value>>,
        variant: FoundationToggleVariant,
        size: FoundationToggleSize = .regular,
        allowsMultiple: Bool = false,
        allowsEmpty: Bool = true,
        @ViewBuilder label: @escaping (FoundationChoice<Value>) -> Label
    ) {
        self.init(choices, selection: selection, orientation: .horizontal, variant: variant, size: size,
                  allowsMultiple: allowsMultiple, allowsEmpty: allowsEmpty, label: label)
    }

    public init<Label: View>(
        _ choices: [FoundationChoice<Value>],
        selection: Binding<Set<Value>>,
        orientation: Axis,
        variant: FoundationToggleVariant,
        size: FoundationToggleSize = .regular,
        allowsMultiple: Bool = false,
        allowsEmpty: Bool = true,
        @ViewBuilder label: @escaping (FoundationChoice<Value>) -> Label
    ) {
        self.choices = choices
        self._selection = selection
        self.allowsMultiple = allowsMultiple
        self.allowsEmpty = allowsEmpty
        self.orientation = orientation
        self.variant = variant
        self.size = size
        self.label = { AnyView(label($0)) }
    }

    public var body: some View {
        let palette = theme.palette(for: colorScheme)
        let shape = RoundedRectangle(cornerRadius: theme.cornerRadius)
        let appearance = variant ?? (theme.preset == .nova ? .plain : nil)

        Group {
            if #available(iOS 17, macOS 14, *) {
                FoundationKeyboardToggleGroup(choices: choices, orientation: orientation,
                                               usesLegacyStyle: appearance == nil, activate: toggle) { choice in
                    item(choice, appearance: appearance, palette: palette, shape: shape)
                }
            } else {
                FoundationToggleGroupLayout(orientation: orientation, usesLegacyStyle: appearance == nil) {
                    ForEach(choices) { choice in
                        item(choice, appearance: appearance, palette: palette, shape: shape)
                    }
                }
            }
        }
        .accessibilityElement(children: .contain)
    }

    @ViewBuilder
    private func item(
        _ choice: FoundationChoice<Value>, appearance: FoundationToggleVariant?, palette: FoundationPalette,
        shape: RoundedRectangle
    ) -> some View {
        let selected = selection.contains(choice.value)
        if let appearance {
            let pressed = Binding(
                get: { selection.contains(choice.value) },
                set: { if $0 != selection.contains(choice.value) { toggle(choice.value) } }
            )
            FoundationToggle(isOn: pressed, variant: appearance, size: size) {
                if let label { label(choice) } else { Text(choice.title) }
            }
            .accessibilityLabel(Text(choice.title))
            .disabled(!choice.isEnabled)
        } else {
            Button {
                toggle(choice.value)
            } label: {
                Text(choice.title)
                    .font(.subheadline.weight(.medium))
                    .foregroundColor(selected ? palette.primaryForeground : palette.foreground)
                    .padding(.horizontal, 10)
                    .frame(minHeight: 44)
                    .background(shape.fill(selected ? palette.primary : .clear))
                    .contentShape(Rectangle())
            }
            .buttonStyle(PlainButtonStyle())
            .disabled(!choice.isEnabled)
            .opacity(isEnabled && choice.isEnabled ? 1 : 0.5)
            .accessibilityAddTraits(selected ? .isSelected : [])
        }
    }

    private func toggle(_ value: Value) {
        if selection.contains(value) {
            if allowsEmpty || selection.count > 1 { selection.remove(value) }
        } else if allowsMultiple {
            selection.insert(value)
        } else {
            selection = [value]
        }
    }
}

private struct FoundationToggleGroupLayout<Content: View>: View {
    @Environment(\.foundationTheme) private var theme
    @Environment(\.colorScheme) private var colorScheme
    let orientation: Axis
    let usesLegacyStyle: Bool
    @ViewBuilder let content: () -> Content

    var body: some View {
        if orientation == .vertical {
            styled(VStack(spacing: 2, content: content))
        } else {
            ScrollView(.horizontal, showsIndicators: false) {
                styled(HStack(spacing: 2, content: content))
            }
        }
    }

    private func styled<Stack: View>(_ stack: Stack) -> some View {
        let palette = theme.palette(for: colorScheme)
        return stack
            .padding(usesLegacyStyle ? 3 : 0)
            .background(RoundedRectangle(cornerRadius: theme.cornerRadius).fill(usesLegacyStyle ? palette.secondary : .clear))
    }
}

@available(iOS 17, macOS 14, *)
private struct FoundationKeyboardToggleGroup<Value: Hashable, Item: View>: View {
    @Environment(\.isEnabled) private var isEnabled
    @Environment(\.layoutDirection) private var layoutDirection
    @FocusState private var focused: Value?
    @State private var cursor: Value?
    let choices: [FoundationChoice<Value>]
    let orientation: Axis
    let usesLegacyStyle: Bool
    let activate: (Value) -> Void
    let item: (FoundationChoice<Value>) -> Item

    var body: some View {
        let navigation = FoundationTabsNavigation(enabledValues: isEnabled ? choices.filter(\.isEnabled).map(\.value) : [])
        let target = cursor.flatMap { navigation.entryFocus(for: $0) } ?? navigation.enabledValues.first
        FoundationToggleGroupLayout(orientation: orientation, usesLegacyStyle: usesLegacyStyle) {
            ForEach(choices) { choice in
                if target == choice.value {
                    item(choice)
                        .focusable()
                        .focused($focused, equals: choice.value)
                        .onKeyPress(keys: [.leftArrow, .rightArrow, .upArrow, .downArrow, .home, .end, .return, .space],
                                    phases: [.down, .repeat]) { key in
                            handle(key, from: choice.value, navigation: navigation)
                        }
                } else {
                    item(choice).focusable(false)
                }
            }
        }
        .defaultFocus($focused, target)
        .task(id: cursor) {
            guard let cursor, cursor == target else { return }
            await Task.yield()
            guard !Task.isCancelled else { return }
            focused = cursor
        }
    }

    private func handle(_ key: KeyPress, from value: Value, navigation: FoundationTabsNavigation<Value>) -> KeyPress.Result {
        guard key.modifiers.intersection([.command, .control, .option]).isEmpty else { return .ignored }
        let move: FoundationTabsNavigation<Value>.Move
        switch key.key {
        case .leftArrow where orientation == .horizontal, .upArrow where orientation == .vertical:
            move = .left
        case .rightArrow where orientation == .horizontal, .downArrow where orientation == .vertical:
            move = .right
        case .home: move = .first
        case .end: move = .last
        case .return, .space:
            if key.phase == .down, navigation.enabledValues.contains(value) { activate(value) }
            return .handled
        default: return .ignored
        }
        let rightToLeft = orientation == .horizontal && layoutDirection == .rightToLeft
        if let destination = navigation.destination(for: move, from: value, rightToLeft: rightToLeft),
           destination != value {
            cursor = destination
        }
        return .handled
    }
}
