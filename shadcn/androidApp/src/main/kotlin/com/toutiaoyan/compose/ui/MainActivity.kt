package com.toutiaoyan.compose.ui

import android.content.Intent
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.safeDrawingPadding
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.core.view.WindowCompat
import com.toutiaoyan.compose.ui.navigation.InboundDeepLinks

class MainActivity : ComponentActivity() {
    private val inboundDeepLinks = InboundDeepLinks()

    override fun onCreate(savedInstanceState: Bundle?) {
        enableEdgeToEdge()
        super.onCreate(savedInstanceState)
        emitViewDeepLink(intent)

        val systemBars = WindowCompat.getInsetsController(window, window.decorView)
        setContent {
            App(
                inboundDeepLinks = inboundDeepLinks.events,
                onDarkModeOverrideChange = { dark ->
                    systemBars.isAppearanceLightStatusBars = !dark
                    systemBars.isAppearanceLightNavigationBars = !dark
                },
                modifier = Modifier.safeDrawingPadding(),
            )
        }
    }

    override fun onNewIntent(intent: Intent) {
        super.onNewIntent(intent)
        setIntent(intent)
        emitViewDeepLink(intent)
    }

    private fun emitViewDeepLink(intent: Intent?) {
        if (intent?.action == Intent.ACTION_VIEW && intent.data != null) {
            intent.dataString?.let { inboundDeepLinks.offer(it) }
        }
    }
}

@Preview
@Composable
fun AppAndroidPreview() {
    App()
}
