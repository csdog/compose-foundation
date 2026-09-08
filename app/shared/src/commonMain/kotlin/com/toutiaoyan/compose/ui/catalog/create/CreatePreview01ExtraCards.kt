@file:OptIn(androidx.compose.foundation.style.ExperimentalFoundationStyleApi::class)

package com.toutiaoyan.compose.ui.catalog.create

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
import androidx.compose.foundation.style.Style
import androidx.compose.foundation.style.contentPadding
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.composables.icons.lucide.ArrowRight
import com.composables.icons.lucide.Calendar
import com.composables.icons.lucide.Lock
import com.composables.icons.lucide.Lucide
import com.composables.icons.lucide.Plus
import com.composables.icons.lucide.RefreshCw
import com.composables.icons.lucide.Search
import com.composables.icons.lucide.X
import com.toutiaoyan.compose.ui.components.accordion.Accordion
import com.toutiaoyan.compose.ui.components.accordion.AccordionItem
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
import com.toutiaoyan.compose.ui.components.empty.Empty
import com.toutiaoyan.compose.ui.components.empty.EmptyContent
import com.toutiaoyan.compose.ui.components.empty.EmptyDescription
import com.toutiaoyan.compose.ui.components.empty.EmptyHeader
import com.toutiaoyan.compose.ui.components.empty.EmptyMedia
import com.toutiaoyan.compose.ui.components.empty.EmptyTitle
import com.toutiaoyan.compose.ui.components.field.Field
import com.toutiaoyan.compose.ui.components.field.FieldDescription
import com.toutiaoyan.compose.ui.components.spinner.Spinner
import com.toutiaoyan.compose.ui.components.field.FieldGroup
import com.toutiaoyan.compose.ui.components.field.FieldLabel
import com.toutiaoyan.compose.ui.components.icon.Icon
import com.toutiaoyan.compose.ui.components.icon.IconVariant
import com.toutiaoyan.compose.ui.components.inputgroup.InputGroup
import com.toutiaoyan.compose.ui.components.inputgroup.InputGroupAddon
import com.toutiaoyan.compose.ui.components.inputgroup.InputGroupAddonAlign
import com.toutiaoyan.compose.ui.components.item.Item
import com.toutiaoyan.compose.ui.components.item.ItemActions
import com.toutiaoyan.compose.ui.components.item.ItemContent
import com.toutiaoyan.compose.ui.components.item.ItemDescription
import com.toutiaoyan.compose.ui.components.item.ItemGroup
import com.toutiaoyan.compose.ui.components.item.ItemMedia
import com.toutiaoyan.compose.ui.components.item.ItemMediaVariant
import com.toutiaoyan.compose.ui.components.item.ItemTitle
import com.toutiaoyan.compose.ui.components.item.ItemVariant
import com.toutiaoyan.compose.ui.components.progress.Progress
import com.toutiaoyan.compose.ui.components.radiobutton.RadioButton
import com.toutiaoyan.compose.ui.components.radiobutton.RadioGroup
import com.toutiaoyan.compose.ui.components.select.Select
import com.toutiaoyan.compose.ui.components.select.SelectTrigger
import com.toutiaoyan.compose.ui.components.select.SelectItem
import com.toutiaoyan.compose.ui.components.select.SelectContent
import com.toutiaoyan.compose.ui.components.separator.Separator
import com.toutiaoyan.compose.ui.components.skeleton.Skeleton
import com.toutiaoyan.compose.ui.components.slider.Slider
import com.toutiaoyan.compose.ui.components.switch.Switch
import com.toutiaoyan.compose.ui.components.text.Text
import com.toutiaoyan.compose.ui.components.text.TextColors
import com.toutiaoyan.compose.ui.components.text.TextVariant
import com.toutiaoyan.compose.ui.components.input.Input
import com.toutiaoyan.compose.ui.theme.currentTheme
import com.toutiaoyan.compose.tailwind.core.twRadius
import com.toutiaoyan.compose.tailwind.core.twSpacing

