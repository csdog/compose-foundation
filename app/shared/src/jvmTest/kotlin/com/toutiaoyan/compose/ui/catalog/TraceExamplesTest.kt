package com.toutiaoyan.compose.ui.catalog

import androidx.compose.foundation.interaction.HoverInteraction
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.runtime.SideEffect
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.setValue
import androidx.compose.ui.graphics.Color
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.width
import androidx.compose.ui.Modifier
import androidx.compose.ui.input.key.Key
import androidx.compose.ui.test.*
import androidx.compose.ui.test.junit4.v2.createComposeRule
import androidx.compose.ui.unit.dp
import com.toutiaoyan.compose.ui.catalog.docs.componentPageMarkdown
import com.toutiaoyan.compose.ui.catalog.docs.traceDoc
import com.toutiaoyan.compose.ui.catalog.docs.TraceTriggerLine
import com.toutiaoyan.compose.ui.components.trace.TraceTitle
import com.toutiaoyan.compose.ui.theme.LocalContentColor
import com.toutiaoyan.compose.ui.theme.currentTheme
import com.toutiaoyan.compose.ui.components.overlay.OverlayHost
import com.toutiaoyan.compose.ui.theme.Theme
import com.toutiaoyan.compose.ui.tokens.StylePreset
import org.junit.Rule
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertTrue

class TraceExamplesTest {
    @get:Rule val rule = createComposeRule()

    private fun show(title: String) {
        rule.setContent {
            Theme(preset = StylePreset.Nova, minTouchTarget = null) {
                OverlayHost {
                    Box(Modifier.width(480.dp).height(720.dp)) {
                        traceDoc.examples.single { it.title == title }.preview()
                    }
                }
            }
        }
    }

    @Test fun pageKeepsTheOfficialRecipeOrderAndTitles() {
        assertEquals("Default", traceDoc.examples.first().title)
        assertEquals(
            listOf(
                "Trace Line",
                "Status",
                "Description",
                "Collapsible",
                "Open State",
                "Link",
                "Dialog",
                "Sheet",
                "Group",
                "Sources",
                "Chain of Thought",
                "Commit",
                "Whole Turn",
                "Reasoning",
                "Tool",
            ),
            traceDoc.examples.drop(1).map { it.title },
        )
        assertEquals("The building block for reasoning, tool calls, sources and more.", traceDoc.description)
        assertTrue(traceDoc.examples.none { it.title == "Expanded" })
    }

    @Test fun demoShowsTheFinishedTurnAndReplayControl() {
        show("Default")
        rule.onNodeWithText("Why does pnpm install fail in CI?").assertIsDisplayed()
        rule.onNodeWithText("Thought for 5s").assertIsDisplayed()
        rule.onNodeWithText("Ran pnpm --version").assertIsDisplayed()
        rule.onNodeWithText("Failed to run pnpm install --frozen-lockfile").assertIsDisplayed()
        rule.onNodeWithText("Used 2 sources").assertIsDisplayed()
        rule.onNodeWithText("package.json | 2 +-", substring = true).assertIsDisplayed()
        rule.onNodeWithText("pnpm-lock.yaml, so the frozen install fails.", substring = true)
            .assertIsDisplayed()
        rule.onNodeWithText("Replay").assertIsDisplayed()
    }

    @Test fun traceLineIsPlainTextWithoutAStatus() {
        show("Trace Line")
        rule.onNodeWithText("Created branch fix/frozen-lockfile").assertIsDisplayed()
    }

    @Test fun statusRowsCoverNoneErrorRunningAndPending() {
        show("Status")
        listOf(
            "Ran pnpm install",
            "Failed to run pnpm lint",
            "Running pnpm build",
            "Run pnpm test",
        ).forEach { rule.onNodeWithText(it).assertIsDisplayed() }
    }

    @Test fun descriptionKeepsCountsAndSearchResultsVisible() {
        show("Description")
        rule.onNodeWithText("+48 \u221212").assertIsDisplayed()
        rule.onNodeWithText("14s").assertIsDisplayed()
        rule.onNodeWithText("8 results").assertIsDisplayed()
    }

