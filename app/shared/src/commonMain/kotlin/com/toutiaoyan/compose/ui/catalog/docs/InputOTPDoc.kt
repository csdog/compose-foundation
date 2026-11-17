@file:OptIn(androidx.compose.foundation.style.ExperimentalFoundationStyleApi::class)

package com.toutiaoyan.compose.ui.catalog.docs

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.style.Style
import androidx.compose.foundation.style.contentPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.unit.sp
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.focus.focusProperties
import androidx.compose.ui.focus.focusRequester
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.platform.LocalLayoutDirection
import androidx.compose.ui.unit.LayoutDirection
import androidx.compose.ui.unit.dp
import com.composables.icons.lucide.Lucide
import com.composables.icons.lucide.RefreshCw
import com.toutiaoyan.compose.tailwind.core.LocalUnitSpacing
import com.toutiaoyan.compose.ui.catalog.catalogArabicFontFamily
import com.toutiaoyan.compose.ui.catalog.catalogHebrewFontFamily
import com.toutiaoyan.compose.ui.components.button.Button
import com.toutiaoyan.compose.ui.components.button.ButtonSize
import com.toutiaoyan.compose.ui.components.button.ButtonVariant
import com.toutiaoyan.compose.ui.components.card.Card
import com.toutiaoyan.compose.ui.components.card.CardContent
import com.toutiaoyan.compose.ui.components.card.CardFooter
import com.toutiaoyan.compose.ui.components.card.CardHeader
import com.toutiaoyan.compose.ui.components.field.Field
import com.toutiaoyan.compose.ui.components.field.FieldDescription
import com.toutiaoyan.compose.ui.components.field.FieldLabel
import com.toutiaoyan.compose.ui.components.icon.Icon
import com.toutiaoyan.compose.ui.components.inputotp.InputOTP
import com.toutiaoyan.compose.ui.components.inputotp.InputOTPGroup
import com.toutiaoyan.compose.ui.components.inputotp.InputOTPSeparator
import com.toutiaoyan.compose.ui.components.inputotp.InputOTPSlot
import com.toutiaoyan.compose.ui.components.text.Text
import com.toutiaoyan.compose.ui.components.text.TextColors
import com.toutiaoyan.compose.ui.theme.Theme
import com.toutiaoyan.compose.ui.theme.currentTheme

/**
 * Current official page (revision 8a35366):
 * demo, About, Installation, Usage, Composition, Pattern, Separator, Disabled, Controlled,
 * Invalid, Four Digits, Alphanumeric, Form, RTL, API Reference.
 *
 * The previous local page was an API dump (Default disabled, Default pattern, Masked,
 * Default masked, Text type, Default type, Complete, Read only, Default read only,
 * Max length, Default max length, Slot, Group, Separator); this round replaces it with
 * the official page structure.
 */
