@file:OptIn(androidx.compose.foundation.style.ExperimentalFoundationStyleApi::class)

package com.toutiaoyan.compose.ui.catalog.docs

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.toutiaoyan.compose.tailwind.core.twSpacing
import com.toutiaoyan.compose.ui.components.avatar.Avatar
import com.toutiaoyan.compose.ui.components.button.Button
import com.toutiaoyan.compose.ui.components.hovercard.HoverCard
import com.toutiaoyan.compose.ui.components.hovercard.HoverCardContent
import com.toutiaoyan.compose.ui.components.hovercard.HoverCardDescription
import com.toutiaoyan.compose.ui.components.hovercard.HoverCardHeader
import com.toutiaoyan.compose.ui.components.hovercard.HoverCardProvider
import com.toutiaoyan.compose.ui.components.hovercard.HoverCardTitle
import com.toutiaoyan.compose.ui.components.hovercard.HoverCardTrigger
import com.toutiaoyan.compose.ui.components.overlay.PopupPlacement
import com.toutiaoyan.compose.ui.components.text.Text
import com.toutiaoyan.compose.ui.components.text.TextColors
import com.toutiaoyan.compose.ui.components.text.TextVariant

val hoverCardDoc =
    ComponentDoc(
        id = "hover-card",
        title = "Hover Card",
        description = "A hover-triggered panel for richer preview content than a Tooltip.",
        usageCode =
            """
            import com.toutiaoyan.compose.ui.components.hovercard.HoverCard
            import com.toutiaoyan.compose.ui.components.hovercard.HoverCardContent
            import com.toutiaoyan.compose.ui.components.hovercard.HoverCardProvider
            import com.toutiaoyan.compose.ui.components.hovercard.HoverCardTrigger

            HoverCardProvider(openDelay = 0) {
                HoverCard {
                    HoverCardTrigger("@shadcn")
                    HoverCardContent {
                        // avatar, name, description
                    }
                }
            }
            """.trimIndent(),
        examples =
            listOf(
                ComponentExample(
                    title = "Default",
                    code =
                        """
                        HoverCard {
                            HoverCardTrigger("@shadcn")
                            HoverCardContent {
                                Row(modifier = Modifier.width(240.dp)) {
                                    Avatar("SC")
                                    Column {
                                        Text(typography = TextTypography.LabelSmall) { "@shadcn" }
                                        Text(typography = TextTypography.BodySmall) { "The React Framework – created and maintained by @vercel." }
                                        Text(typography = TextTypography.BodySmall, textStyle = TextStyles.Muted) { "Joined December 2021" }
                                    }
                                }
                            }
                        }
                        """.trimIndent(),
                    preview = { HoverCardPreview() },
                ),
                ComponentExample(
                    title = "Side",
                    code =
                        """
                        HoverCard {
                            HoverCardTrigger("@shadcn")
                            HoverCardContent(placement = PopupPlacement.Top) {
                                Text { "The React Framework." }
                            }
                        }
                        """.trimIndent(),
                    preview = {
                        HoverCardProvider {
                            HoverCard {
                                HoverCardTrigger("@shadcn")
                                HoverCardContent(placement = PopupPlacement.Top) {
                                    Text(variant = TextVariant.BodySmall) { "The React Framework." }
                                }
                            }
                        }
                    },
                ),
                ComponentExample(
                    title = "Official side prop",
                    code =
                        """
                        HoverCard {
                            HoverCardTrigger("@shadcn")
                            HoverCardContent(side = PopupPlacement.Top) {
                                Text { "The React Framework." }
                            }
                        }
                        """.trimIndent(),
                    preview = {
                        HoverCardProvider {
                            HoverCard {
                                HoverCardTrigger("@shadcn")
                                HoverCardContent(side = PopupPlacement.Top) {
                                    Text(variant = TextVariant.BodySmall) { "The React Framework." }
                                }
                            }
                        }
                    },
                ),
                ComponentExample(
                    title = "Placement",
                    code =
                        """
                        HoverCard {
                            HoverCardTrigger("@shadcn")
                            HoverCardContent(placement = PopupPlacement.Top) {
                                Text { "The React Framework." }
                            }
                        }
                        """.trimIndent(),
                    preview = {
                        HoverCardProvider {
                            HoverCard {
                                HoverCardTrigger("@shadcn")
                                HoverCardContent(placement = PopupPlacement.Top) {
                                    Text(variant = TextVariant.BodySmall) { "The React Framework." }
                                }
                            }
                        }
                    },
                ),
                ComponentExample(
                    title = "Default side",
                    code =
                        """
                        HoverCard(defaultOpen = false) {
                            HoverCardTrigger("@shadcn")
                            HoverCardContent(side = PopupPlacement.Bottom) {
                                Text { "The React Framework." }
                            }
                        }
                        """.trimIndent(),
                    preview = {
                        HoverCardProvider {
                            HoverCard(defaultOpen = false) {
                                HoverCardTrigger("@shadcn")
                                HoverCardContent(side = PopupPlacement.Bottom) {
                                    Text(variant = TextVariant.BodySmall) { "The React Framework." }
                                }
                            }
                        }
                    },
                ),
                ComponentExample(
                    title = "Default placement",
                    code =
                        """
                        HoverCard(defaultOpen = false) {
                            HoverCardTrigger("@shadcn")
                            HoverCardContent(placement = PopupPlacement.Bottom) {
                                Text { "The React Framework." }
                            }
                        }
                        """.trimIndent(),
                    preview = {
                        HoverCardProvider {
                            HoverCard(defaultOpen = false) {
                                HoverCardTrigger("@shadcn")
                                HoverCardContent(placement = PopupPlacement.Bottom) {
                                    Text(variant = TextVariant.BodySmall) { "The React Framework." }
                                }
                            }
                        }
                    },
                ),
                ComponentExample(
                    title = "Delay",
                    code =
                        """
                        HoverCardProvider(openDelay = 400) {
                            HoverCard {
                                HoverCardTrigger("@shadcn")
                                HoverCardContent {
                                    Text { "Opens after 400ms." }
                                }
                            }
                        }
                        """.trimIndent(),
                    preview = {
                        HoverCardProvider(openDelay = 400) {
                            HoverCard {
                                HoverCardTrigger("@shadcn")
                                HoverCardContent {
                                    Text(variant = TextVariant.BodySmall) { "Opens after 400ms." }
                                }
                            }
                        }
                    },
                ),
                ComponentExample(
                    title = "Close delay",
                    code =
                        """
                        HoverCardProvider(closeDelay = 400) {
                            HoverCard {
                                HoverCardTrigger("@shadcn")
                                HoverCardContent {
                                    Text { "Stays open 400ms after hover ends." }
                                }
                            }
                        }
                        """.trimIndent(),
                    preview = {
                        HoverCardProvider(closeDelay = 400) {
                            HoverCard {
                                HoverCardTrigger("@shadcn")
                                HoverCardContent {
                                    Text(variant = TextVariant.BodySmall) { "Stays open 400ms after hover ends." }
                                }
                            }
                        }
                    },
                ),
                ComponentExample(
                    title = "Uncontrolled",
                    code =
                        """
                        HoverCard(defaultOpen = false) {
                            HoverCardTrigger("@shadcn")
                            HoverCardContent {
                                Text(variant = TextVariant.BodySmall) { "The React Framework." }
                            }
                        }
                        """.trimIndent(),
                    preview = {
                        HoverCard(defaultOpen = false) {
                            HoverCardTrigger("@shadcn")
                            HoverCardContent {
                                Text(variant = TextVariant.BodySmall) { "The React Framework." }
                            }
                        }
                    },
                ),
                ComponentExample(
                    title = "Default open",
                    code =
                        """
                        HoverCard(defaultOpen = false) {
                            HoverCardTrigger("@shadcn")
                            HoverCardContent {
                                Text(variant = TextVariant.BodySmall) { "The React Framework." }
                            }
                        }
                        """.trimIndent(),
                    preview = {
                        HoverCard(defaultOpen = false) {
                            HoverCardTrigger("@shadcn")
                            HoverCardContent {
                                Text(variant = TextVariant.BodySmall) { "The React Framework." }
                            }
                        }
                    },
                ),
                ComponentExample(
                    title = "Controlled",
                    code =
                        """
                        var open by remember { mutableStateOf(false) }
                        Button("Toggle", onClick = { open = !open })
                        HoverCard(open = open, onOpenChange = { open = it }) {
                            HoverCardTrigger("@shadcn")
                            HoverCardContent("The React Framework – created and maintained by @vercel.")
                        }
                        """.trimIndent(),
                    preview = {
                        var open by remember { mutableStateOf(false) }
                        Column(verticalArrangement = Arrangement.spacedBy(twSpacing.sm)) {
                            Button("Toggle", onClick = { open = !open })
                            HoverCard(open = open, onOpenChange = { open = it }) {
                                HoverCardTrigger("@shadcn")
                                HoverCardContent {
                                    Text(variant = TextVariant.BodySmall) {
                                        "The React Framework – created and maintained by @vercel."
                                    }
                                }
                            }
                        }
                    },
                ),
                ComponentExample(
                    title = "Open delay",
                    code =
                        """
                        HoverCard(openDelay = 400) {
                            HoverCardTrigger("@shadcn")
                            HoverCardContent("The React Framework – created and maintained by @vercel.")
                        }
                        """.trimIndent(),
                    preview = {
                        HoverCard(openDelay = 400) {
                            HoverCardTrigger("@shadcn")
                            HoverCardContent {
                                Text(variant = TextVariant.BodySmall) {
                                    "The React Framework – created and maintained by @vercel."
                                }
                            }
                        }
                    },
                ),
                ComponentExample(
                    title = "Default delay",
                    code =
                        """
                        HoverCard(openDelay = 0) {
                            HoverCardTrigger("@shadcn")
                            HoverCardContent("The React Framework – created and maintained by @vercel.")
                        }
                        """.trimIndent(),
                    preview = {
                        HoverCard(openDelay = 0) {
                            HoverCardTrigger("@shadcn")
                            HoverCardContent {
                                Text(variant = TextVariant.BodySmall) {
                                    "The React Framework – created and maintained by @vercel."
                                }
                            }
                        }
                    },
                ),
                ComponentExample(
                    title = "Default close delay",
                    code =
                        """
                        HoverCard(closeDelay = 100) {
                            HoverCardTrigger("@shadcn")
                            HoverCardContent("The React Framework – created and maintained by @vercel.")
                        }
                        """.trimIndent(),
                    preview = {
                        HoverCard(closeDelay = 100) {
                            HoverCardTrigger("@shadcn")
                            HoverCardContent {
                                Text(variant = TextVariant.BodySmall) {
                                    "The React Framework – created and maintained by @vercel."
                                }
                            }
                        }
                    },
                ),
                ComponentExample(
                    title = "Side offset",
                    code =
                        """
                        HoverCard {
                            HoverCardTrigger("@shadcn")
                            HoverCardContent(
                                "The React Framework – created and maintained by @vercel.",
                                sideOffset = 12.dp,
                            )
                        }
                        """.trimIndent(),
                    preview = {
                        HoverCardProvider {
                            HoverCard {
                                HoverCardTrigger("@shadcn")
                                HoverCardContent(
                                    "The React Framework – created and maintained by @vercel.",
                                    sideOffset = 12.dp,
                                )
                            }
                        }
                    },
                ),
                ComponentExample(
                    title = "Default side offset",
                    code =
                        """
                        HoverCard {
                            HoverCardTrigger("@shadcn")
                            HoverCardContent(
                                "The React Framework – created and maintained by @vercel.",
                                sideOffset = 4.dp,
                            )
                        }
                        """.trimIndent(),
                    preview = {
                        HoverCardProvider {
                            HoverCard {
                                HoverCardTrigger("@shadcn")
                                HoverCardContent(
                                    "The React Framework – created and maintained by @vercel.",
                                    sideOffset = 4.dp,
                                )
                            }
                        }
                    },
                ),
                ComponentExample(
                    title = "Disabled",
                    code =
                        """
                        HoverCard(disabled = true) {
                            HoverCardTrigger("@shadcn")
                            HoverCardContent("The React Framework – created and maintained by @vercel.")
                        }
                        """.trimIndent(),
                    preview = {
                        HoverCardProvider {
                            HoverCard(disabled = true) {
                                HoverCardTrigger("@shadcn")
                                HoverCardContent("The React Framework – created and maintained by @vercel.")
                            }
                        }
                    },
                ),
                ComponentExample(
                    title = "Default disabled",
                    code =
                        """
                        HoverCard(defaultOpen = false, disabled = false) {
                            HoverCardTrigger("@shadcn")
                            HoverCardContent("The React Framework – created and maintained by @vercel.")
                        }
                        """.trimIndent(),
                    preview = {
                        HoverCardProvider {
                            HoverCard(defaultOpen = false, disabled = false) {
                                HoverCardTrigger("@shadcn")
                                HoverCardContent("The React Framework – created and maintained by @vercel.")
                            }
                        }
                    },
                ),
                ComponentExample(
                    title = "Non-hoverable content",
                    code =
                        """
                        HoverCard(disableHoverableContent = true) {
                            HoverCardTrigger("@shadcn")
                            HoverCardContent("The React Framework – created and maintained by @vercel.")
                        }
                        """.trimIndent(),
                    preview = {
                        HoverCardProvider {
                            HoverCard(disableHoverableContent = true) {
                                HoverCardTrigger("@shadcn")
                                HoverCardContent("The React Framework – created and maintained by @vercel.")
                            }
                        }
                    },
                ),
                ComponentExample(
                    title = "Hoverable content",
                    code =
                        """
                        HoverCard(disableHoverableContent = false) {
                            HoverCardTrigger("@shadcn")
                            HoverCardContent("The React Framework – created and maintained by @vercel.")
                        }
                        """.trimIndent(),
                    preview = {
                        HoverCardProvider {
                            HoverCard(defaultOpen = false, disableHoverableContent = false) {
                                HoverCardTrigger("@shadcn")
                                HoverCardContent("The React Framework – created and maintained by @vercel.")
                            }
                        }
                    },
                ),
                ComponentExample(
                    title = "Default hoverable content",
                    code =
                        """
                        HoverCard(disableHoverableContent = false) {
                            HoverCardTrigger("@shadcn")
                            HoverCardContent("The React Framework – created and maintained by @vercel.")
                        }
                        """.trimIndent(),
                    preview = {
                        HoverCardProvider {
                            HoverCard(defaultOpen = false, disableHoverableContent = false) {
                                HoverCardTrigger("@shadcn")
                                HoverCardContent("The React Framework – created and maintained by @vercel.")
                            }
                        }
                    },
                ),
                ComponentExample(
                    title = "Dismiss on outside click",
                    code =
                        """
                        HoverCard {
                            HoverCardTrigger("@shadcn")
                            HoverCardContent(
                                "The React Framework – created and maintained by @vercel.",
                                dismissOnClickOutside = true,
                            )
                        }
                        """.trimIndent(),
                    preview = {
                        HoverCardProvider {
                            HoverCard {
                                HoverCardTrigger("@shadcn")
                                HoverCardContent(
                                    "The React Framework – created and maintained by @vercel.",
                                    dismissOnClickOutside = true,
                                )
                            }
                        }
                    },
                ),
                ComponentExample(
                    title = "Default dismiss",
                    code =
                        """
                        HoverCard {
                            HoverCardTrigger("@shadcn")
                            HoverCardContent(
                                "The React Framework – created and maintained by @vercel.",
                                dismissOnClickOutside = false,
                            )
                        }
                        """.trimIndent(),
                    preview = {
                        HoverCardProvider {
                            HoverCard {
                                HoverCardTrigger("@shadcn")
                                HoverCardContent(
                                    "The React Framework – created and maintained by @vercel.",
                                    dismissOnClickOutside = false,
                                )
                            }
                        }
                    },
                ),
                ComponentExample(
                    title = "Title and description",
                    code =
                        """
                        HoverCard {
                            HoverCardTrigger("@shadcn")
                            HoverCardContent {
                                HoverCardHeader {
                                    HoverCardTitle("@shadcn")
                                    HoverCardDescription("The React Framework – created and maintained by @vercel.")
                                }
                            }
                        }
                        """.trimIndent(),
                    preview = {
                        HoverCardProvider {
                            HoverCard {
                                HoverCardTrigger("@shadcn")
                                HoverCardContent {
                                    HoverCardHeader {
                                        HoverCardTitle("@shadcn")
                                        HoverCardDescription("The React Framework – created and maintained by @vercel.")
                                    }
                                }
                            }
                        }
                    },
                ),
                ComponentExample(
                    title = "Provider",
                    code =
                        """
                        HoverCardProvider {
                            HoverCard {
                                HoverCardTrigger("@shadcn")
                                HoverCardContent {
                                    Text { "The React Framework." }
                                }
                            }
                        }
                        """.trimIndent(),
                    preview = {
                        HoverCardProvider {
                            HoverCard {
                                HoverCardTrigger("@shadcn")
                                HoverCardContent {
                                    Text { "The React Framework." }
                                }
                            }
                        }
                    },
                ),
                ComponentExample(
                    title = "Header",
                    code =
                        """
                        HoverCardProvider {
                            HoverCard {
                                HoverCardTrigger("@shadcn")
                                HoverCardContent {
                                    HoverCardHeader {
                                        HoverCardTitle("@shadcn")
                                        HoverCardDescription("Compose Title and Description inside Header.")
                                    }
                                }
                            }
                        }
                        """.trimIndent(),
                    preview = {
                        HoverCardProvider {
                            HoverCard {
                                HoverCardTrigger("@shadcn")
                                HoverCardContent {
                                    HoverCardHeader {
                                        HoverCardTitle("@shadcn")
                                        HoverCardDescription("Compose Title and Description inside Header.")
                                    }
                                }
                            }
                        }
                    },
                ),
                ComponentExample(
                    title = "Trigger",
                    code =
                        """
                        HoverCardProvider {
                            HoverCard {
                                HoverCardTrigger("@shadcn")
                                HoverCardContent {
                                    Text { "The React Framework." }
                                }
                            }
                        }
                        """.trimIndent(),
                    preview = {
                        HoverCardProvider {
                            HoverCard {
                                HoverCardTrigger("@shadcn")
                                HoverCardContent {
                                    Text { "The React Framework." }
                                }
                            }
                        }
                    },
                ),
                ComponentExample(
                    title = "Content",
                    code =
                        """
                        HoverCardProvider {
                            HoverCard {
                                HoverCardTrigger("@shadcn")
                                HoverCardContent {
                                    Text { "The React Framework." }
                                }
                            }
                        }
                        """.trimIndent(),
                    preview = {
                        HoverCardProvider {
                            HoverCard {
                                HoverCardTrigger("@shadcn")
                                HoverCardContent {
                                    Text { "The React Framework." }
                                }
                            }
                        }
                    },
                ),
                ComponentExample(
                    title = "Description",
                    code =
                        """
                        HoverCardProvider {
                            HoverCard {
                                HoverCardTrigger("@shadcn")
                                HoverCardContent {
                                    HoverCardHeader {
                                        HoverCardTitle("@shadcn")
                                        HoverCardDescription("HoverCardDescription matches shadcn's description slot.")
                                    }
                                }
                            }
                        }
                        """.trimIndent(),
                    preview = {
                        HoverCardProvider {
                            HoverCard {
                                HoverCardTrigger("@shadcn")
                                HoverCardContent {
                                    HoverCardHeader {
                                        HoverCardTitle("@shadcn")
                                        HoverCardDescription("HoverCardDescription matches shadcn's description slot.")
                                    }
                                }
                            }
                        }
                    },
                ),
                ComponentExample(
                    title = "Title",
                    code =
                        """
                        HoverCardProvider {
                            HoverCard {
                                HoverCardTrigger("@shadcn")
                                HoverCardContent {
                                    HoverCardHeader {
                                        HoverCardTitle("@shadcn")
                                        HoverCardDescription("Compose Title and Description inside Header.")
                                    }
                                }
                            }
                        }
                        """.trimIndent(),
                    preview = {
                        HoverCardProvider {
                            HoverCard {
                                HoverCardTrigger("@shadcn")
                                HoverCardContent {
                                    HoverCardHeader {
                                        HoverCardTitle("@shadcn")
                                        HoverCardDescription("Compose Title and Description inside Header.")
                                    }
                                }
                            }
                        }
                    },
                ),
            ),
    )

@Composable
private fun HoverCardPreview() {
    HoverCardProvider {
        HoverCard {
                            HoverCardTrigger("@shadcn")
        HoverCardContent {
            Row(modifier = Modifier.width(240.dp)) {
                Avatar("SC")
                Column(
                    modifier = Modifier.padding(start = twSpacing.sm),
                    verticalArrangement = Arrangement.spacedBy(twSpacing.xs),
                ) {
                    Text(variant = TextVariant.LabelSmall) { "@shadcn" }
                    Text(variant = TextVariant.BodySmall) { "The React Framework – created and maintained by @vercel." }
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Text(variant = TextVariant.BodySmall, textStyle = TextColors.Muted) { "Joined December 2021" }
                    }
                }
            }
        }
        }
    }
}
