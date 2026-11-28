import SwiftUI
import UIKit

@main
final class CalendarKeyRoutingAudit: NSObject, UIApplicationDelegate {}

@objc(IOSAuditSceneDelegate)
final class CalendarKeyRoutingScene: NSObject, UIWindowSceneDelegate {
    var window: UIWindow?

    func scene(_ scene: UIScene, willConnectTo session: UISceneSession, options: UIScene.ConnectionOptions) {
        guard let scene = scene as? UIWindowScene else { return }
        let window = UIWindow(windowScene: scene)
        self.window = window
        if #available(iOS 15, *), ProcessInfo.processInfo.arguments.contains("--calendar-priority-probe") {
            window.rootViewController = CalendarPriorityProbeController()
        } else {
            window.rootViewController = UIHostingController(rootView: CalendarKeyRoutingFixture(probe: nil))
        }
        window.makeKeyAndVisible()
    }
}

@MainActor
private final class CalendarPriorityProbeState: ObservableObject {
    @Published var pressed = "none"
}

private struct CalendarKeyRoutingFixture: View {
    let probe: CalendarPriorityProbeState?
    @State private var selection: Date? = Self.date(1, 31)

    private static var calendar: Calendar {
        var value = Calendar(identifier: .gregorian)
        value.timeZone = TimeZone(secondsFromGMT: 0)!
        return value
    }

    private static func date(_ month: Int, _ day: Int) -> Date {
        calendar.date(from: DateComponents(year: 2025, month: month, day: day))!
    }

    var body: some View {
        ScrollView {
            VStack(spacing: 8) {
                if let probe { CalendarPriorityProbeLabel(state: probe) }
                Text("Selected: \(dateLabel)")
                FoundationCalendar(selection: $selection, in: Self.date(1, 20)...Self.date(2, 15))
            }
            .padding(12)
        }
        .environment(\.calendar, Self.calendar)
        .environment(\.locale, Locale(identifier: "en_US"))
        .foundationTheme(FoundationTheme(preset: .nova))
    }

    private var dateLabel: String {
        guard let selection else { return "none" }
        let formatter = DateFormatter()
        formatter.calendar = Self.calendar
        formatter.timeZone = Self.calendar.timeZone
        formatter.locale = Locale(identifier: "en_US_POSIX")
        formatter.dateFormat = "yyyy-MM-dd"
        return formatter.string(from: selection)
    }
}

private struct CalendarPriorityProbeLabel: View {
    @ObservedObject var state: CalendarPriorityProbeState

    var body: some View {
        Text("Priority probe: \(state.pressed)")
    }
}

@available(iOS 15, *)
private final class CalendarPriorityProbeController: UIHostingController<CalendarKeyRoutingFixture> {
    private let probe: CalendarPriorityProbeState

    init() {
        let probe = CalendarPriorityProbeState()
        self.probe = probe
        super.init(rootView: CalendarKeyRoutingFixture(probe: probe))
    }

    @MainActor required dynamic init?(coder: NSCoder) { nil }

    override var keyCommands: [UIKeyCommand]? {
        let command = UIKeyCommand(input: UIKeyCommand.inputRightArrow, modifierFlags: [], action: #selector(receiveRight))
        command.wantsPriorityOverSystemBehavior = true
        return (super.keyCommands ?? []) + [command]
    }

    @objc private func receiveRight() {
        probe.pressed = "Right"
    }
}