val inputOTPDoc =
    ComponentDoc(
        id = "input-otp",
        title = "Input OTP",
        description = "Accessible one-time password component with copy-paste functionality.",
        usageImportsCode =
            """
            import com.toutiaoyan.compose.ui.components.inputotp.InputOTP
            import com.toutiaoyan.compose.ui.components.inputotp.InputOTPGroup
            import com.toutiaoyan.compose.ui.components.inputotp.InputOTPSeparator
            import com.toutiaoyan.compose.ui.components.inputotp.InputOTPSlot
            """.trimIndent(),
        usageCode =
            """
            var code by remember { mutableStateOf("") }
            InputOTP(value = code, onValueChange = { code = it }, length = 6) {
                InputOTPGroup {
                    InputOTPSlot(0)
                    InputOTPSlot(1)
                    InputOTPSlot(2)
                }
                InputOTPSeparator()
                InputOTPGroup {
                    InputOTPSlot(3)
                    InputOTPSlot(4)
                    InputOTPSlot(5)
                }
            }
            """.trimIndent(),
        compositionCode =
            """
            InputOTP
            ├── InputOTPGroup
            │   ├── InputOTPSlot
            │   ├── InputOTPSlot
            │   └── InputOTPSlot
            ├── InputOTPSeparator
            ├── InputOTPGroup
            │   ├── InputOTPSlot
            │   ├── InputOTPSlot
            │   └── InputOTPSlot
            ├── InputOTPSeparator
            └── InputOTPGroup
                ├── InputOTPSlot
                └── InputOTPSlot
            """.trimIndent(),
        compositionMarkdown = "Use the following composition to build an `InputOTP`:",
        introductionSections =
            listOf(
                ComponentGuideSection(
                    title = "About",
                    blocks =
                        listOf(
                            ComponentGuideBlock.Paragraph(
                                "Input OTP is built on top of " +
                                    "[input-otp](https://github.com/guilhermerodz/input-otp) " +
                                    "by [@guilherme_rodz](https://twitter.com/guilherme_rodz). " +
                                    "This Kotlin port keeps the same slot composition and " +
                                    "paste/backspace behavior with an invisible text field.",
                            ),
                        ),
                ),
            ),
        examplesHeading = null,
        examples =
            listOf(
                ComponentExample(
                    title = "Default",
                    code =
                        """
                        var code by remember { mutableStateOf("123456") }
                        InputOTP(value = code, onValueChange = { code = it }, length = 6) {
                            InputOTPGroup {
                                InputOTPSlot(0)
                                InputOTPSlot(1)
                                InputOTPSlot(2)
                                InputOTPSlot(3)
                                InputOTPSlot(4)
                                InputOTPSlot(5)
                            }
                        }
                        """.trimIndent(),
                    preview = {
                        var code by remember { mutableStateOf("123456") }
                        InputOTP(value = code, onValueChange = { code = it }, length = 6) {
                            InputOTPGroup {
                                InputOTPSlot(0)
                                InputOTPSlot(1)
                                InputOTPSlot(2)
                                InputOTPSlot(3)
                                InputOTPSlot(4)
                                InputOTPSlot(5)
                            }
                        }
                    },
                ),
                ComponentExample(
                    title = "Pattern",
                    descriptionMarkdown = "Use the `pattern` prop to define a custom pattern for the OTP input.",
                    code =
                        """
                        import com.toutiaoyan.compose.ui.components.inputotp.*

                        var code by remember { mutableStateOf("") }
                        Column(Modifier.widthIn(max = 384.dp), horizontalAlignment = Alignment.Start) {
                            Field {
                                FieldLabel("Digits Only")
                                InputOTP(
                                    value = code,
                                    onValueChange = { code = it },
                                    length = 6,
                                    pattern = Regex("[0-9]"),
                                ) {
                                    InputOTPGroup {
                                        InputOTPSlot(0)
                                        InputOTPSlot(1)
                                        InputOTPSlot(2)
                                        InputOTPSlot(3)
                                        InputOTPSlot(4)
                                        InputOTPSlot(5)
                                    }
                                }
                            }
                        }
                        """.trimIndent(),
                    preview = {
                        var code by remember { mutableStateOf("") }
                        Column(Modifier.widthIn(max = 384.dp), horizontalAlignment = Alignment.Start) {
                            Field {
                                FieldLabel("Digits Only")
                                InputOTP(
                                    value = code,
                                    onValueChange = { code = it },
                                    length = 6,
                                    pattern = Regex("[0-9]"),
                                ) {
                                    InputOTPGroup {
                                        InputOTPSlot(0)
                                        InputOTPSlot(1)
                                        InputOTPSlot(2)
                                        InputOTPSlot(3)
                                        InputOTPSlot(4)
                                        InputOTPSlot(5)
                                    }
                                }
                            }
                        }
                    },
                ),
                ComponentExample(
                    title = "Separator",
                    descriptionMarkdown =
                        "Use the `InputOTPSeparator` component to add a separator between " +
                            "input groups.",
                    code =
                        """
                        var code by remember { mutableStateOf("") }
                        InputOTP(value = code, onValueChange = { code = it }, length = 6) {
                            InputOTPGroup { InputOTPSlot(0); InputOTPSlot(1) }
                            InputOTPSeparator()
                            InputOTPGroup { InputOTPSlot(2); InputOTPSlot(3) }
                            InputOTPSeparator()
                            InputOTPGroup { InputOTPSlot(4); InputOTPSlot(5) }
                        }
                        """.trimIndent(),
                    preview = {
                        var code by remember { mutableStateOf("") }
                        InputOTP(value = code, onValueChange = { code = it }, length = 6) {
                            InputOTPGroup {
                                InputOTPSlot(0)
                                InputOTPSlot(1)
                            }
                            InputOTPSeparator()
                            InputOTPGroup {
                                InputOTPSlot(2)
                                InputOTPSlot(3)
                            }
                            InputOTPSeparator()
                            InputOTPGroup {
                                InputOTPSlot(4)
                                InputOTPSlot(5)
                            }
                        }
                    },
                ),
                ComponentExample(
                    title = "Disabled",
                    descriptionMarkdown = "Use the `disabled` prop to disable the input.",
                    code =
                        """
                        InputOTP(value = "123456", onValueChange = {}, length = 6, disabled = true) {
                            InputOTPGroup { InputOTPSlot(0); InputOTPSlot(1); InputOTPSlot(2) }
                            InputOTPSeparator()
                            InputOTPGroup { InputOTPSlot(3); InputOTPSlot(4); InputOTPSlot(5) }
                        }
                        """.trimIndent(),
                    preview = {
                        InputOTP(value = "123456", onValueChange = {}, length = 6, disabled = true) {
                            InputOTPGroup {
                                InputOTPSlot(0)
                                InputOTPSlot(1)
                                InputOTPSlot(2)
                            }
                            InputOTPSeparator()
                            InputOTPGroup {
                                InputOTPSlot(3)
                                InputOTPSlot(4)
                                InputOTPSlot(5)
                            }
                        }
                    },
                ),
                ComponentExample(
                    title = "Controlled",
                    descriptionMarkdown = "Use the `value` and `onValueChange` props to control the input value.",
                    code =
                        """
                        var value by remember { mutableStateOf("") }
                        Column(verticalArrangement = Arrangement.spacedBy(8.dp), horizontalAlignment = Alignment.CenterHorizontally) {
                            InputOTP(value = value, onValueChange = { value = it }, length = 6) {
                                InputOTPGroup {
                                    InputOTPSlot(0)
                                    InputOTPSlot(1)
                                    InputOTPSlot(2)
                                    InputOTPSlot(3)
                                    InputOTPSlot(4)
                                    InputOTPSlot(5)
                                }
                            }
                            Text {
                                if (value.isEmpty()) "Enter your one-time password." else "You entered: ${'$'}value"
                            }
                        }
                        """.trimIndent(),
                    preview = {
                        var value by remember { mutableStateOf("") }
                        Column(
                            verticalArrangement = Arrangement.spacedBy(8.dp),
                            horizontalAlignment = Alignment.CenterHorizontally,
                        ) {
                            InputOTP(value = value, onValueChange = { value = it }, length = 6) {
                                InputOTPGroup {
                                    InputOTPSlot(0)
                                    InputOTPSlot(1)
                                    InputOTPSlot(2)
                                    InputOTPSlot(3)
                                    InputOTPSlot(4)
                                    InputOTPSlot(5)
                                }
                            }
                            Text {
                                if (value.isEmpty()) "Enter your one-time password." else "You entered: $value"
                            }
                        }
                    },
                ),
                ComponentExample(
                    title = "Invalid",
                    descriptionMarkdown = "Use `invalid` on the slots to show an error state.",
                    code =
                        """
                        var value by remember { mutableStateOf("000000") }
                        InputOTP(value = value, onValueChange = { value = it }, length = 6, invalid = true) {
                            InputOTPGroup { InputOTPSlot(0, invalid = true); InputOTPSlot(1, invalid = true) }
                            InputOTPSeparator()
                            InputOTPGroup { InputOTPSlot(2, invalid = true); InputOTPSlot(3, invalid = true) }
                            InputOTPSeparator()
                            InputOTPGroup { InputOTPSlot(4, invalid = true); InputOTPSlot(5, invalid = true) }
                        }
                        """.trimIndent(),
                    preview = {
                        var value by remember { mutableStateOf("000000") }
                        InputOTP(value = value, onValueChange = { value = it }, length = 6, invalid = true) {
                            InputOTPGroup {
                                InputOTPSlot(0, invalid = true)
                                InputOTPSlot(1, invalid = true)
                            }
                            InputOTPSeparator()
                            InputOTPGroup {
                                InputOTPSlot(2, invalid = true)
                                InputOTPSlot(3, invalid = true)
                            }
                            InputOTPSeparator()
                            InputOTPGroup {
                                InputOTPSlot(4, invalid = true)
                                InputOTPSlot(5, invalid = true)
                            }
                        }
                    },
                ),
                ComponentExample(
                    title = "Four Digits",
                    descriptionMarkdown =
                        "A common pattern for PIN codes. This uses the `pattern` prop with digits only.",
                    code =
                        """
                        var code by remember { mutableStateOf("") }
                        InputOTP(value = code, onValueChange = { code = it }, length = 4, pattern = Regex("[0-9]")) {
                            InputOTPGroup {
                                InputOTPSlot(0)
                                InputOTPSlot(1)
                                InputOTPSlot(2)
                                InputOTPSlot(3)
                            }
                        }
                        """.trimIndent(),
                    preview = {
                        var code by remember { mutableStateOf("") }
                        InputOTP(value = code, onValueChange = { code = it }, length = 4, pattern = Regex("[0-9]")) {
                            InputOTPGroup {
                                InputOTPSlot(0)
                                InputOTPSlot(1)
                                InputOTPSlot(2)
                                InputOTPSlot(3)
                            }
                        }
                    },
                ),
                ComponentExample(
                    title = "Alphanumeric",
                    descriptionMarkdown = "Use the alphanumeric pattern to accept both letters and numbers.",
                    code =
                        """
                        var code by remember { mutableStateOf("") }
                        InputOTP(value = code, onValueChange = { code = it }, length = 6, pattern = Regex("[A-Za-z0-9]"), keyboardType = KeyboardType.Ascii) {
                            InputOTPGroup { InputOTPSlot(0); InputOTPSlot(1); InputOTPSlot(2) }
                            InputOTPSeparator()
                            InputOTPGroup { InputOTPSlot(3); InputOTPSlot(4); InputOTPSlot(5) }
                        }
                        """.trimIndent(),
                    preview = {
                        var code by remember { mutableStateOf("") }
                        InputOTP(
                            value = code,
                            onValueChange = { code = it },
                            length = 6,
                            pattern = Regex("[A-Za-z0-9]"),
                            keyboardType = KeyboardType.Ascii,
                        ) {
                            InputOTPGroup {
                                InputOTPSlot(0)
                                InputOTPSlot(1)
                                InputOTPSlot(2)
                            }
                            InputOTPSeparator()
                            InputOTPGroup {
                                InputOTPSlot(3)
                                InputOTPSlot(4)
                                InputOTPSlot(5)
                            }
                        }
                    },
                ),
                ComponentExample(
                    title = "Form",
                    previewPadding = 0.dp,
                    code =
                        """
                        var code by remember { mutableStateOf("") }
                        val focus = remember { FocusRequester() }
                        BoxWithConstraints(Modifier.widthIn(max = 336.dp)) {
                            val density = LocalDensity.current
                            val insetPixels =
                                with(density) {
                                    ((constraints.maxWidth - 304.dp.roundToPx()) / 2)
                                        .coerceIn(8.dp.roundToPx(), 16.dp.roundToPx())
                                }
                            val inset = with(density) { insetPixels.toDp() }
                            val slotWidth =
                                with(density) {
                                    val available = constraints.maxWidth - 2 * insetPixels - 40.dp.roundToPx()
                                    (available / 6).coerceIn(0, 44.dp.roundToPx()).toDp()
                                }
                            val sectionStyle = Style { contentPadding(horizontal = inset, vertical = 0.dp) }

                            Card {
                                CardHeader(
                                    title = "Verify your login",
                                    description = "Enter the verification code we sent to your email address: m@example.com.",
                                    style = sectionStyle,
                                )
                                CardContent(style = sectionStyle) {
                                    Field {
                                        Row(
                                            Modifier.fillMaxWidth(),
                                            horizontalArrangement = Arrangement.SpaceBetween,
                                            verticalAlignment = Alignment.CenterVertically,
                                        ) {
                                            FieldLabel(
                                                "Verification code",
                                                Modifier.focusProperties { canFocus = false }.clickable { focus.requestFocus() },
                                            )
                                            Button(
                                                onClick = {},
                                                label = "Resend Code",
                                                variant = ButtonVariant.Outline,
                                                size = ButtonSize.Xs,
                                            ) {
                                                Icon(Lucide.RefreshCw, Modifier.size(12.dp))
                                                Text { "Resend Code" }
                                            }
                                        }
                                        InputOTP(
                                            value = code,
                                            onValueChange = { code = it },
                                            length = 6,
                                            modifier = Modifier.focusRequester(focus),
                                            contentDescription = "Verification code",
                                        ) {
                                            repeat(2) { group ->
                                                if (group == 1) InputOTPSeparator(Modifier.padding(horizontal = 4.dp))
                                                InputOTPGroup(Modifier.height(48.dp)) {
                                                    repeat(3) { slot ->
                                                        InputOTPSlot(
                                                            group * 3 + slot,
                                                            textStyle = TextStyle(fontSize = 20.sp, lineHeight = 28.sp),
                                                            modifier = Modifier.size(slotWidth + if (slot == 0) 0.dp else 1.dp, 48.dp),
                                                        )
                                                    }
                                                }
                                            }
                                        }
                                        FieldDescription("I no longer have access to this email address.")
                                    }
                                }
                                CardFooter(style = Style { contentPadding(horizontal = inset, vertical = 16.dp) }) {
                                    Column(Modifier.fillMaxWidth(), verticalArrangement = Arrangement.spacedBy(8.dp)) {
                                        Button("Verify", onClick = {}, modifier = Modifier.fillMaxWidth())
                                        Text(textStyle = TextColors.Muted) {
                                            "Having trouble signing in? Contact support"
                                        }
                                    }
                                }
                            }
                        }
                        """.trimIndent(),
                    preview = { InputOTPFormPreview(Modifier.padding(vertical = 40.dp)) },
                ),
                ComponentExample(
                    title = "RTL",
                    descriptionMarkdown =
                        "To enable RTL support in shadcn/ui, see the " +
                            "[RTL configuration guide](https://ui.shadcn.com/docs/rtl).",
                    code =
                        """
                        var code by remember { mutableStateOf("123456") }
                        CompositionLocalProvider(LocalLayoutDirection provides LayoutDirection.Rtl) {
                            Column(Modifier.widthIn(max = 320.dp), verticalArrangement = Arrangement.spacedBy(8.dp)) {
                                Field {
                                    FieldLabel("رمز التحقق")
                                    InputOTP(value = code, onValueChange = { code = it }, length = 6) {
                                        InputOTPGroup {
                                            InputOTPSlot(0)
                                            InputOTPSlot(1)
                                            InputOTPSlot(2)
                                            InputOTPSlot(3)
                                            InputOTPSlot(4)
                                            InputOTPSlot(5)
                                        }
                                    }
                                }
                            }
                        }
                        """.trimIndent(),
                    preview = { InputOTPRtlPreview() },
                ),
            ),
        afterExamples =
            listOf(
                ComponentGuideSection(
                    title = "API Reference",
                    blocks =
                        listOf(
                            ComponentGuideBlock.Paragraph(
                                "See the [input-otp](https://input-otp.rodz.dev) documentation for more " +
                                    "information. The Kotlin port adds `length`/`maxLength`, `pattern`/`accept`, " +
                                    "`invalid` and `onComplete`.",
                            ),
                        ),
                ),
            ),
        apiSections =
            listOf(
                ComponentApiSection(
                    "InputOTP",
                    "Root that owns the value, filtering and paste handling. Slots are visual; one " +
                        "invisible text field captures typing, paste and backspace.",
                    listOf(
                        ApiProperty("value / onValueChange", "String / (String) -> Unit", "Required"),
                        ApiProperty("length / maxLength", "Int / Int?", "Required / null"),
                        ApiProperty("pattern / accept", "Regex? / (Char) -> Boolean", "null / digits"),
                        ApiProperty("masked / type", "Boolean / InputOTPType?", "false / null"),
                        ApiProperty("disabled / readOnly / invalid", "Boolean", "false"),
                        ApiProperty("onComplete", "((String) -> Unit)?", "null"),
                        ApiProperty("keyboardType", "KeyboardType", "NumberPassword"),
                        ApiProperty(
                            "content",
                            "(@Composable InputOTPScope.() -> Unit)?",
                            "null (flat row when omitted)",
                        ),
                    ),
                ),
                ComponentApiSection(
                    "InputOTPSlot / InputOTPGroup / InputOTPSeparator",
                    "Slot boxes, connected groups and separators. `invalid` adds the destructive " +
                        "border and ring to a slot, matching `aria-invalid` in the official recipe.",
                    listOf(
                        ApiProperty("InputOTPSlot.index", "Int", "Required (0-based)"),
                        ApiProperty("InputOTPSlot.textStyle", "TextStyle", "Default (optional overload)"),
                        ApiProperty("InputOTPSlot.invalid", "Boolean", "false"),
                        ApiProperty("InputOTPSlot.modifier", "Modifier", "Modifier"),
                        ApiProperty("InputOTPGroup.modifier", "Modifier", "Modifier"),
                        ApiProperty("InputOTPSeparator.modifier / style", "Modifier / Style", "Default"),
                    ),
                ),
            ),
    )

