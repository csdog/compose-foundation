// swift-tools-version: 6.0
import PackageDescription

let package = Package(
    name: "ComposeFoundationSwiftUI",
    platforms: [.iOS(.v14), .macOS(.v11)],
    products: [
        .library(name: "ComposeFoundationSwiftUI", targets: ["ComposeFoundationSwiftUI"])
    ],
    targets: [
        .target(name: "ComposeFoundationSwiftUI")
    ]
)
