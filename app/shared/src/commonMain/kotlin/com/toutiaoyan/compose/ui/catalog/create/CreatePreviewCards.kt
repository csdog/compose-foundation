@file:OptIn(androidx.compose.foundation.style.ExperimentalFoundationStyleApi::class)

package com.toutiaoyan.compose.ui.catalog.create

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.style.Style
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.unit.sp
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.unit.dp
import com.composables.icons.lucide.ArrowRight
import com.composables.icons.lucide.Calendar
import com.composables.icons.lucide.ChevronRight
import com.composables.icons.lucide.Lock
import com.composables.icons.lucide.Lucide
import com.composables.icons.lucide.Plus
import com.composables.icons.lucide.RefreshCw
import com.composables.icons.lucide.Search
import com.composables.icons.lucide.Send
import com.composables.icons.lucide.Settings
import com.composables.icons.lucide.X
import com.toutiaoyan.compose.ui.components.alertdialog.AlertDialog
import com.toutiaoyan.compose.ui.components.alertdialog.AlertDialogContent
import com.toutiaoyan.compose.ui.components.badge.Badge
import com.toutiaoyan.compose.ui.components.badge.BadgeVariant
import com.toutiaoyan.compose.ui.components.breadcrumb.Breadcrumb
import com.toutiaoyan.compose.ui.components.breadcrumb.BreadcrumbLink
import com.toutiaoyan.compose.ui.components.breadcrumb.BreadcrumbPage
import com.toutiaoyan.compose.ui.components.breadcrumb.BreadcrumbSeparator
import com.toutiaoyan.compose.ui.components.bubble.Bubble
import com.toutiaoyan.compose.ui.components.bubble.BubbleContent
import com.toutiaoyan.compose.ui.components.bubble.BubbleVariant
import com.toutiaoyan.compose.ui.components.button.Button
import com.toutiaoyan.compose.ui.components.button.ButtonSize
import com.toutiaoyan.compose.ui.components.button.ButtonVariant
import com.toutiaoyan.compose.ui.components.buttongroup.ButtonGroup
import com.toutiaoyan.compose.ui.components.buttongroup.ButtonGroupItem
import com.toutiaoyan.compose.ui.components.card.Card
import com.toutiaoyan.compose.ui.components.card.CardAction
import com.toutiaoyan.compose.ui.components.card.CardDescription
import com.toutiaoyan.compose.ui.components.card.CardContent
import com.toutiaoyan.compose.ui.components.card.CardFooter
import com.toutiaoyan.compose.ui.components.card.CardHeader
import com.toutiaoyan.compose.ui.components.card.CardTitle
import com.toutiaoyan.compose.ui.components.checkbox.Checkbox
import com.toutiaoyan.compose.ui.components.dialog.DialogDescription
import com.toutiaoyan.compose.ui.components.dialog.DialogFooter
import com.toutiaoyan.compose.ui.components.dialog.DialogHeader
import com.toutiaoyan.compose.ui.components.dialog.DialogTitle
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
import com.toutiaoyan.compose.ui.components.icon.Icon
import com.toutiaoyan.compose.ui.components.icon.IconVariant
import com.toutiaoyan.compose.ui.components.inputgroup.InputGroup
import com.toutiaoyan.compose.ui.components.inputgroup.InputGroupAddon
import com.toutiaoyan.compose.ui.components.inputgroup.InputGroupAddonAlign
import com.toutiaoyan.compose.ui.components.inputgroup.InputGroupText
import com.toutiaoyan.compose.ui.components.item.Item
import com.toutiaoyan.compose.ui.components.item.ItemContent
import com.toutiaoyan.compose.ui.components.item.ItemDescription
import com.toutiaoyan.compose.ui.components.item.ItemGroup
import com.toutiaoyan.compose.ui.components.item.ItemMedia
import com.toutiaoyan.compose.ui.components.item.ItemMediaVariant
import com.toutiaoyan.compose.ui.components.item.ItemTitle
import com.toutiaoyan.compose.ui.components.item.ItemVariant
import com.toutiaoyan.compose.ui.components.message.Message
import com.toutiaoyan.compose.ui.components.message.MessageAlign
import com.toutiaoyan.compose.ui.components.message.MessageAvatar
import com.toutiaoyan.compose.ui.components.messagescroller.MessageScroller
import com.toutiaoyan.compose.ui.components.progress.Progress
import com.toutiaoyan.compose.ui.components.radiobutton.RadioButton
import com.toutiaoyan.compose.ui.components.radiobutton.RadioGroup
import com.toutiaoyan.compose.ui.components.select.Select
import com.toutiaoyan.compose.ui.components.select.SelectTrigger
import com.toutiaoyan.compose.ui.components.select.SelectItem
import com.toutiaoyan.compose.ui.components.select.SelectContent
import com.toutiaoyan.compose.ui.components.separator.Separator
import com.toutiaoyan.compose.ui.components.switch.Switch
import com.toutiaoyan.compose.ui.components.text.Text
import com.toutiaoyan.compose.ui.components.text.TextColors
import com.toutiaoyan.compose.ui.components.text.TextVariant
import com.toutiaoyan.compose.ui.components.slider.Slider
import com.toutiaoyan.compose.ui.components.textarea.Textarea
import com.toutiaoyan.compose.ui.components.input.Input
import com.toutiaoyan.compose.ui.theme.currentTheme
import com.toutiaoyan.compose.tailwind.core.twRadius
import com.toutiaoyan.compose.tailwind.core.twSpacing