private val extraCardContent: Modifier
    @Composable get() = Modifier.fillMaxWidth().padding(horizontal = twSpacing.lg)

@Composable
fun CreateIndexInvestingCard(modifier: Modifier = Modifier) {
    Card(modifier) {
        CardHeader {
            CardTitle { "Dollar-Cost Averaging" }
            CardDescription { "A strategy for building wealth over time." }
        }
        Text(modifier = extraCardContent.padding(bottom = twSpacing.lg), textStyle = TextColors.Muted) {
            "Over time, this smooths out the average cost of your investments. " +
                "When prices drop, your fixed amount buys more shares. When prices rise, you buy fewer."
        }
    }
}

@Composable
fun CreateSyncingStateCard(modifier: Modifier = Modifier) {
    // Official syncing-state.tsx:
    // Card > CardContent(p-0) > Empty(p-4) >
    //   EmptyHeader { EmptyMedia(icon)+Spinner, Title, Description }
    //   EmptyContent { Outline Cancel }
    Card(modifier = modifier, style = Style { contentPadding(0.dp) }) {
        CardContent {
            val emptyPadding = twSpacing.lg
            Empty(style = Style { contentPadding(emptyPadding) }) {
                EmptyHeader {
                    EmptyMedia { Spinner() }
                    EmptyTitle("Syncing your accounts")
                    EmptyDescription(
                        "We're pulling in your latest transactions. This usually takes a few seconds.",
                    )
                }
                EmptyContent {
                    Button(onClick = {}, variant = ButtonVariant.Outline, size = ButtonSize.Sm) {
                        Text { "Cancel" }
                    }
                }
            }
        }
    }
}

@Composable
fun CreatePreferencesCard(modifier: Modifier = Modifier) {
    var publicStatistics by remember { mutableStateOf(true) }
    var emailNotifications by remember { mutableStateOf(true) }
    Card(modifier) {
        CardHeader {
            Box(Modifier.fillMaxWidth()) {
                Column(verticalArrangement = Arrangement.spacedBy(twSpacing.xs)) {
                    CardTitle { "Preferences" }
                    CardDescription { "Manage your account settings and notifications." }
                }
                CardAction(modifier = Modifier.align(Alignment.TopEnd)) { Icon(Lucide.X, variant = IconVariant.Small) }
            }
        }
        FieldGroup(extraCardContent) {
            Field {
                FieldLabel("Default Currency")
                Select(
                    value = "USD — United States Dollar",
                    onValueChange = {},
                    modifier = Modifier.fillMaxWidth(),
                ) {
                    SelectTrigger()
                    SelectContent {
                        SelectItem("USD — United States Dollar")
                        SelectItem("EUR — Euro")
                        SelectItem("GBP — British Pound")
                        SelectItem("JPY — Japanese Yen")
                    }
                }
            }
            Separator()
            SettingSwitch(
                "Public Statistics",
                "Allow others to see your total stream count and listening activity",
                publicStatistics,
            ) { publicStatistics = it }
            Separator()
            SettingSwitch(
                "Email Notifications",
                "Monthly royalty reports and distribution updates",
                emailNotifications,
            ) { emailNotifications = it }
        }
        Row(extraCardContent, horizontalArrangement = Arrangement.SpaceBetween) {
            Button(onClick = {}, variant = ButtonVariant.Outline) { Text { "Reset" } }
            Button(onClick = {}) { Text { "Save Preferences" } }
        }
    }
}

