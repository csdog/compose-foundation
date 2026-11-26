import SwiftUI

public struct FoundationSwitchStyle: ToggleStyle {
    @Environment(\.foundationTheme) private var theme
    @Environment(\.colorScheme) private var colorScheme

    public init() {}

    public func makeBody(configuration: Configuration) -> some View {
        let palette = theme.palette(for: colorScheme)

        Toggle(isOn: configuration.$isOn) {
            configuration.label
        }
        .toggleStyle(SwitchToggleStyle(tint: palette.primary))
    }
}
