package com.toutiaoyan.compose.ui.catalog

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.width
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.toPixelMap
import androidx.compose.ui.graphics.Canvas
import androidx.compose.ui.graphics.ImageBitmap
import androidx.compose.ui.graphics.compositeOver
import kotlin.math.roundToInt
import androidx.compose.ui.test.*
import androidx.compose.ui.test.junit4.v2.createComposeRule
import androidx.compose.ui.unit.dp
import androidx.compose.ui.text.TextLayoutResult
import androidx.compose.ui.semantics.SemanticsActions
import com.toutiaoyan.compose.ui.catalog.docs.aspectRatioDoc
import com.toutiaoyan.compose.ui.catalog.docs.PreviewCodeSection
import com.toutiaoyan.compose.ui.components.overlay.OverlayHost
import com.toutiaoyan.compose.ui.theme.Theme
import com.toutiaoyan.compose.ui.tokens.StylePreset
import org.junit.Rule
import kotlin.math.abs
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertNull
import kotlin.test.assertTrue

class AspectRatioExamplesTest {
    @get:Rule val rule = createComposeRule()

    @Test fun officialPageCopyOrderAndRequiredRatioApiStayDocumented() {
        assertEquals(
            listOf("Default", "Square", "Portrait", "RTL"),
            aspectRatioDoc.examples.map { it.title },
        )
        assertNull(aspectRatioDoc.examplesHeading)
        assertEquals(
            "A square aspect ratio component using the `ratio = 1f / 1f` parameter. " +
                "This is useful for displaying images in a square format.",
            aspectRatioDoc.examples.single { it.title == "Square" }.descriptionMarkdown,
        )
        assertEquals(
            "A portrait aspect ratio component using the `ratio = 9f / 16f` parameter. " +
                "This is useful for displaying images in a portrait format.",
            aspectRatioDoc.examples.single { it.title == "Portrait" }.descriptionMarkdown,
        )
        assertEquals(
            "To enable RTL support in shadcn/ui, see the " +
                "[RTL configuration guide](https://ui.shadcn.com/docs/rtl).",
            aspectRatioDoc.examples.single { it.title == "RTL" }.descriptionMarkdown,
        )
        val aspectRatioApi = aspectRatioDoc.apiSections.single { it.title == "AspectRatio" }
        assertEquals(
            "Required",
            aspectRatioApi.properties.single { it.name == "ratio" }.defaultValue,
        )
        assertTrue(
            aspectRatioApi.descriptionMarkdown.contains(
                "this Kotlin API validates that the required ratio is finite and positive.",
            ),
        )
    }

    @Test fun rtlCaptionLineBoxAndPhotoPositionStayStableAcrossLanguagesAndThemes() {
        var dark by mutableStateOf(false)
        var width by mutableStateOf(640.dp)
        rule.setContent {
            Theme(
                preset = StylePreset.Nova,
                fontFamily = catalogFontFamily(),
                isDark = dark,
                minTouchTarget = null,
            ) {
                OverlayHost {
                    Box(Modifier.width(width)) { aspectRatioDoc.examples.single { it.title == "RTL" }.preview() }
                }
            }
        }

        fun check(caption: String): Float {
            val layouts = mutableListOf<TextLayoutResult>()
            val node = rule.onNodeWithText(caption)
            node.performSemanticsAction(SemanticsActions.GetTextLayoutResult) { it(layouts) }
            val result = layouts.single()
            assertEquals(1, result.lineCount)
            node.assertHeightIsEqualTo(20.dp)
            // CSS line boxes can be shorter than the font's natural metrics.
            // Compare the whole rendered glyph against an unclipped paragraph,
            // including any ink outside the 20dp semantic line box.
            val natural = rule.onNodeWithText(caption, useUnmergedTree = true)
            val textBounds = natural.fetchSemanticsNode().boundsInWindow
            val reference = ImageBitmap(result.size.width + 8, result.size.height + 8)
            val canvas = Canvas(reference)
            canvas.translate(4f, 4f)
            result.multiParagraph.paint(canvas, color = result.layoutInput.style.color)
            val expected = reference.toPixelMap()
            val root = rule.onAllNodes(isRoot()).onLast()
            val rootBounds = root.fetchSemanticsNode().boundsInWindow
            val actual = root.captureToImage().toPixelMap()
            val left = (textBounds.left - rootBounds.left).roundToInt() - 4
            val top = (textBounds.top - rootBounds.top).roundToInt() - 4
            val background = actual[left + 4, top + 4]
            var ink = 0
            for (y in 0 until expected.height) for (x in 0 until expected.width) {
                val sample = expected[x, y]
                if (sample.alpha < .5f) continue
                ink++
                val wanted = sample.compositeOver(background)
                val painted = actual[left + x, top + y]
                assertTrue(
                    abs(wanted.red - painted.red) < .04f &&
                        abs(wanted.green - painted.green) < .04f &&
                        abs(wanted.blue - painted.blue) < .04f,
                    "Caption glyph must remain unclipped: $caption, ($x,$y), expected $wanted, got $painted",
                )
            }
            assertTrue(ink > 20, "Caption must actually paint glyphs: $caption")
            return rule.onNodeWithContentDescription("Photo").getUnclippedBoundsInRoot().top.value
        }

        fun select(
            from: String,
            to: String,
        ) {
            rule.onNodeWithText(from).performClick()
            rule.onNodeWithText(to).performClick()
        }
        for ((w, night) in listOf(640.dp to false, 640.dp to true, 340.dp to true, 340.dp to false)) {
            rule.runOnIdle {
                width = w
                dark = night
            }
            val arabic = check("منظر طبيعي جميل")
            select("Arabic (العربية)", "English")
            assertEquals(arabic, check("Beautiful landscape"), .5f)
            select("English", "Hebrew (עברית)")
            assertEquals(arabic, check("נוף יפה"), .5f)
            select("Hebrew (עברית)", "Arabic (العربية)")
            assertEquals(arabic, check("منظر طبيعي جميل"), .5f)
        }
    }