private val cardContentModifier: Modifier
    @Composable get() = Modifier.fillMaxWidth().padding(horizontal = twSpacing.lg)

@Composable
fun CreateUiElementsCard(modifier: Modifier = Modifier) {
    var message by remember { mutableStateOf("") }
    var name by remember { mutableStateOf("") }
    var alertOpen by remember { mutableStateOf(false) }
    var fruit by remember { mutableStateOf("apple") }
    var emailAlerts by remember { mutableStateOf(true) }
    var compact by remember { mutableStateOf(true) }
    Card(modifier = modifier) {
        Column(
            modifier = cardContentModifier.padding(vertical = twSpacing.lg),
            verticalArrangement = Arrangement.spacedBy(twSpacing.lg),
        ) {
            Row(horizontalArrangement = Arrangement.spacedBy(twSpacing.sm)) {
                Button(onClick = {}) {
                    Text { "Button" }
                    Icon(Lucide.ArrowRight)
                }
                Button(onClick = {}, variant = ButtonVariant.Secondary) { Text { "Secondary" } }
                Button(onClick = {}, variant = ButtonVariant.Outline) { Text { "Outline" } }
            }
            FieldGroup {
                Field {
                    InputGroup {
                        Input(value = name, onValueChange = { name = it }, placeholder = "Name")
                        InputGroupAddon(InputGroupAddonAlign.InlineEnd) {
                            Icon(Lucide.Search, variant = IconVariant.Small)
                        }
                    }
                }
                Field {
                    Textarea(value = message, onValueChange = { message = it }, placeholder = "Message")
                }
            }
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(twSpacing.sm),
            ) {
                Badge { Text { "Badge" } }
                Badge(variant = BadgeVariant.Secondary) { Text { "Secondary" } }
                Badge(variant = BadgeVariant.Outline) { Text { "Outline" } }
                RadioGroup {
                    RadioButton(
                        selected = fruit == "apple",
                        onClick = { fruit = "apple" },
                        label = "Apple",
                    )
                    RadioButton(
                        selected = fruit == "banana",
                        onClick = { fruit = "banana" },
                        label = "Banana",
                    )
                }
                Checkbox(checked = emailAlerts, onCheckedChange = { emailAlerts = it }, label = "Enable email alerts")
                Switch(checked = compact, onCheckedChange = { compact = it }, label = "Enable compact notifications")
            }
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(twSpacing.md),
            ) {
                Button(onClick = { alertOpen = true }, variant = ButtonVariant.Outline) {
                    Text { "Alert Dialog" }
                }
                ButtonGroup {
                    ButtonGroupItem("Button Group", onClick = {})
                    ButtonGroupItem("Mute", onClick = {})
                    ButtonGroupItem("Delete", onClick = {})
                }
            }
        }
    }
    AlertDialog(open = alertOpen, onOpenChange = { alertOpen = it }) {
        AlertDialogContent {
            DialogHeader {
                DialogTitle("Allow accessory to connect?")
                DialogDescription(
                    "Do you want to allow the USB accessory to connect to this device and your data?",
                )
            }
            DialogFooter {
                Button(onClick = { alertOpen = false }, variant = ButtonVariant.Outline) {
                    Text { "Don't allow" }
                }
                Button(onClick = { alertOpen = false }) { Text { "Allow" } }
            }
        }
    }
}

