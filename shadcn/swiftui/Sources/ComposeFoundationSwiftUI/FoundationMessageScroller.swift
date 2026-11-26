import SwiftUI

public struct FoundationMessageScroller<Revision: Equatable, Content: View>: View {
    @Environment(\.foundationTheme) private var theme
    @Environment(\.colorScheme) private var colorScheme
    @Environment(\.accessibilityReduceMotion) private var reduceMotion
    @Binding private var followsLatest: Bool
    @Namespace private var tail

    private let revision: Revision
    private let onReachStart: (() -> Void)?
    private let content: Content

    public init(
        revision: Revision,
        followsLatest: Binding<Bool>,
        onReachStart: (() -> Void)? = nil,
        @ViewBuilder content: () -> Content
    ) {
        self.revision = revision
        self._followsLatest = followsLatest
        self.onReachStart = onReachStart
        self.content = content()
    }

    public var body: some View {
        let palette = theme.palette(for: colorScheme)

        ScrollViewReader { proxy in
            ZStack(alignment: .bottomTrailing) {
                ScrollView {
                    LazyVStack(alignment: .leading, spacing: 8) {
                        Color.clear.frame(height: 1).onAppear { onReachStart?() }.accessibilityHidden(true)
                        content
                        Color.clear.frame(height: 1).id(tail).accessibilityHidden(true)
                    }
                }
                .onAppear {
                    if followsLatest { proxy.scrollTo(tail, anchor: .bottom) }
                }
                .foundationOnChange(of: revision) { _ in
                    if followsLatest { proxy.scrollTo(tail, anchor: .bottom) }
                }
                .foundationOnChange(of: followsLatest) { shouldFollow in
                    if shouldFollow {
                        withAnimation(reduceMotion ? nil : .easeOut) { proxy.scrollTo(tail, anchor: .bottom) }
                    }
                }
                .simultaneousGesture(DragGesture(minimumDistance: 12).onChanged { drag in
                    if followsLatest && drag.translation.height > abs(drag.translation.width) {
                        followsLatest = false
                    }
                })
                if !followsLatest {
                    Button {
                        followsLatest = true
                    } label: {
                        Image(systemName: "arrow.down")
                            .frame(width: 44, height: 44)
                            .foregroundColor(palette.primaryForeground)
                            .background(Circle().fill(palette.primary))
                    }
                    .buttonStyle(PlainButtonStyle())
                    .accessibilityLabel(Text("Jump to latest message"))
                    .padding(8)
                }
            }
        }
    }
}
