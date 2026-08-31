@file:OptIn(androidx.compose.foundation.style.ExperimentalFoundationStyleApi::class)

package com.toutiaoyan.compose.ui.catalog.docs

import com.toutiaoyan.compose.ui.components.spinner.Spinner
import com.toutiaoyan.compose.ui.components.spinner.SpinnerSize

val spinnerDoc =
    ComponentDoc(
        id = "spinner",
        title = "Spinner",
        description = "A spinning loading indicator.",
        usageCode =
            """
            import com.toutiaoyan.compose.ui.components.spinner.Spinner

            Spinner()
            """.trimIndent(),
        examples =
            listOf(
                ComponentExample(
                    title = "Default",
                    code = "Spinner()",
                    preview = { Spinner() },
                ),
                ComponentExample(
                    title = "Default size",
                    code = "Spinner(size = SpinnerSize.Default)",
                    preview = { Spinner(size = SpinnerSize.Default) },
                ),
                ComponentExample(
                    title = "Large",
                    code = "Spinner(size = SpinnerSize.Lg)",
                    preview = { Spinner(size = SpinnerSize.Lg) },
                ),
                ComponentExample(
                    title = "Small",
                    code = "Spinner(size = SpinnerSize.Sm)",
                    preview = { Spinner(size = SpinnerSize.Sm) },
                ),
                ComponentExample(
                    title = "Accessible label",
                    code = """Spinner(label = "Saving")""",
                    preview = { Spinner(label = "Saving") },
                ),
                ComponentExample(
                    title = "Default label",
                    code = """Spinner(label = "Loading")""",
                    preview = { Spinner(label = "Loading") },
                ),
            ),
    )
