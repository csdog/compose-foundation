#if os(iOS)
import UIKit

final class FoundationSearchTextField: UITextField, UITextFieldDelegate {
    var activatesOnAttach = false
    var writeQuery: ((String) -> Void)?
    var move: ((Int) -> Bool)?
    var submit: (() -> Void)?
    var dismiss: (() -> Void)?

    override func didMoveToWindow() {
        super.didMoveToWindow()
        guard activatesOnAttach, window != nil, !UIAccessibility.isVoiceOverRunning else { return }
        DispatchQueue.main.async { [weak self] in
            guard let self, window != nil else { return }
            becomeFirstResponder()
        }
    }

    override var keyCommands: [UIKeyCommand]? {
        guard markedTextRange == nil else { return super.keyCommands }
        var inputs = [UIKeyCommand.inputUpArrow, UIKeyCommand.inputDownArrow, "\r"]
        if dismiss != nil { inputs.append(UIKeyCommand.inputEscape) }
        return inputs.map { input in
            let command = UIKeyCommand(input: input, modifierFlags: [], action: #selector(handleCommand(_:)))
            if #available(iOS 15, *) {
                command.wantsPriorityOverSystemBehavior = true
            }
            return command
        }
    }

    @objc private func handleCommand(_ command: UIKeyCommand) {
        guard markedTextRange == nil else { return }
        switch command.input {
        case UIKeyCommand.inputUpArrow: _ = move?(-1)
        case UIKeyCommand.inputDownArrow: _ = move?(1)
        case "\r": submit?()
        case UIKeyCommand.inputEscape: dismiss?()
        default: break
        }
    }

    @objc func updateQuery() { writeQuery?(text ?? "") }

    func textFieldShouldReturn(_ textField: UITextField) -> Bool {
        submit?()
        return false
    }
}
#endif
