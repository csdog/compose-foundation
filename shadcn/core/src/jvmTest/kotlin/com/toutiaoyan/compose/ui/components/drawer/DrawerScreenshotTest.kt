@file:OptIn(androidx.compose.foundation.style.ExperimentalFoundationStyleApi::class)

package com.toutiaoyan.compose.ui.components.drawer

import androidx.compose.foundation.layout.padding
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.toutiaoyan.compose.ui.ScreenshotTest
import com.toutiaoyan.compose.ui.components.button.Button
import com.toutiaoyan.compose.ui.components.field.Field
import com.toutiaoyan.compose.ui.components.field.FieldGroup
import com.toutiaoyan.compose.ui.components.input.Input
import kotlin.test.Test

/**
 * Idle (undragged) states only, one per direction -- the swipe-to-dismiss gesture isn't
 * simulated live, matching this project's avoidance of continuous gesture simulation in
 * this test harness (see `shouldDismissDrawer`'s doc comment). That decision math is
 * unit tested directly in `DrawerTest` instead.
 */
class DrawerScreenshotTest : ScreenshotTest() {
    private fun states(
        darkTheme: Boolean,
        swipeDirection: DrawerSwipeDirection,
        suffix: String,
        showSwipeHandle: Boolean = false,
    ) {
        snapshot("drawer_$suffix", darkTheme = darkTheme) {
            Drawer(
                open = true,
                onOpenChange = {},
                swipeDirection = swipeDirection,
                showSwipeHandle = showSwipeHandle,
            ) {
                DrawerContent {
                    DrawerHeader(
                        "Edit profile",
                        description = "Make changes to your profile here.",
                    )
                }
            }
        }
    }

    private fun editProfile(darkTheme: Boolean) {
        snapshot("drawer_edit_profile", darkTheme = darkTheme) {
            Drawer(open = true, onOpenChange = {}) {
                DrawerContent {
                    DrawerHeader(
                        "Edit profile",
                        description = "Make changes to your profile here. Click save when you're done.",
                    )
                    FieldGroup(modifier = Modifier.padding(16.dp)) {
                        Field("Name") {
                            Input(value = "Pedro Duarte", onValueChange = {})
                        }
                        Field("Username") {
                            Input(value = "@peduarte", onValueChange = {})
                        }
                    }
                    DrawerFooter {
                        Button("Save changes")
                    }
                }
            }
        }
    }

    @Test
    fun bottom_light() =
        states(
            darkTheme = false,
            swipeDirection = DrawerSwipeDirection.Down,
            suffix = "bottom",
            showSwipeHandle = true,
        )

    @Test
    fun bottom_dark() =
        states(
            darkTheme = true,
            swipeDirection = DrawerSwipeDirection.Down,
            suffix = "bottom",
            showSwipeHandle = true,
        )

    @Test
    fun top_light() = states(darkTheme = false, swipeDirection = DrawerSwipeDirection.Up, suffix = "top")

    @Test
    fun end_light() = states(darkTheme = false, swipeDirection = DrawerSwipeDirection.Right, suffix = "end")

    @Test
    fun edit_profile_light() = editProfile(darkTheme = false)

    @Test
    fun edit_profile_dark() = editProfile(darkTheme = true)
}
