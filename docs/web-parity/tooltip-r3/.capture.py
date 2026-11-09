#!/opt/homebrew/opt/python@3.13/bin/python3.13
from __future__ import annotations

import hashlib
import json
import mimetypes
import os
import shutil
import threading
from datetime import datetime, timezone
from http.server import SimpleHTTPRequestHandler, ThreadingHTTPServer
from pathlib import Path
from urllib.parse import urlsplit

from PIL import Image, ImageChops, ImageDraw
from playwright.sync_api import Browser, BrowserContext, Page, sync_playwright


ROOT = Path(__file__).resolve().parents[3]
DIST = ROOT / "app/webApp/build/dist/wasmJs/productionExecutable"
OUT = Path(__file__).resolve().parent
SHOTS = OUT / "screenshots"
OFFICIAL_URL = "https://ui.shadcn.com/docs/components/base/tooltip"
THEME_KEY = "compose-foundation.theme"
VIEWS = (
    ("desktop-light", 1440, 1000, "light"),
    ("desktop-dark", 1440, 1000, "dark"),
    ("phone-light", 390, 844, "light"),
    ("phone-dark", 390, 844, "dark"),
)


class SpaHandler(SimpleHTTPRequestHandler):
    def __init__(self, *args, **kwargs):
        super().__init__(*args, directory=str(DIST), **kwargs)

    def do_GET(self):
        path = urlsplit(self.path).path
        target = DIST / path.lstrip("/")
        if path != "/" and not target.exists():
            self.path = "/index.html"
        return super().do_GET()

    def log_message(self, fmt, *args):
        return


def sha256(path: Path) -> str:
    digest = hashlib.sha256()
    with path.open("rb") as handle:
        for chunk in iter(lambda: handle.read(1024 * 1024), b""):
            digest.update(chunk)
    return digest.hexdigest()


def file_meta(path: Path) -> dict:
    with Image.open(path) as image:
        size = list(image.size)
    return {"path": str(path), "sha256": sha256(path), "pixels": size}


def changed_pixels(left: Path, right: Path, box: tuple[int, int, int, int]) -> int:
    with Image.open(left) as a, Image.open(right) as b:
        region_a = a.crop(box).convert("RGB")
        region_b = b.crop(box).convert("RGB")
    difference = ImageChops.difference(region_a, region_b)
    return sum(1 for pixel in difference.getdata() if pixel != (0, 0, 0))


def wait_for_local(page: Page):
    page.wait_for_function(
        """() => {
          const root = document.getElementById('compose-root');
          const walk = scope => {
            if (!scope) return null;
            const own = scope.querySelector && scope.querySelector('canvas');
            if (own) return own;
            for (const node of scope.querySelectorAll ? scope.querySelectorAll('*') : []) {
              if (node.shadowRoot) {
                const nested = walk(node.shadowRoot);
                if (nested) return nested;
              }
            }
            return null;
          };
          const canvas = walk(root);
          return !!canvas && canvas.width > 0 && canvas.height > 0 &&
            document.body.style.height.includes('calc');
        }""",
        timeout=60000,
    )
    page.wait_for_timeout(1200)


def load_theme(page: Page, url: str, theme: str, width: int, height: int):
    page.set_viewport_size({"width": width, "height": height})
    page.goto(url, wait_until="domcontentloaded", timeout=90000)
    page.evaluate(
        """theme => {
          localStorage.setItem('compose-foundation.theme', theme);
        }""",
        theme,
    )
    page.reload(wait_until="domcontentloaded", timeout=90000)
    wait_for_local(page)


