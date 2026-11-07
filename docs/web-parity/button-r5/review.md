# Button r5 review

## Independent review

The first independent pass found no source or test correctness issue. It did
find stale interaction evidence: the official desktop and phone submenu
captures showed the parent menu or closed page, the interaction manifest still
contained the previous phone coordinate, and the review/progress records were
not final.

The captures were regenerated with Escape-reset, selector-driven official
interactions. A fresh read-only evidence check then opened all six key states
and returned PASS:

- official and local desktop Label As submenus are visible;
- official and local phone submenus are visible and flip left;
- local Work remains selected after reopening;
- styled-link pointer focus and activation are visible;
- the obsolete `(299, 450)` metadata is gone;
- all 22 interaction files match their SHA-256 values.

The verifier independently confirmed catalog 24/24, exact Button core 42/42,
the final Wasm build, quadrants, all 120 section captures, source/image hashes,
the Kotlin-extension label and `git diff --check`.

## Final result

No remaining Button source, test or evidence findings. The only residual
comparison differences are the shared documentation shell/font rasterization
and the platform difference between Compose Canvas link semantics and a native
HTML anchor, both recorded in [README.md](README.md).
