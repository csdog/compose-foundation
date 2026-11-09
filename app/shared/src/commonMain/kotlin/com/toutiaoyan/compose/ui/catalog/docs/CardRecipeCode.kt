package com.toutiaoyan.compose.ui.catalog.docs

// Derived from the actual recipe bodies; catalog navigation/toolbars are replaced with public APIs.
internal fun cardRecipeCode(index: Int): String = cardRecipeCodes[index]

private val cardRecipeCodes =
    listOf(
        """
                @file:OptIn(androidx.compose.foundation.style.ExperimentalFoundationStyleApi::class)


                import androidx.compose.foundation.Image
                import androidx.compose.foundation.background
                import androidx.compose.foundation.gestures.detectTapGestures
                import androidx.compose.foundation.layout.*
                import androidx.compose.foundation.rememberScrollState
                import androidx.compose.foundation.style.Style
                import androidx.compose.foundation.style.contentPadding
                import androidx.compose.foundation.style.fillWidth
                import androidx.compose.foundation.verticalScroll
                import androidx.compose.runtime.*
                import androidx.compose.ui.Alignment
                import androidx.compose.ui.Modifier
                import androidx.compose.ui.draw.drawWithContent
                import androidx.compose.ui.focus.FocusRequester
                import androidx.compose.ui.focus.focusRequester
                import androidx.compose.ui.geometry.Offset
                import androidx.compose.ui.graphics.*
                import androidx.compose.ui.input.pointer.pointerInput
                import androidx.compose.ui.layout.ContentScale
                import androidx.compose.ui.layout.layout
                import androidx.compose.ui.platform.LocalLayoutDirection
                import androidx.compose.ui.platform.LocalUriHandler
                import androidx.compose.ui.semantics.contentDescription
                import androidx.compose.ui.semantics.semantics
                import androidx.compose.ui.text.*
                import androidx.compose.ui.text.font.FontWeight
                import androidx.compose.ui.text.style.LineHeightStyle
        import androidx.compose.ui.text.style.TextDecoration
                import androidx.compose.ui.unit.LayoutDirection
                import androidx.compose.ui.unit.dp
                import androidx.compose.ui.unit.sp
                import com.composables.icons.lucide.ChevronRight
                import com.composables.icons.lucide.Lucide
                import com.toutiaoyan.compose.ui.components.badge.*
                import com.toutiaoyan.compose.ui.components.button.*
                import com.toutiaoyan.compose.ui.components.card.*
                import com.toutiaoyan.compose.ui.components.icon.Icon
                import com.toutiaoyan.compose.ui.components.input.*
                import com.toutiaoyan.compose.ui.components.label.Label
                import com.toutiaoyan.compose.ui.components.text.Text
                import com.toutiaoyan.compose.ui.components.toggle.*
                import com.toutiaoyan.compose.ui.components.togglegroup.*
                import com.toutiaoyan.compose.ui.theme.Theme
                import com.toutiaoyan.compose.ui.theme.currentTheme
                import com.toutiaoyan.compose.tailwind.core.LocalUnitSpacing
                import compose_foundation.app.shared.generated.resources.*
                import org.jetbrains.compose.resources.painterResource

                import com.toutiaoyan.compose.ui.components.select.*
                import androidx.compose.ui.text.font.FontFamily
                import org.jetbrains.compose.resources.Font

                // Supply the referenced image/font resources in your application.
                private data class CardLoginCopy(
                    val title: String,
                    val description: String,
                    val signUp: String,
                    val email: String,
                    val password: String,
                    val forgotPassword: String,
                    val login: String,
                    val google: String,
                )

                private val cardLoginCopies = listOf(
                    CardLoginCopy("Login to your account", "Enter your email below to login to your account", "Sign Up", "Email", "Password", "Forgot your password?", "Login", "Login with Google"),
                    CardLoginCopy("تسجيل الدخول إلى حسابك", "أدخل بريدك الإلكتروني أدناه لتسجيل الدخول إلى حسابك", "إنشاء حساب", "البريد الإلكتروني", "كلمة المرور", "نسيت كلمة المرور؟", "تسجيل الدخول", "تسجيل الدخول باستخدام Google"),
                    CardLoginCopy("התחבר לחשבון שלך", "הזן את האימייל שלך למטה כדי להתחבר לחשבון שלך", "הירשם", "אימייל", "סיסמה", "שכחת את הסיסמה?", "התחבר", "התחבר עם Google"),
                )

                @Composable
                private fun CardLogin(spacing: Int = 16, language: Int = 0) {
                    var email by remember { mutableStateOf("") }
                    var password by remember { mutableStateOf("") }
                    val emailFocus = remember { FocusRequester() }
                    val passwordFocus = remember { FocusRequester() }
                    val c = cardLoginCopies[language]
                    Card(Modifier.widthIn(max = 384.dp).fillMaxWidth(), spacing = spacing.dp) {
                        CardHeader {
                            CardTitle(c.title)
                            CardDescription(c.description)
                            CardAction { Button(c.signUp, variant = ButtonVariant.Link) }
                        }
                        CardContent {
                            Column(verticalArrangement = Arrangement.spacedBy(24.dp)) {
                                Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                                    Label(c.email, Modifier.pointerInput(emailFocus) { detectTapGestures { emailFocus.requestFocus() } })
                                    Input(email, { email = it }, placeholder = "m@example.com", type = if (language == 0) InputType.Email else InputType.Text, required = true,
                                        modifier = Modifier.focusRequester(emailFocus).semantics { contentDescription = c.email })
                                }
                                Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                                    Row(Modifier.fillMaxWidth(), verticalAlignment = Alignment.CenterVertically) {
                                        Label(c.password, Modifier.pointerInput(passwordFocus) { detectTapGestures { passwordFocus.requestFocus() } })
                                        Spacer(Modifier.weight(1f))
                                        CardForgotLink(c.forgotPassword)
                                    }
                                    Input(password, { password = it }, type = InputType.Password, required = true,
                                        modifier = Modifier.focusRequester(passwordFocus).semantics { contentDescription = c.password })
                                }
                            }
                        }
                        CardFooter {
                            Column(Modifier.fillMaxWidth(), verticalArrangement = Arrangement.spacedBy(8.dp)) {
                                Button(c.login, style = Style { fillWidth() })
                                Button(c.google, variant = ButtonVariant.Outline, style = Style { fillWidth() })
                            }
                        }
                    }
                }

                @Composable
                private fun CardForgotLink(text: String) {
                    val uri = LocalUriHandler.current
                    val foreground = currentTheme.colors.foreground
                    Text(textStyle = TextStyle(fontSize = 14.sp, lineHeight = 20.sp, fontWeight = FontWeight.Normal,
                    lineHeightStyle = LineHeightStyle(LineHeightStyle.Alignment.Center, LineHeightStyle.Trim.None, LineHeightStyle.Mode.Tight))) {
                        buildAnnotatedString {
                            withLink(LinkAnnotation.Url("#", styles = TextLinkStyles(
                                style = SpanStyle(color = foreground),
                                hoveredStyle = SpanStyle(textDecoration = TextDecoration.Underline),
                            ), linkInteractionListener = { uri.openUri("#") })) { append(text) }
                        }
                    }
                }


                @Composable
                fun CardExample() { Box(Modifier.wrapContentHeight(unbounded = true)) { CardLogin() } }
        """.trimIndent(),
        """
                @file:OptIn(androidx.compose.foundation.style.ExperimentalFoundationStyleApi::class)


                import androidx.compose.foundation.Image
                import androidx.compose.foundation.background
                import androidx.compose.foundation.gestures.detectTapGestures
                import androidx.compose.foundation.layout.*
                import androidx.compose.foundation.rememberScrollState
                import androidx.compose.foundation.style.Style
                import androidx.compose.foundation.style.contentPadding
                import androidx.compose.foundation.style.fillWidth
                import androidx.compose.foundation.verticalScroll
                import androidx.compose.runtime.*
                import androidx.compose.ui.Alignment
                import androidx.compose.ui.Modifier
                import androidx.compose.ui.draw.drawWithContent
                import androidx.compose.ui.focus.FocusRequester
                import androidx.compose.ui.focus.focusRequester
                import androidx.compose.ui.geometry.Offset
                import androidx.compose.ui.graphics.*
                import androidx.compose.ui.input.pointer.pointerInput
                import androidx.compose.ui.layout.ContentScale
                import androidx.compose.ui.layout.layout
                import androidx.compose.ui.platform.LocalLayoutDirection
                import androidx.compose.ui.platform.LocalUriHandler
                import androidx.compose.ui.semantics.contentDescription
                import androidx.compose.ui.semantics.semantics
                import androidx.compose.ui.text.*
                import androidx.compose.ui.text.font.FontWeight
                import androidx.compose.ui.text.style.LineHeightStyle
        import androidx.compose.ui.text.style.TextDecoration
                import androidx.compose.ui.unit.LayoutDirection
                import androidx.compose.ui.unit.dp
                import androidx.compose.ui.unit.sp
                import com.composables.icons.lucide.ChevronRight
                import com.composables.icons.lucide.Lucide
                import com.toutiaoyan.compose.ui.components.badge.*
                import com.toutiaoyan.compose.ui.components.button.*
                import com.toutiaoyan.compose.ui.components.card.*
                import com.toutiaoyan.compose.ui.components.icon.Icon
                import com.toutiaoyan.compose.ui.components.input.*
                import com.toutiaoyan.compose.ui.components.label.Label
                import com.toutiaoyan.compose.ui.components.text.Text
                import com.toutiaoyan.compose.ui.components.toggle.*
                import com.toutiaoyan.compose.ui.components.togglegroup.*
                import com.toutiaoyan.compose.ui.theme.Theme
                import com.toutiaoyan.compose.ui.theme.currentTheme
                import com.toutiaoyan.compose.tailwind.core.LocalUnitSpacing
                import compose_foundation.app.shared.generated.resources.*
                import org.jetbrains.compose.resources.painterResource

                import com.toutiaoyan.compose.ui.components.select.*
                import androidx.compose.ui.text.font.FontFamily
                import org.jetbrains.compose.resources.Font

                // Supply the referenced image/font resources in your application.
                @Composable
                private fun CardSmallRecipe() {
                    Card(Modifier.widthIn(max = 320.dp).fillMaxWidth(), size = CardSize.Sm) {
                        CardHeader("Scheduled reports", "Weekly snapshots. No more manual exports.")
                        CardContent {
                            Column(Modifier.padding(vertical = 8.dp), verticalArrangement = Arrangement.spacedBy(8.dp)) {
                                listOf("Choose a schedule (daily, or weekly).", "Send to channels or specific teammates.", "Include charts, tables, and key metrics.").forEach { item ->
                                    Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                                        Icon(Lucide.ChevronRight, Modifier.padding(top = 2.dp).size(16.dp), tint = currentTheme.colors.mutedForeground)
                                        Text(textStyle = TextStyle(fontSize = 14.sp, lineHeight = 20.sp)) { item }
                                    }
                                }
                            }
                        }
                        CardFooter {
                            Column(Modifier.fillMaxWidth(), verticalArrangement = Arrangement.spacedBy(8.dp)) {
                                Button("Set up scheduled reports", size = ButtonSize.Sm, style = Style { fillWidth() })
                                Button("See what's new", size = ButtonSize.Sm, variant = ButtonVariant.Outline, style = Style { fillWidth() })
                            }
                        }
                    }
                }


                @Composable
                fun CardExample() { Box(Modifier.wrapContentHeight(unbounded = true)) { CardSmallRecipe() } }
        """.trimIndent(),
        """
                @file:OptIn(androidx.compose.foundation.style.ExperimentalFoundationStyleApi::class)


                import androidx.compose.foundation.Image
                import androidx.compose.foundation.background
                import androidx.compose.foundation.gestures.detectTapGestures
                import androidx.compose.foundation.layout.*
                import androidx.compose.foundation.rememberScrollState
                import androidx.compose.foundation.style.Style
                import androidx.compose.foundation.style.contentPadding
                import androidx.compose.foundation.style.fillWidth
                import androidx.compose.foundation.verticalScroll
                import androidx.compose.runtime.*
                import androidx.compose.ui.Alignment
                import androidx.compose.ui.Modifier
                import androidx.compose.ui.draw.drawWithContent
                import androidx.compose.ui.focus.FocusRequester
                import androidx.compose.ui.focus.focusRequester
                import androidx.compose.ui.geometry.Offset
                import androidx.compose.ui.graphics.*
                import androidx.compose.ui.input.pointer.pointerInput
                import androidx.compose.ui.layout.ContentScale
                import androidx.compose.ui.layout.layout
                import androidx.compose.ui.platform.LocalLayoutDirection
                import androidx.compose.ui.platform.LocalUriHandler
                import androidx.compose.ui.semantics.contentDescription
                import androidx.compose.ui.semantics.semantics
                import androidx.compose.ui.text.*
                import androidx.compose.ui.text.font.FontWeight
                import androidx.compose.ui.text.style.LineHeightStyle
        import androidx.compose.ui.text.style.TextDecoration
                import androidx.compose.ui.unit.LayoutDirection
                import androidx.compose.ui.unit.dp
                import androidx.compose.ui.unit.sp
                import com.composables.icons.lucide.ChevronRight
                import com.composables.icons.lucide.Lucide
                import com.toutiaoyan.compose.ui.components.badge.*
                import com.toutiaoyan.compose.ui.components.button.*
                import com.toutiaoyan.compose.ui.components.card.*
                import com.toutiaoyan.compose.ui.components.icon.Icon
                import com.toutiaoyan.compose.ui.components.input.*
                import com.toutiaoyan.compose.ui.components.label.Label
                import com.toutiaoyan.compose.ui.components.text.Text
                import com.toutiaoyan.compose.ui.components.toggle.*
                import com.toutiaoyan.compose.ui.components.togglegroup.*
                import com.toutiaoyan.compose.ui.theme.Theme
                import com.toutiaoyan.compose.ui.theme.currentTheme
                import com.toutiaoyan.compose.tailwind.core.LocalUnitSpacing
                import compose_foundation.app.shared.generated.resources.*
                import org.jetbrains.compose.resources.painterResource

                import com.toutiaoyan.compose.ui.components.select.*
                import androidx.compose.ui.text.font.FontFamily
                import org.jetbrains.compose.resources.Font

                // Supply the referenced image/font resources in your application.
                private data class CardLoginCopy(
                    val title: String,
                    val description: String,
                    val signUp: String,
                    val email: String,
                    val password: String,
                    val forgotPassword: String,
                    val login: String,
                    val google: String,
                )

                private val cardLoginCopies = listOf(
                    CardLoginCopy("Login to your account", "Enter your email below to login to your account", "Sign Up", "Email", "Password", "Forgot your password?", "Login", "Login with Google"),
                    CardLoginCopy("تسجيل الدخول إلى حسابك", "أدخل بريدك الإلكتروني أدناه لتسجيل الدخول إلى حسابك", "إنشاء حساب", "البريد الإلكتروني", "كلمة المرور", "نسيت كلمة المرور؟", "تسجيل الدخول", "تسجيل الدخول باستخدام Google"),
                    CardLoginCopy("התחבר לחשבון שלך", "הזן את האימייל שלך למטה כדי להתחבר לחשבון שלך", "הירשם", "אימייל", "סיסמה", "שכחת את הסיסמה?", "התחבר", "התחבר עם Google"),
                )

                @Composable
                private fun CardLogin(spacing: Int = 16, language: Int = 0) {
                    var email by remember { mutableStateOf("") }
                    var password by remember { mutableStateOf("") }
                    val emailFocus = remember { FocusRequester() }
                    val passwordFocus = remember { FocusRequester() }
                    val c = cardLoginCopies[language]
                    Card(Modifier.widthIn(max = 384.dp).fillMaxWidth(), spacing = spacing.dp) {
                        CardHeader {
                            CardTitle(c.title)
                            CardDescription(c.description)
                            CardAction { Button(c.signUp, variant = ButtonVariant.Link) }
                        }
                        CardContent {
                            Column(verticalArrangement = Arrangement.spacedBy(24.dp)) {
                                Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                                    Label(c.email, Modifier.pointerInput(emailFocus) { detectTapGestures { emailFocus.requestFocus() } })
                                    Input(email, { email = it }, placeholder = "m@example.com", type = if (language == 0) InputType.Email else InputType.Text, required = true,
                                        modifier = Modifier.focusRequester(emailFocus).semantics { contentDescription = c.email })
                                }
                                Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                                    Row(Modifier.fillMaxWidth(), verticalAlignment = Alignment.CenterVertically) {
                                        Label(c.password, Modifier.pointerInput(passwordFocus) { detectTapGestures { passwordFocus.requestFocus() } })
                                        Spacer(Modifier.weight(1f))
                                        CardForgotLink(c.forgotPassword)
                                    }
                                    Input(password, { password = it }, type = InputType.Password, required = true,
                                        modifier = Modifier.focusRequester(passwordFocus).semantics { contentDescription = c.password })
                                }
                            }
                        }
                        CardFooter {
                            Column(Modifier.fillMaxWidth(), verticalArrangement = Arrangement.spacedBy(8.dp)) {
                                Button(c.login, style = Style { fillWidth() })
                                Button(c.google, variant = ButtonVariant.Outline, style = Style { fillWidth() })
                            }
                        }
                    }
                }

                @Composable
                private fun CardForgotLink(text: String) {
                    val uri = LocalUriHandler.current
                    val foreground = currentTheme.colors.foreground
                    Text(textStyle = TextStyle(fontSize = 14.sp, lineHeight = 20.sp, fontWeight = FontWeight.Normal,
                    lineHeightStyle = LineHeightStyle(LineHeightStyle.Alignment.Center, LineHeightStyle.Trim.None, LineHeightStyle.Mode.Tight))) {
                        buildAnnotatedString {
                            withLink(LinkAnnotation.Url("#", styles = TextLinkStyles(
                                style = SpanStyle(color = foreground),
                                hoveredStyle = SpanStyle(textDecoration = TextDecoration.Underline),
                            ), linkInteractionListener = { uri.openUri("#") })) { append(text) }
                        }
                    }
                }

                @Composable
                private fun CardSpacingRecipe() {
                    var spacing by remember { mutableStateOf("16") }
                    Column(Modifier.widthIn(max = 384.dp).fillMaxWidth(), verticalArrangement = Arrangement.spacedBy(16.dp), horizontalAlignment = Alignment.Start) {
                        ToggleGroup(value = setOf(spacing), onValueChange = { values -> values.firstOrNull()?.let { spacing = it } }, type = ToggleGroupType.Single,
                            variant = ToggleVariant.Outline, size = ToggleSize.Sm, contentDescription = "Card spacing") {
                            listOf("16", "20", "24", "32").forEach { ToggleGroupItem(it, "${'$'}{it}px") }
                        }
                        CardLogin(spacing.toInt())
                    }
                }


                @Composable
                fun CardExample() { Box(Modifier.wrapContentHeight(unbounded = true)) { CardSpacingRecipe() } }
        """.trimIndent(),
        """
                @file:OptIn(androidx.compose.foundation.style.ExperimentalFoundationStyleApi::class)


                import androidx.compose.foundation.Image
                import androidx.compose.foundation.background
                import androidx.compose.foundation.gestures.detectTapGestures
                import androidx.compose.foundation.layout.*
                import androidx.compose.foundation.rememberScrollState
                import androidx.compose.foundation.style.Style
                import androidx.compose.foundation.style.contentPadding
                import androidx.compose.foundation.style.fillWidth
                import androidx.compose.foundation.verticalScroll
                import androidx.compose.runtime.*
                import androidx.compose.ui.Alignment
                import androidx.compose.ui.Modifier
                import androidx.compose.ui.draw.drawWithContent
                import androidx.compose.ui.focus.FocusRequester
                import androidx.compose.ui.focus.focusRequester
                import androidx.compose.ui.geometry.Offset
                import androidx.compose.ui.graphics.*
                import androidx.compose.ui.input.pointer.pointerInput
                import androidx.compose.ui.layout.ContentScale
                import androidx.compose.ui.layout.layout
                import androidx.compose.ui.platform.LocalLayoutDirection
                import androidx.compose.ui.platform.LocalUriHandler
                import androidx.compose.ui.semantics.contentDescription
                import androidx.compose.ui.semantics.semantics
                import androidx.compose.ui.text.*
                import androidx.compose.ui.text.font.FontWeight
                import androidx.compose.ui.text.style.LineHeightStyle
        import androidx.compose.ui.text.style.TextDecoration
                import androidx.compose.ui.unit.LayoutDirection
                import androidx.compose.ui.unit.dp
                import androidx.compose.ui.unit.sp
                import com.composables.icons.lucide.ChevronRight
                import com.composables.icons.lucide.Lucide
                import com.toutiaoyan.compose.ui.components.badge.*
                import com.toutiaoyan.compose.ui.components.button.*
                import com.toutiaoyan.compose.ui.components.card.*
                import com.toutiaoyan.compose.ui.components.icon.Icon
                import com.toutiaoyan.compose.ui.components.input.*
                import com.toutiaoyan.compose.ui.components.label.Label
                import com.toutiaoyan.compose.ui.components.text.Text
                import com.toutiaoyan.compose.ui.components.toggle.*
                import com.toutiaoyan.compose.ui.components.togglegroup.*
                import com.toutiaoyan.compose.ui.theme.Theme
                import com.toutiaoyan.compose.ui.theme.currentTheme
                import com.toutiaoyan.compose.tailwind.core.LocalUnitSpacing
                import compose_foundation.app.shared.generated.resources.*
                import org.jetbrains.compose.resources.painterResource

                import com.toutiaoyan.compose.ui.components.select.*
                import androidx.compose.ui.text.font.FontFamily
                import org.jetbrains.compose.resources.Font

                // Supply the referenced image/font resources in your application.
                private val cardTerms = listOf(
                    "These terms govern your use of the workspace, including access to shared documents, project files, and collaboration tools.",
                    "You are responsible for the content you upload and for ensuring that your team has the appropriate permissions to view or edit it.",
                    "We may update features or limits as the service evolves. When those changes materially affect your workflow, we will notify your workspace administrators.",
                    "By continuing, you agree to keep your account credentials secure and to follow your organization's acceptable use policies.",
                )

                @Composable
                private fun CardEdgeRecipe() {
                    val colors = currentTheme.colors
                    Card(Modifier.widthIn(max = 384.dp).fillMaxWidth()) {
                        CardHeader("Terms of Service", "Review the terms before accepting the agreement.")
                        CardContent(
                            modifier = Modifier.layout { measurable, constraints ->
                                val child = measurable.measure(constraints)
                                layout(child.width, (child.height - 16.dp.roundToPx()).coerceAtLeast(0)) { child.placeRelative(0, 0) }
                            },
                            style = Style { contentPadding(0.dp) },
                        ) {
                            Column(Modifier.fillMaxWidth().heightIn(max = 192.dp).background(colors.muted.copy(alpha = 0.5f))
                                .drawWithContent { drawContent(); drawLine(colors.border, Offset(0f, .5.dp.toPx()), Offset(size.width, .5.dp.toPx()), 1.dp.toPx()) }
                                .verticalScroll(rememberScrollState()).padding(horizontal = 16.dp, vertical = 16.dp),
                                verticalArrangement = Arrangement.spacedBy(16.dp)) {
                                cardTerms.forEach { paragraph -> Text(textStyle = TextStyle(fontSize = 14.sp, lineHeight = 22.75.sp)) { paragraph } }
                            }
                        }
                        CardFooter {
                            Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                                Spacer(Modifier.weight(1f))
                                Button("Decline", variant = ButtonVariant.Outline)
                                Button("Accept")
                            }
                        }
                    }
                }


                @Composable
                fun CardExample() { Box(Modifier.wrapContentHeight(unbounded = true)) { CardEdgeRecipe() } }
        """.trimIndent(),
        """
                @file:OptIn(androidx.compose.foundation.style.ExperimentalFoundationStyleApi::class)


                import androidx.compose.foundation.Image
                import androidx.compose.foundation.background
                import androidx.compose.foundation.gestures.detectTapGestures
                import androidx.compose.foundation.layout.*
                import androidx.compose.foundation.rememberScrollState
                import androidx.compose.foundation.style.Style
                import androidx.compose.foundation.style.contentPadding
                import androidx.compose.foundation.style.fillWidth
                import androidx.compose.foundation.verticalScroll
                import androidx.compose.runtime.*
                import androidx.compose.ui.Alignment
                import androidx.compose.ui.Modifier
                import androidx.compose.ui.draw.drawWithContent
                import androidx.compose.ui.focus.FocusRequester
                import androidx.compose.ui.focus.focusRequester
                import androidx.compose.ui.geometry.Offset
                import androidx.compose.ui.graphics.*
                import androidx.compose.ui.input.pointer.pointerInput
                import androidx.compose.ui.layout.ContentScale
                import androidx.compose.ui.layout.layout
                import androidx.compose.ui.platform.LocalLayoutDirection
                import androidx.compose.ui.platform.LocalUriHandler
                import androidx.compose.ui.semantics.contentDescription
                import androidx.compose.ui.semantics.semantics
                import androidx.compose.ui.text.*
                import androidx.compose.ui.text.font.FontWeight
                import androidx.compose.ui.text.style.LineHeightStyle
        import androidx.compose.ui.text.style.TextDecoration
                import androidx.compose.ui.unit.LayoutDirection
                import androidx.compose.ui.unit.dp
                import androidx.compose.ui.unit.sp
                import com.composables.icons.lucide.ChevronRight
                import com.composables.icons.lucide.Lucide
                import com.toutiaoyan.compose.ui.components.badge.*
                import com.toutiaoyan.compose.ui.components.button.*
                import com.toutiaoyan.compose.ui.components.card.*
                import com.toutiaoyan.compose.ui.components.icon.Icon
                import com.toutiaoyan.compose.ui.components.input.*
                import com.toutiaoyan.compose.ui.components.label.Label
                import com.toutiaoyan.compose.ui.components.text.Text
                import com.toutiaoyan.compose.ui.components.toggle.*
                import com.toutiaoyan.compose.ui.components.togglegroup.*
                import com.toutiaoyan.compose.ui.theme.Theme
                import com.toutiaoyan.compose.ui.theme.currentTheme
                import com.toutiaoyan.compose.tailwind.core.LocalUnitSpacing
                import compose_foundation.app.shared.generated.resources.*
                import org.jetbrains.compose.resources.painterResource

                import com.toutiaoyan.compose.ui.components.select.*
                import androidx.compose.ui.text.font.FontFamily
                import org.jetbrains.compose.resources.Font

                // Supply the referenced image/font resources in your application.
                @Composable
                private fun CardImageRecipe() {
                    val brightness = if (currentTheme.isDark) .4f else .6f
                    val r = .2126f * brightness
                    val g = .7152f * brightness
                    val b = .0722f * brightness
                    val filter = ColorFilter.colorMatrix(ColorMatrix(floatArrayOf(r, g, b, 0f, 0f, r, g, b, 0f, 0f, r, g, b, 0f, 0f, 0f, 0f, 0f, 1f, 0f)))
                    Card(Modifier.widthIn(max = 384.dp).fillMaxWidth()) {
                        CardImage(Modifier.fillMaxWidth().aspectRatio(16f / 9f)) {
                            Image(painterResource(Res.drawable.card_event_cover), "Event cover", Modifier.fillMaxSize(), contentScale = ContentScale.Crop, colorFilter = filter)
                            Box(Modifier.fillMaxSize().background(Color.Black.copy(alpha = .35f)))
                        }
                        CardHeader {
                            CardAction { Badge("Featured", variant = BadgeVariant.Secondary) }
                            CardTitle("Design systems meetup")
                            CardDescription("A practical talk on component APIs, accessibility, and shipping faster.")
                        }
                        CardFooter { Button("View Event", style = Style { fillWidth() }) }
                    }
                }


                @Composable
                fun CardExample() { Box(Modifier.wrapContentHeight(unbounded = true)) { CardImageRecipe() } }
        """.trimIndent(),
        """
                @file:OptIn(androidx.compose.foundation.style.ExperimentalFoundationStyleApi::class)


                import androidx.compose.foundation.Image
                import androidx.compose.foundation.background
                import androidx.compose.foundation.gestures.detectTapGestures
                import androidx.compose.foundation.layout.*
                import androidx.compose.foundation.rememberScrollState
                import androidx.compose.foundation.style.Style
                import androidx.compose.foundation.style.contentPadding
                import androidx.compose.foundation.style.fillWidth
                import androidx.compose.foundation.verticalScroll
                import androidx.compose.runtime.*
                import androidx.compose.ui.Alignment
                import androidx.compose.ui.Modifier
                import androidx.compose.ui.draw.drawWithContent
                import androidx.compose.ui.focus.FocusRequester
                import androidx.compose.ui.focus.focusRequester
                import androidx.compose.ui.geometry.Offset
                import androidx.compose.ui.graphics.*
                import androidx.compose.ui.input.pointer.pointerInput
                import androidx.compose.ui.layout.ContentScale
                import androidx.compose.ui.layout.layout
                import androidx.compose.ui.platform.LocalLayoutDirection
                import androidx.compose.ui.platform.LocalUriHandler
                import androidx.compose.ui.semantics.contentDescription
                import androidx.compose.ui.semantics.semantics
                import androidx.compose.ui.text.*
                import androidx.compose.ui.text.font.FontWeight
                import androidx.compose.ui.text.style.LineHeightStyle
        import androidx.compose.ui.text.style.TextDecoration
                import androidx.compose.ui.unit.LayoutDirection
                import androidx.compose.ui.unit.dp
                import androidx.compose.ui.unit.sp
                import com.composables.icons.lucide.ChevronRight
                import com.composables.icons.lucide.Lucide
                import com.toutiaoyan.compose.ui.components.badge.*
                import com.toutiaoyan.compose.ui.components.button.*
                import com.toutiaoyan.compose.ui.components.card.*
                import com.toutiaoyan.compose.ui.components.icon.Icon
                import com.toutiaoyan.compose.ui.components.input.*
                import com.toutiaoyan.compose.ui.components.label.Label
                import com.toutiaoyan.compose.ui.components.text.Text
                import com.toutiaoyan.compose.ui.components.toggle.*
                import com.toutiaoyan.compose.ui.components.togglegroup.*
                import com.toutiaoyan.compose.ui.theme.Theme
                import com.toutiaoyan.compose.ui.theme.currentTheme
                import com.toutiaoyan.compose.tailwind.core.LocalUnitSpacing
                import compose_foundation.app.shared.generated.resources.*
                import org.jetbrains.compose.resources.painterResource

                import com.toutiaoyan.compose.ui.components.select.*
                import androidx.compose.ui.text.font.FontFamily
                import org.jetbrains.compose.resources.Font

                // Supply the referenced image/font resources in your application.
                private data class CardLoginCopy(
                    val title: String,
                    val description: String,
                    val signUp: String,
                    val email: String,
                    val password: String,
                    val forgotPassword: String,
                    val login: String,
                    val google: String,
                )

                private val cardLoginCopies = listOf(
                    CardLoginCopy("Login to your account", "Enter your email below to login to your account", "Sign Up", "Email", "Password", "Forgot your password?", "Login", "Login with Google"),
                    CardLoginCopy("تسجيل الدخول إلى حسابك", "أدخل بريدك الإلكتروني أدناه لتسجيل الدخول إلى حسابك", "إنشاء حساب", "البريد الإلكتروني", "كلمة المرور", "نسيت كلمة المرور؟", "تسجيل الدخول", "تسجيل الدخول باستخدام Google"),
                    CardLoginCopy("התחבר לחשבון שלך", "הזן את האימייל שלך למטה כדי להתחבר לחשבון שלך", "הירשם", "אימייל", "סיסמה", "שכחת את הסיסמה?", "התחבר", "התחבר עם Google"),
                )

                @Composable
                private fun CardLogin(spacing: Int = 16, language: Int = 0) {
                    var email by remember { mutableStateOf("") }
                    var password by remember { mutableStateOf("") }
                    val emailFocus = remember { FocusRequester() }
                    val passwordFocus = remember { FocusRequester() }
                    val c = cardLoginCopies[language]
                    Card(Modifier.widthIn(max = 384.dp).fillMaxWidth(), spacing = spacing.dp) {
                        CardHeader {
                            CardTitle(c.title)
                            CardDescription(c.description)
                            CardAction { Button(c.signUp, variant = ButtonVariant.Link) }
                        }
                        CardContent {
                            Column(verticalArrangement = Arrangement.spacedBy(24.dp)) {
                                Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                                    Label(c.email, Modifier.pointerInput(emailFocus) { detectTapGestures { emailFocus.requestFocus() } })
                                    Input(email, { email = it }, placeholder = "m@example.com", type = if (language == 0) InputType.Email else InputType.Text, required = true,
                                        modifier = Modifier.focusRequester(emailFocus).semantics { contentDescription = c.email })
                                }
                                Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                                    Row(Modifier.fillMaxWidth(), verticalAlignment = Alignment.CenterVertically) {
                                        Label(c.password, Modifier.pointerInput(passwordFocus) { detectTapGestures { passwordFocus.requestFocus() } })
                                        Spacer(Modifier.weight(1f))
                                        CardForgotLink(c.forgotPassword)
                                    }
                                    Input(password, { password = it }, type = InputType.Password, required = true,
                                        modifier = Modifier.focusRequester(passwordFocus).semantics { contentDescription = c.password })
                                }
                            }
                        }
                        CardFooter {
                            Column(Modifier.fillMaxWidth(), verticalArrangement = Arrangement.spacedBy(8.dp)) {
                                Button(c.login, style = Style { fillWidth() })
                                Button(c.google, variant = ButtonVariant.Outline, style = Style { fillWidth() })
                            }
                        }
                    }
                }

                @Composable
                private fun CardForgotLink(text: String) {
                    val uri = LocalUriHandler.current
                    val foreground = currentTheme.colors.foreground
                    Text(textStyle = TextStyle(fontSize = 14.sp, lineHeight = 20.sp, fontWeight = FontWeight.Normal,
                    lineHeightStyle = LineHeightStyle(LineHeightStyle.Alignment.Center, LineHeightStyle.Trim.None, LineHeightStyle.Mode.Tight))) {
                        buildAnnotatedString {
                            withLink(LinkAnnotation.Url("#", styles = TextLinkStyles(
                                style = SpanStyle(color = foreground),
                                hoveredStyle = SpanStyle(textDecoration = TextDecoration.Underline),
                            ), linkInteractionListener = { uri.openUri("#") })) { append(text) }
                        }
                    }
                }

                @Composable
                private fun catalogArabicFontFamily() = FontFamily(
                    Font(Res.font.catalog_arabic_regular, FontWeight.Normal),
                    Font(Res.font.catalog_arabic_medium, FontWeight.Medium),
                )
                @Composable
                private fun catalogHebrewFontFamily() = FontFamily(
                    Font(Res.font.catalog_hebrew_regular, FontWeight.Normal),
                    Font(Res.font.catalog_hebrew_medium, FontWeight.Medium),
                )
                @Composable
                private fun CardRtlRecipe() {
                    var language by remember { mutableIntStateOf(1) }
                    val inherited = currentTheme
                    Column(Modifier.fillMaxWidth()) {
                        val languages = listOf("English", "Arabic (العربية)", "Hebrew (עברית)")
                        Select(value = languages[language], onValueChange = { language = languages.indexOf(it) }) {
                            SelectTrigger(accessibilityLabel = "Preview language")
                            SelectContent { languages.forEach { SelectItem(it) } }
                        }
                        Box(Modifier.fillMaxWidth().height(480.dp).padding(40.dp).wrapContentHeight(unbounded = true), contentAlignment = Alignment.Center) {
                            val font = when (language) { 1 -> catalogArabicFontFamily(); 2 -> catalogHebrewFontFamily(); else -> inherited.fontFamily }
                            CompositionLocalProvider(LocalLayoutDirection provides if (language == 0) LayoutDirection.Ltr else LayoutDirection.Rtl) {
                                Theme(preset = inherited.activePreset, baseColor = inherited.activeBaseColor, accent = inherited.activeAccent,
                                    isDark = inherited.isDark, baseRadius = inherited.baseRadius, ring = inherited.ring,
                                    unitSpacing = LocalUnitSpacing.current, minTouchTarget = inherited.minTouchTarget, fontFamily = font) {
                                    CardLogin(language = language)
                                }
                            }
                        }
                    }
                }

                @Composable
                fun CardExample() { Box(Modifier.wrapContentHeight(unbounded = true)) { CardRtlRecipe() } }
        """.trimIndent(),
    )
