const fs = await import("node:fs/promises");
const crypto = await import("node:crypto");
const path = await import("node:path");

const root = path.dirname(new URL(import.meta.url).pathname);

async function sha256(file) {
  return crypto.createHash("sha256").update(await fs.readFile(file)).digest("hex");
}

async function pngSize(file) {
  const data = await fs.readFile(file);
  if (data.subarray(1, 4).toString() !== "PNG") throw new Error(`not a PNG: ${file}`);
  return { width: data.readUInt32BE(16), height: data.readUInt32BE(20) };
}

async function checkHashes(manifestPath) {
  const manifest = JSON.parse(await fs.readFile(manifestPath, "utf8"));
  const files = Object.entries(manifest.files);
  if (files.length !== Object.keys(manifest.hashes ?? {}).length) {
    throw new Error(`${manifestPath}: file/hash count mismatch`);
  }
  for (const [key, file] of files) {
    const expected = manifest.hashes[key] ?? manifest.hashes[file];
    if (!expected || (await sha256(path.resolve(root, file))) !== expected) {
      throw new Error(`${manifestPath}: stale hash for ${key}`);
    }
  }
  return manifest;
}

const sources = JSON.parse(await fs.readFile(path.join(root, "sources.json"), "utf8"));
for (const file of sources.files) {
  if ((await sha256(path.join(root, file.path))) !== file.sha256) {
    throw new Error(`stale source hash: ${file.path}`);
  }
}

const browser = JSON.parse(
  await fs.readFile(path.join(root, "browser/meta.json"), "utf8"),
);
if (Object.keys(browser.quadrantFiles).length !== 8) {
  throw new Error("expected 8 quadrant captures");
}
if (Object.keys(browser.sectionFiles).length !== 88) {
  throw new Error("expected 88 section captures");
}
for (const [key, file] of Object.entries(browser.quadrantFiles)) {
  const expected = browser.hashes[file];
  if (!expected || (await sha256(file)) !== expected) {
    throw new Error(`stale quadrant hash: ${key}`);
  }
  const size = await pngSize(file);
  const expectedSize = key.includes("desktop") ? [1440, 1000] : [390, 844];
  if (size.width !== expectedSize[0] || size.height !== expectedSize[1]) {
    throw new Error(`unexpected quadrant size: ${key}`);
  }
}
for (const [key, file] of Object.entries(browser.sectionFiles)) {
  const expected = browser.hashes[file];
  if (!expected || (await sha256(file)) !== expected) {
    throw new Error(`stale section hash: ${key}`);
  }
}

const interactions = await checkHashes(
  path.join(root, "browser/interactions/interactions.json"),
);
if (Object.keys(interactions.files).length !== 7) {
  throw new Error("expected 7 interaction captures");
}

const contactSheets = {};
for (const state of ["desktop-light", "desktop-dark", "phone-light", "phone-dark"]) {
  const file = path.join(root, `browser/contact-${state}.png`);
  contactSheets[state] = {
    file,
    sha256: await sha256(file),
    dimensions: await pngSize(file),
  };
}

const validation = {
  ok: true,
  officialRevision: sources.officialRevision,
  sourceFiles: sources.files.length,
  quadrantFiles: Object.keys(browser.quadrantFiles).length,
  sectionFiles: Object.keys(browser.sectionFiles).length,
  interactionFiles: Object.keys(interactions.files).length,
  contactSheets,
};
await fs.writeFile(
  path.join(root, "validation.json"),
  `${JSON.stringify(validation, null, 2)}\n`,
);
console.log(JSON.stringify(validation));
