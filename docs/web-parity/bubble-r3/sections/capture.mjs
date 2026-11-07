const fs = await import("node:fs/promises");
const crypto = await import("node:crypto");

const task = await taskSpace("bubble r3");
const official = task.page("p1");
const local = task.page("p2");
const base =
  "/Users/cscs/IdeaProjects/angryecho/compose-foundation/docs/web-parity/bubble-r3/sections";
const headings = [
  "Variants",
  "Alignment",
  "Bubble Group",
  "Links and Buttons",
  "Reactions",
  "Show More / Collapsible",
  "Tooltip",
  "Popover",
];
const anchors = headings.map((heading) =>
  heading.toLowerCase().replace(/[^a-z0-9]+/g, "-").replace(/^-|-$/g, ""),
);
const localAnchors = headings.map((_, index) => `example-${index}`);
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

async function setTheme(page, theme) {
  await page.evaluate(
    (value) => {
      localStorage.setItem("theme", value);
      localStorage.setItem("compose-foundation.theme", value);
      document.documentElement.classList.toggle("dark", value === "dark");
      document.documentElement.style.colorScheme = value;
    },
    theme,
  );
}

async function waitForLocal(page) {
  await page.waitForFunction(
    () => {
      const root = document.getElementById("compose-root");
      const child =
        root &&
        root.firstElementChild &&
        root.firstElementChild.firstElementChild;
      return !!child && document.body.style.height.includes("calc");
    },
    undefined,
    { timeout: 60000 },
  );
  await page.waitForTimeout(700);
}

async function hash(path) {
  const data = await fs.readFile(path);
  return crypto.createHash("sha256").update(data).digest("hex");
}

await official.goto(
  "https://ui.shadcn.com/docs/components/base/bubble",
  { timeout: 90000 },
);
await official.waitForLoadState("load").catch(() => {});
await local.goto("http://127.0.0.1:8777/bubble", {
  timeout: 90000,
});
await waitForLocal(local);

const meta = { anchors, localAnchors, views, files: {}, hashes: {} };

for (const view of views) {
  await setViewport(official, view);
  await setTheme(official, view.theme);
  await official.waitForTimeout(250);
  await setViewport(local, view);
  const currentTheme = await local.evaluate(() =>
    localStorage.getItem("compose-foundation.theme"),
  );
  if (currentTheme !== view.theme) {
    await setTheme(local, view.theme);
    await local.reload({ timeout: 60000 });
    await waitForLocal(local);
  }

  for (let index = 0; index < anchors.length; index++) {
    const anchor = anchors[index];
    const localAnchor = localAnchors[index];

    await official.evaluate((heading) => {
      const node = [...document.querySelectorAll("h2")].find(
        (candidate) => candidate.textContent.replace("#", "").trim() === heading,
      );
      if (node) node.scrollIntoView({ block: "start" });
    }, headings[index]);
    await official.waitForTimeout(350);
    await official.evaluate(() => window.scrollBy(0, -64));
    await setTheme(official, view.theme);
    await official.waitForTimeout(150);
    const officialPath = `${base}/official-${anchor}-${view.name}.png`;
    await official.screenshot({ path: officialPath });
    meta.files[`official-${anchor}-${view.name}`] = officialPath;

    await local.evaluate((hashValue) => {
      location.hash = hashValue;
    }, localAnchor);
    await local.waitForTimeout(850);
    const localPath = `${base}/local-${anchor}-${view.name}.png`;
    await local.screenshot({ path: localPath });
    meta.files[`local-${anchor}-${view.name}`] = localPath;
  }
}

for (const path of Object.values(meta.files)) {
  meta.hashes[path] = await hash(path);
}
await fs.writeFile(`${base}/sections.json`, JSON.stringify(meta, null, 2));
console.log(
  JSON.stringify({
    ok: true,
    captures: Object.keys(meta.files).length,
    views: views.map((view) => view.name),
  }),
);
