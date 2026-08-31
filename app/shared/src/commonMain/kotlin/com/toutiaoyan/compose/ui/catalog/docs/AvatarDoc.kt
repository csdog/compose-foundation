@file:OptIn(androidx.compose.foundation.style.ExperimentalFoundationStyleApi::class)

package com.toutiaoyan.compose.ui.catalog.docs

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.toutiaoyan.compose.ui.components.avatar.Avatar
import com.toutiaoyan.compose.ui.components.avatar.AvatarBadge
import com.toutiaoyan.compose.ui.components.avatar.AvatarFallback
import com.toutiaoyan.compose.ui.components.avatar.AvatarGroup
import com.toutiaoyan.compose.ui.components.avatar.AvatarGroupCount
import com.toutiaoyan.compose.ui.components.avatar.AvatarGroupDefaults
import com.toutiaoyan.compose.ui.components.avatar.AvatarImage
import com.toutiaoyan.compose.ui.components.avatar.AvatarSize

val avatarDoc =
    ComponentDoc(
        id = "avatar",
        title = "Avatar",
        description = "A circular user/entity image container with an initials fallback.",
        usageCode =
            """
            import com.toutiaoyan.compose.ui.components.avatar.Avatar
            import com.toutiaoyan.compose.ui.components.avatar.AvatarFallback
            import com.toutiaoyan.compose.ui.components.avatar.AvatarImage
            import com.toutiaoyan.compose.ui.components.avatar.LocalAvatarSize
            import com.toutiaoyan.compose.ui.components.avatar.textStyle
            import com.toutiaoyan.compose.ui.components.text.Text

            Avatar {
                AvatarImage(model = "https://example.com/avatar.png", contentDescription = "Avatar")
                AvatarFallback("CN")
            }
            """.trimIndent(),
        examples =
            listOf(
                ComponentExample(
                    title = "Default",
                    code =
                        """
                        Avatar("CN")
                        """.trimIndent(),
                    preview = {
                        Avatar("CN")
                    },
                ),
                ComponentExample(
                    title = "Sizes",
                    code =
                        """
                        Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                            Avatar("SM", size = AvatarSize.Sm)
                            Avatar("MD", size = AvatarSize.Default)
                            Avatar("LG", size = AvatarSize.Lg)
                        }
                        """.trimIndent(),
                    preview = {
                        Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                            Avatar("SM", size = AvatarSize.Sm)
                            Avatar("MD", size = AvatarSize.Default)
                            Avatar("LG", size = AvatarSize.Lg)
                        }
                    },
                ),
                ComponentExample(
                    title = "Default size",
                    code = """Avatar("CN", size = AvatarSize.Default)""",
                    preview = { Avatar("CN", size = AvatarSize.Default) },
                ),
                ComponentExample(
                    title = "Group",
                    code =
                        """
                        AvatarGroup {
                            Avatar("CN")
                            Avatar("LR")
                            Avatar("ER")
                        }
                        """.trimIndent(),
                    preview = {
                        AvatarGroup {
                            Avatar("CN")
                            Avatar("LR")
                            Avatar("ER")
                        }
                    },
                ),
                ComponentExample(
                    title = "Group count",
                    code =
                        """
                        AvatarGroup {
                            Avatar("CN")
                            Avatar("LR")
                            Avatar("ER")
                            AvatarGroupCount("+3")
                        }
                        """.trimIndent(),
                    preview = {
                        AvatarGroup {
                            Avatar("CN")
                            Avatar("LR")
                            Avatar("ER")
                            AvatarGroupCount("+3")
                        }
                    },
                ),
                ComponentExample(
                    title = "Overlap",
                    code =
                        """
                        AvatarGroup(spacing = AvatarGroupDefaults.Overlap) {
                            Avatar("CN")
                            Avatar("LR")
                            Avatar("ER")
                            AvatarGroupCount("+3")
                        }
                        """.trimIndent(),
                    preview = {
                        AvatarGroup(spacing = AvatarGroupDefaults.Overlap) {
                            Avatar("CN")
                            Avatar("LR")
                            Avatar("ER")
                            AvatarGroupCount("+3")
                        }
                    },
                ),
                ComponentExample(
                    title = "Badge",
                    code =
                        """
                        Avatar {
                            AvatarFallback("CN")
                            AvatarBadge(modifier = Modifier.align(Alignment.BottomEnd))
                        }
                        """.trimIndent(),
                    preview = {
                        Avatar {
                            AvatarFallback("CN")
                            AvatarBadge(modifier = Modifier.align(Alignment.BottomEnd))
                        }
                    },
                ),
                ComponentExample(
                    title = "Fallback delay",
                    code =
                        """
                        Avatar {
                            AvatarFallback("CN", delayMs = 600)
                        }
                        """.trimIndent(),
                    preview = {
                        Avatar {
                            AvatarFallback("CN", delayMs = 600)
                        }
                    },
                ),
                ComponentExample(
                    title = "Default fallback delay",
                    code =
                        """
                        Avatar {
                            AvatarFallback("CN", delayMs = 0)
                        }
                        """.trimIndent(),
                    preview = {
                        Avatar {
                            AvatarFallback("CN", delayMs = 0)
                        }
                    },
                ),
                ComponentExample(
                    title = "Loading status callback",
                    code =
                        """
                        Avatar {
                            AvatarImage(
                                model = null,
                                contentDescription = "Avatar",
                                onLoadingStatusChange = {},
                            )
                            AvatarFallback("CN")
                        }
                        """.trimIndent(),
                    preview = {
                        Avatar {
                            AvatarImage(
                                model = null,
                                contentDescription = "Avatar",
                                onLoadingStatusChange = {},
                            )
                            AvatarFallback("CN")
                        }
                    },
                ),
                ComponentExample(
                    title = "Fallback",
                    code =
                        """
                        Avatar {
                            AvatarFallback("CN")
                        }
                        """.trimIndent(),
                    preview = {
                        Avatar {
                            AvatarFallback("CN")
                        }
                    },
                ),
                ComponentExample(
                    title = "Image",
                    code =
                        """
                        Avatar {
                            AvatarImage(model = null, contentDescription = "Avatar")
                            AvatarFallback("CN")
                        }
                        """.trimIndent(),
                    preview = {
                        Avatar {
                            AvatarImage(model = null, contentDescription = "Avatar")
                            AvatarFallback("CN")
                        }
                    },
                ),
            ),
    )
