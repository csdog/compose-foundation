import { createHash } from "node:crypto";
import { createServer } from "node:http";
import { once } from "node:events";
import fs from "node:fs/promises";
import path from "node:path";
import { chromium } from "/Users/cscs/.agents/skills/guizang-social-card-skill/node_modules/playwright-core/index.mjs";

const projectRoot =
  "/Users/cscs/IdeaProjects/angryecho/compose-foundation";
const distDir = path.join(
  projectRoot,
  "app/webApp/build/dist/wasmJs/productionExecutable",
);
const evidenceDir = path.join(
  projectRoot,
  "docs/web-parity/chip-r1/browser/attempt-3",
);
const chromePath =
  "/Applications/Google Chrome.app/Contents/MacOS/Google Chrome";
const officialUrl = "https://ui.shadcn.com/docs/components/base/chip";
const sourceRecord = JSON.parse(
  await fs.readFile(
    path.join(projectRoot, "docs/web-parity/chip-r1/sources.json"),
    "utf8",
  ),
);
const views = [
  { name: "desktop-light", width: 1440, height: 1000, theme: "light" },
  { name: "desktop-dark", width: 1440, height: 1000, theme: "dark" },
  { name: "phone-light", width: 390, height: 844, theme: "light" },
  { name: "phone-dark", width: 390, height: 844, theme: "dark" },
];
const localSources = [
  "shadcn/core/src/commonMain/kotlin/com/toutiaoyan/compose/ui/components/chip/Chip.kt",
  "shadcn/core/src/commonMain/kotlin/com/toutiaoyan/compose/ui/components/chip/ChipStyles.kt",
  "app/shared/src/commonMain/kotlin/com/toutiaoyan/compose/ui/catalog/docs/ChipDoc.kt",
  "app/shared/src/jvmTest/kotlin/com/toutiaoyan/compose/ui/catalog/ChipExamplesTest.kt",
  "shadcn/core/src/jvmTest/kotlin/com/toutiaoyan/compose/ui/components/chip/ChipScreenshotTest.kt",
];
const contentTypes = new Map([
  [".css", "text/css; charset=utf-8"],
  [".html", "text/html; charset=utf-8"],
  [".js", "text/javascript; charset=utf-8"],
  [".json", "application/json; charset=utf-8"],
  [".map", "application/json; charset=utf-8"],
  [".svg", "image/svg+xml"],
  [".wasm", "application/wasm"],
]);

const failures = [];
const observations = [];
const summary = {
  component: "chip",
  stage: "30c-browser",
  attempt: 3,
  runtime: "headless Google Chrome via installed playwright-core",
  capturedAt: new Date().toISOString(),
  officialUrl,
  officialStatus: sourceRecord.officialProbe.httpStatus,
  officialRevision: sourceRecord.upstreamRevision,
  officialContract: "expected HTTP 404; Chip is a Kotlin extension",
  localUrl: null,
  browserScale: 1,
  views: {},
  interactions: {},
  build: {},
  sources: {},
  screenshots: {},
  failures,
  observations,
};

function check(condition, message) {
  if (!condition) failures.push(message);
}

async function sha256(buffer) {
  return createHash("sha256").update(buffer).digest("hex");
}

async function fileInfo(filePath) {
  const bytes = await fs.readFile(filePath);
  return {
    path: filePath,
    bytes: bytes.length,
    sha256: await sha256(bytes),
  };
}

async function pngInfo(filePath) {
  const bytes = await fs.readFile(filePath);
  return {
    ...(await fileInfo(filePath)),
    width: bytes.readUInt32BE(16),
    height: bytes.readUInt32BE(20),
  };
}

async function capture(page, name, options = {}) {
  const filePath = path.join(evidenceDir, `${name}.png`);
  await page.screenshot({
    path: filePath,
    animations: "disabled",
    ...options,
  });
  const info = await pngInfo(filePath);
  summary.screenshots[name] = info;
  return info;
}

function walkElements(root) {
  const elements = [];
  const visit = (scope) => {
    for (const element of scope.querySelectorAll("*")) {
      elements.push(element);
      if (element.shadowRoot) visit(element.shadowRoot);
    }
  };
  if (root) {
    elements.push(root);
    if (root.shadowRoot) visit(root.shadowRoot);
    visit(root);
  }
  return elements;
}

