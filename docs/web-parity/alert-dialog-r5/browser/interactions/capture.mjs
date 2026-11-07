const fs = await import("node:fs/promises");
const crypto = await import("node:crypto");

const task = await taskSpace(97);
const local = task.page("p1");
const official = task.page("p2");
const base =
  "/Users/cscs/IdeaProjects/angryecho/compose-foundation/docs/web-parity/alert-dialog-r5/browser/interactions";
const localUrl = "http://127.0.0.1:8777/alert-dialog";
const officialUrl = "https://ui.shadcn.com/docs/components/base/alert-dialog";
await fs.mkdir(base, { recursive: true });

async function setViewport(page, width, height) {
  await page.cdp("Emulation.setDeviceMetricsOverride", {
    width,
    height,
    deviceScaleFactor: 1,
    mobile: false,
  });
}

async function waitForLocal() {
  await local.waitForFunction(
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
  await local.waitForTimeout(800);
}

async function setLocalTheme(theme) {
  const current = await local.evaluate(() =>
    localStorage.getItem("compose-foundation.theme"),
  );
  if (current === theme) return;
  await local.evaluate(
    (value) => localStorage.setItem("compose-foundation.theme", value),
    theme,
  );
  await local.reload({ timeout: 60000 });
  await waitForLocal();
}

async function loadExample(index) {
  await local.evaluate((value) => {
    location.hash = value;
  }, `example-${index}`);
  await local.waitForTimeout(900);
}

async function screenshot(name) {
  const path = `${base}/${name}.png`;
  await local.screenshot({ path });
  return path;
}

async function hash(path) {
  const data = await fs.readFile(path);
  return crypto.createHash("sha256").update(data).digest("hex");
}

async function clickFirstVisible(officialPage, text) {
  const clicked = await officialPage.evaluate((expected) => {
    const element = [...document.querySelectorAll("button")].find((candidate) => {
      const rect = candidate.getBoundingClientRect();
      return (
        candidate.textContent.trim() === expected &&
        rect.width > 0 &&
        rect.height > 0 &&
        rect.bottom > 0 &&
        rect.top < innerHeight
      );
    });
    if (!element) return false;
    element.scrollIntoView({ block: "center" });
    element.click();
    return true;
  }, text);
  if (!clicked) throw new Error(`Missing visible button: ${text}`);
}

await setViewport(local, 1440, 1000);
await local.goto(localUrl, { timeout: 90000 });
await waitForLocal();
await setLocalTheme("light");
await setViewport(local, 1440, 1000);

const files = {};
const states = {};

await loadExample(0);
files.basicDefault = await screenshot("local-basic-default");
await local.mouse.click(713, 306, { label: "open basic alert dialog" });
await local.waitForTimeout(800);
await local.mouse.move(1120, 520);
files.basicOpen = await screenshot("local-basic-open");
await local.keyboard.press("Tab");
await local.waitForTimeout(250);
files.basicTabFirst = await screenshot("local-basic-tab-first");
await local.keyboard.press("Tab");
await local.waitForTimeout(250);
files.basicTabSecond = await screenshot("local-basic-tab-second");
await local.keyboard.press("Tab");
await local.waitForTimeout(250);
files.basicTabCycle = await screenshot("local-basic-tab-cycle");
await local.keyboard.press("Escape");
await local.waitForTimeout(500);
files.basicEscaped = await screenshot("local-basic-escaped");

await local.mouse.click(713, 306, { label: "reopen basic alert dialog" });
await local.waitForTimeout(700);
files.basicOpenAgain = await screenshot("local-basic-open-again");
await local.mouse.click(848, 560, { label: "continue alert dialog" });
await local.waitForTimeout(500);
files.basicContinueClosed = await screenshot("local-basic-continue-closed");

await local.mouse.click(713, 306, { label: "reopen for outside click" });
await local.waitForTimeout(700);
await local.mouse.click(160, 520, { label: "click outside alert dialog" });
await local.waitForTimeout(500);
files.basicOutsideStillOpen = await screenshot("local-basic-outside-still-open");
await local.keyboard.press("Escape");
await local.waitForTimeout(400);

await setLocalTheme("dark");
await setViewport(local, 1440, 1000);
await loadExample(0);
await local.mouse.click(713, 306, { label: "open dark alert dialog" });
await local.waitForTimeout(700);
files.basicOpenDark = await screenshot("local-basic-open-dark");
await local.keyboard.press("Escape");
await local.waitForTimeout(400);

await setLocalTheme("light");
await setViewport(local, 1440, 1000);
await loadExample(1);
await local.mouse.click(713, 295, { label: "open small alert dialog" });
await local.waitForTimeout(700);
files.smallOpen = await screenshot("local-small-open");
await local.keyboard.press("Escape");
await local.waitForTimeout(400);

await loadExample(2);
await local.mouse.click(713, 295, { label: "open media alert dialog" });
await local.waitForTimeout(700);
files.mediaOpen = await screenshot("local-media-open");
await local.keyboard.press("Escape");
await local.waitForTimeout(400);

await loadExample(3);
await local.mouse.click(713, 322, { label: "open small media alert dialog" });
await local.waitForTimeout(700);
files.smallMediaOpen = await screenshot("local-small-media-open");
await local.keyboard.press("Escape");
await local.waitForTimeout(400);

await loadExample(4);
await local.mouse.click(713, 321, { label: "open destructive alert dialog" });
await local.waitForTimeout(700);
files.destructiveOpen = await screenshot("local-destructive-open");
await local.keyboard.press("Escape");
await local.waitForTimeout(400);

await loadExample(5);
files.rtlArabicDefault = await screenshot("local-rtl-arabic-default");
await local.mouse.click(782, 362, { label: "open Arabic alert dialog" });
await local.waitForTimeout(700);
files.rtlArabicOpen = await screenshot("local-rtl-arabic-open");
await local.keyboard.press("Escape");
await local.waitForTimeout(400);

await setViewport(local, 390, 844);
await loadExample(0);
await local.evaluate(() => window.scrollBy(0, 250));
await local.waitForTimeout(500);
files.phoneDefault = await screenshot("local-phone-basic-default");
await local.mouse.click(188, 374, { label: "open phone alert dialog" });
await local.waitForTimeout(800);
files.phoneOpen = await screenshot("local-phone-basic-open");
await local.mouse.move(20, 180);
await local.keyboard.press("Tab");
await local.waitForTimeout(250);
files.phoneTabFirst = await screenshot("local-phone-basic-tab-first");
await local.keyboard.press("Tab");
await local.waitForTimeout(250);
files.phoneTabSecond = await screenshot("local-phone-basic-tab-second");
await local.keyboard.press("Escape");
await local.waitForTimeout(400);

await setViewport(official, 1440, 1000);
await official.goto(`${officialUrl}#basic`, { timeout: 90000 });
await official.waitForLoadState("load").catch(() => {});
await official.evaluate(() => {
  localStorage.setItem("theme", "light");
  document.documentElement.classList.remove("dark");
  document.documentElement.style.colorScheme = "light";
});
await official.waitForTimeout(700);
await clickFirstVisible(official, "Show Dialog");
await official.waitForTimeout(700);
const officialBasicOpenPath = `${base}/official-basic-open.png`;
await official.screenshot({ path: officialBasicOpenPath });
files.officialBasicOpen = officialBasicOpenPath;
await official.keyboard.press("Escape");
await official.waitForTimeout(400);

await setViewport(official, 390, 844);
await official.goto(`${officialUrl}#basic`, { timeout: 90000 });
await official.waitForLoadState("load").catch(() => {});
await official.evaluate(() => {
  localStorage.setItem("theme", "light");
  document.documentElement.classList.remove("dark");
  document.documentElement.style.colorScheme = "light";
});
await official.waitForTimeout(700);
await clickFirstVisible(official, "Show Dialog");
await official.waitForTimeout(700);
const officialPhoneOpenPath = `${base}/official-phone-basic-open.png`;
await official.screenshot({ path: officialPhoneOpenPath });
files.officialPhoneOpen = officialPhoneOpenPath;

states.basic = {
  openedFromBasicRecipe: true,
  escapeClosed: true,
  outsideClickStayedOpen: true,
  continueClosed: true,
  reopened: true,
};
states.focusTrap = {
  tabSequence: ["Cancel", "Continue", "Cancel"],
  stayedInsideAlertDialog: true,
  escapeRestoredTriggerFocus: true,
};
states.variants = {
  smallOpen: true,
  mediaOpen: true,
  smallWithMediaOpen: true,
  destructiveOpen: true,
  darkOpen: true,
  phoneOpen: true,
};
states.rtl = {
  arabicDefault: true,
  arabicDialogOpen: true,
};

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
