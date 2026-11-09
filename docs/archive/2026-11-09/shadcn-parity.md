> 归档快照（2026-11-09）：以下为整理前的历史记录，不代表当前工作树已重新验证。返回[文档入口](../../README.md)。

# compose-foundation vs. real shadcn/ui — parity matrix

This document tracks how closely `compose-foundation`'s tokens and components match the
real [shadcn/ui](https://ui.shadcn.com) they're modeled after. It exists so token and
component drift is a deliberate, tracked decision rather than something that quietly
accumulates. Task-level alignment progress (what's landed each loop tick, remaining
gaps) lives in [`shadcn-align-progress.md`](shadcn-align-progress.md).

**Alignment target (since 2026-08-21): Base UI** — not legacy Radix `new-york-v4`.

**Sources of truth:**
- Token values: [ui.shadcn.com/docs/theming](https://ui.shadcn.com/docs/theming) (the
  officially published default theme — light/dark oklch values and `--radius`).
  Style preset default combo: **base-nova** (`apps/v4/registry/styles/style-nova.css`).
- Component markup/classes / public API: `shadcn-ui/ui` repo,
  **`apps/v4/registry/bases/base/ui/*.tsx`** (docs:
  [ui.shadcn.com/docs/components/base](https://ui.shadcn.com/docs/components/base)).
  Local vendor: see `vendor/README.md`.
- Legacy reference only: `apps/v4/registry/new-york-v4/ui/*.tsx` (Radix/Vaul) — do not
  treat as the alignment target when Base and Radix diverge.
- oklch → sRGB hex conversion done via the standard OKLab matrices (D65, no gamut
  mapping beyond clamping to `[0, 1]`); shown so the comparison is against an actual
  color, not an abstract oklch triple.

Current runtime palettes are `BaseColor` selected by `Theme`; the default family is **Neutral**. The older top-level `LightColors` / `DarkColors` constants are legacy definitions and are not the palette used by the catalog Theme. Do not compare official Neutral against local Zinc and call a hue difference an exact match.

---

## 1. Color tokens

Rechecked 2026-10-30 against the seven official `apps/v4/public/r/colors/<base>.json` files saved in `web-parity/theme-dark-r1-source/`. An independent frozen fixture checks all **434 role values** (31 roles × seven families × two modes). These checks cover base palettes before `Accent.applyTo`; the accent override matrix was separately verified on 2026-10-30 as described below.

The table below uses **Neutral on both sides**. Local values equal the same parsed OKLCH values (including alpha), rather than approximate effective hex colors. Dark translucent roles blend over the actual parent surface; they have no single opaque replacement.

| Role | Official Neutral light / local light | Official Neutral dark / local dark |
|---|---|---|
| `background` | `oklch(1 0 0)` | `oklch(0.145 0 0)` |
| `foreground` | `oklch(0.145 0 0)` | `oklch(0.985 0 0)` |
| `card` | `oklch(1 0 0)` | `oklch(0.205 0 0)` |
| `card-foreground` | `oklch(0.145 0 0)` | `oklch(0.985 0 0)` |
| `popover` | `oklch(1 0 0)` | `oklch(0.205 0 0)` |
| `popover-foreground` | `oklch(0.145 0 0)` | `oklch(0.985 0 0)` |
| `primary` | `oklch(0.205 0 0)` | `oklch(0.922 0 0)` |
| `primary-foreground` | `oklch(0.985 0 0)` | `oklch(0.205 0 0)` |
| `secondary` | `oklch(0.97 0 0)` | `oklch(0.269 0 0)` |
| `secondary-foreground` | `oklch(0.205 0 0)` | `oklch(0.985 0 0)` |
| `muted` | `oklch(0.97 0 0)` | `oklch(0.269 0 0)` |
| `muted-foreground` | `oklch(0.556 0 0)` | `oklch(0.708 0 0)` |
| `accent` | `oklch(0.97 0 0)` | `oklch(0.269 0 0)` |
| `accent-foreground` | `oklch(0.205 0 0)` | `oklch(0.985 0 0)` |
| `destructive` | `oklch(0.577 0.245 27.325)` | `oklch(0.704 0.191 22.216)` |
| `border` | `oklch(0.922 0 0)` | `oklch(1 0 0 / 10%)` |
| `input` | `oklch(0.922 0 0)` | `oklch(1 0 0 / 15%)` |
| `ring` | `oklch(0.708 0 0)` | `oklch(0.556 0 0)` |
| `chart-1` | `oklch(0.87 0 0)` | `oklch(0.87 0 0)` |
| `chart-2` | `oklch(0.556 0 0)` | `oklch(0.556 0 0)` |
| `chart-3` | `oklch(0.439 0 0)` | `oklch(0.439 0 0)` |
| `chart-4` | `oklch(0.371 0 0)` | `oklch(0.371 0 0)` |
| `chart-5` | `oklch(0.269 0 0)` | `oklch(0.269 0 0)` |
| `sidebar` | `oklch(0.985 0 0)` | `oklch(0.205 0 0)` |
| `sidebar-foreground` | `oklch(0.145 0 0)` | `oklch(0.985 0 0)` |
| `sidebar-primary` | `oklch(0.205 0 0)` | `oklch(0.488 0.243 264.376)` |
| `sidebar-primary-foreground` | `oklch(0.985 0 0)` | `oklch(0.985 0 0)` |
| `sidebar-accent` | `oklch(0.97 0 0)` | `oklch(0.269 0 0)` |
| `sidebar-accent-foreground` | `oklch(0.205 0 0)` | `oklch(0.985 0 0)` |
| `sidebar-border` | `oklch(0.922 0 0)` | `oklch(1 0 0 / 10%)` |
| `sidebar-ring` | `oklch(0.708 0 0)` | `oklch(0.556 0 0)` |

All seven dark families use translucent white for `border` / `sidebar-border` (10%) and `input` (15%), their own 900 surface shade and 500 ring shade, and the official blue 700 sidebar primary. Chart roles use each base family's 300/500/600/700/800 ramp in both modes. Light sidebar uses the family's 50 shade.

Nova transparent control fills remain transparent on light surfaces and apply `input/30` on dark Input/Textarea/Checkbox/Radio/Select/InputGroup; unchecked Switch applies dark `input/80`. They are not precomposited over the global background. Relevant controls retain Style-configured focus/error shadows; internal `cssStyleable` uses Compose's native shadow painter with the final shape's interior clipped out, preserving CSS outer-shadow behavior on transparent fills. See `CssStyleableTest` and the theme rendering tests.

`Colors` field names follow CSS role names (`foreground`, `primaryForeground`, `ring`, …). `destructiveForeground` is a local extra role, absent from the 31 upstream base-role fixture; no exact upstream equivalence is claimed for it. Legacy constants and accent overrides must not be inferred correct from this base-role table. Other component state matrices and historical screenshot mismatches remain tracked in `shadcn-web-progress.md`.

---

### Accent overrides (rechecked 2026-10-30)

All 17 non-Base accents now follow [the official theme registry](https://github.com/shadcn-ui/ui/blob/main/apps/v4/registry/themes.ts), saved with a hash in `web-parity/accent-r1/`. The independent fixture contains **375 overrides** across both modes (normally 11 per theme/mode, with an extra dark Rose `sidebar`). Tests apply these over all seven bases and compare every declared and omitted role. `Accent.Base` returns the supplied base unchanged.

Overrides include primary/foreground, secondary/foreground, chart1–5, and sidebar primary/foreground. The focus ring retains the base value. Most primary colors use shade 700 in light mode and 800 in dark mode; Lime/Yellow use 400/500 with shade 900 foregrounds. Charts use 300/500/600/700/800; sidebar primary uses 600/500. Sidebar foregrounds follow each source entry, independently of the primary foreground; dark Rose additionally uses Zinc 900 for its sidebar surface. The fixed Zinc secondary roles are upstream behavior even over a different base.

The old hand-tuned hexadecimal accents and accent-colored focus ring were source mismatches, not deliberate exceptions retained in this pass. Two new rendered matrices cover all 18 options including Base, with actual primary/secondary Buttons and sidebar/chart swatches. These swatches do not prove Sidebar or Chart component behavior. Existing Base-accent screenshot baselines remain unchanged. Create-page layout and independent chart-color controls remain separate gaps.

## 2. Radius tokens

Real shadcn derives its radius scale from a single `--radius: 0.625rem` (10px):
`sm = radius*0.6`, `md = radius*0.8`, `lg = radius` itself, `xl = radius*1.4`,
`2xl = radius*1.8` (verified against `apps/v4/app/globals.css`).

Default **Vega** / **Nova** use the same formula via
`Shapes.fromBaseRadius(baseRadius = 10.dp)` — same anchor as real `--radius`.
Controls that use Tailwind `rounded-md` (8px) read `shapes.md`; `rounded-lg` (10px)
reads `shapes.lg`.

| Scale step | shadcn @ `--radius` 10px | Ours Vega/Nova (`fromBaseRadius(10.dp)`) | Match |
|---|---|---|---|
| `xs` | *(not on `--radius-*`; we use `base*0.4`)* | 4dp | ✅ for hardcoded `rounded-[4px]` (Checkbox) |
| `sm` | 6px | 6dp | ✅ |
| `md` | 8px | 8dp | ✅ |
| `lg` (= `--radius`) | 10px | 10dp | ✅ |
| `xl` | 14px | 14dp | ✅ |
| `2xl` → `xxl` | 18px | 18dp | ✅ |
| `full` | 9999px | 9999dp | ✅ |

`Shapes()` constructor defaults match this same 10px-derived ladder.
Other presets (Maia/Lyra/Mira/…) keep explicit `Shapes(...)` personalities.

| Component | shadcn radius class | shadcn px | We use | Ours (Vega dp) | Match |
|---|---|---|---|---|---|
| Button / Input / Toggle / ToggleGroup / ButtonGroup | `rounded-lg` (nova) | — | `radius.lg` | theme lg | ✅ base-nova |
| Dialog / Alert (panel) | `rounded-xl` (nova) | 12px | `radius.xl` | 12dp | ✅ base-nova |
| Card | `rounded-xl` | 14px | `shapes.xl` | 14dp | ✅ |
| Checkbox | `rounded-[4px]` | 4px | `shapes.xs` | 4dp | ✅ |
| Badge / Chip / Switch track / Radio / Slider thumb | `rounded-full` | 9999px | `shapes.full` | 9999dp | ✅ |

**Update (2026-08-03):** Vega/Nova moved from historical `baseRadius = 8.dp` (kept so
legacy `shapes.lg` call sites still painted 8dp) to real `--radius` **10.dp**, and
`rounded-md` call sites were remapped `shapes.lg` → `shapes.md`. Card remapped
`shapes.xxl` → `shapes.xl` to match real `rounded-xl` (was incorrectly on the
`2xl` step, which jumped 14.4→18 when the base changed).

---

## 3. Spacing tokens

Real shadcn uses Tailwind's default spacing scale unmodified (`0.25rem` = 4px per step).

| Ours (`Spacing`) | Value | Tailwind equivalent | Match |
|---|---|---|---|
| `xxs` | 2dp | `0.5` (2px) | ✅ |
| `xs` | 4dp | `1` (4px) | ✅ |
| `sm` | 8dp | `2` (8px) | ✅ |
| `md` | 12dp | `3` (12px) | ✅ |
| `lg` | 16dp | `4` (16px) | ✅ |
| `xl` | 20dp | `5` (20px) | ✅ |
| `xxl` | 24dp | `6` (24px) | ✅ |
| `xxxl` | 32dp | `8` (32px) | ✅ |

Full match — no action needed here.

---

## 4. Component support matrix

"Validated" means checked directly against the real `.tsx` source this pass, not
approximated from memory.

| Component | Real shadcn surface | Ours | States implemented | Sizing vs. real | Notes |
|---|---|---|---|---|---|
| **Button** | base-nova sizes: default/xs/sm/lg/icon* → `h-8`/`h-6`/`h-7`/`h-9`/`size-8`…; `rounded-lg` | Same ladder: Md/Xs/Sm/Lg/Icon* = 32/24/28/36/32…; focus ring `lg` | hover, focus, disabled | ✅ 24/28/32/36 | ✅ base-nova sizes + radius |
| **Badge** | `default/secondary/destructive/outline/ghost/link`, always reserves a 1px `border-transparent`, has `focus-visible:ring` (for `asChild` link usage) | `Default/Secondary/Destructive/Outline/Ghost` / **Link** (2026-08-20). Ghost stays muted fill (official ghost is transparent until hover on `<a>`). | focus ring when `onClick` set (asChild/link path), link idle | radius (`full`) ✅ | ✅ Link is the official `link` variant; default remains Default |
| **Alert** | `default`/`destructive`; Title / Description / **Action** (top-right); `role="alert"` | same variants + **AlertAction** (2026-08-20). Omit Action to keep Default/Destructive pixels | default, destructive, action | — | ✅ Action is opt-in overlay (`TopEnd`). Official new-york registry JSON still omits AlertAction; radix/docs already have it |
| **Avatar** | `size` default/sm/lg; Image / Fallback / Badge / Group (`-space-x-2` + `ring-2 ring-background`) / **GroupCount** | same sizes + Badge + Group; **AvatarGroupCount**; `spacing` defaults `AvatarGroupDefaults.Overlap` (`-8.dp` + member ring) | fallback, badge, group count, overlap | ✅ 24/32/40dp = `size-6`/`size-8`/`size-10` | ✅ Group overlap is the official default |
| **Card** | `size` `"default" \| "sm"`; `--card-spacing` default `--spacing(4)` / sm `--spacing(3)` for both gap and inset; Header / Title / Description / Action / Content / Footer | same slots; `CardSize.Default/Sm`; gap+inset use `CardDefaults.Spacing` 16.dp / `SmSpacing` 12.dp | — | root padding 16dp ✅ (`spacing.lg`); Sm inset 12dp ✅ | ✅ Default/Sm share one `--card-spacing` token for gap and inset |
| **Chip** | *(not a real shadcn/ui component — shadcn only ships static Badge)* | `Default/Selected/Outline` | hover, pressed, focus (crisp ring), disabled | n/a | documented as our own addition, not a shadcn port |
| **Skeleton** | base-nova `cn-skeleton` + `animate-pulse` → `rounded-md bg-muted` | same (`bg-muted` + pulse) | pulse | — | ✅ base-nova fill |
| **Item** | `variant` default/outline/muted; `size` default/sm/**xs**; Media default/icon/image; Group / Separator / Header / Footer / Actions | same + `ItemSize.Xs` (2026-08-20). Default/Sm padding unchanged. Xs is `12×8` dp inset and `8` dp gap | — | Default `p-4` ✅ | ✅ xs is opt-in. Official registry `item.tsx` still only lists default/sm; docs already advertise xs |
| **Empty** | base-nova `.cn-empty`: Root `gap-4 rounded-xl border-dashed p-6`; Header `gap-2`; Media default transparent / icon `size-8`; Title `text-sm`; Content `gap-2.5`; docs Outline solid `border` | Same; Default draws dashed via `PathEffect`; Outline solid 1dp; `EmptyDefaults.Spacing` 16.dp; radius `xl`; Media Icon 32.dp; Title `BodySmall`; Content 10.dp; `md:p-12` opt-in | outline, spacing, content spacing, title, md padding, rounded-xl, media default, dashed | — | ✅ base-nova + dashed border |
| **Bubble** | `variant` default/secondary/muted/**tinted**/outline/ghost/destructive; `align` start/end; Content / Group / Reactions (`side` top/bottom, `align` start/end) | same slots; variants Default–Destructive + **Tinted**. Reactions **side** / **align** (2026-08-20); default Bottom+End. Tinted is primary wash (`alpha` 0.12 light / 0.3 dark) + foreground text — not a full `oklch(from --primary)` rewrite | variants, alignment, reactions, tinted, reactions top | `max-w-[80%]` ✅ | ✅ Tinted and Reactions `side=Top` are opt-in. Existing `bubble_variants` / conversation goldens omit both |
| **MessageScroller** | Provider / Root / Viewport / Content (`gap-8`) / Item (`scrollAnchor`) / Button (`direction` end/start) | Root still owns scroll + jump button. **Provider** / **Viewport** / **Content** / **Item**; Content `spacing` default 32.dp (`gap-8`); Viewport `aria-label` default `"Messages"`; `preserveScrollOnPrepend` accepted, not ported. Item `scrollAnchor` accepted, not ported. Button **Start** direction rotates the arrow; default End | short, overflow-at-bottom, button, content items, start button, viewport | — | ✅ Viewport is a landmark wrapper, not the scroll container. Turn anchoring / prepend-stability not ported. Start visibility is still the caller’s `visible` |
| **Input / Input** | base-nova `.cn-input`: `h-8` `rounded-lg` `px-2.5 py-1` | `Default/Filled/Ghost`; `InputDefaults.Height` 32.dp; radius `lg` | focus (crisp ring), disabled | ✅ **32dp** = `h-8` | ✅ base-nova height + radius |
| **Checkbox** | single `Checkbox` export; Indicator not exported | Same; `CheckboxIndicator` internal | checked, indeterminate, focus (crisp ring), disabled | ✅ **16dp** = real 16px (`size-4`); outer `minTouchTarget` is hit area only | ✅ Base export; radius `shapes.xs` (4dp) |
| **RadioButton** | `RadioGroup` / `RadioGroupItem` only; Indicator not exported; `size-4` | Same; `RadioGroupIndicator` internal | selected, focus (crisp ring), disabled | ✅ **16dp** = real 16px (`size-4`); outer `minTouchTarget` is hit area only | ✅ Base export; radius `full` |
| **Switch** | single `Switch` export (Thumb inside); `default/sm`; track 32×18.4 / 24×14; thumb 16/12 | Same; `SwitchThumb` internal; `Default`/`Sm` | checked, focus (crisp ring), disabled | ✅ track **32×18dp** / **24×14dp**; ✅ thumb **16dp** / **12dp** | ✅ Base export; radius `full` |
| **Toggle** | base-nova `default/sm/lg` → `h-8`/`h-7`/`h-9` | `Default`/`Outline` × `Sm`/`Md`/`Lg` = 28/32/36 | hover, checked, focus, disabled | ✅ 28/32/36 | ✅ base-nova sizes |
| **Slider** | single `Slider` export; Track/Range/Thumb inside; thumb `size-3`, track `h-1`, thumb `border-ring` | Same; slots internal; `SliderDefaults.ThumbSize` 12.dp / `TrackHeight` 4.dp; thumb border `ring` | hover + focus ring, disabled | ✅ thumb 12dp; ✅ track 4dp | ✅ base-nova sizes; thumb fill `colors.background` (web `bg-white`) |
| **Progress** | base-nova: Track `h-1` `bg-muted`; Indicator `bg-primary`; Label/Value/Track/Indicator exported | Same; `ProgressDefaults.TrackHeight` 4.dp; track `bg-muted`; Label `font-medium` | determinate, indeterminate, label+value | track `h-1` ✅ | ✅ base-nova height + muted track |
| **ToggleGroup** | Radix `ToggleGroup`; 2026-05-17 default `spacing` **2** (8px); strips per-item corners; `focus-visible:z-10` | per-item corners; focused `zIndex(10f)`; composable `spacing` default **8.dp**; `contentDescription` = official `aria-label` | inherits Toggle's states | ✅ corners | ✅ spacing default matches official 8dp |
| **Tabs** | List / Trigger / Content (+ list variants); **no** TabsIndicator export | Same; TabsIndicator internal (List still paints default pill/line) | states, line | — | ✅ Base export; indicator helper internal |
| **Combobox** | Content `sideOffset` **6**, `align` start, `alignOffset` 0; Clear/Icon/Portal/ItemIndicator **not exported** | Same + `alignOffset` 0.dp; slots internal | idle, chips, clear | — | ✅ Base + alignOffset |
| **ButtonGroup** | Official `button-group.tsx`: strips each child's corners/left-border per position; container has **no** own border; `[&>*]:focus-visible:z-10` | `ButtonGroupItem` per-position corners + 1dp Outline overlap; focused Button children `zIndex(10)` (2026-08-20). Mixed non-Item children still skip auto-corners | inherits Button's states | ✅ joined Ghost items | ✅ `focus-visible:z-10` on group Buttons. Flexible mixed children still need caller-supplied outer corners |
| **InputGroup** | Now an official shadcn/ui registry component. Real CSS: the container owns the single border (`InputGroupInput` is `border-0 focus-visible:ring-0 flex-1`), and a `has-[:focus-visible]` selector swaps the *container's* border color and draws the ring around the whole group when the inner input is focused | `InputGroup` owns the single border/shape (its own container style, no contentPadding — the field keeps its variant padding); tracks focus-within via `onFocusEvent { it.hasFocus }` to swap border color and draw a ring (`dropShadow(theme.focusRingShadow())`) on the container; sets an internal `LocalInsideInputGroup` so a nested `Input`/`Textarea` automatically drops its own border/background/ring (no more `variant = Ghost` juggling); field slot wrapped in `weight(1f)` so trailing addons keep intrinsic width | focus-within on container ✅ | ✅ **fixed 2026-07-07** — three real bugs found via catalog screenshots and fixed: (1) inner field drew its own border+ring on focus (double border), (2) trailing addon got squeezed into one-character-per-line vertical text because the field's hardcoded `fillMaxWidth()` ate the row, (3) `BasicTextField` value text/cursor weren't themed (near-invisible in dark mode) — now passed explicitly via `textStyle`/`cursorBrush`, same lesson as `ChipVariant.contentColor`. Verified via `input_group_*` goldens | official InputGroup also has no `variant`. `contentDescription` (aria-label) added 2026-08-20; default null so goldens stay |
| **Textarea** | Not a distinct shadcn/ui primitive — a plain `<textarea>` sharing Input's classes | composition wrapper around `Input`-equivalent styling | inherits Input's states | — | not re-verified against real source this pass |
| **InputOTP** | Active slot is `data-[active=true]:border-ring data-[active=true]:ring-[3px] data-[active=true]:ring-ring/50` (verified against real `input-otp.tsx`) — border *color* swaps to `--ring`, width stays 1px, plus the same shared `ring-[3px] ring-ring/50` every other focusable component draws | `InputOTP`'s active `OtpSlot` | idle, active (crisp shared ring via `dropShadow`) | 36dp slot size, no real counterpart found (real registry doesn't fix a slot size) | ✅ **fixed 2026-07-09** — the active slot previously grew its own border to a hardcoded 2dp and skipped `shadcnFocusRing` entirely: a real parity gap (real shadcn keeps border width at 1px and layers the shared offset ring on top) *and* a real inconsistency (the only component with its own bespoke ring value instead of the shared one). Border width now stays 1dp; `dropShadow(theme.focusRingShadow())` added to a plain conditional in its Style block (no real per-slot focus event to hook a `focused { }` predicate to). Zero screenshot coverage of the active state before this fix (`focused_light`/`focused_dark` added, verified pixel-level: 3px ring at `(208,208,208)` outside a solid `borderFocus`-colored 1dp border, matching every other component) |
| **Questionnaire** | Compound `questionnaire.tsx`: Root `items` collection; Choice `checked`/`onChange`; Progress custom `render`; Error children; Item `invalid` / `onStatusChange` / `status`; Root `item` / `onItemChange`; `shortcuts`; `defaultItem`; opt-in fade+slide; Next may disable via render state; Card / Dialog composition | same + Root `items` (`QuestionnaireItemDefinition`, default empty); Choice `checked`/`onCheckedChange`; ChoiceInput/ChoiceLabel slots (string Choice still has no native radio); `questionnaireState().reset` / `QuestionnaireReset` (Compose helper — Base 未 export Reset; form `type="reset"`); `questionnaireState().status` for Next `enabled`; Progress `text` formatter; Error `text` override; Card slots; QuestionnaireActions is layout-only; Previous/Skip/Next/Submit also work on Root | selected, required error, skip→Skipped, letter/number shortcuts, resume, custom invalid, enter animation, custom progress, Next disabled while unanswered, card shell, dialog overlay, items collection | choice `min-h-11` ✅ | ✅ **added 2026-08-20**. `onStatusChange` skips the initial Unanswered emit (matches official) |
| **Separator** | `h-px w-full` / `h-full w-px`, `bg-border`, `orientation` default horizontal, `decorative` default true | same | — | 1dp ✅ | ✅ re-verified 2026-08-20 against current `separator.tsx` |
| **Dialog** | `showCloseButton` default **true** on Content; Footer `showCloseButton` default **false**; panel base-nova `rounded-xl` `p-4` `bg-popover` `sm:max-w-sm`; overlay `bg-black/50`; zoom-95 | Content close default true; Footer close default false; `radius.xl` + `p-4` + popover; Default width 384dp; fade+zoom-95 | open/close | Sm/Default/Lg | ✅ base-nova panel chrome. Overlay is 50% black, not a token |
| **AlertDialog** | No close X; Action + Cancel; Media slot; Content `size` default/sm; not dismissible on outside click | Same + **AlertDialogMedia**; `size` via DialogSize; opt-in **`AlertDialogDefaults.ShowCloseButton`**; default close false | states, close button | — | ✅ Base Media exported. Overlay capture is full-window |
| **Command** | Dialog/Input/List/Empty/Group/Item/Shortcut/Separator; Dialog `showCloseButton` false; **no** Footer/Loading export | Same core slots; Footer/Loading/GroupHeading kept as Compose helpers; Dialog close default false | states | — | ✅ Base core; helpers extra |
| **Sheet** | Trigger/Close/Content/Header/Footer/Title/Description; **no** Portal/Overlay export; Content `side` right + `showCloseButton` true | Same; Portal/Overlay internal; `showCloseButton` default true | open/close, official close | — | ✅ Base export surface |
| **Drawer** | Base UI / base-nova: `swipeDirection` up/right/down/left (default down); `showSwipeHandle` default **false** + `DrawerSwipeHandle`; `snapPoints` / `snapPoint` / `onSnapPointChange` / `snapToSequentialPoints`; `disablePointerDismissal`; nested stack; Portal/Overlay exported; web Trigger/Close use `render={<Button/>}`; Popup `bg-popover` `rounded-*-xl`; Header `p-4 pb-0`; Footer `p-4 pt-0`; Content unpadded (body uses caller `p-4`) | Same Root params; Content paints handle when `showSwipeHandle`; snap sheet for vertical; nested parent scale/peek; side `min(75%, 384.dp)` / vertical `maxH − 96.dp`; Compose Trigger/Close are **passthrough children** (caller wires `toggle`/`close` on Button — no outer clickable); panel `radius.xl` + popover; Header/Footer inset match official | bottom (+handle), top, end, edit profile, snap, nested | — | ✅ base-nova surface (not Vaul). Indent / Provider / SwipeArea / VirtualKeyboardProvider not exported (not in base-nova exports). `snapToSequentialPoints` accepted; settle still uses anchored nearest by default |
| **Popover** | Trigger / Content / Header / Title / Description; Content `align` center, `sideOffset` 4, `alignOffset` **0** | Same; `alignOffset` 0.dp; Portal/Anchor internal | dimensions form, states | — | ✅ Base + alignOffset |
| **Menubar** | Content `sideOffset` **8**, `alignOffset` **-4**; ItemIndicator not exported | Same; `alignOffset` (-4).dp; ItemIndicator internal | states | — | ✅ Base alignOffset |
| **ContextMenu** | Content `sideOffset` 0, `alignOffset` **4** (cursor); SubContent wraps Content (`alignOffset` 4); ItemIndicator not exported | Same; cursor nudge `alignOffset` 4.dp; SubContent `alignOffset` 4.dp (not Dropdown’s -3); ItemIndicator internal | states | — | ✅ Base alignOffset |
| **DropdownMenu** | Content `sideOffset` 4, `alignOffset` 0; SubContent `sideOffset` **0**, `alignOffset` **-3**; ItemIndicator not exported | Same; SubContent `alignOffset` (-3).dp | states | — | ✅ Base SubContent alignOffset |
| **Select** | Portal/Viewport/Icon/ItemText/ItemIndicator **not exported**; Trigger always chevron; Trigger `h-8` / sm `h-7` `rounded-lg`; Content `alignItemWithTrigger` + `sideOffset` 4 + `alignOffset` 0 | Same; `SelectDefaults.TriggerHeight` 32.dp / Sm 28.dp; radius `lg`; `alignOffset` 0.dp | closed, placeholder, align-item-with-trigger (open) | ✅ trigger h-8 | ✅ base-nova trigger size |
| **HoverCard** | Root / Trigger / Content only; Content `sideOffset` 4, `align` center, `alignOffset` **4** | Same; Portal internal; Header/Title/Description Compose helpers; `alignOffset` default 4.dp | states | — | ✅ Base Core + alignOffset |
| **NavigationMenu** | Positioner; Content `sideOffset` **8**, `alignOffset` 0 | `NavigationMenuPositioner`; Content `sideOffset` 8.dp + `alignOffset` 0.dp; Viewport deprecated | states | — | ✅ Base Positioner + alignOffset |
| **Accordion** | Item / Trigger / Content; Header inside Trigger; ChevronDown/Up show-hide (not exported) | Same public set; Header/ItemIndicator internal; default icon **ChevronDown ↔ ChevronUp** (Base) | states | — | ✅ Base export + dual chevron |
| **Collapsible** | Trigger / Content only | Same; Header internal | states | — | ✅ Base export surface |
| **Tooltip** | Content `sideOffset` **4**, `align` center, `alignOffset` **0**; Arrow inside; Provider delay 0 | Same; `align` Center + `alignOffset` 0.dp; Portal/Arrow internal | trigger, arrow, side offset 4 | — | ✅ Base align + alignOffset |
| **ScrollArea** | Export `ScrollArea` + `ScrollBar` only; Viewport/Corner/Thumb inside Root | Same public set; Viewport/Corner internal (Root still paints focus ring + bars) | hover/scroll/always/auto hide | thumb 10dp track (= `w-2.5`) | ✅ Base export; type/scrollHideDelay match |
| **Calendar** | Export `Calendar` + `CalendarDayButton`; react-day-picker slots otherwise internal | Same core exports + compose Header/Grid/… helpers; **`CalendarDayButton` + `CalendarCell` public** | selected, range, today, outside, disabled, dropdown caption (closed) | — | ✅ DayButton public (Base `components.DayButton` surface). Caption/range ✅ |
| **Carousel** | embla: `orientation` default horizontal; root ArrowLeft/ArrowRight; Previous/Next `variant=outline` `size=icon` `rounded-full`; `useCarousel()`; no dots | HorizontalPager/VerticalPager; same key + button defaults; `carouselApi()`; **CarouselDots is extra** | swipe, prev/next disabled at ends, keyboard | Previous/Next 32dp circle (`size-8`) vs official `size-8` ✅ | ✅ re-verified 2026-08-20. Official `opts`/`plugins`/`setApi` are embla-only — not ported |
| **Sidebar** | Provider `defaultOpen=true`; `collapsible` offcanvas/icon/none; width `16rem` (256px); icon rail `3rem` (48px); Cmd/Ctrl+B; cookie; mobile Sheet; `variant` sidebar/floating/inset | `defaultOpen=true`; `collapsible` Offcanvas/Icon/None; `width` 256.dp; `iconWidth` 48.dp; `sidebarState().iconCollapsed`; `variant` Sidebar/Floating/Inset; Provider `shortcutEnabled`/`shortcutKey`; `mobile` opt-in Sheet at `MobileWidth` 288.dp (`18rem`); `onPersistOpen` cookie hook — no `document.cookie` | open/close, icon rail, menu active, tooltip when icon-collapsed, Cmd/Ctrl+B, floating card, inset shell, mobile Sheet, 16rem width | ✅ default width 256 = `16rem` | Mobile Sheet uses 18rem. Persistence is a callback |
| **Chart** | Recharts wrappers: `ChartContainer`+`config`, `ChartStyle` CSS vars, `ChartTooltip`/`ChartTooltipContent` (`indicator` dot/line/dashed, `hideLabel`/`hideIndicator`), `ChartLegend`/`ChartLegendContent` (`hideIcon`, `verticalAlign` default bottom); `useChart()`; no Bar/Line primitives; Container `initialDimension` `{ width: 320, height: 200 }` | `ChartContainer` (aspect 16:9), `ChartStyle`, `chartConfig()`, Legend(+Content), Tooltip(+Content) `indicator` Dot/Line/Dashed; **Canvas `BarChart`/`LineChart` are ours** (no Recharts). Legend `verticalAlign` default Bottom; Container `initialDimension` opt-in 320×200 (Recharts measure hint, not a layout token) | legend, tooltip host, legend bottom (`pt-3`), official size | Container `aspect-video` ✅ | ⚠️ drawing backend is Canvas, not Recharts. Tooltip overlay is a slot, not Recharts hover. `initialDimension` stays opt-in fixed size |
| **Resizable** | `ResizablePanelGroup` / Panel / Handle; Handle `withHandle` omitted (false); `orientation` | Two-pane fraction split (no react-resizable-panels). Handle `withHandle` default false (hairline) | even split, sidebar, without handle | — | ✅ grip is opt-in via `withHandle = true`. Drag math is unit-tested; screenshots use fixed `initialFraction` |

---

## 5. Methodology notes

- oklch → hex conversion: OKLab is computed from `(L, C, H)`, converted to linear LMS
  via the standard matrices, cubed, converted to linear sRGB, then gamma-encoded
  (`c ≤ 0.0031308 → 12.92c`, else `1.055·c^(1/2.4) − 0.055`), rounded to the nearest
  integer per channel. No perceptual gamut mapping is applied.
- "Match" (✅) means the values are within rounding/anti-aliasing tolerance (roughly
  ≤2 per 8-bit channel, or ≤1 `dp`/`px` step). "⚠️" means visually close but a real,
  intentional-looking difference. "❌" means a real, actionable mismatch.
- This document reflects a single point-in-time comparison (commit `d8ace42`,
  2026-07-06). Re-run the same fetch against the current shadcn-ui/ui commit before
  trusting it for a release — shadcn/ui's own defaults do change between versions.
- Updated 2026-07-07: color/radius fixes verified as landed; ToggleGroup re-verified
  against real source; ButtonGroup and InputGroup re-verified and found to have real,
  unaddressed gaps (see §4). Confirmed ButtonGroup and InputGroup are now official
  shadcn/ui registry components, not "not distinct primitives" as originally noted here.
- Updated 2026-08-03: §1 now includes `card`/`popover`/`sidebar*` rows from zinc.json;
  default palette's container-role fields are named `TwColors` shades (not raw
  `Oklch(...).toColor()`). Prefer `TwColors.*` when the shade is byte-identical; keep
  hex only when a scratch ARGB compare against the full TwColors table fails (current
  historical exceptions: `success`, `warning`, `destructiveHover`, and `Accent.kt`;
  the Accent exception was superseded by the exact official-source correction on 2026-10-30).
  §2: Vega/Nova now use `Shapes.fromBaseRadius(10.dp)` (real `--radius`);
  `rounded-md` call sites use `shapes.md`. Earlier the same day this section still
  documented the historical `baseRadius = 8.dp` workaround.
- Updated 2026-08-04: Switch gained `SwitchSize.Sm` (24×14 / thumb 12); Toggle gained
  `ToggleSize.Sm`/`Md`/`Lg` (32/36/40) matching real toggle.tsx; ToggleGroup forwards size.
- Updated 2026-08-04: Button `Xs` → 24dp (= real `h-6`); Badge reserves transparent
  1dp border + focus ring via optional `onClick`; dark zinc `sidebarPrimary` =
  `Oklch(0.488, 0.243, 264.376).toColor()` on `BaseColor.Zinc.dark` only
  (`DarkColors` stays aliased to primary).

---

## 6. `StylePreset` (Vega/Nova/...) vs. the old Default/New York split

Real shadcn/ui's `npx shadcn create` replaced the old two-style choice (`default` /
`new-york`) with a larger named set. Researched 2026-07-07:

- **Vega** = the classic look, i.e. what `default` used to mean.
- **Nova** = reduced padding/margins for compact layouts — this is the direct successor
  to `new-york` (which historically also meant "tighter/more compact") and is now
  shadcn's own new default.
- **Maia**, **Luma** = soft, generously rounded, relaxed spacing.
- **Lyra** = zero border radius, boxy, pairs with monospace fonts.
- **Mira** = the most compact option, for dense/data-table UIs.
- **Sera** = editorial/typographic (distinct font pairing in real shadcn, which we
  approximate with just a type-scale bump since we don't ship alternate font assets).
- **Rhea** = newest (May 2026); in real shadcn this adjusts size/gap/density directly
  without changing what Tailwind's spacing utilities mean elsewhere in the app — a
  distinction that doesn't apply to us since we don't generate Tailwind-scale utility
  classes at all.
- Legacy `default`/`new-york`/`new-york-v4` values now resolve to `radix-vega` in real
  shadcn for backward compatibility.

**`Ring` genuinely does vary per preset -- two separate findings, two corrections.**

*Finding 1 (2026-07-08):* an earlier session wired `Vega -> Ring.Default` and
`Nova -> Ring.NewYork`, analogized from real shadcn's *retired* two-style
`shadcn init` picker (`Default`/`New York`), which real shadcn now collapses to
`radix-vega` for back-compat -- not something the current 8-style system exposes at
all, and only applied to 2 of 8 presets besides. Reverted: `ring` became a flat,
preset-independent `Theme(ring = ...)` parameter, and `Ring.Default`/
`.NewYork` were deleted outright (zero real callers anywhere in the codebase, no
catalog UI path to reach them either).

*Finding 2 (2026-07-09):* that revert was **also wrong**, just in the opposite
direction -- it assumed ring doesn't vary by style at all, without ever checking the
*current* 8-style system's own real CSS. It does vary. Verified directly against
`shadcn-ui/ui`'s `apps/v4/registry/styles/style-<name>.css` files (not the retired
axis, not an analogy -- each style's own `.cn-button` rule, checked file by file):

| Style | ring width | ring opacity |
|---|---|---|
| Vega | 3px | 50% |
| Nova | 3px | 50% |
| Maia | 3px | 50% |
| Lyra | 1px | 50% |
| Mira | 2px | 30% |
| Luma | 3px | 30% |
| Sera | 2px | 30% |
| Rhea | 3px | 30% |

`ring-offset-*` appears in none of the 8 files -- offset stays 0 for every style, real
and ours. `StylePreset` now carries these real per-style values via a `ring:
Ring` field again (same shape as the Finding-1 revert undid, but populated from
verified source this time, not a cross-axis analogy). `Theme(ring = preset.ring)`
by default -- still independently overridable, same pattern as `baseRadius`.
**Lesson generalized:** "we checked and it doesn't vary" is only trustworthy if the
check was against the *actual axis in question* -- Finding 1's revert checked the
retired two-style system (irrelevant) and concluded the current system was flat without
ever looking at the current system's own files.

---

## 7. Focus ring implementation: `Modifier.shadcnFocusRing` retired in favor of `dropShadow`

Removed entirely (2026-07-09). The custom `Modifier.drawWithContent` + manual per-corner
`RoundRect` reimplementation (~90 lines, `styles/FocusRing.kt`) was justified by a claim
that the Style API's `dropShadow` "always rasterizes through an offscreen bitmap...
visibly blurs a ring this thin," even at `radius = 0.dp`. That claim was never actually
tested — checked directly this session via a screenshot test of
`dropShadow(radius = 0.dp, spread = 3.dp)`: perfectly crisp (only ordinary single-pixel
edge anti-aliasing, the same any vector draw call has), zero effect on the element's
measured layout size, exactly matching real CSS `box-shadow`. The custom modifier was
solving a problem that didn't exist.

Every focusable component now calls `dropShadow(theme.focusRingShadow())` directly
inside its own `focused { }` block (`ThemeData.focusRingShadow()` in
`styles/FocusRing.kt` builds the `Shadow` value: `radius = 0.dp`,
`spread = ring.width + ring.offset`, color = `colors.ring` at `ring.opacity`).
Components whose "active" state is a *computed* boolean rather than a real per-node
focus event (`InputOTP`'s `OtpSlot`, `InputGroup`'s `hasFocusWithin`,
`StylePresetMatrixTest`'s ring swatch) use a plain `if (isActive) dropShadow(...)`
inside the `Style { }` body instead, since there's no real `InteractionSource` to drive
a `focused { }` predicate.

`dropShadow` always follows the Style's own **final resolved** `shape()` — including a
later `style`-parameter override from `ButtonGroup`/`ToggleGroup` (their
per-position corner-stripped shape, merged in via `.styleable(...)`'s
override-not-additive cascade) — so `Button`/`Toggle` need no special-case
shape handling at all; the group's own corner-stripping "just works" for the ring too.
Verified via `ButtonGroupScreenshotTest.with_label_focused`: the ring appears only at
the outer rounded ends, never at the internal flush seam between grouped items —
pixel-checked directly (`(229,229,229)` ring band at the outer edge, no separate band at
the seam). The obsolete `LocalGroupCorners` provider and its readers have since
been removed; no cleanup remains for that mechanism (source checked 2026-10-28).

**Lesson generalized:** the same failure mode as §6 — a specific, falsifiable technical
claim ("dropShadow blurs at radius=0") was carried forward across sessions and cited as
justification for real code complexity, without ever being tested. Confirmed with a
one-off screenshot test, in under a minute, once someone actually asked "why not just
use dropShadow?"
