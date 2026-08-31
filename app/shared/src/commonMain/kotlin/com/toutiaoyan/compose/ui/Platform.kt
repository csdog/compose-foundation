package com.toutiaoyan.compose.ui

interface Platform {
    val name: String
}

expect fun getPlatform(): Platform
