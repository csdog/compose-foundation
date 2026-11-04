package com.toutiaoyan.compose.ui.catalog.docs

internal val spinnerImports =
    """
    import androidx.compose.foundation.Canvas
    import androidx.compose.foundation.layout.*
    import androidx.compose.runtime.*
    import androidx.compose.ui.Alignment
    import androidx.compose.ui.Modifier
    import androidx.compose.ui.geometry.Offset
    import androidx.compose.ui.graphics.StrokeCap
    import androidx.compose.ui.graphics.isSpecified
    import androidx.compose.ui.platform.LocalLayoutDirection
    import androidx.compose.ui.text.TextStyle
    import androidx.compose.ui.unit.LayoutDirection
    import androidx.compose.ui.unit.dp
    import com.toutiaoyan.compose.ui.components.badge.*
    import com.toutiaoyan.compose.ui.components.button.*
    import com.toutiaoyan.compose.ui.components.empty.*
    import com.toutiaoyan.compose.ui.components.inputgroup.*
    import com.toutiaoyan.compose.ui.components.item.*
    import com.toutiaoyan.compose.ui.components.spinner.*
    import com.toutiaoyan.compose.ui.components.text.Text
    import com.toutiaoyan.compose.ui.theme.*
    import com.toutiaoyan.compose.tailwind.core.LocalUnitSpacing
    import com.toutiaoyan.compose.ui.tokens.Radius
    import kotlin.math.cos
    import kotlin.math.sin
    import androidx.compose.ui.text.font.FontFamily
    import androidx.compose.ui.text.font.FontWeight
    import compose_foundation.app.shared.generated.resources.*
    import org.jetbrains.compose.resources.Font
    """.trimIndent()

