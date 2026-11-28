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
    @State private var hoverLocation: CGPoint?

    private let labels: [String]
    private let series: [Series]
    private let variant: Variant
    private let axes: Axes?
    private let showsLegend: Bool
    private let showsTooltip: Bool

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
        self.init(labels: labels, series: series, variant: variant, selection: selection,
                  axes: axes, showsLegend: showsLegend, showsTooltip: false)
    }

    public init(
        labels: [String],
        series: [Series],
        variant: Variant = .bar,
        selection: Binding<Int?> = .constant(nil),
        axes: Axes? = nil,
        showsLegend: Bool = false,
        showsTooltip: Bool
    ) {
        self.labels = labels
        self.series = series
        self.variant = variant
        self._selection = selection
        self.axes = axes
        self.showsLegend = showsLegend
        self.showsTooltip = showsTooltip
    }

    public var body: some View {
        let palette = theme.palette(for: colorScheme)
        let values = series.flatMap { $0.values.prefix(labels.count) }.filter(\.isFinite)

        VStack(alignment: .leading, spacing: 6) {
            if labels.isEmpty || values.isEmpty {
                Text("No chart data").font(.subheadline).foregroundColor(palette.mutedForeground)
            } else {
                GeometryReader { geometry in
                    interactivePlot(size: geometry.size, values: values, palette: palette)
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
                if !showsTooltip, let selection, labels.indices.contains(selection) {
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

    private func interactivePlot(size: CGSize, values: [Double], palette: FoundationPalette) -> some View {
        let hoveredIndex = showsTooltip ? hoverLocation.flatMap { index(at: $0.x, width: size.width) } : nil
        let active = hoveredIndex ?? selection
        let content = plot(size: size, values: values, palette: palette, active: active)
            .contentShape(Rectangle())
            .gesture(DragGesture(minimumDistance: 0).onChanged { event in
                if let index = index(at: event.location.x, width: size.width) {
                    selection = index
                }
            })
            .overlay(Group {
                if showsTooltip, let active, labels.indices.contains(active), size.width > 24 {
                    tooltip(at: active, size: size, palette: palette)
                }
            }.allowsHitTesting(false), alignment: .topLeading)
            .accessibilityHidden(true)
        return trackHover(content)
    }

    @ViewBuilder
    private func trackHover<Content: View>(_ content: Content) -> some View {
        if #available(iOS 16, macOS 13, *), showsTooltip {
            content.onContinuousHover { phase in
                switch phase {
                case .active(let location):
                    hoverLocation = location
                case .ended:
                    hoverLocation = nil
                @unknown default:
                    hoverLocation = nil
                }
            }
        } else {
            content
        }
    }

    private func index(at x: CGFloat, width: CGFloat) -> Int? {
        guard width > 0, !labels.isEmpty, x.isFinite else { return nil }
        let position = min(max(x / width, 0), 1)
        return min(Int(position * CGFloat(labels.count)), labels.count - 1)
    }

    private func tooltip(at index: Int, size: CGSize, palette: FoundationPalette) -> some View {
        let width = min(128, size.width - 16)
        let center = size.width * (CGFloat(index) + 0.5) / CGFloat(labels.count)
        let leading = min(max(center + 12, 8), size.width - width - 8)
        let top = hoverLocation.map { $0.y + 12 } ?? size.height * 0.55
        let lastTop = max(8, size.height - 32 - CGFloat(series.count) * 20)
        let shape = RoundedRectangle(cornerRadius: 8)
        return VStack(alignment: .leading, spacing: 6) {
            Text(labels[index]).fontWeight(.medium).lineLimit(1)
            ForEach(series.indices, id: \.self) { item in
                if let value = formattedValue(series[item], at: index) {
                    HStack(spacing: 8) {
                        RoundedRectangle(cornerRadius: 2)
                            .fill(seriesColor(at: item, palette: palette))
                            .frame(width: 10, height: 10)
                        Text(series[item].id)
                            .foregroundColor(palette.mutedForeground)
                            .lineLimit(1)
                        Spacer(minLength: 0)
                        Text(value).font(.system(.caption, design: .monospaced).weight(.medium))
                    }
                }
            }
        }
        .font(.caption)
        .foregroundColor(palette.foreground)
        .padding(.horizontal, 10)
        .padding(.vertical, 6)
        .frame(width: width, alignment: .leading)
        .background(shape.fill(palette.background).shadow(color: .black.opacity(0.18), radius: 8, y: 4))
        .overlay(shape.strokeBorder(palette.border.opacity(0.5)))
        .padding(.leading, leading)
        .padding(.top, min(max(top, 8), lastTop))
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

    private func plot(size: CGSize, values: [Double], palette: FoundationPalette, active: Int?) -> some View {
        let preferredUpper = axes?.preferredUpperBound ?? 0
        let scale = max(1, values.map(abs).max() ?? 1, preferredUpper)
        let upper = max(preferredUpper, values.max() ?? 0) / scale
        let lower = min(0, (values.min() ?? 0) / scale)
        let span = upper == lower ? 1 : upper - lower
        let baseline = size.height * (upper / span)
        let step = size.width / CGFloat(labels.count)

        return ZStack {
            if showsTooltip, variant == .bar, let active, labels.indices.contains(active) {
                Rectangle()
                    .fill(palette.secondary.opacity(0.65))
                    .frame(width: step, height: size.height)
                    .position(x: step * (CGFloat(active) + 0.5), y: size.height / 2)
            }
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
            if let active, labels.indices.contains(active), !showsTooltip || variant != .bar {
                Path { path in
                    let x = step * (CGFloat(active) + 0.5)
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

    private func formattedValue(_ item: Series, at index: Int) -> String? {
        guard item.values.indices.contains(index), item.values[index].isFinite else { return nil }
        return NumberFormatter.localizedString(from: NSNumber(value: item.values[index]), number: .decimal)
    }

    private func summary(at index: Int) -> String {
        let entries = series.compactMap { item in
            formattedValue(item, at: index).map { "\(item.id): \($0)" }
        }
        return ([labels[index]] + entries).joined(separator: ", ")
    }
}
