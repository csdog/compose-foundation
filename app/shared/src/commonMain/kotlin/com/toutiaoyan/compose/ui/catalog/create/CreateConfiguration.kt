package com.toutiaoyan.compose.ui.catalog.create

import com.toutiaoyan.compose.ui.tokens.Accent
import com.toutiaoyan.compose.ui.tokens.BaseColor
import com.toutiaoyan.compose.ui.tokens.StylePreset

internal val HEADING_FONTS = listOf("Inter", "Geist", "Geist Mono", "System", "Serif")
internal val BODY_FONTS = listOf("Inter", "Geist", "Geist Mono", "System", "Mono")
internal val ICON_LIBRARIES = listOf("Lucide", "Hugeicons", "Tabler", "Phosphor", "Remix Icon")
private const val DEFAULT_RADIUS = 10f
private const val SMALL_RADIUS = 7.2f
private const val LARGE_RADIUS = 14f
private const val FULL_RADIUS = 999f
private val radiusValues =
    mapOf(
        "Default" to DEFAULT_RADIUS,
        "None" to 0f,
        "Small" to SMALL_RADIUS,
        "Medium" to DEFAULT_RADIUS,
        "Large" to LARGE_RADIUS,
        "Full" to FULL_RADIUS,
    )
internal val RADII = radiusValues.keys.toList()
private const val PRESET_VERSION = "cf1"
internal val MENU_COLORS =
    listOf(
        "Default / Solid",
        "Default / Translucent",
        "Inverted / Solid",
        "Inverted / Translucent",
    )
internal val MENU_ACCENTS = listOf("Subtle", "Bold", "Primary", "Muted")
internal val COMPONENT_BASES = listOf("Base UI", "Radix", "React Aria")

internal data class CreateConfiguration(
    val style: StylePreset = StylePreset.Nova,
    val baseColor: BaseColor = BaseColor.Neutral,
    val accent: Accent = Accent.Base,
    val chartColor: String = baseColor.label,
    val headingFont: String = HEADING_FONTS.first(),
    val bodyFont: String = BODY_FONTS.first(),
    val iconLibrary: String = ICON_LIBRARIES.first(),
    val radius: String = RADII.first(),
    val menuColor: String = MENU_COLORS.first(),
    val menuAccent: String = MENU_ACCENTS.first(),
    val componentBase: String = COMPONENT_BASES.first(),
) {
    val radiusDp: Float get() = radiusValues.getValue(radius)

    fun changeBase(next: BaseColor): CreateConfiguration =
        copy(
            baseColor = next,
            chartColor = chartColor.takeUnless { it == baseColor.label } ?: next.label,
        )

    fun toPresetCode(): String =
        (listOf(PRESET_VERSION) + presetFields().map { (key, value) -> "$key=$value" }).joinToString("~")

    private fun presetFields(): Map<String, String> =
        mapOf(
            "style" to style.name,
            "baseColor" to baseColor.name,
            "accent" to accent.name,
            "chartColor" to chartColor,
            "headingFont" to headingFont,
            "bodyFont" to bodyFont,
            "iconLibrary" to iconLibrary,
            "radius" to radius,
            "menuColor" to menuColor,
            "menuAccent" to menuAccent,
            "componentBase" to componentBase,
        )

    companion object {
        fun shuffled(): CreateConfiguration {
            val base = BaseColor.entries.random()
            return CreateConfiguration(
                style = StylePreset.entries.random(),
                baseColor = base,
                accent = Accent.entries.random(),
                chartColor = chartColors(base).random(),
                headingFont = HEADING_FONTS.random(),
                bodyFont = BODY_FONTS.random(),
                iconLibrary = ICON_LIBRARIES.random(),
                radius = RADII.random(),
                menuColor = MENU_COLORS.random(),
                menuAccent = MENU_ACCENTS.random(),
                componentBase = COMPONENT_BASES.random(),
            )
        }

        fun fromPresetCode(input: String): CreateConfiguration? =
            runCatching {
                val parts = input.trim().removePrefix("--preset ").trim().split('~')
                require(parts.first() == PRESET_VERSION)
                val values = parts.drop(1).associate { it.substringBefore('=') to it.substringAfter('=', "") }
                require(values.size == parts.size - 1)
                val base = enumValueOf<BaseColor>(values.getValue("baseColor"))
                val preset =
                    CreateConfiguration(
                        style = enumValueOf(values.getValue("style")),
                        baseColor = base,
                        accent = enumValueOf(values.getValue("accent")),
                        chartColor = values.option("chartColor", chartColors(base)),
                        headingFont = values.option("headingFont", HEADING_FONTS),
                        bodyFont = values.option("bodyFont", BODY_FONTS),
                        iconLibrary = values.option("iconLibrary", ICON_LIBRARIES),
                        radius = values.option("radius", RADII),
                        menuColor = values.option("menuColor", MENU_COLORS),
                        menuAccent = values.option("menuAccent", MENU_ACCENTS),
                        componentBase = values.option("componentBase", COMPONENT_BASES),
                    )
                require(preset.presetFields().keys == values.keys)
                preset
            }.getOrNull()
    }
}

private fun Map<String, String>.option(
    key: String,
    choices: List<String>,
): String = getValue(key).also { require(it in choices) }

internal fun chartColors(base: BaseColor): List<String> =
    listOf(base.label) + Accent.entries.filter { it != Accent.Base }.map { it.label }

internal enum class CreateDialog {
    Commands,
    OpenPreset,
    GetCode,
}
