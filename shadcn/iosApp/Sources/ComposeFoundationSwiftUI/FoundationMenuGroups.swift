import SwiftUI

public struct FoundationMenuAction: Identifiable {
    public let id: String
    public let title: String
    public let isEnabled: Bool
    let perform: () -> Void

    public init(id: String, title: String, isEnabled: Bool = true, action: @escaping () -> Void) {
        self.id = id
        self.title = title
        self.isEnabled = isEnabled
        self.perform = action
    }
}

public struct FoundationMenuGroup: Identifiable {
    public let id: String
    public let title: String
    public let isEnabled: Bool
    public let actions: [FoundationMenuAction]

    public init(id: String, title: String, isEnabled: Bool = true, actions: [FoundationMenuAction]) {
        self.id = id
        self.title = title
        self.isEnabled = isEnabled
        self.actions = actions
    }
}

struct FoundationMenuGroups: View {
    let groups: [FoundationMenuGroup]
    let spacing: CGFloat

    var body: some View {
        if #available(iOS 17, macOS 14, *) {
            FoundationKeyboardMenuGroups(groups: groups, spacing: spacing)
        } else {
            HStack(spacing: spacing) {
                ForEach(groups) { group in
                    Menu(group.title) {
                        ForEach(group.actions) { action in
                            Button(action.title, action: action.perform).disabled(!action.isEnabled)
                        }
                    }
                    .disabled(!group.isEnabled)
                }
            }
        }
    }
}

@available(iOS 17, macOS 14, *)
private struct FoundationKeyboardMenuGroups: View {
    @Environment(\.layoutDirection) private var layoutDirection
    #if os(iOS)
    @Environment(\.horizontalSizeClass) private var horizontalSizeClass
    #endif
    #if os(macOS)
    @FocusState private var triggersFocused: Bool
    @State private var activeTriggerID: String?
    #else
    @FocusState private var focus: String?
    #endif
    @State private var openID: String?
    @State private var entryActionID: String?
    @State private var returnID: String?

    let groups: [FoundationMenuGroup]
    let spacing: CGFloat

    private var enabledGroups: [FoundationMenuGroup] { groups.filter(\.isEnabled) }
    private var openGroup: FoundationMenuGroup? { enabledGroups.first { $0.id == openID } }

    #if os(macOS)
    private var activeTrigger: FoundationMenuGroup? {
        enabledGroups.first { $0.id == activeTriggerID } ?? enabledGroups.first
    }
    #endif

    private var isPresented: Binding<Bool> {
        Binding(get: { openID != nil }, set: { if !$0 { dismiss() } })
    }

    var body: some View {
        #if os(iOS)
        if horizontalSizeClass == .compact {
            triggers.sheet(isPresented: isPresented, onDismiss: restoreFocus) { panel }
        } else {
            popover
        }
        #else
        popover
        #endif
    }

    private var triggers: some View {
        HStack(spacing: spacing) {
            ForEach(groups) { group in
                FoundationMenuGroupButton(title: group.title, enabled: group.isEnabled, focused: triggerIsFocused(group)) {
                    open(group)
                }
                .accessibilityValue(openID == group.id ? Text("Expanded") : Text("Collapsed"))
                #if !os(macOS)
                .focusable(group.isEnabled)
                .focused($focus, equals: group.id)
                .onKeyPress(keys: [.leftArrow, .rightArrow, .upArrow, .downArrow, .home, .end, .return, .space]) {
                    handleTrigger($0, group: group)
                }
                #endif
            }
        }
        #if os(macOS)
        .focusable(!enabledGroups.isEmpty)
        .focusEffectDisabled()
        .focused($triggersFocused)
        .defaultFocus($triggersFocused, true, priority: .userInitiated)
        .onKeyPress(keys: [.leftArrow, .rightArrow, .upArrow, .downArrow, .home, .end, .return, .space]) {
            guard let activeTrigger else { return .ignored }
            return handleTrigger($0, group: activeTrigger)
        }
        #else
        .defaultFocus($focus, enabledGroups.first?.id, priority: .userInitiated)
        #endif
        .onChange(of: enabledGroups.map(\.id)) { _, enabled in
            if let openID, !enabled.contains(openID) { dismiss() }
        }
    }

