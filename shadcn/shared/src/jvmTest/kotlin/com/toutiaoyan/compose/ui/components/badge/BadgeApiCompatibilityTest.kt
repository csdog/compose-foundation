package com.toutiaoyan.compose.ui.components.badge

import org.junit.Test
import kotlin.test.assertTrue

class BadgeApiCompatibilityTest {
    @Test
    fun extendedEntryPointsRemainAvailableWithoutInteractionSource() {
        // Check the emitted JVM methods, including Compose's trailing compiler parameters.
        // Existing compiled consumers link to these descriptors, not the new source overloads.
        val methods =
            Class.forName("com.toutiaoyan.compose.ui.components.badge.BadgeKt").declaredMethods
                .filter { it.name == "Badge" }
                .map { method -> method.parameterTypes.map { it.name } }
        val prefix =
            listOf(
                "androidx.compose.ui.Modifier",
                "com.toutiaoyan.compose.ui.components.badge.BadgeVariant",
                "kotlin.jvm.functions.Function0",
                "androidx.compose.foundation.style.Style",
                "java.lang.String",
                "boolean",
                "com.toutiaoyan.compose.ui.components.badge.BadgeIconPlacement",
            )
        val composerTail = listOf("androidx.compose.runtime.Composer", "int", "int")
        assertTrue(
            prefix + "kotlin.jvm.functions.Function2" + composerTail in methods,
            "Retain the extended slot Badge JVM descriptor without interactionSource",
        )
        assertTrue(
            listOf("java.lang.String") + prefix + composerTail in methods,
            "Retain the extended text Badge JVM descriptor without interactionSource",
        )
    }
}
