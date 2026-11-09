package com.toutiaoyan.compose.ui.catalog.docs

// Web-framework guidance is retained as source context, separate from the Compose API.
internal val paginationNextJsGuide =
    ComponentGuideSection(
        "Next.js",
        listOf(
            ComponentGuideBlock.Paragraph(
                "The official web `PaginationLink` renders an `<a />` tag. To use Next.js `Link`, " +
                    "update the web component's prop type and rendered element as shown below. " +
                    "This diff is for the React component; Compose navigation uses `onClick` callbacks.",
            ),
            ComponentGuideBlock.Code(
                """
                + import Link from "next/link"
                
                - type PaginationLinkProps = ... & React.ComponentProps<"a">
                + type PaginationLinkProps = ... & React.ComponentProps<typeof Link>
                
                const PaginationLink = ({...props }: ) => (
                  <PaginationItem>
                -   <a>
                +   <Link>
                      // ...
                -   </a>
                +   </Link>
                  </PaginationItem>
                )
                """.trimIndent(),
                "diff",
            ),
            ComponentGuideBlock.Paragraph(
                "**Official CLI note:** The upstream documentation says the CLI is being updated " +
                    "to apply this integration automatically.",
            ),
        ),
    )

internal val paginationChangelogGuides =
    listOf(
        ComponentGuideSection("Changelog", emptyList()),
        ComponentGuideSection(
            "RTL Support",
            listOf(
                ComponentGuideBlock.Paragraph(
                    "When upgrading an older web Pagination component, add the `text` prop to " +
                        "`PaginationPrevious` and `PaginationNext` so labels can be translated. " +
                        "The following source diffs describe the React migration. Compose already " +
                        "accepts `text` and follows `LocalLayoutDirection`; see the RTL example above.",
                ),
                ComponentGuideBlock.Steps(
                    listOf(
                        ComponentGuideBlock.Step(
                            "Update PaginationPrevious.",
                            code =
                                """
                                  function PaginationPrevious({
                                    className,
                                +   text = "Previous",
                                    ...props
                                - }: React.ComponentProps<typeof PaginationLink>) {
                                + }: React.ComponentProps<typeof PaginationLink> & { text?: string }) {
                                    return (
                                      <PaginationLink
                                        aria-label="Go to previous page"
                                        size="default"
                                        className={cn("cn-pagination-previous", className)}
                                        {...props}
                                      >
                                        <ChevronLeftIcon />
                                        <span className="cn-pagination-previous-text hidden sm:block">
                                -         Previous
                                +         {text}
                                        </span>
                                      </PaginationLink>
                                    )
                                  }
                                """.trimIndent(),
                            language = "diff",
                        ),
                        ComponentGuideBlock.Step(
                            "Update PaginationNext.",
                            code =
                                """
                                  function PaginationNext({
                                    className,
                                +   text = "Next",
                                    ...props
                                - }: React.ComponentProps<typeof PaginationLink>) {
                                + }: React.ComponentProps<typeof PaginationLink> & { text?: string }) {
                                    return (
                                      <PaginationLink
                                        aria-label="Go to next page"
                                        size="default"
                                        className={cn("cn-pagination-next", className)}
                                        {...props}
                                      >
                                -       <span className="cn-pagination-next-text hidden sm:block">Next</span>
                                +       <span className="cn-pagination-next-text hidden sm:block">{text}</span>
                                        <ChevronRightIcon />
                                      </PaginationLink>
                                    )
                                  }
                                """.trimIndent(),
                            language = "diff",
                        ),
                    ),
                ),
            ),
            level = 3,
        ),
    )
