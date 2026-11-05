@file:OptIn(androidx.compose.foundation.style.ExperimentalFoundationStyleApi::class)

package com.toutiaoyan.compose.ui.catalog.docs

import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.widthIn
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.semantics.heading
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.unit.sp
import androidx.compose.ui.unit.dp
import com.composables.icons.lucide.ArrowUpRight
import com.composables.icons.lucide.Brain
import com.composables.icons.lucide.ChevronRight
import com.composables.icons.lucide.Circle
import com.composables.icons.lucide.CircleAlert
import com.composables.icons.lucide.CircleCheck
import com.composables.icons.lucide.FilePen
import com.composables.icons.lucide.FilePlus
import com.composables.icons.lucide.FileText
import com.composables.icons.lucide.FolderSearch
import com.composables.icons.lucide.GitBranch
import com.composables.icons.lucide.GitCommitHorizontal
import com.composables.icons.lucide.Globe
import com.composables.icons.lucide.LoaderCircle
import com.composables.icons.lucide.Lucide
import com.composables.icons.lucide.Search
import com.composables.icons.lucide.Terminal
import com.toutiaoyan.compose.ui.components.badge.Badge
import com.toutiaoyan.compose.ui.components.badge.BadgeVariant
import com.toutiaoyan.compose.ui.components.bubble.Bubble
import com.toutiaoyan.compose.ui.components.bubble.BubbleVariant
import com.toutiaoyan.compose.ui.components.button.Button
import com.toutiaoyan.compose.ui.components.button.ButtonSize
import com.toutiaoyan.compose.ui.components.button.ButtonVariant
import com.toutiaoyan.compose.ui.components.codeblock.CodeBlock
import com.toutiaoyan.compose.ui.components.codeblock.CodeBlockContent
import com.toutiaoyan.compose.ui.components.codeblock.CodeBlockLine
import com.toutiaoyan.compose.ui.components.codeblock.CodeBlockLineVariant
import com.toutiaoyan.compose.ui.components.codeblock.CodeBlockSize
import com.toutiaoyan.compose.ui.components.codeblock.CodeBlockVariant
import com.toutiaoyan.compose.ui.components.dialog.Dialog
import com.toutiaoyan.compose.ui.components.dialog.DialogContent
import com.toutiaoyan.compose.ui.components.dialog.DialogHeader
import com.toutiaoyan.compose.ui.components.dialog.DialogTrigger
import com.toutiaoyan.compose.ui.components.icon.Icon
import com.toutiaoyan.compose.ui.components.message.MessageAlign
import com.toutiaoyan.compose.ui.components.sheet.Sheet
import com.toutiaoyan.compose.ui.components.sheet.SheetContent
import com.toutiaoyan.compose.ui.components.sheet.SheetHeader
import com.toutiaoyan.compose.ui.components.sheet.SheetTrigger
import com.toutiaoyan.compose.ui.components.text.Text
import com.toutiaoyan.compose.ui.components.trace.Trace
import com.toutiaoyan.compose.ui.components.trace.TraceCollapsible
import com.toutiaoyan.compose.ui.components.trace.TraceCollapsibleContent
import com.toutiaoyan.compose.ui.components.trace.TraceCollapsibleTrigger
import com.toutiaoyan.compose.ui.components.trace.TraceDescription
import com.toutiaoyan.compose.ui.components.trace.TraceIcon
import com.toutiaoyan.compose.ui.components.trace.TraceLine
import com.toutiaoyan.compose.ui.components.trace.TraceLink
import com.toutiaoyan.compose.ui.components.trace.TraceStatus
import com.toutiaoyan.compose.ui.components.trace.TraceTitle
import com.toutiaoyan.compose.ui.theme.currentTheme
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

