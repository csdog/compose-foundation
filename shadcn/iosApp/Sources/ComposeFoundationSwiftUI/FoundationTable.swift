import SwiftUI

public struct FoundationTable<Row: Identifiable>: View {
    public struct Column: Identifiable {
        public let id: String
        public let title: String
        public let width: CGFloat
        public let alignment: Alignment
        public let weight: Font.Weight?
        public let value: (Row) -> String
        public let areInIncreasingOrder: ((Row, Row) -> Bool)?

        public init(
            id: String,
            title: String,
            width: CGFloat = 120,
            alignment: Alignment = .leading,
            order: ((Row, Row) -> Bool)? = nil,
            value: @escaping (Row) -> String
        ) {
            self.init(id: id, title: title, width: width, alignment: alignment, weight: nil, order: order, value: value)
        }

        public init(
            id: String,
            title: String,
            width: CGFloat = 120,
            alignment: Alignment = .leading,
            weight: Font.Weight?,
            order: ((Row, Row) -> Bool)? = nil,
            value: @escaping (Row) -> String
        ) {
            self.id = id
            self.title = title
            self.width = width.isFinite ? max(width, 44) : 120
            self.alignment = alignment
            self.weight = weight
            self.value = value
            self.areInIncreasingOrder = order
        }
    }

    @Environment(\.foundationTheme) private var theme
    @Environment(\.colorScheme) private var colorScheme
    @State private var sortColumn: String?
    @State private var ascending = true

    @ScaledMetric(relativeTo: .subheadline) private var novaFontSize: CGFloat = 14

    private let columns: [Column]
    private let rows: [Row]
    private let caption: String?
    private let footer: Row?

    public init(columns: [Column], rows: [Row]) {
        self.init(columns: columns, rows: rows, caption: nil)
    }

    public init(columns: [Column], rows: [Row], caption: String?, footer: Row? = nil) {
        self.columns = columns
        self.rows = rows
        self.caption = caption
        self.footer = footer
    }

    private var isNova: Bool { theme.preset == .nova }

    public var body: some View {
        let palette = theme.palette(for: colorScheme)
        let shape = RoundedRectangle(cornerRadius: isNova ? 0 : theme.cornerRadius)
        let headerHeight: CGFloat = isNova ? (columns.contains { $0.areInIncreasingOrder != nil } ? 44 : 40) : 38
        let sortedRows = orderedRows

        ScrollView(.horizontal) {
            VStack(alignment: .leading, spacing: 0) {
                HStack(spacing: 0) {
                    ForEach(columns) { column in
                        header(column, height: headerHeight)
                    }
                }
                .frame(minHeight: headerHeight)
                .background(isNova ? Color.clear : palette.secondary)
                if isNova { divider(palette) }

                if sortedRows.isEmpty {
                    Text("No rows").font(.subheadline)
                        .padding(12)
                        .foregroundColor(palette.mutedForeground)
                } else {
                    LazyVStack(alignment: .leading, spacing: 0) {
                        ForEach(sortedRows) { row in
                            cells(for: row)
                            if !isNova || row.id != sortedRows.last?.id { divider(palette) }
                        }
                    }
                }

                if let footer {
                    if isNova || sortedRows.isEmpty { divider(palette) }
                    cells(for: footer, weight: .medium)
                        .background(palette.secondary.opacity(0.5))
                }

                if let caption {
                    Text(caption)
                        .font(isNova ? .system(size: novaFontSize) : .subheadline)
                        .foregroundColor(palette.mutedForeground)
                        .multilineTextAlignment(.center)
                        .frame(maxWidth: .infinity)
                        .padding(.horizontal, 8)
                        .padding(.top, 16)
                }
            }
        }
        .foregroundColor(palette.foreground)
        .background(shape.fill(isNova ? Color.clear : palette.card))
        .clipShape(shape)
        .overlay(shape.strokeBorder(isNova ? Color.clear : palette.border).allowsHitTesting(false))
    }

    private func divider(_ palette: FoundationPalette) -> some View {
        Rectangle().fill(palette.border).frame(height: 1).accessibilityHidden(true)
    }

    private func cells(for row: Row, weight: Font.Weight = .regular) -> some View {
        HStack(alignment: isNova ? .center : .top, spacing: 0) {
            ForEach(columns) { column in
                Text(column.value(row))
                    .font(isNova
                          ? .system(size: novaFontSize, weight: column.weight ?? weight)
                          : .subheadline.weight(column.weight ?? weight))
                    .lineLimit(isNova ? 1 : nil)
                    .frame(width: column.width, alignment: column.alignment)
                    .padding(.horizontal, 8)
            }
        }
        .padding(.vertical, 8)
        .frame(minHeight: isNova ? 36 : nil)
        .accessibilityElement(children: .combine)
    }

    @ViewBuilder
    private func header(_ column: Column, height: CGFloat) -> some View {
        let font: Font = isNova ? .system(size: novaFontSize, weight: .medium) : .caption.weight(.semibold)
        if column.areInIncreasingOrder != nil {
            Button {
                ascending = sortColumn == column.id ? !ascending : true
                sortColumn = column.id
            } label: {
                HStack(spacing: 4) {
                    Text(column.title)
                    if sortColumn == column.id {
                        Image(systemName: ascending ? "arrow.up" : "arrow.down").accessibilityHidden(true)
                    }
                }
                .font(font)
                .frame(width: column.width, alignment: column.alignment)
                .padding(.horizontal, 8)
                .frame(minHeight: isNova ? height : nil)
                .contentShape(Rectangle())
            }
            .buttonStyle(PlainButtonStyle())
            .accessibilityValue(Text(sortColumn == column.id ? (ascending ? "Ascending" : "Descending") : ""))
            .accessibilityAddTraits(.isHeader)
        } else {
            Text(column.title)
                .font(font)
                .frame(width: column.width, alignment: column.alignment)
                .padding(.horizontal, 8)
                .accessibilityAddTraits(.isHeader)
        }
    }

    private var orderedRows: [Row] {
        guard let order = columns.first(where: { $0.id == sortColumn })?.areInIncreasingOrder else {
            return rows
        }
        return rows.sorted { ascending ? order($0, $1) : order($1, $0) }
    }
}
