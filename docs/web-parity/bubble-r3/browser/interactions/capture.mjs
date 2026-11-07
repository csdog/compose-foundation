const fs = await import("node:fs/promises");
const crypto = await import("node:crypto");

const task = await taskSpace("bubble r3");
const page = task.page("p2");
const base =
  "/Users/cscs/IdeaProjects/angryecho/compose-foundation/docs/web-parity/bubble-r3/browser/interactions";
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
const files = {};

await loadExample(0);
await page.evaluate(() => window.scrollBy(0, 500));
await page.waitForTimeout(250);
files["local-variants-ghost-full"] = await screenshot(
  "local-variants-ghost-full",
);

await loadExample(3);
files["local-links-default"] = await screenshot("local-links-default");
await page.mouse.click(820, 333, { label: "click password bubble" });
await page.waitForTimeout(300);
files["local-links-toast"] = await screenshot("local-links-toast");
await page.mouse.click(1380, 956, { label: "dismiss toast" });
await page.waitForTimeout(200);

await loadExample(4);
await page.mouse.click(734, 625, { label: "click run command reaction" });
await page.waitForTimeout(300);
files["local-reactions-toast"] = await screenshot("local-reactions-toast");
await page.mouse.click(1380, 956, { label: "dismiss toast" });
await page.waitForTimeout(200);

await loadExample(5);
files["local-collapsible-closed"] = await screenshot(
  "local-collapsible-closed",
);
await page.mouse.click(680, 499, { label: "expand long bubble" });
await page.waitForTimeout(250);
files["local-collapsible-open"] = await screenshot("local-collapsible-open");

await loadExample(7);
files["local-popover-closed"] = await screenshot("local-popover-closed");
await page.mouse.click(704, 344, { label: "open popover" });
await page.waitForTimeout(300);
files["local-popover-open"] = await screenshot("local-popover-open");
await page.keyboard.press("Escape");
await page.waitForTimeout(250);
files["local-popover-closed-after-escape"] = await screenshot(
  "local-popover-closed-after-escape",
);

await setTheme("dark");
await loadExample(7);
await page.mouse.click(704, 344, { label: "open popover in dark mode" });
await page.waitForTimeout(300);
files["local-popover-open-dark"] = await screenshot(
  "local-popover-open-dark",
);
await page.keyboard.press("Escape");

const hashes = {};
for (const [name, path] of Object.entries(files)) {
  hashes[name] = await hash(path);
}
await fs.writeFile(
  `${base}/interactions.json`,
  JSON.stringify({ capturedAt: new Date().toISOString(), files, hashes }, null, 2),
);
console.log(JSON.stringify({ ok: true, states: Object.keys(files) }));