private val dialogResults =
    listOf(
        "Caching build outputs" to "https://nextjs.org/docs/app/guides/caching",
        "Image optimization reference" to
            "https://nextjs.org/docs/app/api-reference/components/image",
        "Deploy hooks and cache keys" to "https://vercel.com/docs/deploy-hooks",
    )

private val exploredFiles =
    listOf(
        "apps/v4/lib/docs.ts",
        "apps/v4/lib/components.ts",
        "apps/v4/next.config.mjs",
        "apps/v4/package.json",
    )

private val sourceLinks =
    listOf(
        "Install Astro | docs.astro.build" to
            "https://docs.astro.build/en/install-and-setup/",
        "create-astro | npmjs.com" to "https://www.npmjs.com/package/create-astro",
        "Settings: minimumReleaseAge | pnpm.io" to
            "https://pnpm.io/settings#minimumreleaseage",
    )

private val changedFiles =
    listOf(
        ChangedFile(
            path = "app/login/page.tsx",
            added = false,
            additions = 2,
            deletions = 2,
            diff =
                """
                @@ -14,7 +14,7 @@ export default async function LoginPage({
                   searchParams,
                 }) {
                   const { next } = await searchParams
                -  const redirectTo =
                -    next && next.startsWith("/") ? next : "/"
                +  const redirectTo = safeRedirect(next)
                +
                   return <LoginForm redirectTo={redirectTo} />
                 }
                """.trimIndent(),
        ),
        ChangedFile(
            path = "lib/auth/redirect.ts",
            added = true,
            additions = 7,
            deletions = 0,
            diff =
                """
                @@ -0,0 +1,7 @@
                +export function safeRedirect(next: string | undefined) {
                +  if (!next || !next.startsWith("/") || next.startsWith("//")) {
                +    return "/"
                +  }
                +
                +  return next
                +}
                """.trimIndent(),
        ),
    )

internal data class ChangedFile(
    val path: String,
    val added: Boolean,
    val additions: Int,
    val deletions: Int,
    val diff: String,
)

/** Centered `max-w-sm` column, matching the official preview's `max-w-sm` wrapper. */
@Composable
private fun TraceColumn(
    height: androidx.compose.ui.unit.Dp? = null,
    content: @Composable androidx.compose.foundation.layout.ColumnScope.() -> Unit,
) {
    val base = Modifier.widthIn(max = 384.dp).fillMaxWidth()
    Column(
        modifier = if (height == null) base else base.height(height),
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
        TraceCollapsibleTrigger {
            TraceLine {
                TraceIcon { Icon(icon) }
                TraceTitle(title)
            }
        }
        TraceCollapsibleContent { TraceOutput(output, destructive = destructive) }
    }
}

