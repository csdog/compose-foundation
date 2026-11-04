@file:OptIn(androidx.compose.foundation.style.ExperimentalFoundationStyleApi::class)

package com.toutiaoyan.compose.ui.catalog.docs

import androidx.compose.foundation.gestures.Orientation
import androidx.compose.foundation.hoverable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.interaction.collectIsHoveredAsState
import androidx.compose.foundation.interaction.collectIsDraggedAsState
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.pager.PageSize
import androidx.compose.foundation.pager.PagerState
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.foundation.style.Style
import androidx.compose.foundation.style.contentPadding
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.platform.LocalLayoutDirection
import androidx.compose.ui.platform.LocalWindowInfo
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.LayoutDirection
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.toutiaoyan.compose.ui.components.card.Card
import com.toutiaoyan.compose.ui.components.card.CardContent
import com.toutiaoyan.compose.ui.components.carousel.*
import com.toutiaoyan.compose.ui.components.text.Text
import com.toutiaoyan.compose.ui.theme.currentTheme
import kotlinx.coroutines.delay
import kotlinx.coroutines.isActive
import kotlinx.coroutines.launch

private val carouselTitles =
    listOf(
        "Default",
        "Sizes",
        "Spacing",
        "Orientation",
        "API",
        "Plugins",
        "RTL",
        "With dot indicators",
    )