@Composable
fun CreateSidebarNavCard(modifier: Modifier = Modifier) {
    val sections =
        listOf(
            "Planning" to listOf("Documents", "Budget", "Reports", "Goals", "Calendar"),
            "Overview" to listOf("Analytics", "Transactions", "Investments", "Accounts", "Spending"),
            "Support" to listOf("Help Center", "Docs", "Contact Us", "Status", "Community"),
            "Account" to listOf("Profile", "Billing", "Notifications", "Security", "Appearance"),
        )
    // Official layout is a 2×2 grid of section cards (Planning/Overview | Support/Account).
    Column(modifier = modifier, verticalArrangement = Arrangement.spacedBy(twSpacing.md)) {
        sections.chunked(2).forEach { row ->
            Row(horizontalArrangement = Arrangement.spacedBy(twSpacing.md)) {
                row.forEach { (title, entries) ->
                    Card(modifier = Modifier.weight(1f)) {
                        Column(
                            modifier =
                                Modifier
                                    .fillMaxWidth()
                                    .padding(twSpacing.md),
                            verticalArrangement = Arrangement.spacedBy(twSpacing.xs2),
                        ) {
                            Text(variant = TextVariant.LabelSmall, textStyle = TextColors.Muted) { title }
                            entries.forEachIndexed { index, entry ->
                                Button(
                                    onClick = {},
                                    modifier = Modifier.fillMaxWidth(),
                                    variant =
                                        if ((title == "Overview" && index == 0) || (title == "Account" && index == 1)) {
                                            ButtonVariant.Secondary
                                        } else {
                                            ButtonVariant.Ghost
                                        },
                                ) {
                                    Icon(
                                        if (entry == "Calendar") Lucide.Calendar else Lucide.ChevronRight,
                                        variant = IconVariant.Small,
                                    )
                                    Text { entry }
                                }
                            }
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun CreateSavingsTargetsCard(modifier: Modifier = Modifier) {
    Card(modifier = modifier) {
        CardHeader {
            CardTitle { "Savings Targets" }
            CardDescription {
                "Active milestones for 2024 across your portfolio. Monitor how close you are to each savings goal."
            }
        }
        Column(modifier = cardContentModifier, verticalArrangement = Arrangement.spacedBy(twSpacing.md)) {
            SavingsTarget("Retirement", "$420,000", .65f, "65% achieved", "$273,000")
            SavingsTarget("Real Estate", "$85,000", .32f, "32% achieved", "$27,200")
            Text(
                variant = TextVariant.BodySmall,
                textStyle = TextColors.Muted,
                modifier = Modifier.fillMaxWidth(),
            ) { "You have not met your targets for this year." }
        }
    }
}

@Composable
private fun SavingsTarget(
    name: String,
    amount: String,
    progress: Float,
    achievement: String,
    saved: String,
) {
    Column(
        modifier =
            Modifier.fillMaxWidth().background(currentTheme.colors.muted, RoundedCornerShape(twRadius.md))
                .padding(twSpacing.md),
        verticalArrangement = Arrangement.spacedBy(twSpacing.sm),
    ) {
        Text(variant = TextVariant.LabelSmall, textStyle = TextColors.Muted) { name }
        Text(variant = TextVariant.TitleLarge) { amount }
        Progress(progress)
        Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
            Text(variant = TextVariant.BodySmall, textStyle = TextColors.Muted) { achievement }
            Text(variant = TextVariant.BodySmall) { saved }
        }
    }
}

@Composable
fun CreateContributionHistoryCard(modifier: Modifier = Modifier) {
    val compact = isCompactCreatePreview()
    Card(modifier = modifier) {
        CardHeader {
            CardTitle { "Contribution History" }
            CardDescription { "Last 6 months of activity" }
        }
        CardContent {
            BarChart(
                listOf(800f, 1100f, 900f, 1300f, 750f, 1400f),
                listOf("Dec", "Jan", "Feb", "Mar", "Apr", "May"),
                200.dp,
            )
        }
        CardContent {
            if (compact) {
                Column(Modifier.fillMaxWidth(), verticalArrangement = Arrangement.spacedBy(12.dp)) {
                    ContributionSummary("Upcoming", "May 25, 2024", "$1,000 scheduled")
                    ContributionSummary("Auto-Save Plan", "Accelerated", "Recurring weekly")
                }
            } else {
                Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                    ContributionSummary("Upcoming", "May 25, 2024", "$1,000 scheduled", Modifier.weight(1f))
                    ContributionSummary("Auto-Save Plan", "Accelerated", "Recurring weekly", Modifier.weight(1f))
                }
            }
        }
        CardFooter {
            Button("View Full Report", modifier = Modifier.fillMaxWidth(), style = Style { width(1f) })
        }
    }
}

@Composable
private fun ContributionSummary(
    label: String,
    value: String,
    description: String,
    modifier: Modifier = Modifier,
) {
    val colors = currentTheme.colors
    Column(
        modifier =
            modifier.fillMaxWidth().background(colors.muted.copy(alpha = .5f), RoundedCornerShape(10.dp))
                .padding(horizontal = 12.dp, vertical = 10.dp),
        verticalArrangement = Arrangement.spacedBy(4.dp),
    ) {
        Text(
            textStyle =
                TextStyle(
                    fontSize = 12.sp,
                    lineHeight = 18.sp,
                    fontWeight = FontWeight.Medium,
                    letterSpacing = .6.sp,
                    color = colors.mutedForeground,
                ),
        ) {
            label.uppercase()
        }
        Text(textStyle = TextStyle(fontSize = 18.sp, lineHeight = 28.sp, fontWeight = FontWeight.SemiBold)) { value }
        Text(
            textStyle = TextStyle(fontSize = 14.sp, lineHeight = 20.sp, color = colors.mutedForeground),
        ) { description }
    }
}

@Composable
fun CreateClaimableBalanceCard(modifier: Modifier = Modifier) {
    Card(modifier = modifier) {
        Column(
            modifier = cardContentModifier.padding(vertical = twSpacing.lg),
            verticalArrangement = Arrangement.spacedBy(twSpacing.md),
        ) {
            Text(variant = TextVariant.BodySmall, textStyle = TextColors.Muted) { "Claimable Balance" }
            Text(variant = TextVariant.TitleLarge) { "$1,211.29" }
            Badge(variant = BadgeVariant.Outline) { Text { "Pending Setup" } }
            Column(
                modifier =
                    Modifier
                        .fillMaxWidth()
                        .background(currentTheme.colors.muted, RoundedCornerShape(twRadius.md))
                        .padding(twSpacing.md),
                verticalArrangement = Arrangement.spacedBy(twSpacing.sm),
            ) {
                Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                    Text(variant = TextVariant.BodySmall, textStyle = TextColors.Muted) { "Net Royalties" }
                    Text(variant = TextVariant.BodySmall) { "$1,248.75" }
                }
                Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                    Text(variant = TextVariant.BodySmall, textStyle = TextColors.Muted) { "Processing Fee" }
                    Text(variant = TextVariant.BodySmall) { "-$37.46" }
                }
                Separator()
                Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                    Text(variant = TextVariant.BodySmall, textStyle = TextColors.Muted) { "Total Ready to Claim" }
                    Text(variant = TextVariant.LabelLarge) { "$1,211.29 USD" }
                }
            }
            Text(variant = TextVariant.BodySmall, textStyle = TextColors.Muted) {
                "Once your bank is connected, balances over $10.00 are automatically eligible " +
                    "for monthly distribution on the 15th of each month."
            }
        }
    }
}

@Composable
fun CreateDividendIncomeCard(modifier: Modifier = Modifier) {
    val holdings =
        listOf(
            "Vanguard" to "450 Shares",
            "S&P 500 VOO" to "112 Shares",
            "Apple AAPL" to "85 Shares",
            "Realty Income" to "320 Shares",
        )
    Card(modifier = modifier) {
        CardHeader {
            Box(Modifier.fillMaxWidth()) {
                Column(verticalArrangement = Arrangement.spacedBy(twSpacing.xs)) {
                    CardTitle { "Q2 Dividend Income" }
                    CardDescription { "Quarterly dividend payouts across your portfolio holdings." }
                }
                CardAction(modifier = Modifier.align(Alignment.TopEnd)) { Icon(Lucide.X, variant = IconVariant.Small) }
            }
        }
        ItemGroup(modifier = cardContentModifier) {
            holdings.forEachIndexed { index, (name, shares) ->
                Item(variant = ItemVariant.Muted) {
                    ItemContent {
                        ItemTitle(name)
                        ItemDescription(shares)
                    }
                    MiniBars(listOf(.45f, .65f, .5f, if (index == 0) 1f else .7f))
                }
            }
        }
    }
}

@Composable
fun CreateNewMilestoneCard(modifier: Modifier = Modifier) {
    var goal by remember { mutableStateOf("") }
    Card(modifier = modifier) {
        CardHeader {
            CardTitle { "Set a new milestone" }
            CardDescription { "Define your financial target and we'll help you pace your savings." }
        }
        FieldGroup(modifier = cardContentModifier) {
            Field {
                FieldLabel("Goal Name")
                Input(
                    goal,
                    { goal = it },
                    placeholder = "e.g. New Car, Home Downpayment",
                )
            }
            Row(horizontalArrangement = Arrangement.spacedBy(twSpacing.sm)) {
                Field(modifier = Modifier.weight(1f)) {
                    FieldLabel("Target Amount")
                    Input("$15,000", {})
                }
                Field(modifier = Modifier.weight(1f)) {
                    FieldLabel("Target Date")
                    Input("Dec 2025", {})
                }
            }
        }
        Column(modifier = cardContentModifier, verticalArrangement = Arrangement.spacedBy(twSpacing.sm)) {
            Button(onClick = {}, modifier = Modifier.fillMaxWidth()) { Text { "Create Goal" } }
            Button(
                onClick = {},
                modifier = Modifier.fillMaxWidth(),
                variant = ButtonVariant.Outline,
            ) { Text { "Cancel" } }
        }
    }
}

@Composable
fun CreatePayoutThresholdCard(modifier: Modifier = Modifier) {
    var notes by remember { mutableStateOf("") }
    var amount by remember { mutableStateOf(2500f) }
    val currencies = listOf("USD — United States Dollar", "EUR — Euro", "GBP — British Pound", "JPY — Japanese Yen")
    var currency by remember { mutableStateOf(currencies.first()) }
    val actionFill = currentTheme.colors.muted
    Card(modifier = modifier) {
        CardHeader {
            CardTitle { "Payout Threshold" }
            CardDescription { "Set the minimum balance required before a payout is triggered." }
            CardAction {
                Button(
                    onClick = {},
                    variant = ButtonVariant.Ghost,
                    size = ButtonSize.IconSm,
                    style = Style { background(actionFill) },
                ) {
                    Icon(Lucide.X, variant = IconVariant.Small, contentDescription = "Close")
                }
            }
        }
        CardContent {
            FieldGroup {
                Field {
                    FieldLabel("Preferred Currency")
                    Select(value = currency, onValueChange = { currency = it }, modifier = Modifier.fillMaxWidth()) {
                        SelectTrigger(modifier = Modifier.fillMaxWidth())
                        SelectContent { currencies.forEach { SelectItem(it) } }
                    }
                }
                Field {
                    Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                        FieldLabel("Minimum Payout Amount", Modifier.weight(1f).alignByBaseline())
                        Text(
                            Modifier.alignByBaseline(),
                            maxLines = 1,
                            textStyle =
                                TextStyle(
                                    fontSize = 24.sp,
                                    lineHeight = 32.sp,
                                    fontWeight = FontWeight.SemiBold,
                                    fontFeatureSettings = "tnum",
                                ),
                        ) { "$${amount.toInt()}.00" }
                    }
                    Slider(
                        value = amount,
                        onValueChange = { amount = it },
                        valueRange = 50f..10000f,
                        step = 50f,
                        label = "Minimum Payout Amount",
                        getValueLabel = { "$${it.toInt()}.00" },
                    )
                    Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                        FieldDescription("$50 (MIN)")
                        FieldDescription("$10,000 (MAX)")
                    }
                }
                Field {
                    FieldLabel("Notes")
                    Textarea(
                        notes,
                        { notes = it },
                        placeholder = "Add any notes for this payout configuration...",
                        modifier = Modifier.heightIn(min = 100.dp).semantics { contentDescription = "Notes" },
                    )
                }
            }
        }
        CardFooter {
            Button("Save Threshold", modifier = Modifier.fillMaxWidth(), style = Style { width(1f) })
        }
    }
}