    @Test fun collapsibleRendersTheTerminalOutputOnlyAfterExpansion() {
        show("Collapsible")
        rule.onNodeWithText("v24.18.0", substring = true).assertDoesNotExist()
        rule.onNodeWithText("Ran node --version").performClick()
        rule.onNodeWithText("v24.18.0", substring = true).assertIsDisplayed()
    }

    @Test fun openStateCombinesDefaultOpenControlledOpenAndAnExternalTrigger() {
        show("Open State")
        rule.onNodeWithText("v24.18.0", substring = true).assertIsDisplayed()
        rule.onNodeWithText("10.33.4", substring = true).assertDoesNotExist()
        rule.onNodeWithText("Show the pnpm output").performClick()
        rule.onNodeWithText("10.33.4", substring = true).assertIsDisplayed()
        rule.onNodeWithText("Hide the pnpm output").performClick()
        rule.onNodeWithText("10.33.4", substring = true).assertDoesNotExist()
    }

    @Test fun linkRowsExposeTheSourceAndTheNewTabSuffix() {
        show("Link")
        rule.onNodeWithText("Read apps/v4/lib/docs.ts").assertIsDisplayed()
        rule.onNodeWithText("Install Astro | docs.astro.build").assertIsDisplayed()
        rule.onNodeWithContentDescription("Install Astro | docs.astro.build (opens in a new tab)")
            .assertIsDisplayed()
    }

    @Test fun dialogRowOpensTheThreeSearchResultsAndEscapeCloses() {
        show("Dialog")
        rule.onNodeWithText("Searched the web for cache keys").performClick()
        rule.onNodeWithText("Three results were used in the response.").assertIsDisplayed()
        listOf(
            "Caching build outputs",
            "Image optimization reference",
            "Deploy hooks and cache keys",
        ).forEach { rule.onNodeWithText(it).assertIsDisplayed() }
        rule.onAllNodes(isRoot()).onLast().performKeyInput { pressKey(Key.Escape) }
        rule.waitForIdle()
        rule.onNodeWithText("Three results were used in the response.").assertDoesNotExist()
        rule.onNodeWithText("Searched the web for cache keys").assertIsDisplayed()
    }

    @Test fun sheetRowOpensTheExploredFileList() {
        show("Sheet")
        rule.onNodeWithText("Explored 4 files").performClick()
        rule.onNodeWithText("Files read while preparing the response.").assertIsDisplayed()
        listOf(
            "apps/v4/lib/docs.ts",
            "apps/v4/lib/components.ts",
            "apps/v4/next.config.mjs",
            "apps/v4/package.json",
        ).forEach { rule.onNodeWithText(it).assertIsDisplayed() }
    }

    @Test fun groupShowsThreeIndependentTraceLines() {
        show("Group")
        listOf(
            "Searched for minimumReleaseAge",
            "Read pnpm-workspace.yaml",
            "Ran pnpm install",
        ).forEach { rule.onNodeWithText(it).assertIsDisplayed() }
    }

    @Test fun sourcesStartsExpandedAndListsEveryLink() {
        show("Sources")
        rule.onNodeWithText("Used 3 sources").assertIsDisplayed()
        listOf(
            "Install Astro | docs.astro.build",
            "create-astro | npmjs.com",
            "Settings: minimumReleaseAge | pnpm.io",
        ).forEach { rule.onNodeWithText(it).assertIsDisplayed() }
    }

    @Test fun chainOfThoughtNestsStepRowsAndExpandsTheBuildLog() {
        show("Chain of Thought")
        rule.onNodeWithText("Looking into the slow deploy").assertIsDisplayed()
        rule.onNodeWithText("Searched for the checkout build log").assertIsDisplayed()
        rule.onNodeWithText("Read the last twelve deploys").assertIsDisplayed()
        rule.onNodeWithText("Searching for the cache key").assertIsDisplayed()
        rule.onNodeWithText("Searched for the checkout build log").performClick()
        rule.onNodeWithText("vercel.com").assertIsDisplayed()
        rule.onNodeWithText("github.com").assertIsDisplayed()
    }

