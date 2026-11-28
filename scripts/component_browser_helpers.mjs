import fs from "node:fs/promises";
import { createHash } from "node:crypto";
import { join, resolve } from "node:path";
import { pathToFileURL } from "node:url";

export function parseArgs(argv, projectRoot, env = process.env) {
  const options = {
    slug: null, title: null, output: null, sections: [], expects: [], animation: null,
    playwrightModule: env.PLAYWRIGHT_MODULE || "playwright",
    browserExecutable: env.PLAYWRIGHT_EXECUTABLE_PATH || undefined,
  };
  const names = new Map([
    ["--slug", "slug"], ["--title", "title"], ["--output", "output"],
    ["--section", "sections"], ["--expect", "expects"],
    ["--animation", "animation"],
    ["--playwright-module", "playwrightModule"],
    ["--browser-executable", "browserExecutable"],
  ]);
  for (let index = 0; index < argv.length; index += 2) {
    const key = names.get(argv[index]);
    if (!key) throw new Error(`unknown argument: ${argv[index]}`);
    const value = argv[index + 1];
    if (!value || value.startsWith("--")) throw new Error(`missing value for ${argv[index]}`);
    if (Array.isArray(options[key])) options[key].push(value);
    else options[key] = value;
  }
  if (!options.slug || !options.title || !options.output) {
    throw new Error("--slug, --title, and --output are required");
  }
  options.output = resolve(projectRoot, options.output);
  if (options.browserExecutable) options.browserExecutable = resolve(options.browserExecutable);
  return options;
}

export function moduleSpecifier(value) {
  return value.startsWith(".") || value.startsWith("/")
    ? pathToFileURL(resolve(value)).href : value;
}

export function docFragment(title) {
  return title.toLowerCase().replace(/[^a-z0-9]+/g, "-").replace(/^-|-$/g, "") || "section";
}

// Content fingerprints identify exactly what was observed, not which source built a bundle.
export async function fingerprintTree(root, paths = ["."]) {
  const files = [];
  async function visit(relative) {
    const path = join(root, relative);
    let stat;
    try { stat = await fs.stat(path); }
    catch (error) { if (error.code === "ENOENT") return; throw error; }
    if (stat.isDirectory()) {
      for (const entry of (await fs.readdir(path)).sort()) {
        if (entry.startsWith(".") || entry === "build" || entry === "node_modules") continue;
        await visit(join(relative, entry));
      }
    } else if (stat.isFile()) {
      const bytes = await fs.readFile(path);
      files.push({ path: relative, bytes: bytes.length,
        sha256: createHash("sha256").update(bytes).digest("hex") });
    }
  }
  for (const path of paths) await visit(path);
  files.sort((a, b) => a.path.localeCompare(b.path));
  return { sha256: createHash("sha256").update(JSON.stringify(files)).digest("hex"), files };
}