def canvas_meta(page: Page) -> dict:
    return page.evaluate(
        """() => {
          const root = document.getElementById('compose-root');
          const walk = scope => {
            if (!scope) return null;
            const own = scope.querySelector && scope.querySelector('canvas');
            if (own) return own;
            for (const node of scope.querySelectorAll ? scope.querySelectorAll('*') : []) {
              if (node.shadowRoot) {
                const nested = walk(node.shadowRoot);
                if (nested) return nested;
              }
            }
            return null;
          };
          const canvas = walk(root);
          if (!canvas) return null;
          const rect = canvas.getBoundingClientRect();
          return {
            rootRect: root.getBoundingClientRect().toJSON(),
            canvasRect: rect.toJSON(),
            canvasBitmap: {width: canvas.width, height: canvas.height},
            devicePixelRatio,
            theme: localStorage.getItem('compose-foundation.theme'),
            bodyHeight: document.body.style.height,
            clientWidth: document.documentElement.clientWidth,
            direction: getComputedStyle(document.documentElement).direction,
            wasm: performance.getEntriesByType('resource')
              .filter(entry => entry.name.endsWith('.wasm'))
              .map(entry => entry.name),
          };
        }"""
    )


def semantic_point(page: Page, wanted: str) -> dict:
    return page.evaluate(
        """wanted => {
          const root = document.getElementById('compose-root');
          const nodes = [];
          const walk = scope => {
            for (const node of scope.querySelectorAll('*')) {
              nodes.push(node);
              if (node.shadowRoot) walk(node.shadowRoot);
            }
          };
          if (root) {
            nodes.push(root);
            if (root.shadowRoot) walk(root.shadowRoot);
            walk(root);
          }
          const matches = nodes.map(node => {
            const text = (node.textContent || '').trim().replace(/\\s+/g, ' ');
            const label = node.getAttribute && (
              node.getAttribute('aria-label') || node.getAttribute('title') || ''
            );
            const rect = node.getBoundingClientRect();
            return {text, label, rect: rect.toJSON(), tag: node.tagName};
          }).filter(entry =>
            entry.rect.width > 0 && entry.rect.height > 0 &&
            (entry.text === wanted || entry.label === wanted)
          ).sort((a, b) => a.rect.width * a.rect.height - b.rect.width * b.rect.height);
          if (!matches.length) return {source: 'viewport-fallback', x: 715, y: 440, matches: []};
          const match = matches[0];
          return {
            source: match.text === wanted ? 'semantic-text' : 'semantic-label',
            x: match.rect.x + match.rect.width / 2,
            y: match.rect.y + match.rect.height / 2,
            matches,
          };
        }""",
        wanted,
    )


def dom_snapshot(page: Page) -> dict:
    return page.evaluate(
        """() => {
          const root = document.getElementById('compose-root');
          const shadows = [];
          const walk = scope => {
            for (const node of scope.querySelectorAll('*')) {
              if (node.shadowRoot) {
                shadows.push({
                  host: node.tagName,
                  html: node.shadowRoot.innerHTML,
                });
                walk(node.shadowRoot);
              }
            }
          };
          if (root) {
            if (root.shadowRoot) shadows.push({host: root.tagName, html: root.shadowRoot.innerHTML});
            walk(root);
          }
          return {
            document: document.documentElement.outerHTML,
            shadows,
          };
        }"""
    )


def ax_nodes(context: BrowserContext, page: Page) -> list[dict]:
    session = context.new_cdp_session(page)
    tree = session.send("Accessibility.getFullAXTree")
    session.detach()
    compact = []
    for node in tree.get("nodes", []):
        role = node.get("role", {}).get("value")
        name = node.get("name", {}).get("value")
        if role or name:
            compact.append({"role": role, "name": name})
    return compact


