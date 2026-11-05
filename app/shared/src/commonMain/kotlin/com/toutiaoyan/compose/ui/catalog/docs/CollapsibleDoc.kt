@file:OptIn(androidx.compose.foundation.style.ExperimentalFoundationStyleApi::class)

package com.toutiaoyan.compose.ui.catalog.docs

import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.style.Style
import androidx.compose.foundation.style.border
import androidx.compose.foundation.style.hovered
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.rotate
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalLayoutDirection
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.LayoutDirection
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.composables.icons.lucide.ChevronDown
import com.composables.icons.lucide.ChevronRight
import com.composables.icons.lucide.ChevronsUpDown
import com.composables.icons.lucide.File
import com.composables.icons.lucide.Folder
import com.composables.icons.lucide.Lucide
import com.composables.icons.lucide.Maximize
import com.composables.icons.lucide.Minimize
import com.toutiaoyan.compose.tailwind.core.LocalUnitSpacing
import com.toutiaoyan.compose.ui.catalog.catalogArabicFontFamily
import com.toutiaoyan.compose.ui.catalog.catalogHebrewFontFamily
import com.toutiaoyan.compose.ui.components.button.Button
import com.toutiaoyan.compose.ui.components.button.ButtonSize
import com.toutiaoyan.compose.ui.components.button.ButtonVariant
import com.toutiaoyan.compose.ui.components.card.Card
import com.toutiaoyan.compose.ui.components.card.CardContent
import com.toutiaoyan.compose.ui.components.card.CardDescription
import com.toutiaoyan.compose.ui.components.card.CardHeader
import com.toutiaoyan.compose.ui.components.card.CardSize
import com.toutiaoyan.compose.ui.components.card.CardTitle
import com.toutiaoyan.compose.ui.components.collapsible.Collapsible
import com.toutiaoyan.compose.ui.components.collapsible.CollapsibleContent
import com.toutiaoyan.compose.ui.components.collapsible.CollapsibleTrigger
import com.toutiaoyan.compose.ui.components.field.Field
import com.toutiaoyan.compose.ui.components.icon.Icon
import com.toutiaoyan.compose.ui.components.input.Input
import com.toutiaoyan.compose.ui.components.tabs.Tabs
import com.toutiaoyan.compose.ui.components.tabs.TabsList
import com.toutiaoyan.compose.ui.components.tabs.TabsTrigger
import com.toutiaoyan.compose.ui.components.text.Text
import com.toutiaoyan.compose.ui.theme.Theme
import com.toutiaoyan.compose.ui.theme.currentTheme

