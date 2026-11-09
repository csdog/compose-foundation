package com.toutiaoyan.compose.ui.catalog.docs

/**
 * Exported code for the Trace page, kept next to the previews so Copy Page and
 * the code panel stay in sync with what the recipe renders.
 */
internal val traceUsageImports =
    """
    import com.toutiaoyan.compose.ui.components.trace.*
    import com.toutiaoyan.compose.ui.components.codeblock.*
    import com.toutiaoyan.compose.ui.components.icon.Icon
    import com.composables.icons.lucide.*
    """.trimIndent()

internal val traceUsageCode =
    """
    Trace {
        TraceLine {
            TraceIcon { Icon(Lucide.Search) }
            TraceTitle("Searched for minimumReleaseAge")
        }
    }

    Trace {
        TraceCollapsible {
            TraceCollapsibleTrigger {
                TraceLine {
                    TraceIcon { Icon(Lucide.Terminal) }
                    TraceTitle("Ran node --version")
                }
            }
            TraceCollapsibleContent {
                CodeBlock(size = CodeBlockSize.Sm) {
                    CodeBlockContent("${'$'} node --version\nv24.18.0")
                }
            }
        }
    }
    """.trimIndent()

internal val traceCompositionCode =
    """
    Trace
    └── TraceLine
        ├── TraceIcon
        ├── TraceTitle
        ├── TraceDescription
        └── TraceIcon

    Trace
    └── TraceCollapsible
        ├── TraceCollapsibleTrigger
        │   └── TraceLine
        └── TraceCollapsibleContent
    """.trimIndent()