def capture_official(browser: Browser, meta: dict):
    context = browser.new_context(
        viewport={"width": 1440, "height": 1000}, device_scale_factor=1
    )
    page = context.new_page()
    try:
        page.goto(OFFICIAL_URL, wait_until="domcontentloaded", timeout=90000)
        page.wait_for_selector("h1", timeout=30000)
        page.wait_for_timeout(1200)
        meta["officialDom"] = page.evaluate(
            """() => {
              const text = node => node ? node.textContent.trim().replace(/\\s+/g, ' ') : null;
              const main = document.querySelector('main') || document.body;
              return {
                title: text(document.querySelector('h1')),
                description: text(document.querySelector('h1')?.nextElementSibling),
                headings: [...main.querySelectorAll('h2, h3')]
                  .map(node => ({tag: node.tagName, text: text(node)})),
                examples: document.querySelectorAll(
                  '[data-slot=component-preview], .component-preview'
                ).length,
              };
            }"""
        )
        meta["officialAccessibility"] = [
            node
            for node in ax_nodes(context, page)
            if node.get("role") in {"button", "tooltip"}
            or node.get("name") in {"Hover", "Add to library"}
        ]
        for name, width, height, theme in VIEWS:
            page.set_viewport_size({"width": width, "height": height})
            page.evaluate(
                """theme => {
                  localStorage.setItem('theme', theme);
                  document.documentElement.classList.toggle('dark', theme === 'dark');
                  document.documentElement.style.colorScheme = theme;
                  window.scrollTo(0, 0);
                }""",
                theme,
            )
            page.wait_for_timeout(500)
            path = SHOTS / f"official-{name}.png"
            page.screenshot(path=str(path), full_page=False)
            meta["official"][name] = file_meta(path)
        meta["officialStatus"] = "pass"
    except Exception as error:
        meta["officialStatus"] = f"environment-skipped: {type(error).__name__}: {error}"
    finally:
        context.close()


