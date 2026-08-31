@file:OptIn(androidx.compose.foundation.style.ExperimentalFoundationStyleApi::class)

package com.toutiaoyan.compose.ui.components.card

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.width
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.unit.dp
import com.toutiaoyan.compose.ui.ScreenshotTest
import com.toutiaoyan.compose.ui.components.button.ButtonSize
import com.toutiaoyan.compose.ui.components.button.ButtonVariant
import kotlin.test.Test
import com.toutiaoyan.compose.ui.components.button.Button
import com.toutiaoyan.compose.ui.components.field.Field
import com.toutiaoyan.compose.ui.components.field.FieldGroup
import com.toutiaoyan.compose.ui.components.field.FieldLabel
import com.toutiaoyan.compose.ui.components.text.Text
import com.toutiaoyan.compose.ui.components.text.TextColors
import com.toutiaoyan.compose.ui.components.input.Input

class CardScreenshotTest : ScreenshotTest() {
    private fun allVariants(darkTheme: Boolean) {
        snapshot("card_variants", darkTheme = darkTheme) {
            Row(horizontalArrangement = Arrangement.spacedBy(16.dp)) {
                listOf(CardVariant.Default, CardVariant.Elevated, CardVariant.Filled).forEach { variant ->
                    Card(
                        modifier = Modifier.width(180.dp),
                        variant = variant,
                    ) {
                        CardHeader {
                            CardTitle { variant::class.simpleName ?: "?" }
                            CardDescription { "Card description" }
                        }
                        CardContent {
                            Text { "Card body content goes here." }
                        }
                        CardFooter {
                            Button("Action", onClick = {}, size = ButtonSize.Sm)
                        }
                    }
                }
            }
        }
    }

    private fun createAccount(darkTheme: Boolean) {
        snapshot("card_create_account", darkTheme = darkTheme) {
            Card(modifier = Modifier.width(320.dp)) {
                CardHeader {
                    CardTitle { "Create an account" }
                    CardDescription { "Enter your email below to create your account" }
                }
                CardContent {
                    FieldGroup {
                        Field {
                            FieldLabel("Email", required = true)
                            Input(value = "", onValueChange = {}, placeholder = "m@example.com")
                        }
                        Field {
                            FieldLabel("Password", required = true)
                            Input(value = "", onValueChange = {})
                        }
                    }
                }
                Column(modifier = Modifier.fillMaxWidth(), verticalArrangement = Arrangement.spacedBy(8.dp)) {
                    Button("Create account", onClick = {}, modifier = Modifier.fillMaxWidth())
                    Button(
                        "Sign up with Google",
                        onClick = {},
                        variant = ButtonVariant.Outline,
                        modifier = Modifier.fillMaxWidth(),
                    )
                }
            }
        }
    }

    @Test
    fun variants_light() = allVariants(darkTheme = false)

    @Test
    fun variants_dark() = allVariants(darkTheme = true)

    @Test
    fun create_account_light() = createAccount(darkTheme = false)

    @Test
    fun create_account_dark() = createAccount(darkTheme = true)

    private fun spacing(darkTheme: Boolean) {
        snapshot("card_spacing", darkTheme = darkTheme) {
            Card(spacing = 32.dp, modifier = Modifier.width(320.dp)) {
                CardHeader {
                    CardTitle { "Terms of Service" }
                    CardDescription { "Official --card-spacing overlay." }
                }
                CardContent {
                    Text { "Gap and inset share one token when spacing is set." }
                }
            }
        }
    }

    @Test
    fun spacing_light() = spacing(darkTheme = false)

    @Test
    fun spacing_dark() = spacing(darkTheme = true)

    /**
     * Regression coverage for a bug where nesting a `muted`/explicitly-`color`-ed [Text]
     * inside [Card] rendered it at full contrast instead: [CardVariant]'s Style sets an
     * ambient `contentColor`, which the Compose Foundation Style API force-applies to every
     * descendant [Text] that doesn't declare its own nearer override -- set
     * [TextColors.Muted] or `textStyle = TextStyle(color = …)` on Text to win over Card.
     */
    @Test
    fun mutedTextSurvivesCardNesting() {
        snapshot("card_muted_text_isolation", darkTheme = false) {
            Card {
                Text { "BODY" }
                Text(textStyle = TextColors.Muted) { "MUTED IN CARD" }
                Text(textStyle = TextStyle(color = Color.Red)) { "EXPLICIT COLOR IN CARD" }
            }
        }
    }
}
