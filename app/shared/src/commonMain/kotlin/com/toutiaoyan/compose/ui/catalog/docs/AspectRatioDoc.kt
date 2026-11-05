@file:OptIn(androidx.compose.foundation.style.ExperimentalFoundationStyleApi::class)

package com.toutiaoyan.compose.ui.catalog.docs

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.ColorFilter
import androidx.compose.ui.graphics.ColorMatrix
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalLayoutDirection
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.LineHeightStyle
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.LayoutDirection
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import compose_foundation.app.shared.generated.resources.Res
import compose_foundation.app.shared.generated.resources.aspect_ratio_demo
import org.jetbrains.compose.resources.painterResource
import com.toutiaoyan.compose.ui.components.aspectratio.AspectRatio
import com.toutiaoyan.compose.ui.components.text.Text
import com.toutiaoyan.compose.ui.theme.currentTheme

private val aspectImageFilterCode =
    """
    val brightness = if (currentTheme.isDark) 0.2f else 1f
    val r = .2126f * brightness
    val g = .7152f * brightness
    val b = .0722f * brightness
    val filter = ColorFilter.colorMatrix(ColorMatrix(floatArrayOf(
        r, g, b, 0f, 0f,
        r, g, b, 0f, 0f,
        r, g, b, 0f, 0f,
        0f, 0f, 0f, 1f, 0f,
    )))
    """.trimIndent()

private val aspectRatioUsage =
    """
    AspectRatio(ratio = 16f / 9f) {
        Image(
            painter = painterResource(Res.drawable.aspect_ratio_demo),
            contentDescription = "Photo",
            contentScale = ContentScale.Crop,
            modifier = Modifier.fillMaxSize().clip(RoundedCornerShape(10.dp)),
        )
    }
    """.trimIndent()

val aspectRatioDoc =
    ComponentDoc(
        id = "aspect-ratio",
        title = "Aspect Ratio",
        description = "Displays content within a desired ratio.",
        usageImportsCode = "import com.toutiaoyan.compose.ui.components.aspectratio.AspectRatio",
        usageCode = aspectRatioUsage,
        examplesHeading = null,
        apiSections =
            listOf(
                ComponentApiSection(
                    "AspectRatio",
                    (
                        "Displays caller-provided content within a width/height ratio. " +
                            "Modifier and Style control placement and appearance. Unlike " +
                            "the current web wrapper's required ratio, this Kotlin API " +
                            "retains its existing square default. Supply a finite positive " +
                            "ratio. Images, clipping, and accessibility descriptions " +
                            "belong to the content."
                    ),
                    listOf(
                        ApiProperty("ratio", "Float", "1f"),
                        ApiProperty("modifier", "Modifier", "Modifier"),
                        ApiProperty("style", "Style", "Style"),
                        ApiProperty("content", "@Composable () -> Unit", "Required"),
                    ),
                ),
            ),
        examples =
            listOf(
                aspectPhotoExample("Default", 16f / 9f, 384.dp, "16f / 9f"),
                aspectPhotoExample(
                    "Square",
                    1f,
                    192.dp,
                    "1f / 1f",
                    (
                        "A square aspect ratio component using the `ratio = 1f / 1f` " +
                            "parameter. This is useful for displaying images in a square " +
                            "format."
                    ),
                ),
                aspectPhotoExample(
                    "Portrait",
                    9f / 16f,
                    160.dp,
                    "9f / 16f",
                    (
                        "A portrait aspect ratio component using the `ratio = 9f / " +
                            "16f` parameter. This is useful for displaying images in a " +
                            "portrait format."
                    ),
                    384.dp,
                ),
                ComponentExample(
                    title = "RTL",
                    descriptionMarkdown = (
                        "To enable RTL support in shadcn/ui, see the [RTL configuration " +
                            "guide](https://ui.shadcn.com/docs/rtl)."
                    ),
                    previewPadding = 0.dp,
                    code =
                        aspectImageFilterCode + "\n\n" +
                            """
                            CompositionLocalProvider(LocalLayoutDirection provides LayoutDirection.Rtl) {
                                Column(Modifier.widthIn(max = 384.dp).fillMaxWidth()) {
                                    AspectRatio(ratio = 16f / 9f) {
                                        Image(
                                            painter = painterResource(Res.drawable.aspect_ratio_demo),
                                            contentDescription = "Photo",
                                            contentScale = ContentScale.Crop,
                                            colorFilter = filter,
                                            modifier = Modifier.fillMaxSize().clip(RoundedCornerShape(10.dp)),
                                        )
                                    }
                                    Spacer(Modifier.height(8.dp))
                                    Box(Modifier.fillMaxWidth().height(20.dp).semantics(mergeDescendants = true) {},
                                        contentAlignment = Alignment.Center) {
                                        Text(modifier = Modifier.fillMaxWidth()
                                                .wrapContentHeight(Alignment.CenterVertically, unbounded = true),
                                        textStyle = TextStyle(fontSize = 14.sp, lineHeight = 20.sp,
                                            textAlign = TextAlign.Center,
                                            lineHeightStyle = LineHeightStyle(
                                                alignment = LineHeightStyle.Alignment.Center,
                                                trim = LineHeightStyle.Trim.None,
                                            ))) { "منظر طبيعي جميل" }
                                    }
                                }
                            }
                            """.trimIndent(),
                    preview = { AspectRatioRtlPreview() },
                ),
            ),
    )

