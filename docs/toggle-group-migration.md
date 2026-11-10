# Toggle Group migration: 0.3.40

The Set-valued `ToggleGroup` overloads now default to `ToggleGroupType.Single`,
matching Base UI's `multiple=false`. Selecting a new item replaces the selection;
selecting the active item clears it. String-valued overloads remain single selection.

This is an intentional breaking default-behavior change from 0.3.39. Existing
signatures, positional parameters and trailing content lambdas remain available.
To retain the previous independent membership behavior, add an explicit type:

```kotlin
ToggleGroup(
    value = selected,
    onValueChange = { selected = it },
    type = ToggleGroupType.Multiple,
) {
    ToggleGroupItem("bold", "Bold")
    ToggleGroupItem("italic", "Italic")
}
```

Apply this to controlled and uncontrolled Set roots, including loop-focus and
cancellable overloads. Keep multi-item `value`/`defaultValue` in Multiple mode;
Single callers should supply at most one selected identity. This change does not
normalize caller-owned initial selection. The catalog's Default, Vertical and
Connected recipes explicitly retain Multiple mode.

Item identities must also be nonempty and unique within each group. Invalid item
values now throw `IllegalArgumentException` instead of sharing navigation state.
The empty String remains the String root sentinel for no selection. Empty groups
are supported. For an empty visible label, supply a nonempty `label` as the
accessible name and an empty custom content slot. Long labels keep their full
accessible name; callers can constrain item width for clipping.
