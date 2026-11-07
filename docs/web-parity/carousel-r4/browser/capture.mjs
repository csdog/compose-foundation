const fs = await import("node:fs/promises");
const crypto = await import("node:crypto");

const task = await taskSpace(99);
const pages = await task.pages();
let official = pages.find((page) => page.label === "p2");
if (!official) official = await task.newPage();
let local = pages.find((page) => page.label === "p1");
if (!local) local = await task.newPage();

const base =
  "/Users/cscs/IdeaProjects/angryecho/compose-foundation/docs/web-parity/carousel-r4/browser";
const officialUrl = "https://ui.shadcn.com/docs/components/base/carousel";
const localUrl = "http://127.0.0.1:8777/carousel";
const views = [
  { name: "desktop-light", width: 1440, height: 1000, theme: "light" },
  { name: "desktop-dark", width: 1440, height: 1000, theme: "dark" },
  { name: "phone-light", width: 390, height: 844, theme: "light" },
  { name: "phone-dark", width: 390, height: 844, theme: "dark" },
];
const recipes = [
  { name: "default", localHash: null, officialIndex: 0 },
  { name: "sizes", localHash: "example-0", officialIndex: 1 },
  { name: "spacing", localHash: "example-1", officialIndex: 2 },
  { name: "orientation", localHash: "example-2", officialIndex: 3 },
  { name: "api", localHash: "example-3", officialIndex: 4 },
  { name: "plugins", localHash: "example-4", officialIndex: 5 },
  { name: "rtl", localHash: "example-5", officialIndex: 6 },
];
const supporting = [
  { name: "about", officialAnchor: "about", localHash: "introduction-0" },
  { name: "options", officialAnchor: "options", localHash: "before-example-3-0" },
  { name: "events", officialAnchor: "events", localHash: "before-example-4-0" },
  { name: "api-reference", officialAnchor: "api-reference", localHash: "api-reference" },
];

await fs.mkdir(`${base}/sections`, { recursive: true });

async function hash(path) {
  const data = await fs.readFile(path);
  return crypto.createHash("sha256").update(data).digest("hex");
}

async function waitForLocal() {
  await local.waitForFunction(
    () => {
      const root = document.getElementById("compose-root");
      const canvas = root?.firstElementChild?.firstElementChild;
      return !!canvas && document.body.style.height.includes("calc");
    },
    undefined,
    { timeout: 60000 },
  );
  await local.waitForTimeout(1000);
}

async function setViewport(page, width, height) {
  await page.cdp("Emulation.setDeviceMetricsOverride", {
    width,
    height,
    deviceScaleFactor: 1,
    mobile: false,
  });
}

async function setTheme(page, theme) {
  const isLocal = await page.evaluate(
    () => location.hostname === "127.0.0.1",
  );
  if (isLocal) {
    await page.evaluate(
      (value) => localStorage.setItem("compose-foundation.theme", value),
      theme,
    );
    await page.reload({ timeout: 60000 });
    await waitForLocal();
  } else {
    await page.evaluate((value) => {
      localStorage.setItem("theme", value);
      document.documentElement.classList.toggle("dark", value === "dark");
      document.documentElement.style.colorScheme = value;
    }, theme);
    await page.waitForTimeout(500);
  }
}

async function forceOfficialTheme(theme) {
  await official.evaluate((value) => {
    localStorage.setItem("theme", value);
    document.documentElement.classList.toggle("dark", value === "dark");
    document.documentElement.style.colorScheme = value;
  }, theme);
  await official.waitForTimeout(120);
}

async function scrollToLocal(anchor, y = 0) {
  await local.evaluate(() => {
    history.replaceState(null, "", location.pathname);
    window.scrollTo(0, 0);
  });
  await local.waitForTimeout(100);
  if (anchor) {
    await local.evaluate((value) => {
      location.hash = value;
    }, anchor);
    await local.waitForTimeout(800);
  }
  if (y) {
    await local.evaluate((value) => window.scrollBy(0, value), y);
    await local.waitForTimeout(250);
  }
}

await setViewport(official, 1440, 1000);
await official.goto(officialUrl, { timeout: 90000 });
await official.waitForLoadState("load").catch(() => {});
await setViewport(local, 1440, 1000);
await local.goto(localUrl, { timeout: 90000 });
await waitForLocal();

