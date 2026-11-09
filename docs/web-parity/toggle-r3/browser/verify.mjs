const fs = await import("node:fs/promises");
const path = await import("node:path");
const http = await import("node:http");
const crypto = await import("node:crypto");
const { once } = await import("node:events");
const { execFile } = await import("node:child_process");
const { promisify } = await import("node:util");

const execFileAsync = promisify(execFile);
const projectRoot =
  "/Users/cscs/IdeaProjects/angryecho/compose-foundation";
const distDir = path.join(
  projectRoot,
  "app/webApp/build/dist/wasmJs/productionExecutable",
);
const evidenceDir = path.join(projectRoot, "docs/web-parity/toggle-r3/browser");
const officialUrl = "https://ui.shadcn.com/docs/components/base/toggle";
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

await fs.mkdir(evidenceDir, { recursive: true });

const server = http.createServer(async (request, response) => {
  const requestUrl = new URL(request.url, "http://127.0.0.1");
  const relativePath = decodeURIComponent(requestUrl.pathname).replace(
    /^\/+/,
    "",
  );
  const candidate = path.resolve(distDir, relativePath || "index.html");
  const safeCandidate = candidate.startsWith(distDir)
    ? candidate
    : path.join(distDir, "index.html");
  let filePath = safeCandidate;
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
      contentTypes.get(path.extname(filePath)) ??
      "application/octet-stream",
  });
  response.end(data);
});
server.listen(0, "127.0.0.1");
await once(server, "listening");
const port = server.address().port;
const localUrl = `http://127.0.0.1:${port}/toggle`;

const task = await taskSpace("toggle parity stage 30 verify attempt 6");
const official = task.page("p1");
const local = await task.newPage();
const failures = [];

function check(condition, message) {
  if (!condition) failures.push(message);
}

async function hashFile(filePath) {
  const data = await fs.readFile(filePath);
  return crypto.createHash("sha256").update(data).digest("hex");
}

async function screenshotInfo(filePath) {
  const { stdout } = await execFileAsync("/usr/bin/sips", [
    "-g",
    "pixelWidth",
    "-g",
    "pixelHeight",
    filePath,
  ]);
  const width = Number(stdout.match(/pixelWidth:\s*(\d+)/)?.[1]);
  const height = Number(stdout.match(/pixelHeight:\s*(\d+)/)?.[1]);
  return {
    path: filePath,
    sha256: await hashFile(filePath),
    width,
    height,
  };
}

async function setViewport(page, view) {
  await page.cdp("Emulation.setDeviceMetricsOverride", {
    width: view.width,
    height: view.height,
    deviceScaleFactor: 1,
    mobile: false,
  });
}

const realDelay = (milliseconds) =>
  new Promise((resolve) => setTimeout(resolve, milliseconds));

async function setVirtualTimePolicy(page, policy, budget) {
  const parameters = { policy };
  if (budget !== undefined) {
    parameters.budget = budget;
    parameters.maxVirtualTimeTaskStarvationCount = 10000;
  }
  return page.cdp("Emulation.setVirtualTimePolicy", parameters);
}

async function virtualClockState(page) {
  return page.evaluate(() => ({
    performanceNowMs: Math.round(performance.now() * 100) / 100,
    documentTimelineMs:
      typeof document.timeline?.currentTime === "number"
        ? Math.round(document.timeline.currentTime * 100) / 100
        : null,
  }));
}

async function advanceVirtualTime(page, budgetMs) {
  const before = await virtualClockState(page);
  await setVirtualTimePolicy(page, "advance", budgetMs);
  let after = before;
  const deadline = Date.now() + 2000;
  while (
    after.performanceNowMs <
      before.performanceNowMs + budgetMs - 1 &&
    Date.now() < deadline
  ) {
    await realDelay(10);
    after = await virtualClockState(page);
  }
  if (after.performanceNowMs < before.performanceNowMs + budgetMs - 1) {
    throw new Error(
      `virtual clock advanced ${after.performanceNowMs - before.performanceNowMs}ms of ${budgetMs}ms`,
    );
  }
  return {
    budgetMs,
    before,
    after,
    advancedMs:
      Math.round((after.performanceNowMs - before.performanceNowMs) * 100) /
      100,
  };
}

