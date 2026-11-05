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
    Column(
        Modifier.widthIn(max = 384.dp).fillMaxWidth().padding(vertical = 24.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp),
    ) {
        Bubble(
            "Why does pnpm install fail in CI?",
            variant = BubbleVariant.Muted,
            align = MessageAlign.End,
        )
        Trace(Modifier.fillMaxWidth()) {
            TraceCollapsible {
                TraceCollapsibleTrigger {
                    TraceLine {
                        TraceIcon { Icon(Lucide.CircleCheck) }
                        TraceTitle("Thought for 5s")
                    }
                }
                TraceCollapsibleContent {
                    Text { "CI installs with a frozen lockfile, so the install stops when " +
                        "package.json and pnpm-lock.yaml disagree." }
                }
            }
            TraceCollapsible {
                TraceCollapsibleTrigger {
                    TraceLine {
                        TraceIcon { Icon(Lucide.CircleCheck) }
                        TraceTitle("Ran pnpm --version")
                    }
                }
                TraceCollapsibleContent {
                    CodeBlock(size = CodeBlockSize.Sm) { CodeBlockContent("10.18.0") }
                }
            }
            TraceCollapsible(defaultOpen = true) {
                TraceCollapsibleTrigger {
                    TraceLine {
                        TraceIcon { Icon(Lucide.CircleCheck) }
                        TraceTitle("Ran git diff --stat HEAD~1 -- package.json pnpm-lock.yaml")
                    }
                }
                TraceCollapsibleContent {
                    CodeBlock(size = CodeBlockSize.Sm) {
                        CodeBlockContent(
                            " package.json | 2 +-\n 1 file changed, 1 insertion(+), 1 deletion(-)",
                        )
                    }
                }
            }
            TraceCollapsible {
                TraceCollapsibleTrigger {
                    TraceLine(status = TraceStatus.Error) {
                        TraceIcon { Icon(Lucide.CircleAlert) }
                        TraceTitle("Failed to run pnpm install --frozen-lockfile")
                    }
                }
                TraceCollapsibleContent {
                    CodeBlock(size = CodeBlockSize.Sm, variant = CodeBlockVariant.Destructive) {
                        CodeBlockContent(
                            "ERR_PNPM_OUTDATED_LOCKFILE  Cannot install with \"frozen-lockfile\" " +
                                "because pnpm-lock.yaml is not up to date with package.json",
                        )
                    }
                }
            }
            TraceCollapsible {
                TraceCollapsibleTrigger {
                    TraceLine {
                        TraceIcon { Icon(Lucide.Globe) }
                        TraceTitle("Used 2 sources")
                    }
                }
                TraceCollapsibleContent {
                    Trace {
                        TraceLink(
                            href = "https://pnpm.io/cli/install#--frozen-lockfile",
                            contentDescription = "pnpm install | pnpm.io (opens in a new tab)",
                        ) {
                            TraceLine {
                                TraceIcon { Icon(Lucide.Globe) }
                                TraceTitle("pnpm install | pnpm.io")
                                TraceIcon { Icon(Lucide.ArrowUpRight) }
                            }
                        }
                        TraceLink(
                            href = "https://pnpm.io/continuous-integration",
                            contentDescription = "Continuous Integration | pnpm.io (opens in a new tab)",
                        ) {
                            TraceLine {
                                TraceIcon { Icon(Lucide.Globe) }
                                TraceTitle("Continuous Integration | pnpm.io")
                                TraceIcon { Icon(Lucide.ArrowUpRight) }
                            }
                        }
                    }
                }
            }
        }
        Text {
            "The last commit bumped a version in package.json without updating " +
                "pnpm-lock.yaml, so the frozen install fails. Run pnpm install locally " +
                "and commit the lockfile."
        }
        Button("Replay", variant = ButtonVariant.Outline, size = ButtonSize.Sm, onClick = { })
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
    Trace(Modifier.widthIn(max = 384.dp)) {
        Dialog {
            DialogTrigger {
                TraceLine {
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
    """.trimIndent()

internal val traceSheetCode =
    """
    val files = listOf(
        "apps/v4/lib/docs.ts",
        "apps/v4/lib/components.ts",
        "apps/v4/next.config.mjs",
        "apps/v4/package.json",
    )

    Trace(Modifier.widthIn(max = 384.dp)) {
        Sheet {
            SheetTrigger {
                TraceLine {
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
                                TraceLine {
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
    """.trimIndent()

internal val traceTurnCode =
    """
    Column(Modifier.widthIn(max = 384.dp)) {
        TraceCollapsible(defaultOpen = true) {
            TraceCollapsibleTrigger {
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
                            TraceCollapsibleTrigger {
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
                                        TraceCollapsibleTrigger {
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
    var started by remember { mutableStateOf(false) }
    Column(
        Modifier.widthIn(max = 384.dp).fillMaxWidth().height(288.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp),
    ) {
        if (started) {
            Bubble(
                "Why is the checkout deploy so slow?",
                variant = BubbleVariant.Muted,
                align = MessageAlign.End,
            )
            Trace(Modifier.fillMaxWidth()) {
                TraceCollapsible {
                    TraceCollapsibleTrigger {
                        TraceLine {
                            TraceIcon { Icon(Lucide.Brain) }
                            TraceTitle("Thought")
                        }
                    }
                    TraceCollapsibleContent {
                        Text {
                            "The build log shows install and compile finish in under a minute. " +
                                "Image optimization takes almost four, and it reruns on every " +
                                "deploy because the cache key includes the commit hash."
                        }
                    }
                }
            }
            Text {
                "Image optimization is the bottleneck. Key its cache on the lockfile and " +
                    "the image directory instead of the commit hash."
            }
        }
        Spacer(Modifier.weight(1f))
        Button(
            text = if (started) "Reset" else "Send message",
            onClick = { started = !started },
            variant = ButtonVariant.Outline,
            size = ButtonSize.Sm,
        )
    }
    """.trimIndent()

internal val traceToolCode =
    """
    var started by remember { mutableStateOf(false) }
    Column(
        Modifier.widthIn(max = 384.dp).fillMaxWidth().height(288.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp),
    ) {
        if (started) {
            Bubble(
                "Which package managers are installed?",
                variant = BubbleVariant.Muted,
                align = MessageAlign.End,
            )
            Trace(Modifier.fillMaxWidth()) {
                TraceCollapsible {
                    TraceCollapsibleTrigger {
                        TraceLine {
                            TraceIcon { Icon(Lucide.Terminal) }
                            TraceTitle("Ran node --version")
                        }
                    }
                    TraceCollapsibleContent {
                        CodeBlock(size = CodeBlockSize.Sm) { CodeBlockContent("v24.18.0") }
                    }
                }
                TraceCollapsible {
                    TraceCollapsibleTrigger {
                        TraceLine {
                            TraceIcon { Icon(Lucide.Terminal) }
                            TraceTitle("Ran npm --version")
                        }
                    }
                    TraceCollapsibleContent {
                        CodeBlock(size = CodeBlockSize.Sm) { CodeBlockContent("11.6.2") }
                    }
                }
                TraceCollapsible {
                    TraceCollapsibleTrigger {
                        TraceLine(status = TraceStatus.Error) {
                            TraceIcon { Icon(Lucide.Terminal) }
                            TraceTitle("Failed to run pnpm --version")
                        }
                    }
                    TraceCollapsibleContent {
                        CodeBlock(size = CodeBlockSize.Sm) {
                            CodeBlockContent("command not found: pnpm")
                        }
                    }
                }
            }
            Text { "Node 24 and npm 11 are installed. pnpm is not." }
        }
        Spacer(Modifier.weight(1f))
        Button(
            text = if (started) "Reset" else "Send message",
            onClick = { started = !started },
            variant = ButtonVariant.Outline,
            size = ButtonSize.Sm,
        )
    }
    """.trimIndent()
