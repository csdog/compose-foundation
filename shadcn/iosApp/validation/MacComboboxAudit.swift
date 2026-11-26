import SwiftUI

@main
struct MacComboboxAudit: App {
    private let arguments = ProcessInfo.processInfo.arguments

    var body: some Scene {
        let manualTabs = arguments.contains("--tabs-manual-interaction")
        let tabs = manualTabs || arguments.contains("--tabs-interaction")
        let menus = arguments.contains("--menus-interaction")
        let overlays = arguments.contains("--overlay-focus")
        let drawer = arguments.contains("--drawer-focus")
        let popover = arguments.contains("--popover-focus")
        let toasts = arguments.contains("--toast-interaction")
        let groups = arguments.contains("--choice-groups-interaction")
        let carousel = arguments.contains("--carousel-interaction")
        let title = drawer ? "Drawer Focus Audit" : popover ? "Popover Focus Audit"
            : groups ? "Choice Groups Audit" : toasts ? "Toast Audit" : overlays ? "Overlay Focus Audit"
            : menus ? "Menu Audit" : tabs ? "Tabs Audit" : carousel ? "Carousel Audit" : "Combobox Audit"
        WindowGroup(title) {
            Group {
                if groups {
                    ChoiceGroupsInteractionFixture()
                } else if carousel {
                    CarouselInteractionFixture(rightToLeft: arguments.contains("--carousel-rtl"))
                } else if toasts {
                    ToastInteractionFixture()
                } else if #available(macOS 14, *), drawer {
                    DrawerFocusInteractionFixture()
                } else if #available(macOS 14, *), popover {
                    PopoverFocusInteractionFixture()
                } else if #available(macOS 14, *), overlays {
                    OverlayFocusInteractionFixture()
                } else if menus {
                    MenuGroupsInteractionFixture()
                } else if tabs {
                    TabsInteractionFixture(showAutomatic: !manualTabs)
                } else {
                    ComboboxInteractionFixture()
                }
            }
            .frame(width: 420, height: 330)
        }
    }
}
