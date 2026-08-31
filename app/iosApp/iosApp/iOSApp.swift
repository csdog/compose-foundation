import SwiftUI
import Shared

@main
struct iOSApp: App {
    var body: some Scene {
        WindowGroup {
            ContentView()
                .onOpenURL { url in
                    MainViewControllerKt.offerIosInboundDeepLink(uri: url.absoluteString)
                }
        }
    }
}
