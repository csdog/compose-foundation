import fs from "node:fs/promises";
import { createHash } from "node:crypto";
import { createServer } from "node:http";
import { extname, join, normalize } from "node:path";
import { chromium } from "/Users/cscs/.agents/skills/guizang-social-card-skill/node_modules/playwright/index.mjs";

const root = "/Users/cscs/IdeaProjects/angryecho/compose-foundation";
const dist = join(root, "app/webApp/build/dist/wasmJs/productionExecutable");
const output = join(root, "docs/web-parity/progress-r5");
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
const localUrl = `http://127.0.0.1:${server.address().port}/progress`;

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
  await page.waitForTimeout(700);
}

async function capture(page, name) {
  const path = join(output, `${name}.png`);
  const bytes = await page.screenshot({ path, animations: "disabled" });
  const record = { name, path, bytes: bytes.length, sha256: sha256(bytes) };
  captures.push(record);
  return record;
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
    const record = await capture(page, `local-${view.name}`);
    const metrics = await page.evaluate(() => {
      const root = document.getElementById("compose-root");
      const bounds = root?.getBoundingClientRect();
      const resources = performance
        .getEntriesByType("resource")
        .map((entry) => entry.name);
      return {
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
        wasm: resources.filter((name) => name.endsWith(".wasm")),
      };
    });
    Object.assign(record, { png: pngSize(Buffer.from(await fs.readFile(record.path))), metrics, errors, failedRequests });
    check(metrics.theme === view.theme, `theme:${view.name}`, metrics.theme);
    check(metrics.root, `compose-root:${view.name}`, metrics.root);
    check(
      record.png.width === view.width && record.png.height === view.height,
      `screenshot-dimensions:${view.name}`,
      record.png,
    );
    check(
      metrics.scrollWidth <= metrics.clientWidth,
      `no-horizontal-overflow:${view.name}`,
      { clientWidth: metrics.clientWidth, scrollWidth: metrics.scrollWidth },
    );
    check(
      metrics.wasm.length > 0,
      `wasm-loaded:${view.name}`,
      metrics.wasm,
    );
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
  const snapshot = await accessibilityText(page);
  await fs.writeFile(join(output, "local-aria.txt"), snapshot, "utf8");
  for (const phrase of [
    "Displays an indicator showing the completion progress of a task",
    "With label and value",
    "Use ProgressLabel and ProgressValue",
    "A progress bar that can be controlled by a slider.",
    "To enable RTL support in shadcn/ui",
  ]) {
    check(snapshot.includes(phrase), "local-copy", phrase);
  }

  for (const [name, section, expected] of [
    ["controlled", "Controlled", "A progress bar that can be controlled by a slider."],
    ["rtl", "RTL", "To enable RTL support in shadcn/ui"],
  ]) {
    const toc = page
      .getByRole("button", { name: section, exact: true })
      .last();
    check(
      (await toc.count()) > 0,
      `toc-visible:${name}`,
      { section },
    );
    await toc.click({ force: true });
    await page.waitForTimeout(400);
    const sectionSnapshot = await accessibilityText(page);
    check(
      sectionSnapshot.includes(expected),
      `section-visible:${name}`,
      expected,
    );
    await capture(page, `local-${name}`);
  }
  await context.close();
} finally {
  await browser.close();
  await new Promise((resolve) => server.close(resolve));
}

check(
  new Set(captures.map((capture) => capture.sha256)).size === captures.length,
  "unique-captures",
  captures.map((capture) => capture.sha256),
);

const summary = {
  component: "progress",
  generatedAt: new Date().toISOString(),
  localUrl,
  ok: failures.length === 0,
  checks: checks.length,
  passed: checks.filter((check) => check.passed).length,
  failed: failures.length,
  failures,
  captures,
};
const summaryPath = join(output, "summary.json");
await fs.writeFile(summaryPath, `${JSON.stringify(summary, null, 2)}\n`, "utf8");
console.log(
  JSON.stringify({
    ok: failures.length === 0,
    checks: summary.checks,
    passed: summary.passed,
    failed: summary.failed,
    summary: summaryPath,
  }),
);
if (failures.length) process.exitCode = 1;
