
const fs = await import('node:fs/promises');
const task = await taskSpace("compose-foundation parity B");
const pages = await task.pages();
let official = pages.find((p) => p.label === "p1");
if (!official) official = await task.newPage();
let local = pages.find((p) => p.label === "p2");
if (!local) local = await task.newPage();
const base = "/Users/cscs/IdeaProjects/angryecho/compose-foundation/docs/web-parity/dropdown-menu-r4";
const slug = "dropdown-menu";
const officialUrl = "https://ui.shadcn.com/docs/components/base/dropdown-menu";
const localUrl = "http://127.0.0.1:" + 8772 + "/" + slug;
const views = [
  { n: "desktop-light", w: 1440, h: 1000, t: "light" },
  { n: "desktop-dark", w: 1440, h: 1000, t: "dark" },
  { n: "phone-light", w: 390, h: 844, t: "light" },
  { n: "phone-dark", w: 390, h: 844, t: "dark" },
];
await fs.mkdir(base, { recursive: true });

async function officialView(v) {
  await official.cdp("Emulation.setDeviceMetricsOverride", { width: 1440, height: 1000, deviceScaleFactor: 1, mobile: false });
  await official.evaluate((t) => {
    localStorage.setItem("theme", t);
    document.documentElement.classList.toggle("dark", t === "dark");
    document.documentElement.style.colorScheme = t;
    window.scrollTo(0, 0);
  }, v.t);
  await official.waitForTimeout(400);
  await official.cdp("Emulation.setDeviceMetricsOverride", { width: v.w, height: v.h, deviceScaleFactor: 1, mobile: false });
  await official.waitForTimeout(500);
  return official.screenshot({ path: base + "/official-" + v.n + ".png" });
}

async function localView(v) {
  await local.cdp("Emulation.setDeviceMetricsOverride", { width: 1440, height: 1000, deviceScaleFactor: 1, mobile: false });
  await local.waitForTimeout(900);
  if ((await local.evaluate(() => localStorage.getItem("compose-foundation.theme"))) !== v.t) {
    for (let attempt = 0; attempt < 3; attempt++) {
      await local.mouse.click(1291, 32, { label: "switch docs theme" });
      try {
        await local.waitForFunction((t) => localStorage.getItem("compose-foundation.theme") === t, v.t, { timeout: 4000 });
        break;
      } catch (error) {
        if (attempt === 2) throw error;
        await local.waitForTimeout(800);
      }
    }
    await local.waitForTimeout(600);
  }
  await local.cdp("Emulation.setDeviceMetricsOverride", { width: v.w, height: v.h, deviceScaleFactor: 1, mobile: false });
  await local.evaluate(() => window.scrollTo(0, 0));
  await local.waitForTimeout(1200);
  return local.screenshot({ path: base + "/local-" + v.n + ".png" });
}

await official.goto(officialUrl, { timeout: 90000 });
await official.waitForLoadState("load").catch(() => {});
await official.waitForTimeout(1200);
await local.goto(localUrl, { timeout: 90000 });
await local.waitForFunction(
  () => {
    const root = document.getElementById("compose-root");
    const canvas = root && root.firstElementChild && root.firstElementChild.firstElementChild;
    return !!canvas && document.body.style.height.includes("calc");
  },
  undefined,
  { timeout: 60000 },
);
await local.waitForTimeout(1500);

const meta = {
  slug,
  officialUrl,
  localUrl,
  capturedAt: new Date().toISOString(),
  app: await local.evaluate(() =>
    performance.getEntriesByType("resource").filter((e) => e.name.endsWith(".wasm")).map((e) => e.name),
  ),
  officialDom: await official.evaluate(() => {
    const txt = (el) => (el ? el.textContent.trim().replace(/\s+/g, " ") : null);
    const main = document.querySelector("main") || document.body;
    return {
      title: txt(document.querySelector("h1")),
      description: txt(document.querySelector("h1")?.nextElementSibling),
      headings: [...main.querySelectorAll("h2, h3")].map((e) => ({ tag: e.tagName, text: txt(e) })),
      tabs: [...document.querySelectorAll("[role=tab]")].map(txt).filter(Boolean),
      examples: [...document.querySelectorAll("[data-slot=component-preview], .component-preview")].length,
      toc: [...document.querySelectorAll('nav a[href^="#"]')].map(txt).filter(Boolean),
      bodyWidth: document.querySelector("main")?.getBoundingClientRect().toJSON() ?? null,
      clientWidth: document.documentElement.clientWidth,
      scrollHeight: document.documentElement.scrollHeight,
    };
  }),
  local: {},
  files: {},
};

for (const v of views) {
  meta.files["official-" + v.n] = await officialView(v);
  meta.files["local-" + v.n] = await localView(v);
  meta.local[v.n] = await local.evaluate(() => ({
    url: location.href,
    theme: localStorage.getItem("compose-foundation.theme"),
    clientWidth: document.documentElement.clientWidth,
    scrollY,
    bodyHeight: document.body.style.height,
  }));
}
await fs.writeFile(base + "/meta.json", JSON.stringify(meta, null, 2));
console.log(JSON.stringify({ ok: true, slug, meta: base + "/meta.json" }));