internal val traceDemoCode =
    """
    import androidx.compose.animation.core.*
    import androidx.compose.foundation.layout.*
    import androidx.compose.runtime.*
    import androidx.compose.ui.Modifier
    import androidx.compose.ui.graphics.graphicsLayer
    import androidx.compose.ui.graphics.vector.ImageVector
    import androidx.compose.ui.unit.dp
    import com.composables.icons.lucide.*
    import com.toutiaoyan.compose.ui.components.bubble.*
    import com.toutiaoyan.compose.ui.components.button.*
    import com.toutiaoyan.compose.ui.components.codeblock.*
    import com.toutiaoyan.compose.ui.components.icon.Icon
    import com.toutiaoyan.compose.ui.components.message.MessageAlign
    import com.toutiaoyan.compose.ui.components.text.Text
    import com.toutiaoyan.compose.ui.components.trace.*
    import kotlinx.coroutines.delay

    private const val demoQuestion = "Why does pnpm install fail in CI?"

    private const val thoughtText =
        "CI installs with a frozen lockfile, so the install stops when package.json and " +
            "pnpm-lock.yaml disagree. I should check what the last commit changed, then " +
            "reproduce the install."

    private const val demoAnswer =
        "The last commit bumped a version in package.json without updating pnpm-lock.yaml, " +
            "so the frozen install fails. Run pnpm install locally and commit the lockfile."

    private const val lockfileError =
        "ERR_PNPM_OUTDATED_LOCKFILE  Cannot install with \"frozen-lockfile\" because " +
            "pnpm-lock.yaml is not up to date with package.json"

    private val demoSources =
        listOf(
            "pnpm install | pnpm.io" to "https://pnpm.io/cli/install#--frozen-lockfile",
            "Continuous Integration | pnpm.io" to "https://pnpm.io/continuous-integration",
        )

    @Composable
    private fun TraceOutput(
        text: String,
        destructive: Boolean = false,
    ) {
        CodeBlock(
            size = CodeBlockSize.Sm,
            variant = if (destructive) CodeBlockVariant.Destructive else CodeBlockVariant.Default,
        ) {
            CodeBlockContent(text, wrap = true)
        }
    }

    @Composable
    private fun TraceOutputRow(
        title: String,
        output: String,
        defaultOpen: Boolean = false,
        open: Boolean? = null,
        onOpenChange: ((Boolean) -> Unit)? = null,
        destructive: Boolean = false,
        icon: ImageVector = Lucide.Terminal,
    ) {
        TraceCollapsible(defaultOpen = defaultOpen, open = open, onOpenChange = onOpenChange) {
            TraceCollapsibleTrigger(status = if (destructive) TraceStatus.Error else null) {
                TraceLine(status = if (destructive) TraceStatus.Error else null) {
                    TraceIcon { Icon(icon) }
                    TraceTitle(title)
                }
            }
            TraceCollapsibleContent { TraceOutput(output, destructive = destructive) }
        }
    }

    @Composable
    private fun TraceSourceRow(
        title: String,
        href: String,
        icon: ImageVector = Lucide.Globe,
    ) {
        TraceLink(
            href = href,
            contentDescription = "${'$'}title (opens in a new tab)",
        ) {
            TraceLine {
                TraceIcon { Icon(icon) }
                TraceTitle(title)
                TraceIcon { Icon(Lucide.ArrowUpRight) }
            }
        }
    }

    @Composable
    private fun TraceSpinIcon() {
        val transition = rememberInfiniteTransition(label = "trace-spin")
        val rotation by
            transition.animateFloat(
                initialValue = 0f,
                targetValue = 360f,
                animationSpec =
                    infiniteRepeatable(
                        tween(durationMillis = 1000, easing = LinearEasing),
                        RepeatMode.Restart,
                    ),
                label = "trace-spin-rotation",
            )
        Icon(Lucide.LoaderCircle, modifier = Modifier.graphicsLayer { rotationZ = rotation })
    }

    /** Local transport: cancelled automatically when its recipe leaves composition. */
    private suspend fun streamTraceText(
        text: String,
        delayMillis: Long,
        update: (String) -> Unit,
    ) {
        val words = text.split(" ")
        words.indices.forEach { index ->
            delay(delayMillis)
            update(words.take(index + 1).joinToString(" "))
        }
    }

    @Composable
    private fun TraceThinkingRow(
        text: String,
        running: Boolean,
        completedTitle: String = "Thought",
        completedIcon: ImageVector = Lucide.Brain,
    ) {
        var open by remember { mutableStateOf<Boolean?>(null) }
        val line: @Composable () -> Unit = {
            TraceLine(status = if (running) TraceStatus.Running else null) {
                TraceIcon { if (running) TraceSpinIcon() else Icon(completedIcon) }
                TraceTitle(if (running) "Thinking" else completedTitle)
            }
        }
        if (text.isEmpty()) {
            line()
        } else {
            TraceCollapsible(open = open ?: running, onOpenChange = { open = it }) {
                TraceCollapsibleTrigger { line() }
                TraceCollapsibleContent { Text { text } }
            }
        }
    }

    @Composable
    private fun TraceCommandRow(
        command: String,
        output: String,
        running: Boolean,
        failed: Boolean = false,
        defaultOpen: Boolean = false,
        completedIcon: ImageVector = Lucide.Terminal,
    ) {
        if (running) {
            TraceLine(status = TraceStatus.Running) {
                TraceIcon { TraceSpinIcon() }
                TraceTitle("Running ${'$'}command")
            }
        } else {
            TraceOutputRow(
                "${'$'}{if (failed) "Failed to run" else "Ran"} ${'$'}command",
                output,
                defaultOpen = defaultOpen,
                destructive = failed,
                icon = completedIcon,
            )
        }
    }

    /** The initial transcript is complete; Replay runs the same local streaming sequence. */
    @Composable
    internal fun TraceDemoPreview() {
        var replayToken by remember { mutableStateOf(0) }
        var stage by remember { mutableStateOf(8) }
        var reasoning by remember { mutableStateOf(thoughtText) }
        var answer by remember { mutableStateOf(demoAnswer) }
        LaunchedEffect(replayToken) {
            if (replayToken == 0) return@LaunchedEffect
            stage = 0
            reasoning = ""
            answer = ""
            delay(200)
            streamTraceText(thoughtText, 40) { reasoning = it }
            stage = 1
            delay(700)
            stage = 2
            delay(900)
            stage = 3
            delay(1200)
            stage = 4
            delay(1000)
            stage = 5
            streamTraceText(demoAnswer, 20) { answer = it }
            stage = 8
        }
        Column(
            Modifier.widthIn(max = 384.dp).fillMaxWidth().padding(vertical = 24.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp),
        ) {
            Bubble(demoQuestion, variant = BubbleVariant.Muted, align = MessageAlign.End)
            key(replayToken) {
                Trace(Modifier.fillMaxWidth()) {
                    TraceThinkingRow(reasoning, stage == 0, "Thought for 5s", completedIcon = Lucide.CircleCheck)
                    if (stage >= 1) {
                        TraceCommandRow(
                            "pnpm --version",
                            "10.18.0",
                            running = stage == 1,
                            completedIcon = Lucide.CircleCheck,
                        )
                    }
                    if (stage >= 2) {
                        TraceCommandRow(
                            "git diff --stat HEAD~1 -- package.json pnpm-lock.yaml",
                            " package.json | 2 +-\n 1 file changed, 1 insertion(+), 1 deletion(-)",
                            running = stage == 2,
                            defaultOpen = true,
                            completedIcon = Lucide.CircleCheck,
                        )
                    }
                    if (stage >= 3) {
                        TraceCommandRow(
                            "pnpm install --frozen-lockfile",
                            lockfileError,
                            running = stage == 3,
                            failed = true,
                            completedIcon = Lucide.CircleAlert,
                        )
                    }
                    if (stage >= 4) {
                        TraceCollapsible {
                            TraceCollapsibleTrigger {
                                TraceLine(status = if (stage == 4) TraceStatus.Running else null) {
                                    TraceIcon { Icon(Lucide.Globe) }
                                    TraceTitle("${'$'}{if (stage == 4) "Using" else "Used"} ${'$'}{demoSources.size} sources")
                                }
                            }
                            TraceCollapsibleContent {
                                Trace { demoSources.forEach { (title, href) -> TraceSourceRow(title, href) } }
                            }
                        }
                    }
                }
            }
            if (answer.isNotEmpty()) Text { answer }
            if (stage == 8) {
                Button(
                    text = "Replay",
                    onClick = { replayToken += 1 },
                    variant = ButtonVariant.Outline,
                    size = ButtonSize.Sm,
                )
            }
        }
    }
    """.trimIndent()