private fun aspectPhotoExample(
    title: String,
    ratio: Float,
    maxWidth: Dp,
    ratioCode: String,
    description: String? = null,
    height: Dp = 288.dp,
) = ComponentExample(
    title = title,
    descriptionMarkdown = description,
    previewPadding = 0.dp,
    minPreviewHeight = height,
    code =
        aspectImageFilterCode + "\n\n" +
            """
            AspectRatio(ratio = $ratioCode,
                modifier = Modifier.widthIn(max = ${maxWidth.value.toInt()}.dp).fillMaxWidth()) {
                Image(
                    painter = painterResource(Res.drawable.aspect_ratio_demo),
                    contentDescription = "Photo",
                    contentScale = ContentScale.Crop,
                    colorFilter = filter,
                    modifier = Modifier.fillMaxSize().clip(RoundedCornerShape(10.dp)),
                )
            }
            """.trimIndent(),
    preview = {
        Box(Modifier.fillMaxWidth().height(height).padding(horizontal = 40.dp), contentAlignment = Alignment.Center) {
            AspectRatioPhoto(ratio, Modifier.widthIn(max = maxWidth).fillMaxWidth())
        }
    },
)

@Composable
private fun AspectRatioPhoto(
    ratio: Float,
    modifier: Modifier = Modifier,
) {
    val colors = currentTheme.colors
    val brightness = if (currentTheme.isDark) 0.2f else 1f
    // CSS grayscale(1) coefficients; brightness scales RGB, not alpha.
    val filter =
        remember(brightness) {
            val r = .2126f * brightness
            val g = .7152f * brightness
            val b = .0722f * brightness
            ColorFilter.colorMatrix(
                ColorMatrix(
                    floatArrayOf(
                        r, g, b, 0f, 0f,
                        r, g, b, 0f, 0f,
                        r, g, b, 0f, 0f,
                        0f, 0f, 0f, 1f, 0f,
                    ),
                ),
            )
        }
    AspectRatio(ratio, modifier.clip(RoundedCornerShape(10.dp)).background(colors.muted)) {
        Image(
            painter = painterResource(Res.drawable.aspect_ratio_demo),
            contentDescription = "Photo",
            contentScale = ContentScale.Crop,
            colorFilter = filter,
            modifier = Modifier.fillMaxSize(),
        )
    }
}

@Composable
private fun AspectRatioRtlPreview() {
    var language by remember { mutableStateOf(0) }
    val labels = listOf("Arabic (العربية)", "English", "Hebrew (עברית)")
    val captions = listOf("منظر طبيعي جميل", "Beautiful landscape", "נוף יפה")
    RtlPreview(language, labels, { language = it }) {
        CompositionLocalProvider(
            LocalLayoutDirection provides if (language == 1) LayoutDirection.Ltr else LayoutDirection.Rtl,
        ) {
            Box(
                Modifier.fillMaxWidth().height(336.dp).padding(horizontal = 16.dp),
                contentAlignment = Alignment.Center,
            ) {
                Column(
                    Modifier.widthIn(max = 384.dp).fillMaxWidth(),
                    verticalArrangement = Arrangement.spacedBy(8.dp),
                ) {
                    AspectRatioPhoto(16f / 9f, Modifier.fillMaxWidth())
                    Box(
                        Modifier.fillMaxWidth().height(20.dp).semantics(mergeDescendants = true) {},
                        contentAlignment = Alignment.Center,
                    ) {
                        Text(
                            modifier =
                                Modifier.fillMaxWidth()
                                    .wrapContentHeight(Alignment.CenterVertically, unbounded = true),
                            textStyle =
                                TextStyle(
                                    fontSize = 14.sp,
                                    lineHeight = 20.sp,
                                    textAlign = TextAlign.Center,
                                    // Keep the CSS 20px line box independent of fallback
                                    // metrics, without clipping the naturally measured text.
                                    lineHeightStyle =
                                        LineHeightStyle(
                                            alignment = LineHeightStyle.Alignment.Center,
                                            trim = LineHeightStyle.Trim.None,
                                        ),
                                    color = currentTheme.colors.mutedForeground,
                                ),
                        ) { captions[language] }
                    }
                }
            }
        }
    }
}
