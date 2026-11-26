package com.toutiaoyan.compose.ui.components.questionnaire

import kotlin.test.*

class QuestionnaireControllerParityTest {
    private fun question(
        name: String = "q",
        required: Boolean = false,
        multiple: Boolean = false,
    ) = QuestionnaireItemDefinition(
        name,
        required,
        multiple,
        choices =
            listOf(
                QuestionnaireChoiceDefinition("a"),
                QuestionnaireChoiceDefinition("b"),
            ),
        input = QuestionnaireInputDefinition(),
    )

    private fun controller(
        items: List<QuestionnaireItemDefinition>,
        submit: (Map<String, List<String>>) -> Unit = {},
    ) = QuestionnaireController(null, null, submit).apply { applyItems(items) }

    @Test fun aSingleItemSelectsEitherFreeformOrFixedAnswerAndKeepsTheDraft() {
        val state = controller(listOf(question()))
        state.setInput("q", "My draft")
        assertEquals(listOf("My draft"), state.answers()["q"])
        state.selectChoice("q", "a")
        assertEquals(listOf("a"), state.answers()["q"])
        assertEquals("My draft", state.inputs["q"])
        state.setInput("q", "Revised draft")
        assertFalse(state.isSelected("q", "a"))
        assertEquals(listOf("Revised draft"), state.answers()["q"])
    }

    @Test fun multiplePayloadUsesCollectionOrderAndAllowsTheFreeformAnswer() {
        val state = controller(listOf(question(multiple = true)))
        state.selectChoice("q", "b")
        state.selectChoice("q", "a")
        state.setInput("q", "other")
        assertEquals(listOf("a", "b", "other"), state.answers()["q"])
    }

    @Test fun skippingTheFinalOptionalQuestionSubmitsWithoutDiscardingItsDraft() {
        var result: Map<String, List<String>>? = null
        val state = controller(listOf(question())) { result = it }
        state.setInput("q", "draft")
        assertTrue(state.canSkip)
        state.skip()
        assertEquals(emptyList(), result?.get("q"))
        assertEquals("draft", state.inputs["q"])
        assertEquals(QuestionnaireItemStatus.Skipped, state.itemStatus("q"))
        assertFalse(state.hasAnswer("q"))
    }

    @Test fun submitReturnsToTheFirstInvalidItemEvenAfterAControlledJump() {
        var submitted = 0
        val state = controller(listOf(question("first", true), question("last", true))) { submitted++ }
        state.goTo("last")
        state.selectChoice("last", "b")
        state.submit()
        assertEquals(0, submitted)
        assertEquals("first", state.activeName)
        assertEquals("Choose an answer to continue.", state.errorMessage("first"))
        state.selectChoice("first", "a")
        state.goTo("last")
        state.submit()
        assertEquals(1, submitted)
    }

    @Test fun aRejectedControlledInvalidJumpDoesNotForceTheExternalItem() {
        val proposed = mutableListOf<String>()
        val state = controller(listOf(question("first", true), question("last", true)))
        state.externalValue = "last"
        state.onValueChange = { proposed += it }
        state.selectChoice("last", "b")
        state.submit()
        assertEquals(listOf("first"), proposed)
        assertEquals("last", state.activeName)
    }

    @Test fun optionalInvalidAndCustomRequiredErrorsUseTheirOwnMessages() {
        val state = controller(listOf(question()))
        state.register("q", false, false, null, false, invalid = true)
        state.submit()
        assertEquals("Choose an answer or skip this question.", state.errorMessage("q"))
        state.register("q", true, false, "Custom required error", false)
        state.submit()
        assertEquals("Custom required error", state.errorMessage("q"))
    }

    @Test fun disabledNavigationAndSubmissionAreInert() {
        var submitted = 0
        val state = controller(listOf(question("first"), question("last"))) { submitted++ }
        state.disabled = true
        state.next()
        state.skip()
        assertEquals("first", state.activeName)
        state.goTo("last")
        state.previous()
        state.submit()
        assertEquals("last", state.activeName)
        assertEquals(0, submitted)
    }

    @Test fun firstSingleDefaultWinsWhileOtherDefaultInputTextRemainsAsADraft() {
        val state = controller(listOf(question()))
        state.registerChoice("q", "a", true, defaultChecked = true)
        state.registerChoice("q", "b", true, defaultChecked = true)
        state.applyInputDefault("q", "draft")
        assertEquals(listOf("a"), state.answers()["q"])
        assertEquals("draft", state.inputs["q"])
    }

    @Test fun optionalUnansweredRequiresAnExplicitSkip() {
        val state = controller(listOf(question("first"), question("last")))
        state.next()
        assertEquals("first", state.activeName)
        assertEquals("Choose an answer or skip this question.", state.errorMessage("first"))
        state.skip()
        assertEquals("last", state.activeName)
        assertEquals(QuestionnaireItemStatus.Skipped, state.itemStatus("first"))
    }

    @Test fun inputShortcutIncludesTheEnabledFreeformControlAndFocusesFixedAnswers() {
        val state = controller(listOf(question()))
        state.shortcuts = QuestionnaireShortcuts.Letters
        state.registerInput("q")
        assertTrue(state.selectByShortcut("C"))
        assertFalse(state.hasAnswer("q"))
        assertTrue(state.selectByShortcut("A"))
        assertEquals("a", state.pendingAnswerFocus)
        assertEquals(listOf("a"), state.answers()["q"])
        state.registerInput("q", enabled = false)
        assertFalse(state.selectByShortcut("C"))
    }
}
