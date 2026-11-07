const fs = await import("node:fs/promises");
const crypto = await import("node:crypto");

const task = await taskSpace(95);
const page = task.page("p1");
const official = task.page("p2");
const base =
  "/Users/cscs/IdeaProjects/angryecho/compose-foundation/docs/web-parity/button-r5/browser/interactions";
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
  await page.waitForTimeout(700);
}

async function screenshot(name) {
  const path = `${base}/${name}.png`;
  await page.screenshot({ path });
  return path;
}

async function clickElement(target, selector, text, scrollToTarget = false) {
  const clicked = await target.evaluate(
    ({ selector: query, text: expectedText, scroll }) => {
      const element = [...document.querySelectorAll(query)].find(
        (candidate) =>
          !expectedText || candidate.textContent.includes(expectedText),
      );
      if (!element) return false;
      if (scroll) element.scrollIntoView({ block: "center" });
      element.click();
      return true;
    },
    { selector, text, scroll: scrollToTarget },
  );
  if (!clicked) throw new Error(`Missing element: ${selector} ${text ?? ""}`);
}

async function hash(path) {
  const data = await fs.readFile(path);
  return crypto.createHash("sha256").update(data).digest("hex");
}

await setViewport(1440, 1000);
await page.goto("http://127.0.0.1:8777/button", { timeout: 90000 });
await waitForLocal();
await page.evaluate(() => {
  localStorage.setItem("compose-foundation.theme", "light");
});
await page.reload({ timeout: 60000 });
await waitForLocal();
await setViewport(1440, 1000);

const files = {};
const states = {};

await page.evaluate(() => {
  location.hash = "example-11";
});
await page.waitForTimeout(900);
files.groupRest = await screenshot("local-group-rest");
await page.mouse.click(841, 321, { label: "open group menu" });
await page.waitForTimeout(900);
files.groupOpen = await screenshot("local-group-menu-open");
await page.mouse.click(780, 499, { label: "open label submenu" });
await page.waitForTimeout(800);
files.groupSubmenu = await screenshot("local-group-label-submenu");
await page.mouse.click(884, 527, { label: "select Work label" });
await page.waitForTimeout(700);
files.groupWorkSelected = await screenshot("local-group-work-selected");
await page.keyboard.press("Escape");
await page.waitForTimeout(350);
await page.keyboard.press("Escape");
await page.waitForTimeout(1000);
files.groupClosed = await screenshot("local-group-menu-closed");
await page.mouse.click(841, 321, { label: "reopen group menu" });
await page.waitForTimeout(1200);
await page.mouse.click(780, 499, { label: "reopen label submenu" });
await page.waitForTimeout(1000);
files.groupReopened = await screenshot("local-group-reopened-label-submenu");
await page.keyboard.press("Escape");
await page.waitForTimeout(350);
await page.keyboard.press("Escape");
await page.waitForTimeout(500);

await setViewport(390, 844);
await page.evaluate(() => {
  location.hash = "example-11";
});
await page.waitForTimeout(1000);
await page.evaluate(() => window.scrollBy(0, 480));
await page.waitForTimeout(500);
files.groupPhoneRest = await screenshot("local-group-phone-rest");
await page.mouse.click(310, 377, { label: "open phone group menu" });
await page.waitForTimeout(900);
files.groupPhoneOpen = await screenshot("local-group-phone-menu-open");
await page.mouse.click(233, 552, { label: "open phone label submenu" });
await page.waitForTimeout(900);
files.groupPhoneSubmenu = await screenshot("local-group-phone-label-submenu");
await page.keyboard.press("Escape");
await page.waitForTimeout(350);
await page.keyboard.press("Escape");
await page.waitForTimeout(400);

await setViewport(1440, 1000);
await page.evaluate(() => {
  location.hash = "example-12";
});
await page.waitForTimeout(1000);
files.linkRest = await screenshot("local-link-rest");
await page.mouse.move(714, 348, { label: "hover styled link" });
await page.waitForTimeout(350);
files.linkHover = await screenshot("local-link-hover");
const beforeActivation = await page.evaluate(() => ({
  hash: location.hash,
  historyLength: history.length,
}));
await page.mouse.down();
await page.waitForTimeout(250);
files.linkPointerFocus = await screenshot("local-link-pointer-focus");
await page.mouse.up();
await page.waitForTimeout(700);
files.linkActivated = await screenshot("local-link-activated");
const afterActivation = await page.evaluate(() => ({
  hash: location.hash,
  historyLength: history.length,
}));
states.linkActivation = { beforeActivation, afterActivation };