internal val traceStaticCode =
    """
    Trace(Modifier.widthIn(max = 384.dp)) {
        TraceLine {
            TraceIcon { Icon(Lucide.GitBranch) }
            TraceTitle("Created branch fix/frozen-lockfile")
        }
    }
    """.trimIndent()

internal val traceStatusCode =
    """
    val spin by rememberInfiniteTransition(label = "spin").animateFloat(
        initialValue = 0f,
        targetValue = 360f,
        animationSpec = infiniteRepeatable(tween(1000, easing = LinearEasing)),
        label = "spin-rotation",
    )

    Trace(Modifier.widthIn(max = 384.dp)) {
        TraceLine {
            TraceIcon { Icon(Lucide.CircleCheck) }
            TraceTitle("Ran pnpm install")
        }
        TraceLine(status = TraceStatus.Error) {
            TraceIcon { Icon(Lucide.CircleAlert) }
            TraceTitle("Failed to run pnpm lint")
        }
        TraceLine(status = TraceStatus.Running) {
            TraceIcon {
                Icon(Lucide.LoaderCircle, modifier = Modifier.graphicsLayer { rotationZ = spin })
            }
            TraceTitle("Running pnpm build")
        }
        TraceLine(status = TraceStatus.Pending) {
            TraceIcon { Icon(Lucide.Circle) }
            TraceTitle("Run pnpm test")
        }
    }
    """.trimIndent()

internal val traceDescriptionCode =
    """
    Trace(Modifier.widthIn(max = 384.dp)) {
        TraceLine {
            TraceIcon { Icon(Lucide.FilePen) }
            TraceTitle("Edited components/settings/notification-preferences-form.tsx")
            TraceDescription("+48 −12", mono = true)
        }
        TraceLine {
            TraceIcon { Icon(Lucide.Terminal) }
            TraceTitle("Ran pnpm test")
            TraceDescription("14s")
        }
        TraceLine {
            TraceIcon { Icon(Lucide.Search) }
            TraceTitle("Searched for useChat")
            TraceDescription("8 results")
        }
    }
    """.trimIndent()

internal val traceCollapsibleCode =
    """
    Column(Modifier.widthIn(max = 384.dp)) {
        TraceCollapsible {
            TraceCollapsibleTrigger {
                TraceLine {
                    TraceIcon { Icon(Lucide.Terminal) }
                    TraceTitle("Ran node --version")
                }
            }
            TraceCollapsibleContent {
                CodeBlock(size = CodeBlockSize.Sm) {
                    CodeBlockContent("${'$'} node --version\nv24.18.0", wrap = true)
                }
            }
        }
    }
    """.trimIndent()

internal val traceOpenCode =
    """
    var open by remember { mutableStateOf(false) }
    Column(
        Modifier.widthIn(max = 384.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp),
    ) {
        TraceCollapsible(defaultOpen = true) {
            TraceCollapsibleTrigger {
                TraceLine {
                    TraceIcon { Icon(Lucide.Terminal) }
                    TraceTitle("Ran node --version")
                }
            }
            TraceCollapsibleContent {
                CodeBlock(size = CodeBlockSize.Sm) {
                    CodeBlockContent("${'$'} node --version\nv24.18.0", wrap = true)
                }
            }
        }
        TraceCollapsible(open = open, onOpenChange = { open = it }) {
            TraceCollapsibleTrigger {
                TraceLine {
                    TraceIcon { Icon(Lucide.Terminal) }
                    TraceTitle("Ran pnpm --version")
                }
            }
            TraceCollapsibleContent {
                CodeBlock(size = CodeBlockSize.Sm) {
                    CodeBlockContent("${'$'} pnpm --version\n10.33.4", wrap = true)
                }
            }
        }
        Button(
            text = if (open) "Hide the pnpm output" else "Show the pnpm output",
            onClick = { open = !open },
            variant = ButtonVariant.Outline,
            size = ButtonSize.Sm,
        )
    }
    """.trimIndent()

internal val traceLinkCode =
    """
    Trace(Modifier.widthIn(max = 384.dp)) {
        TraceLink(href = "#link") {
            TraceLine {
                TraceIcon { Icon(Lucide.FileText) }
                TraceTitle("Read apps/v4/lib/docs.ts")
                TraceIcon { Icon(Lucide.ChevronRight) }
            }
        }
        TraceLink(
            href = "https://docs.astro.build/en/install-and-setup/",
            contentDescription = "Install Astro | docs.astro.build (opens in a new tab)",
        ) {
            TraceLine {
                TraceIcon { Icon(Lucide.Globe) }
                TraceTitle("Install Astro | docs.astro.build")
                TraceIcon { Icon(Lucide.ArrowUpRight) }
            }
        }
    }
    """.trimIndent()