@Composable
fun CreateSavingsProgressCard(modifier: Modifier = Modifier) {
    Card(modifier) {
        Column(
            extraCardContent.padding(vertical = twSpacing.lg),
            verticalArrangement = Arrangement.spacedBy(twSpacing.md),
        ) {
            Column(
                modifier =
                    Modifier.fillMaxWidth()
                        .background(currentTheme.colors.muted, RoundedCornerShape(twRadius.lg)).padding(twSpacing.xl),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.spacedBy(twSpacing.sm),
            ) {
                Text(variant = TextVariant.TitleLarge) { "$24,000" }
                Text(textStyle = TextColors.Muted) { "80% of $30,000" }
                Progress(.8f, modifier = Modifier.fillMaxWidth())
            }
            MetricRow("Projected Finish", "October 2024")
            Separator()
            MetricRow("Monthly Average", "$1,250")
            Separator()
            MetricRow("Top Contributor", "Auto-Transfer")
        }
    }
}

@Composable
fun CreateKitchenIslandCard(modifier: Modifier = Modifier) {
    var enabled by remember { mutableStateOf(true) }
    var scene by remember { mutableStateOf("Cooking") }
    var brightness by remember { mutableStateOf(90f) }
    var colorTemp by remember { mutableStateOf(70f) }
    var volume by remember { mutableStateOf(30f) }
    var fade by remember { mutableStateOf(0f) }
    val scenes =
        mapOf(
            "Cooking" to listOf(90f, 70f, 30f, 0f),
            "Dining" to listOf(50f, 40f, 20f, 60f),
            "Nightlight" to listOf(15f, 20f, 0f, 80f),
            "Focus" to listOf(100f, 85f, 0f, 0f),
        )

    fun applyScene(name: String) {
        scene = name
        val preset = scenes[name] ?: return
        brightness = preset[0]
        colorTemp = preset[1]
        volume = preset[2]
        fade = preset[3]
    }
    Card(modifier) {
        CardHeader {
            Box(Modifier.fillMaxWidth()) {
                Column(verticalArrangement = Arrangement.spacedBy(twSpacing.xs)) {
                    CardTitle { "Kitchen Island" }
                    CardDescription { "Hue Color Ambient" }
                }
                CardAction(modifier = Modifier.align(Alignment.TopEnd)) { Switch(enabled, { enabled = it }) }
            }
        }
        Column(
            extraCardContent.padding(bottom = twSpacing.lg),
            verticalArrangement = Arrangement.spacedBy(twSpacing.md),
        ) {
            Row(horizontalArrangement = Arrangement.spacedBy(twSpacing.xs)) {
                scenes.keys.forEach { name ->
                    Button(
                        onClick = { applyScene(name) },
                        size = ButtonSize.Sm,
                        variant = if (scene == name) ButtonVariant.Secondary else ButtonVariant.Outline,
                        enabled = enabled,
                    ) { Text { name } }
                }
            }
            ItemGroup {
                KitchenSliderRow("Brightness", brightness, enabled) { brightness = it }
                KitchenSliderRow("Color Temp", colorTemp, enabled) { colorTemp = it }
                KitchenSliderRow("Volume", volume, enabled) { volume = it }
                KitchenSliderRow("Fade", fade, enabled) { fade = it }
            }
        }
    }
}

@Composable
private fun KitchenSliderRow(
    label: String,
    value: Float,
    enabled: Boolean,
    onValueChange: (Float) -> Unit,
) {
    Item(variant = ItemVariant.Muted) {
        ItemMedia(variant = ItemMediaVariant.Icon) { Icon(Lucide.RefreshCw, variant = IconVariant.Small) }
        ItemContent { ItemTitle(label) }
        ItemActions(modifier = Modifier.weight(1f)) {
            Slider(
                value = value,
                onValueChange = onValueChange,
                valueRange = 0f..100f,
                enabled = enabled,
                modifier = Modifier.fillMaxWidth(),
            )
        }
    }
}

