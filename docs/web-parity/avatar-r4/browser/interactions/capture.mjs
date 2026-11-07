const fs = await import("node:fs/promises");
const crypto = await import("node:crypto");

const task = await taskSpace("avatar r4");
const page = task.page("p2");
const base =
  "/Users/cscs/IdeaProjects/angryecho/compose-foundation/docs/web-parity/avatar-r4/browser/interactions";
await fs.mkdir(base, { recursive: true });

await page.cdp("Emulation.setDeviceMetricsOverride", {
  width: 1440,
  height: 1000,
  deviceScaleFactor: 1,
  mobile: false,
});

async function setTheme(theme) {
  const current = await page.evaluate(() =>
    localStorage.getItem("compose-foundation.theme"),
  );
  if (current !== theme) {
    await page.evaluate(
      (value) => localStorage.setItem("compose-foundation.theme", value),
      theme,
    );
    await page.reload({ timeout: 60000 });
  }
  await page.waitForTimeout(900);
}

async function loadExample(index) {
  await page.evaluate((value) => {
    location.hash = value;
  }, `example-${index}`);
  await page.waitForTimeout(900);
}

async function screenshot(name) {
  return page.screenshot({ path: `${base}/${name}.png` });
}

async function hash(path) {
  const data = await fs.readFile(path);
  return crypto.createHash("sha256").update(data).digest("hex");
}

await setTheme("light");
await loadExample(7);
const files = {};

files["local-dropdown-default"] = await screenshot("local-dropdown-default");
await page.mouse.click(714, 293, { label: "open avatar menu" });
await page.waitForTimeout(350);
files["local-dropdown-open"] = await screenshot("local-dropdown-open");

await page.keyboard.press("ArrowUp");
await page.waitForTimeout(180);
files["local-dropdown-arrow-up-last"] = await screenshot(
  "local-dropdown-arrow-up-last",
);
await page.keyboard.press("Home");
await page.waitForTimeout(180);
files["local-dropdown-home-first"] = await screenshot(
  "local-dropdown-home-first",
);
await page.keyboard.press("End");
await page.waitForTimeout(180);
files["local-dropdown-end-last"] = await screenshot(
  "local-dropdown-end-last",
);
await page.keyboard.press("Escape");
await page.waitForTimeout(300);
files["local-dropdown-escape-restored"] = await screenshot(
  "local-dropdown-escape-restored",
);

await setTheme("dark");
await loadExample(7);
await page.mouse.click(714, 293, { label: "open avatar menu in dark mode" });
await page.waitForTimeout(350);
files["local-dropdown-open-dark"] = await screenshot(
  "local-dropdown-open-dark",
);
await page.keyboard.press("Escape");

await setTheme("light");
await loadExample(8);
files["local-rtl-arabic"] = await screenshot("local-rtl-arabic");
await page.mouse.click(480, 183, { label: "open RTL language selector" });
await page.waitForTimeout(250);
files["local-rtl-language-popup"] = await screenshot(
  "local-rtl-language-popup",
);
await page.mouse.click(449, 209, { label: "select English" });
await page.waitForTimeout(250);
files["local-rtl-english"] = await screenshot("local-rtl-english");
await page.mouse.click(446, 183, { label: "reopen RTL language selector" });
await page.waitForTimeout(250);
files["local-rtl-language-popup-after-english"] = await screenshot(
  "local-rtl-language-popup-after-english",
);
await page.mouse.click(418, 211, { label: "select Hebrew" });
await page.waitForTimeout(250);
files["local-rtl-hebrew"] = await screenshot("local-rtl-hebrew");

const hashes = {};
for (const [name, path] of Object.entries(files)) {
  hashes[name] = await hash(path);
}
await fs.writeFile(
  `${base}/interactions.json`,
  JSON.stringify({ capturedAt: new Date().toISOString(), files, hashes }, null, 2),
);
console.log(JSON.stringify({ ok: true, states: Object.keys(files) }));