async function waitForLocal(page) {
  try {
    await page.waitForFunction(
      () => {
        const root = document.getElementById("compose-root");
        return (
          root &&
          root.children.length > 0 &&
          document.body.style.height.includes("calc")
        );
      },
      undefined,
      { timeout: 60000 },
    );
  } catch (error) {
    const state = await page.evaluate(() => ({
      title: document.title,
      url: location.href,
      rootChildren: document.getElementById("compose-root")?.children.length,
      canvases: document.querySelectorAll("canvas").length,
      bodyHeight: document.body.style.height,
      readyState: document.readyState,
    }));
    throw new Error(`${error.message}; state=${JSON.stringify(state)}`);
  }
  await page.waitForTimeout(1000);
}

async function setLocalTheme(page, theme) {
  const current = await page.evaluate(() =>
    localStorage.getItem("compose-foundation.theme"),
  );
  if (current === theme) return;
  await page.evaluate(
    (value) => localStorage.setItem("compose-foundation.theme", value),
    theme,
  );
  await page.reload({ timeout: 60000 });
  await waitForLocal(page);
}

async function waitForSemantics(page) {
  await page.waitForFunction(
    () => {
      const root = document.getElementById("compose-root");
      const hasSemantics = (scope) => {
        if (scope.querySelector?.("[role=heading], [role=button]")) {
          return true;
        }
        for (const node of scope.querySelectorAll?.("*") ?? []) {
          if (node.shadowRoot && hasSemantics(node.shadowRoot)) return true;
        }
        return false;
      };
      return !!root && hasSemantics(root);
    },
    undefined,
    { timeout: 30000 },
  );
  await page.waitForTimeout(400);
}

async function geometry(page, view) {
  return page.evaluate((expected) => {
    const root = document.getElementById("compose-root");
    const rootRect = root?.getBoundingClientRect();
    const elements = [];
    const walk = (scope) => {
      for (const element of scope.querySelectorAll("*")) {
        elements.push(element);
        if (element.shadowRoot) walk(element.shadowRoot);
      }
    };
    if (root) {
      elements.push(root);
      if (root.shadowRoot) walk(root.shadowRoot);
      walk(root);
    }
    const canvases = elements
      .filter((element) => element.tagName === "CANVAS")
      .map((canvas) => {
        const rect = canvas.getBoundingClientRect();
        return {
          x: rect.x,
          y: rect.y,
          cssWidth: rect.width,
          cssHeight: rect.height,
          bitmapWidth: canvas.width,
          bitmapHeight: canvas.height,
        };
      })
      .sort(
        (left, right) =>
          right.cssWidth * right.cssHeight -
          left.cssWidth * left.cssHeight,
      );
    return {
      expected,
      title: document.title,
      pathname: location.pathname,
      theme: localStorage.getItem("compose-foundation.theme"),
      devicePixelRatio,
      innerWidth,
      innerHeight,
      clientWidth: document.documentElement.clientWidth,
      clientHeight: document.documentElement.clientHeight,
      scrollWidth: document.documentElement.scrollWidth,
      scrollHeight: document.documentElement.scrollHeight,
      bodyHeight: document.body.style.height,
      root: rootRect
        ? {
            x: rootRect.x,
            y: rootRect.y,
            width: rootRect.width,
            height: rootRect.height,
          }
        : null,
      canvas: canvases[0] ?? null,
      canvasCount: canvases.length,
    };
  }, view);
}

