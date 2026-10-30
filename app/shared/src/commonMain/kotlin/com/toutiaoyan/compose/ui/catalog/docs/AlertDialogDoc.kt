@file:OptIn(androidx.compose.foundation.style.ExperimentalFoundationStyleApi::class)

package com.toutiaoyan.compose.ui.catalog.docs

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.style.Style
import androidx.compose.foundation.text.BasicText
import androidx.compose.runtime.*
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.LocalLayoutDirection
import androidx.compose.ui.text.*
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.unit.LayoutDirection
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.composables.icons.lucide.*
import com.toutiaoyan.compose.ui.catalog.LocalDocNavigation
import com.toutiaoyan.compose.ui.components.alertdialog.*
import com.toutiaoyan.compose.ui.components.button.ButtonVariant
import com.toutiaoyan.compose.ui.components.dialog.DialogSize
import com.toutiaoyan.compose.ui.components.icon.Icon
import com.toutiaoyan.compose.ui.theme.currentTheme

private const val confirmationTitle = "Are you absolutely sure?"
private const val confirmationDescription = (
    "This action cannot be undone. This will permanently delete " +
        "your account from our servers."
)
private const val accessoryTitle = "Allow accessory to connect?"
private const val accessoryDescription = "Do you want to allow the USB accessory to connect to this device?"
private const val deleteDescription = (
    "This will permanently delete this chat conversation. View " +
        "Settings delete any memories saved during this chat."
)

private data class AlertDialogExampleContent(
    val trigger: String,
    val title: String,
    val description: String,
    val cancel: String = "Cancel",
    val action: String = "Continue",
    val small: Boolean = false,
    val icon: ImageVector? = null,
    val iconName: String? = null,
    val destructive: Boolean = false,
)

private val defaultConfirmation = AlertDialogExampleContent("Show Dialog", confirmationTitle, confirmationDescription)
private val smallConfirmation =
    AlertDialogExampleContent("Show Dialog", accessoryTitle, accessoryDescription, "Don't allow", "Allow", small = true)

/** Relative placeholder link has the same current-page fragment behavior as upstream. */
@Composable
private fun DeleteDescription() {
    val navigation = LocalDocNavigation.current
    val colors = currentTheme.colors
    BasicText(
        buildAnnotatedString {
            append("This will permanently delete this chat conversation. View ")
            withLink(
                LinkAnnotation.Url(
                    "#",
                    TextLinkStyles(
                        style = SpanStyle(textDecoration = TextDecoration.Underline),
                        hoveredStyle = SpanStyle(color = colors.foreground),
                    ),
                ) {
                    navigation?.navigate("")
                },
            ) { append("Settings") }
            append(" delete any memories saved during this chat.")
        },
        style =
            currentTheme.typography.bodyMedium.copy(
                fontFamily = currentTheme.fontFamily,
                fontSize = 14.sp,
                lineHeight = 20.sp,
                color = colors.mutedForeground,
                textAlign = TextAlign.Center,
            ),
    )
}

@Composable
private fun Confirmation(content: AlertDialogExampleContent) {
    var open by remember { mutableStateOf(false) }
    val colors = currentTheme.colors
    val dark = currentTheme.isDark
    AlertDialog(open = open, onOpenChange = { open = it }) {
        AlertDialogTrigger(
            content.trigger,
            variant = if (content.destructive) ButtonVariant.Destructive else ButtonVariant.Outline,
        )
        AlertDialogContent(size = if (content.small) DialogSize.Sm else DialogSize.Default) {
            AlertDialogHeader {
                content.icon?.let { icon ->
                    AlertDialogMedia(
                        style =
                            if (content.destructive) {
                                Style {
                                    background(colors.destructive.copy(alpha = if (dark) .2f else .1f))
                                }
                            } else {
                                Style
                            },
                    ) {
                        Icon(icon, tint = if (content.destructive) colors.destructive else colors.popoverForeground)
                    }
                }
                AlertDialogTitle(content.title)
                if (content.destructive) DeleteDescription() else AlertDialogDescription(content.description)
            }
            AlertDialogFooter {
                AlertDialogCancel(content.cancel)
                AlertDialogAction(
                    content.action,
                    variant = if (content.destructive) ButtonVariant.Destructive else ButtonVariant.Default,
                )
            }
        }
    }
}

