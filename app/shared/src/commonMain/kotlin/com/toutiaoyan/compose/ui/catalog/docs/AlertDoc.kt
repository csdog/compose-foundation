@file:OptIn(androidx.compose.foundation.style.ExperimentalFoundationStyleApi::class)

package com.toutiaoyan.compose.ui.catalog.docs

import com.composables.icons.lucide.Lucide
import com.composables.icons.lucide.CircleCheck
import com.composables.icons.lucide.Info
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.widthIn
import androidx.compose.ui.Modifier
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.getValue
import androidx.compose.runtime.setValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.platform.LocalLayoutDirection
import androidx.compose.ui.unit.LayoutDirection
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.style.Style
import com.toutiaoyan.compose.ui.styles.border
import com.toutiaoyan.compose.ui.theme.currentTheme
import com.toutiaoyan.compose.tailwind.core.TwColors
import com.composables.icons.lucide.CircleAlert
import com.composables.icons.lucide.TriangleAlert

import androidx.compose.ui.unit.dp
import com.toutiaoyan.compose.ui.components.alert.Alert
import com.toutiaoyan.compose.ui.components.alert.AlertAction
import com.toutiaoyan.compose.ui.components.alert.AlertDescription
import com.toutiaoyan.compose.ui.components.alert.AlertTitle
import com.toutiaoyan.compose.ui.components.alert.AlertVariant
import com.toutiaoyan.compose.ui.components.button.Button
import com.toutiaoyan.compose.ui.components.button.ButtonSize
import com.toutiaoyan.compose.ui.components.button.ButtonVariant