def make_contact_sheets(meta: dict):
    groups = {
        "local": [f"local-{view[0]}.png" for view in VIEWS],
        "official": [f"official-{view[0]}.png" for view in VIEWS],
    }
    for group, names in groups.items():
        paths = [SHOTS / name for name in names]
        if not all(path.exists() for path in paths):
            continue
        thumbs = []
        for path in paths:
            with Image.open(path) as image:
                thumb = image.convert("RGB")
                thumb.thumbnail((720, 500))
                thumbs.append((path.name, thumb.copy()))
        sheet = Image.new("RGB", (1440, 1060), "white")
        draw = ImageDraw.Draw(sheet)
        for index, (name, thumb) in enumerate(thumbs):
            x = (index % 2) * 720
            y = (index // 2) * 530
            sheet.paste(thumb, (x, y + 24))
            draw.text((x + 8, y + 6), name, fill="black")
        path = OUT / f"contact-{group}.png"
        sheet.save(path)
        meta["contactSheets"][group] = file_meta(path)


def main():
    if not DIST.exists():
        raise SystemExit(f"missing Wasm distribution: {DIST}")
    if SHOTS.exists():
        shutil.rmtree(SHOTS)
    SHOTS.mkdir(parents=True)

    server = ThreadingHTTPServer(("127.0.0.1", 0), SpaHandler)
    thread = threading.Thread(target=server.serve_forever, daemon=True)
    thread.start()
    local_url = f"http://127.0.0.1:{server.server_port}/tooltip"

    meta = {
        "slug": "tooltip",
        "capturedAt": datetime.now(timezone.utc).isoformat(),
        "officialUrl": OFFICIAL_URL,
        "localUrl": local_url,
        "viewportList": VIEWS,
        "distribution": {
            "path": str(DIST),
            "files": {
                name: {"sha256": sha256(DIST / name), "bytes": (DIST / name).stat().st_size}
                for name in ("index.html", "webApp.js")
                if (DIST / name).exists()
            },
            "wasm": {
                path.name: {"sha256": sha256(path), "bytes": path.stat().st_size}
                for path in sorted(DIST.glob("*.wasm"))
            },
        },
        "local": {},
        "official": {},
        "interactions": {},
        "assertions": [],
        "consoleDiagnostics": [],
        "pageErrors": [],
        "officialAccessibility": [],
        "contactSheets": {},
    }

    def assertion(name: str, passed: bool, detail=None):
        entry = {"name": name, "passed": bool(passed)}
        if detail is not None:
            entry["detail"] = detail
        meta["assertions"].append(entry)

    try:
        with sync_playwright() as playwright:
            browser = playwright.chromium.launch(headless=True)
            context = browser.new_context(
                viewport={"width": 1440, "height": 1000},
                device_scale_factor=1,
                locale="en-US",
            )
            page = context.new_page()
            page.on(
                "console",
                lambda message: meta["consoleDiagnostics"].append(
                    {"type": message.type, "text": message.text}
                )
                if message.type in {"error", "warning"}
                else None,
            )
            page.on(
                "pageerror",
                lambda error: meta["pageErrors"].append(str(error)),
            )

            for name, width, height, theme in VIEWS:
                load_theme(page, local_url, theme, width, height)
                page.evaluate("window.scrollTo(0, 0)")
                page.wait_for_timeout(350)
                path = SHOTS / f"local-{name}.png"
                page.screenshot(path=str(path), full_page=False)
                metadata = canvas_meta(page)
                metadata["file"] = file_meta(path)
                meta["local"][name] = metadata
                assertion(f"{name}-loaded", metadata is not None)
                if metadata:
                    assertion(f"{name}-theme", metadata["theme"] == theme, metadata["theme"])
                    bitmap = metadata["canvasBitmap"]
                    css = metadata["canvasRect"]
                    ratio = metadata["devicePixelRatio"]
                    assertion(
                        f"{name}-bitmap-css-ratio",
                        abs(bitmap["width"] - round(css["width"] * ratio)) <= 1
                        and abs(bitmap["height"] - round(css["height"] * ratio)) <= 1,
                        {"bitmap": bitmap, "css": css, "dpr": ratio},
                    )

            load_theme(page, local_url, "light", 1440, 1000)
            page.evaluate("""() => {
              document.documentElement.dir = 'rtl';
              window.scrollTo(0, 0);
            }""")
            page.wait_for_timeout(1200)
            rtl_path = SHOTS / "local-desktop-light-rtl.png"
            page.screenshot(path=str(rtl_path), full_page=False)
            meta["interactions"]["rtl"] = {
                "file": file_meta(rtl_path),
                "direction": page.evaluate(
                    "getComputedStyle(document.documentElement).direction"
                ),
            }
            assertion(
                "rtl-direction-applied",
                meta["interactions"]["rtl"]["direction"] == "rtl",
            )

            load_theme(page, local_url, "light", 1440, 1000)
            page.evaluate("""() => {
              document.documentElement.dir = 'ltr';
              window.scrollTo(0, 0);
            }""")
            page.mouse.move(1100, 800)
            page.wait_for_timeout(300)
            target = semantic_point(page, "Hover")
            hover_base = SHOTS / "interaction-hover-base.png"
            hover_open = SHOTS / "interaction-hover-open.png"
            hover_escape = SHOTS / "interaction-hover-escape.png"
            page.screenshot(path=str(hover_base), full_page=False)
            page.mouse.move(target["x"], target["y"])
            page.wait_for_timeout(900)
            page.screenshot(path=str(hover_open), full_page=False)
            tooltip_box = (640, 360, 790, 435)
            hover_open_pixels = changed_pixels(hover_base, hover_open, tooltip_box)
            meta["interactions"]["hover"] = {
                "changedPixels": hover_open_pixels,
                "base": file_meta(hover_base),
                "open": file_meta(hover_open),
                "target": target,
            }
            assertion("hover-opens-tooltip", hover_open_pixels > 200, hover_open_pixels)
            page.mouse.click(target["x"], target["y"])
            page.wait_for_timeout(150)
            page.keyboard.press("Escape")
            page.mouse.move(1100, 800)
            page.wait_for_timeout(500)
            page.screenshot(path=str(hover_escape), full_page=False)
            hover_escape_pixels = changed_pixels(hover_base, hover_escape, tooltip_box)
            meta["interactions"]["hover"]["escapeChangedPixels"] = hover_escape_pixels
            meta["interactions"]["hover"]["escape"] = file_meta(hover_escape)
            assertion(
                "escape-closes-hover-tooltip",
                hover_escape_pixels < hover_open_pixels // 4,
                hover_escape_pixels,
            )

            load_theme(page, local_url, "light", 1440, 1000)
            page.mouse.move(5, 5)
            page.wait_for_timeout(300)
            keyboard_base = SHOTS / "interaction-keyboard-base.png"
            keyboard_open = SHOTS / "interaction-keyboard-open.png"
            page.screenshot(path=str(keyboard_base), full_page=False)
            keyboard_base_clip = SHOTS / "interaction-keyboard-base-clip.png"
            keyboard_try_clip = SHOTS / "interaction-keyboard-try-clip.png"
            keyboard_clip = {"x": 640, "y": 360, "width": 150, "height": 75}
            page.screenshot(
                path=str(keyboard_base_clip), full_page=False, clip=keyboard_clip
            )
            keyboard_tab = None
            for index in range(1, 81):
                page.keyboard.press("Tab")
                page.wait_for_timeout(80)
                page.screenshot(
                    path=str(keyboard_try_clip), full_page=False, clip=keyboard_clip
                )
                with Image.open(keyboard_base_clip) as base_image:
                    base_pixels = list(base_image.convert("RGB").getdata())
                with Image.open(keyboard_try_clip) as try_image:
                    try_pixels = list(try_image.convert("RGB").getdata())
                if try_pixels != base_pixels:
                    keyboard_tab = index
                    break
            if keyboard_tab is not None:
                page.wait_for_timeout(500)
                page.screenshot(path=str(keyboard_open), full_page=False)
                nodes = ax_nodes(context, page)
                names = {
                    entry["name"] for entry in nodes if entry.get("name")
                }
                meta["interactions"]["keyboard"] = {
                    "tab": keyboard_tab,
                    "open": file_meta(keyboard_open),
                    "accessibilityNames": sorted(
                        name for name in names if name in {"Hover", "Add to library"}
                    ),
                    "accessibilityNodeCount": len(nodes),
                }
                assertion("keyboard-focus-finds-state-change", True, keyboard_tab)
                assertion(
                    "accessibility-trigger-name",
                    "Hover" in names,
                    meta["interactions"]["keyboard"]["accessibilityNames"],
                )
            else:
                assertion("keyboard-focus-finds-state-change", False)

            (OUT / "local-dom.json").write_text(
                json.dumps(dom_snapshot(page), indent=2), encoding="utf-8"
            )
            (OUT / "accessibility.json").write_text(
                json.dumps(ax_nodes(context, page), indent=2), encoding="utf-8"
            )
            context.close()

            official_browser = playwright.chromium.launch(headless=True)
            capture_official(official_browser, meta)
            official_browser.close()
            browser.close()
    finally:
        server.shutdown()
        server.server_close()

    make_contact_sheets(meta)
    assertion("no-page-exceptions", not meta["pageErrors"], meta["pageErrors"][:8])
    failed = [entry["name"] for entry in meta["assertions"] if not entry["passed"]]
    meta["status"] = "pass" if not failed else "fail"
    meta["failedAssertions"] = failed
    (OUT / "browser-summary.json").write_text(
        json.dumps(meta, indent=2) + "\n", encoding="utf-8"
    )
    print(
        json.dumps(
            {
                "status": meta["status"],
                "assertions": len(meta["assertions"]),
                "failed": failed,
                "localCaptures": len(meta["local"]),
                "officialStatus": meta.get("officialStatus"),
                "pageErrors": len(meta["pageErrors"]),
                "consoleDiagnostics": len(meta["consoleDiagnostics"]),
                "summary": str(OUT / "browser-summary.json"),
            }
        )
    )
    raise SystemExit(0 if meta["status"] == "pass" else 1)


if __name__ == "__main__":
    main()
