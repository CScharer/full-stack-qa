/**
 * Wraps contiguous Markdown pipe tables with <!-- prettier-ignore-start/end -->
 * when missing.
 *
 * Run:
 *   node scripts/quality/wrap-md-tables-prettier-ignore.mjs
 */
import fs from "node:fs";
import path from "node:path";

const START = "<!-- prettier-ignore-start -->";
const END = "<!-- prettier-ignore-end -->";

function walkDir(dir, out = []) {
  for (const ent of fs.readdirSync(dir, { withFileTypes: true })) {
    if (ent.name === "node_modules" || ent.name === ".git") continue;
    const p = path.join(dir, ent.name);
    if (ent.isDirectory()) walkDir(p, out);
    else if (ent.name.endsWith(".md")) out.push(p);
  }
  return out;
}

function findTables(lines) {
  const tables = [];
  let fence = false;
  for (let i = 0; i < lines.length; i++) {
    const line = lines[i];
    if (line.trim().startsWith("```")) {
      fence = !fence;
      continue;
    }
    if (fence) continue;
    if (!/^\s*\|/.test(line)) continue;
    const start = i;
    while (i < lines.length && /^\s*\|/.test(lines[i])) i++;
    tables.push({ start, end: i });
    i--;
  }
  return tables;
}

function hasIgnoreAbove(lines, start) {
  let k = start - 1;
  while (k >= 0 && lines[k].trim() === "") k--;
  return k >= 0 && lines[k].trim() === START;
}

function hasIgnoreBelow(lines, end) {
  let m = end;
  while (m < lines.length && lines[m].trim() === "") m++;
  return m < lines.length && lines[m].trim() === END;
}

function wrapFile(absPath) {
  const raw = fs.readFileSync(absPath, "utf8");
  const nl = raw.includes("\r\n") ? "\r\n" : "\n";
  const lines = raw.split(/\r?\n/);
  const tables = findTables(lines);
  if (tables.length === 0) return false;

  for (let t = tables.length - 1; t >= 0; t--) {
    const { start, end } = tables[t];
    const okStart = hasIgnoreAbove(lines, start);
    const okEnd = hasIgnoreBelow(lines, end);
    if (okEnd && okStart) continue;
    if (!okEnd) lines.splice(end, 0, END);
    if (!okStart) lines.splice(start, 0, START);
  }

  const next = lines.join(nl);
  if (next !== raw) {
    fs.writeFileSync(absPath, next, "utf8");
    return true;
  }
  return false;
}

const roots = ["docs", "scripts", "tests"];
const files = [];
for (const r of roots) {
  if (fs.existsSync(r)) walkDir(r, files);
}
for (const ent of fs.readdirSync(".", { withFileTypes: true })) {
  if (ent.isFile() && ent.name.endsWith(".md")) files.push(ent.name);
}

const unique = [...new Set(files)];
let changed = 0;
for (const f of unique) {
  if (wrapFile(f)) {
    changed++;
    console.log(f);
  }
}
console.error(`Updated ${changed} file(s).`);
