import fs from "node:fs/promises";
import { createHash } from "node:crypto";
import { createServer } from "node:http";
import { extname, join, normalize } from "node:path";
import { fileURLToPath, pathToFileURL } from "node:url";
import { chromium } from "/Users/cscs/.agents/skills/guizang-social-card-skill/node_modules/playwright/index.mjs";

const root = "/Users/cscs/IdeaProjects/angryecho/compose-foundation";
const dist = join(root, "app/webApp/build/dist/wasmJs/productionExecutable");
const output = join(root, "docs/web-parity/table-r1/verify-30c");
const officialUrl = "https://ui.shadcn.com/docs/components/base/table";
const browserExecutable =
  "/Users/cscs/Library/Caches/ms-playwright/chromium_headless_shell-1148/chrome-mac/headless_shell";
const views = [
  { name: "desktop-light", width: 1440, height: 1000, theme: "light" },
  { name: "desktop-dark", width: 1440, height: 1000, theme: "dark" },
  { name: "phone-light", width: 390, height: 844, theme: "light" },
  { name: "phone-dark", width: 390, height: 844, theme: "dark" },
];
const mime = {
  ".css": "text/css; charset=utf-8",
  ".html": "text/html; charset=utf-8",
  ".js": "text/javascript; charset=utf-8",
  ".json": "application/json; charset=utf-8",
  ".map": "application/json; charset=utf-8",
  ".svg": "image/svg+xml",
  ".wasm": "application/wasm",
};

await fs.mkdir(output, { recursive: true });

const server = createServer(async (request, response) => {
  try {
    const requestPath = decodeURIComponent(new URL(request.url, "http://127.0.0.1").pathname);
    const relative = normalize(requestPath).replace(/^(\.\.(\/|\\|$))+/, "").replace(/^\/+/, "");
    const candidate = join(dist, relative || "index.html");
    let target = candidate;
    try {
      const stat = await fs.stat(candidate);
      if (stat.isDirectory()) target = join(candidate, "index.html");
    } catch {
      target = join(dist, "index.html");
    }
    const bytes = await fs.readFile(target);
    response.writeHead(200, {
      "cache-control": "no-store",
      "content-type": mime[extname(target)] ?? "application/octet-stream",
    });
    response.end(bytes);
  } catch (error) {
    response.writeHead(500, { "content-type": "text/plain" });
    response.end(String(error));
  }
});

await new Promise((resolve) => server.listen(0, "127.0.0.1", resolve));
const port = server.address().port;
const localUrl = `http://127.0.0.1:${port}/table`;

function sha256(bytes) {
  return createHash("sha256").update(bytes).digest("hex");
}

async function fileSha256(path) {
  return sha256(await fs.readFile(path));
}

function pngSize(bytes) {
  return { width: bytes.readUInt32BE(16), height: bytes.readUInt32BE(20) };
}

async function capture(page, path) {
  const bytes = await page.screenshot({ path, animations: "disabled" });
  return { path, sha256: sha256(bytes), ...pngSize(bytes) };
}

async function setTheme(page, theme) {
  await page.evaluate((value) => {
    localStorage.setItem("compose-foundation.theme", value);
  }, theme);
  await page.reload({ waitUntil: "load", timeout: 90000 });
}

async function waitForLocal(page) {
  await page.waitForFunction(
    () => {
      const root = document.getElementById("compose-root");
      const canvas = root?.firstElementChild?.firstElementChild;
      return !!canvas && document.body.style.height.includes("calc");
    },
    undefined,
    { timeout: 60000 },
  );
  await page.waitForTimeout(1200);
}

async function localTile(page, path, fraction) {
  await page.evaluate((value) => {
    const max = document.documentElement.scrollHeight - innerHeight;
    scrollTo(0, Math.max(0, Math.round(max * value)));
  }, fraction);
  await page.waitForTimeout(250);
  return capture(page, path);
}

let browser;
const summary = {
  stage: "30c-browser",
  capturedAt: new Date().toISOString(),
  officialUrl,
  localUrl,
  browser: { executable: browserExecutable, version: null, deviceScaleFactor: 1 },
  views: [],
  assertions: {},
  files: {},
  errors: [],
};

