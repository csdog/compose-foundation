import AppKit
import SwiftUI

@available(macOS 13, *)
@main
struct MacControlTypographyAudit {
    private struct FontProbe: View {
        @Environment(\.font) private var font
        let report: (Font?) -> Void

        var body: some View {
            report(font)
            return Text("Typography sample")
        }
    }

    @MainActor
    static func main() {
        NSApplication.shared.setActivationPolicy(.prohibited)
        let ancestor = Font.system(size: 36, weight: .heavy)
        let sera = FoundationTheme(preset: .sera)
        checkButton("Sera caption and weight under an ancestor font", theme: sera, ancestor: ancestor,
                    expected: sera.font(.caption, weight: .semibold))

        let nova = FoundationTheme(preset: .nova)
        checkButton("extra-small size under an ancestor font", theme: nova, ancestor: ancestor,
                    size: .extraSmall, expected: .system(size: 12, weight: .medium))
        checkButton("regular size under an ancestor font", theme: nova, ancestor: ancestor,
                    size: .regular, expected: .system(size: 14, weight: .medium))

        let labelOverride = Font.system(size: 17, weight: .bold)
        checkButton("explicit button label font", theme: nova, ancestor: ancestor,
                    size: .regular, labelOverride: labelOverride, expected: labelOverride)

        let referenceTabs = renderTabs(tabs("Overview", theme: nova))
        precondition(referenceTabs != renderTabs(tabs("Different", theme: nova)),
                     "The rendered tab labels are missing from the typography audit")
        precondition(referenceTabs == renderTabs(tabs("Overview", theme: nova).font(ancestor)),
                     "An ancestor font changed Nova Tabs typography")
        print("PASS: Button typography and Nova Tabs are independent of ancestor fonts")
    }

    @MainActor
    private static func tabs(_ title: String, theme: FoundationTheme) -> some View {
        FoundationTabs([
            FoundationChoice(title, value: 0),
            FoundationChoice("Activity", value: 1)
        ], selection: .constant(0)) { _ in
            EmptyView()
        }
        .foundationTheme(theme)
        .environment(\.colorScheme, .light)
        .frame(width: 260, height: 52, alignment: .topLeading)
    }

    @MainActor
    private static func checkButton(
        _ name: String, theme: FoundationTheme, ancestor: Font, size: FoundationButtonStyle.Size? = nil,
        labelOverride: Font? = nil, expected: Font
    ) {
        var actual: Font?
        let probe = FontProbe { actual = $0 }
        let style = size.map { FoundationButtonStyle(size: $0) } ?? FoundationButtonStyle()
        let button = Button {} label: {
            if let labelOverride {
                probe.font(labelOverride)
            } else {
                probe
            }
        }
        .buttonStyle(style)
        .foundationTheme(theme)
        .font(ancestor)
        .fixedSize()
        _ = render(button)
        precondition(actual == expected, "Unexpected font for \(name): \(String(describing: actual))")
    }

    @MainActor
    private static func renderTabs<Content: View>(_ content: Content) -> Data {
        // Tabs uses a native scroll view, so capture it in an AppKit window.
        let bounds = NSRect(x: 0, y: 0, width: 260, height: 52)
        let window = NSWindow(contentRect: bounds, styleMask: [.borderless], backing: .buffered, defer: false)
        window.isReleasedWhenClosed = false
        defer { window.close() }
        let host = NSHostingView(rootView: content)
        window.contentView = host
        host.layoutSubtreeIfNeeded()
        window.display()
        guard let bitmap = host.bitmapImageRepForCachingDisplay(in: host.bounds) else {
            preconditionFailure("Could not render the SwiftUI tab strip")
        }
        host.cacheDisplay(in: host.bounds, to: bitmap)
        guard let png = bitmap.representation(using: .png, properties: [:]) else {
            preconditionFailure("Could not encode the SwiftUI tab strip")
        }
        return png
    }

    @MainActor
    private static func render<Content: View>(_ content: Content) -> Data {
        let renderer = ImageRenderer(content: content)
        renderer.scale = 1
        guard let image = renderer.cgImage,
              let png = NSBitmapImageRep(cgImage: image).representation(using: .png, properties: [:]) else {
            preconditionFailure("Could not render SwiftUI controls")
        }
        return png
    }
}
