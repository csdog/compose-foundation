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
    @State private var heartbeatVisible = false
    @State private var signal = Color(red: 1, green: 0, blue: 0)

    var body: some View {
        if ProcessInfo.processInfo.arguments.contains("--sheet-animation") {
            ComposeView(markSheetTrigger: {
                signal = Color(red: 0, green: 1, blue: 0)
                DispatchQueue.main.asyncAfter(deadline: .now() + 1.25) {
                    signal = .blue
                }
            })
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
