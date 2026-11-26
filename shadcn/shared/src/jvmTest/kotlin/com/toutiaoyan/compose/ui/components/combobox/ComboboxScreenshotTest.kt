@file:OptIn(androidx.compose.foundation.style.ExperimentalFoundationStyleApi::class)

package com.toutiaoyan.compose.ui.components.combobox

import com.toutiaoyan.compose.ui.ScreenshotTest
import kotlin.test.Test

/** The idle (closed) trigger only -- the open list reuses the same row rendering already proven by DropdownMenu/Command. */
class ComboboxScreenshotTest : ScreenshotTest() {
    private fun states(darkTheme: Boolean) {
        snapshot("combobox_idle", darkTheme = darkTheme) {
            Combobox(
                value = null,
                options = listOf("Next.js", "SvelteKit", "Nuxt.js"),
                onValueChange = {},
            ) {
                ComboboxTrigger()
                ComboboxContent()
            }
        }
    }

    @Test
    fun idle_light() = states(darkTheme = false)

    @Test
    fun idle_dark() = states(darkTheme = true)

    private fun chips(darkTheme: Boolean) {
        snapshot("combobox_chips", darkTheme = darkTheme) {
            Combobox(
                values = listOf("Next.js", "Remix"),
                options = listOf("Next.js", "SvelteKit", "Nuxt.js", "Remix", "Astro"),
                onValuesChange = {},
            ) {
                ComboboxChips {
                    ComboboxChip(value = "Next.js", label = "Next.js")
                    ComboboxChip(value = "Remix", label = "Remix")
                    ComboboxChipsInput(placeholder = "Add framework")
                }
                ComboboxContent()
            }
        }
    }

    @Test
    fun chips_light() = chips(darkTheme = false)

    @Test
    fun chips_dark() = chips(darkTheme = true)

    private fun clear(darkTheme: Boolean) {
        snapshot("combobox_clear", darkTheme = darkTheme) {
            Combobox(
                value = "Next.js",
                options = listOf("Next.js", "SvelteKit", "Nuxt.js"),
                onValueChange = {},
                onClear = {},
            ) {
                ComboboxTrigger(showClear = true)
                ComboboxContent()
            }
        }
    }

    @Test
    fun clear_light() = clear(darkTheme = false)

    @Test
    fun clear_dark() = clear(darkTheme = true)

    private fun noTrigger(darkTheme: Boolean) {
        snapshot("combobox_no_trigger", darkTheme = darkTheme) {
            Combobox(
                value = null,
                options = listOf("Next.js", "SvelteKit", "Nuxt.js"),
                onValueChange = {},
            ) {
                ComboboxTrigger(showTrigger = false)
                ComboboxContent()
            }
        }
    }

    @Test
    fun no_trigger_light() = noTrigger(darkTheme = false)

    @Test
    fun no_trigger_dark() = noTrigger(darkTheme = true)

    private fun autoHighlight(darkTheme: Boolean) {
        snapshot("combobox_auto_highlight", darkTheme = darkTheme) {
            Combobox(
                value = null,
                options = listOf("Next.js", "SvelteKit", "Nuxt.js", "Remix", "Astro"),
                onValueChange = {},
                autoHighlight = true,
                defaultQuery = "n",
                defaultOpen = true,
            ) {
                ComboboxTrigger()
                ComboboxContent()
            }
        }
    }

    @Test
    fun auto_highlight_light() = autoHighlight(darkTheme = false)

    @Test
    fun auto_highlight_dark() = autoHighlight(darkTheme = true)
}