const meta = {
  slug: "carousel",
  officialUrl,
  localUrl,
  capturedAt: new Date().toISOString(),
  applicationWasm: await local.evaluate(() =>
    performance
      .getEntriesByType("resource")
      .filter((entry) => entry.name.endsWith(".wasm"))
      .map((entry) => entry.name),
  ),
  officialDom: await official.evaluate(() => {
    const text = (node) =>
      node ? node.textContent.trim().replace(/\s+/g, " ") : null;
    const root = document.querySelector("h1")?.parentElement?.parentElement;
    return {
      title: text(document.querySelector("h1")),
      description: text(root?.querySelector(":scope > p")),
      headings: [
        ...document.querySelectorAll("main h2, main h3"),
      ].map((node) => ({
        tag: node.tagName,
        text: text(node),
        id: node.id,
      })),
      examples: document.querySelectorAll(
        "[data-slot=component-preview], .component-preview",
      ).length,
    };
  }),
  views: {},
  quadrantFiles: {},
  sectionFiles: {},
  hashes: {},
};

for (const view of views) {
  await setViewport(official, 1440, 1000);
  await official.goto(officialUrl, { timeout: 90000 });
  await official.waitForLoadState("load").catch(() => {});
  await setTheme(official, view.theme);
  await setViewport(local, 1440, 1000);
  await local.goto(localUrl, { timeout: 90000 });
  await waitForLocal();
  await setTheme(local, view.theme);

  await setViewport(official, view.width, view.height);
  await forceOfficialTheme(view.theme);
  await official.evaluate(() => window.scrollTo(0, 0));
  await official.waitForTimeout(300);
  await setViewport(local, view.width, view.height);
  await local.evaluate(() => window.scrollTo(0, 0));
  await local.waitForTimeout(500);

  const officialQuadrant = `${base}/official-${view.name}.png`;
  const localQuadrant = `${base}/local-${view.name}.png`;
  await official.screenshot({ path: officialQuadrant });
  await local.screenshot({ path: localQuadrant });
  meta.quadrantFiles[`official-${view.name}`] = officialQuadrant;
  meta.quadrantFiles[`local-${view.name}`] = localQuadrant;
  meta.views[view.name] = await local.evaluate(() => ({
    theme: localStorage.getItem("compose-foundation.theme"),
    clientWidth: document.documentElement.clientWidth,
    bodyHeight: document.body.style.height,
  }));

  for (const recipe of recipes) {
    await official.evaluate((index) => {
      const preview = document.querySelectorAll(
        "[data-slot=component-preview], .component-preview",
      )[index];
      if (preview) preview.scrollIntoView({ block: "start" });
      else window.scrollTo(0, 0);
      window.scrollBy(0, -80);
    }, recipe.officialIndex);
    await forceOfficialTheme(view.theme);
    await official.waitForTimeout(250);
    const officialPath = `${base}/sections/official-${recipe.name}-${view.name}.png`;
    await official.screenshot({ path: officialPath });
    meta.sectionFiles[`official-${recipe.name}-${view.name}`] = officialPath;

    await scrollToLocal(recipe.localHash);
    const localPath = `${base}/sections/local-${recipe.name}-${view.name}.png`;
    await local.screenshot({ path: localPath });
    meta.sectionFiles[`local-${recipe.name}-${view.name}`] = localPath;
  }

  for (const section of supporting) {
    await official.evaluate((anchor) => {
      const element = document.getElementById(anchor);
      element?.scrollIntoView({ block: "start" });
      window.scrollBy(0, -80);
    }, section.officialAnchor);
    await forceOfficialTheme(view.theme);
    await official.waitForTimeout(250);
    const officialPath = `${base}/sections/official-${section.name}-${view.name}.png`;
    await official.screenshot({ path: officialPath });
    meta.sectionFiles[`official-${section.name}-${view.name}`] = officialPath;

    await scrollToLocal(section.localHash);
    const localPath = `${base}/sections/local-${section.name}-${view.name}.png`;
    await local.screenshot({ path: localPath });
    meta.sectionFiles[`local-${section.name}-${view.name}`] = localPath;
  }
}

for (const path of [
  ...Object.values(meta.quadrantFiles),
  ...Object.values(meta.sectionFiles),
]) {
  meta.hashes[path] = await hash(path);
}
await fs.writeFile(`${base}/meta.json`, `${JSON.stringify(meta, null, 2)}\n`);
console.log(
  JSON.stringify({
    ok: true,
    quadrants: Object.keys(meta.quadrantFiles).length,
    sections: Object.keys(meta.sectionFiles).length,
  }),
);
