package com.toutiaoyan.compose.ui.catalog

import com.toutiaoyan.compose.ui.catalog.create.CreateConfiguration
import com.toutiaoyan.compose.ui.tokens.Accent
import com.toutiaoyan.compose.ui.tokens.BaseColor
import com.toutiaoyan.compose.ui.tokens.StylePreset
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertNull

class CreateConfigurationTest {
    private val customized =
        CreateConfiguration(
            style = StylePreset.Mira,
            baseColor = BaseColor.Zinc,
            accent = Accent.Blue,
            chartColor = Accent.Amber.label,
            headingFont = "Serif",
            bodyFont = "Geist Mono",
            iconLibrary = "Tabler",
            radius = "None",
            menuColor = "Inverted / Translucent",
            menuAccent = "Bold",
            componentBase = "React Aria",
        )

    @Test
    fun presetRestoresEveryChoice() {
        val code = customized.toPresetCode()
        assertEquals(customized, CreateConfiguration.fromPresetCode(code))
        assertEquals(customized, CreateConfiguration.fromPresetCode("  --preset $code  "))
    }

    @Test
    fun invalidOrIncompletePresetIsRejected() {
        assertNull(CreateConfiguration.fromPresetCode(""))
        assertNull(CreateConfiguration.fromPresetCode("b2D0wqNxT"))
        assertNull(CreateConfiguration.fromPresetCode(customized.toPresetCode().replace("Serif", "unsupported")))
        assertNull(CreateConfiguration.fromPresetCode(customized.toPresetCode().substringBeforeLast('~')))
    }

    @Test
    fun onlyBaseChartPaletteChangesWhenBaseChanges() {
        assertEquals(Accent.Amber.label, customized.changeBase(BaseColor.Neutral).chartColor)
        assertEquals(
            BaseColor.Neutral.label,
            customized.copy(chartColor = BaseColor.Zinc.label).changeBase(BaseColor.Neutral).chartColor,
        )
    }
}