internal val traceDialogCode =
    """
    import androidx.compose.foundation.hoverable
    import androidx.compose.foundation.interaction.MutableInteractionSource
    import androidx.compose.foundation.interaction.collectIsHoveredAsState
    import androidx.compose.runtime.*
    import com.toutiaoyan.compose.ui.theme.LocalContentColor
    import com.toutiaoyan.compose.ui.theme.currentTheme
    Trace(Modifier.widthIn(max = 384.dp)) {
        Dialog {
            DialogTrigger {
                TraceTriggerLine {
                    TraceIcon { Icon(Lucide.Globe) }
                    TraceTitle("Searched the web for cache keys")
                    TraceIcon { Icon(Lucide.ChevronRight) }
                }
            }
            DialogContent {
                DialogHeader(
                    "Searched the web for cache keys",
                    description = "Three results were used in the response.",
                )
                Trace(Modifier.fillMaxWidth().padding(top = 16.dp)) {
                    TraceLink(href = "https://nextjs.org/docs/app/guides/caching") {
                        TraceLine {
                            TraceIcon { Icon(Lucide.Globe) }
                            TraceTitle("Caching build outputs")
                            TraceIcon { Icon(Lucide.ArrowUpRight) }
                        }
                    }
                    TraceLink(href = "https://nextjs.org/docs/app/api-reference/components/image") {
                        TraceLine {
                            TraceIcon { Icon(Lucide.Globe) }
                            TraceTitle("Image optimization reference")
                            TraceIcon { Icon(Lucide.ArrowUpRight) }
                        }
                    }
                    TraceLink(href = "https://vercel.com/docs/deploy-hooks") {
                        TraceLine {
                            TraceIcon { Icon(Lucide.Globe) }
                            TraceTitle("Deploy hooks and cache keys")
                            TraceIcon { Icon(Lucide.ArrowUpRight) }
                        }
                    }
                }
            }
        }
    }
    /** Mirrors the source's button:hover TraceLine treatment for compound triggers. */
    @Composable
    private fun TraceTriggerLine(
        modifier: Modifier = Modifier,
        interactionSource: MutableInteractionSource = remember { MutableInteractionSource() },
        content: @Composable TraceLineScope.() -> Unit,
    ) {
        val hovered by interactionSource.collectIsHoveredAsState()
        val colors = currentTheme.colors
        CompositionLocalProvider(LocalContentColor provides if (hovered) colors.foreground else colors.mutedForeground) {
            TraceLine(modifier.hoverable(interactionSource), content = content)
        }
    }

    """.trimIndent()

internal val traceSheetCode =
    """
    import androidx.compose.foundation.hoverable
    import androidx.compose.foundation.interaction.MutableInteractionSource
    import androidx.compose.foundation.interaction.collectIsHoveredAsState
    import androidx.compose.runtime.*
    import com.toutiaoyan.compose.ui.theme.LocalContentColor
    import com.toutiaoyan.compose.ui.theme.currentTheme
    val files = listOf(
        "apps/v4/lib/docs.ts",
        "apps/v4/lib/components.ts",
        "apps/v4/next.config.mjs",
        "apps/v4/package.json",
    )

    Trace(Modifier.widthIn(max = 384.dp)) {
        Sheet {
            SheetTrigger {
                TraceTriggerLine {
                    TraceIcon { Icon(Lucide.FolderSearch) }
                    TraceTitle("Explored 4 files")
                    TraceIcon { Icon(Lucide.ChevronRight) }
                }
            }
            SheetContent {
                SheetHeader(
                    "Explored 4 files",
                    description = "Files read while preparing the response.",
                )
                Column(
                    Modifier.padding(horizontal = 24.dp, vertical = 16.dp),
                    verticalArrangement = Arrangement.spacedBy(8.dp),
                ) {
                    for (path in files) {
                        Text(
                            textStyle = TextStyle(fontFamily = FontFamily.Monospace, fontSize = 12.sp),
                        ) { path }
                    }
                }
            }
        }
    }
    /** Mirrors the source's button:hover TraceLine treatment for compound triggers. */
    @Composable
    private fun TraceTriggerLine(
        modifier: Modifier = Modifier,
        interactionSource: MutableInteractionSource = remember { MutableInteractionSource() },
        content: @Composable TraceLineScope.() -> Unit,
    ) {
        val hovered by interactionSource.collectIsHoveredAsState()
        val colors = currentTheme.colors
        CompositionLocalProvider(LocalContentColor provides if (hovered) colors.foreground else colors.mutedForeground) {
            TraceLine(modifier.hoverable(interactionSource), content = content)
        }
    }

    """.trimIndent()

internal val traceGroupCode =
    """
    Trace(Modifier.widthIn(max = 384.dp)) {
        TraceLine {
            TraceIcon { Icon(Lucide.Search) }
            TraceTitle("Searched for minimumReleaseAge")
        }
        TraceLine {
            TraceIcon { Icon(Lucide.FileText) }
            TraceTitle("Read pnpm-workspace.yaml")
        }
        TraceLine {
            TraceIcon { Icon(Lucide.Terminal) }
            TraceTitle("Ran pnpm install")
        }
    }
    """.trimIndent()

