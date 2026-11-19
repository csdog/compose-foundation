package com.toutiaoyan.compose.ui.catalog.create

import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.vector.ImageVector
import com.composables.icons.lucide.ArrowRight
import com.composables.icons.lucide.Calendar
import com.composables.icons.lucide.ChevronRight
import com.composables.icons.lucide.Lock
import com.composables.icons.lucide.Lucide
import com.composables.icons.lucide.Plus
import com.composables.icons.lucide.RefreshCw
import com.composables.icons.lucide.Search
import com.composables.icons.lucide.Send
import com.composables.icons.lucide.Settings
import com.composables.icons.lucide.X
import com.toutiaoyan.compose.ui.components.icon.Icon
import com.toutiaoyan.compose.ui.components.icon.IconVariant
import compose_foundation.app.shared.generated.resources.Res
import compose_foundation.app.shared.generated.resources.create_hugeicons_arrow_right
import compose_foundation.app.shared.generated.resources.create_hugeicons_calendar
import compose_foundation.app.shared.generated.resources.create_hugeicons_chevron_right
import compose_foundation.app.shared.generated.resources.create_hugeicons_lock
import compose_foundation.app.shared.generated.resources.create_hugeicons_plus
import compose_foundation.app.shared.generated.resources.create_hugeicons_refresh_cw
import compose_foundation.app.shared.generated.resources.create_hugeicons_search
import compose_foundation.app.shared.generated.resources.create_hugeicons_send
import compose_foundation.app.shared.generated.resources.create_hugeicons_settings
import compose_foundation.app.shared.generated.resources.create_hugeicons_x
import compose_foundation.app.shared.generated.resources.create_ph_arrow_right
import compose_foundation.app.shared.generated.resources.create_ph_calendar
import compose_foundation.app.shared.generated.resources.create_ph_chevron_right
import compose_foundation.app.shared.generated.resources.create_ph_lock
import compose_foundation.app.shared.generated.resources.create_ph_plus
import compose_foundation.app.shared.generated.resources.create_ph_refresh_cw
import compose_foundation.app.shared.generated.resources.create_ph_search
import compose_foundation.app.shared.generated.resources.create_ph_send
import compose_foundation.app.shared.generated.resources.create_ph_settings
import compose_foundation.app.shared.generated.resources.create_ph_x
import compose_foundation.app.shared.generated.resources.create_ri_arrow_right
import compose_foundation.app.shared.generated.resources.create_ri_calendar
import compose_foundation.app.shared.generated.resources.create_ri_chevron_right
import compose_foundation.app.shared.generated.resources.create_ri_lock
import compose_foundation.app.shared.generated.resources.create_ri_plus
import compose_foundation.app.shared.generated.resources.create_ri_refresh_cw
import compose_foundation.app.shared.generated.resources.create_ri_search
import compose_foundation.app.shared.generated.resources.create_ri_send
import compose_foundation.app.shared.generated.resources.create_ri_settings
import compose_foundation.app.shared.generated.resources.create_ri_x
import compose_foundation.app.shared.generated.resources.create_tabler_arrow_right
import compose_foundation.app.shared.generated.resources.create_tabler_calendar
import compose_foundation.app.shared.generated.resources.create_tabler_chevron_right
import compose_foundation.app.shared.generated.resources.create_tabler_lock
import compose_foundation.app.shared.generated.resources.create_tabler_plus
import compose_foundation.app.shared.generated.resources.create_tabler_refresh_cw
import compose_foundation.app.shared.generated.resources.create_tabler_search
import compose_foundation.app.shared.generated.resources.create_tabler_send
import compose_foundation.app.shared.generated.resources.create_tabler_settings
import compose_foundation.app.shared.generated.resources.create_tabler_x
import org.jetbrains.compose.resources.DrawableResource
import org.jetbrains.compose.resources.vectorResource

internal enum class CreateIcon(
    val lucide: ImageVector,
    val hugeicons: DrawableResource,
    val tabler: DrawableResource,
    val phosphor: DrawableResource,
    val remix: DrawableResource,
) {
    ArrowRight(
        Lucide.ArrowRight,
        Res.drawable.create_hugeicons_arrow_right,
        Res.drawable.create_tabler_arrow_right,
        Res.drawable.create_ph_arrow_right,
        Res.drawable.create_ri_arrow_right,
    ),
    Calendar(
        Lucide.Calendar,
        Res.drawable.create_hugeicons_calendar,
        Res.drawable.create_tabler_calendar,
        Res.drawable.create_ph_calendar,
        Res.drawable.create_ri_calendar,
    ),
    ChevronRight(
        Lucide.ChevronRight,
        Res.drawable.create_hugeicons_chevron_right,
        Res.drawable.create_tabler_chevron_right,
        Res.drawable.create_ph_chevron_right,
        Res.drawable.create_ri_chevron_right,
    ),
    Lock(
        Lucide.Lock,
        Res.drawable.create_hugeicons_lock,
        Res.drawable.create_tabler_lock,
        Res.drawable.create_ph_lock,
        Res.drawable.create_ri_lock,
    ),
    Plus(
        Lucide.Plus,
        Res.drawable.create_hugeicons_plus,
        Res.drawable.create_tabler_plus,
        Res.drawable.create_ph_plus,
        Res.drawable.create_ri_plus,
    ),
    RefreshCw(
        Lucide.RefreshCw,
        Res.drawable.create_hugeicons_refresh_cw,
        Res.drawable.create_tabler_refresh_cw,
        Res.drawable.create_ph_refresh_cw,
        Res.drawable.create_ri_refresh_cw,
    ),
    Search(
        Lucide.Search,
        Res.drawable.create_hugeicons_search,
        Res.drawable.create_tabler_search,
        Res.drawable.create_ph_search,
        Res.drawable.create_ri_search,
    ),
    Send(
        Lucide.Send,
        Res.drawable.create_hugeicons_send,
        Res.drawable.create_tabler_send,
        Res.drawable.create_ph_send,
        Res.drawable.create_ri_send,
    ),
    Settings(
        Lucide.Settings,
        Res.drawable.create_hugeicons_settings,
        Res.drawable.create_tabler_settings,
        Res.drawable.create_ph_settings,
        Res.drawable.create_ri_settings,
    ),
    X(
        Lucide.X,
        Res.drawable.create_hugeicons_x,
        Res.drawable.create_tabler_x,
        Res.drawable.create_ph_x,
        Res.drawable.create_ri_x,
    ),
}

@Composable
internal fun PreviewIcon(
    icon: CreateIcon,
    variant: IconVariant = IconVariant.Default,
    contentDescription: String? = null,
) {
    val resource =
        when (LocalCreatePreviewConfiguration.current.iconLibrary) {
            "Hugeicons" -> icon.hugeicons
            "Tabler" -> icon.tabler
            "Phosphor" -> icon.phosphor
            "Remix Icon" -> icon.remix
            else -> null
        }
    Icon(
        imageVector = if (resource == null) icon.lucide else vectorResource(resource),
        variant = variant,
        contentDescription = contentDescription,
    )
}