async function domSummary(page) {
  const ariaSnapshot = await page
    .locator("body")
    .ariaSnapshot()
    .catch(() => null);
  const dom = await page.evaluate(() => {
    const text = (node) =>
      node?.textContent?.trim().replace(/\s+/g, " ") || "";
    const root = document.getElementById("compose-root");
    const elements = [];
    const walk = (scope) => {
      for (const element of scope.querySelectorAll("*")) {
        elements.push(element);
        if (element.shadowRoot) walk(element.shadowRoot);
      }
    };
    if (root) {
      elements.push(root);
      if (root.shadowRoot) walk(root.shadowRoot);
      walk(root);
    }
    return {
      title: document.title,
      bodyText: text(document.body).slice(0, 1000),
      tagCounts: elements.reduce((counts, node) => {
        counts[node.tagName] = (counts[node.tagName] ?? 0) + 1;
        return counts;
      }, {}),
      semantic: elements
        .filter(
          (node) =>
            node.getAttribute("role") ||
            node.hasAttribute("aria-label") ||
            node.hasAttribute("aria-pressed") ||
            node.hasAttribute("aria-selected") ||
            node.hasAttribute("aria-checked") ||
            node.hasAttribute("aria-disabled"),
        )
        .slice(0, 160)
        .map((node) => ({
          tag: node.tagName,
          role: node.getAttribute("role"),
          ariaLabel: node.getAttribute("aria-label"),
          ariaPressed: node.getAttribute("aria-pressed"),
          ariaSelected: node.getAttribute("aria-selected"),
          ariaChecked: node.getAttribute("aria-checked"),
          ariaDisabled: node.getAttribute("aria-disabled"),
          text: text(node).slice(0, 160),
        })),
      html: document.documentElement.outerHTML,
    };
  });
  return { ...dom, ariaSnapshot };
}

async function semanticRect(page, selector) {
  return page.evaluate((cssSelector) => {
    const nodes = [];
    const walk = (scope) => {
      for (const node of scope.querySelectorAll(cssSelector)) nodes.push(node);
      for (const node of scope.querySelectorAll("*")) {
        if (node.shadowRoot) walk(node.shadowRoot);
      }
    };
    walk(document);
    const candidates = nodes
      .map((node) => {
        const rect = node.getBoundingClientRect();
        return {
          rect: {
            x: rect.x,
            y: rect.y,
            width: rect.width,
            height: rect.height,
          },
          role: node.getAttribute("role"),
          ariaLabel: node.getAttribute("aria-label"),
          ariaPressed: node.getAttribute("aria-pressed"),
          ariaSelected: node.getAttribute("aria-selected"),
          ariaChecked: node.getAttribute("aria-checked"),
          ariaDisabled: node.getAttribute("aria-disabled"),
          text: node.textContent?.trim().replace(/\s+/g, " ") || "",
        };
      })
      .filter((entry) => entry.rect.width > 0 && entry.rect.height > 0);
    return candidates;
  }, selector);
}

async function imageSimilarity(leftPath, rightPath) {
  const { spawn } = await import("node:child_process");
  const process = spawn("/opt/homebrew/bin/ffmpeg", [
    "-hide_banner",
    "-i",
    leftPath,
    "-i",
    rightPath,
    "-lavfi",
    "ssim",
    "-f",
    "null",
    "-",
  ]);
  let stderr = "";
  process.stderr.on("data", (chunk) => {
    stderr += chunk;
  });
  const exitCode = await new Promise((resolve) => process.on("close", resolve));
  const match = stderr.match(/All:\s*([0-9.]+)/);
  return {
    exitCode,
    ssim: match ? Number(match[1]) : null,
  };
}

await Promise.all([
  fs.mkdir(path.join(evidenceDir, "screens"), { recursive: true }),
  fs.mkdir(path.join(evidenceDir, "dom"), { recursive: true }),
  fs.mkdir(path.join(evidenceDir, "interactions"), { recursive: true }),
]);

for (const source of localSources) {
  summary.sources[source] = await fileInfo(path.join(projectRoot, source));
}
for (const name of [
  "index.html",
  "webApp.js",
  "styles.css",
  "658820f72b4617efb380.wasm",
  "f1c944db69f8cf0c1185.wasm",
]) {
  const filePath = path.join(distDir, name);
  summary.build[name] = await fileInfo(filePath);
}

check(
  summary.build["webApp.js"].sha256 ===
    "09e5657c31bc39e41f19ef352ab17f278884087267998ea34c1475800fade8a3",
  "webApp.js hash differs from the recorded 30b build",
);
check(
  summary.build["658820f72b4617efb380.wasm"].sha256 ===
    "08e904ec38a6a81a4809a853acf68022f83905286827e804e88b262c37f2954d",
  "primary Wasm hash differs from the recorded 30b build",
);

