@file:OptIn(androidx.compose.foundation.style.ExperimentalFoundationStyleApi::class)

package com.toutiaoyan.compose.ui.catalog.create

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ColumnScope
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import com.toutiaoyan.compose.ui.components.avatar.Avatar
import com.toutiaoyan.compose.ui.components.avatar.AvatarFallback
import com.toutiaoyan.compose.ui.components.avatar.AvatarGroup
import com.toutiaoyan.compose.ui.components.badge.Badge
import com.toutiaoyan.compose.ui.components.badge.BadgeVariant
import com.toutiaoyan.compose.ui.components.button.Button
import com.toutiaoyan.compose.ui.components.button.ButtonSize
import com.toutiaoyan.compose.ui.components.button.ButtonVariant
import com.toutiaoyan.compose.ui.components.card.Card
import com.toutiaoyan.compose.ui.components.card.CardAction
import com.toutiaoyan.compose.ui.components.card.CardContent
import com.toutiaoyan.compose.ui.components.card.CardDescription
import com.toutiaoyan.compose.ui.components.card.CardHeader
import com.toutiaoyan.compose.ui.components.card.CardTitle
import com.toutiaoyan.compose.ui.components.checkbox.Checkbox
import com.toutiaoyan.compose.ui.components.empty.Empty
import com.toutiaoyan.compose.ui.components.empty.EmptyContent
import com.toutiaoyan.compose.ui.components.empty.EmptyDescription
import com.toutiaoyan.compose.ui.components.empty.EmptyHeader
import com.toutiaoyan.compose.ui.components.empty.EmptyMedia
import com.toutiaoyan.compose.ui.components.empty.EmptyTitle
import com.toutiaoyan.compose.ui.components.field.Field
import com.toutiaoyan.compose.ui.components.field.FieldDescription
import com.toutiaoyan.compose.ui.components.field.FieldGroup
import com.toutiaoyan.compose.ui.components.field.FieldLabel
import com.toutiaoyan.compose.ui.components.progress.Progress
import com.toutiaoyan.compose.ui.components.select.Select
import com.toutiaoyan.compose.ui.components.select.SelectTrigger
import com.toutiaoyan.compose.ui.components.select.SelectItem
import com.toutiaoyan.compose.ui.components.select.SelectContent
import com.toutiaoyan.compose.ui.components.separator.Separator
import com.toutiaoyan.compose.ui.components.skeleton.Skeleton
import com.toutiaoyan.compose.ui.components.text.Text
import com.toutiaoyan.compose.ui.components.text.TextColors
import com.toutiaoyan.compose.ui.components.text.TextVariant
import com.toutiaoyan.compose.ui.components.textarea.Textarea
import com.toutiaoyan.compose.ui.components.input.Input
import com.toutiaoyan.compose.ui.theme.currentTheme
import com.toutiaoyan.compose.tailwind.core.twRadius
import com.toutiaoyan.compose.tailwind.core.twSpacing

private val previewContent: Modifier
    @Composable get() = Modifier.fillMaxWidth().padding(horizontal = twSpacing.lg)

