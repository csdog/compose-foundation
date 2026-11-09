import { createHash } from "node:crypto";
import fs from "node:fs/promises";

const root =
  "/Users/cscs/IdeaProjects/angryecho/compose-foundation/docs/web-parity/checkbox-r3/verify-30c";
const localUrl = "http://127.0.0.1:8765/checkbox";
const officialUrl = "https://ui.shadcn.com/docs/components/base/checkbox";
const anchors = [
  "invalid-state",
  "basic",
  "description",
  "disabled",
  "group",
  "table",
  "rtl",
];
const views = [
  { name: "desktop-light", width: 1440, height: 1000, theme: "light" },
  { name: "desktop-dark", width: 1440, height: 1000, theme: "dark" },
  { name: "phone-light", width: 390, height: 844, theme: "light" },
  { name: "phone-dark", width: 390, height: 844, theme: "dark" },
];

await fs.mkdir(`${root}/screens`, { recursive: true });
await fs.mkdir(`${root}/dom`, { recursive: true });
await fs.mkdir(`${root}/interactions`, { recursive: true });

const task = await taskSpace("checkbox web parity 30c");
let official = (await task.pages()).find((page) => page.label === "p1");
if (!official) official = await task.newPage();
let local = (await task.pages()).find(
  (page) => page.label !== official.label,
);
if (!local) local = await task.newPage();

const result = {
  component: "checkbox",
  stage: "30c-browser",
  capturedAt: new Date().toISOString(),
  localUrl,
  officialUrl,
  distribution: {
    webAppJs:
      "09e5657c31bc39e41f19ef352ab17f278884087267998ea34c1475800fade8a3",
    primaryWasm:
      "08e904ec38a6a81a4809a853acf68022f83905286827e804e88b262c37f2954d",
    indexHtml:
      "0febf14ee7bfce11bb793449f281d55ba8242d006a319fdfe6cb0c8b32b31fd5",
  },
  views: {},
  interactions: {},
  failures: [],
};

async function setViewport(page, view) {
  await page.cdp("Emulation.setDeviceMetricsOverride", {
    width: view.width,
    height: view.height,
    deviceScaleFactor: 1,
    mobile: false,
  });
}

async function setOfficialTheme(page, theme) {
  await page.evaluate((nextTheme) => {
    if (!window.__checkboxThemeGuard) {
      window.__checkboxThemeGuard = true;
      window.addEventListener(
        "storage",
        (event) => event.stopImmediatePropagation(),
        true,
      );
    }
    localStorage.setItem("theme", nextTheme);
    document.documentElement.classList.toggle("dark", nextTheme === "dark");
    document.documentElement.style.colorScheme = nextTheme;
  }, theme);
  await page.waitForTimeout(150);
}

async function waitForLocal(page) {
  await page.waitForFunction(
    () => {
      const root = document.getElementById("compose-root");
      const canvas =
        root && root.firstElementChild && root.firstElementChild.firstElementChild;
      return !!canvas && document.body.style.height.includes("calc");
    },
    undefined,
    { timeout: 60000 },
  );
  await page.waitForTimeout(900);
}

async function setLocalTheme(page, theme) {
  await setViewport(page, views[0]);
  await page.goto(localUrl, { timeout: 90000 });
  await waitForLocal(page);
  const current = await page.evaluate(() =>
    localStorage.getItem("compose-foundation.theme"),
  );
  if (current !== theme) {
    await page.evaluate((nextTheme) => {
      localStorage.setItem("compose-foundation.theme", nextTheme);
    }, theme);
    await page.reload({ timeout: 90000 });
    await waitForLocal(page);
  }
  await page.waitForTimeout(500);
}

async function pngInfo(path) {
  const bytes = await fs.readFile(path);
  return {
    path,
    sha256: createHash("sha256").update(bytes).digest("hex"),
    bytes: bytes.length,
    width: bytes.readUInt32BE(16),
    height: bytes.readUInt32BE(20),
  };
}

async function screenshot(page, path) {
  await page.screenshot({ path });
  return pngInfo(path);
}

