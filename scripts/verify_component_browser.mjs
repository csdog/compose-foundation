#!/usr/bin/env node

import fs from "node:fs/promises";
import { createHash } from "node:crypto";
import { createServer } from "node:http";
import { extname, join, normalize, resolve } from "node:path";
import { parseArgs, moduleSpecifier, docFragment, fingerprintTree } from "./component_browser_helpers.mjs";

const projectRoot = resolve(import.meta.dirname, "..");
const dist = join(
  projectRoot,
  "app/webApp/build/dist/wasmJs/productionExecutable",
);
const contentTypes = new Map([
  [".css", "text/css; charset=utf-8"],
  [".html", "text/html; charset=utf-8"],
  [".js", "text/javascript; charset=utf-8"],
  [".json", "application/json; charset=utf-8"],
  [".map", "application/json; charset=utf-8"],
  [".svg", "image/svg+xml"],
  [".ttf", "font/ttf"],
  [".wasm", "application/wasm"],
]);
const views = [
  { name: "desktop-light", width: 1440, height: 1000, theme: "light" },
  { name: "desktop-dark", width: 1440, height: 1000, theme: "dark" },
  { name: "phone-light", width: 390, height: 844, theme: "light" },
  { name: "phone-dark", width: 390, height: 844, theme: "dark" },
];

const options = parseArgs(process.argv.slice(2), projectRoot);
let chromium;
try {
  ({ chromium } = await import(moduleSpecifier(options.playwrightModule)));
  if (!chromium?.launch) throw new Error("module does not export chromium.launch");
} catch (error) {
  throw new Error("Cannot load Playwright. Install playwright locally or pass --playwright-module /path/to/playwright/index.mjs (PLAYWRIGHT_MODULE). " + error.message);
}
const sourcePaths = ["app", "shadcn", "tailwind", "core", "gradle", "build.gradle.kts", "settings.gradle.kts", "gradle.properties", "scripts/verify_component_browser.mjs", "scripts/component_browser_helpers.mjs"];
const provenance = {
  capturedAt: new Date().toISOString(),
  dist,
  artifacts: await fingerprintTree(dist),
  source: await fingerprintTree(projectRoot, sourcePaths),
  sourcePaths,
  sourceBuildRelationship: "unverified: fingerprints identify observed files; rebuild separately before claiming current-source coverage",
};
await fs.access(join(dist, "index.html"));
await fs.mkdir(options.output, { recursive: true });

const checks = [];
const failures = [];
const captures = [];

function check(condition, name, detail = null) {
  const passed = Boolean(condition);
  checks.push({ name, passed, detail });
  if (!passed) failures.push({ name, detail });
}

function sha256(bytes) {
  return createHash("sha256").update(bytes).digest("hex");
}

function pngSize(bytes) {
  return { width: bytes.readUInt32BE(16), height: bytes.readUInt32BE(20) };
}

async function accessibilityText(page) {
  const session = await page.context().newCDPSession(page);
  const tree = await session.send("Accessibility.getFullAXTree");
  await session.detach();
  return tree.nodes
    .map((node) => node.name?.value)
    .filter(Boolean)
    .join("\n");
}

async function waitForApp(page) {
  await page.waitForFunction(
    () => {
      const root = document.getElementById("compose-root");
      const bounds = root?.getBoundingClientRect();
      return (
        root &&
        bounds &&
        bounds.width > 0 &&
        bounds.height > 0 &&
        document.body.style.height.includes("calc")
      );
    },
    undefined,
    { timeout: 90000 },
  );
  await page.evaluate(() => document.fonts.ready);
  await page.waitForTimeout(700);
}

async function capture(page, name) {
  const path = join(options.output, `${name}.png`);
  const bytes = await page.screenshot({ path, animations: "disabled" });
  const record = {
    name,
    path,
    bytes: bytes.length,
    sha256: sha256(bytes),
    png: pngSize(bytes),
  };
  captures.push(record);
  return record;
}

