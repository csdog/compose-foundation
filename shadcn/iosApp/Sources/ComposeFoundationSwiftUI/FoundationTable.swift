import SwiftUI

public struct FoundationTable<Row: Identifiable>: View {
    public struct Column: Identifiable {
        public let id: String
        public let title: String
        public let width: CGFloat
        public let alignment: Alignment
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
            self.id = id
            self.title = title
            self.width = width.isFinite ? max(width, 44) : 120
            self.alignment = alignment
            self.value = value
            self.areInIncreasingOrder = order
        }
    }

    @Environment(\.foundationTheme) private var theme
    @Environment(\.colorScheme) private var colorScheme
    @State private var sortColumn: String?
    @State private var ascending = true

    private let columns: [Column]
    private let rows: [Row]

    public init(columns: [Column], rows: [Row]) {
        self.columns = columns
        self.rows = rows
    }

    public var body: some View {
        let palette = theme.palette(for: colorScheme)
        let shape = RoundedRectangle(cornerRadius: theme.cornerRadius)

        ScrollView(.horizontal) {
            VStack(alignment: .leading, spacing: 0) {
                HStack(spacing: 0) {
                    ForEach(columns) { column in
                        header(column)
                            .font(.caption.weight(.semibold))
                            .frame(width: column.width, alignment: column.alignment)
                            .padding(.horizontal, 8)
                    }
                }
                .frame(minHeight: 38)
                .background(palette.secondary)
                if rows.isEmpty {
                    Text("No rows").font(.subheadline)
                        .padding(12)
                        .foregroundColor(palette.mutedForeground)
                } else {
                    LazyVStack(alignment: .leading, spacing: 0) {
                        ForEach(orderedRows) { row in
                            HStack(alignment: .top, spacing: 0) {
                                ForEach(columns) { column in
                                    Text(column.value(row))
                                        .font(.subheadline)
                                        .frame(width: column.width, alignment: column.alignment)
                                        .padding(.horizontal, 8)
                                }
                            }
                            .padding(.vertical, 8)
                            .accessibilityElement(children: .combine)
                            Rectangle().fill(palette.border).frame(height: 1).accessibilityHidden(true)
                        }
                    }
                }
            }
        }
        .foregroundColor(palette.foreground)
        .background(shape.fill(palette.card))
        .clipShape(shape)
        .overlay(shape.strokeBorder(palette.border).allowsHitTesting(false))
    }

    @ViewBuilder
    private func header(_ column: Column) -> some View {
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
                .contentShape(Rectangle())
            }
            .buttonStyle(PlainButtonStyle())
            .accessibilityValue(Text(sortColumn == column.id ? (ascending ? "Ascending" : "Descending") : ""))
            .accessibilityAddTraits(.isHeader)
        } else {
            Text(column.title).accessibilityAddTraits(.isHeader)
        }
    }

    private var orderedRows: [Row] {
        guard let order = columns.first(where: { $0.id == sortColumn })?.areInIncreasingOrder else {
            return rows
        }
        return rows.sorted { ascending ? order($0, $1) : order($1, $0) }
    }
}