val collapsibleDoc =
    ComponentDoc(
        id = "collapsible",
        title = "Collapsible",
        description = "An interactive component which expands/collapses a panel.",
        usageImportsCode = "import com.toutiaoyan.compose.ui.components.collapsible.*",
        usageCode =
            """
            var open by remember { mutableStateOf(false) }
            Collapsible(open = open, onOpenChange = { open = it }) {
                CollapsibleTrigger("Can I use this in my project?")
                CollapsibleContent(
                    "Yes. Free to use for personal and commercial projects. No attribution required.",
                )
            }
            """.trimIndent(),
        compositionCode =
            """
            Collapsible
            ├── CollapsibleTrigger
            └── CollapsibleContent
            """.trimIndent(),
        examplesHeading = null,
        beforeExamples =
            listOf(
                ComponentGuideSection(
                    title = "Controlled State",
                    blocks =
                        listOf(
                            ComponentGuideBlock.Paragraph(
                                "Use the `open` and `onOpenChange` props to control the state.",
                            ),
                            ComponentGuideBlock.Code(
                                """
                                var open by remember { mutableStateOf(false) }
                                Collapsible(open = open, onOpenChange = { open = it }) {
                                    CollapsibleTrigger("Toggle")
                                    CollapsibleContent("Content")
                                }
                                """.trimIndent(),
                            ),
                        ),
                ),
            ),
        examples =
            listOf(
                ComponentExample(
                    title = "Default",
                    showHeading = false,
                    previewHeight = 288.dp,
                    previewVerticalAlignment = Alignment.Top,
                    code =
                        """
                        var open by remember { mutableStateOf(false) }
                        Collapsible(
                            modifier = Modifier.width(350.dp),
                            open = open,
                            onOpenChange = { open = it },
                        ) {
                            Row(
                                Modifier.fillMaxWidth().padding(horizontal = 16.dp),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically,
                            ) {
                                Text { "Order #4189" }
                                CollapsibleTrigger(modifier = Modifier.size(32.dp)) {
                                    Icon(Lucide.ChevronsUpDown, Modifier.size(16.dp), contentDescription = "Toggle details")
                                }
                            }
                            Spacer(Modifier.height(8.dp))
                            Row(
                                Modifier.fillMaxWidth().border(1.dp, colors.border, RoundedCornerShape(6.dp))
                                    .padding(horizontal = 16.dp, vertical = 8.dp),
                            ) {
                                Text { "Status" }
                                Text { "Shipped" }
                            }
                            CollapsibleContent {
                                Column(
                                    Modifier.fillMaxWidth().padding(top = 8.dp),
                                    verticalArrangement = Arrangement.spacedBy(8.dp),
                                ) {
                                    InfoRow("Shipping address", "100 Market St, San Francisco")
                                    InfoRow("Items", "2x Studio Headphones")
                                }
                            }
                        }
                        """.trimIndent(),
                    preview = { CollapsibleDefaultPreview() },
                ),
                ComponentExample(
                    title = "Basic",
                    previewHeight = 288.dp,
                    previewVerticalAlignment = Alignment.Top,
                    code =
                        """
                        var open by remember { mutableStateOf(false) }
                        Card(Modifier.widthIn(max = 384.dp).fillMaxWidth()) {
                            CardContent {
                                Collapsible(open = open, onOpenChange = { open = it }) {
                                    CollapsibleTrigger(Modifier.fillMaxWidth()) {
                                        Row(Modifier.fillMaxWidth().padding(horizontal = 16.dp, vertical = 8.dp)) {
                                            Text { "Product details" }
                                            Spacer(Modifier.weight(1f))
                                            Icon(Lucide.ChevronDown, Modifier.size(16.dp).rotate(if (open) 180f else 0f))
                                        }
                                    }
                                    CollapsibleContent {
                                        Column(Modifier.fillMaxWidth().padding(start = 10.dp, end = 10.dp, bottom = 10.dp)) {
                                            Text { "This panel can be expanded or collapsed to reveal additional content." }
                                            Button("Learn More", onClick = {}, size = ButtonSize.Xs)
                                        }
                                    }
                                }
                            }
                        }
                        """.trimIndent(),
                    preview = { CollapsibleBasicPreview() },
                ),
                ComponentExample(
                    title = "Settings Panel",
                    descriptionMarkdown = "Use a trigger button to reveal additional settings.",
                    code =
                        """
                        var open by remember { mutableStateOf(false) }
                        Card(Modifier.widthIn(max = 320.dp).fillMaxWidth(), size = CardSize.Sm) {
                            CardHeader {
                                CardTitle("Radius")
                                CardDescription("Set the corner radius of the element.")
                            }
                            CardContent {
                                Collapsible(open = open, onOpenChange = { open = it }) {
                                    Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                                        Column(Modifier.weight(1f), verticalArrangement = Arrangement.spacedBy(8.dp)) {
                                            Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                                                Field(Modifier.weight(1f)) { Input(x, { x = it }, placeholder = "0") }
                                                Field(Modifier.weight(1f)) { Input(y, { y = it }, placeholder = "0") }
                                            }
                                            CollapsibleContent {
                                                Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                                                    Field(Modifier.weight(1f)) { Input(x2, { x2 = it }, placeholder = "0") }
                                                    Field(Modifier.weight(1f)) { Input(y2, { y2 = it }, placeholder = "0") }
                                                }
                                            }
                                        }
                                        CollapsibleTrigger(modifier = Modifier.size(32.dp)) {
                                            Icon(if (open) Lucide.Minimize else Lucide.Maximize, Modifier.size(16.dp))
                                        }
                                    }
                                }
                            }
                        }
                        """.trimIndent(),
                    preview = { CollapsibleSettingsPreview() },
                ),
                ComponentExample(
                    title = "File Tree",
                    descriptionMarkdown =
                        "Use nested collapsibles to build a file tree. In a conversation, this is the " +
                            "[File Tree](https://ui.shadcn.com/docs/ai-elements/base/file-tree) pattern.",
                    previewHeight = 576.dp,
                    code =
                        """
                        Card(Modifier.widthIn(max = 256.dp).fillMaxWidth(), size = CardSize.Sm) {
                            CardHeader {
                                Tabs(defaultValue = "explorer") {
                                    TabsList(Modifier.fillMaxWidth()) {
                                        TabsTrigger("explorer", "Explorer")
                                        TabsTrigger("settings", "Outline")
                                    }
                                }
                            }
                            CardContent {
                                Column(verticalArrangement = Arrangement.spacedBy(4.dp)) {
                                    fileTree.forEach { FileTreeRow(it) }
                                }
                            }
                        }

                        @Composable
                        fun FileTreeRow(item: FileTreeItem) = when (item) {
                            is FileTreeItem.Dir -> Collapsible {
                                CollapsibleTrigger(Modifier.fillMaxWidth()) {
                                    Row(
                                        Modifier.fillMaxWidth().padding(horizontal = 8.dp, vertical = 6.dp),
                                        horizontalArrangement = Arrangement.spacedBy(8.dp),
                                    ) {
                                        val rotation by animateFloatAsState(if (open) 90f else 0f)
                                        Icon(Lucide.ChevronRight, Modifier.size(16.dp).rotate(rotation))
                                        Icon(Lucide.Folder, Modifier.size(16.dp))
                                        Text { item.name }
                                    }
                                }
                                CollapsibleContent {
                                    Column(Modifier.padding(start = 20.dp, top = 4.dp), verticalArrangement = Arrangement.spacedBy(4.dp)) {
                                        item.items.forEach { FileTreeRow(it) }
                                    }
                                }
                            }
                            is FileTreeItem.File -> Button(
                                onClick = {},
                                variant = ButtonVariant.Link,
                                size = ButtonSize.Sm,
                                modifier = Modifier.fillMaxWidth(),
                            ) {
                                Icon(Lucide.File, Modifier.size(16.dp))
                                Text { item.name }
                            }
                        }
                        """.trimIndent(),
                    preview = { CollapsibleFileTreePreview() },
                ),
                ComponentExample(
                    title = "RTL",
                    descriptionMarkdown =
                        "To enable RTL support in shadcn/ui, see the " +
                            "[RTL configuration guide](https://ui.shadcn.com/docs/rtl).",
                    previewPadding = 0.dp,
                    previewHeight = 352.dp,
                    code =
                        """
                        var language by remember { mutableStateOf(0) }
                        var open by remember { mutableStateOf(false) }
                        RtlPreview(language, listOf("Arabic (العربية)", "English", "Hebrew (עברית)"), { language = it }) {
                            CompositionLocalProvider(
                                LocalLayoutDirection provides if (language == 1) LayoutDirection.Ltr else LayoutDirection.Rtl,
                            ) {
                                Collapsible(
                                    modifier = Modifier.width(350.dp),
                                    open = open,
                                    onOpenChange = { open = it },
                                ) {
                                    // Order header, status row and details use the selected language's strings.
                                }
                            }
                        }
                        """.trimIndent(),
                    preview = { CollapsibleRtlPreview() },
                ),
                ComponentExample(
                    title = "Kotlin extension: Disabled",
                    descriptionMarkdown = "`disabled = true` renders the panel without interaction.",
                    code =
                        """
                        Collapsible("Hide details", "@peduarte starred 3 repositories", disabled = true)
                        """.trimIndent(),
                    preview = {
                        Collapsible("Hide details", "@peduarte starred 3 repositories", disabled = true)
                    },
                ),
                ComponentExample(
                    title = "Kotlin extension: Default open",
                    descriptionMarkdown = "`defaultOpen = true` starts the uncontrolled panel expanded.",
                    code =
                        """
                        Collapsible("Hide details", "@peduarte starred 3 repositories", defaultOpen = true)
                        """.trimIndent(),
                    preview = {
                        Collapsible("Hide details", "@peduarte starred 3 repositories", defaultOpen = true)
                    },
                ),
                ComponentExample(
                    title = "Kotlin extension: Keep mounted",
                    descriptionMarkdown =
                        "`keepMounted = true` keeps collapsed content in the composition so local " +
                            "state survives a collapse.",
                    code =
                        """
                        Collapsible(defaultOpen = true) {
                            CollapsibleTrigger("Hide details")
                            CollapsibleContent("@peduarte starred 3 repositories", keepMounted = true)
                        }
                        """.trimIndent(),
                    preview = {
                        Collapsible(defaultOpen = true) {
                            CollapsibleTrigger("Hide details")
                            CollapsibleContent("@peduarte starred 3 repositories", keepMounted = true)
                        }
                    },
                ),
                ComponentExample(
                    title = "Kotlin extension: Force mount",
                    descriptionMarkdown =
                        "`forceMount = true` keeps the content mounted even while it is visually hidden.",
                    code =
                        """
                        Collapsible(defaultOpen = true) {
                            CollapsibleTrigger("Hide details")
                            CollapsibleContent("@peduarte starred 3 repositories", forceMount = true)
                        }
                        """.trimIndent(),
                    preview = {
                        Collapsible(defaultOpen = true) {
                            CollapsibleTrigger("Hide details")
                            CollapsibleContent("@peduarte starred 3 repositories", forceMount = true)
                        }
                    },
                ),
            ),
        afterExamples =
            listOf(
                ComponentGuideSection(
                    title = "API Reference",
                    blocks =
                        listOf(
                            ComponentGuideBlock.Paragraph(
                                "See the [Base UI](https://base-ui.com/react/components/collapsible#api-reference) " +
                                    "documentation for more information.",
                            ),
                        ),
                ),
            ),
    )

