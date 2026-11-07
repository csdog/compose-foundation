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

async function checkHashes(manifestPath, relativeTo = root) {
  const manifest = JSON.parse(await fs.readFile(manifestPath, "utf8"));
  const files = Object.entries(manifest.files);
  const hashes = Object.values(manifest.hashes ?? {});
  if (files.length !== hashes.length) {
    throw new Error(`${manifestPath}: file/hash count mismatch ${files.length}/${hashes.length}`);
  }
  for (const [key, file] of files) {
    const expected = manifest.hashes[key] ?? manifest.hashes[file];
    if (!expected) throw new Error(`${manifestPath}: missing hash for ${file}`);
    const actual = await sha256(path.resolve(relativeTo, file));
    if (actual !== expected) throw new Error(`${manifestPath}: stale hash for ${file}`);
  }
  return manifest;
}

const sources = JSON.parse(await fs.readFile(path.join(root, "sources.json"), "utf8"));
for (const file of sources.files) {
  const sourcePath = path.join(root, file.path);
  if ((await sha256(sourcePath)) !== file.sha256) {
    throw new Error(`stale source hash: ${file.path}`);
  }
}

const browser = await checkHashes(path.join(root, "browser/meta.json"));
for (const [file, expected] of Object.entries(browser.hashes)) {
  const match = file.match(/(desktop|phone)-(light|dark)\.png$/);
  if (!match) throw new Error(`unexpected quadrant file: ${file}`);
  const size = await pngSize(file);
  const expectedSize = match[1] === "desktop" ? [1440, 1000] : [390, 844];
  if (size.width !== expectedSize[0] || size.height !== expectedSize[1]) {
    throw new Error(`unexpected dimensions for ${file}: ${size.width}x${size.height}`);
  }
}

const sections = await checkHashes(path.join(root, "sections/sections.json"));
const interactions = await checkHashes(path.join(root, "browser/interactions/interactions.json"));
const expectedSections = 48;
const expectedInteractions = 17;
if (Object.keys(sections.files).length !== expectedSections) {
  throw new Error(`expected ${expectedSections} section files`);
}
if (Object.keys(interactions.files).length !== expectedInteractions) {
  throw new Error(`expected ${expectedInteractions} interaction files`);
}

const contactSheets = {};
for (const state of ["desktop-light", "desktop-dark", "phone-light", "phone-dark"]) {
  const contact = path.join(root, `sections/contact-${state}.png`);
  const stat = await fs.stat(contact);
  contactSheets[state] = {
    file: contact,
    bytes: stat.size,
    sha256: await sha256(contact),
    dimensions: await pngSize(contact),
  };
}

const validation = {
  ok: true,
  officialRevision: sources.officialRevision,
  sourceFiles: sources.files.length,
  quadrantFiles: Object.keys(browser.files).length,
  sectionFiles: Object.keys(sections.files).length,
  interactionFiles: Object.keys(interactions.files).length,
  contactSheets,
};
await fs.writeFile(
  path.join(root, "validation.json"),
  `${JSON.stringify(validation, null, 2)}\n`,
);
console.log(JSON.stringify(validation));
