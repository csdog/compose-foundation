package com.toutiaoyan.compose.ui

data class AppConfig(
    val BACKEND_BASE_URL: String,
)

val appConfig: AppConfig =
    AppConfig(
        BACKEND_BASE_URL = BuildKonfig.BACKEND_BASE_URL,
    )
