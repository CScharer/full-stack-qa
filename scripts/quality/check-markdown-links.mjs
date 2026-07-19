/**
 * Validates relative markdown links and #anchors in .md files.
 * Skips http(s), mailto, /.attachments/ (Azure DevOps wiki), and same-document-only # links.
 *
 * How to run (from repository root):
 *
 *   node scripts/quality/check-markdown-links.mjs
 *
 * Exit code 0 = all relative links resolve; 1 = one or more broken links listed in the console.
 * Example output when broken:
 *   Broken links (2):
 *     docs/README.md: (missing/file.md) — target not found
 */
import fs from "node:fs";
import path from "node:path";
import { fileURLToPath } from "node:url";

const repoRoot = path.resolve(
  path.dirname(fileURLToPath(import.meta.url)),
  "../..",
);

const IGNORED_DIRS = new Set([
  "node_modules",
  ".git",
  "target",
  "build",
  "dist",
  "out",
  ".next",
  "coverage",
]);

function walk(dir, out = []) {
  for (const name of fs.readdirSync(dir, { withFileTypes: true })) {
    if (IGNORED_DIRS.has(name.name)) continue;
    const p = path.join(dir, name.name);
    if (name.isDirectory()) walk(p, out);
    else if (name.name.endsWith(".md")) out.push(p);
  }
  return out;
}

function stripHtmlTags(text) {
  // Repeat until stable: a single pass can leave nested/malformed tags
  // (e.g. <scrip<script>t>) and reintroduce the unsafe sequence.
  let previous;
  do {
    previous = text;
    text = text.replace(/<[^>]*>/g, "");
  } while (text !== previous);
  return text;
}

function slugifyHeading(text) {
  return stripHtmlTags(text)
    .trim()
    .toLowerCase()
    .replace(/[^\w\s-]/g, "")
    .replace(/ /g, "-");
}

function collectAnchors(filePath) {
  const content = fs.readFileSync(filePath, "utf8");
  const anchors = new Set();
  for (const line of content.split(/\r?\n/)) {
    const m = line.match(/^#{1,6}\s+(.+)$/);
    if (m) {
      let headingText = m[1].trim();
      const explicit = headingText.match(/\{#([^}]+)\}\s*$/);
      if (explicit) {
        anchors.add(explicit[1]);
        headingText = headingText.replace(/\s*\{#[^}]+\}\s*$/, "").trim();
      }
      anchors.add(slugifyHeading(headingText));
    }
    const html = line.match(/id="([^"]+)"/);
    if (html) anchors.add(html[1]);
  }
  return anchors;
}

const linkRe = /\[([^\]]*)\]\(([^)]+)\)/g;

function isExternal(href) {
  return (
    /^https?:\/\//i.test(href) ||
    /^mailto:/i.test(href) ||
    href.startsWith("//") ||
    href.startsWith("#") ||
    href.startsWith("/.attachments/")
  );
}

const mdFiles = walk(repoRoot);
const anchorCache = new Map();
const broken = [];

for (const file of mdFiles) {
  const content = fs.readFileSync(file, "utf8");
  const relFile = path.relative(repoRoot, file).replace(/\\/g, "/");
  let m;
  linkRe.lastIndex = 0;
  while ((m = linkRe.exec(content)) !== null) {
    let href = m[2].trim();
    if (!href || isExternal(href)) continue;
    if (href.startsWith("<") && href.endsWith(">")) href = href.slice(1, -1);

    const hashIdx = href.indexOf("#");
    const filePart = hashIdx >= 0 ? href.slice(0, hashIdx) : href;
    const anchor = hashIdx >= 0 ? href.slice(hashIdx + 1) : "";

    if (!filePart) {
      if (anchor) {
        if (!anchorCache.has(file)) anchorCache.set(file, collectAnchors(file));
        if (!anchorCache.get(file).has(anchor)) {
          broken.push({
            file: relFile,
            href,
            reason: `missing anchor #${anchor}`,
          });
        }
      }
      continue;
    }

    const target = path.resolve(
      path.dirname(file),
      decodeURIComponent(filePart),
    );
    if (!fs.existsSync(target)) {
      broken.push({ file: relFile, href, reason: "target not found" });
      continue;
    }
    if (anchor && target.endsWith(".md")) {
      if (!anchorCache.has(target))
        anchorCache.set(target, collectAnchors(target));
      if (!anchorCache.get(target).has(anchor)) {
        broken.push({
          file: relFile,
          href,
          reason: `missing anchor #${anchor}`,
        });
      }
    }
  }
}

if (broken.length === 0) {
  console.log(
    `OK: ${mdFiles.length} markdown files, no broken relative links.`,
  );
  process.exit(0);
}

console.log(`Broken links (${broken.length}):`);
for (const b of broken) {
  console.log(`  ${b.file}: (${b.href}) — ${b.reason}`);
}
process.exit(1);