internal fun spinnerCode(name: String): String =
    when (name) {
        "default" ->
            """
            @Composable
            fun SpinnerDefaultRecipe(
                title: String = "Processing payment...",
                amount: String = "${'$'}100.00",
            ) {
                val inherited = currentTheme
                Theme(
                    preset = inherited.activePreset, baseColor = inherited.activeBaseColor,
                    accent = inherited.activeAccent, isDark = inherited.isDark, baseRadius = Radius(16.dp),
                    ring = inherited.ring, unitSpacing = LocalUnitSpacing.current,
                    minTouchTarget = inherited.minTouchTarget, fontFamily = inherited.fontFamily,
                ) {
                    Item(Modifier.widthIn(max = 320.dp).fillMaxWidth(), variant = ItemVariant.Muted) {
                        ItemMedia { Spinner() }
                        ItemContent { ItemTitle(title) }
                        ItemContent(fill = false) {
                            Text(textStyle = TextStyle(fontFeatureSettings = "tnum")) { amount }
                        }
                    }
                }
            }

            SpinnerDefaultRecipe()
            """.trimIndent()
        "customization" ->
            """
            @Composable
            fun SpinnerCustomizationRecipe() {
                val color = LocalContentColor.current.takeIf { it.isSpecified } ?: currentTheme.colors.foreground
                Spinner {
                    Canvas(Modifier.fillMaxSize()) {
                        val unit = size.minDimension / 24f
                        repeat(8) { index ->
                            val angle = index * kotlin.math.PI / 4

                            fun point(radius: Float) =
                                Offset(
                                    size.width / 2 + cos(angle).toFloat() * radius * unit,
                                    size.height / 2 + sin(angle).toFloat() * radius * unit,
                                )
                            drawLine(color, point(6f), point(10f), 2f * unit, StrokeCap.Round)
                        }
                    }
                }
            }

            SpinnerCustomizationRecipe()
            """.trimIndent()
        "size" ->
            """
            @Composable
            fun SpinnerSizeRecipe() {
                Row(horizontalArrangement = Arrangement.spacedBy(24.dp), verticalAlignment = Alignment.CenterVertically) {
                    listOf(12.dp, 16.dp, 24.dp, 32.dp).forEach { Spinner(Modifier.size(it)) }
                }
            }

            SpinnerSizeRecipe()
            """.trimIndent()
        "button" ->
            """
            @Composable
            fun SpinnerButtonRecipe() {
                Column(verticalArrangement = Arrangement.spacedBy(16.dp), horizontalAlignment = Alignment.CenterHorizontally) {
                    listOf(
                        ButtonVariant.Default to "Loading...",
                        ButtonVariant.Outline to "Please wait",
                        ButtonVariant.Secondary to "Processing",
                    ).forEach { (variant, text) ->
                        Button(
                            onClick = {},
                            disabled = true,
                            size = ButtonSize.Sm,
                            variant = variant,
                            iconPlacement = ButtonIconPlacement.Start,
                        ) {
                            Spinner()
                            Text { text }
                        }
                    }
                }
            }

            SpinnerButtonRecipe()
            """.trimIndent()
        "badge" ->
            """
            @Composable
            fun SpinnerBadgeRecipe() {
                Row(
                    Modifier.wrapContentWidth(unbounded = true),
                    horizontalArrangement = Arrangement.spacedBy(16.dp),
                    verticalAlignment = Alignment.CenterVertically,
                ) {
                    listOf(
                        BadgeVariant.Default to "Syncing",
                        BadgeVariant.Secondary to "Updating",
                        BadgeVariant.Outline to "Processing",
                    ).forEach { (variant, text) ->
                        Badge(variant = variant, iconPlacement = BadgeIconPlacement.Start) {
                            Spinner()
                            Text { text }
                        }
                    }
                }
            }

            SpinnerBadgeRecipe()
            """.trimIndent()
        "inputgroup" ->
            """
            @Composable
            fun SpinnerInputGroupRecipe() {
                Column(Modifier.widthIn(max = 448.dp).fillMaxWidth(), verticalArrangement = Arrangement.spacedBy(16.dp)) {
                    InputGroup {
                        InputGroupInput(value = "", disabled = true, placeholder = "Send a message...")
                        InputGroupAddon(InputGroupAddonAlign.InlineEnd) { Spinner() }
                    }
                    InputGroup {
                        InputGroupTextarea(value = "", onValueChange = {}, disabled = true, placeholder = "Send a message...")
                        InputGroupAddon(InputGroupAddonAlign.BlockEnd) {
                            Spinner()
                            Text { "Validating..." }
                            Spacer(Modifier.weight(1f))
                            InputGroupButton(onClick = {}, variant = ButtonVariant.Default, label = "Send") {
                                val color = LocalContentColor.current
                                Canvas(Modifier.size(16.dp)) {
                                    val unit = size.minDimension / 24f

                                    fun point(
                                        x: Float,
                                        y: Float,
                                    ) = Offset(x * unit, y * unit)
                                    drawLine(color, point(12f, 19f), point(12f, 5f), 2f * unit, StrokeCap.Round)
                                    drawLine(color, point(5f, 12f), point(12f, 5f), 2f * unit, StrokeCap.Round)
                                    drawLine(color, point(12f, 5f), point(19f, 12f), 2f * unit, StrokeCap.Round)
                                }
                            }
                        }
                    }
                }
            }

            SpinnerInputGroupRecipe()
            """.trimIndent()
        "empty" ->
            """
            @Composable
            fun SpinnerEmptyRecipe() {
                Empty(Modifier.fillMaxWidth()) {
                    EmptyHeader {
                        EmptyMedia(variant = EmptyMediaVariant.Icon) { Spinner() }
                        EmptyTitle("Processing your request")
                        EmptyDescription("Please wait while we process your request. Do not refresh the page.")
                    }
                    EmptyContent {
                        Button(onClick = {}, variant = ButtonVariant.Outline, size = ButtonSize.Sm) { Text { "Cancel" } }
                    }
                }
            }

            SpinnerEmptyRecipe()
            """.trimIndent()
        "rtl" ->
            """
            @Composable
            fun SpinnerDefaultRecipe(
                title: String = "Processing payment...",
                amount: String = "${'$'}100.00",
            ) {
                val inherited = currentTheme
                Theme(
                    preset = inherited.activePreset, baseColor = inherited.activeBaseColor,
                    accent = inherited.activeAccent, isDark = inherited.isDark, baseRadius = Radius(16.dp),
                    ring = inherited.ring, unitSpacing = LocalUnitSpacing.current,
                    minTouchTarget = inherited.minTouchTarget, fontFamily = inherited.fontFamily,
                ) {
                    Item(Modifier.widthIn(max = 320.dp).fillMaxWidth(), variant = ItemVariant.Muted) {
                        ItemMedia { Spinner() }
                        ItemContent { ItemTitle(title) }
                        ItemContent(fill = false) {
                            Text(textStyle = TextStyle(fontFeatureSettings = "tnum")) { amount }
                        }
                    }
                }
            }

            private val spinnerTranslations =
                listOf(
                    "Processing payment..." to "${'$'}100.00",
                    "جاري معالجة الدفع..." to "١٠٠.٠٠ دولار",
                    "מעבד תשלום..." to "${'$'}100.00",
                )

            @Composable
            fun SpinnerRtlRecipe(language: Int = 1) {
                val inherited = currentTheme
                Column(Modifier.fillMaxWidth()) {

                    // Use 0 for English, 1 for Arabic, or 2 for Hebrew.
                    Box(Modifier.fillMaxWidth().heightIn(min = 320.dp).padding(40.dp), contentAlignment = Alignment.Center) {
                        val font =
                            when (language) {
                                1 -> FontFamily(Font(Res.font.catalog_arabic_regular, FontWeight.Normal),
                                Font(Res.font.catalog_arabic_medium, FontWeight.Medium))
                                2 -> FontFamily(Font(Res.font.catalog_hebrew_regular, FontWeight.Normal),
                                Font(Res.font.catalog_hebrew_medium, FontWeight.Medium))
                                else -> inherited.fontFamily
                            }
                        CompositionLocalProvider(
                            LocalLayoutDirection provides if (language == 0) LayoutDirection.Ltr else LayoutDirection.Rtl,
                        ) {
                            Theme(
                                preset = inherited.activePreset, baseColor = inherited.activeBaseColor,
                                accent = inherited.activeAccent, isDark = inherited.isDark, baseRadius = Radius(16.dp),
                                ring = inherited.ring, unitSpacing = LocalUnitSpacing.current,
                                minTouchTarget = inherited.minTouchTarget, fontFamily = font,
                            ) {
                                val (title, amount) = spinnerTranslations[language]
                                SpinnerDefaultRecipe(title, amount)
                            }
                        }
                    }
                }
            }

            SpinnerRtlRecipe()
            """.trimIndent()
        else -> error("Unknown spinner recipe: $name")
    }
