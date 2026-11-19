package com.toutiaoyan.compose.ui.catalog.create

import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.staticCompositionLocalOf
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.unit.dp
import com.toutiaoyan.compose.tailwind.core.LocalTwRadius
import com.toutiaoyan.compose.tailwind.core.TwRadiusScale
import com.toutiaoyan.compose.ui.catalog.catalogFontFamily
import com.toutiaoyan.compose.ui.catalog.catalogMonoFontFamily
import com.toutiaoyan.compose.ui.theme.Theme
import com.toutiaoyan.compose.ui.theme.currentTheme
import com.toutiaoyan.compose.ui.tokens.Radius
import compose_foundation.app.shared.generated.resources.Res
import compose_foundation.app.shared.generated.resources.inter
import org.jetbrains.compose.resources.Font

internal val LocalCreatePreviewConfiguration = staticCompositionLocalOf { CreateConfiguration() }

internal val LocalCreateHeadingStyle = staticCompositionLocalOf { TextStyle.Default }

@Composable
internal fun CreatePreviewTheme(
    configuration: CreateConfiguration,
    content: @Composable () -> Unit,
) {
    val parent = currentTheme
    val body = createFontFamily(configuration.bodyFont)
    val heading = createFontFamily(configuration.headingFont)
    Theme(
        preset = configuration.style,
        baseColor = configuration.baseColor,
        accent = configuration.accent,
        isDark = parent.isDark,
        baseRadius = Radius(configuration.radiusDp.dp),
        fontFamily = body,
        minTouchTarget = parent.minTouchTarget,
    ) {
        val headingStyle = LocalCreateHeadingStyle provides TextStyle(fontFamily = heading)
        val previewConfiguration = LocalCreatePreviewConfiguration provides configuration
        if (configuration.radius == "Default") {
            CompositionLocalProvider(headingStyle, previewConfiguration, content = content)
        } else {
            CompositionLocalProvider(
                headingStyle,
                previewConfiguration,
                LocalTwRadius provides TwRadiusScale.fromBaseRadius(configuration.radiusDp.dp),
                content = content,
            )
        }
    }
}

@Composable
private fun createFontFamily(name: String): FontFamily =
    when (name) {
        "Geist" -> catalogFontFamily()
        "Geist Mono" -> catalogMonoFontFamily()
        "Serif" -> FontFamily.Serif
        "Mono" -> FontFamily.Monospace
        "System" -> FontFamily.Default
        else -> FontFamily(Font(Res.font.inter))
    }