await page.evaluate(() => {
  location.hash = "example-13";
});
await page.waitForTimeout(900);
files.rtlArabic = await screenshot("local-rtl-arabic");
await page.mouse.click(480, 184, { label: "open RTL language selector" });
await page.waitForTimeout(500);
files.rtlArabicPopup = await screenshot("local-rtl-language-popup");
await page.mouse.click(449, 154, { label: "select English" });
await page.waitForTimeout(500);
files.rtlEnglish = await screenshot("local-rtl-english");
await page.mouse.click(480, 184, { label: "reopen RTL language selector" });
await page.waitForTimeout(500);
files.rtlEnglishPopup = await screenshot("local-rtl-language-popup-after-english");
await page.mouse.click(460, 238, { label: "select Hebrew" });
await page.waitForTimeout(500);
files.rtlHebrew = await screenshot("local-rtl-hebrew");

const officialUrl =
  "https://ui.shadcn.com/docs/components/base/button#button-group";
await official.cdp("Emulation.setDeviceMetricsOverride", {
  width: 1440,
  height: 1000,
  deviceScaleFactor: 1,
  mobile: false,
});
await official.goto(officialUrl, { timeout: 90000 });
await official.waitForLoadState("load").catch(() => {});
await official.evaluate(() => {
  localStorage.setItem("theme", "light");
  document.documentElement.classList.remove("dark");
  document.documentElement.style.colorScheme = "light";
});
await official.keyboard.press("Escape");
await official.waitForTimeout(250);
await official.keyboard.press("Escape");
await official.waitForTimeout(500);
await clickElement(
  official,
  'button[aria-label="More Options"]',
  null,
  true,
);
await official.waitForTimeout(900);
const officialGroupOpenPath = `${base}/official-group-menu-open.png`;
await official.screenshot({ path: officialGroupOpenPath });
files.officialGroupOpen = officialGroupOpenPath;
await clickElement(
  official,
  "[role=menuitem]",
  "Label As...",
);
await official.waitForTimeout(900);
const officialGroupSubmenuPath = `${base}/official-group-label-submenu.png`;
await official.screenshot({ path: officialGroupSubmenuPath });
files.officialGroupSubmenu = officialGroupSubmenuPath;

await official.cdp("Emulation.setDeviceMetricsOverride", {
  width: 390,
  height: 844,
  deviceScaleFactor: 1,
  mobile: false,
});
await official.goto(officialUrl, { timeout: 90000 });
await official.waitForLoadState("load").catch(() => {});
await official.evaluate(() => {
  localStorage.setItem("theme", "light");
  document.documentElement.classList.remove("dark");
  document.documentElement.style.colorScheme = "light";
});
await official.keyboard.press("Escape");
await official.waitForTimeout(250);
await official.keyboard.press("Escape");
await official.waitForTimeout(500);
await clickElement(
  official,
  'button[aria-label="More Options"]',
  null,
  true,
);
await official.waitForTimeout(900);
const officialGroupPhoneOpenPath = `${base}/official-group-phone-menu-open.png`;
await official.screenshot({ path: officialGroupPhoneOpenPath });
files.officialGroupPhoneOpen = officialGroupPhoneOpenPath;
await clickElement(
  official,
  "[role=menuitem]",
  "Label As...",
);
await official.waitForTimeout(900);
const officialGroupPhoneSubmenuPath =
  `${base}/official-group-phone-label-submenu.png`;
await official.screenshot({ path: officialGroupPhoneSubmenuPath });
files.officialGroupPhoneSubmenu = officialGroupPhoneSubmenuPath;

const hashes = {};
for (const [name, path] of Object.entries(files)) {
  hashes[name] = await hash(path);
}
await fs.writeFile(
  `${base}/interactions.json`,
  JSON.stringify(
    {
      capturedAt: new Date().toISOString(),
      groupMenu: {
        localDesktopEllipsis: { x: 841, y: 321 },
        localPhoneEllipsis: { x: 310, y: 377 },
        officialTrigger: 'button[aria-label="More Options"]',
        officialLabelTrigger: '[role=menuitem] containing "Label As..."',
      },
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
    linkActivation: states.linkActivation,
  }),
);
