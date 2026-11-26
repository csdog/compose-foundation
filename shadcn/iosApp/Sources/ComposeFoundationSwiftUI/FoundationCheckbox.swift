import SwiftUI

public enum FoundationCheckboxState: Equatable, Sendable {
    case unchecked
    case checked
    case mixed

    var activated: Self { self == .checked ? .unchecked : .checked }

    fileprivate var mark: String? {
        switch self {
        case .unchecked: nil
        case .checked: "checkmark"
        case .mixed: "minus"
        }
    }

    fileprivate var accessibilityValue: Text {
        switch self {
        case .unchecked: Text("Unchecked")
        case .checked: Text("Checked")
        case .mixed: Text("Partially checked")
        }
    }
}

public struct FoundationCheckbox: ToggleStyle {
    public init() {}

    public func makeBody(configuration: Configuration) -> some View {
        let state: FoundationCheckboxState
        if #available(iOS 16, macOS 13, *), configuration.isMixed {
            state = .mixed
        } else {
            state = configuration.isOn ? .checked : .unchecked
        }
        return CheckboxControl(state: state, label: configuration.label) {
            configuration.isOn.toggle()
        }
    }
}

/// Displays a caller-provided mixed state, including on iOS 14 and macOS 11.
public struct FoundationTriStateCheckbox<Label: View>: View {
    @Binding private var state: FoundationCheckboxState
    private let label: Label

    public init(state: Binding<FoundationCheckboxState>, @ViewBuilder label: () -> Label) {
        self._state = state
        self.label = label()
    }

    public var body: some View {
        CheckboxControl(state: state, label: label) { state = state.activated }
    }
}

private struct CheckboxControl<Label: View>: View {
    @Environment(\.foundationTheme) private var theme
    @Environment(\.colorScheme) private var colorScheme
    @Environment(\.isEnabled) private var isEnabled

    let state: FoundationCheckboxState
    let label: Label
    let action: () -> Void

    var body: some View {
        let palette = theme.palette(for: colorScheme)
        let shape = RoundedRectangle(cornerRadius: 4)
        let marked = state != .unchecked

        let control = Button(action: action) {
            HStack(spacing: 8) {
                shape.fill(marked ? palette.primary : Color.clear)
                    .overlay(shape.strokeBorder(marked ? palette.primary : palette.input))
                    .overlay(
                        Group {
                            if let mark = state.mark {
                                Image(systemName: mark)
                                    .font(.system(size: 10, weight: .semibold))
                                    .foregroundColor(palette.primaryForeground)
                            }
                        }
                    )
                    .frame(width: 16, height: 16)
                    .accessibilityHidden(true)
                label
            }
            .frame(minHeight: 44)
            .contentShape(Rectangle())
            .foregroundColor(palette.foreground)
            .opacity(isEnabled ? 1 : 0.5)
        }
        .buttonStyle(PlainButtonStyle())
        .accessibilityValue(state.accessibilityValue)

        if #available(iOS 17, macOS 14, *) {
            control.accessibilityAddTraits(.isToggle)
        } else {
            control
        }
    }
}
