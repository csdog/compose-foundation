
const fs = await import('node:fs/promises');
const task = await taskSpace("compose-foundation web parity");
const pages = await task.pages();
let official = pages.find((p) => p.label === "p1");
if (!official) official = await task.newPage();
let local = pages.find((p) => p.label === "p2");
if (!local) local = await task.newPage();
const base = "/tmp/cf-checkbox-r3b/sections";
const officialBase = "https://ui.shadcn.com/docs/components/base/checkbox";
const localBase = "http://127.0.0.1:8765/checkbox";
const anchors = ["invalid-state", "basic", "description", "disabled", "group", "table", "rtl"];
const views = [{"name": "desktop-light", "theme": "light", "w": 1440, "h": 1000}, {"name": "phone-light", "theme": "light", "w": 390, "h": 844}];

async function applyTheme(page, theme) {
  await page.cdp("Emulation.setDeviceMetricsOverride", { width: 1440, height: 1000, deviceScaleFactor: 1, mobile: false });
  await page.evaluate((t) => {
    if (location.hostname === "127.0.0.1") {
      localStorage.setItem("compose-foundation.theme", t);
    } else {
      localStorage.setItem("theme", t);
      document.documentElement.classList.toggle("dark", t === "dark");
      document.documentElement.style.colorScheme = t;
    }
  }, theme);
  await page.reload({ timeout: 60000 }).catch(() => {});
  await page.waitForTimeout(theme === "dark" ? 900 : 500);
}

async function readyLocal(page) {
  await page.waitForFunction(
    () => {
      const root = document.getElementById("compose-root");
      const canvas = root && root.firstElementChild && root.firstElementChild.firstElementChild;
      return !!canvas && document.body.style.height.includes("calc");
    },
    undefined,
    { timeout: 60000 },
  );
  await page.waitForTimeout(900);
}

const meta = { anchors, views, files: {} };
for (const view of views) {
  await official.goto(officialBase, { timeout: 90000 });
  await official.waitForLoadState("load").catch(() => {});
  await applyTheme(official, view.theme);
  await local.goto(localBase, { timeout: 90000 });
  await readyLocal(local);
  await applyTheme(local, view.theme);
  await readyLocal(local);
  for (const anchor of anchors) {
    await official.cdp("Emulation.setDeviceMetricsOverride", { width: view.w, height: view.h, deviceScaleFactor: 1, mobile: false });
    await official.evaluate((hash) => {
      const el = document.getElementById(hash);
      if (el) el.scrollIntoView({ block: "start" });
      else location.hash = hash;
    }, anchor);
    await official.waitForTimeout(500);
    await official.evaluate(() => window.scrollBy(0, -64));
    const officialPath = base + "/official-" + anchor + "-" + view.name + ".png";
    await official.screenshot({ path: officialPath });
    meta.files["official-" + anchor + "-" + view.name] = officialPath;

    await local.cdp("Emulation.setDeviceMetricsOverride", { width: view.w, height: view.h, deviceScaleFactor: 1, mobile: false });
    await local.evaluate((hash) => location.hash = hash, anchor);
    await local.waitForTimeout(1000);
    const localPath = base + "/local-" + anchor + "-" + view.name + ".png";
    await local.screenshot({ path: localPath });
    meta.files["local-" + anchor + "-" + view.name] = localPath;
  }
}
await fs.writeFile(base + "/sections.json", JSON.stringify(meta, null, 2));
console.log(JSON.stringify({ ok: true, anchors, views: views.map((v) => v.name) }));