val carouselDoc =
    ComponentDoc(
        id = "carousel", title = "Carousel",
        description = "A carousel with motion and swipe, using Compose Pager.",
        usageImportsCode = (
            "import " +
                "com.toutiaoyan.compose.ui.components.carousel.*\nimport " +
                "androidx.compose.foundation.pager.rememberPagerState"
        ),
        usageCode = carouselCode("Default"),
        compositionCode = (
            "CarouselRoot\n├── CarouselContent\n│   ├── CarouselItem\n│   " +
                "└── CarouselItem\n├── CarouselPrevious\n└── CarouselNext"
        ),
        examplesHeading = null,
        beforeExamples =
            listOf(
                ComponentGuideSection(
                    "About",
                    listOf(
                        ComponentGuideBlock.Paragraph(
                            (
                                "The official web component wraps Embla. Kotlin uses Compose " +
                                    "Pager. CarouselRoot supplies shared "
                            ) +
                                (
                                    "state, orientation and keyboard navigation to content and " +
                                        "controls; the existing standalone Carousel "
                                ) +
                                "pager remains available. A bounded viewport is required.",
                        ),
                    ),
                ),
            ),
        examples =
            carouselTitles.map { title ->
                ComponentExample(
                    title = title,
                    code = carouselCode(title),
                    minPreviewHeight =
                        when (title) {
                            "RTL" -> 576.dp
                            "Default", "Orientation", "API", "Plugins" -> 512.dp
                            else -> 288.dp
                        },
                    mobileMinPreviewHeight =
                        when (title) {
                            "RTL" -> 384.dp
                            "Default" -> 320.dp
                            "API", "Plugins" -> 288.dp
                            else -> null
                        },
                    previewPadding = if (title == "RTL") 0.dp else 40.dp,
                    descriptionMarkdown =
                        when (title) {
                            "RTL" -> "Set LocalLayoutDirection to match the language and navigation direction."
                            "Sizes" -> "Two slides on small screens and three at the large breakpoint."
                            "Spacing" -> "Use a smaller gap between slides without adding an empty leading gutter."
                            "Orientation" -> "A vertical viewport with two visible slides."
                            "API" -> (
                                "PagerState exposes the selected slide, count, scroll " +
                                    "boundaries and suspend scroll functions."
                            )
                            "Plugins" -> (
                                "Advance every two seconds. Like the official stop/reset " +
                                    "recipe, hover or a drag stops autoplay."
                            )
                            "With dot indicators" -> (
                                "An additional Kotlin recipe; the official component does not " +
                                    "provide pagination dots."
                            )
                            else -> null
                        },
                    preview = { if (title == "RTL") CarouselRtlRecipe() else CarouselRecipe(title) },
                )
            },
        afterExamples =
            listOf(
                ComponentGuideSection(
                    "Options",
                    listOf(
                        ComponentGuideBlock.Paragraph(
                            (
                                "Use pageSize and pageSpacing for partial slides, orientation " +
                                    "for the axis, and userScrollEnabled "
                            ) +
                                (
                                    "to control gestures. Embla opts, loop, align modes and " +
                                        "JavaScript plugin objects are not "
                                ) +
                                "accepted by this Kotlin API; full option parity remains pending.",
                        ),
                    ),
                ),
                ComponentGuideSection(
                    "Events",
                    listOf(
                        ComponentGuideBlock.Code(
                            (
                                "LaunchedEffect(state) {\n    snapshotFlow { state.settledPage " +
                                    "}.collect { page ->\n        onSlideSelected(page)\n    }\n}"
                            ),
                        ),
                    ),
                ),
                ComponentGuideSection(
                    "Keyboard and accessibility",
                    listOf(
                        ComponentGuideBlock.Bullets(
                            listOf(
                                (
                                    "ArrowLeft and ArrowRight navigate from the compound root, " +
                                        "including when a control has focus, matching the web handler."
                                ),
                                (
                                    "Buttons disable from actual scroll boundaries, including " +
                                        "partial-slide layouts and empty data."
                                ),
                                (
                                    "Use ariaLabel on the root and descriptions on slides. Use the " +
                                        "reading direction for horizontal swipe and mirrored controls."
                                ),
                            ),
                        ),
                    ),
                ),
            ),
        apiSections =
            listOf(
                ComponentApiSection(
                    "CarouselRoot",
                    (
                        "Compound layout and state context; callers place controls " +
                            "with BoxScope."
                    ),
                    listOf(
                        ApiProperty("state", "PagerState", "Required"),
                        ApiProperty("orientation", "Orientation", "Horizontal"),
                        ApiProperty("ariaLabel", "String?", "null"),
                        ApiProperty("modifier", "Modifier", "Modifier"),
                        ApiProperty("content", "@Composable BoxScope.() -> Unit", "Required"),
                    ),
                ),
                ComponentApiSection(
                    "Carousel / CarouselContent",
                    (
                        "Standalone pager or viewport in a CarouselRoot. Content can " +
                            "inherit the root state and orientation."
                    ),
                    listOf(
                        ApiProperty("state", "PagerState / PagerState?", "Required / inherited"),
                        ApiProperty("pageSize", "PageSize", "Fill"),
                        ApiProperty("pageSpacing", "Dp", "0.dp"),
                        ApiProperty("userScrollEnabled", "Boolean", "true"),
                        ApiProperty("style", "Style", "Style"),
                        ApiProperty("pageContent", "@Composable (Int) -> Unit", "Required"),
                    ),
                ),
                ComponentApiSection(
                    "CarouselPrevious / CarouselNext",
                    (
                        "Nova 28dp circular controls with inherited orientation and " +
                            "RTL icons."
                    ),
                    listOf(
                        ApiProperty("state", "PagerState?", "Inherited"),
                        ApiProperty("disabled", "Boolean", "false"),
                        ApiProperty(
                            "icon",
                            "@Composable () -> Unit",
                            "Chevron",
                        ),
                        ApiProperty(
                            "modifier / style",
                            "Modifier / Style",
                            "Default",
                        ),
                    ),
                ),
                ComponentApiSection(
                    "CarouselItem / CarouselDots",
                    (
                        "Caller content for a slide; optional Kotlin pagination " +
                            "extension."
                    ),
                    listOf(
                        ApiProperty("content", "@Composable BoxScope.() -> Unit", "Required on CarouselItem"),
                        ApiProperty("state", "PagerState?", "Inherited on CarouselDots"),
                    ),
                ),
            ),
    )

