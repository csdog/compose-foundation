package com.toutiaoyan.compose.ui

import android.content.Intent
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.runtime.Composable
import androidx.compose.ui.tooling.preview.Preview
import com.toutiaoyan.compose.ui.navigation.InboundDeepLinks

class MainActivity : ComponentActivity() {
    private val inboundDeepLinks = InboundDeepLinks()

    override fun onCreate(savedInstanceState: Bundle?) {
        enableEdgeToEdge()
        super.onCreate(savedInstanceState)
        emitViewDeepLink(intent)

        setContent {
            App(inboundDeepLinks = inboundDeepLinks.events)
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