const server = createServer(async (request, response) => {
  const requestUrl = new URL(request.url, "http://127.0.0.1");
  const relativePath = decodeURIComponent(requestUrl.pathname).replace(
    /^\/+/,
    "",
  );
  const candidate = path.resolve(distDir, relativePath || "index.html");
  let filePath =
    candidate.startsWith(distDir) ? candidate : path.join(distDir, "index.html");
  let data;
  try {
    data = await fs.readFile(filePath);
  } catch {
    filePath = path.join(distDir, "index.html");
    data = await fs.readFile(filePath);
  }
  response.writeHead(200, {
    "cache-control": "no-store",
    "content-type":
      contentTypes.get(path.extname(filePath)) ?? "application/octet-stream",
  });
  response.end(data);
});
server.listen(0, "127.0.0.1");
await once(server, "listening");
summary.localUrl = `http://127.0.0.1:${server.address().port}/chip`;

let browser;
try {
  browser = await chromium.launch({
    executablePath: chromePath,
    headless: true,
    args: ["--no-sandbox", "--disable-dev-shm-usage"],
  });
  const context = await browser.newContext({
    viewport: { width: views[0].width, height: views[0].height },
    deviceScaleFactor: 1,
    colorScheme: "light",
  });
  const local = await context.newPage();
  const localRuntimeEvents = [];
  local.on("console", (message) => {
    if (message.type() === "error" || message.type() === "warning") {
      localRuntimeEvents.push({
        type: `console:${message.type()}`,
        text: message.text().slice(0, 500),
      });
    }
  });
  local.on("pageerror", (error) => {
    localRuntimeEvents.push({
      type: "pageerror",
      text: String(error?.stack || error).slice(0, 1000),
    });
  });
  local.on("response", (response) => {
    if (response.status() >= 400) {
      localRuntimeEvents.push({
        type: "response",
        status: response.status(),
        url: response.url(),
      });
    }
  });

  check(
    summary.officialStatus === 404,
    `pinned official Chip probe is ${summary.officialStatus}`,
  );

  await local.setViewportSize({
    width: views[0].width,
    height: views[0].height,
  });
  await local.goto(summary.localUrl, {
    timeout: 90000,
    waitUntil: "domcontentloaded",
  });
  await waitForLocal(local);
  summary.localRuntimeEvents = localRuntimeEvents.slice(-80);

  for (const view of views) {
    await local.setViewportSize({
      width: view.width,
      height: view.height,
    });
    await setLocalTheme(local, view.theme);
    await local.evaluate(() => window.scrollTo(0, 0));
    await local.waitForTimeout(700);
    const shot = await capture(local, `screens/local-${view.name}`);
    const localGeometry = await geometry(local, view);
    const localDom = await domSummary(local);
    await fs.writeFile(
      path.join(evidenceDir, `dom/local-${view.name}.json`),
      JSON.stringify(localDom, null, 2),
    );
    await fs.writeFile(
      path.join(evidenceDir, `dom/local-${view.name}.html`),
      localDom.html,
    );
    summary.views[view.name] = {
      screenshot: shot,
      geometry: localGeometry,
      semanticCount: localDom.semantic.length,
      ariaSnapshot: localDom.ariaSnapshot,
      bodyText: localDom.bodyText,
      tagCounts: localDom.tagCounts,
    };
    check(
      shot.width === view.width && shot.height === view.height,
      `${view.name} screenshot dimensions ${shot.width}x${shot.height}`,
    );
    check(
      localGeometry.pathname === "/chip",
      `${view.name} local route is ${localGeometry.pathname}`,
    );
    check(
      localGeometry.theme === view.theme,
      `${view.name} local theme is ${localGeometry.theme}`,
    );
    check(
      localGeometry.devicePixelRatio === 1,
      `${view.name} DPR is ${localGeometry.devicePixelRatio}`,
    );
    check(
      localGeometry.innerWidth === view.width &&
        localGeometry.innerHeight === view.height,
      `${view.name} viewport is ${localGeometry.innerWidth}x${localGeometry.innerHeight}`,
    );
    check(
      localGeometry.scrollWidth <= localGeometry.clientWidth + 1,
      `${view.name} has horizontal overflow ${localGeometry.scrollWidth}>${localGeometry.clientWidth}`,
    );
    check(
      localGeometry.canvas && localGeometry.root,
      `${view.name} canvas/root is missing`,
    );
    if (localGeometry.canvas && localGeometry.root) {
      const canvas = localGeometry.canvas;
      const root = localGeometry.root;
      check(
        Math.abs(canvas.x - root.x) <= 1 &&
          Math.abs(canvas.y - root.y) <= 1,
        `${view.name} canvas/root origin mismatch`,
      );
      check(
        Math.abs(canvas.cssWidth - root.width) <= 1 &&
          Math.abs(canvas.cssHeight - root.height) <= 1,
        `${view.name} canvas/root CSS dimensions mismatch`,
      );
      check(
        Math.abs(canvas.bitmapWidth - canvas.cssWidth) <= 1 &&
          Math.abs(canvas.bitmapHeight - canvas.cssHeight) <= 1,
        `${view.name} canvas bitmap/CSS/DPR dimensions mismatch`,
      );
    }
    check(localDom.semantic.length > 0, `${view.name} semantic DOM is empty`);
    const semanticText = [
      localDom.bodyText,
      localDom.ariaSnapshot,
      ...localDom.semantic.flatMap((node) => [
        node.text,
        node.ariaLabel,
        node.role,
      ]),
    ]
      .filter(Boolean)
      .join(" ");
    check(
      /Kotlin extension/i.test(semanticText),
      `${view.name} page lacks Kotlin extension labeling`,
    );
    check(
      /Chip/i.test(semanticText),
      `${view.name} page lacks Chip labeling`,
    );
  }

  const screenshotHashes = Object.entries(summary.screenshots).filter(
    ([name]) =>
      name.startsWith("screens/local-") ||
      name.startsWith("interactions/"),
  );
  const duplicateHashes = screenshotHashes.filter(
    ([name, value], index) =>
      screenshotHashes.findIndex(([, other]) => other.sha256 === value.sha256) <
      index,
  );
  check(
    duplicateHashes.length === 0,
    `duplicate screenshot hashes: ${duplicateHashes.map(([name]) => name).join(", ")}`,
  );

  await local.setViewportSize({ width: 1440, height: 1000 });
  await setLocalTheme(local, "light");
  await local.evaluate(() => window.scrollTo(0, 0));
  await local.waitForTimeout(500);
  const themeBefore = await capture(
    local,
    "interactions/theme-before",
  );
  const themeBeforeState = await geometry(local, {
    name: "theme-before",
    width: 1440,
    height: 1000,
    theme: "light",
  });
  const themeCandidates = await semanticRect(
    local,
    "[role=button], button, [aria-label]",
  );
  const themeButton = themeCandidates
    .filter(
      (candidate) =>
        candidate.rect.x > 1200 &&
        candidate.rect.y < 120 &&
        /theme|dark|light/i.test(
          `${candidate.text} ${candidate.ariaLabel}`,
        ),
    )
    .sort(
      (left, right) =>
        left.rect.width * left.rect.height -
        right.rect.width * right.rect.height,
    )[0];
  if (themeButton) {
    await local.mouse.click(
      themeButton.rect.x + themeButton.rect.width / 2,
      themeButton.rect.y + themeButton.rect.height / 2,
    );
  } else {
    await local.mouse.click(1291, 32);
  }
  await local.waitForTimeout(700);
  const themeAfter = await capture(local, "interactions/theme-after");
  const themeAfterState = await geometry(local, {
    name: "theme-after",
    width: 1440,
    height: 1000,
    theme: "dark",
  });
  summary.interactions.liveThemeSwitch = {
    trigger: themeButton ?? {
      source: "recorded desktop fallback coordinate",
      x: 1291,
      y: 32,
    },
    before: { screenshot: themeBefore, theme: themeBeforeState.theme },
    after: { screenshot: themeAfter, theme: themeAfterState.theme },
  };
  check(
    themeBeforeState.theme === "light" && themeAfterState.theme === "dark",
    `live theme switch state ${themeBeforeState.theme}->${themeAfterState.theme}`,
  );
  check(
    themeBefore.sha256 !== themeAfter.sha256,
    "live theme switch did not change pixels",
  );

  await local.setViewportSize({ width: 1440, height: 1000 });
  await setLocalTheme(local, "light");
  await waitForSemantics(local);
  let selectableHeading = null;
  const maxScroll = await local.evaluate(
    () => document.documentElement.scrollHeight - window.innerHeight,
  );
  for (let y = 0; y <= maxScroll && !selectableHeading; y += 300) {
    await local.evaluate((scrollY) => window.scrollTo(0, scrollY), y);
    await local.waitForTimeout(150);
    selectableHeading = (
      await semanticRect(local, "[role=heading]")
    ).find(
      (candidate) =>
        candidate.text === "Selectable" &&
        candidate.rect.y >= 0 &&
        candidate.rect.y < 1000,
    );
  }
  check(
    !!selectableHeading,
    "Selectable recipe heading was not exposed",
  );
  if (selectableHeading) {
    await local.evaluate(
      ({ y }) => window.scrollBy(0, y - window.innerHeight / 2),
      { y: selectableHeading.rect.y },
    );
    await local.waitForTimeout(500);
    const refreshedHeading = (
      await semanticRect(local, "[role=heading]")
    ).find((candidate) => candidate.text === "Selectable");
    const chipCandidates = (
      await semanticRect(local, "[role=button]")
    )
      .filter(
        (candidate) =>
          candidate.ariaLabel === "Kotlin" &&
          candidate.rect.y > (refreshedHeading?.rect.y ?? 0),
      )
      .sort((left, right) => left.rect.y - right.rect.y);
    const refreshed = chipCandidates[0] ?? null;
    check(!!refreshed, "selectable Kotlin Chip was not exposed");
    if (refreshed) {
    const clip = {
      x: Math.max(0, refreshed.rect.x - 12),
      y: Math.max(0, refreshed.rect.y - 12),
      width: Math.min(
        1440 - Math.max(0, refreshed.rect.x - 12),
        refreshed.rect.width + 24,
      ),
      height: Math.min(
        1000 - Math.max(0, refreshed.rect.y - 12),
        refreshed.rect.height + 24,
      ),
    };
    const before = await capture(
      local,
      "interactions/chip-pressed-before",
      { clip },
    );
    await local.mouse.click(
      refreshed.rect.x + refreshed.rect.width / 2,
      refreshed.rect.y + refreshed.rect.height / 2,
    );
    await local.waitForTimeout(700);
    const afterCandidates = await semanticRect(local, "[role=button]");
    const afterTarget =
      afterCandidates.find(
        (candidate) =>
          candidate.ariaLabel === "Kotlin" &&
          Math.abs(candidate.rect.y - refreshed.rect.y) < 4,
      ) ?? null;
    const after = await capture(
      local,
      "interactions/chip-pressed-after",
      { clip },
    );
    const similarity = await imageSimilarity(before.path, after.path);
    summary.interactions.selectableChip = {
      target: refreshed,
      stateExposure:
        "Compose Web exposes role=button; selected state is not serialized as aria-pressed/selected/checked",
      before: { screenshot: before },
      after: { screenshot: after },
      similarity,
    };
    check(
      afterTarget !== null,
      "selectable Chip disappeared after click",
    );
    check(
      before.sha256 !== after.sha256 &&
        similarity.ssim !== null &&
        similarity.ssim < 0.9999,
      "selectable Chip pixels did not change after click",
    );
    }
  }
} catch (error) {
  failures.push(`uncaught: ${String(error?.stack || error).slice(0, 2000)}`);
} finally {
  await browser?.close().catch(() => {});
  await new Promise((resolve) => server.close(resolve));
}

summary.passed = failures.length === 0;
const summaryPath = path.join(evidenceDir, "summary.json");
await fs.writeFile(summaryPath, JSON.stringify(summary, null, 2));

console.log(
  `BROWSER_SUMMARY ${summary.passed ? "PASS" : "FAIL"} ` +
    `component=chip views=${views.length} screenshots=${Object.keys(summary.screenshots).length} ` +
    `officialStatus=${summary.officialStatus} failures=${failures.length}`,
);
console.log(`SUMMARY_PATH ${summaryPath}`);
for (const failure of failures.slice(0, 20)) {
  console.log(`FAILURE ${failure}`);
}
if (failures.length > 20) {
  console.log(`FAILURE +${failures.length - 20} more in summary`);
}
if (!summary.passed) process.exitCode = 1;
