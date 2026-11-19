# Catalog shortcut fallback

`catalog-shortcut-symbols.ttf` is a modified Noto Sans Symbols 2 subset that encodes
exactly **U+2318** (⌘). It is used only by the catalog Web app's existing active
Compose font resolver. It does not replace the normal provider, Geist typography,
or the remote fallback downloader.

Source: `google/fonts@8b0a1d0f5983c89bc2b93f1b5fb55f9e252744b5`,
`ofl/notosanssymbols2/NotoSansSymbols2-Regular.ttf`.
- Original SHA-256: `7d5fb73b7ca67a6798101741f5d280a3d016a56a197afcd4199dbb57b4b82a21`
- Subset SHA-256: `b0d02b0edc89f438c9e980d438d97208573e03939fba7cf0bf5f19bdaa1d1fdd`
- Subset size: 6396 bytes.
- License: SIL Open Font License 1.1; copyright/license name records are retained.
  The complete license is shipped as `src/webMain/resources/catalog-shortcut-symbols-OFL.txt`.
- OFL SHA-256: `b118dd41337806a5d4797052c77caf3bd096aed783e5eb21b4d11154351e1ac0`

The subset is renamed **Catalog Shortcut Symbols**, including typographic family
and PostScript names. Its U+2318 outline, advance, units per em and vertical font
metrics are unchanged. Other codepoints are not bundled.

From the repository root, regenerate with the pinned tool environment:

```sh
uv run --no-project --with fonttools==4.60.1 --with brotli==1.1.0 python scripts/regenerate_catalog_shortcut_font.py
```

The script fetches only the pinned source and OFL, checks their hashes, subsets,
renames, checks the final hash/cmap and only then writes both product assets.
Regular Gradle builds do not fetch font sources: they embed the checked-in TTF in
generated Web Kotlin. No runtime font HTTP request or resource base path is needed.
Missing packaged input is required to fail the build. A malformed font is expected
to reach Compose's normal font-load fallback path; this must be proven with an
isolated native-decoder fault build before claiming graceful runtime failure.
