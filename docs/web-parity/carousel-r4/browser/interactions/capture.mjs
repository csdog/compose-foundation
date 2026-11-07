const fs = await import("node:fs/promises");
const crypto = await import("node:crypto");

const task = await taskSpace(99);
const page = task.page("p1");
const base =
  "/Users/cscs/IdeaProjects/angryecho/compose-foundation/docs/web-parity/carousel-r4/browser/interactions";
await fs.mkdir(base, { recursive: true });

async function waitForLocal() {
  await page.waitForFunction(
    () => {
      const root = document.getElementById("compose-root");
      const canvas = root?.firstElementChild?.firstElementChild;
      return !!canvas && document.body.style.height.includes("calc");
    },
    undefined,
    { timeout: 60000 },
  );
  await page.waitForTimeout(700);
}

async function screenshot(name) {
  const path = `${base}/${name}.png`;
  await page.screenshot({ path });
  return path;
}

async function hash(path) {
  const data = await fs.readFile(path);
  return crypto.createHash("sha256").update(data).digest("hex");
}

async function open(anchor) {
  await page.cdp("Emulation.setDeviceMetricsOverride", {
    width: 1440,
    height: 1000,
    deviceScaleFactor: 1,
    mobile: false,
  });
  await page.goto(`http://127.0.0.1:8777/carousel#${anchor}`, {
    timeout: 60000,
  });
  await page.reload({ timeout: 60000 });
  await waitForLocal();
}

await page.goto("http://127.0.0.1:8777/carousel", { timeout: 60000 });
await waitForLocal();
await page.evaluate(() =>
  localStorage.setItem("compose-foundation.theme", "light"),
);
await page.reload({ timeout: 60000 });
await waitForLocal();

const files = {};
const states = {};

await open("example-0");
files.sizesFirst = await screenshot("local-sizes-first");
await page.mouse.click(941, 295, { label: "show next carousel slide" });
await page.waitForTimeout(550);
files.sizesNext = await screenshot("local-sizes-next");
states.sizesNext = { from: "1 2 3", to: "2 3 4" };

await open("example-0");
await page.mouse.move(850, 295, { label: "grab carousel track" });
await page.mouse.down();
await page.mouse.move(430, 295, { label: "drag carousel left" });
await page.mouse.up();
await page.waitForTimeout(700);
files.sizesDragged = await screenshot("local-sizes-dragged");
states.sizesDragged = { direction: "left" };

await open("example-4");
files.pluginsInitial = await screenshot("local-plugins-initial");
await page.waitForTimeout(2200);
files.pluginsAfterTick = await screenshot("local-plugins-after-tick");
states.plugins = { intervalMs: 2000 };

await open("example-5");
await page.waitForTimeout(350);
files.rtlArabic = await screenshot("local-rtl-arabic");
await page.mouse.click(518, 543, { label: "advance RTL carousel" });
await page.waitForTimeout(550);
files.rtlNext = await screenshot("local-rtl-next");
states.rtl = { from: "١", to: "٢" };

const hashes = {};
for (const [name, path] of Object.entries(files)) {
  hashes[name] = await hash(path);
}
await fs.writeFile(
  `${base}/interactions.json`,
  `${JSON.stringify({ capturedAt: new Date().toISOString(), files, hashes, states }, null, 2)}\n`,
);
console.log(
  JSON.stringify({ ok: true, files: Object.keys(files).length, states }),
);