/** A `TraceLine` with an icon, title and optional description/trailing indicator. */
@Composable
private fun TraceStaticLine(
    icon: ImageVector,
    title: String,
    status: TraceStatus? = null,
    description: String? = null,
    descriptionMono: Boolean = false,
    trailing: ImageVector? = null,
) {
    TraceLine(status = status) {
        TraceIcon { Icon(icon) }
        TraceTitle(title)
        if (description != null) TraceDescription(description, mono = descriptionMono)
        if (trailing != null) TraceIcon { Icon(trailing) }
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
        contentDescription = "$title (opens in a new tab)",
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

/** Official top preview: a finished assistant turn with reasoning, tool calls and sources. */
@Composable
internal fun TraceDemoPreview() {
    var replayToken by remember { mutableStateOf(0) }
    var visible by remember { mutableStateOf(true) }
    LaunchedEffect(replayToken) {
        if (replayToken == 0) return@LaunchedEffect
        visible = false
        delay(1200)
        visible = true
    }
    Column(
        Modifier.widthIn(max = 384.dp).fillMaxWidth().padding(vertical = 24.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp),
    ) {
        Bubble(demoQuestion, variant = BubbleVariant.Muted, align = MessageAlign.End)
        if (visible) {
            Trace(Modifier.fillMaxWidth()) {
                TraceCollapsible {
                    TraceCollapsibleTrigger {
                        TraceLine {
                            TraceIcon { Icon(Lucide.CircleCheck) }
                            TraceTitle("Thought for 5s")
                        }
                    }
                    TraceCollapsibleContent { Text { thoughtText } }
                }
                TraceOutputRow("Ran pnpm --version", "10.18.0", icon = Lucide.CircleCheck)
                TraceOutputRow(
                    "Ran git diff --stat HEAD~1 -- package.json pnpm-lock.yaml",
                    " package.json | 2 +-\n 1 file changed, 1 insertion(+), 1 deletion(-)",
                    defaultOpen = true,
                    icon = Lucide.CircleCheck,
                )
                TraceCollapsible {
                    TraceCollapsibleTrigger {
                        TraceLine(status = TraceStatus.Error) {
                            TraceIcon { Icon(Lucide.CircleAlert) }
                            TraceTitle("Failed to run pnpm install --frozen-lockfile")
                        }
                    }
                    TraceCollapsibleContent { TraceOutput(lockfileError, destructive = true) }
                }
                TraceCollapsible {
                    TraceCollapsibleTrigger {
                        TraceLine {
                            TraceIcon { Icon(Lucide.Globe) }
                            TraceTitle("Used ${demoSources.size} sources")
                        }
                    }
                    TraceCollapsibleContent {
                        Trace {
                            demoSources.forEach { (title, href) -> TraceSourceRow(title, href) }
                        }
                    }
                }
            }
            Text { demoAnswer }
        }
        Button(
            text = "Replay",
            onClick = { replayToken += 1 },
            variant = ButtonVariant.Outline,
            size = ButtonSize.Sm,
        )
    }
}

@Composable
internal fun TraceStaticPreview() {
    Trace(Modifier.widthIn(max = 384.dp)) {
        TraceLine {
            TraceIcon { Icon(Lucide.GitBranch) }
            TraceTitle("Created branch fix/frozen-lockfile")
        }
    }
}

@Composable
internal fun TraceStatusPreview() {
    Trace(Modifier.widthIn(max = 384.dp)) {
        TraceStaticLine(Lucide.CircleCheck, "Ran pnpm install")
        TraceStaticLine(Lucide.CircleAlert, "Failed to run pnpm lint", status = TraceStatus.Error)
        TraceLine(status = TraceStatus.Running) {
            TraceIcon { TraceSpinIcon() }
            TraceTitle("Running pnpm build")
        }
        TraceStaticLine(Lucide.Circle, "Run pnpm test", status = TraceStatus.Pending)
    }
}

@Composable
internal fun TraceDescriptionPreview() {
    Trace(Modifier.widthIn(max = 384.dp)) {
        TraceStaticLine(
            Lucide.FilePen,
            "Edited components/settings/notification-preferences-form.tsx",
            description = "+48 \u221212",
            descriptionMono = true,
        )
        TraceStaticLine(Lucide.Terminal, "Ran pnpm test", description = "14s")
        TraceStaticLine(Lucide.Search, "Searched for useChat", description = "8 results")
    }
}

@Composable
internal fun TraceCollapsiblePreview() {
    Column(Modifier.widthIn(max = 384.dp).fillMaxWidth()) {
        TraceNodeVersion()
    }
}

@Composable
private fun TraceNodeVersion() {
    TraceCollapsible {
        TraceCollapsibleTrigger {
            TraceLine {
                TraceIcon { Icon(Lucide.Terminal) }
                TraceTitle("Ran node --version")
            }
        }
        TraceCollapsibleContent {
            TraceOutput("$ node --version\nv24.18.0")
        }
    }
}

@Composable
internal fun TraceOpenPreview() {
    var open by remember { mutableStateOf(false) }
    TraceColumn {
        TraceOutputRow("Ran node --version", "$ node --version\nv24.18.0", defaultOpen = true)
        TraceOutputRow(
            "Ran pnpm --version",
            "$ pnpm --version\n10.33.4",
            open = open,
            onOpenChange = { open = it },
        )
        Button(
            text = if (open) "Hide the pnpm output" else "Show the pnpm output",
            onClick = { open = !open },
            variant = ButtonVariant.Outline,
            size = ButtonSize.Sm,
        )
    }
}

@Composable
internal fun TraceLinkPreview() {
    Trace(Modifier.widthIn(max = 384.dp)) {
        TraceLink(href = "#link") {
            TraceLine {
                TraceIcon { Icon(Lucide.FileText) }
                TraceTitle("Read apps/v4/lib/docs.ts")
                TraceIcon { Icon(Lucide.ChevronRight) }
            }
        }
        TraceSourceRow(
            title = "Install Astro | docs.astro.build",
            href = "https://docs.astro.build/en/install-and-setup/",
        )
    }
}

@Composable
internal fun TraceDialogPreview() {
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
                    title = "Searched the web for cache keys",
                    description = "Three results were used in the response.",
                )
                Trace(Modifier.fillMaxWidth().padding(top = 16.dp)) {
                    dialogResults.forEach { (title, href) -> TraceSourceRow(title, href) }
                }
            }
        }
    }
}

