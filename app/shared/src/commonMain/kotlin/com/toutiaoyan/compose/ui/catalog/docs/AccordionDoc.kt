@file:OptIn(androidx.compose.foundation.style.ExperimentalFoundationStyleApi::class)

package com.toutiaoyan.compose.ui.catalog.docs

import com.composables.icons.lucide.Lucide
import com.composables.icons.lucide.ChevronDown
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.width
import com.toutiaoyan.compose.ui.components.select.Select
import com.toutiaoyan.compose.ui.components.select.SelectTrigger
import com.toutiaoyan.compose.ui.components.select.SelectContent
import com.toutiaoyan.compose.ui.components.select.SelectItem
import androidx.compose.foundation.layout.widthIn
import androidx.compose.ui.unit.dp
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.ui.platform.LocalLayoutDirection
import androidx.compose.ui.unit.LayoutDirection
import com.toutiaoyan.compose.ui.theme.currentTheme
import com.toutiaoyan.compose.ui.components.card.Card
import com.toutiaoyan.compose.ui.components.card.CardContent
import com.toutiaoyan.compose.ui.components.card.CardHeader
import androidx.compose.ui.draw.rotate
import com.toutiaoyan.compose.ui.components.accordion.Accordion
import com.toutiaoyan.compose.ui.components.accordion.AccordionContent
import com.toutiaoyan.compose.ui.components.accordion.AccordionItem
import com.toutiaoyan.compose.ui.components.accordion.AccordionOrientation
import com.toutiaoyan.compose.ui.components.accordion.AccordionTrigger
import com.toutiaoyan.compose.ui.components.accordion.AccordionType