internal val traceSourcesCode =
    """
    val sources = listOf(
        "Install Astro | docs.astro.build" to "https://docs.astro.build/en/install-and-setup/",
        "create-astro | npmjs.com" to "https://www.npmjs.com/package/create-astro",
        "Settings: minimumReleaseAge | pnpm.io" to "https://pnpm.io/settings#minimumreleaseage",
    )

    Column(Modifier.widthIn(max = 384.dp)) {
        TraceCollapsible(defaultOpen = true) {
            TraceCollapsibleTrigger {
                TraceLine {
                    TraceIcon { Icon(Lucide.Globe) }
                    TraceTitle("Used ${'$'}{sources.size} sources")
                }
            }
            TraceCollapsibleContent {
                Trace {
                    for ((title, href) in sources) {
                        TraceLink(
                            href = href,
                            contentDescription = "${'$'}title (opens in a new tab)",
                        ) {
                            TraceLine {
                                TraceIcon { Icon(Lucide.Globe) }
                                TraceTitle(title)
                                TraceIcon { Icon(Lucide.ArrowUpRight) }
                            }
                        }
                    }
                }
            }
        }
    }
    """.trimIndent()

internal val traceChainCode =
    """
    Column(Modifier.widthIn(max = 384.dp)) {
        TraceCollapsible(defaultOpen = true) {
            TraceCollapsibleTrigger {
                TraceLine(status = TraceStatus.Running) {
                    TraceTitle("Looking into the slow deploy")
                }
            }
            TraceCollapsibleContent(keepMounted = true) {
                Trace {
                    TraceCollapsible {
                        TraceCollapsibleTrigger {
                            TraceLine {
                                TraceIcon { Icon(Lucide.Search) }
                                TraceTitle("Searched for the checkout build log")
                            }
                        }
                        TraceCollapsibleContent {
                            Row(horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                                Badge("vercel.com", variant = BadgeVariant.Secondary)
                                Badge("github.com", variant = BadgeVariant.Secondary)
                            }
                        }
                    }
                    TraceLink(href = "#steps") {
                        TraceLine {
                            TraceIcon { Icon(Lucide.FileText) }
                            TraceTitle("Read the last twelve deploys")
                            TraceIcon { Icon(Lucide.ChevronRight) }
                        }
                    }
                    TraceCollapsible {
                        TraceCollapsibleTrigger {
                            TraceLine(status = TraceStatus.Running) {
                                TraceIcon { Icon(Lucide.Search) }
                                TraceTitle("Searching for the cache key")
                            }
                        }
                        TraceCollapsibleContent {
                            CodeBlock(size = CodeBlockSize.Sm) {
                                CodeBlockContent("cache key image optimization", wrap = true)
                            }
                        }
                    }
                }
            }
        }
    }
    """.trimIndent()

internal val traceCommitCode =
    """
    import androidx.compose.foundation.hoverable
    import androidx.compose.foundation.interaction.MutableInteractionSource
    import androidx.compose.foundation.interaction.collectIsHoveredAsState
    import androidx.compose.runtime.*
    import com.toutiaoyan.compose.ui.theme.LocalContentColor
    import com.toutiaoyan.compose.ui.theme.currentTheme
    val files = listOf(
        ChangedFile("app/login/page.tsx", added = false, additions = 2, deletions = 2, diff = "…"),
        ChangedFile("lib/auth/redirect.ts", added = true, additions = 7, deletions = 0, diff = "…"),
    )

    Trace(Modifier.widthIn(max = 384.dp)) {
        TraceCollapsible(defaultOpen = true) {
            TraceCollapsibleTrigger {
                TraceLine {
                    TraceIcon { Icon(Lucide.GitCommitHorizontal) }
                    TraceTitle("Committed a1b2c3: Fix the login redirect")
                }
            }
            TraceCollapsibleContent {
                Trace {
                    for (file in files) {
                        Sheet {
                            SheetTrigger {
                                TraceTriggerLine {
                                    TraceIcon { Icon(if (file.added) Lucide.FilePlus else Lucide.FilePen) }
                                    TraceTitle("${'$'}{if (file.added) "Added" else "Edited"} ${'$'}{file.path}")
                                    TraceDescription("+${'$'}{file.additions} −${'$'}{file.deletions}", mono = true)
                                    TraceIcon { Icon(Lucide.ChevronRight) }
                                }
                            }
                            SheetContent {
                                SheetHeader {
                                    Text(
                                        modifier = Modifier.semantics { heading() },
                                        textStyle = TextStyle(
                                            fontFamily = FontFamily.Monospace,
                                            color = currentTheme.colors.foreground,
                                        ),
                                    ) { file.path }
                                    SheetDescription(
                                        "${'$'}{file.additions} additions, ${'$'}{file.deletions} deletions",
                                    )
                                }
                                TraceDiff(file.diff)
                            }
                        }
                    }
                }
            }
        }
    }

    @Composable
    private fun TraceDiff(diff: String) {
        CodeBlock {
            CodeBlockContent(wrap = true) {
                diff.lineSequence().forEach { line ->
                    val variant = when {
                        line.startsWith("+") -> CodeBlockLineVariant.Added
                        line.startsWith("-") -> CodeBlockLineVariant.Removed
                        else -> CodeBlockLineVariant.Default
                    }
                    CodeBlockLine(
                        text = if (variant == CodeBlockLineVariant.Default) line else line.drop(1),
                        variant = variant,
                    )
                }
            }
        }
    }
    /** Mirrors the source's button:hover TraceLine treatment for compound triggers. */
    @Composable
    private fun TraceTriggerLine(
        modifier: Modifier = Modifier,
        interactionSource: MutableInteractionSource = remember { MutableInteractionSource() },
        content: @Composable TraceLineScope.() -> Unit,
    ) {
        val hovered by interactionSource.collectIsHoveredAsState()
        val colors = currentTheme.colors
        CompositionLocalProvider(LocalContentColor provides if (hovered) colors.foreground else colors.mutedForeground) {
            TraceLine(modifier.hoverable(interactionSource), content = content)
        }
    }

    """.trimIndent()

