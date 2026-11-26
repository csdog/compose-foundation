package com.toutiaoyan.compose.ui.components.questionnaire

import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFalse
import kotlin.test.assertTrue

class QuestionnaireCollectionTest {
    @Test fun removedDefinitionDropsItsAnswerAndUncontrolledActiveStep() {
        val state = QuestionnaireController(null, null, {})
        val first = QuestionnaireItemDefinition("first")
        val last = QuestionnaireItemDefinition("last")
        state.applyItems(listOf(first, last))
        state.setInput("first", "removed draft")
        state.setInput("last", "keep this")

        state.applyItems(listOf(last))

        assertEquals("last", state.activeName)
        assertEquals(listOf("last"), state.enabledNames())
        assertEquals(mapOf("last" to listOf("keep this")), state.answers())
    }

    @Test fun shortcutKeysKeepTheQuestionAndChoiceNamesSeparate() {
        val state = QuestionnaireController(null, null, {})
        state.applyItems(
            listOf(
                QuestionnaireItemDefinition("one/two", choices = listOf(QuestionnaireChoiceDefinition("three"))),
                QuestionnaireItemDefinition(
                    "one",
                    choices = listOf(QuestionnaireChoiceDefinition("two/three", disabled = true)),
                ),
            ),
        )
        state.shortcuts = QuestionnaireShortcuts.Letters
        assertTrue(state.selectByShortcut("A"))
        assertEquals(listOf("three"), state.answers()["one/two"])
        state.goTo("one")
        assertFalse(state.selectByShortcut("A"))
    }

    @Test fun removedDefinitionChoiceIsNoLongerSelectedOrReachableByShortcut() {
        val state = QuestionnaireController(null, null, {})
        state.applyItems(
            listOf(
                QuestionnaireItemDefinition("q", choices = listOf(QuestionnaireChoiceDefinition("obsolete"))),
            ),
        )
        state.selectChoice("q", "obsolete")
        state.shortcuts = QuestionnaireShortcuts.Letters

        state.applyItems(listOf(QuestionnaireItemDefinition("q")))

        assertEquals(emptyList<String>(), state.answers()["q"])
        assertFalse(state.selectByShortcut("A"))
    }
}