async function imageSimilarity(leftPath, rightPath) {
  const compare = async (filter) => {
    try {
      const { stderr } = await execFileAsync(
        "/opt/homebrew/bin/ffmpeg",
        [
          "-hide_banner",
          "-i",
          leftPath,
          "-i",
          rightPath,
          "-lavfi",
          filter,
          "-f",
          "null",
          "-",
        ],
      );
      return stderr;
    } catch (error) {
      return String(error?.stderr || error);
    }
  };
  const [ssimOutput, psnrOutput] = await Promise.all([
    compare("ssim"),
    compare("psnr"),
  ]);
  const ssim = Number(
    ssimOutput.match(/All:\s*([0-9.]+)/)?.[1] ?? Number.NaN,
  );
  const psnrText = psnrOutput.match(/average:\s*([^\s]+)/)?.[1];
  const psnr =
    psnrText === "inf" ? Number.POSITIVE_INFINITY : Number(psnrText);
  return { ssim, psnr };
}

async function setOfficialTheme(page, theme) {
  await page.evaluate((value) => {
    localStorage.setItem("theme", value);
    document.documentElement.classList.toggle("dark", value === "dark");
    document.documentElement.style.colorScheme = value;
    window.scrollTo(0, 0);
  }, theme);
  await page.waitForTimeout(300);
}

