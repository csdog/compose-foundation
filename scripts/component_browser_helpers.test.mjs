import test from "node:test";
import assert from "node:assert/strict";
import fs from "node:fs/promises";
import { tmpdir } from "node:os";
import { join } from "node:path";
import { parseArgs, moduleSpecifier, docFragment, fingerprintTree } from "./component_browser_helpers.mjs";

const required = ["--slug", "input", "--title", "Input", "--output", ".agent-runs/input/browser"];
test("existing invocation and repeated section/expect arguments remain supported", () => {
  const args = parseArgs([...required, "--section", "Usage", "--section", "Examples", "--expect", "Text"], "/project", {});
  assert.equal(args.output, "/project/.agent-runs/input/browser");
  assert.deepEqual(args.sections, ["Usage", "Examples"]);
  assert.deepEqual(args.expects, ["Text"]);
  assert.equal(args.playwrightModule, "playwright");
  assert.equal(args.browserExecutable, undefined);
});
test("CLI dependency overrides take precedence over environment", () => {
  const args = parseArgs([...required, "--playwright-module", "./local.mjs", "--browser-executable", "/bin/chromium"], "/project", { PLAYWRIGHT_MODULE: "other", PLAYWRIGHT_EXECUTABLE_PATH: "/other" });
  assert.equal(args.playwrightModule, "./local.mjs");
  assert.equal(args.browserExecutable, "/bin/chromium");
  assert.equal(parseArgs(required, "/project", { PLAYWRIGHT_MODULE: "playwright-core" }).playwrightModule, "playwright-core");
});
test("missing values cannot swallow subsequent flags", () => {
  assert.throws(() => parseArgs([...required, "--section", "--expect", "Text"], "/project", {}), /missing value for --section/);
  assert.throws(() => parseArgs([...required, "--browser-executable"], "/project", {}), /missing value/);
  assert.throws(() => parseArgs([...required, "--unknown", "x"], "/project", {}), /unknown argument/);
});
test("module paths become importable URLs while package names stay package names", () => {
  assert.equal(moduleSpecifier("/tmp/space dir/playwright.mjs"), "file:///tmp/space%20dir/playwright.mjs");
  assert.equal(moduleSpecifier("playwright-core"), "playwright-core");
});
test("section fragments match the catalog's title algorithm", () => {
  assert.equal(docFragment("  Input / Examples! "), "input-examples");
  assert.equal(docFragment("!!!"), "section");
});
test("provenance changes with file content, not timestamp, and excludes generated builds", async () => {
  const root = await fs.mkdtemp(join(tmpdir(), "browser-verifier-"));
  try {
    await fs.mkdir(join(root, "src"));
    await fs.mkdir(join(root, "build"));
    await fs.writeFile(join(root, "src", "input.kt"), "first");
    await fs.writeFile(join(root, "build", "generated.js"), "ignored");
    const initial = await fingerprintTree(root);
    assert.deepEqual(initial.files.map((file) => file.path), ["src/input.kt"]);
    await fs.utimes(join(root, "src", "input.kt"), new Date(0), new Date(0));
    assert.equal((await fingerprintTree(root)).sha256, initial.sha256);
    await fs.writeFile(join(root, "src", "input.kt"), "other");
    assert.notEqual((await fingerprintTree(root)).sha256, initial.sha256);
  } finally { await fs.rm(root, { recursive: true, force: true }); }
});
