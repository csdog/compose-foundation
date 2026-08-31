@file:OptIn(androidx.compose.foundation.style.ExperimentalFoundationStyleApi::class)

package com.toutiaoyan.compose.ui.catalog.docs

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import com.toutiaoyan.compose.ui.components.field.FieldDescription
import com.toutiaoyan.compose.ui.components.field.FieldLabel
import com.toutiaoyan.compose.ui.components.inputotp.InputOTP
import com.toutiaoyan.compose.ui.components.inputotp.InputOTPGroup
import com.toutiaoyan.compose.ui.components.inputotp.InputOTPSeparator
import com.toutiaoyan.compose.ui.components.inputotp.InputOTPSlot
import com.toutiaoyan.compose.ui.components.inputotp.InputOTPType
import com.toutiaoyan.compose.tailwind.core.twSpacing

val inputOTPDoc =
    ComponentDoc(
        id = "input-otp",
        title = "Input OTP",
        description = "A one-time-passcode input with boxed slots that fill left-to-right.",
        usageCode =
            """
            import com.toutiaoyan.compose.ui.components.inputotp.InputOTP

            var code by remember { mutableStateOf("") }
            InputOTP(value = code, onValueChange = { code = it }, length = 6)
            """.trimIndent(),
        examples =
            listOf(
                ComponentExample(
                    title = "Verify your email",
                    code =
                        """
                        var code by remember { mutableStateOf("") }
                        Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                            FieldLabel("One-time password")
                            InputOTP(value = code, onValueChange = { code = it }, length = 6)
                            FieldDescription("Please enter the one-time password sent to your phone.")
                        }
                        """.trimIndent(),
                    preview = {
                        var code by remember { mutableStateOf("") }
                        Column(verticalArrangement = Arrangement.spacedBy(twSpacing.sm)) {
                            FieldLabel("One-time password")
                            InputOTP(value = code, onValueChange = { code = it }, length = 6)
                            FieldDescription("Please enter the one-time password sent to your phone.")
                        }
                    },
                ),
                ComponentExample(
                    title = "With separator",
                    code =
                        """
                        var code by remember { mutableStateOf("") }
                        InputOTP(value = code, onValueChange = { code = it }, length = 6) {
                            InputOTPGroup {
                                InputOTPSlot(0); InputOTPSlot(1); InputOTPSlot(2)
                                InputOTPSeparator()
                                InputOTPSlot(3); InputOTPSlot(4); InputOTPSlot(5)
                            }
                        }
                        """.trimIndent(),
                    preview = {
                        var code by remember { mutableStateOf("") }
                        InputOTP(value = code, onValueChange = { code = it }, length = 6) {
                            InputOTPGroup {
                                InputOTPSlot(0)
                                InputOTPSlot(1)
                                InputOTPSlot(2)
                                InputOTPSeparator()
                                InputOTPSlot(3)
                                InputOTPSlot(4)
                                InputOTPSlot(5)
                            }
                        }
                    },
                ),
                ComponentExample(
                    title = "Disabled",
                    code = """InputOTP(value = "123456", onValueChange = {}, length = 6, disabled = true)""",
                    preview = { InputOTP(value = "123456", onValueChange = {}, length = 6, disabled = true) },
                ),
                ComponentExample(
                    title = "Default disabled",
                    code = """InputOTP(value = "123456", onValueChange = {}, length = 6, disabled = false)""",
                    preview = { InputOTP(value = "123456", onValueChange = {}, length = 6, disabled = false) },
                ),
                ComponentExample(
                    title = "Pattern",
                    code =
                        """
                        var code by remember { mutableStateOf("") }
                        InputOTP(
                            value = code,
                            onValueChange = { code = it },
                            length = 6,
                            pattern = Regex("[A-Za-z0-9]"),
                        )
                        """.trimIndent(),
                    preview = {
                        var code by remember { mutableStateOf("") }
                        InputOTP(
                            value = code,
                            onValueChange = { code = it },
                            length = 6,
                            pattern = Regex("[A-Za-z0-9]"),
                        )
                    },
                ),
                ComponentExample(
                    title = "Default pattern",
                    code =
                        """
                        var code by remember { mutableStateOf("") }
                        InputOTP(
                            value = code,
                            onValueChange = { code = it },
                            length = 6,
                            pattern = null,
                        )
                        """.trimIndent(),
                    preview = {
                        var code by remember { mutableStateOf("") }
                        InputOTP(
                            value = code,
                            onValueChange = { code = it },
                            length = 6,
                            pattern = null,
                        )
                    },
                ),
                ComponentExample(
                    title = "Masked",
                    code =
                        """
                        var code by remember { mutableStateOf("123456") }
                        InputOTP(value = code, onValueChange = { code = it }, length = 6, type = InputOTPType.Password)
                        """.trimIndent(),
                    preview = {
                        var code by remember { mutableStateOf("123456") }
                        InputOTP(
                            value = code,
                            onValueChange = { code = it },
                            length = 6,
                            type = InputOTPType.Password,
                        )
                    },
                ),
                ComponentExample(
                    title = "Default masked",
                    code =
                        """
                        var code by remember { mutableStateOf("123456") }
                        InputOTP(value = code, onValueChange = { code = it }, length = 6, masked = false)
                        """.trimIndent(),
                    preview = {
                        var code by remember { mutableStateOf("123456") }
                        InputOTP(
                            value = code,
                            onValueChange = { code = it },
                            length = 6,
                            masked = false,
                        )
                    },
                ),
                ComponentExample(
                    title = "Text type",
                    code =
                        """
                        var code by remember { mutableStateOf("123456") }
                        InputOTP(value = code, onValueChange = { code = it }, length = 6, type = InputOTPType.Text)
                        """.trimIndent(),
                    preview = {
                        var code by remember { mutableStateOf("123456") }
                        InputOTP(
                            value = code,
                            onValueChange = { code = it },
                            length = 6,
                            type = InputOTPType.Text,
                        )
                    },
                ),
                ComponentExample(
                    title = "Default type",
                    code =
                        """
                        var code by remember { mutableStateOf("123456") }
                        InputOTP(value = code, onValueChange = { code = it }, length = 6, type = InputOTPType.Text)
                        """.trimIndent(),
                    preview = {
                        var code by remember { mutableStateOf("123456") }
                        InputOTP(
                            value = code,
                            onValueChange = { code = it },
                            length = 6,
                            type = InputOTPType.Text,
                        )
                    },
                ),
                ComponentExample(
                    title = "Complete",
                    code =
                        """
                        var code by remember { mutableStateOf("") }
                        InputOTP(
                            value = code,
                            onValueChange = { code = it },
                            length = 6,
                            onComplete = { /* submit */ },
                        )
                        """.trimIndent(),
                    preview = {
                        var code by remember { mutableStateOf("") }
                        InputOTP(
                            value = code,
                            onValueChange = { code = it },
                            length = 6,
                            onComplete = {},
                        )
                    },
                ),
                ComponentExample(
                    title = "Read only",
                    code =
                        """
                        InputOTP(value = "123456", onValueChange = {}, length = 6, readOnly = true)
                        """.trimIndent(),
                    preview = {
                        InputOTP(value = "123456", onValueChange = {}, length = 6, readOnly = true)
                    },
                ),
                ComponentExample(
                    title = "Default read only",
                    code =
                        """
                        var code by remember { mutableStateOf("123456") }
                        InputOTP(value = code, onValueChange = { code = it }, length = 6, readOnly = false)
                        """.trimIndent(),
                    preview = {
                        var code by remember { mutableStateOf("123456") }
                        InputOTP(
                            value = code,
                            onValueChange = { code = it },
                            length = 6,
                            readOnly = false,
                        )
                    },
                ),
                ComponentExample(
                    title = "Max length",
                    code =
                        """
                        var code by remember { mutableStateOf("") }
                        InputOTP(value = code, onValueChange = { code = it }, length = 6, maxLength = 6)
                        """.trimIndent(),
                    preview = {
                        var code by remember { mutableStateOf("") }
                        InputOTP(
                            value = code,
                            onValueChange = { code = it },
                            length = 6,
                            maxLength = 6,
                        )
                    },
                ),
                ComponentExample(
                    title = "Default max length",
                    code =
                        """
                        var code by remember { mutableStateOf("") }
                        InputOTP(value = code, onValueChange = { code = it }, length = 6, maxLength = null)
                        """.trimIndent(),
                    preview = {
                        var code by remember { mutableStateOf("") }
                        InputOTP(
                            value = code,
                            onValueChange = { code = it },
                            length = 6,
                            maxLength = null,
                        )
                    },
                ),
                ComponentExample(
                    title = "Slot",
                    code =
                        """
                        var code by remember { mutableStateOf("") }
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
                        var code by remember { mutableStateOf("") }
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
                    title = "Group",
                    code =
                        """
                        var code by remember { mutableStateOf("") }
                        InputOTP(value = code, onValueChange = { code = it }, length = 6) {
                            InputOTPGroup {
                                InputOTPSlot(0)
                                InputOTPSlot(1)
                                InputOTPSlot(2)
                            }
                        }
                        """.trimIndent(),
                    preview = {
                        var code by remember { mutableStateOf("") }
                        InputOTP(value = code, onValueChange = { code = it }, length = 6) {
                            InputOTPGroup {
                                InputOTPSlot(0)
                                InputOTPSlot(1)
                                InputOTPSlot(2)
                            }
                        }
                    },
                ),
                ComponentExample(
                    title = "Separator",
                    code =
                        """
                        var code by remember { mutableStateOf("") }
                        InputOTP(value = code, onValueChange = { code = it }, length = 6) {
                            InputOTPGroup {
                                InputOTPSlot(0)
                                InputOTPSlot(1)
                                InputOTPSlot(2)
                                InputOTPSeparator()
                                InputOTPSlot(3)
                                InputOTPSlot(4)
                                InputOTPSlot(5)
                            }
                        }
                        """.trimIndent(),
                    preview = {
                        var code by remember { mutableStateOf("") }
                        InputOTP(value = code, onValueChange = { code = it }, length = 6) {
                            InputOTPGroup {
                                InputOTPSlot(0)
                                InputOTPSlot(1)
                                InputOTPSlot(2)
                                InputOTPSeparator()
                                InputOTPSlot(3)
                                InputOTPSlot(4)
                                InputOTPSlot(5)
                            }
                        }
                    },
                ),
            ),
    )