async function waitForLocal(page) {
  try {
    await page.waitForFunction(
      () => {
        return (
          document.getElementById("compose-root") &&
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
      root: !!document.getElementById("compose-root"),
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

async function localGeometry(page) {
  return page.evaluate(() => {
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
    const canvas = elements.find((element) => element.tagName === "CANVAS");
    const rect = canvas?.getBoundingClientRect();
    return {
      title: document.title,
      pathname: location.pathname,
      theme: localStorage.getItem("compose-foundation.theme"),
      devicePixelRatio: window.devicePixelRatio,
      clientWidth: document.documentElement.clientWidth,
      clientHeight: document.documentElement.clientHeight,
      scrollWidth: document.documentElement.scrollWidth,
      scrollHeight: document.documentElement.scrollHeight,
      bodyHeight: document.body.style.height,
      canvas: canvas
        ? {
            width: canvas.width,
            height: canvas.height,
            cssWidth: rect.width,
            cssHeight: rect.height,
          }
        : null,
    };
  });
}

async function localDomSummary(page) {
  return page.evaluate(() => {
    const text = (node) =>
      node?.textContent?.trim().replace(/\s+/g, " ") || null;
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
    const semantic = elements
      .filter(
        (node) =>
          node.getAttribute("role") ||
          node.hasAttribute("aria-pressed") ||
          node.hasAttribute("aria-label"),
      )
      .slice(0, 80)
      .map((node) => ({
        tag: node.tagName,
        role: node.getAttribute("role"),
        ariaLabel: node.getAttribute("aria-label"),
        ariaPressed: node.getAttribute("aria-pressed"),
        text: text(node)?.slice(0, 120) ?? null,
      }));
    return {
      title: document.title,
      bodyText: text(document.body)?.slice(0, 500) ?? null,
      tagCounts: elements.reduce((counts, node) => {
        counts[node.tagName] = (counts[node.tagName] ?? 0) + 1;
        return counts;
      }, {}),
      semantic,
    };
  });
}

async function bookmarkPoint(page) {
  return page.evaluate(() => {
    const nodes = [];
    const walk = (scope) => {
      for (const node of scope.querySelectorAll("*")) {
        if (
          node.matches(
            "[role=button], button, [aria-pressed], [aria-label]",
          )
        ) {
          nodes.push(node);
        }
        if (node.shadowRoot) walk(node.shadowRoot);
      }
    };
    walk(document);
    const entries = nodes
      .map((node) => {
        const rect = node.getBoundingClientRect();
        return {
          text: node.textContent?.trim().replace(/\s+/g, " ") || "",
          ariaLabel: node.getAttribute("aria-label") || "",
          role: node.getAttribute("role") || node.tagName.toLowerCase(),
          x: rect.x,
          y: rect.y,
          width: rect.width,
          height: rect.height,
        };
      })
      .filter((entry) => entry.width > 0 && entry.height > 0);
    const bookmark = entries
      .filter((entry) =>
        /bookmark/i.test(`${entry.text} ${entry.ariaLabel}`),
      )
      .sort(
        (left, right) =>
          left.width * left.height - right.width * right.height,
      )[0];
    if (bookmark) {
      return {
        x: bookmark.x + bookmark.width / 2,
        y: bookmark.y + bookmark.height / 2,
        rect: {
          x: bookmark.x,
          y: bookmark.y,
          width: bookmark.width,
          height: bookmark.height,
        },
        source: "semantic-bookmark",
        matches: entries
          .filter((entry) =>
            /bookmark/i.test(`${entry.text} ${entry.ariaLabel}`),
          )
          .sort(
            (left, right) =>
              left.width * left.height - right.width * right.height,
          ),
      };
    }
    const centered = entries.find(
      (entry) =>
        entry.x > 350 &&
        entry.x < 1050 &&
        entry.y > 300 &&
        entry.y < 600,
    );
    if (centered) {
      return {
        x: centered.x + centered.width / 2,
        y: centered.y + centered.height / 2,
        rect: {
          x: centered.x,
          y: centered.y,
          width: centered.width,
          height: centered.height,
        },
        source: "centered-semantic",
        matches: [centered],
      };
    }
    return {
      x: 713,
      y: 440,
      rect: { x: 613, y: 390, width: 200, height: 100 },
      source: "viewport-fallback",
      matches: [],
    };
  });
}

async function captureLocalView(view) {
  await setViewport(local, view);
  await setLocalTheme(local, view.theme);
  await local.evaluate(() => window.scrollTo(0, 0));
  await local.waitForTimeout(500);
  const filePath = path.join(evidenceDir, `local-${view.name}.png`);
  await local.screenshot({ path: filePath });
  const geometry = await localGeometry(local);
  const screenshot = await screenshotInfo(filePath);
  check(
    screenshot.width === view.width && screenshot.height === view.height,
    `${view.name}: screenshot is ${screenshot.width}x${screenshot.height}, expected ${view.width}x${view.height}`,
  );
  check(
    geometry.theme === view.theme,
    `${view.name}: local theme is ${geometry.theme}`,
  );
  check(
    geometry.pathname === "/toggle",
    `${view.name}: local route is ${geometry.pathname}`,
  );
  check(
    geometry.scrollWidth <= geometry.clientWidth,
    `${view.name}: horizontal overflow ${geometry.scrollWidth} > ${geometry.clientWidth}`,
  );
  check(
    geometry.canvas &&
      Math.abs(
        geometry.canvas.width -
          geometry.canvas.cssWidth * geometry.devicePixelRatio,
      ) <= 1 &&
      Math.abs(
        geometry.canvas.height -
          geometry.canvas.cssHeight * geometry.devicePixelRatio,
      ) <= 1,
    `${view.name}: canvas bitmap/CSS/DPR dimensions disagree`,
  );
  return { screenshot, geometry };
}

async function captureOfficialView(view) {
  await setViewport(official, view);
  await setOfficialTheme(official, view.theme);
  const filePath = path.join(evidenceDir, `official-${view.name}.png`);
  await official.screenshot({ path: filePath });
  const screenshot = await screenshotInfo(filePath);
  check(
    screenshot.width === view.width && screenshot.height === view.height,
    `official ${view.name}: screenshot is ${screenshot.width}x${screenshot.height}, expected ${view.width}x${view.height}`,
  );
  return screenshot;
}

const meta = {
  component: "toggle",
  stage: "30c-browser",
  attempt: 6,
  capturedAt: new Date().toISOString(),
  officialUrl,
  localUrl,
  viewport: { deviceScaleFactor: 1, browserScale: 1 },
  buildDist: distDir,
  officialDom: null,
  officialError: null,
  localDom: null,
  localViews: {},
  officialViews: {},
  interactions: null,
  loadedResources: {},
  failures,
};

try {
  await setViewport(official, views[0]);
  try {
    await official.goto(officialUrl, { timeout: 90000 });
    await official.waitForLoadState("load").catch(() => {});
    await official.waitForTimeout(800);
    meta.officialDom = await official.evaluate(() => {
      const text = (node) =>
        node?.textContent?.trim().replace(/\s+/g, " ") || null;
      const main = document.querySelector("main") || document.body;
      return {
        title: text(document.querySelector("h1")),
        headings: [...main.querySelectorAll("h2, h3")].map((node) => ({
          tag: node.tagName,
          text: text(node),
          id: node.id || null,
        })),
        examples: document.querySelectorAll(
          "[data-slot=component-preview], .component-preview",
        ).length,
        clientWidth: document.documentElement.clientWidth,
        scrollHeight: document.documentElement.scrollHeight,
      };
    });
    check(meta.officialDom.title === "Toggle", "official title is not Toggle");
    check(
      meta.officialDom.examples >= 6,
      `official examples ${meta.officialDom.examples} < 6`,
    );
  } catch (error) {
    meta.officialError = String(error?.stack || error);
    failures.push("official page navigation/capture failed");
  }

  await setViewport(local, views[0]);
  await local.goto(localUrl, { timeout: 90000 });
  await waitForLocal(local);
  meta.localDom = await localDomSummary(local);

  const loadedResourceUrls = await local.evaluate(() =>
    performance
      .getEntriesByType("resource")
      .map((entry) => entry.name)
      .filter((url) => /\.(wasm|js)(\?|$)/.test(url)),
  );
  for (const resourceUrl of loadedResourceUrls) {
    const resourcePath = path.join(
      distDir,
      decodeURIComponent(new URL(resourceUrl).pathname),
    );
    try {
      meta.loadedResources[resourceUrl] = await hashFile(resourcePath);
    } catch {
      meta.loadedResources[resourceUrl] = null;
    }
  }
  check(
    Object.values(meta.loadedResources).every(Boolean),
    "a loaded JS/Wasm resource does not map to the prepared distribution",
  );

  for (const view of views) {
    meta.localViews[view.name] = await captureLocalView(view);
    if (!meta.officialError) {
      meta.officialViews[view.name] = await captureOfficialView(view);
    }
  }

  await setViewport(local, views[0]);
  await setLocalTheme(local, "light");
  await local.evaluate(() => window.scrollTo(0, 0));
  await local.waitForTimeout(400);
  const point = await bookmarkPoint(local);
  const virtualTimePolicy = await setVirtualTimePolicy(local, "pause");
  const interactionFiles = {};
  const interactionTimings = {};
  const clip = {
    x: Math.max(0, Math.floor(point.rect.x - 24)),
    y: Math.max(0, Math.floor(point.rect.y - 24)),
    width: Math.min(
      views[0].width,
      Math.ceil(point.rect.width + 48),
    ),
    height: Math.min(
      views[0].height,
      Math.ceil(point.rect.height + 48),
    ),
    scale: 1,
  };
  const captureInteraction = async (name, frameState) => {
    const filePath = path.join(evidenceDir, `interaction-${name}.png`);
    const startedAt = performance.now();
    const capture = async () => {
      const advance = setVirtualTimePolicy(local, "advance", 48);
      await realDelay(1);
      const result = await local.cdp("Page.captureScreenshot", {
        format: "png",
        fromSurface: true,
        captureBeyondViewport: false,
        clip,
      });
      await advance;
      if (!result?.data) {
        throw new Error("Page.captureScreenshot returned no data");
      }
      await fs.writeFile(filePath, Buffer.from(result.data, "base64"));
    };
    await capture();
    interactionTimings[name] = {
      captureMs: Math.round((performance.now() - startedAt) * 10) / 10,
      captureMode: "cdp-advance-render-clipped",
      captureAdvanceMs: 48,
      ...frameState,
    };
    interactionFiles[name] = await screenshotInfo(filePath);
  };

  let frameState = await advanceVirtualTime(local, 32);
  await captureInteraction("default", frameState);
  const dispatchPointerButton = async (phase) => {
    if (phase === "down") {
      await local.mouse.down();
    } else {
      await local.mouse.up();
    }
  };
  await local.mouse.move(point.x, point.y, { label: "hover toggle" });
  frameState = await advanceVirtualTime(local, 32);
  await captureInteraction("hover-mid", frameState);
  frameState = await advanceVirtualTime(local, 320);
  await captureInteraction("hover-settled", frameState);
  await dispatchPointerButton("down");
  frameState = await virtualClockState(local);
  await captureInteraction("pressed-mid", frameState);
  frameState = await advanceVirtualTime(local, 320);
  await captureInteraction("pressed-held", frameState);
  await dispatchPointerButton("up");
  frameState = await advanceVirtualTime(local, 320);
  await captureInteraction("selected", frameState);
  await local.mouse.move(100, 100, { label: "leave toggle" });
  frameState = await advanceVirtualTime(local, 320);
  await captureInteraction("selected-unhovered", frameState);

  const interactionHashes = Object.fromEntries(
    Object.entries(interactionFiles).map(([name, file]) => [
      name,
      file.sha256,
    ]),
  );
  const settledInteractionNames = [
    "default",
    "hover-settled",
    "pressed-held",
    "selected",
  ];
  check(
    new Set(
      settledInteractionNames.map((name) => interactionHashes[name]),
    ).size === settledInteractionNames.length,
    "default/hover/press/selected screenshots are not four distinct states",
  );
  check(
    interactionHashes.selected !== interactionHashes["hover-settled"],
    "selected state did not change from hover state",
  );
  check(
    interactionHashes["hover-mid"] !== interactionHashes["hover-settled"],
    "hover transition has no distinct intermediate frame",
  );
  check(
    interactionHashes["pressed-mid"] !== interactionHashes["pressed-held"],
    "pressed transition has no distinct intermediate frame",
  );
  const selectedSimilarity = await imageSimilarity(
    interactionFiles.selected.path,
    interactionFiles["selected-unhovered"].path,
  );
  check(
    selectedSimilarity.ssim >= 0.995 &&
      selectedSimilarity.psnr >= 30,
    "Nova selected state changed after pointer left the control",
  );
  check(
    interactionHashes["selected-unhovered"] !==
      interactionHashes.default,
    "selected state matches the default state after pointer leaves",
  );
  meta.interactions = {
    point,
    clip,
    virtualTimePolicy,
    timings: interactionTimings,
    files: interactionFiles,
    hashes: interactionHashes,
    selectedSimilarity,
  };
} catch (error) {
  failures.push(`browser batch aborted: ${String(error?.message || error)}`);
  meta.fatalError = String(error?.stack || error);
} finally {
  await fs.writeFile(
    path.join(evidenceDir, "meta.json"),
    JSON.stringify(meta, null, 2),
  );
  await fs.writeFile(
    path.join(evidenceDir, "dom-summary.json"),
    JSON.stringify(
      {
        officialDom: meta.officialDom,
        localDom: meta.localDom,
      },
      null,
      2,
    ),
  );
  await new Promise((resolve) => server.close(resolve));
  await task.finish({ keep: [] });
}

const summary = {
  ok: failures.length === 0,
  failures,
  spaceId: task.spaceId,
  localUrl,
  officialTitle: meta.officialDom?.title ?? null,
  officialExamples: meta.officialDom?.examples ?? null,
  localViews: Object.keys(meta.localViews),
  officialViews: Object.keys(meta.officialViews),
  interactionFiles: meta.interactions
    ? Object.keys(meta.interactions.files)
    : [],
  evidence: path.join(evidenceDir, "meta.json"),
};
console.log(JSON.stringify(summary));
if (!summary.ok) process.exitCode = 1;
