@main
enum TabsNavigationAudit {
    static func main() {
        let tabs = FoundationTabsNavigation(enabledValues: ["overview", "activity", "settings"])
        precondition(tabs.entryFocus(for: "activity") == "activity", "Entry follows the enabled selection")
        precondition(tabs.entryFocus(for: "restricted") == "overview", "Disabled selection falls back to an enabled tab")
        precondition(tabs.destination(for: .right, from: "overview", rightToLeft: false) == "activity",
                     "Right skips disabled tabs in left-to-right order")
        precondition(tabs.destination(for: .left, from: "overview", rightToLeft: true) == "activity",
                     "Left skips disabled tabs in right-to-left order")
        precondition(tabs.destination(for: .left, from: "overview", rightToLeft: false) == "settings",
                     "Backward movement wraps from first to last")
        precondition(tabs.destination(for: .left, from: "settings", rightToLeft: true) == "overview",
                     "Forward movement in RTL wraps from last to first")
        precondition(tabs.destination(for: .first, from: "settings", rightToLeft: true) == "overview",
                     "Home finds the first enabled tab even in RTL")
        precondition(tabs.destination(for: .last, from: "overview", rightToLeft: false) == "settings",
                     "End finds the last enabled tab")
        precondition(tabs.destination(for: .right, from: "restricted", rightToLeft: false) == nil,
                     "A disabled focus target cannot handle navigation")

        precondition(tabs.selection(afterFocusing: "activity", previousFocus: "overview", current: "overview",
                                    mode: .automatic) == "activity", "Automatic activation follows focus")
        precondition(tabs.selection(afterFocusing: "activity", previousFocus: "overview", current: "overview",
                                    mode: .manual) == nil, "Manual focus does not change the selected panel")
        precondition(tabs.selection(afterFocusing: "overview", previousFocus: nil, current: "restricted",
                                    mode: .automatic) == nil, "Focus entry does not overwrite a controlled selection")
        precondition(tabs.selection(afterFocusing: "activity", previousFocus: "overview", current: "activity",
                                    mode: .automatic) == nil, "Repeated focus delivery does not write the binding twice")
        precondition(tabs.selection(afterActivating: "activity", current: "overview") == "activity",
                     "Explicit activation commits the enabled focused tab")
        precondition(tabs.selection(afterActivating: "restricted", current: "overview") == nil,
                     "Explicit activation ignores disabled tabs")

        let disabled = FoundationTabsNavigation<String>(enabledValues: [])
        precondition(disabled.entryFocus(for: "overview") == nil, "An entirely disabled strip has no focus entry")
        precondition(disabled.selection(afterActivating: "overview", current: "activity") == nil,
                     "An entirely disabled strip cannot change selection")
        let single = FoundationTabsNavigation(enabledValues: ["overview"])
        precondition(single.destination(for: .left, from: "overview", rightToLeft: false) == "overview",
                     "A strip with one enabled tab wraps to itself")
        print("PASS: Tabs entry focus, LTR and RTL movement, disabled tabs, and controlled activation")
    }
}
