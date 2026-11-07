const fs = await import("node:fs/promises");
const crypto = await import("node:crypto");

const task = await taskSpace(99);
const page = task.page("p1");
const base =
  "/Users/cscs/IdeaProjects/angryecho/compose-foundation/docs/web-parity/accordion-r14/browser/interactions";
await fs.mkdir(base, { recursive: true });

async function setViewport(width, height) {
  await page.cdp("Emulation.setDeviceMetricsOverride", {
    width,
    height,
    deviceScaleFactor: 1,
    mobile: false,
  });
}

async function waitForLocal() {
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
  await page.waitForTimeout(800);
}

async function setTheme(theme) {
  await page.evaluate(
    (value) => localStorage.setItem("compose-foundation.theme", value),
    theme,
  );
  await page.reload({ timeout: 60000 });
  await waitForLocal();
  await setViewport(1440, 1000);
}

async function loadExample(index) {
  await page.evaluate((value) => {
    location.hash = value;
  }, `example-${index}`);
  await page.waitForTimeout(900);
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

await setViewport(1440, 1000);
await page.goto("http://127.0.0.1:8777/accordion", { timeout: 90000 });
await waitForLocal();
await setTheme("light");

const files = {};
const states = {};

await loadExample(0);
files.basicDefault = await screenshot("local-basic-default");
await page.mouse.click(713, 324, { label: "open second accordion item" });
await page.waitForTimeout(500);
files.basicSecondOpen = await screenshot("local-basic-second-open");
await page.mouse.click(713, 211, { label: "return to first accordion item" });
await page.waitForTimeout(500);
files.basicFirstOpenAgain = await screenshot("local-basic-first-open-again");

await loadExample(1);
files.multipleDefault = await screenshot("local-multiple-default");
await page.mouse.click(713, 305, { label: "open second multiple item" });
await page.waitForTimeout(450);
files.multipleSecondOpen = await screenshot("local-multiple-second-open");
await page.mouse.click(713, 457, { label: "open third multiple item" });
await page.waitForTimeout(450);
files.multipleAllOpen = await screenshot("local-multiple-all-open");

await setTheme("dark");
await loadExample(1);
await page.mouse.click(713, 305, { label: "open second item in dark mode" });
await page.mouse.click(713, 457, { label: "open third item in dark mode" });
await page.waitForTimeout(500);
files.multipleAllOpenDark = await screenshot("local-multiple-all-open-dark");

await setTheme("light");
await loadExample(2);
files.disabledDefault = await screenshot("local-disabled-default");
await page.mouse.click(713, 255, { label: "click disabled accordion item" });
await page.waitForTimeout(400);
files.disabledAfterClick = await screenshot("local-disabled-after-click");

await loadExample(5);
files.rtlArabic = await screenshot("local-rtl-arabic");
await page.mouse.click(480, 184, { label: "open RTL language selector" });
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

await loadExample(17);
files.horizontalDefault = await screenshot("local-horizontal-default");
await page.mouse.click(713, 286, { label: "open horizontal second item" });
await page.waitForTimeout(450);
files.horizontalSecondOpen = await screenshot("local-horizontal-second-open");

await setTheme("light");
await setViewport(390, 844);
await loadExample(0);
await page.evaluate(() => window.scrollBy(0, 160));
await page.waitForTimeout(400);
files.phoneDefault = await screenshot("local-phone-basic-default");
await page.mouse.click(214, 313, { label: "open phone second item" });
await page.waitForTimeout(500);
files.phoneSecondOpen = await screenshot("local-phone-basic-second-open");

states.basic = { defaultValue: "first", switchedToSecond: true };
states.multiple = { defaultOpenCount: 1, openedCount: 3, darkVerified: true };
states.disabled = { disabledItemStayedClosed: true };
states.rtl = { selectedLanguages: ["Arabic", "English", "Hebrew"] };
states.horizontal = { secondItemOpened: true };
states.phone = { secondItemOpened: true };

const hashes = {};
for (const [name, path] of Object.entries(files)) {
  hashes[name] = await hash(path);
}
await fs.writeFile(
  `${base}/interactions.json`,
  JSON.stringify(
    {
      capturedAt: new Date().toISOString(),
      files,
      hashes,
      states,
    },
    null,
    2,
  ),
);
console.log(
  JSON.stringify({
    ok: true,
    states: Object.keys(files),
  }),
);
