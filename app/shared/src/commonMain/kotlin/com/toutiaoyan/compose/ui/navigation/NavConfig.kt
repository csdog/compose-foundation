package com.toutiaoyan.compose.ui.navigation

import androidx.navigation3.runtime.NavKey
import androidx.savedstate.serialization.SavedStateConfiguration
import kotlinx.serialization.ExperimentalSerializationApi
import kotlinx.serialization.modules.SerializersModule
import kotlinx.serialization.modules.polymorphic
import kotlinx.serialization.modules.subclassesOfSealed

/**
 * Navigation 3 needs explicit [NavKey] subclass registration on non-Android targets
 * so the back stack can round-trip through SavedState.
 */
@OptIn(ExperimentalSerializationApi::class)
val AppNavSavedStateConfiguration: SavedStateConfiguration =
    SavedStateConfiguration {
        serializersModule =
            SerializersModule {
                polymorphic(NavKey::class) {
                    subclassesOfSealed<RouteKey>()
                }
            }
    }