@Composable
private fun CarouselRtlRecipe() {
    var language by remember { mutableStateOf(0) }
    val mobile = with(LocalDensity.current) { LocalWindowInfo.current.containerSize.width.toDp() } < 640.dp
    RtlPreview(language, listOf("Arabic (العربية)", "English", "Hebrew (עברית)"), { language = it }) {
        Box(
            Modifier.fillMaxWidth().heightIn(
                min =
                    if (mobile) {
                        272.dp
                    } else {
                        464.dp
                    },
            ),
            contentAlignment =
                Alignment.Center,
        ) {
            CompositionLocalProvider(
                LocalLayoutDirection provides
                    if (language == 1) {
                        LayoutDirection.Ltr
                    } else {
                        LayoutDirection.Rtl
                    },
            ) {
                CarouselRecipe("RTL", language)
            }
        }
    }
}

@Composable
private fun CarouselRecipe(
    title: String,
    language: Int = 1,
) {
    val window = with(LocalDensity.current) { LocalWindowInfo.current.containerSize.width.toDp() }
    val multiple = title in listOf("Sizes", "Spacing")
    val vertical = title == "Orientation"
    val small = window < 640.dp
    val width =
        when {
            small && title in listOf("API", "Plugins") -> 160.dp
            small && !vertical -> 192.dp
            multiple && window >= 768.dp -> 384.dp
            else -> 320.dp
        }
    val visible =
        if (multiple) {
            if (window >= 1024.dp) 3 else 2
        } else if (vertical) {
            2
        } else {
            1
        }
    val gap = if (title == "Spacing" || vertical) 4.dp else 16.dp
    val pageWidth = (width + gap) / visible - gap
    val height = if (vertical) 266.dp else pageWidth + 32.dp
    val state = rememberPagerState { 5 }
    val hover = remember { MutableInteractionSource() }
    val hovered by hover.collectIsHoveredAsState()
    val dragged by state.interactionSource.collectIsDraggedAsState()
    var playing by remember { mutableStateOf(true) }
    LaunchedEffect(hovered, dragged) { if (hovered || dragged) playing = false }
    if (title == "Plugins") CarouselAutoplay(state, playing)
    Column(horizontalAlignment = Alignment.CenterHorizontally) {
        CarouselRoot(
            state,
            Modifier.width(width).height(height).hoverable(hover),
            orientation = if (vertical) Orientation.Vertical else Orientation.Horizontal,
            ariaLabel = "Slides",
        ) {
            CarouselContent(
                pageSize = PageSize.Fixed(if (vertical) 131.dp else pageWidth),
                pageSpacing = gap,
            ) { page ->
                Box(Modifier.fillMaxSize().semantics { contentDescription = "Slide ${page + 1} of 5" }) {
                    Card(
                        Modifier.fillMaxWidth().padding(if (title == "API") 1.dp else 4.dp),
                        style = Style { contentPadding(horizontal = 0.dp, vertical = 16.dp) },
                    ) {
                        CardContent(
                            modifier = if (vertical) Modifier else Modifier.aspectRatio(1f),
                            style = Style { contentPadding(24.dp) },
                        ) {
                            Box(
                                if (vertical) {
                                    Modifier.fillMaxWidth()
                                } else {
                                    Modifier.fillMaxSize()
                                },
                                contentAlignment =
                                    Alignment.Center,
                            ) {
                                Text(
                                    textStyle =
                                        TextStyle(
                                            fontSize =
                                                if (title == "Spacing") {
                                                    24.sp
                                                } else {
                                                    if (multiple ||
                                                        vertical
                                                    ) {
                                                        30.sp
                                                    } else {
                                                        36.sp
                                                    }
                                                },
                                            lineHeight =
                                                if (title == "Spacing") {
                                                    32.sp
                                                } else {
                                                    if (multiple ||
                                                        vertical
                                                    ) {
                                                        36.sp
                                                    } else {
                                                        40.sp
                                                    }
                                                },
                                            fontWeight = FontWeight.SemiBold,
                                        ),
                                ) {
                                    if (title == "RTL" &&
                                        language == 0
                                    ) {
                                        "٠١٢٣٤٥٦٧٨٩"[
                                            page +
                                                1,
                                        ].toString()
                                    } else {
                                        (
                                            page +
                                                1
                                        ).toString()
                                    }
                                }
                            }
                        }
                    }
                }
            }
            CarouselPrevious(
                modifier =
                    if (vertical) {
                        Modifier.align(Alignment.TopCenter).offset(y = (-48).dp)
                    } else {
                        Modifier.align(Alignment.CenterStart).offset(x = (-48).dp)
                    },
            )
            CarouselNext(
                modifier =
                    if (vertical) {
                        Modifier.align(Alignment.BottomCenter).offset(y = 48.dp)
                    } else {
                        Modifier.align(Alignment.CenterEnd).offset(x = 48.dp)
                    },
            )
        }
        if (title == "API") {
            Text(
                modifier = Modifier.padding(vertical = 8.dp),
                textStyle =
                    TextStyle(
                        fontSize = 14.sp,
                        lineHeight = 20.sp,
                        color = currentTheme.colors.mutedForeground,
                    ),
            ) { "Slide ${state.settledPage + 1} of ${state.pageCount}" }
        }
        if (title == "With dot indicators") CarouselDots(state, Modifier.padding(top = 12.dp))
    }
}

