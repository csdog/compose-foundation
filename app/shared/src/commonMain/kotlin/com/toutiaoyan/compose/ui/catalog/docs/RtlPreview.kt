@file:OptIn(androidx.compose.foundation.style.ExperimentalFoundationStyleApi::class)

package com.toutiaoyan.compose.ui.catalog.docs

import androidx.compose.foundation.focusable
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.style.Style
import androidx.compose.foundation.style.contentPadding
import com.toutiaoyan.compose.ui.catalog.catalogViewportWidth
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.remember
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.focus.focusRequester
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.composables.icons.lucide.CircleAlert
import com.composables.icons.lucide.Lucide
import com.toutiaoyan.compose.ui.components.button.Button
import com.toutiaoyan.compose.ui.components.button.ButtonSize
import com.toutiaoyan.compose.ui.components.button.ButtonVariant
import com.toutiaoyan.compose.ui.components.overlay.PopupAlign
import com.toutiaoyan.compose.ui.components.popover.Popover
import com.toutiaoyan.compose.ui.components.popover.PopoverContent
import com.toutiaoyan.compose.ui.components.popover.PopoverTrigger
import com.toutiaoyan.compose.ui.components.select.*
import com.toutiaoyan.compose.ui.components.text.Text
import com.toutiaoyan.compose.ui.theme.currentTheme

/** Catalog chrome, outside the component being demonstrated. */
@Composable
internal fun RtlPreview(
    language: Int,
    labels: List<String>,
    onLanguageChange: (Int) -> Unit,
    modifier: Modifier = Modifier,
    content: @Composable () -> Unit,
) {
    val mobile = catalogViewportWidth() < 640.dp
    Column(modifier.fillMaxWidth()) {
        RtlPreviewToolbar(language, labels, onLanguageChange)
        Box(
            Modifier.testTag(
                "rtl-preview-body",
            ).fillMaxWidth().heightIn(min = if (mobile) 0.dp else 288.dp).padding(24.dp),
            contentAlignment = Alignment.Center,
        ) {
            content()
        }
    }
}

/** Shared language and translation controls; each recipe owns its preview layout. */
@Composable
internal fun RtlPreviewToolbar(
    language: Int,
    labels: List<String>,
    onLanguageChange: (Int) -> Unit,
) {
    val colors = currentTheme.colors
    Row(
        Modifier.testTag("rtl-preview-toolbar").fillMaxWidth().height(63.dp).padding(horizontal = 16.dp),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        Select(value = labels[language], onValueChange = { onLanguageChange(labels.indexOf(it)) }) {
            SelectTrigger(Modifier.width(144.dp), size = SelectSize.Sm, accessibilityLabel = "Preview language")
            SelectContent { labels.forEach { SelectItem(it) } }
        }
        Spacer(Modifier.weight(1f))
        Popover {
            val infoFocus = remember { FocusRequester() }
            PopoverTrigger {
                Button(
                    onClick = toggle,
                    variant = ButtonVariant.Ghost,
                    size = ButtonSize.IconSm,
                    modifier = Modifier.size(28.dp),
                    label = "Translation information",
                ) {
                    DocIcon(Lucide.CircleAlert)
                }
            }
            PopoverContent(
                modifier = Modifier.focusRequester(infoFocus).focusable(),
                align = PopupAlign.End,
                style =
                    Style {
                        width(224.dp)
                        contentPadding(0.dp)
                        shape(RoundedCornerShape(10.dp))
                    },
            ) {
                LaunchedEffect(Unit) { infoFocus.requestFocus() }
                Column(Modifier.padding(vertical = 10.dp), verticalArrangement = Arrangement.spacedBy(10.dp)) {
                    val notices =
                        listOf(
                            (
                                "I used AI to translate the text for demonstration purposes. " +
                                    "It's not perfect and may contain errors."
                            ),
                            (
                                "لقد استخدمت الذكاء الاصطناعي لترجمة النص للأغراض التجريبية " +
                                    "فقط. قد لا تكون الترجمة دقيقة وقد تحتوي على أخطاء."
                            ),
                            (
                                "השתמשתי בבינה מלאכותית כדי לתרגם את הטקסט למטרות הדגמה. זה לא " +
                                    "מושלם ויכול להכיל שגיאות."
                            ),
                        )
                    notices.forEachIndexed { index, notice ->
                        if (index > 0) Box(Modifier.fillMaxWidth().height(1.dp).background(colors.border))
                        Text(
                            modifier = Modifier.padding(horizontal = 10.dp),
                            textStyle = TextStyle(fontSize = 12.sp, lineHeight = 16.sp),
                        ) {
                            notice
                        }
                    }
                }
            }
        }
    }
    Box(Modifier.fillMaxWidth().height(1.dp).background(colors.border))
}
