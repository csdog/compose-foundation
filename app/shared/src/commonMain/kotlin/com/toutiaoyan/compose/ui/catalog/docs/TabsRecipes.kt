@file:OptIn(androidx.compose.foundation.style.ExperimentalFoundationStyleApi::class)

package com.toutiaoyan.compose.ui.catalog.docs

import androidx.compose.foundation.layout.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalLayoutDirection
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.LineHeightStyle
import androidx.compose.ui.unit.*
import com.composables.icons.lucide.AppWindow
import com.composables.icons.lucide.Code
import com.composables.icons.lucide.Lucide
import com.toutiaoyan.compose.ui.components.card.*
import com.toutiaoyan.compose.ui.components.icon.Icon
import com.toutiaoyan.compose.ui.components.tabs.*
import com.toutiaoyan.compose.ui.components.text.Text
import com.toutiaoyan.compose.ui.theme.currentTheme

// region shared
private data class TabsRecipePanel(val title: String, val description: String, val content: String)

private val tabsRecipePanels =
    listOf(
        listOf(
            TabsRecipePanel(
                "Overview",
                (
                    "View your key metrics and recent project activity. Track " +
                        "progress across all your active projects."
                ),
                "You have 12 active projects and 3 pending tasks.",
            ),
            TabsRecipePanel(
                "Analytics",
                (
                    "Track performance and user engagement metrics. Monitor trends " +
                        "and identify growth opportunities."
                ),
                "Page views are up 25% compared to last month.",
            ),
            TabsRecipePanel(
                "Reports",
                (
                    "Generate and download your detailed reports. Export data in " +
                        "multiple formats for analysis."
                ),
                "You have 5 reports ready and available to export.",
            ),
            TabsRecipePanel(
                "Settings",
                (
                    "Manage your account preferences and options. Customize your " +
                        "experience to fit your needs."
                ),
                "Configure notifications, security, and themes.",
            ),
        ),
        listOf(
            TabsRecipePanel(
                "نظرة عامة",
                (
                    "عرض مقاييسك الرئيسية وأنشطة المشروع الأخيرة. تتبع التقدم عبر " +
                        "جميع مشاريعك النشطة."
                ),
                "لديك ١٢ مشروعًا نشطًا و٣ مهام معلقة.",
            ),
            TabsRecipePanel(
                "التحليلات",
                (
                    "تتبع مقاييس الأداء ومشاركة المستخدمين. راقب الاتجاهات وحدد " +
                        "فرص النمو."
                ),
                "زادت مشاهدات الصفحة بنسبة ٢٥٪ مقارنة بالشهر الماضي.",
            ),
            TabsRecipePanel(
                "التقارير",
                (
                    "إنشاء وتنزيل تقاريرك التفصيلية. تصدير البيانات بتنسيقات " +
                        "متعددة للتحليل."
                ),
                "لديك ٥ تقارير جاهزة ومتاحة للتصدير.",
            ),
            TabsRecipePanel(
                "الإعدادات",
                "إدارة تفضيلات حسابك وخياراته. تخصيص تجربتك لتناسب احتياجاتك.",
                "تكوين الإشعارات والأمان والسمات.",
            ),
        ),
        listOf(
            TabsRecipePanel(
                "סקירה כללית",
                (
                    "הצג את המדדים העיקריים שלך ופעילות הפרויקט האחרונה. עקוב אחר " +
                        "התקדמות בכל הפרויקטים הפעילים שלך."
                ),
                "יש לך 12 פרויקטים פעילים ו-3 משימות ממתינות.",
            ),
            TabsRecipePanel(
                "אנליטיקה",
                (
                    "עקוב אחר ביצועים ומדדי מעורבות משתמשים. עקוב אחר מגמות וזהה " +
                        "הזדמנויות צמיחה."
                ),
                "צפיות בדף עלו ב-25% בהשוואה לחודש שעבר.",
            ),
            TabsRecipePanel(
                "דוחות",
                (
                    "צור והורד את הדוחות המפורטים שלך. ייצא נתונים בפורמטים מרובים " +
                        "לניתוח."
                ),
                "יש לך 5 דוחות מוכנים וזמינים לייצוא.",
            ),
            TabsRecipePanel(
                "הגדרות",
                (
                    "נהל את העדפות החשבון והאפשרויות שלך. התאם אישית את החוויה שלך " +
                        "כך שתתאים לצרכים שלך."
                ),
                "הגדר התראות, אבטחה וערכות נושא.",
            ),
        ),
    )

