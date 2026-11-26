import AppKit
import SwiftUI

@main @MainActor
enum RenderAudit {
    static func main() throws {
        guard CommandLine.arguments.count == 3 else {
            throw CocoaError(.fileReadInvalidFileName)
        }
        let catalog = URL(fileURLWithPath: CommandLine.arguments[1])
        let directory = URL(fileURLWithPath: CommandLine.arguments[2], isDirectory: true)
        let manifest = try JSONDecoder().decode(SwiftUIAuditFixtures.Registry.self, from: Data(contentsOf: catalog))
        let fixtures = SwiftUIAuditFixtures.cases
        guard Set(manifest.items.map(\.name)) == Set(fixtures.keys) else {
            throw CocoaError(.fileReadCorruptFile)
        }

        NSApplication.shared.setActivationPolicy(.prohibited)
        try FileManager.default.createDirectory(at: directory, withIntermediateDirectories: true)
        let size = NSSize(width: 480, height: 440)
        let window = NSWindow(contentRect: NSRect(origin: .zero, size: size), styleMask: [.borderless],
                              backing: .buffered, defer: false)

        for name in fixtures.keys.sorted() {
            guard let fixture = fixtures[name] else { continue }
            try render(fixture(), dark: false, name: name, in: window, directory: directory, size: size)
            try render(fixture(), dark: true, name: name, in: window, directory: directory, size: size)
        }
        print("Rendered \(fixtures.count) registry fixtures in both appearances.")
    }

    private static func render(
        _ fixture: AnyView, dark: Bool, name: String, in window: NSWindow, directory: URL, size: NSSize
    ) throws {
        let scheme: ColorScheme = dark ? .dark : .light
        let palette = FoundationTheme().palette(for: scheme)
        let host = NSHostingView(rootView: fixture.padding(16)
            .frame(width: size.width, height: size.height)
            .background(palette.background)
            .environment(\.colorScheme, scheme)
            .environment(\.locale, Locale(identifier: "en_US")))
        window.appearance = NSAppearance(named: dark ? .darkAqua : .aqua)
        window.contentView = host
        host.layoutSubtreeIfNeeded()
        window.display()
        guard let bitmap = host.bitmapImageRepForCachingDisplay(in: host.bounds) else {
            throw CocoaError(.fileWriteUnknown)
        }
        host.cacheDisplay(in: host.bounds, to: bitmap)
        guard let png = bitmap.representation(using: .png, properties: [:]) else {
            throw CocoaError(.fileWriteUnknown)
        }
        try png.write(to: directory.appendingPathComponent("\(name)-\(dark ? "dark" : "light").png"))
    }
}
