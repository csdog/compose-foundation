#!/usr/bin/env python3
import json, os, re, glob, difflib
from collections import defaultdict

MDX = "/tmp/cf-parity/copy-audit/mdx"
LOCAL = "/tmp/cf-parity/copy-audit/local-docs.jsonl"
OUT = "/tmp/cf-parity/copy-audit/findings.json"

NON_RECIPE = {
    "installation", "usage", "composition", "api reference", "anatomy", "examples",
    "accessibility", "theming", "styling", "changelog", "structure", "features",
    "performance", "virtualization", "unstyled", "core concepts", "component",
    "updating to recharts v3", "chart config", "cursor", "sidebarprovider",
    "usesidebar", "props", "width", "keyboard shortcut", "rtl support",
}

def parse_mdx(path):
    text = open(path).read()
    lines = text.splitlines()
    headings = []          # (level, text) in order
    previews = []          # {"heading": nearest heading, "section": h2, "name": ...}
    prose = defaultdict(list)
    current_h2 = None
    current_h3 = None
    in_code = False
    i = 0
    while i < len(lines):
        line = lines[i]
        if line.strip().startswith("```"):
            in_code = not in_code
            i += 1
            continue
        if not in_code:
            m = re.match(r"^##\s+(.+?)\s*$", line)
            if m:
                current_h2 = m.group(1).strip()
                current_h3 = None
                headings.append((2, current_h2))
                prose.setdefault(current_h2, [])
                i += 1
                continue
            m3 = re.match(r"^###\s+(.+?)\s*$", line)
            if m3:
                current_h3 = m3.group(1).strip()
                headings.append((3, current_h3))
                prose.setdefault(current_h3, [])
                i += 1
                continue
            if line.lstrip().startswith("<ComponentPreview"):
                block = [line]
                while ">" not in lines[i] and i + 1 < len(lines):
                    i += 1
                    block.append(lines[i])
                blob = " ".join(block)
                name = re.search(r'name="([^"]+)"', blob)
                previews.append({
                    "heading": current_h3 or current_h2,
                    "section": current_h2,
                    "name": name.group(1) if name else None,
                })
                i += 1
                continue
            target = current_h3 or current_h2
            if target and not line.lstrip().startswith("<") and not line.lstrip().startswith("|"):
                if line.strip():
                    prose[target].append(line)
        i += 1
    return headings, previews, prose

def norm(text):
    t = text or ""
    t = re.sub(r"\[([^\]]*)\]\([^)]*\)", r"\1", t)
    t = re.sub(r"`([^`]*)`", r"\1", t)
    t = re.sub(r"<[^>]+>", " ", t)
    t = re.sub(r"\{[^}]*\}", " ", t)
    t = re.sub(r"[*_#>•]", "", t)
    t = re.sub(r"\s+", " ", t).strip()
    return t

def prose_for(prose, heading):
    raw = " ".join(prose.get(heading, []))
    raw = raw.split("```")[0]
    return norm(raw)

local = {}
for line in open(LOCAL):
    line = line.strip()
    if line:
        d = json.loads(line)
        local[d["id"]] = d

official = {}
for path in glob.glob(MDX + "/*.mdx"):
    cid = os.path.basename(path)[:-4]
    headings, previews, prose = parse_mdx(path)
    official[cid] = {"headings": headings, "previews": previews, "prose": prose}

findings = {
    "unprefixed": [], "description_mismatch": [], "rtl_phrase": [], "examples_heading": [],
    "order": [], "missing_local": [], "no_official": [], "nested_preview_used_as_recipe": [],
}

for cid, doc in sorted(local.items()):
    if cid not in official:
        findings["no_official"].append({"id": cid, "titles": [e["title"] for e in doc["examples"]]})
        continue
    off = official[cid]
    all_headings = [t for _, t in off["headings"]]
    heading_set = set(all_headings)
    # recipe titles = preview headings (h3 nested variants included), in order
    recipe_titles = []
    for p in off["previews"]:
        h = p["heading"]
        if h and h not in recipe_titles:
            recipe_titles.append(h)
    examples = doc["examples"]
    titles = [e["title"] for e in examples]
    hero = titles[0] if titles else None

    for idx, e in enumerate(examples):
        t = e["title"]
        if idx == 0 or t in heading_set or t.startswith("Kotlin extension: "):
            continue
        findings["unprefixed"].append({
            "id": cid, "title": t,
            "official_headings": [h for h in all_headings if h not in NON_RECIPE][:20],
        })

    # order: official heading order for titles present locally (ignore hero)
    local_official = [t for t in titles[1:] if t in heading_set and t.lower() not in NON_RECIPE]
    expected = [h for h in all_headings if h in local_official and h.lower() not in NON_RECIPE]
    if local_official != expected:
        findings["order"].append({"id": cid, "local": local_official, "official_expected": expected, "official_all": all_headings})

    # missing official recipes (previews with no matching local title)
    missing = [h for h in recipe_titles if h not in titles]
    if missing:
        findings["missing_local"].append({"id": cid, "missing": missing})

    # nested (h3) preview variants used as top-level recipes
    nested = [h for h in recipe_titles if h not in [x for _, x in off["headings"] if _ == 2]]
    nested_used = [h for h in nested if h in titles]
    if nested_used:
        findings["nested_preview_used_as_recipe"].append({"id": cid, "nested": nested_used})

    # description comparison
    for e in examples:
        t = e["title"]
        if t not in heading_set:
            continue
        local_desc = norm(e.get("description") or "")
        if not local_desc:
            continue
        off_desc = prose_for(off["prose"], t)
        if not off_desc:
            continue
        ratio = difflib.SequenceMatcher(None, local_desc.lower(), off_desc.lower()).ratio()
        if ratio < 0.92:
            findings["description_mismatch"].append({
                "id": cid, "title": t, "ratio": round(ratio, 3),
                "local": local_desc, "official": off_desc[:400],
            })

    # RTL phrase
    for e in examples:
        raw = e.get("description") or ""
        desc = norm(raw)
        if not desc:
            continue
        if re.search(r"RTL", raw) or "layout direction" in desc.lower():
            if "in shadcn/ui" not in desc:
                findings["rtl_phrase"].append({"id": cid, "title": e["title"], "local": desc})

    if doc.get("examplesHeading") is not None:
        has_examples = any(h.lower() == "examples" for h in all_headings)
        findings["examples_heading"].append({"id": cid, "examplesHeading": doc["examplesHeading"], "official_has_examples_heading": has_examples})

json.dump(findings, open(OUT, "w"), indent=2)
for k, v in findings.items():
    print(k, len(v))
