package com.toutiaoyan.compose.ui.components.toast

import kotlinx.coroutines.CompletableDeferred
import kotlinx.coroutines.async
import kotlinx.coroutines.awaitCancellation
import kotlinx.coroutines.cancelAndJoin
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import kotlinx.coroutines.test.runCurrent
import kotlinx.coroutines.test.runTest
import kotlin.test.*

@OptIn(kotlinx.coroutines.ExperimentalCoroutinesApi::class)
class ToastManagerParityTest {
    @Test fun updateKeepsIdentityAndCannotResurrectDismissedMessages() {
        val state = ToastState()
        val id = state.show("Before")
        assertTrue(state.update(id) { it.copy(id = 999, title = "After") })
        assertEquals(id, state.toasts.single().id)
        assertEquals("After", state.toasts.single().title)
        assertEquals(1, state.toasts.single().revision)
        state.dismiss(id)
        assertFalse(state.update(id) { it.copy(title = "Resurrected") })
        assertTrue(state.toasts.isEmpty())
    }

    @Test fun updateFindsItsMessageAfterTheQueueShifts() {
        val state = ToastState()
        val first = state.show("First")
        val second = state.show("Second")
        val third = state.show("Third")

        assertTrue(
            state.update(second) {
                state.dismiss(first)
                it.copy(title = "Updated")
            },
        )
        assertEquals(listOf(second, third), state.toasts.map { it.id })
        assertEquals(listOf("Updated", "Third"), state.toasts.map { it.title })
    }

    @Test fun updateDiscardsTheResultIfTheTransformDismissesItsMessage() {
        val state = ToastState()
        val id = state.show("Before")
        val other = state.show("Other")

        assertFalse(
            state.update(id) {
                state.dismiss(id)
                it.copy(title = "Resurrected")
            },
        )
        assertEquals(other, state.toasts.single().id)
    }

    @Test fun updatePreservesANewerRevisionWrittenByTheTransform() {
        val state = ToastState()
        val id = state.show("Before")

        assertFalse(
            state.update(id) {
                state.update(id) { message -> message.copy(title = "Newer") }
                it.copy(title = "Stale")
            },
        )
        assertEquals("Newer", state.toasts.single().title)
        assertEquals(1, state.toasts.single().revision)
    }

    @Test fun promiseUpdatesOnePersistentLoadingMessageToSuccess() =
        runTest {
            val state = ToastState()
            val result =
                state.promise(loading = "Creating event…", success = { "$it created." }, error = { "Failed" }) {
                    assertEquals(ToastVariant.Loading, state.toasts.single().variant)
                    assertEquals(0L, state.toasts.single().durationMillis)
                    delay(2000)
                    "Event"
                }
            assertEquals("Event", result.getOrThrow())
            assertEquals(1, state.toasts.size)
            assertEquals(0L, state.toasts.single().id)
            assertEquals(ToastVariant.Success, state.toasts.single().variant)
            assertEquals("Event created.", state.toasts.single().title)
            assertEquals(5000L, state.toasts.single().durationMillis)
            assertEquals(1, state.toasts.single().revision)
        }

    @Test fun promiseFailureIsReportedWithoutAnUncaughtException() =
        runTest {
            val state = ToastState()
            val failure = IllegalStateException("Unavailable")
            val result =
                state.promise<String>("Loading", success = { it }, error = { "Could not create event." }) {
                    throw failure
                }
            assertSame(failure, result.exceptionOrNull())
            assertEquals(ToastVariant.Error, state.toasts.single().variant)
            assertEquals("Could not create event.", state.toasts.single().title)
        }

    @Test fun cancellationPropagatesAndRemovesPendingFeedback() =
        runTest {
            val state = ToastState()
            val job =
                launch {
                    state.promise<String>("Loading", success = { it }, error = { "Failed" }) { awaitCancellation() }
                }
            runCurrent()
            assertEquals(ToastVariant.Loading, state.toasts.single().variant)
            job.cancelAndJoin()
            assertTrue(job.isCancelled)
            assertTrue(state.toasts.isEmpty())
        }

    @Test fun errorFormatterFailureRemovesOnlyItsPendingFeedback() =
        runTest {
            val state = ToastState()
            val other = state.show("Other")
            val formatterFailure = IllegalArgumentException("Cannot format error")
            val thrown =
                assertFailsWith<IllegalArgumentException> {
                    state.promise<String>("Loading", success = { it }, error = { throw formatterFailure }) {
                        error("Request failed")
                    }
                }
            assertSame(formatterFailure, thrown)
            assertEquals(listOf(other), state.toasts.map { it.id })
        }

    @Test fun errorFormatterFailureKeepsFeedbackItAlreadyUpdated() =
        runTest {
            val state = ToastState()
            val formatterFailure = IllegalArgumentException("Cannot format error")
            assertFailsWith<IllegalArgumentException> {
                state.promise<String>(
                    "Loading",
                    success = { it },
                    error = {
                        state.update(state.toasts.single().id) {
                            it.copy(title = "Handled", variant = ToastVariant.Error)
                        }
                        throw formatterFailure
                    },
                ) { error("Request failed") }
            }
            assertEquals("Handled", state.toasts.single().title)
        }

    @Test fun closingLoadingFeedbackDoesNotRecreateItAfterCompletion() =
        runTest {
            val state = ToastState()
            val gate = CompletableDeferred<String>()
            val result =
                async { state.promise("Loading", success = { it }, error = { "Failed" }) { gate.await() } }
            runCurrent()
            state.close(state.toasts.single().id)
            gate.complete("Done")
            assertEquals("Done", result.await().getOrThrow())
            assertTrue(state.toasts.isEmpty())
        }
}
