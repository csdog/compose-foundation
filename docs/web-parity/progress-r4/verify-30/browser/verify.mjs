import { createHash } from "node:crypto";
import { mkdir, readFile, writeFile } from "node:fs/promises";

const base = "/Users/cscs/IdeaProjects/angryecho/compose-foundation/docs/web-parity/progress-r4/verify-30/browser";
const localUrl = "http://127.0.0.1:8775/progress";
const officialUrl = "https://ui.shadcn.com/docs/components/base/progress";
const views = [
  { name: "desktop-light", width: 1440, height: 1000, theme: "light" },
  { name: "desktop-dark", width: 1440, height: 1000, theme: "dark" },
  { name: "phone-light", width: 390, height: 844, theme: "light" },
  { name: "phone-dark", width: 390, height: 844, theme: "dark" },
];
const failures = [];
const observations = [];
const hashes = {};
let task;

await mkdir(base, { recursive: true });

async function hashFile(path) {
  return createHash("sha256").update(await readFile(path)).digest("hex");
}

async function sha(path) {
  hashes[path.slice(base.length + 1)] = await hashFile(path);
  return path;
}

function check(condition, message, details = undefined) {
  if (!condition) {
    failures.push(details === undefined ? message : `${message}: ${JSON.stringify(details)}`);
  }
}

async function setViewport(page, width, height) {
  await page.cdp("Emulation.setDeviceMetricsOverride", {
    width,
    height,
    deviceScaleFactor: 1,
    mobile: false,
  });
  await page.waitForTimeout(250);
}

async function resetScroll(page) {
  await page.mouse.move(720, 500);
  await page.mouse.wheel(0, -6000, { label: "reset page scroll" });
  await page.waitForTimeout(500);
}

async function setLocalTheme(page, theme) {
  await setViewport(page, 1440, 1000);
  await resetScroll(page);
  const current = await page.evaluate(() => localStorage.getItem("compose-foundation.theme"));
  if (current !== theme) {
    await page.mouse.click(1291, 32, { label: "switch docs theme" });
    await page.waitForFunction(
      (expected) => localStorage.getItem("compose-foundation.theme") === expected,
      theme,
      { timeout: 5000 },
    );
    await page.waitForTimeout(500);
  }
}

async function setOfficialTheme(page, theme) {
  await setViewport(page, 1440, 1000);
  await page.evaluate((expected) => {
    localStorage.setItem("theme", expected);
    document.documentElement.classList.toggle("dark", expected === "dark");
    document.documentElement.style.colorScheme = expected;
    window.scrollTo(0, 0);
  }, theme);
  await page.waitForTimeout(350);
}

async function localMetadata(page, view) {
  return page.evaluate((expected) => {
    const root = document.getElementById("compose-root");
    const canvases = [];
    const walk = (node) => {
      if (!node) return;
      if (node.tagName === "CANVAS") {
        canvases.push({
          cssHeight: node.getBoundingClientRect().height,
          cssWidth: node.getBoundingClientRect().width,
          height: node.height,
          width: node.width,
        });
      }
      if (node.shadowRoot) {
        for (const child of node.shadowRoot.querySelectorAll("*")) walk(child);
      }
      if (node.children) {
        for (const child of node.children) walk(child);
      }
    };
    walk(root);
    return {
      bodyHeight: document.body.style.height,
      canvases,
      clientHeight: document.documentElement.clientHeight,
      clientWidth: document.documentElement.clientWidth,
      devicePixelRatio,
      root: root?.getBoundingClientRect().toJSON() ?? null,
      theme: localStorage.getItem("compose-foundation.theme"),
      title: document.title,
      url: location.href,
      viewportHeight: window.innerHeight,
      viewportWidth: window.innerWidth,
      view: expected,
    };
  }, view);
}

async function captureClip(page, name, clip) {
  const result = await page.cdp("Page.captureScreenshot", {
    captureBeyondViewport: false,
    clip: { ...clip, scale: 1 },
    format: "png",
    fromSurface: true,
  });
  const path = `${base}/${name}.png`;
  await writeFile(path, Buffer.from(result.data, "base64"));
  return sha(path);
}

