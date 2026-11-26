import SwiftUI

public struct FoundationDrawer<Panel: View>: ViewModifier {
    @Environment(\.foundationTheme) private var theme
    @Environment(\.colorScheme) private var colorScheme
    @Environment(\.accessibilityReduceMotion) private var reduceMotion
    @Binding private var isPresented: Bool
    @GestureState private var drag: CGFloat = 0

    private let title: String
    private let onDismiss: () -> Void
    private let panel: () -> Panel

    public init(isPresented: Binding<Bool>, title: String, @ViewBuilder panel: @escaping () -> Panel) {
        self.init(isPresented: isPresented, title: title, onDismiss: {}, panel: panel)
    }

    public init(
        isPresented: Binding<Bool>, title: String, onDismiss: @escaping () -> Void,
        @ViewBuilder panel: @escaping () -> Panel
    ) {
        self._isPresented = isPresented
        self.title = title
        self.onDismiss = onDismiss
        self.panel = panel
    }

    public func body(content: Content) -> some View {
        let palette = theme.palette(for: colorScheme)

        ZStack(alignment: .bottom) {
            content
                .disabled(isPresented)
                .allowsHitTesting(!isPresented)
                .accessibilityHidden(isPresented)
            if isPresented {
                Color.black.opacity(0.42)
                    .edgesIgnoringSafeArea(.all)
                    .onTapGesture { isPresented = false }
                    .accessibilityHidden(true)

                VStack(alignment: .leading, spacing: 12) {
                    Capsule()
                        .fill(palette.mutedForeground)
                        .frame(width: 32, height: 4)
                        .frame(maxWidth: .infinity, minHeight: 24)
                        .contentShape(Rectangle())
                        .gesture(
                            DragGesture(coordinateSpace: .global)
                                .updating($drag) { value, offset, _ in offset = max(0, value.translation.height) }
                                .onEnded { value in
                                    if value.predictedEndTranslation.height > 90 { isPresented = false }
                                }
                        )
                        .accessibilityHidden(true)
                    FoundationPresentationHeader(
                        title: title, description: nil, alignment: .center,
                        close: Button("Close") { isPresented = false },
                        dismiss: { isPresented = false }
                    )
                    panel()
                }
                .padding(16)
                .foregroundColor(palette.foreground)
                .background(RoundedRectangle(cornerRadius: theme.cornerRadius + 6).fill(palette.card))
                .offset(y: drag)
                .transition(.move(edge: .bottom))
                .accessibilityElement(children: .contain)
                .accessibilityAction(.escape) { isPresented = false }
                .onDisappear {
                    if !isPresented { onDismiss() }
                }
            }
        }
        .animation(reduceMotion ? nil : .easeInOut(duration: 0.2), value: isPresented)
    }
}