@Composable
private fun InputOTPFormPreview(modifier: Modifier = Modifier) {
    var code by remember { mutableStateOf("") }
    val focus = remember { FocusRequester() }
    BoxWithConstraints(modifier.widthIn(max = 336.dp)) {
        val density = LocalDensity.current
        val insetPixels =
            with(density) {
                ((constraints.maxWidth - 304.dp.roundToPx()) / 2)
                    .coerceIn(8.dp.roundToPx(), 16.dp.roundToPx())
            }
        val inset = with(density) { insetPixels.toDp() }
        val slotWidth =
            with(density) {
                val available = constraints.maxWidth - 2 * insetPixels - 40.dp.roundToPx()
                (available / 6).coerceIn(0, 44.dp.roundToPx()).toDp()
            }
        val sectionStyle = Style { contentPadding(horizontal = inset, vertical = 0.dp) }

        Card {
            CardHeader(
                title = "Verify your login",
                description = "Enter the verification code we sent to your email address: m@example.com.",
                style = sectionStyle,
            )
            CardContent(style = sectionStyle) {
                Field {
                    Row(
                        Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically,
                    ) {
                        FieldLabel(
                            "Verification code",
                            Modifier.focusProperties { canFocus = false }.clickable { focus.requestFocus() },
                        )
                        Button(
                            onClick = {},
                            label = "Resend Code",
                            variant = ButtonVariant.Outline,
                            size = ButtonSize.Xs,
                        ) {
                            Icon(Lucide.RefreshCw, Modifier.size(12.dp))
                            Text { "Resend Code" }
                        }
                    }
                    InputOTP(
                        value = code,
                        onValueChange = { code = it },
                        length = 6,
                        modifier = Modifier.focusRequester(focus),
                        contentDescription = "Verification code",
                    ) {
                        repeat(2) { group ->
                            if (group == 1) InputOTPSeparator(Modifier.padding(horizontal = 4.dp))
                            InputOTPGroup(Modifier.height(48.dp)) {
                                repeat(3) { slot ->
                                    InputOTPSlot(
                                        group * 3 + slot,
                                        textStyle = TextStyle(fontSize = 20.sp, lineHeight = 28.sp),
                                        modifier = Modifier.size(slotWidth + if (slot == 0) 0.dp else 1.dp, 48.dp),
                                    )
                                }
                            }
                        }
                    }
                    FieldDescription("I no longer have access to this email address.")
                }
            }
            CardFooter(style = Style { contentPadding(horizontal = inset, vertical = 16.dp) }) {
                Column(Modifier.fillMaxWidth(), verticalArrangement = Arrangement.spacedBy(8.dp)) {
                    Button("Verify", onClick = {}, modifier = Modifier.fillMaxWidth())
                    Text(textStyle = TextColors.Muted) {
                        "Having trouble signing in? Contact support"
                    }
                }
            }
        }
    }
}

