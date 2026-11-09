const fs = await import("node:fs/promises");
const task = await taskSpace(121);
const local = task.page("p1");
const pages = await task.pages();
const official = pages.find((page) => page.label === "p2") ?? (await task.newPage());
const base =
  "/Users/cscs/IdeaProjects/angryecho/compose-foundation/docs/web-parity/chart-r6/browser";
const localUrl = "http://127.0.0.1:8775/chart";
const officialUrl = "https://ui.shadcn.com/docs/components/base/chart";
const views = [
  { name: "desktop-light", width: 1440, height: 1000, theme: "light" },
  { name: "desktop-dark", width: 1440, height: 1000, theme: "dark" },
  { name: "phone-light", width: 390, height: 844, theme: "light" },
  { name: "phone-dark", width: 390, height: 844, theme: "dark" },
];

await fs.mkdir(base, { recursive: true });

async function waitForLocal() {
  await local.waitForFunction(
    () => {
      const root = document.getElementById("compose-root");
      return (
        root?.firstElementChild?.firstElementChild?.tagName === "CANVAS" &&
        document.body.style.height.includes("calc")
      );
    },
    undefined,
    { timeout: 60_000 },
  );
  await local.waitForTimeout(1000);
}

async function setViewport(page, view) {
  await page.cdp("Emulation.setDeviceMetricsOverride", {
    width: view.width,
    height: view.height,
    deviceScaleFactor: 1,
    mobile: false,
  });
}

async function captureLocal(view) {
  await setViewport(local, view);
  await local.evaluate((theme) => localStorage.setItem("compose-foundation.theme", theme), view.theme);
  await local.reload({ timeout: 60_000 });
  await waitForLocal();
  await local.evaluate(() => window.scrollTo(0, 0));
  await local.waitForTimeout(500);
  const path = `${base}/local-${view.name}.png`;
  await local.screenshot({ path });
  return path;
}

async function captureOfficial(view) {
  await setViewport(official, view);
  await official.evaluate((theme) => {
    localStorage.setItem("theme", theme);
    document.documentElement.classList.toggle("dark", theme === "dark");
    document.documentElement.style.colorScheme = theme;
    window.scrollTo(0, 0);
  }, view.theme);
  await official.waitForTimeout(300);
  const path = `${base}/official-${view.name}.png`;
  await official.screenshot({ path });
  return path;
}

await setViewport(local, views[0]);
await local.goto(localUrl, { timeout: 60_000 });
await waitForLocal();

await setViewport(official, views[0]);
await official.goto(officialUrl, { timeout: 90_000 });
await official.waitForLoadState("load").catch(() => {});
await official.waitForTimeout(1000);

const meta = {
  slug: "chart",
  localUrl,
  officialUrl,
  capturedAt: new Date().toISOString(),
  app: await local.evaluate(() =>
    performance
      .getEntriesByType("resource")
      .filter((entry) => entry.name.endsWith(".wasm"))
      .map((entry) => entry.name),
  ),
  views: {},
  files: {},
};

for (const view of views) {
  meta.files[`local-${view.name}`] = await captureLocal(view);
  meta.files[`official-${view.name}`] = await captureOfficial(view);
  meta.views[view.name] = await local.evaluate(() => ({
    clientWidth: document.documentElement.clientWidth,
    scrollY,
    bodyHeight: document.body.style.height,
    theme: localStorage.getItem("compose-foundation.theme"),
  }));
}

await setViewport(local, views[0]);
await local.evaluate(() => {
  localStorage.setItem("compose-foundation.theme", "light");
  window.scrollTo(0, 0);
});
await local.reload({ timeout: 60_000 });
await waitForLocal();

const snapshot = await local.snapshot();
const orderedLabels = [
  "Component",
  "Updating to Recharts v3",
  "Installation",
  "Usage",
  "Your First Chart",
  "Add a Grid",
  "Add an Axis",
  "Add Tooltip",
  "Add Legend",
  "Chart Config",
  "Theming",
  "CSS Variables",
  "hex, hsl or oklch",
  "Using Colors",
  "Tooltip",
  "Tooltip Props",
  "Legend",
  "Accessibility",
  "RTL",
];
const positions = orderedLabels.map((label) => ({
  label,
  index: snapshot.indexOf(`text "${label}"`),
}));
const orderIsValid = positions.every(
  (entry, index) => entry.index >= 0 && (index === 0 || entry.index > positions[index - 1].index),
);
if (!orderIsValid) {
  throw new Error(`Chart section order failed: ${JSON.stringify(positions)}`);
}

await local.mouse.click(896, 321, { label: "switch to mobile series" });
await local.waitForTimeout(500);
meta.files["interaction-mobile-series"] = `${
  base
}/interaction-mobile-series.png`;
await local.screenshot({ path: meta.files["interaction-mobile-series"] });
await local.mouse.click(770, 321, { label: "switch to desktop series" });
await local.waitForTimeout(500);

await fs.writeFile(
  `${base}/order.json`,
  JSON.stringify({ snapshotBytes: snapshot.length, positions }, null, 2),
);
await fs.writeFile(`${base}/meta.json`, JSON.stringify(meta, null, 2));
console.log(
  JSON.stringify({
    ok: true,
    app: meta.app,
    order: positions,
    meta: `${base}/meta.json`,
  }),
);

await task.finish({ keep: [] });
