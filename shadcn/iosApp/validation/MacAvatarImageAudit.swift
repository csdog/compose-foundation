import AppKit
import SwiftUI

private struct AvatarProbe: View {
    let remote: URL
    let image: NSImage

    var body: some View {
        HStack(spacing: 20) {
            FoundationAvatar("Local avatar", image: Image(nsImage: image))
            FoundationAvatar("Remote avatar", imageURL: remote.appendingPathComponent("avatar.png"))
            FoundationAvatar("Unavailable image", imageURL: remote.appendingPathComponent("missing.png"), fallback: "")
        }
        .frame(width: 240, height: 120)
        .background(Color.white)
        .foundationTheme(FoundationTheme(preset: .nova))
        .environment(\.colorScheme, .light)
    }
}

@main @MainActor
enum MacAvatarImageAudit {
    static func main() {
        let args = CommandLine.arguments
        guard args.count == 3, let remote = URL(string: args[1]), let image = NSImage(contentsOfFile: args[2]) else {
            fail("Expected the loopback URL and a local PNG")
        }
        let application = NSApplication.shared
        application.setActivationPolicy(.regular)
        let view = NSHostingView(rootView: AvatarProbe(remote: remote, image: image))
        let window = NSWindow(contentRect: NSRect(x: 0, y: 0, width: 240, height: 120),
                              styleMask: [.titled, .closable], backing: .buffered, defer: false)
        window.contentView = view
        window.center()
        window.makeKeyAndOrderFront(nil)

        Task { @MainActor in
            for _ in 0..<50 {
                try? await Task.sleep(for: .milliseconds(120))
                guard let bitmap = view.bitmapImageRepForCachingDisplay(in: view.bounds) else { continue }
                view.cacheDisplay(in: view.bounds, to: bitmap)
                let scale = CGFloat(bitmap.pixelsWide) / view.bounds.width
                let y = bitmap.pixelsHigh / 2
                let colors = [68, 120, 172].compactMap { x in
                    bitmap.colorAt(x: Int(CGFloat(x) * scale), y: y)?.usingColorSpace(.sRGB)
                }
                guard colors.count == 3, isRed(colors[0]), isRed(colors[1]) else { continue }
                let fallback = colors[2]
                guard (0.93..<0.99).contains(fallback.redComponent),
                      abs(fallback.redComponent - fallback.greenComponent) < 0.02,
                      abs(fallback.redComponent - fallback.blueComponent) < 0.02 else {
                    fail("The unavailable image should render a neutral fallback")
                }
                print("PASS: Avatar renders local and HTTP images and preserves the HTTP error fallback")
                exit(0)
            }
            fail("The local and remote avatar images did not render")
        }
        application.run()
    }

    private static func isRed(_ color: NSColor) -> Bool {
        color.redComponent > 0.75 && color.greenComponent < 0.28 && color.blueComponent < 0.4
    }

    private static func fail(_ message: String) -> Never {
        print("FAIL: \(message)")
        exit(1)
    }
}