@Composable
fun CreatePreview02StyleOverviewCard(modifier: Modifier = Modifier) {
    Card(modifier) {
        Column(
            previewContent.padding(vertical = twSpacing.lg),
            verticalArrangement = Arrangement.spacedBy(twSpacing.lg),
        ) {
            Text(variant = TextVariant.TitleLarge) { "Default - Inter" }
            Text(textStyle = TextColors.Muted) {
                "Designers love packing quirky glyphs into test phrases. This is a preview of the typography styles."
            }
            repeat(2) { row ->
                Row(horizontalArrangement = Arrangement.spacedBy(twSpacing.sm)) {
                    repeat(6) { column ->
                        val index = row * 6 + column
                        Column(modifier = Modifier.weight(1f), horizontalAlignment = Alignment.CenterHorizontally) {
                            Box(
                                Modifier.fillMaxWidth().height(28.dp)
                                    .background(previewColor(index), RoundedCornerShape(twRadius.md)),
                            )
                            Text(variant = TextVariant.LabelSmall, textStyle = TextColors.Muted) {
                                listOf(
                                    "bg",
                                    "fg",
                                    "primary",
                                    "secondary",
                                    "muted",
                                    "accent",
                                    "border",
                                    "chart 1",
                                    "chart 2",
                                    "chart 3",
                                    "chart 4",
                                    "chart 5",
                                )[index]
                            }
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun CreatePreview02TypographySpecimenCard(modifier: Modifier = Modifier) {
    Card(modifier) {
        Column(previewContent, verticalArrangement = Arrangement.spacedBy(twSpacing.sm)) {
            Text(variant = TextVariant.LabelSmall, textStyle = TextColors.Muted) { "Inherit - Default" }
            Text(variant = TextVariant.TitleLarge) { "Designing with rhythm and hierarchy." }
            Text(textStyle = TextColors.Muted) {
                "A strong body style keeps long-form content readable and balances the visual weight of headings."
            }
            Text(textStyle = TextColors.Muted) {
                "Thoughtful spacing and cadence help paragraphs scan quickly without feeling dense."
            }
        }
        Button(onClick = {}, modifier = previewContent) { Text { "Share Feedback" } }
    }
}

@Composable
fun CreatePreview02IconGridCard(modifier: Modifier = Modifier) {
    Card(modifier) {
        Row(
            previewContent.padding(vertical = twSpacing.lg),
            horizontalArrangement = Arrangement.spacedBy(twSpacing.sm),
        ) {
            repeat(8) { column ->
                Column(modifier = Modifier.weight(1f), verticalArrangement = Arrangement.spacedBy(twSpacing.sm)) {
                    repeat(2) {
                        Box(
                            Modifier.fillMaxWidth().size(30.dp)
                                .background(currentTheme.colors.muted, RoundedCornerShape(twRadius.sm)),
                            contentAlignment = Alignment.Center,
                        ) {
                            Text(variant = TextVariant.LabelSmall) {
                                listOf(
                                    "□",
                                    "!",
                                    "×",
                                    "↗",
                                    "◫",
                                    "…",
                                    "◌",
                                    "+",
                                    "−",
                                    "←",
                                    "→",
                                    "✓",
                                    "⌄",
                                    "›",
                                    "⌕",
                                    "⚙",
                                )[column * 2 + it]
                            }
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun CreatePreview02EnvironmentVariablesCard(modifier: Modifier = Modifier) {
    PreviewCard(
        modifier,
        "Environment Variables",
        "Production · 8 variables",
        footer = {
            Row(previewContent, horizontalArrangement = Arrangement.SpaceBetween) {
                Button(
                    onClick = {},
                    variant = ButtonVariant.Outline,
                ) { Text { "Edit" } }
                Button(onClick = {}) { Text { "Deploy" } }
            }
        },
    ) {
        listOf(
            "DATABASE_URL     ••••••••",
            "NEXT_PUBLIC_API     https://api.example.com",
            "STRIPE_SECRET     ••••••••",
        ).forEach { VariableRow(it) }
    }
}

@Composable
fun CreatePreview02BarChartCard(modifier: Modifier = Modifier) {
    PreviewCard(
        modifier,
        "Traffic channels",
        "Monthly desktop and mobile traffic for the last six months, compare volume and mix " +
            "across platforms and devices at a glance.",
        footer = { Button(onClick = {}, modifier = previewContent) { Text { "View report" } } },
    ) {
        PreviewBars(listOf(.45f, .75f, .58f, .25f, .6f, .66f), listOf("Jan", "Feb", "Mar", "Apr", "May", "Jun"))
        Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceEvenly) {
            Summary("Desktop", "1,224")
            Summary("Mobile", "860")
            Summary("Mix Delta", "+42%")
        }
    }
}

@Composable
fun CreatePreview02PieChartCard(modifier: Modifier = Modifier) {
    PreviewCard(
        modifier,
        "Browser Share",
        "January - June 2026",
        action = { Badge(variant = BadgeVariant.Outline) { Text { "Firefox" } } },
    ) {
        Column(
            Modifier.fillMaxWidth(),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(twSpacing.md),
        ) {
            Box(
                Modifier.size(130.dp).background(currentTheme.colors.primary, RoundedCornerShape(65.dp)),
                contentAlignment = Alignment.Center,
            ) {
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    Text(variant = TextVariant.TitleLarge) { "935" }
                    Text(
                        textStyle = TextColors.Muted,
                    ) { "Visitors" }
                }
            }
            Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                Text { "Firefox" }
                Text(
                    textStyle = TextColors.Muted,
                ) { "31%" }
            }
            Progress(.31f)
        }
    }
}

@Composable
fun CreatePreview02InviteTeamCard(modifier: Modifier = Modifier) {
    var link by remember { mutableStateOf("https://app.co/invite/x8f2k") }
    PreviewCard(
        modifier,
        "Invite Team",
        "Add members to your workspace",
        footer = { Button(onClick = {}, modifier = previewContent) { Text { "Send Invites" } } },
    ) {
        FieldGroup(Modifier.fillMaxWidth()) {
            listOf("alex@example.com" to "Editor", "sam@example.com" to "Viewer").forEach { (email, role) ->
                Row(horizontalArrangement = Arrangement.spacedBy(twSpacing.sm)) {
                    Input(email, {}, modifier = Modifier.weight(1f))
                    Select(
                        value = role,
                        onValueChange = {},
                        modifier = Modifier.weight(.55f),
                    ) {
                        SelectTrigger()
                        SelectContent {
                            SelectItem("Admin")
                            SelectItem("Editor")
                            SelectItem("Viewer")
                        }
                    }
                }
            }
            Button(
                onClick = {},
                variant = ButtonVariant.Outline,
                modifier = Modifier.fillMaxWidth(),
            ) { Text { "Add another" } }
            Separator()
            Field {
                FieldLabel("Or share invite link")
                Row(
                    horizontalArrangement = Arrangement.spacedBy(twSpacing.sm),
                    verticalAlignment = Alignment.CenterVertically,
                ) {
                    Input(link, { link = it }, modifier = Modifier.weight(1f))
                    Button(onClick = {}, size = ButtonSize.Sm, variant = ButtonVariant.Outline) { Text { "Copy" } }
                }
            }
        }
    }
}

@Composable
fun CreatePreview02FeedbackFormCard(modifier: Modifier = Modifier) {
    var feedback by remember { mutableStateOf("") }
    Card(modifier) {
        FieldGroup(previewContent) {
            Field {
                FieldLabel("Topic")
                Select(
                    value = null,
                    onValueChange = {},
                    modifier = Modifier.fillMaxWidth(),
                    placeholder = "Select a topic",
                ) {
                    SelectTrigger()
                    SelectContent {
                        SelectItem("AI")
                        SelectItem("Accounts and Access Controls")
                        SelectItem("Billing")
                        SelectItem("CDN (Firewall, Caching)")
                        SelectItem("CI/CD")
                        SelectItem("Dashboard Interface")
                        SelectItem("Domains")
                        SelectItem("Frameworks")
                        SelectItem("Marketplace and Integrations")
                        SelectItem("Observability")
                        SelectItem("Storage")
                    }
                }
            }
            Field {
                FieldLabel("Feedback")
                Textarea(
                    feedback,
                    { feedback = it },
                    placeholder = "Your feedback helps us improve...",
                )
            }
        }

        Button(onClick = {}, modifier = previewContent) { Text { "Submit" } }
    }
}

@Composable
fun CreatePreview02ReportBugCard(modifier: Modifier = Modifier) {
    var title by remember { mutableStateOf("") }
    var steps by remember { mutableStateOf("") }
    PreviewCard(
        modifier,
        "Report Bug",
        "Help us fix issues faster.",
        footer = {
            Row(
                previewContent,
                horizontalArrangement = Arrangement.spacedBy(twSpacing.sm),
            ) {
                Button(
                    onClick = {},
                    modifier = Modifier.weight(1f),
                    variant = ButtonVariant.Outline,
                ) { Text { "Attach File" } }
                Button(
                    onClick = {},
                    modifier = Modifier.weight(1f),
                ) { Text { "Submit Bug" } }
            }
        },
    ) {
        FieldGroup(Modifier.fillMaxWidth()) {
            Field {
                FieldLabel("Title")
                Input(title, { title = it }, placeholder = "Brief description of the issue")
            }
            Row(horizontalArrangement = Arrangement.spacedBy(twSpacing.sm)) {
                Field(Modifier.weight(1f)) {
                    FieldLabel("Severity")
                    Select(
                        value = "Medium",
                        onValueChange = {},
                        modifier = Modifier.fillMaxWidth(),
                    ) {
                        SelectTrigger()
                        SelectContent {
                            SelectItem("Critical")
                            SelectItem("High")
                            SelectItem("Medium")
                            SelectItem("Low")
                        }
                    }
                }
                Field(Modifier.weight(1f)) {
                    FieldLabel("Component")
                    Select(
                        value = "Dashboard",
                        onValueChange = {},
                        modifier = Modifier.fillMaxWidth(),
                    ) {
                        SelectTrigger()
                        SelectContent {
                            SelectItem("Dashboard")
                            SelectItem("Auth")
                            SelectItem("API")
                            SelectItem("Billing")
                        }
                    }
                }
            }
            Field {
                FieldLabel("Steps to reproduce")
                Textarea(
                    steps,
                    { steps = it },
                    placeholder = "1. Go to\n2. Click on\n3. Observe...",
                )
            }
        }
    }
}

@Composable
fun CreatePreview02SkeletonLoadingCard(modifier: Modifier = Modifier) {
    Card(modifier) {
        Column(
            previewContent.padding(vertical = twSpacing.lg),
            verticalArrangement = Arrangement.spacedBy(twSpacing.md),
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(twSpacing.md),
            ) {
                Skeleton(Modifier.size(40.dp))
                Column(
                    Modifier.weight(1f),
                    verticalArrangement = Arrangement.spacedBy(twSpacing.sm),
                ) {
                    Skeleton(Modifier.fillMaxWidth(.75f).height(16.dp))
                    Skeleton(
                        Modifier.fillMaxWidth(.5f).height(12.dp),
                    )
                }
            }
            repeat(3) { Skeleton(Modifier.fillMaxWidth(if (it == 2) .8f else 1f).height(12.dp)) }
            Row(horizontalArrangement = Arrangement.spacedBy(twSpacing.sm)) {
                repeat(2) {
                    Skeleton(
                        Modifier.width(80.dp).height(32.dp),
                    )
                }
            }
        }
    }
}

@Composable
fun CreatePreview02NoTeamMembersCard(modifier: Modifier = Modifier) {
    Card(modifier) {
        Empty(previewContent.padding(vertical = twSpacing.xl)) {
            EmptyHeader {
                EmptyMedia { AvatarGroup { repeat(3) { Avatar { AvatarFallback { Text { "CN" } } } } } }
                EmptyTitle(
                    "No Team Members",
                )
                EmptyDescription("Invite your team to collaborate on this project.")
            }
            EmptyContent { Button(onClick = {}, size = ButtonSize.Sm) { Text { "Invite Members" } } }
        }
    }
}

@Composable
fun CreatePreview02BookAppointmentCard(modifier: Modifier = Modifier) {
    PreviewCard(
        modifier,
        "Book Appointment",
        "Dr. Sarah Chen · Cardiology",
        footer = { Button(onClick = {}, modifier = previewContent) { Text { "Book Appointment" } } },
    ) {
        FieldGroup(Modifier.fillMaxWidth()) {
            Field {
                FieldLabel("Available on March 18, 2026")
                Row(
                    horizontalArrangement = Arrangement.spacedBy(twSpacing.xs),
                ) {
                    listOf("9:00 AM", "10:30 AM", "11:00 AM", "1:30 PM").forEachIndexed { index, time ->
                        Button(
                            onClick = {},
                            size = ButtonSize.Sm,
                            variant = if (index == 0) ButtonVariant.Secondary else ButtonVariant.Outline,
                        ) { Text { time } }
                    }
                }
            }
            Field {
                FieldLabel("New patient?")
                FieldDescription("Please arrive 15 minutes early.")
            }
        }
    }
}

@Composable
fun CreatePreview02FileUploadCard(modifier: Modifier = Modifier) =
    PreviewEmptyCard(
        modifier,
        "File Upload",
        "Drag and drop or browse",
        "Upload files",
        "PNG, JPG, PDF up to 10MB",
        "Browse Files",
    )

@Composable
fun CreatePreview02UsageCard(modifier: Modifier = Modifier) {
    PreviewCard(modifier, "5 days remaining in cycle") {
        listOf(
            "Edge Requests" to "\$1.83K",
            "Fast Data Transfer" to "\$952.51",
            "Monitoring data points" to "\$901.20",
            "Web Analytics Events" to "\$603.71",
            "ISR Writes" to "524.52K / 2M",
            "Function Duration" to "5.11 GB Hrs / 1K GB Hrs",
        ).forEach { (label, value) ->
            Row(
                Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
            ) {
                Text { label }
                Text(textStyle = TextColors.Muted) { value }
            }
        }
    }
}

@Composable
fun CreatePreview02AnomalyAlertCard(modifier: Modifier = Modifier) =
    PreviewEmptyCard(
        modifier,
        null,
        null,
        "Get alerted for anomalies",
        "Automatically monitor your projects for anomalies and get notified.",
        "Upgrade to Observability Plus",
    )

@Composable
fun CreatePreview02VisitorsCard(modifier: Modifier = Modifier) {
    PreviewCard(
        modifier,
        "Visitors",
        "Last 6 months",
        action = { Badge(variant = BadgeVariant.Secondary) { Text { "+2% vs last month" } } },
    ) {
        PreviewBars(
            listOf(
                .45f,
                .75f,
                .58f,
                .25f,
                .6f,
                .66f,
            ),
            emptyList(),
        )
    }
}

@Composable
fun CreatePreview02NotFoundCard(modifier: Modifier = Modifier) {
    var query by remember { mutableStateOf("") }
    Card(modifier) {
        Empty(previewContent.padding(vertical = twSpacing.xl)) {
            EmptyHeader {
                EmptyTitle("404 - Not Found")
                EmptyDescription(
                    "The page you're looking for doesn't exist. Try searching for what you need below.",
                )
            }
            EmptyContent {
                Input(
                    query,
                    { query = it },
                    placeholder = "Try searching for pages...",
                    modifier = Modifier.fillMaxWidth(),
                )
                Button(onClick = {}, variant = ButtonVariant.Ghost) { Text { "Go to homepage" } }
            }
        }
    }
}

@Composable
fun CreatePreview02GithubProfileCard(modifier: Modifier = Modifier) {
    var name by remember { mutableStateOf("") }
    var bio by remember { mutableStateOf("") }
    PreviewCard(
        modifier,
        "Profile",
        "Manage your profile information.",
        footer = { Button(onClick = {}, modifier = previewContent) { Text { "Save Profile" } } },
    ) {
        FieldGroup(Modifier.fillMaxWidth()) {
            Field {
                FieldLabel("Name")
                Input(
                    name,
                    { name = it },
                    placeholder = "Taylor",
                )
                FieldDescription(
                    "Your name may appear around GitHub where you contribute or are mentioned. " +
                        "You can remove it at any time.",
                )
            }
            Field {
                FieldLabel("Public Email")
                Select(
                    value = "taylor@example.com",
                    onValueChange = {},
                    modifier = Modifier.fillMaxWidth(),
                ) {
                    SelectTrigger()
                    SelectContent {
                        SelectItem("taylor@example.com")
                        SelectItem("work@example.com")
                    }
                }
                FieldDescription("You can manage verified email addresses in your email settings.")
            }
            Field {
                FieldLabel("Bio")
                Textarea(
                    bio,
                    { bio = it },
                    placeholder = "Tell us a little bit about yourself",
                )
                FieldDescription("You can @mention other users and organizations to link to them.")
            }
        }
    }
}

@Composable
fun CreatePreview02ContributorsCard(modifier: Modifier = Modifier) {
    PreviewCard(
        modifier,
        "Contributors",
        action = { Badge(variant = BadgeVariant.Secondary) { Text { "312" } } },
        footer = {
            Text(
                Modifier.padding(start = twSpacing.lg, end = twSpacing.lg, bottom = twSpacing.lg),
                textStyle = TextColors.Muted,
            ) { "+ 810 contributors" }
        },
    ) {
        Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(twSpacing.xs)) {
            listOf(
                "S",
                "V",
                "N",
                "T",
                "T",
                "E",
                "P",
                "B",
                "W",
                "R",
                "V",
                "A",
                "R",
                "V",
                "A",
                "S",
            ).forEach { initial -> Avatar { AvatarFallback { Text { initial } } } }
        }
    }
}

@Composable
fun CreatePreview02ShortcutsCard(modifier: Modifier = Modifier) {
    PreviewCard(modifier, "Shortcuts") {
        listOf(
            "Search" to "K",
            "Quick Actions" to "J",
            "New File" to "N",
            "Save" to "S",
            "Toggle Sidebar" to "B",
        ).forEachIndexed { index, (label, key) ->
            if (index > 0) Separator()
            Row(
                Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
            ) {
                Text { label }
                Row(horizontalArrangement = Arrangement.spacedBy(twSpacing.xs)) {
                    Badge(variant = BadgeVariant.Outline) { Text { "⌘" } }
                    Badge(
                        variant = BadgeVariant.Outline,
                    ) { Text { key } }
                }
            }
        }
    }
}

@Composable
fun CreatePreview02SleepReportCard(modifier: Modifier = Modifier) {
    PreviewCard(
        modifier,
        "Sleep Report",
        "Last night · 7h 24m",
        footer = {
            Row(
                previewContent,
                horizontalArrangement = Arrangement.SpaceBetween,
            ) {
                Badge(variant = BadgeVariant.Outline) { Text { "Good" } }
                Button(
                    onClick = {},
                    variant = ButtonVariant.Outline,
                    size = ButtonSize.Sm,
                ) { Text { "Details" } }
            }
        },
    ) {
        PreviewBars(listOf(.45f, .65f, .8f, .75f, .6f, .7f, .5f, .65f, .45f), emptyList())
        Row(
            Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceEvenly,
        ) {
            Summary("Deep", "2h 10m")
            Summary("Light", "3h 48m")
            Summary("REM", "1h 26m")
            Summary("Score", "84")
        }
    }
}

@Composable
fun CreatePreview02WeeklyFitnessCard(modifier: Modifier = Modifier) =
    PreviewCard(
        modifier,
        "Weekly Fitness Summary",
        "Calories and workout load by day",
        footer = { Button(onClick = {}, modifier = previewContent) { Text { "View details" } } },
    ) {
        PreviewBars(
            listOf(
                .84f,
                .52f,
                .73f,
                .66f,
                .91f,
                .48f,
                .61f,
            ),
            listOf("M", "T", "W", "T", "F", "S", "S"),
        )
    }

@Composable
fun CreatePreview02CodespacesCard(modifier: Modifier = Modifier) =
    PreviewEmptyCard(
        modifier,
        "Codespaces",
        "Your workspaces in the cloud",
        "No codespaces",
        "You don't have any codespaces with this repository checked out",
        "Create Codespace",
    )

@Composable
fun CreatePreview02InvoiceCard(modifier: Modifier = Modifier) {
    PreviewCard(
        modifier,
        "Invoice #INV-2847",
        "Due March 30, 2026",
        action = { Badge(variant = BadgeVariant.Secondary) { Text { "Pending" } } },
        footer = {
            Row(previewContent, horizontalArrangement = Arrangement.SpaceBetween) {
                Button(
                    onClick = {},
                    size = ButtonSize.Sm,
                    variant = ButtonVariant.Outline,
                ) { Text { "Download PDF" } }
                Button(onClick = {}, size = ButtonSize.Sm) { Text { "Pay Now" } }
            }
        },
    ) {
        listOf(
            "Design System License" to "\$499.00",
            "Priority Support" to "\$1,188.00",
            "Custom Components" to "\$750.00",
            "Total Due" to "\$2,437.00",
        ).forEach { (item, amount) ->
            Row(
                Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
            ) {
                Text { item }
                Text { amount }
            }
        }
    }
}

@Composable
fun CreatePreview02ShippingAddressCard(modifier: Modifier = Modifier) {
    var street by remember { mutableStateOf("") }
    var city by remember { mutableStateOf("") }
    var save by remember { mutableStateOf(true) }
    PreviewCard(
        modifier,
        "Shipping Address",
        "Where should we deliver?",
        footer = {
            Row(
                previewContent,
                horizontalArrangement = Arrangement.spacedBy(twSpacing.sm),
            ) {
                Button(
                    onClick = {},
                    modifier = Modifier.weight(1f),
                    size = ButtonSize.Sm,
                    variant = ButtonVariant.Outline,
                ) { Text { "Cancel" } }
                Button(
                    onClick = {},
                    modifier = Modifier.weight(1f),
                    size = ButtonSize.Sm,
                ) { Text { "Save Address" } }
            }
        },
    ) {
        FieldGroup(Modifier.fillMaxWidth()) {
            Field {
                FieldLabel("Street address")
                Input(
                    street,
                    { street = it },
                    placeholder = "123 Main Street",
                )
            }
            Field {
                FieldLabel("Apt / Suite")
                Input(
                    "",
                    {},
                    placeholder = "Apt 4B",
                )
            }
            Row(horizontalArrangement = Arrangement.spacedBy(twSpacing.sm)) {
                Field(Modifier.weight(1f)) {
                    FieldLabel("City")
                    Input(
                        city,
                        { city = it },
                        placeholder = "San Francisco",
                    )
                }
                Field(Modifier.weight(1f)) {
                    FieldLabel("State")
                    Select(value = "California", onValueChange = {}, modifier = Modifier.fillMaxWidth()) {
                        SelectTrigger()
                        SelectContent {
                            SelectItem("California")
                            SelectItem("New York")
                            SelectItem("Texas")
                        }
                    }
                }
            }
            Checkbox(save, { save = it }, label = "Save as default address")
        }
    }
}

@Composable
fun CreatePreview02ObservabilityCard(modifier: Modifier = Modifier) =
    PreviewCard(
        modifier,
        "Observability Plus is replacing Monitoring",
        "Switch to the improved way to explore your data, with natural language. " +
            "Monitoring will no longer be available on the Pro plan in November, 2025",
        footer = {
            Row(
                previewContent,
                horizontalArrangement = Arrangement.SpaceBetween,
            ) {
                Button(onClick = {}) { Text { "Create Query" } }
                Badge(variant = BadgeVariant.Secondary) { Text { "Warning" } }
            }
        },
    ) {
        Box(
            Modifier.fillMaxWidth().height(96.dp)
                .background(currentTheme.colors.primary.copy(alpha = .45f), RoundedCornerShape(twRadius.md)),
        )
    }

@Composable
fun CreatePreview02ActivateAgentCard(modifier: Modifier = Modifier) =
    PreviewCard(
        modifier,
        "Activate Agent",
        "Give your agent the tools it needs to start working.",
        footer = {
            Button(
                onClick = {},
                modifier = previewContent,
            ) { Text { "Activate Agent" } }
        },
    ) {
        Text(
            textStyle = TextColors.Muted,
        ) { "Your agent can access your project and run tasks in a secure environment." }
    }

@Composable
fun CreatePreview02AssignIssueCard(modifier: Modifier = Modifier) {
    PreviewCard(
        modifier,
        "Assign Issue",
        "Select users to assign to this issue.",
        action = { Button(onClick = {}, size = ButtonSize.Sm, variant = ButtonVariant.Outline) { Text { "+" } } },
    ) {
        Field {
            FieldLabel("Assignees")
            Row(
                horizontalArrangement = Arrangement.spacedBy(twSpacing.sm),
                verticalAlignment = Alignment.CenterVertically,
            ) {
                Avatar { AvatarFallback { Text { "S" } } }
                Text { "shadcn" }
                Badge(variant = BadgeVariant.Outline) { Text { "Assigned" } }
            }
        }
        Input("", {}, placeholder = "Select an item...")
    }
}

@Composable
fun CreatePreview02BarVisualizerCard(modifier: Modifier = Modifier) {
    var state by remember { mutableStateOf("Speaking") }
    PreviewCard(
        modifier,
        "Audio Frequency Visualizer",
        "Real-time frequency band visualization with animated state transitions",
    ) {
        Row(
            Modifier.fillMaxWidth().height(140.dp)
                .background(currentTheme.colors.muted, RoundedCornerShape(twRadius.md)).padding(twSpacing.md),
            horizontalArrangement = Arrangement.spacedBy(twSpacing.xs),
            verticalAlignment = Alignment.CenterVertically,
        ) {
            listOf(
                .28f,
                .52f,
                .8f,
                .44f,
                .68f,
                .9f,
                .58f,
                .36f,
                .72f,
                .48f,
                .82f,
                .62f,
                .32f,
                .55f,
                .76f,
            ).forEachIndexed { index, value ->
                Box(
                    Modifier.weight(1f).height((28 + value * 80).dp).background(
                        if (state == "Speaking") previewColor(index) else currentTheme.colors.border,
                        RoundedCornerShape(8.dp),
                    ),
                )
            }
        }
        Row(horizontalArrangement = Arrangement.spacedBy(twSpacing.sm)) {
            listOf("Connecting", "Listening", "Speaking").forEach { label ->
                Button(
                    onClick = { state = label },
                    size = ButtonSize.Sm,
                    variant = if (state == label) ButtonVariant.Secondary else ButtonVariant.Outline,
                ) { Text { label } }
            }
        }
    }
}

@Composable
fun CreatePreview02AnalyticsCard(modifier: Modifier = Modifier) {
    PreviewCard(
        modifier,
        "Analytics",
        action = {
            Button(
                onClick = {},
                size = ButtonSize.Sm,
                variant = ButtonVariant.Outline,
            ) { Text { "View Analytics" } }
        },
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(twSpacing.sm),
        ) {
            Text { "418.2K Visitors" }
            Badge { Text { "+10%" } }
        }
        PreviewBars(listOf(.7f, .95f, .8f, .32f, .72f, .76f), emptyList())
    }
}

@Composable
fun CreatePreview02LiveWaveformCard(modifier: Modifier = Modifier) {
    var listening by remember { mutableStateOf(false) }
    var processing by remember { mutableStateOf(true) }
    var scrolling by remember { mutableStateOf(false) }
    PreviewCard(modifier, "Live Audio Waveform", "Real-time microphone input visualization with audio reactivity") {
        Row(
            Modifier.fillMaxWidth().height(80.dp).background(currentTheme.colors.muted, RoundedCornerShape(twRadius.md))
                .padding(horizontal = twSpacing.sm),
            horizontalArrangement = Arrangement.spacedBy(3.dp),
            verticalAlignment = Alignment.CenterVertically,
        ) {
            val bars =
                if (scrolling) {
                    listOf(
                        .2f,
                        .36f,
                        .56f,
                        .3f,
                        .72f,
                        .45f,
                        .9f,
                        .55f,
                        .68f,
                        .32f,
                        .78f,
                        .5f,
                        .26f,
                        .62f,
                        .4f,
                    )
                } else {
                    listOf(.2f, .28f, .4f, .6f, .76f, .9f, .7f, .5f, .7f, .9f, .76f, .6f, .4f, .28f, .2f)
                }
            bars.forEachIndexed { index, value ->
                Box(
                    Modifier.weight(1f).height((8 + value * 56).dp).background(
                        if (processing || listening) previewColor(index) else currentTheme.colors.border,
                        RoundedCornerShape(6.dp),
                    ),
                )
            }
        }
        Row(horizontalArrangement = Arrangement.spacedBy(twSpacing.sm)) {
            Button(
                onClick = {
                    listening = !listening
                    if (listening) processing = false
                },
                size = ButtonSize.Sm,
                variant = if (listening) ButtonVariant.Secondary else ButtonVariant.Outline,
            ) { Text { if (listening) "Stop Listening" else "Start Listening" } }
            Button(
                onClick = {
                    processing = !processing
                    if (processing) listening = false
                },
                size = ButtonSize.Sm,
                variant = if (processing) ButtonVariant.Secondary else ButtonVariant.Outline,
            ) { Text { if (processing) "Stop Processing" else "Start Processing" } }
            Button(
                onClick = { scrolling = !scrolling },
                size = ButtonSize.Sm,
                variant = ButtonVariant.Outline,
            ) { Text { if (scrolling) "Scrolling" else "Static" } }
        }
    }
}

@Composable
fun CreatePreview02ContributionsActivityCard(modifier: Modifier = Modifier) {
    var privateProfile by remember { mutableStateOf(false) }
    PreviewCard(
        modifier,
        "Contributions & Activity",
        "Manage your contributions and activity visibility.",
        footer = { Button(onClick = {}, modifier = previewContent) { Text { "Save Changes" } } },
    ) {
        Checkbox(privateProfile, { privateProfile = it }, label = "Make profile private and hide activity")
        Text(textStyle = TextColors.Muted) {
            "Enabling this will hide your contributions and activity from your GitHub profile " +
                "and social features like followers, stars, feeds, leaderboards and releases."
        }
    }
}

/** Official `preview` registry layout, arranged in seven independently scrollable columns. */
@Composable
fun CreatePreview02CanvasColumns(gap: Dp) {
    Preview02Column(gap) {
        CreatePreview02StyleOverviewCard()
        CreatePreview02TypographySpecimenCard()
        CreatePreview02CodespacesCard()
        CreatePreview02InvoiceCard()
    }
    Preview02Column(gap) {
        CreatePreview02IconGridCard()
        CreateUiElementsCard()
        CreatePreview02ObservabilityCard()
        CreatePreview02ShippingAddressCard()
    }
    Preview02Column(gap) {
        CreatePreview02EnvironmentVariablesCard()
        CreatePreview02BarChartCard()
        CreatePreview02InviteTeamCard()
        CreatePreview02ActivateAgentCard()
    }
    Preview02Column(gap) {
        CreatePreview02SkeletonLoadingCard()
        CreatePreview02PieChartCard()
        CreatePreview02NoTeamMembersCard()
        CreatePreview02ReportBugCard()
        CreatePreview02ContributorsCard()
    }
    Preview02Column(gap) {
        CreatePreview02FeedbackFormCard()
        CreatePreview02BookAppointmentCard()
        CreatePreview02SleepReportCard()
        CreatePreview02GithubProfileCard()
    }
    Preview02Column(gap) {
        CreatePreview02WeeklyFitnessCard()
        CreatePreview02FileUploadCard()
        CreatePreview02AnalyticsCard()
        CreatePreview02UsageCard()
        CreatePreview02ShortcutsCard()
    }
    Preview02Column(gap) {
        CreatePreview02AnomalyAlertCard()
        CreatePreview02LiveWaveformCard()
        CreatePreview02VisitorsCard()
        CreatePreview02ContributionsActivityCard()
        CreatePreview02NotFoundCard()
    }
}

@Composable
private fun PreviewCard(
    modifier: Modifier,
    title: String,
    description: String? = null,
    action: @Composable (() -> Unit)? = null,
    footer: @Composable (() -> Unit)? = null,
    content: @Composable ColumnScope.() -> Unit,
) {
    Card(modifier) {
        CardHeader {
            if (action != null) {
                Box(Modifier.fillMaxWidth()) {
                    Column(verticalArrangement = Arrangement.spacedBy(twSpacing.xs)) {
                        CardTitle { title }
                        if (description != null) CardDescription { description }
                    }
                    CardAction(modifier = Modifier.align(Alignment.TopEnd)) { action() }
                }
            } else {
                CardTitle { title }
                if (description != null) CardDescription { description }
            }
        }
        CardContent {
            Column(previewContent, verticalArrangement = Arrangement.spacedBy(twSpacing.md), content = content)
        }
        if (footer != null) {
            footer()
        }
    }
}

@Composable
private fun PreviewEmptyCard(
    modifier: Modifier,
    title: String?,
    description: String?,
    emptyTitle: String,
    emptyDescription: String,
    button: String,
) {
    Card(modifier) {
        if (title != null) {
            CardHeader {
                CardTitle { title }
                if (description != null) CardDescription { description }
            }
        }
        Empty(previewContent.padding(vertical = twSpacing.xl)) {
            EmptyHeader {
                EmptyMedia { Text { "↑" } }
                EmptyTitle(emptyTitle)
                EmptyDescription(emptyDescription)
            }
            EmptyContent { Button(onClick = {}) { Text { button } } }
        }
    }
}

@Composable
private fun Preview02Column(
    gap: Dp,
    content: @Composable ColumnScope.() -> Unit,
) {
    Column(
        Modifier.width(320.dp).fillMaxHeight().verticalScroll(rememberScrollState()),
        verticalArrangement = Arrangement.spacedBy(gap),
        content = content,
    )
}

@Composable
private fun PreviewBars(
    values: List<Float>,
    labels: List<String>,
) {
    Row(
        Modifier.fillMaxWidth().height(130.dp),
        horizontalArrangement = Arrangement.spacedBy(twSpacing.xs),
        verticalAlignment = Alignment.Bottom,
    ) {
        values.forEachIndexed { index, value ->
            Column(
                Modifier.weight(1f).fillMaxHeight(),
                verticalArrangement = Arrangement.Bottom,
                horizontalAlignment = Alignment.CenterHorizontally,
            ) {
                Box(
                    Modifier.weight(value).fillMaxWidth().background(
                        previewColor(index),
                        RoundedCornerShape(topStart = twRadius.sm, topEnd = twRadius.sm),
                    ),
                )
                if (labels.isNotEmpty()) {
                    Text(
                        variant = TextVariant.LabelSmall,
                        textStyle = TextColors.Muted,
                    ) { labels[index] }
                }
            }
        }
    }
}

@Composable
private fun VariableRow(value: String) =
    Text(
        Modifier.fillMaxWidth().background(currentTheme.colors.muted, RoundedCornerShape(twRadius.sm))
            .padding(twSpacing.sm),
        variant = TextVariant.LabelSmall,
    ) { value }

@Composable
private fun Summary(
    label: String,
    value: String,
) = Column(horizontalAlignment = Alignment.CenterHorizontally) {
    Text(
        variant = TextVariant.LabelSmall,
        textStyle = TextColors.Muted,
    ) { label }
    Text { value }
}

@Composable
private fun previewColor(index: Int) =
    when (index % 4) {
        0 -> currentTheme.colors.primary
        1 -> currentTheme.colors.secondary
        2 -> currentTheme.colors.muted
        else -> currentTheme.colors.accent
    }
