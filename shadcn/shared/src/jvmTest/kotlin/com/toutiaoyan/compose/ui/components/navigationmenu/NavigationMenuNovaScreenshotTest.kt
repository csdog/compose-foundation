@file:OptIn(androidx.compose.foundation.style.ExperimentalFoundationStyleApi::class)

package com.toutiaoyan.compose.ui.components.navigationmenu

import androidx.compose.foundation.layout.*
import androidx.compose.runtime.*
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.sp
import com.toutiaoyan.compose.ui.components.text.TextColors
import androidx.compose.ui.Modifier
import androidx.compose.ui.test.*
import androidx.compose.ui.unit.dp
import com.toutiaoyan.compose.ui.ScreenshotTest
import com.toutiaoyan.compose.ui.components.text.Text
import com.toutiaoyan.compose.ui.tokens.StylePreset
import kotlin.test.*

class NavigationMenuNovaScreenshotTest : ScreenshotTest() {
    private fun novaChrome(
        dark: Boolean,
        open: Boolean,
    ) = snapshot(
        if (open) "navigation_menu_nova_open" else "navigation_menu_nova_idle",
        darkTheme = dark,
        stylePreset = StylePreset.Nova,
    ) {
        Box(Modifier.width(440.dp).height(if (open) 320.dp else 36.dp)) {
            NavigationMenu(defaultValue = if (open) "start" else null) {
                NavigationMenuList {
                    NavigationMenuItem("start") {
                        NavigationMenuTrigger("Getting started")
                        NavigationMenuContent {
                            Column(Modifier.width(384.dp)) {
                                listOf(
                                    "Introduction" to "Re-usable components built with Tailwind CSS.",
                                    "Installation" to "How to install dependencies and structure your app.",
                                    "Typography" to "Styles for headings, paragraphs, lists...etc",
                                ).forEach { (title, description) ->
                                    NavigationMenuLink(onClick = {}) {
                                        Column(verticalArrangement = Arrangement.spacedBy(4.dp)) {
                                            Text(
                                                textStyle =
                                                    TextStyle(
                                                        fontSize =
                                                            14.sp,
                                                        lineHeight =
                                                            14.sp,
                                                        fontWeight =
                                                            FontWeight.Medium,
                                                    ),
                                            ) { title }
                                            Text(
                                                maxLines =
                                                2,
                                                overflow =
                                                    TextOverflow.Ellipsis,
                                                textStyle =
                                                    TextColors.Muted,
                                            ) { description }
                                        }
                                    }
                                }
                            }
                        }
                    }
                    NavigationMenuItem("components") { NavigationMenuTrigger("Components") }
                    NavigationMenuItem("docs") {
                        NavigationMenuLink("Docs", onClick = {}, style = rememberNavMenuItemStyle(false))
                    }
                }
            }
        }
    }

    @Test fun nova_idle_light() = novaChrome(false, false)

    @Test fun nova_idle_dark() = novaChrome(true, false)

    @Test fun nova_open_light() = novaChrome(false, true)

    @Test fun nova_open_dark() = novaChrome(true, true)
}
