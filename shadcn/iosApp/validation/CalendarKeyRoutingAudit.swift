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
        if ProcessInfo.processInfo.arguments.contains("--fka-native-control") {
            window.rootViewController = CalendarNativeControlProbe()
        } else if ProcessInfo.processInfo.arguments.contains("--fka-swiftui-control") {
            let scrolls = ProcessInfo.processInfo.arguments.contains("--fka-scroll")
            window.rootViewController = UIHostingController(rootView: CalendarSwiftUIControlProbe(scrolls: scrolls))
        } else if #available(iOS 15, *), ProcessInfo.processInfo.arguments.contains("--calendar-priority-probe") {
            window.rootViewController = CalendarPriorityProbeController()
        } else {
            window.rootViewController = UIHostingController(rootView: CalendarKeyRoutingFixture(probe: nil))
        }
        window.makeKeyAndVisible()
    }
}

private struct CalendarSwiftUIControlProbe: View {
    let scrolls: Bool
    @State private var result = "none"

    var body: some View {
        if scrolls {
            ScrollView { controls }
        } else {
            controls
        }
    }

    private var controls: some View {
        VStack(spacing: 16) {
            Text("SwiftUI result: \(result)")
            Button("First SwiftUI control") { result = "First control" }
            Button("Second SwiftUI control") { result = "Second control" }
        }
        .padding(24)
    }
}

private final class CalendarNativeControlProbe: UIViewController {
    private let result = UILabel()

    override func viewDidLoad() {
        super.viewDidLoad()
        view.backgroundColor = .systemBackground
        result.text = "UIKit result: none"
        let controls = ["First control", "Second control"].map { title in
            let button = UIButton(type: .system)
            button.setTitle(title, for: .normal)
            button.addTarget(self, action: #selector(activate(_:)), for: .touchUpInside)
            return button
        }
        let stack = UIStackView(arrangedSubviews: [result])
        for control in controls { stack.addArrangedSubview(control) }
        stack.axis = .vertical
        stack.spacing = 16
        stack.translatesAutoresizingMaskIntoConstraints = false
        view.addSubview(stack)
        NSLayoutConstraint.activate([
            stack.centerXAnchor.constraint(equalTo: view.centerXAnchor),
            stack.topAnchor.constraint(equalTo: view.safeAreaLayoutGuide.topAnchor, constant: 32),
        ])
    }

    @objc private func activate(_ sender: UIButton) {
        result.text = "UIKit result: \(sender.currentTitle ?? "unknown")"
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