@Composable
fun CreateAccountAccessCard(modifier: Modifier = Modifier) {
    var email by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }
    Card(modifier = modifier) {
        CardHeader {
            CardTitle { "Account Access" }
            CardDescription { "Update your credentials or re-authenticate." }
        }
        FieldGroup(modifier = cardContentModifier) {
            Field {
                FieldLabel("Email Address")
                Input(email, { email = it }, placeholder = "artist@studio.inc")
            }
            Field {
                FieldLabel("Current Password")
                Input(
                    password,
                    { password = it },
                    placeholder = "••••••••••••••••••••••••",
                )
            }
        }
        Column(modifier = cardContentModifier, verticalArrangement = Arrangement.spacedBy(twSpacing.md)) {
            Button(onClick = {}, modifier = Modifier.fillMaxWidth()) {
                Icon(Lucide.Lock)
                Text { "Update Security" }
            }
            Item(variant = ItemVariant.Muted) {
                ItemMedia(variant = ItemMediaVariant.Icon) { Icon(Lucide.X) }
                ItemContent {
                    ItemTitle("Danger Zone")
                    ItemDescription("Archive account and remove catalog")
                }
                Icon(Lucide.ArrowRight, variant = IconVariant.Small)
            }
        }
    }
}

private val qrCells =
    listOf(
        "111111100101101111111", "100000101001001000001", "101110101111101011101", "101110100100001011101",
        "101110101010101011101", "100000100111001000001", "111111101010101111111", "000000001101000000000",
        "101011111001111010110", "010100001110010101001", "111010111011101111010", "001101000101000010101",
        "110111101111010111011", "000000001001010001010", "111111101101111101001", "100000100010001001111",
        "101110101011101110100", "101110100110100010011", "101110101000111101110", "100000101101000011001",
        "111111101011101101111",
    )

