@file:OptIn(ExperimentalFoundationStyleApi::class)

package com.toutiaoyan.compose.ui.components.badge
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.style.ExperimentalFoundationStyleApi
import androidx.compose.foundation.style.Style
import androidx.compose.foundation.style.hovered
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.unit.sp
import com.toutiaoyan.compose.ui.tokens.Colors
import com.toutiaoyan.compose.ui.utils.rememberTheme
import com.toutiaoyan.compose.ui.styles.border
import com.toutiaoyan.compose.ui.styles.focusRing

sealed interface BadgeVariant {
    data object Default : BadgeVariant

    data object Secondary : BadgeVariant

    data object Destructive : BadgeVariant

    data object Outline : BadgeVariant

    data object Ghost : BadgeVariant

    /** Official `variant="link"`: primary text, underline on hover. */
    data object Link : BadgeVariant
}

/** Same color each variant passes to Style `contentColor(...)`, for [LocalContentColor]. */
fun BadgeVariant.contentColor(colors: Colors): Color =
    when (this) {
        BadgeVariant.Default -> colors.primaryForeground
        BadgeVariant.Secondary -> colors.secondaryForeground
        BadgeVariant.Destructive -> colors.destructiveForeground
        BadgeVariant.Outline -> colors.foreground
        BadgeVariant.Ghost -> colors.mutedForeground
        BadgeVariant.Link -> colors.primary
    }

@Composable
fun BadgeVariant.rememberStyle(): Style {
    val receiver = this
    return rememberTheme(receiver) {
        val foreground = receiver.contentColor(colors)
        // Real badge.tsx always reserves `border border-transparent` (1px) so
        // switching to Outline doesn't change layout size; focus-visible ring is
        // for the asChild / link use case (see Badge's optional onClick).
        when (receiver) {
            BadgeVariant.Default ->
                Style {
                    background(colors.primary)
                    contentColor(foreground)
                    border(Color.Transparent)
                    contentPadding(horizontal = spacing.sm, vertical = spacing.xs2)
                    fontSize(12.sp)
                    fontWeight(FontWeight.SemiBold)
                    focusRing(RoundedCornerShape(radius.full))
                }

            BadgeVariant.Secondary ->
                Style {
                    background(colors.secondary)
                    contentColor(foreground)
                    border(Color.Transparent)
                    contentPadding(horizontal = spacing.sm, vertical = spacing.xs2)
                    fontSize(12.sp)
                    fontWeight(FontWeight.SemiBold)
                    focusRing(RoundedCornerShape(radius.full))
                }

            BadgeVariant.Destructive ->
                Style {
                    background(colors.destructive)
                    contentColor(foreground)
                    border(Color.Transparent)
                    contentPadding(horizontal = spacing.sm, vertical = spacing.xs2)
                    fontSize(12.sp)
                    fontWeight(FontWeight.SemiBold)
                    focusRing(RoundedCornerShape(radius.full))
                }

            BadgeVariant.Outline ->
                Style {
                    contentColor(foreground)
                    border(colors.border)
                    contentPadding(horizontal = spacing.sm, vertical = spacing.xs2)
                    fontSize(12.sp)
                    fontWeight(FontWeight.SemiBold)
                    focusRing(RoundedCornerShape(radius.full))
                }

            BadgeVariant.Ghost ->
                Style {
                    background(colors.muted)
                    contentColor(foreground)
                    border(Color.Transparent)
                    contentPadding(horizontal = spacing.sm, vertical = spacing.xs2)
                    fontSize(12.sp)
                    focusRing(RoundedCornerShape(radius.full))
                }

            BadgeVariant.Link ->
                Style {
                    contentColor(foreground)
                    border(Color.Transparent)
                    contentPadding(horizontal = spacing.sm, vertical = spacing.xs2)
                    fontSize(12.sp)
                    fontWeight(FontWeight.SemiBold)
                    hovered { textDecoration(TextDecoration.Underline) }
                    focusRing(RoundedCornerShape(radius.full))
                }
        }
    }
}
