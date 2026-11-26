package com.toutiaoyan.compose.ui.screenshots

import android.content.res.Configuration
import androidx.compose.ui.tooling.preview.Preview

@Target(AnnotationTarget.FUNCTION, AnnotationTarget.ANNOTATION_CLASS)
@Retention(AnnotationRetention.BINARY)
@Preview(name = "phone_light", widthDp = 360, apiLevel = 37, uiMode = Configuration.UI_MODE_NIGHT_NO)
@Preview(name = "phone_dark", widthDp = 360, apiLevel = 37, uiMode = Configuration.UI_MODE_NIGHT_YES)
@Preview(name = "tablet_light", widthDp = 600, apiLevel = 37, uiMode = Configuration.UI_MODE_NIGHT_NO)
@Preview(name = "large_font", widthDp = 360, apiLevel = 37, fontScale = 1.5f, uiMode = Configuration.UI_MODE_NIGHT_NO)
annotation class AndroidScreenshotConfigurations