@Composable
fun CreateQrConnectCard(modifier: Modifier = Modifier) {
    val foreground = currentTheme.colors.foreground
    Card(modifier = modifier) {
        CardHeader {
            CardTitle { "Scan to connect your mobile device" }
            CardDescription { "Open the Ledger mobile app and scan this code to link your device." }
        }
        Box(modifier = cardContentModifier.padding(bottom = twSpacing.lg), contentAlignment = Alignment.Center) {
            Canvas(
                modifier =
                    Modifier.size(168.dp)
                        .background(currentTheme.colors.background, RoundedCornerShape(twRadius.md)).padding(16.dp),
            ) {
                val cell = size.width / 21f
                qrCells.forEachIndexed { y, row ->
                    row.forEachIndexed { x, value ->
                        if (value == '1') {
                            drawRect(
                                foreground,
                                androidx.compose.ui.geometry.Offset(x * cell, y * cell),
                                androidx.compose.ui.geometry.Size(cell, cell),
                            )
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun CreatePaymentsCard(modifier: Modifier = Modifier) {
    val payments =
        listOf(
            Triple("Change transfer limit", "Adjust how much you can send from your balance.", Lucide.Settings),
            Triple("Scheduled transfers", "Set up a transfer to send at a later date.", Lucide.Calendar),
            Triple("Recurring card payments", "Manage your repeated card transactions.", Lucide.RefreshCw),
        )
    Card(modifier = modifier) {
        Column(
            modifier = cardContentModifier.padding(vertical = twSpacing.lg),
            verticalArrangement = Arrangement.spacedBy(twSpacing.md),
        ) {
            Breadcrumb {
                BreadcrumbLink("Home", onClick = {})
                BreadcrumbSeparator()
                BreadcrumbLink("…", onClick = {})
                BreadcrumbSeparator()
                BreadcrumbPage("Payments")
            }
            ItemGroup {
                payments.forEach { (title, description, icon) ->
                    Item(variant = ItemVariant.Muted) {
                        ItemMedia(variant = ItemMediaVariant.Icon) { Icon(icon) }
                        ItemContent {
                            ItemTitle(title)
                            ItemDescription(description)
                        }
                        Icon(Lucide.ArrowRight, variant = IconVariant.Small)
                    }
                }
            }
        }
    }
}

@Composable
fun CreateEmptyDistributeTrackCard(modifier: Modifier = Modifier) {
    Card(modifier = modifier) {
        Empty(modifier = cardContentModifier.padding(vertical = twSpacing.xl)) {
            EmptyMedia { Icon(Lucide.Plus) }
            EmptyHeader {
                EmptyTitle("Distribute Track")
                EmptyDescription(
                    "Upload your first master to start reaching listeners on Spotify, Apple Music, and more.",
                )
            }
            EmptyContent { Button(onClick = {}) { Text { "Create Release" } } }
        }
    }
}

@Composable
fun CreateAnalyticsCard(modifier: Modifier = Modifier) {
    val chartColor = previewChartColors()[0]
    Card(modifier = modifier) {
        CardHeader {
            Box(Modifier.fillMaxWidth()) {
                Column(verticalArrangement = Arrangement.spacedBy(twSpacing.xs)) {
                    CardTitle { "Analytics" }
                }
                CardAction(modifier = Modifier.align(Alignment.TopEnd)) {
                    Button(
                        onClick = {},
                        variant = ButtonVariant.Outline,
                        size = ButtonSize.Sm,
                    ) { Text { "View Analytics" } }
                }
            }
        }
        Column(modifier = cardContentModifier.padding(bottom = twSpacing.lg)) {
            Row(
                horizontalArrangement = Arrangement.spacedBy(twSpacing.sm),
                verticalAlignment = Alignment.CenterVertically,
            ) {
                Text(variant = TextVariant.BodySmall, textStyle = TextColors.Muted) { "418.2K Visitors" }
                Badge { Text { "+10%" } }
            }
            Canvas(modifier = Modifier.fillMaxWidth().height(100.dp)) {
                val path =
                    Path().apply {
                        moveTo(0f, size.height * .62f)
                        lineTo(
                            size.width * .18f,
                            size.height * .45f,
                        )
                        lineTo(size.width * .36f, size.height * .54f)
                        lineTo(size.width * .54f, size.height * .8f)
                        lineTo(size.width * .72f, size.height * .55f)
                        lineTo(
                            size.width,
                            size.height * .53f,
                        )
                    }
                drawPath(path, chartColor, style = Stroke(width = 3f))
            }
        }
    }
}

@Composable
fun CreateNotificationSettingsCard(modifier: Modifier = Modifier) {
    val notifications =
        listOf(
            Triple("Transaction alerts", "Deposits, withdrawals, and transfers.", true),
            Triple("Security alerts", "Login attempts and account changes.", true),
            Triple("Goal milestones", "Updates at 25%, 50%, 75%, and 100%.", false),
            Triple("Market updates", "Daily portfolio summary and price alerts.", false),
        )
    Card(modifier = modifier) {
        CardHeader {
            CardTitle { "Notifications" }
            CardDescription { "Choose which email and push alerts you want to receive." }
        }
        FieldGroup(modifier = cardContentModifier) {
            notifications.forEach { (title, description, checked) ->
                Field {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(twSpacing.md),
                    ) {
                        Checkbox(checked, {}, label = title)
                        Column {
                            Text(variant = TextVariant.LabelLarge) { title }
                            Text(
                                variant = TextVariant.BodySmall,
                                textStyle = TextColors.Muted,
                            ) { description }
                        }
                    }
                }
            }
        }
        Button(onClick = {}, modifier = cardContentModifier) { Text { "Save Preferences" } }
    }
}

@Composable
fun CreatePowerUsageCard(modifier: Modifier = Modifier) {
    Card(modifier = modifier) {
        CardHeader {
            CardTitle { "Power Usage" }
            CardDescription { "Whole Home" }
        }
        Column(
            modifier = cardContentModifier.padding(bottom = twSpacing.lg),
            verticalArrangement = Arrangement.spacedBy(twSpacing.md),
        ) {
            BarChart(
                listOf(.32f, .74f, .82f, .63f, .9f, .76f, 1f, .84f),
                listOf("6a", "8a", "10a", "12p", "2p", "4p", "6p", "8p"),
                140.dp,
            )
            Separator()
            Row(modifier = Modifier.fillMaxWidth()) {
                Column(modifier = Modifier.weight(1f)) {
                    Text(textStyle = TextColors.Muted) { "Currently Using" }
                    Text(
                        variant = TextVariant.TitleMedium,
                    ) { "3.4 kW" }
                }
                Column(modifier = Modifier.weight(1f)) {
                    Text(textStyle = TextColors.Muted) { "Solar Gen" }
                    Text(
                        variant = TextVariant.TitleMedium,
                    ) { "+1.2 kW" }
                }
            }
        }
    }
}

@Composable
fun CreateTransferFundsCard(modifier: Modifier = Modifier) {
    val from = listOf("Main Checking (··8402) — $12,450.00", "Business (··7731) — $8,920.00")
    val to = listOf("High Yield Savings (··1192) — $42,100.00", "Investment (··3349) — $18,200.00")
    Card(modifier = modifier) {
        CardHeader {
            Box(Modifier.fillMaxWidth()) {
                Column(verticalArrangement = Arrangement.spacedBy(twSpacing.xs)) {
                    CardTitle { "Transfer Funds" }
                    CardDescription { "Move money between your connected accounts." }
                }
                CardAction(modifier = Modifier.align(Alignment.TopEnd)) { Icon(Lucide.X, variant = IconVariant.Small) }
            }
        }
        FieldGroup(modifier = cardContentModifier) {
            Field {
                FieldLabel("Amount to Transfer")
                InputGroup {
                    InputGroupAddon(InputGroupAddonAlign.InlineStart) { InputGroupText("$") }
                    Input("1,200.00", {})
                }
            }
            Field {
                FieldLabel("From Account")
                Select(
                    value = from.first(),
                    onValueChange = {},
                    modifier = Modifier.fillMaxWidth(),
                ) {
                    SelectTrigger()
                    SelectContent {
                        from.forEach { SelectItem(it) }
                    }
                }
            }
            Field {
                FieldLabel("To Account")
                Select(
                    value = to.first(),
                    onValueChange = {},
                    modifier = Modifier.fillMaxWidth(),
                ) {
                    SelectTrigger()
                    SelectContent {
                        to.forEach { SelectItem(it) }
                    }
                }
            }
            SummaryBlock(
                "Estimated arrival",
                "Today, Apr 14",
                "Transaction fee   $0.00\nTotal amount   $1,200.00",
                Modifier.fillMaxWidth(),
            )
        }
        Button(onClick = {}, modifier = cardContentModifier) { Text { "Confirm Transfer" } }
    }
}

@Composable
fun CreateMessageScrollerCard(modifier: Modifier = Modifier) {
    Card(modifier = modifier.height(340.dp)) {
        CardHeader {
            Box(Modifier.fillMaxWidth()) {
                Column(verticalArrangement = Arrangement.spacedBy(twSpacing.xs)) {
                    CardTitle { "New Chat" }
                    CardDescription { "How can I help you today?" }
                }
                CardAction(modifier = Modifier.align(Alignment.TopEnd)) {
                    Icon(
                        Lucide.RefreshCw,
                        variant = IconVariant.Small,
                    )
                }
            }
        }
        MessageScroller(modifier = cardContentModifier.weight(1f)) {
            ChatMessage("AI", "Hi! Ask me anything about MessageScroller.", false)
            ChatMessage("Me", "How does auto-scroll work?", true)
            ChatMessage("AI", "It follows new messages until you scroll away.", false)
        }
        InputGroup(modifier = cardContentModifier) {
            InputGroupAddon(InputGroupAddonAlign.InlineStart) {
                Icon(Lucide.Plus, variant = IconVariant.Small)
            }
            Input("", {}, placeholder = "Message MessageScroller…")
            InputGroupAddon(InputGroupAddonAlign.InlineEnd) {
                Icon(Lucide.Send, variant = IconVariant.Small)
            }
        }
    }
}

@Composable
private fun ChatMessage(
    name: String,
    message: String,
    mine: Boolean,
) {
    val align = if (mine) MessageAlign.End else MessageAlign.Start
    Message(align = align, avatar = { MessageAvatar { Text { name } } }) {
        Bubble(align = align) {
            BubbleContent(variant = if (mine) BubbleVariant.Default else BubbleVariant.Muted) { Text { message } }
        }
    }
}

@Composable
private fun SummaryBlock(
    label: String,
    value: String,
    description: String,
    modifier: Modifier = Modifier,
) {
    Column(
        modifier =
            modifier.background(currentTheme.colors.muted, RoundedCornerShape(twRadius.md))
                .padding(twSpacing.md),
        verticalArrangement = Arrangement.spacedBy(twSpacing.xs2),
    ) {
        Text(variant = TextVariant.LabelSmall, textStyle = TextColors.Muted) { label }
        Text(variant = TextVariant.LabelLarge) { value }
        Text(variant = TextVariant.BodySmall, textStyle = TextColors.Muted) { description }
    }
}

@Composable
private fun BarChart(
    values: List<Float>,
    labels: List<String>,
    height: androidx.compose.ui.unit.Dp,
) {
    val color = previewChartColors()[1]
    val maximum = values.maxOrNull()?.coerceAtLeast(1f) ?: 1f
    val corner =
        if (currentTheme.activePreset in
            listOf(
                com.toutiaoyan.compose.ui.tokens.StylePreset.Lyra,
                com.toutiaoyan.compose.ui.tokens.StylePreset.Sera,
            )
        ) {
            0.dp
        } else {
            6.dp
        }
    Row(
        modifier = Modifier.fillMaxWidth().height(height).padding(top = 8.dp),
        horizontalArrangement = Arrangement.spacedBy(twSpacing.xs),
        verticalAlignment = Alignment.Bottom,
    ) {
        values.forEachIndexed { index, value ->
            Column(
                modifier = Modifier.weight(1f).fillMaxHeight(),
                horizontalAlignment = Alignment.CenterHorizontally,
            ) {
                // Each category has an independent plot area. A weight on its only
                // flexible child always filled that area, regardless of the value.
                Box(Modifier.weight(1f).fillMaxWidth(), contentAlignment = Alignment.BottomCenter) {
                    Box(
                        Modifier.widthIn(max = 40.dp).fillMaxWidth()
                            .fillMaxHeight((value / maximum).coerceIn(0f, 1f))
                            .background(color, RoundedCornerShape(topStart = corner, topEnd = corner))
                            .semantics { contentDescription = "${labels[index]}: $value" },
                    )
                }
                Box(Modifier.fillMaxWidth().height(30.dp).padding(top = 8.dp), contentAlignment = Alignment.TopCenter) {
                    Text(
                        textStyle =
                            TextStyle(
                                fontSize = 12.sp,
                                lineHeight = 18.sp,
                                color = currentTheme.colors.mutedForeground,
                            ),
                    ) {
                        labels[index]
                    }
                }
            }
        }
    }
}

@Composable
private fun MiniBars(values: List<Float>) {
    Row(
        modifier = Modifier.width(56.dp).height(28.dp),
        horizontalArrangement = Arrangement.spacedBy(2.dp),
        verticalAlignment = Alignment.Bottom,
    ) {
        values.forEach { value ->
            Box(
                modifier =
                    Modifier.weight(1f).fillMaxHeight(value)
                        .background(previewChartColors()[1], RoundedCornerShape(topStart = 2.dp, topEnd = 2.dp)),
            )
        }
    }
}