internal val traceTurnCode =
    """
    Column(Modifier.widthIn(max = 384.dp)) {
        TraceCollapsible(defaultOpen = true) {
            TraceCollapsibleTrigger(status = TraceStatus.Error) {
                TraceLine(status = TraceStatus.Error) {
                    TraceTitle("Worked for 2m 4s, 1 command failed")
                }
            }
            TraceCollapsibleContent(keepMounted = true) {
                Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                    Trace(Modifier.fillMaxWidth()) {
                        TraceCollapsible {
                            TraceCollapsibleTrigger {
                                TraceLine {
                                    TraceIcon { Icon(Lucide.Brain) }
                                    TraceTitle("Thought")
                                    TraceDescription("12s")
                                }
                            }
                            TraceCollapsibleContent {
                                Text { "I should check which package managers are installed " +
                                    "before I scaffold anything." }
                            }
                        }
                        TraceCollapsible(defaultOpen = true) {
                            TraceCollapsibleTrigger(status = TraceStatus.Error) {
                                TraceLine(status = TraceStatus.Error) {
                                    TraceIcon { Icon(Lucide.Terminal) }
                                    TraceTitle("Ran 2 commands, 1 failed")
                                }
                            }
                            TraceCollapsibleContent(keepMounted = true) {
                                Trace(Modifier.fillMaxWidth()) {
                                    TraceLine {
                                        TraceIcon { Icon(Lucide.Terminal) }
                                        TraceTitle("Ran node --version")
                                    }
                                    TraceCollapsible {
                                        TraceCollapsibleTrigger(status = TraceStatus.Error) {
                                            TraceLine(status = TraceStatus.Error) {
                                                TraceIcon { Icon(Lucide.Terminal) }
                                                TraceTitle("Failed to run pnpm --version")
                                            }
                                        }
                                        TraceCollapsibleContent {
                                            CodeBlock(
                                                size = CodeBlockSize.Sm,
                                                variant = CodeBlockVariant.Destructive,
                                            ) {
                                                CodeBlockContent("command not found: pnpm", wrap = true)
                                            }
                                        }
                                    }
                                }
                            }
                        }
                    }
                    Text { "Node is installed and pnpm is not. Reading the docs config." }
                    Trace(Modifier.fillMaxWidth()) {
                        TraceLink(href = "#turn") {
                            TraceLine {
                                TraceIcon { Icon(Lucide.FileText) }
                                TraceTitle("Read apps/v4/lib/docs.ts")
                                TraceIcon { Icon(Lucide.ChevronRight) }
                            }
                        }
                    }
                }
            }
        }
    }
    """.trimIndent()

