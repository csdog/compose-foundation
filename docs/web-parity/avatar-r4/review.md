# Independent review

## Findings resolved

The first review found that mixed groups should not always use the largest
member. The official CSS gives `sm` precedence:

- `sm` -> 24dp
- `default` -> 32dp
- `lg` -> 40dp
- any mixed group containing `sm` -> 24dp

`AvatarGroupState` was changed to `any Sm -> Sm; else any Lg -> Lg; else
Default`, and a mixed Sm + Default + Lg test plus golden was added. The API
documentation and KDoc were corrected as part of the same fix.

## Final review

Reviewer result: **No findings.**

- Mixed-size precedence matches the archived official CSS.
- Dynamic member sizing and explicit Count overrides are covered.
- Unconditional group-member rings, Dropdown keyboard/focus behavior and RTL
  evidence are consistent with the source.
- Final test and browser evidence is current.
