#!/usr/bin/env python3
import json, re
from collections import defaultdict

f = json.load(open("findings.json"))
local = {}
for line in open("local-docs.jsonl"):
    line=line.strip()
    if line:
        d=json.loads(line); local[d["id"]]=d

# refine nested finding using headingLevel
nested = []
for x in f["nested_preview_used_as_recipe"]:
    doc = local[x["id"]]
    bad = []
    for t in x["nested"]:
        ex = next((e for e in doc["examples"] if e["title"] == t), None)
        if ex is None:
            bad.append((t, "absent"))
        elif ex["headingLevel"] > 2 and ex["headingLevel"] != 3:
            bad.append((t, ex["headingLevel"]))
        elif ex["headingLevel"] == 2:
            bad.append((t, 2))
    if bad:
        nested.append({"id": x["id"], "promoted": bad})

# description classification
adap_markers_local = [" = ", "parameter", "Style", "LocalLayoutDirection", "Composable", "slot",
                      "onClick", "href", "Modifier", "remember", "Kotlin", "Icon(", "contentPadding",
                      "Orientation.", "Variant.", "Size.", "Point", "PagerState", "ScrollState"]
adap_markers_off = ["=", "prop", "className", "render", "data-", "aria-", "class", "next/image",
                    "enum", "style", "CSS", "scroll-", "shimmer-", "Recharts", "React", "TanStack"]
cls = []
for x in f["description_mismatch"]:
    lo, of = x["local"], x["official"]
    kind = "paraphrase"
    if any(m in lo for m in adap_markers_local) or any(m in of for m in adap_markers_off):
        kind = "kotlin-api-adaptation"
    # official has substantially more prose than local (missing official content)
    if len(of) > len(lo) * 1.45 and of.lower().startswith(lo.lower()[:20]):
        kind = "missing-official-content"
    cls.append({**x, "kind": kind})

by_kind = defaultdict(list)
for x in cls: by_kind[x["kind"]].append(x)

print("nested promoted:", json.dumps(nested))
print("desc kinds:", {k: len(v) for k, v in by_kind.items()})
for k, v in by_kind.items():
    print("\n##", k)
    comps = defaultdict(list)
    for x in v: comps[x["id"]].append(x["title"])
    for c, ts in sorted(comps.items()): print(" ", c, ts)
json.dump({"nested_promoted": nested, "description_classes": cls}, open("findings_classified.json","w"), indent=2)