val alertDoc =
    ComponentDoc(
        id = "alert",
        title = "Alert",
        description = "Displays a callout for user attention.",
        usageImportsCode =
            """
            import com.toutiaoyan.compose.ui.components.alert.*
            import com.toutiaoyan.compose.ui.components.button.*
            import com.toutiaoyan.compose.ui.components.icon.Icon
            import com.composables.icons.lucide.Info
            import com.composables.icons.lucide.Lucide
            """.trimIndent(),
        usageCode =
            """
            Alert(icon = { Icon(Lucide.Info) }) {
                AlertTitle("Heads up!")
                AlertDescription("You can add components and dependencies to your app.")
                AlertAction { Button("Enable", onClick = {}, variant = ButtonVariant.Outline) }
            }
            """.trimIndent(),
        compositionCode =
            """
            Alert
            ├── Icon (icon parameter)
            ├── AlertTitle
            ├── AlertDescription
            └── AlertAction
            """.trimIndent(),
        examplesHeading = null,
        apiSections =
            listOf(
                ComponentApiSection(
                    "Alert",
                    (
                        "An attention callout. Use the slot overload for composition, " +
                            "or the title/description convenience overload."
                    ),
                    listOf(
                        ApiProperty("variant", "AlertVariant", "AlertVariant.Default"),
                        ApiProperty("icon", "(@Composable () -> Unit)?", "null"),
                        ApiProperty("modifier", "Modifier", "Modifier"),
                        ApiProperty("style", "Style", "Style"),
                        ApiProperty("content", "@Composable AlertScope.() -> Unit", "Required (slot overload)"),
                        ApiProperty("title", "String", "Required (convenience overload)"),
                        ApiProperty("description", "String?", "null (convenience overload)"),
                    ),
                ),
                ComponentApiSection(
                    "AlertTitle",
                    (
                        "The title of the alert. Invoke inside Alert; modifier and " +
                            "style are available on the text-lambda overload."
                    ),
                    listOf(
                        ApiProperty("text", "() -> String", "Required"),
                        ApiProperty("modifier", "Modifier", "Modifier"),
                        ApiProperty("style", "Style", "Style"),
                    ),
                ),
                ComponentApiSection(
                    "AlertDescription",
                    (
                        "The description or supporting content of the alert. Invoke " +
                            "inside Alert; modifier and style are available on the " +
                            "text-lambda overload."
                    ),
                    listOf(
                        ApiProperty("text", "() -> String", "Required"),
                        ApiProperty("modifier", "Modifier", "Modifier"),
                        ApiProperty("style", "Style", "Style"),
                    ),
                ),
                ComponentApiSection(
                    "AlertAction",
                    (
                        "An action at the top end of the alert. Nova keeps the " +
                            "official 72dp end reserve. Put multiple controls inside one " +
                            "slot and add end padding to the body column for wider " +
                            "content; see Multiple actions (Compose). RTL uses the logical " +
                            "end."
                    ),
                    listOf(
                        ApiProperty("modifier", "Modifier", "Modifier"),
                        ApiProperty("content", "@Composable () -> Unit", "Required"),
                    ),
                ),
            ),
        examples =
            listOf(
                ComponentExample(
                    title = "Default",
                    mobileMinPreviewHeight = 0.dp,
                    previewPadding = 24.dp,
                    code =
                        """
                        Column(Modifier.widthIn(max = 448.dp).fillMaxWidth(), verticalArrangement = Arrangement.spacedBy(16.dp)) {
                            Alert(
                                title = "Payment successful",
                                description = "Your payment of $29.99 has been processed. A receipt has been sent to your email address.",
                                icon = { Icon(Lucide.CircleCheck) },
                            )
                            Alert(
                                title = "New feature available",
                                description = "We've added dark mode support. You can enable it in your account settings.",
                                icon = { Icon(Lucide.Info) },
                            )
                        }
                        """.trimIndent(),
                    preview = {
                        Column(
                            Modifier.widthIn(max = 448.dp).fillMaxWidth(),
                            verticalArrangement = Arrangement.spacedBy(16.dp),
                        ) {
                            Alert(
                                title = "Payment successful",
                                description = (
                                    "Your payment of $29.99 has been processed. A receipt has been " +
                                        "sent to your email address."
                                ),
                                icon = { DocIcon(Lucide.CircleCheck) },
                            )
                            Alert(
                                title = "New feature available",
                                description = (
                                    "We've added dark mode support. You can enable it in your " +
                                        "account settings."
                                ),
                                icon = { DocIcon(Lucide.Info) },
                            )
                        }
                    },
                ),
                ComponentExample(
                    title = "Basic",
                    descriptionMarkdown = "An alert with an icon, title, and description.",
                    mobileMinPreviewHeight = 0.dp,
                    previewPadding = 24.dp,
                    code =
                        """
                        Alert(
                            title = "Account updated successfully",
                            description = "Your profile information has been saved. Changes will be reflected immediately.",
                            modifier = Modifier.widthIn(max = 448.dp).fillMaxWidth(),
                            icon = { Icon(Lucide.CircleCheck) },
                        )
                        """.trimIndent(),
                    preview = {
                        Alert(
                            title = "Account updated successfully",
                            description = (
                                "Your profile information has been saved. Changes will be " +
                                    "reflected immediately."
                            ),
                            modifier = Modifier.widthIn(max = 448.dp).fillMaxWidth(),
                            icon = { DocIcon(Lucide.CircleCheck) },
                        )
                    },
                ),
                ComponentExample(
                    title = "Destructive",
                    descriptionMarkdown = "Set `variant = AlertVariant.Destructive` to display a destructive alert.",
                    mobileMinPreviewHeight = 0.dp,
                    previewPadding = 24.dp,
                    code =
                        """
                        Alert(
                            title = "Payment failed",
                            description = "Your payment could not be processed. Please check your payment method and try again.",
                            modifier = Modifier.widthIn(max = 448.dp).fillMaxWidth(),
                            variant = AlertVariant.Destructive,
                            icon = { Icon(Lucide.CircleAlert, tint = currentTheme.colors.destructive) },
                        )
                        """.trimIndent(),
                    preview = {
                        Alert(
                            title = "Payment failed",
                            description = (
                                "Your payment could not be processed. Please check your " +
                                    "payment method and try again."
                            ),
                            modifier = Modifier.widthIn(max = 448.dp).fillMaxWidth(),
                            variant = AlertVariant.Destructive,
                            icon = { DocIcon(Lucide.CircleAlert, tint = currentTheme.colors.destructive) },
                        )
                    },
                ),
                ComponentExample(
                    title = "Action",
                    descriptionMarkdown = "Use `AlertAction` to add a button or another action element to the alert.",
                    mobileMinPreviewHeight = 0.dp,
                    previewPadding = 24.dp,
                    code =
                        """
                        Alert(Modifier.widthIn(max = 448.dp).fillMaxWidth()) {
                            AlertTitle("Dark mode is now available")
                            AlertDescription("Enable it under your profile settings to get started.")
                            AlertAction { Button("Enable", onClick = {}, size = ButtonSize.Xs, variant = ButtonVariant.Default) }
                        }
                        """.trimIndent(),
                    preview = {
                        Alert(Modifier.widthIn(max = 448.dp).fillMaxWidth()) {
                            AlertTitle("Dark mode is now available")
                            AlertDescription("Enable it under your profile settings to get started.")
                            AlertAction {
                                Button(
                                    "Enable",
                                    onClick = {},
                                    size = ButtonSize.Xs,
                                    variant = ButtonVariant.Default,
                                )
                            }
                        }
                    },
                ),
                ComponentExample(
                    title = "Custom Colors",
                    descriptionMarkdown = (
                        "You can customize the alert colors with `Style` properties " +
                            "such as `background` and `contentColor` on the `Alert` " +
                            "component."
                    ),
                    mobileMinPreviewHeight = 0.dp,
                    previewPadding = 24.dp,
                    code =
                        """
                        val dark = currentTheme.isDark
                        val foreground = if (dark) TwColors.amber50 else TwColors.amber900
                        Alert(
                            title = "Your subscription will expire in 3 days.",
                            description = "Renew now to avoid service interruption or upgrade to a paid plan to continue using the service.",
                            modifier = Modifier.widthIn(max = 448.dp).fillMaxWidth(),
                            icon = { Icon(Lucide.TriangleAlert, tint = foreground) },
                            style = Style {
                                background(if (dark) TwColors.amber950 else TwColors.amber50)
                                border(if (dark) TwColors.amber900 else TwColors.amber200)
                                contentColor(foreground)
                            },
                        )
                        """.trimIndent(),
                    preview = {
                        val dark = currentTheme.isDark
                        val foreground = if (dark) TwColors.amber50 else TwColors.amber900
                        Alert(
                            title = "Your subscription will expire in 3 days.",
                            description = (
                                "Renew now to avoid service interruption or upgrade to a paid " +
                                    "plan to continue using the service."
                            ),
                            modifier = Modifier.widthIn(max = 448.dp).fillMaxWidth(),
                            icon = { DocIcon(Lucide.TriangleAlert, tint = foreground) },
                            style =
                                Style {
                                    background(if (dark) TwColors.amber950 else TwColors.amber50)
                                    border(if (dark) TwColors.amber900 else TwColors.amber200)
                                    contentColor(foreground)
                                },
                        )
                    },
                ),
                ComponentExample(
                    title = "RTL",
                    descriptionMarkdown = (
                        "To enable RTL support, provide `LocalLayoutDirection`. See " +
                            "the [RTL configuration guide](https://ui.shadcn.com/docs/rtl)."
                    ),
                    minPreviewHeight = 0.dp,
                    mobileMinPreviewHeight = 0.dp,
                    previewPadding = 0.dp,
                    code =
                        """
                        CompositionLocalProvider(LocalLayoutDirection provides LayoutDirection.Rtl) {
                            Column(Modifier.widthIn(max = 448.dp).fillMaxWidth(), verticalArrangement = Arrangement.spacedBy(16.dp)) {
                                Alert(
                                    title = "تم الدفع بنجاح",
                                    description = "تمت معالجة دفعتك البالغة 29.99 دولارًا. تم إرسال إيصال إلى عنوان بريدك الإلكتروني.",
                                    icon = { Icon(Lucide.CircleCheck) },
                                )
                                Alert(
                                    title = "ميزة جديدة متاحة",
                                    description = "لقد أضفنا دعم الوضع الداكن. يمكنك تفعيله في إعدادات حسابك.",
                                    icon = { Icon(Lucide.Info) },
                                )
                            }
                        }
                        """.trimIndent(),
                    preview = {
                        var language by remember { mutableStateOf(1) }
                        val labels = listOf("English", "Arabic (العربية)", "Hebrew (עברית)")
                        val translations =
                            listOf(
                                listOf(
                                    "Payment successful",
                                    "Your payment of ${'$'}29.99 has been processed. " +
                                        "A receipt has been sent to your email address.",
                                    "New feature available",
                                    "We've added dark mode support. You can enable it in your account settings.",
                                ),
                                listOf(
                                    "تم الدفع بنجاح",
                                    (
                                        "تمت معالجة دفعتك البالغة 29.99 دولارًا. تم إرسال إيصال إلى " +
                                            "عنوان بريدك الإلكتروني."
                                    ),
                                    "ميزة جديدة متاحة",
                                    "لقد أضفنا دعم الوضع الداكن. يمكنك تفعيله في إعدادات حسابك.",
                                ),
                                listOf(
                                    "התשלום בוצע בהצלחה",
                                    "התשלום שלך בסך 29.99 דולר עובד. קבלה נשלחה לכתובת האימייל שלך.",
                                    "תכונה חדשה זמינה",
                                    "הוספנו תמיכה במצב כהה. אתה יכול להפעיל אותו בהגדרות החשבון שלך.",
                                ),
                            )
                        RtlPreview(language, labels, onLanguageChange = { language = it }) {
                            Column(
                                Modifier.widthIn(max = 448.dp).fillMaxWidth(),
                                verticalArrangement = Arrangement.spacedBy(16.dp),
                            ) {
                                CompositionLocalProvider(
                                    LocalLayoutDirection provides
                                        if (language == 0) {
                                            LayoutDirection.Ltr
                                        } else {
                                            LayoutDirection.Rtl
                                        },
                                ) {
                                    Alert(
                                        title =
                                            translations[language][0],
                                        description =
                                            translations[language][1],
                                        icon =
                                            {
                                                DocIcon(Lucide.CircleCheck)
                                            },
                                    )
                                    Alert(
                                        title = translations[language][2],
                                        description = translations[language][3],
                                        icon = { DocIcon(Lucide.Info) },
                                    )
                                }
                            }
                        }
                    },
                ),
                ComponentExample(
                    title = "Kotlin extension: Slots",
                    mobileMinPreviewHeight = 0.dp,
                    previewPadding = 24.dp,
                    code =
                        """
                        Alert(Modifier.widthIn(max = 448.dp).fillMaxWidth()) {
                            AlertTitle("Heads up!")
                            AlertDescription("You can add components to your app.")
                        }
                        """.trimIndent(),
                    preview = {
                        Alert(Modifier.widthIn(max = 448.dp).fillMaxWidth()) {
                            AlertTitle("Heads up!")
                            AlertDescription("You can add components to your app.")
                        }
                    },
                ),
                ComponentExample(
                    title = "Kotlin extension: Title",
                    mobileMinPreviewHeight = 0.dp,
                    previewPadding = 24.dp,
                    code =
                        """
                        Alert(Modifier.widthIn(max = 448.dp).fillMaxWidth()) {
                            AlertTitle("Heads up!")
                        }
                        """.trimIndent(),
                    preview = {
                        Alert(Modifier.widthIn(max = 448.dp).fillMaxWidth()) {
                            AlertTitle("Heads up!")
                        }
                    },
                ),
                ComponentExample(
                    title = "Kotlin extension: Description",
                    mobileMinPreviewHeight = 0.dp,
                    previewPadding = 24.dp,
                    code =
                        """
                        Alert(Modifier.widthIn(max = 448.dp).fillMaxWidth()) {
                            AlertDescription("You can add components to your app.")
                        }
                        """.trimIndent(),
                    preview = {
                        Alert(Modifier.widthIn(max = 448.dp).fillMaxWidth()) {
                            AlertDescription("You can add components to your app.")
                        }
                    },
                ),
                ComponentExample(
                    title = "Kotlin extension: Multiple actions",
                    descriptionMarkdown = (
                        "Compose extension: the upstream alert keeps a fixed 72dp " +
                            "action reserve. This 136dp action row uses 80dp extra end " +
                            "padding on the body column to preserve an 8dp gap. Both " +
                            "buttons update the example message."
                    ),
                    mobileMinPreviewHeight = 0.dp,
                    previewPadding = 24.dp,
                    code =
                        """
                        var message by remember { mutableStateOf("Choose an action for this draft.") }
                        Alert(Modifier.widthIn(max = 448.dp).fillMaxWidth()) {
                            // 136dp action width - the compact 56dp action allowance.
                            Column(Modifier.padding(end = 80.dp), verticalArrangement = Arrangement.spacedBy(2.dp)) {
                                AlertTitle("Review changes")
                                AlertDescription(message)
                            }
                            AlertAction {
                                Row(Modifier.width(136.dp), horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                                    Button("Save", onClick = { message = "Save selected." }, size = ButtonSize.Xs, style = Style { width(64.dp) })
                                    Button("Undo", onClick = { message = "Undo selected." }, size = ButtonSize.Xs, variant = ButtonVariant.Outline, style = Style { width(64.dp) })
                                }
                            }
                        }
                        """.trimIndent(),
                    preview = {
                        var message by remember { mutableStateOf("Choose an action for this draft.") }
                        Alert(Modifier.widthIn(max = 448.dp).fillMaxWidth()) {
                            Column(Modifier.padding(end = 80.dp), verticalArrangement = Arrangement.spacedBy(2.dp)) {
                                AlertTitle("Review changes")
                                AlertDescription(message)
                            }
                            AlertAction {
                                Row(Modifier.width(136.dp), horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                                    Button(
                                        "Save",
                                        onClick = { message = "Save selected." },
                                        size = ButtonSize.Xs,
                                        style = Style { width(64.dp) },
                                    )
                                    Button(
                                        "Undo",
                                        onClick = {
                                            message = "Undo selected."
                                        },
                                        size = ButtonSize.Xs,
                                        variant = ButtonVariant.Outline,
                                        style =
                                            Style {
                                                width(
                                                    64.dp,
                                                )
                                            },
                                    )
                                }
                            }
                        }
                    },
                ),
            ),
    )