private fun confirmationCode(content: AlertDialogExampleContent): String {
    fun quote(text: String) = "\"${text.replace("\\", "\\\\").replace("\"", "\\\"")}\""
    val media =
        content.iconName?.let { name ->
            if (content.destructive) {
                """
                AlertDialogMedia(style = Style {
                    background(colors.destructive.copy(alpha = if (dark) .2f else .1f))
                }) { Icon(Lucide.$name, tint = colors.destructive) }
                """.trimIndent()
            } else {
                "AlertDialogMedia { Icon(Lucide.$name) }"
            }
        }.orEmpty()
    val description =
        if (content.destructive) {
            """
            BasicText(
                buildAnnotatedString {
                    append("This will permanently delete this chat conversation. View ")
                    withLink(LinkAnnotation.Url("#", TextLinkStyles(style = SpanStyle(textDecoration = TextDecoration.Underline), hoveredStyle = SpanStyle(color = colors.foreground)))) {
                        append("Settings")
                    }
                    append(" delete any memories saved during this chat.")
                },
                style = TextStyle(fontFamily = currentTheme.fontFamily, fontSize = 14.sp,
                    lineHeight = 20.sp, color = colors.mutedForeground, textAlign = TextAlign.Center),
            )
            """.trimIndent()
        } else {
            "AlertDialogDescription(${quote(content.description)})"
        }
    val setup = if (content.destructive) "\nval colors = currentTheme.colors\nval dark = currentTheme.isDark" else ""
    val header =
        listOf(media, "AlertDialogTitle(${quote(content.title)})", description).filter {
            it.isNotEmpty()
        }.joinToString("\n").prependIndent("            ")
    val trigger = quote(content.trigger)
    val action = quote(content.action)
    val cancel = quote(content.cancel)
    val triggerVariant = if (content.destructive) "Destructive" else "Outline"
    val actionVariant = if (content.destructive) "Destructive" else "Default"
    val dialogSize = if (content.small) "Sm" else "Default"
    return """
        |var open by remember { mutableStateOf(false) }$setup
        |AlertDialog(open = open, onOpenChange = { open = it }) {
        |    AlertDialogTrigger($trigger, variant = ButtonVariant.$triggerVariant)
        |    AlertDialogContent(size = DialogSize.$dialogSize) {
        |        AlertDialogHeader {
        |$header
        |        }
        |        AlertDialogFooter {
        |            AlertDialogCancel($cancel)
        |            AlertDialogAction($action, variant = ButtonVariant.$actionVariant)
        |        }
        |    }
        |}
        """.trimMargin()
}

private fun confirmationExample(
    title: String,
    description: String?,
    content: AlertDialogExampleContent,
) = ComponentExample(title = title, descriptionMarkdown = description, code = confirmationCode(content), preview = {
    Confirmation(content)
})

private data class ConfirmationTranslation(
    val label: String,
    val direction: LayoutDirection,
    val normal: AlertDialogExampleContent,
    val small: AlertDialogExampleContent,
)

private val confirmationTranslations =
    listOf(
        ConfirmationTranslation(
            "English",
            LayoutDirection.Ltr,
            defaultConfirmation,
            smallConfirmation.copy(trigger = "Show Dialog (sm)", icon = Lucide.Bluetooth, iconName = "Bluetooth"),
        ),
        ConfirmationTranslation(
            "Arabic (العربية)",
            LayoutDirection.Rtl,
            AlertDialogExampleContent(
                "إظهار الحوار",
                "هل أنت متأكد تمامًا؟",
                "لا يمكن التراجع عن هذا الإجراء. سيؤدي هذا إلى حذف حسابك نهائيًا من خوادمنا.",
                "إلغاء",
                "متابعة",
            ),
            AlertDialogExampleContent(
                "إظهار الحوار (صغير)",
                "السماح للملحق بالاتصال؟",
                "هل تريد السماح لملحق USB بالاتصال بهذا الجهاز؟",
                "عدم السماح",
                "السماح",
                true,
                Lucide.Bluetooth,
                "Bluetooth",
            ),
        ),
        ConfirmationTranslation(
            "Hebrew (עברית)",
            LayoutDirection.Rtl,
            AlertDialogExampleContent(
                "הצג דיאלוג",
                "האם אתה בטוח לחלוטין?",
                "פעולה זו לא ניתנת לביטול. זה ימחק לצמיתות את החשבון שלך מהשרתים שלנו.",
                "ביטול",
                "המשך",
            ),
            AlertDialogExampleContent(
                "הצג דיאלוג (קטן)",
                "לאפשר להתקן להתחבר?",
                "האם אתה רוצה לאפשר להתקן USB להתחבר למכשיר זה?",
                "אל תאפשר",
                "אפשר",
                true,
                Lucide.Bluetooth,
                "Bluetooth",
            ),
        ),
    )