async function officialSectionTop(page, id) {
  return page.evaluate((sectionId) => {
    const byId = document.getElementById(sectionId);
    const headings = [...document.querySelectorAll("main h2")];
    const label = sectionId.replaceAll("-", " ");
    const heading =
      byId ||
      headings.find(
        (candidate) =>
          candidate.textContent
            .replaceAll("#", "")
            .trim()
            .toLowerCase() === label,
      );
    if (!heading) return null;
    const top = heading.getBoundingClientRect().top + window.scrollY - 64;
    window.scrollTo(0, Math.max(0, top));
    return { top, scrollY: window.scrollY };
  }, id);
}

async function localSection(page, id) {
  await page.evaluate((nextHash) => {
    window.location.hash = `#${nextHash}`;
  }, id);
  await page.waitForTimeout(500);
  return page.evaluate(() => ({
    hash: location.hash,
    scrollY: window.scrollY,
  }));
}

await official.goto(officialUrl, { timeout: 90000 });
await official.waitForLoadState("load").catch(() => {});
await official.waitForTimeout(1200);
await local.goto(localUrl, { timeout: 90000 });
await waitForLocal(local);

const officialDom = await official.evaluate(() => {
  const text = (element) =>
    element
      ? element.textContent.trim().replace(/\s+/g, " ")
      : null;
  const main = document.querySelector("main") || document.body;
  return {
    title: text(document.querySelector("h1")),
    headings: [...main.querySelectorAll("h2, h3")].map((element) => ({
      tag: element.tagName,
      id: element.id,
      text: text(element),
    })),
    examples: document.querySelectorAll(
      "[data-slot=component-preview], .component-preview",
    ).length,
    mainRect: main.getBoundingClientRect().toJSON(),
    clientWidth: document.documentElement.clientWidth,
    scrollHeight: document.documentElement.scrollHeight,
  };
});
const localDom = await local.evaluate(() => {
  const composeRoot = document.getElementById("compose-root");
  const canvas =
    composeRoot &&
    composeRoot.firstElementChild &&
    composeRoot.firstElementChild.firstElementChild;
  return {
    title: document.title,
    bodyHeight: document.body.style.height,
    clientWidth: document.documentElement.clientWidth,
    scrollHeight: document.documentElement.scrollHeight,
    composeRoot: composeRoot ? composeRoot.getBoundingClientRect().toJSON() : null,
    canvas: canvas ? canvas.getBoundingClientRect().toJSON() : null,
    canvasBitmap: canvas
      ? { width: canvas.width, height: canvas.height }
      : null,
    devicePixelRatio: window.devicePixelRatio,
    wasmResources: performance
      .getEntriesByType("resource")
      .map((entry) => entry.name)
      .filter((name) => name.endsWith(".wasm")),
  };
});

await fs.writeFile(
  `${root}/dom/official.json`,
  JSON.stringify(officialDom, null, 2),
);
await fs.writeFile(
  `${root}/dom/local.json`,
  JSON.stringify(localDom, null, 2),
);
await fs.writeFile(
  `${root}/dom/official.html`,
  await official.evaluate(() => document.documentElement.outerHTML),
);
await fs.writeFile(
  `${root}/dom/local.html`,
  await local.evaluate(() => document.documentElement.outerHTML),
);

result.officialDom = officialDom;
result.localDom = localDom;

for (const view of views) {
  await setViewport(official, view);
  await setOfficialTheme(official, view.theme);
  await official.evaluate(() => window.scrollTo(0, 0));
  await official.waitForTimeout(200);

  await setLocalTheme(local, view.theme);
  await setViewport(local, view);
  await local.evaluate(() => window.scrollTo(0, 0));
  await local.waitForTimeout(500);

  const viewResult = {
    top: {
      official: await screenshot(
        official,
        `${root}/screens/official-${view.name}-top.png`,
      ),
      local: await screenshot(
        local,
        `${root}/screens/local-${view.name}-top.png`,
      ),
    },
    sections: {},
  };

  for (const anchor of anchors) {
    const officialPosition = await officialSectionTop(official, anchor);
    await setOfficialTheme(official, view.theme);
    await official.waitForTimeout(250);
    const localPosition = await localSection(local, anchor);
    if (!officialPosition) {
      result.failures.push(`${view.name}: missing official section ${anchor}`);
    }
    if (
      anchor !== "invalid-state" &&
      (!Number.isFinite(localPosition.scrollY) ||
        localPosition.scrollY < 64)
    ) {
      result.failures.push(
        `${view.name}: local section ${anchor} did not scroll (${localPosition.scrollY})`,
      );
    }
    viewResult.sections[anchor] = {
      officialPosition,
      localPosition,
      official: await screenshot(
        official,
        `${root}/screens/official-${view.name}-${anchor}.png`,
      ),
      local: await screenshot(
        local,
        `${root}/screens/local-${view.name}-${anchor}.png`,
      ),
    };
  }

  const topOfficial = viewResult.top.official;
  const topLocal = viewResult.top.local;
  viewResult.dimensionCheck = {
    official:
      topOfficial.width === view.width && topOfficial.height === view.height,
    local: topLocal.width === view.width && topLocal.height === view.height,
  };
  if (!viewResult.dimensionCheck.official || !viewResult.dimensionCheck.local) {
    result.failures.push(`${view.name}: screenshot bitmap dimensions mismatch`);
  }
  result.views[view.name] = viewResult;
}