@Composable
private fun TabsPanelRecipe(
    language: Int = 0,
    maxWidth: Dp = 400.dp,
) {
    val panels = tabsRecipePanels[language]
    val values = listOf("overview", "analytics", "reports", "settings")
    Tabs(
        defaultValue =
            "overview",
        modifier =
            Modifier.widthIn(
                max =
                maxWidth,
            ).fillMaxWidth().testTag("tabs-example"),
    ) {
        TabsList(Modifier.testTag("tabs-list")) {
            values.forEachIndexed { index, value ->
                TabsTrigger(value, panels[index].title, Modifier.testTag("tabs-$value"))
            }
        }
        values.forEachIndexed { index, value ->
            TabsContent(value, Modifier.testTag("tabs-panel-$value")) {
                val panel = panels[index]
                Card(Modifier.fillMaxWidth()) {
                    CardHeader(title = panel.title, description = panel.description)
                    CardContent {
                        Text(
                            textStyle =
                                TextStyle(
                                    fontSize = 14.sp,
                                    lineHeight = 20.sp,
                                    lineHeightStyle =
                                        LineHeightStyle(
                                            LineHeightStyle.Alignment.Center,
                                            LineHeightStyle.Trim.None,
                                        ),
                                    color = currentTheme.colors.mutedForeground,
                                ),
                        ) { panel.content }
                    }
                }
            }
        }
    }
}
// endregion shared

// region default
@Composable
internal fun TabsDefaultRecipe() {
    TabsPanelRecipe()
}
// endregion default

// region line
@Composable
internal fun TabsLineRecipe() {
    Tabs(defaultValue = "overview", modifier = Modifier.testTag("tabs-example")) {
        TabsList(Modifier.testTag("tabs-list"), variant = TabsListVariant.Line) {
            listOf("Overview", "Analytics", "Reports").forEach {
                TabsTrigger(it.lowercase(), it, Modifier.testTag("tabs-${it.lowercase()}"))
            }
        }
    }
}
// endregion line

// region vertical
@Composable
internal fun TabsVerticalRecipe() {
    Tabs(
        defaultValue =
            "account",
        orientation =
            TabsOrientation.Vertical,
        modifier =
            Modifier.testTag("tabs-example"),
    ) {
        TabsList(Modifier.testTag("tabs-list")) {
            listOf("Account", "Password", "Notifications").forEach {
                TabsTrigger(it.lowercase(), it, Modifier.testTag("tabs-${it.lowercase()}"))
            }
        }
    }
}
// endregion vertical

// region disabled
@Composable
internal fun TabsDisabledRecipe() {
    Tabs(defaultValue = "home", modifier = Modifier.testTag("tabs-example")) {
        TabsList(Modifier.testTag("tabs-list")) {
            TabsTrigger("home", "Home", Modifier.testTag("tabs-home"))
            TabsTrigger("settings", "Disabled", Modifier.testTag("tabs-disabled"), disabled = true)
        }
    }
}
// endregion disabled

// region icons
@Composable
internal fun TabsIconsRecipe() {
    Tabs(defaultValue = "preview", modifier = Modifier.testTag("tabs-example")) {
        TabsList(Modifier.testTag("tabs-list")) {
            TabsTrigger("preview", Modifier.testTag("tabs-preview")) {
                Icon(Lucide.AppWindow, Modifier.size(16.dp))
                Text(
                    textStyle =
                        TextStyle(
                            fontSize =
                                14.sp,
                            lineHeight =
                                20.sp,
                            fontWeight =
                                FontWeight.Medium,
                        ),
                ) { "Preview" }
            }
            TabsTrigger("code", Modifier.testTag("tabs-code")) {
                Icon(Lucide.Code, Modifier.size(16.dp))
                Text(
                    textStyle =
                        TextStyle(
                            fontSize =
                                14.sp,
                            lineHeight =
                                20.sp,
                            fontWeight =
                                FontWeight.Medium,
                        ),
                ) { "Code" }
            }
        }
    }
}
// endregion icons

// region rtl
@Composable
internal fun TabsRtlRecipe(language: Int = 1) {
    CompositionLocalProvider(
        LocalLayoutDirection provides
            if (language == 0) {
                LayoutDirection.Ltr
            } else {
                LayoutDirection.Rtl
            },
    ) {
        TabsPanelRecipe(language, maxWidth = 384.dp)
    }
}
// endregion rtl

internal val tabsOfficialExamples =
    listOf(
        ComponentExample("Default", code = tabsDefaultRecipeCode, preview = { TabsDefaultRecipe() }),
        ComponentExample(
            "Line",
            code = tabsLineRecipeCode,
            descriptionMarkdown = "Use the line variant on `TabsList` for a line style.",
            preview = { TabsLineRecipe() },
        ),
        ComponentExample(
            "Vertical",
            code = tabsVerticalRecipeCode,
            descriptionMarkdown = "Use `orientation = TabsOrientation.Vertical` for vertical tabs.",
            preview = { TabsVerticalRecipe() },
        ),
        ComponentExample("Disabled", code = tabsDisabledRecipeCode, preview = { TabsDisabledRecipe() }),
        ComponentExample("Icons", code = tabsIconsRecipeCode, preview = { TabsIconsRecipe() }),
        ComponentExample(
            "RTL",
            code = tabsRtlRecipeCode,
            descriptionMarkdown =
                (
                    "To enable RTL support in shadcn/ui, see the [RTL configuration guide](https://ui.shadcn.com/docs/rtl)."
                ),
            previewPadding = 0.dp,
            preview = {
            var language by remember { mutableIntStateOf(1) }
            RtlPreview(language, listOf("English", "Arabic (العربية)", "Hebrew (עברית)"), { language = it }) {
                TabsRtlRecipe(language)
            }
        }),
    )
