@file:OptIn(androidx.compose.foundation.style.ExperimentalFoundationStyleApi::class)

package com.toutiaoyan.compose.ui.catalog.docs

import com.toutiaoyan.compose.ui.components.breadcrumb.Breadcrumb
import com.toutiaoyan.compose.ui.components.breadcrumb.BreadcrumbEllipsis
import com.toutiaoyan.compose.ui.components.breadcrumb.BreadcrumbItem
import com.toutiaoyan.compose.ui.components.breadcrumb.BreadcrumbLink
import com.toutiaoyan.compose.ui.components.breadcrumb.BreadcrumbList
import com.toutiaoyan.compose.ui.components.breadcrumb.BreadcrumbPage
import com.toutiaoyan.compose.ui.components.breadcrumb.BreadcrumbSeparator
import com.toutiaoyan.compose.ui.components.dropdownmenu.DropdownMenu
import com.toutiaoyan.compose.ui.components.dropdownmenu.DropdownMenuContent
import com.toutiaoyan.compose.ui.components.dropdownmenu.DropdownMenuItem
import com.toutiaoyan.compose.ui.components.dropdownmenu.DropdownMenuTrigger
import com.toutiaoyan.compose.ui.components.text.Text
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue

val breadcrumbDoc =
    ComponentDoc(
        id = "breadcrumb",
        title = "Breadcrumb",
        description = "A row of navigation links showing the current page's location in a hierarchy.",
        usageCode =
            """
            import com.toutiaoyan.compose.ui.components.*

            Breadcrumb {
                BreadcrumbList {
                    BreadcrumbItem("Home", onClick = {})
                    BreadcrumbSeparator()
                    BreadcrumbItem("Settings")
                }
            }
            """.trimIndent(),
        examples =
            listOf(
                ComponentExample(
                    title = "Default",
                    code =
                        """
                        Breadcrumb {
                            BreadcrumbList {
                                BreadcrumbItem("Home", onClick = {})
                                BreadcrumbSeparator()
                                BreadcrumbItem("Components", onClick = {})
                                BreadcrumbSeparator()
                                BreadcrumbItem("Breadcrumb")
                            }
                        }
                        """.trimIndent(),
                    preview = {
                        Breadcrumb {
                            BreadcrumbList {
                                BreadcrumbItem("Home", onClick = {})
                                BreadcrumbSeparator()
                                BreadcrumbItem("Components", onClick = {})
                                BreadcrumbSeparator()
                                BreadcrumbItem("Breadcrumb")
                            }
                        }
                    },
                ),
                ComponentExample(
                    title = "With ellipsis",
                    code =
                        """
                        Breadcrumb {
                            BreadcrumbList {
                                BreadcrumbItem("Home", onClick = {})
                                BreadcrumbSeparator()
                                BreadcrumbItem { BreadcrumbEllipsis() }
                                BreadcrumbSeparator()
                                BreadcrumbItem("Settings")
                            }
                        }
                        """.trimIndent(),
                    preview = {
                        Breadcrumb {
                            BreadcrumbList {
                                BreadcrumbItem("Home", onClick = {})
                                BreadcrumbSeparator()
                                BreadcrumbItem { BreadcrumbEllipsis() }
                                BreadcrumbSeparator()
                                BreadcrumbItem("Settings")
                            }
                        }
                    },
                ),
                ComponentExample(
                    title = "Custom separator",
                    code =
                        """
                        Breadcrumb {
                            BreadcrumbList {
                                BreadcrumbItem("Home", onClick = {})
                                BreadcrumbSeparator { Text { "/" } }
                                BreadcrumbItem("Docs", onClick = {})
                                BreadcrumbSeparator { Text { "/" } }
                                BreadcrumbItem("Breadcrumb")
                            }
                        }
                        """.trimIndent(),
                    preview = {
                        Breadcrumb {
                            BreadcrumbList {
                                BreadcrumbItem("Home", onClick = {})
                                BreadcrumbSeparator { Text { "/" } }
                                BreadcrumbItem("Docs", onClick = {})
                                BreadcrumbSeparator { Text { "/" } }
                                BreadcrumbItem("Breadcrumb")
                            }
                        }
                    },
                ),
                ComponentExample(
                    title = "With dropdown",
                    code =
                        """
                        var open by remember { mutableStateOf(false) }
                        Breadcrumb {
                            BreadcrumbList {
                                BreadcrumbItem("Home", onClick = {})
                                BreadcrumbSeparator()
                                BreadcrumbItem {
                                    DropdownMenu(open = open, onOpenChange = { open = it }) {
                                        DropdownMenuTrigger { BreadcrumbEllipsis() }
                                        DropdownMenuContent {
                                            DropdownMenuItem("Documentation", onClick = {})
                                            DropdownMenuItem("Themes", onClick = {})
                                            DropdownMenuItem("GitHub", onClick = {})
                                        }
                                    }
                                }
                                BreadcrumbSeparator()
                                BreadcrumbItem("Breadcrumb")
                            }
                        }
                        """.trimIndent(),
                    preview = {
                        var open by remember { mutableStateOf(false) }
                        Breadcrumb {
                            BreadcrumbList {
                                BreadcrumbItem("Home", onClick = {})
                                BreadcrumbSeparator()
                                BreadcrumbItem {
                                    DropdownMenu(open = open, onOpenChange = { open = it }) {
                                        DropdownMenuTrigger { BreadcrumbEllipsis() }
                                        DropdownMenuContent {
                                            DropdownMenuItem("Documentation", onClick = {})
                                            DropdownMenuItem("Themes", onClick = {})
                                            DropdownMenuItem("GitHub", onClick = {})
                                        }
                                    }
                                }
                                BreadcrumbSeparator()
                                BreadcrumbItem("Breadcrumb")
                            }
                        }
                    },
                ),
                ComponentExample(
                    title = "Disabled",
                    code =
                        """
                        Breadcrumb {
                            BreadcrumbList {
                                BreadcrumbItem("Home", onClick = {}, disabled = true)
                                BreadcrumbSeparator()
                                BreadcrumbItem("Breadcrumb")
                            }
                        }
                        """.trimIndent(),
                    preview = {
                        Breadcrumb {
                            BreadcrumbList {
                                BreadcrumbItem("Home", onClick = {}, disabled = true)
                                BreadcrumbSeparator()
                                BreadcrumbItem("Breadcrumb")
                            }
                        }
                    },
                ),
                ComponentExample(
                    title = "Default disabled",
                    code =
                        """
                        Breadcrumb {
                            BreadcrumbList {
                                BreadcrumbItem("Home", onClick = {}, disabled = false)
                                BreadcrumbSeparator()
                                BreadcrumbItem("Breadcrumb")
                            }
                        }
                        """.trimIndent(),
                    preview = {
                        Breadcrumb {
                            BreadcrumbList {
                                BreadcrumbItem("Home", onClick = {}, disabled = false)
                                BreadcrumbSeparator()
                                BreadcrumbItem("Breadcrumb")
                            }
                        }
                    },
                ),
                ComponentExample(
                    title = "Accessible label",
                    code =
                        """
                        Breadcrumb(ariaLabel = "Page path") {
                            BreadcrumbList {
                                BreadcrumbItem("Home", onClick = {})
                                BreadcrumbSeparator()
                                BreadcrumbItem("Breadcrumb")
                            }
                        }
                        """.trimIndent(),
                    preview = {
                        Breadcrumb(ariaLabel = "Page path") {
                            BreadcrumbList {
                                BreadcrumbItem("Home", onClick = {})
                                BreadcrumbSeparator()
                                BreadcrumbItem("Breadcrumb")
                            }
                        }
                    },
                ),
                ComponentExample(
                    title = "Default label",
                    code =
                        """
                        Breadcrumb(ariaLabel = "breadcrumb") {
                            BreadcrumbList {
                                BreadcrumbItem("Home", onClick = {})
                                BreadcrumbSeparator()
                                BreadcrumbItem("Breadcrumb")
                            }
                        }
                        """.trimIndent(),
                    preview = {
                        Breadcrumb(ariaLabel = "breadcrumb") {
                            BreadcrumbList {
                                BreadcrumbItem("Home", onClick = {})
                                BreadcrumbSeparator()
                                BreadcrumbItem("Breadcrumb")
                            }
                        }
                    },
                ),
                ComponentExample(
                    title = "Page",
                    code =
                        """
                        Breadcrumb {
                            BreadcrumbList {
                                BreadcrumbItem("Home", onClick = {})
                                BreadcrumbSeparator()
                                BreadcrumbItem { BreadcrumbPage("Breadcrumb") }
                            }
                        }
                        """.trimIndent(),
                    preview = {
                        Breadcrumb {
                            BreadcrumbList {
                                BreadcrumbItem("Home", onClick = {})
                                BreadcrumbSeparator()
                                BreadcrumbItem { BreadcrumbPage("Breadcrumb") }
                            }
                        }
                    },
                ),
                ComponentExample(
                    title = "Link",
                    code =
                        """
                        Breadcrumb {
                            BreadcrumbList {
                                BreadcrumbItem { BreadcrumbLink("Home", onClick = {}) }
                                BreadcrumbSeparator()
                                BreadcrumbItem { BreadcrumbPage("Breadcrumb") }
                            }
                        }
                        """.trimIndent(),
                    preview = {
                        Breadcrumb {
                            BreadcrumbList {
                                BreadcrumbItem { BreadcrumbLink("Home", onClick = {}) }
                                BreadcrumbSeparator()
                                BreadcrumbItem { BreadcrumbPage("Breadcrumb") }
                            }
                        }
                    },
                ),
                ComponentExample(
                    title = "List",
                    code =
                        """
                        Breadcrumb {
                            BreadcrumbList {
                                BreadcrumbItem("Home", onClick = {})
                                BreadcrumbSeparator()
                                BreadcrumbItem("Breadcrumb")
                            }
                        }
                        """.trimIndent(),
                    preview = {
                        Breadcrumb {
                            BreadcrumbList {
                                BreadcrumbItem("Home", onClick = {})
                                BreadcrumbSeparator()
                                BreadcrumbItem("Breadcrumb")
                            }
                        }
                    },
                ),
                ComponentExample(
                    title = "Separator",
                    code =
                        """
                        Breadcrumb {
                            BreadcrumbList {
                                BreadcrumbItem("Home", onClick = {})
                                BreadcrumbSeparator()
                                BreadcrumbItem("Components", onClick = {})
                                BreadcrumbSeparator()
                                BreadcrumbItem("Breadcrumb")
                            }
                        }
                        """.trimIndent(),
                    preview = {
                        Breadcrumb {
                            BreadcrumbList {
                                BreadcrumbItem("Home", onClick = {})
                                BreadcrumbSeparator()
                                BreadcrumbItem("Components", onClick = {})
                                BreadcrumbSeparator()
                                BreadcrumbItem("Breadcrumb")
                            }
                        }
                    },
                ),
                ComponentExample(
                    title = "Ellipsis",
                    code =
                        """
                        Breadcrumb {
                            BreadcrumbList {
                                BreadcrumbItem("Home", onClick = {})
                                BreadcrumbSeparator()
                                BreadcrumbItem { BreadcrumbEllipsis() }
                                BreadcrumbSeparator()
                                BreadcrumbItem("Breadcrumb")
                            }
                        }
                        """.trimIndent(),
                    preview = {
                        Breadcrumb {
                            BreadcrumbList {
                                BreadcrumbItem("Home", onClick = {})
                                BreadcrumbSeparator()
                                BreadcrumbItem { BreadcrumbEllipsis() }
                                BreadcrumbSeparator()
                                BreadcrumbItem("Breadcrumb")
                            }
                        }
                    },
                ),
                ComponentExample(
                    title = "Item",
                    code =
                        """
                        Breadcrumb {
                            BreadcrumbList {
                                BreadcrumbItem("Home", onClick = {})
                                BreadcrumbSeparator()
                                BreadcrumbItem("Breadcrumb")
                            }
                        }
                        """.trimIndent(),
                    preview = {
                        Breadcrumb {
                            BreadcrumbList {
                                BreadcrumbItem("Home", onClick = {})
                                BreadcrumbSeparator()
                                BreadcrumbItem("Breadcrumb")
                            }
                        }
                    },
                ),
            ),
    )