    private func triggerIsFocused(_ group: FoundationMenuGroup) -> Bool {
        #if os(macOS)
        triggersFocused && activeTrigger?.id == group.id
        #else
        focus == group.id
        #endif
    }

    @ViewBuilder private var panel: some View {
        if let group = openGroup {
            FoundationMenuGroupPanel(
                group: group, entryID: entryActionID,
                dismiss: dismiss, switchGroup: { key in switchGroup(key, from: group.id) }
            )
            #if !os(macOS)
            .id(group.id)
            #endif
        }
    }

    private var popover: some View {
        #if os(macOS)
        triggers.modifier(FoundationPopover(isPresented: isPresented, onDismiss: restoreFocus) { panel })
        #else
        triggers.popover(isPresented: isPresented) { panel }
            .onChange(of: openID) { previous, current in
                if previous != nil, current == nil { restoreFocus() }
            }
        #endif
    }

    private func dismiss() {
        guard let openID else { return }
        returnID = openID
        self.openID = nil
    }

    private func restoreFocus() {
        guard openID == nil, let returnID else { return }
        self.returnID = nil
        #if os(macOS)
        activeTriggerID = returnID
        triggersFocused = !enabledGroups.isEmpty
        #else
        if enabledGroups.contains(where: { $0.id == returnID }) {
            focus = returnID
        }
        #endif
    }

    private func open(_ group: FoundationMenuGroup, atEnd: Bool = false) {
        guard group.isEnabled else { return }
        #if os(macOS)
        activeTriggerID = group.id
        #endif
        returnID = nil
        let enabled = group.actions.filter(\.isEnabled)
        entryActionID = (atEnd ? enabled.last : enabled.first)?.id
        openID = group.id
    }

    private func handleTrigger(_ press: KeyPress, group: FoundationMenuGroup) -> KeyPress.Result {
        guard menuAccepts(press) else { return .ignored }
        switch press.key {
        case .leftArrow, .rightArrow, .home, .end:
            if let target = menuDestination(press.key, from: group.id, within: enabledGroups.map(\.id), layoutDirection: layoutDirection) {
                #if os(macOS)
                activeTriggerID = target
                #else
                focus = target
                #endif
            }
        case .upArrow, .downArrow, .return, .space:
            if press.phase == .down { open(group, atEnd: press.key == .upArrow) }
        default: return .ignored
        }
        return .handled
    }

    private func switchGroup(_ key: KeyEquivalent, from id: String) {
        let candidates = enabledGroups
        if let target = menuDestination(key, from: id, within: candidates.map(\.id), layoutDirection: layoutDirection),
           let next = candidates.first(where: { $0.id == target }) {
            open(next)
        }
    }
}

@available(iOS 17, macOS 14, *)
private struct FoundationMenuGroupPanel: View {
    @Environment(\.foundationTheme) private var theme
    @Environment(\.colorScheme) private var colorScheme
    #if os(macOS)
    // Retain the same focus owner when the popover replaces the visible menu group.
    @FocusState private var panelFocused: Bool
    @State private var activeActionID: String?
    #else
    @FocusState private var focus: String?
    #endif

    let group: FoundationMenuGroup
    let entryID: String?
    let dismiss: () -> Void
    let switchGroup: (KeyEquivalent) -> Void

