@file:OptIn(androidx.compose.foundation.style.ExperimentalFoundationStyleApi::class)

package com.toutiaoyan.compose.ui.catalog.create

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ColumnScope
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.RowScope
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.style.Style
import androidx.compose.foundation.verticalScroll
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import com.toutiaoyan.compose.ui.components.button.Button
import com.toutiaoyan.compose.ui.components.button.ButtonSize
import com.toutiaoyan.compose.ui.components.button.ButtonVariant
import com.toutiaoyan.compose.ui.components.text.Text
import com.toutiaoyan.compose.ui.components.text.TextColors
import com.toutiaoyan.compose.ui.theme.Theme
import com.toutiaoyan.compose.ui.theme.currentTheme
import com.toutiaoyan.compose.tailwind.core.twSpacing

/**
 * Create-page preview canvas with official 01/02 switcher
 * ([preview.tsx](https://github.com/shadcn-ui/ui/blob/main/apps/v4/app/(app)/(create)/components/preview.tsx) +
 * [preview-switcher.tsx](https://github.com/shadcn-ui/ui/blob/main/apps/v4/app/(app)/(create)/components/preview-switcher.tsx)):
 * - **01** → registry `preview-02` (finance / payout dashboard)
 * - **02** → registry `preview` (dev / product cards)
 */
@Composable
fun CreatePreviewCanvas(modifier: Modifier = Modifier) {
    // Official default item is preview-02 (= our 01).
    var previewIndex by remember { mutableIntStateOf(0) }
    val gap = twSpacing.lg
    // preview.tsx: rounded-2xl ring ring-foreground/10
    val frameShape = RoundedCornerShape(16.dp)

    Box(
        modifier =
            modifier
                .fillMaxSize()
                .clip(frameShape)
                .border(1.dp, currentTheme.colors.foreground.copy(alpha = 0.10f), frameShape)
                // preview.tsx: bg-muted / dark:bg-muted/30
                .background(currentTheme.colors.muted.copy(alpha = if (currentTheme.isLight) 1f else 0.30f)),
    ) {
        Row(
            modifier =
                Modifier
                    .fillMaxSize()
                    .horizontalScroll(rememberScrollState())
                    .padding(twSpacing.xl),
            horizontalArrangement = Arrangement.spacedBy(gap),
        ) {
            when (previewIndex) {
                0 -> CreatePreview01Columns(gap)
                else -> CreatePreview02CanvasColumns(gap)
            }
        }

        CreatePreviewSwitcher(
            selectedIndex = previewIndex,
            onSelect = { previewIndex = it },
            modifier =
                Modifier
                    .align(Alignment.BottomEnd)
                    .padding(12.dp),
        )
    }
}

/**
 * Official `preview-02` column mix (finance dashboard). This was previously shown
 * as Create's "02"; it is now the default "01".
 */
@Composable
private fun RowScope.CreatePreview01Columns(gap: Dp) {
    PreviewColumn(gap) {
        CreateContributionHistoryCard()
        CreateEmptyDistributeTrackCard()
        CreateQrConnectCard()
        CreateDividendIncomeCard()
        CreateIndexInvestingCard()
        CreateSyncingStateCard()
    }
    PreviewColumn(gap) {
        CreatePayoutThresholdCard()
        CreateClaimableBalanceCard()
        CreatePreferencesCard()
        CreateSavingsProgressCard()
        CreateKitchenIslandCard()
    }
    PreviewWideColumn(gap) {
        CreateSavingsTargetsCard()
        CreateRecentTransactionsCard()
        Row(horizontalArrangement = Arrangement.spacedBy(gap)) {
            Column(modifier = Modifier.width(320.dp), verticalArrangement = Arrangement.spacedBy(gap)) {
                CreateSidebarNavCard()
                CreateFaqCard()
            }
            Column(modifier = Modifier.width(320.dp), verticalArrangement = Arrangement.spacedBy(gap)) {
                CreatePaymentsCard()
                CreateFrontDoorCard()
            }
        }
        CreateReleaseCatalogCard()
    }
    PreviewColumn(gap) {
        CreateAccountAccessCard()
        CreateCardOverviewCard()
        CreateTransferFundsCard()
        CreateCoverArtCard()
        CreateLoadingCard()
    }
    PreviewColumn(gap) {
        CreateReceivingMethodCard()
        CreatePowerUsageCard()
        CreateEmptyConnectBankCard()
        CreateUpcomingPaymentsCard()
        CreateRollerShadesCard()
    }
    PreviewColumn(gap) {
        CreateStockPerformanceCard()
        CreateEmptyExploreCatalogCard()
        CreateNewMilestoneCard()
        CreateSocialLinksCard()
        CreateNotificationSettingsCard()
    }
}

/**
 * Official black glass tabs:
 * `dark … rounded-xl bg-card/90 p-1` with ghost / active accent buttons
 * (`h-7 min-w-8 text-xs`). Compose elevation `shadow-xl` reads as a soft blur smear
 * under the pill, so we skip it and keep the opaque glass only.
 */
@Composable
private fun CreatePreviewSwitcher(
    selectedIndex: Int,
    onSelect: (Int) -> Unit,
    modifier: Modifier = Modifier,
) {
    Theme(isDark = true) {
        val pillShape = RoundedCornerShape(12.dp)
        Row(
            modifier =
                modifier
                    .clip(pillShape)
                    .background(
                        currentTheme.colors.card.copy(alpha = 0.9f),
                        pillShape,
                    )
                    .padding(4.dp),
            horizontalArrangement = Arrangement.spacedBy(4.dp),
        ) {
            listOf("01", "02").forEachIndexed { index, label ->
                val active = selectedIndex == index
                Button(
                    onClick = { onSelect(index) },
                    // data-[active=true]:bg-accent → Secondary on dark theme; h-7 ≈ between Xs/Sm
                    variant = if (active) ButtonVariant.Secondary else ButtonVariant.Ghost,
                    size = ButtonSize.Xs,
                    style =
                        Style {
                            height(28.dp)
                            shape(RoundedCornerShape(8.dp))
                        },
                ) {
                    if (active) {
                        Text { label }
                    } else {
                        Text(textStyle = TextColors.Muted) { label }
                    }
                }
            }
        }
    }
}

@Composable
internal fun PreviewColumn(
    gap: Dp,
    content: @Composable ColumnScope.() -> Unit,
) {
    Column(
        modifier =
            Modifier
                .width(320.dp)
                .fillMaxHeight()
                .verticalScroll(rememberScrollState()),
        verticalArrangement = Arrangement.spacedBy(gap),
        content = content,
    )
}

@Composable
private fun PreviewWideColumn(
    gap: Dp,
    content: @Composable ColumnScope.() -> Unit,
) {
    Column(
        modifier =
            Modifier
                .width(656.dp)
                .fillMaxHeight()
                .verticalScroll(rememberScrollState()),
        verticalArrangement = Arrangement.spacedBy(gap),
        content = content,
    )
}
