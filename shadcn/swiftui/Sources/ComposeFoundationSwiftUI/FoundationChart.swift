import SwiftUI

public struct FoundationChart: View {
    public enum Variant: Sendable { case bar, line, area }

    public struct Series: Identifiable {
        public let id: String
        public let values: [Double]
        public let color: Color?

        public init(_ id: String, values: [Double], color: Color? = nil) {
            self.id = id
            self.values = values
            self.color = color
        }
    }

    @Environment(\.foundationTheme) private var theme
    @Environment(\.colorScheme) private var colorScheme
    @Binding private var selection: Int?

    private let labels: [String]
    private let series: [Series]
    private let variant: Variant

    public init(
        labels: [String],
        series: [Series],
        variant: Variant = .bar,
        selection: Binding<Int?> = .constant(nil)
    ) {
        self.labels = labels
        self.series = series
        self.variant = variant
        self._selection = selection
    }

    public var body: some View {
        let palette = theme.palette(for: colorScheme)
        let values = series.flatMap { $0.values.prefix(labels.count) }.filter(\.isFinite)

        VStack(alignment: .leading, spacing: 6) {
            if labels.isEmpty || values.isEmpty {
                Text("No chart data").font(.subheadline).foregroundColor(palette.mutedForeground)
            } else {
                GeometryReader { geometry in
                    plot(size: geometry.size, values: values, palette: palette)
                        .contentShape(Rectangle())
                        .gesture(DragGesture(minimumDistance: 0).onChanged { value in
                            guard geometry.size.width > 0 else { return }
                            let position = min(max(value.location.x / geometry.size.width, 0), 1)
                            selection = min(Int(position * CGFloat(labels.count)), labels.count - 1)
                        })
                        .accessibilityHidden(true)
                }
                .frame(minHeight: 140)
                HStack {
                    Text(labels[0])
                    Spacer(minLength: 0)
                    if labels.count > 1 { Text(labels[labels.count - 1]) }
                }
                .font(.caption)
                .foregroundColor(palette.mutedForeground)
                .accessibilityHidden(true)
                if let selection, labels.indices.contains(selection) {
                    Text(summary(at: selection)).font(.caption).foregroundColor(palette.foreground)
                }
            }
        }
        .accessibilityElement(children: .ignore)
        .accessibilityLabel(Text(values.isEmpty ? "No chart data" : accessibleSummary))
        .accessibilityAdjustableAction { direction in
            guard !labels.isEmpty, !values.isEmpty else { return }
            let current = selection.flatMap { labels.indices.contains($0) ? $0 : nil }
            switch direction {
            case .increment:
                selection = current.map { min($0 + 1, labels.count - 1) } ?? 0
            case .decrement:
                selection = current.map { max($0 - 1, 0) } ?? 0
            @unknown default:
                break
            }
        }
    }

    private func plot(size: CGSize, values: [Double], palette: FoundationPalette) -> some View {
        let scale = max(1, values.map(abs).max() ?? 1)
        let upper = max(0, (values.max() ?? 0) / scale)
        let lower = min(0, (values.min() ?? 0) / scale)
        let span = upper == lower ? 1 : upper - lower
        let baseline = size.height * (upper / span)
        let step = size.width / CGFloat(labels.count)

        return ZStack {
            Path { path in
                path.move(to: CGPoint(x: 0, y: baseline))
                path.addLine(to: CGPoint(x: size.width, y: baseline))
            }
            .stroke(palette.border, lineWidth: 1)
            ForEach(series.indices, id: \.self) { index in
                let points = series[index].values.prefix(labels.count).enumerated().compactMap { position, value in
                    value.isFinite
                        ? CGPoint(x: step * (CGFloat(position) + 0.5), y: size.height * (upper - value / scale) / span)
                        : nil
                }
                let color = series[index].color ?? palette.primary.opacity(1 / (1 + Double(index) * 0.35))
                marks(points: points, index: index, step: step, baseline: baseline, color: color)
            }
            if let selection, labels.indices.contains(selection) {
                Path { path in
                    let x = step * (CGFloat(selection) + 0.5)
                    path.move(to: CGPoint(x: x, y: 0))
                    path.addLine(to: CGPoint(x: x, y: size.height))
                }
                .stroke(palette.ring, style: StrokeStyle(lineWidth: 1, dash: [3, 3]))
            }
        }
    }

    @ViewBuilder
    private func marks(points: [CGPoint], index: Int, step: CGFloat, baseline: CGFloat, color: Color) -> some View {
        if let first = points.first, let last = points.last {
            if variant == .bar {
                let width = step * 0.75 / CGFloat(series.count)
                Path { path in
                    for point in points {
                        let x = point.x - step * 0.375 + CGFloat(index) * width
                        path.addRect(CGRect(
                            x: x, y: min(point.y, baseline), width: width * 0.9, height: abs(point.y - baseline)
                        ))
                    }
                }
                .fill(color)
            } else {
                if variant == .area {
                    Path { path in
                        path.move(to: CGPoint(x: first.x, y: baseline))
                        for point in points { path.addLine(to: point) }
                        path.addLine(to: CGPoint(x: last.x, y: baseline))
                        path.closeSubpath()
                    }
                    .fill(color.opacity(0.18))
                }
                Path { $0.addLines(points) }
                    .stroke(color, style: StrokeStyle(lineWidth: 2, lineCap: .round, lineJoin: .round))
                if points.count == 1 {
                    Circle().fill(color).frame(width: 5, height: 5).position(first)
                }
            }
        }
    }

    private var accessibleSummary: String {
        guard !labels.isEmpty else { return "No chart data" }
        if let selection, labels.indices.contains(selection) { return summary(at: selection) }
        return "Chart, \(labels.count) data points"
    }

    private func summary(at index: Int) -> String {
        let entries = series.compactMap { item -> String? in
            guard item.values.indices.contains(index), item.values[index].isFinite else { return nil }
            let value = NumberFormatter.localizedString(from: NSNumber(value: item.values[index]), number: .decimal)
            return "\(item.id): \(value)"
        }
        return ([labels[index]] + entries).joined(separator: ", ")
    }
}