await setLocalTheme(local, "light");
await setViewport(local, views[0]);
await localSection(local, "basic");
await local.waitForTimeout(300);

result.interactions.basic = {
  before: await screenshot(
    local,
    `${root}/interactions/basic-before.png`,
  ),
};
await local.mouse.click(610, 294, { label: "toggle basic checkbox" });
await local.waitForTimeout(350);
result.interactions.basic.checked = await screenshot(
  local,
  `${root}/interactions/basic-checked.png`,
);
await local.mouse.click(610, 294, { label: "toggle basic checkbox back" });
await local.waitForTimeout(350);
result.interactions.basic.restored = await screenshot(
  local,
  `${root}/interactions/basic-restored.png`,
);

await localSection(local, "table");
await local.waitForTimeout(300);
result.interactions.table = {
  before: await screenshot(
    local,
    `${root}/interactions/table-before.png`,
  ),
};
await local.mouse.click(450, 217, { label: "toggle Sarah row checkbox" });
await local.waitForTimeout(350);
result.interactions.table.cleared = await screenshot(
  local,
  `${root}/interactions/table-cleared.png`,
);
await local.mouse.click(450, 217, { label: "restore Sarah row checkbox" });
await local.waitForTimeout(350);
result.interactions.table.restored = await screenshot(
  local,
  `${root}/interactions/table-restored.png`,
);

for (const target of ["basic", "table"]) {
  const pair = result.interactions[target];
  const changed =
    pair.before.sha256 !==
    (target === "basic" ? pair.checked : pair.cleared).sha256;
  const toggledBack =
    pair.before.sha256 === pair.restored.sha256 ||
    pair.restored.sha256 !==
      (target === "basic" ? pair.checked : pair.cleared).sha256;
  pair.clickChanged = changed;
  pair.secondClickChanged = toggledBack;
  if (!changed || !toggledBack) {
    result.failures.push(`${target}: click interaction did not change twice`);
  }
}

await localSection(local, "basic");
await local.mouse.click(1291, 32, { label: "switch docs theme live" });
await local.waitForFunction(
  () => localStorage.getItem("compose-foundation.theme") === "dark",
  undefined,
  { timeout: 5000 },
);
await local.waitForTimeout(500);
result.interactions.liveThemeDark = await screenshot(
  local,
  `${root}/interactions/basic-live-dark.png`,
);
result.interactions.liveThemeChanged =
  result.interactions.liveThemeDark.sha256 !==
  result.interactions.basic.restored.sha256;
if (!result.interactions.liveThemeChanged) {
  result.failures.push("live local theme switch did not change the render");
}

result.summary = {
  viewCount: views.length,
  sectionCount: anchors.length,
  screenshotCount:
    views.length * (2 + anchors.length * 2) + 7,
  officialExamples: officialDom.examples,
  officialHeadingCount: officialDom.headings.length,
  localWasmResources: localDom.wasmResources.length,
  failures: result.failures,
};

await fs.writeFile(
  `${root}/metadata.json`,
  JSON.stringify(result, null, 2),
);
console.log(
  JSON.stringify({
    ok: result.failures.length === 0,
    ...result.summary,
    metadata: `${root}/metadata.json`,
  }),
);
await task.finish({ keep: [] });
