const fs = await import("node:fs/promises");
const crypto = await import("node:crypto");

const task = await taskSpace(99);
const page = task.page("p1");
const base =
  "/Users/cscs/IdeaProjects/angryecho/compose-foundation/docs/web-parity/card-r4/browser/interactions";
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
  await page.waitForTimeout(1000);
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

await page.cdp("Emulation.setDeviceMetricsOverride", {
  width: 1440,
  height: 1000,
  deviceScaleFactor: 1,
  mobile: false,
});
await page.goto("http://127.0.0.1:8777/card#example-1", {
  timeout: 60000,
});
await waitForLocal();
await page.evaluate(() => localStorage.setItem("compose-foundation.theme", "light"));
await page.reload({ timeout: 60000 });
await waitForLocal();
await page.evaluate(() => {
  location.hash = "example-1";
});
await page.waitForTimeout(900);

const files = {};
const states = {};

files.spacing16 = await screenshot("local-spacing-16");
await page.mouse.click(733, 341, { label: "select 32px card spacing" });
await page.waitForTimeout(500);
files.spacing32 = await screenshot("local-spacing-32");
states.spacing = { selected: "32px" };

await page.goto("http://127.0.0.1:8777/card#example-4", {
  timeout: 60000,
});
await waitForLocal();
files.rtlArabic = await screenshot("local-rtl-arabic");
await page.mouse.click(482, 184, { label: "open RTL language selector" });
await page.waitForTimeout(400);
files.rtlLanguagePopup = await screenshot("local-rtl-language-popup");
await page.mouse.click(449, 154, { label: "select English" });
await page.waitForTimeout(450);
files.rtlEnglish = await screenshot("local-rtl-english");
await page.mouse.click(446, 184, { label: "reopen RTL language selector" });
await page.waitForTimeout(350);
await page.mouse.click(418, 238, { label: "select Hebrew" });
await page.waitForTimeout(450);
files.rtlHebrew = await screenshot("local-rtl-hebrew");
states.rtl = { selectedLanguages: ["Arabic", "English", "Hebrew"] };

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