private data class InputOTPRtlText(
    val language: String,
    val direction: LayoutDirection,
    val verificationCode: String,
)

private val inputOTPRtlTexts =
    listOf(
        InputOTPRtlText("Arabic (العربية)", LayoutDirection.Rtl, "رمز التحقق"),
        InputOTPRtlText("English", LayoutDirection.Ltr, "Verification code"),
        InputOTPRtlText("Hebrew (עברית)", LayoutDirection.Rtl, "קוד אימות"),
    )

@Composable
private fun InputOTPRtlPreview() {
    var language by remember { mutableStateOf(0) }
    var code by remember { mutableStateOf("123456") }
    val focus = remember { FocusRequester() }
    val text = inputOTPRtlTexts[language]
    val inherited = currentTheme
    RtlPreview(language, inputOTPRtlTexts.map { it.language }, { language = it }) {
        val font =
            when (language) {
                0 -> catalogArabicFontFamily()
                2 -> catalogHebrewFontFamily()
                else -> inherited.fontFamily
            }
        CompositionLocalProvider(LocalLayoutDirection provides text.direction) {
            Theme(
                preset = inherited.activePreset,
                baseColor = inherited.activeBaseColor,
                accent = inherited.activeAccent,
                isDark = inherited.isDark,
                baseRadius = inherited.baseRadius,
                ring = inherited.ring,
                unitSpacing = LocalUnitSpacing.current,
                minTouchTarget = inherited.minTouchTarget,
                fontFamily = font,
            ) {
                Column(Modifier.widthIn(max = 320.dp), verticalArrangement = Arrangement.spacedBy(8.dp)) {
                    Field {
                        FieldLabel(
                            text.verificationCode,
                            Modifier.focusProperties { canFocus = false }.clickable { focus.requestFocus() },
                        )
                        InputOTP(
                            value = code,
                            onValueChange = { code = it },
                            length = 6,
                            modifier = Modifier.focusRequester(focus),
                            contentDescription = text.verificationCode,
                        ) {
                            InputOTPGroup {
                                repeat(6) { InputOTPSlot(it) }
                            }
                        }
                    }
                }
            }
        }
    }
}
