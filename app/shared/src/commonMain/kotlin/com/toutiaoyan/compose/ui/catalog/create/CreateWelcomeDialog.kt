package com.toutiaoyan.compose.ui.catalog.create

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.unit.dp
import com.toutiaoyan.compose.ui.components.button.Button
import com.toutiaoyan.compose.ui.components.dialog.Dialog
import com.toutiaoyan.compose.ui.components.dialog.DialogContent
import com.toutiaoyan.compose.ui.components.dialog.DialogDescription
import com.toutiaoyan.compose.ui.components.dialog.DialogFooter
import com.toutiaoyan.compose.ui.components.dialog.DialogHeader
import com.toutiaoyan.compose.ui.components.dialog.DialogTitle
import com.toutiaoyan.compose.ui.components.text.Text
import com.toutiaoyan.compose.ui.components.text.TextVariant

/**
 * First-visit welcome modal matching
 * [welcome-dialog.tsx](https://github.com/shadcn-ui/ui/blob/main/apps/v4/app/(app)/(create)/components/welcome-dialog.tsx):
 * dark logo plane, "Build your own shadcn/ui", dual descriptions, full-width Get Started.
 */
@Composable
fun CreateWelcomeDialog(
    visible: Boolean,
    onDismissRequest: () -> Unit,
) {
    Dialog(open = visible, onOpenChange = { if (!it) onDismissRequest() }) {
        DialogContent(showCloseButton = false) {
        // Official: aspect-[2/1.2] rounded-t-xl bg-neutral-950 text-neutral-100
        Box(
            modifier =
                Modifier
                    .fillMaxWidth()
                    .aspectRatio(2f / 1.2f)
                    .background(
                        Color(0xFF0A0A0A),
                        RoundedCornerShape(topStart = 12.dp, topEnd = 12.dp),
                    ),
            contentAlignment = Alignment.Center,
        ) {
            Text(
                variant = TextVariant.TitleLarge,
                textStyle = TextStyle(color = Color(0xFFF5F5F5)),
            ) { "shadcn/ui" }
        }
        DialogHeader {
            DialogTitle("Build your own shadcn/ui")
            DialogDescription(
                "Customize everything from the ground up. Pick your component library, font, color scheme, and more.",
            )
            // Official second line: font-medium text-foreground
            DialogDescription(
                "Available for all major Compose Multiplatform targets.",
            )
        }
        DialogFooter {
            Button(onClick = onDismissRequest, modifier = Modifier.fillMaxWidth()) {
                Text { "Get Started" }
            }
        }
        }
    }
}
