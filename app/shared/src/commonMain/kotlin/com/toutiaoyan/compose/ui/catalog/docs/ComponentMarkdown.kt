package com.toutiaoyan.compose.ui.catalog.docs

/** The exact payload used by Copy Page, kept separate from preview composition. */
internal fun componentPageMarkdown(doc: ComponentDoc): String =
    buildString {
        appendLine("# ${doc.title}\n\n${doc.description}\n")

        fun example(example: ComponentExample) {
            if (example.showHeading) appendLine("${"#".repeat(example.headingLevel)} ${example.title}\n")
            example.descriptionMarkdown?.let { appendLine("$it\n") }
            appendLine("```kotlin\n${example.code}\n```\n")
        }
        doc.examples.firstOrNull()?.let(::example)
        appendLine("## Installation\n\n```kotlin\n$catalogInstallationCode\n```\n")
        appendLine("## Usage\n")
        doc.usageImportsCode?.let { appendLine("```kotlin\n$it\n```\n") }
        appendLine("```kotlin\n${doc.usageCode}\n```\n")
        doc.compositionCode?.let { appendLine("## Composition\n\n```text\n$it\n```\n") }

        fun guide(section: ComponentGuideSection) {
            appendLine("${"#".repeat(section.level)} ${section.title}\n")
            section.blocks.forEach { block ->
                when (block) {
                    is ComponentGuideBlock.Paragraph -> appendLine("${block.markdown}\n")
                    is ComponentGuideBlock.Bullets -> {
                        block.items.forEach { appendLine("- $it") }
                        appendLine()
                    }
                    is ComponentGuideBlock.Code -> appendLine("```${block.language.orEmpty()}\n${block.code}\n```\n")
                }
            }
        }
        doc.beforeExamples.forEach(::guide)
        val additionalExamples = doc.examples.drop(1)
        if (additionalExamples.isNotEmpty()) {
            doc.examplesHeading?.let { appendLine("## $it\n") }
            additionalExamples.forEach(::example)
        }
        doc.afterExamples.forEach(::guide)
        if (doc.apiProperties.isNotEmpty() || doc.apiSections.isNotEmpty()) {
            appendLine("## API Reference\n")

            fun properties(rows: List<ApiProperty>) {
                appendLine("| Property | Type | Default |\n| --- | --- | --- |")
                rows.forEach { row ->
                    fun cell(value: String) = value.replace("|", "\\|").replace("\n", " ")
                    appendLine("| `${cell(row.name)}` | `${cell(row.type)}` | `${cell(row.defaultValue)}` |")
                }
                appendLine()
            }
            if (doc.apiProperties.isNotEmpty()) properties(doc.apiProperties)
            doc.apiSections.forEach { section ->
                appendLine("### ${section.title}\n\n${section.descriptionMarkdown}\n")
                properties(section.properties)
            }
        }
    }