try {
  task = await taskSpace("compose-foundation progress r4 stage 30c");
  const local = task.page("p1");
  const official = await task.newPage();

  await setViewport(local, 1440, 1000);
  await local.goto(localUrl, { timeout: 90000 });
  await local.waitForFunction(
    () => {
      const root = document.getElementById("compose-root");
      return !!root && document.body.style.height.includes("calc");
    },
    undefined,
    { timeout: 60000 },
  );
  await local.waitForTimeout(1200);

  const localFiles = {};
  const localState = {};
  for (const view of views) {
    await setLocalTheme(local, view.theme);
    await setViewport(local, view.width, view.height);
    await resetScroll(local);
    await local.waitForTimeout(700);
    const path = `${base}/local-${view.name}.png`;
    await local.screenshot({ path });
    localFiles[view.name] = await sha(path);
    localState[view.name] = await localMetadata(local, view);
    check(localState[view.name].theme === view.theme, `local ${view.name} theme`, localState[view.name]);
    check(localState[view.name].viewportWidth === view.width, `local ${view.name} width`);
    check(localState[view.name].viewportHeight === view.height, `local ${view.name} height`);
    check(localState[view.name].bodyHeight.includes("calc"), `local ${view.name} root sizing`);
    check(localState[view.name].canvases.length > 0, `local ${view.name} canvas missing`);
    for (const canvas of localState[view.name].canvases) {
      check(
        Math.abs(canvas.width - canvas.cssWidth * localState[view.name].devicePixelRatio) <= 1 &&
          Math.abs(canvas.height - canvas.cssHeight * localState[view.name].devicePixelRatio) <= 1,
        `local ${view.name} canvas bitmap/CSS mismatch`,
        canvas,
      );
    }
  }

  const localSnapshot = await local.snapshot({ scope: "full_page" });
  const localSnapshotPath = `${base}/local-full-snapshot.txt`;
  await writeFile(localSnapshotPath, localSnapshot);
  for (const phrase of [
    "Displays an indicator showing the completion progress of a task",
    "With label and value",
    "Use ProgressLabel and ProgressValue",
    "A progress bar that can be controlled by a slider.",
    "To enable RTL support in shadcn/ui",
  ]) {
    check(localSnapshot.includes(phrase), `local page missing copy`, phrase);
  }
  const copyOrder = [
    "Displays an indicator showing the completion progress of a task",
    "Use ProgressLabel and ProgressValue to add a label and value display.",
    "A progress bar that can be controlled by a slider.",
    "To enable RTL support in shadcn/ui",
  ].map((phrase) => localSnapshot.indexOf(phrase));
  check(
    copyOrder.every((position, index) => position >= 0 && (index === 0 || position > copyOrder[index - 1])),
    "local recipe copy order",
    copyOrder,
  );

  const localAx = await local.cdp("Accessibility.getFullAXTree");
  const roleCounts = {};
  const axNames = [];
  for (const node of localAx.nodes) {
    const role = node.role?.value ?? "none";
    roleCounts[role] = (roleCounts[role] ?? 0) + 1;
    const name = node.name?.value;
    if (name && /progress|percent|upload/i.test(name)) axNames.push({ role, name });
  }
  check(
    axNames.some((item) => /A progress bar that can be controlled/.test(item.name)),
    "local accessible text missing controlled progress copy",
  );
  observations.push({
    kind: "accessibility",
    note:
      "Chromium AX tree exposes Progress page text but no progressbar/slider roles for the Compose canvas.",
    progressRoleCount: roleCounts.progressbar ?? 0,
    sliderRoleCount: roleCounts.slider ?? 0,
    progressNames: axNames,
  });

  await official.goto(officialUrl, { timeout: 90000 });
  await official.waitForLoadState("load").catch(() => {});
  await official.waitForTimeout(1400);
  const officialFiles = {};
  const officialState = {};
  for (const view of views) {
    await setOfficialTheme(official, view.theme);
    await setViewport(official, view.width, view.height);
    await official.waitForTimeout(500);
    const path = `${base}/official-${view.name}.png`;
    await official.screenshot({ path });
    officialFiles[view.name] = await sha(path);
    officialState[view.name] = {
      dark: await official.evaluate(() => document.documentElement.classList.contains("dark")),
      theme: await official.evaluate(() => localStorage.getItem("theme")),
      view,
    };
    check(officialState[view.name].dark === (view.theme === "dark"), `official ${view.name} theme`);
  }

  const officialDom = await official.evaluate(() => {
    const text = (node) =>
      node?.textContent?.trim().replace(/\s+/g, " ").replace(/#$/, "") ?? null;
    const main = document.querySelector("main") ?? document.body;
    return {
      title: text(document.querySelector("h1")),
      headings: [...main.querySelectorAll("h2, h3")].map((node) => ({
        level: node.tagName.toLowerCase(),
        text: text(node),
      })),
      examples: main.querySelectorAll("[data-slot=component-preview], .component-preview").length,
      progressNodes: [...document.querySelectorAll("[role=progressbar], [data-slot=progress], [data-slot=progress-track], [data-slot=progress-indicator]")].map(
        (node) => ({
          ariaValueNow: node.getAttribute("aria-valuenow"),
          role: node.getAttribute("role"),
          slot: node.getAttribute("data-slot"),
          tag: node.tagName.toLowerCase(),
        }),
      ),
      toc: [...document.querySelectorAll('nav a[href^="#"]')].map(text).filter(Boolean),
      url: location.href,
    };
  });
  await writeFile(`${base}/official-dom.json`, JSON.stringify(officialDom, null, 2));
  check(officialDom.title === "Progress", "official title", officialDom.title);
  check(officialDom.examples === 4, "official preview count", officialDom.examples);
  for (const heading of ["Composition", "Label", "Controlled", "RTL"]) {
    check(
      officialDom.headings.some((item) => item.text === heading),
      "official heading missing",
      heading,
    );
  }

  await official.close();
  await local.waitForTimeout(250);
  await setLocalTheme(local, "light");
  await setViewport(local, 1440, 1000);
  await resetScroll(local);
  await local.mouse.wheel(0, 2000, { label: "scroll to controlled progress" });
  await local.waitForTimeout(700);
  const interactionClip = { x: 430, y: 250, width: 660, height: 400 };
  const barClip = { x: 430, y: 385, width: 460, height: 100 };
  const controlledInitial = await captureClip(local, "controlled-initial", interactionClip);
  await local.mouse.click(685, 446, { label: "focus controlled slider" });
  await local.waitForTimeout(350);
  const controlledFocused = await captureClip(local, "controlled-focused", interactionClip);
  await local.keyboard.press("End");
  await local.waitForTimeout(500);
  const controlledEnd = await captureClip(local, "controlled-end-100", interactionClip);
  await local.keyboard.press("Home");
  await local.waitForTimeout(500);
  const controlledHome = await captureClip(local, "controlled-home-0", interactionClip);
  const barHome = await captureClip(local, "controlled-bar-home", barClip);
  await local.mouse.click(820, 420, { label: "click inert progress track" });
  await local.waitForTimeout(400);
  const barTrackAfter = await captureClip(local, "controlled-bar-track-after", barClip);
  await local.mouse.click(820, 446, { label: "set controlled slider from track" });
  await local.waitForTimeout(500);
  const controlledPointer = await captureClip(local, "controlled-pointer-set", interactionClip);
  check(controlledInitial !== controlledEnd, "controlled End did not change progress");
  check(controlledEnd !== controlledHome, "controlled Home did not change progress");
  check(barHome === barTrackAfter, "progress track click changed progress");
  check(controlledHome !== controlledPointer, "controlled slider pointer did not change progress");

  await resetScroll(local);
  await local.mouse.wheel(0, 2700, { label: "scroll to RTL progress" });
  await local.waitForTimeout(700);
  await captureClip(local, "rtl-arabic", { x: 380, y: 100, width: 420, height: 300 });
  await local.mouse.click(435, 158, { label: "open RTL language menu" });
  await local.waitForTimeout(500);
  const rtlMenuSnapshot = await local.snapshot();
  await writeFile(`${base}/rtl-menu-snapshot.txt`, rtlMenuSnapshot);
  const rtlMenu = await captureClip(local, "rtl-menu", { x: 380, y: 100, width: 420, height: 300 });
  check(
    ["English", "Arabic", "Hebrew"].every((language) => rtlMenuSnapshot.includes(language)),
    "RTL language menu items missing",
  );
  await local.mouse.click(435, 201, { label: "switch RTL preview to English" });
  await local.waitForTimeout(600);
  const rtlEnglish = await captureClip(local, "rtl-english", { x: 380, y: 100, width: 420, height: 300 });
  check(rtlMenu !== rtlEnglish, "RTL English selection did not change preview");

  const buildEntry = "/Users/cscs/IdeaProjects/angryecho/compose-foundation/app/webApp/build/dist/wasmJs/productionExecutable/index.html";
  const summary = {
    stage: "30c-browser",
    component: "progress",
    capturedAt: new Date().toISOString(),
    browser: "Ego Chromium",
    viewportBrowserScale: 1,
    buildEntrySha256: await hashFile(buildEntry),
    localUrl,
    officialUrl,
    localFiles,
    officialFiles,
    localState,
    officialState,
    officialDom,
    interactions: {
      controlledInitial,
      controlledFocused,
      controlledEnd,
      controlledHome,
      barHome,
      barTrackAfter,
      controlledPointer,
      rtlMenu,
      rtlEnglish,
      assertions: {
        endChanged: controlledInitial !== controlledEnd,
        homeChanged: controlledEnd !== controlledHome,
        progressTrackInert: barHome === barTrackAfter,
        sliderPointerChanged: controlledHome !== controlledPointer,
        rtlMenuChanged: rtlMenu !== rtlEnglish,
      },
    },
    accessibility: {
      roleCounts: Object.fromEntries(
        Object.entries(roleCounts).filter(([role]) => ["generic", "progressbar", "slider"].includes(role)),
      ),
      progressNames: axNames,
    },
    observations,
    hashes,
    failures,
    status: failures.length === 0 ? "pass" : "fail",
  };
  const summaryPath = `${base}/summary.json`;
  await writeFile(summaryPath, JSON.stringify(summary, null, 2));

  console.log(
    JSON.stringify(
      {
        status: summary.status,
        failures,
        screenshots: Object.keys(localFiles).length + Object.keys(officialFiles).length,
        summaryPath,
        buildEntrySha256: summary.buildEntrySha256,
      },
      null,
      2,
    ),
  );
  if (failures.length > 0) process.exitCode = 1;
} catch (error) {
  failures.push(`browser batch aborted: ${String(error?.message ?? error)}`);
  const summaryPath = `${base}/summary.json`;
  await writeFile(
    summaryPath,
    JSON.stringify({ stage: "30c-browser", component: "progress", failures, status: "fail" }, null, 2),
  );
  console.error(JSON.stringify({ status: "fail", failures, summaryPath }, null, 2));
  process.exitCode = 1;
} finally {
  if (task) await task.finish({ keep: [] }).catch(() => {});
}