@Composable
internal fun TraceSheetPreview() {
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
                    title = "Explored 4 files",
                    description = "Files read while preparing the response.",
                )
                Column(
                    Modifier.padding(horizontal = 24.dp, vertical = 16.dp),
                    verticalArrangement = Arrangement.spacedBy(8.dp),
                ) {
                    exploredFiles.forEach { path -> MonoText(path) }
                }
            }
        }
    }
}

@Composable
private fun MonoText(text: String) {
    Text(
        textStyle =
            currentTheme.typography.bodyMedium.copy(
                fontFamily = FontFamily.Monospace,
                fontSize = 12.sp,
                color = currentTheme.colors.foreground,
            ),
    ) {
        text
    }
}

@Composable
internal fun TraceGroupPreview() {
    Trace(Modifier.widthIn(max = 384.dp)) {
        TraceStaticLine(Lucide.Search, "Searched for minimumReleaseAge")
        TraceStaticLine(Lucide.FileText, "Read pnpm-workspace.yaml")
        TraceStaticLine(Lucide.Terminal, "Ran pnpm install")
    }
}

@Composable
internal fun TraceSourcesPreview() {
    Column(Modifier.widthIn(max = 384.dp).fillMaxWidth()) {
        TraceCollapsible(defaultOpen = true) {
            TraceCollapsibleTrigger {
                TraceLine {
                    TraceIcon { Icon(Lucide.Globe) }
                    TraceTitle("Used ${sourceLinks.size} sources")
                }
            }
            TraceCollapsibleContent {
                Trace {
                    sourceLinks.forEach { (title, href) -> TraceSourceRow(title, href) }
                }
            }
        }
    }
}

@Composable
internal fun TraceChainPreview() {
    Column(Modifier.widthIn(max = 384.dp).fillMaxWidth()) {
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
                        TraceCollapsibleContent { TraceOutput("cache key image optimization") }
                    }
                }
            }
        }
    }
}

@Composable
internal fun TraceCommitPreview() {
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
                    changedFiles.forEach { file ->
                        Sheet {
                            SheetTrigger {
                                TraceLine {
                                    TraceIcon { Icon(if (file.added) Lucide.FilePlus else Lucide.FilePen) }
                                    TraceTitle("${if (file.added) "Added" else "Edited"} ${file.path}")
                                    TraceDescription(
                                        "+${file.additions} \u2212${file.deletions}",
                                        mono = true,
                                    )
                                    TraceIcon { Icon(Lucide.ChevronRight) }
                                }
                            }
                            SheetContent {
                                SheetHeader {
                                    Text(
                                        modifier = Modifier.semantics { heading() },
                                        textStyle =
                                            currentTheme.typography.bodyMedium.copy(
                                                fontFamily = FontFamily.Monospace,
                                                color = currentTheme.colors.foreground,
                                            ),
                                    ) {
                                        file.path
                                    }
                                    Text(
                                        textStyle =
                                            currentTheme.typography.bodyMedium.copy(
                                                color = currentTheme.colors.mutedForeground,
                                            ),
                                    ) {
                                        "${file.additions} additions, ${file.deletions} deletions"
                                    }
                                }
                                TraceDiff(file.diff)
                            }
                        }
                    }
                }
            }
        }
    }
}

