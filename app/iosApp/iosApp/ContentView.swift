import UIKit
import SwiftUI
import Shared

struct ComposeView: UIViewControllerRepresentable {
    var markSheetReady: (() -> Void)?
    var markSheetTrigger: (() -> Void)?

    func makeUIViewController(context: Self.Context) -> UIViewController {
        if let markSheetReady, let markSheetTrigger {
            return MainViewControllerKt.sheetAnimationViewController(
                markReady: markSheetReady,
                markTrigger: markSheetTrigger
            )
        }
        return MainViewControllerKt.MainViewController()
    }

    func updateUIViewController(_ uiViewController: UIViewController, context: Self.Context) {}
}

struct ContentView: View {
    @State private var heartbeatVisible = false
    @State private var sheetReady = false
    @State private var signal = Color.clear

    var body: some View {
        if ProcessInfo.processInfo.arguments.contains("--sheet-animation") {
            ComposeView(
                markSheetReady: {
                    DispatchQueue.main.async {
                        guard !sheetReady else { return }
                        sheetReady = true
                        signal = Color(red: 1, green: 0, blue: 0)
                    }
                },
                markSheetTrigger: {
                    signal = Color(red: 0, green: 1, blue: 0)
                    DispatchQueue.main.asyncAfter(deadline: .now() + 1.25) {
                        signal = .blue
                    }
                }
            )
                .overlay(
                    HStack(spacing: 4) {
                        Rectangle().fill(signal).frame(width: 16, height: 16)
                        Rectangle().fill(Color.blue).frame(width: 16, height: 16)
                            .opacity(heartbeatVisible ? 1 : 0.2)
                    }
                    .padding(.top, 64)
                    .allowsHitTesting(false),
                    alignment: .topLeading
                )
                .ignoresSafeArea()
                .preferredColorScheme(.light)
                .onAppear {
                    withAnimation(.linear(duration: 0.5).repeatForever(autoreverses: true)) {
                        heartbeatVisible = true
                    }
                }
        } else {
            ComposeView().ignoresSafeArea()
        }
    }
}
