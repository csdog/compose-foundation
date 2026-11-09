import { createHash } from "node:crypto";
import fs from "node:fs/promises";
import { chromium } from "/Users/cscs/.agents/skills/guizang-social-card-skill/node_modules/playwright-core/index.mjs";

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

const browser = await chromium.launch({
  headless: true,
  executablePath:
    "/Applications/Google Chrome.app/Contents/MacOS/Google Chrome",
  args: ["--no-sandbox", "--disable-dev-shm-usage"],
});
const context = await browser.newContext({
  viewport: { width: 1440, height: 1000 },
  deviceScaleFactor: 1,
});
const official = await context.newPage();
const local = await context.newPage();

const result = {
  component: "checkbox",
  stage: "30c-browser",
  runtime: "headless Chrome via installed playwright-core",
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

async function setOfficialTheme(theme) {
  await official.evaluate((nextTheme) => {
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
  await official.waitForTimeout(150);
}

async function waitForLocal() {
  await local.waitForFunction(
    () => {
      const rootNode = document.getElementById("compose-root");
      const canvas =
        rootNode &&
        rootNode.firstElementChild &&
        rootNode.firstElementChild.firstElementChild;
      return !!canvas && document.body.style.height.includes("calc");
    },
    undefined,
    { timeout: 60000 },
  );
  await local.waitForTimeout(900);
}

async function setLocalTheme(theme) {
  await local.setViewportSize({ width: 1440, height: 1000 });
  await local.goto(localUrl, { timeout: 90000 });
  await waitForLocal();
  const current = await local.evaluate(() =>
    localStorage.getItem("compose-foundation.theme"),
  );
  if (current !== theme) {
    await local.evaluate((nextTheme) => {
      localStorage.setItem("compose-foundation.theme", nextTheme);
    }, theme);
    await local.reload({ timeout: 90000 });
    await waitForLocal();
  }
  await local.waitForTimeout(400);
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
  await page.screenshot({ path, animations: "disabled" });
  return pngInfo(path);
}

async function officialSectionTop(id) {
  return official.evaluate((sectionId) => {
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

async function localSection(id) {
  await local.evaluate((nextHash) => {
    window.location.hash = `#${nextHash}`;
  }, id);
  await local.waitForTimeout(500);
  return local.evaluate(() => ({
    hash: location.hash,
    scrollY: window.scrollY,
  }));
}

await official.goto(officialUrl, { timeout: 90000, waitUntil: "load" });
await official.waitForTimeout(1200);
await local.goto(localUrl, { timeout: 90000, waitUntil: "load" });
await waitForLocal();

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
  const canvases = [];
  const visit = (rootNode) => {
    for (const element of rootNode.querySelectorAll("*")) {
      if (element.tagName === "CANVAS") canvases.push(element);
      if (element.shadowRoot) visit(element.shadowRoot);
    }
  };
  if (composeRoot) visit(composeRoot);
  const canvas = canvases[0] || null;
  return {
    title: document.title,
    bodyHeight: document.body.style.height,
    clientWidth: document.documentElement.clientWidth,
    scrollHeight: document.documentElement.scrollHeight,
    composeRoot: composeRoot
      ? composeRoot.getBoundingClientRect().toJSON()
      : null,
    canvas: canvas ? canvas.getBoundingClientRect().toJSON() : null,
    canvasBitmap: canvas
      ? { width: canvas.width, height: canvas.height }
      : null,
    canvasCount: canvases.length,
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
  await official.setViewportSize({ width: view.width, height: view.height });
  await setOfficialTheme(view.theme);
  await official.evaluate(() => window.scrollTo(0, 0));
  await official.waitForTimeout(200);

  await setLocalTheme(view.theme);
  await local.setViewportSize({ width: view.width, height: view.height });
  await local.evaluate(() => window.scrollTo(0, 0));
  await local.waitForTimeout(400);

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
    const officialPosition = await officialSectionTop(anchor);
    await setOfficialTheme(view.theme);
    await official.waitForTimeout(250);
    const localPosition = await localSection(anchor);
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

  const dimensionCheck = {
    official:
      viewResult.top.official.width === view.width &&
      viewResult.top.official.height === view.height,
    local:
      viewResult.top.local.width === view.width &&
      viewResult.top.local.height === view.height,
  };
  viewResult.dimensionCheck = dimensionCheck;
  if (!dimensionCheck.official || !dimensionCheck.local) {
    result.failures.push(`${view.name}: screenshot bitmap dimensions mismatch`);
  }
  result.views[view.name] = viewResult;
}

await setLocalTheme("light");
await local.setViewportSize({ width: 1440, height: 1000 });
await localSection("basic");
await local.waitForTimeout(300);

result.interactions.basic = {
  before: await screenshot(local, `${root}/interactions/basic-before.png`),
};
await local.mouse.click(610, 294);
await local.waitForTimeout(350);
result.interactions.basic.checked = await screenshot(
  local,
  `${root}/interactions/basic-checked.png`,
);
await local.mouse.click(610, 294);
await local.waitForTimeout(350);
result.interactions.basic.restored = await screenshot(
  local,
  `${root}/interactions/basic-restored.png`,
);

await localSection("table");
await local.waitForTimeout(300);
result.interactions.table = {
  before: await screenshot(local, `${root}/interactions/table-before.png`),
};
await local.mouse.click(450, 217);
await local.waitForTimeout(350);
result.interactions.table.cleared = await screenshot(
  local,
  `${root}/interactions/table-cleared.png`,
);
await local.mouse.click(450, 217);
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
  pair.clickChanged = changed;
  pair.secondClickChanged =
    pair.restored.sha256 !==
    (target === "basic" ? pair.checked : pair.cleared).sha256;
  if (!pair.clickChanged || !pair.secondClickChanged) {
    result.failures.push(`${target}: click interaction did not change twice`);
  }
}

await localSection("basic");
await local.mouse.click(1323, 33);
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
  screenshotCount: views.length * (2 + anchors.length * 2) + 7,
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
await browser.close();