internal val traceReasoningCode =
    """
    import androidx.compose.animation.core.*
    import androidx.compose.foundation.layout.*
    import androidx.compose.runtime.*
    import androidx.compose.ui.Modifier
    import androidx.compose.ui.graphics.graphicsLayer
    import androidx.compose.ui.graphics.vector.ImageVector
    import androidx.compose.ui.unit.dp
    import com.composables.icons.lucide.*
    import com.toutiaoyan.compose.ui.components.bubble.*
    import com.toutiaoyan.compose.ui.components.button.*
    import com.toutiaoyan.compose.ui.components.codeblock.*
    import com.toutiaoyan.compose.ui.components.icon.Icon
    import com.toutiaoyan.compose.ui.components.message.MessageAlign
    import com.toutiaoyan.compose.ui.components.text.Text
    import com.toutiaoyan.compose.ui.components.trace.*
    import kotlinx.coroutines.delay

    /** Centered `max-w-sm` column, matching the official preview's `max-w-sm` wrapper. */
    @Composable
    private fun TraceColumn(
        minHeight: androidx.compose.ui.unit.Dp = 0.dp,
        content: @Composable androidx.compose.foundation.layout.ColumnScope.() -> Unit,
    ) {
        val base = Modifier.widthIn(max = 384.dp).fillMaxWidth()
        Column(
            modifier = base.heightIn(min = minHeight),
            verticalArrangement = Arrangement.spacedBy(16.dp),
            content = content,
        )
    }

    @Composable
    private fun TraceSpinIcon() {
        val transition = rememberInfiniteTransition(label = "trace-spin")
        val rotation by
            transition.animateFloat(
                initialValue = 0f,
                targetValue = 360f,
                animationSpec =
                    infiniteRepeatable(
                        tween(durationMillis = 1000, easing = LinearEasing),
                        RepeatMode.Restart,
                    ),
                label = "trace-spin-rotation",
            )
        Icon(Lucide.LoaderCircle, modifier = Modifier.graphicsLayer { rotationZ = rotation })
    }

    /** Local transport: cancelled automatically when its recipe leaves composition. */
    private suspend fun streamTraceText(
        text: String,
        delayMillis: Long,
        update: (String) -> Unit,
    ) {
        val words = text.split(" ")
        words.indices.forEach { index ->
            delay(delayMillis)
            update(words.take(index + 1).joinToString(" "))
        }
    }

    @Composable
    private fun TraceThinkingRow(
        text: String,
        running: Boolean,
        completedTitle: String = "Thought",
        completedIcon: ImageVector = Lucide.Brain,
    ) {
        var open by remember { mutableStateOf<Boolean?>(null) }
        val line: @Composable () -> Unit = {
            TraceLine(status = if (running) TraceStatus.Running else null) {
                TraceIcon { if (running) TraceSpinIcon() else Icon(completedIcon) }
                TraceTitle(if (running) "Thinking" else completedTitle)
            }
        }
        if (text.isEmpty()) {
            line()
        } else {
            TraceCollapsible(open = open ?: running, onOpenChange = { open = it }) {
                TraceCollapsibleTrigger { line() }
                TraceCollapsibleContent { Text { text } }
            }
        }
    }

    private const val reasoningText =
        "The build log shows install and compile finish in under a minute. " +
            "Image optimization takes almost four, and it reruns on every deploy " +
            "because the cache key includes the commit hash."

    private const val reasoningAnswer =
        "Image optimization is the bottleneck. Key its cache on the lockfile and the " +
            "image directory instead of the commit hash."

    @Composable
    internal fun TraceReasoningPreview() {
        var started by remember { mutableStateOf(false) }
        var running by remember { mutableStateOf(false) }
        var text by remember { mutableStateOf("") }
        var answer by remember { mutableStateOf("") }
        LaunchedEffect(started) {
            if (!started) return@LaunchedEffect
            running = true
            delay(200)
            streamTraceText(reasoningText, 60) { text = it }
            running = false
            delay(400)
            streamTraceText(reasoningAnswer, 20) { answer = it }
        }
        TraceColumn(minHeight = 288.dp) {
            if (started) {
                Bubble("Why is the checkout deploy so slow?", variant = BubbleVariant.Muted, align = MessageAlign.End)
                Trace { TraceThinkingRow(text, running) }
                if (answer.isNotEmpty()) Text { answer }
            }
            if (!started || answer == reasoningAnswer) {
                Button(
                    text = if (started) "Reset" else "Send message",
                    onClick = {
                        started = !started
                        text = ""
                        answer = ""
                    },
                    variant = ButtonVariant.Outline,
                    size = ButtonSize.Sm,
                )
            }
        }
    }
    """.trimIndent()