    @Test fun commitListsChangedFilesAndEachFileOpensItsDiff() {
        show("Commit")
        rule.onNodeWithText("Committed a1b2c3: Fix the login redirect").assertIsDisplayed()
        rule.onNodeWithText("Edited app/login/page.tsx").assertIsDisplayed()
        rule.onNodeWithText("Added lib/auth/redirect.ts").assertIsDisplayed()
        rule.onNodeWithText("2 additions, 2 deletions").assertDoesNotExist()
        rule.onNodeWithText("Edited app/login/page.tsx").performClick()
        rule.onNodeWithText("2 additions, 2 deletions").assertIsDisplayed()
        rule.onNodeWithText("safeRedirect", substring = true).assertIsDisplayed()
    }

    @Test fun wholeTurnNestsThreeLevelsAndKeepsTheRowsBetweenTraceLists() {
        show("Whole Turn")
        rule.onNodeWithText("Worked for 2m 4s, 1 command failed").assertIsDisplayed()
        rule.onNodeWithText("Thought").assertIsDisplayed()
        rule.onNodeWithText("Ran 2 commands, 1 failed").assertIsDisplayed()
        rule.onNodeWithText("Ran node --version").assertIsDisplayed()
        rule.onNodeWithText("Failed to run pnpm --version").assertIsDisplayed()
        rule.onNodeWithText("command not found: pnpm").assertDoesNotExist()
        rule.onNodeWithText("Failed to run pnpm --version").performClick()
        rule.onNodeWithText("command not found: pnpm").assertIsDisplayed()
        rule.onNodeWithText("Node is installed and pnpm is not. Reading the docs config.")
            .assertIsDisplayed()
        rule.onNodeWithText("Read apps/v4/lib/docs.ts").assertIsDisplayed()
    }

    @Test fun reasoningStartsEmptyAndSendMessageRendersTheThoughtRow() {
        show("Reasoning")
        rule.onNodeWithText("Send message").assertIsDisplayed()
        rule.onNodeWithText("Thought").assertDoesNotExist()
        rule.onNodeWithText("Send message").performClick()
        rule.mainClock.advanceTimeBy(5000)
        rule.onNodeWithText("Thought").assertIsDisplayed()
        rule.onNodeWithText("Image optimization is the bottleneck.", substring = true).assertIsDisplayed()
        rule.onNodeWithText("Reset").performClick()
        rule.onNodeWithText("Thought").assertDoesNotExist()
    }

    @Test fun toolStartsEmptyAndSendMessageRendersTheThreeToolRows() {
        show("Tool")
        rule.onNodeWithText("Send message").performClick()
        rule.mainClock.advanceTimeBy(4000)
        listOf(
            "Ran node --version",
            "Ran npm --version",
            "Failed to run pnpm --version",
        ).forEach { rule.onNodeWithText(it).assertIsDisplayed() }
        rule.onNodeWithText("Node 24 and npm 11 are installed. pnpm is not.").assertIsDisplayed()
        rule.onNodeWithText("Ran npm --version").performClick()
        rule.onNodeWithText("11.6.2").assertIsDisplayed()
    }

    @Test fun reasoningStreamsAndTheReadersCollapseWins() {
        show("Reasoning")
        rule.mainClock.autoAdvance = false
        rule.onNodeWithText("Send message").performClick()
        rule.mainClock.advanceTimeBy(32)
        rule.onNodeWithText("Thinking").assertIsDisplayed().assertHasNoClickAction()
        rule.onNodeWithText("Reset").assertDoesNotExist()
        rule.mainClock.advanceTimeBy(600)
        rule.onNodeWithText("The build log", substring = true).assertIsDisplayed()
        rule.onNodeWithText("Thinking").performClick()
        rule.mainClock.advanceTimeBy(300)
        rule.onNodeWithText("The build log", substring = true).assertDoesNotExist()
        rule.mainClock.advanceTimeBy(600)
        rule.onNodeWithText("The build log", substring = true).assertDoesNotExist()
        rule.mainClock.advanceTimeBy(4000)
        rule.onNodeWithText("Thought").assertIsDisplayed()
        rule.onNodeWithText("Reset").performClick()
        rule.mainClock.advanceTimeBy(32)
        rule.onNodeWithText("Thought").assertDoesNotExist()
        rule.onNodeWithText("Send message").performClick()
        rule.mainClock.advanceTimeBy(700)
        rule.onNodeWithText("The build log", substring = true).assertIsDisplayed()
    }