async function clickToc(page, name) {
  for (const locator of [
    page.getByRole("button", { name, exact: true }),
    page.getByRole("link", { name, exact: true }),
    page.getByText(name, { exact: true }),
  ]) {
    if ((await locator.count()) === 0) continue;
    for (let index = (await locator.count()) - 1; index >= 0; index -= 1) {
      const node = locator.nth(index);
      const box = await node.boundingBox();
      if (!box || box.width <= 0 || box.height <= 0) continue;
      await node.click({ force: true });
      await page.waitForTimeout(350);
      return true;
    }
  }
  return false;
}

const server = createServer(async (request, response) => {
  const requestPath = decodeURIComponent(
    new URL(request.url, "http://127.0.0.1").pathname,
  );
  const relative = normalize(requestPath)
    .replace(/^(\.\.(\/|\\|$))+/, "")
    .replace(/^\/+/, "");
  let target = join(dist, relative || "index.html");
  try {
    const stat = await fs.stat(target);
    if (stat.isDirectory()) target = join(target, "index.html");
  } catch {
    target = join(dist, "index.html");
  }
  const bytes = await fs.readFile(target);
  response.writeHead(200, {
    "cache-control": "no-store",
    "content-type":
      contentTypes.get(extname(target)) ?? "application/octet-stream",
  });
  response.end(bytes);
});
await new Promise((listening) =>
  server.listen(0, "127.0.0.1", listening),
);
const localUrl = `http://127.0.0.1:${server.address().port}/${options.slug}`;

let browser;
try {
  browser = await chromium.launch({
  executablePath: options.browserExecutable,
  headless: true,
  args: [
    "--disable-gpu",
    "--disable-dev-shm-usage",
    "--no-sandbox",
    "--js-flags=--max-old-space-size=4096",
  ],
});
} catch (error) {
  await new Promise((closed) => server.close(closed));
  throw new Error("Cannot launch Chromium. Install the matching browser with npx playwright install chromium or pass --browser-executable /path/to/chromium (PLAYWRIGHT_EXECUTABLE_PATH). " + error.message);
}