val alertDialogDoc =
    ComponentDoc(
        id = "alert-dialog",
        title = "Alert Dialog",
        description = "A modal dialog that interrupts the user with important content and expects a response.",
        usageImportsCode =
            """
            import androidx.compose.foundation.layout.Arrangement
            import androidx.compose.foundation.layout.FlowRow
            import androidx.compose.ui.platform.LocalLayoutDirection
            import androidx.compose.ui.unit.LayoutDirection
            import androidx.compose.foundation.text.BasicText
            import androidx.compose.foundation.style.Style
            import androidx.compose.runtime.*
            import androidx.compose.ui.text.*
            import androidx.compose.ui.text.style.TextAlign
            import androidx.compose.ui.text.style.TextDecoration
            import androidx.compose.ui.unit.sp
            import com.toutiaoyan.compose.ui.theme.currentTheme
            import com.toutiaoyan.compose.ui.components.alertdialog.*
            import com.toutiaoyan.compose.ui.components.button.ButtonVariant
            import com.toutiaoyan.compose.ui.components.dialog.DialogSize
            import com.toutiaoyan.compose.ui.components.icon.Icon
            import com.composables.icons.lucide.*
            """.trimIndent(),
        usageCode = confirmationCode(defaultConfirmation),
        compositionCode =
            """
            AlertDialog
            ├── AlertDialogTrigger
            └── AlertDialogContent
                ├── AlertDialogHeader
                │   ├── AlertDialogMedia (optional)
                │   ├── AlertDialogTitle
                │   └── AlertDialogDescription
                └── AlertDialogFooter
                    ├── AlertDialogCancel
                    └── AlertDialogAction
            """.trimIndent(),
        examplesHeading = null,
        examples =
            listOf(
                confirmationExample("Default", null, defaultConfirmation),
                confirmationExample(
                    "Basic",
                    "A basic alert dialog that requires a response from the user.",
                    defaultConfirmation.copy(
                        description = (
                            "This action cannot be undone. This will permanently delete " +
                                "your account and remove your data from our servers."
                        ),
                    ),
                ),
                confirmationExample(
                    "Small",
                    "Use `size = DialogSize.Sm` to make the alert dialog smaller.",
                    smallConfirmation,
                ),
                confirmationExample(
                    "Media",
                    "Use `AlertDialogMedia` to add a media element such as an icon or image to the alert dialog.",
                    AlertDialogExampleContent(
                        "Share Project",
                        "Share this project?",
                        "Anyone with the link will be able to view and edit this project.",
                        action = "Share",
                        icon = Lucide.CircleFadingPlus,
                        iconName = "CircleFadingPlus",
                    ),
                ),
                confirmationExample(
                    "Small with Media",
                    "Combine `size = DialogSize.Sm` with `AlertDialogMedia` for a compact media dialog.",
                    smallConfirmation.copy(icon = Lucide.Bluetooth, iconName = "Bluetooth"),
                ),
                confirmationExample(
                    "Destructive",
                    (
                        "Use the destructive button variant to indicate a destructive " +
                            "action. The Settings link is a current-page placeholder, as " +
                            "in the upstream example."
                    ),
                    AlertDialogExampleContent(
                        "Delete Chat",
                        "Delete chat?",
                        deleteDescription,
                        action = "Delete",
                        small = true,
                        icon = Lucide.Trash2,
                        iconName = "Trash2",
                        destructive = true,
                    ),
                ),
                ComponentExample(
                    title = "RTL",
                    descriptionMarkdown = (
                        "To enable RTL support in your app, see the [RTL configuration " +
                            "guide](https://ui.shadcn.com/docs/rtl). Source shows the " +
                            "Arabic pair; the catalog toolbar also previews English and " +
                            "Hebrew."
                    ),
                    previewPadding = 0.dp,
                    code =
                        """
                            CompositionLocalProvider(LocalLayoutDirection provides LayoutDirection.Rtl) {
                                FlowRow(horizontalArrangement = Arrangement.spacedBy(16.dp), verticalArrangement = Arrangement.spacedBy(16.dp)) {
                                    key("default") {
                        ${confirmationCode(confirmationTranslations[1].normal).prependIndent("                ")}
                                    }
                                    key("small") {
                        ${confirmationCode(confirmationTranslations[1].small).prependIndent("                ")}
                                    }
                                }
                            }
                        """.trimIndent(),
                    preview = {
                        var language by remember { mutableStateOf(1) }
                        val translation = confirmationTranslations[language]
                        RtlPreview(language, confirmationTranslations.map { it.label }, { language = it }) {
                            CompositionLocalProvider(LocalLayoutDirection provides translation.direction) {
                                FlowRow(
                                    horizontalArrangement = Arrangement.spacedBy(16.dp),
                                    verticalArrangement = Arrangement.spacedBy(16.dp),
                                ) {
                                    Confirmation(translation.normal)
                                    Confirmation(translation.small)
                                }
                            }
                        }
                    },
                ),
            ),
        apiSections =
            listOf(
                ComponentApiSection(
                    "AlertDialog",
                    (
                        "Controlled or uncontrolled state. By default, outside clicks " +
                            "do not dismiss an alert dialog; Cancel and Action both " +
                            "request closure. Escape is supported by the shared overlay. " +
                            "The modal=false option, optional close button and " +
                            "outside-dismiss callbacks are Compose extensions."
                    ),
                    listOf(
                        ApiProperty("open", "Boolean?", "null"),
                        ApiProperty("onOpenChange", "((Boolean) -> Unit)?", "null"),
                        ApiProperty("defaultOpen", "Boolean", "false"),
                        ApiProperty("modal", "Boolean", "true"),
                        ApiProperty("content", "@Composable DialogScope.() -> Unit", "Required"),
                    ),
                ),
                ComponentApiSection(
                    "AlertDialogContent",
                    (
                        "Place inside AlertDialog. Nova default width is 384dp on " +
                            "desktop and 320dp on phones; Sm is 320dp. Content clamps to " +
                            "the viewport. Lg is a Compose extension. Caller Style can " +
                            "override chrome."
                    ),
                    listOf(
                        ApiProperty(
                            "size",
                            "DialogSize",
                            "DialogSize.Default",
                        ),
                        ApiProperty("showCloseButton", "Boolean", "false"),
                        ApiProperty(
                            "dismissOnClickOutside",
                            "Boolean",
                            "false",
                        ),
                        ApiProperty("closeAriaLabel", "String", "Close"),
                        ApiProperty(
                            "onPointerDownOutside",
                            "(() -> Boolean)?",
                            "null",
                        ),
                        ApiProperty("onInteractOutside", "(() -> Boolean)?", "null"),
                        ApiProperty("modifier", "Modifier", "Modifier"), ApiProperty("style", "Style", "Style"),
                        ApiProperty("content", "@Composable ColumnScope.() -> Unit", "Required"),
                    ),
                ),
                ComponentApiSection(
                    "Header and Media",
                    (
                        "Nova uses a 6dp title/description gap. Media is 40dp with an " +
                            "8dp bottom margin and a default 24dp inline icon. Default " +
                            "desktop media sits in a leading column, 16dp from the body; " +
                            "phones and Sm stack and center. Compose rich descriptions can " +
                            "use BasicText inside Header."
                    ),
                    listOf(
                        ApiProperty("AlertDialogHeader.content", "@Composable ColumnScope.() -> Unit", "Required"),
                        ApiProperty("AlertDialogMedia.content", "@Composable () -> Unit", "Required"),
                        ApiProperty("AlertDialogTitle.text", "String or () -> String", "Required"),
                        ApiProperty("AlertDialogDescription.text", "String or () -> String", "Required"),
                    ),
                ),
                ComponentApiSection(
                    "Footer and Actions",
                    (
                        "Default phone actions stack in reverse visual order; Sm uses " +
                            "two equal columns. Logical placement follows layout " +
                            "direction. onClick runs before the close request; controlled " +
                            "callers may reject it. The catalog Settings placeholder " +
                            "navigates the current page fragment, not a fabricated " +
                            "settings screen."
                    ),
                    listOf(
                        ApiProperty("AlertDialogFooter.content", "@Composable RowScope.() -> Unit", "Required"),
                        ApiProperty("Action/Cancel.onClick", "() -> Unit", "{}"),
                        ApiProperty("enabled", "Boolean", "true"),
                        ApiProperty("disabled", "Boolean", "false"),
                        ApiProperty("Action.variant", "ButtonVariant", "ButtonVariant.Default"),
                        ApiProperty("Cancel.variant", "ButtonVariant", "ButtonVariant.Outline"),
                        ApiProperty("size", "ButtonSize", "ButtonSize.Md"),
                    ),
                ),
            ),
    )