val accordionDoc =
    ComponentDoc(
        id = "accordion",
        title = "Accordion",
        description = "A vertically stacked set of interactive headings that each reveal a section of content.",
        usageCode =
            """
            import com.toutiaoyan.compose.ui.components.accordion.Accordion
            import com.toutiaoyan.compose.ui.components.accordion.AccordionContent
                        import com.toutiaoyan.compose.ui.components.accordion.AccordionItem
            import com.toutiaoyan.compose.ui.components.accordion.AccordionTrigger

            var value by remember { mutableStateOf(setOf("item-1")) }
            Accordion(type = AccordionType.Single, value = value, onValueChange = { value = it }) {
                AccordionItem("item-1", "Is it accessible?", "Yes. It adheres to the WAI-ARIA design pattern.")
                AccordionItem("item-2", "Is it styled?", "Yes. It comes with default styles.")
            }
            """.trimIndent(),
        examples =
            listOf(
                ComponentExample(
                    title = "Default",
                    minPreviewHeight = 300.dp,
                    code =
                        """
                        var value by remember { mutableStateOf(setOf("item-1")) }
                        Accordion(modifier = Modifier.widthIn(max = 384.dp).fillMaxWidth(), type = AccordionType.Single, value = value, onValueChange = { value = it }) {
                            AccordionItem("item-1", "What are your shipping options?", "We offer standard (5-7 days), express (2-3 days), and overnight shipping. Free shipping on international orders.")
                            AccordionItem("item-2", "What is your return policy?", "Returns accepted within 30 days. Items must be unused and in original packaging. Refunds processed within 5-7 business days.")
                            AccordionItem("item-3", "How can I contact customer support?", "Reach us via email, live chat, or phone. We respond within 24 hours during business days.")
                        }
                        """.trimIndent(),
                    preview = {
                        var value by remember { mutableStateOf(setOf("item-1")) }
                        Accordion(
                            modifier =
                                Modifier.widthIn(
                                    max = 384.dp,
                                ).fillMaxWidth(),
                            type = AccordionType.Single,
                            value = value,
                            onValueChange = {
                                value = it
                            },
                        ) {
                            AccordionItem(
                                "item-1",
                                "What are your shipping options?",
                                (
                                    "We offer standard (5-7 days), express (2-3 days), and " +
                                        "overnight shipping. Free shipping on international orders."
                                ),
                            )
                            AccordionItem(
                                "item-2",
                                "What is your return policy?",
                                (
                                    "Returns accepted within 30 days. Items must be unused and in " +
                                        "original packaging. Refunds processed within 5-7 business " +
                                        "days."
                                ),
                            )
                            AccordionItem(
                                "item-3",
                                "How can I contact customer support?",
                                (
                                    "Reach us via email, live chat, or phone. We respond within 24 " +
                                        "hours during business days."
                                ),
                            )
                        }
                    },
                ),
                ComponentExample(
                    title = "Basic (official)",
                    minPreviewHeight = 300.dp,
                    code =
                        """
                        Accordion(
                            modifier = Modifier.widthIn(max = 384.dp).fillMaxWidth(),
                            defaultValue = setOf("item-1"),
                        ) {
                            AccordionItem("item-1", "How do I reset my password?", "Click on 'Forgot Password' on the login page, enter your email address, and we'll send you a link to reset your password. The link will expire in 24 hours.")
                            AccordionItem("item-2", "Can I change my subscription plan?", "Yes, you can upgrade or downgrade your plan at any time from your account settings. Changes will be reflected in your next billing cycle.")
                            AccordionItem("item-3", "What payment methods do you accept?", "We accept all major credit cards, PayPal, and bank transfers. All payments are processed securely through our payment partners.")
                        }
                        """.trimIndent(),
                    preview = {
                        Accordion(
                            modifier = Modifier.widthIn(max = 384.dp).fillMaxWidth(),
                            defaultValue = setOf("item-1"),
                        ) {
                            AccordionItem(
                                "item-1",
                                "How do I reset my password?",
                                (
                                    "Click on 'Forgot Password' on the login page, enter your " +
                                        "email address, and we'll send you a link to reset your " +
                                        "password. The link will expire in 24 hours."
                                ),
                            )
                            AccordionItem(
                                "item-2",
                                "Can I change my subscription plan?",
                                (
                                    "Yes, you can upgrade or downgrade your plan at any time from " +
                                        "your account settings. Changes will be reflected in your next " +
                                        "billing cycle."
                                ),
                            )
                            AccordionItem(
                                "item-3",
                                "What payment methods do you accept?",
                                (
                                    "We accept all major credit cards, PayPal, and bank transfers. " +
                                        "All payments are processed securely through our payment " +
                                        "partners."
                                ),
                            )
                        }
                    },
                ),
                ComponentExample(
                    title = "Multiple (official)",
                    minPreviewHeight = 450.dp,
                    code =
                        """
                        Accordion(
                            modifier = Modifier.widthIn(max = 384.dp).fillMaxWidth(),
                            defaultValue = setOf("notifications"),
                            multiple = true,
                        ) {
                            AccordionItem("notifications", "Notification Settings", "Manage how you receive notifications. You can enable email alerts for updates or push notifications for mobile devices.")
                            AccordionItem("privacy", "Privacy & Security", "Control your privacy settings and security preferences. Enable two-factor authentication, manage connected devices, review active sessions, and configure data sharing preferences. You can also download your data or delete your account.")
                            AccordionItem("billing", "Billing & Subscription", "View your current plan, payment history, and upcoming invoices. Update your payment method, change your subscription tier, or cancel your subscription.")
                        }
                        """.trimIndent(),
                    preview = {
                        Accordion(
                            modifier = Modifier.widthIn(max = 384.dp).fillMaxWidth(),
                            defaultValue = setOf("notifications"),
                            multiple = true,
                        ) {
                            AccordionItem(
                                "notifications",
                                "Notification Settings",
                                (
                                    "Manage how you receive notifications. You can enable email " +
                                        "alerts for updates or push notifications for mobile devices."
                                ),
                            )
                            AccordionItem(
                                "privacy",
                                "Privacy & Security",
                                (
                                    "Control your privacy settings and security preferences. " +
                                        "Enable two-factor authentication, manage connected devices, " +
                                        "review active sessions, and configure data sharing " +
                                        "preferences. You can also download your data or delete your " +
                                        "account."
                                ),
                            )
                            AccordionItem(
                                "billing",
                                "Billing & Subscription",
                                (
                                    "View your current plan, payment history, and upcoming " +
                                        "invoices. Update your payment method, change your " +
                                        "subscription tier, or cancel your subscription."
                                ),
                            )
                        }
                    },
                ),
                ComponentExample(
                    title = "Disabled (official)",
                    minPreviewHeight = 300.dp,
                    code =
                        """
                        Accordion(
                            modifier = Modifier.widthIn(max = 384.dp).fillMaxWidth(),
                            defaultValue = emptySet(),
                        ) {
                            AccordionItem("item-1", "Can I access my account history?", "Yes, you can view your complete account history including all transactions, plan changes, and support tickets in the Account History section of your dashboard.")
                            AccordionItem("item-2", "Premium feature information", "This section contains information about premium features. Upgrade your plan to access this content.", disabled = true)
                            AccordionItem("item-3", "How do I update my email address?", "You can update your email address in your account settings. You'll receive a verification email at your new address to confirm the change.")
                        }
                        """.trimIndent(),
                    preview = {
                        Accordion(
                            modifier = Modifier.widthIn(max = 384.dp).fillMaxWidth(),
                            defaultValue = emptySet(),
                        ) {
                            AccordionItem(
                                "item-1",
                                "Can I access my account history?",
                                (
                                    "Yes, you can view your complete account history including all " +
                                        "transactions, plan changes, and support tickets in the " +
                                        "Account History section of your dashboard."
                                ),
                            )
                            AccordionItem(
                                "item-2",
                                "Premium feature information",
                                (
                                    "This section contains information about premium features. " +
                                        "Upgrade your plan to access this content."
                                ),
                                disabled = true,
                            )
                            AccordionItem(
                                "item-3",
                                "How do I update my email address?",
                                (
                                    "You can update your email address in your account settings. " +
                                        "You'll receive a verification email at your new address to " +
                                        "confirm the change."
                                ),
                            )
                        }
                    },
                ),
                ComponentExample(
                    title = "Borders (official)",
                    minPreviewHeight = 300.dp,
                    code =
                        """
                        Column(Modifier.widthIn(max = 384.dp).fillMaxWidth().border(1.dp, currentTheme.colors.border, RoundedCornerShape(8.dp))) {
                            Accordion(modifier = Modifier.fillMaxWidth(), defaultValue = setOf("billing")) {
                                AccordionItem("billing") {
                                AccordionTrigger("How does billing work?", Modifier.padding(horizontal = 16.dp))
                                AccordionContent("We offer monthly and annual subscription plans. Billing is charged at the beginning of each cycle, and you can cancel anytime. All plans include automatic backups, 24/7 support, and unlimited team members.", Modifier.padding(horizontal = 16.dp))
                            }
                                AccordionItem("security") {
                                AccordionTrigger("Is my data secure?", Modifier.padding(horizontal = 16.dp))
                                AccordionContent("Yes. We use end-to-end encryption, SOC 2 Type II compliance, and regular third-party security audits. All data is encrypted at rest and in transit using industry-standard protocols.", Modifier.padding(horizontal = 16.dp))
                            }
                                AccordionItem("integration") {
                                AccordionTrigger("What integrations do you support?", Modifier.padding(horizontal = 16.dp))
                                AccordionContent("We integrate with 500+ popular tools including Slack, Zapier, Salesforce, HubSpot, and more. You can also build custom integrations using our REST API and webhooks.", Modifier.padding(horizontal = 16.dp))
                            }
                            }
                        }
                        """.trimIndent(),
                    preview = {
                        Column(
                            Modifier.widthIn(
                                max = 384.dp,
                            ).fillMaxWidth().border(1.dp, currentTheme.colors.border, RoundedCornerShape(8.dp)),
                        ) {
                            Accordion(modifier = Modifier.fillMaxWidth(), defaultValue = setOf("billing")) {
                                AccordionItem("billing") {
                                    AccordionTrigger("How does billing work?", Modifier.padding(horizontal = 16.dp))
                                    AccordionContent(
                                        (
                                            "We offer monthly and annual subscription plans. Billing is " +
                                                "charged at the beginning of each cycle, and you can cancel " +
                                                "anytime. All plans include automatic backups, 24/7 support, " +
                                                "and unlimited team members."
                                        ),
                                        Modifier.padding(horizontal = 16.dp),
                                    )
                                }
                                AccordionItem("security") {
                                    AccordionTrigger("Is my data secure?", Modifier.padding(horizontal = 16.dp))
                                    AccordionContent(
                                        (
                                            "Yes. We use end-to-end encryption, SOC 2 Type II compliance, " +
                                                "and regular third-party security audits. All data is " +
                                                "encrypted at rest and in transit using industry-standard " +
                                                "protocols."
                                        ),
                                        Modifier.padding(horizontal = 16.dp),
                                    )
                                }
                                AccordionItem("integration") {
                                    AccordionTrigger(
                                        "What integrations do you support?",
                                        Modifier.padding(horizontal = 16.dp),
                                    )
                                    AccordionContent(
                                        (
                                            "We integrate with 500+ popular tools including Slack, Zapier, " +
                                                "Salesforce, HubSpot, and more. You can also build custom " +
                                                "integrations using our REST API and webhooks."
                                        ),
                                        Modifier.padding(horizontal = 16.dp),
                                    )
                                }
                            }
                        }
                    },
                ),
                ComponentExample(
                    title = "Card (official)",
                    minPreviewHeight = 435.dp,
                    code =
                        """
                        Card(modifier = Modifier.widthIn(max = 384.dp).fillMaxWidth()) {
                            CardHeader("Subscription & Billing", description = "Common questions about your account, plans, payments and cancellations.")
                            CardContent {
                                Accordion(modifier = Modifier.fillMaxWidth(), defaultValue = setOf("plans")) {
                                    AccordionItem("plans", "What subscription plans do you offer?", "We offer three subscription tiers: Starter (${'$'}9/month), Professional (${'$'}29/month), and Enterprise (${'$'}99/month). Each plan includes increasing storage limits, API access, priority support, and team collaboration features.")
                                    AccordionItem("billing", "How does billing work?", "Billing occurs automatically at the start of each billing cycle. We accept all major credit cards, PayPal, and ACH transfers for enterprise customers. You'll receive an invoice via email after each payment.")
                                    AccordionItem("cancel", "How do I cancel my subscription?", "You can cancel your subscription anytime from your account settings. There are no cancellation fees or penalties. Your access will continue until the end of your current billing period.")
                                }
                            }
                        }
                        """.trimIndent(),
                    preview = {
                        Card(modifier = Modifier.widthIn(max = 384.dp).fillMaxWidth()) {
                            CardHeader(
                                "Subscription & Billing",
                                description = "Common questions about your account, plans, payments and cancellations.",
                            )
                            CardContent {
                                Accordion(modifier = Modifier.fillMaxWidth(), defaultValue = setOf("plans")) {
                                    AccordionItem(
                                        "plans",
                                        "What subscription plans do you offer?",
                                        "We offer three subscription tiers: Starter (${'$'}9/month), " +
                                            "Professional (${'$'}29/month), and Enterprise (${'$'}99/month). " +
                                            "Each plan includes increasing storage limits, API access, " +
                                            "priority support, and team collaboration features.",
                                    )
                                    AccordionItem(
                                        "billing",
                                        "How does billing work?",
                                        (
                                            "Billing occurs automatically at the start of each billing " +
                                                "cycle. We accept all major credit cards, PayPal, and ACH " +
                                                "transfers for enterprise customers. You'll receive an invoice " +
                                                "via email after each payment."
                                        ),
                                    )
                                    AccordionItem(
                                        "cancel",
                                        "How do I cancel my subscription?",
                                        (
                                            "You can cancel your subscription anytime from your account " +
                                                "settings. There are no cancellation fees or penalties. Your " +
                                                "access will continue until the end of your current billing " +
                                                "period."
                                        ),
                                    )
                                }
                            }
                        }
                    },
                ),
                ComponentExample(
                    title = "RTL (official languages)",
                    code =
                        """
                        var language by remember { mutableStateOf(1) }
                        val labels = listOf("English", "Arabic (العربية)", "Hebrew (עברית)")
                        val translations = listOf(
                            listOf("How do I reset my password?", "Can I change my subscription plan?", "What payment methods do you accept?", "Click on 'Forgot Password' on the login page, enter your email address, and we'll send you a link to reset your password.", "Yes, you can upgrade or downgrade your plan at any time from your account settings. Changes will be reflected in your next billing cycle.", "We accept all major credit cards, PayPal, and bank transfers. All payments are processed securely through our payment partners."),
                            listOf("كيف يمكنني إعادة تعيين كلمة المرور؟", "هل يمكنني تغيير خطة الاشتراك الخاصة بي؟", "ما هي طرق الدفع التي تقبلونها؟", "انقر على 'نسيت كلمة المرور' في صفحة تسجيل الدخول، أدخل عنوان بريدك الإلكتروني، وسنرسل لك رابطًا لإعادة تعيين كلمة المرور. سينتهي صلاحية الرابط خلال 24 ساعة.", "نعم، يمكنك ترقية أو تخفيض خطتك في أي وقت من إعدادات حسابك. ستظهر التغييرات في دورة الفوترة التالية.", "نقبل جميع بطاقات الائتمان الرئيسية و PayPal والتحويلات المصرفية. تتم معالجة جميع المدفوعات بأمان من خلال شركاء الدفع لدينا."),
                            listOf("איך אני מאפס את הסיסמה שלי?", "האם אני יכול לשנות את תוכנית המנוי שלי?", "אילו אמצעי תשלום אתם מקבלים?", "לחץ על 'שכחתי סיסמה' בעמוד ההתחברות, הזן את כתובת האימייל שלך, ונשלח לך קישור לאיפוס הסיסמה. הקישור יפוג תוך 24 שעות.", "כן, אתה יכול לשדרג או להוריד את התוכנית שלך בכל עת מההגדרות של החשבון שלך. השינויים יבואו לידי ביטוי במחזור החיוב הבא.", "אנו מקבלים כרטיסי אשראי, PayPal והעברות בנקאיות.")
                        )
                        Column(Modifier.widthIn(max = 384.dp).fillMaxWidth()) {
                            Select(value = labels[language], onValueChange = { next -> language = labels.indexOf(next) }) {
                                SelectTrigger(Modifier.width(180.dp))
                                SelectContent { labels.forEach { SelectItem(it) } }
                            }
                            CompositionLocalProvider(LocalLayoutDirection provides if (language == 0) LayoutDirection.Ltr else LayoutDirection.Rtl) {
                                Accordion(defaultValue = setOf("item-1")) {
                                    repeat(3) { index ->
                                        AccordionItem("item-${'$'}{index + 1}", translations[language][index], translations[language][index + 3])
                                    }
                                }
                            }
                        }
                        """.trimIndent(),
                    preview = {
                        var language by remember { mutableStateOf(1) }
                        val labels = listOf("English", "Arabic (العربية)", "Hebrew (עברית)")
                        val translations =
                            listOf(
                                listOf(
                                    "How do I reset my password?",
                                    "Can I change my subscription plan?",
                                    "What payment methods do you accept?",
                                    (
                                        "Click on 'Forgot Password' on the login page, enter your " +
                                            "email address, and we'll send you a link to reset your " +
                                            "password."
                                    ),
                                    (
                                        "Yes, you can upgrade or downgrade your plan at any time from " +
                                            "your account settings. Changes will be reflected in your next " +
                                            "billing cycle."
                                    ),
                                    (
                                        "We accept all major credit cards, PayPal, and bank transfers. " +
                                            "All payments are processed securely through our payment " +
                                            "partners."
                                    ),
                                ),
                                listOf(
                                    "كيف يمكنني إعادة تعيين كلمة المرور؟",
                                    "هل يمكنني تغيير خطة الاشتراك الخاصة بي؟",
                                    "ما هي طرق الدفع التي تقبلونها؟",
                                    (
                                        "انقر على 'نسيت كلمة المرور' في صفحة تسجيل الدخول، أدخل عنوان " +
                                            "بريدك الإلكتروني، وسنرسل لك رابطًا لإعادة تعيين كلمة المرور. " +
                                            "سينتهي صلاحية الرابط خلال 24 ساعة."
                                    ),
                                    (
                                        "نعم، يمكنك ترقية أو تخفيض خطتك في أي وقت من إعدادات حسابك. " +
                                            "ستظهر التغييرات في دورة الفوترة التالية."
                                    ),
                                    (
                                        "نقبل جميع بطاقات الائتمان الرئيسية و PayPal والتحويلات " +
                                            "المصرفية. تتم معالجة جميع المدفوعات بأمان من خلال شركاء الدفع " +
                                            "لدينا."
                                    ),
                                ),
                                listOf(
                                    "איך אני מאפס את הסיסמה שלי?",
                                    "האם אני יכול לשנות את תוכנית המנוי שלי?",
                                    "אילו אמצעי תשלום אתם מקבלים?",
                                    (
                                        "לחץ על 'שכחתי סיסמה' בעמוד ההתחברות, הזן את כתובת האימייל " +
                                            "שלך, ונשלח לך קישור לאיפוס הסיסמה. הקישור יפוג תוך 24 שעות."
                                    ),
                                    (
                                        "כן, אתה יכול לשדרג או להוריד את התוכנית שלך בכל עת מההגדרות " +
                                            "של החשבון שלך. השינויים יבואו לידי ביטוי במחזור החיוב הבא."
                                    ),
                                    "אנו מקבלים כרטיסי אשראי, PayPal והעברות בנקאיות.",
                                ),
                            )
                        Column(Modifier.widthIn(max = 384.dp).fillMaxWidth()) {
                            Select(value = labels[language], onValueChange = {
                                    next ->
                                language = labels.indexOf(next)
                            }) {
                                SelectTrigger(Modifier.width(180.dp))
                                SelectContent { labels.forEach { SelectItem(it) } }
                            }
                            CompositionLocalProvider(
                                LocalLayoutDirection provides
                                    if (language == 0) {
                                        LayoutDirection.Ltr
                                    } else {
                                        LayoutDirection.Rtl
                                    },
                            ) {
                                Accordion(defaultValue = setOf("item-1")) {
                                    repeat(3) { index ->
                                        AccordionItem(
                                            "item-${index + 1}",
                                            translations[language][index],
                                            translations[language][index + 3],
                                        )
                                    }
                                }
                            }
                        }
                    },
                ),
                ComponentExample(
                    title = "Custom icon",
                    code =
                        """
                        // Accordion's chevron is a plain glyph by default (this library
                        // takes no icon-library dependency) -- override the icon slot with a
                        // real vector from any icon set, e.g. Lucide. The slot
                        // receives isOpen so the override can drive its own rotation too.
                        var value by remember { mutableStateOf(setOf("item-1")) }
                        Accordion(
                            value = value,
                            onValueChange = { value = it },
                            icon = { isOpen ->
                                val rotation by animateFloatAsState(if (isOpen) 180f else 0f)
                                DocIcon(Lucide.ChevronDown, modifier = Modifier.rotate(rotation))
                            },
                        ) {
                            AccordionItem("item-1", "Is it accessible?", "Yes. It adheres to the WAI-ARIA design pattern.")
                            AccordionItem("item-2", "Is it styled?", "Yes. It comes with default styles.")
                        }
                        """.trimIndent(),
                    preview = {
                        var value by remember { mutableStateOf(setOf("item-1")) }
                        Accordion(
                            value = value,
                            onValueChange = { value = it },
                            icon = { isOpen ->
                                val rotation by animateFloatAsState(if (isOpen) 180f else 0f)
                                DocIcon(Lucide.ChevronDown, modifier = Modifier.rotate(rotation))
                            },
                        ) {
                            AccordionItem(
                                "item-1",
                                "Is it accessible?",
                                "Yes. It adheres to the WAI-ARIA design pattern.",
                            )
                            AccordionItem("item-2", "Is it styled?", "Yes. It comes with default styles.")
                        }
                    },
                ),
                ComponentExample(
                    title = "Multiple",
                    code =
                        """
                        var value by remember { mutableStateOf(setOf("item-1", "item-2")) }
                        Accordion(type = AccordionType.Multiple, value = value, onValueChange = { value = it }) {
                            AccordionItem("item-1", "Is it accessible?", "Yes. It adheres to the WAI-ARIA design pattern.")
                            AccordionItem("item-2", "Is it styled?", "Yes. It comes with default styles.")
                            AccordionItem("item-3", "Is it animated?", "Yes. It's animated by default.")
                        }
                        """.trimIndent(),
                    preview = {
                        var value by remember { mutableStateOf(setOf("item-1", "item-2")) }
                        Accordion(type = AccordionType.Multiple, value = value, onValueChange = { value = it }) {
                            AccordionItem(
                                "item-1",
                                "Is it accessible?",
                                "Yes. It adheres to the WAI-ARIA design pattern.",
                            )
                            AccordionItem("item-2", "Is it styled?", "Yes. It comes with default styles.")
                            AccordionItem("item-3", "Is it animated?", "Yes. It's animated by default.")
                        }
                    },
                ),
                ComponentExample(
                    title = "Single",
                    code =
                        """
                        var value by remember { mutableStateOf(setOf("item-1")) }
                        Accordion(type = AccordionType.Single, value = value, onValueChange = { value = it }) {
                            AccordionItem("item-1", "Is it accessible?", "Yes. It adheres to the WAI-ARIA design pattern.")
                            AccordionItem("item-2", "Is it styled?", "Yes. It comes with default styles.")
                        }
                        """.trimIndent(),
                    preview = {
                        var value by remember { mutableStateOf(setOf("item-1")) }
                        Accordion(type = AccordionType.Single, value = value, onValueChange = { value = it }) {
                            AccordionItem(
                                "item-1",
                                "Is it accessible?",
                                "Yes. It adheres to the WAI-ARIA design pattern.",
                            )
                            AccordionItem("item-2", "Is it styled?", "Yes. It comes with default styles.")
                        }
                    },
                ),
                ComponentExample(
                    title = "Default type",
                    code =
                        """
                        var value by remember { mutableStateOf(setOf("item-1")) }
                        Accordion(type = AccordionType.Single, value = value, onValueChange = { value = it }) {
                            AccordionItem("item-1", "Is it accessible?", "Yes. It adheres to the WAI-ARIA design pattern.")
                            AccordionItem("item-2", "Is it styled?", "Yes. It comes with default styles.")
                        }
                        """.trimIndent(),
                    preview = {
                        var value by remember { mutableStateOf(setOf("item-1")) }
                        Accordion(type = AccordionType.Single, value = value, onValueChange = { value = it }) {
                            AccordionItem(
                                "item-1",
                                "Is it accessible?",
                                "Yes. It adheres to the WAI-ARIA design pattern.",
                            )
                            AccordionItem("item-2", "Is it styled?", "Yes. It comes with default styles.")
                        }
                    },
                ),
                ComponentExample(
                    title = "Default multiple",
                    code =
                        """
                        var value by remember { mutableStateOf(setOf("item-1")) }
                        Accordion(
                            type = AccordionType.Single,
                            multiple = null,
                            value = value,
                            onValueChange = { value = it },
                        ) {
                            AccordionItem("item-1", "Is it accessible?", "Yes. It adheres to the WAI-ARIA design pattern.")
                            AccordionItem("item-2", "Is it styled?", "Yes. It comes with default styles.")
                        }
                        """.trimIndent(),
                    preview = {
                        var value by remember { mutableStateOf(setOf("item-1")) }
                        Accordion(
                            type = AccordionType.Single,
                            multiple = null,
                            value = value,
                            onValueChange = { value = it },
                        ) {
                            AccordionItem(
                                "item-1",
                                "Is it accessible?",
                                "Yes. It adheres to the WAI-ARIA design pattern.",
                            )
                            AccordionItem("item-2", "Is it styled?", "Yes. It comes with default styles.")
                        }
                    },
                ),
                ComponentExample(
                    title = "Not collapsible",
                    code =
                        """
                        var value by remember { mutableStateOf(setOf("item-1")) }
                        Accordion(
                            type = AccordionType.Single,
                            collapsible = false,
                            value = value,
                            onValueChange = { value = it },
                        ) {
                            AccordionItem("item-1", "Is it accessible?", "Yes. It adheres to the WAI-ARIA design pattern.")
                            AccordionItem("item-2", "Is it styled?", "Yes. It comes with default styles.")
                        }
                        """.trimIndent(),
                    preview = {
                        var value by remember { mutableStateOf(setOf("item-1")) }
                        Accordion(
                            type = AccordionType.Single,
                            collapsible = false,
                            value = value,
                            onValueChange = { value = it },
                        ) {
                            AccordionItem(
                                "item-1",
                                "Is it accessible?",
                                "Yes. It adheres to the WAI-ARIA design pattern.",
                            )
                            AccordionItem("item-2", "Is it styled?", "Yes. It comes with default styles.")
                        }
                    },
                ),
                ComponentExample(
                    title = "Collapsible",
                    code =
                        """
                        var value by remember { mutableStateOf(setOf("item-1")) }
                        Accordion(
                            type = AccordionType.Single,
                            collapsible = true,
                            value = value,
                            onValueChange = { value = it },
                        ) {
                            AccordionItem("item-1", "Is it accessible?", "Yes. It adheres to the WAI-ARIA design pattern.")
                            AccordionItem("item-2", "Is it styled?", "Yes. It comes with default styles.")
                        }
                        """.trimIndent(),
                    preview = {
                        var value by remember { mutableStateOf(setOf("item-1")) }
                        Accordion(
                            type = AccordionType.Single,
                            collapsible = true,
                            value = value,
                            onValueChange = { value = it },
                        ) {
                            AccordionItem(
                                "item-1",
                                "Is it accessible?",
                                "Yes. It adheres to the WAI-ARIA design pattern.",
                            )
                            AccordionItem("item-2", "Is it styled?", "Yes. It comes with default styles.")
                        }
                    },
                ),
                ComponentExample(
                    title = "Default collapsible",
                    code =
                        """
                        var value by remember { mutableStateOf(setOf("item-1")) }
                        Accordion(
                            type = AccordionType.Single,
                            collapsible = true,
                            value = value,
                            onValueChange = { value = it },
                        ) {
                            AccordionItem("item-1", "Is it accessible?", "Yes. It adheres to the WAI-ARIA design pattern.")
                            AccordionItem("item-2", "Is it styled?", "Yes. It comes with default styles.")
                        }
                        """.trimIndent(),
                    preview = {
                        var value by remember { mutableStateOf(setOf("item-1")) }
                        Accordion(
                            type = AccordionType.Single,
                            collapsible = true,
                            value = value,
                            onValueChange = { value = it },
                        ) {
                            AccordionItem(
                                "item-1",
                                "Is it accessible?",
                                "Yes. It adheres to the WAI-ARIA design pattern.",
                            )
                            AccordionItem("item-2", "Is it styled?", "Yes. It comes with default styles.")
                        }
                    },
                ),
                ComponentExample(
                    title = "Disabled",
                    code =
                        """
                        var value by remember { mutableStateOf(setOf("item-1")) }
                        Accordion(type = AccordionType.Single, value = value, onValueChange = { value = it }) {
                            AccordionItem("item-1", "Is it accessible?", "Yes. It adheres to the WAI-ARIA design pattern.")
                            AccordionItem(
                                "item-2",
                                "Is it disabled?",
                                "This item cannot be opened.",
                                disabled = true,
                            )
                        }
                        """.trimIndent(),
                    preview = {
                        var value by remember { mutableStateOf(setOf("item-1")) }
                        Accordion(type = AccordionType.Single, value = value, onValueChange = { value = it }) {
                            AccordionItem(
                                "item-1",
                                "Is it accessible?",
                                "Yes. It adheres to the WAI-ARIA design pattern.",
                            )
                            AccordionItem(
                                "item-2",
                                "Is it disabled?",
                                "This item cannot be opened.",
                                disabled = true,
                            )
                        }
                    },
                ),
                ComponentExample(
                    title = "Default disabled",
                    code =
                        """
                        Accordion(defaultValue = "item-1") {
                            AccordionItem("item-1", "Is it accessible?", "Yes. It adheres to the WAI-ARIA design pattern.")
                            AccordionItem(
                                "item-2",
                                "Is it styled?",
                                "Yes. It comes with default styles.",
                                disabled = false,
                            )
                        }
                        """.trimIndent(),
                    preview = {
                        Accordion(defaultValue = "item-1") {
                            AccordionItem(
                                "item-1",
                                "Is it accessible?",
                                "Yes. It adheres to the WAI-ARIA design pattern.",
                            )
                            AccordionItem(
                                "item-2",
                                "Is it styled?",
                                "Yes. It comes with default styles.",
                                disabled = false,
                            )
                        }
                    },
                ),
                ComponentExample(
                    title = "Default value",
                    code =
                        """
                        Accordion(defaultValue = "item-1") {
                            AccordionItem("item-1", "Is it accessible?", "Yes. It adheres to the WAI-ARIA design pattern.")
                            AccordionItem("item-2", "Is it styled?", "Yes. It comes with default styles.")
                        }
                        """.trimIndent(),
                    preview = {
                        Accordion(defaultValue = "item-1") {
                            AccordionItem(
                                "item-1",
                                "Is it accessible?",
                                "Yes. It adheres to the WAI-ARIA design pattern.",
                            )
                            AccordionItem("item-2", "Is it styled?", "Yes. It comes with default styles.")
                        }
                    },
                ),
                ComponentExample(
                    title = "Horizontal",
                    code =
                        """
                        var value by remember { mutableStateOf(setOf("item-1")) }
                        Accordion(
                            type = AccordionType.Single,
                            value = value,
                            onValueChange = { value = it },
                            orientation = AccordionOrientation.Horizontal,
                        ) {
                            AccordionItem("item-1", "Accessible?", "Yes. It adheres to WAI-ARIA.")
                            AccordionItem("item-2", "Styled?", "Yes. It comes with default styles.")
                        }
                        """.trimIndent(),
                    preview = {
                        var value by remember { mutableStateOf(setOf("item-1")) }
                        Accordion(
                            type = AccordionType.Single,
                            value = value,
                            onValueChange = { value = it },
                            orientation = AccordionOrientation.Horizontal,
                        ) {
                            AccordionItem("item-1", "Accessible?", "Yes. It adheres to WAI-ARIA.")
                            AccordionItem("item-2", "Styled?", "Yes. It comes with default styles.")
                        }
                    },
                ),
                ComponentExample(
                    title = "Vertical",
                    code =
                        """
                        var value by remember { mutableStateOf(setOf("item-1")) }
                        Accordion(
                            type = AccordionType.Single,
                            value = value,
                            onValueChange = { value = it },
                            orientation = AccordionOrientation.Vertical,
                        ) {
                            AccordionItem("item-1", "Accessible?", "Yes. It adheres to WAI-ARIA.")
                            AccordionItem("item-2", "Styled?", "Yes. It comes with default styles.")
                        }
                        """.trimIndent(),
                    preview = {
                        var value by remember { mutableStateOf(setOf("item-1")) }
                        Accordion(
                            type = AccordionType.Single,
                            value = value,
                            onValueChange = { value = it },
                            orientation = AccordionOrientation.Vertical,
                        ) {
                            AccordionItem("item-1", "Accessible?", "Yes. It adheres to WAI-ARIA.")
                            AccordionItem("item-2", "Styled?", "Yes. It comes with default styles.")
                        }
                    },
                ),
                ComponentExample(
                    title = "Default orientation",
                    code =
                        """
                        var value by remember { mutableStateOf(setOf("item-1")) }
                        Accordion(
                            type = AccordionType.Single,
                            value = value,
                            onValueChange = { value = it },
                            orientation = AccordionOrientation.Vertical,
                        ) {
                            AccordionItem("item-1", "Accessible?", "Yes. It adheres to WAI-ARIA.")
                            AccordionItem("item-2", "Styled?", "Yes. It comes with default styles.")
                        }
                        """.trimIndent(),
                    preview = {
                        var value by remember { mutableStateOf(setOf("item-1")) }
                        Accordion(
                            type = AccordionType.Single,
                            value = value,
                            onValueChange = { value = it },
                            orientation = AccordionOrientation.Vertical,
                        ) {
                            AccordionItem("item-1", "Accessible?", "Yes. It adheres to WAI-ARIA.")
                            AccordionItem("item-2", "Styled?", "Yes. It comes with default styles.")
                        }
                    },
                ),
                ComponentExample(
                    title = "No loop",
                    code =
                        """
                        var value by remember { mutableStateOf(setOf("item-1")) }
                        Accordion(
                            type = AccordionType.Single,
                            value = value,
                            onValueChange = { value = it },
                            loop = false,
                        ) {
                            AccordionItem("item-1", "Accessible?", "Yes. It adheres to WAI-ARIA.")
                            AccordionItem("item-2", "Styled?", "Yes. It comes with default styles.")
                        }
                        """.trimIndent(),
                    preview = {
                        var value by remember { mutableStateOf(setOf("item-1")) }
                        Accordion(
                            type = AccordionType.Single,
                            value = value,
                            onValueChange = { value = it },
                            loop = false,
                        ) {
                            AccordionItem("item-1", "Accessible?", "Yes. It adheres to WAI-ARIA.")
                            AccordionItem("item-2", "Styled?", "Yes. It comes with default styles.")
                        }
                    },
                ),
                ComponentExample(
                    title = "Loop",
                    code =
                        """
                        var value by remember { mutableStateOf(setOf("item-1")) }
                        Accordion(
                            type = AccordionType.Single,
                            value = value,
                            onValueChange = { value = it },
                            loop = true,
                        ) {
                            AccordionItem("item-1", "Accessible?", "Yes. It adheres to WAI-ARIA.")
                            AccordionItem("item-2", "Styled?", "Yes. It comes with default styles.")
                        }
                        """.trimIndent(),
                    preview = {
                        var value by remember { mutableStateOf(setOf("item-1")) }
                        Accordion(
                            type = AccordionType.Single,
                            value = value,
                            onValueChange = { value = it },
                            loop = true,
                        ) {
                            AccordionItem("item-1", "Accessible?", "Yes. It adheres to WAI-ARIA.")
                            AccordionItem("item-2", "Styled?", "Yes. It comes with default styles.")
                        }
                    },
                ),
                ComponentExample(
                    title = "Default loop",
                    code =
                        """
                        var value by remember { mutableStateOf(setOf("item-1")) }
                        Accordion(
                            type = AccordionType.Single,
                            value = value,
                            onValueChange = { value = it },
                            loop = true,
                        ) {
                            AccordionItem("item-1", "Accessible?", "Yes. It adheres to WAI-ARIA.")
                            AccordionItem("item-2", "Styled?", "Yes. It comes with default styles.")
                        }
                        """.trimIndent(),
                    preview = {
                        var value by remember { mutableStateOf(setOf("item-1")) }
                        Accordion(
                            type = AccordionType.Single,
                            value = value,
                            onValueChange = { value = it },
                            loop = true,
                        ) {
                            AccordionItem("item-1", "Accessible?", "Yes. It adheres to WAI-ARIA.")
                            AccordionItem("item-2", "Styled?", "Yes. It comes with default styles.")
                        }
                    },
                ),
                ComponentExample(
                    title = "Disabled accordion",
                    code =
                        """
                        Accordion(type = AccordionType.Single, defaultValue = setOf("item-1"), disabled = true) {
                            AccordionItem("item-1", "Accessible?", "Yes. It adheres to WAI-ARIA.")
                            AccordionItem("item-2", "Styled?", "Yes. It comes with default styles.")
                        }
                        """.trimIndent(),
                    preview = {
                        Accordion(
                            type = AccordionType.Single,
                            defaultValue = setOf("item-1"),
                            disabled = true,
                        ) {
                            AccordionItem("item-1", "Accessible?", "Yes. It adheres to WAI-ARIA.")
                            AccordionItem("item-2", "Styled?", "Yes. It comes with default styles.")
                        }
                    },
                ),
                ComponentExample(
                    title = "Disabled trigger",
                    code =
                        """
                        Accordion(type = AccordionType.Single, defaultValue = setOf("item-1")) {
                            AccordionItem("item-1") {
                                AccordionTrigger("Accessible?")
                                AccordionContent("Yes. It adheres to WAI-ARIA.")
                            }
                            AccordionItem("item-2") {
                                AccordionTrigger("Disabled?", disabled = true)
                                AccordionContent("This trigger cannot be opened.")
                            }
                        }
                        """.trimIndent(),
                    preview = {
                        Accordion(type = AccordionType.Single, defaultValue = setOf("item-1")) {
                            AccordionItem("item-1") {
                                AccordionTrigger("Accessible?")
                                AccordionContent("Yes. It adheres to WAI-ARIA.")
                            }
                            AccordionItem("item-2") {
                                AccordionTrigger("Disabled?", disabled = true)
                                AccordionContent("This trigger cannot be opened.")
                            }
                        }
                    },
                ),
                ComponentExample(
                    title = "Controlled value",
                    code =
                        """
                        var value by remember { mutableStateOf("item-1") }
                        Accordion(value = value, onValueChange = { value = it }) {
                            AccordionItem("item-1", "Is it accessible?", "Yes. It adheres to the WAI-ARIA design pattern.")
                            AccordionItem("item-2", "Is it styled?", "Yes. It comes with default styles.")
                        }
                        """.trimIndent(),
                    preview = {
                        var value by remember { mutableStateOf("item-1") }
                        Accordion(value = value, onValueChange = { value = it }) {
                            AccordionItem(
                                "item-1",
                                "Is it accessible?",
                                "Yes. It adheres to the WAI-ARIA design pattern.",
                            )
                            AccordionItem("item-2", "Is it styled?", "Yes. It comes with default styles.")
                        }
                    },
                ),
                ComponentExample(
                    title = "Multiple flag",
                    code =
                        """
                        var value by remember { mutableStateOf(setOf("item-1", "item-2")) }
                        Accordion(multiple = true, value = value, onValueChange = { value = it }) {
                            AccordionItem("item-1", "Is it accessible?", "Yes. It adheres to the WAI-ARIA design pattern.")
                            AccordionItem("item-2", "Is it styled?", "Yes. It comes with default styles.")
                            AccordionItem("item-3", "Is it animated?", "Yes. It's animated by default.")
                        }
                        """.trimIndent(),
                    preview = {
                        var value by remember { mutableStateOf(setOf("item-1", "item-2")) }
                        Accordion(multiple = true, value = value, onValueChange = { value = it }) {
                            AccordionItem(
                                "item-1",
                                "Is it accessible?",
                                "Yes. It adheres to the WAI-ARIA design pattern.",
                            )
                            AccordionItem("item-2", "Is it styled?", "Yes. It comes with default styles.")
                            AccordionItem("item-3", "Is it animated?", "Yes. It's animated by default.")
                        }
                    },
                ),
                ComponentExample(
                    title = "Keep mounted",
                    code =
                        """
                        Accordion(defaultValue = setOf("item-1")) {
                            AccordionItem("item-1") {
                                AccordionTrigger("Accessible?")
                                AccordionContent("Yes. It adheres to WAI-ARIA.", keepMounted = true)
                            }
                        }
                        """.trimIndent(),
                    preview = {
                        Accordion(defaultValue = setOf("item-1")) {
                            AccordionItem("item-1") {
                                AccordionTrigger("Accessible?")
                                AccordionContent("Yes. It adheres to WAI-ARIA.", keepMounted = true)
                            }
                        }
                    },
                ),
                ComponentExample(
                    title = "Not keep mounted",
                    code =
                        """
                        Accordion(defaultValue = setOf("item-1")) {
                            AccordionItem("item-1") {
                                AccordionTrigger("Accessible?")
                                AccordionContent("Yes. It adheres to WAI-ARIA.", keepMounted = false)
                            }
                        }
                        """.trimIndent(),
                    preview = {
                        Accordion(defaultValue = setOf("item-1")) {
                            AccordionItem("item-1") {
                                AccordionTrigger("Accessible?")
                                AccordionContent("Yes. It adheres to WAI-ARIA.", keepMounted = false)
                            }
                        }
                    },
                ),
                ComponentExample(
                    title = "Default keep mounted",
                    code =
                        """
                        Accordion(defaultValue = setOf("item-1")) {
                            AccordionItem("item-1") {
                                AccordionTrigger("Accessible?")
                                AccordionContent("Yes. It adheres to WAI-ARIA.", keepMounted = false)
                            }
                        }
                        """.trimIndent(),
                    preview = {
                        Accordion(defaultValue = setOf("item-1")) {
                            AccordionItem("item-1") {
                                AccordionTrigger("Accessible?")
                                AccordionContent("Yes. It adheres to WAI-ARIA.", keepMounted = false)
                            }
                        }
                    },
                ),
                ComponentExample(
                    title = "Force mount",
                    code =
                        """
                        Accordion(defaultValue = setOf("item-1")) {
                            AccordionItem("item-1") {
                                AccordionTrigger("Accessible?")
                                AccordionContent("Yes. It adheres to WAI-ARIA.", forceMount = true)
                            }
                        }
                        """.trimIndent(),
                    preview = {
                        Accordion(defaultValue = setOf("item-1")) {
                            AccordionItem("item-1") {
                                AccordionTrigger("Accessible?")
                                AccordionContent("Yes. It adheres to WAI-ARIA.", forceMount = true)
                            }
                        }
                    },
                ),
                ComponentExample(
                    title = "Not force mount",
                    code =
                        """
                        Accordion(defaultValue = setOf("item-1")) {
                            AccordionItem("item-1") {
                                AccordionTrigger("Accessible?")
                                AccordionContent("Yes. It adheres to WAI-ARIA.", forceMount = false)
                            }
                        }
                        """.trimIndent(),
                    preview = {
                        Accordion(defaultValue = setOf("item-1")) {
                            AccordionItem("item-1") {
                                AccordionTrigger("Accessible?")
                                AccordionContent("Yes. It adheres to WAI-ARIA.", forceMount = false)
                            }
                        }
                    },
                ),
                ComponentExample(
                    title = "Default force mount",
                    code =
                        """
                        Accordion(defaultValue = setOf("item-1")) {
                            AccordionItem("item-1") {
                                AccordionTrigger("Accessible?")
                                AccordionContent("Yes. It adheres to WAI-ARIA.", forceMount = false)
                            }
                        }
                        """.trimIndent(),
                    preview = {
                        Accordion(defaultValue = setOf("item-1")) {
                            AccordionItem("item-1") {
                                AccordionTrigger("Accessible?")
                                AccordionContent("Yes. It adheres to WAI-ARIA.", forceMount = false)
                            }
                        }
                    },
                ),
                ComponentExample(
                    title = "Uncontrolled",
                    code =
                        """
                        Accordion {
                            AccordionItem("item-1", "Is it accessible?", "Yes. It adheres to the WAI-ARIA design pattern.")
                            AccordionItem("item-2", "Is it styled?", "Yes. It comes with default styles.")
                            AccordionItem("item-3", "Is it animated?", "Yes. It's animated by default.")
                        }
                        """.trimIndent(),
                    preview = {
                        Accordion {
                            AccordionItem(
                                "item-1",
                                "Is it accessible?",
                                "Yes. It adheres to the WAI-ARIA design pattern.",
                            )
                            AccordionItem("item-2", "Is it styled?", "Yes. It comes with default styles.")
                            AccordionItem("item-3", "Is it animated?", "Yes. It's animated by default.")
                        }
                    },
                ),
                ComponentExample(
                    title = "Content",
                    code =
                        """
                        Accordion {
                            AccordionItem("item-1") {
                                AccordionTrigger("Is it accessible?")
                                AccordionContent("Yes. It adheres to the WAI-ARIA design pattern.")
                            }
                        }
                        """.trimIndent(),
                    preview = {
                        Accordion {
                            AccordionItem("item-1") {
                                AccordionTrigger("Is it accessible?")
                                AccordionContent("Yes. It adheres to the WAI-ARIA design pattern.")
                            }
                        }
                    },
                ),
                ComponentExample(
                    title = "Item",
                    code =
                        """
                        Accordion {
                            AccordionItem("item-1", "Is it accessible?", "Yes. It adheres to the WAI-ARIA design pattern.")
                        }
                        """.trimIndent(),
                    preview = {
                        Accordion {
                            AccordionItem(
                                "item-1",
                                "Is it accessible?",
                                "Yes. It adheres to the WAI-ARIA design pattern.",
                            )
                        }
                    },
                ),
                ComponentExample(
                    title = "Trigger",
                    code =
                        """
                        Accordion {
                            AccordionItem("item-1") {
                                AccordionTrigger("Is it accessible?")
                                AccordionContent("Yes. It adheres to the WAI-ARIA design pattern.")
                            }
                        }
                        """.trimIndent(),
                    preview = {
                        Accordion {
                            AccordionItem("item-1") {
                                AccordionTrigger("Is it accessible?")
                                AccordionContent("Yes. It adheres to the WAI-ARIA design pattern.")
                            }
                        }
                    },
                ),
            ),
    )