try {
  for (const view of views) {
    const context = await browser.newContext({
      viewport: { width: view.width, height: view.height },
      deviceScaleFactor: 1,
      colorScheme: view.theme,
    });
    await context.addInitScript((theme) => {
      localStorage.setItem("compose-foundation.theme", theme);
    }, view.theme);
    const page = await context.newPage();
    const errors = [];
    const failedRequests = [];
    page.on("pageerror", (error) => errors.push(String(error)));
    page.on("requestfailed", (request) => {
      failedRequests.push({
        url: request.url(),
        failure: request.failure()?.errorText ?? "unknown",
      });
    });
    await page.goto(localUrl, {
      waitUntil: "domcontentloaded",
      timeout: 90000,
    });
    await waitForApp(page);
    const record = await capture(page, view.name);
    const metrics = await page.evaluate(() => {
      const root = document.getElementById("compose-root");
      const bounds = root?.getBoundingClientRect();
      const resources = performance
        .getEntriesByType("resource")
        .map((entry) => entry.name);
      return {
        url: location.href,
        theme: localStorage.getItem("compose-foundation.theme"),
        browserColorScheme: getComputedStyle(document.documentElement).colorScheme,
        bodyHeight: document.body.style.height,
        clientWidth: document.documentElement.clientWidth,
        scrollWidth: document.documentElement.scrollWidth,
        root: bounds
          ? {
              x: bounds.x,
              y: bounds.y,
              width: bounds.width,
              height: bounds.height,
            }
          : null,
        wasm: resources.filter((name) => name.endsWith(".wasm")),
      };
    });
    Object.assign(record, { metrics, errors, failedRequests });
    check(metrics.theme === view.theme, `stored-theme-preference:${view.name}`, metrics.theme);
    check(metrics.browserColorScheme === view.theme, `browser-color-scheme:${view.name}`, metrics.browserColorScheme);
    check(metrics.root, `compose-root:${view.name}`, metrics.root);
    check(
      record.png.width === view.width && record.png.height === view.height,
      `screenshot-dimensions:${view.name}`,
      record.png,
    );
    check(
      metrics.scrollWidth <= metrics.clientWidth,
      `no-dom-horizontal-overflow:${view.name}`,
      { clientWidth: metrics.clientWidth, scrollWidth: metrics.scrollWidth },
    );
    check(metrics.wasm.length > 0, `wasm-loaded:${view.name}`, metrics.wasm);
    check(errors.length === 0, `page-errors:${view.name}`, errors);
    check(
      failedRequests.length === 0,
      `request-failures:${view.name}`,
      failedRequests,
    );
    check(record.bytes > 10_000, `screenshot-size:${view.name}`, record.bytes);
    await context.close();
  }

  const context = await browser.newContext({
    viewport: { width: 1440, height: 1000 },
    deviceScaleFactor: 1,
    colorScheme: "light",
  });
  await context.addInitScript(() => {
    localStorage.setItem("compose-foundation.theme", "light");
  });
  const page = await context.newPage();
  await page.goto(localUrl, {
    waitUntil: "domcontentloaded",
    timeout: 90000,
  });
  await waitForApp(page);
  const initialAx = await accessibilityText(page);
  await fs.writeFile(join(options.output, "aria.txt"), initialAx, "utf8");
  check(initialAx.includes(options.title), "accessible-title", options.title);
  for (const expected of options.expects) {
    check(initialAx.includes(expected), "accessible-copy", expected);
  }
  for (const section of options.sections) {
    check(
      await clickToc(page, section),
      `toc-click:${section}`,
      section,
    );
    const expectedFragment = docFragment(section);
    try {
      await page.waitForFunction((fragment) => location.hash === `#${fragment}`, expectedFragment, { timeout: 5000 });
    } catch { /* Report the observed destination below rather than aborting the report. */ }
    check(new URL(page.url()).hash === `#${expectedFragment}`, `toc-url-fragment:${section}`, {
      expected: expectedFragment, observed: new URL(page.url()).hash,
    });
    await page.evaluate(() => document.fonts.ready);
    await page.waitForTimeout(500);
    await capture(
      page,
      `section-${section.toLowerCase().replace(/[^a-z0-9]+/g, "-")}`,
    );
  }
  await context.close();
} finally {
  await browser.close();
  await new Promise((closed) => server.close(closed));
}

check((await fingerprintTree(dist)).sha256 === provenance.artifacts.sha256,
  "artifacts-unchanged-during-run");
check((await fingerprintTree(projectRoot, sourcePaths)).sha256 === provenance.source.sha256,
  "source-unchanged-during-run");
const summary = {
  component: options.slug,
  title: options.title,
  generatedAt: new Date().toISOString(),
  localUrl,
  provenance,
  coverageLimits: [
    "Theme checks verify stored preference and browser color-scheme, not rendered Compose canvas palette or live theme switching.",
    "DOM overflow does not detect clipping or horizontal overflow inside the Compose canvas.",
    "TOC checks verify click dispatch and URL fragment, not the rendered heading's scroll destination.",
    "Screenshot size and dimensions do not prove visual correctness; captures require visual review.",
    "Source/build correspondence is unverified; artifact and source fingerprints identify this run only.",
  ],
  uniqueCaptureCount: new Set(captures.map((capture) => capture.sha256)).size,
  checkResults: checks,
  ok: failures.length === 0,
  checks: checks.length,
  passed: checks.filter((check) => check.passed).length,
  failed: failures.length,
  failures,
  captures,
};
const summaryPath = join(options.output, "summary.json");
await fs.writeFile(
  summaryPath,
  `${JSON.stringify(summary, null, 2)}\n`,
  "utf8",
);
console.log(
  JSON.stringify({
    ok: summary.ok,
    checks: summary.checks,
    passed: summary.passed,
    failed: summary.failed,
    summary: summaryPath,
  }),
);
if (!summary.ok) process.exitCode = 1;
