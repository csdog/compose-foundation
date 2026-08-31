package com.toutiaoyan.compose.ui.catalog

import com.toutiaoyan.compose.ui.navigation.RouteKey

/**
 * Static registry of every entry the catalog sidebar can show -- both docs pages
 * (Getting Started) and component demos. Add an entry here whenever a new page or
 * component ships -- this is what drives the sidebar's grouping and the detail
 * route dispatch, so new categories (Forms & Inputs, Overlays & Navigation, Data
 * Display & Feedback, ...) slot in without touching navigation plumbing.
 */
enum class CatalogCategory(val title: String) {
    GETTING_STARTED("Getting Started"),
    CORE_PRIMITIVES("Core Primitives"),
    FORMS_AND_INPUTS("Forms & Inputs"),
    DATA_DISPLAY("Data Display"),
    FEEDBACK("Feedback"),
    DISCLOSURE("Disclosure & Navigation"),
    OVERLAYS("Overlays & Navigation"),
    DATA_AND_LAYOUT("Data & Layout"),
    AI_ELEMENTS("AI Elements"),
    UTILS("Utils"),
}

data class CatalogEntry(
    val route: RouteKey,
    val title: String,
    val category: CatalogCategory,
    val isNew: Boolean = false,
)

val catalogEntries: List<CatalogEntry> =
    listOf(
        // Getting Started -- listed first so groupBy keeps this section on top.
        CatalogEntry(
            route = RouteKey.ComponentDetail("introduction"),
            title = "Introduction",
            category = CatalogCategory.GETTING_STARTED
        ),
        CatalogEntry(route = RouteKey.ComponentDetail("installation"), title = "Installation", category = CatalogCategory.GETTING_STARTED),
        CatalogEntry(route = RouteKey.ComponentDetail("theming"), title = "Theming", category = CatalogCategory.GETTING_STARTED),
        CatalogEntry(route = RouteKey.ComponentDetail("dark-mode"), title = "Dark Mode", category = CatalogCategory.GETTING_STARTED),
        CatalogEntry(route = RouteKey.ComponentDetail("typography"), title = "Typography", category = CatalogCategory.GETTING_STARTED),
        // Core Primitives
        CatalogEntry(route = RouteKey.ComponentDetail("button"), title = "Button", category = CatalogCategory.CORE_PRIMITIVES),
        CatalogEntry(route = RouteKey.ComponentDetail("card"), title = "Card", category = CatalogCategory.CORE_PRIMITIVES),
        CatalogEntry(route = RouteKey.ComponentDetail("badge"), title = "Badge", category = CatalogCategory.CORE_PRIMITIVES),
        CatalogEntry(route = RouteKey.ComponentDetail("chip"), title = "Chip", category = CatalogCategory.CORE_PRIMITIVES),
        CatalogEntry(route = RouteKey.ComponentDetail("input"), title = "Input", category = CatalogCategory.CORE_PRIMITIVES),
        CatalogEntry(route = RouteKey.ComponentDetail("text"), title = "Text", category = CatalogCategory.CORE_PRIMITIVES),
        // Forms & Inputs
        CatalogEntry(route = RouteKey.ComponentDetail("label"), title = "Label", category = CatalogCategory.FORMS_AND_INPUTS),
        CatalogEntry(route = RouteKey.ComponentDetail("checkbox"), title = "Checkbox", category = CatalogCategory.FORMS_AND_INPUTS),
        CatalogEntry(route = RouteKey.ComponentDetail("radio-group"), title = "Radio Group", category = CatalogCategory.FORMS_AND_INPUTS),
        CatalogEntry(route = RouteKey.ComponentDetail("switch"), title = "Switch", category = CatalogCategory.FORMS_AND_INPUTS),
        CatalogEntry(route = RouteKey.ComponentDetail("toggle"), title = "Toggle", category = CatalogCategory.FORMS_AND_INPUTS),
        CatalogEntry(route = RouteKey.ComponentDetail("slider"), title = "Slider", category = CatalogCategory.FORMS_AND_INPUTS),
        CatalogEntry(route = RouteKey.ComponentDetail("toggle-group"), title = "Toggle Group", category = CatalogCategory.FORMS_AND_INPUTS),
        CatalogEntry(route = RouteKey.ComponentDetail("stepper"), title = "Stepper", category = CatalogCategory.FORMS_AND_INPUTS, isNew = true),
        CatalogEntry(route = RouteKey.ComponentDetail("input-group"), title = "Input Group", category = CatalogCategory.FORMS_AND_INPUTS),
        CatalogEntry(route = RouteKey.ComponentDetail("button-group"), title = "Button Group", category = CatalogCategory.FORMS_AND_INPUTS),
        CatalogEntry(route = RouteKey.ComponentDetail("textarea"), title = "Textarea", category = CatalogCategory.FORMS_AND_INPUTS),
        CatalogEntry(route = RouteKey.ComponentDetail("field"), title = "Field", category = CatalogCategory.FORMS_AND_INPUTS),
        CatalogEntry(
            route = RouteKey.ComponentDetail("questionnaire"),
            title = "Questionnaire",
            category = CatalogCategory.FORMS_AND_INPUTS,
            isNew = true
        ),
        CatalogEntry(route = RouteKey.ComponentDetail("input-otp"), title = "Input OTP", category = CatalogCategory.FORMS_AND_INPUTS),
        // Data Display
        CatalogEntry(route = RouteKey.ComponentDetail("avatar"), title = "Avatar", category = CatalogCategory.DATA_DISPLAY),
        CatalogEntry(route = RouteKey.ComponentDetail("aspect-ratio"), title = "Aspect Ratio", category = CatalogCategory.DATA_DISPLAY),
        CatalogEntry(route = RouteKey.ComponentDetail("separator"), title = "Separator", category = CatalogCategory.DATA_DISPLAY),
        CatalogEntry(route = RouteKey.ComponentDetail("kbd"), title = "Kbd", category = CatalogCategory.DATA_DISPLAY),
        CatalogEntry(route = RouteKey.ComponentDetail("item"), title = "Item", category = CatalogCategory.DATA_DISPLAY),
        CatalogEntry(route = RouteKey.ComponentDetail("empty"), title = "Empty", category = CatalogCategory.DATA_DISPLAY),
        // Feedback
        CatalogEntry(route = RouteKey.ComponentDetail("alert"), title = "Alert", category = CatalogCategory.FEEDBACK),
        CatalogEntry(route = RouteKey.ComponentDetail("progress"), title = "Progress", category = CatalogCategory.FEEDBACK),
        CatalogEntry(route = RouteKey.ComponentDetail("skeleton"), title = "Skeleton", category = CatalogCategory.FEEDBACK),
        CatalogEntry(route = RouteKey.ComponentDetail("spinner"), title = "Spinner", category = CatalogCategory.FEEDBACK),
        CatalogEntry(route = RouteKey.ComponentDetail("toast"), title = "Toast", category = CatalogCategory.FEEDBACK),
        // Disclosure & Navigation
        CatalogEntry(route = RouteKey.ComponentDetail("collapsible"), title = "Collapsible", category = CatalogCategory.DISCLOSURE),
        CatalogEntry(route = RouteKey.ComponentDetail("accordion"), title = "Accordion", category = CatalogCategory.DISCLOSURE),
        CatalogEntry(route = RouteKey.ComponentDetail("tabs"), title = "Tabs", category = CatalogCategory.DISCLOSURE),
        CatalogEntry(route = RouteKey.ComponentDetail("breadcrumb"), title = "Breadcrumb", category = CatalogCategory.DISCLOSURE),
        // Overlays & Navigation
        CatalogEntry(route = RouteKey.ComponentDetail("tooltip"), title = "Tooltip", category = CatalogCategory.OVERLAYS),
        CatalogEntry(route = RouteKey.ComponentDetail("popover"), title = "Popover", category = CatalogCategory.OVERLAYS),
        CatalogEntry(route = RouteKey.ComponentDetail("hover-card"), title = "Hover Card", category = CatalogCategory.OVERLAYS),
        CatalogEntry(route = RouteKey.ComponentDetail("dropdown-menu"), title = "Dropdown Menu", category = CatalogCategory.OVERLAYS),
        CatalogEntry(route = RouteKey.ComponentDetail("context-menu"), title = "Context Menu", category = CatalogCategory.OVERLAYS),
        CatalogEntry(route = RouteKey.ComponentDetail("dialog"), title = "Dialog", category = CatalogCategory.OVERLAYS),
        CatalogEntry(route = RouteKey.ComponentDetail("alert-dialog"), title = "Alert Dialog", category = CatalogCategory.OVERLAYS),
        CatalogEntry(route = RouteKey.ComponentDetail("sheet"), title = "Sheet", category = CatalogCategory.OVERLAYS),
        CatalogEntry(route = RouteKey.ComponentDetail("drawer"), title = "Drawer", category = CatalogCategory.OVERLAYS),
        CatalogEntry(route = RouteKey.ComponentDetail("combobox"), title = "Combobox", category = CatalogCategory.OVERLAYS),
        CatalogEntry(route = RouteKey.ComponentDetail("select"), title = "Select", category = CatalogCategory.OVERLAYS),
        CatalogEntry(route = RouteKey.ComponentDetail("date-picker"), title = "Date Picker", category = CatalogCategory.OVERLAYS),
        CatalogEntry(route = RouteKey.ComponentDetail("command"), title = "Command", category = CatalogCategory.OVERLAYS),
        CatalogEntry(route = RouteKey.ComponentDetail("menubar"), title = "Menubar", category = CatalogCategory.OVERLAYS),
        CatalogEntry(route = RouteKey.ComponentDetail("navigation-menu"), title = "Navigation Menu", category = CatalogCategory.OVERLAYS),
        // Data & Layout
        CatalogEntry(route = RouteKey.ComponentDetail("table"), title = "Table", category = CatalogCategory.DATA_AND_LAYOUT),
        CatalogEntry(route = RouteKey.ComponentDetail("pagination"), title = "Pagination", category = CatalogCategory.DATA_AND_LAYOUT),
        CatalogEntry(route = RouteKey.ComponentDetail("scroll-area"), title = "Scroll Area", category = CatalogCategory.DATA_AND_LAYOUT),
        CatalogEntry(route = RouteKey.ComponentDetail("chart"), title = "Chart", category = CatalogCategory.DATA_AND_LAYOUT),
        CatalogEntry(route = RouteKey.ComponentDetail("calendar"), title = "Calendar", category = CatalogCategory.DATA_AND_LAYOUT),
        CatalogEntry(route = RouteKey.ComponentDetail("carousel"), title = "Carousel", category = CatalogCategory.DATA_AND_LAYOUT),
        CatalogEntry(route = RouteKey.ComponentDetail("resizable"), title = "Resizable", category = CatalogCategory.DATA_AND_LAYOUT),
        CatalogEntry(route = RouteKey.ComponentDetail("sidebar"), title = "Sidebar", category = CatalogCategory.DATA_AND_LAYOUT),
        // AI Elements
        CatalogEntry(route = RouteKey.ComponentDetail("message"), title = "Message", category = CatalogCategory.AI_ELEMENTS),
        CatalogEntry(route = RouteKey.ComponentDetail("bubble"), title = "Bubble", category = CatalogCategory.AI_ELEMENTS),
        CatalogEntry(route = RouteKey.ComponentDetail("attachment"), title = "Attachment", category = CatalogCategory.AI_ELEMENTS),
        CatalogEntry(route = RouteKey.ComponentDetail("marker"), title = "Marker", category = CatalogCategory.AI_ELEMENTS),
        CatalogEntry(route = RouteKey.ComponentDetail("message-scroller"), title = "Message Scroller", category = CatalogCategory.AI_ELEMENTS),
        // Utils
        CatalogEntry(route = RouteKey.ComponentDetail("shimmer"), title = "Shimmer", category = CatalogCategory.UTILS),
        CatalogEntry(route = RouteKey.ComponentDetail("scroll-fade"), title = "Scroll Fade", category = CatalogCategory.UTILS),
    )

val catalogEntriesByCategory: Map<CatalogCategory, List<CatalogEntry>> =
    catalogEntries.groupBy { it.category }
