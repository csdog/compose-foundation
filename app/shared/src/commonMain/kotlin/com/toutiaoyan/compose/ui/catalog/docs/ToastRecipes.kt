@file:OptIn(androidx.compose.foundation.style.ExperimentalFoundationStyleApi::class)

package com.toutiaoyan.compose.ui.catalog.docs

import androidx.compose.foundation.layout.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
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

// region installation
@Composable
internal fun ToastRoot(
    modifier: Modifier = Modifier,
    content: @Composable (ToastState) -> Unit,
) {
    val state = rememberToastState()
    Box(modifier.fillMaxSize()) {
        content(state)
        Toaster(state, Modifier.align(Alignment.BottomEnd).padding(16.dp))
    }
}
// endregion installation

// region usage
internal fun showEventToast(state: ToastState): Long =
    state.show(
        title = "Event created",
        description = ToastEventDescription,
    )
// endregion usage

// region action
internal fun showUndoableEventToast(
    state: ToastState,
    description: String? = null,
): Long {
    var id = -1L
    id =
        state.show(
            title = "Event created",
            description = description,
            actionLabel = "Undo",
            onAction = { state.close(id) },
        )
    return id
}
// endregion action

// region default
@Composable
internal fun ToastDefaultRecipe(state: ToastState) {
    Button(
        "Show Toast",
        modifier = Modifier.testTag("toast-example"),
        variant = ButtonVariant.Outline,
        onClick = { showUndoableEventToast(state, ToastEventDescription) },
    )
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
        ComponentExample(
            title = "Types",
            descriptionMarkdown =
                "Set `variant` to render a status icon. The built-in renderer recognizes " +
                    "`Success`, `Info`, `Warning`, `Error`, and `Loading`.",
            code = toastTypesRecipeCode,
            preview = {
                ToastTypesRecipe(checkNotNull(com.toutiaoyan.compose.ui.catalog.LocalCatalogToastState.current))
            },
        ),
        ComponentExample(
            title = "Promise",
            descriptionMarkdown =
                "Use `state.promise` to update one toast as an asynchronous task moves through " +
                    "loading, success, and error states.",
            code = toastPromiseRecipeCode,
            preview = {
                ToastPromiseRecipe(checkNotNull(com.toutiaoyan.compose.ui.catalog.LocalCatalogToastState.current))
            },
        ),
    )

// region duration
@Composable
internal fun ToastDurationRecipe() {
    val state = rememberToastState()
    Column {
        Button("Show toast", onClick = {
            state.show(
                title = "Event created",
                description = "This stays for 8 seconds.",
                duration = 8000L,
            )
        })
        Toaster(state = state)
    }
}
// endregion duration

// region position
@Composable
internal fun ToastPositionRecipe() {
    val state = rememberToastState()
    Column {
        Button("Show toast", onClick = { state.show(title = "Event created") })
        Toaster(state = state, position = ToastPosition.TopEnd)
    }
}
// endregion position

// region closeButton
@Composable
internal fun ToastCloseButtonRecipe() {
    val state = rememberToastState()
    Column {
        Button("Show toast", onClick = { state.show(title = "Event created") })
        Toaster(state = state, closeButton = true)
    }
}
// endregion closeButton

internal val toastKotlinExamples =
    listOf(
        ComponentExample(
            title = "Kotlin extension: Duration",
            code = toastDurationRecipeCode,
            preview = { ToastDurationRecipe() },
        ),
        ComponentExample(
            title = "Kotlin extension: Position",
            code = toastPositionRecipeCode,
            preview = { ToastPositionRecipe() },
        ),
        ComponentExample(
            title = "Kotlin extension: Close button",
            code = toastCloseButtonRecipeCode,
            preview = { ToastCloseButtonRecipe() },
        ),
    )
