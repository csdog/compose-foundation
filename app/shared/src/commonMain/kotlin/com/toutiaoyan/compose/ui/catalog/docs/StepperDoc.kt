@file:OptIn(androidx.compose.foundation.style.ExperimentalFoundationStyleApi::class)

package com.toutiaoyan.compose.ui.catalog.docs

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.toutiaoyan.compose.tailwind.core.twRadius
import com.toutiaoyan.compose.tailwind.core.twSpacing
import com.toutiaoyan.compose.ui.components.button.Button
import com.toutiaoyan.compose.ui.components.button.ButtonVariant
import com.toutiaoyan.compose.ui.components.field.Field
import com.toutiaoyan.compose.ui.components.input.Input
import com.toutiaoyan.compose.ui.components.stepper.Stepper
import com.toutiaoyan.compose.ui.components.stepper.StepperItem
import com.toutiaoyan.compose.ui.components.stepper.StepperOrientation
import com.toutiaoyan.compose.ui.components.text.Text
import com.toutiaoyan.compose.ui.components.text.TextColors
import com.toutiaoyan.compose.ui.components.text.TextVariant
import com.toutiaoyan.compose.ui.theme.currentTheme

val stepperDoc =
    ComponentDoc(
        id = "stepper",
        title = "Stepper",
        description = "A numbered multi-step progress indicator for wizards, onboarding, and checkout flows.",
        usageCode =
            """
            import com.toutiaoyan.compose.ui.components.stepper.Stepper
            import com.toutiaoyan.compose.ui.components.stepper.StepperItem

            Stepper(currentStep = 0) {
                StepperItem("Details", "Enter the required details")
                StepperItem("Review", "Confirm your information")
                StepperItem("Done", "All set")
            }
            """.trimIndent(),
        examples =
            listOf(
                ComponentExample(
                    title = "Default",
                    code =
                        """
                        var step by remember { mutableStateOf(0) }
                        Column {
                            Stepper(currentStep = step) {
                                StepperItem("Details", "Enter the required details")
                                StepperItem("Review", "Confirm your information")
                                StepperItem("Done", "All set")
                            }
                            Row {
                                Button("Back", onClick = { step = (step - 1).coerceAtLeast(0) }, variant = ButtonVariant.Outline)
                                Button("Next", onClick = { step = (step + 1).coerceAtMost(2) })
                            }
                        }
                        """.trimIndent(),
                    preview = {
                        var step by remember { mutableStateOf(0) }
                        Column(modifier = Modifier.width(320.dp)) {
                            Stepper(currentStep = step) {
                                StepperItem("Details", "Enter the required details")
                                StepperItem("Review", "Confirm your information")
                                StepperItem("Done", "All set")
                            }
                            Row(
                                modifier = Modifier.width(320.dp),
                                horizontalArrangement = Arrangement.spacedBy(8.dp),
                            ) {
                                Button(
                                    "Back",
                                    onClick = { step = (step - 1).coerceAtLeast(0) },
                                    variant = ButtonVariant.Outline,
                                )
                                Button("Next", onClick = { step = (step + 1).coerceAtMost(2) })
                            }
                        }
                    },
                ),
                ComponentExample(
                    title = "Titles only",
                    code =
                        """
                        Stepper(currentStep = 1) {
                            StepperItem("Step 1")
                            StepperItem("Step 2")
                            StepperItem("Step 3")
                        }
                        """.trimIndent(),
                    preview = {
                        Stepper(currentStep = 1, modifier = Modifier.width(320.dp)) {
                            StepperItem("Step 1")
                            StepperItem("Step 2")
                            StepperItem("Step 3")
                        }
                    },
                ),
                ComponentExample(
                    title = "Compact",
                    code =
                        """
                        Stepper(currentStep = 1, showLabels = false) {
                            StepperItem("1")
                            StepperItem("2")
                            StepperItem("3")
                        }
                        """.trimIndent(),
                    preview = {
                        Stepper(
                            currentStep = 1,
                            showLabels = false,
                            modifier = Modifier.width(320.dp),
                        ) {
                            StepperItem("1")
                            StepperItem("2")
                            StepperItem("3")
                        }
                    },
                ),
                ComponentExample(
                    title = "Default show labels",
                    code =
                        """
                        Stepper(currentStep = 1, showLabels = true) {
                            StepperItem("Account", "Create an account")
                            StepperItem("Profile", "Set up your profile")
                            StepperItem("Complete", "Complete the setup")
                        }
                        """.trimIndent(),
                    preview = {
                        Stepper(
                            currentStep = 1,
                            showLabels = true,
                            modifier = Modifier.width(320.dp),
                        ) {
                            StepperItem("Account", "Create an account")
                            StepperItem("Profile", "Set up your profile")
                            StepperItem("Complete", "Complete the setup")
                        }
                    },
                ),
                ComponentExample(
                    title = "Vertical",
                    code =
                        """
                        Stepper(currentStep = 1, orientation = StepperOrientation.Vertical) {
                            StepperItem("Account", "Create an account")
                            StepperItem("Profile", "Set up your profile")
                            StepperItem("Complete", "Complete the setup")
                        }
                        """.trimIndent(),
                    preview = {
                        Stepper(
                            currentStep = 1,
                            orientation = StepperOrientation.Vertical,
                        ) {
                            StepperItem("Account", "Create an account")
                            StepperItem("Profile", "Set up your profile")
                            StepperItem("Complete", "Complete the setup")
                        }
                    },
                ),
                ComponentExample(
                    title = "Horizontal",
                    code =
                        """
                        Stepper(currentStep = 1, orientation = StepperOrientation.Horizontal) {
                            StepperItem("Account", "Create an account")
                            StepperItem("Profile", "Set up your profile")
                            StepperItem("Complete", "Complete the setup")
                        }
                        """.trimIndent(),
                    preview = {
                        Stepper(
                            currentStep = 1,
                            orientation = StepperOrientation.Horizontal,
                        ) {
                            StepperItem("Account", "Create an account")
                            StepperItem("Profile", "Set up your profile")
                            StepperItem("Complete", "Complete the setup")
                        }
                    },
                ),
                ComponentExample(
                    title = "Default orientation",
                    code =
                        """
                        Stepper(currentStep = 1, orientation = StepperOrientation.Horizontal) {
                            StepperItem("Account", "Create an account")
                            StepperItem("Profile", "Set up your profile")
                            StepperItem("Complete", "Complete the setup")
                        }
                        """.trimIndent(),
                    preview = {
                        Stepper(
                            currentStep = 1,
                            orientation = StepperOrientation.Horizontal,
                        ) {
                            StepperItem("Account", "Create an account")
                            StepperItem("Profile", "Set up your profile")
                            StepperItem("Complete", "Complete the setup")
                        }
                    },
                ),
                ComponentExample(
                    title = "Step content",
                    code =
                        """
                        var step by remember { mutableStateOf(0) }
                        val titles = listOf("Details", "Review", "Done")
                        Column {
                            Stepper(currentStep = step) {
                                StepperItem("Details", "Enter the required details for this step")
                                StepperItem("Review", "Confirm your information and choices")
                                StepperItem("Done", "All set. Review completed")
                            }
                            Box { Text { "${'$'}{titles[step]} content" } }
                            Row {
                                Button("Back", onClick = { step = (step - 1).coerceAtLeast(0) }, variant = ButtonVariant.Outline)
                                Button("Next", onClick = { step = (step + 1).coerceAtMost(titles.lastIndex) })
                            }
                        }
                        """.trimIndent(),
                    preview = {
                        var step by remember { mutableStateOf(0) }
                        val titles = listOf("Details", "Review", "Done")
                        Column(
                            modifier = Modifier.width(320.dp),
                            verticalArrangement = Arrangement.spacedBy(twSpacing.md),
                        ) {
                            Stepper(currentStep = step) {
                                StepperItem("Details", "Enter the required details for this step")
                                StepperItem("Review", "Confirm your information and choices")
                                StepperItem("Done", "All set. Review completed")
                            }
                            Box(
                                modifier =
                                    Modifier
                                        .fillMaxWidth()
                                        .height(64.dp)
                                        .background(currentTheme.colors.muted, RoundedCornerShape(twRadius.md))
                                        .padding(twSpacing.md),
                            ) {
                                Text(
                                    variant = TextVariant.BodySmall,
                                    textStyle = TextColors.Muted,
                                ) { "${titles[step]} content" }
                            }
                            Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                                Button(
                                    "Back",
                                    onClick = { step = (step - 1).coerceAtLeast(0) },
                                    variant = ButtonVariant.Outline,
                                )
                                Button("Next", onClick = { step = (step + 1).coerceAtMost(titles.lastIndex) })
                            }
                        }
                    },
                ),
                ComponentExample(
                    title = "Mid-flow action",
                    code =
                        """
                        var step by remember { mutableStateOf(1) }
                        Column {
                            Stepper(currentStep = step) {
                                StepperItem("Step 1")
                                StepperItem("Step 2")
                                StepperItem("Step 3")
                            }
                            Row {
                                Button("Back", onClick = { step = (step - 1).coerceAtLeast(0) }, variant = ButtonVariant.Outline)
                                Button("Complete Step", onClick = { }, variant = ButtonVariant.Secondary)
                                Button("Next", onClick = { step = (step + 1).coerceAtMost(2) })
                            }
                        }
                        """.trimIndent(),
                    preview = {
                        var step by remember { mutableStateOf(1) }
                        Column(
                            modifier = Modifier.width(320.dp),
                            verticalArrangement = Arrangement.spacedBy(twSpacing.md),
                        ) {
                            Stepper(currentStep = step) {
                                StepperItem("Step 1")
                                StepperItem("Step 2")
                                StepperItem("Step 3")
                            }
                            Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                                Button(
                                    "Back",
                                    onClick = { step = (step - 1).coerceAtLeast(0) },
                                    variant = ButtonVariant.Outline,
                                )
                                Button("Complete Step", onClick = { }, variant = ButtonVariant.Secondary)
                                Button("Next", onClick = { step = (step + 1).coerceAtMost(2) })
                            }
                        }
                    },
                ),
                ComponentExample(
                    title = "Form step",
                    code =
                        """
                        var firstName by remember { mutableStateOf("") }
                        var lastName by remember { mutableStateOf("") }
                        Column {
                            Stepper(currentStep = 0) {
                                StepperItem("Personal Info")
                                StepperItem("Contact Info")
                                StepperItem("Address")
                            }
                            Field("First Name") {
                                Input(value = firstName, onValueChange = { firstName = it })
                            }
                            Field("Last Name") {
                                Input(value = lastName, onValueChange = { lastName = it })
                            }
                            Button("Next", onClick = {})
                        }
                        """.trimIndent(),
                    preview = {
                        var firstName by remember { mutableStateOf("") }
                        var lastName by remember { mutableStateOf("") }
                        Column(
                            modifier = Modifier.width(320.dp),
                            verticalArrangement = Arrangement.spacedBy(twSpacing.md),
                        ) {
                            Stepper(currentStep = 0) {
                                StepperItem("Personal Info")
                                StepperItem("Contact Info")
                                StepperItem("Address")
                            }
                            Field("First Name") {
                                Input(
                                    value = firstName,
                                    onValueChange = { firstName = it },
                                    modifier = Modifier.fillMaxWidth(),
                                )
                            }
                            Field("Last Name") {
                                Input(
                                    value = lastName,
                                    onValueChange = { lastName = it },
                                    modifier = Modifier.fillMaxWidth(),
                                )
                            }
                            Button("Next", onClick = {})
                        }
                    },
                ),
                ComponentExample(
                    title = "Disabled",
                    code =
                        """
                        Stepper(currentStep = 0) {
                            StepperItem("Details", "Enter the required details")
                            StepperItem("Review", "Confirm your information", disabled = true)
                            StepperItem("Done", "All set")
                        }
                        """.trimIndent(),
                    preview = {
                        Stepper(currentStep = 0, modifier = Modifier.width(320.dp)) {
                            StepperItem("Details", "Enter the required details")
                            StepperItem("Review", "Confirm your information", disabled = true)
                            StepperItem("Done", "All set")
                        }
                    },
                ),
                ComponentExample(
                    title = "Default disabled",
                    code =
                        """
                        Stepper(currentStep = 0) {
                            StepperItem("Details", "Enter the required details")
                            StepperItem("Review", "Confirm your information", disabled = false)
                            StepperItem("Done", "All set")
                        }
                        """.trimIndent(),
                    preview = {
                        Stepper(currentStep = 0, modifier = Modifier.width(320.dp)) {
                            StepperItem("Details", "Enter the required details")
                            StepperItem("Review", "Confirm your information", disabled = false)
                            StepperItem("Done", "All set")
                        }
                    },
                ),
                ComponentExample(
                    title = "Item",
                    code =
                        """
                        Stepper(currentStep = 0) {
                            StepperItem("Details")
                        }
                        """.trimIndent(),
                    preview = {
                        Stepper(currentStep = 0, modifier = Modifier.width(320.dp)) {
                            StepperItem("Details")
                        }
                    },
                ),
                ComponentExample(
                    title = "Indicator",
                    code =
                        """
                        Stepper(currentStep = 0) {
                            StepperItem("Details")
                        }
                        """.trimIndent(),
                    preview = {
                        Stepper(currentStep = 0, modifier = Modifier.width(320.dp)) {
                            StepperItem("Details")
                        }
                    },
                ),
                ComponentExample(
                    title = "Title",
                    code =
                        """
                        Stepper(currentStep = 0) {
                            StepperItem("Details")
                        }
                        """.trimIndent(),
                    preview = {
                        Stepper(currentStep = 0, modifier = Modifier.width(320.dp)) {
                            StepperItem("Details")
                        }
                    },
                ),
                ComponentExample(
                    title = "Description",
                    code =
                        """
                        Stepper(currentStep = 0) {
                            StepperItem("Details", "Enter the required details")
                        }
                        """.trimIndent(),
                    preview = {
                        Stepper(currentStep = 0, modifier = Modifier.width(320.dp)) {
                            StepperItem("Details", "Enter the required details")
                        }
                    },
                ),
                ComponentExample(
                    title = "Separator",
                    code =
                        """
                        Stepper(currentStep = 0) {
                            StepperItem("Details")
                            StepperItem("Review")
                        }
                        """.trimIndent(),
                    preview = {
                        Stepper(currentStep = 0, modifier = Modifier.width(320.dp)) {
                            StepperItem("Details")
                            StepperItem("Review")
                        }
                    },
                ),
            ),
    )
