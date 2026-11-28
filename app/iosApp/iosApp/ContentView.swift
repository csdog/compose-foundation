import UIKit
import SwiftUI
import Shared

struct ComposeView: UIViewControllerRepresentable {
    var markSheetTrigger: (() -> Void)?

    func makeUIViewController(context: Self.Context) -> UIViewController {
        if let markSheetTrigger {
            return MainViewControllerKt.sheetAnimationViewController(markTrigger: markSheetTrigger)
        }
        return MainViewControllerKt.MainViewController()
    }

    func updateUIViewController(_ uiViewController: UIViewController, context: Self.Context) {}
}

struct ContentView: View {
    @State private var sheetTriggered = false

    var body: some View {
        if ProcessInfo.processInfo.arguments.contains("--sheet-animation") {
            ComposeView(markSheetTrigger: { sheetTriggered = true })
                .overlay(alignment: .topLeading) {
                    Rectangle()
                        .fill(sheetTriggered ? Color(red: 0, green: 1, blue: 0) : Color(red: 1, green: 0, blue: 0))
                        .frame(width: 16, height: 16)
                        .padding(.top, 64)
                        .allowsHitTesting(false)
                }
                .ignoresSafeArea()
                .preferredColorScheme(.light)
        } else {
            ComposeView().ignoresSafeArea()
        }
    }
}