@Composable
fun CreateRecentTransactionsCard(modifier: Modifier = Modifier) {
    val transactions =
        listOf(
            Triple("Blue Bottle Coffee", "Food & Drink · Today, 10:24 AM", "-$6.50"),
            Triple("Whole Foods Market", "Groceries · Yesterday", "-$142.30"),
            Triple("Stripe Payout", "Income · Oct 12", "+$4,200.00"),
            Triple("Uber Technologies", "Transport · Oct 11", "-$24.10"),
            Triple("Netflix Subscription", "Entertainment · Oct 10", "-$19.99"),
        )
    Card(modifier) {
        CardHeader {
            Box(Modifier.fillMaxWidth()) {
                Column(verticalArrangement = Arrangement.spacedBy(twSpacing.xs)) {
                    CardTitle { "Recent Transactions" }
                    CardDescription { "Your latest account activity." }
                }
                CardAction(modifier = Modifier.align(Alignment.TopEnd)) {
                    Button(
                        onClick = {},
                        size = ButtonSize.Sm,
                        variant = ButtonVariant.Outline,
                    ) { Text { "View All" } }
                }
            }
        }
        ItemGroup(extraCardContent.padding(bottom = twSpacing.lg)) {
            transactions.forEach { (title, description, amount) ->
                Item(variant = ItemVariant.Muted) {
                    ItemMedia(variant = ItemMediaVariant.Icon) { Icon(Lucide.ArrowRight, variant = IconVariant.Small) }
                    ItemContent {
                        ItemTitle(title)
                        ItemDescription(description)
                    }
                    Text(variant = TextVariant.LabelLarge) { amount }
                }
            }
        }
    }
}

@Composable
fun CreateFaqCard(modifier: Modifier = Modifier) {
    var expanded by remember { mutableStateOf(setOf("security")) }
    Card(modifier) {
        Column(
            extraCardContent.padding(vertical = twSpacing.lg),
            verticalArrangement = Arrangement.spacedBy(twSpacing.md),
        ) {
            Row {
                listOf("General", "Billing", "Goals").forEachIndexed { index, label ->
                    Button(
                        label,
                        onClick = {},
                        modifier = Modifier.weight(1f),
                        size = ButtonSize.Sm,
                        variant = if (index == 0) ButtonVariant.Secondary else ButtonVariant.Outline,
                    )
                }
            }
            Accordion(value = expanded, onValueChange = { expanded = it }) {
                AccordionItem("security", "How secure is my financial data with Ledger?") {
                    Text(textStyle = TextColors.Muted) {
                        "We use bank-level AES-256 encryption, SOC 2 Type II certified " +
                            "infrastructure, and read-only access tokens."
                    }
                }
                AccordionItem("accounts", "How do I connect my bank or investment accounts?") {
                    Text(
                        textStyle = TextColors.Muted,
                    ) { "Go to Settings > Linked Accounts and search for your institution." }
                }
                AccordionItem("tax", "Can I export my data for tax purposes?") {
                    Text(
                        textStyle = TextColors.Muted,
                    ) { "Navigate to Reports > Tax Export to download a CSV or PDF summary." }
                }
            }
        }
        Column(extraCardContent, verticalArrangement = Arrangement.spacedBy(twSpacing.sm)) {
            Button(
                onClick = {},
                modifier = Modifier.fillMaxWidth(),
                variant = ButtonVariant.Outline,
            ) { Text { "Contact Support" } }
            Button(
                onClick = {},
                modifier = Modifier.fillMaxWidth(),
                variant = ButtonVariant.Ghost,
            ) { Text { "Learn More" } }
        }
    }
}