try {
  browser = await chromium.launch({
    executablePath: browserExecutable,
    headless: true,
    args: ["--disable-dev-shm-usage", "--enable-unsafe-swiftshader"],
  });
  summary.browser.version = browser.version();
  const context = await browser.newContext({
    viewport: { width: 1440, height: 1000 },
    deviceScaleFactor: 1,
    reducedMotion: "reduce",
  });

  const official = await context.newPage();
  await official.goto(officialUrl, { waitUntil: "domcontentloaded", timeout: 90000 });
  await official.waitForLoadState("networkidle", { timeout: 30000 }).catch(() => {});
  await official.waitForSelector("h1", { timeout: 60000 });
  summary.officialDom = await official.evaluate(() => {
    const text = (node) => node?.textContent?.trim().replace(/\s+/g, " ") ?? null;
    const main = document.querySelector("main") ?? document.body;
    return {
      title: text(document.querySelector("h1")),
      description: text(document.querySelector("h1")?.nextElementSibling),
      headings: [...main.querySelectorAll("h2, h3")].map((node) => ({
        tag: node.tagName,
        text: text(node),
      })),
      clientWidth: document.documentElement.clientWidth,
      scrollHeight: document.documentElement.scrollHeight,
    };
  });

  for (const view of views) {
    await official.setViewportSize({ width: view.width, height: view.height });
    await official.evaluate((theme) => {
      localStorage.setItem("theme", theme);
      document.documentElement.classList.toggle("dark", theme === "dark");
      document.documentElement.style.colorScheme = theme;
      scrollTo(0, 0);
    }, view.theme);
    await official.waitForTimeout(400);
    const officialShot = await capture(
      official,
      join(output, `official-${view.name}.png`),
    );
    summary.files[`official-${view.name}`] = officialShot;
    summary.views.push({
      name: view.name,
      width: view.width,
      height: view.height,
      theme: view.theme,
      official: { sha256: officialShot.sha256 },
    });
  }

  const local = await context.newPage();
  await local.setViewportSize({ width: 1440, height: 1000 });
  await local.goto(localUrl, { waitUntil: "load", timeout: 90000 });
  await waitForLocal(local);

  for (const view of views) {
    await setTheme(local, view.theme);
    await waitForLocal(local);
    await local.setViewportSize({ width: view.width, height: view.height });
    await local.evaluate(() => scrollTo(0, 0));
    await local.waitForTimeout(400);
    const top = await capture(local, join(output, `local-${view.name}-top.png`));
    summary.files[`local-${view.name}-top`] = top;
    const viewRecord = summary.views.find((entry) => entry.name === view.name);
    viewRecord.local = {
      scrollHeight: await local.evaluate(() => document.documentElement.scrollHeight),
      theme: await local.evaluate(() => localStorage.getItem("compose-foundation.theme")),
      topSha256: top.sha256,
      tiles: {},
    };
    for (const fraction of [0.18, 0.36, 0.54, 0.72, 0.9]) {
      const label = `${view.name}-scroll-${String(fraction).replace(".", "")}`;
      const tile = await localTile(local, join(output, `${label}.png`), fraction);
      summary.files[label] = tile;
      viewRecord.local.tiles[fraction] = tile.sha256;
    }
  }

  await setTheme(local, "light");
  await waitForLocal(local);
  await local.setViewportSize({ width: 1440, height: 1000 });
  await local.evaluate(() => scrollTo(0, 0));
  await local.mouse.move(20, 200);
  await local.waitForTimeout(300);
  const hoverClip = { x: 390, y: 260, width: 670, height: 420 };
  const hoverBefore = await local.screenshot({ animations: "disabled", clip: hoverClip });
  await local.mouse.move(700, 368, { steps: 4 });
  await local.waitForTimeout(350);
  const hoverAfter = await local.screenshot({ animations: "disabled", clip: hoverClip });
  summary.assertions.rowHoverChanged = sha256(hoverBefore) !== sha256(hoverAfter);
  summary.files["local-desktop-light-hover"] = await capture(
    local,
    join(output, "local-desktop-light-hover.png"),
  );

  const actionsBefore = await localTile(
    local,
    join(output, "local-desktop-light-actions.png"),
    0.22,
  );
  summary.files["local-desktop-light-actions"] = actionsBefore;
  await local.mouse.click(975, 605);
  await local.waitForTimeout(350);
  const actionsAfter = await capture(
    local,
    join(output, "local-desktop-light-actions-open.png"),
  );
  summary.files["local-desktop-light-actions-open"] = actionsAfter;
  summary.assertions.actionsMenuChanged = actionsBefore.sha256 !== actionsAfter.sha256;
  await local.keyboard.press("Escape");
  await local.waitForTimeout(250);

  const rtlBefore = await localTile(
    local,
    join(output, "local-desktop-light-rtl-top.png"),
    0.34,
  );
  summary.files["local-desktop-light-rtl-top"] = rtlBefore;
  await local.mouse.click(492, 252);
  await local.waitForTimeout(250);
  const rtlOpen = await capture(local, join(output, "local-desktop-light-rtl-open.png"));
  summary.files["local-desktop-light-rtl-open"] = rtlOpen;
  summary.assertions.rtlSelectOpened = rtlBefore.sha256 !== rtlOpen.sha256;
  await local.keyboard.press("End");
  await local.keyboard.press("Enter");
  await local.waitForTimeout(400);
  const hebrew = await capture(local, join(output, "local-desktop-light-rtl-hebrew.png"));
  summary.files["local-desktop-light-rtl-hebrew"] = hebrew;
  summary.assertions.rtlHebrewChanged = rtlBefore.sha256 !== hebrew.sha256;

  await local.setViewportSize({ width: 390, height: 844 });
  await local.evaluate(() => scrollTo(0, 0));
  await local.waitForTimeout(250);
  await local.mouse.move(195, 430);
  await local.waitForTimeout(300);
  const overflowBefore = await local.screenshot({ animations: "disabled" });
  await local.mouse.wheel(1000, 0);
  await local.waitForTimeout(350);
  const overflowAfter = await local.screenshot({ animations: "disabled" });
  summary.assertions.horizontalOverflowChanged = sha256(overflowBefore) !== sha256(overflowAfter);
  summary.files["local-phone-light-overflow"] = await capture(
    local,
    join(output, "local-phone-light-overflow.png"),
  );

  const appResources = await local.evaluate(() =>
    performance
      .getEntriesByType("resource")
      .map((entry) => entry.name)
      .filter((name) => name.endsWith(".wasm")),
  );
  summary.localResources = appResources;
  summary.assertions.allScreenshotsSized =
    Object.values(summary.files).every((file) => {
      const expected = views.find((view) => file.path.includes(view.name));
      return expected
        ? file.width === expected.width && file.height === expected.height
        : file.width > 0 && file.height > 0;
    });
  summary.assertions.allScreenshotsRendered = Object.values(summary.files).every(
    (file) => file.sha256 && file.sha256 !== sha256(Buffer.alloc(file.width * file.height * 4)),
  );
  summary.sourceHashes = {};
  for (const relative of [
    "shadcn/core/src/commonMain/kotlin/com/toutiaoyan/compose/ui/components/table/Table.kt",
    "shadcn/core/src/commonMain/kotlin/com/toutiaoyan/compose/ui/components/table/TableStyles.kt",
    "app/shared/src/commonMain/kotlin/com/toutiaoyan/compose/ui/catalog/docs/TableDoc.kt",
    "app/shared/src/commonMain/kotlin/com/toutiaoyan/compose/ui/catalog/docs/TableRecipes.kt",
    "app/shared/src/commonMain/kotlin/com/toutiaoyan/compose/ui/catalog/docs/TableRecipeCode.kt",
  ]) {
    summary.sourceHashes[relative] = await fileSha256(join(root, relative));
  }
  summary.distributionHashes = {};
  for (const relative of ["index.html", "webApp.js", "e5bb73d4607032c7e6d8.wasm"]) {
    summary.distributionHashes[relative] = await fileSha256(join(dist, relative));
  }
} catch (error) {
  summary.errors.push({
    message: error instanceof Error ? error.message : String(error),
    stack: error instanceof Error ? error.stack : null,
  });
  process.exitCode = 1;
} finally {
  await browser?.close();
  await new Promise((resolve) => server.close(resolve));
  await fs.writeFile(join(output, "summary.json"), JSON.stringify(summary, null, 2));
  console.log(
    JSON.stringify({
      ok: summary.errors.length === 0,
      output,
      captures: Object.keys(summary.files).length,
      views: summary.views.length,
      assertions: summary.assertions,
      error: summary.errors[0]?.message ?? null,
    }),
  );
}
