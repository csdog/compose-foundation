# Compose Foundation for SwiftUI

Native SwiftUI implementations for all 69 Compose Foundation registry entries,
including the theme, utilities, and chat and AI components. The package
supports iOS 14+ and macOS 11+ and builds with Swift 6.

Add the local `shadcn/swiftui` package in Xcode and select the
`ComposeFoundationSwiftUI` product. To copy component source into your own
package, point shadcn at an empty directory and add the resulting local package
to your app.

```sh
mkdir -p Packages/ComposeFoundationSwiftUI
npx shadcn@latest add \
  https://compose-foundation.devops.cn.toutiaoyan.com/r/swiftui/button.json \
  --cwd Packages/ComposeFoundationSwiftUI
```

The registry adds the package manifest, theme and component source. Add other
components from `/r/swiftui/registry.json` to the same directory. To preview the
registry locally, follow the [local server instructions](../registry/README.md)
and use `http://127.0.0.1:8765/r/swiftui/button.json`.

```swift
import ComposeFoundationSwiftUI
import SwiftUI

struct SaveButton: View {
    let save: () -> Void

    var body: some View {
        Button("Save", action: save)
            .buttonStyle(FoundationButtonStyle())
    }
}
```

Set `.foundationTheme(FoundationTheme(...))` on an ancestor view to customize
the light and dark palettes and corner radius. Components use the system's
color scheme to select the palette.

Opt in to a shadcn style with `.foundationTheme(FoundationTheme(preset: .nova))`.
The preset choices are Vega, Nova, Maia, Lyra, Mira, Luma, Sera, and Rhea.
Presets use the same palette API and accept an optional `cornerRadius`
override. Use the initializer without a preset for the original styling.

Use `FoundationButtonStyle`, `FoundationSwitchStyle`, and `FoundationCheckbox`
as styles for SwiftUI buttons and toggles. Apply overlays such as
`FoundationDialog`, `FoundationPopover`, and `FoundationToast` with `.modifier(...)`.
Selection components such as Tabs, Select, and Combobox accept
`FoundationChoice` values. Native menus and presentations follow the platform's
keyboard, pointer, and adaptive presentation behavior.

Combobox accepts `Binding<Value?>` for single selection and
`Binding<Set<Value>>` for multiple selection. Multiple selections appear as
removable chips. Combobox uses UIKit key commands on iOS and SwiftUI key events
on macOS 14+ for Up, Down, Return, and Escape.

Select and Combobox also accept `groups: [FoundationChoiceGroup<Value>]`.
Combobox searches across the choices and hides groups with no matches. Use
a distinct choice value for each item in the control.

```swift
let groups = [
    FoundationChoiceGroup("Fruit", choices: [
        FoundationChoice("Apple", value: "apple"),
    ]),
    FoundationChoiceGroup("Vegetables", choices: [
        FoundationChoice("Carrot", value: "carrot"),
    ]),
]

FoundationSelect("Produce", groups: groups, selection: $selectedProduce)
```

Tabs use automatic keyboard activation by default on iOS 17+ and macOS 14+.
Pass `activationMode: .manual` to move focus with Left, Right, Home, or End and
activate the focused tab with Return or Space.

Use `FoundationMenubar(groups:)` or `FoundationNavigationMenu(groups:)` to
provide named `FoundationMenuGroup` values and their
`FoundationMenuAction` buttons. The grouped overloads handle horizontal
navigation between triggers and vertical navigation within their panels
on iOS 17+ and macOS 14+. The view-builder initializers continue to accept
custom content.

Use `FoundationToaster` with a `@StateObject` to show multiple notifications.
Apply the modifier at the root of your content. It shows the three newest
notifications by default. Set `durationMilliseconds: 0` to keep a toast visible
until it is dismissed.

```swift
struct SavePanel: View {
    @StateObject private var toasts = FoundationToastState()

    var body: some View {
        Button("Save") {
            toasts.show("Changes saved", variant: .success)
        }
        .frame(maxWidth: .infinity, maxHeight: .infinity)
        .modifier(FoundationToaster(state: toasts))
    }
}
```

Dialog, Sheet, Drawer, and Popover accept an `onDismiss` closure for restoring
focus to the presenting control. Dialog, Sheet, and Drawer move initial
keyboard focus to their close action on iOS 17+ and macOS 14+, and prefer the
title with VoiceOver. Popover content can use its own `FocusState`.

Calendar accepts single-date, range, and multiple-date bindings. Use
`FoundationCalendar(range: $range)` with `FoundationCalendarRange?` for a
two-step selection, or `FoundationCalendar(selectedDates: $dates)` with
`Set<Date>` for independent days. Both initializers accept an optional
inclusive `in:` bound.

Pass `numberOfMonths: 2` to display consecutive months and
`captionLayout: .dropdown` to add month and year menus. Use
`.dropdownMonths` or `.dropdownYears` for just one menu. The date bounds
also constrain the visible months and the available menu choices.

Set the width of `FoundationCarousel` with a frame or its parent layout.
The viewport takes the height of the selected page. Drag horizontally to
reveal the adjacent page, or use Previous and Next. The selection binding
updates when the carousel commits to a page.

Use `FoundationTriStateCheckbox(state: $state) { Text("Select all") }` with
`FoundationCheckboxState` to display `.mixed`. Activating a mixed checkbox
checks it. The existing `FoundationCheckbox` toggle style can also render the
mixed state supplied by SwiftUI on iOS 16+ and macOS 13+.

See the [component audit](../../docs/swiftui-parity-audit.md) for the verified
coverage, platform differences, and rendering commands.
