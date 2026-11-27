import SwiftUI

public struct FoundationSwitchStyle: ToggleStyle {
    @Environment(\.foundationTheme) private var theme
    @Environment(\.colorScheme) private var colorScheme
    @Environment(\.isEnabled) private var isEnabled
    @Environment(\.layoutDirection) private var layoutDirection
    @Environment(\.accessibilityReduceMotion) private var reduceMotion

    public init() {}

    @ViewBuilder
    public func makeBody(configuration: Configuration) -> some View {
        let palette = theme.palette(for: colorScheme)
        if theme.preset == .nova {
            novaSwitch(configuration, palette: palette)
        } else {
            nativeSwitch(configuration, tint: palette.primary)
        }
    }

    private func nativeSwitch(_ configuration: Configuration, tint: Color) -> some View {
        Toggle(isOn: configuration.$isOn) {
            configuration.label
        }
        .toggleStyle(SwitchToggleStyle(tint: tint))
    }

    @ViewBuilder
    private func novaSwitch(_ configuration: Configuration, palette: FoundationPalette) -> some View {
        let isOn = configuration.isOn
        let track = isOn ? palette.primary : palette.input.opacity(colorScheme == .dark ? 0.8 : 1)
        let thumb = colorScheme == .dark
            ? (isOn ? palette.primaryForeground : palette.foreground)
            : palette.background
        let thumbOffset: CGFloat = layoutDirection == .rightToLeft ? -14 : 14

        let control = Button {
            configuration.isOn.toggle()
        } label: {
            HStack(spacing: 8) {
                ZStack(alignment: .leading) {
                    Capsule().fill(track)
                    Circle().fill(thumb)
                        .frame(width: 16, height: 16)
                        .padding(.leading, 1)
                        .offset(x: isOn ? thumbOffset : 0)
                }
                .frame(width: 32, height: 18.4)
                .animation(reduceMotion ? nil : .easeInOut(duration: 0.15), value: isOn)
                .accessibilityHidden(true)

                configuration.label
            }
            .foregroundColor(palette.foreground)
            .opacity(isEnabled ? 1 : 0.5)
            .frame(minHeight: 44)
            .contentShape(Rectangle())
        }
        .buttonStyle(PlainButtonStyle())

        if #available(iOS 15, macOS 12, *) {
            control.accessibilityRepresentation {
                nativeSwitch(configuration, tint: palette.primary)
                    .disabled(!isEnabled)
            }
        } else {
            control.accessibilityValue(isOn ? Text("On") : Text("Off"))
        }
    }
}
