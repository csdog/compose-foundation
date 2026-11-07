
const fs = await import('node:fs/promises');
const crypto = await import("node:crypto");
const task = await taskSpace(99);
const pages = await task.pages();
let official = pages.find((p) => p.label === "p2");
if (!official) official = await task.newPage();
let local = pages.find((p) => p.label === "p1");
if (!local) local = await task.newPage();
const base = "/Users/cscs/IdeaProjects/angryecho/compose-foundation/docs/web-parity/accordion-r14/sections";
const officialBase = "https://ui.shadcn.com/docs/components/base/accordion";
const localBase = "http://127.0.0.1:8777/accordion";
const anchors = ["basic", "multiple", "disabled", "borders", "card", "rtl"];
const localAnchors = ["example-0", "example-1", "example-2", "example-3", "example-4", "example-5"];
const views = [
  { name: "desktop-light", width: 1440, height: 1000, theme: "light" },
  { name: "desktop-dark", width: 1440, height: 1000, theme: "dark" },
  { name: "phone-light", width: 390, height: 844, theme: "light" },
  { name: "phone-dark", width: 390, height: 844, theme: "dark" },
];

async function applyTheme(page, theme) {
  const isLocal = await page.evaluate(() => location.hostname === "127.0.0.1");
  if (isLocal) {
    await page.evaluate((t) => localStorage.setItem("compose-foundation.theme", t), theme);
    await page.reload({ timeout: 60000 }).catch(() => {});
    await page.waitForTimeout(theme === "dark" ? 900 : 500);
  } else {
    // The official site's reload path can lose the theme race for later anchors, so set
    // the resolved theme directly (same technique as capture.py) and verify it below.
    await page.evaluate(([t, dark]) => {
      localStorage.setItem("theme", t);
      document.documentElement.classList.toggle("dark", dark);
      document.documentElement.style.colorScheme = t;
    }, [theme, theme === "dark"]);
    await page.waitForTimeout(theme === "dark" ? 900 : 500);
  }
}

async function ensureTheme(page, theme, isLocal) {
  if (isLocal) {
    const current = await page.evaluate(() => localStorage.getItem("compose-foundation.theme"));
    if (current !== theme) throw new Error("local theme drifted: " + current);
  } else {
    const ok = await page.evaluate(([t, dark]) => {
      localStorage.setItem("theme", t);
      if (document.documentElement.classList.contains("dark") !== dark) {
        document.documentElement.classList.toggle("dark", dark);
        document.documentElement.style.colorScheme = t;
        return false;
      }
      return true;
    }, [theme, theme === "dark"]);
    if (!ok) await page.waitForTimeout(400);
    const verified = await page.evaluate((dark) => document.documentElement.classList.contains("dark") === dark, theme === "dark");
    if (!verified) throw new Error("official theme did not apply");
  }
}

// Other agents share the same ui.shadcn.com origin in this browser, so the official
// theme can flip between anchors. Re-assert it immediately before every screenshot.
async function forceOfficialTheme(page, theme) {
  await page.evaluate(([t, dark]) => {
    localStorage.setItem("theme", t);
    document.documentElement.classList.toggle("dark", dark);
    document.documentElement.style.colorScheme = t;
  }, [theme, theme === "dark"]);
  await page.waitForTimeout(120);
}

async function readyLocal(page) {
  await page.waitForFunction(
    () => {
      const root = document.getElementById("compose-root");
      const child = root && root.firstElementChild && root.firstElementChild.firstElementChild;
      return !!child && document.body.style.height.includes("calc");
    },
    undefined,
    { timeout: 60000 },
  );
  await page.waitForTimeout(900);
}

const meta = { anchors, localAnchors, views, files: {}, hashes: {} };
for (const view of views) {
  await official.cdp("Emulation.setDeviceMetricsOverride", { width: 1440, height: 1000, deviceScaleFactor: 1, mobile: false });
  await official.goto(officialBase, { timeout: 90000 });
  await official.waitForLoadState("load").catch(() => {});
  await applyTheme(official, view.theme);
  await local.cdp("Emulation.setDeviceMetricsOverride", { width: 1440, height: 1000, deviceScaleFactor: 1, mobile: false });
  await local.goto(localBase, { timeout: 90000 });
  await readyLocal(local);
  await applyTheme(local, view.theme);
  await readyLocal(local);
  for (let i = 0; i < anchors.length; i++) {
    const anchor = anchors[i];
    const localAnchor = localAnchors[i];
    await official.cdp("Emulation.setDeviceMetricsOverride", { width: view.width, height: view.height, deviceScaleFactor: 1, mobile: false });
    await official.evaluate((hash) => {
      const el = document.getElementById(hash);
      if (el) el.scrollIntoView({ block: "start" });
      else location.hash = hash;
    }, anchor);
    await official.waitForTimeout(500);
    await official.evaluate(() => window.scrollBy(0, -64));
    await official.waitForTimeout(200);
    await forceOfficialTheme(official, view.theme);
    await forceOfficialTheme(official, view.theme);
    const officialPath = base + "/official-" + anchor + "-" + view.name + ".png";
    await official.screenshot({ path: officialPath });
    meta.files["official-" + anchor + "-" + view.name] = officialPath;

    await local.cdp("Emulation.setDeviceMetricsOverride", { width: view.width, height: view.height, deviceScaleFactor: 1, mobile: false });
    await local.evaluate(() => window.scrollTo(0, 0));
    await local.waitForTimeout(300);
    await local.evaluate((hash) => { location.hash = hash; }, localAnchor);
    await local.waitForTimeout(1100);
    const localPath = base + "/local-" + anchor + "-" + view.name + ".png";
    await local.screenshot({ path: localPath });
    meta.files["local-" + anchor + "-" + view.name] = localPath;
  }
}
for (const path of Object.values(meta.files)) {
  const data = await fs.readFile(path);
  meta.hashes[path] = crypto.createHash("sha256").update(data).digest("hex");
}
await fs.writeFile(base + "/sections.json", JSON.stringify(meta, null, 2));
console.log(JSON.stringify({ ok: true, anchors, localAnchors, views: views.map((v) => v.name) }));