    @Test fun previewReservesItsBorderBeforeApplyingImagePadding() {
        val example = aspectRatioDoc.examples.first()
        rule.setContent {
            Theme(preset = StylePreset.Nova, minTouchTarget = null) {
                Box(Modifier.width(342.dp)) {
                    PreviewCodeSection(
                        code = example.code,
                        previewPadding = example.previewPadding,
                        preview = example.preview,
                    )
                }
            }
        }
        // 390px phone: 24px page margins, 1px frame border, 40px preview padding.
        rule.onNodeWithContentDescription("Photo").assertWidthIsEqualTo(260.dp).assertHeightIsEqualTo(146.dp)
    }

    @Test fun allOfficialRatiosAreResponsiveAndImagesFillTheirFrame() {
        var width by mutableStateOf(640.dp)
        var title by mutableStateOf("Default")
        rule.setContent {
            Theme(preset = StylePreset.Nova, minTouchTarget = null) {
                Box(Modifier.width(width)) { aspectRatioDoc.examples.single { it.title == title }.preview() }
            }
        }

        fun check(
            w: Float,
            h: Float,
        ) {
            rule.onNodeWithContentDescription("Photo").assertWidthIsEqualTo(w.dp).assertHeightIsEqualTo(h.dp)
        }
        check(384f, 216f)
        rule.runOnIdle { width = 312.dp }
        check(232f, 131f)
        rule.runOnIdle { title = "Square" }
        check(192f, 192f)
        rule.runOnIdle { title = "Portrait" }
        check(160f, 284f)
        rule.runOnIdle { width = 200.dp }
        check(120f, 213f)
    }

    @Test fun imageIsActuallyGrayscaleAndDarkBrightnessUpdatesWithoutResize() {
        var dark by mutableStateOf(false)
        rule.setContent {
            Theme(preset = StylePreset.Nova, isDark = dark, minTouchTarget = null) {
                Box(Modifier.width(640.dp)) { aspectRatioDoc.examples.first().preview() }
            }
        }

        fun samples(): List<Float> {
            val p = rule.onNodeWithContentDescription("Photo").captureToImage().toPixelMap()
            return listOf(.25f, .5f, .75f).map { f ->
                val c = p[(p.width * f).toInt(), p.height / 2]
                assertEquals(c.red, c.green, .006f)
                assertEquals(c.red, c.blue, .006f)
                c.red
            }
        }
        val light = samples()
        // The source avatar's colors have similar luminance; compare its actual
        // center-line samples instead of assuming a high-contrast photograph.
        val source =
            javax.imageio.ImageIO.read(
                java.io.File("src/commonMain/composeResources/drawable/aspect_ratio_demo.png"),
            )
        listOf(.25f, .5f, .75f).zip(light).forEach { (f, actual) ->
            val rgb = source.getRGB((source.width * f).toInt(), source.height / 2)
            val expected =
                (
                    ((rgb shr 16) and 255) * .2126f +
                        ((rgb shr 8) and 255) * .7152f +
                        (rgb and 255) * .0722f
                ) / 255f
            assertEquals(expected, actual, .008f, "Must paint the source image after grayscale")
        }
        rule.runOnIdle { dark = true }
        val night = samples()
        light.zip(night).forEach { (l, d) -> assertEquals(l * .2f, d, .008f) }
        rule.runOnIdle { dark = false }
        samples().zip(light).forEach { (a, b) -> assertEquals(a, b, .006f) }
    }

    @Test fun rtlLanguageChangesCaptionWithoutChangingImageOrRatio() {
        rule.setContent {
            Theme(preset = StylePreset.Nova, minTouchTarget = null) {
                OverlayHost {
                    Box(Modifier.width(640.dp)) { aspectRatioDoc.examples.single { it.title == "RTL" }.preview() }
                }
            }
        }
        rule.onNodeWithText("منظر طبيعي جميل").assertIsDisplayed()
        rule.onNodeWithText("Arabic (العربية)").performClick()
        rule.onNodeWithText("English").performClick()
        rule.onNodeWithText("Beautiful landscape").assertIsDisplayed()
        rule.onNodeWithText("English").performClick()
        rule.onNodeWithText("Hebrew (עברית)").performClick()
        rule.onNodeWithText("נוף יפה").assertIsDisplayed()
        rule.onNodeWithContentDescription("Photo").assertWidthIsEqualTo(384.dp).assertHeightIsEqualTo(216.dp)
    }
}
