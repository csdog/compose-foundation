package com.toutiaoyan.compose.ui.catalog

import androidx.compose.runtime.Composable
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import compose_foundation.app.shared.generated.resources.Res
import compose_foundation.app.shared.generated.resources.calendar_cjk
import compose_foundation.app.shared.generated.resources.geist_mono_regular
import compose_foundation.app.shared.generated.resources.geist_bold
import compose_foundation.app.shared.generated.resources.geist_medium
import compose_foundation.app.shared.generated.resources.geist_regular
import compose_foundation.app.shared.generated.resources.geist_semibold
import org.jetbrains.compose.resources.Font

/** The documentation font; bundled locally so previews work without a font CDN. */
@Composable
fun catalogFontFamily(): FontFamily =
    FontFamily(
        Font(Res.font.geist_regular, FontWeight.Normal),
        Font(Res.font.geist_medium, FontWeight.Medium),
        Font(Res.font.geist_semibold, FontWeight.SemiBold),
        Font(Res.font.geist_bold, FontWeight.Bold),
    )

/** Explicit asset: Wasm cannot consistently resolve the platform Monospace alias. */
@Composable
fun catalogMonoFontFamily(): FontFamily = FontFamily(Font(Res.font.geist_mono_regular))

/** A small licensed date-glyph subset; arbitrary CJK content needs a full caller-supplied font. */
@Composable
fun catalogCalendarCjkFontFamily(): FontFamily = FontFamily(Font(Res.font.calendar_cjk))
