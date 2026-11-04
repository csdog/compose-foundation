@file:OptIn(androidx.compose.foundation.style.ExperimentalFoundationStyleApi::class)

package com.toutiaoyan.compose.ui.catalog.docs

import androidx.compose.foundation.layout.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.unit.dp
import com.toutiaoyan.compose.ui.components.button.Button
import com.toutiaoyan.compose.ui.components.button.ButtonVariant
import com.toutiaoyan.compose.ui.components.toast.*
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

// region shared
private const val ToastEventDescription = "Sunday, December 3 at 9:00 AM"
// endregion shared

// region default
@Composable
internal fun ToastDefaultRecipe(state: ToastState) {
    Button("Show Toast", variant = ButtonVariant.Outline, modifier = Modifier.testTag("toast-example"), onClick = {
        var id = -1L
        id =
            state.show(
                "Event created",
                description =
                ToastEventDescription,
                actionLabel =
                    "Undo",
                onAction =
                    { state.close(id) },
            )
    })
}
// endregion default

// region types
@Composable
internal fun ToastTypesRecipe(state: ToastState) {
    FlowRow(
        Modifier.testTag("toast-example"),
        horizontalArrangement = Arrangement.spacedBy(8.dp),
        verticalArrangement = Arrangement.spacedBy(8.dp),
    ) {
        Button("Default", variant = ButtonVariant.Outline, onClick = {
            state.show(description = "Event has been created.")
        })
        Button("Success", variant = ButtonVariant.Outline, onClick = {
            state.show(description = "Event has been created.", variant = ToastVariant.Success)
        })
        Button("Info", variant = ButtonVariant.Outline, onClick = {
            state.show(description = "Arrive 10 minutes before the event.", variant = ToastVariant.Info)
        })
        Button("Warning", variant = ButtonVariant.Outline, onClick = {
            state.show(description = "The event cannot start before 8:00 AM.", variant = ToastVariant.Warning)
        })
        Button("Error", variant = ButtonVariant.Outline, onClick = {
            state.show(
                description =
                    "The event could not be created.",
                variant =
                    ToastVariant.Error,
                priority =
                    ToastPriority.High,
            )
        })
    }
}
// endregion types

// region promise
@Composable
internal fun ToastPromiseRecipe(state: ToastState) {
    val scope = rememberCoroutineScope()
    Button("Create Event", variant = ButtonVariant.Outline, modifier = Modifier.testTag("toast-example"), onClick = {
        scope.launch {
            state.promise(
                loading = "Creating event…",
                success = { name: String -> "$name created." },
                error = { "Could not create event." },
            ) {
                delay(2000)
                "Event"
            }
        }
    })
}
// endregion promise

internal val toastOfficialExamples =
    listOf(
        ComponentExample("Default", code = toastDefaultRecipeCode, preview = {
            ToastDefaultRecipe(checkNotNull(com.toutiaoyan.compose.ui.catalog.LocalCatalogToastState.current))
        }),
        ComponentExample("Types", code = toastTypesRecipeCode, preview = {
            ToastTypesRecipe(checkNotNull(com.toutiaoyan.compose.ui.catalog.LocalCatalogToastState.current))
        }),
        ComponentExample("Promise", code = toastPromiseRecipeCode, preview = {
            ToastPromiseRecipe(checkNotNull(com.toutiaoyan.compose.ui.catalog.LocalCatalogToastState.current))
        }),
    )
