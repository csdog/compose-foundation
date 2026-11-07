# Independent review

Reviewer finding: **No findings.**

Checks reported by the independent reviewer:

- Required `ratio` matches the pinned Base UI source.
- All repository call sites provide `ratio`.
- Invalid zero, negative, NaN and infinity values have focused coverage.
- API documentation and assertions consistently report `Required`.
- Credited test evidence passes: catalog 6/6 and core 5/5.
- Browser evidence covers Square, Portrait and RTL across 1440/390 and
  light/dark, plus Arabic/English/Hebrew interaction.
- Owning ktlint reports contain no Aspect Ratio file violations; failures are
  from unrelated pre-existing files.
- `git diff --check` passes.

The public signature change was accepted as intentional for the unreleased
pre-release library line.
