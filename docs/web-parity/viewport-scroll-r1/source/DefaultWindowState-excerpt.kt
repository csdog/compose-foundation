internal class DefaultWindowState(private val viewportContainer: Element) : ComposeWindowState {
    private val channel = Channel<IntSize>(CONFLATED)

    override val globalEvents = EventTargetListener(window)

    override fun init() {

        globalEvents.addDisposableEvent("resize") {
            channel.trySend(getParentContainerBox())
        }

        initMediaEventListener {
            channel.trySend(getParentContainerBox())
        }

        channel.trySend(getParentContainerBox())
    }

    private fun getParentContainerBox(): IntSize {
        return IntSize(viewportContainer.clientWidth, viewportContainer.clientHeight)
    }

    private fun initMediaEventListener(handler: (Double) -> Unit) {
        val contentScale = actualDensity
        window.matchMedia("(resolution: ${contentScale}dppx)")
            .addEventListener("change", { evt ->
                evt as MediaQueryListEvent
                if (!evt.matches) {
                    handler(contentScale)
                }
                initMediaEventListener(handler)
            }, AddEventListenerOptions(capture = true, once = true))
    }

    override fun sizeFlow() = channel.receiveAsFlow()
}

