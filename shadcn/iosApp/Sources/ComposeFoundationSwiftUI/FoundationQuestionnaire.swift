import SwiftUI

public struct FoundationQuestionnaire: View {
    public enum Kind {
        case single([FoundationChoice<String>])
        case multiple([FoundationChoice<String>])
        case text(placeholder: String)
    }

    public enum Answer: Equatable {
        case single(String), multiple(Set<String>), text(String), skipped
    }

    public struct Question: Identifiable {
        public let id: String
        public let title: String
        public let description: String?
        public let kind: Kind
        public let isRequired: Bool

        public init(id: String, title: String, description: String? = nil, kind: Kind, isRequired: Bool = true) {
            self.id = id
            self.title = title
            self.description = description
            self.kind = kind
            self.isRequired = isRequired
        }
    }

    @Environment(\.foundationTheme) private var theme
    @Environment(\.colorScheme) private var colorScheme
    @Binding private var answers: [String: Answer]
    @Binding private var index: Int
    @State private var invalidQuestion: String?

    private let questions: [Question]
    private let onSubmit: ([String: Answer]) -> Void

    public init(
        _ questions: [Question],
        index: Binding<Int>,
        answers: Binding<[String: Answer]>,
        onSubmit: @escaping ([String: Answer]) -> Void
    ) {
        self.questions = questions
        self._index = index
        self._answers = answers
        self.onSubmit = onSubmit
    }

    public var body: some View {
        let palette = theme.palette(for: colorScheme)

        if !questions.isEmpty {
            let current = min(max(index, 0), questions.count - 1)
            let question = questions[current]

            VStack(alignment: .leading, spacing: 10) {
                ProgressView(value: Double(current + 1), total: Double(questions.count))
                    .accentColor(palette.primary)
                    .accessibilityLabel(Text("Question \(current + 1) of \(questions.count)"))
                Text(question.title).font(.headline).accessibilityAddTraits(.isHeader)
                if let description = question.description {
                    Text(description).font(.subheadline).foregroundColor(palette.mutedForeground)
                }
                input(for: question).id(question.id)
                if invalidQuestion == question.id && !hasAnswer(to: question) {
                    Text("An answer is required").font(.caption).foregroundColor(palette.destructive)
                }
                HStack {
                    Button("Back") {
                        invalidQuestion = nil
                        index = current - 1
                    }
                    .disabled(current == 0)
                    Spacer(minLength: 0)
                    if !question.isRequired {
                        Button("Skip") {
                            answers[question.id] = .skipped
                            advance(from: current)
                        }
                    }
                    Button(current == questions.count - 1 ? "Submit" : "Next") {
                        if question.isRequired && !hasAnswer(to: question) {
                            invalidQuestion = question.id
                        } else {
                            advance(from: current)
                        }
                    }
                }
            }
            .foregroundColor(palette.foreground)
            .padding(14)
            .background(RoundedRectangle(cornerRadius: theme.cornerRadius).fill(palette.card))
        }
    }

    @ViewBuilder
    private func input(for question: Question) -> some View {
        switch question.kind {
        case let .single(choices):
            ForEach(choices) { choice in
                let selected = answers[question.id] == .single(choice.value)
                Button {
                    answers[question.id] = .single(choice.value)
                } label: {
                    Label(choice.title, systemImage: selected ? "largecircle.fill.circle" : "circle")
                }
                .buttonStyle(PlainButtonStyle())
                .disabled(!choice.isEnabled)
                .accessibilityAddTraits(selected ? .isSelected : [])
            }
        case let .multiple(choices):
            ForEach(choices) { choice in
                Toggle(choice.title, isOn: Binding(
                    get: { multipleAnswers(question.id).contains(choice.value) },
                    set: { selected in
                        var values = multipleAnswers(question.id)
                        if selected { values.insert(choice.value) } else { values.remove(choice.value) }
                        answers[question.id] = .multiple(values)
                    }
                ))
                .disabled(!choice.isEnabled)
            }
        case let .text(placeholder):
            TextField(placeholder, text: Binding(
                get: {
                    guard case let .text(value) = answers[question.id] else { return "" }
                    return value
                },
                set: { answers[question.id] = .text($0) }
            ))
            .textFieldStyle(RoundedBorderTextFieldStyle())
            .accessibilityLabel(Text(question.title))
        }
    }

    private func multipleAnswers(_ id: String) -> Set<String> {
        guard case let .multiple(values) = answers[id] else { return [] }
        return values
    }

    private func hasAnswer(to question: Question) -> Bool {
        switch (question.kind, answers[question.id]) {
        case let (.single(choices), .single(value)):
            return choices.contains { $0.value == value && $0.isEnabled }
        case let (.multiple(choices), .multiple(values)):
            return choices.contains { values.contains($0.value) && $0.isEnabled }
        case let (.text, .text(value)):
            return !value.trimmingCharacters(in: .whitespacesAndNewlines).isEmpty
        default:
            return false
        }
    }

    private func advance(from current: Int) {
        invalidQuestion = nil
        if current < questions.count - 1 {
            index = current + 1
        } else if let missing = questions.firstIndex(where: { $0.isRequired && !hasAnswer(to: $0) }) {
            index = missing
            invalidQuestion = questions[missing].id
        } else {
            onSubmit(answers)
        }
    }
}