    @Test fun toolsRunInOrderAndOnlyCompletedToolsExpand() {
        show("Tool")
        rule.mainClock.autoAdvance = false
        rule.onNodeWithText("Send message").performClick()
        rule.mainClock.advanceTimeBy(32)
        rule.onNodeWithText("Running node --version").assertIsDisplayed().assertHasNoClickAction()
        rule.onNodeWithText("Running npm --version").assertDoesNotExist()
        rule.onNodeWithText("Reset").assertDoesNotExist()
        rule.mainClock.advanceTimeBy(1000)
        rule.onNodeWithText("Ran node --version").performClick()
        rule.mainClock.advanceTimeBy(250)
        rule.onNodeWithText("v24.18.0").assertIsDisplayed()
        rule.onNodeWithText("Running npm --version").assertIsDisplayed()
        rule.mainClock.advanceTimeBy(700)
        rule.onNodeWithText("Running pnpm --version").assertIsDisplayed()
        rule.mainClock.advanceTimeBy(1600)
        rule.onNodeWithText("Failed to run pnpm --version").performClick()
        rule.mainClock.advanceTimeBy(250)
        rule.onNodeWithText("command not found: pnpm").assertIsDisplayed()
        rule.onNodeWithText("Reset").assertIsDisplayed()
    }

    @Test fun replayClearsTheFinishedTurnAndStreamsItAgain() {
        show("Default")
        rule.mainClock.autoAdvance = false
        rule.onNodeWithText("Replay").performClick()
        rule.mainClock.advanceTimeBy(32)
        rule.onNodeWithText("Why does pnpm install fail in CI?").assertIsDisplayed()
        rule.onNodeWithText("Thinking").assertIsDisplayed()
        rule.onNodeWithText("Ran pnpm --version").assertDoesNotExist()
        rule.onNodeWithText("Used 2 sources").assertDoesNotExist()
        rule.onNodeWithText("Replay").assertDoesNotExist()
        rule.mainClock.advanceTimeBy(1700)
        rule.onNodeWithText("Running pnpm --version").assertIsDisplayed()
        rule.mainClock.advanceTimeBy(7000)
        rule.onNodeWithText("Used 2 sources").assertIsDisplayed()
        rule.onNodeWithText("Failed to run pnpm install --frozen-lockfile").assertIsDisplayed()
        rule.onNodeWithText("Replay").assertIsDisplayed()
    }

    @Test fun guideSectionsMatchTheOfficialPageCopy() {
        val titles =
            traceDoc.introductionSections.map { it.title } +
                traceDoc.beforeExamples.map { it.title } + traceDoc.afterExamples.map { it.title }
        assertEquals(listOf("Built with Trace", "Features", "Accessibility"), titles)
        assertEquals(
            8,
            (
                traceDoc.afterExamples.first().blocks.last() as
                    com.toutiaoyan.compose.ui.catalog.docs.ComponentGuideBlock.Bullets
            ).items.size,
        )
        assertEquals(
            listOf(
                "Trace", "TraceLine", "TraceIcon", "TraceTitle", "TraceDescription",
                "TraceCollapsible", "TraceCollapsibleTrigger", "TraceCollapsibleContent", "TraceLink",
            ),
            traceDoc.apiSections.map { it.title },
        )
    }

