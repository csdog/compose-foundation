import SwiftUI

public struct FoundationChart: View {
    public enum Variant: Sendable { case bar, line, area }

    public struct Axes {
        public let horizontalGridLineCount: Int
        public let preferredUpperBound: Double?
        public let xLabel: (String) -> String

        public init(
            horizontalGridLineCount: Int = 3,
            preferredUpperBound: Double? = nil,
            xLabel: @escaping (String) -> String = { $0 }
        ) {
            self.horizontalGridLineCount = min(max(horizontalGridLineCount, 0), 20)
            self.preferredUpperBound = preferredUpperBound.flatMap { $0.isFinite && $0 > 0 ? $0 : nil }
            self.xLabel = xLabel
        }
    }

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
    private let axes: Axes?
    private let showsLegend: Bool

    public init(
        labels: [String],
        series: [Series],
        variant: Variant = .bar,
        selection: Binding<Int?> = .constant(nil)
    ) {
        self.init(labels: labels, series: series, variant: variant, selection: selection, axes: nil)
    }

    public init(
        labels: [String],
        series: [Series],
        variant: Variant = .bar,
        selection: Binding<Int?> = .constant(nil),
        axes: Axes?
    ) {
        self.init(labels: labels, series: series, variant: variant, selection: selection,
                  axes: axes, showsLegend: false)
    }

    public init(
        labels: [String],
        series: [Series],
        variant: Variant = .bar,
        selection: Binding<Int?> = .constant(nil),
        axes: Axes? = nil,
        showsLegend: Bool
    ) {
        self.labels = labels
        self.series = series
        self.variant = variant
        self._selection = selection
        self.axes = axes
        self.showsLegend = showsLegend
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
                if let axes {
                    axisLabels(axes, palette: palette)
                } else {
                    HStack {
                        Text(labels[0])
                        Spacer(minLength: 0)
                        if labels.count > 1 { Text(labels[labels.count - 1]) }
                    }
                    .font(.caption)
                    .foregroundColor(palette.mutedForeground)
                    .accessibilityHidden(true)
                }
                if showsLegend {
                    legend(palette: palette)
                }
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

    private func legend(palette: FoundationPalette) -> some View {
        HStack(spacing: 16) {
            ForEach(series.indices, id: \.self) { index in
                HStack(spacing: 6) {
                    RoundedRectangle(cornerRadius: 2)
                        .fill(seriesColor(at: index, palette: palette))
                        .frame(width: 8, height: 8)
                        .fixedSize()
                    Text(series[index].id)
                        .font(.caption)
                        .foregroundColor(palette.foreground)
                        .lineLimit(1)
                }
            }
        }
        .frame(maxWidth: .infinity)
        .padding(.top, 8)
        .accessibilityHidden(true)
    }

    private func seriesColor(at index: Int, palette: FoundationPalette) -> Color {
        series[index].color ?? palette.primary.opacity(1 / (1 + Double(index) * 0.35))
    }

    private func axisLabels(_ axes: Axes, palette: FoundationPalette) -> some View {
        GeometryReader { geometry in
            let step = geometry.size.width / CGFloat(labels.count)
            let stride = max(1, Int(ceil(40 / max(step, 1))))
            let shown = labels.indices.filter { $0.isMultiple(of: stride) }
            ForEach(shown, id: \.self) { index in
                Text(axes.xLabel(labels[index]))
                    .font(.caption)
                    .foregroundColor(palette.mutedForeground)
                    .lineLimit(1)
                    .position(x: step * (CGFloat(index) + 0.5), y: geometry.size.height / 2)
            }
        }
        .frame(height: 20)
        .accessibilityHidden(true)
    }

    private func plot(size: CGSize, values: [Double], palette: FoundationPalette) -> some View {
        let preferredUpper = axes?.preferredUpperBound ?? 0
        let scale = max(1, values.map(abs).max() ?? 1, preferredUpper)
        let upper = max(preferredUpper, values.max() ?? 0) / scale
        let lower = min(0, (values.min() ?? 0) / scale)
        let span = upper == lower ? 1 : upper - lower
        let baseline = size.height * (upper / span)
        let step = size.width / CGFloat(labels.count)

        return ZStack {
            Path { path in
                let count = axes?.horizontalGridLineCount ?? 0
                let offsets = count > 1 ? (0..<count).map { size.height * CGFloat($0) / CGFloat(count - 1) }
                    : count == 1 ? [size.height] : []
                for y in offsets {
                    path.move(to: CGPoint(x: 0, y: y))
                    path.addLine(to: CGPoint(x: size.width, y: y))
                }
                if offsets.isEmpty || (upper > 0 && lower < 0) {
                    path.move(to: CGPoint(x: 0, y: baseline))
                    path.addLine(to: CGPoint(x: size.width, y: baseline))
                }
            }
            .stroke(palette.border, lineWidth: 1)
            ForEach(series.indices, id: \.self) { index in
                let points = series[index].values.prefix(labels.count).enumerated().compactMap { position, value in
                    value.isFinite
                        ? CGPoint(x: step * (CGFloat(position) + 0.5), y: size.height * (upper - value / scale) / span)
                        : nil
                }
                marks(points: points, index: index, step: step, baseline: baseline,
                      color: seriesColor(at: index, palette: palette))
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
                let groupWidth = step * (axes == nil ? 0.75 : 0.8)
                let width = groupWidth / CGFloat(series.count)
                let barWidth = axes == nil ? width * 0.9 : width - min(4, width * 0.2)
                Path { path in
                    for point in points {
                        let x = point.x - groupWidth / 2 + CGFloat(index) * width
                        let bar = CGRect(x: x, y: min(point.y, baseline), width: barWidth, height: abs(point.y - baseline))
                        if axes != nil {
                            let radius = min(4, bar.width / 2, bar.height / 2)
                            path.addRoundedRect(in: bar, cornerSize: CGSize(width: radius, height: radius))
                        } else {
                            path.addRect(bar)
                        }
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
        let chart = "Chart, \(labels.count) data points"
        return showsLegend ? ([chart] + series.map(\.id)).joined(separator: ", ") : chart
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
