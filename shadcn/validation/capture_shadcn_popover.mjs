import { execFileSync } from "node:child_process"
import { createHash } from "node:crypto"
import { mkdtemp, mkdir, readFile, rm, writeFile } from "node:fs/promises"
import { tmpdir } from "node:os"
import { join, resolve } from "node:path"
import { moduleSpecifier } from "../../scripts/component_browser_helpers.mjs"

if (!process.argv[2]) throw new Error("Pass an output directory for the shadcn reference PNGs")
const destination = resolve(process.argv[2], "shadcn", "popover")
const plan = JSON.parse(await readFile(new URL("./plan.json", import.meta.url), "utf8"))
if (!/^[a-f0-9]{40}$/.test(plan.upstreamRevision) || plan.primitive !== "base" || plan.style !== "nova") {
  throw new Error("This capture uses a pinned Base UI / Nova revision")
}
const { chromium } = await import(moduleSpecifier(process.env.PLAYWRIGHT_MODULE || "playwright"))
const work = await mkdtemp(join(tmpdir(), "shadcn-popover-"))
const origin = `https://raw.githubusercontent.com/shadcn-ui/ui/${plan.upstreamRevision}`
const example = plan.components.popover.reference
const sources = {
  "demo.tsx": example,
  "ui/button.tsx": "apps/v4/registry/bases/base/ui/button.tsx",
  "ui/input.tsx": "apps/v4/registry/bases/base/ui/input.tsx",
  "ui/label.tsx": "apps/v4/registry/bases/base/ui/label.tsx",
  "ui/popover.tsx": "apps/v4/registry/bases/base/ui/popover.tsx",
  "globals.css": "apps/v4/app/globals.css",
  "legacy-themes.css": "apps/v4/app/legacy-themes.css",
  "style-nova.css": "apps/v4/registry/styles/style-nova.css",
  "shadcn.css": "packages/shadcn/src/tailwind.css",
}
const packages = [
  "react@19.2.3", "react-dom@19.2.3", "@base-ui/react@1.6.0",
  "class-variance-authority@0.7.1", "cn@0.2.4", "tailwindcss@4.3.0",
  "@tailwindcss/cli@4.3.0", "tw-animate-css@1.4.0", "geist@1.9.2", "esbuild@0.25.12",
]
const run = (command, args) => execFileSync(command, args, { cwd: work, stdio: "inherit", timeout: 120_000 })

try {
  await mkdir(join(work, "ui"))
  for (const [file, source] of Object.entries(sources)) {
    run("curl", ["-fsSL", "--max-time", "20", `${origin}/${source}`, "-o", file])
  }
  run("npm", ["install", "--prefix", work, "--cache", join(work, "npm-cache"), "--no-audit", "--no-fund", ...packages])

  const globals = await readFile(join(work, "globals.css"), "utf8")
  if (!globals.includes('@import "shadcn/tailwind.css";')) {
    throw new Error("The pinned upstream stylesheet has an unexpected import")
  }
  await writeFile(join(work, "globals.css"), globals.replace(
    '@import "shadcn/tailwind.css";',
    '@import "./shadcn.css";\n@import "./style-nova.css";',
  ))
  await writeFile(join(work, "main.tsx"), [
    'import { createRoot } from "react-dom/client"',
    'import Demo from "./demo"',
    'createRoot(document.getElementById("root")!).render(<Demo />)',
  ].join("\n"))
  run("node_modules/.bin/esbuild", [
    "main.tsx", "--bundle", "--jsx=automatic", "--alias:@/styles/base-nova/ui=./ui",
    "--outfile=preview.js",
  ])
  run("node_modules/.bin/tailwindcss", ["-i", "globals.css", "-o", "preview.css"])

  const font = await readFile(join(work, "node_modules/geist/dist/fonts/geist-sans/Geist-Variable.woff2"))
  const html = `<!doctype html>
    <html class="style-nova" lang="en"><head><meta charset="utf-8">
    <style>
      @font-face{font-family:Geist;src:url(data:font/woff2;base64,${font.toString("base64")}) format("woff2");font-weight:100 900}
      body{--font-sans:Geist,sans-serif;--font-heading:Geist,sans-serif;font-family:var(--font-sans);background:var(--background);color:var(--foreground)}
      #root{margin:24px}
    </style></head><body><main id="root"></main></body></html>`
  const css = await readFile(join(work, "preview.css"), "utf8")
  const script = await readFile(join(work, "preview.js"), "utf8")

  await mkdir(destination, { recursive: true })
  const browser = await chromium.launch({ executablePath: process.env.PLAYWRIGHT_EXECUTABLE_PATH })
  try {
    const viewport = { width: 420, height: 324 }
    for (const theme of ["light", "dark"]) {
      const page = await browser.newPage({ viewport, colorScheme: theme, reducedMotion: "reduce" })
      await page.setContent(html)
      await page.addStyleTag({ content: css })
      await page.addScriptTag({ content: script })
      await page.evaluate((mode) => document.documentElement.classList.toggle("dark", mode === "dark"), theme)
      await page.evaluate(() => document.fonts.ready)
      await page.getByRole("button", { name: "Open popover" }).click()
      const panel = page.locator('[data-slot="popover-content"]')
      await panel.waitFor()
      for (const label of ["Width", "Max. width", "Height", "Max. height"]) {
        await panel.getByLabel(label, { exact: true }).waitFor()
      }
      await page.mouse.move(viewport.width - 1, viewport.height - 1)
      await page.screenshot({
        path: join(destination, `popover-default-open-${theme}.png`),
        animations: "disabled",
        caret: "hide",
      })
      await page.close()
    }
    await writeFile(join(destination, "provenance.json"), JSON.stringify({
      source: `https://github.com/shadcn-ui/ui/blob/${plan.upstreamRevision}/${example}`,
      revision: plan.upstreamRevision, primitive: plan.primitive, style: plan.style,
      sourceFiles: sources, dependencies: packages, browser: browser.version(), viewport,
      fontSha256: createHash("sha256").update(font).digest("hex"),
      capture: "Pinned upstream code in a local preview wrapper; Popover opened by Playwright",
    }, null, 2) + "\n")
  } finally {
    await browser.close()
  }
  console.log(destination)
} finally {
  await rm(work, { recursive: true, force: true })
}
