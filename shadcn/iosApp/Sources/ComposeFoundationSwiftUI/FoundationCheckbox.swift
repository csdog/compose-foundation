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
        let nova = theme.preset == .nova
        let unmarkedFill = nova && colorScheme == .dark ? palette.novaDarkOutlineFill : Color.clear

        let control = Button(action: action) {
            HStack(alignment: nova ? .firstTextBaseline : .center, spacing: 8) {
                shape.fill(marked ? palette.primary : unmarkedFill)
                    .overlay(shape.strokeBorder(marked ? palette.primary : palette.input))
                    .overlay(markView(nova: nova, palette: palette))
                    .frame(width: 16, height: 16)
                    .alignmentGuide(.firstTextBaseline) { $0[VerticalAlignment.center] + 5 }
                    .accessibilityHidden(true)
                label
            }
            .frame(minHeight: 44)
            .contentShape(Rectangle())
            .foregroundColor(palette.foreground)
            .opacity(nova || isEnabled ? 1 : 0.5)
        }
        .buttonStyle(PlainButtonStyle())
        .accessibilityValue(state.accessibilityValue)

        if #available(iOS 17, macOS 14, *) {
            control.accessibilityAddTraits(.isToggle)
        } else {
            control
        }
    }

    @ViewBuilder
    private func markView(nova: Bool, palette: FoundationPalette) -> some View {
        if let mark = state.mark {
            if nova {
                Path { path in
                    if state == .mixed {
                        path.move(to: CGPoint(x: 2.92, y: 7))
                        path.addLine(to: CGPoint(x: 11.08, y: 7))
                    } else {
                        path.move(to: CGPoint(x: 2.33, y: 7))
                        path.addLine(to: CGPoint(x: 5.25, y: 9.92))
                        path.addLine(to: CGPoint(x: 11.67, y: 3.5))
                    }
                }
                .stroke(palette.primaryForeground, style: StrokeStyle(lineWidth: 1.17, lineCap: .round, lineJoin: .round))
                .frame(width: 14, height: 14)
            } else {
                Image(systemName: mark)
                    .font(.system(size: 10, weight: .semibold))
                    .foregroundColor(palette.primaryForeground)
            }
        }
    }
}
