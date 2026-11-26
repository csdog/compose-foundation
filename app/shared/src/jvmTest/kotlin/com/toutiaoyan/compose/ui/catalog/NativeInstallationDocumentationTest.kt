package com.toutiaoyan.compose.ui.catalog

import com.toutiaoyan.compose.ui.catalog.docs.alertDoc
import com.toutiaoyan.compose.ui.catalog.docs.componentPageMarkdown
import com.toutiaoyan.compose.ui.catalog.docs.sourceInstallations
import kotlin.test.Test
import kotlin.test.assertContains
import kotlin.test.assertEquals
import kotlin.test.assertFalse
import kotlin.test.assertTrue

class NativeInstallationDocumentationTest {
    @Test
    fun exportedComponentIncludesTheNativeInstallCommands() {
        val markdown = componentPageMarkdown(alertDoc)
        assertContains(markdown, "### Jetpack Compose")
        assertContains(markdown, "/r/android/alert.json")
        assertContains(markdown, "### SwiftUI")
        assertContains(markdown, "/r/swiftui/alert.json")
    }

    @Test
    fun datePickerUsesActualRegistryItemsForEveryTarget() {
        val recipe = sourceInstallations("date-picker")
        assertEquals(3, recipe.size)
        recipe.forEach { installation ->
            assertContains(installation.code, "/calendar.json")
            assertContains(installation.code, "/popover.json")
            assertFalse(installation.code.contains("/date-picker.json"))

            val command = installation.code.substringAfter("npx shadcn@latest add")
            assertEquals(3, command.lines().dropLast(1).size)
            assertTrue(command.lines().dropLast(1).all { it.endsWith('\\') })
        }
    }
}
