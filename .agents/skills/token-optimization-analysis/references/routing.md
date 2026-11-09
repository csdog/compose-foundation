# Cheap Subagent Routing

Use subagents to isolate expensive observation and tool execution, not to avoid decisions.
Count the subagent's startup and result tokens; routing is only a win when total work falls.

## Route Selection

| Work | Route | Return contract |
| --- | --- | --- |
| Search many files, map call sites, summarize logs/docs | cheap read-only scout | answer, `path:line` evidence, unresolved questions |
| Run tests/build/lint and classify failures | cheap runner | commands, exit codes, failing tests, first actionable error |
| One or two targeted file reads | main agent | no subagent |
| Architecture, public API, security, ambiguous product choice | main agent | no cheap delegation |
| Isolated implementation with a complete contract | main or capable worker | diff plus verification evidence |

Batch read-only questions into one scout. Do not start one subagent per file or per command.

## Scout Prompt

```text
Read-only investigation. Do not edit files.
Question: <one precise question>
Scope: <paths/globs>
Use rg/LSP first and retrieve only relevant ranges.
Return at most 30 lines:
- answer
- evidence as path:line
- relevant symbols
- unresolved questions
Do not return full file contents or the search transcript.
```

## Runner Prompt

```text
Run only these commands from <cwd>:
<exact commands>
Do not edit files or broaden the test scope.
Store full logs under <artifact path>.
Return at most 40 lines:
- command and exit code
- failing tests
- primary error signature
- log path
Omit successful command output unless it proves the requested result.
```

## Escalation

Return to the main agent when:

- the task requires changing code or configuration;
- evidence conflicts with the requested design;
- a security or destructive-action decision appears;
- the scout cannot find a primary error after one focused retry;
- the combined subagent overhead exceeds the context that would have entered the main task.