@Composable
private fun TraceDiff(diff: String) {
    Column(Modifier.fillMaxWidth().padding(start = 24.dp, end = 24.dp, top = 16.dp)) {
        CodeBlock {
            CodeBlockContent(wrap = true) {
                diff.lineSequence().forEach { line ->
                    val variant =
                        when {
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
}

@Composable
internal fun TraceTurnPreview() {
    Column(Modifier.widthIn(max = 384.dp).fillMaxWidth()) {
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
                                Text {
                                    "I should check which package managers are installed before " +
                                        "I scaffold anything."
                                }
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
                                    TraceStaticLine(Lucide.Terminal, "Ran node --version")
                                    TraceCollapsible {
                                        TraceCollapsibleTrigger {
                                            TraceLine(status = TraceStatus.Error) {
                                                TraceIcon { Icon(Lucide.Terminal) }
                                                TraceTitle("Failed to run pnpm --version")
                                            }
                                        }
                                        TraceCollapsibleContent {
                                            TraceOutput("command not found: pnpm", destructive = true)
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
}

@Composable
private fun TraceReasoningConversation() {
    Column(Modifier.fillMaxWidth(), verticalArrangement = Arrangement.spacedBy(16.dp)) {
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
                            "Image optimization takes almost four, and it reruns on every deploy " +
                            "because the cache key includes the commit hash."
                    }
                }
            }
        }
        Text {
            "Image optimization is the bottleneck. Key its cache on the lockfile and the " +
                "image directory instead of the commit hash."
        }
    }
}

@Composable
internal fun TraceReasoningPreview() {
    var started by remember { mutableStateOf(false) }
    Column(
        Modifier.widthIn(max = 384.dp).fillMaxWidth().height(288.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp),
    ) {
        if (started) TraceReasoningConversation()
        Spacer(Modifier.weight(1f))
        Button(
            text = if (started) "Reset" else "Send message",
            onClick = { started = !started },
            variant = ButtonVariant.Outline,
            size = ButtonSize.Sm,
        )
    }
}

@Composable
private fun TraceToolConversation() {
    Column(Modifier.fillMaxWidth(), verticalArrangement = Arrangement.spacedBy(16.dp)) {
        Bubble(
            "Which package managers are installed?",
            variant = BubbleVariant.Muted,
            align = MessageAlign.End,
        )
        Trace(Modifier.fillMaxWidth()) {
            TraceOutputRow("Ran node --version", "v24.18.0")
            TraceOutputRow("Ran npm --version", "11.6.2")
            TraceOutputRow(
                "Failed to run pnpm --version",
                "command not found: pnpm",
                destructive = true,
            )
        }
        Text { "Node 24 and npm 11 are installed. pnpm is not." }
    }
}

@Composable
internal fun TraceToolPreview() {
    var started by remember { mutableStateOf(false) }
    Column(
        Modifier.widthIn(max = 384.dp).fillMaxWidth().height(288.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp),
    ) {
        if (started) TraceToolConversation()
        Spacer(Modifier.weight(1f))
        Button(
            text = if (started) "Reset" else "Send message",
            onClick = { started = !started },
            variant = ButtonVariant.Outline,
            size = ButtonSize.Sm,
        )
    }
}