    @Test fun compoundTriggerHoverTracksTheCurrentThemeAndReturnsToMuted() {
        val source = MutableInteractionSource()
        var dark by mutableStateOf(false)
        var observed = Color.Unspecified
        var muted = Color.Unspecified
        var foreground = Color.Unspecified
        rule.setContent {
            Theme(preset = StylePreset.Nova, isDark = dark) {
                val colors = currentTheme.colors
                SideEffect {
                    muted = colors.mutedForeground
                    foreground = colors.foreground
                }
                TraceTriggerLine(interactionSource = source) {
                    val contentColor = LocalContentColor.current
                    SideEffect { observed = contentColor }
                    TraceTitle("Open results")
                }
            }
        }
        rule.runOnIdle { assertEquals(muted, observed) }
        val enter = HoverInteraction.Enter()
        rule.runOnIdle { source.tryEmit(enter) }
        rule.runOnIdle { assertEquals(foreground, observed) }
        rule.runOnIdle { dark = true }
        rule.runOnIdle { assertEquals(foreground, observed) }
        rule.runOnIdle { source.tryEmit(HoverInteraction.Exit(enter)) }
        rule.runOnIdle { assertEquals(muted, observed) }
    }

    @Test fun sourceTablesAndSupplementalCodeKeepTheirContentAndPosition() {
        val patterns =
            traceDoc.introductionSections.single().blocks.last() as
                com.toutiaoyan.compose.ui.catalog.docs.ComponentGuideBlock.Table
        assertEquals(listOf("Pattern", "Shows"), patterns.headers)
        assertEquals(11, patterns.rows.size)
        val statuses =
            traceDoc.afterExampleSections.getValue("Status").single() as
                com.toutiaoyan.compose.ui.catalog.docs.ComponentGuideBlock.Table
        assertEquals(listOf("Status", "Use for"), statuses.headers)
        assertEquals(4, statuses.rows.size)
        val markdown = componentPageMarkdown(traceDoc)
        assertTrue(markdown.indexOf("Every line has the same shape") < markdown.indexOf("## Built with Trace"))
        assertTrue(markdown.indexOf("## Built with Trace") < markdown.indexOf("## Installation"))
        assertTrue(markdown.contains("| Pattern | Shows |"))
        assertTrue(markdown.contains("| Status | Use for |"))
        val openStart = markdown.indexOf("## Open State")
        val linkStart = markdown.indexOf("## Link", openStart)
        val openSection = markdown.substring(openStart, linkStart)
        assertTrue(openSection.contains("open ?: running"))
        assertTrue(openSection.indexOf("To open a row while it streams") > openSection.indexOf("Show the pnpm output"))
        val linkSection = markdown.substring(linkStart, markdown.indexOf("## Dialog", linkStart))
        assertTrue(linkSection.contains("Install Astro (opens in a new tab)"))
        assertTrue(markdown.contains("Inside a [Message Scroller](/message-scroller), each line is announced once"))
    }

    @Test fun guideTablesRenderEveryCellWithoutAnExtraHeading() {
        rule.setContent {
            Theme(preset = StylePreset.Nova) {
                GuideBlocks(traceDoc.afterExampleSections.getValue("Status"))
            }
        }
        rule.onNodeWithText("Status").assertIsDisplayed()
        rule.onNodeWithText("Use for").assertIsDisplayed()
        rule.onNodeWithText("Work that is done", substring = true).assertIsDisplayed()
        rule.onNodeWithText("Work that has not started", substring = true).assertIsDisplayed()
        rule.onAllNodes(isHeading()).assertCountEquals(2)
    }

    @Test fun copyPageMarkdownKeepsTheRecipeOrderAndUsage() {
        val markdown = componentPageMarkdown(traceDoc)
        val positions =
            traceDoc.examples.map { markdown.indexOf("## ${it.title}") }
        assertEquals(positions.sorted(), positions)
        assertTrue(markdown.contains("The building block for reasoning, tool calls, sources and more."))
        assertTrue(markdown.contains("## Accessibility"))
        assertTrue(markdown.contains("## Built with Trace"))
        assertTrue(markdown.contains("keepMounted"))
    }
}