@Composable
internal fun CarouselAutoplay(
    state: PagerState,
    enabled: Boolean,
    delayMillis: Long = 2000,
) {
    require(delayMillis > 0)
    val scope = rememberCoroutineScope()
    LaunchedEffect(state, enabled, delayMillis) {
        if (!enabled) return@LaunchedEffect
        while (isActive) {
            delay(delayMillis)
            if (state.pageCount > 1 && !state.isScrollInProgress) {
                val target = if (state.canScrollForward) state.targetPage + 1 else 0
                scope.launch { state.animateScrollToPage(target) }
            }
        }
    }
}

private fun carouselCode(title: String): String {
    val partial = title in listOf("Sizes", "Spacing")
    val vertical = title == "Orientation"
    val width =
        if (title in listOf("API", "Plugins")) {
            "if (window < 640.dp) 160.dp else 320.dp"
        } else if (vertical) {
            "320.dp"
        } else if (partial) {
            (
                "if (window < 640.dp) 192.dp else if (window < 768.dp) 320.dp " +
                    "else 384.dp"
            )
        } else {
            "if (window < 640.dp) 192.dp else 320.dp"
        }
    val visible = if (partial) "if (window >= 1024.dp) 3 else 2" else "1"
    val gap = if (title == "Spacing" || vertical) 4 else 16
    val height = if (vertical) "266.dp" else "pageWidth + 32.dp"
    val axis = if (vertical) "Orientation.Vertical" else "Orientation.Horizontal"
    val pageSize = if (vertical) "131.dp" else "pageWidth"
    val cardSize = if (vertical) "Modifier" else "Modifier.aspectRatio(1f)"
    val innerSize = if (vertical) "Modifier.fillMaxWidth()" else "Modifier.fillMaxSize()"
    val font =
        if (title == "Spacing") {
            24
        } else if (partial || vertical) {
            30
        } else {
            36
        }
    val line =
        if (title == "Spacing") {
            32
        } else if (partial || vertical) {
            36
        } else {
            40
        }
    val previous =
        if (vertical) {
            "Modifier.align(Alignment.TopCenter).offset(y = (-48).dp)"
        } else {
            "Modifier.align(Alignment.CenterStart).offset(x = (-48).dp)"
        }
    val next =
        if (vertical) {
            "Modifier.align(Alignment.BottomCenter).offset(y = 48.dp)"
        } else {
            "Modifier.align(Alignment.CenterEnd).offset(x = 48.dp)"
        }
    val padding = if (title == "API") 1 else 4
    val body =
        """
        val window = with(LocalDensity.current) { LocalWindowInfo.current.containerSize.width.toDp() }
        val width = $width
        val visible = $visible
        val gap = $gap.dp
        val pageWidth = (width + gap) / visible - gap
        val state = rememberPagerState { 5 }
        Column(horizontalAlignment = Alignment.CenterHorizontally) {
            CarouselRoot(state, Modifier.width(width).height($height), orientation = $axis, ariaLabel = "Slides") {
                CarouselContent(pageSize = PageSize.Fixed($pageSize), pageSpacing = gap) { page ->
                    Box(Modifier.fillMaxSize()) {
                        Card(Modifier.fillMaxWidth().padding($padding.dp),
                        style = Style { contentPadding(horizontal = 0.dp, vertical = 16.dp) },
                    ) {
                            CardContent(modifier = $cardSize, style = Style { contentPadding(24.dp) }) {
                                Box($innerSize, contentAlignment = Alignment.Center) {
                                    Text(textStyle = TextStyle(fontSize = $font.sp, lineHeight = $line.sp, fontWeight = FontWeight.SemiBold)) {
                                        (page + 1).toString()
                                    }
                                }
                            }
                        }
                    }
                }
                CarouselPrevious(modifier = $previous)
                CarouselNext(modifier = $next)
            }
        }
        """.trimIndent()
    return when (title) {
        "API" ->
            body.replace(
                "\n    }\n}",
                (
                    "\n    }\n    Text { \"Slide \" + (state.settledPage + 1) + \" " +
                        "of \" + state.pageCount }\n}"
                ),
            )
        "With dot indicators" ->
            body.replace(
                "\n    }\n}",
                (
                    "\n    }\n    CarouselDots(state, Modifier.padding(top = " +
                        "12.dp))\n}"
                ),
            )
        "Plugins" ->
            body.replace(
                "val state = rememberPagerState { 5 }",
                "val state = rememberPagerState { 5 }\n" +
                    carouselAutoplayCode,
            )
                .replace("Modifier.width(width).height(", "Modifier.hoverable(hover).width(width).height(")
        "RTL" ->
            "var language by remember { mutableStateOf(0) }\n" +
                (
                    "val mobile = with(LocalDensity.current) { " +
                        "LocalWindowInfo.current.containerSize.width.toDp() } < " +
                        "640.dp\n"
                ) +
                (
                    "RtlPreview(language, listOf(\"Arabic (العربية)\", " +
                        "\"English\", \"Hebrew (עברית)\"), { language = it }) {\n"
                ) +
                (
                    (
                        "    Box(Modifier.fillMaxWidth().heightIn(min = if (mobile) " +
                            "272.dp else 464.dp), contentAlignment = Alignment.Center) {\n"
                    ) +
                        "    CompositionLocalProvider(LocalLayoutDirection provides if " +
                        "(language == 1) LayoutDirection.Ltr else LayoutDirection.Rtl) " +
                        "{\n"
                ) +
                body.replace(
                    "(page + 1).toString()",
                    (
                        "if (language == 0) \"٠١٢٣٤٥٦٧٨٩\"[page + 1].toString() else " +
                            "(page + 1).toString()"
                    ),
                )
                    .prependIndent("        ") + "\n    }\n    }\n}"
        else -> body
    }
}

private val carouselAutoplayCode: String
    get() =
        """
        val hover = remember { MutableInteractionSource() }
        val hovered by hover.collectIsHoveredAsState()
        val dragged by state.interactionSource.collectIsDraggedAsState()
        var playing by remember { mutableStateOf(true) }
        LaunchedEffect(hovered, dragged) { if (hovered || dragged) playing = false }
        val scope = rememberCoroutineScope()
        LaunchedEffect(state, playing) {
            if (playing) while (isActive) {
                delay(2000)
                if (state.pageCount > 1 && !state.isScrollInProgress) {
                    val target = if (state.canScrollForward) state.targetPage + 1 else 0
                    scope.launch { state.animateScrollToPage(target) }
                }
            }
        }
        """.trimIndent()
