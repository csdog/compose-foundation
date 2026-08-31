# vendor/

Local read-only checkouts used when auditing parity against upstream.

## `shadcn-ui/`

Sparse clone of [shadcn-ui/ui](https://github.com/shadcn-ui/ui) — only the registry
paths this project compares against.

**Alignment target (since 2026-08-21): Base UI** — not legacy `new-york-v4`.

| Path | Purpose |
|---|---|
| `apps/v4/registry/bases/base/ui/*.tsx` | **Primary** component source of truth (Base UI) |
| `apps/v4/registry/bases/base/lib/` | Shared utils for the base registry |
| `apps/v4/registry/styles/` | `style-*.css` theme presets (nova / vega / …; default combo **base-nova**) |
| `apps/v4/styles/` | Global CSS notes |
| `apps/v4/registry/new-york-v4/ui/*.tsx` | Legacy Radix/Vaul registry — historical diff only |
| `apps/v4/registry/new-york-v4/lib/` | Legacy shared utils |

Gitignored (except this README) — clone locally, do not commit the checkout.

### Setup / refresh

```bash
# first time
git clone --depth 1 --filter=blob:none --sparse \
  https://github.com/shadcn-ui/ui.git vendor/shadcn-ui
cd vendor/shadcn-ui
git sparse-checkout set \
  apps/v4/registry/bases/base \
  apps/v4/registry/new-york-v4/ui \
  apps/v4/registry/new-york-v4/lib \
  apps/v4/registry/styles \
  apps/v4/styles

# later updates
cd vendor/shadcn-ui && git pull --ff-only
```

Example: `Drawer` ↔ `vendor/shadcn-ui/apps/v4/registry/bases/base/ui/drawer.tsx`  
Docs: https://ui.shadcn.com/docs/components/base/drawer