@Composable
fun CreateFrontDoorCard(modifier: Modifier = Modifier) {
    Card(modifier) {
        CardHeader {
            Box(Modifier.fillMaxWidth()) {
                Column(verticalArrangement = Arrangement.spacedBy(twSpacing.xs)) {
                    CardTitle { "Front Door" }
                    CardDescription { "Smart Lock Pro" }
                }
                CardAction(modifier = Modifier.align(Alignment.TopEnd)) {
                    Row {
                        Text(textStyle = TextColors.Muted) { "Locked" }
                        Icon(
                            Lucide.Lock,
                            variant = IconVariant.Small,
                        )
                    }
                }
            }
        }
        Box(
            modifier =
                extraCardContent.padding(bottom = twSpacing.lg).fillMaxWidth().height(150.dp)
                    .background(currentTheme.colors.muted, RoundedCornerShape(twRadius.lg)),
            contentAlignment = Alignment.TopEnd,
        ) {
            Badge(modifier = Modifier.padding(twSpacing.sm), variant = BadgeVariant.Destructive) { Text { "Live" } }
        }
    }
}

@Composable
fun CreateReleaseCatalogCard(modifier: Modifier = Modifier) {
    val holdings =
        listOf(
            "VOO" to "Vanguard S&P 500 ETF · 112 Shares · Jan 2021",
            "VIG" to "Vanguard Dividend Appreciation · 450 Shares · Mar 2022",
            "AAPL" to "Apple Inc. · 85 Shares · Nov 2020",
            "O" to "Realty Income Corp · 320 Shares · Jun 2023",
        )
    Card(modifier) {
        Column(
            extraCardContent.padding(vertical = twSpacing.lg),
            verticalArrangement = Arrangement.spacedBy(twSpacing.md),
        ) {
            InputGroup {
                InputGroupAddon(InputGroupAddonAlign.InlineStart) {
                    Icon(Lucide.Search, variant = IconVariant.Small)
                }
                Input("", {}, placeholder = "Search holdings or tickers...")
            }
            Row(horizontalArrangement = Arrangement.spacedBy(twSpacing.xs)) {
                listOf("Stocks", "ETFs", "REITs").forEach {
                    Button(
                        onClick = {},
                        size = ButtonSize.Sm,
                        variant = if (it == "ETFs") ButtonVariant.Secondary else ButtonVariant.Outline,
                    ) { Text { it } }
                }
            }
            ItemGroup {
                holdings.forEachIndexed { index, (ticker, name) ->
                    Item(variant = ItemVariant.Muted) {
                        ItemMedia { Text(variant = TextVariant.LabelLarge) { ticker } }
                        ItemContent {
                            ItemTitle(name.substringBefore(" · "))
                            ItemDescription(name.substringAfter(" · "))
                        }
                        Badge(variant = BadgeVariant.Outline) {
                            Text {
                                if (index == 2) {
                                    "Stock"
                                } else if (index == 3) {
                                    "REIT"
                                } else {
                                    "ETF"
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
fun CreateCardOverviewCard(modifier: Modifier = Modifier) {
    Column(modifier, verticalArrangement = Arrangement.spacedBy(twSpacing.md)) {
        Row(horizontalArrangement = Arrangement.spacedBy(twSpacing.md)) {
            OverviewMetric("Card Balance", "US$12.94", "US$11,337.06 Available", Modifier.weight(1f))
            Card(Modifier.weight(1f)) {
                Column(
                    extraCardContent.padding(vertical = twSpacing.lg),
                    verticalArrangement = Arrangement.spacedBy(twSpacing.md),
                ) {
                    Text(textStyle = TextColors.Muted) { "Payment Due" }
                    Text(variant = TextVariant.TitleLarge) { "1 Apr" }
                    Button(
                        onClick = {},
                        modifier = Modifier.fillMaxWidth(),
                        size = ButtonSize.Sm,
                        variant = ButtonVariant.Outline,
                    ) { Text { "Pay Early" } }
                }
            }
        }
        Card {
            Column(
                extraCardContent.padding(vertical = twSpacing.lg),
                verticalArrangement = Arrangement.spacedBy(twSpacing.sm),
            ) {
                Row(
                    Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                ) {
                    Text(textStyle = TextColors.Muted) { "Yearly Activity" }
                    Badge(variant = BadgeVariant.Secondary) { Text { "+US$0.25 Daily Cash" } }
                }
                Progress(.68f, modifier = Modifier.fillMaxWidth())
                Text(
                    variant = TextVariant.LabelSmall,
                    textStyle = TextColors.Muted,
                ) { "J  F  M  A  M  J  J  A  S  O  N  D" }
            }
        }
    }
}

@Composable
fun CreateCoverArtCard(modifier: Modifier = Modifier) {
    Card(modifier) {
        Column(
            extraCardContent.padding(vertical = twSpacing.lg),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(twSpacing.md),
        ) {
            Text(variant = TextVariant.LabelSmall, textStyle = TextColors.Muted) { "COVER ART" }
            Box(
                Modifier.fillMaxWidth().height(220.dp)
                    .background(currentTheme.colors.muted, RoundedCornerShape(twRadius.lg)),
                contentAlignment = Alignment.Center,
            ) { Icon(Lucide.Plus) }
        }
        Column(extraCardContent, horizontalAlignment = Alignment.CenterHorizontally) {
            Button(
                onClick = {},
                modifier = Modifier.fillMaxWidth(),
                variant = ButtonVariant.Secondary,
            ) { Text { "Upload Artwork" } }
            Text(
                variant = TextVariant.LabelSmall,
                textStyle = TextColors.Muted,
            ) { "Minimum 3000 × 3000px\nJPEG or PNG only" }
        }
    }
}

@Composable
fun CreateLoadingCard(modifier: Modifier = Modifier) {
    Card(modifier) {
        Column(
            extraCardContent.padding(vertical = twSpacing.lg),
            verticalArrangement = Arrangement.spacedBy(twSpacing.md),
        ) {
            Skeleton(Modifier.width(128.dp).height(20.dp))
            Skeleton(Modifier.width(192.dp).height(16.dp))
            Skeleton(Modifier.fillMaxWidth().height(128.dp))
            repeat(3) { Skeleton(Modifier.fillMaxWidth(if (it == 2) .5f else 1f).height(16.dp)) }
            Row(horizontalArrangement = Arrangement.spacedBy(twSpacing.sm)) {
                repeat(2) {
                    Skeleton(
                        Modifier.weight(1f).height(36.dp),
                    )
                }
            }
        }
    }
}

@Composable
fun CreateReceivingMethodCard(modifier: Modifier = Modifier) {
    var method by remember { mutableStateOf("bank") }
    Card(modifier) {
        CardHeader {
            Box(Modifier.fillMaxWidth()) {
                Column(verticalArrangement = Arrangement.spacedBy(twSpacing.xs)) {
                    CardTitle { "Receiving Method" }
                    CardDescription { "Payout Preferences" }
                }
                CardAction(modifier = Modifier.align(Alignment.TopEnd)) { Icon(Lucide.X, variant = IconVariant.Small) }
            }
        }
        FieldGroup(extraCardContent) {
            Field {
                FieldLabel("Account Holder Name")
                Input("Synthetic Horizons Music LLC", {})
            }
            Field {
                FieldLabel("Receiving Method")
                RadioGroup {
                    RadioButton(method == "bank", { method = "bank" }, label = "Bank Transfer — SWIFT / IBAN")
                    RadioButton(method == "paypal", { method = "paypal" }, label = "PayPal — Instant Payout")
                }
            }
            Field {
                FieldLabel("IBAN / Account Number")
                Input("", {}, placeholder = "DE89 3704 0044 ....")
            }
        }
        Button(onClick = {}, modifier = extraCardContent, enabled = false) { Text { "Save Payout Settings" } }
    }
}

@Composable
fun CreateEmptyConnectBankCard(modifier: Modifier = Modifier) =
    PreviewEmptyCard(
        modifier,
        "Connect Bank",
        "Link your payout method to receive monthly royalty distributions automatically.",
        "Set Up Payouts",
    )

@Composable
fun CreateUpcomingPaymentsCard(modifier: Modifier = Modifier) {
    Card(modifier) {
        CardHeader {
            CardTitle { "Upcoming Payments" }
            CardDescription { "Select a date to view scheduled payments." }
        }
        Column(
            extraCardContent.padding(bottom = twSpacing.lg),
            verticalArrangement = Arrangement.spacedBy(twSpacing.md),
        ) {
            Box(
                Modifier.fillMaxWidth().height(190.dp)
                    .background(currentTheme.colors.muted, RoundedCornerShape(twRadius.lg)),
                contentAlignment = Alignment.Center,
            ) {
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    Icon(Lucide.Calendar)
                    Text { "April 2024" }
                    Text(
                        textStyle = TextColors.Muted,
                    ) { "Select a date" }
                }
            }
            ItemGroup {
                listOf(
                    "Netflix Subscription" to "Apr 15, 2024 · $19.99",
                    "Rent Payment" to "Apr 1, 2024 · $2,400.00",
                    "Auto Insurance" to "Apr 22, 2024 · $186.00",
                ).forEach { (title, detail) ->
                    Item(variant = ItemVariant.Muted) {
                        ItemContent {
                            ItemTitle(title)
                            ItemDescription(detail)
                        }
                        Icon(
                            Lucide.ArrowRight,
                            variant = IconVariant.Small,
                        )
                    }
                }
            }
        }
    }
}

@Composable
fun CreateRollerShadesCard(modifier: Modifier = Modifier) {
    var position by remember { mutableStateOf(50f) }
    Card(modifier) {
        CardHeader {
            CardTitle { "Living Room" }
            CardDescription { "Roller Shades" }
        }
        Column(
            extraCardContent.padding(bottom = twSpacing.lg),
            verticalArrangement = Arrangement.spacedBy(twSpacing.md),
        ) {
            Box(
                Modifier.fillMaxWidth().height(128.dp)
                    .background(currentTheme.colors.muted, RoundedCornerShape(twRadius.lg)),
            ) {
                Box(
                    Modifier
                        .fillMaxWidth()
                        .height((128 * (position / 100f)).dp)
                        .background(currentTheme.colors.mutedForeground, RoundedCornerShape(twRadius.lg)),
                )
            }
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(twSpacing.sm),
            ) {
                Text(variant = TextVariant.LabelSmall) { "Open" }
                Slider(
                    value = position,
                    onValueChange = { position = it },
                    valueRange = 0f..100f,
                    modifier = Modifier.weight(1f),
                )
                Text(variant = TextVariant.LabelSmall) { "Close" }
            }
        }
        Row(extraCardContent) {
            listOf(
                "Open" to 0f,
                "Half" to 50f,
                "Closed" to 100f,
            ).forEach { (label, value) ->
                Button(
                    onClick = { position = value },
                    modifier = Modifier.weight(1f),
                    size = ButtonSize.Sm,
                    variant = if (position == value) ButtonVariant.Secondary else ButtonVariant.Outline,
                ) { Text { label } }
            }
        }
    }
}

@Composable
fun CreateStockPerformanceCard(modifier: Modifier = Modifier) {
    var ticker by remember { mutableStateOf("VOO") }
    Card(modifier) {
        CardHeader {
            CardTitle { "Stock Performance" }
            CardDescription { "6-month price history." }
        }
        Column(
            extraCardContent.padding(bottom = twSpacing.lg),
            verticalArrangement = Arrangement.spacedBy(twSpacing.md),
        ) {
            Field {
                FieldLabel("Ticker")
                Select(
                    value = ticker,
                    onValueChange = { ticker = it },
                    modifier = Modifier.fillMaxWidth(),
                ) {
                    SelectTrigger()
                    SelectContent {
                        SelectItem("VOO")
                        SelectItem("VIG")
                        SelectItem("AAPL")
                        SelectItem("MSFT")
                        SelectItem("GOOGL")
                        SelectItem("AMZN")
                        SelectItem("TSLA")
                    }
                }
            }
            Separator()
            Text(textStyle = TextColors.Muted) { "$ticker price history" }
            Box(
                Modifier.fillMaxWidth().height(180.dp)
                    .background(currentTheme.colors.muted, RoundedCornerShape(twRadius.lg)),
                contentAlignment = Alignment.Center,
            ) { Text { "Jan  ·  Feb  ·  Mar  ·  Apr  ·  May  ·  Jun" } }
        }
    }
}

@Composable
fun CreateEmptyExploreCatalogCard(modifier: Modifier = Modifier) =
    PreviewEmptyCard(
        modifier,
        "Explore Catalog",
        "Check your ISRC codes, metadata, and visual assets before going live.",
        "View Catalog",
    )

@Composable
fun CreateSocialLinksCard(modifier: Modifier = Modifier) {
    var spotify by remember { mutableStateOf("spotify.com/artist/3j...2k") }
    var instagram by remember { mutableStateOf("@julianduryea_music") }
    var soundcloud by remember { mutableStateOf("") }
    var website by remember { mutableStateOf("") }
    Card(modifier) {
        CardHeader {
            CardTitle { "Social Links" }
        }
        FieldGroup(extraCardContent) {
            Field {
                FieldLabel("Spotify Artist URL")
                InputGroup {
                    InputGroupAddon(InputGroupAddonAlign.InlineStart) {
                        Icon(Lucide.Plus, variant = IconVariant.Small)
                    }
                    Input(spotify, { spotify = it })
                }
            }
            Field {
                FieldLabel("Instagram Handle")
                Input(instagram, { instagram = it })
            }
            Field {
                FieldLabel("SoundCloud URL")
                Input(
                    soundcloud,
                    { soundcloud = it },
                    placeholder = "soundcloud.com/username",
                )
            }
            Field {
                FieldLabel("Website")
                Input(website, { website = it }, placeholder = "https://yoursite.com")
            }
        }
        Row(extraCardContent, horizontalArrangement = Arrangement.spacedBy(twSpacing.sm)) {
            Button(
                onClick = {},
                modifier = Modifier.weight(1f),
                variant = ButtonVariant.Secondary,
            ) { Text { "Discard" } }
            Button(onClick = {}, modifier = Modifier.weight(1f)) { Text { "Save Changes" } }
        }
    }
}

@Composable
private fun SettingSwitch(
    title: String,
    description: String,
    checked: Boolean,
    onCheckedChange: (Boolean) -> Unit,
) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(twSpacing.md),
    ) {
        Column(modifier = Modifier.weight(1f)) {
            Text(variant = TextVariant.LabelLarge) { title }
            FieldDescription(
                description,
            )
        }
        Switch(checked, onCheckedChange)
    }
}

@Composable
private fun MetricRow(
    label: String,
    value: String,
) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween,
    ) {
        Text(textStyle = TextColors.Muted) { label }
        Text(variant = TextVariant.LabelLarge) { value }
    }
}

@Composable
private fun OverviewMetric(
    title: String,
    value: String,
    description: String,
    modifier: Modifier,
) {
    Card(modifier) {
        Column(extraCardContent.padding(vertical = twSpacing.lg)) {
            Text(textStyle = TextColors.Muted) { title }
            Text(
                variant = TextVariant.TitleLarge,
            ) { value }
            Text(textStyle = TextColors.Muted) { description }
        }
    }
}

@Composable
private fun PreviewEmptyCard(
    modifier: Modifier,
    title: String,
    description: String,
    action: String,
) {
    Card(modifier) {
        Empty(extraCardContent.padding(vertical = twSpacing.xl)) {
            EmptyMedia { Icon(Lucide.Plus) }
            EmptyHeader {
                EmptyTitle(title)
                EmptyDescription(description)
            }
            EmptyContent { Button(onClick = {}) { Text { action } } }
        }
    }
}
