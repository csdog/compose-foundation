@file:OptIn(androidx.compose.foundation.style.ExperimentalFoundationStyleApi::class)

package com.toutiaoyan.compose.ui.catalog.docs

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.unit.dp
import com.toutiaoyan.compose.ui.components.overlay.PopupPlacement
import com.toutiaoyan.compose.ui.components.tooltip.Tooltip
import com.toutiaoyan.compose.ui.components.tooltip.TooltipContent
import com.toutiaoyan.compose.ui.components.tooltip.TooltipDefaults
import com.toutiaoyan.compose.ui.components.tooltip.TooltipProvider
import com.toutiaoyan.compose.ui.components.tooltip.TooltipTrigger

val tooltipDoc =
    ComponentDoc(
        id = "tooltip",
        title = "Tooltip",
        description = "A small hover-triggered label for an interactive element.",
        usageCode =
            """
            import com.toutiaoyan.compose.ui.components.tooltip.Tooltip
            import com.toutiaoyan.compose.ui.components.tooltip.TooltipContent
            import com.toutiaoyan.compose.ui.components.tooltip.TooltipProvider
            import com.toutiaoyan.compose.ui.components.tooltip.TooltipTrigger

            TooltipProvider(delayDuration = 0) {
                Tooltip {
                    TooltipTrigger("Hover me")
                    TooltipContent("Add to library")
                }
            }
            """.trimIndent(),
        examples =
            listOf(
                ComponentExample(
                    title = "Default",
                    code =
                        """
                        Tooltip {
                            TooltipTrigger("Hover me")
                            TooltipContent("Add to library")
                        }
                        """.trimIndent(),
                    preview = {
                        TooltipProvider {
                            Tooltip {
                                TooltipTrigger("Hover me")
                                TooltipContent("Add to library")
                            }
                        }
                    },
                ),
                ComponentExample(
                    title = "Arrow",
                    code =
                        """
                        Tooltip {
                            TooltipTrigger("Hover me")
                            TooltipContent("Add to library", showArrow = true)
                        }
                        """.trimIndent(),
                    preview = {
                        TooltipProvider {
                            Tooltip {
                                TooltipTrigger("Hover me")
                                TooltipContent("Add to library", showArrow = true)
                            }
                        }
                    },
                ),
                ComponentExample(
                    title = "Side",
                    code =
                        """
                        Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                            Tooltip {
                                TooltipTrigger("Top")
                                TooltipContent("Top", placement = PopupPlacement.Top)
                            }
                            Tooltip {
                                TooltipTrigger("Right")
                                TooltipContent("Right", placement = PopupPlacement.End)
                            }
                            Tooltip {
                                TooltipTrigger("Bottom")
                                TooltipContent("Bottom", placement = PopupPlacement.Bottom)
                            }
                            Tooltip {
                                TooltipTrigger("Left")
                                TooltipContent("Left", placement = PopupPlacement.Start)
                            }
                        }
                        """.trimIndent(),
                    preview = {
                        TooltipProvider {
                            Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                                Tooltip {
                                    TooltipTrigger("Top")
                                    TooltipContent("Top", placement = PopupPlacement.Top)
                                }
                                Tooltip {
                                    TooltipTrigger("Right")
                                    TooltipContent("Right", placement = PopupPlacement.End)
                                }
                                Tooltip {
                                    TooltipTrigger("Bottom")
                                    TooltipContent("Bottom", placement = PopupPlacement.Bottom)
                                }
                                Tooltip {
                                    TooltipTrigger("Left")
                                    TooltipContent("Left", placement = PopupPlacement.Start)
                                }
                            }
                        }
                    },
                ),
                ComponentExample(
                    title = "Official side prop",
                    code =
                        """
                        Tooltip {
                            TooltipTrigger("Hover me")
                            TooltipContent("Add to library", side = PopupPlacement.Top)
                        }
                        """.trimIndent(),
                    preview = {
                        TooltipProvider {
                            Tooltip {
                                TooltipTrigger("Hover me")
                                TooltipContent("Add to library", side = PopupPlacement.Top)
                            }
                        }
                    },
                ),
                ComponentExample(
                    title = "Default side",
                    code =
                        """
                        Tooltip {
                            TooltipTrigger("Hover me")
                            TooltipContent("Add to library", side = PopupPlacement.Top)
                        }
                        """.trimIndent(),
                    preview = {
                        TooltipProvider {
                            Tooltip {
                                TooltipTrigger("Hover me")
                                TooltipContent("Add to library", side = PopupPlacement.Top)
                            }
                        }
                    },
                ),
                ComponentExample(
                    title = "Default placement",
                    code =
                        """
                        Tooltip {
                            TooltipTrigger("Hover me")
                            TooltipContent("Add to library", placement = PopupPlacement.Top)
                        }
                        """.trimIndent(),
                    preview = {
                        TooltipProvider {
                            Tooltip {
                                TooltipTrigger("Hover me")
                                TooltipContent("Add to library", placement = PopupPlacement.Top)
                            }
                        }
                    },
                ),
                ComponentExample(
                    title = "Placement",
                    code =
                        """
                        Tooltip {
                            TooltipTrigger("Hover me")
                            TooltipContent("Add to library", placement = PopupPlacement.Top)
                        }
                        """.trimIndent(),
                    preview = {
                        TooltipProvider {
                            Tooltip {
                                TooltipTrigger("Hover me")
                                TooltipContent("Add to library", placement = PopupPlacement.Top)
                            }
                        }
                    },
                ),
                ComponentExample(
                    title = "Delay",
                    code =
                        """
                        TooltipProvider(delayDuration = 400) {
                            Tooltip {
                                TooltipTrigger("Hover me")
                                TooltipContent("Opens after 400ms")
                            }
                        }
                        """.trimIndent(),
                    preview = {
                        TooltipProvider(delayDuration = 400) {
                            Tooltip {
                                TooltipTrigger("Hover me")
                                TooltipContent("Opens after 400ms")
                            }
                        }
                    },
                ),
                ComponentExample(
                    title = "Skip delay",
                    code =
                        """
                        TooltipProvider(delayDuration = 400, skipDelayDuration = 300) {
                            Row(horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                                Tooltip {
                                    TooltipTrigger("First")
                                    TooltipContent("Then hover the next one")
                                }
                                Tooltip {
                                    TooltipTrigger("Second")
                                    TooltipContent("Opens without waiting")
                                }
                            }
                        }
                        """.trimIndent(),
                    preview = {
                        TooltipProvider(delayDuration = 400, skipDelayDuration = 300) {
                            Row(horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                                Tooltip {
                                    TooltipTrigger("First")
                                    TooltipContent("Then hover the next one")
                                }
                                Tooltip {
                                    TooltipTrigger("Second")
                                    TooltipContent("Opens without waiting")
                                }
                            }
                        }
                    },
                ),
                ComponentExample(
                    title = "Default skip delay",
                    code =
                        """
                        TooltipProvider(skipDelayDuration = 300) {
                            Tooltip {
                                TooltipTrigger("Hover me")
                                TooltipContent("Add to library")
                            }
                        }
                        """.trimIndent(),
                    preview = {
                        TooltipProvider(skipDelayDuration = 300) {
                            Tooltip {
                                TooltipTrigger("Hover me")
                                TooltipContent("Add to library")
                            }
                        }
                    },
                ),
                ComponentExample(
                    title = "Open delay",
                    code =
                        """
                        Tooltip(delayDuration = 400) {
                            TooltipTrigger("Hover me")
                            TooltipContent("Opens after 400ms")
                        }
                        """.trimIndent(),
                    preview = {
                        Tooltip(delayDuration = 400) {
                            TooltipTrigger("Hover me")
                            TooltipContent("Opens after 400ms")
                        }
                    },
                ),
                ComponentExample(
                    title = "Default delay",
                    code =
                        """
                        Tooltip(delayDuration = 0) {
                            TooltipTrigger("Hover me")
                            TooltipContent("Opens instantly")
                        }
                        """.trimIndent(),
                    preview = {
                        Tooltip(delayDuration = 0) {
                            TooltipTrigger("Hover me")
                            TooltipContent("Opens instantly")
                        }
                    },
                ),
                ComponentExample(
                    title = "Controlled",
                    code =
                        """
                        var open by remember { mutableStateOf(false) }
                        Tooltip(open = open, onOpenChange = { open = it }) {
                            TooltipTrigger("Hover me")
                            TooltipContent("Add to library")
                        }
                        """.trimIndent(),
                    preview = {
                        var open by remember { mutableStateOf(false) }
                        Tooltip(open = open, onOpenChange = { open = it }) {
                            TooltipTrigger("Hover me")
                            TooltipContent("Add to library")
                        }
                    },
                ),
                ComponentExample(
                    title = "Hoverable content",
                    code =
                        """
                        Tooltip(disableHoverableContent = false) {
                            TooltipTrigger("Hover me")
                            TooltipContent("You can move the pointer onto this tooltip.")
                        }
                        """.trimIndent(),
                    preview = {
                        Tooltip(disableHoverableContent = false) {
                            TooltipTrigger("Hover me")
                            TooltipContent("You can move the pointer onto this tooltip.")
                        }
                    },
                ),
                ComponentExample(
                    title = "Default hoverable content",
                    code =
                        """
                        Tooltip(disableHoverableContent = true) {
                            TooltipTrigger("Hover me")
                            TooltipContent("Add to library")
                        }
                        """.trimIndent(),
                    preview = {
                        Tooltip(disableHoverableContent = true) {
                            TooltipTrigger("Hover me")
                            TooltipContent("Add to library")
                        }
                    },
                ),
                ComponentExample(
                    title = "Side offset",
                    code =
                        """
                        Tooltip {
                            TooltipTrigger("Hover me")
                            TooltipContent("Add to library", sideOffset = 12.dp)
                        }
                        """.trimIndent(),
                    preview = {
                        Tooltip {
                            TooltipTrigger("Hover me")
                            TooltipContent("Add to library", sideOffset = 12.dp)
                        }
                    },
                ),
                ComponentExample(
                    title = "Default side offset",
                    code =
                        """
                        Tooltip {
                            TooltipTrigger("Hover me")
                            TooltipContent("Add to library", sideOffset = 4.dp)
                        }
                        """.trimIndent(),
                    preview = {
                        Tooltip {
                            TooltipTrigger("Hover me")
                            TooltipContent("Add to library", sideOffset = 4.dp)
                        }
                    },
                ),
                ComponentExample(
                    title = "Official side offset",
                    code =
                        """
                        Tooltip {
                            TooltipTrigger("Hover me")
                            TooltipContent("Add to library", sideOffset = TooltipDefaults.SideOffset)
                        }
                        """.trimIndent(),
                    preview = {
                        TooltipProvider {
                            Tooltip {
                                TooltipTrigger("Hover me")
                                TooltipContent("Add to library", sideOffset = TooltipDefaults.SideOffset)
                            }
                        }
                    },
                ),
                ComponentExample(
                    title = "Disabled",
                    code =
                        """
                        Tooltip(disabled = true) {
                            TooltipTrigger("Hover me")
                            TooltipContent("Add to library")
                        }
                        """.trimIndent(),
                    preview = {
                        Tooltip(disabled = true) {
                            TooltipTrigger("Hover me")
                            TooltipContent("Add to library")
                        }
                    },
                ),
                ComponentExample(
                    title = "Default disabled",
                    code =
                        """
                        Tooltip(disabled = false) {
                            TooltipTrigger("Hover me")
                            TooltipContent("Add to library")
                        }
                        """.trimIndent(),
                    preview = {
                        Tooltip(disabled = false) {
                            TooltipTrigger("Hover me")
                            TooltipContent("Add to library")
                        }
                    },
                ),
                ComponentExample(
                    title = "Provider",
                    code =
                        """
                        TooltipProvider {
                            Tooltip {
                                TooltipTrigger("Hover me")
                                TooltipContent("Add to library")
                            }
                        }
                        """.trimIndent(),
                    preview = {
                        TooltipProvider {
                            Tooltip {
                                TooltipTrigger("Hover me")
                                TooltipContent("Add to library")
                            }
                        }
                    },
                ),
                ComponentExample(
                    title = "Trigger",
                    code =
                        """
                        Tooltip {
                            TooltipTrigger("Hover me")
                            TooltipContent("Add to library")
                        }
                        """.trimIndent(),
                    preview = {
                        Tooltip {
                            TooltipTrigger("Hover me")
                            TooltipContent("Add to library")
                        }
                    },
                ),
                ComponentExample(
                    title = "Content",
                    code =
                        """
                        Tooltip {
                            TooltipTrigger("Hover me")
                            TooltipContent("Add to library")
                        }
                        """.trimIndent(),
                    preview = {
                        Tooltip {
                            TooltipTrigger("Hover me")
                            TooltipContent("Add to library")
                        }
                    },
                ),
            ),
    )
