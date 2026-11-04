package com.toutiaoyan.compose.ui.components.questionnaire

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.width
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.toutiaoyan.compose.ui.ScreenshotTest
import com.toutiaoyan.compose.ui.tokens.StylePreset
import kotlin.test.Test

class QuestionnaireNovaScreenshotTest : ScreenshotTest() {
    private fun choices(dark: Boolean) {
        snapshot("questionnaire_nova_choices", darkTheme = dark, stylePreset = StylePreset.Nova) {
            Column(Modifier.width(400.dp), verticalArrangement = Arrangement.spacedBy(16.dp)) {
                Questionnaire(shortcuts = QuestionnaireShortcuts.Letters) {
                    QuestionnaireItem("radio", required = true) {
                        QuestionnaireChoices {
                            QuestionnaireChoice(
                                "a",
                                "Radio selected",
                                description =
                                    "Supporting text.",
                                defaultChecked =
                                true,
                            )
                            QuestionnaireChoice("b", "Radio unchecked")
                        }
                    }
                }
                Questionnaire(shortcuts = QuestionnaireShortcuts.Numbers) {
                    QuestionnaireItem("checkbox", multiple = true) {
                        QuestionnaireChoices {
                            QuestionnaireChoice("a", "Checkbox selected", defaultChecked = true)
                            QuestionnaireChoice("b", "Checkbox disabled", disabled = true)
                        }
                    }
                }
            }
        }
    }

    private fun invalid(dark: Boolean) {
        snapshot("questionnaire_nova_invalid", darkTheme = dark, stylePreset = StylePreset.Nova) {
            Questionnaire(Modifier.width(400.dp)) {
                QuestionnaireProgress()
                QuestionnaireItem("q", required = true, invalid = true) {
                    QuestionnaireTitle("Choose a direction")
                    QuestionnaireDescription("Choose one answer to continue.")
                    QuestionnaireChoices { QuestionnaireChoice("a", "A direction") }
                    QuestionnaireError()
                }
                QuestionnaireActions {
                    QuestionnairePrevious()
                    QuestionnaireSkip()
                    QuestionnaireSubmit()
                }
            }
        }
    }

    @Test fun choicesLight() = choices(false)

    @Test fun choicesDark() = choices(true)

    @Test fun invalidLight() = invalid(false)

    @Test fun invalidDark() = invalid(true)
}
