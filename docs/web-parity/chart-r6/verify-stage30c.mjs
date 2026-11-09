import fs from "node:fs/promises";
import { createHash } from "node:crypto";
import { createServer } from "node:http";
import { extname, join, normalize } from "node:path";
import { chromium } from "/Users/cscs/.agents/skills/guizang-social-card-skill/node_modules/playwright/index.mjs";

const root = "/Users/cscs/IdeaProjects/angryecho/compose-foundation";
const dist = join(root, "app/webApp/build/dist/wasmJs/productionExecutable");
const output = join(root, "docs/web-parity/chart-r6");
const sourceRoot = join(root, "docs/web-parity/chart-r1/source");
const currentSourceRoot = join(root, "docs/web-parity/chart-r6/source");
const sourcesPath = join(root, "docs/web-parity/chart-r5/sources.json");
const browserExecutable =
  "/Users/cscs/Library/Caches/ms-playwright/chromium_headless_shell-1148/chrome-mac/headless_shell";
const views = [
  { name: "desktop-light", width: 1440, height: 1000, theme: "light" },
  { name: "desktop-dark", width: 1440, height: 1000, theme: "dark" },
  { name: "phone-light", width: 390, height: 844, theme: "light" },
  { name: "phone-dark", width: 390, height: 844, theme: "dark" },
];
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

await fs.mkdir(output, { recursive: true });

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

await new Promise((resolve) => server.listen(0, "127.0.0.1", resolve));
const port = server.address().port;
const localUrl = `http://127.0.0.1:${port}/chart`;
const checks = [];
const failures = [];

function check(condition, name, detail = null) {
  const passed = Boolean(condition);
  checks.push({ name, passed, detail });
  if (!passed) failures.push({ name, detail });
}

function sha256(bytes) {
  return createHash("sha256").update(bytes).digest("hex");
}

async function fileSha256(path) {
  return sha256(await fs.readFile(path));
}

function pngSize(bytes) {
  return { width: bytes.readUInt32BE(16), height: bytes.readUInt32BE(20) };
}

const sources = JSON.parse(await fs.readFile(sourcesPath, "utf8"));
for (const [sourcePath, expected] of Object.entries(sources.files)) {
  let actual = null;
  for (const directory of [sourceRoot, currentSourceRoot]) {
    try {
      actual = await fileSha256(
        join(directory, sourcePath.split("/").at(-1)),
      );
      break;
    } catch {
      // Try the next archived source directory.
    }
  }
  check(
    actual === expected,
    `source:${sourcePath.split("/").at(-1)}`,
    { expected, actual },
  );
}

const browser = await chromium.launch({
  executablePath: browserExecutable,
  headless: true,
  args: [
    "--disable-gpu",
    "--disable-dev-shm-usage",
    "--no-sandbox",
    "--js-flags=--max-old-space-size=4096",
  ],
});
const captures = [];
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
    page.on("console", (message) => {
      if (
        message.type() === "error" &&
        !message.text().includes("wasmExports` is deprecated")
      ) {
        errors.push(message.text());
      }
    });
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
    try {
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
    } catch (error) {
      const diagnostic = await page.evaluate(() => ({
        rootHtml: document.getElementById("compose-root")?.innerHTML.slice(0, 500),
        bodyHeight: document.body.style.height,
        childCount: document.getElementById("compose-root")?.children.length,
        resources: performance.getEntriesByType("resource").map((entry) => ({
          name: entry.name,
          duration: Math.round(entry.duration),
          transferSize: entry.transferSize,
        })),
      }));
      console.error(
        JSON.stringify({
          view: view.name,
          diagnostic,
          errors,
          failedRequests,
        }),
      );
      await page.screenshot({
        path: join(output, `debug-${view.name}.png`),
      });
      throw error;
    }
    await page.waitForTimeout(900);

    const path = join(output, `${view.name}.png`);
    const bytes = await page.screenshot({
      path,
      animations: "disabled",
    });
    const metrics = await page.evaluate(() => {
      const root = document.getElementById("compose-root");
      const bounds = root?.getBoundingClientRect();
      const resources = performance
        .getEntriesByType("resource")
        .map((entry) => entry.name);
      return {
        title: document.title,
        url: location.href,
        theme: localStorage.getItem("compose-foundation.theme"),
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
        canvasCount: document.querySelectorAll("canvas").length,
        svgCount: document.querySelectorAll("svg").length,
        devicePixelRatio,
        wasm: resources.filter((name) => name.endsWith(".wasm")),
      };
    });
    const capture = {
      view: view.name,
      path,
      sha256: sha256(bytes),
      bytes: bytes.length,
      png: pngSize(bytes),
      metrics,
      errors,
      failedRequests,
    };
    captures.push(capture);
    check(metrics.theme === view.theme, `theme:${view.name}`, metrics.theme);
    check(metrics.root, `compose-root:${view.name}`, metrics.root);
    check(
      capture.png.width === view.width &&
        capture.png.height === view.height,
      `screenshot-dimensions:${view.name}`,
      capture.png,
    );
    check(
      metrics.scrollWidth <= metrics.clientWidth,
      `no-horizontal-overflow:${view.name}`,
      {
        clientWidth: metrics.clientWidth,
        scrollWidth: metrics.scrollWidth,
      },
    );
    check(
      metrics.bodyHeight.includes("calc"),
      `compose-height:${view.name}`,
      metrics.bodyHeight,
    );
    check(
      metrics.wasm.length > 0,
      `wasm-loaded:${view.name}`,
      metrics.wasm,
    );
    check(errors.length === 0, `page-errors:${view.name}`, errors);
    check(bytes.length > 10_000, `screenshot-size:${view.name}`, bytes.length);
    await context.close();
  }
} finally {
  await browser.close();
  await new Promise((resolve) => server.close(resolve));
}

check(
  new Set(captures.map((capture) => capture.sha256)).size === views.length,
  "unique-view-captures",
  captures.map((capture) => capture.sha256),
);

const summary = {
  slug: "chart",
  generatedAt: new Date().toISOString(),
  localUrl,
  pinnedRevision: sources.revision,
  checks: checks.length,
  passed: checks.filter((check) => check.passed).length,
  failed: failures.length,
  failures,
  captures,
};
await fs.writeFile(
  join(output, "summary.json"),
  `${JSON.stringify(summary, null, 2)}\n`,
  "utf8",
);
console.log(
  JSON.stringify({
    ok: failures.length === 0,
    checks: summary.checks,
    passed: summary.passed,
    failed: summary.failed,
    summary: join(output, "summary.json"),
  }),
);
if (failures.length) process.exitCode = 1;
