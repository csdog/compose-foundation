const fs = await import("node:fs/promises");
const crypto = await import("node:crypto");

const task = await taskSpace(95);
const official = task.page("p2");
const local = task.page("p1");
const base =
  "/Users/cscs/IdeaProjects/angryecho/compose-foundation/docs/web-parity/button-r5/browser";
const officialUrl = "https://ui.shadcn.com/docs/components/base/button";
const localUrl = "http://127.0.0.1:8777/button";
const views = [
  { name: "desktop-light", width: 1440, height: 1000, theme: "light" },
  { name: "desktop-dark", width: 1440, height: 1000, theme: "dark" },
  { name: "phone-light", width: 390, height: 844, theme: "light" },
  { name: "phone-dark", width: 390, height: 844, theme: "dark" },
];

await fs.mkdir(base, { recursive: true });

async function setViewport(page, view) {
  await page.cdp("Emulation.setDeviceMetricsOverride", {
    width: view.width,
    height: view.height,
    deviceScaleFactor: 1,
    mobile: false,
  });
}

async function setOfficialTheme(page, theme) {
  await page.evaluate((value) => {
    localStorage.setItem("theme", value);
    document.documentElement.classList.toggle("dark", value === "dark");
    document.documentElement.style.colorScheme = value;
    window.scrollTo(0, 0);
  }, theme);
  await page.waitForTimeout(250);
}

async function setLocalTheme(page, theme) {
  const current = await page.evaluate(() =>
    localStorage.getItem("compose-foundation.theme"),
  );
  if (current === theme) return;
  await page.evaluate(
    (value) => localStorage.setItem("compose-foundation.theme", value),
    theme,
  );
  await page.reload({ timeout: 60000 });
  await waitForLocal(page);
}

async function waitForLocal(page) {
  await page.waitForFunction(
    () => {
      const root = document.getElementById("compose-root");
      const canvas =
        root &&
        root.firstElementChild &&
        root.firstElementChild.firstElementChild;
      return !!canvas && document.body.style.height.includes("calc");
    },
    undefined,
    { timeout: 60000 },
  );
  await page.waitForTimeout(900);
}

async function hash(path) {
  const data = await fs.readFile(path);
  return crypto.createHash("sha256").update(data).digest("hex");
}

await setViewport(official, views[0]);
await official.goto(officialUrl, { timeout: 90000 });
await official.waitForLoadState("load").catch(() => {});
await official.waitForTimeout(800);

await setViewport(local, views[0]);
await local.goto(localUrl, { timeout: 90000 });
await waitForLocal(local);

const meta = {
  officialUrl,
  officialRevision: "42f3bb63b46a69e52a24bd005cdc9ec15ba7a7b9",
  localUrl,
  capturedAt: new Date().toISOString(),
  app: await local.evaluate(() =>
    performance
      .getEntriesByType("resource")
      .filter((entry) => entry.name.endsWith(".wasm"))
      .map((entry) => entry.name),
  ),
  officialDom: await official.evaluate(() => {
    const text = (node) =>
      node ? node.textContent.trim().replace(/\s+/g, " ") : null;
    const main = document.querySelector("main") || document.body;
    return {
      title: text(document.querySelector("h1")),
      headings: [...main.querySelectorAll("h2, h3")].map((node) => ({
        tag: node.tagName,
        text: text(node),
        id: node.id || null,
      })),
      examples: document.querySelectorAll(
        "[data-slot=component-preview], .component-preview",
      ).length,
      theme:
        localStorage.getItem("theme") ??
        (document.documentElement.classList.contains("dark")
          ? "dark"
          : "light"),
      clientWidth: document.documentElement.clientWidth,
      scrollHeight: document.documentElement.scrollHeight,
    };
  }),
  views: {},
  files: {},
};

for (const view of views) {
  await setViewport(official, view);
  await setOfficialTheme(official, view.theme);
  await official.evaluate(() => window.scrollTo(0, 0));
  await official.waitForTimeout(250);
  const officialPath = `${base}/official-${view.name}.png`;
  await official.screenshot({ path: officialPath });
  meta.files[`official-${view.name}`] = officialPath;

  await setViewport(local, view);
  await setLocalTheme(local, view.theme);
  await local.evaluate(() => window.scrollTo(0, 0));
  await local.waitForTimeout(500);
  const localPath = `${base}/local-${view.name}.png`;
  await local.screenshot({ path: localPath });
  meta.files[`local-${view.name}`] = localPath;
  meta.views[view.name] = await local.evaluate(() => ({
    theme: localStorage.getItem("compose-foundation.theme"),
    clientWidth: document.documentElement.clientWidth,
    scrollHeight: document.documentElement.scrollHeight,
    bodyHeight: document.body.style.height,
  }));
}

meta.hashes = {};
for (const path of Object.values(meta.files)) {
  meta.hashes[path] = await hash(path);
}
await fs.writeFile(`${base}/meta.json`, JSON.stringify(meta, null, 2));
console.log(
  JSON.stringify({
    ok: true,
    spaceId: task.spaceId,
    views: Object.keys(meta.views),
    examples: meta.officialDom.examples,
  }),
);