@Composable
private fun DemoText(
    text: String,
    muted: Boolean = false,
    medium: Boolean = false,
    semibold: Boolean = false,
) {
    val theme = currentTheme
    Text(
        textStyle =
            theme.typography.bodyMedium.copy(
                fontFamily = theme.fontFamily,
                fontSize = 14.sp,
                lineHeight = 20.sp,
                fontWeight =
                    when {
                        semibold -> FontWeight.SemiBold
                        medium -> FontWeight.Medium
                        else -> FontWeight.Normal
                    },
                color = if (muted) theme.colors.mutedForeground else theme.colors.foreground,
            ),
    ) { text }
}

@Composable
private fun InfoRow(title: String, value: String) {
    val colors = currentTheme.colors
    Column(
        Modifier.fillMaxWidth()
            .border(1.dp, colors.border, RoundedCornerShape(6.dp))
            .padding(horizontal = 16.dp, vertical = 8.dp),
    ) {
        DemoText(title, medium = true)
        DemoText(value, muted = true)
    }
}

@Composable
private fun CollapsibleDefaultPreview() {
    var open by remember { mutableStateOf(false) }
    val colors = currentTheme.colors
    Box(Modifier.fillMaxWidth().height(208.dp), contentAlignment = Alignment.TopCenter) {
        Collapsible(
            open = open,
            onOpenChange = { open = it },
            modifier = Modifier.width(350.dp),
        ) {
        Row(
            Modifier.fillMaxWidth().padding(horizontal = 16.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically,
        ) {
            DemoText("Order #4189", semibold = true)
            CollapsibleTrigger(
                modifier = Modifier.size(32.dp).testTag("collapsible-default-toggle"),
                style =
                    Style {
                        shape(RoundedCornerShape(6.dp))
                        hovered { background(colors.muted) }
                    },
            ) {
                Icon(Lucide.ChevronsUpDown, Modifier.size(16.dp), contentDescription = "Toggle details")
            }
        }
        Spacer(Modifier.height(8.dp))
        Row(
            Modifier.fillMaxWidth().border(1.dp, colors.border, RoundedCornerShape(6.dp))
                .padding(horizontal = 16.dp, vertical = 8.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically,
        ) {
            DemoText("Status", muted = true)
            DemoText("Shipped", medium = true)
        }
        CollapsibleContent {
            Column(
                Modifier.fillMaxWidth().padding(top = 8.dp),
                verticalArrangement = Arrangement.spacedBy(8.dp),
            ) {
                InfoRow("Shipping address", "100 Market St, San Francisco")
                InfoRow("Items", "2x Studio Headphones")
            }
        }
        }
    }
}

@Composable
private fun CollapsibleBasicPreview() {
    var open by remember { mutableStateOf(false) }
    val colors = currentTheme.colors
    Box(Modifier.fillMaxWidth().height(208.dp), contentAlignment = Alignment.TopCenter) {
        Card(Modifier.widthIn(max = 384.dp).fillMaxWidth()) {
        CardContent {
            Collapsible(open = open, onOpenChange = { open = it }, modifier = Modifier.fillMaxWidth()) {
                CollapsibleTrigger(
                    modifier = Modifier.fillMaxWidth().testTag("collapsible-basic-toggle"),
                    style =
                        Style {
                            shape(RoundedCornerShape(6.dp))
                            background(if (open) colors.muted else Color.Transparent)
                            hovered { background(colors.muted) }
                        },
                ) {
                    Row(
                        Modifier.fillMaxWidth().padding(horizontal = 16.dp, vertical = 8.dp),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically,
                    ) {
                        DemoText("Product details", medium = true)
                        Icon(
                            Lucide.ChevronDown,
                            Modifier.size(16.dp).rotate(if (open) 180f else 0f),
                            contentDescription = "Toggle details",
                        )
                    }
                }
                CollapsibleContent(
                    style = Style { background(if (open) colors.muted else Color.Transparent) },
                ) {
                    Column(
                        Modifier.fillMaxWidth().padding(start = 10.dp, end = 10.dp, bottom = 10.dp),
                        verticalArrangement = Arrangement.spacedBy(8.dp),
                        horizontalAlignment = Alignment.Start,
                    ) {
                        DemoText("This panel can be expanded or collapsed to reveal additional content.")
                        Button(
                            "Learn More",
                            onClick = {},
                            variant = ButtonVariant.Default,
                            size = ButtonSize.Xs,
                        )
                    }
                }
            }
        }
        }
    }
}

@Composable
private fun CollapsibleSettingsPreview() {
    var open by remember { mutableStateOf(false) }
    var radiusX by remember { mutableStateOf("0") }
    var radiusY by remember { mutableStateOf("0") }
    var radiusX2 by remember { mutableStateOf("0") }
    var radiusY2 by remember { mutableStateOf("0") }
    val colors = currentTheme.colors
    Card(Modifier.widthIn(max = 320.dp).fillMaxWidth(), size = CardSize.Sm) {
        CardHeader {
            CardTitle("Radius")
            CardDescription("Set the corner radius of the element.")
        }
        CardContent {
            Collapsible(open = open, onOpenChange = { open = it }, modifier = Modifier.fillMaxWidth()) {
                Row(
                    Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(8.dp),
                    verticalAlignment = Alignment.Top,
                ) {
                    Column(Modifier.weight(1f), verticalArrangement = Arrangement.spacedBy(8.dp)) {
                        Row(
                            Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.spacedBy(8.dp),
                        ) {
                            Field(Modifier.weight(1f)) { Input(radiusX, { radiusX = it }, placeholder = "0") }
                            Field(Modifier.weight(1f)) { Input(radiusY, { radiusY = it }, placeholder = "0") }
                        }
                        CollapsibleContent {
                            Row(
                                Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.spacedBy(8.dp),
                            ) {
                                Field(Modifier.weight(1f)) { Input(radiusX2, { radiusX2 = it }, placeholder = "0") }
                                Field(Modifier.weight(1f)) { Input(radiusY2, { radiusY2 = it }, placeholder = "0") }
                            }
                        }
                    }
                    CollapsibleTrigger(
                        modifier = Modifier.size(32.dp).testTag("collapsible-settings-toggle"),
                        style =
                            Style {
                                shape(RoundedCornerShape(6.dp))
                                border(1.dp, colors.border)
                                hovered { background(colors.muted) }
                            },
                    ) {
                        Icon(
                            if (open) Lucide.Minimize else Lucide.Maximize,
                            Modifier.size(16.dp),
                            contentDescription = if (open) "Collapse settings" else "Expand settings",
                        )
                    }
                }
            }
        }
    }
}

private sealed interface FileTreeItem {
    val name: String

    data class Dir(override val name: String, val items: List<FileTreeItem>) : FileTreeItem

    data class Leaf(override val name: String) : FileTreeItem
}

private val fileTree =
    listOf(
        FileTreeItem.Dir(
            "components",
            listOf(
                FileTreeItem.Dir(
                    "ui",
                    listOf(
                        FileTreeItem.Leaf("button.tsx"),
                        FileTreeItem.Leaf("card.tsx"),
                        FileTreeItem.Leaf("dialog.tsx"),
                        FileTreeItem.Leaf("input.tsx"),
                        FileTreeItem.Leaf("select.tsx"),
                        FileTreeItem.Leaf("table.tsx"),
                    ),
                ),
                FileTreeItem.Leaf("login-form.tsx"),
                FileTreeItem.Leaf("register-form.tsx"),
            ),
        ),
        FileTreeItem.Dir(
            "lib",
            listOf(FileTreeItem.Leaf("utils.ts"), FileTreeItem.Leaf("cn.ts"), FileTreeItem.Leaf("api.ts")),
        ),
        FileTreeItem.Dir(
            "hooks",
            listOf(
                FileTreeItem.Leaf("use-media-query.ts"),
                FileTreeItem.Leaf("use-debounce.ts"),
                FileTreeItem.Leaf("use-local-storage.ts"),
            ),
        ),
        FileTreeItem.Dir("types", listOf(FileTreeItem.Leaf("index.d.ts"), FileTreeItem.Leaf("api.d.ts"))),
        FileTreeItem.Dir(
            "public",
            listOf(FileTreeItem.Leaf("favicon.ico"), FileTreeItem.Leaf("logo.svg"), FileTreeItem.Leaf("images")),
        ),
        FileTreeItem.Leaf("app.tsx"),
        FileTreeItem.Leaf("layout.tsx"),
        FileTreeItem.Leaf("globals.css"),
        FileTreeItem.Leaf("package.json"),
        FileTreeItem.Leaf("tsconfig.json"),
        FileTreeItem.Leaf("README.md"),
        FileTreeItem.Leaf(".gitignore"),
    )

@Composable
private fun CollapsibleFileTreePreview() {
    Card(Modifier.widthIn(max = 256.dp).fillMaxWidth(), size = CardSize.Sm) {
        CardHeader {
            Tabs(defaultValue = "explorer") {
                TabsList(Modifier.fillMaxWidth()) {
                    TabsTrigger("explorer", "Explorer")
                    TabsTrigger("settings", "Outline")
                }
            }
        }
        CardContent {
            Column(Modifier.fillMaxWidth(), verticalArrangement = Arrangement.spacedBy(4.dp)) {
                fileTree.forEach { FileTreeRow(it) }
            }
        }
    }
}

@Composable
private fun FileTreeRow(item: FileTreeItem) {
    val colors = currentTheme.colors
    when (item) {
        is FileTreeItem.Dir -> {
            Collapsible(modifier = Modifier.fillMaxWidth()) {
                CollapsibleTrigger(
                    modifier = Modifier.fillMaxWidth().testTag("file-tree-" + item.name),
                    style =
                        Style {
                            shape(RoundedCornerShape(6.dp))
                            hovered { background(colors.muted) }
                        },
                ) {
                    Row(
                        Modifier.fillMaxWidth().padding(horizontal = 8.dp, vertical = 6.dp),
                        horizontalArrangement = Arrangement.spacedBy(8.dp),
                        verticalAlignment = Alignment.CenterVertically,
                    ) {
                        val rotation by animateFloatAsState(if (open) 90f else 0f)
                        Icon(
                            Lucide.ChevronRight,
                            Modifier.size(16.dp).rotate(rotation),
                            contentDescription = null,
                        )
                        Icon(Lucide.Folder, Modifier.size(16.dp), contentDescription = null)
                        DemoText(item.name)
                    }
                }
                CollapsibleContent {
                    Column(
                        Modifier.padding(start = 20.dp, top = 4.dp),
                        verticalArrangement = Arrangement.spacedBy(4.dp),
                    ) {
                        item.items.forEach { FileTreeRow(it) }
                    }
                }
            }
        }
        is FileTreeItem.Leaf -> {
            Button(
                onClick = {},
                variant = ButtonVariant.Link,
                size = ButtonSize.Sm,
                modifier = Modifier.fillMaxWidth(),
                label = item.name,
                style = Style { contentColor(colors.foreground) },
            ) {
                Row(
                    Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(8.dp),
                    verticalAlignment = Alignment.CenterVertically,
                ) {
                    Icon(Lucide.File, Modifier.size(16.dp), contentDescription = null)
                    DemoText(item.name)
                }
            }
        }
    }
}

private val rtlLabels = listOf("Arabic (العربية)", "English", "Hebrew (עברית)")

private class CollapsibleRtlCopy(
    val orderNumber: String,
    val status: String,
    val shipped: String,
    val shippingAddress: String,
    val address: String,
    val items: String,
    val itemsDescription: String,
)

private val rtlCopy =
    listOf(
        CollapsibleRtlCopy(
            orderNumber = "الطلب #4189",
            status = "الحالة",
            shipped = "تم الشحن",
            shippingAddress = "عنوان الشحن",
            address = "100 Market St, San Francisco",
            items = "العناصر",
            itemsDescription = "2x سماعات الاستوديو",
        ),
        CollapsibleRtlCopy(
            orderNumber = "Order #4189",
            status = "Status",
            shipped = "Shipped",
            shippingAddress = "Shipping address",
            address = "100 Market St, San Francisco",
            items = "Items",
            itemsDescription = "2x Studio Headphones",
        ),
        CollapsibleRtlCopy(
            orderNumber = "הזמנה #4189",
            status = "סטטוס",
            shipped = "נשלח",
            shippingAddress = "כתובת משלוח",
            address = "100 Market St, San Francisco",
            items = "פריטים",
            itemsDescription = "2x אוזניות סטודיו",
        ),
    )

@Composable
private fun CollapsibleRtlPreview() {
    var language by remember { mutableStateOf(0) }
    Column(Modifier.fillMaxWidth()) {
        RtlPreviewToolbar(language, rtlLabels, { language = it })
        Box(
            Modifier.fillMaxWidth().height(288.dp).padding(40.dp),
            contentAlignment = Alignment.TopCenter,
        ) {
        val inherited = currentTheme
        val fontFamily =
            when (language) {
                0 -> catalogArabicFontFamily()
                2 -> catalogHebrewFontFamily()
                else -> inherited.fontFamily
            }
        CompositionLocalProvider(
            LocalLayoutDirection provides if (language == 1) LayoutDirection.Ltr else LayoutDirection.Rtl,
        ) {
            Theme(
                preset = inherited.activePreset,
                baseColor = inherited.activeBaseColor,
                accent = inherited.activeAccent,
                isDark = inherited.isDark,
                baseRadius = inherited.baseRadius,
                ring = inherited.ring,
                unitSpacing = LocalUnitSpacing.current,
                minTouchTarget = inherited.minTouchTarget,
                fontFamily = fontFamily,
            ) {
                CollapsibleRtlPanel(rtlCopy[language])
            }
        }
        }
    }
}

@Composable
private fun CollapsibleRtlPanel(copy: CollapsibleRtlCopy) {
    var open by remember { mutableStateOf(false) }
    val colors = currentTheme.colors
    Collapsible(
        open = open,
        onOpenChange = { open = it },
        modifier = Modifier.width(350.dp),
    ) {
        Row(
            Modifier.fillMaxWidth().padding(horizontal = 16.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically,
        ) {
            DemoText(copy.orderNumber, semibold = true)
            CollapsibleTrigger(
                modifier = Modifier.size(32.dp),
                style =
                    Style {
                        shape(RoundedCornerShape(6.dp))
                        hovered { background(colors.muted) }
                    },
            ) {
                Icon(Lucide.ChevronsUpDown, Modifier.size(16.dp), contentDescription = "Toggle details")
            }
        }
        Spacer(Modifier.height(8.dp))
        Row(
            Modifier.fillMaxWidth().border(1.dp, colors.border, RoundedCornerShape(6.dp))
                .padding(horizontal = 16.dp, vertical = 8.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically,
        ) {
            DemoText(copy.status, muted = true)
            DemoText(copy.shipped, medium = true)
        }
        CollapsibleContent {
            Column(
                Modifier.fillMaxWidth().padding(top = 8.dp),
                verticalArrangement = Arrangement.spacedBy(8.dp),
            ) {
                InfoRow(copy.shippingAddress, copy.address)
                InfoRow(copy.items, copy.itemsDescription)
            }
        }
    }
}
