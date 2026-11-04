"""Freeze Noto's width/weight axes for offline catalog RTL examples (requires fonttools)."""
from pathlib import Path
import argparse
import hashlib
import json
from fontTools.ttLib import TTFont
from fontTools.varLib.instancer import instantiateVariableFont

parser = argparse.ArgumentParser(description=__doc__)
parser.add_argument("--source", type=Path, required=True, help="Directory containing pinned Google Fonts inputs")
parser.add_argument("--output", type=Path, required=True, help="Compose font resource directory")
args = parser.parse_args()
args.output.mkdir(parents=True, exist_ok=True)
results = []
for language, family in [("arabic", "NotoSansArabic"), ("hebrew", "NotoSansHebrew")]:
    source = args.source / f"notosans{language}-{family}[wdth,wght].ttf"
    for weight, label in [(400, "regular"), (500, "medium")]:
        font = TTFont(source, recalcTimestamp=False)
        font = instantiateVariableFont(font, {"wdth": 100, "wght": weight}, inplace=True)
        font.recalcTimestamp = False
        destination = args.output / f"catalog_{language}_{label}.ttf"
        font.save(destination)
        results.append({"file": destination.name, "weight": weight, "width": 100,
                        "source": source.name, "sourceSha256": hashlib.sha256(source.read_bytes()).hexdigest(),
                        "sha256": hashlib.sha256(destination.read_bytes()).hexdigest(),
                        "bytes": destination.stat().st_size})
print(json.dumps(results, indent=2))
