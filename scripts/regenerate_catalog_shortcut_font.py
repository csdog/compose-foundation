#!/usr/bin/env python3
"""Rebuild the licensed, single-codepoint catalog shortcut fallback."""
from hashlib import sha256
from io import BytesIO
from pathlib import Path
from urllib.request import urlopen

import fontTools
from fontTools import subset
from fontTools.ttLib import TTFont

REVISION = "8b0a1d0f5983c89bc2b93f1b5fb55f9e252744b5"
BASE_URL = f"https://raw.githubusercontent.com/google/fonts/{REVISION}/ofl/notosanssymbols2"
FONT_SHA256 = "7d5fb73b7ca67a6798101741f5d280a3d016a56a197afcd4199dbb57b4b82a21"
LICENSE_SHA256 = "b118dd41337806a5d4797052c77caf3bd096aed783e5eb21b4d11154351e1ac0"
SUBSET_SHA256 = "b0d02b0edc89f438c9e980d438d97208573e03939fba7cf0bf5f19bdaa1d1fdd"
ROOT = Path(__file__).resolve().parents[1]


def read_pinned(name: str, expected: str) -> bytes:
    with urlopen(f"{BASE_URL}/{name}") as response:
        data = response.read()
    if sha256(data).hexdigest() != expected:
        raise ValueError(f"Pinned source hash mismatch: {name}")
    return data


def main() -> None:
    if fontTools.__version__ != "4.60.1":
        raise ValueError("Use the documented fonttools==4.60.1 environment")
    font = TTFont(BytesIO(read_pinned("NotoSansSymbols2-Regular.ttf", FONT_SHA256)))
    license_bytes = read_pinned("OFL.txt", LICENSE_SHA256)
    options = subset.Options()
    options.name_IDs = ["*"]
    options.name_legacy = True
    options.name_languages = ["*"]
    options.notdef_glyph = True
    options.notdef_outline = True
    options.recalc_timestamp = False
    subsetter = subset.Subsetter(options=options)
    subsetter.populate(unicodes=[0x2318])
    subsetter.subset(font)
    replacements = {
        1: "Catalog Shortcut Symbols",
        2: "Regular",
        3: "CatalogShortcutSymbols-2318-v1",
        4: "Catalog Shortcut Symbols Regular",
        6: "CatalogShortcutSymbols-Regular",
        16: "Catalog Shortcut Symbols",
        17: "Regular",
    }
    for record in font["name"].names:
        if record.nameID in replacements:
            record.string = replacements[record.nameID].encode(record.getEncoding())
    font.recalcTimestamp = False
    output = BytesIO()
    font.save(output)
    data = output.getvalue()
    if sha256(data).hexdigest() != SUBSET_SHA256:
        raise ValueError("Subset hash mismatch; no product files written")
    if set(TTFont(BytesIO(data)).getBestCmap()) != {0x2318}:
        raise ValueError("Subset must encode exactly U+2318")
    font_path = ROOT / "app/webApp/fonts/catalog-shortcut-symbols.ttf"
    license_path = ROOT / "app/webApp/src/webMain/resources/catalog-shortcut-symbols-OFL.txt"
    font_path.parent.mkdir(parents=True, exist_ok=True)
    license_path.parent.mkdir(parents=True, exist_ok=True)
    font_path.write_bytes(data)
    license_path.write_bytes(license_bytes)
    print(f"{len(data)} bytes {SUBSET_SHA256} {font_path}")


if __name__ == "__main__":
    main()