    var body: some View {
        let palette = theme.palette(for: colorScheme)
        ScrollViewReader { scroll in
            ScrollView {
                VStack(alignment: .leading, spacing: 2) {
                    ForEach(group.actions) { action in
                        FoundationMenuGroupButton(
                            title: action.title, enabled: action.isEnabled, focused: highlightedActionID == action.id, fillsWidth: true
                        ) {
                            activate(action)
                        }
                        #if os(macOS)
                        .id(action.id)
                        #else
                        .focusable(action.isEnabled)
                        .focused($focus, equals: action.id)
                        .id(action.id)
                        .onKeyPress(keys: [.leftArrow, .rightArrow, .upArrow, .downArrow, .home, .end, .return, .space, .escape]) {
                            handle($0, action: action, scroll: scroll)
                        }
                        #endif
                    }
                }
            }
            .frame(minWidth: 156, maxWidth: 280, maxHeight: 314)
            .fixedSize(horizontal: false, vertical: true)
            #if os(macOS)
            .focusable()
            .focusEffectDisabled()
            .focused($panelFocused)
            .defaultFocus($panelFocused, true)
            .onKeyPress(keys: [.leftArrow, .rightArrow, .upArrow, .downArrow, .home, .end, .return, .space, .escape]) {
                handle($0, action: nil, scroll: scroll)
            }
            .task(id: group.id) {
                activeActionID = entryID
                if let entryID { scroll.scrollTo(entryID) }
                panelFocused = true
            }
            #else
            .onChange(of: focus) { _, target in
                if let target { scroll.scrollTo(target) }
            }
            #endif
        }
        .padding(5)
        .background(palette.card)
        #if !os(macOS)
        .defaultFocus($focus, entryID)
        .task(id: group.id) {
            await Task.yield()
            guard !Task.isCancelled else { return }
            focus = entryID
        }
        #endif
        .accessibilityAction(.escape, dismiss)
    }

    private var highlightedActionID: String? {
        #if os(macOS)
        let intended = activeActionID ?? entryID
        return group.actions.first { $0.id == intended && $0.isEnabled }?.id
            ?? group.actions.first(where: \.isEnabled)?.id
        #else
        focus
        #endif
    }

    private func activate(_ action: FoundationMenuAction) {
        guard action.isEnabled else { return }
        dismiss()
        action.perform()
    }

    private func handle(_ press: KeyPress, action: FoundationMenuAction?, scroll: ScrollViewProxy) -> KeyPress.Result {
        guard menuAccepts(press) else { return .ignored }
        let enabled = group.actions.filter(\.isEnabled)
        let active = enabled.first { $0.id == highlightedActionID } ?? action ?? enabled.first
        switch press.key {
        case .escape: dismiss()
        case .return, .space:
            if press.phase == .down, let active { activate(active) }
        case .upArrow, .downArrow, .home, .end:
            if let active,
               let target = menuDestination(press.key, from: active.id, within: enabled.map(\.id), layoutDirection: .leftToRight) {
                #if os(macOS)
                activeActionID = target
                scroll.scrollTo(target)
                #else
                focus = target
                #endif
            }
        case .leftArrow, .rightArrow:
            switchGroup(press.key)
        default: return .ignored
        }
        return .handled
    }
}

private struct FoundationMenuGroupButton: View {
    @Environment(\.foundationTheme) private var theme
    @Environment(\.colorScheme) private var colorScheme

    let title: String
    let enabled: Bool
    let focused: Bool
    var fillsWidth = false
    let action: () -> Void

    var body: some View {
        let palette = theme.palette(for: colorScheme)
        let shape = RoundedRectangle(cornerRadius: theme.cornerRadius)
        Button(action: action) {
            Text(title)
                .padding(.horizontal, 10)
                .frame(maxWidth: fillsWidth ? .infinity : nil, minHeight: 44, alignment: .leading)
                .foregroundColor(enabled ? palette.foreground : palette.mutedForeground)
                .overlay(shape.strokeBorder(focused ? palette.ring : .clear, lineWidth: 2).allowsHitTesting(false))
                .contentShape(Rectangle())
        }
        .buttonStyle(PlainButtonStyle())
        .disabled(!enabled)
    }
}

@available(iOS 17, macOS 14, *)
private func menuDestination(
    _ key: KeyEquivalent, from current: String, within ids: [String], layoutDirection: LayoutDirection
) -> String? {
    guard let index = ids.firstIndex(of: current) else { return nil }
    switch key {
    case .home: return ids.first
    case .end: return ids.last
    default:
        let forward = key == .downArrow || key == (layoutDirection == .rightToLeft ? .leftArrow : .rightArrow)
        return ids[(index + (forward ? 1 : -1) + ids.count) % ids.count]
    }
}

@available(iOS 17, macOS 14, *)
private func menuAccepts(_ press: KeyPress) -> Bool {
    press.modifiers.intersection([.command, .control, .option, .shift]).isEmpty
}
