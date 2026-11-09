const fs = await import("node:fs/promises");

const task = await taskSpace("compose-foundation token three components 2026-11-07");
const existing = await task.pages();
let official = existing.find((page) => page.label === "p1") ?? (await task.newPage());
let local = existing.find((page) => page.label === "p2") ?? (await task.newPage());

const root = "/Users/cscs/IdeaProjects/angryecho/compose-foundation/docs/web-parity";
const components = [
  { slug: "badge", output: `${root}/badge-r4/browser` },
  { slug: "alert", output: `${root}/alert-r12/browser` },
  { slug: "separator", output: `${root}/separator-r3/browser` },
];
const views = [
  { name: "desktop-light", width: 1440, height: 1000, theme: "light" },
  { name: "desktop-dark", width: 1440, height: 1000, theme: "dark" },
  { name: "phone-light", width: 390, height: 844, theme: "light" },
  { name: "phone-dark", width: 390, height: 844, theme: "dark" },
];

async function forceOfficialTheme(theme) {
  await official.evaluate((value) => {
    localStorage.setItem("theme", value);
    document.documentElement.classList.toggle("dark", value === "dark");
    document.documentElement.style.colorScheme = value;
    window.scrollTo(0, 0);
  }, theme);
  await official.waitForTimeout(200);
}

async function forceLocalTheme(theme) {
  const current = await local.evaluate(() => localStorage.getItem("compose-foundation.theme"));
  if (current === theme) return;
  await local.evaluate((value) => localStorage.setItem("compose-foundation.theme", value), theme);
  await local.reload({ timeout: 60000 });
  await local.waitForFunction(
    () => {
      const root = document.getElementById("compose-root");
      const canvas = root && root.firstElementChild && root.firstElementChild.firstElementChild;
      return !!canvas && document.body.style.height.includes("calc");
    },
    undefined,
    { timeout: 60000 },
  );
}

const summary = {
  capturedAt: new Date().toISOString(),
  taskSpaceId: task.spaceId,
  wasm: null,
  components: {},
};

await official.goto("https://ui.shadcn.com/docs/components/badge", { timeout: 90000 });
await official.waitForLoadState("load").catch(() => {});
await local.goto("http://127.0.0.1:8791/badge", { timeout: 90000 });
await local.waitForFunction(
  () => {
    const root = document.getElementById("compose-root");
    const canvas = root && root.firstElementChild && root.firstElementChild.firstElementChild;
    return !!canvas && document.body.style.height.includes("calc");
  },
  undefined,
  { timeout: 60000 },
);
summary.wasm = await local.evaluate(() =>
  [...performance.getEntriesByType("resource")]
    .map((entry) => entry.name)
    .find((name) => name.endsWith(".wasm")) ?? null,
);

for (const component of components) {
  await fs.mkdir(component.output, { recursive: true });
  await official.goto(`https://ui.shadcn.com/docs/components/base/${component.slug}`, {
    timeout: 90000,
  });
  await official.waitForLoadState("load").catch(() => {});
  await official.waitForTimeout(500);
  await local.goto(`http://127.0.0.1:8791/${component.slug}`, { timeout: 90000 });
  await local.waitForFunction(
    () => {
      const root = document.getElementById("compose-root");
      const canvas = root && root.firstElementChild && root.firstElementChild.firstElementChild;
      return !!canvas && document.body.style.height.includes("calc");
    },
    undefined,
    { timeout: 60000 },
  );

  const files = {};
  for (const view of views) {
    await official.cdp("Emulation.setDeviceMetricsOverride", {
      width: 1440,
      height: 1000,
      deviceScaleFactor: 1,
      mobile: false,
    });
    await forceOfficialTheme(view.theme);
    await official.cdp("Emulation.setDeviceMetricsOverride", {
      width: view.width,
      height: view.height,
      deviceScaleFactor: 1,
      mobile: false,
    });
    await forceOfficialTheme(view.theme);
    files[`official-${view.name}`] = await official.screenshot({
      path: `${component.output}/official-${view.name}.png`,
    });

    await forceLocalTheme(view.theme);
    await local.cdp("Emulation.setDeviceMetricsOverride", {
      width: view.width,
      height: view.height,
      deviceScaleFactor: 1,
      mobile: false,
    });
    await local.evaluate(() => window.scrollTo(0, 0));
    await local.waitForTimeout(700);
    files[`local-${view.name}`] = await local.screenshot({
      path: `${component.output}/local-${view.name}.png`,
    });
  }
  summary.components[component.slug] = files;
}

await fs.writeFile(
  `${root}/token-three-components.json`,
  JSON.stringify(summary, null, 2),
);
console.log(JSON.stringify({ ok: true, ...summary }));
await task.finish({ keep: [] });
