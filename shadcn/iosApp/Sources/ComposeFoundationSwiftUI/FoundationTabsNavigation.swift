public enum FoundationTabsActivationMode {
    case automatic
    case manual
}

struct FoundationTabsNavigation<Value: Hashable> {
    enum Move {
        case first, last, left, right
    }

    let enabledValues: [Value]

    func entryFocus(for selection: Value) -> Value? {
        enabledValues.contains(selection) ? selection : enabledValues.first
    }

    func destination(for move: Move, from focused: Value, rightToLeft: Bool) -> Value? {
        guard let index = enabledValues.firstIndex(of: focused) else { return nil }
        switch move {
        case .first: return enabledValues.first
        case .last: return enabledValues.last
        case .left, .right:
            let offset = move == (rightToLeft ? .left : .right) ? 1 : -1
            return enabledValues[(index + offset + enabledValues.count) % enabledValues.count]
        }
    }

    func selection(
        afterFocusing value: Value, previousFocus: Value?, current: Value, mode: FoundationTabsActivationMode
    ) -> Value? {
        guard mode == .automatic, previousFocus != nil else { return nil }
        return selection(afterActivating: value, current: current)
    }

    func selection(afterActivating value: Value, current: Value) -> Value? {
        guard value != current, enabledValues.contains(value) else { return nil }
        return value
    }
}