internal val traceToolCode =
    """
    import androidx.compose.animation.core.*
    import androidx.compose.foundation.layout.*
    import androidx.compose.runtime.*
    import androidx.compose.ui.Modifier
    import androidx.compose.ui.graphics.graphicsLayer
    import androidx.compose.ui.graphics.vector.ImageVector
    import androidx.compose.ui.unit.dp
    import com.composables.icons.lucide.*
    import com.toutiaoyan.compose.ui.components.bubble.*
    import com.toutiaoyan.compose.ui.components.button.*
    import com.toutiaoyan.compose.ui.components.codeblock.*
    import com.toutiaoyan.compose.ui.components.icon.Icon
    import com.toutiaoyan.compose.ui.components.message.MessageAlign
    import com.toutiaoyan.compose.ui.components.text.Text
    import com.toutiaoyan.compose.ui.components.trace.*
    import kotlinx.coroutines.delay

    /** Centered `max-w-sm` column, matching the official preview's `max-w-sm` wrapper. */
    @Composable
    private fun TraceColumn(
        minHeight: androidx.compose.ui.unit.Dp = 0.dp,
        content: @Composable androidx.compose.foundation.layout.ColumnScope.() -> Unit,
    ) {
        val base = Modifier.widthIn(max = 384.dp).fillMaxWidth()
        Column(
            modifier = base.heightIn(min = minHeight),
            verticalArrangement = Arrangement.spacedBy(16.dp),
            content = content,
        )
    }

    @Composable
    private fun TraceOutput(
        text: String,
        destructive: Boolean = false,
    ) {
        CodeBlock(
            size = CodeBlockSize.Sm,
            variant = if (destructive) CodeBlockVariant.Destructive else CodeBlockVariant.Default,
        ) {
            CodeBlockContent(text, wrap = true)
        }
    }

    @Composable
    private fun TraceOutputRow(
        title: String,
        output: String,
        defaultOpen: Boolean = false,
        open: Boolean? = null,
        onOpenChange: ((Boolean) -> Unit)? = null,
        destructive: Boolean = false,
        icon: ImageVector = Lucide.Terminal,
    ) {
        TraceCollapsible(defaultOpen = defaultOpen, open = open, onOpenChange = onOpenChange) {
            TraceCollapsibleTrigger(status = if (destructive) TraceStatus.Error else null) {
                TraceLine(status = if (destructive) TraceStatus.Error else null) {
                    TraceIcon { Icon(icon) }
                    TraceTitle(title)
                }
            }
            TraceCollapsibleContent { TraceOutput(output, destructive = destructive) }
        }
    }

    @Composable
    private fun TraceSpinIcon() {
        val transition = rememberInfiniteTransition(label = "trace-spin")
        val rotation by
            transition.animateFloat(
                initialValue = 0f,
                targetValue = 360f,
                animationSpec =
                    infiniteRepeatable(
                        tween(durationMillis = 1000, easing = LinearEasing),
                        RepeatMode.Restart,
                    ),
                label = "trace-spin-rotation",
            )
        Icon(Lucide.LoaderCircle, modifier = Modifier.graphicsLayer { rotationZ = rotation })
    }

    /** Local transport: cancelled automatically when its recipe leaves composition. */
    private suspend fun streamTraceText(
        text: String,
        delayMillis: Long,
        update: (String) -> Unit,
    ) {
        val words = text.split(" ")
        words.indices.forEach { index ->
            delay(delayMillis)
            update(words.take(index + 1).joinToString(" "))
        }
    }

    @Composable
    private fun TraceCommandRow(
        command: String,
        output: String,
        running: Boolean,
        failed: Boolean = false,
        defaultOpen: Boolean = false,
        completedIcon: ImageVector = Lucide.Terminal,
    ) {
        if (running) {
            TraceLine(status = TraceStatus.Running) {
                TraceIcon { TraceSpinIcon() }
                TraceTitle("Running ${'$'}command")
            }
        } else {
            TraceOutputRow(
                "${'$'}{if (failed) "Failed to run" else "Ran"} ${'$'}command",
                output,
                defaultOpen = defaultOpen,
                destructive = failed,
                icon = completedIcon,
            )
        }
    }

    @Composable
    internal fun TraceToolPreview() {
        var started by remember { mutableStateOf(false) }
        var completed by remember { mutableStateOf(0) }
        var answer by remember { mutableStateOf("") }
        LaunchedEffect(started) {
            if (!started) return@LaunchedEffect
            repeat(3) {
                delay(900)
                completed = it + 1
            }
            streamTraceText("Node 24 and npm 11 are installed. pnpm is not.", 20) { answer = it }
        }
        TraceColumn(minHeight = 288.dp) {
            if (started) {
                Bubble("Which package managers are installed?", variant = BubbleVariant.Muted, align = MessageAlign.End)
                Trace {
                    TraceCommandRow("node --version", "v24.18.0", running = completed == 0)
                    if (completed >= 1) TraceCommandRow("npm --version", "11.6.2", running = completed == 1)
                    if (completed >= 2) {
                        TraceCommandRow(
                            "pnpm --version",
                            "command not found: pnpm",
                            running = completed == 2,
                            failed = true,
                        )
                    }
                }
                if (answer.isNotEmpty()) Text { answer }
            }
            if (!started || answer == "Node 24 and npm 11 are installed. pnpm is not.") {
                Button(
                    text = if (started) "Reset" else "Send message",
                    onClick = {
                        started = !started
                        completed = 0
                        answer = ""
                    },
                    variant = ButtonVariant.Outline,
                    size = ButtonSize.Sm,
                )
            }
        }
    }
    """.trimIndent()

internal val traceStreamingOpenCode =
    """
    var open by remember { mutableStateOf<Boolean?>(null) }
    TraceCollapsible(open = open ?: running, onOpenChange = { open = it }) {
        TraceCollapsibleTrigger {
            TraceLine(status = if (running) TraceStatus.Running else null) {
                TraceTitle(if (running) "Thinking" else "Thought")
            }
        }
        TraceCollapsibleContent { Text { text } }
    }
    """.trimIndent()

internal val traceNewTabCode =
    """
    TraceLink(
        href = "https://docs.astro.build",
        contentDescription = "Install Astro (opens in a new tab)",
    ) {
        TraceLine {
            TraceTitle("Install Astro")
            TraceIcon { Icon(Lucide.ArrowUpRight) }
        }
    }
    """.trimIndent()

internal val traceDialogTriggerCode =
    """
    Dialog {
        DialogTrigger {
            TraceLine {
                TraceTitle("Searched the web for cache keys")
                TraceIcon { Icon(Lucide.ChevronRight) }
            }
        }
        DialogContent { Text { "Search results" } }
    }
    """.trimIndent()
